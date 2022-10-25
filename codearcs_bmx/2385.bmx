; ID: 2385
; Author: Underwood
; Date: 2008-12-25 17:12:37
; Title: Text 'Shadow'
; Description: Draw text on screen with a simple shadow.

Function DrawText2(s:String,x%,y%,shadowoffset%,r%,g%,b%)

SetColor(Max(0,r - 100),(Max(0,g - 100),Max(0,b - 100))
	DrawText(s,x+shadowoffset,y+shadowoffset)
SetColor(r,g,b)
	DrawText(s,x,y)

End Function
