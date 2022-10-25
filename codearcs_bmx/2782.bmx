; ID: 2782
; Author: Warner
; Date: 2010-10-26 16:58:56
; Title: minib3d AlignToVector
; Description: minib3d aligntovector

Strict

Import sidesign.minib3d

Graphics3D 800, 600, 0, 2

CreateLight()

'camera
Local cam:TCamera = CreateCamera()
MoveEntity cam, 0, 5, -15

'cone 
Local cube:TMesh = CreateCone()
RotateMesh cube, 90, 0, 0
PositionMesh cube, 0, 0, 1
Local surf:TSurface = GetSurface(cube, 1)
VertexColor surf, 0, 255, 0, 0
VertexColor surf, 1, 255, 0, 0
VertexColor surf, 2, 255, 0, 0
EntityFX cube, 2

'sphere
Local sph:TMesh = CreateSphere()
ScaleMesh sph, 5, 5, 5
EntityColor sph, 0, 255, 0
EntityPickMode sph, 2
surf = GetSurface(sph, 1)
For Local i% = 0 To CountVertices(surf) - 1
	VertexColor surf, i, 0, Rand(128, 255), 0
Next
EntityFX sph, 2

PointEntity cam, cube

Repeat

	'turn sphere using keys
	TurnEntity sph, 0, KeyDown(39)-KeyDown(37), KeyDown(40)-KeyDown(38)
	
	'click on sphere to place cone
	If MouseDown(1)
		CameraPick cam, MouseX(), MouseY()
		PositionEntity cube, PickedX(), PickedY(), PickedZ()
		
		AlignToVector cube, PickedNX(), PickedNY(), PickedNZ()
	End If
		
	RenderWorld
			
	Flip

'esc=exit
Until KeyHit(27)

End

'--------------------------------------------------------------------------------------------------------------------------
'											AlignToVector
'--------------------------------------------------------------------------------------------------------------------------
Function AlignToVector(e:TEntity, x#, y#, z#)

	'order=yaw-pitch-roll
	
	Local yaw#,pitch#,roll#
	Local x1#,y1#,z1#
	Local x2#,y2#,z2#
	Local x3#,y3#,z3#
	
	yaw# = -ATan2(x, z)
	
	x1# = z*Sin(yaw) + x*Cos(yaw)
	y1# = y	
	z1# = z*Cos(yaw) - x*Sin(yaw)
	
	pitch# = -ATan2(y1, z1)
	x2# = x1
	y2# = y1*Cos(pitch) - z1*Sin(pitch)
	z2# = y1*Sin(pitch) + z1*Cos(pitch)
	
	roll# = -ATan2(x2, y2)
	x3# = x2*Cos(roll) - y2*Sin(roll)
	y3# = x2*Sin(roll) + y2*Cos(roll)
	z3# = z2

	'FIX - might turn out it should be If y <= 0 .. haven't tested it thouroughly enough
	If y < 0 roll :+ 180
	
	RotateEntity e, pitch, yaw, roll
	
End Function
