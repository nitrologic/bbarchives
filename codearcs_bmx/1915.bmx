; ID: 1915
; Author: Chroma
; Date: 2007-02-03 23:47:06
; Title: BMax Math Lib v3.0
; Description: New high speed to easy use math library.

' BMax Math Lib v3.0
' by Chroma


' Internal Math Vars - Do Not Alter
Const Tol:Float = 0.0001
Global vTmp:TVector3 = New TVector3
Global qTmp:TQuaternion = New TQuaternion


'	----------
'	----------
'	----------


' Vector Class

Type TVector3

	Field x:Double, y:Double, z:Double
	
'	----------
	
	'Example1: v1:TVector3 = TVector3.Create(1,2,3)
	Function Create:TVector3(x:Double = 0, y:Double = 0, z:Double = 0)
		Local v:TVector3 = New TVector3
		v.x = x
		v.y = y
		v.z = z
		Return v
	End Function
	
	'Example1: v.Set(1,2,3)
	Method Set(x:Double = 0, y:Double = 0, z:Double = 0)
		self.x = x
		self.y = y
		self.z = z
	End Method

'	----------

	'Example: v1 = v1 + v2 is written v1.Add( v1, v2 )
	Method Add(a:TVector3, b:TVector3)
		self.x = a.x + b.x
		self.y = a.y + b.y
		self.z = a.z + b.z
	End Method

	'Example: v1 = v2 + 5 is written v1.AddScalar( v2, 5 )
	Method AddScalar(a:TVector3, s:Double)
		self.x = a.x + s
		self.y = a.y + s
		self.z = a.z + s
	End Method

'	----------

	'Example: v1 = v2 - v3 is written v1.Sub( v2, v3 )
	Method Sub(a:TVector3, b:TVector3)
		self.x = a.x - b.x
		self.y = a.y - b.y
		self.z = a.z - b.z
	End Method
	
	'Example: v1 = v2 - 5 is written v1.SubScalar( v2, 5 )
	Method SubScalar(a:TVector3, s:Double)
		self.x = a.x - s
		self.y = a.y - s
		self.z = a.z - s
	End Method

'	----------

	'Example: v1 = v2 * v3 is written v1.Mul( v2, v3 )
	Method Mul(a:TVector3, b:TVector3)
		self.x = a.x * b.x
		self.y = a.y * b.y
		self.z = a.z * b.z
	End Method

	'Example: v1 = v2 * 5 is written v1.MulScalar( v2, 5 )
	Method MulScalar(a:TVector3, s:Double)
		self.x = a.x * s
		self.y = a.y * s
		self.z = a.z * s
	End Method

'	----------

	'Example: v1 = v2 / v3 is written v1.Div( v2, v3 )
	Method Div(a:TVector3, b:TVector3)
		self.x = a.x / b.x
		self.y = a.y / b.y
		self.z = a.z / b.z
	End Method

	'Example: v1 = v2 / 5 is written v1.DivScalar( v2, 5 )
	Method DivScalar(a:TVector3, s:Double)
		self.x = a.x / s
		self.y = a.y / s
		self.z = a.z / s
	End Method

'	----------
	
	'Example: mag:Double = v.Magnitude()
	Method Magnitude:Double()
		Return Sqr(self.x * self.x + self.y * self.y + self.z * self.z)
	End Method
	
	'Example: v.Normalize()
	Method Normalize()
		Local mag:Double = self.Magnitude()
		self.x :/ mag
		self.y :/ mag
		self.z :/ mag
		If Abs(self.x) < Tol self.x = 0
		If Abs(self.y) < Tol self.y = 0
		If Abs(self.z) < Tol self.z = 0
	End Method
	
'	----------

	'Example: v1 = v2 ^ v3 is written v1.CrossProduct( v2, v3 )
	Method CrossProduct(a:TVector3, b:TVector3)
		self.x =  a.y * b.z - a.z * b.y
		self.y = -a.x * b.z + a.z * b.x
		self.z =  a.x * b.y - a.y * b.x
	EndMethod

	'Example1: dotp:double = v1.DotProduct( v2 )
	Method DotProduct:Double( a:TVector3 )
		Return self.x * a.x + self.y * a.y + self.z * a.z
	End Method

'	----------
	
	'Returns Inverse Instance of a Vector
	Method Inverse:TVector3()
		vTmp.x = -self.x
		vTmp.y = -self.y
		vTmp.z = -self.z
		Return vTmp
	End Method

	' Example: v.GetVecFromQuat( q )
	Method GetVecFromQuat(a:TQuaternion)
		self.x = a.x
		self.y = a.y
		self.z = a.z
	End Method

	'Example: v.MakeEulerFromQuat( q )
	Method MakeEulerFromQuat(q:TQuaternion)
		Local	r11#, r21#, r31#, r32#, r33#, r12#, r13#
		Local	q00#, q11#, q22#, q33#
		Local	tmp#
	
		q00 = q.n * q.n
		q11 = q.x * q.x
		q22 = q.y * q.y
		q33 = q.z * q.z
	
		r11 = q00 + q11 - q22 - q33
		r21 = 2 * (q.x*q.y + q.n*q.z)
		r31 = 2 * (q.x*q.z - q.n*q.y)
		r32 = 2 * (q.y*q.z + q.n*q.x)
		r33 = q00 - q11 - q22 + q33
	
		tmp = Abs(r31)
		If (tmp > 0.999999)
			r12 = 2 * (q.x*q.y - q.n*q.z)
			r13 = 2 * (q.x*q.z + q.n*q.y)
	
			self.x = RadiansToDegrees(0.0)							' roll
			self.y = RadiansToDegrees( Float (-(Pi/2) * r31/tmp))	' pitch
			self.z = RadiansToDegrees( Float ATan2(-r12, -r31*r13))' yaw
		Else
			self.x = RadiansToDegrees( Float ATan2(r32, r33))		' roll
			self.y = RadiansToDegrees( Float ASin(-r31))			' pitch
			self.z = RadiansToDegrees( Float ATan2(r21, r11))		' yaw
		EndIf
	End Method

'	----------

	'Example: vPos.AddTimeStep( vVel, dt )
	Method AddTimeStep(a:TVector3, timestep:Float)
		self.x :+ a.x * timestep
		self.y :+ a.y * timestep
		self.z :+ a.z * timestep
	End Method

	'Example: vVel.AddGravity(  , dt )
	Method AddGravity(g:Float = 9.8, timestep:Float)
		self.y :- g * timestep
	End Method

	'Example1: v.Show("v",5,5)
	Method Show(cap:String, xpos:Int, ypos:Int, spc:Int = 12)
		DrawText cap + "_x:" + self.x,xpos,ypos
		DrawText cap + "_y:" + self.y,xpos,ypos + spc
		DrawText cap + "_z:" + self.z,xpos,ypos + spc * 2
	End Method

End Type


'	----------
'	----------
'	----------


' Quaternion Class

Type TQuaternion

	Field n:Double, x:Double, y:Double, z:Double
	
'	----------
	
	Function Create:TQuaternion(n:Double = 1.0, x:Double = 0, y:Double = 0, z:Double = 0)
		Local q:TQuaternion = New TQuaternion
		q.n = n
		q.x = x
		q.y = y
		q.z = z
		Return q
	End Function

'	----------

	' Example: q1=q2+q3 would be q1.Add( q2, q3 )
	Method Add(a:TQuaternion, b:TQuaternion)
		self.n = a.n + b.n
		self.x = a.x + b.x
		self.y = a.y + b.y
		self.z = a.z + b.z
	End Method

'	----------

	' Example: q1=q2-q3 would be q1.Sub( q2, q3 )
	Method Sub(a:TQuaternion, b:TQuaternion)
		self.n = a.n - b.n
		self.x = a.x - b.x
		self.y = a.y - b.y
		self.z = a.z - b.z
	End Method

'	----------

	' Example: q1=q2*q3 would be q1.Mul( q2, q3 )
	Method Mul(a:TQuaternion, b:TQuaternion)
		self.n = a.n * b.n - a.x * b.x - a.y * b.y - a.z * b.z
		self.x = a.n * b.x + a.x * b.n + a.y * b.z - a.z * b.y
		self.y = a.n * b.y + a.y * b.n + a.z * b.x - a.x * b.z
		self.z = a.n * b.y + a.y * b.n + a.z * b.x - a.x * b.z
	End Method

	' Example: q1=q2*2 would be q1.MulScalar( q2, 2 )
	Method MulScalar( a:TQuaternion, s:Double )
		self.n = a.n * s
		self.x = a.x * s
		self.y = a.y * s
		self.z = a.z * s
	End Method
	
'	----------
	
	' Example: q1=q2/2 would be q1.DivS( q2, 2 )
	Method DivScalar(a:TQuaternion, s:Double )
		self.n = a.n / s
		self.x = a.x / s
		self.y = a.y / s
		self.z = a.z / s
	End Method
	
'	----------

	' Example: Local qmag:Double = q.Magnitude()
	Method Magnitude:Double( a:TQuaternion )
		Return Sqr( a.n * a.n + a.x * a.x + a.y * a.y + a.z * a.z )
	End Method

'	----------

	'Returns Inverse Instance of a Quaternion
	Method Inverse:TQuaternion()
		qTmp.n =  self.n
		qTmp.x = -self.x
		qTmp.y = -self.y
		qTmp.z = -self.z
		Return qTmp
	End Method

'	----------

	'Example: q.MakeQuatFromEuler( pitch, yaw, roll )
	Method MakeQuatFromEuler(x:Float, y:Float, z:Float)
		Local roll:Float = DegreesToRadians(x)
		Local pitch:Float = DegreesToRadians(y)
		Local yaw:Float = DegreesToRadians(z)
		
		Local cyaw#, cpitch#, croll#, syaw#, spitch#, sroll#
		Local cyawcpitch#, syawspitch#, cyawspitch#, syawcpitch#

		cyaw = Cos(0.5 * yaw)
		cpitch = Cos(0.5 * pitch)
		croll = Cos(0.5 * roll)
		syaw = Sin(0.5 * yaw)
		spitch = Sin(0.5 * pitch)
		sroll = Sin(0.5 * roll)
	
		cyawcpitch = cyaw*cpitch
		syawspitch = syaw*spitch
		cyawspitch = cyaw*spitch
		syawcpitch = syaw*cpitch

		self.n = Float (cyawcpitch * croll + syawspitch * sroll)
		self.x = Float (cyawcpitch * sroll - syawspitch * croll) 
		self.y = Float (cyawspitch * croll + syawcpitch * sroll)
		self.z = Float (syawcpitch * croll - cyawspitch * sroll)
	End Method

'	----------


	Method Show(cap:String = "", xpos:Int, ypos:Int, spc:Int = 12)
		DrawText cap + "_w:" + self.n,xpos,ypos
		DrawText cap + "_x:" + self.x,xpos,ypos + spc 
		DrawText cap + "_y:" + self.y,xpos,ypos + spc * 2
		DrawText cap + "_z:" + self.z,xpos,ypos + spc * 3
	End Method

End Type



'	----------
'	----------
'	----------

' Matrix Class

Type TMatrix

	Field e11:Double, e12:Double, e13:Double
	Field e21:Double, e22:Double, e23:Double
	Field e31:Double, e32:Double, e33:Double

'	----------

	Function Create:TMatrix(e11!=0, e12!=0, e13!=0, e21!=0, e22!=0, e23!=0, e31!=0, e32!=0, e33!=0)
		Local m:TMatrix = New TMatrix
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
	
'	----------


'	----------


End Type


'	----------
'	----------
'	----------


' Degrees To Radians Conversion
Function DegreesToRadians:Float(deg:Float)
	Return deg * Pi / 180.0
End Function

' Radians To Degrees Conversion
Function RadiansToDegrees:Float(rad:Float)
	Return rad * 180.0 / Pi
End Function
