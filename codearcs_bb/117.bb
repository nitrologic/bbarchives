; ID: 117
; Author: Zenith(Matt Griffith)
; Date: 2001-12-14 00:04:19
; Title: CreateQuad()
; Description: Create a 3d one sided square from two triangles

Function CreateQuad()
	sprite=CreateMesh()
	he=CreateBrush(255,255,255)
	v=CreateSurface(sprite,he)
	FreeBrush he
	AddVertex ( v,-3,3,0,1,0)  ; top left 0,1;1,0
	AddVertex ( v,3,3,0,0,0)   ; top right 1,1;1,1
	AddVertex ( v,-3,-3,0,1,1) ; bottom left 0,0;,0,0
	AddVertex ( v,3,-3,0,0,1)  ; bottom right 1,0;0,1
	AddTriangle( v,0,1,2)
	AddTriangle( v,3,2,1)
	FlipMesh(sprite)
	HideEntity(sprite)
	Return sprite
End Function
