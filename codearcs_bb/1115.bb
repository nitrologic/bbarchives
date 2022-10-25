; ID: 1115
; Author: Chroma
; Date: 2004-07-23 10:18:22
; Title: Vector 3D Math Library [v1.7]
; Description: Vector 3D Library converted from C++.

;// Vector Math Library v1.7
;// by Chroma

;// Last Update: November 30, 2004
;// Comments: Renamed and rearranged the code slightly


;----------------------------------------------------------------------;
;// Testing...
;// Remove ";--" in front of lines to see the Vectory Library test demo
;--Graphics 400,300,16,2
;--SetBuffer BackBuffer()
;--AppTitle "Vector 3D Math Library v1.7 - Vector Addition Test"

;// Create two test vectors
;--this.Vector = Vector()
;--that.Vector = Vector(1,2,3)

;// Loop
;--While Not KeyHit(1)
;--Cls

;--Vector_Add(this,this,that)
;--Vector_Show(this,10,10,"Test")

;--Flip
;--Wend
;--End
;//...End Test
;----------------------------------------------------------------------;


;// Tolerance
Const tol# = 0.001


;// Vector Type
Type Vector
	Field x#
	Field y#
	Field z#
End Type


;// Create a Vector
;// Example: this.Vector = Vector()
;// Example: this.Vector = Vector(1,2,3)
Function Vector.Vector(x#=0,y#=0,z#=0)
	v.Vector = New Vector
	v\x=x
	v\y=y
	v\z=z
	Return v
End Function 


;// Set a Vector with New Components
Function Vector_Set(v.Vector,x#,y#,z#)
	v\x = x
	v\y = y
	v\z = z
End Function


;// Vector Component Set
;// Example 1: Vector_SetX(this.Vector,1.0)
;// Example 2: this\x = 1.0
Function Vector_SetX(v.Vector,x#)
	v\x = x
End Function

Function Vector_SetY(v.Vector,y#)
	v\y = y
End Function

Function Vector_SetZ(v.Vector,z#)
	v\z = z
End Function


;// Vector Component Retrieval
;// Example 1: myvar# = Vector_GetX(this.Vector)
;// Example 2: myvar# = this\x
Function Vector_GetX#(v.Vector)
	Return v\x
End Function

Function Vector_GetY#(v.Vector)
	Return v\y
End Function

Function Vector_GetZ#(v.Vector)
	Return v\z
End Function


;// Vector Addition
;// Form of: Vector1 = Vector2 + Vector3
Function Vector_Add(v1.Vector,v2.Vector,v3.Vector)
	v1\x = v2\x + v3\x
	v1\y = v2\y + v3\y
	v1\z = v2\z + v3\z
End Function


;// Vector Scalar Addition
;// Form of: Vector1 = Vector2 + Scalar#
Function Vector_AddScalar(v1.Vector,v2.Vector,s#)
	v1\x = v2\x + s
	v1\y = v2\y + s
	v1\z = v2\z + s
End Function


;// Vector Addition * Time Step
;// Form of: Vector1 = Vector1 + Vector2 * Time_Step#
Function Vector_AddTimeStep(v1.Vector,v2.Vector,time_step#)
	v1\x = v1\x + v2\x * time_step
	v1\y = v1\y + v2\y * time_step
	v1\z = v1\z + v2\z * time_step
End Function


;// Vector Subtraction
;// Form of: Vector1 = Vector2 - Vector3
Function Vector_Subtract(v1.Vector,v2.Vector,v3.Vector)
	v1\x = v2\x - v3\x
	v1\y = v2\y - v3\y
	v1\z = v2\z - v3\z
End Function


;// Vector Scalar Subtraction
;// Form of: Vector1 = Vector2 - Scalar#
Function Vector_SubtractScalar.Vector(v1.Vector,v2.Vector,s#)
	v1\x = v2\x - s
	v1\y = v2\y - s
	v1\z = v2\z - s
End Function


;// Vector Scalar Multiplication
;// Form of: Vector1 = Vector2 * Scalar#
Function Vector_MultiplyScalar(v1.Vector,v2.Vector,s#)
	v1\x = v2\x * s
	v1\y = v2\y * s
	v1\z = v2\z * s
End Function


;// Vector Scalar Division
;// Form of: Vector1 = Vector1 / Scalar#
Function Vector_DivideScalar(v1.Vector,v2.Vector,s#)
	v1\x = v2\x / s
	v1\y = v2\y / s
	v1\z = v2\z / s
End Function


;// Cross Product
;// Form of: Vector1 = U.Vector |CrossProduct| V.Vector
Function Vector_CrossProduct(v1.Vector,u.Vector,v.Vector)
	v1\x =  u\y * v\z  -  u\z * v\y 
	v1\y = -u\x * v\z  +  u\z * v\x 
	v1\z =  u\x * v\y  -  u\y * v\x
End Function


;// Dot Product
Function Vector_DotProduct#(u.Vector,v.Vector)
	Return u\x * v\x + u\y * v\y + u\z * v\z
End Function


;// Magnitude
;// Example: this_magnitude# = Vector_Magnitude(this.Vector)
Function Vector_Magnitude#(v.Vector)
	Return Sqr(v\x * v\x + v\y * v\y + v\z * v\z)
End Function


;// Normalize
;// Example: Vector_Normalize(this.Vector)
Function Vector_Normalize(v.Vector)
	mag#=Sqr(v\x * v\x + v\y * v\y + v\z * v\z)
	v\x = v\x / mag
	v\y = v\y / mag
	v\z = v\z / mag
	If (Abs(v\x) < tol) v\x = 0.0
	If (Abs(v\y) < tol) v\y = 0.0
	If (Abs(v\z) < tol) v\z = 0.0
End Function


;// Reverse a Vector
;// Form of: Vector1 = -Vector2
;// Example: Vector_Reverse(this.Vector,that.Vector)
Function Vector_Reverse(v1.Vector,v2.Vector)
	v1\x = -v2\x
	v1\y = -v2\y
	v1\z = -v2\z
End Function


;// Reset a Vector to Zero
;// Example: Vector_Reset(this.Vector)
Function Vector_Reset(v.Vector)
	v\x = 0.0
	v\y = 0.0
	v\z = 0.0
End Function


;// Vector 1 is set to Vector 2
;// Example 1: Vector_Clone(this.Vector,that.Vector)
;// Example 2: this.Vector = that.Vector
Function Vector_Clone(v1.Vector,v2.Vector)
	v1\x = v2\x
	v1\y = v2\y
	v1\z = v2\z
End Function


;// Free a Vector
;// Example: Vector_Free(this.Vector)
Function Vector_Free(v.Vector)
	Delete v
End Function


;// PositionEntity Replacement for Vector Object
;// Example: Vector_PositionEntity MyEntity,v.vector
Function Vector_PositionEntity(ent,v.vector)
	PositionEntity ent,v\x,v\y,v\z
End Function


;// Show Vector Values
;// Place This Function After Renderworld
;// Example: Vector_Show(this.Vector,15,15,"Test")
Function Vector_Show(v.Vector,xpos,ypos,label$)
	Text xpos,ypos, label$ + "_X=" + v\x
	Text xpos,ypos + 15,label$ + "_Y=" + v\y
	Text xpos,ypos + 30,label$ + "_Z=" + v\z
End Function


;// Degrees To Radians Conversion
Function DegreesToRadians#(deg#)
	Return deg * Pi / 180.0
End Function


;// Radians To Degrees Conversion
Function RadiansToDegrees#(rad#)
	Return rad * 180.0 / Pi
End Function
