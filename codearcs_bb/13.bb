; ID: 13
; Author: BlitzSupport
; Date: 2001-08-16 21:43:03
; Title: LabelEntity (camera, entity, text$)
; Description: Simple function to clearly label an entity when it's in view

Function LabelEntity (camera, entity, label$)
	If EntityInView (entity, camera)
		CameraProject camera, EntityX (entity), EntityY (entity), EntityZ (entity)
		w = StringWidth (label$)
		h = StringHeight (label$)
		x = ProjectedX () - (w / 2) - 1
		y = ProjectedY () - (h / 2) - 1
		Color 0, 0, 0
		Rect x, y, w + 2, h + 2, 1
		Color 255, 255, 255
		Text x, y, label$
	EndIf
End Function
