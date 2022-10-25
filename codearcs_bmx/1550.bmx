; ID: 1550
; Author: Chroma
; Date: 2005-12-01 19:27:26
; Title: 3D Math Lib - BMax
; Description: Easy to use 3d math lib for BMax.  Can do complex equations on a single line.

' 3D Math Library v2.0
' BlitzMAX Edition
' by Chroma

' You can do some lengthy equations with this Math Lib
' Just remember it does the maths in order of parenthesis
' So you have to write the equation accordingly (in reverse?)
' For example the equation: v=v1+v2*v3+v4
' It doesn't automatically do the multiplication first
' You have to engineer the equation manually like so:
' v = v1.Add( v4.Add( v2.Mul( v3 ) ) )
' Just be aware of the order they are calc'ed in

' Here's a physics example:
' vAcceleration = vForces.DivS( Mass )
' vVelocity.AddTimeStep( vAcceleration, dt )
' vPosition.AddTimeStep( vVelocity, dt )

'Test
'Graphics 800,600,32,85
'a:Vector = Vector.Create()
'b:Vector = Vector.Create(1,2,3)
'While Not KeyHit(KEY_ESCAPE)
'Cls
'a.AddTimeStep( b, .015)
'a.Show(5,5,"Test")
'Flip
'Wend
'End


' Constants
Const Tol:Float = 0.0001

'-------------------------------------------
'---- Vector Type and Functions/Methods ----
'-------------------------------------------
Type Vector

	Field x:Float
	Field y:Float
	Field z:Float
	
	' Purpose: Create a New Vector
	' Returns: Vector
	' Example1: v1:Vector = New Vector
	' Example2: v1:Vector = Vector.Create()
	' Example3: v1:Vector = Vector.Create(1,2,3)
	Function Create:Vector( x:Float = 0, y:Float = 0, z:Float = 0 )
		Local v:Vector = New Vector
		v.x = x
		v.y = y
		v.z = z
		Return v
	End Function
	
	' Purpose: Set a Vector with New XYZ Components
	' Returns: Nothing
	' Example: v1.Set(1,2,3)
	' Example: v1.Set() will set a vector to (0,0,0)
	Method Set( newx:Float = 0, newy:Float = 0, newz:Float = 0 )
		x = newx
		y = newy
		z = newz
	End Method
	
	' Purpose: Add Two Vectors
	' Returns: Vector
	' Example1: v1=v1+v2 would be written v1 = v1.Add( v2 )
	' Example2: v1=v2+v3 would be written v1 = v2.Add( v3 )
	Method Add:Vector( v:Vector )
		Local res:Vector = New Vector
		res.x = x + v.x 
		res.y = y + v.y
		res.z = z + v.z
		Return res
	End Method

	' Purpose: Subtract Two Vectors
	' Returns: Vector
	' Example1: v1=v1-v2 would be written v1 = v1.Sub( v2 ) 
	' Example2: v1=v2-v3 would be written v1 = v2.Sub( v3 ) 
	Method Sub:Vector( v:Vector )
		Local res:Vector = New Vector
		res.x = x - v.x
		res.y = y - v.y
		res.z = z - v.z
		Return res
	End Method
	
	' Purpose: Multiply Two Vectors
	' Returns: Vector
	' Example1: v1=v1*v2 would be written v1 = v1.Mul( v2 ) 
	' Example2: v1=v2*v3 would be written v1 = v2.Mul( v3 )
	Method Mul:Vector( v:Vector )
		Local res:Vector = New Vector
		res.x = x * v.x
		res.y = y * v.y
		res.z = z * v.z
		Return res
	End Method
	
	' Purpose: Divide One Vector By Another
	' Returns: Vector
	' Example1: v1=v1/v2 would be written v1 = v1.Div( v2 )
	' Example2: v1=v2/v3 would be written v1 = v2.Div( v3 )
	Method Div:Vector( v:Vector )
		Local res:Vector = New Vector
		res.x = x / v.x
		res.y = y / v.y
		res.z = z / v.z
		Return res
	End Method
	
	' Purpose: Add a Scalar to a Vector
	' Returns: Vector
	' Example1: v1=v1+1 would be written v1 = v1.AddS( 1 )
	' Example2: v1=v2+1 would be written v1 = v2.AddS( 1 )
	Method AddS:Vector( s:Float )
		Local res:Vector = New Vector
		res.x = x + s
		res.y = y + s
		res.z = z + s
		Return res
	End Method
	
	' Purpose: Subtract a Scalar from a Vector
	' Returns: Vector
	' Example1: v1=v1-1 would be written v1 = v1.SubS( 1 )
	' Example2: v1=v2-1 would be written v1 = v2.SubS( 1 )
	Method SubS:Vector( s:Float )
		Local res:Vector = New Vector
		res.x = x - s
		res.y = y - s
		res.z = z - s
		Return res
	End Method

	' Purpose: Multiply a Vector by a Scalar
	' Returns: Vector
	' Example1: v1=v1*2 would be written v1 = v1.MulS( 2 )
	' Example2: v1=v2*2 would be written v1 = v2.MulS( 2 )
	Method MulS:Vector( s:Float )
		Local res:Vector = New Vector
		res.x = x * s
		res.y = y * s
		res.z = z * s
		Return res
	End Method
	
	' Purpose: Divide a Vector by a Scalar
	' Returns: Vector
	' Example1: v1=v1/2 would be written v1 = v1.DivS( 2 )
	' Example2: v1=v2/2 would be written v1 = v2.DivS( 2 )
	Method DivS:Vector( s:Float )
		Local res:Vector = New Vector
		res.x = x / s
		res.y = y / s
		res.z = z / s
		Return res
	End Method
	
	' Purpose: Multiply a Vector by a Quaternion
	' Returns: Quaternion
	' Example: q1=v1*q1 would be written q1 = v1.MulQ ( q1 )	
	Method MulQ:Quaternion( q:Quaternion )
		Local res:Quaternion = New Quaternion
		res.n = -(q.v.x*x + q.v.y*y + q.v.z*z)
		res.v.x = q.n*x + q.v.z*y - q.v.y*z
		res.v.y = q.n*y + q.v.x*z - q.v.z*x
		res.v.z = q.n*z + q.v.y*x - q.v.x*y
		Return res
	End Method

	' Purpose: Multiply a Vector by a Matrix
	' Returns: Vector
	' Example: v1=v1*m1 would be written v1 = v1.MulM( m1 )	
	Method MulM:Vector ( m:Matrix )
		Local res:Vector = New Vector	
		res.x = x*m.e11 + y*m.e21 + z*m.e31
		res.y = x*m.e12 + y*m.e22 + z*m.e32
		res.z = x*m.e13 + y*m.e23 + z*m.e33
		Return res
	End Method

	' Purpose: Calculate the Magnitude of a Vector
	' Returns: Float
	' Example: Local a:Float = v1.Magnitude()
	Method Magnitude:Float()
		Return Sqr(x * x + y * y + z * z)
	End Method

	' Purpose: Normalize a Vector
	' Returns: Nothing - Directly Normalizes Target Vector
	' Example: v1.Normalize()
	Method Normalize()
		Local mag:Float = Magnitude()
		If mag = 0.0 Then Set();Return
		x :/ mag
		y :/ mag
		z :/ mag
		If Abs( x ) < Tol x = 0.0
		If Abs( y ) < Tol y = 0.0
		If Abs( z ) < Tol z = 0.0
	End Method
	
	' Purpose: Calculate the Cross Product of Two Vectors
	' Returns: Vector
	' Example1: v1 = v1.CrossP( v2 )
	' Example2: v1 = v2.CrossP( v3 )
	Method CrossP:Vector( v:Vector )
		Local res:Vector = New Vector
		res.x =  y * v.z  -  z * v.y 
		res.y = -x * v.z  +  z * v.x 
		res.z =  x * v.y  -  y * v.x
		Return res
	EndMethod
	
	' Purpose: Calculate the Dot Product Between Two Vectors
	' Returns: Float
	' Example: Local mydot:float = v1.DotP( v2 )
	Method DotP:Float( v:Vector )
		Return x * v.x + y * v.y + z * v.z
	EndMethod
	
	Method Inverse:Vector()
		Return Vector.Create( -x, -y, -z )
	End Method
	
	' Purpose: Add Two Vectors and Multiply by a DeltaTime
	' Returns: Nothing - Directly Changes the Target Vector
	' Example: vPos=vPos+vVel*dt would be vPos.AddTimeStep( vVel, dt )
	Method AddTimeStep( v:Vector, time_step:Float )
		x :+ v.x * time_step
		y :+ v.y * time_step
		z :+ v.z * time_step
	End Method
	
	' Purpose: Show Vector Values for Debug Purposes
	' Returns: Nothing
	' Note: Graphics mode must be set for this command to work
	' Example: v1.Show( 5, 5, "vPosition" )
	Method Show( xpos:Int = 5, ypos:Int = 5, label:String = "", vspc:Int = 12 )
		DrawText label + "_X: " + x, xpos, ypos
		DrawText label + "_Y: " + y, xpos, ypos + vspc
		DrawText label + "_Z: " + z, xpos, ypos + vspc * 2
	End Method
	
End Type

' Degrees To Radians Conversion
Function DegreesToRadians:Float(deg:Float)
	Return deg * Pi / 180.0
End Function

' Radians To Degrees Conversion
Function RadiansToDegrees:Float(rad:Float)
	Return rad * 180.0 / Pi
End Function


'-------------------------------------------
'-- Quaternion Type and Functions/Methods --
'-------------------------------------------
Type Quaternion
	
	Field n:Float
	Field v:Vector = New Vector
	
	' Purpose: Create a New Quaternion
	' Returns: Quaternion
	' Example1: q1:Quaternion = New Quaternion
	' Example2: q1:Quaternion = Quaternion.Create( 1, 2, 3, 4 )
	' Example3: q1:Quaternion = Quaternion.Create()
	Function Create:Quaternion( n:Float = 0, x:Float = 0, y:Float = 0, z:Float = 0 )
		Local q:Quaternion = New Quaternion
		q.n = n
		q.v.x = x
		q.v.y = y
		q.v.z = z
		Return q
	End Function
	
	' Purpose: Add Two Quaternions
	' Returns: Quaternion
	' Example1: q1=q1+q2 would be q1 = q1.Add( q2 )
	' Example2: q1=q2+q3 would be q1 = q2.Add( q3 )
	Method Add:Quaternion( q:Quaternion )
		Local res:Quaternion = New Quaternion
		res.n = n + q.n
		res.v.x = v.x + q.v.x
		res.v.y = v.y + q.v.y
		res.v.z = v.z + q.v.z
		Return res
	End Method
	
	' Purpose: Subtract Two Quaternions
	' Returns: Quaternion
	' Example1: q1=q1-q2 would be q1 = q1.Sub( q2 )
	' Example2: q1=q2-q3 would be q1 = q2.Sub( q3 )
	Method Sub:Quaternion( q:Quaternion )
		Local res:Quaternion = New Quaternion
		res.n = n - q.n
		res.v.x = v.x - q.v.x
		res.v.y = v.y - q.v.y
		res.v.z = v.z - q.v.z
		Return res
	End Method
	
	' Purpose: Multiply Two Quaternions
	' Returns: Quaternion
	' Example1: q1=q1*q2 would be q1 = q1.Mul( q2 )
	' Example2: q1=q2*q3 would be q1 = q2.Mul( q3 )
	Method Mul:Quaternion( q:Quaternion )
		Local res:Quaternion = New Quaternion
		res.n = n*q.n - v.x*q.v.x - v.y*q.v.y - v.z*q.v.z
		res.v.x = n*q.v.x + v.x*q.n + v.y*q.v.z - v.z*q.v.y
		res.v.y = n*q.v.y + v.y*q.n + v.z*q.v.x - v.x*q.v.z
		res.v.z = n*q.v.y + v.y*q.n + v.z*q.v.x - v.x*q.v.z
		Return res
	End Method
	
	' Purpose: Multiply a Quaternion by a Scalar
	' Returns: Quaternion
	' Example1: q1=q1*2 would be q1 = q1.MulS( 2 )
	' Example2: q1=q2*2 would be q1 = q2.MulS( 2 )
	Method MulS:Quaternion( s:Float )
		Local res:Quaternion = New Quaternion
		res.n = n * s
		res.v.x = v.x * s
		res.v.y = v.y * s
		res.v.z = v.z * s
		Return res
	End Method

	' Purpose: Divide a Quaternion by a Scalar
	' Returns: Quaternion
	' Example1: q1=q1/2 would be q1 = q1.DivS( 2 )
	' Example2: q1=q2/2 would be q1 = q2.DivS( 2 )
	Method DivS:Quaternion( s:Float )
		Local res:Quaternion = New Quaternion
		res.n = n / s
		res.v.x = v.x / s
		res.v.y = v.y / s
		res.v.z = v.z / s
		Return res
	End Method
	
	' Purpose: Multiply a Quaternion by a Vector
	' Returns: Quaternion
	' Example1: q1=q1*v1 would be written q1 = q1.MulV( v1 )
	Method MulV:Quaternion( v:Vector )
		res:Quaternion = New Quaternion
		res.n = -(self.v.x*v.x + self.v.y*v.y + self.v.z*v.z)
		res.v.x = self.n*v.x + self.v.y*v.z - self.v.z*v.y
		res.v.y = self.n*v.y + self.v.z*v.x - self.v.x*v.z
		res.v.z = self.n*v.z + self.v.x*v.y - self.v.y*v.x
		Return res
	End Method
	
	' Purpose: Calculate the Magnitude of a Quaternion
	' Returns: Float
	' Example: Local qmag:float = q1.Magnitude()
	Method Magnitude:Float( q:Quaternion )
		Return Float Sqr( n * n + v.x * v.x + v.y * v.y + v.z * v.z )
	End Method

	' Purpose: Gets the XYZ Component of a Quaternion
	' Returns: Vector
	' Example: v1 = q1.GetVector()
	Method GetVector:Vector()
		Return Vector.Create(v.x, v.y, v.z)
	End Method
	
	' Purpose: Inverse a Quaternion
	' Returns: Quaternion
	' Example1: q1=~q1 would be q1 = q1.Inverse()
	' Example2: q1=~q2 would be q1 = q2.Inverse() 
	Method Inverse:Quaternion()
		Return Quaternion.Create(n, -v.x, -v.y, -v.z)
	End Method
	
	' Purpose: Show Quaternion Values for Debug Purposes
	' Returns: Nothing
	' Note: Graphics mode must be set for this command to work
	' Example: q1.Show( 5, 5, "qAngularVelocity" )
	Method Show( xpos:Int, ypos:Int, label:String = "", vspc:Int = 12 )
		DrawText n, xpos, ypos
		DrawText v.x, xpos, ypos + vspc
		DrawText v.y, xpos, ypos + vspc * 2
		DrawText v.z, xpos, ypos + vspc * 3
	End Method

	' Purpose: Used in Physics
	' Returns: Quaternion
	' Example: q1 = q1.QRotate( q2 )
	Method QRotate:Quaternion( q:Quaternion )
		' q1*q2*(~q1)
		Local t:Quaternion = New Quaternion
		t = self.Mul( q )
		Return t.Mul( self.Inverse() )
	End Method

	' Purpose: Used in Physics
	' Returns: Vector
	' Example: v1 = q1.QVRotate( v2 )
	Method QVRotate:Vector( v )
		Local t:Quaternion = New Quaternion
		' t = q*v*(~q);
		t = self.MulV( v )
		t = t.Mul( self.Inverse() )
		Return t.GetVector()
	End Method

End Type


'-------------------------------------------
'---- Matrix Type and Functions/Methods ----
'-------------------------------------------

Type Matrix

	Field e11:Float = 0, e12:Float = 0, e13:Float = 0
	Field e21:Float = 0, e22:Float = 0, e23:Float = 0
	Field e31:Float = 0, e32:Float = 0, e33:Float = 0

	Function Create:Matrix(e11#=0,e12#=0,e13#=0,e21#=0,e22#=0,e23#=0,e31#=0,e32#=0,e33#=0)
		Local m:Matrix = New Matrix
		m.e11 = e11
		m.e12 = e12
		m.e13 = e13
		m.e21 = e21
		m.e22 = e22
		m.e23 = e23
		m.e31 = e31
		m.e32 = e32
		m.e33 = e33
		Return m
	End Function

	Method Det:Float()
		Return e11*e22*e33-e11*e32*e23+e21*e32*e13-e21*e12*e33+e31*e12*e23-e31*e22*e13
	End Method

	Method Transpose:Matrix()
		Return Matrix.Create(e11,e21,e31,e12,e22,e32,e13,e23,e33)
	End Method
		
	Method 	Inverse:Matrix()
		Local dd:Float = e11*e22*e33-e11*e32*e23+e21*e32*e13-e21*e12*e33+e31*e12*e23-e31*e22*e13
		If dd = 0 Then dd = 1
		Local a:Float = (e22*e33-e23*e32)/dd
		Local b:Float = -(e12*e33-e13*e32)/dd
		Local c:Float = (e12*e23-e13*e22)/dd
		Local d:Float = -(e21*e33-e23*e31)/dd
		Local e:Float = (e11*e33-e13*e31)/dd
		Local f:Float = -(e11*e23-e13*e21)/dd
		Local g:Float = (e21*e32-e22*e31)/dd
		Local h:Float = -(e11*e32-e12*e31)/dd
		Local i:Float = (e11*e22-e12*e21)/dd
		Return Matrix.Create(a,b,c,d,e,f,g,h,i)
	End Method
		
	Method Add:Matrix( m:Matrix )
		Local res:Matrix = New Matrix
		res.e11 = e11 + m.e11
		res.e12 = e12 + m.e12
		res.e13 = e13 + m.e13
		res.e21 = e21 + m.e21
		res.e22 = e22 + m.e22
		res.e23 = e23 + m.e23
		res.e31 = e31 + m.e31
		res.e32 = e32 + m.e32
		res.e33 = e33 + m.e33
		Return res
	End Method

	Method Sub:Matrix( m:Matrix )
		Local res:Matrix = New Matrix
		res.e11 = e11 - m.e11
		res.e12 = e12 - m.e12
		res.e13 = e13 - m.e13
		res.e21 = e21 - m.e21
		res.e22 = e22 - m.e22
		res.e23 = e23 - m.e23
		res.e31 = e31 - m.e31
		res.e32 = e32 - m.e32
		res.e33 = e33 - m.e33
		Return res
	End Method

	Method Mul:Matrix( m:Matrix )
		Local res:Matrix = New Matrix
		res.e11 = e11*m.e11 + e12*m.e21 + e13*m.e31
		res.e12 = e11*m.e12 + e12*m.e22 + e13*m.e32
		res.e13 = e11*m.e13 + e12*m.e23 + e13*m.e33
		res.e21 = e21*m.e11 + e22*m.e21 + e23*m.e31
		res.e22 = e21*m.e12 + e22*m.e22 + e23*m.e32
		res.e23 = e21*m.e13 + e22*m.e23 + e23*m.e33
		res.e31 = e31*m.e11 + e32*m.e21 + e33*m.e31
		res.e32 = e31*m.e12 + e32*m.e22 + e33*m.e32
		res.e33 = e31*m.e13 + e32*m.e23 + e33*m.e33
		Return res
	End Method

	Method MulS:Matrix( s:Float )
		Local res:Matrix = New Matrix
		res.e11 = e11 * s
		res.e12 = e12 * s
		res.e13 = e13 * s
		res.e21 = e21 * s
		res.e22 = e22 * s
		res.e23 = e23 * s
		res.e31 = e31 * s
		res.e32 = e32 * s
		res.e33 = e33 * s
		Return res
	End Method

	Method DivS:Matrix( s:Float )
		Local res:Matrix = New Matrix
		res.e11 = e11 * s
		res.e12 = e12 * s
		res.e13 = e13 * s
		res.e21 = e21 * s
		res.e22 = e22 * s
		res.e23 = e23 * s
		res.e31 = e31 * s
		res.e32 = e32 * s
		res.e33 = e33 * s
		Return res
	End Method

	' Purpose: Multiply a Matrix by a Vector and Return a Vector Result
	' Example: v1=m1*v1 would be v1=m1.MulV( v1 )
	Method MulV:Vector( v:Vector )
		Local res:Vector = New Vector
		res.x = e11*v.x + e12*v.y + e13*v.z
		res.y = e21*v.x + e22*v.y + e23*v.z
		res.z = e31*v.x + e32*v.y + e33*v.z
		Return res
	End Method

End Type
