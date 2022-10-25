; ID: 396
; Author: Perturbatio
; Date: 2002-08-15 21:03:38
; Title: ImageToBank, BankToImage functions
; Description: A simple example of loading from and storing images in banks

Graphics 640,480
SetBuffer BackBuffer()

bBank = CreateBank(5)
iImage = LoadImage("testin.png")

ImageToBank(bBank, iImage)

newImage = BankToImage(bBank)
DrawImage newImage, 0,0


Flip
WaitKey()

FreeBank bBank
FreeImage newImage
FreeImage iImage
End


;;;;;;;;;;;;;;;;;;;;;;;;
; Function ImageToBank ;
;;;;;;;;;;;;;;;;;;;;;;;;
;Accepts a bank which will be resized to store the data, and an image to store, 
;optional parameter is the AlphaFlag (currently irrelevant)
;Returns the error code.
Function ImageToBank(bankImage, bufferImage, iAlphaFlag=0)
bufOldBuffer = GraphicsBuffer();get the handle for the current buffer
Error = 0; Error 0 = no error, error 1 = image size invalid

;Check the image dimensions, if invalid then exit function with errorcode 1
If (ImageWidth(bufferImage)<1) Or (ImageHeight(bufferImage)<1) Then 
	Error = 1
	Goto EndBufferToBank
EndIf

;get the total number of bytes the image makes up
SizeOfImage = ImageWidth(bufferImage)*ImageHeight(bufferImage)

ResizeBank bankImage, (SizeOfImage*4) + 9 ;(4 bytes per int for the image info + 9 extra bytes for header)

PokeInt bankImage,0,ImageWidth(bufferImage);store the width in the bank
PokeInt bankImage,4,ImageHeight(bufferImage);store the height

;store other info in byte 8 (the ninth byte)
iByte8 = %00000000
If iAlphaFlag Then iByte8 = iByte8 Or %00000001 ; OR the first bit to 1 if it has an alpha mask

PokeByte bankImage,8,iByte8 ;store iByte8

;DebugLog "Byte 8: " + iByte8

iBankPointer = 9
SetBuffer ImageBuffer(bufferImage)
LockBuffer ImageBuffer(bufferImage)
For iLoopX = 0 To ImageWidth(bufferImage)-1
	For iLoopY = 0 To ImageHeight(bufferImage)-1
		PokeInt bankImage,ibankPointer, ReadPixelFast(iLoopX,iLoopY)
		iBankPointer = iBankPointer + 4;increment by 4 each time since it is an int we are poking
	Next
Next
UnlockBuffer ImageBuffer(bufferImage)
SetBuffer bufOldBuffer ; restore the buffer

.EndBufferToBank
Return Error
End Function


;;;;;;;;;;;;;;;;;;;;;;;;
; Function BankToImage ;
;;;;;;;;;;;;;;;;;;;;;;;;
; Accepts a bank that has previously had its' data set with ImageToBank
; Returns an image
Function BankToImage(bankImage)
bufOldBuffer = GraphicsBuffer()
iWidth = PeekInt(bankImage,0)
iHeight = PeekInt(bankImage,4)
iiByte8 = PeekByte(bankImage,5)
;get the length of the data block
SizeOfData = iWidth*iHeight
imgReturn = CreateImage(iWidth,iHeight)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;Insert code To process iByte8
; NOTE FOR ALPHA: if ((BIT0) And %00000001) = 1 then (process alpha info)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

iBankPointer = 9

SetBuffer ImageBuffer(imgReturn)
LockBuffer ImageBuffer(imgReturn)
For iLoopX = 0 To ImageWidth(imgReturn)-1
	For iLoopY = 0 To ImageHeight(imgReturn)-1
		WritePixelFast(iLoopX,iLoopY,PeekInt(bankImage,iBankPointer))
		iBankPointer = iBankPointer + 4;increment by 4 each time since it is an int we are peekin
	Next
Next
UnlockBuffer ImageBuffer(imgReturn)
SetBuffer bufOldBuffer


Return imgReturn


End Function


; The following is a description of the format in which the information is stored in the bank
; -------------------------------------------------------------------------------------------
; 
; Bytes 0-3 contains the width of the image (4-byte integer) must not be less than 1
; Bytes 4-7 contains the height of the image (4-byte integer) must not be less than 1
; Bit 0 of Byte 8 describes whether the image has an alpha mask (0=no, 1=yes) 
; 		may contain more information at a later date
;
; NOTE FOR ALPHA: if ((BIT0) And %00000001) = 1 then (process alpha info)
;
; Bytes onward from 9 descrive the image as integers, because the length of this can be calculated
; by multiplying the width by the height, there is no need for a terminator
