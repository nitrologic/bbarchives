; ID: 362
; Author: BadJim
; Date: 2002-07-06 17:07:17
; Title: Mesh terrain
; Description: Non-dynamic terrain made from meshes. High poly

Graphics3D 800,600

Global mills=MilliSecs()
Global lod=2000
Global wframe=0
AmbientLight 50,50,50
landscapesize=256


cam=CreateCamera()
PositionEntity cam,landscapesize/2,10,landscapesize/2
CameraViewport cam,0,0,GraphicsWidth(),GraphicsHeight()
CameraRange cam,0.2,80
SetBuffer BackBuffer()

plane=CreatePlane()
PositionEntity plane,0,-100,0

light=CreateLight(2)
PositionEntity light,0,35,0
LightRange light,50


terrain=makeheightmap(landscapesize,landscapesize)
maketerrainpatchmesh(terrain,landscapesize,landscapesize)
Global lodterrain=makeoldterrain(terrain,landscapesize)
HideEntity lodterrain

Repeat
	controls(cam)
	render()
	Delay 1
Until KeyHit(1)
End

Type terrainpatch;------------------------------
Field patch
End Type

Function render();-----------------------------------------------------------
	UpdateWorld()
	RenderWorld()
	WritePixel GraphicsWidth()/2,GraphicsHeight()/2,16777215
	Text 0,0,"polys : "+TrisRendered()
	Text 0,18,"millisec render : "+(MilliSecs()-mills)
	Text 0,50,"w,a,s,d,space,c + mouse =movement"
	Text 0,68,"1=lod terrain, 2=mesh terrain, +/- = adjust lod, tab=wireframe"
	mills=MilliSecs()
	Flip
End Function


Function controls(cam);---------------------------------------------------------
	If KeyDown(17) Then MoveEntity cam,0,0,0.4;forward
	If KeyDown(31) Then MoveEntity cam,0,0,-0.4;back
	If KeyDown(30) Then MoveEntity cam,-0.4,0,0;left
	If KeyDown(32) Then MoveEntity cam,0.4,0,0;right
	If KeyDown(57) Then MoveEntity cam,0,0.4,0;up
	If KeyDown(46) Then MoveEntity cam,0,-0.4,0;down

	
	TurnEntity cam,MouseYSpeed(),0-MouseXSpeed(),0
	RotateEntity cam,EntityPitch(cam),EntityYaw(cam),0
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	
	If KeyHit(2)
		For terrainpatch.terrainpatch=Each terrainpatch
			HideEntity terrainpatch\patch
		Next
		ShowEntity lodterrain
	EndIf
	If KeyHit(3)
		For terrainpatch.terrainpatch=Each terrainpatch
			ShowEntity terrainpatch\patch
		Next
		HideEntity lodterrain
	EndIf
	If KeyHit(12) Then lod=lod/1.41
	If KeyHit(13) Then lod=lod*1.41
	TerrainDetail lodterrain,lod,True
	If KeyHit(15) Then wframe=1-wframe
	WireFrame wframe
End Function


Function maketerrainpatchmesh(heightmap,xlength,zlength);------------------------------------------------------------------

	For z=0 To zlength-32 Step 32
		For x = 0 To xlength-32 Step 32
			terrainpatch.terrainpatch = New terrainpatch
			terrainpatch\patch=maketerrainpatch(heightmap,xlength,zlength,x,z,x+32,z+32)
		Next
	Next
	
	
	Return terrainpatch\patch
End Function

Function maketerrainpatch(heightmap,xlength,zlength,x1,z1,x2,z2);-----------------------------------------------
	terrain=CreateMesh()
	brush=CreateBrush()
	BrushFX brush,0
	tex=maketexture()
	ScaleTexture tex,10,10
	BrushTexture brush,tex
	surface=CreateSurface(terrain,brush)
	
	
	For z=z1 To z2;add vertices
		For x=x1 To x2
			AddVertex surface,x,PeekFloat(heightmap,(x+z*xlength)*4),z,x,z
		Next
	Next

	For z=0 To z2-(z1+2) Step 2;add triangles
		For x=0 To x2-(x1+2) Step 2
			AddTriangle surface,	(x+0)+(z+0)*(x2-x1+1),	(x+0)+(z+1)*(x2-x1+1),	(x+1)+(z+0)*(x2-x1+1)
			AddTriangle surface,	(x+1)+(z+1)*(x2-x1+1),	(x+1)+(z+0)*(x2-x1+1),	(x+0)+(z+1)*(x2-x1+1)

			AddTriangle surface,	(x+1)+(z+0)*(x2-x1+1),	(x+1)+(z+1)*(x2-x1+1),	(x+2)+(z+0)*(x2-x1+1)
			AddTriangle surface,	(x+2)+(z+1)*(x2-x1+1),	(x+2)+(z+0)*(x2-x1+1),	(x+1)+(z+1)*(x2-x1+1)

			AddTriangle surface,	(x+0)+(z+1)*(x2-x1+1),	(x+0)+(z+2)*(x2-x1+1),	(x+1)+(z+1)*(x2-x1+1)
			AddTriangle surface,	(x+1)+(z+2)*(x2-x1+1),	(x+1)+(z+1)*(x2-x1+1),	(x+0)+(z+2)*(x2-x1+1)

			AddTriangle surface,	(x+1)+(z+1)*(x2-x1+1),	(x+1)+(z+2)*(x2-x1+1),	(x+2)+(z+1)*(x2-x1+1)
			AddTriangle surface,	(x+2)+(z+2)*(x2-x1+1),	(x+2)+(z+1)*(x2-x1+1),	(x+1)+(z+2)*(x2-x1+1)

		Next
	Next

	ScaleEntity terrain,1,5,1
	UpdateNormals terrain	
	
	Return terrain
End Function


Function makeoldterrain(heightmap,scope);---------------------------------------------------------
	terrain=CreateTerrain(scope)
	For z=0 To scope-1;set heightmap
		For x=0 To scope-1
			ModifyTerrain terrain,x,z,PeekFloat( heightmap,(z*scope+x)*4)
		Next
	Next
	tex=makeTexture()
	ScaleTexture tex,10,10
	EntityTexture terrain,tex
	ScaleEntity terrain,1,5,1
	TerrainShading terrain,True
	Return terrain
End Function

Function makeheightmap(xlength,zlength);----------------------------------------------------------
	xlength=xlength+1
	zlength=zlength+1
	heightmap=CreateBank(xlength*zlength*4)
	
	For z=0 To zlength-1;generate random data
		For x=0 To xlength-1
			PokeFloat heightmap,(z*xlength+x)*4,Rnd(1)
		Next
	Next
	
	For n=1 To 4;smooth terrain.................................
		If KeyHit(1) Then End;emergency get out
		For z=0 To zlength-1;x smooth
			For x=0 To xlength-1
				height1#=PeekFloat(heightmap,(z*xlength+x)*4)
				height2#=PeekFloat(heightmap,(z*xlength+((x+1)Mod xlength))*4)
				PokeFloat heightmap,(z*xlength+x)*4,(height1#+height2#)/2; +Rnd(0.03)
			Next
		Next
		For z=0 To zlength-1;z smooth
			For x=0 To xlength-1
				height1#=PeekFloat(heightmap,(z*xlength+x)*4)
				height2#=PeekFloat(heightmap,(((z+1)Mod zlength)*xlength+x)*4)
				PokeFloat heightmap,(z*xlength+x)*4,(height1#+height2#)/2;+Rnd(0.03)
			Next
		Next
		Cls
		Text 0,0,n
		Flip
	Next


	lowest#=100;normalise..............................
	highest#=0
	For z=0 To zlength-1
		For x=0 To xlength-1
			If PeekFloat(heightmap,(z*xlength+x)*4)<lowest# Then lowest#=PeekFloat(heightmap,(z*xlength+x)*4)
			If PeekFloat(heightmap,(z*xlength+x)*4)>highest# Then highest#=PeekFloat(heightmap,(z*xlength+x)*4)
		Next
	Next

	lowest#=lowest#-0.01
	highest#=highest#+0.01
	For z=0 To zlength-1
		For x=0 To xlength-1
			PokeFloat heightmap,(z*xlength+x)*4,(PeekFloat(heightmap,(z*xlength+x)*4)-lowest#) / (highest#-lowest#)
		Next
	Next

	
	Return heightmap
End Function

Function maketexture();-----------------------------------------------------------------
	tex=CreateTexture(256,256,8)
		For y=0 To 255
			For x=0 To 255
				WritePixel x,y,(Rand(64)+32)*66049,TextureBuffer(tex)
			Next
		Next
	ScaleTexture tex,4,4
	Return tex
End Function
