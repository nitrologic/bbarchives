; ID: 938
; Author: Beeps
; Date: 2004-02-19 14:05:18
; Title: Quaternion Math library
; Description: Most of the functions required to work with quaternions

;Quaternion Maths lib by Beeps

Include "vector.bb"

Type quaternion
	Field n#
	Field vec.vector
	Field magnitude#
End Type

Function quaternion_create.quaternion(n#=0,x#=0,y#=0,z#=0)
	;create and initialize a quaternion, default to 0 values
	result.quaternion = New quaternion
	result\n=n
	result\vec.vector = New vector
	result\vec\x=x
	result\vec\y=y
	result\vec\z=z
	
	Return result
End Function

Function quaternion_magnitude#(quat.quaternion)
	;returns the magnitude of a quaternion, similar to vector magnitude but incorporating 'n'
	quat\magnitude=Sqr(quat\n^2 + quat\vec\x^2 + quat\vec\y^2 + quat\vec\z^2)
	Return quat\magnitude
End Function

Function quaternion_add.quaternion(quat1.quaternion,quat2.quaternion)
	;add 2 quaternions together element by element return the result as a new quaternion
	result.quaternion=quaternion_create(quat1\n+quat2\n,quat1\vec\x+quat2\vec\x,quat1\vec\y+quat2\vec\y,quat1\vec\z+quat2\vec\z)
	
	Return result
End Function

Function quaternion_subtract.quaternion(quat1.quaternion,quat2.quaternion)
	;subtract quart2 from quart1 element by element and return the result as a new quaternion
	result.quaternion=quaternion_create(quat1\n-quat2\n,quat1\vec\x-quat2\vec\x,quat1\vec\y-quat2\vec\y,quat1\vec\z-quat2\vec\z)
	
	Return result
End Function

Function quaternion_scalarMultiply(quat1.quaternion,scale#)
	;multiply a quaternion by a scalar value
	quat1\n=quat1\n*scale
	quat1\vec\x=quat1\vec\x*scale
	quat1\vec\y=quat1\vec\y*scale
	quat1\vec\z=quat1\vec\z*scale
End Function

Function quaternion_scalarDivide(quat1.quaternion,scale#)
	;divide a quaternion by a scalar value
	quat1\n=quat1\n/scale
	quat1\vec\x=quat1\vec\x/scale
	quat1\vec\y=quat1\vec\y/scale
	quat1\vec\z=quat1\vec\z/scale
End Function

Function quaternion_conjugate(quat1.quaternion)
	;conjugate (reverse) the vector part of this quaternion
	oldvec.vector=quat1\vec
	quat1\vec=vector_conjugate.vector(quat1\vec)
	Delete oldvec
End Function

Function quaternion_multiply.quaternion(quat1.quaternion,quat2.quaternion)

	;multiplay 2 quaternions together and return the result as a new quaternion
	result.quaternion = quaternion_create()
	
	result\n=    (quat1\n*quat2\n) - (quat1\vec\x*quat2\vec\x) - (quat1\vec\y*quat2\vec\y) - (quat1\vec\z*quat2\vec\z)
	result\vec\x=(quat1\n*quat2\vec\x) + (quat1\vec\x*quat2\n) + (quat1\vec\y*quat2\vec\z) - (quat1\vec\z*quat2\vec\y)
	result\vec\y=(quat1\n*quat2\vec\y) + (quat1\vec\y*quat2\n) + (quat1\vec\z*quat2\vec\x) - (quat1\vec\x*quat2\vec\z)
	result\vec\z=(quat1\n*quat2\vec\z) + (quat1\vec\z*quat2\n) + (quat1\vec\x*quat2\vec\y) - (quat1\vec\y*quat2\vec\x)

	Return result
End Function

Function quaternion_multiplyVector.quaternion(quat1.quaternion,vec1.vector)
	;multiply a quaternion by a vector and return the result as a new quaternion
	result.quaternion=quaternion_create()
	
	result\n=  -(quat1\vec\x*vec1\x + quat1\vec\y*vec1\y + quat1\vec\z*vec1\z)
	result\vec\x=quat1\n*vec1\x     + quat1\vec\y*vec1\z - quat1\vec\z*vec1\y
	result\vec\y=quat1\n*vec1\y     + quat1\vec\z*vec1\x - quat1\vec\x*vec1\z
	result\vec\z=quat1\n*vec1\z     + quat1\vec\x*vec1\y - quat1\vec\y*vec1\x
	
	Return result
End Function

Function quaternion_getAngle#(quat1.quaternion)
	;Return the angle of rotation about the axis represented by the vector part of quart1
	Return (2*ACos (quat1\n))
End Function

Function quaternion_getAxis.vector(quat1.quaternion)
	;return the unit vector along the axis of rotation represented by the vector part of quat1
	tempvec.vector=New vector
	
	tempvec\x=quat1\vec\x
	tempvec\y=quat1\vec\y
	tempvec\z=quat1\vec\z

	
	m#=vector_calcMagnitude(tempvec)
	
	If m>vector_tol Then
		tempvec\x=tempvec\x/m
		tempvec\y=tempvec\y/m
		tempvec\z=tempvec\z/m
	EndIf

	Return tempvec
End Function

Function quaternion_rotate.quaternion(quat1.quaternion,quat2.quaternion)
	;rotate quat1 by quat2 and return the result as a new quaternion

	conjquat1.quaternion=quaternion_create(quat1\n,quat1\vec\x,quat1\vec\y,quat1\vec\z)
	quaternion_conjugate(conjquat1)

	result.quaternion=quaternion_multiply(quat1,quat2)
	result=quaternion_multiply(result,conjquat1)
	
        Delete conjquat1

	Return result
End Function

Function quaternion_createFromAngles.quaternion(x#,y#,z#)
	;create a quaternion from specified x,y,z angles (pitch,yaw,roll)
	roll#=degreesToRadians(x)
	pitch#=degreesToRadians(y)
	yaw#=degreesToRadians(z)
	
	cyaw#=Cos(0.5*yaw)
	cpitch#=Cos(0.5*pitch)
	croll=Cos(0.5*roll)
	syaw#=Sin(0.5*yaw)
	spitch=Sin(0.5*pitch)
	sroll=Sin(0.5*roll)
	
	cyawcpitch#=cyaw*cpitch
	syawspitch#=syaw*spitch
	cyawspitch#=cyaw*spitch
	syawcpitch#=syaw*cpitch
	
	result.quaternion=quaternion_create()
	result\n=    cyawcpitch*croll + syawspitch*sroll
	result\vec\x=cyawcpitch*sroll - syawspitch*croll 
	result\vec\y=cyawspitch*croll + syawcpitch*sroll
	result\vec\z=syawcpitch*croll - cyawspitch*sroll
	
	Return result
End Function

Function quaternion_getAngles.vector(quat1.quaternion)
	;return the yaw pitch and roll for a quaternion as a vector
	result.vector=vector_create(0,0,0)
	
	q00=quat1\n^2
	q11=quat1\vec\x^2
	q22=quat1\vec\y^2
	q33=quat1\vec\z^2
	
	r11=q00 + q11 - q22 - q33
	r21=2*(quat1\vec\x*quat1\vec\y + quat1\n*quat1\vec\z)
	r31=2*(quat1\vec\x*quat1\vec\z + quat1\n*quat1\vec\y)
	r32=2*(quat1\vec\y*quat1\vec\z + quat1\n*quat1\vec\x)
	r33=q00-q11-q22+q33
	
	tmp=Abs(r31)
	
	If tmp>0.999999 Then
		r12=2*(quat1\vec\x*quat1\vec\y + quat1\n*quat1\vec\z)
		r13=2*(quat1\vec\x*quat1\vec\z + quat1\n*quat1\vec\y)
		
		result\x=radiansToDegrees(0)
		result\y=radiansToDegrees(-(Pi/2)*r31/tmp)
		result\z=radiansToDegrees(ATan2(-r12,-r31*r13))
	Else
		
		result\x=radiansToDegrees(ATan2(r32,r33))
		result\y=radiansToDegrees(ASin(-r31))
		result\z=radiansToDegrees(ATan2(r21,r11))
	EndIf
		
	Return result
End Function

Function radiansToDegrees(rad#)
	Return rad * 180.0 / Pi
End Function

Function degreesToRadians(deg#)
	Return deg * Pi /180.0
End Function
