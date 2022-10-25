; ID: 1887
; Author: thelizardking
; Date: 2006-12-24 02:55:38
; Title: SetRes( )
; Description: Sets the graphics mode to the current desktop resolution! UPDATED

SetRes()

While Not KeyHit(1)
	Text 0,0,"OK, graphic mode has been set!"
	Text 0,12, GraphicsWidth()+"x"+GraphicsHeight()
	Flip
Wend

Function SetRes();This function is for BlitzPlus ONLY - it sets the graphics mode to the current resolution

graphics gadgetwidth(desktop()),gadgetheight(desktop());get the width and height of the desktop - the current screen

End Function
