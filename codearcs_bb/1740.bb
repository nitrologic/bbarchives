; ID: 1740
; Author: b32
; Date: 2006-07-02 15:26:08
; Title: saveanimmesh
; Description: save animated x file

;-------------------------------------------------------------------------------------------------------
;												SaveAnimFile()
;-------------------------------------------------------------------------------------------------------
;save an animated mesh to the ".x" format
;the x format doesn't support vertex weight, only parenting objects to each other
;
;SYNTAX:
;
; SaveAnimFile mesh, filename$
;
;   mesh     - mesh handle
;   filename - name of file
;

;print debug output
Const   debug = False

Global	frameindex
Global 	meshindex
Global	animindex
Global	orgmesh

;save animated mesh
Function SaveAnimFile(mesh, filename$)
	
	;reset counters
	frameindex = 0
	meshindex = 0
	animindex = 0

	;open file for writing
	ff = WriteFile(filename)
	
	;write header data
	WriteLine ff, "xof 0302txt 0064"
	WriteLine ff, " " 
	WriteLine ff, "Header {"
	WriteLine ff, " 1;"
	WriteLine ff, " 0;"
	WriteLine ff, " 1;"
	WriteLine ff, "}"
	WriteLine ff, " "
		
	;save object "mesh" to file
	SaveAnimObj(ff, mesh)
	
	;save animation data to file
	SaveAnimAnim(ff, mesh)
	
	;close file
	CloseFile ff
	
End Function

;-------------------------------------------------------------------------------------------------------
;												SaveAnimObj()
;-------------------------------------------------------------------------------------------------------
;save object and children (recursive)
Function SaveAnimObj(ff, mesh)
	
	;generate name
	name$ = "Frame" + frameindex
	frameindex = frameindex + 1
	
	;start frame block
	WriteLine ff, "Frame frm_" + name$ + " {"
	WriteLine ff, " "
	WriteLine ff, "  FrameTransformMatrix { "
	TFormPoint 1, 0, 0, mesh, GetParent(mesh)
	xx# = TFormedX() - EntityX(mesh)
	xy# = TFormedY() - EntityY(mesh)
	xz# = TFormedZ() - EntityZ(mesh)
	TFormPoint 0, 1, 0, mesh, GetParent(mesh)
	yx# = TFormedX() - EntityX(mesh)
	yy# = TFormedY() - EntityY(mesh)
	yz# = TFormedZ() - EntityZ(mesh)
	TFormPoint 0, 0, 1, mesh, GetParent(mesh)
	zx# = TFormedX() - EntityX(mesh)
	zy# = TFormedY() - EntityY(mesh)
	zz# = TFormedZ() - EntityZ(mesh)
	WriteLine ff, NoNan(xx) + "," + NoNan(xy) + "," + NoNan(xz) + "," + "0.000000,"
	WriteLine ff, NoNan(yx) + "," + NoNan(yy) + "," + NoNan(yz) + "," + "0.000000,"
	WriteLine ff, NoNan(zx) + "," + NoNan(zy) + "," + NoNan(zz) + "," + "0.000000,"
	WriteLine ff, NoNan(EntityX(mesh)) + "," + NoNan(EntityY(mesh)) + "," +  NoNan(EntityZ(mesh)) + "," + "1.000000;"
	WriteLine ff, "  }"
	WriteLine ff, " "
	
	;save children
	For i = 1 To CountChildren(mesh)
		
		g = GetChild(mesh, i)
		SaveAnimObj ff, g
		
	Next

	;if entity is a mesh, then save the mesh data	
	If EntityClass$(mesh) = "Mesh" Then SaveAnimMesh(ff, mesh)
	
	;end frame block	
	WriteLine ff, "}"
	WriteLine ff, " "
		
End Function


;-------------------------------------------------------------------------------------------------------
;											SaveAnimMesh()
;-------------------------------------------------------------------------------------------------------
;save mesh data to file
Function SaveAnimMesh(ff, mesh)

	;generate name
	name$ = "Mesh" + meshindex
	meshindex = meshindex + 1
	
	;debug	
	If debug Then Print "Mesh->Frame:" + name$

	;loop through all surfaces
	For surfindex = 1 To CountSurfaces(mesh)
	
		;generate name
		objectname$ = name$ + "_surface" + surfindex

		;debug
		If debug Then Print "Surface->Mesh:" + objectname$

		;get surface
		surf 		= GetSurface(mesh, surfindex)
		numVert  	= CountVertices(surf)
		numTris 	= CountTriangles(surf)
		
		;get texname
		brush 		= GetSurfaceBrush(surf)
		tex			= GetBrushTexture(brush)
		texname$	= TextureName$(tex)
		If Instr(texname$, "\") > 0 Then
			test = 0
			For i = Len(texname$) To 1 Step -1
				 If Mid$(texname$, i, 1) = "\" Then test = i: Exit
			Next
			texname$ = Mid$(texname$, test + 1, Len(texname$))
		End If
		FreeTexture tex
		FreeBrush brush

		;start mesh block		
		WriteLine 	ff, " Mesh " + objectname$ + " { "
			
		;write vertices
		WriteLine 	ff, " " + numVert + ";"
		
		For i = 0 To numVert - 1
			If i = numVert - 1 Then st$ = ";" Else st$ = ","
			xx# = VertexX(surf, i)
			yy# = VertexY(surf, i)
			zz# = VertexZ(surf, i)
			WriteLine ff, NoNan(xx#) + ";" + NoNan(yy#) + ";" + NoNan(zz#) + ";" + st$
		Next
	
		;write triangles	
		WriteLine ff, " " + numTris + ";"
		
		For i = 0 To numTris - 1
			aa = TriangleVertex(surf, i, 0)
			bb = TriangleVertex(surf, i, 1)
			cc = TriangleVertex(surf, i, 2)
			If i = numTris - 1 Then st$ = ";" Else st$ = ","
			WriteLine ff, "3;" + aa + "," + bb + "," + cc + ";" + st$
		Next
	
		;write material properties	
		WriteLine ff, " "
		WriteLine ff, "MeshMaterialList {"
		WriteLine ff, "1;"
		;----------------------------------------
		;This part is not compatible with Blender
		;Blender wants all triangles defined here
		WriteLine ff, "1;"
		WriteLine ff, "0;;"
		;Besides this, Blender still complains
		;about these files, at least when they
		;are animated
		;----------------------------------------
		WriteLine ff, " "
			
		WriteLine ff, "Material {"
		WriteLine ff, " 1.000000,1.000000,1.000000,1.000000;;"
		WriteLine ff, " 1.000000;"
		WriteLine ff, " 0.500000,0.500000,0.500000;;"
		WriteLine ff, " 0.000000,0.000000,0.000000;;"
		
		;write texture file name	
		If texname$ <> "" Then
			WriteLine ff, "      TextureFilename {"
			WriteLine ff, "        " + Chr$(34) + texname$ + Chr$(34) + "; }"
			If debug Then Print "texture:" + texname$
		End If
		
		;close material block		
		WriteLine ff, "}"
		WriteLine ff, "}"
		WriteLine ff, " "
	
		;write normals	
		WriteLine ff, "MeshNormals {"	
		WriteLine ff, " " + numVert + ";"
		
		;write normal vertices
		For i = 0 To numVert - 1
			If (i = numVert - 1) Then st$ = ";" Else st$ = ","
			xx# 		= VertexNX(surf, i)
			yy# 		= VertexNY(surf, i)
			zz# 		= VertexNZ(surf, i)
			WriteLine 	ff, NoNan(xx#) + ";" + NoNan(yy#) + ";" + NoNan(zz#) + ";" + st$
		Next
		
		;write normal triangles
		WriteLine ff, " " + numTris + ";"
		For i = 0 To numTris - 1
			If (i = numTris - 1) Then st$ = ";" Else st$ = ","
			aa 			= TriangleVertex(surf, i, 0)
			bb 			= TriangleVertex(surf, i, 1)
			cc 			= TriangleVertex(surf, i, 2)
			WriteLine 	ff, "3;" + aa + "," + bb + "," + cc + ";" + st$
		Next
	
		;close normals block	
		WriteLine ff, 	"}"
		WriteLine ff, 	" "
		
		;write texture coordinates U,V
		WriteLine ff, 	"MeshTextureCoords {"	
		WriteLine ff, 	" " + numVert + ";"
		
		For i = 0 To numVert - 1
			If (i = numVert - 1) Then st$ = ";" Else st$ = ","
			uu# 		= VertexU(surf, i)
			vv# 		= VertexV(surf, i)
			WriteLine 	ff, NoNan(uu#) + ";" + NoNan(vv#) + ";" + st$
		Next
	
		WriteLine ff, "  }"
	
		;close mesh block	
		WriteLine ff, " }"
		WriteLine ff, " "
		
	Next

End Function

;-------------------------------------------------------------------------------------------------------
;												SaveAnimAnim()
;-------------------------------------------------------------------------------------------------------
;save animation sequence data to file
Function SaveAnimAnim(ff, mesh)

	If AnimLength(mesh) < 0 Then Return
	
	;used for "Animate" command
	orgmesh = mesh
	
	;start animation sequence block
	WriteLine ff, "AnimationSet Sequence01 {"

	;save animation data	
	SaveAnimDat(ff, mesh, AnimLength(mesh) + 1)
		
	;close animation sequence block
	WriteLine ff, "}"
			
End Function
			
;-------------------------------------------------------------------------------------------------------
;												SaveAnimDat()
;-------------------------------------------------------------------------------------------------------
;save animation data
Function SaveAnimDat(ff, mesh, maxframe)
	
	animname$   = "ani_" + animindex
	objectname$ = "Frame" + animindex
	animindex = animindex + 1
	
	If debug Then Print "Animation" + animname$
	If debug Then Print "-->" + objectname$
	
	WriteLine ff, "Animation " + animname$ + " {"
	WriteLine ff, " "
	WriteLine ff, "{frm_" + objectname$ + "} "
	WriteLine ff, " "
				
		;write rotation data
		WriteLine ff, "AnimationKey {"
		WriteLine ff, "0;"
		WriteLine ff, (maxframe) + ";"
			For i = 0 To maxframe
				iSetAnimTime orgmesh, i
				WriteLine ff, RotationToString(i, EntityPitch(mesh), EntityYaw(mesh), EntityRoll(mesh))
			Next	
		WriteLine ff, "}"
		WriteLine ff, " "
		
		;write scaling data
		WriteLine ff, "AnimationKey {"
		WriteLine ff, "1;"
		WriteLine ff, (maxframe) + ";"
			For i = 0 To maxframe
				iSetAnimTime orgmesh, i
				WriteLine ff, ScalingToString(i, EntityWidth(mesh), EntityHeight(mesh), EntityDepth(mesh))
			Next
		WriteLine ff, "}"
		WriteLine ff, " "

		;write position data
		WriteLine ff, "AnimationKey {"
		WriteLine ff, "2;"
		WriteLine ff, (maxframe) + ";"
			For i = 0 To maxframe
				iSetAnimTime orgmesh, i
				WriteLine ff, PositionToString(i, EntityX(mesh), EntityY(mesh), EntityZ(mesh))
			Next
		WriteLine ff, "}"
		WriteLine ff, " "
	
	;write animation options
	WriteLine ff, "AnimationOptions {"
	WriteLine ff , "1;"		;0 = closed (default)  1 = open
	WriteLine ff , "1; }"	;0 = splines  1 = linear
	
	;close animation block
	WriteLine ff, "}"
	
	;save children
	For i = 1 To CountChildren(mesh)
		
		g = GetChild(mesh, i)
		
		SaveAnimDat(ff, g, maxframe)
	
	Next			
	
End Function

;-----------------------------------------------------------------------------------------------------
; 										    RotationToString$()
;-----------------------------------------------------------------------------------------------------
;convert rotation information to string
Function RotationToString$(ptime, iPitch#, iYaw#, iRoll#)	

	;this code was written by LeadWerks
	;http://www.blitzbasic.com/Community/posts.php?topic=51579
	sp# = Sin(iYaw   / 2)
	cp# = Cos(iYaw   / 2)
	sy# = Sin(iRoll  / 2)
	cy# = Cos(iRoll  / 2)
	sr# = Sin(iPitch / 2)
	cr# = Cos(iPitch / 2)
	
	w# = + (cr * cp * cy - sr * sp * sy)
	x# = - (sr * cp * cy - cr * sp * sy)
	y# = + (cr * sp * cy + sr * cp * sy)
	z# = - (sr * sp * cy + cr * cp * sy)
	
	Return ptime + "; 4; " + NoNan(w) + "," + NoNan(x) + "," + NoNan(y) + "," + NoNan(z) + ";;;"
	
End Function

;-----------------------------------------------------------------------------------------------------
;											 PositionToString$()
;-----------------------------------------------------------------------------------------------------
;convert position information to string
Function PositionToString$(ptime, iX#, iY#, iZ#)
	
	Return pTime + "; 3;" + NoNan(iX) + "," + NoNan(iY) + "," + NoNan(iZ) + ";;,"
	
End Function

;-----------------------------------------------------------------------------------------------------
;											ScalingToString$()
;-----------------------------------------------------------------------------------------------------
;convert scaling information to string
Function ScalingToString$(pTime, iWidth#, iHeight#, iDepth#)
	
	Return pTime + "; 3;" + NoNan(iWidth) + "," + NoNan(iHeight) + "," + NoNan(iDepth) + ";;,"
	
End Function

;-----------------------------------------------------------------------------------------------------
;											EntityWidth()
;-----------------------------------------------------------------------------------------------------
;returns width of an entity
Function EntityWidth#( mesh )
	
	If EntityClass$(mesh) <> "Mesh" Then Return 1
	If MeshWidth(mesh) = 0 Then Return 1

	TFormPoint MeshWidth(mesh), 0, 0, mesh, 0	
	xx# = TFormedX()
	yy# = TFormedY()
	zz# = TFormedZ()
	TFormPoint 0, 0, 0, mesh, 0	
	xx# = TFormedX() - xx
	yy# = TFormedY() - yy
	zz# = TFormedZ() - zz	
	ll# = Sqr(xx * xx + yy * yy + zz * zz) / MeshWidth(mesh)
	
	If ll = 0 Then ll = 1
	Return ll
	
End Function

;-----------------------------------------------------------------------------------------------------
;											EntityHeight()
;-----------------------------------------------------------------------------------------------------
;returns height of an entity
Function EntityHeight#( mesh )

	If EntityClass$(mesh) <> "Mesh" Then Return 1
	If MeshHeight(mesh) = 0 Then Return 1
	
	TFormPoint 0, MeshHeight(mesh), 0, mesh, 0
	xx# = TFormedX()
	yy# = TFormedY()
	zz# = TFormedZ()
	TFormPoint 0, 0, 0, mesh, 0
	xx# = TFormedX() - xx
	yy# = TFormedY() - yy
	zz# = TFormedZ() - zz
	ll# = Sqr(xx * xx + yy * yy + zz * zz) / MeshHeight(mesh)
	
	If ll = 0 Then ll = 1
	Return ll
	
End Function

;-----------------------------------------------------------------------------------------------------
;											EntityDepth()
;-----------------------------------------------------------------------------------------------------
;returns depth of an entity
Function EntityDepth#( mesh )

	If EntityClass$(mesh) <> "Mesh" Then Return 1
	If MeshDepth(mesh) = 0 Then Return 1
	
	TFormPoint 0, 0, MeshDepth(mesh), mesh, 0	
	xx# = TFormedX()
	yy# = TFormedY()
	zz# = TFormedZ()
	TFormPoint 0, 0, 0, mesh, 0
	xx# = TFormedX() - xx
	yy# = TFormedY() - yy
	zz# = TFormedZ() - zz
	
	ll# = Sqr(xx * xx + yy * yy + zz * zz) / MeshDepth(mesh)
	
	If ll = 0 Then ll = 1
	Return ll
		
End Function

;-----------------------------------------------------------------------------------------------------
;											    iSetAnimTime()
;-----------------------------------------------------------------------------------------------------
;user defined SetAnimTime
Function iSetAnimTime( mesh, time# )
	If time >= AnimLength(mesh) Then time = AnimLength(mesh) - 0.01
	SetAnimTime mesh, time
	If debug Then If time Mod 10 = 0 Then Print "frame:" + time
End Function

;-----------------------------------------------------------------------------------------------------
;												   NoNan()
;-----------------------------------------------------------------------------------------------------
;avoids writing 'Nan' (not a number) to a file.
Function NoNan#(t$)

	If t$ = "NaN" Then t$ = "0"
	Return t$
	
End Function

;-----------------------------------------------------------------------------------------------------
;										    Test with Mak_Robotic.X
;-----------------------------------------------------------------------------------------------------

	Graphics3D 800, 600, 0, 2
	SetBuffer BackBuffer()

	;setup camera	
	camera = CreateCamera()
	PositionEntity camera,0,20,-100
	
	CreateLight()
		
	If FileType("C:\Program Files\Blitz3D\Samples\Blitz 3D Samples\mak\anim\makbot") <> 2 Then RuntimeError "erm .. mak_robotic is not where i expected .. sorry"
	
	;go to directory	
	ChangeDir "C:\Program Files\Blitz3D\Samples\Blitz 3D Samples\mak\anim\makbot"
	
	;load anim mesh
	robot = LoadAnimMesh("mak_robotic.3ds")
	
	;animate mesh
	Animate robot, 1

	;save animated mesh	
	SaveAnimFile robot, "robot.x"
	
	;re-apply animation
	Animate robot, 1
	
	;load saved mesh
	robot2 = LoadAnimMesh("robot.x")
	If AnimLength(robot2) > 0 Then Animate robot2, 1
	
	;move meshes to the side
	MoveEntity robot2, -35, 0, 0
	MoveEntity robot, 20, 0, 0
	
	;main loop
	While Not KeyDown(1)
	
		UpdateWorld 
		RenderWorld 
		Flip 
	
	Wend
	
	;clean up saved file
	DeleteFile "robot.x"
	
	End
