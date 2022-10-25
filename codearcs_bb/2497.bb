; ID: 2497
; Author: Santiworld
; Date: 2009-06-05 10:37:51
; Title: Mesh deform
; Description: deform all Vertex of a mesh

;trying to deform an mesh
;by latatoy

Graphics3D  640,480
HidePointer()

;scenary
Global cam = CreateCamera()
CameraClsColor cam,200,200,255
Global zero = CreateSphere(4)
EntityColor zero,255,0,0
PositionEntity cam,0,5,-9
luz = CreateLight(2)
PositionEntity luz,0,120,0
LightRange luz,100
;piso = CreatePlane(4)
;EntityColor piso,200,255,160


;original mesh
Global original = CreateCube()
PositionEntity original,-3,0,0
EntityColor original,100,255,100

;this is the mesh i wont to deform
Global target = CreateSphere(16)
PositionEntity target,3,0,0
EntityColor target,100,100,255

Type vert
	Field mesh
	Field id
	Field surface
	Field x#
	Field y#
	Field z#
End Type

prepared(target)

Color 0,0,0
While Not KeyHit(1)
	
	UpdateWorld()
	RenderWorld()
	cams()
	
	If MouseDown(1) Then deform(target)
	
	v= 0
	For a.vert = Each vert
		v= v + 1
	Next
	
	Text 10,30,"VERTICES : " + v
	
	Text 10,10,"Press MOUSE BOTTON 1 to deform the blue mesh"
	
	Flip 
Wend

End


Function prepared(mesh)
	
	surf=GetSurface(target,1)
	vertices=CountVertices(surf)
	
	For i=0 To vertices-1
		a.vert=New vert
		a\mesh=mesh
		a\id=i
		a\surface=surf
		a\x = VertexX(surf,i)
		a\y = VertexY(surf,i)
		a\z = VertexZ(surf,i)
	Next
	
End Function

Function deform(mesh)
	Text GraphicsWidth()*.5,GraphicsHeight()-20,"Deforming...",1,1
	For a.vert = Each vert
		
		d#= .01
		dx# = Rnd(-d,d)
		dy# = Rnd(-d,d)
		dz# = Rnd(-d,d)
		
		VertexCoords a\surface,a\id,VertexX(a\surface,a\id)+ dx,VertexY(a\surface,a\id)+ dy,VertexZ(a\surface,a\id)+ dz
		
		
	Next
End Function

Function cams()
	MoveEntity cam,MouseXSpeed()*.1,0,-MouseYSpeed()*.1
	PositionEntity cam,EntityX(cam),5,EntityZ(cam)
	MoveMouse 200,200
	PointEntity cam,zero
End Function
