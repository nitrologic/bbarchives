; ID: 1011
; Author: big10p
; Date: 2004-05-03 10:59:39
; Title: "Twirly" special FX demo
; Description: Spinning, star shaped entity thingies

; 
; Twirly Special FX demo by big10p (A.K.A. Chris Chadwick) 2003
;


	Graphics3D 800,600,32
	SetBuffer BackBuffer()

	WireFrame 0
	AntiAlias 0

	SeedRnd MilliSecs()

	Type twirly_layerT
		Field ent			; Layer star mesh entity.
		Field rot#			; Amount to rotate layer each update.
		Field clut_i%		; Index of layer's CLUT colour.
	End Type
	
	Type twirlyT
		Field tx#,ty#		; Demo-specific 2D twirly movement amounts.
		
		Field piv			; Control pivot - all layers are attached to this.
		Field hidden%		; Bool flag to indicate if Twirly is hidden.
		Field layer_bank	; Bank holding Handle() IDs of all layers.
		Field num_layers%	; Total number of Twirly layers.
		Field clut%			; clut_banks() index of Twirly's CLUT.
		Field max_clut_i%	; Bank index of last colour (RGB set) in CLUT.
		Field clut_cycle%	; CLUT cycle control. - left, 0 no cycle, + right.
		Field cycle_count%	; Count down to next CLUT cycle.
	End Type

	; Create 2 starfield planes.
	Global tex1 = CreateTexture(128,128,4)
	make_tex(tex1)
	plane1 = CreatePlane()
	RotateEntity plane1,-90,0,0
	PositionEntity plane1,0,0,10
	EntityTexture plane1,tex1
	Global tex2 = CreateTexture(128,128,4)
	make_tex(tex2)
	plane2 = CreatePlane()
	RotateEntity plane2,-90,0,0
	PositionEntity plane2,0,0,11
	EntityTexture plane2,tex2
	
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
		
	Global cam = CreateCamera()
	PositionEntity cam,0,0,-20
	CameraZoom cam,1.4
	
	light = CreateLight()	
	
	Const END_OF_CLUT% = -1
	Const END_OF_CLUT_LIST% = -2
	Const MAX_CLUTS% = 100
	Global num_cluts% = 0
	
	Dim clut_banks(MAX_CLUTS-1)
	open_twirly()
	copy_clut_grad(1,5)

	; Create some twirlies.
	For n = 1 To 3
		tw1.twirlyT = create_preset_twirly(n)
		PositionEntity tw1\piv,Rnd(-5,5),Rnd(-5,5),-10	
	Next
	
	vp1# = 1 : vp2# = 1
		
	
	; --- Main loop ---
	
	While Not KeyHit(1)

		frame_start = MilliSecs()

		If KeyHit(28) Then slowmo = Not slowmo
		If KeyHit(14) Then wiref = Not wiref : WireFrame wiref

		For this.twirlyT = Each twirlyT
			CameraProject cam,EntityX(this\piv),EntityY(this\piv),EntityZ(this\piv)
			If ProjectedX() > 800 Or ProjectedX() < 0 Then this\tx=-this\tx
			If ProjectedY() > 600 Or ProjectedY() < 0 Then this\ty=-this\ty
			TranslateEntity this\piv,this\tx,this\ty,0
		Next
		
		vp1 = vp1 - 0.003
		vp2 = vp2 - 0.001
		PositionTexture tex1,1,vp1
		PositionTexture tex2,1,vp2
		
		update_twirly()
		RenderWorld
		;show_info()
		;show_clut(10,80)

		frame_time = MilliSecs() - frame_start	
		
		WaitTimer(fps_timer)
		Flip(1)

		If slowmo Then Delay 200
	Wend

	close_twirly()
	ClearWorld	

	End


; The Twirly CLUTS, defined in RGB sets.
.twirly_clut_data

Data 255,0,0
Data 0,255,0
Data 0,0,255
Data END_OF_CLUT

Data 255,255,0
Data 0,255,0
Data 0,255,255
Data 0,0,255
Data 255,0,255
Data 255,0,0
Data END_OF_CLUT

Data END_OF_CLUT_LIST


;
; Draws random coloured starfield onto text.
;
Function make_tex(tex)

	SetBuffer TextureBuffer(tex)

	For y = 0 To 127
		For x = 0 To 127
			WritePixel x,y,$00000000
		Next
	Next
	Color 255,255,255

	For n = 1 To 200
		r = Rand(0,255) Shl 16
		g = Rand(0,255) Shl 8
		b = Rand(0,255)
		WritePixel Rand(0,127),Rand(0,127),$ff000000 Or (r Or g Or b)
	Next
	ScaleTexture tex,30,30
	SetBuffer BackBuffer()
	
End Function


;
; Prepares the Twirly system ready for use.
;
Function open_twirly()

	Local r%,g%,b%
	Local clut_size%[MAX_CLUTS-1]
	Local cs% = 0
		
	; Count CLUT entries and size.
	Restore twirly_clut_data
	Repeat
		Read r
		If r = END_OF_CLUT
			clut_size[num_cluts] = cs
			num_cluts = num_cluts + 1
			cs = 0
			
			Read r : If r = END_OF_CLUT_LIST Then Exit
		EndIf
		Read g
		Read b
		
		cs = cs + 1
	Forever
	
	; Create CLUT banks.
	Restore twirly_clut_data
	For i = 0 To num_cluts-1
		clut_banks(i) = CreateBank((clut_size[i]*3)*4)
	
		For n = 0 To BankSize(clut_banks(i))-1 Step 12
			Read r,g,b
			PokeInt clut_banks(i),n,r
			PokeInt clut_banks(i),n+4,g
			PokeInt clut_banks(i),n+8,b
		Next
		
		Read dummy	; Skip END_OF_CLUT terminator.
	Next
	
End Function


;
; Closes down the Twirly system, freeing all resources.
;
Function close_twirly()

	; Kill all Twirlies.
	For this.twirlyT = Each twirlyT
		free_twirly(this)
	Next

	; Free the CLUT banks.
	For i = 0 To num_cluts-1
		FreeBank clut_banks(i)
	Next
	
End Function


;
; Frees all resources used by a given twirly.
;
; Params:
; twirly - The twirly to be freed.
;
Function free_twirly(twirly.twirlyT)

	; Free all Twirly layer resources.
	For n = 0 To BankSize(twirly\layer_bank)-1 Step 4
		layer.twirly_layerT = Object.twirly_layerT(PeekInt(twirly\layer_bank,n))
		FreeEntity layer\ent
		Delete layer	
	Next
	
	; Free actual Twirly resources.
	FreeEntity twirly\piv
	FreeBank layer_bank
	Delete twirly
	
End Function


;
; Creates a single layer twirly. See create_star_mesh() for params.
;	
Function create_twirly.twirlyT(radius#,points%,indent#,ratio%=True)
	
	twirly.twirlyT = New twirlyT
	twirly\piv = CreatePivot()
	RotateEntity twirly\piv,0,180,0	; So we can point twirly at camera.
	twirly\num_layers = 1
	twirly\hidden = False

	twirly\tx = Rnd(.01,.03) : If Rand(0,1) Then twirly\tx = -twirly\tx
	twirly\ty = Rnd(.01,.03) : If Rand(0,1) Then twirly\ty = -twirly\ty
	
	layer.twirly_layerT = New twirly_layerT
	twirly\layer_bank = CreateBank(4)
	PokeInt twirly\layer_bank,0,Handle(layer)
	
	; Create initial twirly layer.
	layer\ent = create_star_mesh(radius,points,indent,ratio)
	EntityParent layer\ent,twirly\piv
	PositionEntity layer\ent,0,0,0,0
	EntityFX layer\ent,1+4+8

	Return twirly

End Function


;
; Adds a layer to the specified, existing twirly.
; See create_star_mesh() for other params.
;
Function add_twirly_layer(twirly.twirlyT,radius#,points%,indent#,ratio%=True)

	layer.twirly_layerT = New twirly_layerT
	bs = BankSize(twirly\layer_bank)
	ResizeBank twirly\layer_bank,bs+4
	PokeInt twirly\layer_bank,bs,Handle(layer)
	
	; Create twirly layer.
	layer\ent = create_star_mesh(radius,points,indent,ratio)
	PositionEntity layer\ent,0,0,Float(twirly\num_layers)*0.001
	EntityParent layer\ent,twirly\piv,1

	twirly\num_layers = twirly\num_layers + 1
	EntityFX layer\ent,1+4+8

End Function


;
; Sequentially 'paints' all layers of a twirly with the given CLUT.
;
; Params:
; twirly - Twirly to be coloured with the specified CLUT. 
; clut   - Number of CLUT to colour twirly with.
; cycle  - Specifies how to cycle the CLUT every time the twirly is updated:
;          0 (default) to not cycle the CLUT.
;          -1 to cycle the CLUT to the left.
;          1 to cycle the CLUT to the right.
; dir    - Direction to colour the twirly layers with:
;          0 (default) to colour layers from first-to-last.
;          1 to colour layers from last-to-first.
;
Function set_twirly_clut(twirly.twirlyT,clut%,cycle%=0,dir%=0)

	clut_i% = 0
	clut_size% = BankSize(clut_banks(clut))	
	cb = clut_banks(clut)
	
	If dir = 0
		For n = 0 To twirly\num_layers-1
			id = PeekInt(twirly\layer_bank,n*4)
			layer.twirly_layerT = Object.twirly_layerT(id)
			layer\clut_i = clut_i
			EntityColor layer\ent,PeekInt(cb,clut_i),PeekInt(cb,clut_i+4),PeekInt(cb,clut_i+8)
			clut_i = clut_i + 12
			If clut_i = clut_size Then clut_i = 0
		Next	
	Else
		For n = twirly\num_layers-1 To 0 Step -1
			id = PeekInt(twirly\layer_bank,n*4)
			layer.twirly_layerT = Object.twirly_layerT(id)
			layer\clut_i = clut_i
			EntityColor layer\ent,PeekInt(cb,clut_i),PeekInt(cb,clut_i+4),PeekInt(cb,clut_i+8)
			clut_i = clut_i + 12
			If clut_i = clut_size Then clut_i = 0
		Next	
	EndIf
	
	twirly\clut = clut
	twirly\clut_cycle = cycle
	twirly\cycle_count = Abs(cycle)
	twirly\max_clut_i = clut_size-12
	
End Function


;
; Sets all layers of a twirly to use a given blend mode.
;
Function set_twirly_blend(twirly.twirlyT,mode%)

	For n = 0 To BankSize(twirly\layer_bank)-1 Step 4
		layer.twirly_layerT = Object.twirly_layerT(PeekInt(twirly\layer_bank,n))
		EntityBlend layer\ent,mode
	Next
	
End Function


;
; Sets all layers of a twirly to use a given alpha level.
;
Function set_twirly_alpha(twirly.twirlyT,alpha#)

	For n = 0 To BankSize(twirly\layer_bank)-1 Step 4
		layer.twirly_layerT = Object.twirly_layerT(PeekInt(twirly\layer_bank,n))
		EntityAlpha layer\ent,alpha
	Next
	
End Function


;
; Creates a copy of an existing CLUT with gradients between each colour.
;
; Params:
; clut   - Number of CLUT to be copied.
; grads  - Number of gradients to insert between each colour.
; grad_r - Each colour from the copied CLUT is faded to this colour.
; grad_g   If not specified (default), colours fade to the next colour
; grad_b   from the CLUT being copied.
; 
; Returns:
; Index of new clut in clut_banks() array.
;
Function copy_clut_grad(clut%,grads%,grad_r#=-1,grad_g#=-1,grad_b#=-1)

	old_clut = clut_banks(clut)
	old_size = BankSize(clut_banks(clut))

	new_clut = CreateBank(old_size*(grads+1))
	clut_banks(num_cluts) = new_clut
	num_cluts = num_cluts + 1
	
	poke_i = 0
	
	If grad_r=-1 Or grads=0
		; Create gradients between adjacent colours from the copied CLUT.

		grad_div = grads + 1
		
		For i = 0 To old_size-1 Step 12
			r1# = Float(PeekInt(old_clut,i))
			g1# = Float(PeekInt(old_clut,i+4))
			b1# = Float(PeekInt(old_clut,i+8))
	
			; Get colour to fade to.
			If i = old_size-12
				; Wraparound to first colour.
				r2# = Float(PeekInt(old_clut,0))
				g2# = Float(PeekInt(old_clut,4))
				b2# = Float(PeekInt(old_clut,8))
			Else
				; Use next colour in CLUT.
				r2# = Float(PeekInt(old_clut,i+12))
				g2# = Float(PeekInt(old_clut,i+12+4))
				b2# = Float(PeekInt(old_clut,i+12+8))
			EndIf
					
			r_step# = (r2-r1)/grad_div
			g_step# = (g2-g1)/grad_div
			b_step# = (b2-b1)/grad_div
			
			; Add original colour and gradients to new CLUT.
			For n = 0 To grads
				PokeInt new_clut,poke_i,  Int(r1 + (n*r_step))
				PokeInt new_clut,poke_i+4,Int(g1 + (n*g_step))
				PokeInt new_clut,poke_i+8,Int(b1 + (n*b_step))
				poke_i = poke_i + 12
			Next
		Next
	Else
		; Create gradients from copied CLUT colour to grad_r,grad_g,grad_b.
		
		For i = 0 To old_size-1 Step 12
			r1# = Float(PeekInt(old_clut,i))
			g1# = Float(PeekInt(old_clut,i+4))
			b1# = Float(PeekInt(old_clut,i+8))
		
			r_step# = (grad_r-r1)/grads
			g_step# = (grad_g-g1)/grads
			b_step# = (grad_b-b1)/grads
			
			; Add original colour and gradients to new CLUT.
			For n = 0 To grads
				PokeInt new_clut,poke_i,  Int(r1 + (n*r_step))
				PokeInt new_clut,poke_i+4,Int(g1 + (n*g_step))
				PokeInt new_clut,poke_i+8,Int(b1 + (n*b_step))
				poke_i = poke_i + 12
			Next
		Next	
	EndIf
	
	Return num_cluts - 1
			
End Function


;
; Updates ALL non-hidden Twirlies.
;
Function update_twirly()

	For this.twirlyT = Each twirlyT
		If Not this\hidden	
			PointEntity this\piv,cam
			cb = clut_banks(this\clut)
	
			; Twirly's CLUT cycle frequency management.
			do_cycle = False
			If this\clut_cycle
				this\cycle_count = this\cycle_count - 1
				If this\cycle_count = 0
					this\cycle_count = Abs(this\clut_cycle)
					do_cycle = True
				EndIf
			EndIf
			
			If do_cycle
				; Update each layer attached to this twirly, cycling the CLUT.
				For n = 0 To BankSize(this\layer_bank)-1 Step 4
					layer.twirly_layerT = Object.twirly_layerT(PeekInt(this\layer_bank,n))
					TurnEntity layer\ent,0,0,layer\rot
			
					If this\clut_cycle < 0
						; Cycle CLUT to the left.
						clut_i = layer\clut_i + 12
						If clut_i > this\max_clut_i Then clut_i = 0
					Else
						; Cycle CLUT to the right.
						clut_i = layer\clut_i - 12
						If clut_i < 0 Then clut_i = this\max_clut_i
					EndIf
		
					layer\clut_i = clut_i
					EntityColor layer\ent,PeekInt(cb,clut_i),PeekInt(cb,clut_i+4),PeekInt(cb,clut_i+8)
				Next
			Else
				; Update each layer attached to this twirly.
				For n = 0 To BankSize(this\layer_bank)-1 Step 4
					layer.twirly_layerT = Object.twirly_layerT(PeekInt(this\layer_bank,n))
					TurnEntity layer\ent,0,0,layer\rot			
				Next
			EndIf

		End If
	Next
		
End Function


;
; Creates a planar, star-shaped mesh.
;
; Params:
; radius - Radius of star in world units.
; points - Number of points the star should have.
; indent - Sets the 'depth' of the indent between the points of the star.
; ratio  - True (default) to indicate that indent is a ratio of the radius
;          e.g. 1=no indent (circle), .5=indent midway between point and centre.
;          False to indicate that indent is an absolute size.
;
; Returns:
; The newly created star mesh.
;
Function create_star_mesh(radius#=1.0,points%=5,indent#=0.5,ratio%=True)

	If ratio Then indent = radius * indent	; Convert indent to ratio of radius.

	mesh = CreateMesh()
	surf = CreateSurface(mesh)
	
	verts% = points * 2
	vang# = 0
	vang_step# = 360.0/Float(verts)

	; Add verts for all star points & indents.
	For n = 0 To verts-1

		If (n And 1) Then size# = indent Else size# = radius

		AddVertex(surf,Cos(vang)*size,Sin(vang)*size,0)
		VertexNormal surf,n,0,0,-1	

		vang = vang + vang_step
	Next	

	; Add centre vert.
	AddVertex(surf,0,0,0)
	VertexNormal surf,verts,0,0,-1
	
	; Make triangles.
	For n = 0 To verts-2
		AddTriangle surf,n,verts,n+1
	Next
	AddTriangle surf,verts-1,verts,0

	Return mesh
	
End Function


;
; Creates a twirly_type preset twirly.
;
Function create_preset_twirly.twirlyT(twirly_type%)

	Select twirly_type
		Case 1
			dir#=.01
			For n = 1 To 30
				If n=1
					tw.twirlyT = create_twirly(n*.01,10,.2)
				Else
					add_twirly_layer(tw,n*.1,10,.1)
				EndIf
				set_layer_rotate(tw,n-1,dir*n)
			Next
			set_twirly_clut(tw,2,2,1)
			set_twirly_blend(tw,3)
			set_twirly_alpha(tw,.2)
			Return tw
		Case 2
			dir#=.01
			For n = 1 To 30
				If n=1
					tw.twirlyT = create_twirly(n*.01,5,.2)
				Else
					add_twirly_layer(tw,n*.1,10,.3)
				EndIf
				set_layer_rotate(tw,n-1,dir*n)
				dir = -dir
			Next
			set_twirly_clut(tw,2,2,1)
			set_twirly_blend(tw,3)
			set_twirly_alpha(tw,.2)
			Return tw
		Case 3
			dir#=.01
			For n = 1 To 30
				If n=1
					tw.twirlyT = create_twirly(n*.01,5,.2)
				Else
					add_twirly_layer(tw,n*.1,5,.3)
				EndIf
				set_layer_rotate(tw,n-1,dir*n)
			Next
			set_twirly_clut(tw,2,2,1)
			set_twirly_blend(tw,3)
			set_twirly_alpha(tw,.2)
			Return tw
	End Select		
	
End Function


;
; Hides a Twirly from view. Ignores if Twirly is already hidden.
;
; Params:
; twirly - The Twirly to hide from view.
;
Function hide_twirly(twirly.twirlyT)

	If twirly\hidden = False
		HideEntity twirly\piv
		twirly\hidden = True
	End If
	
End Function


;
; Makes a hidden Twirly visible. Ignores if Twirly is already visible.
;
; Params:
; twirly - The hidden Twirly to make visible.
;
Function show_twirly(twirly.twirlyT)

	If twirly\hidden = True
		ShowEntity twirly\piv
		twirly\hidden = False
	End If
	
End Function


;
; Sets a given twirly layer to an absolute rotation.
;
Function rotate_twirly_layer(twirly.twirlyT,layer%,rot#)

	star.twirly_layerT = Object.twirly_layerT(PeekInt(twirly\layer_bank,layer*4))
	RotateEntity star\ent,0,0,rot,1

End Function


;
; Sets how much a given twirly layer should rotate by every update.
;
Function set_layer_rotate(twirly.twirlyT,layer%,rot#)

	star.twirly_layerT = Object.twirly_layerT(PeekInt(twirly\layer_bank,layer*4))
	star\rot = rot

End Function


;
; Debug function to visually display CLUT(s).
;
; Params:
; cx   - Screen X coord to display CLUT at.
; cy   - Screen Y coord to display CLUT at.
; clut - Number of CLUT to display.
;        If not specified (default) then all CLUTS are displayed.
;
Function show_clut(cx%=0,cy%=0,clut%=-1)

	Local cxs = cx
	Local cw = 10
	Local ch = 10
	Local gap = 0
	
	If clut = -1
		first_clut = 0
		last_clut = num_cluts - 1
	Else
		first_clut = clut
		last_clut = clut
	EndIf
		
	For n = first_clut To last_clut
		clut = clut_banks(n)
		
		For j = 0 To BankSize(clut)-1 Step 12
			r = PeekInt(clut,j)
			g = PeekInt(clut,j+4)
			b = PeekInt(clut,j+8)
			Color r,g,b
			Rect cx,cy,cw,ch,1
			cx = cx + cw + gap
			If (cx+cw-1)>=GraphicsWidth() Then cx=cxs:cy=cy+ch+gap
		Next
	
		cx = cxs
		cy = cy + ch + 4
	Next

	Color 255,255,255
	
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
