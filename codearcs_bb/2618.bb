; ID: 2618
; Author: Ion
; Date: 2009-11-30 13:27:20
; Title: Fish!
; Description: A pseudo-fish mesh in code

;********************************
; Fishes! by Ion
; Copyright (c) 2009 E.Sandberg (Ion)
; This code is free to the public domain
;
;********************************

;Funny variables
Type TFish
	Field Entity, Yaw#, Speed#
End Type
Global fishes.TFish[999]

;Initialize
Graphics3D 1024, 768, 16, 2
SetBuffer BackBuffer()
SeedRnd MilliSecs()
WireFrame False
HidePointer

;A little light
light = CreateLight()

;Set up the camera
camera = CreateCamera()
campiv = CreatePivot()
EntityParent camera, campiv
PositionEntity camera, 0, 50, -50
RotateEntity camera, 45, 0, 0
CameraClsColor camera, 64, 64, 255
CameraFogMode camera, 1
CameraFogColor camera, 64, 64, 255
CameraFogRange camera, 10, 100

;Create 100 fishes!
For i = 0 To 99
	f.TFish = New TFish
	f\Entity = CreateFish()
	f\Yaw = Rnd(-1, 1)
	f\Speed = Rnd(0.1, 0.5)
	PositionEntity f\Entity, Rand(-50, 50), Rand(-50, 50), Rand(-50, 50)
	RotateEntity f\Entity, 0, Rand(0, 360), Rand(-45, 45)
	fishes[i] = f
Next

;Gameloop
While Not KeyHit(1)
	TurnEntity campiv, 0, -MouseXSpeed(), 0
	For i = 0 To 99
		AnimateFish(fishes[i]\Entity, an + 90*i)
		MoveEntity fishes[i]\Entity, fishes[i]\Speed, 0, 0
		TurnEntity fishes[i]\Entity, 0, fishes[i]\Yaw, 0
	Next
	
	an = an + Rnd(10, 20)
	an = an Mod 360
	
	MoveMouse 320, 240
	RenderWorld
	Flip
Wend

;Create a fish!
Function CreateFish()
	mesh = CreateMesh()
	surf = CreateSurface(mesh)
	
	v0 = AddVertex(surf, 1, 0, 0)
	v1 = AddVertex(surf, 0, -0.5, 0)
	v2 = AddVertex(surf, 0, 0.5, 0)
	v3 = AddVertex(surf, -1, 0, -0.5)
	v4 = AddVertex(surf, -1, 0, 0.5)
	v5 = AddVertex(surf, -2, 0.5, 0)
	v6 = AddVertex(surf, Rand(-2.5, -1), Rand(1, 1.2), 0)
	v7 = AddVertex(surf, -2, -0.5, 0)
	v8 = AddVertex(surf, -3, -0.8, 0)
	v9 = AddVertex(surf, -3.5, -0.5, 0)
	v10 = AddVertex(surf, -3.5, 1.5, 0)
	v11 = AddVertex(surf, -1, Rand(0.5, -1), 0)
	
	;Head
	AddTriangle surf, v3, v2, v0
	AddTriangle surf, v0, v2, v4
	AddTriangle surf, v1, v3, v0
	AddTriangle surf, v0, v4, v1
	AddTriangle surf, v3, v5, v2
	AddTriangle surf, v2, v5, v4

	;Fin
	AddTriangle surf, v5, v6, v2
	AddTriangle surf, v2, v6, v5
	
	;Body
	AddTriangle surf, v3, v7, v5
	AddTriangle surf, v5, v7, v4
	AddTriangle surf, v3, v1, v7
	AddTriangle surf, v7, v1, v4
	
	;Back-fin
	AddTriangle surf, v7, v10, v5
	AddTriangle surf, v5, v10, v7
	AddTriangle surf, v7, v9, v10
	AddTriangle surf, v10, v9, v7
	AddTriangle surf, v8, v9, v7
	AddTriangle surf, v7, v9, v8
	
	;Under-fin
	AddTriangle surf, v11, v7, v1
	AddTriangle surf, v1, v7, v11

	UpdateNormals mesh
	
	;Color fish!
	For i = 0 To 10
		VertexColor surf, i, Rnd(0, 255), Rnd(0, 255), Rnd(0, 255)
	Next
	EntityFX mesh, 2
	Return mesh
End Function

;Stops a fish's animation!
Function StopFish(mesh)
	surf = GetSurface(mesh, 1)
	VertexCoords surf, 10, VertexX(surf, 10), VertexY(surf, 10), 0
	VertexCoords surf, 9, VertexX(surf, 9), VertexY(surf, 9), 0
	VertexCoords surf, 8, VertexX(surf, 8), VertexY(surf, 8), 0
	VertexCoords surf, 6, VertexX(surf, 6), VertexY(surf, 6), 0
	VertexCoords surf, 5, VertexX(surf, 5), VertexY(surf, 5), 0
	VertexCoords surf, 7, VertexX(surf, 7), VertexY(surf, 7), 0
	VertexCoords surf, 11, VertexX(surf, 11), VertexY(surf, 11), 0
	
	VertexCoords surf, 3, VertexX(surf, 3), VertexY(surf, 3), -0.5
	VertexCoords surf, 4, VertexX(surf, 4), VertexY(surf, 4), 0.5
	VertexCoords surf, 2, VertexX(surf, 2), VertexY(surf, 2), 0
	VertexCoords surf, 1, VertexX(surf, 1), VertexY(surf, 1), 0
End Function

;Animate a fish!
Function AnimateFish(mesh, an#)
	surf = GetSurface(mesh, 1)
	VertexCoords surf, 10, VertexX(surf, 10), VertexY(surf, 10), Sin(an)*0.5
	VertexCoords surf, 9, VertexX(surf, 9), VertexY(surf, 9), Sin(an)*0.5
	VertexCoords surf, 8, VertexX(surf, 8), VertexY(surf, 8), Sin(an)*0.5
	VertexCoords surf, 6, VertexX(surf, 6), VertexY(surf, 6), Cos(an)*-0.2
	VertexCoords surf, 5, VertexX(surf, 5), VertexY(surf, 5), Cos(an)*-0.2
	VertexCoords surf, 7, VertexX(surf, 7), VertexY(surf, 7), Cos(an)*-0.2
	VertexCoords surf, 11, VertexX(surf, 11), VertexY(surf, 11), Cos(an)*-0.2
		
	VertexCoords surf, 3, VertexX(surf, 3), VertexY(surf, 3), -0.5+Cos(an)*-0.1
	VertexCoords surf, 4, VertexX(surf, 4), VertexY(surf, 4), 0.5+Cos(an)*-0.1
	VertexCoords surf, 2, VertexX(surf, 2), VertexY(surf, 2), Sin(an)*-0.1
	VertexCoords surf, 1, VertexX(surf, 1), VertexY(surf, 1), Sin(an)*-0.1
End Function
