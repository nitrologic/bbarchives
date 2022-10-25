; ID: 561
; Author: Russell
; Date: 2003-01-29 20:23:15
; Title: Seq2Stripv1.0
; Description: Convert a series of images to a single AnimImage "strip"

Graphics 640,480,16,2
.Beginning
Cls
sImageSeq$ = Input$("Image sequence, minus index and extension. (ex: Pic NOT Pic000.bmp)")

.GetExt
sImageExt$ = Lower$(Input$("Input file type ( bmp, jpeg/jpg, or png ONLY)"))
If sImageExt$ <> "bmp" And sImageExt$ <> "jpg" And sImageExt$ <> "jpeg" And sImageExt$ <> "png" Then Goto GetExt
	
.GetDigits
iNumDigits = Input$("Number of digits (1-3 ONLY ex: 3 for Pic000, etc)")
If iNumDigits > 3 Then Goto GetDigits

.GetFrames
iTotFrames = Input$("Number of frames in sequence (1-" + 10^iNumDigits + ")")
If iTotFrames > (10^iNumDigits) Then Goto GetFrames
Print "Ready to process? Press any key to process..."
WaitKey()
sFileName$ = sImageSeq$ + String$("0",iNumDigits) + "." + sImageExt$
If FileType(sFileName$) = 0 Then
	Cls
	Print "File not found! Press any key..."
	WaitKey()
	Goto Beginning
EndIf

imgFirst = LoadImage(sFileName$)
testvalid = ImageWidth(imgFirst) * iTotFrames
If testvalid > 32000 Then
	Cls
	Print "Resulting image would be too big! (" + testvalid + " pixels wide" + ")"
	Print "Press any key..."
	WaitKey()
	Goto Beginning
EndIf
	
testvalid = ImageHeight(imgFirst)
If testvalid > 32000 Then
	Cls
	Print "Resulting image would be too big! (" + testvalid + " pixels wide" + ")"
	Print "Press any key..."
	WaitKey()
	Goto Beginning
EndIf

tmpBuffer = CreateImage( ImageWidth(imgFirst) * iTotFrames, ImageHeight(imgFirst) )
If Not tmpBuffer Then RuntimeError "Couldn't create buffer for image!"
Cls
Print "I will use " + sImageSeq$ + String$("0",iNumDigits) + "." + sImageExt$ + " as the first frame. Anykey..."
WaitKey()
SetBuffer ImageBuffer(tmpBuffer)
For i = 0 To (iTotFrames - 1)
	index$ = Str i
	index$ = RSet$(index$,iNumDigits)
	index$ = Replace$(index$," ","0")
	
	tmpImage = LoadImage(sImageSeq$ + index$ + "." + sImageExt$)
	DrawBlock tmpImage, x, 0
	x = x + ImageWidth(imgFirst)
	FreeImage tmpImage
Next

SaveImage(tmpBuffer, sImageSeq$ + ".bmp")
Cls
Print "Done! Saved as '" + sImageSeq$ + ".bmp'. Press any key for anim demo! (escape to quit)"
Print "Use up and down to speed up or slow down the frame rate"
WaitKey()

FreeImage tmpBuffer
AnimSequence = LoadAnimImage(sImageSeq$ + ".bmp", ImageWidth(imgFirst),ImageHeight(imgFirst),0,iTotFrames-1)
FreeImage imgFirst

While Not KeyDown(1)
	SetBuffer BackBuffer()
	Cls
	DrawImage AnimSequence, MouseX(), MouseY(), frame
	frame = frame + 1
	If frame = (iTotFrames - 1) Then frame = 0
	Delay iDelay
	
	If KeyDown(200) Then 
		iDelay = iDelay - 20
		If iDelay < 0 Then iDelay = 0
	Else If KeyDown(208) Then
		iDelay = iDelay + 20
	EndIf
	
	Flip
Wend
End
