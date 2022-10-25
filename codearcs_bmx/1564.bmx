; ID: 1564
; Author: ozak
; Date: 2005-12-14 05:59:39
; Title: Vector 3 class
; Description: Simple, robust vector class.

' Vector3f class by Odin Jensen (www.furi.dk)
' Free to use as you please :)

Strict

' Vector3f class (Lots of static functions which takes an existing result vector for speed)
Type Vector3f

	' Members
	Field x:Float, y:Float, z:Float
	
	' Set vector from x, y, z
	Method FromXYZ(x:Float, y:Float, z:Float)
	
		self.x = x
		self.y = y
		self.z = z
				
	EndMethod
	
	' Set vector from Vector3f
	Method FromVector(v:Vector3f)
	
		self.x = v.x
		self.y = v.y
		self.z = v.z
				
	EndMethod
	
	' Get coordinate from index
	Method GetCoord:Float(Index:Int)
	
		If Index = 0 Return x

		If Index = 1 Return y
		
		Return z
											
	EndMethod		
	
	' Create new vector from x, y, z
	Function CreateFromXYZ:Vector3f(x:Float, y:Float, z:Float)
	
		Local Result:Vector3f = New Vector3f
		
		Result.FromXYZ(x, y, z)
		
		Return Result
		
	EndFunction		
	
	' Create new vector from another
	Function CreateFromVector:Vector3f(v:Vector3f)
	
		Local Result:Vector3f = New Vector3f
		
		Result.FromVector(v)
		
		Return Result
	
	EndFunction
	
	' Add two vectors
	Function Add:Vector3f(Result:Vector3f, v1:Vector3f, v2:Vector3f)
	
		Result.x = v1.x + v2.x
		Result.y = v1.y + v2.y
		Result.z = v1.z + v2.z
		
		Return Result
		
	EndFunction
	
	' Subtract two vectors
	Function Sub:Vector3f(Result:Vector3f, v1:Vector3f, v2:Vector3f)
	
		Result.x = v1.x - v2.x
		Result.y = v1.y - v2.y
		Result.z = v1.z - v2.z
		
		Return Result
		
	EndFunction
	
	' Reverse vector
	Function Reverse:Vector3f(Result:Vector3f, v1:Vector3f)
	
		Result.x = -v1.x
		Result.y = -v1.y
		Result.z = -v1.z

		Return Result
	
	EndFunction
	
	' Multiply vector with scalar
	Function MulScalar:Vector3f(Result:Vector3f, v:Vector3f, Scalar:Float)
	
		Result.x = v.x * Scalar
		Result.y = v.y * Scalar
		Result.z = v.z * Scalar
		
		Return Result
		
	EndFunction	
	
	' Get squared length of vector
	Function SquaredLength:Float(v:Vector3f)
	
		Return Dot(v, v)
	
	EndFunction
	
	' Get length of vector
	Function Length:Float(v:Vector3f)
	
		Return Sqr(SquaredLength(v))
	
	EndFunction
	
	' Normalize vector
	Function Normalize:Vector3f(Result:Vector3f, v:Vector3f)
	
		Local l:Float = Length(v)
		
		If l = 0.0 Then
		
            Result = v ' Avoid divide by zero by just returning self. Shouldn't happen anyway :)

		Else
		
			MulScalar(Result, v, 1.0 / l)
		
		EndIf
		
		Return Result
	
	EndFunction
	
	' Dot product of vector
	Function Dot:Float(v1:Vector3f, v2:Vector3f)

		Return (v1.x * v2.x + v1.y * v2.y + v1.z * v2.z);	
	
	EndFunction
	
	' Cross product of vector
	Function Cross:Vector3f(Result:Vector3f, v1:Vector3f, v2:Vector3f)
	
		Result.x = v1.y * v2.z - v1.z * v2.y;
        Result.y = v2.z * v2.x - v1.x * v2.z;
        Result.z = v2.x * v2.y - v1.y * v2.x;

		Return Result
	
	EndFunction
	
	' Interpolate two vectors
	Function Interpolate:Vector3f(Result:Vector3f, v1:Vector3f, v2:Vector3f, t:Float)
	
		Result.x = v1.x + t * (v2.x - v1.x);
        Result.y = v1.y + t * (v2.y - v1.y);
        Result.z = v1.z + t * (v2.z - v1.z);

		Return Result
	
	EndFunction
	
	' Get distance between two points
	Function GetDistance:Float(v1:Vector3f, v2:Vector3f)
	
		Return Sqr( (v2.x - v1.x) * (v2.x - v1.x) + (v2.y - v1.y) * (v2.y - v1.y) + (v2.z - v1.z) * (v2.z - v1.z) )
	
	EndFunction
	
			
EndType
