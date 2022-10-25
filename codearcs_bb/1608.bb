; ID: 1608
; Author: jfk EO-11110
; Date: 2006-02-03 07:40:11
; Title: FrameGrabber using AVIFil32.dll
; Description: Use some functions of AVIFil32.dll to extract certain frames from an Avi quickly

; Approach: Fast and reliable direct Avi Frame access (for Video FX Processing etc.)
; AVIFIL32.DLL is used to read-access certain Frames of an Avi.

; A decls file named Avifil32.decls is required, containing:

;.lib "avifil32.dll"
;AVIFileInit%()
;AVIFileOpen%(ppfile*,szFile$,uMode%,lpHandler%)
;AVIFileRelease%(pfile%)
;AVIFileInfo%(pfile%,pfi*,lSize%)
;AVIFileGetStream%(pfile%,ppavi*,fccType%,lParam%)
;AVIFileExit%()
;AVIStreamStart%(pavi%)
;AVIStreamRelease%(pavi%)
;AVIStreamLength%(pavi%)
;AVIStreamGetFrameOpen%(pavi%,lpbiWanted%)
;AVIStreamGetFrame%(pFrame%,index%)
;AVIStreamGetFrameClose%(pFrame%)

;Additionally in "kernel32.decls" the following lines are required:

;.lib "kernel32.dll"
;RtlMoveMemory2%(Destination*,Source,Length) : "RtlMoveMemory"

;Of course, everything without semicolon.

Graphics 800,600,32,2
SetBuffer BackBuffer()


Const OF_READ=$0

; The following Avi codecs were tested and seem to work without problems:
; HuffYuf
; Indeo 5
; Cinepak Radius
; MPG4 V2


; Some Avi codecs that made troubles:
; DV-Avi: will be detected as not accessible
; VP6n (VP61, VP62 etc.) This one can be accessed, but when the stream and avi is closed,
;      it will produce a Windows Error message that cannot be clicked away until the machine
;      will be rebootet (at least on Win98). Unfortunately it's not possible to detect
;      this problem since Accessing the Frames works, only closing the handles forces the error.
;      I am desperatly seeking for a solution for this issue since it forces the user to
;      manually check the Avi codec before loading to make sure it's not vp6.
;      Any help is welcome!



testfile$="D:\video\smoke.AVI" 




; **** demo ****

AVIFileInit(); this needs to be called before any other Avifil32.dll function call

; multiple movies may now be opened simultanously, each one gets a bank handle:
avi_struct = bbOpenAvi(testfile$) ; this bank contains useful info. See function bbOpenAvi
; if it's zero then our app failed to open the avi sor some reason:
If avi_struct=0 Then 
 AVIFileExit()
 RuntimeError "error opening avi "+testfile$
Endif

FirstFrame1=PeekInt(avi_struct,136)
NumFrames1 =PeekInt(avi_struct,140)

index1=FirstFrame1

ClsColor 50,50,50
While KeyDown(1)=0
  img=bbAviGetFrame(avi_struct,index1)
  Cls
  DrawBlock img,10,10
  FreeImage img
  Text 100,100,"Frame: "+index1+"  "
  Flip
  index1=index1+1
  If index1>=NumFrames1 Then index1=FirstFrame1
Wend

If avi_struct <> 0 Then 
 bbCloseAvi(avi_struct) ; don't forget this!
EndIf

AVIFileExit()          ; this needs to be called  here to release the avifil32.dll initialisation !
End







; **********************************************************************************************
Function bbOpenAvi(file$)
 ; After opening the avi file we will need to retrieve some info from the avi handler.
 ; the following structure will be used:
 ; AVIFILEINFO structure:
 ;    0 dwMaxBytesPerSec; 
 ;    4 dwFlags; 
 ;    8 dwCaps; 
 ;   12 dwStreams; 
 ;   16 dwSuggestedBufferSize; 
 ;   20 dwWidth; 
 ;   24 dwHeight; 
 ;   28 dwScale; 
 ;   32 dwRate; 
 ;   36 dwLength; 
 ;   40 dwEditCount; 
 ;   44 string  szFileType 64 bytes; 
 ; (11*4)+64 =108
 ; size of AVIFILEINFO structure: 108 bytes
 s=108
 ; create a bank that A) stores the AVIFILEINFO that is given by Avifil32.dll, and B) store
 ; some bank handles, avifile handles, stream handles, frame handles that will be used
 ; in other functions as well. In case of B) the Data is stored at offset 128 and higher.
 AviFileInfoBank=CreateBank(512)
 For i=0 To 508 Step 4
  PokeInt AviFileInfoBank,i,0
 Next

 ret=CreateBank(8) ; will hold the avifileopen handle
 res=AVIFileOpen(ret,file$,OF_READ,0)
 avi_handle=PeekInt(ret,0)
 If res<>0 Then ; error opening!
  If avi_handle<>0
   AVIFileRelease(avi_handle)
  EndIf
  FreeBank ret
  FreeBank AviFileInfoBank
  Return 0
 EndIf
 ;Print "opened file" ; used for debugging purposes

 ;*** GET AVI INFO ***
 res=AVIFileInfo(avi_handle,AviFileInfoBank,108)
 ; string "szFileType", 64 bytes, contains some info about the Avi handler (well not used here, but, anyway)
 szFileType$=""
 For i=44 To 108
  If PeekByte(AviFileInfoBank,i)=0 Then Exit
  szFileType$=szFileType$+Chr$(PeekByte(AviFileInfoBank,i))
 Next
 ;Print "got info" ; used for debugging purposes
 ;Print szFileType$

 PokeInt AviFileInfoBank,128,avi_handle ; store avi_handle for future usage


 ;*** Open AVI STREAM ***
 ; fccType is a 32Bit long string that may be:
 ; "vids","auds","mids" or "txts" (of course we only use "vids")
 ; sent as 32 bit unsigned long:
 ; 1935960438 vids
 ; 1935963489 auds
 ; 1935960429 mids
 ; 1937012852 txts
 fccType=1935960438
 res=AVIFileGetStream(avi_handle,ret,fccType,0)
 stream_handle=PeekInt(ret,0)

 If res<>0 Then ; error getting stream!
  If stream_handle<>0
   AVIStreamRelease(stream_handle)
  EndIf
  If avi_handle<>0
   AVIFileRelease(avi_handle)
  EndIf
  FreeBank ret
  FreeBank AviFileInfoBank
  Return 0
 EndIf
 ;Print "got stream" ; used for debugging purposes

 PokeInt AviFileInfoBank,132,stream_Handle ; store stream_handle for future usage

 FirstFrame=AVIStreamStart(stream_handle)
 If firstframe<0 Then ; signs of incorrect avi data (no frames)
  If stream_handle<>0
   AVIStreamRelease(stream_handle)
  EndIf
  If avi_handle<>0
   AVIFileRelease(avi_handle)
  EndIf
  FreeBank ret
  FreeBank AviFileInfoBank
  Return 0
 EndIf
 ;Print "firstframe >-1" ; used for debugging purposes

 NumFrames=AVIStreamLength(stream_handle)
 If numframes<=0 Then ; no frames in avi
  If stream_handle<>0
   AVIStreamRelease(stream_handle)
  EndIf
  If avi_handle<>0
   AVIFileRelease(avi_handle)
  EndIf
  FreeBank ret
  FreeBank AviFileInfoBank
  Return 0
 EndIf
 ;Print "numframes > 0" ; used for debugging purposes

 PokeInt AviFileInfoBank,136,FirstFrame ; store FirstFrame for future usage
 PokeInt AviFileInfoBank,140,NumFrames ; store NumFrames for future usage



 frame_handle=AVIStreamGetFrameOpen(stream_handle,0) ; 0= default, 1=bestdisplay
 If frame_handle=0 ; failed to create a frame access handle
  If stream_handle<>0
   AVIStreamRelease(stream_handle)
  EndIf
  If avi_handle<>0
   AVIFileRelease(avi_handle)
  EndIf
  FreeBank ret
  FreeBank AviFileInfoBank
  Return 0
 EndIf
 PokeInt AviFileInfoBank,144,frame_handle ; store frame_handle for future usage
 ;Print "frame handle <> 0" ; used for debugging purposes

 ; create a bank that will be used to store the DIB bitmaps delivered by "AviStreamGetFrame".
 ; (currently only 24 bit DIB mode is supported)
 FrameBank=CreateBank((PeekInt(AviFileInfoBank,20)*PeekInt(AviFileInfoBank,24)*3)+40+1000)
 PokeInt AviFileInfoBank,148,FrameBank ; store FrameBank for future usage
 FreeBank ret
 Return AviFileInfoBank
End Function








Function bbAviGetFrame(AviFileInfoBank,index) ;request a certain frame and move the DIB image to a bank
 frame_adress=AVIStreamGetFrame(PeekInt(AviFileInfoBank,144),index)
 RtlMoveMemory2(PeekInt(AviFileInfoBank,148),frame_adress,40+(PeekInt(AviFileInfoBank,20)*PeekInt(AviFileInfoBank,24)*3))
 img=decodeDIB(PeekInt(AviFileInfoBank,148),PeekInt(AviFileInfoBank,20),PeekInt(AviFileInfoBank,24))
 Return img
End Function



Function bbCloseAvi(AviFileInfoBank) ; close frame, stream, avi, release used banks
 AVIStreamGetFrameClose(PeekInt(AviFileInfoBank,132))
 AVIStreamRelease(PeekInt(AviFileInfoBank,132))
 AVIFileRelease(PeekInt(AviFileInfoBank,128))
 FreeBank PeekInt(AviFileInfoBank,148)
 FreeBank AviFileInfoBank
End Function




Function decodeDIB(bank,w,h) ; create an image from a 24bit DIB image stored in a bank
 img=CreateImage(w,h)
 SetBuffer ImageBuffer(img)
 LockBuffer ImageBuffer(img)
 z=40
 For y=0 To h-1
  For x=0 To w-1
   rgb=PeekInt(bank,z) And $FFFFFF
   WritePixelFast x,(h-1)-y,rgb
   z=z+3
  Next
 Next
 UnlockBuffer ImageBuffer(img)
 SetBuffer BackBuffer()
 Return img
End Function
