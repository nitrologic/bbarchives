; ID: 571
; Author: Neo Genesis10
; Date: 2003-02-03 16:45:30
; Title: LoadMeshTerrain( file$, [parent] )
; Description: Turns a bitmap into a mesh terrain.

Dim vertex( 512, 512 )
Global IMdivider# = 10

Function MeshPlane(width, height, dist#=1, parent=0)
	width = width - 1
	height = height - 1

	If width > 127 Then width = 127
	If height > 127 Then height = 127

	mesh = CreateMesh()
	surface = CreateSurface(mesh)

	For x = 0 To width + 1
		For z = 0 To height + 1
			IMx# = Float#(x * dist#)
			IMz# = Float#(z * dist#)
			vertex(x,z) = AddVertex(surface,IMx#,0,IMz#, x, z)
		Next
	Next

	For x = 0 To width
		For z = 0 To height		
			AddTriangle(surface, vertex(x,z), vertex(x,z+1), vertex(x+1,z+1) )
			AddTriangle(surface, vertex(x+1,z+1), vertex(x+1,z), vertex(x,z) )
		Next
	Next
	
	EntityParent mesh, parent
	Return mesh
	
End Function

Function LoadMeshTerrain( filename$, parent=0 )

	img = LoadImage(filename$)
	If img = 0 RuntimeError "Mesh does not exist: " + filename$
	
	size = ImageWidth( img )
	If ImageHeight( img ) <> wth Then ResizeImage img, size, size
	
	Local returner
	
	buff = GraphicsBuffer()
	SetBuffer ImageBuffer( img )
	
	Lr = ColorRed()
	Lg = ColorGreen()
	Lb = ColorBlue()
	
	Local Ix, Iy
	
	If size >= 128
		For I = 1 To size Step 128
			For J = 1 To size Step 128
				mesh = MeshPlane( 128, 128 )

				If I = 1 And J = 1
					returner = mesh
					EntityParent mesh, parent
				Else
					EntityParent mesh, returner
				EndIf
				
				Origin I-1, J-1
				
				For x = 0 To 128
					For y = 0 To 128
					
						GetColor x, y
						r = ColorRed()
					
						surface = GetSurface( mesh, 1 )
						Vx# = VertexX#( surface, vertex(x,y) )
						Ny# = Float(r / IMdivider#)
						Vz# = VertexZ#( surface, vertex(x,y) )
										
						VertexCoords surface, vertex(x,y), Vx#, Ny#, Vz#
					Next
				Next
								
				PositionEntity mesh, I , 0, J, True
			Next
		Next
	Else
		mesh = MeshPlane( size, size )
		returner = mesh
		
		For x = 0 To (size - 1)
			For y = 0 To (size - 1)
			
				GetColor x, y
				r = ColorRed()
			
				surface = GetSurface( mesh, 1 )
				Vx# = VertexX#( surface, vertex(x,y) )
				Ny# = Float(r / IMdivider#)
				Vz# = VertexZ#( surface, vertex(x,y) )
								
				VertexCoords surface, vertex(x,y), Vx#, Ny#, Vz#
			Next
		Next
	EndIf
	
	; Behave yourself nicely :)
	Color r, g, b
	Origin 0, 0
	SetBuffer buff
	EntityParent returner, parent
	Return returner
	
End Function
