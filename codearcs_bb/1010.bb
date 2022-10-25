; ID: 1010
; Author: fredborg
; Date: 2004-05-03 09:31:50
; Title: Realtime Shadows
; Description: And Fairly Fast  Too :)

;
; Realtime Shadow thingy
;
; Created by Mikkel Fredborg
; Use as you please!
;

Type dl_receiver
	Field mesh
	Field surf
	Field orig
End Type

Type dl_light
	Field entity,camera
	Field width,height
	Field backcull
	Field lastx#,lasty#,lastz#
	Field lastpitch#,lastyaw#,lastroll#
End Type

Global dl_brush
Global dl_tex

Function DL_Init()

	ClearTextureFilters
	dl_tex = CreateTexture(512,512,1+16+32+256)
	TextureBlend dl_tex,5

	dl_brush = CreateBrush()
	BrushBlend dl_brush,2
	BrushFX dl_brush,1
	BrushTexture dl_brush,dl_tex	

End Function

Function DL_Free()

	For dlr.dl_receiver = Each dl_receiver
		FreeEntity dlr\mesh
		Delete dlr
	Next

	For dll.dl_light = Each dl_light
		Delete dll
	Next

	If dl_tex	Then FreeTexture dl_tex
	If dl_brush	Then FreeBrush dl_brush

	dl_tex		= 0
	dl_brush	= 0

End Function

Function DL_SetReceiver(mesh)

	dlr.dl_receiver = New dl_receiver
	dlr\mesh = CreateMesh(mesh)
	dlr\surf = CreateSurface(dlr\mesh)
	PaintSurface dlr\surf,dl_brush
	dlr\orig = mesh
	
End Function

Function DL_SetLight(entity,width=256,height=256,backcull=False)

	dll.dl_light = First dl_light

	If dll = Null
		dll.dl_light = New dl_light
	End If
	
	dll\camera	 = CreateCamera(entity)
	CameraProjMode dll\camera,0
	CameraRange dll\camera,1.0,1000.0
	CameraClsColor dll\camera,255,255,255
	CameraViewport dll\camera,0,0,width,height
	CameraZoom dll\camera,0.002 ;<--- This may need changing depending on the scene and light position
	
	dll\entity 	 = entity
	dll\width	 = width
	dll\height	 = height

	dll\backcull = backcull
		
End Function

Function DL_Update()

	dll.dl_light = First dl_light
	If dll = Null Then Return

	; Hide shadow meshes
	For dlr.dl_receiver = Each dl_receiver
		HideEntity dlr\mesh
	Next
	
	; Render shadow
	CameraProjMode dll\camera,2 ;<---- Works with perspective as well, but you need to change the zoom
	RenderWorld
	CopyRect 0,0,dll\width,dll\height,0,0,BackBuffer(),TextureBuffer(dl_tex)

	; Show shadow meshes
	For dlr.dl_receiver = Each dl_receiver
		ShowEntity dlr\mesh
	Next
	
	x# = EntityX(dll\entity,True)
	y# = EntityY(dll\entity,True)
	z# = EntityZ(dll\entity,True)

	pitch# 	= EntityPitch(dll\entity,True)
	yaw# 	= EntityYaw(dll\entity,True)
	roll# 	= EntityRoll(dll\entity,True)
		
	For dlr.dl_receiver = Each dl_receiver

		If dll\backcull
			
			;
			; Remove backfacing triangles
			If x<>dll\lastx Or y<>dll\lasty Or z<>dll\lastz Or pitch<>dll\lastpitch Or yaw<>dll\lastyaw Or roll<>dll\lastroll 
				ClearSurface dlr\surf
				n_surfs = CountSurfaces(dlr\orig)
				For s = 1 To n_surfs
					surf = GetSurface(dlr\orig,s)
					n_tris = CountTriangles(surf)-1
					For t = 0 To n_tris
						v0 = TriangleVertex(surf,t,0)
						v1 = TriangleVertex(surf,t,1)
						v2 = TriangleVertex(surf,t,2)
						
						ex0# = VertexX(surf,v1) - VertexX(surf,v0)				
						ey0# = VertexY(surf,v1) - VertexY(surf,v0)
						ez0# = VertexZ(surf,v1) - VertexZ(surf,v0)
		
						ex1# = VertexX(surf,v2) - VertexX(surf,v0)				
						ey1# = VertexY(surf,v2) - VertexY(surf,v0)
						ez1# = VertexZ(surf,v2) - VertexZ(surf,v0)
		
						; Triangle normal
						nx# = ey0 * ez1 - ez0 * ey1
						ny# = ez0 * ex1 - ex0 * ez1
						nz# = ex0 * ey1 - ey0 * ex1
						
						; Transform it compared to light
						TFormNormal nx,ny,nz,dlr\orig,dll\entity
						If TFormedZ()<0.0
							nv0 = AddVertex(dlr\surf,VertexX(surf,v0),VertexY(surf,v0),VertexZ(surf,v0))
							nv1 = AddVertex(dlr\surf,VertexX(surf,v1),VertexY(surf,v1),VertexZ(surf,v1))
							nv2 = AddVertex(dlr\surf,VertexX(surf,v2),VertexY(surf,v2),VertexZ(surf,v2))
							AddTriangle dlr\surf,nv0,nv1,nv2
						End If
					Next
				Next
			End If
			
		ElseIf CountVertices(dlr\surf) = 0
		
			;
			; No backface culling
			ClearSurface dlr\surf
			n_surfs = CountSurfaces(dlr\orig)
			For s = 1 To n_surfs
				surf = GetSurface(dlr\orig,s)
				n_tris = CountTriangles(surf)-1
				For t = 0 To n_tris
					v0 = TriangleVertex(surf,t,0)
					v1 = TriangleVertex(surf,t,1)
					v2 = TriangleVertex(surf,t,2)

					nv0 = AddVertex(dlr\surf,VertexX(surf,v0),VertexY(surf,v0),VertexZ(surf,v0))
					nv1 = AddVertex(dlr\surf,VertexX(surf,v1),VertexY(surf,v1),VertexZ(surf,v1))
					nv2 = AddVertex(dlr\surf,VertexX(surf,v2),VertexY(surf,v2),VertexZ(surf,v2))
					AddTriangle dlr\surf,nv0,nv1,nv2
				Next
			Next			
			
		End If
		
		;
		; Calculate uv's
		n_verts = CountVertices(dlr\surf)-1
		For v = 0 To n_verts
			TFormPoint VertexX(dlr\surf,v),VertexY(dlr\surf,v),VertexZ(dlr\surf,v),dlr\mesh,0
			CameraProject dll\camera,TFormedX(),TFormedY(),TFormedZ()
			tu# = ProjectedX()/Float(dll\width)
			tv# = ProjectedY()/Float(dll\height)
			VertexTexCoords dlr\surf,v,tu,tv
		Next
	Next

	; Update positions
	dll\lastx = x
	dll\lasty = y
	dll\lastz = z
	dll\lastpitch	= pitch
	dll\lastyaw		= yaw
	dll\lastroll	= roll

	; disable shadow camera
	CameraProjMode dll\camera,0
	
End Function



;
; Example 
;
Graphics3D 800,600,0,2
SetBuffer BackBuffer()

HidePointer
AmbientLight 32,32,32
SeedRnd MilliSecs()

camera = CreateCamera()

bob = CreateCube()
ScaleMesh bob,15,5,5
EntityColor bob,255,255,255

For i = 0 To 20
	cube = CreateCube()
	ScaleMesh cube,Rnd(1,10),Rnd(1,10),Rnd(1,10)
	RotateMesh cube,Rnd(-90,90),Rnd(-180,180),Rnd(-180,180)
	PositionMesh cube,Rnd(-100,100),Rnd(-100,100),Rnd(-100,100)
	AddMesh cube,bob
	FreeEntity cube
Next

For i = 0 To 20
	sphere = CreateSphere()
	s# = Rnd(4,20)
	ScaleMesh sphere,s,s,s 
	PositionMesh sphere,Rnd(-100,100),Rnd(-100,100),Rnd(-100,100)
	AddMesh sphere,bob
	FreeEntity sphere	
Next

scene = CreateSphere(32)
ScaleEntity scene,100,100,100
FlipMesh scene

light = CreateLight()
RotateEntity light,70,40,0
PositionEntity light,0,1000,0
PointEntity light,scene

DL_Init()
DL_SetReceiver(scene)
DL_SetLight(light,512,512)

spd# = 2.0

zoom# = 1.0

a# = 0.0
b# = 0.0

Repeat

	MoveEntity camera,(KeyDown(205)-KeyDown(203))*spd,0,(KeyDown(200)-KeyDown(208))*spd
	TurnEntity camera,-MouseYSpeed()*0.25,-MouseXSpeed()*0.25,0
	RotateEntity camera,EntityPitch(camera,True),EntityYaw(camera,True),0

	If KeyDown(2) Or KeyDown(3)
		zoom# = zoom + (KeyDown(2)-KeyDown(3))*0.01
		CameraZoom camera,zoom
	End If

	MoveMouse 320,240

	b = b + 1

	RotateEntity light,90+(Sin(b)*20),20+(Cos(b)*20),0

	a# = a+1
	PositionEntity bob,Sin(a)*50,(Sin(a)*50)+100,Cos(a)*50
	TurnEntity bob,1,1,1

	HideEntity scene
	CameraProjMode camera,0
	EntityColor bob,0,0,0

	DL_Update()

	EntityColor bob,255,255,255	
	ShowEntity scene
	CameraProjMode camera,1

	RenderWorld
	
	Text 0,0,zoom
	Flip

Until KeyHit(1)

DL_Free()

End
