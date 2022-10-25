; ID: 1108
; Author: Nilium
; Date: 2004-07-17 02:51:48
; Title: Wire Primitives and Drawing
; Description: Functions to create wireframe primitives and draw them

Graphics3D 800,600,32,2

C = CreateCamera()
CameraClsColor C,240,255,215

WireCylinder = CreateWireCylinder(8)
EntityColor WireCylinder,0,0,0
ScaleEntity WireCylinder,1.05,1.05,1.05
Cylinder = CreateCylinder(8)
EntityColor Cylinder,235,255,110
EntityAlpha Cylinder,.7
EntityParent Cylinder,WireCylinder


Cube = CreateCube()
WireCube = CreateWireCube()
ScaleEntity WireCube,1.05,1.05,1.05
EntityAlpha Cube,.7
EntityColor Cube,235,255,110
EntityColor WireCube,0,0,0
EntityParent Cube,WireCube

Sphere = CreateSphere(4)
WireSphere = CreateWireSphere(4)
ScaleEntity WireSphere,1.05,1.05,1.05
EntityAlpha Sphere,.7
EntityColor Sphere,235,255,110
EntityColor WireSphere,0,0,0
EntityParent Sphere,WireSphere

PositionEntity WireCube,3,0,-3
PositionEntity WireSphere,-3,0,3

PositionEntity C,4,4,4
PointEntity C,Cylinder

L = CreateLight(2)
PositionEntity L,4,4,4
LightRange L,4

Repeat
	HideEntity Cylinder
	HideEntity Cube
	HideEntity Sphere
	
	TurnEntity WireCylinder,.5,.6,.7
	TurnEntity WireCube,.5,.6,.7
	TurnEntity WireSphere,.5,.6,.7

	DrawWireMeshes(C)
	
	ShowEntity Cylinder
	ShowEntity Cube
	ShowEntity Sphere

	RenderWorld
	
	Flip

Until KeyHit(1)



Type WireMesh
	Field Entity
End Type

Function DrawWireMeshes(Camera,Tween#=-1)
	For W.WireMesh = Each WireMesh
		EntityAlpha W\Entity,1
	Next
	
	CameraClsMode Camera,1,1
	WireFrame True
	
	If Tween Then
		RenderWorld Tween
	Else
		RenderWorld
	EndIf
	
	WireFrame False
	CameraClsMode Camera,0,0
	
	For W.WireMesh = Each WireMesh
		EntityAlpha W\Entity,0
	Next
End Function

Function CreateWireCube()
	M=CreateMesh()
	S = CreateSurface(M)
	;outer edges
	WireLine3D S,-1,-1,-1,-1,1,-1
	WireLine3D S,1,-1,-1,1,1,-1
	WireLine3D S,1,-1,1,1,1,1
	WireLine3D S,-1,-1,1,-1,1,1
	
	;top edges
	WireLine3D S,1,1,1,-1,1,1
	WireLine3D S,1,1,-1,-1,1,-1
	WireLine3D S,-1,1,-1,-1,1,1
	WireLine3D S,1,1,-1,1,1,1
	
	; bottom edges
	WireLine3D S,1,-1,1,-1,-1,1
	WireLine3D S,1,-1,-1,-1,-1,-1
	WireLine3D S,-1,-1,-1,-1,-1,1
	WireLine3D S,1,-1,-1,1,-1,1

	EntityFX M,1+16
	EntityAlpha M,0
	W.WireMesh = New WireMesh
	W\Entity = M
	Return M
End Function

Function CreateWireSphere(Segments=8)
	M = CreateMesh()
	S = CreateSurface(M)
	Dummy = CreateSphere(Segments)
	DS = GetSurface(Dummy,1)
	For N = 0 To CountTriangles(DS)-1
		V1 = TriangleVertex(DS,N,0)
		V2 = TriangleVertex(DS,N,1)
		V3 = TriangleVertex(DS,N,2)
		If V1 < Segments*2 Then WireLine3D S,VertexX(DS,V1),VertexY(DS,V1),VertexZ(DS,V1),VertexX(DS,V2),VertexY(DS,V2),VertexZ(DS,V2)
		WireLine3D S,VertexX(DS,V2),VertexY(DS,V2),VertexZ(DS,V2),VertexX(DS,V3),VertexY(DS,V3),VertexZ(DS,V3)
	Next
	FreeEntity Dummy
	EntityFX M,1+16
	EntityAlpha M,0
	W.WireMesh = New WireMesh
	W\Entity = M
	Return M
End Function

Function CreateWireCylinder(Segments=8)
	M = CreateMesh()
	S = CreateSurface(M)
	AngleStep# = 360.0/Segments
	While N < 360
		WireLine3D S,Sin(N),1,Cos(N),Sin(N),-1,Cos(N)
		WireLine3D S,Sin(N),1,Cos(N),Sin(N+AngleStep),1,Cos(N+AngleStep)
		WireLine3D S,Sin(N),-1,Cos(N),Sin(N+AngleStep),-1,Cos(N+AngleStep)
		N = N + AngleStep
	Wend
	EntityFX M,1+16
	EntityAlpha M,0
	W.WireMesh = New WireMesh
	W\Entity = M
	Return M
End Function

Function CreateWireRing(Segments=8)
	M = CreateMesh()
	S = CreateSurface(M)
	AngleStep# = 360.0/Segments
	While N < 360
		WireLine3D S,Sin(N),0,Cos(N),Sin(N+AngleStep),0,Cos(N+AngleStep)
		N = N + AngleStep
	Wend
	EntityFX M,1+16
	EntityAlpha M,0
	W.WireMesh = New WireMesh
	W\Entity = M
	Return M
End Function

Function WireLine3D(S,X#,Y#,Z#,X2#,Y2#,Z2#)
	V=AddVertex(S,X,Y,Z)
	AddVertex(S,X2,Y2,Z2)
	AddVertex(S,(X+X2)/2,(Y+Y2)/2,(Z+Z2)/2)
	AddTriangle S,V,V+1,V+2
End Function
