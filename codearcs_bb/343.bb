; ID: 343
; Author: Sunteam Software
; Date: 2002-08-26 15:32:13
; Title: 2D Alpha Blending (as an include file)
; Description: An includable 2D alpha blender (Modified July 2003)

; ================================================
; Small Alpha routines by Mike of Sunteam Software
; ================================================

; Modified by Matt Sephton (July 2003)

Global alphamw,alphamh

; readargb
;
; Params: a as an alpha type (see below), buffer to be read from (must be locked before), x position of pixel, y position of pixel
Function readargb(a.alpha,buffer,x,y)
	rrgb=ReadPixelFast(x,y,buffer)
	a\r = (rrgb And $FF0000) Shr 16
	a\g = (rrgb And $00FF00) Shr 8
	a\b = (rrgb And $0000FF)
End Function

; writeargb
;
; Params: a as an alpha type (see below), buffer to write to (must be locked before), x position of pixel, y position of pixel
Function writeargb(a.alpha,buffer,x,y)
	If x>=0 And y>=0 And x<=alphamw And y<=alphamh Then
		argb = (a\r Shl 16) + (a\g Shl 8) + a\b
		WritePixelFast x,y,argb,buffer
	EndIf
End Function

; adjustalpha
;
; Params: a as an alpha type, red value (0-1 as float), green value (0-1 as float), blue value (0-1 as float)
Function adjustalpha(a.alpha,r#,g#,b#)
	a\r = (a\r*r#)
	a\g = (a\g*g#)
	a\b = (a\b*b#)
End Function

; drawimagealpha
;
; Params: srcimage handle (does not need locking and will be unlocked after),destbuffer (does not need locking and will be unlocked after),x,y,alpha level (0-1 as float), mask red, mask green, mask blue, operation (-1 for sub, 1 for add)
; NB: srcimage handle is an image handle not a buffer handle like dest buffer is!
; NBB: the mask is an int value in r,g,b representing the colour to be ignored (masked)
Function drawimagealpha(src,dest,x,y,a#,maskr,maskg,maskb,op)
	w = ImageWidth(src)-1
	h = ImageHeight(src)-1
	LockBuffer ImageBuffer(src)
	LockBuffer dest
	t.alpha = New alpha
	u.alpha = New alpha
	mr = maskr
	mg = maskg
	alphamw=GraphicsWidth()-1
	alphamh=GraphicsHeight()-1
	For o = 0 To h
		For n = 0 To w
			readargb(t,ImageBuffer(src),n,o)
			If Not (t\r = mr And t\g = mg And t\b = maskb)
				readargb(u,dest,x+n,y+o)
				
				If (op = 0) Then
					;multiply blend
					;dest = dest + (source - dest) * alpha
					u\r = u\r + (t\r - u\r) * a
					u\g = u\g + (t\g - u\g) * a
					u\b = u\b + (t\b - u\b) * a
				Else
					adjustalpha(t,a#,a#,a#)
					u\r = (u\r + (t\r*op))
					u\g = (u\g + (t\g*op))
					u\b = (u\b + (t\b*op))
				EndIf

				; check And remove any rollover
				;If u\r > $FF Then 
				;	u\r = $FF
				;ElseIf u\r < $0 
				;	u\r = $0
				;EndIf
				;If u\g > $FF Then
				;	u\g = $FF
				;ElseIf u\g < $0
				;	u\g = $0
				;EndIf
				;If u\b > $FF Then
				;	u\b = $FF
				;ElseIf u\b <$0
				;	u\b = $0
				;EndIf
				
				; faster rollover check
				u\r = u\r And $FF
				u\g = u\g And $FF
				u\b = u\b And $FF

				writeargb(u,dest,x+n,y+o)
			EndIf
		Next
	Next
	Delete t
	Delete u
	UnlockBuffer ImageBuffer(src)
	UnlockBuffer dest
End Function

; alpha type (red,green,blue as 2 bit ints)
Type alpha
	Field r,g,b
End Type
