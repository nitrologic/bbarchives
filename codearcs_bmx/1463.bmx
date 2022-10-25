; ID: 1463
; Author: Nilium
; Date: 2005-09-15 23:07:31
; Title: Cower.Math3D
; Description: 3D Math code (Matrix, Vector3, Quaternion, Vector2, Rectangle)

Rem
	This software is written by Noel R. Cower
	<hooker.with.a.penis@gmail.com>
	
	Copyright (C) 2005 Noel Raymond Cower
EndRem

Rem bbdoc: 3D Maths
EndRem
Module Cower.Math3D

Import Brl.Math

Strict

Private

Const RAD_TO_DEGREE! = 180!/Pi
Const DEGREE_TO_RAD! = Pi/180!

Public

Rem bbdoc: Converts degrees to radians
End Rem
Function DegreeToRad!( d! )
	Return d*DEGREE_TO_RAD
End Function

Rem bbdoc: Converts radians to degrees
End Rem
Function RadToDegree!( r! )
	Return r*RAD_TO_DEGREE
End Function

Rem bbdoc: Gets the cotangent of an angle
End Rem
Function Cotangent!( A! )
	Return Tan( 90! - A )
End Function

Rem bbdoc: Gets the nearest power of two to an integer
End Rem
Function NearestPower:Int( i:Int )
	Local q:Int,r:Int=1
	While Not ( i < r And i > q )
		q=r
		r=q*2
	Wend
	Local da:Int=i-q
	Local db:Int=r-i
	If da > db Then
		Return r
	Else
		Return q
	EndIf
End Function

Rem bbdoc: Gets the slope of two points
End Rem
Function Slope!( x1!, y1!, x2!, y2! )
	Local m! = ( y1-y2 )/( x1-x2 )
	If m <= .0001! And m > 0! Then Return .0001!
	If m >= -.0001! And m < 0! Then Return -.0001!
	Return m
End Function

Rem bbdoc: Gets the Y intercept of two points
End Rem
Function YIntercept!( x1!, y1!, x2!, y2! )
	Return y1 - ( Slope( x1, y1, x2, y2 ) * x1 )
End Function

Rem bbdoc: Gets the Y value of X along the line made by two points
End Rem
Function ReturnedY!( x!, x1!, y1!, x2!, y2! )
	Return Slope( x1, y1, x2, y2 ) * x + YIntercept( x1, y1, x2, y2 )
End Function

Rem bbdoc: 4-by-4 homogenous matrix class
about:
Keep in mind that aside from Translate and Scale, all methods
will return a new matrix with the requested operations performed.
End Rem
Type Matrix
	Field m00! = 1, m01! = 0, m02! = 0, m03! = 0
	Field m10! = 0, m11! = 1, m12! = 0, m13! = 0
	Field m20! = 0, m21! = 0, m22! = 1, m23! = 0
	Field m30! = 0, m31! = 0, m32! = 0, m33! = 1
	
	Rem bbdoc: Copies the Matrix class
	End Rem
	Method Copy:Matrix( )
		Local i:Matrix = New Matrix
		MemCopy( Varptr i.m00, Varptr m00, 64 )
		Return i
	End Method
	
	Rem bbdoc: Sets the translation elements of the matrix
	End Rem
	Method Translate( x!, y!, z! )
		m03 = x
		m13 = y
		m23 = z
	End Method
	
	Rem bbdoc: Gets the translation elements of the matrix
	End Rem
	Method GetTranslation( x! Var, y! Var, z! Var )
		x = m03
		y = m13
		z = m23
	End Method
	
	Rem bbdoc: Scales the rotation elements of the matrix
	End Rem
	Method Scale( x!, y!, z! )
		m00 :* x
		m10 :* x
		m20 :* x
		
		m01 :* y
		m11 :* y
		m21 :* y
		
		m02 :* z
		m12 :* z
		m22 :* z
	End Method
	
	Rem bbdoc: Transforms the matrix by another matrix
	End Rem
	Method TransformMat:Matrix( i:Matrix )
		Local r:Matrix = New Matrix
		
		r.m00 = m00 * i.m00 + m01 * i.m10 + m02 * i.m20 + m03 * i.m30
		r.m01 = m00 * i.m01 + m01 * i.m11 + m02 * i.m21 + m03 * i.m31
		r.m02 = m00 * i.m02 + m01 * i.m12 + m02 * i.m22 + m03 * i.m32
		r.m03 = m00 * i.m03 + m01 * i.m13 + m02 * i.m23 + m03 * i.m33
		
		r.m10 = m10 * i.m00 + m11 * i.m10 + m12 * i.m20 + m13 * i.m30
		r.m11 = m10 * i.m01 + m11 * i.m11 + m12 * i.m21 + m13 * i.m31
		r.m12 = m10 * i.m02 + m11 * i.m12 + m12 * i.m22 + m13 * i.m32
		r.m13 = m10 * i.m03 + m11 * i.m13 + m12 * i.m23 + m13 * i.m33
		
		r.m20 = m20 * i.m00 + m21 * i.m10 + m22 * i.m20 + m23 * i.m30
		r.m21 = m20 * i.m01 + m21 * i.m11 + m22 * i.m21 + m23 * i.m31
		r.m22 = m20 * i.m02 + m21 * i.m12 + m22 * i.m22 + m23 * i.m32
		r.m23 = m20 * i.m03 + m21 * i.m13 + m22 * i.m23 + m23 * i.m33
		
		r.m30 = m30 * i.m00 + m31 * i.m10 + m32 * i.m20 + m33 * i.m30
		r.m31 = m30 * i.m01 + m31 * i.m11 + m32 * i.m21 + m33 * i.m31
		r.m32 = m30 * i.m02 + m31 * i.m12 + m32 * i.m22 + m33 * i.m32
		r.m33 = m30 * i.m03 + m31 * i.m13 + m32 * i.m23 + m33 * i.m33
		
		Return r
	End Method
	
	Rem bbdoc: Transforms a vector by the matrix
	End Rem
	Method TransformVec:Vector3( i:Vector3, addTranslation:Int = 0 )
		Local r:Vector3 = New Vector3
		Local w! = 1.0/( m30 + m31 + m32 + m33 )
		
		addTranslation = Min( Max( addTranslation, 0 ), 1 )
		
		r.x = ( ( m00*i.x ) + ( m01*i.y ) + ( m02*i.z ) + m03 * addTranslation ) * w
		r.y = ( ( m10*i.x ) + ( m11*i.y ) + ( m12*i.z ) + m13 * addTranslation ) * w
		r.z = ( ( m20*i.x ) + ( m21*i.y ) + ( m22*i.z ) + m23 * addTranslation ) * w
		
		Return r
	End Method
	
	Rem bbdoc: Adds two matrices
	End Rem
	Method Add:Matrix( i:Matrix )
		Local a:Double Ptr = GetPtr( )
		Local b:Double Ptr = GetPtr( )
		Local r:Matrix = New Matrix
		Local c:Double Ptr = r.GetPtr( )
		For Local n:Int = 0 To 15
			c[n]=a[n]+b[n]
		Next
		Return r
	End Method
	
	Rem bbdoc: Subtracts two matrices
	End Rem
	Method Subtract:Matrix( i:Matrix )
	 Local a:Double Ptr = GetPtr( )
		Local b:Double Ptr = GetPtr( )
		Local r:Matrix = New Matrix
		Local c:Double Ptr = r.GetPtr( )
		For Local n:Int = 0 To 15
			c[n]=a[n]-b[n]
		Next
		Return r
	End Method
	
	Rem bbdoc: Transposes the matrix
	End Rem
	Method Transpose:Matrix( )
	 Local x:Int,y:Int
	 Local r:Matrix = New Matrix
	 Local a:Double Ptr = GetPtr( )
	 Local b:Double Ptr = r.GetPtr( )
	 For x = 0 To 3
		 For y = 0 To 3
			 b[x*4+y] = a[y*4+x]
		 Next
	 Next
	 Return r
	End Method
	
	Rem bbdoc: Returns a pointer to the first matrix element
	End Rem
	Method GetPtr:Double Ptr( )
		Return Varptr m00
	End Method
	
	Rem bbdoc: Returns an array made from the elements of the matrix
	End Rem
	Method ToArray:Double[]( )
		Local r:Double[16]
		MemCopy( Varptr r[0], GetPtr( ), 64 )
		Return r
	End Method
	
	Rem bbdoc: Creates a matrix from an array of Doubles
	End Rem
	Function FromArray:Matrix( arr:Double[] )
		Return FromPtr( Varptr arr[0] )
	End Function
	
	Rem bbdoc: Creates an array from a pointer to an array of Doubles
	End Rem
	Function FromPtr:Matrix( arr:Double Ptr )
		Local r:Matrix = New Matrix
		Local p:Double Ptr = r.GetPtr( )
		MemCopy( p, arr, 64 )
		Return r
	End Function
End Type

Rem bbdoc: Three-component vector class
about:
Keep in mind that aside from Dot, Magnitude, Normalize, Floor, and Ceil,
all methods will return a new Vector3 with the requested operations performed.
End Rem
Type Vector3
	Rem bbdoc: X component
	End Rem
	Field x!
	Rem bbdoc: Y component
	End Rem
	Field y!
	Rem bbdoc: Z component
	End Rem
	Field z!
	
	Rem bbdoc: Adds a vector
	End Rem
	Method Add:Vector3( i:Vector3 )
		Local r:Vector3 = New Vector3
		r.x = x+i.x
		r.y = y+i.y
		r.z = z+i.z
		Return r
	End Method
	
	Rem bbdoc: Subtracts a vector
	End Rem
	Method Subtract:Vector3( i:Vector3 )
		Local r:Vector3 = New Vector3
		r.x = x-i.x
		r.y = y-i.y
		r.z = z-i.z
		Return r
	End Method
	
	Rem bbdoc: Multiplies a vector with another vector
	End Rem
	Method Multiply:Vector3( i:Vector3 )
		Local r:Vector3 = New Vector3
		r.x = x*i.x
		r.y = y*i.y
		r.z = z*i.z
		Return r
	End Method
	
	Rem bbdoc: Divides a vector by another vector
	End Rem
	Method Divide:Vector3( i:Vector3 )
		Local r:Vector3 = New Vector3
		r.x = x/i.x
		r.y = y/i.y
		r.z = z/i.z
		Return r
	End Method
	
	Rem bbdoc: Scales a vector by a scalar
	End Rem
	Method Scale:Vector3( i:Double )
		Local r:Vector3 = New Vector3
		r.x = x*i
		r.y = y*i
		r.z = z*i
		Return r
	End Method
	
	Rem bbdoc: Gets the dot product of two vectors (Self and another)
	End Rem
	Method Dot:Double( i:Vector3 )
		Return x*i.x+y*i.y+z*i.z
	End Method
	
	Rem bbdoc: Gets the magnitude of the vector
	End Rem
	Method Magnitude:Double( )
		Return Sqr( x*x + y*y + z*z )
	End Method
	
	Rem bbdoc: Normalizes the vector
	End Rem
	Method Normalize( )
		Local s:Double = 1.0 / Magnitude( )
		x:*s
		y:*s
		z:*s
	End Method
	
	Rem bbdoc: Gets the cross product of two vectors (Self and another)
	End Rem
	Method Cross:Vector3( i:Vector3 )
		Local r:Vector3 = New Vector3
		r.x = y*i.z - z*i.y
		r.y = x*i.z - z*i.x
		r.z = x*i.y - y*i.x
		Return r
	End Method
	
	Rem bbdoc: Returns a reflection vector
	End Rem
	Method Reflect:Vector3( i:Vector3 )
		Local f:Double = 2*Dot( i )
		Return Subtract( i.Scale( f ) )
	End Method
	
	Rem bbdoc: Returns a quaternion containing the rotation between two vectors
	End Rem
	Method RotationTo:Quaternion( dest:Vector3 )
		' Based on the Axiom engine's Vector3.GetRotationTo method code
		' Which is in turn based on Stan Melax's article in Game Programming Gems
		Local q:Quaternion = New Quaternion
		
		Local v0:Vector3 = Vector3.Create( x, y, z )
		Local v1:Vector3 = New Vector3
	 
		Local c:Vector3 = v0.Cross( v1 )
		Local d:Double = v0.Dot( v1 )
		
		If d >= 1.0 Then Return New Quaternion
	 
		Local s:Double = Sqr( ( 1+d ) * 2 )
		Local inverse:Double = 1.0 / s
		
		q.x = c.x * inverse
		q.y = c.y * inverse
		q.z = c.z * inverse
		q.w = s*.5
	 
		Return q
	End Method
	
	Rem bbdoc: Floors a vector
	End Rem
	Method Floor( i:Vector3 )
		If i.x < x Then x = i.x
		If i.y < y Then y = i.y
		If i.z < z Then z = i.z
	End Method
	
	Rem bbdoc: Ceils a vector
	End Rem
	Method Ceil( i:Vector3 )
		If i.x > x Then x = i.x
		If i.y > y Then y = i.y
		If i.z > z Then z = i.z
	End Method
	
	Rem bbdoc: Returns a pointer to the first component of the vector
	End Rem
	Method GetPtr:Double Ptr( )
		Return Varptr x
	End Method
	
	Rem bbdoc: Converts the vector to a Double array
	End Rem
	Method ToArray:Double[]( )
		Local r:Double[3]
		MemCopy( Varptr r[0], GetPtr( ), 12 )
		Return r
	End Method
	
	Rem bbdoc: Copies the Vector3 class
	End Rem
	Method Copy:Vector3( )
		Local i:Vector3 = New Vector3
		MemCopy( i.GetPtr( ), GetPtr( ), 12 )
		Return i
	End Method
	
	Rem bbdoc: Creates a new vector
	End Rem
	Function Create:Vector3( x!, y!, z! )
		Local i:Vector3 = New Vector3
		i.x = x
		i.y = y
		i.z = z
		Return i
	End Function
	
	Rem bbdoc: Creates a vector from an array of Doubles
	End Rem
	Function FromArray:Vector3( arr:Double[] )
		Return FromPtr( Varptr arr[0] )
	End Function
	
	Rem bbdoc: Creates a vector from a pointer to a Double array
	End Rem
	Function FromPtr:Vector3( arr:Double Ptr )
		Local r:Vector3 = New Vector3
		Local p:Double Ptr = r.GetPtr( )
		MemCopy( p, arr, 12 )
		Return r
	End Function
End Type

Rem bbdoc: Quaternion class.
about:
A lot of code in this class is based off of that in the
<a href="http://www.axiom3d.org">Axiom engine</a>.<br/><br/>
Aside from Magnitude, Normalize, and Dot, all methods will
return a new Quaternion with the requested operations performed.
End Rem
Type Quaternion
	Rem bbdoc: W component
	End Rem
	Field w!=1
	Rem bbdoc: X component
	End Rem
	Field x!=0
	Rem bbdoc: Y component
	End Rem
	Field y!=0
	Rem bbdoc: Z component
	End Rem
	Field z!=0
	
	Rem bbdoc: Gets the magnitude of the quaternion
	End Rem
	Method Magnitude:Double( )
		Return Sqr( w*w + x*x + y*y + z*z )
	End Method
	
	Rem bbdoc: Normalizes a quaternion
	End Rem
	Method Normalize( )
		Local s:Double = 1.0 / Magnitude( )
		w:*s
		x:*s
		y:*s
		z:*s
	End Method
	
	Rem bbdoc: Multiplies a quaternion
	End Rem
	Method MultiplyQuat:Quaternion( i:Quaternion )
		Local r:Quaternion = New Quaternion
		
		r.w = w*i.w - x*i.x - y*i.y - z*i.z
		r.x = w*i.x - x*i.w - y*i.z - z*i.y
		r.y = w*i.y - y*i.w - z*i.x - x*i.z
		r.z = w*i.z - z*i.w - x*i.y - y*i.x
		
		Return r
	End Method
	
	Rem bbdoc: Multiplies a vector
	End Rem
	Method MultiplyVec:Vector3( i:Vector3 )
		Local a:Vector3,b:Vector3,c:Vector3=Vector3.FromArray( [x,y,z] )
		a=c.Cross( i )
		b=c.Cross( a )
		a.Scale( 2*w )
		b.Scale( 2 )
		
		Return i.Add( a.Add( b ) )
	End Method
	
	Rem bbdoc: Scales the quaternion
	End Rem
	Method Scale:Quaternion( f:Double )
		Local r:Quaternion = New Quaternion
		r.w = w*f
		r.x = x*f
		r.y = y*f
		r.z = z*f
		Return r
	End Method
	
	Rem bbdoc: Adds the quaternion
	End Rem
	Method Add:Quaternion( i:Quaternion )
		Local r:Quaternion = New Quaternion
		r.w = w+i.w
		r.x = x+i.x
		r.y = y+i.y
		r.z = z+i.z
		Return r
	End Method
	
	Rem bbdoc: Subtracts the quaternion
	End Rem
	Method Subtract:Quaternion( i:Quaternion )
		Local r:Quaternion = New Quaternion
		r.w = w+i.w
		r.x = x+i.x
		r.y = y+i.y
		r.z = z+i.z
		Return r
	End Method
	
	Rem bbdoc: Gets the dot product of two quaternions (Self and another)
	End Rem
	Method Dot:Double( i:Quaternion )
		Return w*i.w + x*i.x + y*i.y + z*i.z
	End Method
	
	Rem bbdoc: Gets the quaternion slerp of two quaternions
	End Rem
	Function Slerp:Quaternion( time:Double, a:Quaternion, b:Quaternion, useShortest = False )
		' Based off of code in Axiom
		Local cs:Double = a.Dot( b )
		Local angle:Double = ACos( cs )
		
		If Abs(angle) < .0001 Then Return a.Copy( )
		
		Local sn:Double = Sin( angle )
		Local iSin:Double = 1.0/sn
		Local co1:Double = Sin( ( 1.0-time ) * angle ) * iSin
		Local co2:Double = Sin( time * angle ) * iSin
		
		Local r:Quaternion
		
		If cs < .0 And useShortest > 0 Then
			co1 = -co1
			r = a.Scale( co1 ).Add( b.Scale( co2 ) )
			r.Normalize( )
		Else
			r = a.Scale( co1 ).Add( b.Scale( co2 ) )
		EndIf
		
		Return r
	End Function
	
	Rem bbdoc: Creates a quaternion from an angle and an axis
	End Rem
	Function FromAngleAxis:Quaternion( a:Double, ax:Vector3 )
		' Based off of code in Axiom
		Local r:Quaternion = New Quaternion
		
		Local ha:Double = .5*a
		Local sn:Double = Sin( ha )
		
		r.w = Cos( ha )
		r.x = sn*ax.x
		r.y = sn*ax.y
		r.z = sn*ax.z
		
		Return r
	End Function
	
	Rem bbdoc: Blank
	End Rem
	Function Squad:Quaternion( t:Double, p:Quaternion, a:Quaternion, b:Quaternion, q:Quaternion, useShortest = False )
		' Based off of code in Axiom
		Local time:Double = 2*t*( 1.0-t )
		
		Local slerpA:Quaternion = Slerp( t, p, q, useShortest )
		Local slerpB:Quaternion = Slerp( t, a, b )
		
		Return Slerp( time, slerpA, slerpB )
	End Function
	
	Rem bbdoc: Sets @angle to the angle of the quaternion and @ax to the axis.
	End Rem
	Method ToAngleAxis( angle:Double Var, ax:Vector3 Var )
		' Based off of code in Axiom
		Local sqrLen:Double = x*x + y*y + z*z
		
		If sqrLen > 0 Then
			angle = 2 * ACos( w )
			Local invLength:Double = 1.0 / Sqr( sqrLen )
			ax.x = x * invLength
			ax.y = y * invLength
			ax.z = z * invLength
		Else
			angle = 0
			ax.x = 1
			ax.y = 0
			ax.z = 0
		EndIf
	End Method
	
	Rem bbdoc: Returns a matrix made from the quaternion
	End Rem
	Method ToMatrix:Matrix( )
		' Based off of code in Axiom
		Local m:Matrix = New Matrix
		
		Local tx! = 2*x
		Local ty! = 2*y
		Local tz! = 2*z
		Local twx! = tx*w
		Local twy! = ty*w
		Local twz! = tz*w
		Local txx! = tx*x
		Local txy! = ty*x
		Local txz! = tz*x
		Local tyy! = ty*y
		Local tyz! = tz*y
		Local tzz! = tz*z
		
		m.m00 = 1.0-(tyy+tzz)
		m.m01 = txy-twz
		m.m02 = txz+twy
		m.m10 = txy+twz
		m.m11 = 1.0-(txx+tzz)
		m.m12 = tyz-twx
		m.m20 = txz-twy
		m.m21 = tyz+twx
		m.m22 = 1.0-(txx+tyy)
		
		Return m
	End Method
	
	Rem bbdoc: Returns the inverse of the quaternion
	End Rem
	Method Inverse:Quaternion( )
		Local norm:Double = Dot( Self )
		If norm > 0 Then
			Local r:Quaternion = New Quaternion
			Local inorm:Double = 1.0 / norm
			r.w = w * inorm
			r.x = -x * inorm
			r.y = -y * inorm
			r.z = -z * inorm
			Return r
		EndIf
		
		Return Quaternion.Zero( )
	End Method
	
	Rem bbdoc: Returns the axises of the quaternion
	End Rem
	Method ToAxes( xAxis:Vector3 Var, yAxis:Vector3 Var, zAxis:Vector3 Var )
		xAxis = New Vector3
		yAxis = New Vector3
		zAxis = New Vector3
		
		Local rot:Matrix = ToMatrix( )
		
		xAxis.x = rot.m00
		xAxis.y = rot.m10
		xAxis.z = rot.m20
		
		yAxis.x = rot.m01
		yAxis.y = rot.m11
		yAxis.z = rot.m21
		
		zAxis.x = rot.m02
		zAxis.y = rot.m12
		zAxis.z = rot.m22
	End Method
	
	Rem bbdoc: Creates a quaternion from axises
	End Rem
	Method FromAxes( xAxis:Vector3, yAxis:Vector3, zAxis:Vector3 )
		Local rot:Matrix = New Matrix
		
		rot.m00 = xAxis.x
		rot.m10 = xAxis.y
		rot.m20 = xAxis.z
		
		rot.m01 = yAxis.x
		rot.m11 = yAxis.y
		rot.m21 = yAxis.z
		
		rot.m02 = zAxis.x
		rot.m12 = zAxis.y
		rot.m22 = zAxis.z
		
		Local q:Quaternion = FromRotationMatrix( rot )
		MemCopy( GetPtr( ), q.GetPtr( ), 16 )
	End Method
	
	Rem bbdoc: Creates a quaternion from a matrix
	End Rem
	Function FromRotationMatrix:Quaternion( mat:Matrix )
		Local this:Quaternion = New Quaternion
		
		Local trace:Double = mat.m00 + mat.m11 + mat.m22
		Local root:Double = 0
		
		If trace > 0 Then
			root = Sqr( trace + 1 )
			this.w = .5 * root
			root = .5 / root
			this.x = ( mat.m21-mat.m12 ) * root
			this.y = ( mat.m02-mat.m20 ) * root
			this.z = ( mat.m10-mat.m01 ) * root
		Else
			Local p:Double Ptr = mat.GetPtr( )
			Local i:Int = 0
			If mat.m11 > mat.m00 Then i=1
			If mat.m22 > p[i*4+i] Then i=2
			
			Local j:Int = _next( i )
			Local k:Int = _next( j )
			
			root = Sqr( p[i*4+i] - p[j*4+j] - p[k*4+k] + 1.0 )
			
			Local aq:Double Ptr = this.GetPtr( )
			aq[i] = .5 * root
			this.w = .5 / root
			aq[j] = (p[j+i*4] + p[i+j*4])*root
			aq[k] = (p[k+i*4] + p[i+k*4])*root
		EndIf
		
		Return this
		
		Function _next( i:Int )
			Select i
				Case 0
					Return 1
				Case 1
					Return 2
				Case 2
					Return 0
			End Select
		End Function
	End Function
	
	Rem bbdoc: Blank
	End Rem
	Method Log:Quaternion( )
		Local r:Quaternion = Quaternion.Zero( )
		If Abs( w ) < 1.0 Then
			Local angle:Double = ACos( w )
			Local sn:Double = Sin( angle )
			
			If Abs( sn ) > .0001 Then
				Local co:Double = angle / sn
				r.x = co*x
				r.y = co*y
				r.z = co*z
			Else
				r.x = x
				r.y = y
				r.z = z
			EndIf
		EndIf
		
		Return r
	End Method
	
	Rem bbdoc: Gets a zero-ed quaternion
	End Rem
	Function Zero:Quaternion( )
		Return Create( 0, 0, 0, 0 )
	End Function
	
	Rem bbdoc: Creates a new quaternion
	End Rem
	Function Create:Quaternion( w! = 1, x! = 0, y! = 0, z! = 0 )
		Local r:Quaternion = New Quaternion
		r.w = w; r.x = x; r.y = y; r.z = z
		Return r
	End Function
	
	Rem bbdoc: Converts the quaternion to a Double array
	End Rem
	Method ToArray:Double[]( )
		Local r:Double[4]
		MemCopy( Varptr r[0], GetPtr( ), 16 )
		Return r
	End Method
	
	Rem bbdoc: Returns a Double pointer to the first component of the quaternion
	End Rem
	Method GetPtr:Double Ptr( )
		Return Varptr w
	End Method
	
	Rem bbdoc: Creates a quaternion from an array
	End Rem
	Function FromArray:Quaternion( arr:Double[] )
		Return FromPtr( Varptr arr[0] )
	End Function
	
	Rem bbdoc: Creates a quaternion from a pointer to a Double array
	End Rem
	Function FromPtr:Quaternion( arr:Double Ptr )
		Local r:Quaternion = New Quaternion
		MemCopy( r.GetPtr( ), arr, 16 )
		Return r
	End Function
	
	Rem bbdoc: Copies the quaternion class
	End Rem
	Method Copy:Quaternion( )
		Local r:Quaternion = New Quaternion
		MemCopy( r.GetPtr( ), GetPtr( ), 16 )
		Return r
	End Method
End Type

Rem bbdoc: Plane class
End Rem
Type Plane
	Field norm:Vector3, d!
	
	Method New( )
		norm = Vector3.Create( 0, 1, 0 )
		d = 1
	End Method
	
	Rem bbdoc: Gets the distance from a vector to the plane
	End Rem
	Method Distance!( p:Vector3 )
		Return norm.Dot( p ) + d
	End Method
	
	Rem bbdoc: Returns which side of the plane a vector is on
	End Rem
	Method Side( p:Vector3 )
		Local di:Double = Distance( p )
		
		If di > 0 Then
			Return 1
		ElseIf di < 0 Then
			Return -1
		EndIf
		Return 0
	End Method
	
	Rem bbdoc: Gets the plane normal vector
	End Rem
	Method GetNormal:Vector3( )
		Return norm.Copy( )
	End Method
	
	Rem bbdoc: Creates a new plane
	End Rem
	Function Create:Plane( normal:Vector3, d! )
		Local i:Plane = New Plane
		i.norm = normal.Copy( )
		i.d = d
		Return i
	End Function
	
	Rem bbdoc: Creates a plane from an array of Doubles
	End Rem
	Function FromArray:Plane( arr:Double[] )
		Return FromPtr( Varptr arr[0] )
	End Function
	
	Rem bbdoc: Creates a plane from a pointer to an array of Doubles
	End Rem
	Function FromPtr:Plane( arr:Double Ptr )
		Local i:Plane = New Plane
		i.norm = Vector3.FromPtr( arr )
		i.d = arr[3]
		Return i
	End Function
	
	'' Can't do a standard memory copy for Planes, as they have a Vector3 reference
	Rem bbdoc: Copies the Plane class
	End Rem
	Method Copy:Plane( )
		Local i:Plane = New Plane
		i.d = d
		i.norm = norm.Copy( )
		Return i
	End Method
End Type

Rem bbdoc: Rectangle class
End Rem
Type Rectangle
	Rem bbdoc: X component
	End Rem
	Field x!
	Rem bbdoc: Y component
	End Rem
	Field y!
	Rem bbdoc: Width component
	End Rem
	Field w!
	Rem bbdoc: Height component
	End Rem
	Field h!
	
	Rem bbdoc: Whether or not the rectangle intersects with another rectangle
	End Rem
	Method Intersects( other:Rectangle )
		If x > other.x+other.w Or..
			x+w < other.x Or..
			y > other.y+other.h Or..
			y+h < other.y Then Return 0
		Return 1
	End Method
	
	Rem bbdoc: Returns whether or not @p is inside the rectangle
	End Rem
	Method PointInside( p:Vector2 )
		If p.x < x+w And p.y < y+h And p.x > x And p.y > y Then Return 1
		Return 0
	End Method
End Type

Rem bbdoc: Vector2 class
End Rem
Type Vector2
	Rem bbdoc: X component
	End Rem
	Field x!
	Rem bbdoc: Y component
	End Rem
	Field y!
	
	Rem bbdoc: Returns the magnitude of the point
	End Rem
	Method Magnitude!( )
		Return Sqr( x*x + y*y )
	End Method
	
	Rem bbdoc: Returns the difference of two points
	End Rem
	Method Subtract:Vector2( i:Vector2 )
		Return Create( x-i.x, y-i.y )
	End Method
	
	Rem bbdoc: Returns the sum of two points
	End Rem
	Method Add:Vector2( i:Vector2 )
		Return Create( x+i.x, y+i.y )
	End Method
	
	Rem bbdoc: Returns the product of two points
	End Rem
	Method Multiply:Vector2( i:Vector2 )
		Return Create( x*i.x, y*i.y )
	End Method
	
	Rem bbdoc: Returns the divisor of two points
	End Rem
	Method Divide:Vector2( i:Vector2 )
		Return Create( x/i.x, y/i.y )
	End Method
	
	Rem bbdoc: Creates a new point
	End Rem
	Function Create:Vector2( x!, y! )
		Local i:Vector2 = New Vector2
		i.x = x
		i.y = y
		Return i
	End Function
	
	Rem bbdoc: Copies the point
	End Rem
	Method Copy:Vector2( )
		Return Create( x, y )
	End Method
End Type
