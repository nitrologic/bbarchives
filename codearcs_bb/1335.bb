; ID: 1335
; Author: Jonathan Nguyen
; Date: 2005-03-22 01:12:49
; Title: Non-Type Based Vector Lib
; Description: Your standard vector lib. with banks instead of types and many vector functions.

; /////////////////////////////////// VECTORS



; // Create Vector
;    Action: Creates a vector.
;    Return: Vector handle of the new vector.
Function CreateVector(x#=0,y#=0,z#=0)
	Local vectorBank=CreateBank(13)
		PokeFloat vectorBank,0,x#
		PokeFloat vectorBank,4,y#
		PokeFloat vectorBank,8,z#
		PokeByte vectorBank,12,0
	Return vectorBank
End Function

; // Temporary Vector
;    Action: Creates a temporary vector.
;    Return: Vector handle of the new vector.
;    Notes: Deleted automatically when used in a non-data-returning vector function.
Function TVector(x#=0,y#=0,z#=0)
	Local vectorBank=CreateBank(13)
		PokeFloat vectorBank,0,x#
		PokeFloat vectorBank,4,y#
		PokeFloat vectorBank,8,z#
		PokeByte vectorBank,12,1
	Return vectorBank
End Function

; // Free Vector
;    Action: Deletes a vector.
;    Return: True if successful, False if failed.
Function FreeVector(vectorBank)
	If vectorBank=0
		Return False
		Else
		FreeBank vectorBank
		Return True
	EndIf
End Function

; // Copy Vector
;    Action: Copies the components of Vector 2 onto Vector 1.
;    Return: True if successful, False if failed.
Function CopyVector(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return False
		Else
		PokeFloat vector1Bank,0,PeekFloat#(vector2Bank,0)
		PokeFloat vector1Bank,4,PeekFloat#(vector2Bank,4)
		PokeFloat vector1Bank,8,PeekFloat#(vector2Bank,8)
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return True
	EndIf
End Function

; // Set Vector
;    Action: Set's a vector's components.
;    Return: True if successful, False if failed.
Function SetVector(vectorBank,x#=0,y#=0,z#=0)
	If vectorBank=0
		Return False
		Else
		PokeFloat vectorBank,0,x#
		PokeFloat vectorBank,4,y#
		PokeFloat vectorBank,8,z#
		Return True
	EndIf
End Function

; // Set Vector X
;    Action: Sets the X component of a vector.
;    Return: Nothing.
Function SetVectorX#(vectorBank,tX#)
	PokeFloat vectorBank,0,tX#
End Function

; // Set Vector Y
;    Action: Sets the Y component of a vector.
;    Return: Nothing.
Function SetVectorY#(vectorBank,tY#)
	PokeFloat vectorBank,4,tY#
End Function

; // Set Vector Z
;    Action: Sets the Z component of a vector.
;    Return: Nothing.
Function SetVectorZ#(vectorBank,tZ#)
	PokeFloat vectorBank,8,tZ#
End Function

; // Vector X
;    Action: Returns the X component of a vector.
;    Return: The X component of a vector.
Function VectorX#(vectorBank)
	Return PeekFloat#(vectorBank,0)
End Function

; // Vector Y
;    Action: Returns the Y component of a vector.
;    Return: The Y component of a vector.
Function VectorY#(vectorBank)
	Return PeekFloat#(vectorBank,4)
End Function

; // Vector Z
;    Action: Returns the Z component of a vector.
;    Return: The Z component of a vector.
Function VectorZ#(vectorBank)
	Return PeekFloat#(vectorBank,8)
End Function

; // Vector AX
;    Action: Returns the "pitch" of a vector in Blitz3D space.
;    Return: The "pitch" of a vector in Blitz3D space.
Function VectorAX#(vectorBank)
	Return ATan2(Sqr#(PeekFloat#(vectorBank,0)^2+PeekFloat#(vectorBank,8)^2),PeekFloat#(vectorBank,4))-90
End Function

; // Vector AY
;    Action: Returns the "yaw" of a vector in Blitz3D space.
;    Return: The "yaw" of a vector in Blitz3D space.
Function VectorAY#(vectorBank)
	Return ATan2(-PeekFloat#(vectorBank,0),PeekFloat#(vectorBank,8))
End Function

; // Vector Magnitude
;    Action: Returns the magnitude of a vector.
;    Return: The magnitude of a vector, 0 if failed.
Function VectorMagnitude#(vectorBank)
	If vectorBank=0
		Return 0
		Else
		Return Sqr#(PeekFloat#(vectorBank,0)^2+PeekFloat#(vectorBank,4)^2+PeekFloat#(vectorBank,8)^2)
	EndIf
End Function

; // Add Vectors
;    Action: Vector 1 = Vector 1 + Vector 2
;    Return: True if successful, False if failed.
;    Notes: Directly writes the result onto Vector 1.
Function AddVectors(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return False
		Else
		PokeFloat vector1Bank,0,PeekFloat#(vector1Bank,0)+PeekFloat#(vector2Bank,0)
		PokeFloat vector1Bank,4,PeekFloat#(vector1Bank,4)+PeekFloat#(vector2Bank,4)
		PokeFloat vector1Bank,8,PeekFloat#(vector1Bank,8)+PeekFloat#(vector2Bank,8)
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return True
	EndIf
End Function

; // Subtract Vectors
;    Action: Vector 1 = Vector 1 - Vector 2
;    Return: True if successful, False if failed.
;    Note: Directly writes the result onto Vector 1.
Function SubtractVectors(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return False
		Else
		PokeFloat vector1Bank,0,PeekFloat#(vector1Bank,0)-PeekFloat#(vector2Bank,0)
		PokeFloat vector1Bank,4,PeekFloat#(vector1Bank,4)-PeekFloat#(vector2Bank,4)
		PokeFloat vector1Bank,8,PeekFloat#(vector1Bank,8)-PeekFloat#(vector2Bank,8)
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return True
	EndIf
End Function

; // Temporary Add Vectors
;    Action: Adds Vector 1 and Vector 2 and returns the handle of the new resultant vector.
;    Return: Vector handle of the resultant vector.
;    Notes: Deleted automatically when used in a non-data-returning vector function.
Function TAddVectors(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		tBank=TVector(PeekFloat#(vector1Bank,0)+PeekFloat#(vector2Bank,0),PeekFloat#(vector1Bank,4)+PeekFloat#(vector2Bank,4),PeekFloat#(vector1Bank,8)+PeekFloat#(vector2Bank,8))
		If PeekByte(vector1Bank,12)=1 Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return tBank
	EndIf
End Function

; // Temporary Subtract Vectors
;    Action: Subtracts Vector 2 from Vector 1 and returns the handle of the new resultant vector.
;    Return: Vector handle of the resultant vector.
;    Notes: Deleted automatically when used in a non-data-returning vector function.
Function TSubtractVectors(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		tBank=TVector(PeekFloat#(vector1Bank,0)-PeekFloat#(vector2Bank,0),PeekFloat#(vector1Bank,4)-PeekFloat#(vector2Bank,4),PeekFloat#(vector1Bank,8)-PeekFloat#(vector2Bank,8))
		If PeekByte(vector1Bank,12)=1 Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return tBank
	EndIf
End Function

; // Scale Vector
;    Action: Scales a vector by the second parameter.
;    Return: True if successful, False if failed.
Function ScaleVector(vectorBank,c#)
	If vectorBank=0
		Return False
		Else
		PokeFloat vectorBank,0,PeekFloat#(vectorBank,0)*c#
		PokeFloat vectorBank,4,PeekFloat#(vectorBank,4)*c#
		PokeFloat vectorBank,8,PeekFloat#(vectorBank,8)*c#
		Return True
	EndIf
End Function

; // Unit Vector
;    Action: Scales a vector to a unit vector (vector of length 1).
;    Return: True if successful, False if failed.
Function UnitVector(vectorBank)
	If vectorBank=0
		Return False
		Else
		ScaleVector vectorBank,1.0/VectorMagnitude#(vectorBank)
		Return True
	EndIf
End Function

; // Temporary Scale Vector
;    Action: Scales a vector by the second parameter.
;    Return: Vector handle of resulting scaled vector.
;    Notes: Deleted automatically when used in a non-data-returning vector function.
Function TScaleVector(vectorBank,c#,freeTemporary=True)
	If vectorBank=0
		Return 0
		Else
		tBank=TVector(PeekFloat#(vectorBank,0)*c#,PeekFloat#(vectorBank,4)*c#,PeekFloat#(vectorBank,8)*c#)
		If PeekByte(vectorBank,12)=1 And freeTemporary=True Then FreeBank vectorBank
		Return tBank
	EndIf
End Function

; // Temporary Unit Vector
;    Action: Scales a vector to a unit vector (vector of length 1).
;    Return: Vector handle of resulting unit vector.
;    Notes: Deleted automatically when used in a non-data-returning vector function.
Function TUnitVector(vectorBank,freeTemporary=True)
	If vectorBank=0
		Return 0
		Else
		c#=VectorMagnitude#(vectorBank)
		tBank=TVector(PeekFloat#(vectorBank,0)/c#,PeekFloat#(vectorBank,4)/c#,PeekFloat#(vectorBank,8)/c#)
		If PeekByte(vectorBank,12)=1 And freeTemporary=True Then FreeBank vectorBank
		Return tBank
	EndIf
End Function

; // Dot Product Explanation
;    v1 = <x,y,z>
;    v2 = <a,b,c>
;    v1 * v2 = x*a+y*b+z*c
;    v1 * v2 is a scalar, NOT a vector.
;    If v1 * v2 = 0 Then v1 is orthogonal (perpendicular) to v2.
;    If v1 * v2 > 0 Then angle between v1 and v2 is less than 90 degrees.
;    If v1 * v2 < 0 Then angle between v1 and v2 is greater than 90 degrees.

; // Dot Product
;    Action: Returns the dot product of two vectors.
;    Return: The dot product of the two vectors, 0 if failed.
;    Notes: Last parameter is only for internal library usage (projections).
Function DotProduct#(vector1Bank,vector2Bank,freeTemporary=True)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		tDot#=PeekFloat#(vector1Bank,0)*PeekFloat#(vector2Bank,0)+PeekFloat#(vector1Bank,4)*PeekFloat#(vector2Bank,4)+PeekFloat#(vector1Bank,8)*PeekFloat#(vector2Bank,8)
		If PeekByte(vector1Bank,12)=1 And freeTemporary=True Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 And freeTemporary=True Then FreeBank vector2Bank
		Return tDot#
	EndIf
End Function

; // Cross Product Explanation
;    v1 = <x,y,z>
;    v2 = <a,b,c>
;    v1 X v2 = <y*c-z*b,z*a-x*c,x*b-y*a>
;    v1 X v2 is a new vector.
;    v1 X v2 is orthogonal (perpendicular) to v1 AND v2.
;    If v1 X v2 = <0,0,0> Then v1 is parallel to v2.

; // Cross Product
;    Action: Crosses Vector 1 to Vector 2 and returns the handle of the new resultant vector.
;    Return: Vector handle of the resultant vector.
Function CrossProduct(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		tBank=CreateVector(PeekFloat#(vector1Bank,4)*PeekFloat#(vector2Bank,8)-PeekFloat#(vector1Bank,8)*PeekFloat#(vector2Bank,4),PeekFloat#(vector1Bank,8)*PeekFloat#(vector2Bank,0)-PeekFloat#(vector1Bank,0)*PeekFloat#(vector2Bank,8),PeekFloat#(vector1Bank,0)*PeekFloat#(vector2Bank,4)-PeekFloat#(vector1Bank,4)*PeekFloat#(vector2Bank,0))
		If PeekByte(vector1Bank,12)=1 Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return tBank
	EndIf
End Function

; // Temporary Cross Product
;    Action: Crosses Vector 1 to Vector 2 and returns the handle of the new resultant vector.
;    Return: Vector handle of the resultant vector.
;    Notes: Deleted automatically when used in a non-data-returning vector function.
Function TCrossProduct(vector1Bank,vector2Bank,freeTemporary=True)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		tBank=TVector(PeekFloat#(vector1Bank,4)*PeekFloat#(vector2Bank,8)-PeekFloat#(vector1Bank,8)*PeekFloat#(vector2Bank,4),PeekFloat#(vector1Bank,8)*PeekFloat#(vector2Bank,0)-PeekFloat#(vector1Bank,0)*PeekFloat#(vector2Bank,8),PeekFloat#(vector1Bank,0)*PeekFloat#(vector2Bank,4)-PeekFloat#(vector1Bank,4)*PeekFloat#(vector2Bank,0))
		If PeekByte(vector1Bank,12)=1 And freeTemporary=True Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 And freeTemporary=True Then FreeBank vector2Bank
		Return tBank
	EndIf
End Function

; // Dot Angle Between
;    Action: Returns the absolute angle between Vector 1 and Vector 2 [0,180].
;    Return: Absolute angle between Vector 1 and Vector 2.
;    Notes: The angle returned is between 0 and 180 degrees meaning that it gives no indication of direction.
Function DotAngleBetween#(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		tAngle#=ACos(DotProduct#(vector1Bank,vector2Bank,False)/(VectorMagnitude#(vector1Bank)*VectorMagnitude#(vector2Bank)))
		If PeekByte(vector1Bank,12)=1 Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return tAngle#
	EndIf
End Function

; // Cross Angle Between
;    Action: Returns the absolute angle between the lines drawn by Vector 1 and Vector 2 [0,90].
;    Return: Absolute angle between lines drawn by Vector 1 and Vector 2.
;    Notes: The angle returned is between 0 and 90 degrees meaning that it gives no indication of direction.
Function CrossAngleBetween#(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		tAngle#=ASin(VectorMagnitude#(TCrossProduct(vector1Bank,vector2Bank,False))/(VectorMagnitude#(vector1Bank)*VectorMagnitude#(vector2Bank)))
		If PeekByte(vector1Bank,12)=1 Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return tAngle#
	EndIf
End Function

; // Scalar Projection
;    Action: Returns the length of Vector 2 projected onto Vector 1.
;    Return: Length of projection of Vector 2 onto Vector 1.
;    Notes: The return is a scalar, not a vector. Use vector projection for that.
Function ScalarProjection#(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		tScalar#=DotProduct#(vector1Bank,vector2Bank,False)/VectorMagnitude#(vector1Bank)
		If PeekByte(vector1Bank,12)=1 Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return tScalar#
	EndIf
End Function

; // Vector Projection
;    Action: Returns the handle for the vector result of Vector 2 projected onto Vector 1.
;    Return: Vector handle of the resultant vector.
;    Notes: The return is a vector in the direction of Vector 1.
Function VectorProjection(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		c#=DotProduct#(vector1Bank,vector2Bank,False)/DotProduct#(vector1Bank,vector1Bank,False)
		tBank=CreateVector(PeekFloat#(vector1Bank,0)*c#,PeekFloat#(vector1Bank,4)*c#,PeekFloat#(vector1Bank,8)*c#)
		If PeekByte(vector1Bank,12)=1 Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return tBank
	EndIf
End Function

; // Temporary Vector Projection
;    Action: Returns the handle for the vector result of Vector 2 projected onto Vector 1.
;    Return: Vector handle of the resultant vector.
;    Notes: Deleted automatically when used in a non-data-returning vector function.
Function TVectorProjection(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		c#=DotProduct#(vector1Bank,vector2Bank,False)/DotProduct#(vector1Bank,vector1Bank,False)
		tBank=TVector(PeekFloat#(vector1Bank,0)*c#,PeekFloat#(vector1Bank,4)*c#,PeekFloat#(vector1Bank,8)*c#)
		If PeekByte(vector1Bank,12)=1 Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return tBank
	EndIf
End Function

; // Orthogonal Projection
;    Action: Returns the handle for the orthogonal vector result of Vector 2 projected onto Vector 1.
;    Return: Vector handle of the resultant orthogonal vector.
;    Notes: The return is a vector orthogonal (perpendicular) to Vector 1.
Function OrthogonalProjection(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		c#=DotProduct#(vector1Bank,vector2Bank,False)/DotProduct#(vector1Bank,vector1Bank,False)
		tBank=CreateVector()
		CopyVector(tBank,TSubtractVectors(vector2Bank,TVector(PeekFloat#(vector1Bank,0)*c#,PeekFloat#(vector1Bank,4)*c#,PeekFloat#(vector1Bank,8)*c#)))
		If PeekByte(vector1Bank,12)=1 Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return tBank
	EndIf
End Function

; // Temporary Orthogonal Projection
;    Action: Returns the handle for the vector result of Vector 2 projected onto Vector 1.
;    Return: Vector handle of the resultant vector.
;    Notes: Deleted automatically when used in a non-data-returning vector function.
Function TOrthogonalProjection(vector1Bank,vector2Bank)
	If vector1Bank=0 Or vector2Bank=0
		Return 0
		Else
		c#=DotProduct#(vector1Bank,vector2Bank,False)/DotProduct#(vector1Bank,vector1Bank,False)
		tBank=TSubtractVectors(vector2Bank,TVector(PeekFloat#(vector1Bank,0)*c#,PeekFloat#(vector1Bank,4)*c#,PeekFloat#(vector1Bank,8)*c#))
		If PeekByte(vector1Bank,12)=1 Then FreeBank vector1Bank
		If PeekByte(vector2Bank,12)=1 Then FreeBank vector2Bank
		Return tBank
	EndIf
End Function
