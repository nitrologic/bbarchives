; ID: 2173
; Author: nawi
; Date: 2007-12-24 13:01:48
; Title: Simple shadows
; Description: Shadow system

Graphics3D 800,600,0,2
SetBuffer BackBuffer()


Global Cam = CreateCamera()
MoveEntity Cam,0,5,-2



NewObject = GetObject()



;Position our entity
MoveEntity NewObject,0,2.5,0

;White plane
Plane = CreatePlane()
EntityColor Plane,255,255,255
MoveEntity Plane,0,-0.01,0

;Create light
Light = CreateSphere(5)
Spot = CreateLight(2,Light)
ScaleEntity Light,0.1,0.1,0.1
EntityColor Light,255,255,0



Repeat
	;Camera movement
	If KeyDown(200) Then MoveEntity Cam,0,0,1
	If KeyDown(208) Then MoveEntity Cam,0,0,-1
	If KeyDown(203) Then MoveEntity Cam,-1,0,0
	If KeyDown(205) Then MoveEntity Cam,1,0,0


	;Shadow object must be created and destroyed every frame
	Shadow = CreateMesh()
	
	EntityAlpha Shadow,1 
	;There is sadly a bug with alpha shadows.. Didn't figure out a proper solution to this one
	;You could use a camera, but would be a major slowdown
	
;	EntityColor Shadow,0,0,0
	

	;Light movement
	GY# = 7+Sin(MilliSecs()/10)*0.5
	GX# = Cos(MilliSecs()/10)*3
	
	;Draw shadow
	DrawShadow(NewObject,GX#,GY#,GZ#,Shadow)
	PositionEntity Light,GX#,GY#,GZ#
	
	
	;FPS
	FPSC=FPSC+1
	If MilliSecs()>FPSTimer+999 Then
		FPSTimer = MilliSecs()
		FPS=FPSC
		FPSC=0
	EndIf
	
	


	RenderWorld

	Text 0,0,"FPS: " + FPS
	;Text 0,20,"Trisrendered: " + TrisRendered()
	Flip 0

	;Destroy shadow
	FreeEntity Shadow

Until KeyHit(1)

Function DrawShadow(Entity,SX#,SY#,SZ#,Shadow)
	;This works by looping through all triangles, then calculating a line for each vertex to ground (y=0)

	EX#=EntityX#(Entity,1)
	EY#=EntityY#(Entity,1)
	EZ#=EntityZ#(Entity,1)
	ShadowSurface = CreateSurface(Shadow)
	Surfaces=CountSurfaces(Entity)
	For s=1 To Surfaces
		Surface = GetSurface(Entity,s)
		

		Triangles=CountTriangles(Surface)-1
		For t=0 To Triangles
			v1 = TriangleVertex(Surface,t,0)
			v2 = TriangleVertex(Surface,t,1)
			v3 = TriangleVertex(Surface,t,2)
			
			v1X# = VertexX#(Surface,v1)+EX#
			v1Y# = VertexY#(Surface,v1)+EY#
			v1Z# = VertexZ#(Surface,v1)+EZ#
			K#=-SY#/(SY#-v1Y#)
			v1XR# = SX# + (SX#-v1X#)*K#
			v1ZR# = SZ# + (SZ#-v1Z#)*K#

			v2X# = VertexX#(Surface,v2)+EX#
			v2Y# = VertexY#(Surface,v2)+EY#
			v2Z# = VertexZ#(Surface,v2)+EZ#
			K#=-SY#/(SY#-v2Y#)
			v2XR# = SX# + (SX#-v2X#)*K#
			v2ZR# = SZ# + (SZ#-v2Z#)*K#
			
			v3X# = VertexX#(Surface,v3)+EX#
			v3Y# = VertexY#(Surface,v3)+EY#
			v3Z# = VertexZ#(Surface,v3)+EZ#
			K#=-SY#/(SY#-v3Y#)
			v3XR# = SX# + (SX#-v3X#)*K#
			v3ZR# = SZ# + (SZ#-v3Z#)*K#
			
			;Backface culling works by calculating a camera vector and a surface normal
			;If the dot product is > 0 then draw
			
			;Camera vector
			CX#=SX#-(v1X#+v2X#+v3X#)*0.33333
			CY#=SY#-(v1Y#+v2Y#+v3Y#)*0.33333
			CZ#=SZ#-(v1Z#+v2Z#+v3Z#)*0.33333
			
			;Surface normal, create 2 vectors and then create vector W from their cross product
			UX# = V2X#-V1X#
			UY# = V2Y#-V1Y#
			UZ# = V2Z#-V1Z#
			VX# = V3X#-V1X#
			VY# = V3Y#-V1Y#
			VZ# = V3Z#-V1Z#
			WX# = UY#*VZ#-UZ#*VY#
			WY# = -(UX#*VZ#-UZ#*VX#)
			WZ# = UX#*VY#-UY#*VX#
			
			;Add triangle
			If (SY#>v1Y#) And (SY#>v2Y#) And (SY#>v3Y#) And (CX#*WX#+CY#*WY#+CZ#*WZ#)>0.0 Then
				AddTriangle(ShadowSurface,AddVertex(ShadowSurface,v1XR#,0,v1ZR#),AddVertex(ShadowSurface,v2XR#,0,v2ZR#),AddVertex(ShadowSurface,v3XR#,0,v3ZR#))
			EndIf

		Next
	Next
End Function

;Create a test object
Function GetObject()
	NewObject = CreateMesh()
	Cube = CreateCube()
	ScaleMesh Cube,0.6,2.5,0.6
	
	PositionMesh Cube,-3,0,3
	AddMesh Cube,NewObject
	
	PositionMesh Cube,6,0,0
	AddMesh Cube,NewObject
	
	PositionMesh Cube,-6,0,-6
	AddMesh Cube,NewObject
	
	PositionMesh Cube,6,0,0
	AddMesh Cube,NewObject
	
	Sphere = CreateSphere(16)

	
	PositionMesh Sphere,-3,2.5,3
	AddMesh Sphere,NewObject

	PositionMesh Sphere,6,0,0
	AddMesh Sphere,NewObject
	
	PositionMesh Sphere,-6,0,-6
	AddMesh Sphere,NewObject
	
	PositionMesh Sphere,6,0,0
	AddMesh Sphere,NewObject
	
	FreeEntity Cube
	FreeEntity Sphere
	
	EntityColor NewObject,0,200,0
	Return NewObject
End Function
