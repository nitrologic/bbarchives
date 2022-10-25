; ID: 2834
; Author: Andy_A
; Date: 2011-04-01 12:41:18
; Title: Intersection Points of Two Circles
; Description: Find the intersection of 2 circles

; Source: http://paulbourke.net/geometry/2circle/
; C source code example by Tim Voght

; Ported to BlitzPlus 2011.04.01
; by Andy_A


AppTitle "Circle to Circle intersection points"
Graphics 800,600,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()
Global xi1#, yi1#, xi2#, yi2# ;the intersection coords

While Not(KeyHit(1))
	Cls
	rad1# = Rand(10,20)*10.0
	rad2# = Rand(10,20)*10.0
	cx1# = Rand(rad1, 800-rad1)
	cy1# = Rand(rad1, 600-rad1)
	cx2# = Rand(rad2, 800-rad2)
	cy2# = Rand(rad2, 600-rad2)
	;show the circle center points
	Color 255,0,0
	Oval cx1-3,cy1-3,6,6,True
	Oval cx2-3,cy2-3,6,6,True
	;show the circles
	Color 255, 255, 0
	Oval cx1-rad1, cy1-rad1, rad1*2, rad1*2,False
	Oval cx2-rad2, cy2-rad2, rad2*2, rad2*2,False
	;check for intersection of circles
	chk% = c2c(cx1,cy1,rad1,cx2,cy2,rad2)
	Select chk
		Case -1
			Color 255,0,0
			Text 5,20,"One circle contains the other, no intersection"
		Case 0
			Color 255,255,0
			Text 5,20,"Circles do not intersect'
		Case 1
			Color 0,255,0
			Text 5,20,"The circles intersect at:"
			Text 5,40,xi1+", "+yi1
			Text 5,60,xi2+", "+yi2
			Oval xi1-3, yi1-3, 6, 6, True
			Oval xi2-3, yi2-3, 6, 6, True
			Color 255, 0, 255
			Line(xi1, yi1, xi2, yi2)
	End Select
	Color 255,255,255
	Text 400,580,"Press a key for more examples, [ESC] to exit",True
	Flip
	WaitKey()
Wend
End


Function c2c(x0#, y0#, r0#, x1#, y1#, r1#)
	Local a#, dx#, dy#, d#, h#, rx#, ry#, x2#, y2#

;  /* dx And dy are the vertical And horizontal distances between
;   * the circle centers.
;   */
	dx = x1 - x0;
	dy = y1 - y0;

;  /* Determine the straight-Line distance between the centers. */
	d = Sqr((dy*dy) + (dx*dx))


;  /* Check For solvability. */
	If (d > (r0 + r1)) Then
;    /* no solution. circles do Not intersect. */
		Return 0;
	End If

	If (d < Abs(r0 - r1)) Then
;    /* no solution. one circle is contained in the other */
		Return -1
	End If

;  /* 'point 2' is the point where the Line through the circle
;   * intersection points crosses the Line between the circle
;   * centers.  
;   */

;  /* Determine the distance from point 0 To point 2. */
	a = ((r0*r0) - (r1*r1) + (d*d)) / (2.0 * d) ;

;  /* Determine the coordinates of point 2. */
	x2 = x0 + (dx * a/d);
	y2 = y0 + (dy * a/d);

;  /* Determine the distance from point 2 To either of the
;   * intersection points.
;   */
	h = Sqr((r0*r0) - (a*a));

;  /* Now determine the offsets of the intersection points from
;   * point 2.
;   */
	rx = -dy * (h/d);
	ry = dx * (h/d);

;  /* Determine the absolute intersection points. */
	xi1 = x2 + rx;
	xi2 = x2 - rx;
	yi1 = y2 + ry;
	yi2 = y2 - ry;

 	Return 1;
End Function
