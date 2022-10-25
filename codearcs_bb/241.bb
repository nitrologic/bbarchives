; ID: 241
; Author: Phish
; Date: 2002-02-14 10:42:12
; Title: Transparent 2D GUI Example
; Description: A cut down set of functions using a clever method of achieving transparency in "2D" windows.

;--------------------------;
; -- Unity				-- ;
; -- Joseph Humfrey		-- ;
; -- phish@aelius.com	-- ;
; -- February 2002		-- ;
;--------------------------;

;--------------------- CODE FOR GUI DRAWING ---------------------------
;-- INCLUDED: 3D TRANSPARENT BACKGROUND CODE + 2D DRAWING  ------------
;----------------------------------------------------------------------

;-----------------------------------
;---- D A T A   H A N D L I N G ----
;-----------------------------------

;GUI distance from the camera
;Change this depending on the scale of your game world
Const GUI_Distance# = 1000

;If you use the entity order for any other stuff,
;you may wish to change this. Otherwise, just leave it.
Const GUI_DrawOrder = -1

;Transparency of the windows
Const GUI_Alpha# = 0.9

;Blend mode of the windows.
;3 is nifty effect, but if your window is
;on top of a white background, and you have
;white text in the window, it will not be readable
Const GUI_BlendMode = 3

Type GUI_Window
	Field entity	;sprite entity for background
	
	Field width
	Field height
	
	Field x			;position
	Field y
End Type

Global GUI_windowPrototype


;----------------------------
;---- F U N C T I O N S -----
;----------------------------


; --------------------------------------------------------
Function InitialiseGUI()
; --------------------------------------------------------
	
	;If you want to give your window a background texture,
	;put it in here
	
	GUI_windowPrototype = CreateSprite()
		EntityColor GUI_windowPrototype, 20, 20, 50
		HideEntity GUI_windowPrototype

End Function


; --------------------------------------------------------
Function UpdateGUI()
; --------------------------------------------------------
	
	;Draw the border around each window
	Color 100, 100, 125
	For w.GUI_Window = Each GUI_Window
		Line w\x, w\y, w\x+w\width, w\y
		Line w\x, w\y, w\x, w\y+w\height
		Line w\x+w\width, w\y, w\x+w\width, w\y+w\height
		Line w\x, w\y+w\height, w\x+w\width, w\y+w\height
	Next
	
End Function

; --------------------------------------------------------
Function CreateNewWindow.GUI_window(width, height)
; --------------------------------------------------------
	window.GUI_Window = New GUI_Window
	
	window\entity = CopyEntity(GUI_windowPrototype, camera)
	
	window\width = width
	window\height = height
	
	;make it drawn after everything else (on top)
	EntityOrder window\entity, GUI_DrawOrder
	
	;Set the transparency
	EntityAlpha window\entity, GUI_Alpha
	
	;Set the blend mode
	EntityBlend window\entity, GUI_BlendMode
	
	;Scale the window, depending on the screen resolution
	ScaleSprite window\entity, Float(width)/GraphicsWidth()*GUI_Distance, Float(height)/GraphicsWidth()*GUI_Distance
	
	;Move into position - to the origin 0, 0
	PositionWindow(window, 0, 0)
	
	Return window
End Function




; --------------------------------------------------------
Function PositionWindow(window.GUI_window, x#, y#)
; --------------------------------------------------------
	
	;Store the values passed in
	window\x = x
	window\y = y
	
	;Resolution independant coordinates
	xPos# = (x/GraphicsWidth()) * 2*GUI_Distance - GUI_Distance
	xWidth# = (Float(window\width)/GraphicsWidth()) * 2*GUI_Distance

	yDist# = GUI_Distance * 0.75
	yPos# = (-y/GraphicsHeight()) * 2*yDist + yDist
	yHeight# = (Float(window\height)/GraphicsHeight()) * 2*yDist
	
	;Move into position
	PositionEntity window\entity, (xPos + xWidth/2), (yPos - yHeight/2), GUI_Distance
	
End Function






; --------------------------------------------------------
Function SizeWindow(window.GUI_window, width#, height#)
; --------------------------------------------------------

	;Store the values passed in
	window\width = width
	window\height = height

	;And resize with respect to the current graphics resolution
	ScaleSprite window\entity, Float(width)/GraphicsWidth()*GUI_Distance, Float(height)/GraphicsWidth()*GUI_Distance

	;We have to put it in the right postion, so that it scales towards
	;the top left. Try commenting this line out if you don't understand...
	PositionWindow(window, window\x, window\y)

End Function
