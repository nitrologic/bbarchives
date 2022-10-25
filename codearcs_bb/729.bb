; ID: 729
; Author: poopla
; Date: 2003-06-27 07:36:51
; Title: rectangle picking (good for RTSs)
; Description: PickRect( x, y, width, height )

;Fast rect picking
;coded by michael conaway

Type Entity
	Field entity ;this field has to be a valid mesh or blitz entity before calling PickRect()
	Field picked ;a flag for checking if the entity instance was picked successfully
End Type

Function PickRect(cam, x, y, width, height) ;specify the rect's X,Y coord and it's dimensions.
	;cam is the camera you wish to pick from
	For E.Entity = Each Entity
		e\picked = False;Reset picking flag <-Could stick this line in a CleanPick() func for clarity
		
		;find the entity's coords on the screen(2d)
		CameraProject cam, EntityX#(e\entity), EntityY#(e\entity), EntityZ#(e\entity)
		PX = ProjectedX()
		PY = ProjectedY()
		
		;Check to see if the entity is within this rectangle
		If PX > x And PX < x + width 
			If PY > y And PY < y + height
				e\picked = True
			EndIf
		EndIf
	Next
End Function

Function EntityRectPicked(e.entity) ;a simple function to return if an entity has been PickRect()'d 
	Return e\picked
End Function
				 
;you could also use 'picked' as a variable for weather or not the entity is selected, when it comes time
;To issue commands, check for entity instances with picked = true, then when you find them, issue the
;command to your units.  (If you are working on an rts)
