; ID: 3036
; Author: Kryzon
; Date: 2013-03-07 22:12:49
; Title: Circular Screen Fade
; Description: Screen fade effect with a circular iris

Graphics3D 800,600,0,2

camera = CreateCamera()
CameraClsColor camera,190,120,35

cube = CreateCube()
MoveEntity cube,0,0,5

light = CreateLight(1)
TurnEntity light,60,-30,0

Const SQUARE_SIZE = 256
;Calculate half the diagonal of the screen and map it to 3D units, so the circle circumscribes the screen perfectly when fully open.
Global HALF_SCREEN_DIAGONAL# = (4.0 * (Sqr(GraphicsWidth()*GraphicsWidth() + GraphicsHeight()*GraphicsHeight()) / 2.0)) / Float(GraphicsWidth()) + 0.1
fadeQuad = CreateCircleFade(camera)
fadeState = False
fadeSize# = 1.0 ;Goes from 1.0 (fully open) to 0.0 (fully closed).
fadeSpeed# = 0.015


fpsTimer = CreateTimer(60)

While Not KeyHit(1)
	WaitTimer(fpsTimer)
	
	TurnEntity cube,0.5,0.6,0.7

	If KeyHit(57) Or KeyHit(28) Then fadeState = Not fadeState
	If fadeState Then 
		fadeSize = fadeSize - fadeSpeed
		If fadeSize < 0 Then fadeSize = 0
	Else
		fadeSize = fadeSize + fadeSpeed
		If fadeSize > 1.0 Then fadeSize = 1.0
	EndIf 
	UpdateCircleFade(fadeQuad, fadeSize)
	
	RenderWorld()
	Color 255,255,255
	Text 10,10,"Press SPACE or ENTER to play the circular fade."
	
	Flip
Wend 

End 


Function CreateCircleFade(camera)
	mesh = CreateMesh(camera)
	surf = CreateSurface(mesh)
	
	xOff# = (4.0 / 2) + 1
	yOff# = (4.0 / 2) / (Float(GraphicsWidth())/GraphicsHeight()) + 1

	v0 = AddVertex(surf,-xOff,yOff,0)
	v1 = AddVertex(surf,xOff,yOff,0,0)
	v2 = AddVertex(surf,-xOff,-yOff,0)
	v3 = AddVertex(surf,xOFf,-yOff,0)
	
	diag# = HALF_SCREEN_DIAGONAL
	
	;Build the mesh.	
	v4 = AddVertex(surf,-diag,diag,0, 0,0)
	v5 = AddVertex(surf,diag,diag,0, 0,0)
	v6 = AddVertex(surf,-diag,-diag,0, 0,0)
	v7 = AddVertex(surf,diag,-diag,0, 0,0)	
		
	v8c = AddVertex(surf,-diag,diag,0, 0,0)
	v9c = AddVertex(surf,diag,diag,0, 1.0,0)
	v10c = AddVertex(surf,-diag,-diag,0, 0,1)
	v11c = AddVertex(surf,diag,-diag,0, 1,1)
	
	AddTriangle(surf,v0,v1,v4)
	AddTriangle(surf,v0,v4,v2)
	AddTriangle(surf,v1,v5,v4)
	AddTriangle(surf,v1,v3,v5)
	AddTriangle(surf,v3,v7,v5)
	AddTriangle(surf,v3,v2,v7)
	AddTriangle(surf,v2,v6,v7)
	AddTriangle(surf,v2,v4,v6)
	
	AddTriangle surf,v8c,v9c,v10c
	AddTriangle surf,v9c,v11c,v10c
	
	EntityColor mesh,0,0,0
	EntityFX mesh,1
	MoveEntity mesh,0,0,2
	
	EntityTexture mesh,CreateMaskedCircleTexture()
		
	Return mesh
End Function


;Here the texture is created procedurally, but one made in Photoshop or whatever, with alpha flag and smooth border, will
;look much better. I wanted to create everything in code so it'd be easier to run, just copy-paste.
Function CreateMaskedCircleTexture()
	Local tex = CreateTexture(SQUARE_SIZE, SQUARE_SIZE, 1+2)
	
	SetBuffer TextureBuffer(tex)
		ClsColor 0,0,0
		Cls
		Color 1,1,1
		Oval 0,0,SQUARE_SIZE,SQUARE_SIZE,True
	SetBuffer BackBuffer()
	
	LockBuffer TextureBuffer(tex)
	For x = 0 To SQUARE_SIZE-1
		For y = 0 To SQUARE_SIZE-1
			argb = ReadPixelFast(x,y,TextureBuffer(tex))			
			If (argb = $FF010101) Then WritePixelFast(x,y,$00000000,TextureBuffer(tex))
		Next
	Next
	UnlockBuffer TextureBuffer(tex)
	
	Return tex
End Function


Function UpdateCircleFade(fadeQuad, fadeSize#=1.0)
	;The "doubled" vertices are 4,5,6 and 7. Their purpose is to keep the area outside the circle black.
	;The circle`s vertices are 8,9,10 and 11, based on the order the vertices were created.
	
	Local surface = GetSurface(fadeQuad,1)
	
	;The animation consists of modulating the vertices' "open" positions by a "unit" value, such as "fadeSize" in this case. 
	;This way the vertices close the circle down.
	Local offset# = HALF_SCREEN_DIAGONAL*fadeSize
	
	VertexCoords(surface, 4,-offset,offset,0)
	VertexCoords(surface, 5,offset,offset,0)
	VertexCoords(surface, 6,-offset,-offset,0)
	VertexCoords(surface, 7,offset,-offset,0)
	
	VertexCoords(surface, 8,-offset,offset,0)
	VertexCoords(surface, 9,offset,offset,0)
	VertexCoords(surface, 10,-offset,-offset,0)
	VertexCoords(surface, 11,offset,-offset,0)

End Function
