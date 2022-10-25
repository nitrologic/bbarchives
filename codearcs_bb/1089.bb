; ID: 1089
; Author: Nilium
; Date: 2004-06-17 16:06:49
; Title: Simple Bounding Cubes
; Description: 3D and 2D functions for drawing cubes similar to the bounding boxes in 3ds around entities

; ID: 1089
; Author: Noel R. Cower
; Date: 2004-06-17 16:06:49
; Title: Simple Bounding Cubes
; Description: 3D and 2D functions for drawing cubes similar to the bounding boxes in 3ds around entities

Graphics3D 800,600,32,2

C = CreateCamera()
M = CreateCube()
Box = BoundEntity3D(M)
MoveEntity C,-6,5.2,4
PointEntity C,M

L = CreateLight(2)
PositionEntity L,32,32,32
LightRange L,24
LightColor L,200,200,200
CameraClsMode C,0,0
AmbientLight 32,32,32
Repeat
	TurnEntity M,-.5,-.6,-.7
	ScaleEntity M,Abs Sin(Float(MilliSecs())/40)*2,Abs Sin(Float(MilliSecs())/80)*2,Abs Cos(Float(MilliSecs())/160)*2
	PositionEntity M,Sin(Float(MilliSecs())/40)*3,0,Cos(Float(MilliSecs())/40)*5
	
	If KeyHit(57) Then
		Mode = Not Mode
		If Mode = 0 Then
			ShowEntity Box
		Else
			HideEntity Box
		EndIf
	EndIf
	
	If Mode = 0 Then BoundEntity3D M,Box,.2
	UpdateWorld
	Cls
	WireFrame False
	AmbientLight 32,32,32
	CameraClsMode C,0,1
	RenderWorld
	CameraClsMode C,0,0
	WireFrame True
	AmbientLight 255,255,255
	HideEntity Box
	RenderWorld
	If Mode = 0 Then ShowEntity Box
	If Mode = 1 Then BoundEntity2D M,C
	Flip
Until KeyHit(1)

Function BoundEntity3D(Entity,Box = 0,Outline#=.15)
	If Entity = 0 Then Return False
	EClass$ = EntityClass(Entity)
	If Lower(EClass$) <> "mesh" Then Return False

	If Box = 0 Then
		Box = CreateCube()
		EntityFX Box,1+16
		T = CreateTexture(256,256,1+16+32)
		SetBuffer(TextureBuffer(T))
		Color 32,32,32
		Rect 0,0,256,256,1
		Color 0,0,0
		Rect 6,6,256-12,256-12,True
		
		Color 255,255,255
		
		Line 0,0,0,32
		Line 0,255-32,0,255
		
		Line 256,0,256,32
		Line 255,255-32,255,255
		
		Line 0,0,32,0
		Line 255-32,0,255,0
		
		Line 0,255,32,255
		Line 255-32,255,255,255
		
		SetBuffer(BackBuffer())
		EntityTexture Box,T,0,0
		EntityBlend Box,3
		FreeTexture T
	EndIf
	
	Local X#,Y#,Z#,BX#,BY#,BZ#
	X = -999999
	Y = -999999
	Z = -999999
	BX = 999999
	BY = 999999
	BZ = 999999
	
	For Surface = 1 To CountSurfaces(Entity)
		S = GetSurface(Entity,Surface)
		For N = 0 To CountVertices(S)-1
			TFormPoint VertexX(S,N),VertexY(S,N),VertexZ(S,N),Entity,0
			If TFormedX() > X Then
				X = TFormedX()
			ElseIf TFormedX() < BX Then
				BX = TFormedX()
			EndIf
			
			If TFormedY() > Y Then
				Y = TFormedY()
			ElseIf TFormedY() < BY Then
				BY = TFormedY()
			EndIf
			
			If TFormedZ() > Z Then
				Z = TFormedZ()
			ElseIf TFormedZ() < BZ Then
				BZ = TFormedZ()
			EndIf
		Next
	Next
	
	FitMesh Box,X+Outline,Y+Outline,Z+Outline,BX-X-Outline*2,BY-Y-Outline*2,BZ-Z-Outline*2,0
	Return Box
End Function

Function BoundEntity2D(Entity,Camera,Outline=16)
	If Entity = 0 Or Camera = 0 Then Return False
	EClass$ = EntityClass(Entity)
	If Lower(EClass$) <> "mesh" Then Return False

	Local X,Y,BX,BY
	BX = GraphicsWidth()
	BY = GraphicsHeight()
	For Surface = 1 To CountSurfaces(Entity)
		S = GetSurface(Entity,Surface)
		For N = 0 To CountVertices(S)-1
			TFormPoint VertexX(S,N),VertexY(S,N),VertexZ(S,N),Entity,0
			
			CameraProject Camera,TFormedX(),TFormedY(),TFormedZ()
			
			PX = ProjectedX()
			PY = ProjectedY()
			
			If PX > X X = PX
			If PX < BX
				BX = PX
			EndIf
			
			If PY > Y Y = PY
			If PY < BY
				BY = PY
			EndIf
		Next
	Next
	
	X = X + Outline
	Y = Y + Outline
	BX = BX - Outline
	BY = BY - Outline
	
	Line X,Y,X-32,Y
	Line X,Y,X,Y-32
	
	Line BX,BY,BX+32,BY
	Line BX,BY,BX,BY+32
	
	Line BX,Y,BX+32,Y
	Line BX,Y,BX,Y-32
	
	Line X,BY,X-32,BY
	Line X,BY,X,BY+32
End Function
