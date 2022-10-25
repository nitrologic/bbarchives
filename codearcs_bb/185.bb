; ID: 185
; Author: Myke-P
; Date: 2002-01-10 19:08:08
; Title: 16-bit Image Correction
; Description: Get rid of those ugly 16-bit rendered blues in 2D

Graphics 640,480,32,2
Const sourcefile$ = "C:\process\inputimage.bmp"
Const outputfile$ = "C:\process\outputimage.bmp"

starttime = MilliSecs()
Image_16bitCorrect(sourcefile$,outputfile$)
SetBuffer FrontBuffer()
endtime = MilliSecs()
Text 0,0,"That took " + (endtime-starttime)
WaitKey()
End

Function Image_16bitCorrect(filein$,fileout$)
	source = LoadImage(filein$)
	SetBuffer ImageBuffer(source)
	LockBuffer()
	For j = 0 To ImageWidth(source)-1
		For k = 0 To ImageHeight(source)-1
			col = ReadPixelFast(j,k) And $FFFFFF
			redlevel# = (col Shr 16) And $FF
			greenlevel# = (col Shr 8) And $FF
			bluelevel# = col And $FF
			redlevel# = Int(redlevel#/8)*8
			If Int(redlevel#) = 256 Then
				redlevel# = 248
			End If
			greenlevel# = Int(greenlevel#/8)*8
			If Int(greenlevel#) = 256 Then
				greenlevel# = 248
			End If
			bluelevel# = Int(bluelevel#/8)*8
			If Int(bluelevel#) = 256 Then
				bluelevel# = 248
			End If
			argb = (Int(bluelevel) Or (Int(greenlevel) Shl 8) Or (Int(redlevel) Shl 16) Or (255 Shl 24))
			WritePixelFast j,k,argb
		Next
	Next
	UnlockBuffer()
	SaveBuffer(ImageBuffer(source),fileout$)
	FreeImage source
End Function
