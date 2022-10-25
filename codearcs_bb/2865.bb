; ID: 2865
; Author: Andy_A
; Date: 2011-07-01 22:43:28
; Title: Cartoon Thought Bubbles
; Description: Function to draw cartoon thought bubbles

;     Title: Thought Bubbles-Clouds
;Programmer: Andy Amaya
;      Date: 2011.06.21

AppTitle "Cartoon Thought Bubbles"
Global sw, sh
sw = 800: sh = 600
Graphics sw, sh, 32, 2
SetBuffer BackBuffer()

Const black% = $00
Const gray1% = $D0D0D0
Const gray2% = $808080
Const yellow%= $FFFFD0
Const white% = $FFFFFF
ClsColor 32, 160, 255



Arial12 = LoadFont(Arial,12,True)
Arial18 = LoadFont(Arial,18,True)
Arial60 = LoadFont(Arial,60,True)

While MouseHit(1) = 0
	Cls
	tBubble( 60, sh/2, 100, 50, 4,  2, gray2, gray1)
	tBubble(240, sh/2, 200,130, 8,  6, black, yellow)
	tBubble(580, sh/2, 280,480,24, 20)
	et = MilliSecs()-st
	Color 0,0,0


	SetFont Arial12
	Text 60,sh/2-15, "Small",1
	Text 60,sh/2, "Thought",1
	
	SetFont Arial18
	Text 240,sh/2-18,"Medium",1
	Text 240,sh/2, "Thought",1
	Text 400,sh-18,"R-click for more random bubbles.........L-click to exit",True
		
	Color 255,0,0
	SetFont Arial60
	Text 580,sh/2-96, "Hey!",1
	Text 580,sh/2-32, "Whaddaya",1
	Text 580,sh/2+32, "Know?",1
	

	Flip
	WaitMouse()
Wend

FreeFont Arial12
FreeFont Arial18
FreeFont Arial60
End

Function setColor(hexValue%)
	Color (hexValue Shr 16) And 255,(hexValue Shr 8) And 255, hexValue And 255
End Function

Function tBubble(ex%, ey%, ew%, eh%, bump%, pen%, outline%=0, fill%=$FFFFFF)
;=====================================================================================
; tBubble() - Thought Bubble function for displaying the thoughts of players
;			  and NPC's. Could also be used to make cheesy cartoon clouds,
; 			  puffs of smoke, or whatever else comes to mind.
;=====================================================================================
;Parameters:
; ex,ey are the center coords of the main ellipse used to construct the thought bubble
;
; ew is the total  width of the main ellipse ( xRadius * 2)
; eh is the total height of the main ellipse ( yRadius * 2)
;
; bump is a value used to determine overall height of bumps encircling the main
; ellipse - inversely proportional (i.e. smaller bump values make bigger bumps
; and larger bump values make smaller bumps)
;
; pen determines the width of the outline stroke used to draw thought bubble
;
; outline is a hex color value of bubble outline (default = black)
;
; fill is the hex color value used to fill the bubble (default = white)
; 
; (NB: pre-defined constants recommended for hex color values or use rgb2hex function)
;=====================================================================================	
	Local radX%, radY%, rand1%, rand2%, arcUsed%, arc%, rad%
	Local cx%, cy%, x1%, y1%, x2%, y2%, oldX%, oldY%, pen2%
	If pen < 2 Then pen = 2
	pen2 = pen Shr 1

    radX = ew Shr 1 ;radius of main ellipse along X axis
    radY = eh Shr 1 ;radius of main ellipse along Y axis
    rand1 = Int(ew/bump)    ;1/bump the width of main ellipse
    rand2 = Int(eh/bump)    ;1/bump the height of main ellipse

    ;make sure rand1 is less than rand2
    If rand2 < rand1 Then tmp = rand1:rand1=rand2:rand2=tmp
	;if rand1 = rand2 then divide rand1 by 2
    If Abs(rand1 - rand2) < 5 Then rand1 = rand1/2

	;this variable keeps track of degrees used by all arc segments
	arcUsed = 0
	;first point is a random location on ellipse in degrees (always even)
	arc = Rand(0,179)*2
	;this is a point on main ellipse at number of degrees in 'degree' variable
	x1 = Cos(arc)*radX+ex
	y1 = Sin(arc)*radY+ey
	;remember this first point to draw the last bump and close the bubble later
	oldX = x1: oldY = y1
	;use loop to keep adding bumps (circles)
	While arcUsed <= 360-rand2*2
		;get a new random number of degrees
		stp = Rand(rand1, rand2)*2
		;keep running total of 'angle of arc' used so far
		arcUsed = arcUsed + stp
		;keep track of current location in degrees on the main ellipse
		arc = arc+stp
		;calc x,y coords on main ellipse at current 'arc' value
		x2 = Cos(arc)*radX+ex
		y2 = Sin(arc)*radY+ey
		;calc radius of the circle used to draw the bump
		rad = Floor(Sqr((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))+.5)+1
		;find mid-point of x1,y1 and x2,y2 to use as center of circle used to draw bump
		cx = (x1+x2)/2
		cy = (y1+y2)/2
		;change color to hex value in 'outline' parameter
		setColor(outline)
		;draw a filled circle 'pen2' pixels larger than inner filled circle (thickness of outline)
		Oval cx-rad/2-pen2, cy-rad/2-pen2, rad+pen, rad+pen,True
		;change color to hex value in 'fill' parameter
		setColor(fill)
		;draw the 'fill' colored filled circle
		Oval cx-rad/2, cy-rad/2, rad, rad, True
		;remember last coords on ellipse drawn to, as x1,y1
		x1 = x2: y1 = y2
	Wend
	;============================================================================
	; OK, ellipse nearly encircled with bumps, only room enough for one last bump
	;============================================================================
	;these are coords of first point we used to start drawing bumps
	;x1,y1 holds the coords of the last coords used to draw a bump in loop
	x2 = oldX : y2 = oldY
	;calc the radius of the circle used to draw the last bump
	rad = Int(Sqr((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1)) +.5)+1
	;calc the mid point coords between x1,y1 and x2,y2 to use as center of circle
	cx = (x1+x2)/2
	cy = (y1+y2)/2
	;change color to hex value in 'outline' parameter
	setColor(outline)
	;draw a filled circle 'pen2' pixels larger than inner filled circle (thickness of outline)
	Oval cx-rad/2-pen2, cy-rad/2-pen2, rad+pen, rad+pen, True
	;change color to hex value in 'fill' parameter
	setColor(fill)
	;draw the 'fill' colored filled circle
	Oval cx-rad/2, cy-rad/2, rad, rad, True
	;draw the main filled ellipse, which erases all of the semi-circles inside of the ellipse
	Oval ex-radX, ey-radY, ew, eh,True
End Function
