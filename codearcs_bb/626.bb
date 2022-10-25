; ID: 626
; Author: BlitzSupport
; Date: 2003-03-16 12:42:28
; Title: PointInQuad
; Description: Checks whether a point is inside a given quad

; Generic/on-the-fly version (version lower down uses pre-defined quads)...
; (The first two parameters, x and y, specify the point you're checking.)

Function GenericPointInQuad (x, y, x0, y0, x1, y1, x2, y2, x3, y3)
	If ((y - y0) * (x1 - x0)) - ((x - x0) * (y1 - y0)) <= 0 Then Return 0
	If ((y - y1) * (x2 - x1)) - ((x - x1) * (y2 - y1)) <= 0 Then Return 0
	If ((y - y2) * (x3 - x2)) - ((x - x2) * (y3 - y2)) <= 0 Then Return 0
	If ((y - y3) * (x0 - x3)) - ((x - x3) * (y0 - y3)) <= 0 Then Return 0
	Return True
End Function









; Pre-defined quad version...

; PointInQuad ()

; Returns True if a point is inside a quad (must be clockwise-defined)...

; Quad definition...

Type Quad
	Field x [3]
	Field y [3]
End Type

; x0, y0, etc, are the x/y positions of each corner of the quad. Define
; them in clockwise order...

Function CreateQuad.Quad (x0, y0, x1, y1, x2, y2, x3, y3)
	q.Quad = New Quad
	q\x[0] = x0: q\y[0] = y0
	q\x[1] = x1: q\y[1] = y1
	q\x[2] = x2: q\y[2] = y2
	q\x[3] = x3: q\y[3] = y3
	Return q
End Function

; Quick quad draw-er...

Function DrawQuad (q.Quad)
	Line q\x [0], q\y [0], q\x [1], q\y [1]
	Line q\x [1], q\y [1], q\x [2], q\y [2]
	Line q\x [2], q\y [2], q\x [3], q\y [3]
	Line q\x [3], q\y [3], q\x [0], q\y [0]
End Function

; PointInQuad -- actually checks each line in turn to see if the point
; is on one side or the other; if all are on the 'correct' side (I think
; it's the left side of the line in this case), then we're inside the quad...

; Set BORDERLINE to True if you consider 'on the line' results to be inside the quad (slight inaccuracies when you check at the end of each line though)...
Const BORDERLINE = False ; True

Function PointInQuad (x, y, q.Quad)
	inside = True
	For point = 0 To 3
		If point < 3 Then nextpoint = point + 1 Else nextpoint = 0
		result = ((y - q\y [point]) * (q\x [nextpoint] - q\x [point])) - ( (x - q\x [point]) * (q\y [nextpoint] - q\y [point]))
		If result = 0
			inside = BORDERLINE: Exit
		Else
			If result < 0 Then inside = False: Exit
		EndIf
	Next
	Return inside
End Function



; Quick demo...

AppTitle "Position mouse inside and outside quad..."

Graphics 640, 480, 0, 2
SetBuffer BackBuffer ()

poly.Quad = CreateQuad (100, 100, 400, 200, 300, 400, 120, 300)

Repeat
	
	Cls
	
	DrawQuad (poly)
		
	If PointInQuad (MouseX (), MouseY (), poly)
		Text 20, 20, "Inside quad!"
	Else
		Text 20, 20, "Outside quad!"
	EndIf
	
	Flip
	
Until KeyHit (1)

End
