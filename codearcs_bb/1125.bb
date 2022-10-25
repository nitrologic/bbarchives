; ID: 1125
; Author: AntMan - Banned in the line of duty.
; Date: 2004-08-04 12:46:36
; Title: SmoothTrack
; Description: Track an entity smoothly

Function smoothTrack(ent1,ent2,speed#=0.2)
	np#=sang(EntityPitch(ent1),EntityPitch(ent2),speed)
	ny#=sang(EntityYaw(ent1),EntityYaw(ent2),speed)
	nr#=sang(EntityRoll(ent1),EntityRoll(ent2),speed)
	RotateEntity ent1,np,ny,nr
End Function

Function sang#(ang1#,ang2#,spd#)
	If ang1>ang2
		d1#=ang1-ang2
		d2#=360.-ang1+ang2
		If d1<d2
			out#=ang1-(d1*spd)
		Else
			out#=ang1+(d2*spd)
		EndIf
	Else
		d1#=ang2-ang1
		d2#=360.-ang2+ang1
		If d1<d2
			out#=ang1+(d1*spd)
		Else
			out#=ang1-(d2*spd)
		EndIf
	EndIf
	If out<0. out=360.-Abs(out)
	If out>360. out=out-360.
	Return out
End Function
