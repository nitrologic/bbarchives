; ID: 2299
; Author: Nate the Great
; Date: 2008-08-21 00:26:21
; Title: 2d Alpha for b3d
; Description: Alows alpha blending on images

Graphics 640,480,0,2

Dim aray(60,60,3)


img = CreateImage(50,50)
img1 = CreateImage(50,50)
SetBuffer ImageBuffer(img)
ClsColor 255,0,0
Cls
Color 0,255,0
Rect 20,20,15,10
SetBuffer ImageBuffer(img1)
ClsColor 0,0,255
Cls

SetBuffer BackBuffer()
ClsColor 0,0,0

While Not KeyDown(1)
Cls

DrawImage img1,MouseX(),MouseY()

DrawImage1(img,320,240,0,.5,1)

Flip
Wend
End

Function DrawImage1(name,x,y,frm = 0,alpha# = 1,alphabmode = 0,red = 255,green = 255,blue = 255)

If alphabmode > 0 Then



SetBuffer ImageBuffer(name,frm)

LockBuffer ImageBuffer(name,frm)

For x1 = 1 To ImageWidth(name)-1
	For y1 = 1 To ImageHeight(name)-1
		rgb = ReadPixelFast(x1,y1)
		aray(x1,y1,0) = getred(rgb)
		aray(x1,y1,1) = getgreen(rgb)
		aray(x1,y1,2) = getblue(rgb)
	Next
Next

UnlockBuffer ImageBuffer(name,frm)	

SetBuffer BackBuffer()

LockBuffer BackBuffer()

For x1 = 1 To ImageWidth(name)-1
	For y1 = 1 To ImageHeight(name)-1
		If alphabmode = 1 Then
			If x1 + x > 0 And x1+x < GraphicsWidth()-1 And y1 + y > 0 And y1 + y < GraphicsHeight()-1 Then
				rgb = ReadPixelFast(x1+x,y1+y)
				WritePixelFast(x1+x,y1+y,getrgb(getred(rgb)+aray(x1,y1,0)*alpha#*(red/255),getgreen(rgb)+aray(x1,y1,1)*alpha#*(green/255),getblue(rgb)+aray(x1,y1,2)*alpha#*(blue/255)))
			EndIf
		ElseIf alphabmode = 2 Then
			If x1 + x > 0 And x1+x < GraphicsWidth()-1 And y1 + y > 0 And y1 + y < GraphicsHeight()-1 Then
				rgb = ReadPixelFast(x1+x,y1+y)
				brt = getred(rgb)+getgreen(rgb)+getblue(rgb)
				WritePixelFast(x1+x,y1+y,getrgb(getred(rgb)+aray(x1,y1,0)*alpha#*(red/255)*brt/765,getgreen(rgb)*aray(x1,y1,1)*alpha#*(green/255)*brt/765,getblue(rgb)*aray(x1,y1,2)*alpha#*(blue/255)*brt/765))
			EndIf
		EndIf
	Next
Next
Else
DrawImage name,x,y,frm
EndIf

UnlockBuffer BackBuffer()
End Function

Function GetRed(rgb)
	Return rgb Shr 16 And %11111111
End Function
Function GetGreen(rgb)
	Return rgb Shr 8 And %11111111
End Function
Function GetBlue(rgb)
	Return rgb And %11111111
End Function 






Function GetRGB(red,green,blue)
	Return blue Or (green Shl 8) Or (red Shl 16)
End Function
