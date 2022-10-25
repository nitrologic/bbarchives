; ID: 2296
; Author: Nate the Great
; Date: 2008-08-17 18:28:15
; Title: Advanced Shadows
; Description: Makes Shadows

Graphics3D 320,240,0,2
cam = CreateCamera()
box = CreateCube()
box2 = CreateCube()

tex = CreateTexture(64,64)
SetBuffer TextureBuffer(tex)
ClsColor 255,255,255
Color 0,0,0
Cls
Text 1,1,"SHADOW"
Text 1,16,"SHADOW"
Text 1,32,"SHADOW"
ClsColor 0,0,0

EntityTexture box2,tex

lit = CreateLight()
RotateEntity lit,90,45,0
EntityPickMode box,3
EntityPickMode box2,2
MoveEntity box2,-2.5,-.5,0
TurnEntity box2,0,45,0
ScaleEntity  box2,.5,.5,.5

plane = CreatePlane()
EntityPickMode plane,2
MoveEntity plane,0,-.5,0

EntityColor box,255,0,0
EntityColor plane,0,0,255

TurnEntity cam,45,0,0
MoveEntity cam,0,0,-5

SetBuffer BackBuffer()

Cls

UpdateWorld()
RenderWorld2(cam,10,10,0)
Flip
WaitKey()
End

Function renderworld2(cam,x1#,y1#,z1#)

RenderWorld()
For x = 1 To GraphicsWidth()-1
	For y = 1 To GraphicsHeight()-1
		ent = CameraPick(cam,x,y)
		If ent <> LinePick(x1#,y1#,z1#,(PickedX#()-x1#),(PickedY#()-y1#),(PickedZ#()-z1#)) Then
			
			;Plot x,y	
			LockBuffer BackBuffer()
				
				rgb = ReadPixelFast(x,y)
				Color (getred(rgb)/2),(getgreen(rgb)/2),(getblue(rgb)/2)
				
			UnlockBuffer BackBuffer()
				Plot x,y
		EndIf
	Next
Next

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
