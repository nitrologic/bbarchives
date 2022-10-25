; ID: 1088
; Author: Perturbatio
; Date: 2004-06-15 08:30:06
; Title: GenImage
; Description: A simple piece of code to generate opposite direction animations

Graphics 640,480,32,2

;setting these here so that I can modify this later to work with commandline or drag n drop
Global ImageName$ = "elfmoves.bmp"
Global FrameWidth% = 32
Global FrameHeight% = 32
Global FirstFrame% = 0
Global tempImage = LoadImage(ImageName)
Global FrameCount% = ImageWidth(tempImage)/FrameWidth
Global OutImageName$ = "new_" + Left(ImageName$, Len(ImageName$)-Len(ExtractFileExt(ImageName$))-1)+".bmp"
Global XOffset = FrameCount * FrameWidth

	FreeImage tempImage
	tempImage = 0

;images
Global inImage = LoadAnimImage(ImageName, FrameWidth , FrameHeight, FirstFrame, FrameCount)
Global NewImage = CreateImage((FrameWidth * FrameCount)*2, FrameHeight, FrameCount * 2)
tempImage = CreateImage(FrameWidth, FrameHeight)


;first, copy the original frames to the new image
SetBuffer ImageBuffer(NewImage)
For Count = 0 To FrameCount -1
	
	DrawImage inImage, Count * FrameWidth, 0, Count
		
Next



;next copy and flip the frames and place them in the new image in the same sequence
StartTime = MilliSecs()

For Count = 0 To FrameCount -1
	SetBuffer ImageBuffer(tempImage)
	Cls
	DrawImage inImage, 0, 0, Count
	tempImage = FlipImageH(tempImage)
	SetBuffer ImageBuffer(NewImage)
	DrawImage tempImage, XOffset + (Count * FrameWidth), 0
Next
EndTime = MilliSecs() - StartTime
;SAVE THE IMAGE
SaveImage(NewImage, OutImageName$)


;DISPLAY THE END RESULTS
SetBuffer BackBuffer()

DrawImage NewImage, 0,0

Text 0, ImageHeight(NewImage), "Done - file saved as: " + OutImageName$
Text 0, ImageHeight(NewImage) + 24, "Time taken to convert:" + EndTime + "ms"

;END OF PROGRAM

WaitKey()

FreeImage NewImage
FreeImage inImage
FreeImage tempImage

EndGraphics

End

;FUNCTION FileImageH
;Accepts an image, flips it horizontally (using CopyRect), then returns the resulting image

Function FlipImageH(srcImage)
;LOCAL VARS
Local Width = ImageWidth(srcImage)
Local Height = ImageHeight(srcImage)
Local imgTemp = CreateImage(Width, Height)

;MAIN

For X = 0 To ImageWidth(srcImage)-1
	CopyRect X, 0, 1, Height, (Width-1) - X, 0, ImageBuffer(srcImage), ImageBuffer(imgTemp)
Next

Return imgTemp

End Function

;FUNCTION ExtractFileName
;Accepts a filepath and returns the filename

Function ExtractFileName$(sFilePath$)
;LOCAL VARS
Local iStartPos% = 0
Local iSearchPos% = 0
Local iFilePathLength = 0
Local sFileName$ = ""
	
;BEGIN FUNCTION CODE
iFilePathLength = Len(sFilePath$)
iSearchPos% = iFilePathLength
 
While (iStartPos% < 1) And (iSearchPos% > 1)

	iStartPos% = Instr(sFilePath$, "\", iSearchPos%)
	iSearchPos% = iSearchPos% - 1
	
Wend

If iStartPos = 0 Then ;if the filepath contains no backslashes
	sFileName$ = sFilePath$
Else
	sFileName$ = Right$(sFilePath$, iFilePathLength% - iStartPos%)
EndIf


Return sFileName$

End Function

;FUNCTION ExtractFileExt
;Accepts a filepath and returns the extension for the file

Function ExtractFileExt$(sFilePath$)
;LOCAL VARS
Local iStartPos% = 0
Local iSearchPos% = 0
Local iFilePathLength = 0
Local sFileExt$ = ""
	
;BEGIN FUNCTION CODE
iFilePathLength = Len(sFilePath$)
iSearchPos% = iFilePathLength
 
While (iStartPos% < 1) And (iSearchPos% > 1)

	iStartPos% = Instr(sFilePath$, ".", iSearchPos%)
	iSearchPos% = iSearchPos% - 1
	
Wend

If iStartPos = 0 Then ;if the filepath contains no .
	sFileExt$ = sFilePath$
Else
	sFileExt$ = Right$(sFilePath$, iFilePathLength% - iStartPos%)
EndIf


Return sFileExt$

End Function

;FUNCTION ExtractFilePath
;Accepts a filepath with filename and returns only the path
;i.e. pass c:\temp\test.txt the return value will be c:\temp\

Function ExtractFilePath$(sFilePath$)
;LOCAL VARS
Local iStartPos% = 0
Local iSearchPos% = 0
Local iFilePathLength = 0
Local sFileExt$ = ""
	
;BEGIN FUNCTION CODE
iFilePathLength = Len(sFilePath$)
iSearchPos% = iFilePathLength
 
While (iStartPos% < 1) And (iSearchPos% > 1)

	iStartPos% = Instr(sFilePath$, "\", iSearchPos%)
	iSearchPos% = iSearchPos% - 1
	
Wend

If iStartPos = 0 Then ;if the filepath contains no backslashes
	sFileExt$ = sFilePath$
Else
	sFileExt$ = Left$(sFilePath$, iStartPos%)
EndIf


Return sFileExt$

End Function
