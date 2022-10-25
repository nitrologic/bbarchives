; ID: 1127
; Author: AntMan - Banned in the line of duty.
; Date: 2004-08-05 18:01:10
; Title: SmoothTrack V2.0 (Quats)
; Description: Much better version, with built in tracking and internally uses quats.

Type stag
	Field id,p#,y#,r#
	Field c.quat,d.quat,rt.rotation,rb.quat
	Field tar.rotation
End Type

Function smoothTrack(ent1,ent2,speed#=0.2,lockYaw#=0,range#=150)
For s.stag=Each stag
	If s\id=ent1
		ap#=s\p
		ay#=s\y
		ar#=s\r
		found=True
		
		Exit
	EndIf
Next

If Not found
	ap#=EntityPitch(ent1,True)
	ay#=EntityYaw(ent1,True)
	ar#=EntityRoll(ent1,True)
	s.stag=New stag
	s\id=ent1
	s\rt=rotation(ap,ay,ar)
	s\c=New quat
	s\d=New quat
	eulerToQuat(s\c,s\rt)
	s\tar=New rotation

	s\rb=New quat
EndIf
	range=range/2
	y2#=EntityYaw(ent2,True)
	ay=lockAng(ay,lockyaw,range)
	s\tar\pitch=EntityPitch(ent2,True)
	s\tar\yaw=EntityYaw(ent2,True)
	s\tar\roll=EntityRoll(ent2,True)
	eulertoquat(s\d,s\tar)
	quatSlerp(s\rb,s\c,s\d,0.1)
	quatToEuler(s\rt,s\rb)
	s\rt\yaw=lockAng(s\rt\yaw,lockYaw,range)
	RotateEntity ent1,s\rt\pitch,s\rt\yaw,s\rt\roll,True
	eulertoQuat(s\c,s\rt)
	s\p=ap
	s\y=ay
	s\r=ar
End Function
Function lockAng#(from#,lock#,range#)
If KeyDown(57) DebugLog "In>"+from

out#=from
	If from>lock
		d1#=360.-from+lock
		d2#=from-lock
		If d1>d2
			If d2>range
				out#=lock+range
			EndIf
		Else
			If d1>range
				out#=lock-range
			EndIf
		EndIf
	Else
		d1#=360-lock+from
		d2#=lock-from
		If d1>d2
			If d2>range
				out#=lock-range
			EndIf
		Else
			If d1>range
				out#=lock+range
			EndIf
		EndIf
	EndIf
;	If out<0. out=360.-Abs(out)
;	If out>360. out=out-360.
If KeyDown(57) DebugLog "Out>"+out

	Return out
	
End Function

;-----
;The quat lib below here is not by me, it's public domain code wrote by vector. (Inform me if I've remembered your name wrong ;) )

Type Rotation
Field pitch#, yaw#, roll#
End Type

Type Quat
Field w#, x#, y#, z#
End Type

; Change these constants if you notice slips in accuracy
Const QuatToEulerAccuracy# = 0.001
Const QuatSlerpAccuracy#   = 0.0001


Function rotation.rotation(pitch#,yaw#,roll#)
	r.rotation=New rotation
	r\pitch=pitch
	r\yaw=yaw
	r\roll=roll
	Return r
End Function

Function copyQuat.quat(in.quat)
	out.quat=New quat
	out\w=in\w
	out\x=in\x
	out\y=in\y
	out\z=in\z
	Return out
End Function


Function quat.quat(w#,x#,y#,z#)
	q.quat=New quat
	q\w=w
	q\x=x
	q\y=y
	q\z=z
	Return q
End Function
Function setQuat(q.quat,w#,x#,y#,z#)
	q\w=w
	q\x=x
	q\y=y
	q\z=z
End Function

Function setRotation(r.rotation,pitch#,yaw#,roll#)
	r\pitch=pitch
	r\yaw=yaw
	r\roll=roll
End Function



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
sinv = ((2. * src\y * src\z) + (2 * src\w * src\x)) / cost
cosv = (1. - (2. * src\x * src\x) - (2 * src\y * src\y)) / cost
sinf = ((2. * src\x * src\y) + (2 * src\w * src\z)) / cost
cosf = (1. - (2. * src\y * src\y) - (2 * src\z * src\z)) / cost
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

If (1.0 - cosom) > QuatSlerpAccuracy
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
Function MultiplyQuat(q1.Quat, q2.Quat)
Local a#, b#, c#, d#, e#, f#, g#, h#

a = (q1\w + q1\x) * (q2\w + q2\x)
b = (q1\z - q1\y) * (q2\y - q2\z)
c = (q1\w - q1\x) * (q2\y + q2\z)
d = (q1\y + q1\z) * (q2\w - q2\x)
e = (q1\x + q1\z) * (q2\x + q2\y)
f = (q1\x - q1\z) * (q2\x - q2\y)
g = (q1\w + q1\y) * (q2\w - q2\z)
h = (q1\w - q1\y) * (q2\w + q2\z)

q1\w = b + (-e - f + g + h) / 2.
q1\x = a - ( e + f + g + h) / 2.
q1\y = c + ( e - f + g - h) / 2.
q1\z = d + ( e - f - g + h) / 2.
End Function

; convenience function to fill in a rotation structure
Function FillRotation(r.Rotation, pitch#, yaw#, roll#)
r\pitch = pitch
r\yaw = yaw
r\roll = roll
End Function
