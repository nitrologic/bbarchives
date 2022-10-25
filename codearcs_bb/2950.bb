; ID: 2950
; Author: Dragonfk
; Date: 2012-06-18 16:02:57
; Title: Spherical Terrains and Maths
; Description: This uses a matrix of floats to create a spherical terrain.

;This is a prototest of a refined logistic system that will mold planets for a rocky effect using a sphere matrix.
;For now, this project is called Spiky.
;Coded by Michael Harris Jr. (MK2Y10/Dragonfk)

;Set the graphics to a standard size and mode.
	Graphics3D 800,600,32,2

;This DIM is used later, so don't delete. This keeps track of the verticies when creating the sphere.
	Dim C_V(1,1)

;Now we call 'CreateSphereMatrix' to create a high-level detail matrix for editing.
	M=CreateSphereMatrix(1,2)	;CreateSphereMatrix ( Default Level , Step [The lower, the more detail. Use 'Safe Numbers')

;Get the width and height of the matrix
	SX=MatrixWidth(M)
	SY=MatrixHeight(M)

;Loop trough placing random spikes along the surface.
	For X=0 To SX-1
	For Y=0 To SY-1
		;Poke the Lon/Lat (X,Y) in the matrix to a random value between 0.92 and 1.05.
			PokeMatrix(M,X,Y,Rnd(0.92,1.05))
	Next
	Next
	
;Now create the sphere using the matrix.
	S=CreateMatrixSphere(M)

;This is for programs with the window hidden on start, just ignore this if you don't know what it is.
	;Win=SystemProperty("AppHWND")
	;api_ShowWindow(Win,1)

;Create a scene
	Piv=CreatePivot()
	Cam=CreateCamera(Piv)
	CameraRange(Cam,.0001,1000)
	PositionEntity(Cam,1.1,1.1,1.1)
	PointEntity(Cam,Piv)
	Light=CreateLight(1)
	PositionEntity(Light,1,1,1)
	PointEntity(Light,Piv)

;Now the main loop
	SetBuffer BackBuffer()
	While Not KeyHit(1)
	Cls
	RenderWorld()
	
;Controls to spin the ball (Up and Down) and to turn the camera (Left and Right)
	If KeyDown(203) Then TurnEntity(Piv,0,2,0)
	If KeyDown(205) Then TurnEntity(Piv,0,-2,0)
	If KeyDown(200) Then TurnEntity(S,0,0,-2)
	If KeyDown(208) Then TurnEntity(S,0,0,2)
;Press 'W' to toggle wireframe view.
	Text 0,0,"Check out wireframe mode! Press W to toggle."
	If KeyHit(17)
		WW=WW+1
		If WW=2 Then WW=0
		WireFrame(WW)
	EndIf
	
	Flip
	Delay(20)
	Wend
;Close
	End

;These functions are the result of beta tests and a few trial and error sessions. Read the comments to see what makes them tick.

;This creates a sphere matrix using a default float value and a step value.
;Safe numbers are numbers that will go into 180 and 360 as a whole. The lower the number, the more detail of the sphere.
;1 (use with caution!,) 2, 5, 10, 20, 30, 60.
Function CreateSphereMatrix(Def#=1,Stp=10)
	SX=(180/Stp)+1
	SY=(360/Stp)+1
	Bnk=CreateBank(SX*SY*4+1)
	PokeByte(Bnk,0,Stp)
	FlattenMatrix(Bnk,Def#)
	Return Bnk
End Function

;This makes all the floats in a matrix one value, thus makeing the terrain 'Flat'
;M is the Matrix and D is the value to flatten the matrix at.
Function FlattenMatrix(M,D#=1)
	Stp=PeekByte(M,0)
	SX=(180/Stp)+1
	SY=(360/Stp)+1
	For X=0 To SX
	For Y=0 To SY
	PokeMatrix(M,X,Y,D#)
	Next
	Next
End Function

;This pokes a float in a matrix using the X and Y location (Log/Lat)
Function PokeMatrix(M,X,Y,V#)
	Stp=PeekByte(M,0)
	SX=(180/Stp)
	SY=(360/Stp)
	Lc=(X+(Y*(SX)))*4+1
	PokeFloat(M,Lc,V#)
End Function

;This returns the vlaue of a float in a matrix using the X and Y location (Log/Lat)
Function PeekMatrix#(M,X,Y)
	Stp=PeekByte(M,0)
	SX=(180/Stp)
	SY=(360/Stp)
	Lc=(X+(Y*(SX)))*4+1
	Re#=PeekFloat(M,Lc)
	Return Re#
End Function

;This returns the Matrix Width
Function MatrixWidth(M)
	Stp=PeekByte(M,0)
	SX=(180/Stp)+1
	Return SX
End Function

;This returns the Matrix Height
Function MatrixHeight(M)
	Stp=PeekByte(M,0)
	SY=(360/Stp)+1
	Return SY
End Function

;This just frees the Matrix from memory
Function FreeMatrix(M)
	FreeBank(B)
End Function

;This makes the amazing matrix sphere: (M is matrix and PAR is the Parent
Function CreateMatrixSphere(Mat=0,Par=0)
	Stp=PeekByte(Mat,0)
	SX=180/Stp
	SY=360/Stp
	Dim C_V(SX,SY)
	M=CreateMesh(Par)
	S=CreateSurface(M)
;Vertex Creation
;Here the verticies are made and placed right where they belong
;with influence of the float in the matrix that matches the X/Y location.
	For A=0 To SX
	For B=0 To SY
	AA#=A*Stp
	BB#=B*Stp
	P#=PeekMatrix(Mat,A,B)
	X#=SphereX#(AA,BB)*P#
	Y#=SphereY#(AA,BB)*P#
	Z#=SphereZ#(AA,BB)*P#
	C_V(A,B)=AddVertex(S,X#,Y#,Z#)
	Next
	Next
;Triangle Creation
	For A=0 To SX-1
	For B=0 To SY
;First we fill in one side of the triangle.
	A1=A+1:If A1>SX Then A1=A1-(SX)
	B1=B+1:If B1>SY Then B1=B1-(SY)
	A2=A+1:If A2>SX Then A2=A2-(SX)
	B2=B
	AddTriangle(S,C_V(A1,B1),C_V(A2,B2),C_V(A,B))
;Then the other. These two make a for sided 3D polygon on a sector of the circle.
	A1=A+1:If A1>SX Then A1=A1-(SX)
	B1=B+1:If B1>SY Then B1=B1-(SY)
	A2=A
	B2=B+1:If B2>SY Then B2=B2-(SY)
	AddTriangle(S,C_V(A,B),C_V(A2,B2),C_V(A1,B1))
	Next
	Next
;Update it all and return the new entity!
	UpdateNormals(M)
	Return M
End Function

;3D Maths for spheres (assumes sphere is 1 unit in diameter.
Function SphereX#(U#,V#)
	Re#=Sin(U#)*Sin(V#)
	Return Re#
End Function

Function SphereY#(U#,V#)
	Re#=Sin(U#)*Cos(V#)
	Return Re#
End Function

Function SphereZ#(U#,V#)
	Re#=Cos(U#)
	Return Re#
End Function
