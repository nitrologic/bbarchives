; ID: 1008
; Author: big10p
; Date: 2004-04-28 18:17:34
; Title: 3D Points Demo
; Description: (Another) 3D points system for vertex handles etc.

;
; 3D points demo by big10p (A.K.A. Chris Chadwick) 2004.
;
; Written with Blitz3D v1.86
;
; Known issues:
; - Tiny (1 pixel) points don't always render correctly: very occasionally they
;   render as 2 pixels, or even not at all. This depends on the distance/angle
;   the point is viewed at. I think the problem is with single precision floats
;   not being quite precise enough to calculate such points accurately. As such,
;   there's not much I can do about it.
; - The method of making pivot points selectable by giving them a pickable
;   radius is quite effective, but not ideal: it can sometimes be difficult to
;   select the desired point when it's in close proximity to other pickable
;   points on screen, as their pick spheres can 'get in the way'. It's OK for
;   the purposes of this demo and I do know of a way to improve this, but I
;   haven't had time to try it yet. :)
; - When moving the camera close-up to a vertex point, the camera bias can cause
;   it to be clipped by the near clipping range, even though the vertex it
;   represents is still visible. The bigger the bias, the worse the problem gets
;   so a minimal bias is recommended! I'm sure this issue if fixable but isn't
;   a big deal, IMO.
; - There appears to be a z-fighting issue when using a camera with fairly
;   extreme scaling. To see this, scale the camera to x=.01, y=.01, z=1, then
;   pull the camera away from the sphere. There doesn't appear to be a problem
;   in 16-bit modes, which use a w-buffer.
;

	Graphics3D 800,600,32
	SetBuffer BackBuffer()

	WireFrame 0
	AntiAlias 0

	SeedRnd MilliSecs()

	Type pointT
		Field first_vert
		Field surf
		Field x#, y#, z#
		Field size%
	End Type

	Type ppointT
		Field first_vert
		Field surf
		Field piv
		Field size%
		Field mesh_surf	; Used by vertex points.
		Field mesh_vert	;
	End Type
	
	Type free_pointT
		Field first_vert
		Field surf
	End Type

	; 3D points system defaults.	
	Const MAX_BATCH_POINTS% = 16383 ; 16383=maximum points that fit a single surface.
	Const POINT_BIAS# = 0.0
	Const PPOINT_BIAS# = 0.1
	Const PICK_RAD# = 0.05
	Const POINT_SIZE% = 3
	Const POINT_R% = 200
	Const POINT_G% = 200
	Const POINT_B% = 200
			
	Global screen_width% = GraphicsWidth()
	Global screen_height% = GraphicsHeight()
	Global vport_width%
	Global vport_height%
	Global vport_x%
	Global vport_y%
	Global pixel_size#
	Global cam_scale_x#
	Global cam_scale_y#
	Global cam_scale_z#
	Global cam_zoom#

	Global cam_yaw# = 0.0
	Global cam_pitch# = 45.0
	Global sphere
	Global world_piv = CreatePivot()
	Global picked_ent
	Global picked_point.ppointT = Null
	Global mouse_on% = True

	Global batch_points% = 0
	Global active_points% = 0
	Global point_mesh
	Global point_surf
	Global pad_mesh
	Global pad_surf
	Global quad_size_x#, quad_size_y#
	
	Const NORMAL_OP% = 0
	Const MOVING_POINT% = 1
	Const MOVING_CAMERA% = 2
	Global op_state% = NORMAL_OP
		
	Global frame_count%
	Global fps%
	Global slowest_fps%
	Global fps_timeout%
	Global frame_time%
	Global slowest_frame%
	Global frame_start%
	fps_timer = CreateTimer(60)
	Global wiref_on% = False
	Global info_state% = 1
	
	Global campiv = CreatePivot()	
	Global cam = CreateCamera(campiv)
	PositionEntity cam,0,0,-5
	TurnEntity campiv,cam_pitch,cam_yaw,0
	CameraRange cam,.1,1000
	
	light = CreateLight(1,cam)

	point_mesh = CreateMesh()
	point_surf = CreateSurface(point_mesh)
	EntityFX point_mesh,1+2
	
	pad_mesh = CreateMesh()
	pad_surf = CreateSurface(pad_mesh)
	AddVertex(pad_surf,0,0,0)
	AddVertex(pad_surf,0,0,0)
	AddVertex(pad_surf,0,0,0)
	AddVertex(pad_surf,0,0,0)
	AddTriangle(pad_surf,0,1,2)
	AddTriangle(pad_surf,3,2,1)
	HideEntity pad_mesh

	set_viewport(screen_width,screen_height)
	set_cam_scale(1,1,1)
	set_cam_zoom(1)
	create_world()

	MoveMouse screen_width/2,screen_height/2


	; --- Main loop ---
	
	While Not KeyHit(1)
		frame_start = MilliSecs()
		
		Cls	

		mouse_control()
		keyboard_control()
		update_points()

		If wiref_on
			; Render world objects in wireframe.
			WireFrame 1
			HideEntity point_mesh
			ShowEntity world_piv
			CameraClsMode cam,1,1		
			RenderWorld
			
			; Render point quads in non-wireframe.
			WireFrame 0
			ShowEntity point_mesh
			HideEntity world_piv
			CameraClsMode cam,0,0		
			RenderWorld

			ShowEntity world_piv
		Else
			RenderWorld
		EndIf
		
		Color 200,0,0
		Rect vport_x,vport_y,vport_width,vport_height,0
		
		show_info()

		If mouse_on
			; Draw mouse pointer.
			mx = MouseX()
			my = MouseY()
			Color 0,0,0
			Rect mx-10,my-1,7,3
			Rect mx-1,my-10,3,7
			Rect mx+4,my-1,7,3
			Rect mx-1,my+4,3,7
			Color 255,255,255
			Rect mx-9,my,5,1
			Rect mx,my-9,1,5
			Rect mx+5,my,5,1
			Rect mx,my+5,1,5
			Plot mx,my
		EndIf

		frame_time = MilliSecs() - frame_start	
		;WaitTimer(fps_timer)
		Flip(0)
	Wend

	close_3Dpoint_system()	; Free 3D point specific resources.
	ClearWorld				; Free all other demo specific resources.

	End

	
;
; Creates and positions a basic (floating) point.
;
; Params:
; x,y,z - World coords to place point at.
; size  - Size to make point.
;
; Returns:
; Pointer to the pointT instance of the created point.
;
Function create_point.pointT(x#=0, y#=0, z#=0, size%=POINT_SIZE)

	point.pointT = New pointT
	point\x = x
	point\y = y
	point\z = z

	pool.free_pointT = First free_pointT

	If pool <> Null
		point\first_vert = pool\first_vert
		point\surf = pool\surf
		
		Delete pool
	Else
		; Create a new quad in the point mesh.

		If batch_points = MAX_BATCH_POINTS
			; Current batch surface is full so create a new one.
			batch_points = 0
			point_surf = CreateSurface(point_mesh)
		EndIf
		
		v0 = AddVertex(point_surf,0,0,0)
		v1 = AddVertex(point_surf,0,0,0)
		v2 = AddVertex(point_surf,0,0,0)
		v3 = AddVertex(point_surf,0,0,0)
		AddTriangle(point_surf,v0,v1,v2)
		AddTriangle(point_surf,v3,v2,v1)

		point\first_vert = v0
		point\surf = point_surf

		batch_points = batch_points + 1
	EndIf
	
	colour_point(point,POINT_R,POINT_G,POINT_B)
	
	point\size = size	

	active_points = active_points + 1
	
	Return point
	
End Function


;
; Creates and positions a pivot point.
;
; Params:
; x,y,z - World coords to place pivot point at.
; size  - Size to make pivot point.
;
; Returns:
; Pointer to the ppointT instance of the created pivot point.
;
Function create_piv_point.ppointT(x#=0, y#=0, z#=0, size%=POINT_SIZE)

	point.ppointT = New ppointT
	point\piv = CreatePivot()
	PositionEntity point\piv,x,y,z,1
 	
	; Store ppointT instance ID as pivot's name.
	NameEntity point\piv,Str$(Handle(point))

	pool.free_pointT = First free_pointT

	If pool <> Null
		point\first_vert = pool\first_vert
		point\surf = pool\surf
		
		Delete pool
	Else
		; Create a new quad in the point mesh.

		If batch_points = MAX_BATCH_POINTS
			; Current batch surface is full so create a new one.
			batch_points = 0
			point_surf = CreateSurface(point_mesh)
		EndIf
		
		v0 = AddVertex(point_surf,0,0,0)
		v1 = AddVertex(point_surf,0,0,0)
		v2 = AddVertex(point_surf,0,0,0)
		v3 = AddVertex(point_surf,0,0,0)
		AddTriangle(point_surf,v0,v1,v2)
		AddTriangle(point_surf,v3,v2,v1)

		point\first_vert = v0
		point\surf = point_surf

		batch_points = batch_points + 1
	EndIf
	
	colour_piv_point(point,POINT_R,POINT_G,POINT_B)
	
	point\size = size	

	active_points = active_points + 1
	
	Return point

End Function


;
; Creates and attaches a pivot point to every vertex in the specified mesh.
;
; Params:
; mesh   - Mesh to create vertex points for.
; radius - The pick radius for each vertex point.
; r,g,b  - Red, green & blue components of points' colour.
; size   - Size to make all vertex points.
;
Function create_vert_points(mesh, radius#=PICK_RAD, r%=POINT_R, g%=POINT_G, b%=POINT_B, size%=POINT_SIZE)

	For sn = 1 To CountSurfaces(mesh)

		surf = GetSurface(mesh,sn)

		For n = 0 To CountVertices(surf)-1
			
			vx# = VertexX(surf,n)
			vy# = VertexY(surf,n)
			vz# = VertexZ(surf,n)
			
			TFormPoint vx,vy,vz,mesh,0

			point.ppointT = create_piv_point(TFormedX(),TFormedY(),TFormedZ(),size)
			EntityParent point\piv,mesh
			point\mesh_surf = surf
			point\mesh_vert = n
			
			colour_piv_point(point,r,g,b)
			set_pick_radius(point,radius)
		Next

	Next

End Function


;
; Frees a point by making it invisible and available for recycling.
;
; Params:
; point - The point to be freed.
;
Function free_point(point.pointT)

	fv = point\first_vert
	surf = point\surf
	
	; Add point to the list of deleted points.
	deleted.free_pointT = New free_pointT
	deleted\first_vert = fv
	deleted\surf = surf

	; Make deleted point invisible.
	VertexCoords surf,fv,0,0,0
	VertexCoords surf,fv+1,0,0,0
	VertexCoords surf,fv+2,0,0,0
	VertexCoords surf,fv+3,0,0,0
	
	Delete point
	
	active_points = active_points - 1

End Function


;
; Frees a pivot point by making it invisible and available for recycling.
;
; Params:
; point - The pivot point to be freed.
;
Function free_piv_point(point.ppointT)

	fv = point\first_vert
	surf = point\surf
	
	; Add point to the list of deleted points.
	deleted.free_pointT = New free_pointT
	deleted\first_vert = fv
	deleted\surf = surf

	; Make deleted point invisible.
	VertexCoords surf,fv,0,0,0
	VertexCoords surf,fv+1,0,0,0
	VertexCoords surf,fv+2,0,0,0
	VertexCoords surf,fv+3,0,0,0
	
	FreeEntity point\piv
	Delete point
	
	active_points = active_points - 1
	
End Function


;
; Frees all the vertex points attached to the specified mesh.
;
; Params:
; mesh - The mesh you want vertex points removed from.
;
Function free_vert_points(mesh)

	For point.ppointT = Each ppointT
	
		If GetParent(point\piv) = mesh Then free_piv_point(point)
	
	Next

End Function


;
; Sets the colour of an existing point.
;
; Params:
; point - The point to be coloured.
; r,g,b - Red, green & blue components of the point's colour.
;
Function colour_point(point.pointT, r=POINT_R, g=POINT_G, b=POINT_B)

	VertexColor point\surf,point\first_vert,r,g,b
	VertexColor point\surf,point\first_vert+1,r,g,b
	VertexColor point\surf,point\first_vert+2,r,g,b
	VertexColor point\surf,point\first_vert+3,r,g,b

End Function


;
; Sets the colour of an existing pivot point.
;
; Params:
; point - The point to be coloured.
; r,g,b - Red, green & blue components of the point's colour.
;
Function colour_piv_point(point.ppointT, r=POINT_R, g=POINT_G, b=POINT_B)

	VertexColor point\surf,point\first_vert,r,g,b
	VertexColor point\surf,point\first_vert+1,r,g,b
	VertexColor point\surf,point\first_vert+2,r,g,b
	VertexColor point\surf,point\first_vert+3,r,g,b

End Function


;
; Simple wrapper function to position a point in 3D space.
;
; Params:
; point - The point to be positioned.
; x,y,z - 3D world coords to position point at.
;
Function position_point(point.pointT, x#, y#, z#)

	point\x = x
	point\y = y
	point\z = z

End Function


;
; Sets the pick radius of an existing pivot point.
;
; Params:
; point  - Pivot point to set pick radius of.
; radius - The pick radius of the pivot point.
;          Note: a radius of 0 makes the point unpickable.
;
Function set_pick_radius(point.ppointT, radius#=PICK_RAD)

	If radius > 0
		; Make point pivot pickable and set it's radius.
		EntityRadius point\piv,radius;,radius
		EntityPickMode point\piv,1
	Else
		; Make point pivot unpickable.
		EntityRadius point\piv,0,0
		EntityPickMode point\piv,0
	EndIf

End Function


;
; Calculates all variables required to achieve correct scaling of points.
;
Function set_scaling()

	pixel_size# = 1.0 / vport_width
	quad_size_x = pixel_size * cam_scale_x
	quad_size_y = pixel_size * cam_scale_y

End Function


;
; Sets dimensions of camera viewport & centres it on screen.
;
; Params:
; width, height - New dimensions to set the viewport to.
;
Function set_viewport(width%, height%)

	If width > screen_width
		width = screen_width
	ElseIf width < 100
		width = 100
	EndIf
	
	If height > screen_height
		height = screen_height
	ElseIf height < 100
		height = 100
	EndIf

	vport_width = width
	vport_height = height
	vport_x = (screen_width-vport_width) / 2
	vport_y = (screen_height-vport_height) / 2

	CameraViewport cam,vport_x,vport_y,width,height
	
	set_scaling()

End Function


;
; Sets camera scale.
;
; Params:
; x,y,z - New x,y,z scale to set camera to.
;
Function set_cam_scale(x#, y#, z#)

	If x < .01 Then x = .01
	If y < .01 Then y = .01
	If z < .01 Then z = .01

	ScaleEntity cam,x,y,z
	
	cam_scale_x = x
	cam_scale_y = y
	cam_scale_z = z
	
	set_scaling()
	
End Function


;
; Sets camera zoom.
;
; Params:
; zoom - New zoom to set camera to.
;
Function set_cam_zoom(zoom#)

	If zoom < .01 Then zoom = .01
	
	CameraZoom cam,zoom
	cam_zoom = zoom

End Function


;
; Updates all active points.
;
Function update_points()

	; Build correctly sized template quad in scratch pad mesh.
	VertexCoords pad_surf,0,-quad_size_x,quad_size_y,0
	VertexCoords pad_surf,1,quad_size_x,quad_size_y,0
	VertexCoords pad_surf,2,-quad_size_x,-quad_size_y,0
	VertexCoords pad_surf,3,quad_size_x,-quad_size_y,0
	
	; Set template quad to match camera plane rotation.
	RotateMesh pad_mesh,EntityPitch(cam,1),EntityYaw(cam,1),EntityRoll(cam,1)

	; Retrieve vertex vectors of template quad.
	; (we can then scale these base vectors according
	; to the distance each point is from the camera plane).
	v0x# = VertexX(pad_surf,0)
	v0y# = VertexY(pad_surf,0)
	v0z# = VertexZ(pad_surf,0)
	v1x# = VertexX(pad_surf,1)
	v1y# = VertexY(pad_surf,1)
	v1z# = VertexZ(pad_surf,1)
	v2x# = VertexX(pad_surf,2)
	v2y# = VertexY(pad_surf,2)
	v2z# = VertexZ(pad_surf,2)
	v3x# = VertexX(pad_surf,3)
	v3y# = VertexY(pad_surf,3)
	v3z# = VertexZ(pad_surf,3)

	camx# = EntityX(cam,1)
	camy# = EntityY(cam,1)
	camz# = EntityZ(cam,1)

	If POINT_BIAS <> 0	
		; Update all floating (non-pivot) points, including camera bias.
		
		For p.pointT = Each pointT
	
			fv = p\first_vert
			surf = p\surf
			x# = p\x
			y# = p\y
			z# = p\z
	
			; Re-calculate point's position to include bias.
			vbx# = camx - x
			vby# = camy - y
			vbz# = camz - z
			vl# = Sqr(vbx*vbx + vby*vby + vbz*vbz)
			x = x + ((vbx / vl) * POINT_BIAS)
			y = y + ((vby / vl) * POINT_BIAS)
			z = z + ((vbz / vl) * POINT_BIAS)
		
			TFormPoint x,y,z,0,cam
			scale# = (TFormedZ()/cam_zoom) * p\size
			
			VertexCoords surf,fv,x+(v0x*scale),y+(v0y*scale),z+(v0z*scale)
			VertexCoords surf,fv+1,x+(v1x*scale),y+(v1y*scale),z+(v1z*scale)
			VertexCoords surf,fv+2,x+(v2x*scale),y+(v2y*scale),z+(v2z*scale)
			VertexCoords surf,fv+3,x+(v3x*scale),y+(v3y*scale),z+(v3z*scale)
			
		Next

	Else
		; Update all floating (non-pivot) points without camera bias.
		
		For p.pointT = Each pointT
	
			fv = p\first_vert
			surf = p\surf
			x# = p\x
			y# = p\y
			z# = p\z	

			TFormPoint x,y,z,0,cam
			scale# = (TFormedZ()/cam_zoom) * p\size
			
			VertexCoords surf,fv,x+(v0x*scale),y+(v0y*scale),z+(v0z*scale)
			VertexCoords surf,fv+1,x+(v1x*scale),y+(v1y*scale),z+(v1z*scale)
			VertexCoords surf,fv+2,x+(v2x*scale),y+(v2y*scale),z+(v2z*scale)
			VertexCoords surf,fv+3,x+(v3x*scale),y+(v3y*scale),z+(v3z*scale)
		Next
	
	EndIf


	If PPOINT_BIAS <> 0
		; Update all pivot points, including camera bias.
	
		For pp.ppointT = Each ppointT
	
			fv = pp\first_vert
			surf = pp\surf
			x# = EntityX(pp\piv,1)
			y# = EntityY(pp\piv,1)
			z# = EntityZ(pp\piv,1)
	
			; Re-calculate point's position to include bias.
			vbx# = camx - x
			vby# = camy - y
			vbz# = camz - z
			vl# = Sqr(vbx*vbx + vby*vby + vbz*vbz)
			x = x + ((vbx / vl) * PPOINT_BIAS)
			y = y + ((vby / vl) * PPOINT_BIAS)
			z = z + ((vbz / vl) * PPOINT_BIAS)
	
			TFormPoint x,y,z,0,cam
			scale# = (TFormedZ()/cam_zoom) * pp\size
			
			VertexCoords surf,fv,x+(v0x*scale),y+(v0y*scale),z+(v0z*scale)
			VertexCoords surf,fv+1,x+(v1x*scale),y+(v1y*scale),z+(v1z*scale)
			VertexCoords surf,fv+2,x+(v2x*scale),y+(v2y*scale),z+(v2z*scale)
			VertexCoords surf,fv+3,x+(v3x*scale),y+(v3y*scale),z+(v3z*scale)
	
		Next

	Else
		; Update all pivot points without camera bias.
	
		For pp.ppointT = Each ppointT
	
			fv = pp\first_vert
			surf = pp\surf
			x# = EntityX(pp\piv,1)
			y# = EntityY(pp\piv,1)
			z# = EntityZ(pp\piv,1)
		
			TFormPoint x,y,z,0,cam
			scale# = (TFormedZ()/cam_zoom) * pp\size
			
			VertexCoords surf,fv,x+(v0x*scale),y+(v0y*scale),z+(v0z*scale)
			VertexCoords surf,fv+1,x+(v1x*scale),y+(v1y*scale),z+(v1z*scale)
			VertexCoords surf,fv+2,x+(v2x*scale),y+(v2y*scale),z+(v2z*scale)
			VertexCoords surf,fv+3,x+(v3x*scale),y+(v3y*scale),z+(v3z*scale)
	
		Next

	EndIf
		
End Function


;
; Closes down the 3D points system by freeing all resources used.
;
Function close_3Dpoint_system()

	For p.ppointT = Each ppointT
		Delete p
	Next

	For pp.ppointT = Each ppointT
		FreeEntity pp\piv
		Delete pp
	Next
	
	For fp.free_pointT = Each free_pointT
		Delete fp
	Next
	
	FreeEntity point_mesh
	FreeEntity pad_mesh
	
End Function


;
; Creates all demo world objects.
;
Function create_world()

	sphere = CreateSphere(16,world_piv)
	EntityColor sphere,0,0,200
	create_vert_points(sphere)
	EntityPickMode sphere,2

	; Create plane of points.
	rows% = 70
	cols% = 70

	For x# = -(rows/2.0) To (rows-1)/2.0

		For z# = -(cols/2.0) To (cols-1)/2.0
			this.pointT = create_point(x/1,-2,z/1)

			r = Rand(0,150)
			g = Rand(0,150)
			b = Rand(0,150)
			colour_point(this, r, g, b)
		Next

	Next

End Function


;
; Reads and acts on keyboard controls.
;
Function keyboard_control()

	sa# = 0.01

	If KeyDown(42)
		; Check shifted key presses...

		If KeyDown(75) Then set_cam_scale(cam_scale_x-sa,cam_scale_y,cam_scale_z)
		If KeyDown(77) Then set_cam_scale(cam_scale_x+sa,cam_scale_y,cam_scale_z)
		If KeyDown(72) Then set_cam_scale(cam_scale_x,cam_scale_y-sa,cam_scale_z)
		If KeyDown(80) Then set_cam_scale(cam_scale_x,cam_scale_y+sa,cam_scale_z)
		If KeyDown(74) Then set_cam_scale(cam_scale_x,cam_scale_y,cam_scale_z-sa)
		If KeyDown(78) Then set_cam_scale(cam_scale_x,cam_scale_y,cam_scale_z+sa)
	Else
		; Check non-shifted key presses...

		If KeyHit(17)
			wiref_on = Not wiref_on

			If wiref_on
				For n = 1 To CountChildren(world_piv)
					ent = GetChild(world_piv,n)
					EntityFX ent,1+16
				Next 
			Else
				For n = 1 To CountChildren(world_piv)
					ent = GetChild(world_piv,n)
					EntityFX ent,0
				Next 
	
				CameraClsMode cam,1,1		
				WireFrame 0
				ShowEntity point_mesh
				ShowEntity world_piv
			EndIf
	
		EndIf

		If KeyHit(59) Then info_state = Not info_state
		
		If KeyDown(200) Then MoveEntity cam,0,0,.1
		If KeyDown(208) Then MoveEntity cam,0,0,-.1
		If KeyDown(205) Then MoveEntity cam,.1,0,0
		If KeyDown(203) Then MoveEntity cam,-.1,0,0
	
		If KeyDown(75) Then set_viewport(vport_width-1,vport_height)
		If KeyDown(77) Then set_viewport(vport_width+1,vport_height)
		If KeyDown(72) Then set_viewport(vport_width,vport_height-1)
		If KeyDown(80) Then set_viewport(vport_width,vport_height+1)
	
		If KeyDown(74) Then set_cam_zoom(cam_zoom-sa)
		If KeyDown(78) Then set_cam_zoom(cam_zoom+sa)
	EndIf

End Function


;
; Reads and acts on mouse controls.
;
Function mouse_control()

	Select op_state
	Case NORMAL_OP
		If MouseDown(2)
			If picked_point <> Null
				; Reset old picked point.
				colour_piv_point(picked_point)
				picked_point\size = POINT_SIZE
				picked_point = Null
			EndIf

			; Flush mouse speed.
			MouseXSpeed() : MouseYSpeed() :	MouseZSpeed()

			mouse_on = False
			op_state = MOVING_CAMERA			
		Else
			If MouseDown(1)
			
				If picked_point <> Null
					mouse_on = False
					op_state = MOVING_POINT
				EndIf
			Else
				picked_ent = CameraPick(cam,MouseX()-vport_x,MouseY()-vport_y)
		
				If picked_ent
					; An entity has been picked...
					
					pp.ppointT = Object.ppointT(EntityName(picked_ent))
		
					If pp <> Null
						; Picked entity is a pivot point...
						
						If pp <> picked_point
							; Picked point is a new one...
							
							If picked_point <> Null
								; Reset old picked point.
								colour_piv_point(picked_point)
								picked_point\size = POINT_SIZE
							EndIf
							
							colour_piv_point(pp,255,0,0)
							pp\size = 5
							picked_point = pp
						EndIf
					Else
						; Picked entity is not a pivot point...
						
						If picked_point <> Null
							; Reset old picked point.
							colour_piv_point(picked_point)
							picked_point\size = POINT_SIZE
							picked_point = Null
						EndIf
					EndIf
				Else
					; No entity has been picked...
					
					If picked_point <> Null
						; Reset old picked point.
						colour_piv_point(picked_point)
						picked_point\size = POINT_SIZE
						picked_point = Null
					EndIf
				EndIf
			EndIf	
		EndIf
			
	Case MOVING_POINT
		If MouseDown(1)
			ps = picked_point\mesh_surf
			pv = picked_point\mesh_vert
			nx# = VertexNX(ps,pv) * .01
			ny# = VertexNY(ps,pv) * .01
			nz# = VertexNZ(ps,pv) * .01
			vx# = VertexX(ps,pv) + nx
			vy# = VertexY(ps,pv) + ny
			vz# = VertexZ(ps,pv) + nz
			VertexCoords ps,pv,vx,vy,vz
			PositionEntity picked_ent,vx,vy,vz
		Else
			mouse_on = True
			op_state = NORMAL_OP
		EndIf
	Case MOVING_CAMERA
		If MouseDown(2)
			cam_dist# = EntityDistance(cam,campiv)	
	
			mx = MouseXSpeed()
			my = MouseYSpeed()
			mz = MouseZSpeed()
			MoveMouse screen_width/2,screen_height/2
			FlushMouse
			
			If mz
				mm# = mz * (cam_dist/7.0)
				If Abs(mm) < .1 Then mm = .1 * Sgn(mm)
				MoveEntity cam,0,0,mm
			EndIf
	
			If MouseDown(1)
				TranslateEntity cam,0,-my * (cam_dist/1000),0
				TranslateEntity cam,mx * (cam_dist/1000),0,0
			ElseIf MouseDown(3)
				mm# = -my * (cam_dist/800)
				If Abs(mm) < .1 Then mm = .1 * Sgn(mm)
				MoveEntity cam,0,0,mm
			Else	 
				cam_pitch = cam_pitch + (-my/4.0)
				cam_yaw = cam_yaw + (mx/4.0)
				RotateEntity campiv,cam_pitch,cam_yaw,0
			EndIf
		Else
			mouse_on = True
			op_state = NORMAL_OP
		EndIf
		
	End Select

End Function

;
; Display demo info.
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
		
		If info_state = 1
			Color 255,255,255
			Text 10,10, "(Press F1 for help)"
			Color 0,255,0
			Text 10,25, "  Triangles: " + TrisRendered()
			Color 255,255,0
			Text 10,40, "  Millisecs: " + frame_time
			Text 10,55, "    Slowest: " + slowest_frame
			Color 0,255,255
			Text 10,70, "        FPS: " + fps
			Text 10,85, "      Worst: " + slowest_fps
			Color 0,255,0
			Text 10,100,"Point Surfs: " + CountSurfaces(point_mesh)
			Text 10,115,"     Points: " + active_points
			Color 255,255,0
			Text 10,130,"  Cam Scale: x="+cam_scale_x+" y="+cam_scale_y+" z="+cam_scale_z
			Text 10,145,"   Cam Zoom: "+cam_zoom
			Text 10,160,"   Viewport: width="+vport_width+" height="+vport_height
		Else
			Color 255,255,255
			Text 10,10, "Mouse Controls:"
			Color 200,255,0
			Text 10,40, "Click and hold left mouse button on a vertex"
			Text 10,55, "handle to move that vertex along it's normal."
			Color 0,255,0
			Text 10,85, "With right mouse button held down:"
			Text 10,100,"  drag to tumble camera."
			Text 10,115,"  use wheel to dolly camera (in steps)."
			Text 10,130,"  hold middle button and drag to dolly camera (fine tune)."
			Text 10,145,"  hold left button and drag to track camera."
			Color 255,255,255
			Text 10,175,"Keyboard Controls:"
			Color 0,255,0
			Text 10,205,"Up/Down arrows dolly camera."
			Text 10,220,"Left/Right arrows track camera."
			Text 10,235,"W toggles wireframe."
			Text 10,250,"F1 toggles info."
			Text 10,265,"On numeric keypad:"
			Text 10,280,"  arrows resize camera viewport."
			Text 10,295,"  +/- zoom camera in/out."
			Text 10,310,"On numeric keypad with LEFT SHIFT held down:"
			Text 10,325,"  arrows scale camera on x/y axes."
			Text 10,340,"  +/- scale camera on z axis."		
		EndIf
	Else
		; First call initialization.
		fps_timeout = MilliSecs() + 1000 
	EndIf
	
End Function
