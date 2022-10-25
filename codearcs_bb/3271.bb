; ID: 3271
; Author: TomToad
; Date: 2016-05-22 10:19:33
; Title: Export stl
; Description: Export an entity as an stl

;SaveStl() by TomToad
;
;Usage
; SaveStl(Filename$,Entity,Children)
;
;	Filename$ - name of the file to be saved
;	Entity - the entity to be saved
;	Children - True, all children will be saved in the file; False - Only the referenced entity will be saved

;v1.1 05/22/16 z axis needed to be flipped
;v1.0 05/22/16 original version
Global SaveStlTrisCount = 0 ;This holds the total number of triangles saved

;Type to hold a 3d vector
Type Vector3D
	Field X#
	Field Y#
	Field z#
End Type


;This function saves the actual triangles.  Your program will not call this function.  It is
;	called by SaveStl() and recursively calls itself for each child entity
Function SaveStlTris(Stream,Entity,Children)
	; if saving children, then check if the entity has any children.  If no children exist, then
	; recursively call this function with children set to false, otherwise call this function
	; for each child entity
	If Children = True Then
		If CountChildren(Entity) > 0 Then
			For i = 1 To CountChildren(Entity)
				SaveStlTris(Stream,GetChild(Entity,i),True)
			Next
		End If
		SaveStlTris(Stream,Entity,False)
	Else
		;Now to save the actual entity.
		For SurfaceIndex = 1 To CountSurfaces(Entity) ;Go through each surface
			Surface = GetSurface(Entity,SurfaceIndex)
			SaveStlTrisCount = SaveStlTrisCount + CountTriangles(Surface) ;Keep track of number of triangles
			For TriangleIndex = 0 To CountTriangles(Surface)-1 ;go through each triangle on the surface
				v0 = TriangleVertex(Surface,TriangleIndex,0) ;get the vertices of the triangle
				v1 = TriangleVertex(Surface,TriangleIndex,2) ; vertex 1 and 2 are swapped as stl uses a 
				v2 = TriangleVertex(Surface,TriangleIndex,1) ; counter-clockwise ordering
				
				;stl doesn't use scale or rotation, so all the vertices must be transformed to
				; world coordinates
				t0.Vector3D = New Vector3d
				TFormPoint(VertexX(surface,v0),VertexY(surface,v0),VertexZ(surface,V0),Entity,0)
				t0\x = TFormedX()
				t0\y = TFormedY()
				t0\z = -TFormedZ()

				t1.Vector3D = New Vector3d
				TFormPoint(VertexX(surface,v1),VertexY(surface,v1),VertexZ(surface,V1),Entity,0)
				t1\x = TFormedX()
				t1\y = TFormedY()
				t1\z = -TFormedZ()
				
				t2.Vector3D = New Vector3d
				TFormPoint(VertexX(surface,v2),VertexY(surface,v2),VertexZ(surface,V2),Entity,0)
				t2\x = TFormedX()
				t2\y = TFormedY()
				t2\z = -TFormedZ()
				
				;Now to create the surface normal so that the stl file knows which way is out
				U.Vector3D = New Vector3D
				V.Vector3D = New Vector3D
				
				U\x = t1\x-t0\x
				U\y = t1\y-t0\y
				U\z = t1\z-t0\z
				
				V\x = t2\x-t0\x
				V\y = t2\y-t0\y
				V\z = t2\z-t0\z
				
				Normal.Vector3D = New Vector3D
				Normal\x = U\y*V\z-U\z*V\y
				Normal\y = U\z*V\x-U\x*V\z
				Normal\z = U\x*V\y-U\y*V\x
				
				;write the normal to the file
				WriteFloat(Stream,Normal\x)
				WriteFloat(Stream,Normal\y)
				WriteFloat(Stream,Normal\z)
				
				;write the triangle to the file
				WriteFloat(Stream,t0\x)
				WriteFloat(Stream,t0\y)
				WriteFloat(Stream,t0\z)
				
				WriteFloat(Stream,t1\x)
				WriteFloat(Stream,t1\y)
				WriteFloat(Stream,t1\z)
				
				WriteFloat(Stream,t2\x)
				WriteFloat(Stream,t2\y)
				WriteFloat(Stream,t2\z)
				
				;free the types
				Delete Normal
				Delete U
				Delete V
				Delete t0
				Delete t1
				Delete t2
				
				;attribute count.  set to 0
				WriteShort(Stream,0)
			Next
		Next
	End If
End Function

;Your program will call this function
;Filename: Name of the file to be saved
;Entity: Parent entity to be saved
;Children: True to aslo save child entities, false to only save parent

Function SaveStl(Filename$,Entity,Children)
	SaveStlTrisCount = 0 ;reset the triangle count to 0
	Stream = WriteFile(Filename)
	For i = 1 To 21 ;80 byte header + triangle count
		WriteInt(Stream,0)
	Next
	
	SaveStlTris(Stream,Entity,Children) ;save the triangles
	current = FilePos(Stream) ;save the current stream position
	SeekFile(Stream,80) ;move to the triangle count positon
	WriteInt(Stream,SaveStlTrisCount) ;write the number of triangles saved
	CloseFile Stream
End Function

;--------------------------------------------
;
;  The code below is a sample of using the
;     Function SaveStl()
;
;---------------------------------------------

Graphics3D 800,600

cube = CreateCube() ;create a cube
sphere = CreateSphere(8,Cube) ;create a sphere, make cube its parent
ScaleEntity sphere,2,2,2 ;scale and move the sphere
PositionEntity sphere,5,0,0

SaveStl("cube.stl",Cube,False) ;save the cube, but not its children

SaveStl("sphere.stl",sphere,False) ;save the sphere

SaveStl("all.stl",Cube,True) ;save the cube and all its children


camera = CreateCamera()
PositionEntity camera,0,0,-10

light = CreateLight()
RotateEntity light,45,45,45

While Not KeyHit(1)
	Cls
	
	UpdateWorld
	RenderWorld
	Flip
	If KeyDown(17) ;w
		MoveEntity camera,0,0,.2
	End If
	If KeyDown(31) ;s
		MoveEntity camera,0,0,-.2
	End If
	If KeyDown(30) ;a
		TurnEntity camera,0,-1,0
	End If
	If KeyDown(32) ;d
		TurnEntity camera,0,1,0
	End If
Wend
