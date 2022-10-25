; ID: 1855
; Author: Andy_A
; Date: 2006-11-07 22:48:34
; Title: Yet Another Line Intersect Function
; Description: Function to check if two line segments cross

;Lines Intersect Function
;  	By: Andy Amaya
; Date: Nov 07, 2006

AppTitle "Line Intersection Function by Andy_A"

Graphics 800,600,32,2
SetBuffer BackBuffer()

Global intersectX%, intersectY%
SeedRnd MilliSecs()

While KeyHit(1) = 0
	Cls
	x1 = Rand(0,799) : y1 = Rand(60,599)
	x2 = Rand(0,799) : y2 = Rand(60,599)
	Line(x1,y1, x2,y2)

	x3 = Rand(0,799) : y3 = Rand(60,599)
	x4 = Rand(0,799) : y4 = Rand(60,599)
	Line(x3,y3, x4,y4)

	result = linesIntersect(x1,y1,x2,y2,x3,y3,x4,y4)

	Text 0,0,"Result= "+result
	If result = True
		Text(10,20,"Intersection Coords")
		Text(10,40,"   X="+ Str(intersectX) +" , Y="+Str(intersectY))
		Color 255,0,0
		Oval intersectX-3, intersectY-3, 7, 7, True
		Color 255,255,255
	Else
		Text(10,20,"No Intersection")
	End If

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
;		Text(10,20,"No intersection")
;		If numeratorA = 0.0 And numeratorB = 0.0 And denominator = 0.0 Then
;			Text(10,40,"Lines are coincident")
;		Else
;			Text(10,40,"Lines are parallel")
;		End If
		Return False
	Else
		Ua# = numeratorA/denominator
		Ub# = numeratorB/denominator
		range1% = Ua >= 0.0 And Ua <= 1.0
		range2% = Ub >= 0.0 And Ub <= 1.0
		If range1 And range2 Then
			intersectX% = Floor(x1 + Ua*(x2-x1)+.5)
			intersectY% = Floor(y1 + Ua*(y2-y1)+.5)
			Return True
		Else
;			Text(10,20,"No intersection")
			Return False
		End If
    End If
End Function
