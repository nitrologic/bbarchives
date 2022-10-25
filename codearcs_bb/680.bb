; ID: 680
; Author: big10p
; Date: 2003-05-09 19:53:52
; Title: Explodable mesh demo (updated)
; Description: Create a mesh with unwelded, sub-divided tris... then blow it up!

;
; Explodable mesh demo by big10p (A.K.A. Chris Chadwick) 2003.
;
; Written with Blitz3D v1.83
;

	Graphics3D 800,600,32

	WireFrame 0
	AntiAlias 0

	SetFont LoadFont("arial",18,1)

	SeedRnd MilliSecs()

	; Explosion control constants:
	; FADE_START  - Set to value between 0 and 1 to say when, during an explosion
	;               animation, the pieces should start to fade out. e.g. 1 to start
	;               immediately, .5 to start halfway through, 0 to never fade.
	; GRAVITY     - Per frame factor by which pieces gravitate towards the world.
	; XZ_DECAY    - Per frame factor by which pieces' world X and Z velocity decays.
	; JIGGLE_TRAJ - False to set exploding pieces on an exact trajectory defined by
	;               the vector from the blast focus to the centre of the tri.
	;               True to set exploding pieces on a trajectory based on the vector
	;               defined above, but with an added 'jiggle' factor. This adds a
	;               more realistic, chaotic appearance to the explosion and helps
	;               disguise the often uniform construction of meshes. However, this
	;               makes a call to start_mesh_explode() slower.
	Const FADE_START# = 0.5
	Const GRAVITY# = 0.001
	Const XZ_DECAY# = 1.0 + GRAVITY		
	Const JIGGLE_TRAJ% = True

	Const BOMB_TYPE% = 1
	Const UFO_TYPE% = 2
	
	; Check pattern texture.
	Global tex = CreateTexture(64,64)
	SetBuffer TextureBuffer(tex)
	For y = 0 To 63 Step 4
		For x = 0 To 63 Step 2
			If c Then Color 255,255,255 Else Color 255,0,0
			c = Not c
			Rect x,y,2,4
		Next
		c = Not c
	Next

	; UFO texture.
	Global tex2 = CreateTexture(128,128)
	SetBuffer TextureBuffer(tex2)
	Color 255,255,255			; Stripes.
	Rect 0,0,128,128
	Color 150,150,150
	For x = 0 To 127 Step 8
		Rect x,0,4,128
	Next
	Color 0,255,255				; Windows.
	Rect 0,33,128,10
	Color 0,0,100				; Hoops.
	For y=3 To 127 Step 10
	Rect 0,y,128,1
	Next
	Color 0,0,0					; Black bands.
	Rect 0,56,128,16
	Rect 0,111,128,1
	Color 255,0,255				; Base light.
	Rect 0,112,128,16
	For x = 0 To 127 Step 4		; Red & yellow rim lights.
		If c
			Color 255,0,0
		Else
			Color 255,255,0
		EndIf
		c = Not c
		Rect x,60,3,8
	Next

	; Explosion flash texture.
	Global tex3 = CreateTexture(64,64,4)
	SetBuffer TextureBuffer(tex3)
	For y = 0 To 63
		For x = 0 To 63
		WritePixel x,y,$00000000
		Next
	Next
	Color 255,255,255
	For i = 1 To 4
		Select i
			Case 1
				Color 255,0,0
				min = 20
				max = 31
			Case 2
				Color 255,125,0
				min = 15
				max = 25
			Case 3
				Color 255,255,0
				min = 10
				max = 20
			Case 4
				Color 255,255,255
				min = 5
				max = 15
		End Select
		
		For n = 0 To 359 Step 2
			r = Rand(min,max)
			Line 32,32,32+Cos(n)*r,32+Sin(n)*r
		Next
	Next

	SetBuffer BackBuffer()
	Color 255,255,255
		
	Type tri_linkT
		Field prev.tri_linkT
		Field surf
		Field tri
		Field dx#,dy#,dz#
		Field pitch#,yaw#,roll#
	End Type

	Type explode_ctrlT
		Field tri_list.tri_linkT
		Field mesh
		Field life%
		Field fader#
		Field fade_start%
		Field exploding%
		Field o_pitch#
		Field o_yaw#
		Field o_roll#
	End Type	

	Type demo_textT
		Field x%,y%
		Field txt$
		Field r%,g%,b%
	End Type
		
	Global frame_count%
	Global fps%
	Global slowest_fps%
	Global fps_timeout%
	Global frame_time%
	Global slowest_frame%
	Global frame_start%
	fps_timer = CreateTimer(60)
	slowmo% = False
	wiref% = False
	Global demo_no% = 1
	Global first_call% = True
	Global alter.demo_textT
	Global exp_ctrl.explode_ctrlT
	Global exp_flash%
	Global exp_stren%
	Global loaded%
	Global explode_chain%
	Global chain_timer%
				
	Global cam = CreateCamera()
	PositionEntity cam,0,0,-9

	light = CreateLight()
	TurnEntity light,0,90,0
	
	; Scratch pad single tri mesh for doing rotations.
	Global tm_mesh = CreateMesh()
	Global tm_surf = CreateSurface(tm_mesh)
	AddVertex(tm_surf,0,0,0)
	AddVertex(tm_surf,0,0,0)
	AddVertex(tm_surf,0,0,0)
	AddTriangle(tm_surf,0,1,2)
	HideEntity tm_mesh

	; Scratch pad mesh used by copy_tri_explode() for subdivision.
	Global sd_mesh = CreateMesh()
	Global sd_surf = CreateSurface(sd_mesh)
	HideEntity sd_mesh

	; Scratch pad single vertex mesh used to 'jiggle' particle trajectory vectors.
	Global vm_mesh = CreateMesh()
	Global vm_surf = CreateSurface(vm_mesh)
	AddVertex(vm_surf,0,0,0)
	HideEntity vm_mesh

	; Some prims to copy.
	Global cube = CreateCube()
	Global sphere = CreateSphere(16)
	Global diamond = CreateSphere(2)
	Global pyramid = CreateCone(4)
	Global cone = CreateCone(16)
	Global cylinder = CreateCylinder(16)
	HideEntity cube
	HideEntity sphere
	HideEntity diamond
	HideEntity cone
	HideEntity cylinder
	HideEntity pyramid
	Global ufo = CreateSphere(12)
	ball = CreateSphere(8)
	ScaleMesh ufo,3,.5,3
	ScaleMesh ball,1.5,1,1.5
	AddMesh ball,ufo
	FreeEntity ball
	HideEntity ufo

	; B3D cone prims seem to have screwed endcap normals!?
	UpdateNormals pyramid
	UpdateNormals cone

	; Explosive sphere.
	Global explosive = CreateSphere(6)
	ScaleMesh explosive,.15,.15,.15
	EntityRadius explosive,.15
	EntityType explosive,BOMB_TYPE
	HideEntity explosive
	Global explosive2 = CopyMesh(explosive)
	Global explosive3 = CopyMesh(explosive)
	HideEntity explosive2
	HideEntity explosive3
	Global exp_piv = CreatePivot()	
	Global exp_piv2 = CreatePivot()	

	; Explosion flash sprite.
	Global flash = CreateSprite()
	EntityTexture flash,tex3
	ScaleSprite flash,3,3
	EntityFX flash,1
	HideEntity flash

		
	; Main loop
	While Not KeyHit(1)

		frame_start = MilliSecs()

		If KeyHit(28) Then slowmo = Not slowmo
		If KeyHit(14)
			wiref = Not wiref
			WireFrame wiref
		EndIf
				
		UpdateWorld
		demo_handler()
		update_mesh_explode()
		RenderWorld

		; Display demo screen text.
		For t.demo_textT = Each demo_textT
			Color t\r Shr 1,t\g Shr 1,t\b Shr 1
			Text 400 + 2,t\y + 2,t\txt$,1,1
			Color t\r,t\g,t\b
			Text 400,t\y,t\txt$,1,1
		Next
		
		frame_time = MilliSecs() - frame_start	
		;show_info()

		WaitTimer(fps_timer)
		Flip(1)

		If slowmo Then Delay 200
	Wend

	ClearWorld

	End


.demo_1_text
Data 50
Data 255,255,0,   "Exploding Mesh Demo"
Data 255,255,0,   "by"
Data 255,255,0,   "big10p (A.K.A. Chris Chadwick) 2003"
Data 0,0,0,		  ""
Data 0,0,0,		  ""
Data 0,255,255,	  "At any time during this demo, press:"
Data 0,255,255,	  "BACKSPACE to toggle wireframe on/off (may not work with your hardware)"
Data 0,255,255,   "ENTER to toggle slow motion on/off"
Data 0,255,255,   "ESCAPE to quit"
Data 0,0,0,		  ""
Data 0,0,0,		  ""
Data 255,0,175,	  "This demo creates explodable meshes by copying existing meshes, unwelding all"
Data 245,0,185,	  "triangles and performing optional, recursive subdivision to give large-tri meshes"
Data 235,0,195,	  "more pieces to explode. Explosion animations are done using dynamic deformation of"
Data 225,0,205,	  "a single mesh/surface, which should be more efficient/faster than using a collection"
Data 215,0,215,	  "of single-tri meshes to achieve the same effect. Explosions are dynamic: the variable"
Data 205,0,225,	  "strength explosion that disintegrates a mesh can be set to any point relative to the"
Data 195,0,235,	  "mesh's centre, either using coords aligned to the mesh's axes or aligned to the world"
Data 185,0,245,	  "axes. This also allows explosions to be either internal or external to the mesh."
Data 175,0,255,	  "Umm.. oh yeah, meshes can be blown-up with multiple explosions, too."
Data 0,0,0,		  ""
Data 0,0,0,		  ""
Data 0,255,0,	  "Anyway, enough of the blurb! Let's go and blow-up some stuff..."
Data 0,0,0,		  ""
Data 0,0,0,		  ""
Data 255,255,255, "Press SPACE to continue"
Data -100

.demo_2_text
Data 530
Data 0,255,0,     "An explosive will be placed at the centre of each shape in turn"
Data 0,255,0,     "Press numeric keypad +/- to alter strength : 000"
Data 255,255,255, "Press SPACE to detonate explosive"
Data -100

.demo_3_text
Data 530
Data 255,0,255,   "An explosive will be placed at the base of each shape in turn"
Data 255,0,255,   "Press numeric keypad +/- to alter strength : 000"
Data 255,255,255, "Press SPACE to detonate explosive"
Data -100

.demo_4_text
Data 530
Data 0,255,255,   "An explosive will be placed directly below each shape in turn"
Data 0,255,255,   "Press numeric keypad +/- to alter strength : 000"
Data 255,255,255, "Press SPACE to detonate explosive"
Data -100

.demo_5_text
Data 530
Data 255,255,0,   "An explosive will orbit around each shape in turn"
Data 255,255,0,   "Press numeric keypad +/- to alter strength : 000"
Data 255,255,255, "Press SPACE to detonate explosive"
Data -100

.demo_6_text
Data 530
Data 0,0,255,     "Shoot down the UFO's with an explosive"
Data 0,0,255,     "Press numeric keypad +/- to alter strength : 000"
Data 255,255,255, "Press left/right ARROW KEYS to move and SPACE to fire"
Data -100

.demo_7_text
Data 530
Data 255,0,0,     "Three different strength explosives have been attached to the UFO"
Data 255,0,0,     "They have been set to explode one after the other"
Data 255,255,255, "Press SPACE to detonate first explosive"
Data -100




;
; Come back, function pointers - all is forgiven!
;
Function demo_handler()

	Select demo_no
		Case 1
			demo_1()
		Case 2
			demo_2()
		Case 3
			demo_3()
		Case 4
			demo_4()
		Case 5
			demo_5()
		Case 6
			demo_6()
		Case 7
			demo_7()
	End Select
	
End Function


;
; Intro screen.
;
Function demo_1()

	If first_call
		create_demo_text()
						
		first_call = False
	Else
		If KeyHit(57)
			demo_no = demo_no + 1
			first_call = True
		EndIf
	EndIf

End Function


;
; Demo screen 2 - central explosives.
;
Function demo_2()

	If first_call
		create_demo_text()
				
		first_call = False

		shape_ctrl.explode_ctrlT = copy_mesh_explode(cube,3)
		PositionEntity shape_ctrl\mesh,-4,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(sphere)
		PositionEntity shape_ctrl\mesh,0,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(cone,2,1)
		PositionEntity shape_ctrl\mesh,4,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(cylinder,2)
		PositionEntity shape_ctrl\mesh,4,3,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(diamond,3)
		PositionEntity shape_ctrl\mesh,0,3,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(pyramid,3)
		PositionEntity shape_ctrl\mesh,-4,3,0
		EntityTexture shape_ctrl\mesh,tex
			
		ShowEntity explosive
		exp_ctrl = First explode_ctrlT
		PositionEntity explosive,EntityX(exp_ctrl\mesh),EntityY(exp_ctrl\mesh),EntityZ(exp_ctrl\mesh)

		exp_stren = 20
	EndIf

	If Last explode_ctrlT = Null
		demo_no = demo_no + 1
		first_call = True
		Return
	EndIf

	If KeyDown(74) And exp_stren > 1 Then exp_stren = exp_stren - 1
	If KeyDown(78) And exp_stren < 100 Then exp_stren = exp_stren + 1

	alter\txt$ = Left(alter\txt$,Instr(alter\txt,":")) + " " + Str(exp_stren*0.1)

	If KeyHit(57) Then bang = True

	For ctrl.explode_ctrlT = Each explode_ctrlT
		If ctrl\exploding = False
			If bang
				bang = False
				start_mesh_explode(ctrl,(exp_stren*0.1),200)

				exp_ctrl = After exp_ctrl
				
				If exp_ctrl = Null
					HideEntity explosive
				Else
					PositionEntity explosive,EntityX(exp_ctrl\mesh),EntityY(exp_ctrl\mesh),EntityZ(exp_ctrl\mesh)
				EndIf
			Else				
				TurnEntity ctrl\mesh,1,0,.5
			EndIf
		EndIf
	Next
	
	flash_explosive()

End Function


;
; Demo screen 3 - explosives fixed to base.
;
Function demo_3()

	If first_call
		create_demo_text()
				
		first_call = False
	
		shape_ctrl.explode_ctrlT = copy_mesh_explode(cube,3)
		PositionEntity shape_ctrl\mesh,-4,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(sphere)
		PositionEntity shape_ctrl\mesh,0,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(cone,2,1)
		PositionEntity shape_ctrl\mesh,4,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(cylinder,2)
		PositionEntity shape_ctrl\mesh,4,3,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(diamond,3)
		PositionEntity shape_ctrl\mesh,0,3,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(pyramid,3)
		PositionEntity shape_ctrl\mesh,-4,3,0
		EntityTexture shape_ctrl\mesh,tex

		ShowEntity explosive
		exp_ctrl = First explode_ctrlT
		EntityParent explosive,exp_ctrl\mesh
		PositionEntity explosive,0,-1,0
				
		exp_stren = 20
	EndIf

	If Last explode_ctrlT = Null
		demo_no = demo_no + 1
		first_call = True
		Return
	EndIf

	If KeyDown(74) And exp_stren > 1 Then exp_stren = exp_stren - 1
	If KeyDown(78) And exp_stren < 100 Then exp_stren = exp_stren + 1

	alter\txt$ = Left(alter\txt$,Instr(alter\txt,":")) + " " + Str(exp_stren*0.1)

	If KeyHit(57) Then bang = True

	For ctrl.explode_ctrlT = Each explode_ctrlT
		If ctrl\exploding = False
			If bang
				bang = False
				start_mesh_explode(ctrl,(exp_stren*0.1),200,0,-1,0,1)
				
				exp_ctrl = After exp_ctrl
				
				If exp_ctrl = Null
					EntityParent explosive,0
					HideEntity explosive
				Else
					EntityParent explosive,exp_ctrl\mesh
					PositionEntity explosive,0,-1,0
				EndIf
			Else				
				TurnEntity ctrl\mesh,1,0,.5
			EndIf
		EndIf
	Next

	flash_explosive()
	
End Function


;
; Demo screen 4 - static, external explosives.
;
Function demo_4()

	If first_call
		create_demo_text()
				
		first_call = False
	
		shape_ctrl.explode_ctrlT = copy_mesh_explode(cube,3)
		PositionEntity shape_ctrl\mesh,-4,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(sphere)
		PositionEntity shape_ctrl\mesh,0,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(cone,2,1)
		PositionEntity shape_ctrl\mesh,4,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(cylinder,2)
		PositionEntity shape_ctrl\mesh,4,3,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(diamond,3)
		PositionEntity shape_ctrl\mesh,0,3,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(pyramid,3)
		PositionEntity shape_ctrl\mesh,-4,3,0
		EntityTexture shape_ctrl\mesh,tex

		ShowEntity explosive
		exp_ctrl = First explode_ctrlT
		PositionEntity explosive,EntityX(exp_ctrl\mesh),EntityY(exp_ctrl\mesh)-2,EntityZ(exp_ctrl\mesh)
				
		exp_stren = 20
	EndIf

	If Last explode_ctrlT = Null
		demo_no = demo_no + 1
		first_call = True
		Return
	EndIf

	If KeyDown(74) And exp_stren > 1 Then exp_stren = exp_stren - 1
	If KeyDown(78) And exp_stren < 100 Then exp_stren = exp_stren + 1

	alter\txt$ = Left(alter\txt$,Instr(alter\txt,":")) + " " + Str(exp_stren*0.1)

	If KeyHit(57) Then bang = True

	For ctrl.explode_ctrlT = Each explode_ctrlT
		If ctrl\exploding = False
			If bang
				bang = False
				start_mesh_explode(ctrl,(exp_stren*0.1),200,0,-2,0)
				
				exp_ctrl = After exp_ctrl
				
				If exp_ctrl = Null
					HideEntity explosive
				Else
					PositionEntity explosive,EntityX(exp_ctrl\mesh),EntityY(exp_ctrl\mesh)-2,EntityZ(exp_ctrl\mesh)
				EndIf
			Else				
				TurnEntity ctrl\mesh,1,0,.5
			EndIf
		EndIf
	Next

	flash_explosive()
	
End Function


;
; Demo screen 5 - orbiting explosives.
;
Function demo_5()

	If first_call
		create_demo_text()
				
		first_call = False
	
		shape_ctrl.explode_ctrlT = copy_mesh_explode(cube,3)
		PositionEntity shape_ctrl\mesh,-4,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(sphere)
		PositionEntity shape_ctrl\mesh,0,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(cone,2,1)
		PositionEntity shape_ctrl\mesh,4,-1.5,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(cylinder,2)
		PositionEntity shape_ctrl\mesh,4,3,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(diamond,3)
		PositionEntity shape_ctrl\mesh,0,3,0
		EntityTexture shape_ctrl\mesh,tex
	
		shape_ctrl = copy_mesh_explode(pyramid,3)
		PositionEntity shape_ctrl\mesh,-4,3,0
		EntityTexture shape_ctrl\mesh,tex

		exp_ctrl = First explode_ctrlT
		PositionEntity exp_piv,EntityX(exp_ctrl\mesh),EntityY(exp_ctrl\mesh),EntityZ(exp_ctrl\mesh)
		EntityParent explosive,exp_piv
		ShowEntity explosive
		PositionEntity explosive,0,-2,0
				
		exp_stren = 20
	EndIf

	If Last explode_ctrlT = Null
		demo_no = demo_no + 1
		first_call = True
		RotateEntity exp_piv,0,0,0
		Return
	EndIf

	TurnEntity exp_piv,0,0,-1.5
	
	flash_explosive()

	If KeyDown(74) And exp_stren > 1 Then exp_stren = exp_stren - 1
	If KeyDown(78) And exp_stren < 100 Then exp_stren = exp_stren + 1

	alter\txt$ = Left(alter\txt$,Instr(alter\txt,":")) + " " + Str(exp_stren*0.1)

	If KeyHit(57) Then bang = True

	For ctrl.explode_ctrlT = Each explode_ctrlT
		If ctrl\exploding = False
			If bang
				bang = False
				
				ex# = EntityX(explosive,1) - EntityX(ctrl\mesh)
				ey# = EntityY(explosive,1) - EntityY(ctrl\mesh)
				ez# = EntityZ(explosive,1) - EntityZ(ctrl\mesh)
				start_mesh_explode(ctrl,(exp_stren*0.1),200,ex,ey,ez)
				
				exp_ctrl = After exp_ctrl
				
				If exp_ctrl = Null
					EntityParent explosive,0
					HideEntity explosive
				Else
					PositionEntity exp_piv,EntityX(exp_ctrl\mesh),EntityY(exp_ctrl\mesh),EntityZ(exp_ctrl\mesh)
					RotateEntity exp_piv,0,0,0
				EndIf
			Else				
				TurnEntity ctrl\mesh,1,0,.5
			EndIf
		EndIf
	Next
	
End Function


;
; Demo screen 6 - shoot the UFO's.
;
Function demo_6()

	If first_call
		create_demo_text()
				
		first_call = False
	
		ufo_ctrl.explode_ctrlT = copy_mesh_explode(ufo)
		RotateMesh ufo_ctrl\mesh,3,0,0
		ScaleMesh ufo_ctrl\mesh,.9,.9,.9
		TurnEntity ufo_ctrl\mesh,0,60,0
		PositionEntity ufo_ctrl\mesh,0,3,0
		EntityTexture ufo_ctrl\mesh,tex2
		EntityType ufo_ctrl\mesh,UFO_TYPE

		ufo_ctrl = copy_mesh_explode(ufo)
		RotateMesh ufo_ctrl\mesh,3,0,0
		ScaleMesh ufo_ctrl\mesh,.5,.5,.5
		TurnEntity ufo_ctrl\mesh,0,120,0
		PositionEntity ufo_ctrl\mesh,-4,1.5,0
		EntityTexture ufo_ctrl\mesh,tex2
		EntityType ufo_ctrl\mesh,UFO_TYPE

		ufo_ctrl = copy_mesh_explode(ufo)
		RotateMesh ufo_ctrl\mesh,3,0,0
		ScaleMesh ufo_ctrl\mesh,.7,.7,.7
		TurnEntity ufo_ctrl\mesh,0,180,0
		PositionEntity ufo_ctrl\mesh,3.5,1.2,0
		EntityTexture ufo_ctrl\mesh,tex2
		EntityType ufo_ctrl\mesh,UFO_TYPE

		ufo_ctrl = copy_mesh_explode(ufo)
		RotateMesh ufo_ctrl\mesh,3,0,0
		ScaleMesh ufo_ctrl\mesh,.4,.4,.4
		TurnEntity ufo_ctrl\mesh,0,240,0
		PositionEntity ufo_ctrl\mesh,-.5,.5,0
		EntityTexture ufo_ctrl\mesh,tex2
		EntityType ufo_ctrl\mesh,UFO_TYPE

		PositionEntity explosive,0,-4,0
		ShowEntity explosive
		loaded = True
				
		exp_stren = 20
		
		Collisions BOMB_TYPE,UFO_TYPE,2,1
	EndIf

	If CountCollisions(explosive)
		ec = EntityCollided(explosive,UFO_TYPE)

		For ctrl.explode_ctrlT = Each explode_ctrlT
			If ec = ctrl\mesh
				cx# = CollisionX(explosive,1) - EntityX(ctrl\mesh)
				cy# = CollisionY(explosive,1) - EntityY(ctrl\mesh)
				cz# = CollisionZ(explosive,1) - EntityZ(ctrl\mesh)

				start_mesh_explode(ctrl,(exp_stren*0.1),200,cx,cy,cz)

				EntityType ctrl\mesh,0
				Exit
			EndIf
		Next
		
		loaded = True
		HideEntity explosive
		PositionEntity explosive,0,-4,0
		ShowEntity explosive
	EndIf

	If Last explode_ctrlT = Null
		demo_no = demo_no + 1
		first_call = True
		ClearCollisions
		HideEntity explosive
		Return
	EndIf
	
	If KeyDown(74) And exp_stren > 1 Then exp_stren = exp_stren - 1
	If KeyDown(78) And exp_stren < 100 Then exp_stren = exp_stren + 1

	alter\txt$ = Left(alter\txt$,Instr(alter\txt,":")) + " " + Str(exp_stren*0.1)

	If KeyHit(57) Then loaded = False

	If loaded
		If KeyDown(203) And EntityX(explosive) > -7 Then TranslateEntity explosive,-0.1,0,0
		If KeyDown(205) And EntityX(explosive) < 7 Then TranslateEntity explosive,0.1,0,0
	Else
		If EntityY(explosive) < 5
			TranslateEntity explosive,0,0.2,0
		Else
			FlushKeys
			loaded = True
			HideEntity explosive
			PositionEntity explosive,0,-4,0
			ShowEntity explosive
		EndIf
	EndIf
		
	For ctrl.explode_ctrlT = Each explode_ctrlT
		If Not ctrl\exploding Then TurnEntity ctrl\mesh,0,5,0
	Next

	flash_explosive()
	
End Function


;
; Demo screen 7 - destroy UFO with multiple explosives.
;
Function demo_7()

	If first_call
		create_demo_text()
				
		first_call = False
		explode_chain = False
		chain_timer = 0

		PositionEntity exp_piv2,0,1.5,0
		EntityParent exp_piv,exp_piv2
		PositionEntity exp_piv,0,0,0
					
		ufo_ctrl.explode_ctrlT = copy_mesh_explode(ufo,1)
		EntityParent ufo_ctrl\mesh,exp_piv
		PositionEntity ufo_ctrl\mesh,0,0,0
		EntityTexture ufo_ctrl\mesh,tex2

		EntityParent explosive,exp_piv
		PositionEntity explosive,-2.7,-0.2,0
		ShowEntity explosive

		EntityParent explosive2,exp_piv
		PositionEntity explosive2,2.7,-0.2,0
		ShowEntity explosive2

		EntityParent explosive3,exp_piv
		PositionEntity explosive3,0,-1,0
		ShowEntity explosive3
		
		RotateEntity exp_piv,3,0,0
	EndIf

	ctrl.explode_ctrlT = First explode_ctrlT

	If ctrl = Null
		demo_no = 1
		first_call = True
		EntityParent exp_piv,0
		RotateEntity exp_piv,0,0,0
		RotateEntity exp_piv2,0,0,0
		Return
	EndIf

	If KeyHit(57) And (Not explode_chain) Then explode_chain = True

	If explode_chain
		Select chain_timer
			Case 0
				ex# = EntityX(explosive)
				ey# = EntityY(explosive)
				ez# = EntityZ(explosive)
				EntityParent explosive,0
				HideEntity explosive
				PositionEntity flash,EntityX(explosive,1),EntityY(explosive,1),EntityZ(explosive,1)
				ScaleSprite flash,1,1
				ShowEntity flash
				start_mesh_explode(ctrl,1,200,ex,ey,ez,1)
			Case 20
				ex# = EntityX(explosive2)
				ey# = EntityY(explosive2)
				ez# = EntityZ(explosive2)
				EntityParent explosive2,0
				HideEntity explosive2
				PositionEntity flash,EntityX(explosive2,1),EntityY(explosive2,1),EntityZ(explosive2,1)
				ScaleSprite flash,2,2
				ShowEntity flash
				start_mesh_explode(ctrl,2,200,ex,ey,ez,1)
			Case 40
				ex# = EntityX(explosive3)
				ey# = EntityY(explosive3)
				ez# = EntityZ(explosive3)
				EntityParent explosive3,0
				HideEntity explosive3
				PositionEntity flash,EntityX(explosive3,1),EntityY(explosive3,1),EntityZ(explosive3,1)
				ScaleSprite flash,5,5
				ShowEntity flash
				start_mesh_explode(ctrl,5,200,ex,ey,ez,1)
			Case 2,22,42
				HideEntity flash	
		End Select
		
		chain_timer = chain_timer + 1
	EndIf
		
	If Not ctrl\exploding Then TurnEntity exp_piv2,0,5,0

	flash_explosive()
	
End Function


;
; Alternates colour of explosive spheres, to make them flash.
;
Function flash_explosive()

	exp_flash = exp_flash + 1

	If exp_flash = 4
		EntityColor explosive,0,0,255
		EntityColor explosive2,0,0,255
		EntityColor explosive3,0,0,255
	ElseIf exp_flash = 8
		EntityColor explosive,255,255,0
		EntityColor explosive2,255,255,0
		EntityColor explosive3,255,255,0
		exp_flash = 0
	EndIf

End Function


;
; Creates all text types required by the current demo screen.
;
Function create_demo_text()

	free_demo_text()

	Select demo_no
		Case 1
			Restore demo_1_text
		Case 2
			Restore demo_2_text		
		Case 3
			Restore demo_3_text		
		Case 4
			Restore demo_4_text		
		Case 5
			Restore demo_5_text		
		Case 6
			Restore demo_6_text		
		Case 7
			Restore demo_7_text		
	End Select

	Read text_y
	
	Repeat
		Read r : If r = -100 Then Exit
		Read g,b
		Read txt$

		t.demo_textT = New demo_textT
		t\y = text_y
		t\txt$ = txt$
		t\r = r
		t\g = g
		t\b = b
		
		text_y = text_y + 20

		If Right(txt$,3) = "000" Then alter = t
	Forever

End Function


;
; Deletes all currently defined demo text types.
;
Function free_demo_text()

	For t.demo_textT = Each demo_textT
		Delete t
	Next

End Function




;
; Updates ALL explodable meshes that are currently exploding.
;
Function update_mesh_explode()

	For this.explode_ctrlT = Each explode_ctrlT
		
		If this\exploding
			
			If this\life
				tri.tri_linkT = this\tri_list
			
				If this\life <= this\fade_start
					EntityAlpha this\mesh,this\life * this\fader
				EndIf
				
				; Update all tris in linked list.
				While tri <> Null
					v0 = TriangleVertex(tri\surf,tri\tri,0)
					v1 = TriangleVertex(tri\surf,tri\tri,1)
					v2 = TriangleVertex(tri\surf,tri\tri,2)
					
					; Get 'centre of tri' vertex.
					cx# = VertexX(tri\surf,v2+1)
					cy# = VertexY(tri\surf,v2+1)
					cz# = VertexZ(tri\surf,v2+1)

					; Load scratch pad tri mesh.
					VertexCoords tm_surf,0,VertexX(tri\surf,v0)-cx,VertexY(tri\surf,v0)-cy,VertexZ(tri\surf,v0)-cz
					VertexCoords tm_surf,1,VertexX(tri\surf,v1)-cx,VertexY(tri\surf,v1)-cy,VertexZ(tri\surf,v1)-cz
					VertexCoords tm_surf,2,VertexX(tri\surf,v2)-cx,VertexY(tri\surf,v2)-cy,VertexZ(tri\surf,v2)-cz
					VertexNormal tm_surf,0,VertexNX(tri\surf,v0),VertexNY(tri\surf,v0),VertexNZ(tri\surf,v0)
					VertexNormal tm_surf,1,VertexNX(tri\surf,v1),VertexNY(tri\surf,v1),VertexNZ(tri\surf,v1)
					VertexNormal tm_surf,2,VertexNX(tri\surf,v2),VertexNY(tri\surf,v2),VertexNZ(tri\surf,v2)
			
					; Do rotations.
					RotateMesh tm_mesh,tri\pitch,tri\yaw,tri\roll

					; Copy back to mesh tri adding translation.
					VertexCoords tri\surf,v0,VertexX(tm_surf,0)+cx+tri\dx,VertexY(tm_surf,0)+cy+tri\dy,VertexZ(tm_surf,0)+cz+tri\dz
					VertexCoords tri\surf,v1,VertexX(tm_surf,1)+cx+tri\dx,VertexY(tm_surf,1)+cy+tri\dy,VertexZ(tm_surf,1)+cz+tri\dz
					VertexCoords tri\surf,v2,VertexX(tm_surf,2)+cx+tri\dx,VertexY(tm_surf,2)+cy+tri\dy,VertexZ(tm_surf,2)+cz+tri\dz
					VertexNormal tri\surf,v0,VertexNX(tm_surf,0),VertexNY(tm_surf,0),VertexNZ(tm_surf,0)
					VertexNormal tri\surf,v1,VertexNX(tm_surf,1),VertexNY(tm_surf,1),VertexNZ(tm_surf,1)
					VertexNormal tri\surf,v2,VertexNX(tm_surf,2),VertexNY(tm_surf,2),VertexNZ(tm_surf,2)

					VertexCoords tri\surf,v2+1,cx+tri\dx,cy+tri\dy,cz+tri\dz
					
					; Update gravity effect, ready for next update.
					tri\dy = tri\dy-GRAVITY
					tri\dx = tri\dx/XZ_DECAY
					tri\dz = tri\dz/XZ_DECAY
					
					tri = tri\prev
				Wend
			
				this\life = this\life - 1
			Else
				; Mesh has finished exploding.
				free_mesh_explode(this)
			EndIf

		EndIf

	Next

End Function


;
; Creates a subdivided (optional), unwelded copy of an existing mesh.
; Explode info for all tris in the new mesh are kept in a separate linked list
; contained in the control variable returned to the caller.
; Note: the source mesh being copied remains completely unaltered.
;
; Params:
; s_mesh     - Source mesh to be copied.
; divs       - Number of times to perform x4 subdivision on each source tri.
; keep_surfs - True to keep the same amount of surfaces in
;              the copy as in the source mesh.
;              False (Default) to copy all tris in the source mesh
;              to a single surface in the copy.
;
; Returns:
; Pointer to the control variable (of type explode_ctrlT) for the
; new explodable mesh created.
;
Function copy_mesh_explode.explode_ctrlT(s_mesh,divs=0,keep_surfs%=False)

	tri_list.tri_linkT = Null

	d_mesh = CreateMesh()
		
	For sno = 1 To CountSurfaces(s_mesh)
		
		s_surf = GetSurface(s_mesh,sno)
		nt = CountTriangles(s_surf)

		If sno = 1 Or keep_surfs Then d_surf = CreateSurface(d_mesh)
		
		For tno = 0 To nt-1
			tri_list = copy_tri_explode(s_surf,tno,d_surf,divs,tri_list)
		Next
	
	Next

	; Create and return this explodable mesh's control variable.
	ctrl.explode_ctrlT = New explode_ctrlT
	ctrl\tri_list = tri_list
	ctrl\mesh = d_mesh
	ctrl\exploding = False
		
	Return ctrl
	
End Function


;
; Adds an unwelded copy of a tri from the source surface to the destination
; surface, performing any subdivision requested.
;
; Subdivision is done by calling this function recursively, splitting the
; source tri into 4 unwelded tris each time, like so:
;
;           v1
;           /\ 
;         /____\
;       /  \  /  \
;     /_____\/_____\
;    v0            v2
;
; Params:
; s_surf   - Source surface containing tri to be copied.
; tri      - Index of tri to be copied.
; d_surf   - Destination surface to copy the source tri to.
; divs     - Number of times to perform x4 subdivision on the source tri.
; tri_list - Current top item in the linked list.
; reset    - True (default) to clear scratch pad mesh.
;            False to not clear scratch pad mesh (used internally only!).
;
; Returns:
; The current top item in the linked list.
;
Function copy_tri_explode.tri_linkT(s_surf,tri,d_surf,divs,tri_list.tri_linkT,reset%=True)

	If reset Then ClearSurface(sd_surf)
	
	sv0 = TriangleVertex(s_surf,tri,0)
	sv1 = TriangleVertex(s_surf,tri,1)
	sv2 = TriangleVertex(s_surf,tri,2)

	; Get coords of all 3 vertices of source tri.
	x0# = VertexX(s_surf,sv0)
	y0# = VertexY(s_surf,sv0)
	z0# = VertexZ(s_surf,sv0)
	x1# = VertexX(s_surf,sv1)
	y1# = VertexY(s_surf,sv1)
	z1# = VertexZ(s_surf,sv1)
	x2# = VertexX(s_surf,sv2)
	y2# = VertexY(s_surf,sv2)
	z2# = VertexZ(s_surf,sv2)
	
	; Get normals of all 3 vertices of source tri.
	nx0# = VertexNX(s_surf,sv0)
	ny0# = VertexNY(s_surf,sv0)
	nz0# = VertexNZ(s_surf,sv0)
	nx1# = VertexNX(s_surf,sv1)
	ny1# = VertexNY(s_surf,sv1)
	nz1# = VertexNZ(s_surf,sv1)
	nx2# = VertexNX(s_surf,sv2)
	ny2# = VertexNY(s_surf,sv2)
	nz2# = VertexNZ(s_surf,sv2)

	; Get tex coords of all 3 vertices of source tri.
	u0# = VertexU(s_surf,sv0)
	v0# = VertexV(s_surf,sv0)
	w0# = VertexW(s_surf,sv0)
	u1# = VertexU(s_surf,sv1)
	v1# = VertexV(s_surf,sv1)
	w1# = VertexW(s_surf,sv1)
	u2# = VertexU(s_surf,sv2)
	v2# = VertexV(s_surf,sv2)
	w2# = VertexW(s_surf,sv2)

	; Get colour of all 3 vertices of source tri.
	r0# = VertexRed(s_surf,sv0)
	g0# = VertexGreen(s_surf,sv0)
	b0# = VertexBlue(s_surf,sv0)
	r1# = VertexRed(s_surf,sv1)
	g1# = VertexGreen(s_surf,sv1)
	b1# = VertexBlue(s_surf,sv1)
	r2# = VertexRed(s_surf,sv2)
	g2# = VertexGreen(s_surf,sv2)
	b2# = VertexBlue(s_surf,sv2)

	If divs
		; Calculate coords of the 3 discrete vertices used by subdivision.
		x3# = (x1+x0)/2.0
		y3# = (y1+y0)/2.0
		z3# = (z1+z0)/2.0
		x4# = (x2+x1)/2.0
		y4# = (y2+y1)/2.0
		z4# = (z2+z1)/2.0
		x5# = (x0+x2)/2.0
		y5# = (y0+y2)/2.0
		z5# = (z0+z2)/2.0
		
		; Calculate normals of the 3 discrete vertices used by subdivision.
		nx3# = (nx1+nx0)/2.0
		ny3# = (ny1+ny0)/2.0
		nz3# = (nz1+nz0)/2.0
		nx4# = (nx2+nx1)/2.0
		ny4# = (ny2+ny1)/2.0
		nz4# = (nz2+nz1)/2.0
		nx5# = (nx0+nx2)/2.0
		ny5# = (ny0+ny2)/2.0
		nz5# = (nz0+nz2)/2.0
		
		; Unify.
		nl# = Sqr(nx3*nx3 + ny3*ny3 + nz3*nz3)
		nx3 = nx3 / nl#
		ny3 = ny3 / nl#
		nz3 = nz3 / nl#
		nl# = Sqr(nx4*nx4 + ny4*ny4 + nz4*nz4)
		nx4 = nx4 / nl#
		ny4 = ny4 / nl#
		nz4 = nz4 / nl#
		nl# = Sqr(nx5*nx5 + ny5*ny5 + nz5*nz5)
		nx5 = nx5 / nl#
		ny5 = ny5 / nl#
		nz5 = nz5 / nl#
	
		; Calculate tex coords of the 3 discrete vertices used by subdivision.
		u3# = (u1+u0)/2.0
		v3# = (v1+v0)/2.0
		w3# = (w1+w0)/2.0
		u4# = (u2+u1)/2.0
		v4# = (v2+v1)/2.0
		w4# = (w2+w1)/2.0
		u5# = (u0+u2)/2.0
		v5# = (v0+v2)/2.0
		w5# = (w0+w2)/2.0
	
		; Calculate colour of the 3 discrete vertices used by subdivision.
		r3# = (r1+r0)/2.0
		g3# = (g1+g0)/2.0
		b3# = (b1+b0)/2.0
		r4# = (r2+r1)/2.0
		g4# = (g2+g1)/2.0
		b4# = (b2+b1)/2.0
		r5# = (r0+r2)/2.0
		g5# = (g0+g2)/2.0
		b5# = (b0+b2)/2.0
		
		; Add the 4 unwelded tris comprising the subdivision to the
		; temporary, scratch pad mesh surface.
		tv0 = AddVertex(sd_surf,x0,y0,z0)
		tv3 = AddVertex(sd_surf,x3,y3,z3)
		tv5 = AddVertex(sd_surf,x5,y5,z5)
		tri0 = AddTriangle(sd_surf,tv0,tv3,tv5)
		VertexNormal sd_surf,tv0,nx0,ny0,nz0
		VertexNormal sd_surf,tv3,nx3,ny3,nz3
		VertexNormal sd_surf,tv5,nx5,ny5,nz5
		VertexColor sd_surf,tv0,r0,g0,b0
		VertexColor sd_surf,tv3,r3,g3,b3
		VertexColor sd_surf,tv5,r5,g5,b5
		VertexTexCoords sd_surf,tv0,u0,v0,w0
		VertexTexCoords sd_surf,tv3,u3,v3,w3
		VertexTexCoords sd_surf,tv5,u5,v5,w5
		
		tv1 = AddVertex(sd_surf,x1,y1,z1)
		tv4 = AddVertex(sd_surf,x4,y4,z4)
		tv3b = AddVertex(sd_surf,x3,y3,z3)
		tri1 = AddTriangle(sd_surf,tv1,tv4,tv3b)
		VertexNormal sd_surf,tv1,nx1,ny1,nz1
		VertexNormal sd_surf,tv4,nx4,ny4,nz4
		VertexNormal sd_surf,tv3b,nx3,ny3,nz3
		VertexColor sd_surf,tv1,r1,g1,b1
		VertexColor sd_surf,tv4,r4,g4,b4
		VertexColor sd_surf,tv3b,r3,g3,b3
		VertexTexCoords sd_surf,tv1,u1,v1,w1
		VertexTexCoords sd_surf,tv4,u4,v4,w4
		VertexTexCoords sd_surf,tv3b,u3,v3,w3

		tv2 = AddVertex(sd_surf,x2,y2,z2)
		tv5b = AddVertex(sd_surf,x5,y5,z5)
		tv4b = AddVertex(sd_surf,x4,y4,z4)
		tri2 = AddTriangle(sd_surf,tv2,tv5b,tv4b)
		VertexNormal sd_surf,tv2,nx2,ny2,nz2
		VertexNormal sd_surf,tv5b,nx5,ny5,nz5
		VertexNormal sd_surf,tv4b,nx4,ny4,nz4
		VertexColor sd_surf,tv2,r2,g2,b2
		VertexColor sd_surf,tv5b,r5,g5,b5
		VertexColor sd_surf,tv4b,r4,g4,b4
		VertexTexCoords sd_surf,tv2,u2,v2,w2
		VertexTexCoords sd_surf,tv5b,u5,v5,w5
		VertexTexCoords sd_surf,tv4b,u4,v4,w4
	
		tv3c = AddVertex(sd_surf,x3,y3,z3)
		tv4c = AddVertex(sd_surf,x4,y4,z4)
		tv5c = AddVertex(sd_surf,x5,y5,z5)
		tri3 = AddTriangle(sd_surf,tv3c,tv4c,tv5c)
		VertexNormal sd_surf,tv3c,nx3,ny3,nz3
		VertexNormal sd_surf,tv4c,nx4,ny4,nz4
		VertexNormal sd_surf,tv5c,nx5,ny5,nz5
		VertexColor sd_surf,tv3c,r3,g3,b3
		VertexColor sd_surf,tv4c,r4,g4,b4
		VertexColor sd_surf,tv5c,r5,g5,b5
		VertexTexCoords sd_surf,tv3c,u3,v3,w3
		VertexTexCoords sd_surf,tv4c,u4,v4,w4
		VertexTexCoords sd_surf,tv5c,u5,v5,w5
	
		divs = divs - 1
	
		tri_list = copy_tri_explode(sd_surf,tri0,d_surf,divs,tri_list,False)
		tri_list = copy_tri_explode(sd_surf,tri1,d_surf,divs,tri_list,False)
		tri_list = copy_tri_explode(sd_surf,tri2,d_surf,divs,tri_list,False)
		tri_list = copy_tri_explode(sd_surf,tri3,d_surf,divs,tri_list,False)

	Else

		dv0 = AddVertex(d_surf,x0,y0,z0)
		dv1 = AddVertex(d_surf,x1,y1,z1)
		dv2 = AddVertex(d_surf,x2,y2,z2)

		; Calculate and add a lone 'centre of tri' vertex (needed for
		; per-tri rotations and blast trajectory calculation).
		tx# = (x2+x1)/2.0
		ty# = (y2+y1)/2.0
		tz# = (z2+z1)/2.0
		cvx# = tx - ((tx-x0)/3.0)
		cvy# = ty - ((ty-y0)/3.0)
		cvz# = tz - ((tz-z0)/3.0)
		AddVertex(d_surf,cvx,cvy,cvz)
		
		real_tri = AddTriangle(d_surf,dv0,dv1,dv2)

		VertexNormal d_surf,dv0,nx0,ny0,nz0
		VertexNormal d_surf,dv1,nx1,ny1,nz1
		VertexNormal d_surf,dv2,nx2,ny2,nz2
		VertexColor d_surf,dv0,r0,g0,b0
		VertexColor d_surf,dv1,r1,g1,b1
		VertexColor d_surf,dv2,r2,g2,b2
		VertexTexCoords d_surf,dv0,u0,v0,w0
		VertexTexCoords d_surf,dv1,u1,v1,w1
		VertexTexCoords d_surf,dv2,u2,v2,w2

		; Add this tri to the linked list.
		link.tri_linkT = New tri_linkT
		link\prev = tri_list
		link\surf = d_surf
		link\tri = real_tri
		
		tri_list = link	
	EndIf
	
	Return tri_list
	
End Function


;
; Initializes and authorizes a mesh to start exploding.
; Note: can handle starting an explosion on a mesh that's already exploding.
;
; Params:
; ctrl        - Control variable of the mesh to start exploding.
; blast       - Strength of the blast to explode the mesh with.
; life        - Duration of the explosion animation (in frames).
; bfx,bfy,bfz - Blast focus coords (point in 3D space where the explosion is to
;               occur), relative to mesh centre.
; loc         - True to set blast focus using local space coords.
;               False (default) to set blast focus using coords aligned to world
;               axes. e.g. a blast focus of 0,-1,0 will cause an explosion directly
;               below the mesh's centre, regardless of the mesh's current rotation.
;
Function start_mesh_explode(ctrl.explode_ctrlT,blast#=1,life%=100,bfx#=0,bfy#=0,bfz#=0,loc%=False)

	If loc
		If ctrl\exploding
			; Temporarily re-instate mesh's original rotation so we can
			; calculate local blast focus coords.
			RotateEntity ctrl\mesh,ctrl\o_pitch,ctrl\o_yaw,ctrl\o_roll,1

			; Convert blast focus to world-aligned coords.	
			TFormPoint bfx,bfy,bfz,ctrl\mesh,0
			bfx = TFormedX() - EntityX(ctrl\mesh,1)
			bfy = TFormedY() - EntityY(ctrl\mesh,1)
			bfz = TFormedZ() - EntityZ(ctrl\mesh,1)

			RotateEntity ctrl\mesh,0,0,0,1
		Else
			; Convert blast focus to world-aligned coords.	
			TFormPoint bfx,bfy,bfz,ctrl\mesh,0
			bfx = TFormedX() - EntityX(ctrl\mesh,1)
			bfy = TFormedY() - EntityY(ctrl\mesh,1)
			bfz = TFormedZ() - EntityZ(ctrl\mesh,1)
		EndIf
	EndIf

	If Not ctrl\exploding
		; Align mesh axes to world axes, keeping mesh's visible
		; orientation (needed for gravity to work).
		ctrl\o_pitch = EntityPitch(ctrl\mesh,1)
		ctrl\o_yaw = EntityYaw(ctrl\mesh,1)
		ctrl\o_roll = EntityRoll(ctrl\mesh,1)
		RotateEntity ctrl\mesh,0,0,0,1
		RotateMesh ctrl\mesh,ctrl\o_pitch,ctrl\o_yaw,ctrl\o_roll
	EndIf
	
	tri.tri_linkT = ctrl\tri_list

	If JIGGLE_TRAJ
		While tri <> Null
		
			; Get 'centre of tri' vertex.
			cv = TriangleVertex(tri\surf,tri\tri,2) + 1
			cvx# = VertexX(tri\surf,cv)
			cvy# = VertexY(tri\surf,cv)
			cvz# = VertexZ(tri\surf,cv)
	
			; Use the vector from bfx,bfy,bfz (blast focus) to cvx,cvy,cvz ('centre
			; of tri' vert) as the trajectory, adding a random 'jiggle' factor.
			VertexCoords vm_surf,0,cvx-bfx,cvy-bfy,cvz-bfz
			RotateMesh vm_mesh,Rnd(-10,10),Rnd(-10,10),Rnd(-10,10)
	
			tvx# = VertexX(vm_surf,0)
			tvy# = VertexY(vm_surf,0)
			tvz# = VertexZ(vm_surf,0)
			tvl# = Sqr(tvx*tvx + tvy*tvy + tvz*tvz)

			; Set velocity along trajectory vector based on proximity to
			; explosion (blast focus), explosion strength and a random factor.
			v_scale# = tvl * Rnd(20.0,50.0)

			; Add to current dx,dy,dz in case this mesh is already exploding!
			tri\dx = tri\dx + (((tvx/tvl)/v_scale) * blast)
			tri\dy = tri\dy + (((tvy/tvl)/v_scale) * blast)
			tri\dz = tri\dz + (((tvz/tvl)/v_scale) * blast)
			
			; Base rotation on velocity.
			rot# = (Abs(tri\dx) + Abs(tri\dy) + Abs(tri\dz)) * 100
			tri\pitch = Rnd(-rot,rot)
			tri\yaw = Rnd(-rot,rot)
			tri\roll = Rnd(-rot,rot)
	
			tri = tri\prev
		Wend
	Else
		While tri <> Null
		
			; Get 'centre of tri' vertex.
			cv = TriangleVertex(tri\surf,tri\tri,2) + 1
			cvx# = VertexX(tri\surf,cv)
			cvy# = VertexY(tri\surf,cv)
			cvz# = VertexZ(tri\surf,cv)
	
			; Use the vector from bfx,bfy,bfz (blast focus) to cvx,cvy,cvz
			; ('centre of tri' vert) as the trajectory.
			tvx# = cvx - bfx
			tvy# = cvy - bfy
			tvz# = cvz - bfz
			tvl# = Sqr(tvx*tvx + tvy*tvy + tvz*tvz)

			; Set velocity along trajectory vector based on proximity to
			; explosion (blast focus), explosion strength and a random factor.
			v_scale# = tvl * Rnd(20.0,50.0)

			; Add to current dx,dy,dz in case this mesh is already exploding!
			tri\dx = tri\dx + (((tvx/tvl)/v_scale) * blast)
			tri\dy = tri\dy + (((tvy/tvl)/v_scale) * blast)
			tri\dz = tri\dz + (((tvz/tvl)/v_scale) * blast)

			; Base rotation on velocity.
			rot# = (Abs(tri\dx) + Abs(tri\dy) + Abs(tri\dz)) * 100
			tri\pitch = Rnd(-rot,rot)
			tri\yaw = Rnd(-rot,rot)
			tri\roll = Rnd(-rot,rot)

			tri = tri\prev
		Wend
	EndIf

	ctrl\life = life
	ctrl\fade_start = Ceil(Float(life)*FADE_START#)
	ctrl\fader = 1.0/(ctrl\fade_start+1)
	ctrl\exploding = True

	EntityFX ctrl\mesh,16

End Function


;
; Free all mem used by an explodable mesh created with copy_mesh_explode().
;
; Params:
; ctrl - Control variable of the explodable mesh to be freed.
;
Function free_mesh_explode(ctrl.explode_ctrlT)

	this.tri_linkT = ctrl\tri_list
	
	While this <> Null
		delme.tri_linkT = this
		this = delme\prev
		Delete delme
	Wend
	
	FreeEntity ctrl\mesh
	Delete ctrl
	
End Function


;
; Display debug info.
;
Function show_info()
	
	If fps_timeout
		frame_count = frame_count + 1

		If MilliSecs() > fps_timeout Then
			fps_timeout = MilliSecs() + 1000 
			fps = frame_count 
			frame_count = 0 
		
			If fps < slowest_fps Or slowest_fps = 0 Then slowest_fps = fps
		EndIf 
		
		If frame_time > slowest_frame Then slowest_frame = frame_time
		
		Color 0,255,0
		Text 10,10," Triangles: " + TrisRendered()
		Color 255,255,0
		Text 10,25," Millisecs: " + frame_time
		Text 10,40,"   Slowest: " + slowest_frame
		Color 0,255,255
		Text 10,55,"       FPS: " + fps
		Text 10,70,"     Worst: " + slowest_fps
		Color 255,255,255
	Else
		; First call initialization.
		fps_timeout = MilliSecs() + 1000 
	EndIf
	
End Function
