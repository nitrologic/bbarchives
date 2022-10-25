; ID: 2817
; Author: Andy_A
; Date: 2011-01-24 19:05:24
; Title: Bresenham like Circle and Ellipse functions
; Description: Bresenham: Circle & Ellipse

;Source:
;	http://homepage.smc.edu/kennedy_john/papers.htm
;	http://homepage.smc.edu/kennedy_john/belipse.pdf

;	A Fast Bresenham Type Algorithm For Drawing Ellipses
;	by 
;	John Kennedy

;	Blitzplus/Blitz 3D port by Andy_A

AppTitle "Bresenham Ellipse"
Graphics 800,600,32,2
SetBuffer BackBuffer()


numRepeats% = 100

LockBuffer GraphicsBuffer()

st = MilliSecs()
For i = 1 To numRepeats
	Ellipse(400, 300, 390, 290,$FFE020, 0)
Next
et1# = MilliSecs()-st

st = MilliSecs()
For i = 1 To numRepeats
	Oval 10, 10, 780, 580, 1
Next
et2# = MilliSecs()-st

UnlockBuffer


Text 5,	5,"Avg/Ellipse: "+(et1/Float(numRepeats))+"ms"
Text 5,20,"   Avg/Oval: "+(et2/Float(numRepeats))+"ms"


Flip
WaitMouse()
End

Function Ellipse(CX%, CY%, XRadius%, YRadius%, colr%, fill%);
	Local X%, Y%
	Local XChange%, YChange%
	Local EllipseError%
	Local TwoASquare%, TwoBSquare%
	Local StoppingX%, StoppingY%
	Color (colr And $FF0000) Shr 16, (colr And $FF00) Shr 8, colr And $FF

	TwoASquare = (XRadius*XRadius) Shl 1
	TwoBSquare = (YRadius*YRadius) Shl 1
	X = XRadius
	Y = 0
	XChange = YRadius*YRadius*(1-(XRadius Shl 1))
	YChange = XRadius*XRadius
	EllipseError = 0
	StoppingX = TwoBSquare*XRadius
	StoppingY = 0

	While StoppingX >= StoppingY 				; do {1st set of points, y' > -1}

		If fill <> 0 Then
			Line(CX-X, CY+Y, CX+X, CY+Y)		; used calc'd points to draw scan
			Line(CX-X, CY-Y, CX+X, CY-Y)		; lines from opposite quadrants
		Else
			WritePixelFast(CX+X, CY+Y, colr)	; {point in quadrant 1}
			WritePixelFast(CX-X, CY+Y, colr)	; {point in quadrant 2}
			WritePixelFast(CX-X, CY-Y, colr)	; {point in quadrant 3}
			WritePixelFast(CX+X, CY-Y, colr)	; {point in quadrant 4}
		End If

		Y = Y + 1
		StoppingY = StoppingY + TwoASquare
		EllipseError = EllipseError + YChange
		YChange = YChange + TwoASquare

		If (EllipseError Shl 1) + XChange > 0 Then
			X = X - 1
			StoppingX = StoppingX - TwoBSquare
			EllipseError = EllipseError + XChange
			XChange = XChange + TwoBSquare
		End If
	Wend
	
	;{ 1st set of points is done; start the 2nd set of points }
	X = 0
	Y = YRadius
	XChange = YRadius*YRadius
	YChange = XRadius*XRadius*(1 - (YRadius Shl 1))
	EllipseError = 0
	StoppingX = 0
	StoppingY = TwoASquare*YRadius

	While StoppingX <= StoppingY 				;do {2nd set of points, y' < -1}

		If fill <> 0 Then					
			Line(CX-X, CY+Y, CX+X, CY+Y)		; used calc'd points to draw scan
			Line(CX-X, CY-Y, CX+X, CY-Y)		; lines from opposite quadrants
		Else
			WritePixelFast(CX+X, CY+Y, colr)	; {point in quadrant 1}
			WritePixelFast(CX-X, CY+Y, colr)	; {point in quadrant 2}
			WritePixelFast(CX-X, CY-Y, colr)	; {point in quadrant 3}
			WritePixelFast(CX+X, CY-Y, colr)	; {point in quadrant 4}
		End If

		X = X + 1
		StoppingX = StoppingX + TwoBSquare
		EllipseError = EllipseError + XChange
		XChange = XChange + TwoBSquare

		If (EllipseError Shl 1) + YChange > 0 Then
			Y = Y - 1
			StoppingY = StoppingY - TwoASquare
			EllipseError = EllipseError + YChange
			YChange = YChange + TwoASquare
		End If
	Wend
End Function
