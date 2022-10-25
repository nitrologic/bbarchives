; ID: 1856
; Author: Andy_A
; Date: 2006-11-07 23:01:21
; Title: Line-Bezier Intersect Demo
; Description: Check for intersection between Line Segment and Bezier Curve

;Line Segment/Bezier Curve Intersect Demo
;  	By: Andy Amaya
; Date: Nov 07, 2006

AppTitle "Line-Bezier Intersection Demo by Andy_A"
Graphics 800,600,32,2
SetBuffer BackBuffer()

;set high for accuracy, set low for speed
numBezierPoints% = 5000

Dim p%(numBezierPoints + 1,3), result%(numBezierPoints + 1)
Global intersectX%, intersectY%, n%
SeedRnd MilliSecs()


While KeyHit(1) = 0
	Cls
	x1% = Rand(175,799) : y1% = Rand(60,599)
	x2% = Rand(175,799) : y2% = Rand(60,599)
	Color 0,0,255
	Line(x1,y1, x2,y2)

	x3% = Rand(175,799) : y3% = Rand(60,599)
	x4% = Rand(175,799) : y4% = Rand(60,599)
	x5% = Rand(175,799) : y5% = Rand(60,599)
	x6% = Rand(175,799) : y6% = Rand(60,599)

	Color 255,255,0
	bezier(x3,y3, x4,y4, x5,y5, x6,y6, numBezierPoints)

	lineCount% = 0
	For j% = 0 To n%
		result(j) = linesIntersect(x1,y1, x2,y2, p(j,0),p(j,1), p(j,2),p(j,3) )
		If result(j)=1 Then
			lineCount = lineCount+1
			Color 0,255,0
			Text(130,lineCount*14,"X,Y="+Str(intersectX)+","+Str(intersectY))	
			Text(5,lineCount*14,"Segment "+Str(j)+"="+Str(result(j) ) )
		End If
	Next

	Flip
	WaitKey()
Wend
End

;Source: http://Local.wasp.uwa.edu.au/~pbourke/geometry/lineline2d/
;Intersection point of two lines
;(2 dimensions)

Function linesIntersect(x1%,y1%, x2%,y2%, x3%,y3%, x4%,y4%)

    numeratorA#  = (x4-x3)*(y1-y3)-(y4-y3)*(x1-x3)
    numeratorB#  = (x2-x1)*(y1-y3)-(y2-y1)*(x1-x3)
    denominator# = (y4-y3)*(x2-x1)-(x4-x3)*(y2-y1)

    If denominator = 0.0 Then
;        Text(10,20,"No intersection")
;        If numeratorA = 0.0 And numeratorB = 0.0 And denominator = 0.0 Then
;            Text(10,40,"Lines are coincident")
;        Else
;            Text(10,40,"Lines are parallel")
;        End If
        Return False
    Else
        Ua# = numeratorA/denominator
        Ub# = numeratorB/denominator
        range1% = Ua >= 0.0 And Ua <= 1.0
        range2% = Ub >= 0.0 And Ub <= 1.0
        If range1 And range2 Then
			intersectX% = Floor(x1 + Ua*(x2-x1)+.5)
			intersectY% = Floor(y1 + Ua*(y2-y1)+.5)

			;highlight point of intersection
			Color 255,0,0
			Oval intersectX-3, intersectY-3, 7, 7, True

			Return True
		Else
;			Text(10,20,"No intersection")
			Return False
		End If
	End If
End Function

;Source:   http://www.wikipedia.org/wiki/Bezier_curve
;The parametric form of the curve is:
;P(t) = A(1 - t)3 + 3Bt(1 - t)2 + 3Ct2(1 - t) + Dt3    Where 0.0 <= t <= 1.0
Function bezier (x0%, y0%, x1%, y1%, x2%, y2%, x3%, y3%, rate%)

	xt# = 1.0 / rate    ;rate = number of line segments defining Bezier curve
	xp# = 1.0 - xi#

	tmpX1# = x0
	tmpY1# = y0
	n%=0
	While xi < 1.0
		xi# = xi# + xt#
		xp# = 1.0 - xi#
		
		xp2# = xp# * xp#
		xi2# = xi# * xi#

		t1# = xp2# * xp#
		t2# = xp2# * xi# * 3.0
		t3# = xi2# * xp# * 3.0
		t4# = xi2# * xi#
		tmpX2# = (t1 * x0) + (t2 * x1) + (t3 * x2) + (t4 * x3)
		tmpY2# = (t1 * y0) + (t2 * y1) + (t3 * y2) + (t4 * y3)

		;Store these points defining line segment to be drawn
		;they will be used in testing for intersection		
		p(n,0)=tmpX1
		p(n,1)=tmpY1
		p(n,2)=tmpX2
		p(n,3)=tmpY2
		;count number of line seg coords to store
		n=n+1
		
		Line tmpX1, tmpY1, tmpX2, tmpY2
		tmpX1 = tmpX2
		tmpY1 = tmpY2
	Wend
End Function
