; ID: 2727
; Author: AndrewT
; Date: 2010-06-08 17:46:11
; Title: 3D Math Functions
; Description: Matrix and vector functions designed as an alternative to the deprecated OpenGL matrix functions.

Rem
3D Math Library

By Andrew Thorwall (AndrewT)

-----------------------------------------------------------------------
INFORMATION
-----------------------------------------------------------------------

As of OpenGL 3.0, the built-in matrix functions are deprecated. This means you must handle
matrices yourself. This library is designed to help you do that.

Note that there is no Matrix UDT. Matrices must be defined as 16-element Float arrays, i.e. Local MyMatrix:Float[16].

The matrices are serialized in column-major format, i.e

   1   5   9   13

   2   6   10  14

   3   7   11  15

   4   8   12  16

-----------------------------------------------------------------------
USING IT WITH OPENGL
-----------------------------------------------------------------------

As previously mentioned, ALL of the matrix functinos are deprecated.
This includes glLoadMatrix{fd}(). In addition to this, the fixed-function
pipeline is also deprecated. This means you must use vertex shaders to transform your
vertices. In order to let your shader have access to your transformation
and projection matrices, you must declare appropriate uniforms in your shader
and set them from your OpenGL program with glUniformMatrix4fv(). The first paremeter
is the location of the uniform in your shader (get it with glGetUniformLocation), 
the second parameter is the number of matrices you're modifying (usually one,
unless you're modifying an array of matrices), the third parameter specifies whether or not
you want OpenGL to use the transpose of your matrix (only use this if you've used row-major matrices
for some reason, set it to false if you're using this library), and a pointer to the first
value of your matrix.

-----------------------------------------------------------------------
FUNCTION LIST
-----------------------------------------------------------------------

IdentityMatrix()

SetMatrix()
GetMatrix()

CopyMatrix()
AddMatrix()
SubtractMatrix()
MultiplyMatrix()

TranslationMatrix()
TranslateMatrix()

XRotationMatrix()
YRotationMatrix()
ZRotationMatrix()
XRotateMatrix()
YRotateMatrix()
ZRotateMatrix()

ScalingMatrix()
XYZScalingMatrix()
ScaleMatrix()
XYZScaleMatrix()

RHPerspectiveProjectionMatrix()

ExtractNormalMatrix()

PrintMatrix()

NewVector2()
NewVector3()
NewVector4()

AddVector()
AddVectorScalar()
SubtractVector()
SubtractVectorScalar()
MultiplyVector()
MultiplyVectorScalar()
DivideVector()
DivideVectorScalar()

VectorDotProduct()
VectorCrossProduct()

VectorMagnitude()
NormalizeVector()

ProjectVector()

MultiplyMatrixVector()


----------------------------------------------------------------------------------
Diffference between MatTranslation/Rotation/Scaling and MatTranslate/Rotate/Scale
----------------------------------------------------------------------------------

Some of the functions are similarly named and it may be difficult To know which does what.
MatTranslationMatrix(), Mat*RotationMatrix(), And MatScalingMatrix() build transformation matrices according
To the parameters you provide. However, MatTranslateMatrix(), Mat*RotateMatrix(), And MatScaleMatrix() all operate
on existing matrices--they first build the correct matrix, Then multiply the existing matrix by it. These functions are
similar To what OpenGL does--you only have transformation matrix (ModelView matrix) And you just perform different operations
on it, whereas with the former three functions you have a different matrix returned from each Function, And you have To multiply
them all together.

EndRem


Type GLMath

	'********************************************************************************************
	' MATRICES
	'********************************************************************************************
	Method IdentityMatrix:Float[]()
		Local Mat:Float[16]
		Mat[0] = 1.0
		Mat[5] = 1.0
		Mat[10] = 1.0
		Mat[15] = 1.0
		Return Mat
	EndMethod
	'Sets an element.
	Method SetMatrix(Mat:Float[], Row:Int, Column:Int, Val:Float)
		Mat[Column * 4 + Row] = Val
	EndMethod
	
	'Gets an element.
	
	Method GetMatrix:Float(Mat:Float[], Row:Int, Column:Int)
		Return Mat[Column * 4 + Row]
	EndMethod
	
	'Copies a matrix.
	
	Method CopyMatrix:Float[](Mat:Float[])
		Local Mat2:Float[16]
		MemCopy(Mat2, Mat, SizeOf(Mat))
		Return Mat2
	EndMethod
	
	'Returns Mat1 + Mat2.
	
	Method AddMatrix:Float[](Mat1:Float[], Mat2:Float[])
		Local MatResult:Float[16]
		For Local I:Int = 0 To 15
			MatResult[I] = Mat1[I] + Mat2[I]
		Next
		Return MatResult
	EndMethod
	
	'Returns Mat1 - Mat2.
	
	Method SubtractMatrix:Float[](Mat1:Float[], Mat2:Float[])
		Local MatResult:Float[16]
		For Local I:Int = 0 To 15
			MatResult[I] = Mat1[I] - Mat2[I]
		Next
		Return MatResult
	EndMethod
	
	'Returns Mat1 * Mat2.
	
	Method MultiplyMatrix:Float[](Mat1:Float[], Mat2:Float[])
		Local MatResult:Float[16]
		For Local X:Int = 0 To 3
			For Local Y:Int = 0 To 3
				For Local I:Int = 0 To 3
					MatResult[X * 4 + Y] :+ Mat1[I * 4 + Y] * Mat2[X * 4 + I]
				Next
			Next
		Next
		Return MatResult
	EndMethod
	
	'Builds a translation matrix.
	
	Method TranslationMatrix:Float[](X:Float, Y:Float, Z:Float)
		Local MatResult:Float[16]
		MatResult = IdentityMatrix()
		MatResult[12] = X
		MatResult[13] = Y
		MatResult[14] = Z
		Return MatResult
	EndMethod
	
	'Translates the matrix.
	
	Method TranslateMatrix(Mat:Float[] Var, X:Float, Y:Float, Z:Float)
		Local Mat2:Float[16]
		Mat2 = TranslationMatrix(X, Y, Z)
		Mat = MultiplyMatrix(Mat, Mat2)
	EndMethod
	
	'Builds an x-axis rotation matrix.
	
	Method XRotationMatrix:Float[](Ang:Float)
		Local MatResult:Float[16]
		MatResult = IdentityMatrix()
		SetMatrix(MatResult, 1, 1, Cos(Ang))
		SetMatrix(MatResult, 2, 2, Cos(Ang))
		SetMatrix(MatResult, 2, 1, -Sin(Ang))
		SetMatrix(MatResult, 1, 2, Sin(Ang))
		Return MatResult
	EndMethod
	
	'Build a y-axis rotation matrix.
	
	Method YRotationMatrix:Float[](Ang:Float)
		Local MatResult:Float[16]
		MatResult = IdentityMatrix()
		SetMatrix(MatResult, 0, 0, Cos(Ang))
		SetMatrix(MatResult, 2, 2, Cos(Ang))
		SetMatrix(MatResult, 0, 2, -Sin(Ang))
		SetMatrix(MatResult, 2, 0, Sin(Ang))
		Return MatResult
	EndMethod	
	
	'Builds a z-axis rotation matrix.
	
	Method ZRotationMatrix:Float[](Ang:Float)
		Local MatResult:Float[16]
		MatResult = IdentityMatrix()
		SetMatrix(MatResult, 0, 0, Cos(Ang))
		SetMatrix(MatResult, 1, 1, Cos(Ang))
		SetMatrix(MatResult, 1, 0, -Sin(Ang))
		SetMatrix(MatResult, 0, 1, Sin(Ang))
		Return MatResult
	EndMethod
	
	'Rotates the matrix on the x-axis.
	
	Method XRotateMatrix(Mat:Float[] Var, Ang:Float)
		Local Mat2:Float[16]
		Mat2 = XRotationMatrix(Ang)
		Mat = MultiplyMatrix(Mat, Mat2)
	EndMethod
	
	'Rotates the matrix on the y-axis.
	
	Method YRotateMatrix(Mat:Float[] Var, Ang:Float)
		Local Rot:Float[16]
		Rot = YRotationMatrix(Ang)
		Mat = MultiplyMatrix(Mat, Rot)
	EndMethod
	
	'Rotates the matrix on the z-axis.
	
	Method ZRotateMatrix(Mat:Float[] Var, Ang:Float)
		Local Mat2:Float[16]
		Mat2 = ZRotationMatrix(Ang)
		Mat = MultiplyMatrix(Mat, Mat2)
	EndMethod
	
	'Builds a scaling matrix with the same scale for each axis.
	
	Method ScalingMatrix:Float[](S:Float)
		Local MatResult:Float[16]
		MatResult = IdentityMatrix()
		SetMatrix(MatResult, 0, 0, S)
		SetMatrix(MatResult, 1, 1, S)
		SetMatrix(MatResult, 2, 2, S)
		Return MatResult
	EndMethod
	
	'Builds a scaling matrix, with a different scale for each axis.
	
	Method XYZScalingMatrix:Float[](SX:Float, SY:Float, SZ:Float)
		Local MatResult:Float[16]
		MatResult = IdentityMatrix()
		SetMatrix(MatResult, 0, 0, SX)
		SetMatrix(MatResult, 1, 1, SY)
		SetMatrix(MatResult, 2, 2, SZ)
		Return MatResult
	EndMethod
	
	'Scales a matrix by the same amount on each axis.
	
	Method ScaleMatrix(Mat:Float[] Var, S:Float)
		Local Mat2:Float[16]
		Mat2 = ScalingMatrix(S)
		Mat = MultiplyMatrix(Mat, Mat2)
	EndMethod
	
	'Scales a matrix, with the option of choosing different scales for different axes.
	
	Method XYZScaleMatrix(Mat:Float[] Var, SX:Float, SY:Float, SZ:Float)
		Local Mat2:Float[16]
		Mat2 = XYZScalingMatrix(SX, SY, SZ)
		Mat = MultiplyMatrix(Mat, Mat2)
	EndMethod
	
	'Builds a right-handed perspective projection matrix.
	
	Method RHPerspectiveProjectionMatrix:Float[](FOV:Float, Aspect:Float, ZNear:Float, ZFar:Float)
		Local MatResult:Float[16]
		
		Local Top:Float = ZNear * Tan(FOV / 2.0)
		Local Bottom:Float = -Top
		Local _Right:Float = Top * Aspect
		Local _Left:Float = -_Right
	
		MatResult[0]  = (2 * ZNear) / (_Right - _Left)
		MatResult[1]  = 0
		MatResult[2]  = 0
		MatResult[3]  = 0
		
		MatResult[4]  = 0
		MatResult[5]  = (2 * ZNear) / (Top - Bottom)
		MatResult[6]  = 0
		MatResult[7]  = 0
		
		MatResult[8]  = (_Right + _Left) / (_Right - _Left)
		MatResult[9]  = (Top + Bottom) / (Top - Bottom)
		MatResult[10] = -(ZFar + ZNear) / (ZFar - ZNear)
		MatResult[11] = -1
		
		MatResult[12] = 0
		MatResult[13] = 0
		MatResult[14] = -(2 * ZFar * ZNear) / (ZFar - ZNear)
		MatResult[15] = 0
	
		Return MatResult
	EndMethod
	
	'Extracts the normal matrix from a modelview matrix, i.e. the transpose of the inverse of the upper-left 3x3
	'sub-matrix of our modelview matrix.
	
	Method ExtractNormalMatrix:Float[](Mat:Float[])
		'Our normal matrix. It's 3x3 instead of 4x4.
		Local NM:Float[9]
		
		'Now we set its values to the upper-left corner of our modelview matrix.
		NM[0] = Mat[0]
		NM[1] = Mat[1]
		NM[2] = Mat[2]
		
		NM[3] = Mat[4]
		NM[4] = Mat[5]
		NM[5] = Mat[6]
		
		NM[6] = Mat[8]
		NM[7] = Mat[9]
		NM[8] = Mat[10]
		
		'Now we find the determinant.
		Local Det:Float = ..
			NM[0] * (NM[4] * NM[8] - NM[5] * NM[7]) - ..
			NM[3] * (NM[1] * NM[8] - NM[2]* NM[7]) + ..
			NM[6] * (NM[1] * NM[5] - NM[2] * NM[4])
			
		'Recipricate.
		Local RecDet:Float = 1 / Det
		
		'Now we're going to do two steps in one. We are going
		'calculate the adjugate matrix and multiply it by the reciprocal
		'of the determinant in the same step.
		Local Adj:Float[9]
		Adj[0] = (NM[4] * NM[8] - NM[5] * NM[7]) * RecDet
		Adj[1] = (NM[1] * NM[8] - NM[2] * NM[7]) * RecDet
		Adj[2] = (NM[1] * NM[5] - NM[2] * NM[4]) * RecDet
		
		Adj[3] = (NM[3] * NM[8] - NM[5] * NM[6]) * RecDet
		Adj[4] = (NM[0] * NM[8] - NM[2] * NM[6]) * RecDet
		Adj[5] = (NM[0] * NM[5] - NM[2] * NM[3]) * RecDet
		
		Adj[6] = (NM[3] * NM[7] - NM[4] * NM[6]) * RecDet
		Adj[7] = (NM[0] * NM[7] - NM[1] * NM[6]) * RecDet
		Adj[8] = (NM[0] * NM[4] - NM[1] * NM[3]) * RecDet
		
		'Now we return it.
		Return Adj
	EndMethod
	
	Method PrintMatrix(Mat:Float[])
		Local MatStr:String
		For Local I:Int = 0 To 15
			If I Mod 4 = 0
				MatStr :+ Chr(10)
			EndIf
			MatStr :+ Mat[I]
			If I Mod 4 <> 3
				MatStr :+ ", "
			EndIf
		Next
		Print MatStr
	EndMethod
	'----------------------------------------------------------------------------------------------------------------------------
	'VECTORS
	'----------------------------------------------------------------------------------------------------------------------------
	
	'Creates a 2-dimensional vector.
	
	Method NewVector2:Float[](X:Float, Y:Float)
		Local Vec:Float[2]
		Vec[0] = X
		Vec[1] = Y
		Return Vec
	EndMethod
	
	'Creates a 3-dimensional vector.
	
	Method NewVector3:Float[](X:Float, Y:Float, Z:Float)
		Local Vec:Float[3]
		Vec[0] = X
		Vec[1] = Y
		Vec[2] = Z
		Return Vec
	EndMethod
	
	'Creates a 4-dimensional vector.
	
	Method NewVector4:Float[](X:Float, Y:Float, Z:Float, W:Float)
		Local Vec:Float[4]
		Vec[0] = X
		Vec[1] = Y
		Vec[2] = Z
		Vec[3] = W
		Return Vec
	EndMethod
	
	'Adds two vectors together.
	
	Method AddVector:Float[](V1:Float[], V2:Float[])
		Local VecResult:Float[Len(V1)]
		For Local I:Int = 0 To Len(V1) - 1
			VecResult[I] = V1[I] + V2[I]
		Next
		Return VecResult
	EndMethod
	
	'Adds a scalar.
	
	Method AddVectorScalar:Float[](V:Float[], S:Float)
		Local VecResult:Float[Len(V)]
		For Local I:Int = 0 To Len(V) - 1
			VecResult[I] = V[I] + S
		Next
		Return VecResult
	EndMethod
	
	'Subtracts two vectors.
	
	Method SubtractVector:Float[](V1:Float[], V2:Float[])
		Local VecResult:Float[Len(V1)]
		For Local I:Int = 0 To Len(V1) - 1
			VecResult[I] = V1[I] - V2[I]
		Next
		Return VecResult
	EndMethod
	
	'Subtracts a scalar.
	
	Method SubtractVectorScalar:Float[](V:Float[], S:Float)
		Local VecResult:Float[Len(V)]
		For Local I:Int = 0 To Len(V) - 1
			VecResult[I] = V[I] - S
		Next
		Return VecResult
	EndMethod
	
	'Multiplies by a vector. NOTE: This function just multiplies straight across,
	'if you want to do something different look at VectorDotProduct/CrossProduct.
	
	Method MultiplyVector:Float[](V1:Float[], V2:Float[])
		Local VecResult:Float[Len(V1)]
		For Local I:Int = 0 To Len(V1) - 1
			VecResult[I] = V1[I] * V2[I]
		Next
		Return VecResult
	EndMethod
	
	'Mutliplies by a scalar.
	
	Method MultiplyVectorScalar:Float[](V:Float[], S:Float)
		Local VecResult:Float[Len(V)]
		For Local I:Int = 0 To Len(V) - 1
			VecResult[I] = V[I] * S
		Next
		Return VecResult
	EndMethod
	
	'Divides by a vector. Just divides straight across.
	
	Method DivideVector:Float[](V1:Float[], V2:Float[])
		Local VecResult:Float[Len(V1)]
		For Local I:Int = 0 To Len(V1) - 1
			VecResult[I] = V1[I] / V2[I]
		Next
		Return VecResult
	EndMethod
	
	'Divides by a scalar.
	
	Method DivideVectorScalar:Float[](V:Float[], S:Float)
		Local VecResult:Float[Len(V)]
		For Local I:Int = 0 To Len(V) - 1
			VecResult[I] = V[I] / S
		Next
		Return VecResult
	EndMethod
	
	'Returns the dot product.
	
	Method VectorDotProduct:Float(V1:Float[], V2:Float[])
		Local Result:Float
		For Local I:Int = 0 To Len(V1) - 1
			Result :+ V1[I] * V2[I]
		Next
		Return Result
	EndMethod
	
	'Returns cross product. Vector must be 3D.
	
	Method VectorCrossProduct:Float[](V1:Float[], V2:Float[])
		Local VecResult:Float[3]
		VecResult[0] = V1[1] * V2[2] - V2[1] * V1[2]
		VecResult[1] = V1[2] * V2[0] - V1[0] * V2[2]
		VecResult[2] = V1[0] * V2[1] - V1[1] * V2[0]
		Return VecResult
	EndMethod
	
	'Returns the magnitude of a vector.
	
	Method VectorMagnitude:Float(V:Float[])
		Local SumSq:Float
		For Local I:Int = 0 To Len(V) - 1
			SumSq :+ V[I] * V[I]
		Next
		Return Sqr(SumSq)
	EndMethod
	
	'Returns a normalized vector.
	
	Method NormalizeVector:Float[](V:Float[])
		Local VecResult:Float[Len(V)]
		Local M:Float = VectorMagnitude(V)
		For Local I:Int = 0 To Len(V) - 1
			VecResult[I] = V[I] / M
		Next
		Return VecResult
	EndMethod
	
	'Projects vector A onto vector B.
	
	Method ProjectVector:Float[](V1:Float[], V2:Float[])
		Local VecResult:Float[Len(V1)]
		VecResult = MultiplyVectorScalar(V2, VectorDotProduct(V1, V2) / (VectorMagnitude(V2) ^ 2))
		Return VecResult
	EndMethod
	
	'Multiplies a matrix by a vector and returns the resulting vector.
	'The vector must be 4D.
	
	Method MultiplyMatrixVector:Float[](Mat:Float[], Vec:Float[])
		Local VecResult:Float[4]
		
		For Local E:Int = 0 To 3
			For Local I:Int = 0 To 3
				VecResult[E] :+ Mat[E + I * 4] * Vec[I]
			Next
		Next
		Return VecResult
	EndMethod

EndType
