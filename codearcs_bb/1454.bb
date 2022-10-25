; ID: 1454
; Author: Grey Alien
; Date: 2005-08-25 19:51:55
; Title: Shadow Text
; Description: Basic black shadow under text (and rectangle)

; -----------------------------------------------------------------------------
; ccShadowText
; -----------------------------------------------------------------------------
Function ccShadowText(x%, y%, TheText$, Centre)
	;make a black shadow in the same font behind the text so it shows up on top of images
        Local ShadowTextDepth% = 1

	;first store the current color
	red = ColorRed()
	green = ColorGreen()
	blue = ColorBlue()
	
	Color 0,0,0
	Text x + ShadowTextDepth, y + ShadowTextDepth, TheText, Centre
	Color red, green, blue
	Text x, y, TheText, Centre	
End Function

Function ccShadowRect(x, y, w, h)	
	;draw a shadow first
	Color 0,0,0
	Rect x+4, y+4, w, h, 1 
	;now a grey rect	
	Color 150,150,150
	Rect x, y, w, h, 1 	
End Function
