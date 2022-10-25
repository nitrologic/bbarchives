; ID: 2868
; Author: Andy_A
; Date: 2011-07-03 19:29:34
; Title: Filled Rotated Ellipses
; Description: Rotate an ellipse to any angle and fill it!

;     Title: Filled Rotated Ellipses
;Programmer: Andy Amaya
;      Date: 2011.06.20

;=======================================================
;FillTriangle by Tom Toad
;http://www.blitzmax.com/codearcs/codearcs.php?code=1200
;=======================================================

AppTitle "Filled Rotated Ellipses"
Global sw = 800 : sh = 600
Graphics sw, sh, 32, 2
ClsColor 255,222,173

numOvals = 30

While MouseHit(1) = 0
	Cls
	LockBuffer()
	st = MilliSecs()
	For i = 1 To numOvals
		Color Rand(0,255),Rand(0,255),Rand(0,255)
		ovalWide = Rand(20,300)
		ovalHigh = ovalWide/2
		ovalCentX = Rand(ovalWide/2,sw-ovalWide/2)
		ovalCenty = Rand(ovalWide/2+20,sh-ovalWide/2-20)
		angle# = Rand(0.0,359.0)
		rEllipse(ovalCentX, ovalCentY,ovalWide/2,ovalHigh/2,30, angle, 1)
	Next
	et = MilliSecs()-st
	UnlockBuffer()
	Color 0,0,0
	Text 5,5,"et: "+et
	Text sw/2,sh-15,"R-click for more ovals          L-click to exit",True
	Flip
	WaitMouse()
Wend

End

Function rEllipse(centerX%, centerY%, radius1#, radius2#, segments#, angle#, filled%=0)
;============================== Rotated Ellipse Function ==============================
; Parameters are:
;
; centerX, centerY coords locate center of ellipse to be plotted
; radius1 is width of ellipse along the X axis
; radius2 is height of ellipse along the Y axis
; segments is number of line segments used to draw the ellipse (minimum of 3)
; angle is number of degrees to rotate the ellipse			   (NOTE: 0 degrees = East)
; filled - default is 0 to draw an unfilled ellipse else draw a filled ellipse
;
; NOTE: This function is dependent on fillTriangle() function to perform the filling
;       of rotated ellipses.
;======================================================================================
	Local rca#, rsa#, incSize#, i#, ca#, sa#, x1%, y1%, x2%, y2%

	rca = Cos(angle) : rsa = Sin(angle)
	If segments < 3.0 Then segments = 3.0
	incSize = 360.0/segments
	If angle = 0 Then
		x1 = radius1+centerX : y1 = centerY
	Else
		x1 = rca*radius1+centerX : y1 = rsa*radius1+centerY
	End If
;	Plot x1, y1
	i = incSize
	While i <= 360.01
		ca = Cos(i)*radius1 : sa = Sin(i)*radius2
		If angle = 0.0 Then
			x2 = Int(ca + centerX) : y2 = Int(sa + centerY)
		Else
			x2 = Int(rca*ca-rsa*sa +centerX) : y2 = Int(rsa*ca+rca*sa +centerY)
		End If
		;if mode is zero draw an unfilled ellipse
		If filled = 0 Then
			Line x1, y1, x2, y2
		Else
			;otherwise use the fillTriangle function to fill current arc segment
			fillTriangle(centerX, centerY, x1, y1, x2, y2)
		End If
		x1 = x2 : y1 = y2
		i = i + incSize
	Wend
End Function

;=======================================================
;FillTriangle by Tom Toad
;http://www.blitzmax.com/codearcs/codearcs.php?code=1200
;=======================================================
Function fillTriangle(x1#,y1#,x2#,y2#,x3#,y3#)
	Local slope1#,slope2#,slope3#,x#,y#,length#

	;make sure the triangle coordinates are ordered so that x1 < x2 < x3
	If x2 < x1 Then x = x2: y = y2: x2 = x1: y2 = y1: x1 = x: y1 = y
	If x3 < x1 Then x = x3: y = y3: x3 = x1: y3 = y1: x1 = x: y1 = y
	If x3 < x2 Then x = x3: y = y3: x3 = x2: y3 = y2: x2 = x: y2 = y
	
	If x1 <> x3 Then slope1 = (y3-y1)/(x3-x1)
	length = x2 - x1
	;draw the first half of the triangle
	If length <> 0 Then
		slope2 = (y2-y1)/(x2-x1)
		For x = 0 To length
			Line x+x1,x*slope1+y1,x+x1,x*slope2+y1
		Next
	End If

	y = length*slope1+y1
	length = x3-x2
	;draw the second half
	If length <> 0 Then
		slope3 = (y3-y2)/(x3-x2)
		For x = 0 To length
			Line x+x2,x*slope1+y,x+x2,x*slope3+y2
		Next
	End If
End Function
