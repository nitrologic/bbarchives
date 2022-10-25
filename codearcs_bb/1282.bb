; ID: 1282
; Author: xlsior
; Date: 2005-02-07 05:23:56
; Title: BlitzMax Desaturation fader
; Description: Fade a screen from color to Black-and-white, and back again

' BlitzMax Color-to-B&W Fade Routine
' Takes a full screen image, and smoothly desaturates it.
'
' *Great* for using when pausing a game!
'
' Freeware, (C) 2005 by xlsior/Marc van den Dikkenberg
'
' - Press any key to start the fade effect.
' - Press Escape to exit.

Strict

Graphics 800,600,0

Local pixcolor:Long	' Pixel colors as read from the pixmap
Local pixred:Int
Local pixgreen:Int
Local pixblue:Int     
Local pixavg:Int      ' Averaged RGB values
Local orgformat:Int   ' Keep track of the original PixelMap format, for conversion
Local monopix:Int     ' Colors in ARGB format
Local workimg:timage
Local workpix:tpixmap
Local bwimg:timage
Local sourceimg:timage

sourceimg:timage=LoadImage("d:/working/background3.jpg",DYNAMICIMAGE)
workimg:timage=CreateImage (GraphicsWidth(),GraphicsHeight(),1,DYNAMICIMAGE)

DrawImage sourceimg,0,0
'
' blah blah blah, run a program, draw things to the backbuffer, until
' you want to invoke the effect:
'

GrabImage (workimg:timage,0,0)
Flip

workpix:tpixmap=LockImage(workimg:timage)

orgformat=PixmapFormat(workpix)
If orgformat<>PF_RGB888 Then 
	' if the Pixmap in a different format than these routined expect, then convert:
	workpix:tpixmap=ConvertPixmap:tpixmap(workpix,PF_RGB888)
End If

For Local xx=0 To GraphicsWidth()-1
	For Local yy=0 To GraphicsHeight()-1
		pixcolor=ReadPixel(workpix,xx,yy)
		pixred=(pixcolor & $00FF0000) Shr 16
		pixgreen=(pixcolor & $FF00) Shr 8
		pixblue=(pixcolor & $FF)
		pixavg=(pixred*0.299)+(pixgreen*0.587)+(pixblue*0.114)
		pixred=pixavg Shl 16
		pixgreen=pixavg Shl 8
		pixblue=pixavg
		monopix=pixred | pixgreen | pixblue
		WritePixel(workpix,xx,yy,monopix)
	Next
Next

If orgformat<>PF_RGB888 Then
	' The pixmap was converted to a different format originally.
	' Now convert it back to what it was.
	workpix:tpixmap=ConvertPixmap:tpixmap(workpix,orgformat)
End If

bwimg:timage=LoadImage(workpix:tpixmap)
UnlockImage(workimg:timage)

' At this point we have the following:
' workpix     - A pixmap containing a black-and-white version
' bwimg       - An image containing a black-and-white version
' workimg     - The screen grabbed full-color version
' sourceimg   - The original image loaded from disk

WaitKey()

For Local counter=0 To 100 Step 2
	crossfade(workimg:timage,bwimg:timage,counter)
	FlushMem
	Flip
	If KeyDown (key_Escape) Then Exit
Next

WaitKey()

For Local counter=0 To 100 Step 2
	crossfade(bwimg:timage,workimg:timage,counter)
	FlushMem
	Flip
	If KeyDown (key_Escape) Then Exit
Next

WaitKey()

Function CrossFade(pic1:timage,pic2:timage,perc:Float)
	' Generic Crossfade routine, to fade between the full color and
	' Black-and-white versions of the image.
	' Parameters: sourcepic, destination pic, percentage faded
	If perc<1 Then
		perc=1
	ElseIf perc>100 Then
		perc=100
	End If
	SetBlend (SolidBlend)
	SetColor 255,255,255
	DrawImage (pic1,0,0)
	SetBlend (Alphablend)
	SetAlpha (perc/100)
	DrawImage (pic2,0,0)
End Function
