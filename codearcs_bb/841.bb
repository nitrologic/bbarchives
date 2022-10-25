; ID: 841
; Author: churchaxe
; Date: 2003-11-28 12:32:36
; Title: model viewer
; Description: yet another .x .3ds .b3d model viewer

;Blitz .x, .3dx, .b3d only modelviewer, based on original blitz mediaviewer

Graphics3D 800, 600, 32, 2
AntiAlias True

Global appdir$ = SystemProperty("APPDIR")
Global camera
Global campiv
Global fullscreen
Global koopiv, koopiv2
Global model
Global plane, plane1
Global scale#
Global showkoo
Global sizeinfo$
Global stopzeit
Global unitsized
Global wire

If CommandLine() = "" Then RuntimeError "...need a file (.x .3ds .b3d) to view!"
Global fil$ = Lower$(CommandLine$())

;get rid of them "
If Left(fil, 1) = Chr(34) Then fil = Mid(fil, 2)
index = Instr(fil, Chr(34))
If index > 0 fil = Left(fil, index - 1)

AppTitle fil

index = Instr(fil$, ".")
If index > 0 ext$ = Mid$(fil$, index + 1)

If ext = "x" Or ext = "3ds" Or ext = "b3d"
	model = LoadMesh(fil$)
Else
	Text 10, 20, "Unknown extension: " + ext
	WaitKey()
	End
EndIf

;no model loaded
If model = 0 RuntimeError "Unable to load 3D mesh:" + fil$

;count Surfaces, Vertices and Triangles
sc = CountSurfaces(model)
Dim texturenames$(sc)

For k = 1 To sc
	vc = vc + CountVertices(GetSurface(model, k))
	tc = tc + CountTriangles(GetSurface(model, k))
	texturenames(k) = TextureName(GetBrushTexture(GetSurfaceBrush(GetSurface(Model, k)), 0))
Next

surf = GetSurface(Model, 1)
brush = GetSurfaceBrush(surf)
tex = GetBrushTexture(brush, 0)

light = CreateLight()
TurnEntity light, 45, 45, 0

campiv = CreatePivot()
camera = CreateCamera()
CameraClsColor camera, 0, 0, 64
CameraFogMode camera, 1
CameraFogColor camera, 0, 0, 64
PositionEntity camera, 0, 0, MeshWidth(model)
PointEntity camera, model
EntityParent camera, campiv
SetCameraParams()

;Axis
	koopiv = CreatePivot()
	temp = CreateCube(koopiv)
	EntityColor temp, 255, 255, 255
	ScaleEntity temp, .05, .05, .05
	
	For i = 1 To 10
		temp2 = CopyEntity(temp, koopiv)
		EntityColor temp2, 255, 0, 0
		PositionEntity temp2, i, 0, 0
	Next
	
	For i = 1 To 10
		temp2 = CopyEntity(temp, koopiv)
		EntityColor temp2, 0, 255, 0
		PositionEntity temp2, 0, i, 0
	Next
	
	For i = 1 To 10
		temp2 = CopyEntity(temp, koopiv)
		EntityColor temp2, 0, 0, 255
		PositionEntity temp2, 0, 0, i
	Next
	ScaleEntity koopiv, 1, 1, 1
	
	koopiv2 = CreatePivot()
	xaxis = CreateCube(koopiv2)
	ScaleEntity xaxis, 5, .01, .01
	EntityColor xaxis, 255, 0, 0
	PositionEntity xaxis, 5, 0, 0
	
	yaxis = CreateCube(koopiv2)
	ScaleEntity yaxis, .01, 5, .01
	EntityColor yaxis, 0, 255, 0
	PositionEntity yaxis, 0, 5, 0
	
	zaxis = CreateCube(koopiv2)
	ScaleEntity zaxis, .01, .01, 5
	EntityColor zaxis, 0, 0, 255
	PositionEntity zaxis, 0, 0, 5

loadsettings()

While Not KeyHit(1)
	RenderWorld()
	
	If unitsized
		Color 255, 128, 0
	Else
		Color 255, 255, 255
	EndIf
	
	Text GraphicsWidth() * .4, 0, sizeinfo
	If plane <> 0 Then Text GraphicsWidth() * .75, 0, "checker scale: " + scale + " units"
	Text 0, 0, "surfaces:  " + sc
	Text 0, 14, "vertices:  " + vc
	Text 0, 28, "triangles: " + tc
	Text 0, 56, "x-size: " + MeshWidth(model)
	Text 0, 70, "y-size: " + MeshHeight(model)
	Text 0, 84, "z-size: " + MeshDepth(model)

	If viewtextures
		If sc > 1
			For k = 1 To sc
				surf = GetSurface(Model, k)
				brush = GetSurfaceBrush(surf)
				tex = GetBrushTexture(brush, 0)
				Text 0, GraphicsHeight() * .97 - (sc - k) * 14, "texture" + k + " " + TextureName(tex)
			Next
		Else
			Text 0, GraphicsHeight() * .97, "texture " + TextureName(tex)
		EndIf
	EndIf
	
	Flip

	If MouseDown(1) = True
		mxspd# = MouseXSpeed()
		myspd# = MouseYSpeed()

		If KeyDown(29) = False And KeyDown(42) = False	;rotate cam
			If MouseHit(1)	;compensate initial rotation of view
				TurnEntity campiv, 0, mxspd * .4, 0, 1
				TurnEntity campiv, myspd * .4, 0, 0, 0
			EndIf
		
			TurnEntity campiv, 0, -mxspd * .4, 0, 1
			TurnEntity campiv, -myspd * .4, 0, 0, 0
		ElseIf KeyDown(29) = True	;strg: pan cam
			If MouseHit(1)
				MoveEntity camera, mxspd * .002 * MeshWidth(model), -myspd * .002 * MeshWidth(model), 0
			EndIf
			
			MoveEntity camera, -mxspd * .002 * MeshWidth(model), myspd * .002 * MeshWidth(model), 0
		ElseIf KeyDown(42) = True	;shift: zoom cam
			If MouseHit(1)
				MoveEntity camera, 0, 0, myspd * .002 * MeshWidth(model)
			EndIf
			MoveEntity camera, 0, 0, -myspd * .002 * MeshWidth(model)
		EndIf
	EndIf

	;MouseWheel
	If mz > MouseZ()
		If KeyDown(29)
			MoveEntity camera, 0, 0, .01 * MeshWidth(model)
		ElseIf KeyDown(42)
			MoveEntity camera, 0, 0, 1 * MeshWidth(model)
		Else
			MoveEntity camera, 0, 0, .1 * MeshWidth(model)
		EndIf
	ElseIf mz < MouseZ()
		If KeyDown(29)
			MoveEntity camera, 0, 0, -.01 * MeshWidth(model)
		ElseIf KeyDown(42)
			MoveEntity camera, 0, 0, -1 * MeshWidth(model)
		Else
			MoveEntity camera, 0, 0, -.1 * MeshWidth(model)
		EndIf
	EndIf
	mz = MouseZ()
	
	If KeyHit(59)	;F1
		ToggleWireframe()
	ElseIf KeyHit(60)	;F2-toggle axis
		If showkoo
			HideEntity koopiv
			HideEntity koopiv2
			showkoo = False
		Else
			ShowEntity koopiv
			ShowEntity koopiv2
			showkoo = True
		EndIf
	ElseIf KeyHit(61)	;F3-toggle xz-plane
		If plane = 0
			TogglePlane(True)
		Else
			TogglePlane(False)
		EndIf
	ElseIf KeyHit(62)	;F4-toggle size
		If unitsized = False
			FitMesh model, -.5, -.5, -.5, 1, 1, 1, True
			sizeinfo = "scaled to 1 unit, centered"
			scale = 1
			CameraRange camera, .1, 30
			CameraFogRange camera, 15, 30
			unitsized = True
		Else
			FreeEntity model
			model = LoadMesh(fil$)
			sizeinfo = "actual size and position"
			unitsized = False
		EndIf
		SetCameraParams()
		If plane <> 0
			TogglePlane(True)
		EndIf
	ElseIf KeyHit(63)	;F5-reset view to starting-values
		FreeEntity model
		model = LoadMesh(fil$)
		wire = True
		togglewireframe()
		loadsettings()
	ElseIf KeyHit(64)	;F6-reset view straight
		RotateEntity campiv, 0, 0, 0
		PositionEntity camera, 0, 0, MeshWidth(model)
		SetCameraParams()
	ElseIf KeyHit(65)	;F7- reset view perspectively
		RotateEntity campiv, -20, -30, 0
		PositionEntity camera, 0, 0, MeshWidth(model)
		SetCameraParams()
	ElseIf KeyHit(66)	;F8-view textures
		viewtextures = Not viewtextures
	ElseIf KeyHit(67)	;F9-browse dir with ACDSEE32
		If FileType("c:\programme\acdsee32\acdsee32.exe") = 1
			ExecFile "c:\programme\acdsee32\acdsee32.exe"
			mz = MouseZ()
		EndIf
	ElseIf KeyHit(68)	;F10-view textures with ACDSEE32
		tmp$ = ""
		For k = 1 To sc
			tmp$ = tmp$ + " " + texturenames(k)
		Next
		If FileType("c:\programme\acdsee32\acdsee32.exe") = 1
			ExecFile "c:\programme\acdsee32\acdsee32.exe " + tmp$
			mz = MouseZ()
		EndIf
	ElseIf KeyHit(87)	;F11-edit with Notepad
		ExecFile "notepad " + fil
		mz = MouseZ()
	ElseIf KeyHit(88)	;F12-switch to fullscreen
		If fullscreen
			EndGraphics()
			Graphics3D 800, 600, 32, 2
			fullscreen = False
		Else
			EndGraphics()
			Graphics3D 1280, 1024, 32, 1
			fullscreen = True
		EndIf
	
		plane = 0
		plane1 = 0
		model = LoadMesh(fil$)
		;no model loaded
		If model = 0 RuntimeError "Unable to load 3D mesh:" + fil$
		
		;count Surfaces, Vertices and Triangles
		sc = CountSurfaces(model)
		Dim texturenames$(sc)
		
		For k = 1 To sc
			vc = vc + CountVertices(GetSurface(model, k))
			tc = tc + CountTriangles(GetSurface(model, k))
			texturenames(k) = TextureName(GetBrushTexture(GetSurfaceBrush(GetSurface(Model, k)), 0))
		Next
		
		surf = GetSurface(Model, 1)
		brush = GetSurfaceBrush(surf)
		tex = GetBrushTexture(brush, 0)
		
		light = CreateLight()
		TurnEntity light, 45, 45, 0
		
		campiv = CreatePivot()
		camera = CreateCamera()
		CameraClsColor camera, 0, 0, 64
		CameraFogMode camera, 1
		CameraFogColor camera, 0, 0, 64
		PositionEntity camera, 0, 0, MeshWidth(model)
		PointEntity camera, model
		EntityParent camera, campiv
		SetCameraParams()

		;axis
		koopiv = CreatePivot()
		temp = CreateCube(koopiv)
		EntityColor temp, 255, 255, 255
		ScaleEntity temp, .05, .05, .05
		
		For i = 1 To 10
			temp2 = CopyEntity(temp, koopiv)
			EntityColor temp2, 255, 0, 0
			PositionEntity temp2, i, 0, 0
		Next
		
		For i = 1 To 10
			temp2 = CopyEntity(temp, koopiv)
			EntityColor temp2, 0, 255, 0
			PositionEntity temp2, 0, i, 0
		Next
		
		For i = 1 To 10
			temp2 = CopyEntity(temp, koopiv)
			EntityColor temp2, 0, 0, 255
			PositionEntity temp2, 0, 0, i
		Next
		ScaleEntity koopiv, 1, 1, 1
		
		koopiv2 = CreatePivot()
		xaxis = CreateCube(koopiv2)
		ScaleEntity xaxis, 5, .01, .01
		EntityColor xaxis, 255, 0, 0
		PositionEntity xaxis, 5, 0, 0
		yaxis = CreateCube(koopiv2)
		ScaleEntity yaxis, .01, 5, .01
		EntityColor yaxis, 0, 255, 0
		PositionEntity yaxis, 0, 5, 0
		zaxis = CreateCube(koopiv2)
		ScaleEntity zaxis, .01, .01, 5
		EntityColor zaxis, 0, 0, 255
		PositionEntity zaxis, 0, 0, 5
		
		loadsettings()			
	EndIf
Wend
savesettings()
End

Function create_checker_tex(r1, g1, b1, r2, g2, b2, texscale#, texsize = 128)
	texture_handle = CreateTexture(texsize, texsize)
	SetBuffer TextureBuffer(texture_handle)
	Color r1, g1, b1
	Rect 0, 0, texsize, texsize
	Color r2, g2, b2
	Rect 0, 0, texsize/2, texsize/2, 1
	Rect texsize/2, texsize/2, texsize/2, texsize/2, 1
	ScaleTexture texture_handle, texscale*2, texscale*2
	SetBuffer BackBuffer()
	Return texture_handle
End Function

Function savesettings()
	file = WriteFile(appdir + "mediaview.ini")
		WriteLine(file, "axis=" + showkoo)
		If plane <> 0
			WriteLine(file, "plane=" + 1)
		Else
			WriteLine(file, "plane=" + 0)
		EndIf
		WriteLine(file, "unitsized=" + unitsized)
		WriteLine(file, "campitch=" + EntityPitch(campiv))
		WriteLine(file, "camyaw=" + EntityYaw(campiv))
	CloseFile(file)
End Function

Function loadsettings()
	If FileType(appdir + "mediaview.ini") = 0 
		file = WriteFile(appdir + "mediaview.ini")	;generate new file with initial settings
			WriteLine(file, "axis=" + 1)
			WriteLine(file, "plane=" + 0)
			WriteLine(file, "unitsized=" + 0)
			WriteLine(file, "campitch=" + EntityPitch(campiv))
			WriteLine(file, "camyaw=" + EntityYaw(campiv))
		CloseFile(file)
	EndIf

	file = ReadFile(appdir + "mediaview.ini")
		While Not Eof(file)
			zeile$ = ReadLine(file)
			separator = Instr(zeile, "=")
			Wert$ = Mid(zeile, separator + 1, -1)
	
			Select Trim(Mid(zeile, 1, separator - 1))
			Case "axis"
				showkoo = wert
				If showkoo = False
					HideEntity koopiv
					HideEntity koopiv2
				Else
					ShowEntity koopiv
					ShowEntity koopiv2
				EndIf
			Case "plane"
				If wert = 1 Then TogglePlane(True)
			Case "unitsized"
				unitsized = wert
				If unitsized = 1
					FitMesh model, -.5, -.5, -.5, 1, 1, 1, True
					sizeinfo = "scaled to 1 unit, centered"
				Else
					sizeinfo = "actual size and position"
				EndIf
				PositionEntity camera, 0, 0, MeshWidth(model)
				SetCameraParams()
			Case "campitch"
				pitch# = wert
			Case "camyaw"
				yaw# = wert
				RotateEntity campiv, pitch, yaw, 0
			End Select
		Wend
	CloseFile(file)
End Function

Function TogglePlane(anzeigen)
	If anzeigen = True
		FreeTexture planetex
		FreeEntity plane
		FreeEntity plane1
	
		planetex = create_checker_tex(0, 0, 64, 255, 255, 255, scale)
						
		;upper plane
		plane = CreatePlane()
		EntityAlpha plane, .3
		EntityTexture plane, planetex
		
		;lower plane
		plane1 = CreatePlane()
		RotateEntity plane1, 0, 0, 180
		EntityAlpha plane1, .3
		EntityTexture plane1, planetex
		If wire	
			HideEntity plane
			HideEntity plane1
		EndIf
	Else
		FreeTexture planetex
		FreeEntity plane
		FreeEntity plane1
		plane = 0
		plane1 = 0
	EndIf
End Function

Function ToggleWireframe()
	If wire
		wire = False
		If plane <> 0
			ShowEntity plane
			ShowEntity plane1
		EndIf
	Else
		If plane <> 0
			HideEntity plane
			HideEntity plane1
		EndIf
		wire = True
	EndIf
	WireFrame wire
End Function

Function SetCameraParams()
	If MeshWidth(model) < .1
		scale# = .01 
	ElseIf MeshWidth(model) < 1
		scale# = .1 
	ElseIf MeshWidth(model) < 10
		scale = 1
	Else
		scale = 10
	EndIf

	CameraRange camera, .1 * scale, 100 * scale
	CameraFogRange camera, 2 * EntityDistance(camera, model), 4 * EntityDistance(camera, model)

	If plane <> 0
		TogglePlane(True)
	EndIf
	
End Function
