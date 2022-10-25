; ID: 203
; Author: Rob
; Date: 2002-01-26 09:19:28
; Title: Fast 3D hud design system
; Description: Enables perspective/resolution independant hud overlays

;fov is the same as your camerazoom.
Function Sprite2D(sprite,x#,y#,fov#)
	PositionEntity sprite,2*(x-320),-2*(y-240),fov#*640
End Function

;scale sprite in screen pixels relative to a 640x480 res when used with Sprite2D
Function ScaleSprite2(sprite,x,y)
	ScaleEntity sprite,x,y,1
End Function

;please pass camera to this function or 0 for a billboard type with mesh.
Function CreateSprite2(parent)
	If parent<>0
		m=CreateMesh(parent)
	Else
		m=CreateMesh()
	EndIf
	s=CreateSurface(m)
	AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
	AddVertex s,+1,-1,-1,1,1:AddVertex s,-1,-1,-1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	ScaleEntity m,100,100,1
	Return m
End Function
