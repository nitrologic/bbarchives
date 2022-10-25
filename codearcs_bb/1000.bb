; ID: 1000
; Author: fredborg
; Date: 2004-04-17 06:32:28
; Title: Textured Spotlight
; Description: One way of projecting a texture onto meshes

;
;
; Textured spotlight thingy
;
; Created by Mikkel Fredborg
; Use as you please!
;

Type dl_receiver
	Field mesh
End Type

Type dl_light
	Field entity
	Field range#
	Field scale#
	Field intensity#
	Field flicker#
	Field flickerrange#
	Field r#,g#,b#
End Type

Global dl_brush
Global dl_tex

Function DL_Init()

	ClearTextureFilters
	dl_tex = LoadTexture("spotlight2.png",1+16+32)

	dl_brush = CreateBrush()
	BrushBlend dl_brush,3
	BrushFX dl_brush,1+2
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
	dlr\mesh = CopyMesh(mesh)
	PaintMesh dlr\mesh,dl_brush
	
End Function

Function DL_SetLight(entity,range#=500.0,scale#=0.75,intensity#=2.0,flicker#=0.05,flickerrange#=0.5,r#=200,g#=220,b#=255)

	dll.dl_light = First dl_light

	If dll = Null
		dll.dl_light = New dl_light
	End If
	
	dll\entity 	 = entity
	dll\range  	 = range
	dll\scale		= scale
	dll\intensity = intensity
	dll\flicker	 = flicker
	dll\flickerrange = flickerrange
	
	dll\r		= r
	dll\g		= g
	dll\b		= b
	
End Function

Function DL_Update()

	dll.dl_light = First dl_light
	If dll = Null Then Return

	If Rnd(0.0,1.0)<dll\flicker
		intensity# = dll\intensity*Rnd(dll\flickerrange,1.0)
	Else
		intensity# = dll\intensity
	End If

	For dlr.dl_receiver = Each dl_receiver
		mesh	= dlr\mesh
		n_surfs = CountSurfaces(mesh)
		For s = 1 To n_surfs
			surf = GetSurface(mesh,s)
			n_verts = CountVertices(surf)-1
			For v = 0 To n_verts
				TFormPoint VertexX(surf,v),VertexY(surf,v),VertexZ(surf,v),mesh,dll\entity
				x# = TFormedX()
				y# = TFormedY()
				z# = TFormedZ()
				
				dist# = Sqr(x*x + y*y + z*z)*dll\scale
				tu# = (x/dist)+0.5
				tv# = 1.0-((y/dist)+0.5)
				
				VertexTexCoords surf,v,tu,tv

				If z>dll\range Then z = dll\range
				falloff# = 1.0-(z/dll\range)
				If falloff<0.0 Then falloff = 0.0
				If falloff>1.0 Then falloff = 1.0
				
				TFormNormal VertexNX(surf,v),VertexNY(surf,v),VertexNZ(surf,v),mesh,dll\entity
				dot# = -TFormedZ()*falloff*intensity
				If dot>0.0
					VertexColor surf,v,dot*dll\r,dot*dll\g,dot*dll\b
				Else
					VertexColor surf,v,0,0,0
				End If
			Next
		Next		
	Next

End Function



;
; Example 
;
Graphics3D 640,480,0,2
SetBuffer BackBuffer()

HidePointer
AmbientLight 12,24,50
SeedRnd MilliSecs()

light = CreateLight()
RotateEntity light,70,40,0
LightColor light,50,50,50

camera = CreateCamera()
scene = CreateMesh()

For i = 0 To 100

	cube = CreateCube()
	ScaleMesh cube,Rnd(1,10),Rnd(1,10),Rnd(1,10)
	RotateMesh cube,Rnd(-90,90),Rnd(-180,180),Rnd(-180,180)
	PositionMesh cube,Rnd(-100,100),Rnd(-100,100),Rnd(-100,100)
	
	AddMesh cube,scene
	
	FreeEntity cube
	
Next

cube = CreateCube()
ScaleMesh cube,150,150,150
FlipMesh cube
AddMesh cube,scene
FreeEntity cube

DL_Init()
DL_SetReceiver(scene)
DL_SetLight(camera)

spd# = 2.0

Repeat

	MoveEntity camera,(KeyDown(205)-KeyDown(203))*spd,0,(KeyDown(200)-KeyDown(208))*spd
	TurnEntity camera,-MouseYSpeed()*0.25,-MouseXSpeed()*0.25,0
	RotateEntity camera,EntityPitch(camera,True),EntityYaw(camera,True),0

	MoveMouse 320,240

	DL_Update()

	RenderWorld
	Flip

Until KeyHit(1)

DL_Free()

End
