; ID: 2320
; Author: Yahfree
; Date: 2008-09-21 20:38:13
; Title: 2D Vector module
; Description: OOP Vector module for 2d applications

SuperStrict

Module yah.tVec2

ModuleInfo "Name: Yahfree's 2d vector Object"
ModuleInfo "Description: Object for handling vectors"
ModuleInfo "License: Public Domain"
ModuleInfo "Author: Yahfree"

Import brl.math

Public
Rem
bbdoc: 2D Vector Object
End Rem
Type TVec2
	Field x:Float , y:Float
	
	Rem
	bbdoc: Initalizes vector with x/y values
	EndRem
	Method Init:TVec2(_x:Float,_y:Float) 
		x = _x
		y = _y
		Return Self
	End Method
	
	Rem
	bbdoc: Get the vector's X value
	EndRem
	Method GetX:Float() 
		Return x
	End Method
	
	Rem
	bbdoc: Get the vector's Y value
	EndRem
	Method GetY:Float()
		Return y
	End Method
	
	Rem
	bbdoc: Set the vector's X value
	EndRem
	Method SetX(_x:Float) 
		x = _x
	End Method
	
	Rem
	bbdoc: Set the vector's Y value
	EndRem
	Method SetY(_y:Float) 
		y = _y
	End Method
	
	Rem
	bbdoc: Get the vector's angle
	EndRem
	Method GetAngle:Float()
		Return ATan2(y,x)
	End Method
	
	Rem
	bbdoc: Rotate the vector to an angle
	EndRem
	Method Rotate(ang:Float)
		Local xprime:Float=Cos(ang)*x - Sin(ang)*y 
		Local yprime:Float=Sin(ang)*x + Cos(ang)*y
		x=xprime
		y=yprime
	End Method
	
	Rem
	bbdoc: Add values to X and Y
	EndRem
	Method Add(_x:Float,_y:Float)
		x:+_x
		y:+_y
	End Method
	
	Rem
	bbdoc: Add a vector's x/y to this vector
	EndRem
	Method AddVec(Vec:TVec2)
		If Vec=Null Return
		x:+Vec.x
		y:+Vec.y
	End Method
	
	Rem
	bbdoc: Subtract values from X and Y
	EndRem
	Method Subtract(_x:Float,_y:Float)
		x:-_x
		y:-_y
	End Method
	
	Rem
	bbdoc: Subtract a vector's x/y from this vector
	EndRem
	Method SubtractVec(Vec:TVec2)
		If Vec=Null Return
		x:-Vec.x
		y:-Vec.y
	EndMethod
	
	Rem
	bbdoc: Multiply a vector's x/y by 2 respective factors
	EndRem
	Method Multiply(_x:Float,_y:Float)
		x:*_x
		y:*_y
	EndMethod
	
	Rem
	bbdoc: Multiply this vector by another vector's x/y values
	EndRem
	Method MultiplyVec(Vec:TVec2)
		If Vec=Null Return
		x:*Vec.x
		y:*Vec.y
	EndMethod
	
	Rem
	bbdoc: Divide a vector's x/y by 2 respective factors
	EndRem
	Method Divide(_x:Float,_y:Float)
		If _x = 0 Or _y = 0 Return
		x:/_x
		y:/_y
	EndMethod
	
	Rem
	bbdoc: Divide this vector by another vector's x/y values
	EndRem
	Method DivideVec(Vec:TVec2)
		If Vec=Null Return
		x:/Vec.x
		y:/Vec.y
	EndMethod
	
	Rem
	bbdoc: Get the dot product of this vector and "Vec"
	EndRem
	Method DotProduct:Float(Vec:TVec2)
		Return x*Vec.x+y*Vec.y
	End Method
	
	Rem
	bbdoc: Get the angle difference between this vector and "Vec"
	EndRem
	Method GetAngleDif:Float(Vec:TVec2) 
		If Vec=Null Return 0
		Return Abs(TrueMod(ATan2(y,x)+180-ATan2(Vec.y,Vec.x),360)-180)
	EndMethod
	
	Rem
	bbdoc: Returns a new vector that is the result of this vector reflecting off of "Vec"
	EndRem
	Method Reflected:TVec2(Vec:TVec2) 
		Local VecN:TVec2 = Vec.Normalized()
		Local Vec1:TVec2 = Self.Copy()
		Local VecN_DOT_Vec1:Float = VecN.DotProduct(Vec1) 
		VecN.Multiply(2*VecN_DOT_Vec1, 2*VecN_DOT_Vec1)
		Vec1.SubtractVec(VecN) 
		Return Vec1
	End Method
	
	Rem
	bbdoc: Returns a new vector that is the normalized version of this vector
	EndRem
	Method Normalized:TVec2() 
		Local magn:Float=Self.GetMagnitude()
		Local Vector:TVec2=Self.Copy()
		If magn<>0
			Vector.x=x/magn
			Vector.y=y/magn
		EndIf
		Return Vector
	End Method
	
	Rem
	bbdoc: Get the length (magnitude) of the vector
	EndRem
	Method GetMagnitude:Float() 
		Return Sqr(x*x+y*y)
	End Method
	
	Rem
	bbdoc: Returns a new vector that is an exact copy of this vector.
	EndRem
	Method Copy:TVec2() 
		Return New TVec2.Init(x,y)
	End Method
End Type

Private
Function TrueMod:Float(val:Float,modul:Short)
	val:Mod modul
	If val<0 Then val:+modul
	Return val
EndFunction
