; ID: 3272
; Author: TomToad
; Date: 2016-05-22 15:03:16
; Title: Stl Export for OpenB3D
; Description: Exports entities as Stl

'SaveStl() by TomToad
'
'Usage
' SaveStl(Filename$,Entity,Children)
'
'	Filename$ - name of the file To be saved
'	Entity - the entity To be saved
'	Children - True, all children will be saved in the file; False - Only the referenced entity will be saved

'v1.2 05/22/16 BMax - OpenB3D version created
'v1.1 05/22/16 z axis needed To be flipped
'v1.0 05/22/16 original version
SuperStrict
Import OpenB3d.OpenB3d
Import OpenB3d.B3dglGraphics

Global SaveStlTrisCount:Long = 0 'This holds the total number of triangles saved

'Type To hold a 3d vector
Type Vector3D
	Field X:Float
	Field Y:Float
	Field z:Float
End Type


'This Function saves the actual triangles.  Your program will Not call this Function.  It is
'	called by SaveStl() And recursively calls itself For each child entity
Function SaveStlTris(Stream:TStream,Entity:TEntity,Children:Int)
	' If saving children, Then check If the entity has any children.  If no children exist, Then
	' recursively call this Function with children set To False, otherwise call this Function
	' For each child entity
	If Children = True Then
		If CountChildren(Entity) > 0 Then
			For Local i:Int = 1 To CountChildren(Entity)
				SaveStlTris(Stream,GetChild(Entity,i),True)
			Next
		End If
		SaveStlTris(Stream,Entity,False)
	Else
		'Now To save the actual entity.
		For Local SurfaceIndex:Int = 1 To CountSurfaces(TMesh(Entity)) 'Go through each surface
			Local Surface:TSurface = GetSurface(TMesh(Entity),SurfaceIndex)
			SaveStlTrisCount = SaveStlTrisCount + CountTriangles(Surface) 'Keep track of number of triangles
			For Local TriangleIndex:Int = 0 Until CountTriangles(Surface) 'go through each triangle on the surface
				Local v0:Int = TriangleVertex(Surface,TriangleIndex,0) 'get the vertices of the triangle
				Local v1:Int = TriangleVertex(Surface,TriangleIndex,2) ' vertex 1 And 2 are swapped as stl uses a 
				Local v2:Int = TriangleVertex(Surface,TriangleIndex,1) ' counter-clockwise ordering
				
				'stl doesn't use scale or rotation, so all the vertices must be transformed to
				' world coordinates
				Local t0:Vector3D = New Vector3d
				TFormPoint(VertexX(surface,v0),VertexY(surface,v0),VertexZ(surface,V0),Entity,Null)
				t0.x = TFormedX()
				t0.y = TFormedY()
				t0.z = -TFormedZ()

				Local t1:Vector3D = New Vector3d
				TFormPoint(VertexX(surface,v1),VertexY(surface,v1),VertexZ(surface,V1),Entity,Null)
				t1.x = TFormedX()
				t1.y = TFormedY()
				t1.z = -TFormedZ()
				
				Local t2:Vector3D = New Vector3d
				TFormPoint(VertexX(surface,v2),VertexY(surface,v2),VertexZ(surface,V2),Entity,Null)
				t2.x = TFormedX()
				t2.y = TFormedY()
				t2.z = -TFormedZ()
				
				'Now To Create the surface normal so that the stl file knows which way is out
				Local U:Vector3D = New Vector3D
				Local V:Vector3D = New Vector3D
				
				U.x = t1.x-t0.x
				U.y = t1.y-t0.y
				U.z = t1.z-t0.z
				
				V.x = t2.x-t0.x
				V.y = t2.y-t0.y
				V.z = t2.z-t0.z
				
				Local Normal:Vector3D = New Vector3D
				Normal.x = U.y*V.z-U.z*V.y
				Normal.y = U.z*V.x-U.x*V.z
				Normal.z = U.x*V.y-U.y*V.x
				
				'write the normal To the file
				WriteFloat(Stream,Normal.x)
				WriteFloat(Stream,Normal.y)
				WriteFloat(Stream,Normal.z)
				
				'write the triangle To the file
				WriteFloat(Stream,t0.x)
				WriteFloat(Stream,t0.y)
				WriteFloat(Stream,t0.z)
				
				WriteFloat(Stream,t1.x)
				WriteFloat(Stream,t1.y)
				WriteFloat(Stream,t1.z)
				
				WriteFloat(Stream,t2.x)
				WriteFloat(Stream,t2.y)
				WriteFloat(Stream,t2.z)
								
				'attribute count.  set To 0
				WriteShort(Stream,0)
			Next
		Next
	End If
End Function

'Your program will call this Function
'Filename: Name of the file To be saved
'Entity: Parent entity To be saved
'Children: True To aslo save child entities, False To only save parent

Function SaveStl(Filename:String,Entity:TEntity,Children:Int = False)
	SaveStlTrisCount = 0 'reset the triangle count To 0
	Local Stream:TStream = WriteFile(Filename)
	For Local i:Int = 1 To 21 '80 Byte header + triangle count
		WriteInt(Stream,0)
	Next
	
	SaveStlTris(Stream,Entity,Children) 'save the triangles
	SeekStream(Stream,80) 'move To the triangle count positon
	WriteInt(Stream,SaveStlTrisCount) 'write the number of triangles saved
	CloseFile Stream
End Function

'--------------------------------------------
'
'  The code below is a sample of using the
'     Function SaveStl()
'
'---------------------------------------------

Graphics3D 800,600

Local cube:TEntity = CreateCube() 'Create a cube
Local sphere:TEntity = CreateSphere(8,Cube) 'Create a sphere, make cube its parent
ScaleEntity sphere,2,2,2 'scale And move the sphere
PositionEntity sphere,5,0,0

SaveStl("cube.stl",Cube,False) 'save the cube, but Not its children

SaveStl("sphere.stl",sphere,False) 'save the sphere

SaveStl("all.stl",Cube,True) 'save the cube And all its children


Local camera:TCamera = CreateCamera()
PositionEntity camera,0,0,-10

Local light:TLight = CreateLight()
RotateEntity light,45,45,45

While Not KeyHit(KEY_ESCAPE) And Not AppTerminate()
	Cls
	
	UpdateWorld
	RenderWorld
	Flip
	If KeyDown(KEY_W) 'w
		MoveEntity camera,0,0,.2
	End If
	If KeyDown(KEY_S) 's
		MoveEntity camera,0,0,-.2
	End If
	If KeyDown(KEY_A) 'a
		TurnEntity camera,0,-1,0
	End If
	If KeyDown(KEY_D) 'd
		TurnEntity camera,0,1,0
	End If
Wend
