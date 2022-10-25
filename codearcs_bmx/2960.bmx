; ID: 2960
; Author: Pineapple
; Date: 2012-07-10 10:28:16
; Title: 2D vector library
; Description: 2D vector type using doubles and with useful math functions.

' 	--+-----------------------------------------------------------------------------------------+--
'	  |   This code was originally written by Sophie Kirschner (sophiek@pineapplemachine.com)   |  
' 	  | It is released as public domain. Please don't interpret that as liberty to claim credit |  
' 	  |   that isn't yours, or to sell this code when it could otherwise be obtained for free   |  
'	  |                because that would be a really shitty thing of you to do.                |
' 	--+-----------------------------------------------------------------------------------------+--

SuperStrict

Import brl.math

' example program
Rem
Graphics 256,192
Local center:vect2d=vect2d.Create(128,96)
Local mouse:vect2d=vect2d.Create(0,0)
Repeat
	Cls
	
	mouse.set MouseX(),MouseY()
	
	SetColor 0,180,255
	Local angle:vect2d=vect2d.forangle(center.angleto(mouse))
	angle.applyscale(16)
	Local angleperp1:vect2d=angle.perp()
	Local angleperp2:vect2d=angle.rperp()
	angle.applyadd(center)
	angleperp1.applyadd(center)
	angleperp2.applyadd(center)
	DrawLine center.x,center.y,angle.x,angle.y
	SetColor 255,70,50
	DrawLine center.x,center.y,angleperp1.x,angleperp1.y
	SetColor 0,220,0
	DrawLine center.x,center.y,angleperp2.x,angleperp2.y
	
	Flip
Until KeyDown(27) Or AppTerminate()
EndRem

Rem
bbdoc: 2D vector type using doubles and with useful math functions.
EndRem
Type vect2d
	Field x!,y!
	Rem
	bbdoc: Returns a new vect2d with the specified x,y values.
	EndRem
	Function Create:vect2d(x!=0,y!=0)
		Local n:vect2d=New vect2d
		n.x=x;n.y=y
		Return n
	End Function
	Rem
	bbdoc: Returns a copy of this vect2d.
	EndRem
	Method copy:vect2d()
		Return Create(x,y)
	End Method
	Rem
	bbdoc: Sets this vect2d's values to the given ones.
	EndRem
	Method set(_x!,_y!)
		x=_x;y=_y
	End Method
	Rem
	bbdoc: Sets this vect2d's values to the given vect2d's values.
	EndRem
	Method setto(o:vect2d)
		x=o.x;y=o.y
	End Method
	Rem
	bbdoc: Sets this vect2d's values to zero.
	EndRem
	Method setzero()
		x=0;y=0
	End Method
	Rem
	bbdoc: Returns True if the magnitude of this vect2d is zero, False otherwise.
	EndRem
	Method iszero%()
		Return x=0 And y=0
	End Method
	Rem
	bbdoc: Returns True if this vect2d is equivalent to another, false otherwise.
	about: Considers differences in the x and y components that are less than or equal to the threshold (scaled epsilon) as being equal.
	EndRem
	Method equals%(o:vect2d,epsilon!=1.0e-12)
		Return Abs(x-o.x)<Abs(x*epsilon) And Abs(y-o.y)<Abs(y*epsilon)
	End Method
	Rem
	bbdoc: Returns True if this vect2d is exactly equivalent to another, false otherwise.
	about: May cause errors due to imprecisions.
	EndRem
	Method equalsexact%(o:vect2d)
		Return x=o.x And y=o.y
	End Method
	Rem
	bbdoc: Returns 1 if this vect2d is greater than another, 0 if they are equal, -1 otherwise.
	about: Compares lengths.
	EndRem
	Method comparevects%(o:vect2d)
		Local thisl!=lengthsq()
		Local thatl!=o.lengthsq()
		If thisl>thatl 
			Return 1
		ElseIf thisl=thatl 
			Return 0
		Else
			Return -1
		EndIf
	End Method
	Rem
	bbdoc: Returns the sum of this and another vect2d.
	EndRem
	Method add:vect2d(o:vect2d)
		Return Create(x+o.x,y+o.y)
	End Method
	Rem
	bbdoc: Returns a vect2d which has the sum of the x values of two vect2ds and keeps the original y.
	EndRem
	Method addx:vect2d(o:vect2d)
		Return Create(x+o.x,y)
	End Method
	Rem
	bbdoc: Returns a vect2d which has the sum of the y values of two vect2ds and keeps the original x.
	EndRem
	Method addy:vect2d(o:vect2d)
		Return Create(x,y+o.y)
	End Method
	Rem
	bbdoc: Adds another vect2d to this one.
	EndRem
	Method applyadd(o:vect2d)
		x:+o.x;y:+o.y
	End Method
	Rem
	bbdoc: Adds the x value of another vect2d to this one.
	EndRem
	Method applyaddx:vect2d(o:vect2d)
		x:+o.x
	End Method
	Rem
	bbdoc: Adds the y value of another vect2d to this one.
	EndRem
	Method applyaddy:vect2d(o:vect2d)
		y:+o.y
	End Method
	Rem
	bbdoc: Returns the vect2d that is the subtraction of a vect2d from this one.
	EndRem
	Method sub:vect2d(o:vect2d)
		Return Create(x-o.x,y-o.y)
	End Method
	Rem
	bbdoc: Subtracts a vect2d from this one.
	EndRem
	Method applysub(o:vect2d)
		x:-o.x;y:-o.y
	End Method
	Rem
	bbdoc: Returns the product of two vect2ds.
	EndRem
	Method mult:vect2d(o:vect2d)
		Return Create(x*o.x,y*o.y)
	End Method
	Rem
	bbdoc: Multiplies this vect2d by another.
	EndRem
	Method applymult:vect2d(o:vect2d)
		x:*o.x;y:*o.y
	End Method
	Rem
	bbdoc: Returns the negation of this vect2d.
	EndRem
	Method negate:vect2d()
		Return Create(-x,-y)
	End Method
	Rem
	bbdoc: Negates this vect2d.
	EndRem
	Method applynegate()
		x=-x;y=-y
	End Method
	Rem
	bbdoc: Returns the vect2d that has this one's negated x and original y.
	EndRem
	Method negatex:vect2d()
		Return Create(-x,y)
	End Method
	Rem
	bbdoc: Returns the vect2d that has this one's original x and negated y.
	EndRem
	Method negatey:vect2d()
		Return Create(x,-y)
	End Method
	Rem
	bbdoc: Negates this vect2d's x value.
	EndRem
	Method applynegatex()
		x=-x
	End Method
	Rem
	bbdoc: Negates this vect2d's y value.
	EndRem
	Method applynegatey()
		y=-y
	End Method
	Rem
	bbdoc: Returns the vect2d that is this one scaled by a scalar value.
	about: Individually multiplies the x and y components by a single value.
	EndRem
	Method scale:vect2d(v!)
		Return Create(x*v,y*v)
	End Method
	Rem
	bbdoc: Scales this vect2d by a scalar value.
	about: Individually multiplies the x and y components by a single value.
	EndRem
	Method applyscale(v!)
		x:*v;y:*v
	End Method
	Rem
	bbdoc: Returns the dot product of this and another vect2d.
	EndRem
	Method dot!(o:vect2d)
		Return x*o.x+y*o.y
	End Method
	Rem
	bbdoc: Returns the magnitude of the z component of the cross product of this and another vect2d.
	EndRem
	Method cross!(o:vect2d)
		Return y*o.y-x*o.x
	End Method
	Rem
	bbdoc: Returns the magnitude of this vect2d.
	about: The length or magnitude of a vector is its distance from the origin.
	EndRem
	Method length!()
		Return Sqr((x*x)+(y*y))
	End Method
	Rem
	bbdoc: Returns the magnitude of this vect2d squared. (Faster than length() due to no Sqr() call and perfectly adequate for comparisons.)
	about: The length or magnitude of a vector is its distance from the origin.
	EndRem
	Method lengthsq!()
		Return ((x*x)+(y*y))
	End Method
	Rem
	bbdoc: Returns the distance from this vect2d to another.
	EndRem
	Method dist!(o:vect2d)
		Local dx!=x-o.x
		Local dy!=y-o.y
		Return Sqr((dx*dx)+(dy*dy))
	End Method
	Rem
	bbdoc: Returns the distance from this vect2d to another squared. (Faster than length() due to no Sqr() call and perfectly adequate for comparisons.)
	EndRem
	Method distsq!(o:vect2d)
		Local dx!=x-o.x
		Local dy!=y-o.y
		Return ((dx*dx)+(dy*dy))
	End Method
	Rem
	bbdoc: Returns the normalization of this vect2d.
	about: The normalization of a vector is when its components are divided by its magnitude so that it adopts a unit length.
	EndRem
	Method normalize:vect2d()
		Local v!=length()
		Return Create(x/v,y/v)
	End Method
	Rem
	bbdoc: Causes this vect2d to become its normalization.
	about: The normalization of a vector is when its components are divided by its magnitude so that it adopts a unit length.
	EndRem
	Method applynormalize()
		Local v!=length()
		x:/v;y:/v
	End Method
	Rem
	bbdoc: Returns the normalization of this vect2d.
	about: Protects against diviside by zero errors.
	EndRem
	Method normalizesafe:vect2d()
		Local v!=length()
		If v=0 Return New vect2d
		Return Create(x/v,y/v)
	End Method
	Rem
	bbdoc: Causes this vect2d to become its normalization.
	about:  Protects against diviside by zero errors.
	EndRem
	Method applynormalizesafe()
		Local v!=length()
		If v=0 x=0;y=0;Return
		x:/v;y:/v
	End Method
	Rem
	bbdoc: Returns a vect2d that is this one clamped to the given length.
	EndRem
	Method clamp:vect2d(length!)
		If lengthsq()>length*length
			Return normalize().scale(length)
		Else
			Return copy()
		EndIf
	End Method
	Rem
	bbdoc: Clamp this vect2d to the given length.
	EndRem
	Method applyclamp(length!)
		If lengthsq()>length*length
			applynormalize()
			applyscale(length)
		EndIf
	End Method
	Rem
	bbdoc: Returns a perpendicular vect2d. (90 degree rotation)
	EndRem
	Method perp:vect2d()
		Return Create(-y,x)
	End Method
	Rem
	bbdoc: Returns a perpendicular vect2d. (-90 degree rotation)
	EndRem
	Method rperp:vect2d()
		Return Create(y,-x)
	End Method
	Rem
	bbdoc: Returns the vector projection of this onto o.
	EndRem
	Method project:vect2d(o:vect2d)
		Return o.scale(dot(o)/o.lengthsq())
	End Method
	Rem
	bbdoc: Uses complex number multiplication to rotate this vect2d by another.
	about: Scaling will occur if this is not a unit vector.
	EndRem
	Method rotate:vect2d(o:vect2d)
		Return Create(x*o.x-y*o.y,x*o.y+y*o.x)
	End Method
	Rem
	bbdoc: Inverse of rotate()
	EndRem
	Method unrotate:vect2d(o:vect2d)
		Return Create(x*o.x+y*o.y,y*o.x-x*o.y)
	End Method
	Rem
	bbdoc: Linearly interpolate between this vect2d and another.
	EndRem
	Method lerp:vect2d(o:vect2d,t!)
		Return scale(1!-t).add(o.scale(t))
	End Method
	Rem
	bbdoc: Linearly interpolate between this vect2d and another by the given distance.
	EndRem
	Method lerpconst:vect2d(o:vect2d,distance#)
		Return add(o.sub(Self).clamp(distance))
	End Method
	Rem
	bbdoc: Returns the angular direction from this vect2d to another. (In degrees)
	EndRem
	Method angleto!(o:vect2d)
		Return ATan2(o.y-y,o.x-x)
	End Method
	Rem
	bbdoc: Returns the unit length vect2d for the given angle. (In degrees)
	EndRem
	Function forangle:vect2d(angle!)
		Return Create(Cos(angle),Sin(angle))
	End Function
	Rem
	bbdoc: Returns the angular direction this vect2d is pointing in. (In degrees)
	EndRem
	Method toangle!()
		Return ATan2(y,x)
	End Method
	Rem
	bbdoc: Returns a string representation of this vect2d.
	EndRem
	Method ToString$()
		Return x+","+y
	End Method
	Rem
	bbdoc: Compare two vect2ds by their length.
	EndRem
	Method compare%(o2:Object)
		Return comparevects(vect2d(o2))
	End Method
End Type
