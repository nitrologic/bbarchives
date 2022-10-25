; ID: 145
; Author: Entity
; Date: 2001-12-19 06:03:48
; Title: Bezier Curves v1.2
; Description: Simple recursive bezier curve draw function

;
; Recursive Bezier Curve Demo (v1.2)
; by Jamie "Entity" van den Berge <entity@vapor.com>
;
 
Type Bezier
	Field  sx#,  sy#	; startpoint
	Field  ex#,  ey#	; endpoint
	Field csx#, csy#	; startpoint's controlpoint
	Field cex#, cey#	; endpoint's controlpoint
End Type

Function DrawBezier( depth, b.Bezier )
	Local acsx#, acsy#

	If depth > 0
		Local l.Bezier = New Bezier, r.Bezier = New Bezier
		; we haven't reached desired precision level yet,
		; so we subdivide into two smaller curves.

		l\sx = b\sx: l\sy = b\sy
		r\ex = b\ex: r\ey = b\ey

		l\csx = (b\sx + b\csx) / 2.0: r\cex = (b\ex + b\cex) / 2.0
		l\csy = (b\sy + b\csy) / 2.0: r\cey = (b\ey + b\cey) / 2.0

	    acsx = (b\csx + b\cex) / 2.0	; X control point average
		acsy = (b\csy + b\cey) / 2.0	; Y control point average

		l\cex = ( l\csx + acsx ) / 2.0 : r\csx = ( r\cex + acsx ) / 2.0
        l\cey = ( l\csy + acsy ) / 2.0 : r\csy = ( r\cey + acsy ) / 2.0

		l\ex  = ( l\cex + r\csx ) / 2.0: r\sx = l\ex
		l\ey  = ( l\cey + r\csy ) / 2.0: r\sy = l\ey

		depth = depth - 1
		DrawBezier( depth, l ): Delete l	; subdivide left
		DrawBezier( depth, r ): Delete r	; subdivide right
	Else
		Line b\sx, b\sy, b\ex, b\ey
	EndIf
End Function


;---------
; EXAMPLE
;---------

b.Bezier = New Bezier

SeedRnd MilliSecs()

While Not KeyHit( 1 )
	; set startpoint to previous end point
	b\sx = b\ex: b\sy = b\ey

	; set startpoint's controlpoint to previous end point's inverse control point
	b\csx = b\sx+(b\sx-b\cex)
	b\csy = b\sy+(b\sy-b\cey)

	; pick a new endpoint
	b\ex  = Rand( 0, GraphicsWidth()-1 ): b\ey = Rand( 0, GraphicsHeight()-1 )

	; pick a random control point for the new end point.
	b\cex = Rand( 0, GraphicsWidth()-1 ): b\cey = Rand( 0, GraphicsHeight()-1 )
	
	; and draw the curve
	DrawBezier( 5, b )

	Delay 400
Wend
Delete b
End
