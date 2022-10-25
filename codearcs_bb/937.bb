; ID: 937
; Author: Beeps
; Date: 2004-02-19 11:58:17
; Title: Matrix Math library
; Description: Matrix maths library including most of the functions you might use.

;3x3 matrix library by Beeps

Include "vector.bb"

Type matrix
	Field element#[9]
End Type

Function matrix_getElement(mat.matrix,x,y)
	Return mat\element[ (x*3)+y ]
End Function

Function matrix_setElement(mat.matrix,x,y,value)
	mat\element[ (x*3) + y ] = value
End Function

Function matrix_createLoadedMatrix.matrix(r1c1#,r1c2#,r1c3#,r2c1#,r2c2#,r2c3#,r3c1#,r3c2#,r3c3#)
	;create a new populated matrix and return it
	mat.matrix=New matrix
	
	mat\element[0]=r1c1
	mat\element[1]=r1c2
	mat\element[2]=r1c3
	mat\element[3]=r2c1
	mat\element[4]=r2c2
	mat\element[5]=r2c3
	mat\element[6]=r3c1
	mat\element[7]=r3c2
	mat\element[8]=r3c3

	Return mat
End Function

Function matrix_determinant#(mat.matrix)
	;returns the determinant of a matrix
	result#= mat\element[0] * mat\element[4] * mat\element[8]
	result=result - (mat\element[0] * mat\element[7] * mat\element[5] )
	result=result + (mat\element[3] * mat\element[7] * mat\element[2] )
	result=result - (mat\element[3] * mat\element[1] * mat\element[8] )
	result=result + (mat\element[6] * mat\element[1] * mat\element[5] )
	result=result - (mat\element[6] * mat\element[4] * mat\element[2] )

	Return result
End Function

Function matrix_transpose.matrix(mat.matrix)
	;transposes a matrix (swaps columns for rows) and returns the result as a new matrix
	trans.matrix=New matrix
	trans\element[0]=mat\element[0]
	trans\element[1]=mat\element[3]
	trans\element[2]=mat\element[6]
	trans\element[3]=mat\element[1]
	trans\element[4]=mat\element[4]
	trans\element[5]=mat\element[7]
	trans\element[6]=mat\element[2]
	trans\element[7]=mat\element[5]
	trans\element[8]=mat\element[8]
	Return trans
End Function 

Function matrix_inverse.matrix(mat.matrix)
	;inverses a matrix and returns it as a new matrix
	inverse.matrix=New matrix
	
	a#=    mat\element[0]*mat\element[4]*mat\element[8]
	a=a - (mat\element[0]*mat\element[7]*mat\element[5])
	a=a + (mat\element[3]*mat\element[7]*mat\element[2])
	a=a - (mat\element[3]*mat\element[1]*mat\element[8])
	a=a + (mat\element[6]*mat\element[1]*mat\element[5])
	a=a - (mat\element[6]*mat\element[4]*mat\element[2])
	
	If a=0 Then a=1
	
	inverse\element[0]= ( (mat\element[4]*mat\element[8]) - (mat\element[5]*mat\element[7]) )/a
	inverse\element[1]=-( (mat\element[1]*mat\element[8]) - (mat\element[2]*mat\element[7]) )/a
	inverse\element[2]= ( (mat\element[1]*mat\element[5]) - (mat\element[2]*mat\element[4]) )/a
	inverse\element[3]=-( (mat\element[3]*mat\element[8]) - (mat\element[5]*mat\element[6]) )/a
	inverse\element[4]= ( (mat\element[0]*mat\element[8]) - (mat\element[2]*mat\element[6]) )/a
	inverse\element[5]=-( (mat\element[0]*mat\element[5]) - (mat\element[2]*mat\element[3]) )/a
	inverse\element[6]= ( (mat\element[3]*mat\element[7]) - (mat\element[4]*mat\element[6]) )/a
	inverse\element[7]=-( (mat\element[0]*mat\element[7]) - (mat\element[1]*mat\element[6]) )/a
	inverse\element[8]= ( (mat\element[0]*mat\element[4]) - (mat\element[1]*mat\element[3]) )/a
	
	Return inverse
End Function

Function matrix_add.matrix(mat1.matrix,mat2.matrix)
	;adds matrix2 to matrix1 and returns the result as a new matrix
	result.matrix=New matrix
	
	For i=0 To 8
		result\element[i] = mat1\element[i] + mat2\element[i]
	Next
	
	Return result
End Function

Function matrix_subtract.matrix(mat1.matrix,mat2.matrix)
	;subtracts mat2 from mat1 and returns the resultant matrix
	result.matrix=New matrix
	
	For i=0 To 8
		result\element[i] = mat1\element[i] - mat2\element[i]
	Next
	
	Return result
End Function

Function matrix_scalarMultiply.matrix(mat1.matrix,scale#)
	;multiplies each element in a matrix by a scalar value
	For i=0 To 8
		mat1\element[i]=mat1\element[i]*scale
	Next
End Function

Function matrix_scalarDivision.matrix(mat1.matrix,scale#)
	;divides each element in a matrix by a scalar value
	For i=0 To 8
		mat1\element[i]=mat1\element[i]/scale
	Next
End Function

Function matrix_Multiply.matrix(mat1.matrix,mat2.matrix)
	;multiply mat1 by mat2 and return the resultant matrix 
	result.matrix=New matrix
	
	result\element[0]= (mat1\element[0]*mat2\element[0]) + (mat1\element[1]*mat2\element[3]) + (mat1\element[2]*mat2\element[6])
	result\element[1]= (mat1\element[0]*mat2\element[1]) + (mat1\element[1]*mat2\element[4]) + (mat1\element[2]*mat2\element[7])
	result\element[2]= (mat1\element[0]*mat2\element[2]) + (mat1\element[1]*mat2\element[5]) + (mat1\element[2]*mat2\element[8])
	
	result\element[3]= (mat1\element[3]*mat2\element[0]) + (mat1\element[4]*mat2\element[3]) + (mat1\element[5]*mat2\element[6])
	result\element[4]= (mat1\element[3]*mat2\element[1]) + (mat1\element[4]*mat2\element[4]) + (mat1\element[5]*mat2\element[7])
	result\element[5]= (mat1\element[3]*mat2\element[2]) + (mat1\element[4]*mat2\element[5]) + (mat1\element[5]*mat2\element[8])

	result\element[6]= (mat1\element[6]*mat2\element[0]) + (mat1\element[7]*mat2\element[3]) + (mat1\element[8]*mat2\element[6])
	result\element[7]= (mat1\element[6]*mat2\element[1]) + (mat1\element[7]*mat2\element[4]) + (mat1\element[8]*mat2\element[7])
	result\element[8]= (mat1\element[6]*mat2\element[2]) + (mat1\element[7]*mat2\element[5]) + (mat1\element[8]*mat2\element[8])

	Return result
End Function

Function matrix_multiplyVector.vector(mat1.matrix,vec1.vector)
	;multiply a vector by a matrix and return the resulting vector
	result.vector=New vector
	
	result\x=(mat1\element[0]*vec1\x) + (mat1\element[1]*vec1\y) + mat1\element[2]*vec1\z
	result\y=(mat1\element[3]*vec1\x) + (mat1\element[4]*vec1\y) + mat1\element[5]*vec1\z
	result\z=(mat1\element[6]*vec1\x) + (mat1\element[7]*vec1\y) + mat1\element[8]*vec1\z
	
	Return result
End Function
