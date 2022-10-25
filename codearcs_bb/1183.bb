; ID: 1183
; Author: big10p
; Date: 2004-10-29 17:16:14
; Title: Screen interference effect
; Description: It's variable, too!

;
; Variable screen filter static/interference effect demo.
;
; By Big10p (A.K.A. Chris Chadwick) 2004
;

	Graphics3D 800,600,32

	WireFrame 0
	AntiAlias 0
	SeedRnd MilliSecs()
	fps_timer = CreateTimer(60)

	; Create fuzz texture.
	fuzz_tex = CreateTexture(128,128)
	SetBuffer TextureBuffer(fuzz_tex)
	For n = 1 To 5000
		c = Rand(0,255)
		Color c,c,c
		Plot Rand(0,127),Rand(0,127)
	Next

	; Create line texture.
	line_tex = CreateTexture(128,128)
	SetBuffer TextureBuffer(line_tex)
	Color 255,255,255
	Line 0,50,127,50
	Color 128,128,128
	Line 0,51,127,51
	Line 0,49,127,49
	SetBuffer BackBuffer()

	; Lights, camera!
	;
	piv = CreatePivot()
	cam = CreateCamera(piv)
	PositionEntity cam,0,5,-5
	PointEntity cam,piv
	light = CreateLight(1,cam)
	RotateEntity light,45,45,0

	; Create static filter.
	;
	filter = create_quad(5)
	ScaleMesh filter,2,2,1
	EntityFX filter,1
	EntityParent filter,cam
	TextureBlend fuzz_tex,5
	TextureBlend line_tex,3
	EntityTexture filter,fuzz_tex,0,0
	EntityTexture filter,line_tex,0,1
	PositionEntity filter,EntityX(cam,1),EntityY(cam,1),EntityZ(cam,1),1
	RotateEntity filter,EntityPitch(cam),EntityYaw(cam),EntityRoll(cam),1
	MoveEntity filter,0,0,1.01
	EntityOrder filter,-1
	filter_alpha# = 0.5
	line_scroll# = 0.0

	; Create scene objects.
	;	
	ground = CreateCube()
	ScaleMesh ground,10,.1,10
	EntityColor ground,100,100,200
	PositionEntity ground,0,-2,0
	EntityAlpha ground,.4
	mirror = CreateMirror()
	PositionEntity mirror,0,-2,0
	ball = CreateSphere(16)
	cube = CreateCube()
	EntityColor cube,255,0,0
	PositionEntity cube,4,0,0
	cone = CreateCone(16)
	EntityColor cone,0,255,0
	PositionEntity cone,-4,0,0
	cylinder = CreateCylinder(16)
	EntityColor cylinder,0,0,255
	PositionEntity cylinder,0,0,4
	thing = CreateSphere(3)
	EntityColor thing,255,255,0
	PositionEntity thing,0,0,-4

	info$ = "Press up/down ARROW KEYS to alter effect strength"

			
	; --- Main loop ---
	
	While Not KeyHit(1)
		TurnEntity piv,0,.5,0
		
		; Set filter alpha (visibility).
		;
		If KeyDown(200) Then filter_alpha = filter_alpha + .01
		If KeyDown(208) Then filter_alpha = filter_alpha - .01
		If filter_alpha > 1.0
			filter_alpha = 1.0
		ElseIf filter_alpha < 0
			filter_alpha = 0
		EndIf
		EntityAlpha filter,filter_alpha

		; Update filter textures.
		;
		PositionTexture fuzz_tex,Rnd(0,1),Rnd(0,1)
		PositionTexture line_tex,0,line_scroll
		line_scroll = (line_scroll + .01) Mod 1.0

		RenderWorld
		
		Color 255,0,0
		Text 401,10,info$,1
		Text 399,10,info$,1
		Text 401,9,info$,1
		Text 401,11,info$,1
		Color 255,255,0
		Text 400,10,info$,1
		
		WaitTimer(fps_timer)
		Flip(1)
	Wend

	End

Function create_quad(tex_scale#)
	
	m = CreateMesh()
	s = CreateSurface(m)
	
	AddVertex s,-1,1,0,  0,0
	AddVertex s,1,1,0,   tex_scale,0
	AddVertex s,-1,-1,0, 0,tex_scale
	AddVertex s,1,-1,0,  tex_scale,tex_scale
	
	AddTriangle s,0,1,3
	AddTriangle s,0,3,2
	
	Return m

End Function
