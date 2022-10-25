; ID: 2737
; Author: Sauer
; Date: 2010-06-28 10:35:06
; Title: Vector2 Lib
; Description: A 2D Vector Library

;----------------------------
;  Vector2 Library
;----------------------------

Type Vector2
	Field x#,y#
End Type

Function init.Vector2(x,y)
	;easy way to init a new vector
	tmp.Vector2=New Vector2
	tmp\x=x
	tmp\y=y
	Return tmp
End Function

Function copy.Vector2(v.Vector2)
	;makes a unique instance copying another vector
	tmp.Vector2=New Vector2
	tmp\x=v\x
	tmp\y=v\y
	Return tmp
End Function 

Function dot#(v.Vector2,w.Vector2)
	;returns the dot product of two vectors, returned value is a scalar
	Return (v\x#*w\x#)+(v\y#*w\y#)
End Function

Function length#(v.Vector2)
	;returns the length (magnitude) of a vector
	Return Sqr(dot(v.Vector2,v.Vector2))
End Function 

Function angle_between#(v.Vector2,w.Vector2)
	;finds the angle, in degrees, between two vectors
	Return ACos(dot(normalize(v),normalize(w)))
End Function 

Function rotate_vector2.Vector2(v.Vector2,deg#)
	;counter clockwise rotation, in degrees
	tmp.Vector2=New Vector2
	tmp\x#=(Cos#(deg)*v\x#)-(Sin#(deg)*v\y#)
	tmp\y#=(Sin#(deg)*v\x#)+(Cos#(deg)*v\y#)
	Return tmp
End Function 

Function cross.Vector2(v.Vector2)
	;returns a vector perpendicular to v
	tmp.Vector2=New Vector2
	tmp\x=v\y*1
	tmp\y=-v\x*1
	Return tmp
End Function 

Function normalize.Vector2(v.Vector2)
	;creates a vector with length one in the same direction as original
	tmp.Vector2=New Vector2
	leng#=length#(v)
	tmp\x#=v\x#/leng#
	tmp\y#=v\y#/leng#
	Return tmp
End Function 

Function mul.Vector2(v.Vector2,mag#)
	;multiply a vector by a scalar
	tmp.Vector2=New Vector2
	tmp\x#=v\x#*mag#
	tmp\y#=v\y#*mag#
	Return tmp
End Function  

Function add.Vector2(v.Vector2,w.Vector2)
	;adds two vectors
	tmp.Vector2=New Vector2
	tmp\x#=v\x#+w\x#
	tmp\y#=v\y#+w\y#
	Return tmp
End Function

Function sub.Vector2(v.Vector2,w.Vector2)
	;subtracts two vectors
	tmp.Vector2=New Vector2
	tmp\x#=v\x#-w\x#
	tmp\y#=v\y#-w\y#
	Return tmp 
End Function  

Function ref.Vector2(v.Vector2,w.Vector2,through=False)
	;a reflection vector, v being direction of object and w being surface.  Through means through wall or bounce off wall (default)
	If through=False
		n.Vector2=copy(w)
	Else
		n.Vector2=cross(w)
	EndIf 
	l.Vector2=normalize(v)
	n=normalize(n)
	q.Vector2=mul(n,dot(l,n))
	r.Vector2=sub(mul(q.Vector2,2),l)
	r=normalize(r)
	Return r
End Function 

Function random.Vector2()
	;returns a vector pointing in a random direction
	tmp.Vector2=init(Rnd(-10,10),Rnd(10,10))
	tmp=normalize(tmp)
	Return tmp
End Function 

Function print_vector2(v.Vector2,name$="-")
	;prints a vector with an optional name
	Print "Vector2 '"+name$+"' X: "+v\x#+" Y: "+v\y#
End Function 

Function draw_vector2(v.Vector2,w.Vector2)
	;draws a line representing a vector, taking a vector v as an origin and w as direction/magnitude
	Line v\x#,v\y#,v\x#+w\x#,v\y#+w\y#
End Function 
	
Function circle_circle(v.Vector2,w.Vector2,dist#)
	;simple circle to circle collision using vectors
	If dist(v,w)<dist#*dist#
		Return True
	Else
		Return False
	EndIf
End Function 

Function dist(v.Vector2,w.Vector2)
	;returns the distance squared between two vectors
	Return (v\x-w\x)*(v\x-w\x)+(v\y-w\y)*(v\y-w\y)
End Function
	

Function circle_vector2(v.Vector2,w.Vector2,w1.Vector2,dist#)
	;determines if circle (with center v and radius dist) and vector w are colliding, where w is direction/magnitude and w1 is origin
	Local v1.Vector2=sub(v,w1)
	Local tmp.Vector2=normalize(w)
	Local proj.Vector2=mul(tmp,dot(tmp,v1)) 
	Local a=circle_circle(v1,proj,dist#)
	Local leng#=length#(w)+dist#
	leng#=(leng#*leng#)
	Return a And dist(proj,init(0,0))<=leng#+dist# And dist(proj,w)<=leng#+dist#
End Function
