; ID: 2539
; Author: Krischan
; Date: 2009-07-22 07:13:39
; Title: Cube to Sphere Transformation
; Description: Deforms the vertices of a cube that it appears like a sphere

AppTitle "Cube to Sphere Transformation"

Graphics3D 800,600,32,2

; detail of the cube/sphere
detail=10

Global transform$[1] : transform[0]="Sphere" : transform[1]="Cube"

pivot=CreatePivot()

; Camera
cam=CreateCamera(pivot)
CameraRange cam,0.01,1000
PositionEntity cam,0,0,-3

; create segmented Cube
cube=CreateSegCube(detail)
EntityColor cube,0,255,0
EntityFX cube,1+16

MoveMouse 400,300

WireFrame 1

While Not KeyHit(1)
	
	; movement / rotating
	mxs#=MouseXSpeed()
	mys#=MouseYSpeed()
	TurnEntity cube,0,mxs,0
	TurnEntity pivot,mys,0,0
	MoveEntity cam,0,0,(KeyDown(200)-KeyDown(208))*1.0/60
	
	; W = Wireframe
	If KeyHit(17) Then wf=1-wf : WireFrame 1-wf
	
	; SPACE = transform
	If KeyHit(57) Then
		
		switch=1-switch
		
		If switch=1 Then
			
			; transforms cube to sphere
			Cube2Sphere(cube)
			
		Else
			
			; creates a new cube
			FreeEntity cube
			cube=CreateSegCube(detail)
			EntityColor cube,0,255,0
			EntityFX cube,1+16
			
			
		EndIf
		
	EndIf
	
	RenderWorld
	
	Text 0,0,TrisRendered()+" Tris"
	Text 0,15,"W for Wireframe"
	Text 0,30,"SPACE for "+transform[switch]
	
	Flip
	
Wend

End

; transform a cube patch to sphere patch
Function Cube2Sphere(mesh%)
	
	Local s%,surf%,v%
	Local vx#,vy#,vz#
	
	For s=1 To CountSurfaces(mesh)
		
		surf=GetSurface(mesh,s)
		
		For v=0 To CountVertices(surf)-1
			
			vx=VertexX(surf,v)
			vy=VertexY(surf,v)
			vz=VertexZ(surf,v)
			
			VertexCoords surf,v,SphericalX(vx,vy,vz)*1.333333,SphericalY(vx,vy,vz)*1.333333,SphericalZ(vx,vy,vz)*1.333333
			
		Next
		
	Next
	
End Function

; calculate spherical X
Function SphericalX#(x#,y#,z#)
	
	Return x*Sqr(1.0-y*y*0.5-z*z*0.5+y*y*z*z*1.0/3)
	
End Function

; calculate spherical Y
Function SphericalY#(x#,y#,z#)

	Return y*Sqr(1.0-z*z*0.5-x*x*0.5+z*z*x*x*1.0/3)

End Function

; calculate spherical Z
Function SphericalZ#(x#,y#,z#)
	
	Return z*Sqr(1.0-x*x*0.5-y*y*0.5+x*x*y*y*1.0/3)
	
End Function

; creates a segmented cube
Function CreateSegCube(segs=1,parent=0)
	
	Local side%,surf%
	Local stx#,sty#,stp#,y#
	Local a%,x#,v#,b%,u#
	Local v0%,v1%,v2%,v3%
	Local mesh%=CreateMesh(parent)
	
	; sides
	For side=0 To 3
		
		surf=CreateSurface( mesh )
		stx=-.5
		sty=stx
		stp=Float(1)/Float(segs)
		y=sty
		
		For a=0 To segs
			
			x=stx
			v=a/Float(segs)
			
			For b=0 To segs
				
				u=b/Float(segs)
				AddVertex(surf,x,y,0.5,u,v)
				x=x+stp
				
			Next
			
			y=y+stp
			
		Next
		
		For a=0 To segs-1
			
			For b=0 To segs-1
				
				v0=a*(segs+1)+b:v1=v0+1
				v2=(a+1)*(segs+1)+b+1:v3=v2-1
				AddTriangle( surf,v0,v1,v2 )
				AddTriangle( surf,v0,v2,v3 )
				
			Next
			
		Next
		
		RotateMesh mesh,0,90,0
		
	Next
	
	;top and bottom
	RotateMesh mesh,0,90,90
	
	For side=0 To 1
		
		surf=CreateSurface( mesh )
		stx#=-.5
		sty#=stx
		stp#=Float(1)/Float(segs)
		y#=sty
		
		For a=0 To segs
			x#=stx
			v#=a/Float(segs)
			For b=0 To segs
				u#=b/Float(segs)
				AddVertex(surf,x,y,0.5,u,v)
				x=x+stp
			Next
			y=y+stp
		Next
		
		For a=0 To segs-1
			For b=0 To segs-1
				v0=a*(segs+1)+b:v1=v0+1
				v2=(a+1)*(segs+1)+b+1:v3=v2-1
				AddTriangle( surf,v0,v1,v2 )
				AddTriangle( surf,v0,v2,v3 )
			Next
		Next
		
		RotateMesh mesh,180,0,0
		
	Next
	
	; scale uniform to -1 to +1 space in X/Y/Z dimensions
	FitMesh mesh,-1,-1,-1,2,2,2,1
	
	Return mesh
	
End Function
