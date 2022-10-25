; ID: 490
; Author: Wavey
; Date: 2002-11-15 13:05:49
; Title: Quaternions
; Description: Some basic quaternion functions (useful for rotating objects)

; Quat.bb : v1.0 : 15/11/02

; A tutorial on how to use this file is at http://www.dscho.co.uk/blitz/tutorials/quaternions.shtml

; Types
Type Rotation
	Field pitch#, yaw#, roll#
End Type

Type Quat
	Field w#, x#, y#, z#
End Type

; Change these constants if you notice slips in accuracy
Const QuatToEulerAccuracy# = 0.001
Const QuatSlerpAccuracy#   = 0.0001

; convert a Rotation to a Quat
Function EulerToQuat(out.Quat, src.Rotation)
	; NB roll is inverted due to change in handedness of coordinate systems
	Local cr# = Cos(-src\roll/2)
	Local cp# = Cos(src\pitch/2)
	Local cy# = Cos(src\yaw/2)

	Local sr# = Sin(-src\roll/2)
	Local sp# = Sin(src\pitch/2)
	Local sy# = Sin(src\yaw/2)

	; These variables are only here to cut down on the number of multiplications
	Local cpcy# = cp * cy
	Local spsy# = sp * sy
	Local spcy# = sp * cy
	Local cpsy# = cp * sy

	; Generate the output quat
	out\w = cr * cpcy + sr * spsy
	out\x = sr * cpcy - cr * spsy
	out\y = cr * spcy + sr * cpsy
	out\z = cr * cpsy - sr * spcy
End Function

; convert a Quat to a Rotation
Function QuatToEuler(out.Rotation, src.Quat)
	Local sint#, cost#, sinv#, cosv#, sinf#, cosf#
	Local cost_temp#

	sint = (2 * src\w * src\y) - (2 * src\x * src\z)
	cost_temp = 1.0 - (sint * sint)

	If Abs(cost_temp) > QuatToEulerAccuracy
		cost = Sqr(cost_temp)
	Else
		cost = 0
	EndIf

	If Abs(cost) > QuatToEulerAccuracy
		sinv = ((2 * src\y * src\z) + (2 * src\w * src\x)) / cost
		cosv = (1 - (2 * src\x * src\x) - (2 * src\y * src\y)) / cost
		sinf = ((2 * src\x * src\y) + (2 * src\w * src\z)) / cost
		cosf = (1 - (2 * src\y * src\y) - (2 * src\z * src\z)) / cost
	Else
		sinv = (2 * src\w * src\x) - (2 * src\y * src\z)
		cosv = 1 - (2 * src\x * src\x) - (2 * src\z * src\z)
		sinf = 0
		cosf = 1
	EndIf

	; Generate the output rotation
	out\roll = -ATan2(sinv, cosv) ;  inverted due to change in handedness of coordinate system
	out\pitch = ATan2(sint, cost)
	out\yaw = ATan2(sinf, cosf)
End Function

; use this to interpolate between quaternions
Function QuatSlerp(res.Quat, start.Quat, fin.Quat, t#)
	Local scaler_w#, scaler_x#, scaler_y#, scaler_z#
	Local omega#, cosom#, sinom#, scale0#, scale1#

	cosom = start\x * fin\x + start\y * fin\y + start\z * fin\z + start\w * fin\w

	If cosom <= 0.0
		cosom = -cosom
		scaler_w = -fin\w
		scaler_x = -fin\x
		scaler_y = -fin\y
		scaler_z = -fin\z
	Else
		scaler_w = fin\w
		scaler_x = fin\x
		scaler_y = fin\y
		scaler_z = fin\z
	EndIf

	If (1 - cosom) > QuatSlerpAccuracy
		omega = ACos(cosom)
		sinom = Sin(omega)
		scale0 = Sin((1 - t) * omega) / sinom
		scale1 = Sin(t * omega) / sinom
	Else
		; Angle too small: use linear interpolation instead
		scale0 = 1 - t
		scale1 = t
	EndIf

	res\x = scale0 * start\x + scale1 * scaler_x
	res\y = scale0 * start\y + scale1 * scaler_y
	res\z = scale0 * start\z + scale1 * scaler_z
	res\w = scale0 * start\w + scale1 * scaler_w
End Function

; result will be the same rotation as doing q1 then q2 (order matters!)
Function MultiplyQuat(result.Quat, q1.Quat, q2.Quat)
	Local a#, b#, c#, d#, e#, f#, g#, h#

	a = (q1\w + q1\x) * (q2\w + q2\x)
	b = (q1\z - q1\y) * (q2\y - q2\z)
	c = (q1\w - q1\x) * (q2\y + q2\z)
	d = (q1\y + q1\z) * (q2\w - q2\x)
	e = (q1\x + q1\z) * (q2\x + q2\y)
	f = (q1\x - q1\z) * (q2\x - q2\y)
	g = (q1\w + q1\y) * (q2\w - q2\z)
	h = (q1\w - q1\y) * (q2\w + q2\z)

	result\w = b + (-e - f + g + h) / 2
	result\x = a - ( e + f + g + h) / 2
	result\y = c + ( e - f + g - h) / 2
	result\z = d + ( e - f - g + h) / 2
End Function

; convenience function to fill in a rotation structure
Function FillRotation(r.Rotation, pitch#, yaw#, roll#)
	r\pitch = pitch
	r\yaw = yaw
	r\roll = roll
End Function
