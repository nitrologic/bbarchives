; ID: 151
; Author: Jim Brown
; Date: 2001-11-29 13:52:08
; Title: Texture Fill
; Description: At last, a texture fill routine!

; Texture Fill routine by Jim Brown
; ##########################################
; Modified Steven Smiths Flood Fill code.
; Adapted from code by Petter Holmberg.
; Get the original from www.basicguru.com/abc/graphics.htm


; 	USAGE: TextureFill(X,Y,TEXTURE)

; fills an area of same colour (on the front buffer) with a texture
;
; X/Y      - coordinates to fill from
; TEXTURE  - supplied image texture


Const sw=640, sh=480	; screen width/height
Graphics sw,sh,32

texture=LoadImage("brick.bmp")  ; <---- Supply your own here!!
If texture=0 ; make one if no texture found
	texture=CreateImage(64,64)
	SetBuffer ImageBuffer(texture)
	ClsColor 0,20,100 : Cls : Color 60,140,0
	Oval 32-28,32-28,56,56,1 : Rect 1,1,62,62,0
EndIf

; include this part in your code
; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Const fillstack=2350 ; increase this if you get holes in complex fills
; type to hold pixel coords
Type point
	Field x,y
End Type
;Create a stack of point types
Dim pixdata.point(fillstack)
For fstack=0 To fillstack : pixdata(fstack)=New point : Next
; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

SeedRnd MilliSecs()
SetBuffer FrontBuffer() : ClsColor 0,0,0

While Not KeyHit(1)
	Cls : Color Rand(10,200),Rand(10,200),Rand(10,200)
	For d=1 To Rand(50,80)
		xp=Rand(100,sw-100) : yp=Rand(100,sh-100)
		radx=Rand(20,180) : rady=Rand(20,150)
		Oval xp-radx/2,yp-rady/2,radx,rady,True
	Next
	Oval sw/2-25,sh/2-25,50,50
	TextureFill(sw/2,sh/2,texture)
	Delay 400
Wend
End


; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
; Texture Fill routine
; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Function TextureFill(x,y,tex)
	If tex=0 Then Return 0 			; exit if no texture
	Local fillwidth=GraphicsWidth(), fillheight=GraphicsHeight()
	Local twidth=ImageWidth(tex), theight=ImageHeight(tex)
	LockBuffer FrontBuffer() : LockBuffer ImageBuffer(tex)
	;clear the coord stack
	For fstack=0 To fillstack : pixdata(fstack)\x=0 : pixdata(fstack)\y=0 : Next
	bcol=ReadPixel(x,y) 			; get the background colour
	firstentry=0 : lastentry=1
	Repeat
		; fill to the right
		fx=pixdata(firstentry)\x : fy=pixdata(firstentry)\y
		Repeat
			fillcol=ReadPixelFast(x+fx,y+fy) : flag=True
			If fillcol = bcol And (x+fx>=0 And x+fx<fillwidth) And (y+fy>=0 And y+fy<fillheight)
				CopyPixelFast (x+fx) Mod twidth,(y+fy) Mod theight, ImageBuffer(tex), x+fx,y+fy , FrontBuffer()
				If ReadPixelFast(x+fx,y+fy-1) = bcol ; above
					pixdata(lastentry)\x=fx : pixdata(lastentry)\y=fy-1
					lastentry=lastentry+1 : If lastentry=fillstack+1 Then lastentry=0
				EndIf
				If ReadPixelFast(x+fx,y+fy+1) = bcol ; below
					pixdata(lastentry)\x=fx : pixdata(lastentry)\y=fy+1
					lastentry=lastentry+1 : If lastentry=fillstack+1 Then lastentry=0
				EndIf
			Else
				flag=False
			EndIf
			fx=fx+1
		Until flag=False
		fx=pixdata(firstentry)\x-1 : fy=pixdata(firstentry)\y
		; fill to the left
		Repeat
			fillcol=ReadPixelFast(x+fx,y+fy) : flag=True
			If fillcol = bcol And (x+fx>=0 And x+fx<fillwidth) And (y+fy>=0 And y+fy<fillheight)
				CopyPixelFast (x+fx) Mod twidth,(y+fy) Mod theight, ImageBuffer(tex), x+fx,y+fy , FrontBuffer()
				If ReadPixelFast(x+fx,y+fy-1) = bcol ; above
					pixdata(lastentry)\x=fx : pixdata(lastentry)\y=fy-1
					lastentry=lastentry+1 : If lastentry=fillstack+1 Then lastentry=0
				EndIf
				If ReadPixelFast(x+fx,y+fy+1) = bcol ; below
					pixdata(lastentry)\x=fx : pixdata(lastentry)\y=fy+1
					lastentry=lastentry+1 : If lastentry=fillstack+1 Then lastentry=0
				EndIf
			Else
				flag=False
			EndIf
			fx=fx-1
		Until flag=False
		firstentry=firstentry+1 : If firstentry=fillstack+1 Then firstentry=0
	Until firstentry=lastentry
	UnlockBuffer FrontBuffer() : UnlockBuffer ImageBuffer(tex)
	Return
End Function
