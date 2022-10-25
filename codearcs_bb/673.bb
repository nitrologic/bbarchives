; ID: 673
; Author: Birdie
; Date: 2003-05-06 14:55:00
; Title: Triangle Rotate
; Description: Rotate a triangle on a surface.

Function TurnTriangle(surf,index,ax#,ay#,az#)
	Local CosAX#=Cos(ax),SinAX#=Sin(ax)
	Local CosAY#=Cos(ay),SinAY#=Sin(ay)
	Local CosAZ#=Cos(az),SinAZ#=Sin(az)
	Local x#[2],y#[2],z#[2],i[2]
	Local avx#,avy#,avz#
	
	For a=0 To 2
		i[a]=TriangleVertex(surf,index,a)
		x[a]=VertexX(surf,i[a])
		y[a]=VertexY(surf,i[a])
		z[a]=VertexZ(surf,i[a])
		avx=avx+x[a]
		avy=avy+y[a]
		avz=avz+z[a]
	Next
	ox#=avx/Float(3)
	oy#=avy/Float(3)
	oz#=avz/Float(3)
	For a=0 To 2
		x[a]=x[a]-ox
		y[a]=y[a]-oy
		z[a]=z[a]-oz
		;rotate on x
		ty#=y[a]*cosAX+z[a]*sinAX
		z[a]=z[a]*cosAX-y[a]*sinAX
		y[a]=ty
		;rotate on y
		tx#=x[a]*cosAY+z[a]*sinAY
		z[a]=z[a]*cosAY-x[a]*sinAY
		x[a]=tx
		;rotate on z
		tx#=x[a]*cosAZ+y[a]*sinAZ
		y[a]=y[a]*cosAZ-x[a]*sinAZ
		x[a]=tx
		x[a]=x[a]+ox
		y[a]=y[a]+oy
		z[a]=z[a]+oz
		VertexCoords surf,i[a],x[a],y[a],z[a]
	Next
End Function
