; ID: 2137
; Author: big10p
; Date: 2007-11-03 09:23:38
; Title: point_in_polygon
; Description: Fast, compact function to determine if a point lies inside a polygon.

Graphics 800,600,0,2
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Const POLY_VERTS = 100

; Create banks to hold polygon's vertex X/Y coords.
bank_x = CreateBank(POLY_VERTS * 4)
bank_y = CreateBank(POLY_VERTS * 4)

; Create a random, convex polygon.
da# = 360.0 / POLY_VERTS
ang# = 0.0

For i = 0 To (BankSize(bank_x) - 1) Step 4
	dist# = Rnd(50,300)
	PokeFloat bank_x, i, (GraphicsWidth() / 2) + (Cos(ang) * dist)
	PokeFloat bank_y, i, (GraphicsHeight() / 2) + (Sin(ang) * dist)
	ang = ang + da
Next
	
; Main loop.
While Not KeyHit(1)

	Cls
		
	inside = point_in_polygon(MouseX(), MouseY(), bank_x, bank_y)
		
	If inside
		Color 255,0,0
		status$ = "INSIDE"
	Else
		Color 255,255,255
		status$ = "OUTSIDE"
	EndIf
		
	draw_polygon(bank_x, bank_y)
		
	Color 255,255,0
	Text 10,10,"Mouse is " + status$ + " polygon!"
		
	Flip 1

Wend

End


;
; Determines whether a point lies inside a convex polygon.
;
; Params:
; x,y    - Coords of point to check.
; vert_x - Float bank holding polygon vertex X coords.
; vert_y - Float bank holding polygon vertex Y coords.
;
; Returns:
; True if the point is inside the polygon, False otherwise.
;
Function point_in_polygon(x#, y#, vert_x, vert_y)

	in = False
	
	last_byte = BankSize(vert_x) - 1

	For i = 0 To last_byte Step 4
	
		If i Then j = (i - 4) Else j = (last_byte - 3)
		
		x1# = PeekFloat(vert_x,i)
		y1# = PeekFloat(vert_y,i)

		x2# = PeekFloat(vert_x,j)
		y2# = PeekFloat(vert_y,j)

		If ((((y1 <= y) And (y < y2)) Or ((y2 <= y) And (y < y1))) And (x < (((x2 - x1) * (y - y1)) / (y2 - y1)) + x1))
			in = Not in
		EndIf

	Next

	Return in
	
End Function


;
; Draws a polygon.
;
; Params:
; vert_x - Float bank holding polygon vertex X coords.
; vert_y - Float bank holding polygon vertex Y coords.
;
Function draw_polygon(vert_x, vert_y)

	last_byte = BankSize(vert_x) - 1

	For i = 0 To last_byte Step 4
	
		If i Then j = (i - 4) Else j = (last_byte - 3)
		
		x1# = PeekFloat(vert_x,i)
		y1# = PeekFloat(vert_y,i)

		x2# = PeekFloat(vert_x,j)
		y2# = PeekFloat(vert_y,j)

		Line x1, y1, x2, y2

	Next

End Function
