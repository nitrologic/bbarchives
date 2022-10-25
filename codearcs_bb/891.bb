; ID: 891
; Author: EdzUp[GD]
; Date: 2004-01-17 12:41:54
; Title: RenderWireFrame
; Description: Render a wireframe representation of any 3d mesh

;RenderWireFrame by EdzUp

;Safe wireframe rendering of a mesh, useful for hud effects.
;NOTE: this rendering isnt depth specific so you will be able to see the render through ALL
;3d objects (basically it draws the representation to the screen.

;If you find this useful please give credit where its due ;)

Graphics3D 640,480,16
SetBuffer BackBuffer()

Global Camera = CreateCamera()

AmbientLight 255, 255, 255

Global Mesh = CreateSphere( 8 )
AmbientLight 255, 255, 255

MoveEntity camera, 0, 0, -5

While Not KeyDown(1)
	TurnEntity Mesh, 0, 1, 0
	UpdateWorld
	RenderWorld
	Color 255, 255, 255
	Color 255, 0, 0
	RenderWireFrame( Mesh )
	Flip
Wend
End

Function RenderWireFrame( MeshEntity )
	
	Local SC = CountSurfaces( MeshEntity )
	Local Vert1=0, C1X#, C1Y#, C1Z#
	Local Vert2=0, C2X#, C2Y#, C2Z#
	Local Vert3=0, C3X#, C3Y#, C3Z#

	For CS = 1 To SC
		GS = GetSurface( MeshEntity, CS )
		For CT =0 To CountTriangles( GS )-1
			Vert1 = TriangleVertex( GS, CT, 0 )
			Vert2 = TriangleVertex( GS, CT, 1 )
			Vert3 = TriangleVertex( GS, CT, 2 )

			TFormPoint VertexX#( GS, Vert1 ), VertexY#( GS, Vert1 ), VertexZ#( GS, Vert1 ), MeshEntity, 0
			CameraProject Camera, TFormedX#(), TFormedY#(), TFormedZ#()
			C1X# = ProjectedX#()
			C1Y# = ProjectedY#()
			C1Z# = ProjectedZ#()
			TFormPoint VertexX#( GS, Vert2 ), VertexY#( GS, Vert2 ), VertexZ#( GS, Vert2 ), MeshEntity, 0
			CameraProject Camera, TFormedX#(), TFormedY#(), TFormedZ#()
			C2X# = ProjectedX#()
			C2Y# = ProjectedY#()
			C2Z# = ProjectedZ#()
			TFormPoint VertexX#( GS, Vert3 ), VertexY#( GS, Vert3 ), VertexZ#( GS, Vert3 ), MeshEntity, 0
			CameraProject Camera, TFormedX#(), TFormedY#(), TFormedZ#()
			C3X# = ProjectedX#()
			C3Y# = ProjectedY#()
			C3Z# = ProjectedZ#()
			
			Line C1X#, C1Y#, C2X#, C2Y#
			Line C2X#, C2Y#, C3X#, C3Y#
			Line C3X#, C3Y#, C1X#, C1Y#
		Next
	Next
End Function
