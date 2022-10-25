; ID: 2
; Author: BlitzSupport
; Date: 2001-08-16 22:12:49
; Title: Lens effect
; Description: Circular lens effect over whatever's on-screen

; -----------------------------
; This MUST be included!!!
; -----------------------------
lensDiameter = 60
; -----------------------------
Dim Tfm  (lensDiameter * lensDiameter * 2)
Dim Org  (lensDiameter * lensDiameter * 2, 3)
Dim Dest (lensDiameter * lensDiameter * 2, 3)
; -----------------------------


; -----------------------------
;  Lens effect by maLi/FiNESSE,
;  ported to Blitz by someone
;  whose name I don't have
;  anymore (sorry!), converted
;  to use Read/WritePixelFast
;  by blitzsupport@guildhallleisure.com
; -----------------------------

Graphics 640, 480
backpicture = LoadImage ("brick.bmp")

SetBuffer BackBuffer()
MoveMouse GraphicsWidth () / 2, GraphicsHeight () / 2

CreateLens (lensDiameter, 15)

Repeat

	Cls
	TileImage backpicture
	
	x = MouseX ()
	y = MouseY ()
	
	If x => GraphicsWidth  () - lensDiameter Then x = GraphicsWidth  () - lensDiameter
	If y >= GraphicsHeight () - lensDiameter Then y = GraphicsHeight () - lensDiameter

	DrawLens (x, y, lensDiameter)

	Flip

Until KeyDown (1)

End

Function CreateLens (diameter, magnification)
	r = Int (diameter / 2)
	s# = Sqr# (r * r - magnification * magnification)
	For y = -r To -r + (diameter)
		For x = -r To r + (diameter - 1)
			If (x * x + y * y) >= (s * s)
				a = x
				b = y
			Else
				z = Sqr# (r * r - x * x - y * y)
				a = Int (x * magnification / z + 0.5)
				b = Int (y * magnification / z + 0.5)
			EndIf
			Tfm (1 + (y + r) * diameter + (x + r)) = (b + r) * diameter + (a + r)
		Next
	Next
End Function

Function DrawLens (x, y, diameter)
	LockBuffer BackBuffer ()
	For i = x To (x + diameter) - 1
		For j = y To (y + diameter) - 1
			rgb = ReadPixelFast (i, j)
			Org (cx, 1) = rgb Shr 16 And %11111111
			Org (cx, 2) = rgb Shr 8 And %11111111
			Org (cx, 3) = rgb And %11111111
			cx = cx + 1
		Next
	Next
	cx = 1
	For i = x To (x + diameter) - 1
		For j = y To (y + diameter) - 1
			Dest (cx, 1) = Org (Tfm (cx), 1)
			Dest (cx, 2) = Org (Tfm (cx), 2)
			Dest (cx, 3) = Org (Tfm (cx), 3)
			WritePixelFast i, j, Dest (cx, 3) Or (Dest (cx, 2) Shl 8) Or (Dest (cx, 1) Shl 16)
			Plot (i, j)
			cx = cx + 1
		Next
	Next
	UnlockBuffer BackBuffer ()
End Function
