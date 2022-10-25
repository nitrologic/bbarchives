; ID: 1518
; Author: jfk EO-11110
; Date: 2005-11-04 19:20:05
; Title: BankAsImage
; Description: Let a DLL access blitz images

Graphics 640,480,32,2
SetBuffer BackBuffer()

;WARNING - this will crash your PC in any other Mode than 32 Bit Color! If you run this windowed,
;the desktop needs to be True Color! (not 16 Bit Aka 65535 Colors!)


;the physical structure of a bank is as follows:
; -at the physical adress (bankhandle + 4) is a 32bit pointer to the banks data segment
; -at the physical adress (bankhandle + 8) is a value that relfects the size of the bank in bytes

;The following example will redirect a banks data pointer to the data of an existing image.
;This method allows to send the physical adress of an image to an external DLL, so the DLL
;may write RGB Data directly to the image and therefor allow Blitz to draw the Image as any
;other Blitz Image.

; There needs to be a kernel32.decls in the userlib folder, containing at least the following 
; declaration:


;.lib "kernel32.dll" 
;RtlMoveMemory%(Destination,Source*,Length) : "RtlMoveMemory" 
;RtlMoveMemory2%(Destination*,Source,Length) : "RtlMoveMemory"
;RtlMoveMemory4%(Destination,Source,Length) : "RtlMoveMemory"


; Sending a DLL our Images physical Adress (well infact it's a bank) over a Userlib interface 
; would require to use something like (assuming there is a function "SendImageAdress" in the DLL):

; (In Blitz:)
; SendImageAdress(FakeBank+4)

; (In the Userlib:)
; SendImageAdress%(imagepointer)

; Please note: this is an undocumeted hack. It's compatibility with future updates of Blitz3D
; is in no way ensured.
; Thanks to Tom who did this hack in the first place.

Const IMAGEBUFFER_OFFSET=72

w=320
h=240

hack_image=CreateImage(w,h)
LockBuffer ImageBuffer(hack_image) ; required : kind of activation
UnlockBuffer ImageBuffer(hack_image)

;------------

FakeBank=CreateBank(4) ; create a bank Handle
; Store the old banks pointer so we can clean up properly
OldBankInfo=CreateBank(8)
StoreBankInfo(FakeBank,OldBankInfo) ; store FakeBanks original size and pointer to its memory 
PointBankToImagebuffer(FakeBank,hack_image)



;testing "plot to the bank": some pink dots
For i=0 To 10000
 PokeImageBuffer(hack_image,FakeBank,Rand(w),Rand(h),$FF00FF)
Next

DrawImage hack_image,0,0
Flip
WaitKey()


; required(!), never forget this:
RestoreBankInfo(FakeBank,OldBankInfo)
End



Function PokeImageBuffer(image,buffer,x,y,rgba)
 imageH=ImageWidth(image)
 where=(y*imageH*4) + (4*x)
 wmax=(ImageWidth(image)*ImageHeight(image)*16) ; clip to image size
 If where<wmax
  PokeInt( buffer, where, rgba )
 EndIf
End Function



Function StoreBankInfo(b,info)
 Local temp=CreateBank(4)
 RtlMoveMemory2(temp,b+4,4)
 PokeInt(info,0,PeekInt(temp,0)) ; store old pointer
 RtlMoveMemory2(temp,b+8,4)
 PokeInt(info,4,PeekInt(temp,0)) ; store old size
 FreeBank temp
End Function



; The clever bit, we change 'Bank' to point to the ImageBuffer!
Function PointBankToImagebuffer(b,image)
 RtlMoveMemory4(b+4,ImageBuffer(image)+IMAGEBUFFER_OFFSET,4)
 size=ImageWidth(image) * ImageHeight(image) * 4
 Local temp=CreateBank(4)
 PokeInt(temp,0,size)
 RtlMoveMemory(b+8,temp,4)
 FreeBank temp
End Function



Function RestoreBankInfo(b,info) ; fake,old
 Local temp=CreateBank(4)
 PokeInt(temp,0,PeekInt(info,0))
 RtlMoveMemory(b+4,temp,4); restore old pointer
 PokeInt(temp,0,PeekInt(info,4))
 RtlMoveMemory(b+8,temp,4); restore old size
 FreeBank temp
End Function
