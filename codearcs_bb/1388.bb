; ID: 1388
; Author: fredborg
; Date: 2005-05-31 15:54:43
; Title: Interpolate Angles
; Description: Interpolate between two angles...

Function interpolate_angle#(a#,b#,blend#=0.5)
	ix# = Sin(a)
	iy# = Cos(a)
	jx# = Sin(b)
	jy# = Cos(b)
	Return ATan2(ix-(ix-jx)*blend,iy-(iy-jy)*blend)
End Function
