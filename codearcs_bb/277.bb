; ID: 277
; Author: David Bird(Birdie)
; Date: 2002-03-21 17:51:15
; Title: Box Mapper
; Description: Creates uv coords for a mesh

;Box Mapping
;(c)2002 David Bird
;enquire@davebird.fsnet.co.uk
;www.davebird.fsnet.co.uk

Type vector
	Field x#
	Field y#
	Field z#
	Field u#
	Field v#
End Type
Global Tnorm.vector=New vector
Global CProd.vector=New vector
Global Normal.vector=New vector

Graphics3D 640,480,0,2
SetBuffer BackBuffer()

cam=CreateCamera()
PositionEntity cam,0,0,-3

sp=CreateCube()

tex=LoadTexture("tex0.jpg")
EntityTexture sp,tex
BoxMap sp,.5,.5
c=CreateCube()
PositionEntity c,4,4,8
EntityTexture c,tex
Repeat
	If KeyDown(200) Then TurnEntity sp,1,0,0
	If KeyDown(208) Then TurnEntity sp,-1,0,0
	If KeyDown(203) Then TurnEntity sp,0,0,1
	If KeyDown(205) Then TurnEntity sp,0,0,-1
	RenderWorld
	Flip
Until KeyDown(1)

End

Function BoxMap(ent,scaleu#=1,scalev#=1)
	c=CreateCube()
	ScaleMesh c,.5,.5,.5
	FlipMesh c
	EntityPickMode c,2
	For s=1 To CountSurfaces(ent)
		surf=GetSurface(ent,s)
		For t=0 To CountTriangles(surf)-1
			;first find the direction of the triangle by normal
			;flat shaded will work
			i0=TriangleVertex(surf,t,0)
			i1=TriangleVertex(surf,t,1)
			i2=TriangleVertex(surf,t,2)
			x0#=VertexX(surf,i0)
			y0#=VertexY(surf,i0)
			z0#=VertexZ(surf,i0)
			x1#=VertexX(surf,i1)
			y1#=VertexY(surf,i1)
			z1#=VertexZ(surf,i1)
			x2#=VertexX(surf,i2)
			y2#=VertexY(surf,i2)
			z2#=VertexZ(surf,i2)
			GetNormal(x0,y0,z0,x1,y1,z1,x2,y2,z2)
			dx#=Abs(TNorm\x)
			dy#=Abs(TNorm\y)
			dz#=Abs(TNorm\z)
			If dx>=dy And dx>=dz Then 
				LinePick 0,0,0,2*x0,2*y0,2*z0
				VertexTexCoords surf,i0,PickedY()+0.5,PickedZ()+0.5
				LinePick 0,0,0,2*x1,2*y1,2*z1
				VertexTexCoords surf,i1,PickedY()+0.5,PickedZ()+0.5
				LinePick 0,0,0,2*x2,2*y2,2*z2
				VertexTexCoords surf,i2,PickedY()+0.5,PickedZ()+0.5
			EndIf
			If dy>dx And dy>=dz Then 
				LinePick 0,0,0,2*x0,2*y0,2*z0
				VertexTexCoords surf,i0,PickedX()+0.5,PickedZ()+0.5
				LinePick 0,0,0,2*x1,2*y1,2*z1
				VertexTexCoords surf,i1,PickedX()+0.5,PickedZ()+0.5
				LinePick 0,0,0,2*x2,2*y2,2*z2
				VertexTexCoords surf,i2,PickedX()+0.5,PickedZ()+0.5
			EndIf
			If dz>dx And dz>dy Then 
				LinePick 0,0,0,2*x0,2*y0,2*z0
				VertexTexCoords surf,i0,PickedX()+0.5,PickedY()+0.5
				LinePick 0,0,0,2*x1,2*y1,2*z1
				VertexTexCoords surf,i1,PickedX()+0.5,PickedY()+0.5
				LinePick 0,0,0,2*x2,2*y2,2*z2
				VertexTexCoords surf,i2,PickedX()+0.5,PickedY()+0.5
			EndIf
			VertexTexCoords surf,i0,VertexU(surf,i0)/scaleu,VertexV(surf,i0)/scalev
			VertexTexCoords surf,i1,VertexU(surf,i1)/scaleu,VertexV(surf,i1)/scalev
			VertexTexCoords surf,i2,VertexU(surf,i2)/scaleu,VertexV(surf,i2)/scalev
		Next
	Next
	FreeEntity c
End Function

Function GetNormal(x1#,y1#,z1#,x2#,y2#,z2#,x3#,y3#,z3#)
	xx1#=x2-x1:yy1#=y2-y1:zz1#=z2-z1
	xx2#=x3-x1:yy2#=y3-y1:zz2#=z3-z1
	Norm(xx1,yy1,zz1)
	xx1=NormX():yy1=normy():zz1=normz()
	Norm(xx2,yy2,zz2)
	xx2=NormX():yy2=normy():zz2=normz()
	CrossProduct(xx2,yy2,zz2,xx1,yy1,zz1)
	TNorm\x=CProductX()
	TNorm\y=CProductY()
	TNorm\z=CProductZ()
	Normalise TNorm
End Function
Function CrossProduct(x1#,y1#,z1#,x2#,y2#,z2#)
	CProd\x=(y1*z2)-(z1*y2)
	CProd\y=(z1*x2)-(x1*z2)
	CProd\z=(x1*y2)-(y1*x2)
End Function
Function Norm(x#,y#,z#)
	l# = Mag(x,y,z)
	Normal\x=x/l
	Normal\y=y/l
	Normal\z=z/l
End Function
Function Mag#(x#,y#,z#)
	Return Sqr(x^2+y^2+z^2)
End Function
Function Normalise(a.vector)
	l# = Mag(a\x,a\y,a\z)
	a\x=a\x/l
	a\y=a\y/l
	a\z=a\z/l
	Return
End Function
Function NormX#()
	Return Normal\x
End Function
Function NormY#()
	Return Normal\y
End Function
Function NormZ#()
	Return Normal\z
End Function
Function CproductX#()
	Return CProd\x
End Function
Function CproductY#()
	Return CProd\y
End Function
Function CproductZ#()
	Return CProd\z
End Function
