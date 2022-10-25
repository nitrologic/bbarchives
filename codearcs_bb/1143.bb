; ID: 1143
; Author: big10p
; Date: 2004-08-22 18:32:38
; Title: Mesh Clipping Demo
; Description: Clips a mesh along an arbitrary plane.

;
; Mesh clip demo by big10p (A.K.A. Chris Chadwick) 2004.
;
; Written with Blitz3D v1.87
;
; Known issues:
; 1) copy_mesh_clip() attempts to paint the clipped mesh with the same brushes
;    that are applied to the mesh being copied. For multiple surface meshes,
;    GetSurfaceBrush() is used but only works if the mesh's surfaces have been
;    painted with PaintSurface(). If the multi surface mesh has been painted
;    en masse - using EntityTexture, EntityColor etc. - it doesn't work.
; 2) There seems to be a slight, noticeable color change when clipping tris that
;    are vertex colored. Not sure if this is down to a lack of precision with floats
;    or something I'm doing wrong. I'll have to investigate some more, I think. :)
; 3) I seem to get a much higher FPS (with frame locking turned off) if I turn off the
;    visible clipping plane. Not sure why it makes such a big difference as it's only
;    one surface with two triangles in!? :/
;


	Graphics3D 800,600,32

	; Create chequer pattern texture.
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
	SetBuffer BackBuffer()
	
	WireFrame 0
	AntiAlias 0

	SeedRnd MilliSecs()

	; Demo mesh info type.
	Type infoT
		Field mesh
		Field FX%
		Field vert_info%
	End Type
	
	; Frame timing and control.
	Global frame_count%
	Global fps%
	Global slowest_fps%
	Global fps_timeout%
	Global frame_time%
	Global slowest_frame%
	Global frame_start%
	fps_timer = CreateTimer(60)

	; Demo state flags.
	show_text% = True
	show_plane% = True
	rotate_plane% = True
	rotate_mesh% = True
	cull_backface% = False
	wiref% = False
	slowmo% = False
	frame_lock% = True

	; Flags to tell clip_tri functions the order to wind triangles.
	Const WIND_012% = 0
	Const WIND_102% = 1
	Const WIND_201% = 2

	; Flags to tell copy_mesh_clip() which vertex info to include when clipping.
	Const CLIP_NORMALS% = 1
	Const CLIP_UV_SET0% = 2
	Const CLIP_UV_SET1% = 4
	Const CLIP_RGBA%    = 8
	Const DEF_CLIP_INFO% = CLIP_NORMALS+CLIP_UV_SET0
	
	cam = CreateCamera()
	CameraRange cam,1,100
	PositionEntity cam,0,0,-3
	
	light = CreateLight()
	AmbientLight 100,100,100
	
	; Create the demo meshes.
	Dim mesh.infoT(0)
	create_meshes()
	current_mesh% = 0
			
	; Clipping plane.
	Const MAX_PLANE_DIST# = 1.3
	plane_pitch# = 0
	plane_yaw# = 0
	plane_dist# = -MAX_PLANE_DIST
	plane_move# = 0.01	
	plane_piv = CreatePivot()
	plane = CreateMesh(plane_piv)
	ps = CreateSurface(plane)
	AddVertex(ps,-2,0,2)
	AddVertex(ps,2,0,2)
	AddVertex(ps,-2,0,-2)
	AddVertex(ps,2,0,-2)
	AddTriangle(ps,0,1,2)
	AddTriangle(ps,1,3,2)
	EntityAlpha plane,.3
	EntityColor plane,0,0,200
	EntityFX plane,1+16
	PositionEntity plane,0,plane_dist,0
	RotateEntity plane_piv,plane_pitch,plane_yaw,0
	
	clip_mesh = copy_mesh_clip(mesh(current_mesh)\mesh,plane_dist,plane_pitch,plane_yaw,mesh(current_mesh)\vert_info)
	EntityFX clip_mesh,mesh(current_mesh)\FX Xor (cull_backface Shl 4)

	;
	; --- Main loop ---
	;
	
	While Not KeyHit(1)

		frame_start = MilliSecs()
	
		If KeyHit(2) Then show_text = Not show_text
		If KeyHit(3)
			show_plane = Not show_plane
			If show_plane Then ShowEntity plane Else HideEntity plane
		EndIf
		If KeyHit(4)
			rotate_plane = Not rotate_plane
			plane_pitch = 0
			plane_yaw = 0
		EndIf
		If KeyHit(5) Then rotate_mesh = Not rotate_mesh
		If KeyHit(6) Then cull_backface = Not cull_backface
		If KeyHit(7) Then wiref = Not wiref : WireFrame wiref
		If KeyHit(8) Then slowmo = Not slowmo
		If KeyHit(9) Then frame_lock = Not frame_lock
		If KeyHit(57) Then current_mesh = (current_mesh + 1) Mod 5

		If rotate_mesh Then RotateMesh mesh(current_mesh)\mesh,1,0,1

		; Change clipping plane distance.
		plane_dist = plane_dist + plane_move
		If Abs(plane_dist) > MAX_PLANE_DIST Then plane_move = -plane_move
		
		; Rotate clipping plane.
		If rotate_plane
			plane_pitch = (plane_pitch + 1) Mod 360
			plane_yaw = (plane_yaw + .5) Mod 360
		EndIf
		
		; Create an updated, clipped version of the current mesh.
		FreeEntity clip_mesh
		clip_mesh = copy_mesh_clip(mesh(current_mesh)\mesh,plane_dist,plane_pitch,plane_yaw,mesh(current_mesh)\vert_info)
		EntityFX clip_mesh,mesh(current_mesh)\FX Xor (cull_backface Shl 4)

		; Position visible plane to match clipping plane.
		If show_plane
			PositionEntity plane,0,plane_dist,0
			RotateEntity plane_piv,plane_pitch,plane_yaw,0
		EndIf
		
		RenderWorld

		frame_time = MilliSecs() - frame_start	
		If show_text Then show_info()

		If frame_lock
			WaitTimer(fps_timer)
			Flip True
		Else
			Flip False
		EndIf

		If slowmo Then Delay 200

	Wend

	End


;
; Creates a copy of a mesh, clipped along the given plane.
; Note: this function can only really perform clipping along an XZ aligned plane.
;       To perform clipping on an arbitrary, rotated plane, we cheat :) - first
;       we align the mesh to the XZ plane, perform the clipping, then return
;       the mesh to it's original orientation.
;
; Params:
; source_mesh - Mesh to be copied (remains unaltered by this function).
; plane_y     - Y distance of clipping plane from mesh origin.
; plane_pitch - Pitch of clipping plane.
; plane_yaw   - Yaw of clipping plane.
; vert_info   - Bit flags defining vertex info to include when clipping.
;
; Returns:
; Handle of newly created, clipped mesh.
;
Function copy_mesh_clip(source_mesh,plane_y#,plane_pitch#,plane_yaw#,vert_info%=DEF_CLIP_INFO)

	surf_count = CountSurfaces(source_mesh)

	; Temporarily align source mesh to XZ clipping plane.
	; Note: doing yaw THEN pitch rotation separately is important!
	; Prevents gimble lock when returning the mesh back to it's original rotation.
	RotateMesh source_mesh,0,-plane_yaw,0
	RotateMesh source_mesh,-plane_pitch,0,0

	If surf_count=1
		; We can make a copy of single surface meshes more quickly using CopyMesh.
		; (Unfortunately, CopyMesh combines surfs unless they use different textures
		; so this method can't be used with multi surface meshes).
		dest_mesh = CopyMesh(source_mesh)

		; Kill tris but keep verts.
		ClearSurface GetSurface(dest_mesh,1),False,True

		brush = GetEntityBrush(source_mesh)
		PaintEntity dest_mesh,brush
		FreeBrush brush
	Else
		dest_mesh = CreateMesh()
	
		; Copy all surfaces and vertices from source_mesh into dest_mesh.
		;
		For ss=1 To surf_count
			
			source_surf = GetSurface(source_mesh,ss)
			brush = GetSurfaceBrush(source_surf)
			dest_surf = CreateSurface(dest_mesh,brush)
			FreeBrush brush
	
			For sv=0 To CountVertices(source_surf)-1
				vy# = VertexY(source_surf,sv)
				dv = AddVertex(dest_surf,VertexX(source_surf,sv),vy,VertexZ(source_surf,sv))
				
				; We don't need to bother with any other info for verts
				; in dest_mesh that are beyond the clipping plane.
				If Not vy<plane_y
					VertexNormal dest_surf,dv,VertexNX(source_surf,sv),VertexNY(source_surf,sv),VertexNZ(source_surf,sv)
					VertexTexCoords dest_surf,dv,VertexU(source_surf,sv,0),VertexV(source_surf,sv,0),0, 0
					VertexTexCoords dest_surf,dv,VertexU(source_surf,sv,1),VertexV(source_surf,sv,1),0, 1
					VertexColor dest_surf,dv,VertexRed(source_surf,sv),VertexGreen(source_surf,sv),VertexBlue(source_surf,sv),VertexAlpha(source_surf,sv)
				EndIf
			Next
		Next
	EndIf	

	; Process all surfaces in source_mesh...
	;
	For ss=1 To CountSurfaces(source_mesh)
		
		source_surf = GetSurface(source_mesh,ss)
		dest_surf = GetSurface(dest_mesh,ss)
		
		; Process all trinagles in source_surf...
		;
		For t=0 To CountTriangles(source_surf)-1

			v0 = TriangleVertex(source_surf,t,0)
			v1 = TriangleVertex(source_surf,t,1)
			v2 = TriangleVertex(source_surf,t,2)
			
			clip0 = VertexY(source_surf,v0) < plane_y
			clip1 = VertexY(source_surf,v1) < plane_y
			clip2 = VertexY(source_surf,v2) < plane_y
			
			Select clip0+clip1+clip2
			
			Case 0	; There are no 'bad' verts in this triangle...
			
				; Add this triangle so it's the same as it appears in source_surf.
				AddTriangle(dest_surf,v0,v1,v2)

			Case 1	; There is 1 'bad' vert in this triangle...
					
				;Find the 1 'bad' vert.
				;
				If clip0	 	; v0 is bad...

					clip_tri_1bad(source_surf,dest_surf,plane_y, v0,v1,v2, WIND_012, vert_info)

				ElseIf clip1	;v1 is bad...

					clip_tri_1bad(source_surf,dest_surf,plane_y, v1,v0,v2, WIND_102, vert_info)
				
				Else			;v2 is bad...

					clip_tri_1bad(source_surf,dest_surf,plane_y, v2,v0,v1, WIND_201, vert_info)
			
				EndIf
				
			Case 2	; There are 2 'bad' verts in this triangle...
			
				; Find the 1 'good' vert.
				;
				If Not clip0		; v0 is good...
				
					clip_tri_2bad(source_surf,dest_surf,plane_y, v0,v1,v2, WIND_012, vert_info)

				ElseIf Not clip1	; v1 is good...

					clip_tri_2bad(source_surf,dest_surf,plane_y, v1,v0,v2, WIND_102, vert_info)

				Else				; v2 is good...

					clip_tri_2bad(source_surf,dest_surf,plane_y, v2,v0,v1, WIND_201, vert_info)

				EndIf				

			End Select

		Next
		
	Next

	; Rotate source_mesh and dest_mesh to source_mesh's original rotation.
	RotateMesh source_mesh,plane_pitch,plane_yaw,0
	RotateMesh dest_mesh,plane_pitch,plane_yaw,0

	Return dest_mesh
	
End Function


;
; Creates a clipped triangle from a source_surf triangle that has 2 'bad' vertices. i.e. 2 of the
; triangles verts are beyond the clipping plane.
;
; Params:
; source_surf - Surface containing the triangle to be clipped.
; dest_surf   - Surface to build the clipped triangle in.
; plane_y     - Y distance of clipping plane from mesh origin.
; good_v      - Index of the one 'good' vertex in triangle.
; bad1_v      - Index of the first 'bad' vertex in the triangle.
; bad2_v      - Index of the second 'bad' vertex in the triangle.
; wind_order  - Flag indicating the order to wind the clipped triangle.
; vert_info   - Bit flags defining vertex info to include when clipping.
;
Function clip_tri_2bad(source_surf,dest_surf,plane_y#, good_v%,bad1_v%,bad2_v%, wind_order%, vert_info%)

	; Retrieve good/bad vert coords.
	good_vx# = VertexX(source_surf,good_v)
	good_vy# = VertexY(source_surf,good_v)
	good_vz# = VertexZ(source_surf,good_v)
	bad1_vx# = VertexX(source_surf,bad1_v)
	bad1_vy# = VertexY(source_surf,bad1_v)
	bad1_vz# = VertexZ(source_surf,bad1_v)
	bad2_vx# = VertexX(source_surf,bad2_v)
	bad2_vy# = VertexY(source_surf,bad2_v)
	bad2_vz# = VertexZ(source_surf,bad2_v)

	; Find Y distance from good vert to clipping plane.
	clip_y_dist# = good_vy-plane_y

	;
	; Calculate & add first new vert...
	;
	
	; Vector to bad vert 1 from good vert.
	xv# = bad1_vx-good_vx
	yv# = bad1_vy-good_vy
	zv# = bad1_vz-good_vz
	
	y_dist# = good_vy-bad1_vy
	side# = 1.0/y_dist
	ratio1# = clip_y_dist*side
	new1_v = AddVertex(dest_surf,good_vx+xv*ratio1,good_vy+yv*ratio1,good_vz+zv*ratio1)

	;
	; Calculate & add second new vert...
	;
	
	; Vector to bad vert 2 from good vert.
	xv# = bad2_vx-good_vx
	yv# = bad2_vy-good_vy
	zv# = bad2_vz-good_vz
	
	y_dist# = good_vy-bad2_vy
	side# = 1.0/y_dist
	ratio2# = clip_y_dist*side
	new2_v = AddVertex(dest_surf,good_vx+xv*ratio2,good_vy+yv*ratio2,good_vz+zv*ratio2)

	; Make the clipped triangle, wound in the specified way.
	Select wind_order
	Case WIND_012
		AddTriangle(dest_surf,good_v,new1_v,new2_v)
	Case WIND_102
		AddTriangle(dest_surf,new1_v,good_v,new2_v)
	Case WIND_201
		AddTriangle(dest_surf,new1_v,new2_v,good_v)
	End Select

	;
	; Update the requested settings for the 2 new verts...
	;
	
	If vert_info And CLIP_NORMALS
		; Retrieve original triangle's normals.
		good_nx# = VertexNX(source_surf,good_v)
		good_ny# = VertexNY(source_surf,good_v)
		good_nz# = VertexNZ(source_surf,good_v)
		bad1_nx# = VertexNX(source_surf,bad1_v)
		bad1_ny# = VertexNY(source_surf,bad1_v)
		bad1_nz# = VertexNZ(source_surf,bad1_v)
		bad2_nx# = VertexNX(source_surf,bad2_v)
		bad2_ny# = VertexNY(source_surf,bad2_v)
		bad2_nz# = VertexNZ(source_surf,bad2_v)
	
		; Calculate & set normals for the 2 new verts.
		nx# = good_nx + (bad1_nx-good_nx) * ratio1
		ny# = good_ny + (bad1_ny-good_ny) * ratio1
		nz# = good_nz + (bad1_nz-good_nz) * ratio1
		nl# = Sqr(nx*nx + ny*ny + nz*nz)
		VertexNormal dest_surf,new1_v,nx/nl,ny/nl,nz/nl
		nx# = good_nx + (bad2_nx-good_nx) * ratio2
		ny# = good_ny + (bad2_ny-good_ny) * ratio2
		nz# = good_nz + (bad2_nz-good_nz) * ratio2
		nl# = Sqr(nx*nx + ny*ny + nz*nz)
		VertexNormal dest_surf,new2_v,nx/nl,ny/nl,nz/nl
	EndIf

	If vert_info And CLIP_UV_SET0
		; Retrieve original triangle's set 0 UVs.
		good_tu# = VertexU(source_surf,good_v,0)
		good_tv# = VertexV(source_surf,good_v,0)
		bad1_tu# = VertexU(source_surf,bad1_v,0)
		bad1_tv# = VertexV(source_surf,bad1_v,0)
		bad2_tu# = VertexU(source_surf,bad2_v,0)
		bad2_tv# = VertexV(source_surf,bad2_v,0)
	
		; Calculate & set set 0 UVs for the 2 new verts.
		u# = good_tu + (bad1_tu-good_tu) * ratio1
		v# = good_tv + (bad1_tv-good_tv) * ratio1
		VertexTexCoords dest_surf,new1_v, u,v,0, 0
		u# = good_tu + (bad2_tu-good_tu) * ratio2
		v# = good_tv + (bad2_tv-good_tv) * ratio2
		VertexTexCoords dest_surf,new2_v, u,v,0, 0
	EndIf

	If vert_info And CLIP_UV_SET1
		; Retrieve original triangle's set 1 UVs.
		good_tu# = VertexU(source_surf,good_v,1)
		good_tv# = VertexV(source_surf,good_v,1)
		bad1_tu# = VertexU(source_surf,bad1_v,1)
		bad1_tv# = VertexV(source_surf,bad1_v,1)
		bad2_tu# = VertexU(source_surf,bad2_v,1)
		bad2_tv# = VertexV(source_surf,bad2_v,1)
	
		; Calculate & set set 1 UVs for the 2 new verts.
		u# = good_tu + (bad1_tu-good_tu) * ratio1
		v# = good_tv + (bad1_tv-good_tv) * ratio1
		VertexTexCoords dest_surf,new1_v, u,v,0, 1
		u# = good_tu + (bad2_tu-good_tu) * ratio2
		v# = good_tv + (bad2_tv-good_tv) * ratio2
		VertexTexCoords dest_surf,new2_v, u,v,0, 1
	EndIf

	If vert_info And CLIP_RGBA
		; Retrieve original triangle's vert colors.
		good_r# = VertexRed(source_surf,good_v)
		good_g# = VertexGreen(source_surf,good_v)
		good_b# = VertexBlue(source_surf,good_v)
		bad1_r# = VertexRed(source_surf,bad1_v)
		bad1_g# = VertexGreen(source_surf,bad1_v)
		bad1_b# = VertexBlue(source_surf,bad1_v)
		bad2_r# = VertexRed(source_surf,bad2_v)
		bad2_g# = VertexGreen(source_surf,bad2_v)
		bad2_b# = VertexBlue(source_surf,bad2_v)
	
		; Retrieve original triangle's vert alpha.
		good_a# = VertexAlpha(source_surf,good_v)
		bad1_a# = VertexAlpha(source_surf,bad1_v)
		bad2_a# = VertexAlpha(source_surf,bad2_v)
	
		; Calculate & set color and alpha for the 2 new verts.
		r# = good_r + (bad1_r-good_r) * ratio1
		g# = good_g + (bad1_g-good_g) * ratio1
		b# = good_b + (bad1_b-good_b) * ratio1
		a# = good_a + (bad1_a-good_a) * ratio1
		VertexColor dest_surf,new1_v,r,g,b,a
		r# = good_r + (bad2_r-good_r) * ratio2
		g# = good_g + (bad2_g-good_g) * ratio2
		b# = good_b + (bad2_b-good_b) * ratio2
		a# = good_a + (bad2_a-good_a) * ratio2
		VertexColor dest_surf,new2_v,r,g,b,a
	EndIf

End Function


;
; Creates a clipped triangle from a source_surf triangle that has 1 'bad' vertex. i.e. 1 of the
; triangles verts is beyond the clipping plane.
; Note: this actually requires us to make a quad out of 2 tris.
;
; Params:
; source_surf - Surface containing the triangle to be clipped.
; dest_surf   - Surface to build the clipped triangle in.
; plane_y     - Y distance of clipping plane from mesh origin.
; bad_v       - Index of the one 'bad' vertex in triangle.
; good1_v     - Index of the first 'good' vertex in the triangle.
; good2_v     - Index of the second 'good' vertex in the triangle.
; wind_order  - Flag indicating the order to wind the clipped triangle(s).
; vert_info   - Bit flags defining vertex info to include when clipping.
;
Function clip_tri_1bad(source_surf,dest_surf,plane_y#, bad_v%,good1_v%,good2_v%, wind_order%, vert_info)

	; Retrieve good/bad vert coords.
	bad_vx#   = VertexX(source_surf,bad_v)
	bad_vy#   = VertexY(source_surf,bad_v)
	bad_vz#   = VertexZ(source_surf,bad_v)
	good1_vx# = VertexX(source_surf,good1_v)
	good1_vy# = VertexY(source_surf,good1_v)
	good1_vz# = VertexZ(source_surf,good1_v)
	good2_vx# = VertexX(source_surf,good2_v)
	good2_vy# = VertexY(source_surf,good2_v)
	good2_vz# = VertexZ(source_surf,good2_v)

	;
	; Calculate & add first new vert...
	;

	; Find Y distance from good vert 1 to clipping plane.
	clip_y_dist# = good1_vy-plane_y

	; Vector to bad vert from good vert 1.
	xv# = bad_vx-good1_vx
	yv# = bad_vy-good1_vy
	zv# = bad_vz-good1_vz
	
	y_dist# = good1_vy-bad_vy
	side# = 1.0/y_dist
	ratio1# = clip_y_dist*side
	new1_v = AddVertex(dest_surf,good1_vx+xv*ratio1,good1_vy+yv*ratio1,good1_vz+zv*ratio1)

	;
	; Calculate & add second new vert...
	;

	; Find Y distance from good vert 2 to clipping plane.
	clip_y_dist# = good2_vy-plane_y

	; Vector to bad vert from good vert 2.
	xv# = bad_vx-good2_vx
	yv# = bad_vy-good2_vy
	zv# = bad_vz-good2_vz
	
	y_dist# = good2_vy-bad_vy
	side# = 1.0/y_dist
	ratio2# = clip_y_dist*side
	new2_v = AddVertex(dest_surf,good2_vx+xv*ratio2,good2_vy+yv*ratio2,good2_vz+zv*ratio2)
	
	; Make the 2 clipped triangles, wound in the specified way.
	Select wind_order
	Case WIND_012
		AddTriangle(dest_surf,new1_v,good1_v,good2_v)
		AddTriangle(dest_surf,good2_v,new2_v,new1_v)
	Case WIND_102
		AddTriangle(dest_surf,good1_v,new1_v,good2_v)
		AddTriangle(dest_surf,good2_v,new1_v,new2_v)
	Case WIND_201
		AddTriangle(dest_surf,good1_v,good2_v,new1_v)
		AddTriangle(dest_surf,good2_v,new2_v,new1_v)
	End Select

	;
	; Update the requested settings for the 2 new verts...
	;
	
	If vert_info And CLIP_NORMALS
		; Retrieve original triangle's normals.
		bad_nx#   = VertexNX(source_surf,bad_v)
		bad_ny#   = VertexNY(source_surf,bad_v)
		bad_nz#   = VertexNZ(source_surf,bad_v)
		good1_nx# = VertexNX(source_surf,good1_v)
		good1_ny# = VertexNY(source_surf,good1_v)
		good1_nz# = VertexNZ(source_surf,good1_v)
		good2_nx# = VertexNX(source_surf,good2_v)
		good2_ny# = VertexNY(source_surf,good2_v)
		good2_nz# = VertexNZ(source_surf,good2_v)
	
		; Calculate & set normals for the 2 new verts.
		nx# = good1_nx + (bad_nx-good1_nx) * ratio1
		ny# = good1_ny + (bad_ny-good1_ny) * ratio1
		nz# = good1_nz + (bad_nz-good1_nz) * ratio1
		nl# = Sqr(nx*nx + ny*ny + nz*nz)
		VertexNormal dest_surf,new1_v,nx/nl,ny/nl,nz/nl
		nx# = good2_nx + (bad_nx-good2_nx) * ratio2
		ny# = good2_ny + (bad_ny-good2_ny) * ratio2
		nz# = good2_nz + (bad_nz-good2_nz) * ratio2
		nl# = Sqr(nx*nx + ny*ny + nz*nz)
		VertexNormal dest_surf,new2_v,nx/nl,ny/nl,nz/nl
	EndIf

	If vert_info And CLIP_UV_SET0
		; Retrieve original triangle's set 0 UVs.
		bad_tu#   = VertexU(source_surf,bad_v,0)
		bad_tv#   = VertexV(source_surf,bad_v,0)
		good1_tu# = VertexU(source_surf,good1_v,0)
		good1_tv# = VertexV(source_surf,good1_v,0)
		good2_tu# = VertexU(source_surf,good2_v,0)
		good2_tv# = VertexV(source_surf,good2_v,0)
	
		; Calculate & set set 0 UVs for the 2 new verts.
		u# = good1_tu + (bad_tu-good1_tu) * ratio1
		v# = good1_tv + (bad_tv-good1_tv) * ratio1
		VertexTexCoords dest_surf,new1_v, u,v,0, 0
		u# = good2_tu + (bad_tu-good2_tu) * ratio2
		v# = good2_tv + (bad_tv-good2_tv) * ratio2
		VertexTexCoords dest_surf,new2_v, u,v,0, 0
	EndIf

	If vert_info And CLIP_UV_SET1
		; Retrieve original triangle's set 1 UVs.
		bad_tu#   = VertexU(source_surf,bad_v,1)
		bad_tv#   = VertexV(source_surf,bad_v,1)
		good1_tu# = VertexU(source_surf,good1_v,1)
		good1_tv# = VertexV(source_surf,good1_v,1)
		good2_tu# = VertexU(source_surf,good2_v,1)
		good2_tv# = VertexV(source_surf,good2_v,1)
	
		; Calculate & set set 1 UVs for the 2 new verts.
		u# = good1_tu + (bad_tu-good1_tu) * ratio1
		v# = good1_tv + (bad_tv-good1_tv) * ratio1
		VertexTexCoords dest_surf,new1_v, u,v,0, 1
		u# = good2_tu + (bad_tu-good2_tu) * ratio2
		v# = good2_tv + (bad_tv-good2_tv) * ratio2
		VertexTexCoords dest_surf,new2_v, u,v,0, 1
	EndIf

	If vert_info And CLIP_RGBA
		; Retrieve original triangle's vert colors.
		bad_r#   = VertexRed(source_surf,bad_v)
		bad_g#   = VertexGreen(source_surf,bad_v)
		bad_b#   = VertexBlue(source_surf,bad_v)
		good1_r# = VertexRed(source_surf,good1_v)
		good1_g# = VertexGreen(source_surf,good1_v)
		good1_b# = VertexBlue(source_surf,good1_v)
		good2_r# = VertexRed(source_surf,good2_v)
		good2_g# = VertexGreen(source_surf,good2_v)
		good2_b# = VertexBlue(source_surf,good2_v)
	
		; Retrieve original triangle's vert alpha.
		bad_a# = VertexAlpha(source_surf,bad_v)
		good1_a# = VertexAlpha(source_surf,good1_v)
		good2_a# = VertexAlpha(source_surf,good2_v)
	
		; Calculate & set color and alpha for the 2 new verts.
		r# = good1_r + (bad_r-good1_r) * ratio1
		g# = good1_g + (bad_g-good1_g) * ratio1
		b# = good1_b + (bad_b-good1_b) * ratio1
		a# = good1_a + (bad_a-good1_a) * ratio1
		VertexColor dest_surf,new1_v,r,g,b,a
		r# = good2_r + (bad_r-good2_r) * ratio2
		g# = good2_g + (bad_g-good2_g) * ratio2
		b# = good2_b + (bad_b-good2_b) * ratio2
		a# = good2_a + (bad_a-good2_a) * ratio2
		VertexColor dest_surf,new2_v,r,g,b,a
	EndIf

End Function


;
; Creates various meshes to demonstrate clipping on.
;
Function create_meshes()

	Dim mesh.infoT(4)
	
	; Textured sphere.
	mesh(0) = New infoT
	mesh(0)\mesh = CreateSphere(16)
	mesh(0)\FX = 16
	mesh(0)\vert_info = CLIP_NORMALS+CLIP_UV_SET0
	ScaleMesh mesh(0)\mesh,1.2,1.2,1.2
	EntityTexture mesh(0)\mesh,tex
	HideEntity mesh(0)\mesh
		
	; Vertex colored cone.
	mesh(1) = New infoT
	mesh(1)\mesh = CreateCone(32,1)
	mesh(1)\FX = 2+16
	mesh(1)\vert_info = CLIP_NORMALS+CLIP_RGBA
	UpdateNormals mesh(1)\mesh
	For s=1 To 2
		surf = GetSurface(mesh(1)\mesh,s)
		For v=0 To CountVertices(surf)-1
			VertexColor surf,v,Rand(0,255),Rand(0,255),Rand(0,255)
		Next
	Next
	HideEntity mesh(1)\mesh
	
	; Plain cylinder.
	mesh(2) = New infoT
	mesh(2)\mesh = CreateCylinder(32,1)
	mesh(2)\FX = 16
	mesh(2)\vert_info = CLIP_NORMALS
	HideEntity mesh(2)\mesh
	
	; Textured, vertex colored cube.
	mesh(3) = New infoT
	mesh(3)\mesh = CreateCube()
	mesh(3)\FX = 2+16
	mesh(3)\vert_info = CLIP_NORMALS+CLIP_UV_SET0+CLIP_RGBA
	surf = GetSurface(mesh(3)\mesh,1)
	For v=0 To CountVertices(surf)-1
		VertexColor surf,v,Rand(0,255),Rand(0,255),Rand(0,255)
	Next
	EntityTexture mesh(3)\mesh,tex
	HideEntity mesh(3)\mesh

	; Textured bars.
	bar = CreateCylinder(16,1)
	ScaleMesh bar,.2,1.5,.2
	mesh(4) = New infoT
	mesh(4)\mesh = CreateMesh()
	AddMesh bar,mesh(4)\mesh
	PositionMesh bar,.5,0,0
	RotateMesh bar,20,0,0
	AddMesh bar,mesh(4)\mesh
	PositionMesh bar,-1,0,0
	RotateMesh bar,20,0,0
	AddMesh bar,mesh(4)\mesh
	mesh(4)\FX = 16
	mesh(4)\vert_info = CLIP_NORMALS+CLIP_UV_SET0
	HideEntity mesh(4)\mesh
	FreeEntity bar
	EntityTexture mesh(4)\mesh,tex
	EntityColor mesh(4)\mesh,200,0,150
	EntityShininess mesh(4)\mesh,1

End Function


;
; Displays info.
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
		
		Color 255,255,255
		Text 10,10, "Press to toggle:"
		Text 10,25, "1 - This text"
		Text 10,40, "2 - Clipping plane"
		Text 10,55, "3 - Clipping plane rotation"
		Text 10,70, "4 - Mesh rotation"
		Text 10,85, "5 - Backface culling"
		Text 10,100,"6 - Wireframe"
		Text 10,115,"7 - Slow motion"
		Text 10,130,"8 - Frame lock"
		Text 10,160,"Press SPACE to change mesh"
		Color 255,255,0
		Text 10,190,"Millisecs: " + frame_time
		Text 10,205,"  Slowest: " + slowest_frame
		Color 0,255,255
		Text 10,220,"      FPS: " + fps
		Text 10,235,"    Worst: " + slowest_fps
	Else
		; First call initialization.
		fps_timeout = MilliSecs() + 1000 
	EndIf
	
End Function
