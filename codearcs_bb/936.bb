; ID: 936
; Author: Beeps
; Date: 2004-02-19 10:56:15
; Title: Vector Math library
; Description: A simple blitz include that gives you most of the vector maths functions.

;vector maths lib by Beeps

;used to normalize vector
Const vector_tol#=0.0001

Type vector
	Field x#
	Field y#
	Field z#
	Field magnitude#
End Type

Function vector_createVector.vector(vx#,vy#,vz#)
	;create a populated vector and return
	vec.vector=New vector
	vec\x=vx
	vec\y=vy
	vec\z=vz
	Return vec
End Function

Function vector_calcMagnitude(vec.vector)
	;calc the magnitude of a vector and stor in vector
	vec\magnitude=Sqr(vec\x*vec\x + vec\y*vec\y + vec\z*vec\z) 
End Function

Function vector_normalize(vec.vector)
	;normalize a vector so it's length = 1 and apply tolerance based on our constant tolerance value
	m#=Sqr(Sqr(vec\x*vec\x + vec\y*vec\y + vec\z*vec\z) )
	If m<= vector_tol Then m=1
	vec\x=vec\x/m
	vec\y=vec\y/m
	vec\z=vec\z/m
	If Abs(vec\x)<vector_tol Then vec\x=0
	If Abs(vec\y)<vector_tol Then vec\y=0
	If Abs(vec\z)<vector_tol Then vec\z=0
End Function	

Function vector_reverse(vec.vector)
	;reverse a vector
	vec\x=-vec\x
	vec\y=-vec\y
	vec\z=-vec\z
End Function

Function vector_add.vector(vec1.vector,vec2.vector)
	;add two vectors together and return the resulting vector
	result.vector=New vector
	
	result\x=vec1\x+vec2\x
	result\y=vec1\y+vec2\y
	result\z=vec1\z+vec2\z
	
	Return result
End Function

Function vector_subtract.vector(vec1.vector,vec2.vector)
	;subtract vec1 from vec2 and return the resulting vector
	result.vector=New vector
	
	result\x=vec1\x-vec2\x
	result\y=vec1\y-vec2\y
	result\z=vec1\z-vec2\z
	
	Return result
End Function

Function vector_scalarMultiply.vector(vec1.vector,scale#)
	;scalar multiplication of a vector with result returned as new vector
	;used to scale a vector by 'scale'
	result.vector=New vector
	result\x=vec1\x*scale
	result\y=vec1\y*scale
	result\z=vec1\z*scale
	Return result
End Function

Function vector_scalarDivision.vector(vec1.vector,scale#)
	;scalar division of a vector with result returned as new vector
	;used to scale a vector
	result.vector=New vector
	result\x=vec1\x/scale
	result\y=vec1\y/scale
	result\z=vec1\z/scale
	Return result
End Function

Function vector_conjugate.vector(vec1.vector)
	;conjugate operator takes the negative of each vector component
	;can be used when subtracting one vector from another or for
	;reversing the direction of a vector.
	;applying conjugate is the same as reversing a vector
	;returns a new vector
	result.vector = New vector
	result\x=-vec1\x
	result\y=-vec1\y
	result\z=-vec1\z
	Return result
End Function

Function vector_crossProduct.vector(vec1.vector,vec2.vector)
	;takes vec1 and vec2 and returns the cross product vec1 X vec2
	;the cross product is a vector perpendicular to both vec1 and vec2
	;this is the normal of 2 vectors
	result.vector=New vector
	
	result\x=(vec1\y*vec2\z) - (vec1\z*vec2\y)
	result\y=(vec1\z*vec2\x) - (vec1\x*vec2\z) 
	result\z=(vec1\x*vec2\y) - (vec1\y*vec2\x)

	Return result
End Function

Function vector_dotProduct#(vec1.vector,vec2.vector)
	;calculate and return the dot product of 2 vectors (distance)
	result#=(vec1\x*vec2\x)+(vec1\y*vec2\y)+(vec1\z*vec2\z)
	Return result
End Function

Function vector_tripleScalarProduct#(vec1.vector,vec2.vector,vec3.vector)
	;calculate the triple scalar function and return it
	result#=          vec1\x * ( (vec2\y*vec3\z ) - (vec2\z * vec3\y) ) 
	result=result + ( vec1\y * ( (-vec2\x*vec3\z) + (vec2\z * vec3\x) ) ) 
	result=result + ( vec1\z * ( ( vec2\x*vec3\y) + (vec2\y * vec3\x) ) )
	Return result
End Function
