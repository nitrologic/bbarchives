; ID: 1661
; Author: jfk EO-11110
; Date: 2006-04-12 08:33:18
; Title: Save AVI with Sound
; Description: Save Movies with Sound using AviFil32.dll

; Save Avi with sound. Provided by Dieter Marfurt aka jfk 11110

; userlibs:

; avifil32.decls:

;.lib "avifil32.dll"
;AVIFileInit%()
;AVIFileOpen%(ppfile*,szFile$,uMode%,lpHandler%)
;AVIFileRelease%(pfile%)
;AVIFileInfo%(pfile%,pfi*,lSize%)
;AVIFileGetStream%(pfile%,ppavi*,fccType%,lParam%)
;AVIFileExit%()
;AVIFileCreateStream%(pfile%, ppavi*, psi*)
;AVIStreamInfo%(pStream%,pfi*,lSize%)
;AVIStreamStart%(pavi%)
;AVIStreamRelease%(pavi%)
;AVIStreamLength%(pavi%)
;AVIStreamGetFrameOpen%(pavi%,lpbiWanted%)
;AVIStreamGetFrame%(pFrame%,index%)
;AVIStreamGetFrameClose%(pFrame%)
;AVIStreamReadFormat%(pavi%, lPos%, lpFormat%, lpcbFormat*)
;AVIStreamSetFormat%(pavi%, lPos%, lpFormat*, cbFormat%)
;AVIStreamRead%(pavi%, lStart%, lSamples%, lpBuffer%, cbBuffer%, plBytes*, plSamples*)
;AVIStreamWrite%(pavi%, lStart%, lSamples%, lpBuffer*, cbBuffer%, dwflags%, plSampWritten%, plBytesWritten%)
;AVISaveOptions%(hwnd%, uiFlags%, nStreams%, ppavi*, plpOptions*)
;AVISaveOptionsFree%(nstreams%, plpOptions*)
;AVIMakeCompressedStream%(ppsCompressed*, psSource%, lpOptions%, pclsidHandler%)

; kernel32.decls:

;.lib "kernel32.dll" 
;RtlMoveMemory2%(Destination*,Source,Length) : "RtlMoveMemory"



Graphics 800,600,32,2
SetBuffer BackBuffer()

Const streamtypeAUDIO = 1935963489
Const streamtypeVIDEO = 1935960438
Const ICMF_CHOOSE_KEYFRAME=1 
Const ICMF_CHOOSE_DATARATE=2 
Const ICMF_CHOOSE_ALLCOMPRESSORS=8

Const AVIERR_OK=0 
Const AVIERR_NOCOMPRESSOR=$80000000 Or $40000 Or ($4000 + 113)
Const AVIERR_MEMORY=      $80000000 Or $40000 Or ($4000 + 103)
Const AVIERR_UNSUPPORTED= $80000000 Or $40000 Or ($4000 + 101)

Const AVIIF_KEYFRAME=$10 
Const Lib = 0 

Const OF_READ=$0
Const OF_WRITE=$1
Const OF_SHARE_DENY_WRITE=$20
Const OF_CREATE=$1000

Global hwnd=SystemProperty$("AppHWND")



; demo:

file$="test_save.avi"
target_width=640
target_height=480
target_rate=25




AVIFileInit(); this needs to be called before any other Avifil32.dll function call

; open avi for writing...
my_handle=OpenWriteAvi(file$,target_width,target_height,target_rate,"test.wav") ; optional last parameter is wav file to be added  (will be muxed in the CloseWriteAvi function)
If my_handle=0: CloseWriteAvi(my_handle): AVIFileExit(): End: EndIf


 ; write 11 frames to the avi
 For i=0 To 10
  Cls
  Color 0,255,0
  Rect 0,0,640,480,0
  For r=0 To 100
   Color Rand(255),Rand(255),Rand(255)
   Text (Rand(target_width) And $FFF0),Rand(target_height),i+"*+*"
  Next
  Flip
  AddFrameWriteAvi(my_handle, i); third (optional) parameter may be an image handle (make sure it's big enought), if omitted, the frame content will be grabbed from backbuffer
 Next 

CloseWriteAvi(my_handle)
AVIFileExit()

Print "ok!"
WaitKey()
End








Function OpenWriteAvi(file$,target_width,target_height,target_rate,soundfile$="")
 Local i ; probalby you better make all variables explicit locals (?), I'm too lazy for now

 AviControlBank=CreateBank(1024+128) ; this bank will be used to store several bank handles, variables etc. that are required in other functions, especially for closing/releasing things and writing frames to.
 PokeInt AviControlBank,36,target_width
 PokeInt AviControlBank,40,target_height
 PokeInt AviControlBank,44,target_rate

 PokeInt AviControlBank,128,Len(soundfile$)
 For i=1 To Len(soundfile$)
  PokeByte AviControlBank,132+i-1,Asc(Mid$(soundfile$,i,1))
 Next

 ret=CreateBank($128) ; will hold the avifileopen handle
 PokeInt AviControlBank, 8,ret
 If AVIFileOpen(ret,file$,OF_WRITE Or OF_CREATE,0)=0
  avi_handle=PeekInt(ret,0)
  PokeInt AviControlBank, 0,avi_handle

  AVISTREAMINFO_bank=CreateBank($ffff)
  PokeInt AviControlBank,12,AVISTREAMINFO_bank

  ; possible flags:
  avisf_disabled=$1
  avisf_video_palchanges=$10000
  avisf_knownflags=$10001
  loc_fccType=streamtypeVIDEO
  loc_fccHandler=(Asc("c") Shl 24)Or(Asc("v") Shl 16)Or(Asc("s") Shl 8)Or(Asc("m"))

  For i=0 To 511 Step 4
   PokeInt AVISTREAMINFO_bank,i,0
  Next

  PokeInt AVISTREAMINFO_bank, 0,loc_fccType
  PokeInt AVISTREAMINFO_bank, 4,loc_fccHandler
  PokeInt AVISTREAMINFO_bank,20,1000/target_rate; eg 1000/25= 40...
  PokeInt AVISTREAMINFO_bank,24,1000;loc_dwRate ;    1000/40= 25 fps    (typical M$ :) )
  PokeInt AVISTREAMINFO_bank,40,40+(target_width*target_height*3) ;loc_dwSuggestedBufferSize ; 0

  If AVIFileCreateStream(avi_handle,ret,AVISTREAMINFO_bank)=0
   stream_Handle=PeekInt(ret,0)
   PokeInt AviControlBank, 4,stream_handle

   ; required by AviStreamSetFormat (seems to be the same as the header of DIBHEADER)
   BITMAPINFOHEADER=CreateBank(40)
   PokeInt AviControlBank,16,BITMAPINFOHEADER
   PokeInt BITMAPINFOHEADER, 0,40 ; size of header
   PokeInt BITMAPINFOHEADER, 4,target_width ; width
   PokeInt BITMAPINFOHEADER, 8,target_height ; height
   PokeShort BITMAPINFOHEADER,12,1 ; planes
   PokeShort BITMAPINFOHEADER,14,24  ; bitCount
   PokeInt BITMAPINFOHEADER,16,0 ; compression (0=no compression in DIB)
   PokeInt BITMAPINFOHEADER,20,(target_width*target_height*3);0 ; size in bytes (may be 0 for non compressed)
   PokeInt BITMAPINFOHEADER,24,3600 ; horiz pixel per meter
   PokeInt BITMAPINFOHEADER,28,3600 ; vert pixel per meter
   PokeInt BITMAPINFOHEADER,32,0;$FFFFFF ; Colors used (for palette)
   PokeInt BITMAPINFOHEADER,36,0;$FFFFFF ; number of important colors (lol)

   ; this bank will be used to write our frame data to, so avifil32 can grab it from there
   DIBHEADER=CreateBank(40+(target_width*target_height*3)+1000) 
   PokeInt AviControlBank,20,DIBHEADER
   For i=0 To 36 ; we simply reuse BITMAPINFOHEADER
    PokeInt DIBHEADER,i,PeekInt(BITMAPINFOHEADER,i)
   Next

   ; A pointer to space for AVICOMPRESSOPTIONS: first Int of bank points to itself (offset 8)
   plpo=8 
   plpOptionsList=CreateBank(8000) ; req. size guessed, but hey...
   PokeInt AviControlBank,24,plpOptionsList
   compression_handle=CreateBank(8000)
   PokeInt AviControlBank,28,compression_handle
   dumm_bank=CreateBank(8)
   PokeInt AviControlBank,32,dumm_bank
   RtlMoveMemory2(dumm_bank,plpOptionsList+4,4)
   PokeInt plpOptionsList,0,PeekInt(dumm_bank,0)+plpo 
   PokeInt plpOptionsList,4,PeekInt(dumm_bank,0)+plpo+4000

   If AVISaveOptions(hwnd, 3, 1, ret, plpOptionsList)=1 ;=0
    res=AVIMakeCompressedStream(compression_handle, stream_handle, PeekInt(plpOptionsList,0), 0)
    If res=0
     ;If res= AVIERR_OK Then Print "ok!!!"
     ;If res= AVIERR_NOCOMPRESSOR Then Print "nocompressor"
     ;If res= AVIERR_MEMORY       Then Print "memory"
     ;If res= AVIERR_UNSUPPORTED  Then Print "unsupported"
     If AVIStreamSetFormat(PeekInt(compression_handle,0), 0, BITMAPINFOHEADER,40)=0
      Return AviControlBank ; ok ready to rumble
     EndIf
    EndIf
   EndIf
  EndIf
 EndIf
 Return 0 ; bah, failure
End Function



Function CloseWriteAvi(AviControlBank)
 Local i
 If AviControlBank<>0
   avi_handle=PeekInt(AviControlBank, 0)
   stream_handle=PeekInt(AviControlBank, 4)
   ret=PeekInt(AviControlBank, 8)
   AVISTREAMINFO_bank=PeekInt(AviControlBank,12)
   BITMAPINFOHEADER=PeekInt(AviControlBank,16)
   DIBHEADER=PeekInt(AviControlBank,20)
   plpOptionsList=PeekInt(AviControlBank,24)
   compression_handle=PeekInt(AviControlBank,28)
   dumm_bank=PeekInt(AviControlBank,32)
   len_wav=PeekInt(AviControlBank,128)
   wav_to_add$=""
   For i=1 To len_wav
    a=PeekByte(AviControlBank,132+i-1)
    If a=0 Then Exit
    wav_to_add$=wav_to_add$+Chr$(a)
   Next
   ; close all
   If compression_handle<>0
    AVIStreamRelease(PeekInt(compression_handle,0))
   EndIf
   If plpOptionsList<>0
    AVISaveOptionsFree(1,plpOptionsList)
   EndIf
   If stream_Handle<>0
    AVIStreamRelease(stream_Handle)
   EndIf
   ; multiplex Audio stream, if any
   If (wav_to_add$<>"") And (FileType(wav_to_add$)=1)
    AddWavWriteAvi(wav_to_add$,avi_handle)
   EndIf
   If avi_handle<>0
    AVIFileRelease(avi_handle)
   EndIf
   ;free banks...
   If ret<>0 Then FreeBank ret
   If AVISTREAMINFO_bank<>0 Then FreeBank AVISTREAMINFO_bank
   If nullBank<>0 Then FreeBank nullBank
   If BITMAPINFOHEADER<>0 Then FreeBank BITMAPINFOHEADER
   If DIBHEADER<>0 Then FreeBank DIBHEADER
   If plpOptionsList<>0 Then FreeBank plpOptionsList
   If compression_handle<>0 Then FreeBank compression_handle
   If dumm_bank<>0 Then FreeBank dumm_bank
   FreeBank AviControlBank
   ; i really hope I released everything, otherwise there would be a memoryleak
 EndIf
End Function


Function AddFrameWriteAvi(AviControlBank, index, buffer=0)
 If AviControlBank<>0
  width=PeekInt(AviControlBank,36)
  height=PeekInt(AviControlBank,40)
  If buffer=0 Then buffer = BackBuffer()
  DIBHEADER=PeekInt(AviControlBank,20)
  If DIBHEADER<>0
   compression_handle=PeekInt(AviControlBank,28)
   If compression_handle<>0
    Buffer2Dib(width,height,DIBHEADER,buffer)
    AVIStreamWrite(PeekInt(compression_handle,0), index, 1,  DIBHEADER ,40+(width * height *3) , AVIIF_KEYFRAME, 0,0)
   EndIf
  EndIf
 EndIf
End Function




Function Buffer2Dib(w,h,bank,buffer=0)
 Local x,y,count,rgb
 If buffer=0 Then buffer=BackBuffer()
 count=0
 SetBuffer Buffer
 LockBuffer Buffer
 For y=0 To h-1
  For x=0 To w-1
   rgb=ReadPixelFast(x,(h-1)-y) And $FFFFFF
   PokeByte bank,count+2,(rgb Shr 16) And $FF
   PokeByte bank,count+1,(rgb Shr 8) And $FF
   PokeByte bank,count+0, rgb And $FF
   count=count+3
  Next
 Next
 UnlockBuffer Buffer
End Function





Function AddWavWriteAvi(file$,avi_handle)
 Local b=0
 wav_handle=CreateBank(8)
 If AVIFileOpen(wav_handle,file$, OF_READ, 0)=0
;  Print "opened wav"
  wavstream_handle=CreateBank(8)
  If AVIFileGetStream(PeekInt(wav_handle,0), wavstream_handle, streamtypeAUDIO, 0) =0
;   Print "openened wav read stream"
   avi_info_bank=CreateBank(256);140
   If AVIStreamInfo(PeekInt(wavstream_handle,0), avi_info_bank,140) =0;140
;    Print "retrievend wav read stream info"
    lfmtSize=CreateBank(2560)
    lpFormat=CreateBank(256)
    If AVIStreamReadFormat(PeekInt(wavstream_handle,0), 0, 0, lfmtSize) =0 
;     Print "used to read format of read stream"
     If PeekInt(lfmtSize,0)>0
      fmtWav=CreateBank(256)
      dummy=CreateBank(8)
      RtlMoveMemory2(dummy,fmtWav+4,4)
      If AVIStreamReadFormat(PeekInt(wavstream_handle,0), 0, PeekInt(dummy,0), lfmtSize)=0
;       Print "read format 2"
       lStreamLength = AVIStreamLength(PeekInt(wavstream_handle,0)) 
;       Print "got wav lenght"
       If lStreamLength <>0
        nullBank=CreateBank(lStreamLength)
        lpbData = CreateBank(lStreamLength+100000)
        RtlMoveMemory2(dummy,lpbData+4,4)
        If AVIStreamRead(PeekInt(wavstream_handle,0), 0, lStreamLength, PeekInt(dummy,0), lStreamLength, nullbank, nullbank)=0
;         Print "avistreamread wav data"
         psAvi=CreateBank(256)
         siWav=CreateBank(256)
         If AVIFileCreateStream(avi_handle, psAvi, avi_info_bank)=0
;          Print "created avi streat for writing audio"
          If AVIStreamSetFormat(PeekInt(psAvi,0), 0, fmtWav, PeekInt(lfmtSize,0))=0
;           Print "used to set format for audio write stream"
           If AVIStreamWrite (PeekInt(psAvi,0), 0, lStreamLength, lpbData, lStreamLength, AVIIF_KEYFRAME, 0, 0)=0
;            Print "AviWriteStream: written wav data to stream"
            b=1
           EndIf
          EndIf
          AVIStreamRelease(PeekInt(psAvi,0))
         EndIf
        EndIf
        FreeBank lpbData
        lpbData=0
       EndIf
      EndIf
     EndIf   
    EndIf
   EndIf
   AVIStreamRelease(PeekInt(wavstream_handle,0))
  EndIf
  AVIFileRelease(PeekInt(wav_handle,0))
 EndIf
 If wav_handle		<>0 Then FreeBank wav_handle
 If wavstream_handle	<>0 Then FreeBank wavstream_handle
 If avi_info_bank	<>0 Then FreeBank avi_info_bank
 If lfmtSize		<>0 Then FreeBank lfmtSize
 If lpFormat		<>0 Then FreeBank lpFormat
 If fmtWav		<>0 Then FreeBank fmtWav
 If dummy		<>0 Then FreeBank dummy
 If lpbData		<>0 Then FreeBank lpbData
 If psAvi		<>0 Then FreeBank psAvi
 If siWav		<>0 Then FreeBank siWav
 If nullbank	<>0 Then FreeBank nullbank
 Return b
End Function
