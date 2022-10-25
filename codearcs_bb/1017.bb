; ID: 1017
; Author: Sarakan
; Date: 2004-05-08 03:44:58
; Title: Mesh Terrain Generator
; Description: CreateTerrainMesh(x,y) Function

Function CreateTerrainMesh(x,y)
	;x = how many horizontal vertices
	;y = how many vertical vertices

	mesh = CreateMesh()
	surf = CreateSurface(mesh)	
	If x < 2 Or y < 2 Then Return mesh		;in case user gives a bad number simply return the mesh
	For tempx = 1 To x
		For tempy = 1 To y
			AddVertex(surf,tempx*4,0,tempy*4)
		Next	
	Next	

	For v0 = 0 To (x*y)-y-2
		For Z = 1 To x*y
			If v0 = z*y-1 Then v0 = v0 + 1
		Next
		v1 = V0 + 1
		v2 = v1 + y
		v3 = v0 + y
		AddTriangle(surf,v0,v1,v2)
		AddTriangle(surf,v0,v2,v3)
	Next

	Return mesh
End Function





;EXAMPLE
Graphics3D 800,600,16,2 
SetBuffer BackBuffer() 
SeedRnd MilliSecs() 

Global camera = CreateCamera() 
Global light = CreateLight(camera) 
MoveEntity camera,5,4,-6 



meshy_terrain = CreateTerrainMesh(5,7) ;<======== HERE IT IS 


PlaceBallOnEachVertex(meshy_terrain) 




While Not KeyDown(1) 
Get_Input() 
RenderWorld 
Text 1,1, "Arrow keys, A key, and Z key move camera" 
Flip 
Cls 
Wend 

End 



Function PlaceBallOnEachVertex(mesh) 
surface = GetSurface(mesh,1) 
For temp = 0 To CountVertices(surface) - 1 

tempball = CreateSphere(8) 
PositionEntity tempball,VertexX#(surface,temp), VertexY#(surface,temp), VertexZ#(surface,temp) 
EntityColor tempball,190,0,0 
EntityAlpha tempball,.4 
EntityParent tempball, mesh 
NameEntity tempball,"Vertex " + temp 

tempimg = CreateTexture(Len(temp) * 10,15) 
SetBuffer TextureBuffer(tempimg) 
Cls 
Text 1,1,temp 
SetBuffer BackBuffer() 

vert_sprite = CreateSprite() 
EntityTexture vert_sprite,tempimg 
PositionEntity vert_sprite,VertexX#(surface,temp), VertexY#(surface,temp), VertexZ#(surface,temp),1 
EntityParent vert_sprite, tempball 
Next 
End Function 


Function CreateTerrainMesh(x,y) 
;x = how many horizontal vertices 
;y = how many vertical vertices 

mesh = CreateMesh() 
surf = CreateSurface(mesh) 
If x < 2 Or y < 2 Then Return mesh ;in case user gives a bad number simply return the mesh 
For tempx = 1 To x 
For tempy = 1 To y 
AddVertex(surf,tempx*4,0,tempy*4) 
Next 
Next 

For v0 = 0 To (x*y)-y-2 
For Z = 1 To x*y 
If v0 = z*y-1 Then v0 = v0 + 1 
Next 
v1 = V0 + 1 
v2 = v1 + y 
v3 = v0 + y 
AddTriangle(surf,v0,v1,v2) 
AddTriangle(surf,v0,v2,v3) 
Next 

Return mesh 
End Function 


Function Get_Input() 
shift = 0 
up = 0 
down = 0 
left_key = 0 
right_key = 0 
a = 0 
z = 0 
If KeyDown(shiftkey) = 1 Then shift = 1 
If KeyDown(200) = 1 Then up = 1 
If KeyDown(208) = 1 Then down = 1 
If KeyDown(203) = 1 Then Left_key = 1 
If KeyDown(205) = 1 Then Right_key = 1 
If KeyDown(30) = 1 Then a = 1 
If KeyDown(44) = 1 Then z = 1 

If right_key = 1 Then MoveEntity camera,1,0,0 
If left_key = 1 Then MoveEntity camera,-1,0,0 
If up = 1 Then MoveEntity camera,0,0,1 
If down = 1 Then MoveEntity camera,0,0,-1 
If a = 1 Then MoveEntity camera,0,1,0 
If z = 1 Then MoveEntity camera,0,-1,0 
If KeyDown(211) Then TurnEntity camera,0,2,0 
If KeyDown(209) Then TurnEntity camera,0,-2,0 
End Function
