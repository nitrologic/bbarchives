; ID: 134
; Author: Myke-P
; Date: 2001-11-10 18:53:19
; Title: Image Processing Functions
; Description: W.I.P. of a set of PSP-style Image Processing Functions

;Image Functions by Myke-P 2001
;various PixelFast functions that do funky things to images.
;Thanks to John C and Rob Cummings for their invaluable help.

AppTitle "Image Functions by Myke-P 2001"
Graphics 640,480,0,2
Global sourceimage = LoadImage("TestIn.bmp")
Global destinimage
Dim floydsarray#(0,0)

starttime = MilliSecs()

;uncomment one at a time, as appropriate, for a demo,
;otherwise you'll get the dreaded "illegal memory address"! :)

;destinimage = Image_Greyscale(sourceimage)
;destinimage = Image_Pixelate(sourceimage,5,5,2)
;destinimage = Image_Scanline(sourceimage,100,1)
;destinimage = Image_Colourise(sourceimage,255,0,255,1)
;destinimage = Image_Brightness(sourceimage,80,50)
;destinimage = Image_Negative(sourceimage)
;destinimage = Image_FloydDither(sourceimage)

endtime = MilliSecs()
SaveBuffer (ImageBuffer(destinimage),"TestOut.bmp")
SetBuffer FrontBuffer()
DrawBlock destinimage,0,20
DrawBlock sourceimage,ImageWidth(destinimage),20
Text 0,0,"That took: " + (endtime-starttime) + " millisecs."
WaitKey()
End

;IMAGE_GREYSCALE
;Turns an image into hues of Grey.
;
;source = source image handle
Function Image_Greyscale(source)
currbuff = GraphicsBuffer()
destin = CopyImage (source)
SetBuffer ImageBuffer(destin)
LockBuffer()
For i = 0 To ImageWidth(destin)-1
	For j = 0 To ImageHeight(destin)-1
		col = ReadPixelFast(i,j) And $FFFFFF
		redlevel = (col Shr 16) And $FF
		greenlevel = (col Shr 8) And $FF
		bluelevel = col And $FF
		greylevel = Int(0.298039215 * redlevel) + Int(0.588235293 * greenlevel) + Int(0.109803921 * bluelevel)
		argb = (greylevel Or (greylevel Shl 8) Or (greylevel Shl 16) Or (255 Shl 24))
		WritePixelFast i,j,argb
	Next
Next
UnlockBuffer()
SetBuffer currbuff
Return destin
End Function

;IMAGE_PIXELATE
;Turns an image into chunky pixels (size of your choice)
;
;source = source image handle
;x = width of pixelation
;y = height of pixelation
;option = 1: averaging off (default), 2: averaging on
Function Image_Pixelate(source,x,y,option)
currbuff = GraphicsBuffer()
If option <> 1 And option <> 2 Then
	option = 1
End If
destin = CopyImage(source)
SetBuffer ImageBuffer(destin)
LockBuffer()
Select option
	Case 1
		i = 0
		While i <= ImageWidth(destin)-1
			j = 0
			While j <= ImageHeight(destin)-1
				col = ReadPixelFast(i,j) And $FFFFFF
				redlevel = (col Shr 16) And $FF
				greenlevel = (col Shr 8) And $FF
				bluelevel = col And $FF
				argb = (bluelevel Or (greenlevel Shl 8) Or (redlevel Shl 16) Or (255 Shl 24))
				For k = 0 To x-1
					For l = 0 To y-1
						If ((i+k) < ImageWidth(destin)) And ((j+l) < ImageHeight(destin)) Then
							WritePixelFast (i+k),(j+l),argb
						End If
					Next
				Next
				j = j + y
			Wend
			i = i + x
		Wend
	Case 2
		i = 0
		While i <= ImageWidth(destin)-1
			j = 0
			While j <= ImageHeight(destin)-1
				redlevel = 0
				greenlevel = 0
				bluelevel = 0
				numpixels = 0
				;pass one - add all the r, g and b values together
				For k = 0 To x-1
					For l = 0 To y-1
						If ((i+k) < ImageWidth(destin)) And ((j+l) < ImageHeight(destin)) Then
							col = ReadPixelFast(i+k,j+l) And $FFFFFF
							redlevel = redlevel + ((col Shr 16) And $FF)
							greenlevel = greenlevel + ((col Shr 8) And $FF)
							bluelevel = bluelevel + (col And $FF)
							numpixels = numpixels + 1
						End If
					Next
				Next
				;work out the average r, g and b values by deviding by the number of counted pixels in the x*y block
				redlevel = Int(redlevel/numpixels)
				greenlevel = Int(greenlevel/numpixels)
				bluelevel = Int(bluelevel/numpixels)
				argb = (bluelevel Or (greenlevel Shl 8) Or (redlevel Shl 16) Or (255 Shl 24))
				;pass two - draw pixels of that colour
				For k = 0 To x-1
					For l = 0 To y-1
						If ((i+k) < ImageWidth(destin)) And ((j+l) < ImageHeight(destin)) Then
							WritePixelFast (i+k),(j+l),argb
						End If
					Next
				Next
				j = j + y
			Wend
			i = i + x
		Wend
End Select
UnlockBuffer()
SetBuffer currbuff
Return destin
End Function

;IMAGE_SCANLINE
;Adds a scanline effect on alternate lines (intensity and h/v direction of your choice)
;
;source = source image handle
;intensity = 0 to 100% as an integer
;option = 1: horizontal (default), 2: vertical
Function Image_Scanline(source,intensity#,option)
currbuff = GraphicsBuffer()
If intensity > 100 Then
	intensity = 100
Else If intensity < 0 Then
	intensity = 0
End If
intensity# = 1+(intensity/100)
If option <> 1 And option <> 2 Then
	option = 1
End If
destin = CopyImage (source)
SetBuffer ImageBuffer(destin)
LockBuffer()
Select option
	Case 2	;vertical
		For i = 0 To ImageWidth(destin)-1 Step 2
			For j = 0 To ImageHeight(destin)-1
				col = ReadPixelFast(i,j) And $FFFFFF
				redlevel = ((col Shr 16) And $FF) / intensity
				greenlevel = ((col Shr 8) And $FF) / intensity
				bluelevel = (col And $FF) / intensity
				argb = (bluelevel Or (greenlevel Shl 8) Or (redlevel Shl 16) Or (255 Shl 24))
				WritePixelFast i,j,argb
			Next
		Next
	Default	;horizontal
		For i = 0 To ImageWidth(destin)-1
			For j = 0 To ImageHeight(destin)-1 Step 2
				col = ReadPixelFast(i,j) And $FFFFFF
				redlevel = ((col Shr 16) And $FF) / intensity
				greenlevel = ((col Shr 8) And $FF) / intensity
				bluelevel = (col And $FF) / intensity
				argb = (bluelevel Or (greenlevel Shl 8) Or (redlevel Shl 16) Or (255 Shl 24))
				WritePixelFast i,j,argb
			Next
		Next
End Select
UnlockBuffer()
SetBuffer currbuff
Return destin
End Function

;IMAGE_COLOURISE
;Turns an image into hues of an RGB colour of your chosing (2 modes)
;
;source = source image handle
;red, green, blue = RGB values to aim toward
;option = 1: true colourise ala PSP (default), 2: alternate colourise 
Function Image_Colourise(source,red#,green#,blue#,option)
currbuff = GraphicsBuffer()
red# = red#/255
green# = green#/255
blue# = blue#/255
If option <> 1 And option <> 2 Then
	option = 1
End If

destin = CopyImage (source)
SetBuffer ImageBuffer(destin)
LockBuffer()
For i = 0 To ImageWidth(destin)-1
	For j = 0 To ImageHeight(destin)-1
		col = ReadPixelFast(i,j) And $FFFFFF
		redlevel = (col Shr 16) And $FF
		greenlevel = (col Shr 8) And $FF
		bluelevel = col And $FF
		greylevel = Int(0.298039215 * redlevel) + Int(0.588235293 * greenlevel) + Int(0.109803921 * bluelevel)
		Select option
			Case 2	;alternate colourise
				redlevel = Int(greylevel*red)
				greenlevel = Int(greylevel*green)
				bluelevel = Int(bluelevel*blue)
			Default	;true colorise (PSP emulation)
				If greylevel >= 128 Then
					redlevel = 255 * red + (1-red)*(greylevel-(255-greylevel))
					greenlevel = 255 * green + (1-green)*(greylevel-(255-greylevel))
					bluelevel = 255 * blue + (1-blue)*(greylevel-(255-greylevel))
				Else
					redlevel = Int(greylevel*red)*2
					greenlevel = Int(greylevel*green)*2
					bluelevel = Int(greylevel*blue)*2
				End If
		End Select
		argb = (bluelevel Or (greenlevel Shl 8) Or (redlevel Shl 16) Or (255 Shl 24))
		WritePixelFast i,j,argb
	Next
Next
UnlockBuffer()
SetBuffer currbuff
Return destin
End Function

;IMAGE_BRIGHTNESS
;Alters an images brightness and contrast
;
;NOTES: contrast equation not 100% accurate, but a bloody close approximation! ;)
;
;source = source image handle
;brightness = RGB level offset in the range -255 to 255
;contrast = contrast in the range -100% to 100% as an integer
Function Image_Brightness(source,brightness,contrast#)
currbuff = GraphicsBuffer()
destin = CopyImage (source)
If contrast# > 100 Then
	contrast# = 100
Else If contrast# < -100 Then
	contrast# = -100
End If
If contrast# >= 0 Then
	contrast# = (contrast#/(101-contrast#))
Else
	contrast# = (0-(contrast#/50))*(contrast#/(101-contrast#))
End If
SetBuffer ImageBuffer(destin)
LockBuffer()
For i = 0 To ImageWidth(destin)-1
	For j = 0 To ImageHeight(destin)-1
		col = ReadPixelFast(i,j) And $FFFFFF
		redlevel = (col Shr 16) And $FF
		greenlevel = (col Shr 8) And $FF
		bluelevel = col And $FF
		If contrast <= 0 Then
			If redlevel < 128 Then
				redlevel = Int(redlevel - (127-redlevel)*contrast) + brightness
			Else
				redlevel = Int(redlevel + (redlevel-127)*contrast) + brightness
			End If
			If greenlevel < 128 Then
				greenlevel = Int(greenlevel - (127-greenlevel)*contrast) + brightness
			Else
				greenlevel = Int(greenlevel + (greenlevel-127)*contrast) + brightness
			End If
			If bluelevel < 128 Then
				bluelevel = Int(bluelevel - (127-bluelevel)*contrast) + brightness
			Else
				bluelevel = Int(bluelevel + (bluelevel-127)*contrast) + brightness
			End If
		Else
			If redlevel < 128 Then
				redlevel = Int(redlevel + brightness)
				redlevel = redlevel - (127-redlevel)*contrast
			Else
				redlevel = Int(redlevel + brightness)
				redlevel = redlevel + (redlevel-127)*contrast
			End If
			If greenlevel < 128 Then
				greenlevel = Int(greenlevel + brightness)
				greenlevel = greenlevel - (127-greenlevel)*contrast
			Else
				greenlevel = Int(greenlevel + brightness)
				greenlevel = greenlevel + (greenlevel-127)*contrast
			End If
			If bluelevel < 128 Then
				bluelevel = Int(bluelevel + brightness)
				bluelevel = bluelevel - (127-bluelevel)*contrast
			Else
				bluelevel = Int(bluelevel + brightness)
				bluelevel = bluelevel + (bluelevel-127)*contrast
			End If
		End If
		If redlevel > 255 Then
			redlevel = 255
		End If
		If redlevel < 0 Then
			redlevel = 0
		End If
		If greenlevel > 255 Then
			greenlevel = 255
		End If
		If greenlevel < 0 Then
			greenlevel = 0
		End If
		If bluelevel > 255 Then
			bluelevel = 255
		End If
		If bluelevel < 0 Then
			bluelevel = 0
		End If
		
		argb = (bluelevel Or (greenlevel Shl 8) Or (redlevel Shl 16) Or (255 Shl 24))
		WritePixelFast i,j,argb
	Next
Next
UnlockBuffer()
SetBuffer currbuff
Return destin
End Function

;IMAGE_NEGATIVE
;Turns an image into it's negative form.
;
;source = source image handle
Function Image_Negative(source)
currbuff = GraphicsBuffer()
destin = CopyImage (source)
SetBuffer ImageBuffer(destin)
LockBuffer()
For i = 0 To ImageWidth(destin)-1
	For j = 0 To ImageHeight(destin)-1
		col = ReadPixelFast(i,j) And $FFFFFF
		redlevel = 255-((col Shr 16) And $FF)
		greenlevel = 255-((col Shr 8) And $FF)
		bluelevel = 255-(col And $FF)
		argb = (bluelevel Or (greenlevel Shl 8) Or (redlevel Shl 16) Or (255 Shl 24))
		WritePixelFast i,j,argb
	Next
Next
UnlockBuffer()
SetBuffer currbuff
Return destin
End Function

;IMAGE_FLOYDDITHER
;Dithers an image using Floyd-Steinberg approximation into 2 colours
;
;source = source image handle
;optional parameters (defaults given will be used if omitted)
;erroroffset = multiplier for error#
;redhigh,greenhigh,bluehigh = RGB values for light pixels
;redlow,greenlow,bluelow = RGB values for dark pixels
Function Image_FloydDither(source,erroroffset#=1,redhigh=255,greenhigh=255,bluehigh=255,redlow=0,greenlow=0,bluelow=0)
currbuff = GraphicsBuffer()
destin = CopyImage (source)
Dim floydsarray#(ImageWidth(destin),ImageHeight(destin))
SetBuffer ImageBuffer(destin)
LockBuffer()
;pass one - read greylevels into array
For i = 0 To ImageWidth(destin)-1
	For j = 0 To ImageHeight(destin)-1
		col = ReadPixelFast(i,j) And $FFFFFF
		redlevel = (col Shr 16) And $FF
		greenlevel = (col Shr 8) And $FF
		bluelevel = col And $FF
		greylevel# = Int(((222 * redlevel) + (707 * greenlevel) + (71 * bluelevel))/1000)
		floydsarray(i,j) = greylevel#/255
		argb = (greylevel Or (greylevel Shl 8) Or (greylevel Shl 16) Or (255 Shl 24))
		WritePixelFast i,j,argb
	Next
Next
;pass two - dither based on greylevels
For i = 0 To ImageWidth(destin)-1
	For j = 0 To ImageHeight(destin)-1
		If floydsarray(i,j) < 0.5 Then
			bright = 0
		Else
			bright = 1
		End If
		error# = erroroffset*(floydsarray(i,j) - bright)
		If (j+1 <= ImageHeight(destin)-1) Then
			floydsarray(i,j+1) = floydsarray(i,j+1) + error#*7/16
		End If
		If j-1 >=0 Then
			If (i+1 <= ImageWidth(destin)-1) Then
				floydsarray(i+1,j-1) = floydsarray(i+1,j-1) + error#*3/16
			End If
		End If
		If (i+1 <= ImageWidth(destin)-1) Then
			floydsarray(i+1,j) = floydsarray(i+1,j) + error#*5/16
		End If
		If (i+1 <= ImageWidth(destin)-1) And (j+1 <= ImageHeight(destin)-1) Then
			floydsarray(i+1,j+1) = floydsarray(i+1,j+1) + error#*1/16
		End If
	Next
Next
;pass three - write white or black pixels from array
For i = 0 To ImageWidth(destin)-1
	For j = 0 To ImageHeight(destin)-1
		If floydsarray(i,j) > 1 Then
			floydsarray(i,j) = 1
		ElseIf floydsarray(i,j) < 0 Then
			floydsarray(i,j) = 0
		End If
		If floydsarray(i,j) < 0.5 Then
			argb = (bluelow Or (greenlow Shl 8) Or (redlow Shl 16) Or (255 Shl 24))
		Else
			argb = (bluehigh Or (greenhigh Shl 8) Or (redhigh Shl 16) Or (255 Shl 24))
		End If
		WritePixelFast i,j,argb
	Next
Next
UnlockBuffer()
SetBuffer currbuff
Return destin
End Function
