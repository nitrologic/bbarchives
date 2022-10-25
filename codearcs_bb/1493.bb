; ID: 1493
; Author: Andres
; Date: 2005-10-19 14:18:57
; Title: AnimLine
; Description: Animating Line

Function AnimatingLine(x1, y1, x2, y2)
	Local dis = Sqr((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
	
	For i = 1 To dis
		flt# = Float i / dis
		x = x1 + (x2 - x1) * flt#
		y = y1 + (y2 - y1) * flt#
		a =  i * 10 + MilliSecs() / 2
			red = (Cos(a) + 1) * .5 * 255
			green = (Cos(a) + 1) * .5 * 255
			blue = (Cos(a) + 1) * .5 * 255
		WritePixel x, y, (blue Or (green Shl 8) Or (red Shl 16) Or ($FF000000)), GraphicsBuffer()
	Next
End Function
