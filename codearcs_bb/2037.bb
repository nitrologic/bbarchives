; ID: 2037
; Author: Andy_A
; Date: 2007-06-12 08:09:58
; Title: Point In Polygon
; Description: Check for point in n-sided polygon: uses crossing method

;Point in Polygon
;ported to Blitz+
;by Andy Amaya
;Jun 11,2007

;Source: http://local.wasp.uwa.edu.au/~pbourke/geometry/insidepoly/
;using the C routine attributed to Randolph Franklin (shown below)
;
;    int pnpoly(int npol, float *xp, float *yp, float x, float y)
;    {
;      Int i, j, c = 0;
;      For (i = 0, j = npol-1; i < npol; j = i++) {
;        If ((((yp[i] <= y) && (y < yp[j])) ||
;             ((yp[j] <= y) && (y < yp[i]))) &&
;            (x < (xp[j] - xp[i]) * (y - yp[i]) / (yp[j] - yp[i]) + xp[i]))
;          c = !c;
;      }
;      Return c;
;    }

AppTitle "Point in Polygon"
Global sw = 640
Global sh = 480

Graphics sw,sh,32,2
SeedRnd MilliSecs()
Dim xp#(99), yp#(99)

Repeat
	Cls
	minX# = 640
	maxX# = 0
	minY# = 480
	maxY# = 0
	drawGrid()

	st% = MilliSecs()
	numPoints% =  Rand(3,15)
	For i% = 0 To numPoints%-1
		xp(i%) = Rnd(100,540)
		yp(i%) = Rnd(60,420)
		minX = min(minX, xp(i%))
		maxX = max(maxX, xp(i%))
		minY = min(minY, yp(i%))
		maxY = max(maxY, yp(i%))
	Next
	Color 0,255,0
	Text 5,0,"Number of points = "+numPoints

	Color 255,0,32
	For x# = minX To maxX
			r# = 255.0
			Color r,0,32
		For y# = minY To maxY
			If y And 1 Then
				r = r - 1.4
				Color r,0,32
			End If
			If pointInPoly(x,y,numPoints%) Then Plot x,y
		Next
	Next
	
	Color 255,255,255
	For i% = 0 To numPoints%-2
		Line xp(i%), yp(i%), xp(i%+1), yp(i%+1)
	Next
	Line xp(numPoints%-1), yp(numPoints%-1), xp(0), yp(0)	

	Color 0,255,0
	et = MilliSecs()-st
	Text 5,15,"ET: "+et+" ms"
	Text 150,460,"Press a key to continue. Press [ESC] to exit."
	
	;==================================================================
	;== Un-comment to show rectangle defined by minX,minY, maxX,maxY ==
	;==================================================================
	;Rect minX, minY, maxX-minX+1, maxY-minY+1, False
	;==================================================================
	
	Flip
	WaitKey

Until KeyHit(1)
End

Function pointInPoly(x#, y#, points%)
	Local i%, j%, c%
	Local v1#, v2#, v3#, v4#, v5#, v6#, v7#
	c = 0
	For i = 0 To points-1
		j = (i+1) Mod points
		v1 = (yp(i) <= y)
		v2 = (y < yp(j))
		v3 = (yp(j) <= y)
		v4 = (y < yp(i))
		v5 = ( xp(j)-xp(i) ) * ( y-yp(i) )
		v6 = (yp(j)-yp(i))
		If v6 = 0.0 Then v6 = 0.0001
		v7 = xp(i)
		If (   ( (v1 And v2)  Or ( v3 And v4) ) And ( x < v5 / v6 + v7)   ) Then c = 1 - c
	Next
	Return c
End Function

Function min#(flt1#, flt2#)
	If flt1 < flt2 Then Return flt1 Else Return flt2
End Function

Function max#(flt1#, flt2#)
	If flt2 > flt1 Then Return flt2 Else Return flt1
End Function

Function drawGrid()
    Color 96,96,96
    For x = 20 To sw-20 Step 20
        Line x,40,x, sh-40
    Next
    For y = 40 To sh-40 Step 20
        Line 20,y,sw-20,y
    Next
End Function
