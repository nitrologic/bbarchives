; ID: 658
; Author: jhocking
; Date: 2003-04-22 13:43:38
; Title: 2D fade
; Description: fade in/out in 2D graphics mode

Graphics 800,600,32,2
SetBuffer BackBuffer()

logo = LoadImage("facing.jpg")

Blend_Black(logo,0,0,0,1000)
time=MilliSecs()+1000
While MilliSecs()<time
	DrawBlock logo,0,0
	Flip
Wend
Blend_Black(logo,0,0,1,1000)

While Not KeyHit(1)
Wend
End

;------------------------------------------------------------------
; Blends an Image with a Black Background (In & Out)
; -----------------------------------------------------------------
; Image = The Image to Blend
; x     = X Coordinate in the Screen
; y     = Y Coordinate in the Screen
;         50000 for both to center image
; Mode  = 0 Blend In
;       = 1 Blend Out
; Time  = Time in Milliseconds
;------------------------------------------------------------------

Function Blend_Black(image,x,y,mode,time)
	source = ImageBuffer(image)
	dest   = BackBuffer()
	maxx   = ImageWidth(image)-1
	maxy   = ImageHeight(image)-1
	start  = MilliSecs()
	
	LockBuffer source
	
	If X = 50000 And y = 50000
		x = (GraphicsWidth()/2)-(ImageWidth(image)/2)
		y = (GraphicsHeight()/2)-(ImageHeight(image)/2)
	EndIf
	
	While MilliSecs()-start<time
Cls
		LockBuffer dest
		count=(count+1) Mod 4
		If count=0 Then minx=0: miny=0
		If count=1 Then minx=1: miny=1
		If count=2 Then minx=1: miny=0
		If count=3 Then minx=0: miny=1
		If mode=0 Then value=MilliSecs()-start
		If mode=1 Then value=time-MilliSecs()+start
		For ii=miny To maxy Step 2
			For i=minx To maxx Step 2
				rgb=ReadPixelFast(i,ii,source)
				r=(rgb And $FF0000)/$10000
				g=(rgb And $FF00)/$100
				b=rgb And $FF
				rgb=r*value/time*65536+g*value/time*256+b*value/time
				WritePixelFast x+i,y+ii,rgb,dest
			Next
		Next
		UnlockBuffer dest
		Flip
	Wend
	UnlockBuffer source
	If mode=0 Then DrawBlock image,x,y
	If mode=1 Then Color 0,0,0: Rect x,y,maxx+1,maxy+1
	Flip
End Function
