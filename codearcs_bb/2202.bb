; ID: 2202
; Author: Nebula
; Date: 2008-01-20 16:02:13
; Title: 2 point Angle difference
; Description: Function Returns the difference

Function volledigehoek#(x1,y1,x2,y2) ; x2 y2 naar
	Local hoek = 0
	Local laagste = 1024
	For i=0 To 360
		x3 = x1+Cos(hoek) * 211
		y3 = y1+Sin(hoek) * 211
		hoek = hoek + 1
		nieuw = Sqr((x3-x2)^2+(y3-y2)^2)
		If nieuw < laagste Then laagste = nieuw : uitgraad = i
	Next
	Return uitgraad
End Function
