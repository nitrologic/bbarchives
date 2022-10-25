; ID: 548
; Author: Pepsi
; Date: 2003-01-13 03:37:22
; Title: Copy Image to Masked Texture
; Description: Finding out that I wasn't the only one having trouble when doing this...

; copy image to masked texture - Pepsi 2003
Graphics3D 640,480,16,2


; array to store color values
Dim image_store(255,255,2)

; create example image
img1=CreateImage(256,256)

; draw stuff on image
buf0=ImageBuffer( img1 )
SetBuffer buf0
ClsColor 0,0,0
Cls
Color 255,255,0

bfont=LoadFont("Arial",24,1)
SetFont bfont
Text 10,10,"wha'zzuuup :P"
Color 255,0,0
Rect 0,0,255,255,False

; store image color values in array
For y=0 To 255
	For x=0 To 255
		GetColor x,y
		image_store(x,y,0)=ColorRed()
		image_store(x,y,1)=ColorGreen()
		image_store(x,y,2)=ColorBlue()
	Next
Next

; create example texture
tex1=CreateTexture( 256,256,5)

; copy contents of array to texture
buf1=TextureBuffer( tex1 )
SetBuffer buf1
For y=0 To 255
	For x=0 To 255

	red=image_store(x,y,0)
	green=image_store(x,y,1)
	blue=image_store(x,y,2)

	If red=0 And green=0 And blue=0
		WritePixel x,y,0,buf1
	Else	
		Color red,green,blue
		Plot x,y
	EndIf

	Next
Next
SetBuffer BackBuffer()

; normal stuff
camera=CreateCamera()
PositionEntity camera,0,0,0
CameraClsColor camera,0,0,0


; set texture to an example sprite
sprite=CreateSprite()
EntityTexture sprite,tex1
PositionEntity sprite,0,0,1.5


Color 255,255,0
While Not KeyHit(1)
Cls
	UpdateWorld
	RenderWorld

	Flip
Wend

End
