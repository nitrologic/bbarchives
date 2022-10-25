; ID: 1085
; Author: big10p
; Date: 2004-06-14 17:25:38
; Title: Show Normals
; Description: Simple debug library to visually check normals by drawing them.

;
; "show normals" debug library by big10p (A.K.A. Chris Chadwick) 2004
;
; Written with Blitz3D v1.86
;
; Description:
; This is a simple include library that enables mesh vertex normals to be drawn,
; allowing the user to visually verify that a given mesh's normals 'look right'.
; This will probably be of most use when creating/deforming meshes 'manually'
; in-code, as it allows the user to confirm he is calculating the vertex normals
; correctly.
;
; Known issues:
; 1) Does not work properly with animated meshes.
;
; Usage:
; 1) Place a copy of LIB_show_normals.bb in the same directory as your project.
; 2) Add the following line to your code, somewhere after the Graphics3D call:
; 	   Include "LIB_show_normals.bb"
; 3) Add the following line to your code, somewhere after the RenderWorld call:
;      render_normals(camera)
; 4) For every mesh you want normals drawn for, add the following line:
;      show_normals(mesh)
; 5) To turn off normal rendering for a given mesh, add the following line:
;      hide_normals(mesh)
;
; Note: If you FreeEntity a mesh whose normals are being shown, you MUST also
;       hide_normals() it before the next call to render_normals().
;
; See text at the start of each function for a full description, params required etc.
;

	; Type for registering meshes for normal rendering.
	Type snT
		Field mesh			; Mesh to show normals of.
		Field R#, G#, B#	; Color to render normals in.
	End Type

	; Maximum number of lines/triangles/normals to place in each surface.
	Const SN_LINES_PER_SURF% = 10000

	; Out-of-world position to render normal mesh at.
	Const SN_MESH_X# = 0.0
	Const SN_MESH_Y# = 10000.0
	Const SN_MESH_Z# = 0.0

	; Length to render normals at (in blitz units).
	Const SN_NORM_LENGTH# = 0.3

	; Default normal color (used if RGB isn't specified when calling show_normals()).
	Const SN_DEFAULT_R# = 0	
	Const SN_DEFAULT_G# = 0	
	Const SN_DEFAULT_B# = 255	

	; A single mesh to hold all normals.
	Global sn_norm_mesh

	; Tempory storage for camera's children.
	Dim cam_sprogs(1)

	
;
; Main function to handle the rendering of normals for all meshes that
; have had show_normals() run on them.
;
; Params:
; cam         - Camera to render normals on.
; wiref       - State to reset WireFrame to after rendering normal mesh.
; cls_color   - State to reset CameraClsMode's cls_color param to
;               after rendering the normal mesh.
; cls_zbuffer - State to reset CameraClsMode's cls_zbuffer param to
;               after rendering the normal mesh.
;
Function render_normals(cam, wiref%=False, cls_color%=True, cls_zbuffer%=True)

	; Temporarily dis-own all camera's children so we can
	; move it without dragging them along! :)
	children% = CountChildren(cam)
	If children
		Dim cam_sprogs(children)
		For n = 1 To children
			cam_sprogs(n) = GetChild(cam,n)
		Next
		For n = 1 To children
			EntityParent cam_sprogs(n),0,True
		Next
	EndIf

	build_normal_mesh()

	WireFrame 1
	CameraClsMode cam,0,0

	; Reposition camera relative to the out-of-world normal mesh.
	cam_x# = EntityX(cam,1)
	cam_y# = EntityY(cam,1)
	cam_z# = EntityZ(cam,1)
	PositionEntity cam,cam_x+SN_MESH_X,cam_y+SN_MESH_Y,cam_z+SN_MESH_Z,True

	ShowEntity sn_norm_mesh	
	RenderWorld
	HideEntity sn_norm_mesh
	
	; Reset everything back to how it was.
	WireFrame wiref
	CameraClsMode cam,cls_color,cls_zbuffer
	PositionEntity cam,cam_x,cam_y,cam_z,True

	; Pick-up the kids from the pool. :P
	If children
		For n = 1 To children
			EntityParent cam_sprogs(n),cam,True
		Next
	EndIf

End Function


;
; Builds the normal mesh from scratch.
;
Function build_normal_mesh()

	If sn_norm_mesh Then FreeEntity sn_norm_mesh

	sn_norm_mesh = CreateMesh()
	CreateSurface(sn_norm_mesh)
	EntityFX sn_norm_mesh,1+2+16
	PositionEntity sn_norm_mesh,SN_MESH_X,SN_MESH_Y,SN_MESH_Z,1
	HideEntity sn_norm_mesh
	
	For sn.snT = Each snT
		add_mesh_normals(sn\mesh,sn\R,sn\G,sn\B)
	Next

End Function


;
; Adds the specified mesh's normals to the normal mesh.
;
; Params:
; mesh  - Mesh from which normals will be added to the normal mesh.
; R,G,B - Color of normals to be added.
;
Function add_mesh_normals(mesh, R#, G#, B#)
	
	For sno = 1 To CountSurfaces(mesh)
		
		surf = GetSurface(mesh,sno)

		For vno = 0 To CountVertices(surf)-1

			TFormNormal VertexNX(surf,vno),VertexNY(surf,vno),VertexNZ(surf,vno),mesh,0
			nx# = TFormedX() * SN_NORM_LENGTH
			ny# = TFormedY() * SN_NORM_LENGTH
			nz# = TFormedZ() * SN_NORM_LENGTH
			
			TFormPoint VertexX(surf,vno),VertexY(surf,vno),VertexZ(surf,vno),mesh,0
			x0# = TFormedX()
			y0# = TFormedY()
			z0# = TFormedZ()
			x1# = x0 + nx
			y1# = y0 + ny
			z1# = z0 + nz
			
			create_3D_line(x0,y0,z0, x1,y1,z1, R,G,B)
			
		Next
		
	Next

End Function


;
; Registers a mesh to have it's normals rendered.
;
; Params:
; mesh  - Mesh to have it's normals rendered.
; R,G,B - Color of mesh's normals.
;
Function show_normals(mesh, R#=SN_DEFAULT_R, G#=SN_DEFAULT_G, B#=SN_DEFAULT_B)

	sn.snT = New snT
	sn\mesh = mesh
	sn\R = R
	sn\G = G
	sn\B = B
				
End Function


;
; Un-registers a mesh to stop it's normals being rendered.
;
; Params:
; mesh - mesh to un-register, preventing it's normals from being rendered.
;
Function hide_normals(mesh)

	For sn.snT = Each snT
		If sn\mesh = mesh
			Delete sn
			Return
		End If
	Next
	
End Function


;
; Adds a 3D line to the normal mesh. Based on original function by koekjesbaby ;)
; Note: 3D lines are only properly visible when rendered in wireframe mode!
; 
; Params:
; x0,y0,z0 - Start point of line.
; x1,y2,z1 - End point of line.
; r,g,b    - Line colour.
;
Function create_3D_line(x0#,y0#,z0#, x1#,y1#,z1#, r#,g#,b#) 

	last_surf = CountSurfaces(sn_norm_mesh)
	surf = GetSurface(sn_norm_mesh,last_surf)
	If CountTriangles(surf) = SN_LINES_PER_SURF Then surf = CreateSurface(sn_norm_mesh)

	v0 = AddVertex(surf,x0,y0,z0) 
	v1 = AddVertex(surf,x1,y1,z1)  
	v2 = AddVertex(surf,x0,y0,z0)  
	AddTriangle surf,v0,v1,v2
	
	VertexColor surf,v0,r,g,b
	VertexColor surf,v1,r,g,b
	VertexColor surf,v2,r,g,b

End Function
