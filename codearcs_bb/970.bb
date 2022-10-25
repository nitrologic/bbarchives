; ID: 970
; Author: Jeremy Alessi
; Date: 2004-03-18 00:18:24
; Title: 2D Multiple Resolution Helpers
; Description: Use these functions with Text calls or other 2D calls to position or scale things correctly in higher than 640 X 480 Resolutions.

;====== CORRECTX ==========================================================

Function CorrectX(pixel)
	
	Return ( pixel * GraphicsWidth() / 640 )

End Function

;==========================================================================

;====== CorrectY ==========================================================

Function CorrectY(pixel)
	
	Return ( pixel * GraphicsHeight() / 480 ) 

End Function 

;==========================================================================
