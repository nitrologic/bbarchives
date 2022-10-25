; ID: 27
; Author: skidracer
; Date: 2001-08-29 17:52:04
; Title: DrawAlphaImage
; Description: DrawImage command with alpha channel 

; drawalphaimage.bb
; by simon@acid.co.nz

image=LoadImage("test.bmp")
For i=0 To 100 Step 10
	DrawAlphaImage(image,i,i)
Next
MouseWait
End	

Function DrawAlphaImage(image,px,py,alphaimage=0)
	If alphaimage=0 alphaimage=image
; size
	w=ImageWidth(image)
	h=ImageHeight(image)
	gw=GraphicsWidth()
	gh=GraphicsWidth()
; clip
	x0=px:y0=py
	If x0<0 w=w+x0 x0=0
	If y0<0 h=h+y0 y0=0
	If x0+w>gw w=gw-x0
	If y0+h>gh h=gh-y0
	If w<=0 Or h<=0 Return
	x1=x0+w-1
	y1=y0+h-1
; lock buffers	
	ibuffer=ImageBuffer(image)
	abuffer=ImageBuffer(alphaimage)
	gbuffer=GraphicsBuffer()
	LockBuffer ibuffer
	LockBuffer abuffer
	LockBuffer gbuffer
; draw
	For y=y0 To y1
		For x=x0 To x1
			alpha=ReadPixelFast(x-px,y-py,abuffer) And 255
			If alpha>1
				rgb0=ReadPixelFast(x-px,y-py,ibuffer)
				rgb1=ReadPixelFast(x,y,gbuffer)
				bit=$80
				rgb=0
				While bit>1
					rgb0=(rgb0 Shr 1) And $7f7f7f
					rgb1=(rgb1 Shr 1) And $7f7f7f
					If (alpha And bit) rgb=rgb+rgb0 Else rgb=rgb+rgb1
					bit=bit Shr 1
				Wend
				WritePixelFast x,y,rgb
			EndIf
		Next
	Next
; unlock
	UnlockBuffer gbuffer	
	UnlockBuffer ibuffer
	UnlockBuffer abuffer
End Function
