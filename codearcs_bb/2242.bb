; ID: 2242
; Author: Wings
; Date: 2008-04-13 19:15:09
; Title: Furys Alpha Terrain example.
; Description: Alpha terrain example. build of several smal meshes.

;Images and compiled examples.
;http://www.tiberion.eu/furysalphademo.zip

;Furys Terrain Alpha Demo
;
;This is a alpha demo that show how to use alpha maps on a surface.
;

;Written by Daniel Eriksson (Tiberion Crew) in 2008-04-13

Global camera

Dim verts(65,65,8) ;8 layers of vertices
Dim surfs(8)

Dim hmap#(1024,1024)
Dim TEXTMAP(1024,1024,8) ; this is the textmap buffer


Dim Face_NX#(32768)
Dim Face_NY#(32768)
Dim Face_NZ#(32768)
		
Dim Vertex_ConnectedTris(32768)
Dim Vertex_TriList(32768, 32)

Global brush_grass,brush_rock,brush_rock2,brush_mud,trisr



Global player,playeravtar,camtarketroot,camchasetarget,swimmode
Global WX=1024
Global WZ=1024





Graphics3D 1280,800,32,2
SetBuffer BackBuffer()
Cls

camera=CreateCamera()
PositionEntity camera,0,150,0
CameraViewport camera,0,0,1280,800
PositionEntity camera,1,50,0
CameraRange camera,0.1,64
CameraClsColor camera,140,128,255
CameraFogColor camera,138,125,253
CameraFogMode camera,1
CameraFogRange camera,1,64


light=CreateLight()


;Load terrain brushes
brush_grass=LoadBrush ("img\grass3.jpg",1,4,4)
If brush_grass=0 Then RuntimeError "grass01.jpg not found."
BrushFX brush_grass,2+32

brush_rock=LoadBrush ("img\rock3.jpg",1,2,2)
If Not brush_rock Then RuntimeError "rock01.jpg not found."
BrushFX brush_rock,2+32


brush_rock2=LoadBrush ("img\cliff.jpg",1,8,8)
If Not brush_rock2 Then RuntimeError "brush_rock2.bmp not found."
BrushFX brush_rock2,2+32


brush_mud=LoadBrush ("img\mudd.jpg",1,8,8)  ; (this is base texture no alpha enabled)
;BrushFX brush_mud,2+32


sandplane=CreatePlane()
PaintEntity sandplane,brush_mud
PositionEntity sandplane,0,-0.5,0

brush_water=LoadBrush("img\water512.jpg",1,8,8)
plane=CreatePlane()



PaintEntity plane,brush_water

PositionEntity plane,0,4.5,0
EntityFX plane,32
EntityAlpha plane,0.66



;Create a smal whaterlake on the big island
lake=CreateCube()
laketext=LoadTexture("img\water512.jpg")
ScaleTexture laketext,0.2,0.2
ScaleEntity lake,20,0.1,21
EntityTexture lake,laketext
EntityFX lake,32
EntityAlpha lake,0.66
PositionEntity lake,134.15,9.335,114.701



;LoadHeight map
image=LoadImage("img\hmap.bmp")
If Not image Then RuntimeError "img\hmap.bmp not found."

For x=0 To ImageWidth(image)
	For z=0 To ImageHeight(image)

		h#=(ReadPixel(x,z,ImageBuffer(image))And 255)/10.0

		hmap#(x,z)=h#

	Next ;z
Next ;x


;LoadSandMap
smap=LoadImage("img\sandmap.bmp")
If Not smap Then RuntimeError "cant find img\sandmap.bmp"



;This init the alpha values in varius planes 
For x=0 To ImageWidth(image)
	For z=0 To ImageHeight(image)
		TEXTMAP(x,z,0)=0 ; Alpha value 0 
		TEXTMAP(x,z,1)=0 ; Alpha value 0
		TEXTMAP(x,z,2)=0 ; Alpha value 0
		TEXTMAP(x,z,3)=1 ; Alpha value 1
	Next
Next



;Montain emulation texturing.. Paint montains
For x=1 To ImageWidth(image)
	For z=1 To ImageHeight(image)
	
	sand=ReadPixel(x,z,ImageBuffer(smap)) And $FF
	
	testy#=HMAP#(x,z)
	
	
	
	If testy#<5
	
		;TEXTMAP(X,Z,2)=1 ;Remed this line out to test if whater works good then normal mapping is enabled
	
	Else
	
	TEXTMAP(X,Z,2)=1
	
		For testx=-1 To 1
		For testz=-1 To 1
	
	
			If hmap#(testx+x,testz+z)>testy#+0.5
				TEXTMAP(x,z,0)=0
				TEXTMAP(x,z,1)=1
			
				;TEXTMAP(testx+x,testz+z,0)=0
				;TEXTMAP(testx+x,testz+z,1)=1
			End If
	
	
			;Verry steep hill
			If hmap#(testx+x,testz+z)>testy#+1

				TEXTMAP(x,z,0)=1
				TEXTMAP(x,z,1)=1
				TEXTMAP(x,z,3)=1
			End If
	
		Next
		Next
	
	End If
	
	If sand=255 ; ops this is sand so all else is invalid.
	
				TEXTMAP(x,z,0)=0 ; Alpha value 0 
		TEXTMAP(x,z,1)=0 ; Alpha value 0
		TEXTMAP(x,z,2)=0 ; Alpha value 0
		TEXTMAP(x,z,3)=1 ; Alpha value 1
	
	End If
	
	
	
	Next
	Print "Building up montain emulation..."+x
Next




;Creates the world meshs
For x=0 To ImageWidth(image) Step 63
	For z=0 To ImageHeight(image) Step 63
	mesh=create_cell(x,z)
	Next
Next



While Not KeyHit(1)


camera_handling()




UpdateWorld
RenderWorld


Text 0,0,"Tris total "+trisr
Text 0,10,"Tris rendered "+TrisRendered()
Text 0,20,"FPS: "+fps
Text 0,30,"Camera X: "+EntityX(camera)
Text 0,40,"Camera Y: "+EntityY(camera)
Text 0,50,"Camera Z: "+EntityZ(camera)


Flip 
ct=ct+1

If MilliSecs()-oldms>1000

	oldms=MilliSecs()
	fps=ct
	ct=0


End If


Wend



Function create_cell(xoffs,zoffs)


	;empty verts
	For layer=0 To 3
	For x=0 To 63
	For z=0 To 63
		verts(x,z,layer)=-1
	Next
	Next
	Next
	

	;Create the grass mesh
	mesh=CreateMesh()


	For layer=0 To 3

		If layer=0 Then surf=CreateSurface(mesh,brush_rock2)
		If layer=1 Then surf=CreateSurface(mesh,brush_rock)
		If layer=2 Then surf=CreateSurface(mesh,brush_grass)
		If layer=3 Then surf=CreateSurface(mesh,brush_mud)




		For z=0 To 63
			For x=0 To 63
			
				;Check if to skipp creation of this vertex
				hitt=0
				
				If textmap(x+xoffs,z+zoffs,layer)=1 Then hitt=hitt+1
				If textmap(x+xoffs+1,z+zoffs,layer)=1 Then hitt=hitt+1
				If textmap(x+xoffs,z+zoffs+1,layer)=1 Then hitt=hitt+1
				If textmap(x+xoffs+1,z+zoffs+1,layer)=1 Then hitt=hitt+1

				If z>0 And x>0 Then
					If textmap(x+xoffs-1,z+zoffs-1,layer)=1 Then hitt=hitt+1
					If textmap(x+xoffs-1,z+zoffs,layer)=1 Then hitt=hitt+1
					If textmap(x+xoffs,z+zoffs-1,layer)=1 Then hitt=hitt+1
				End If



			
				If layer=3 Then hitt=1 ;Always create if its layer mud
			
				If hitt Then verts(X,Z,layer) =AddVertex (surf,X,hmap#(x+xoffs,z+zoffs),Z,X,Z)
			
			Next ;X
		Next ;z


		;Add triangles to mesh
		For z=0 To 62
			For x=0 To 62
			
				;Create this quad if there is vertices available.
				hitt=0
				If verts(x,z,layer)<>-1 Then hitt=hitt+1
				;If verts(x+1,z,layer)<>-1 Then hitt=hitt+1
				If verts(x,z+1,layer)<>-1 Then hitt=hitt+1
				If verts(x+1,z+1,layer)<>-1 Then hitt=hitt+1
				

				If hitt=3

				
					AddTriangle surf,verts(x,z,layer),verts(x,z+1,layer),verts(x+1,z+1,layer)
				
				End If
				
				hitt=0
				If verts(x,z,layer)<>-1 Then hitt=hitt+1
				If verts(x+1,z,layer)<>-1 Then hitt=hitt+1
				;If verts(x,z+1,layer)<>-1 Then hitt=hitt+1
				If verts(x+1,z+1,layer)<>-1 Then hitt=hitt+1
					
				If hitt=3
					AddTriangle surf,verts(x,z,layer),verts(x+1,z+1,layer),verts(x+1,z,layer)
				trisr=trisr+1	
				End If	
			Next ; X
		Next ;Z


		;Montain emulation
		For x=0 To 63
			For z=0 To 63
	If verts(x,z,layer)<>-1
				VertexColor surf,verts(x,z,layer),255,255,255,0
				If TEXTMAP(x+xoffs,z+zoffs,layer)=1 Then VertexColor surf,verts(x,z,layer),255,255,255,255   
	End If
			Next ;Z
		Next ;X


	Next ; Layer


	PositionEntity mesh,xoffs,0,zoffs

	;UpdateNormals mesh so it have shade. and dont look flat.
	Calculate_Normals(mesh)


	Return mesh

End Function






Function camera_handling()


	If KeyDown(17) Then MoveEntity camera,0,0,0.02  ;W	
	If KeyDown(18) Then MoveEntity camera,0,0,1 ; E
	If KeyDown(31) Then	MoveEntity camera,0,0,-1 ;S
	If KeyDown(30) Then	MoveEntity camera,-0.02,0,0 ;A
	If KeyDown(32) Then MoveEntity camera,0.02,0,0  ;D
If MouseDown(2)
	smx#=MouseXSpeed()/10.0
	smy#=MouseYSpeed()/10.0
	

	TurnEntity camera,smy#,-smx#,0
	RotateEntity camera,EntityPitch#(camera),EntityYaw#(camera),0
	;TranslateEntity camera,0,-0.1,0
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
End If


;	y#=BilinearInterpValue# ( Float(EntityX(camera))/2 ,Float(EntityZ(camera))/2 )

;	If y#+0.5>EntityY#(camera)

;		PositionEntity camera,EntityX(camera),y#+0.5,EntityZ(camera)

;	End If

End Function












Function Calculate_Normals(ThisMesh)
	
	; Loop through all surfaces of the mesh.
	Surfaces = CountSurfaces(ThisMesh)
	For LOOP_Surface = 1 To Surfaces

		Surface_Handle = GetSurface(ThisMesh, LOOP_Surface)

		; Reset the number of connected polygons for each vertex.
		For LoopV = 0 To 32767	
			Vertex_ConnectedTris(LoopV) = 0
		Next	
	
		; Loop through all triangles in this surface of the mesh.
		Tris = CountTriangles(Surface_Handle)
		For LOOP_Tris = 0 To Tris-1

			; Get the vertices that make up this triangle.
				Vertex_0 = TriangleVertex(Surface_Handle, LOOP_Tris, 0)
				Vertex_1 = TriangleVertex(Surface_Handle, LOOP_Tris, 1)
				Vertex_2 = TriangleVertex(Surface_Handle, LOOP_Tris, 2)
	
			; Adjust the number of triangles each vertex is connected to and
			; store this triangle in each vertex's list of triangles it is connected to.
				ConnectedTris = Vertex_ConnectedTris(Vertex_0)
				Vertex_TriList(Vertex_0, ConnectedTris) = LOOP_Tris
				Vertex_ConnectedTris(Vertex_0) = ConnectedTris + 1

				ConnectedTris = Vertex_ConnectedTris(Vertex_1)
				Vertex_TriList(Vertex_1, ConnectedTris) = LOOP_Tris
				Vertex_ConnectedTris(Vertex_1) = ConnectedTris + 1

				ConnectedTris = Vertex_ConnectedTris(Vertex_2)
				Vertex_TriList(Vertex_2, ConnectedTris) = LOOP_Tris
				Vertex_ConnectedTris(Vertex_2) = ConnectedTris + 1

			; Calculate the normal for this face.

				; Get the corners of this face:
				Ax# = VertexX#(Surface_Handle, Vertex_0)
				Ay# = VertexY#(Surface_Handle, Vertex_0)
				Az# = VertexZ#(Surface_Handle, Vertex_0)

				Bx# = VertexX#(Surface_Handle, Vertex_1)
				By# = VertexY#(Surface_Handle, Vertex_1)
				Bz# = VertexZ#(Surface_Handle, Vertex_1)

				Cx# = VertexX#(Surface_Handle, Vertex_2)
				Cy# = VertexY#(Surface_Handle, Vertex_2)
				Cz# = VertexZ#(Surface_Handle, Vertex_2)

				; Triangle 1
				; Get the vectors for two edges of the triangle.
				Px# = Ax#-Bx#
				Py# = Ay#-By#
				Pz# = Az#-Bz#

				Qx# = Bx#-Cx#
				Qy# = By#-Cy#
				Qz# = Bz#-Cz#

				; Compute their cross product.
				Nx# = Py#*Qz# - Pz#*Qy#
				Ny# = Pz#*Qx# - Px#*Qz#
				Nz# = Px#*Qy# - Py#*Qx#

				; Store the face normal.
				Face_NX#(LOOP_Tris) = Nx#
				Face_NY#(LOOP_Tris) = Ny#
				Face_NZ#(LOOP_Tris) = Nz#

		Next

		; Now that all the face normals for this surface have been calculated, calculate the vertex normals.
		Vertices = CountVertices(Surface_Handle)
		For LOOP_Vertices = 0 To Vertices-1

			; Reset this normal.
			Nx# = 0
			Ny# = 0
			Nz# = 0

			; Add the normals of all polygons which are connected to this vertex.
			Polys = Vertex_ConnectedTris(LOOP_Vertices)
				
			For LOOP_Polys = 0 To Polys-1

				ThisPoly = Vertex_TriList(LOOP_Vertices, LOOP_Polys)

				Nx# = Nx# + Face_NX#(ThisPoly)
				Ny# = Ny# + Face_NY#(ThisPoly)
				Nz# = Nz# + Face_NZ#(ThisPoly)			
				
			Next	
				
			; Normalize the new vertex normal.
			; (Normalizing is scaling the vertex normal down so that it's length = 1)

				Nl# = Sqr(Nx#^2 + Ny#^2 + Nz#^2)

				; Avoid a divide by zero error if by some freak accident, the vectors add up to 0.
				; If Nl# = 0 Then Nl# = 0.1

				Nx# = Nx# / Nl#
				Ny# = Ny# / Nl#
				Nz# = Nz# / Nl#

			; Set the vertex normal.
			
				VertexNormal Surface_Handle, LOOP_Vertices, Nx#, Ny#, Nz#
				;VertexColor Surface_Handle, LOOP_Vertices, polys*127, polys*127, polys*127
		
		Next

	Next

End Function
