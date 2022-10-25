; ID: 420
; Author: Rob 
; Date: 2002-09-07 10:17:27
; Title: Transform Texture!
; Description: Ever wanted to scale a texture and/or rotate it yet keep it in the middle? Now you can!

Function TransTex(texture,angle#,scale#=1)
	ScaleTexture texture,scale,scale
	RotateTexture texture,angle#
	x#=Cos(angle)/scale/2
	y#=Sin(angle)/scale/2
	PositionTexture texture,(x-.5)-y,(y-.5)+x
End Function
