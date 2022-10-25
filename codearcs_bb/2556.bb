; ID: 2556
; Author: Zethrax
; Date: 2009-08-05 18:52:05
; Title: CenterMesh
; Description: Centers an off-center mesh. Provides position values which allow the mesh position to be restored.

; These globals are used to return x, y, and z float values from functions.
; They must be accessed immediately after any function that uses them is called
; as they will be modified by other functions which use them.
Global G_return_x#, G_return_y#, G_return_z#


Function CenterMesh( mesh )
; Centers the vertex data of the specified mesh to be at the world center.

; RETURNS:-
; The function returns the total number of vertices found in the mesh.
; If a zero is returned, then no vertices were found and the globals G_return_x#, G_return_y#, G_return_z#
; do not contain valid data, and should not be used for any purpose.

; The original offset position of the mesh in the globals G_return_x#, G_return_y#, G_return_z#.

; NOTES:-
; Requires the G_return_x#, G_return_y#, G_return_z# globals.

Local max_surface, max_vertex, surface, vertex, vx#, vy#, vz#, vertex_count
Local px#, py#, pz#, nx#, ny#, nz#

; Count the number of surfaces.
max_surface = CountSurfaces ( mesh )

If max_surface ; If the mesh has surface data...

	; Loop through all the surface indexes.
	For surface_index = 1 To max_surface
	
		; Get the surface for the current surface index.
		surface = GetSurface ( mesh, surface_index )
		
		; Count the number of vertices.
		max_vertex = CountVertices ( surface )
		
		; Increment the count of all vertices found, so that it can be returned by the function.
		vertex_count = vertex_count + max_vertex
		
		If max_vertex ; If the surface has vertex data...
		
			; Vertex indexes start at zero, so adjust max_vertex to suit.
			max_vertex = max_vertex - 1	
		
			; Seed min/max vertex variables.
			px# = VertexX# ( surface, 0 ) : nx# = px#
			py# = VertexY# ( surface, 0 ) : ny# = py#
			pz# = VertexZ# ( surface, 0 ) : nz# = pz#
		
			; Loop through all the vertex indexes.
			For vertex_index = 0 To max_vertex
			
				; Get the x, y, z position of the vertex.
				vx# = VertexX# ( surface, vertex_index )
				vy# = VertexY# ( surface, vertex_index )
				vz# = VertexZ# ( surface, vertex_index )
				
				; Find the highest x, y, z vertex position values.
				If vx# > px# Then px# = vx#
				If vy# > py# Then py# = vy#
				If vz# > pz# Then pz# = vz#
				
				; Find the lowest x, y, z vertex position values.
				If vx# < nx# Then nx# = vx#
				If vy# < ny# Then ny# = vy#
				If vz# < nz# Then nz# = vz#
			
			Next
			
		EndIf
	
	Next
	
	; Find the mid-point between the highest and lowest x, y, z vertex position values.
	G_return_x# = ( px# + nx# ) / 2.0
	G_return_y# = ( py# + ny# ) / 2.0
	G_return_z# = ( pz# + nz# ) / 2.0
	
	; Position the mesh data back to the world center.
	PositionMesh mesh, -G_return_x#, -G_return_y#, -G_return_z#
	
EndIf

; Return the total number of vertices found. If a zero is returned,
; then no vertices were found and the globals G_return_x#, G_return_y#, G_return_z#
; do not contain valid data, and should not be used for any purpose.
Return vertex_count

End Function


; *** EXAMPLE ***


Graphics3D 800, 600, 0, 2
SetBuffer BackBuffer()

TurnEntity CreateLight(), 45.0, 0.0, 0.0

Global G_cam = CreateCamera()
CameraZoom G_cam, 1.6
MoveEntity G_cam, 0.0, 0.0, -20.0

; Create a box to mark the world center.
Global G_world_center_marker = CreateCube()
ScaleEntity G_world_center_marker, 0.25, 0.25, 0.25
EntityColor G_world_center_marker, 255, 0, 0

Global G_test_mesh = CreateCube()
EntityAlpha G_test_mesh, 0.5
PositionMesh G_test_mesh, 10.0, 10.0, 10.0

; Create a box to mark the center of the test mesh.
Global G_testmesh_center_marker = CreateCube()
PositionEntity G_testmesh_center_marker, 10.0, 10.0, 10.0
ScaleEntity G_testmesh_center_marker, 0.25, 0.25, 0.25
EntityColor G_testmesh_center_marker, 0, 255, 0


UpdateWorld : RenderWorld : Flip

Print "Press any key to center the mesh."

WaitKey

If CenterMesh( G_test_mesh ) = 0 Then Print "ERROR: No vertex data found." : WaitKey : End

UpdateWorld : RenderWorld : Flip

Print "Press any key to move the mesh back to its original position."

WaitKey

; Position the G_test_mesh entity back where it was.
PositionEntity G_test_mesh, G_return_x#, G_return_y#, G_return_z#

UpdateWorld : RenderWorld : Flip

Print "Press any key to continue."

WaitKey

While Not KeyDown( 1 )

If KeyDown( 205 )=True Then TurnEntity G_cam,0,-1,0
If KeyDown( 203 )=True Then TurnEntity G_cam,0,1,0
If KeyDown( 208 )=True Then MoveEntity G_cam,0,0,-0.05
If KeyDown( 200 )=True Then MoveEntity G_cam,0,0,0.05

; Use camera project to get 2D coordinates from 3D coordinates of cube
CameraProject(G_cam,EntityX(G_test_mesh),EntityY(G_test_mesh),EntityZ(G_test_mesh))

RenderWorld

; If cube is in view then draw text, if not then draw nothing otherwise text will be drawn at 0,0
If EntityInView(G_test_mesh,G_cam)=True

; Use ProjectedX() and ProjectedY() to get 2D coordinates from when CameraProject was used.
; Use these coordinates to draw text at a 2D position, on top of a 3D scene.
Text ProjectedX#(),ProjectedY#(),"Test Mesh"

EndIf

Text 0,0,"Use cursor keys to move about"
Text 0,20,"ProjectedX: "+ProjectedX#()
Text 0,40,"ProjectedY: "+ProjectedY#()
Text 0,60,"ProjectedZ: "+ProjectedZ#()
Text 0,80,"EntityInView: "+EntityInView(G_test_mesh,G_cam)
Text 0,100,"Tris Rendered: "+TrisRendered()

Flip

Wend

End
