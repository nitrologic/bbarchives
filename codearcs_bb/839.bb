; ID: 839
; Author: koekjesbaby
; Date: 2003-11-24 06:37:45
; Title: 3d lines
; Description: 1pixel wide lines in 3d

Function createline(x1#,y1#,z1#, x2#,y2#,z2#, mesh=0)
	
	If mesh = 0 Then 
		mesh=CreateMesh()
		EntityFX(mesh,16)
		surf=CreateSurface(mesh)	
		verts = 0	
	
		AddVertex surf,x1#,y1#,z1#,0,0
	Else
		surf = GetSurface(mesh,1)
		verts = CountVertices(surf)-1
	End If
	
	AddVertex surf,(x1#+x2#)/2,(y1#+y2#)/2,(z1#+z2#)/2,0,0 
	; you could skip creating the above vertex and change the line below to
	; AddTriangle surf,verts,verts+1,verts+0
	; so your line mesh would use less vertices, the drawback is that some videocards (like the matrox g400)
	; aren't able to create a triangle with 2 vertices. so, it's your call :)
	AddVertex surf,x2#,y2#,z2#,1,0
	
	AddTriangle surf,verts,verts+2,verts+1
	
	Return mesh
End Function

; --- set graphics
Graphics3D 640,480,32,0
SetBuffer(BackBuffer())

; --- create scene setup
camPiv = CreatePivot()
camera = CreateCamera(camPiv)
PositionEntity(camera, 0,0,-10)

light=CreateLight(2) 
PositionEntity(light,4,10,0) 
LightRange(light,10)

; --- create test cube
cube=CreateCube()
ScaleMesh(cube, 2,1,1)
EntityAlpha(cube, 0.5)
cube2=CreateCube()
ScaleMesh(cube2, 1.8,0.8,0.8)

; --- create lines
lines = createLine(2,1,1,    1,2,1)
lines = createLine(1,2,1,    0,2.3,1, lines)
lines = createLine(0,2.3,1, -1,2,1, lines)
lines = createLine(-1,2,1,  -2,1,1, lines)
EntityColor(lines, 255,0,0)

; okay, this is a bit cheating and very wrong/memory leak prone and shouldn't be used this way
; but i wanted to show more than one Line quickly

lines = createLine(2,1,-1,    1,2,-1)
lines = createLine(1,2,-1,    0,2.3,-1, lines)
lines = createLine(0,2.3,-1, -1,2,-1, lines)
lines = createLine(-1,2,-1,  -2,1,-1, lines)
EntityColor(lines, 255,0,0)

lines = createLine(-3,1,1,   3,1,1)
EntityColor(lines, 255,0,0)
lines = createLine(-5,1,-1,  4,1,-1)
EntityColor(lines, 255,0,0)
lines = createLine(-4,-1,1,  3,-1,1)
EntityColor(lines, 255,0,0)
lines = createLine(-3,-1,-1, 5,-1,-1)
EntityColor(lines, 255,0,0)

TurnEntity(campiv, 35,35,35)

While Not KeyHit(1)

	; --- camera controls
	scrollwheel = MouseZSpeed()
	If MouseDown(1) Then 
		TurnEntity(camPiv, MouseYSpeed(),-MouseXSpeed(),0)
	Else If scrollwheel <> 0 Then 
		MoveEntity(camera, 0,0,scrollwheel*3)
	Else
		dummy = MouseYSpeed():dummy = MouseXSpeed():dummy = MouseZSpeed() ; prevent mousespeed blips.
	End If

	; --- rendering
	CameraClsMode(camera, 1, 1)
	WireFrame(0)
	ShowEntity(cube)
	ShowEntity(cube2)	
	RenderWorld()
	
	CameraClsMode(camera, 0, 0)
	WireFrame(1)
	HideEntity(cube)
	HideEntity(cube2)	
	RenderWorld()

	; or try this:
	;CameraClsMode(camera, 1, 1)
	;WireFrame(1)
	;HideEntity(cube)
	;HideEntity(cube2)	
	;RenderWorld()
	
	;CameraClsMode(camera, 0, 0)
	;WireFrame(0)
	;ShowEntity(cube)
	;ShowEntity(cube2)	
	;RenderWorld()
	Flip()
Wend

End
