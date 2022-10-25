; ID: 2869
; Author: Andy_A
; Date: 2011-07-03 21:35:48
; Title: Cartoon Sheep Function
; Description: Draws cartoonish sheep

;     Title: Cartoon Sheep Function
;Programmer: Andy Amaya
;      Date: 2011.06.29

;=======================================================
;FillTriangle by Tom Toad
;http://www.blitzmax.com/codearcs/codearcs.php?code=1200
;=======================================================

AppTitle "Cartoon Sheep"
Global sw = 800, sh = 600, retX, retY
Graphics sw, sh, 32, 2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

;color constants
Const black%    = $00
Const white%    = $FFFFFF
Const red%      = $FF0000
Const yellow%   = $FFFFE0
Const green%    = $FF00
Const blue%     = $FF
Const darkblue% = $80
Const lightgray%= $D0D0D0
Const gray%     = $808080
Const peach%    = $FFB060
Const skyblue%  = $20A0FF
Const brown%    = $604020

setClsColor(skyblue)

While MouseHit(1) = 0
	Cls
	For i = 1 To 9
		colr = Rand(1,3)
		Select colr
			Case 1: outline = black	   : fill = white
			Case 2: outline = black	   : fill = lightgray
			Case 3: outline = brown    : fill = yellow
		End Select
		
		scale = Rand(10,50) ; scale values of 10 to 100 recommended
		x = Rand(scale*5,sw-scale*5) ;keep 'em on
		y = Rand(scale*3,sh-scale*3) ;screen here
		If toggle Then
			sheep(x, y, scale*3, scale*5, outline, fill, Rand(-70,70), Rand(0,1),1)
		Else 
			sheep(x, y, scale*5, scale*3, outline, fill, Rand(-70,70), Rand(0,1),1)
		End If
		toggle = 1-toggle
	Next
	setColor(black)
	Text 400,580,"Right click for more random sheep --------- Left click to Exit",True
	Flip
	WaitMouse()
Wend
End

Function sheep(x, y, ovalw, ovalh, outline, fill, headAngle, headPos, penWidth)
;==============================================================================
; sheep function to draw cartoon sheep
;==============================================================================
; Parameters:
; x,y are the center coords of the main ellipse used to construct the body
; ovalw, ovalh are the width and height of the main ellipse
; outline is the Hex color used to draw the sheep's outline
; fill is the Hex color used to fill in the wool
; headAngle is the angle of the sheep's head
; headPos locates the head on the left or right of the body
; penWidth determines the width of the outline in pixels
;
;=======================================================================================
; NB: requires setColor(), fillTriangle(), rLocate(), rEllipse() and tBubble() functions
;=======================================================================================
	Local bigRad%, legW%, legH%, radx%, rady%, tx%, ty%, bodyBump%, stroke%
	Local headRadX%, headRadY%, eyeRadX%, eyeRadY%, earLocX%, earLocY%
	Local eyeOutlineX%, eyeOutlineY%

	;make sure we derive proportions from the longest axis
	If ovalw > ovalh Then bigRad = ovalw Else bigRad = ovalh
	legW = bigRad/20
	If legW < 3 Then legW = 3
	legH = legW*5
	radx = ovalw Shr 1
	rady = ovalh Shr 1

	setColor(outline)
	;left front leg oval center
	tx = Cos(51)*radx +x
	ty = Sin(51)*rady +y
	rEllipse(tx, ty, legW, legH, 30, Rand(-12,12), 1)
	
	;rt front leg oval center
	tx = Cos(68)*radx +x
	ty = Sin(68)*rady +y
	rEllipse(tx, ty, legW, legH, 30, Rand(-12,12), 1)
	
	;left back leg oval center
	tx = Cos(110)*radx +x
	ty = Sin(110)*rady +y-legW
	rEllipse(tx, ty, legW, legH, 30, Rand(-12,12), 1)

	;right back leg oval center
	tx = Cos(127)*radx +x
	ty = Sin(127)*rady +y+legW
	rEllipse(tx, ty, legW, legH, 30, Rand(-12,12), 1)

	;calc proportional bumps	
	bodyBump = bigRad Shr 4
	If bodyBump < 1 Then bodyBump = 1
	stroke = bodyBump/3
	If stroke < 1 Then stroke = 1

	tBubble(x, y, ovalw, ovalh, bodyBump, stroke, outline, fill)	;body

	;place head center on left or right of body
	If headPos = 0 Then 
		hx = x-radx	;left
	Else
		hx = x+radx ;right
	End If
	hy = y
	
	;proportions for head
	headRadX = bigRad/8
	headRadY = bigRad/4
	eyeRadX = headRadX*.3
	eyeRadY = headRadY*.3
	If eyeRadX < 2 Then eyeRadX = 2
	If eyeRadY < 4 Then eyeRadY = 4

	;draw the head
	setColor(peach)
	rEllipse(hx, hy, headRadX, headRadY, 30, headAngle, 1)
	setColor(outline)
	rEllipse(hx, hy, headRadX, headRadY, 30, headAngle, 0)
	setColor(peach)
	
	;calc ear center distances
	earLocX = Float(headRadX)*1.1
	earLocY = Float(headRadY)*1.1
	
	;calc center of left ear
	rLocate(hx, hy, earLocX, earLocY, headAngle, 325)
	rEllipse(retX, retY, eyeRadX, eyeRadY, 30, headAngle+45, 1)	

	;calc center of right ear
	rLocate(hx, hy, earLocX, earLocY, headAngle, 215)
	rEllipse(retX, retY, eyeRadX, eyeRadY, 30, headAngle-45, 1)

	;calc eye center distances
	eyeOutlineX% = Float(headRadX)*.4
	eyeOutlineY% = Float(headRadY)*.4
	
	;calc center of right eye
	setColor(outline)
	rLocate(hx, hy, eyeOutlineX, eyeOutlineY, headAngle,190)
	exR = retX : eyR = retY
	rEllipse(exR, eyR, eyeRadX, eyeRadY, 30, headAngle, 1)

	;calc center of left eye
	rLocate(hx, hy, eyeOutlineX, eyeOutlineY, headAngle,350)
	exL = retX : eyL = retY
	rEllipse(exL, eyL, eyeRadX, eyeRadY, 30, headAngle, 1)
	
	;draw the whites of eyes
	setColor white
	whiteX = eyeRadX-2
	If whiteX < 1 Then whiteX = 1
	whiteY = eyeRadY-2
	If whiteY < 2 Then whiteY = 2
	rEllipse(exL, eyL, whiteX,whiteY,30,headAngle, 1)
	rEllipse(exR, eyR, whiteX,whiteY,30,headAngle, 1)
	
	
	setColor(outline)
	;calc proportional pupil radii
	eyePupilX = eyeOutlineX * .35
	eyePupilY = eyeOutlineY * .35
	
	;calc center of pupils
	angle = Rand(0,359)
	rLocate(exR, eyR, eyePupilX, eyePupilY, headAngle, angle)
	rEllipse(retX, retY, eyePupilX, eyePupilY, 10, headAngle, 1)
	
	rLocate(exL, eyL, eyePupilX, eyePupilY, headAngle, angle)
	rEllipse(retX, retY, eyePupilX, eyePupilY, 10, headAngle, 1)

	
	;calc location of head wool
	woolBump = bodyBump/6
	If woolBump < 1 Then woolBump = 1
	rLocate(hx, hy, headRadX*.72, headRadY*.72, headAngle, 270)
	tBubble(retX, retY, headRadY*.75, headRadX*.75, woolBump, woolBump, outline, fill)	;head wool
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

Function tBubble(x%, y%, ew%, eh%, bump%, pen%, outline%=0, fill%=$FFFFFF)
;========================= Thought Bubble Function ==================================
;Parameters:
; x,y are the center of the main ellipse used to construct the thought bubble
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
;=====================================================================================	
; NB: requires setColor() function
;=====================================================================================	
	Local ex%, ey%, radX%, radY%, rand1%, rand2%, arcUsed%, arc%
	Local rad%, cx%, cy%, x1%, y1%, x2%, y2%, oldX%, oldY%, pen2%
	If pen < 2 Then pen = 2
	pen2 = pen Shr 1

    ex = x ;center X of main ellipse
    ey = y ;center Y of main ellipse
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

Function rEllipse(centerX%, centerY%, radius1#, radius2#, segments#, angle#, mode%)
;============================== Rotated Ellipse Function ==============================
; Parameters are:
;
; centerX, centerY coords locate center of ellipse to be plotted
; radius1 is width of ellipse along the X axis
; radius2 is height of ellipse along the Y axis
; segments is number of line segments used to draw the ellipse (minimum of 3)
; angle is number of degrees to rotate the ellipse			   (NOTE: 0 degrees = East)
; mode - when mode = 0 then draw an un-filled ellipse else draw a filled ellipse
;
;======================================================================================
; NB: requires fillTriangle() function
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
		;if mode is zero draw an un-filled ellipse
		If mode = 0 Then
			Line x1, y1, x2, y2
		Else
			;otherwise use the fillTriangle function to fill current arc segment
			fillTriangle(centerX, centerY, x1, y1, x2, y2)
		End If
		x1 = x2 : y1 = y2
		i = i + incSize
	Wend
End Function

;locate any angle location on a rotated ellipse
Function rLocate#(cx, cy, r1, r2, headAngle, angle)
	Local rca#, rsa#, ca#, sa#,x#, y#
	rca =Cos(headAngle) : rsa = Sin(headAngle)
	ca = Cos(angle)*r1: sa = Sin(angle)*r2
	;==============================================
	;        *** retX and retY are GLOBAL! ***
	; which allows us to return both x and y values
	;==============================================
	retX = Int(rca*ca-rsa*sa+cx)
	retY = Int(rsa*ca+rca*sa+cy)
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
