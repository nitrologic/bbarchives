; ID: 2867
; Author: Andy_A
; Date: 2011-07-03 17:31:08
; Title: Cartoon Speech Bubbles
; Description: Function to create cartoon speech bubbles

;     Title: Cartoon Speech Bubbles
;Programmer: Andy Amaya
;      Date: 2011.06.21

;	FillTriangle by Tom Toad
;http://www.blitzmax.com/codearcs/codearcs.php?code=1200

AppTitle "Cartoon Speech Bubbles"
Global sw, sh
sw = 800: sh = 600
Graphics sw, sh, 32, 2
SetBuffer BackBuffer()

Const black% = $00
Const darkblue%= $80
Const red%   = $FF0000
Const green% = $8000
Const palegreen%=$A0FFA0
Const gray1% = $D0D0D0
Const yellow%= $FFFFC0
Const white% = $FFFFFF
Const skyblue%=$20A0FF

setClsColor skyblue

While MouseHit(1) = 0
	angle# = (angle# + 20.0)
	If angle > 360.0 Then angle = angle - 360.0
	Cls
	colr = (colr + 1) Mod 4
	Select colr
		Case 0: fill = white: outline = black
		Case 1: fill = gray1: outline = black
		Case 2: fill = yellow: outline = darkblue
		Case 3: fill = palegreen: outline = green
	End Select
	
	sBubble(400, 300, 300, 150, Rand(30,200), angle, Rand(2,6), outline, fill)
	
	setColor red
	Text 400,260,"Speech bubbles can be",True
	Text 400,275, "small or large,", True
	Text 400,290,"and can be just about any color!",True

	setColor outline
	Text 400,335,"R-click for more bubbles",True
	Text 400,350,"L-click to exit",True
	Flip
	WaitMouse()
Wend

Function sBubble(x%, y%, ew%, eh%, tail%, angle#=0., pen%, outline%=0, fill=$FFFFFF)
;=====================================================================================
;	sBubble - Speech Bubble function
;=====================================================================================
; Parameters:
; x, y are the center coords of the ellipse used to construct the speech bubble
; ew, eh are the width and height of the ellipse, respectively
; tail = the length of the 'tail' coming from speech bubble its variable so you can
;        position the speech bubble far or near to the character
; angle = angle of tail coming from speech bubble    NOTE: 0 degrees is due East
; pen = thickness of outline surrounding the speech bubble
; outline = is the color of the outline in Hex (i.e. $000000 = black)
; fill    = is the fill color of the bubble in Hex (i.e. $FFFFFF = white)
; 
; NB: This function requires: setColor(); fatLine(), and fillTriangle() functions.
;=====================================================================================
	Local p2%, rx%, ry%, ltSideX%, ltSideY%, rtSideX%, rtSideY%, tailX%, tailY%
	
	If pen < 2 Then pen = 2
	p2% = pen Shl 1
	rx = ew/2
	ry = eh/2
	
	;draw the outline oval
	setColor outline
	Oval x-rx-pen, y-ry-pen, ew+p2, eh+p2, True
	;calc the points used to draw the tail outline
	ltSideX = Cos(angle + 5.)*(rx-5) + x
	ltSideY = Sin(angle + 5.)*(ry-5) + y
	
	rtSideX = Cos(angle - 5.)*(rx-5) + x
	rtSideY = Sin(angle - 5.)*(ry-5) + y
	
	tailX = Cos(angle)*(rx+tail)+x
	tailY = Sin(angle)*(ry+tail)+y

	;draw the tail outline
	fatLine(ltSideX, ltSideY, tailX, tailY, p2)
	fatLine(rtSideX, rtSideY, tailX, tailY, p2)
	
	;fill the tail and the main ellipse
	setColor fill
	fillTriangle(ltSideX, ltSideY, tailX, tailY, rtSideX, rtSideY)
	Oval x-rx,y-ry,ew,eh,True
End Function

Function setColor(hexValue%)
	Color (hexValue Shr 16) And 255,(hexValue Shr 8) And 255, hexValue And 255
End Function

Function setClsColor(hexValue%)
	ClsColor (hexValue Shr 16) And 255,(hexValue Shr 8) And 255, hexValue And 255
End Function

Function rgb2hex%(r%, g%, b%)
	Return (r Shl 16) Or (g Shl 8) Or b
End Function

Function reticle(x,y,colr)
	Local r%, g%, b%
	r = ColorRed
	g = ColorGreen
	b = ColorBlue
	setColor(colr)
	Line(x-5,y,x+5,y)
	Line(x,y-5,x,y+5)
	Color r,g,b
End Function

;FillTriangle by Tom Toad
;http://www.blitzmax.com/codearcs/codearcs.php?code=1200
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

Function fatLine(x1%, y1%, x2%, y2%, penSize%)
	If penSize% < 1 Then Return False
	If penSize% = 1 Then Line(x1%, y1%, x2%, y2%): Return
	; penSize% is thickness to draw line
	offset% = penSize% / 2 ;offset needed to place ovals correctly

	; Calculate deltax and deltay for initialization
	deltax% = Abs(x2% - x1%)
	deltay% = Abs(y2% - y1%)
 
	; Initialize all vars based on which is the independent variable
	If deltax% >= deltay% Then
		; x is the independent variable
		numovals% = deltax% + 1
		d% = (2 * deltay%) - deltax%
		dinc1% = deltay% Shl 1
		dinc2% = (deltay% - deltax%) Shl 1
		xinc1% = 1
		xinc2% = 1
		yinc1% = 0
		yinc2% = 1
	Else 
		; y is the independent variable
		numovals% = deltay% + 1
		d% = (2 * deltax%) - deltay%
		dinc1% = deltax% Shl 1
		dinc2% = (deltax% - deltay%) Shl 1
		xinc1% = 0
		xinc2% = 1
		yinc1% = 1
		yinc2% = 1
	End If
	; Make sure x and y move in the right directions
	If x1% > x2% Then
		xinc1% = - xinc1%
		xinc2% = - xinc2%
	End If
	If y1% > y2% Then 
		yinc1% = - yinc1%
		yinc2% = - yinc2%
	End If
	; Start drawing at x%, y%
	x% = x1% - offset%
	y% = y1% - offset%
	; Draw the filled ovals
	For i% = 1 To numovals%
		Oval x%, y%, penSize%, penSize%, True
		If d% < 0 Then 
			d% = d% + dinc1%
			x% = x% + xinc1%
			y% = y% + yinc1%
      	Else
			d% = d% + dinc2%
			x% = x% + xinc2%
			y% = y% + yinc2%
		End If
	Next
End Function
