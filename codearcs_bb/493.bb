; ID: 493
; Author: Vertex
; Date: 2002-11-18 15:12:53
; Title: Milkshape - ASCII
; Description: Export a Milksahpe - ASCII file

Graphics3D 640,480,32,2
SetBuffer BackBuffer()

Sphere = CreateSphere(20)

saveASCII(Sphere,"Sphere.txt")

Camera = CreateCamera()
PositionEntity Camera,0,5,5
PointEntity Camera,Sphere

Light = CreateLight(1,Camera)

While Not KeyDown(1)
 	TurnEntity Sphere,0,1,0
	UpdateWorld : RenderWorld : Flip
Wend 
ClearWorld : End 

Function saveASCII(Entity,File$)
	Local S,V,VX#,VY#,VZ#,VU#,VV#
	Local NX#,NY#,NZ#,T,V0,V1,V2
	
	Stream = WriteFile(File$)
	
	WriteLine Stream,"Frames: 1" ; Totalframes
	WriteLine Stream,"Frame: 1" ; Currentfame
	WriteLine Stream,"Meshes: " + CountSurfaces(Entity) ; Number of meshes
	
	For S = 1 To CountSurfaces(Entity)
	    Surface = GetSurface(Entity,S)
		; Mesh name flag and materialindex
		If S = 1 Then 
			If EntityName$(Entity) <> "" Then 
				WriteLine Stream,Chr$(34) + EntityName$(Entity) + Chr$(34) + " 0 -1"
			Else
				WriteLine Stream,Chr$(34) + "Group" + S + Chr$(34) + " 0 -1"				
			EndIf
		Else
			WriteLine Stream,Chr$(34) + "Group" + S + Chr$(34) + " 0 -1"
		EndIf
				
				
		; Number of vertices
		WriteLine Stream,CountVertices(Surface)
		For V = 0 To CountVertices(Surface) - 1
			VX# = VertexX#(Surface,V) : VY# = VertexY#(Surface,V) 
			VZ# = VertexZ#(Surface,V) : VU# = VertexU#(Surface,V)
			VV# = VertexV#(Surface,V)
			; Vertex flag X Y Z U V boneindex
			WriteLine Stream,"0 "+VX#+" "+VY#+" "+VZ#+" "+VU#+" "+VV#+" -1"
		Next 
		
		; Number of normals
		WriteLine Stream,CountVertices(Surface)
		For V = 0 To CountVertices(Surface) - 1
			NX# = VertexNX#(Surface,V)
			NY# = VertexNY#(Surface,V)
			NZ# = VertexNZ#(Surface,V)
			; Normal X Y Z 
			WriteLine Stream,NX# + " " + NY# + " " + NZ# 
		Next 
		
		; Number of triangles
		WriteLine Stream,CountTriangles(Surface) 
		For T = 0 To CountTriangles(Surface) - 1
			V0 = TriangleVertex(Surface,T,0)
			V1 = TriangleVertex(Surface,T,1)	
			V2 = TriangleVertex(Surface,T,2)
			; Triangle flag vertexindex 0 1 2 Normalindex 0 1 2 smoothinggroup
			WriteLine Stream,"0 "+V0+" "+V1+" "+V2+" "+V0+" "+V1+" "+V2+" 0"
		Next 
		
		; Bones...
	Next
	WriteLine Stream,"Materials: 0"
	WriteLine Stream,"Bones: 0" 
	CloseFile Stream
End Function
