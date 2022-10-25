; ID: 867
; Author: Richard Betson
; Date: 2003-12-30 00:28:40
; Title: Align an Object to a Triangle.
; Description: Align an Object to a Triangle.

;Alighn object to triangle - By Richard Betson
;www.redeyeware.50megs.com
;vidiot@getgoin.net
;
;Special thanks to Halo, BlitzSupport, Floyed and Ross C
;
;This code shows how to Align an Object to a triangle. 
;Use as you see fit :)
;



Global vectorx#
Global vectory#
Global vectorz#
Global vectorw#




Graphics3D 640, 480, 0, 2
AppTitle "Use Arrow Keys to turn"

cam = CreateCamera ()
MoveEntity cam, 0, 0, -10


Global cone = CreateCone ()
EntityColor cone, 0, 0, 255

light = CreateLight ()
MoveEntity light, -50, -5, 10



;Mesh<<<<<
Global mesh=CreateMesh()

Global surf=CreateSurface(mesh)

v0=AddVertex(surf,0,1,0)
v1=AddVertex(surf,1,0,0)
v2=AddVertex(surf,-1,0,0)

tri=AddTriangle(surf,v0,v1,v2)

ScaleMesh mesh,3,3,3
PositionEntity mesh,0,-1,0

;-----------------------


Repeat


	x()
		
	If KeyDown (203) TurnEntity mesh, 0, -1, 0, 1
	If KeyDown (205) TurnEntity mesh, 0, 1, 0, 1
	If KeyDown (200) TurnEntity mesh, 1, 0, 0
	If KeyDown (208) TurnEntity mesh, -1, 0, 0

	RenderWorld
	Flip
	
Until KeyHit (1)

End


Function x()

	v0 = TriangleVertex (surf, tri, 0)
	v1 = TriangleVertex (surf, tri, 1)
	v2 = TriangleVertex (surf, tri, 2)

	; ---------------------------------------------------------
	; Mark's code for backface culling...
	; ---------------------------------------------------------
	
    ; Vertex positions...

    	x0# = VertexX (surf, v0)
	y0# = VertexY (surf, v0)
	z0# = VertexZ (surf, v0)
	
    	x1# = VertexX (surf, v1)
	y1# = VertexY (surf, v1)
	z1# = VertexZ (surf, v1)
	
    	x2# = VertexX (surf, v2)
	y2# = VertexY (surf, v2)
	z2# = VertexZ (surf, v2)


d#=TriangleNormal#(x0#,y0#,z0#,x1#,y1#,z1#,x2#,y2#,z2#)
	TFormVector vectorx, vectory, vectorz,mesh,0
	AlignToVector cone, TFormedX (), TFormedY (), TFormedZ (), 2,1
End Function


Function TriangleNormal#(Ax#,Ay#,Az#,Bx#,By#,Bz#,Cx#,Cy#,Cz#)
	SubVector Bx#,By#,Bz#,Ax#,Ay#,Az#
	ux#=VectorX()
	uy#=VectorY()
	uz#=VectorZ()
	SubVector Cx#,Cy#,Cz#,Bx#,By#,Bz#
	vx#=VectorX()
	vy#=VectorY()
	vz#=VectorZ()
	CrossProduct vx#,vy#,vz#,ux#,uy#,uz#
	Normalize vectorx,vectory,vectorz
	Return Ax#*vectorx+Ay#*vectory+Az#*vectorz
End Function

Function VectorX#()
	Return vectorx
End Function

Function VectorY#()
	Return vectory
End Function

Function VectorZ#()
	Return vectorz
End Function

Function VectorW#()
	Return vectorw
End Function

Function SubVector(Ax#,Ay#,Az#,Bx#,By#,Bz#)
	vectorx#=ax#-bx#
	vectory#=ay#-by#
	vectorz#=az#-bz#
End Function

Function Normalize(nx#,ny#,nz#)
	If nx=0 And ny=0 And nz=0 Return
	m#=Magnitude(nx#,ny#,nz#)
	vectorx#=nx#/m#
	vectory#=ny#/m#
	vectorz#=nz#/m#
End Function

Function CrossProduct(Ax#,Ay#,Az#,Bx#,By#,Bz#)
	vectorx#=Ay#*Bz#-Az#*By#
	vectory#=Az#*Bx#-Ax#*Bz#
	vectorz#=Ax#*By#-Ay#*Bx#
End Function


Function Magnitude(nx#,ny#,nz#) 
	m#= Sqr( (nx*nx) + (ny*ny) + (nz*nz) ) 
	Return m# 
End Function
