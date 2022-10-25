; ID: 2091
; Author: MCP
; Date: 2007-08-07 14:19:12
; Title: VectorGFX example
; Description: Create retro-style 3d vector objects

;*** VectorGFX - R. Ferriby

Graphics3D 800,600,32,0
SetBuffer BackBuffer()
AntiAlias 1
AppTitle "VectorGFX demo - R Ferriby"

Global camera%=CreateCamera()
PositionEntity camera,0,0,-40

vectorcube%=CreateVectorMesh()

AddVector(vectorcube,-1,-1,-1, -1,1,-1)	; 0,1
VectorTo(vectorcube,1,1,-1)		; 1,2
VectorTo(vectorcube,1,-1,-1)		; 2,3
CloseVector(vectorcube,0,3)		; 3,0

AddVector(vectorcube,-1,-1, 1, -1,1, 1)	; 4,5
VectorTo(vectorcube,1,1, 1)		; 5,6
VectorTo(vectorcube,1,-1, 1)		; 6,7
CloseVector(vectorcube,7,4)		; 7,4

CloseVector(vectorcube,0,4)		; 0,4
CloseVector(vectorcube,1,5)		; 1,5
CloseVector(vectorcube,2,6)		; 2,6
CloseVector(vectorcube,3,7)		; 3,7

ScaleEntity vectorcube,10,10,10
EntityColor vectorcube,255,255,255

cube%=CreateCube()      ; used for hidden line removal
EntityFX cube,5
EntityColor cube,0,0,0
ScaleEntity cube,10,10,10
alpha#=0
r%=255 : g%=255 : b%=255

While Not KeyHit(1)
	If KeyDown(200)
		alpha=alpha+0.01
		If alpha>1.0
			alpha=1.0
		EndIf
	EndIf
	If KeyDown(208)
		alpha=alpha-0.01
		If alpha<0.0
			alpha=0.0
		EndIf
	EndIf
	If KeyDown(2)
		r=255 : g=255 : b=255
	EndIf
	If KeyDown(3)
		r=255 : g=0 : b=0
	EndIf
	If KeyDown(4)
		r=0 : g=255 : b=0
	EndIf
	If KeyDown(5)
		r=0 : g=0 : b=255
	EndIf
	EntityAlpha cube,alpha
	EntityColor vectorcube,r,g,b
	TurnEntity cube,0.1,0.2,0.3
	TurnEntity vectorcube,0.1,0.2,0.3
	CameraClsMode camera,1,1
	WireFrame 1
	ShowEntity vectorcube
	HideEntity cube
	RenderWorld
	CameraClsMode camera,0,0
	WireFrame 0
	ShowEntity cube
	HideEntity vectorcube
	RenderWorld
	Text 10,10,"Up - increase solidity"
	Text 10,30,"Down - decrease solidity"
	Text 10,50,"Keys 1,2,3,4 - change color"
	Text 10,70,"ESC - quit"
	Flip 1
Wend
End


Function CreateVectorMesh%()
	Local mesh%=CreateMesh()
	Local surf%=CreateSurface(mesh)
	EntityFX mesh,17
	Return mesh
End Function

Function AddVector(mesh%,x1#,y1#,z1#,x2#,y2#,z2#)
	Local surf%=GetSurface(mesh,1)
	Local v1%,v2%

	v1=AddVertex(surf,x1,y1,z1)
	v2=AddVertex(surf,x2,y2,z2)
	AddTriangle(surf,v1,v2,v1)
End Function

Function VectorTo(mesh,x2#,y2#,z2#)
	Local surf%=GetSurface(mesh,1)
	Local v1,v2%
	v2=AddVertex(surf,x2,y2,z2)
	v1=v2-1
	AddTriangle(surf,v1,v2,v1)
End Function

Function CloseVector(mesh,v1%,v2%)
	Local surf%=GetSurface(mesh,1)
	AddTriangle(surf,v1,v2,v1)
End Function
