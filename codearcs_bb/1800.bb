; ID: 1800
; Author: Jesse B Andersen
; Date: 2006-08-31 15:19:05
; Title: Camera_Pick with range
; Description: Returns picked mesh within the given range

;Give me credit!
;xmlspy
Function Camera_Pick(cam, x#, y#, distance#)
	r = CameraPick(cam, x#, y#)
	If r <> 0 Then
		ix# = EntityX(cam, True)
		iy# = EntityY(cam,True)
		iz# = EntityZ(cam, True)
		
		dx# = PickedX()
		dy# = PickedY()
		dz# = PickedZ()
		
		x# = (dx#-ix#)^2
		y# = (dy#-iy#)^2
		z# = (dz#-iz#)^2
		
		d# = Sqr((x#+y#+z#))
		If d# < distance# Then
			Return r
		Else
			Return 0
		EndIf
	EndIf
End Function
