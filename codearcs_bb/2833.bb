; ID: 2833
; Author: Andy_A
; Date: 2011-03-15 18:30:26
; Title: FAST - Scan Line Filled Polygon Function
; Description: Fast filled polygons

;Scan Line Filled Polygon
;public-domain code by Darel Rex Finley, 2007
;http://alienryderflex.com/polygon_fill/
;
;Ported to BlitzPlus
;by Andres Amaya Jr.
;2011.02.27

;polyFill() Demo
;by Andres Amaya Jr.
;2011.03.18

AppTitle "polyFill Demo"
Global sw%, sh%, scrn%
sw = 800: sh = 600
Graphics sw,sh,32,2
SeedRnd MilliSecs()

scrn = CreateImage(sw,sh)
Dim polyX#(500), polyY#(500), nodeX%(500)


Repeat
	intro()
	WaitMouse()
	
	flipper()
	WaitMouse()
	
	tree()
	WaitMouse()
	
	bird()
	WaitKey()

Until KeyHit(1)

FreeImage scrn
End

Function intro()
	Local st%, et%, r%, g%, b%, angle%

	ClsColor 0,65,85
	Cls
	drawGrid()
	st = MilliSecs()	
	LockBuffer BackBuffer()
	starBackground()
	UnlockBuffer BackBuffer()
	et = MilliSecs()-st
	Color 255,255,255
	Text 5,15,"200 differently colored and rotated stars in "+et+"ms"
	Text 460,15,"The spinning star is drawn in real time."
	Text sw/2,sh-30,"Left-Click to continue",1
	GrabImage scrn,0,0
	st = MilliSecs()+1000
	r=255: g=255: b = 128
	While MouseHit(1) = 0
		If st < MilliSecs() Then
			st = MilliSecs()+500
			r = Rand(90,255)
			g = Rand(90,255)
			b = Rand(90,255)
		End If
		DrawBlock  scrn, 0, 0
		mouseStar(r,g,b, MouseX(), MouseY(), Float(angle))
		angle = (angle + 5) Mod 360
		Flip
	Wend
End Function

Function flipper()
	Local numVerts%, oX%, oY%, wide%, high%, x%, y%, st%, et%, et2%
	Local minX#, minY#, maxX#, maxY#

	ClsColor 32,32,160
	Cls
	Restore Dolph
	Read numVerts%, oX%, oY%, wide%, high%
	minX = oX : maxX = oX+wide-1
	minY = oY : maxY = oY+high-1
	For i% = 0 To numVerts-1
		Read x%, y%
		polyX(i) = x + oX
		polyY(i) = y + oY
	Next
	Color 128,128,128
	st = MilliSecs()
	LockBuffer BackBuffer()
	polyFill(numVerts, minX, minY, maxX, maxY)
	UnlockBuffer BackBuffer()
	et = MilliSecs()-st
	st = MilliSecs()
	drawPoly(0,0,0, numVerts, 2)
	Color 0,0,0
	Oval 615, 158, 16, 9, True
	et2 = MilliSecs()-st
	Color 255,255,255
	Text 10, 10, "The 143 vertice dolphin polygon, filled in "+et+"ms"
	Text 20, 30, "The thick outline took "+et2+"ms"
	Text sw/2,sh-50,"The next example takes a couple of seconds to render",1
	Text sw/2,sh-30,"Left-Click to continue",1
	Flip
End Function

Function tree()
	Local numVerts%, oX%, oY%, wide%, high%, x%, y%, st%, et%, et2%
	Local minX#, minY#, maxX#, maxY#
	Local r#, g#, b#, dr#, dg#, db#, h#
	
	h = Float(sh)
	r = 255. : g = 200. : b = 10.
	dr = -.99*r/h: dg = -.99*g/h: db = -.99*b/h
	For i = 0 To sh-1
		Color r,g,b
		Line(0,i,sw,i)
		r = r + dr: g = g + dg: b = b + db
	Next
	Restore Tree
	Read numVerts%, oX%, oY%, wide%, high%
	minX = oX : maxX = oX+wide-1
	minY = oY : maxY = oY+high-1
	For i% = 0 To numVerts-1
		Read x%, y%
		polyX(i) = x + oX
		polyY(i) = y + oY
	Next
	r = 200: g = 255: b = 90
	dr = -89./90.: dg = -74./75.: db = -77./78
	st = MilliSecs()
	For i= 64 To 5 Step -1
		drawPoly(r, g, b, numVerts, i)
		r = r + dr: g = g + dg: b = b + db
	Next
	et = MilliSecs()-st
	Color 80,80,0
	st = MilliSecs()
	LockBuffer BackBuffer()
	polyFill(numVerts, minX, minY, maxX, maxY)
	UnlockBuffer BackBuffer()
	et2 = MilliSecs()-st

	Color 255, 255, 255
	Text 10,sh-220, "The 418 vertice tree trunk"
	Text 10,sh-205, "polygon was filled in "+et2+"ms"
	Text 5,sh-180, "The green outline took "+et+"ms"
	Text 5,sh-165, "and uses the same polygon definition."
	Text sw/2,sh-30,"Left-Click to continue",1
	Flip
End Function

Function bird()
	Local numVerts%, oX%, oY%, wide%, high%, x%, y%, st%, et%
	Local minX#, minY#, maxX#, maxY#

	ClsColor 80, 180, 255
	Cls
	Restore eagle1
	Read numVerts, oX, oY, wide, high
	minX = oX : maxX = oX+wide-1
	minY = oY : maxY = oY+high-1
	For i% = 0 To numVerts-1
		Read x, y
		polyX(i) = x + oX
		polyY(i) = y + oY
	Next
	Color 68, 68, 0
	
	st = MilliSecs()
	LockBuffer BackBuffer()
	polyFill(numVerts, minX, minY, maxX, maxY)

	Restore eagle2
	Read numVerts%, oX%, oY%, wide%, high%
	minX = oX : maxX = oX+wide-1
	minY = oY : maxY = oY+high-1
	For i% = 0 To numVerts-1
		Read x%, y%
		polyX(i) = x + oX
		polyY(i) = y + oY
	Next
	Color 83, 83, 0
	polyFill(numVerts, minX, minY, maxX, maxY)

	Restore eagle3
	Read numVerts%, oX%, oY%, wide%, high%
	minX = oX : maxX = oX+wide-1
	minY = oY : maxY = oY+high-1
	For i% = 0 To numVerts-1
		Read x%, y%
		polyX(i) = x + oX
		polyY(i) = y + oY
	Next
	Color 160,160,120
	polyFill(numVerts, minX, minY, maxX, maxY)

	Restore eagle4
	Read numVerts%, oX%, oY%, wide%, high%
	minX = oX : maxX = oX+wide-1
	minY = oY : maxY = oY+high-1
	For i% = 0 To numVerts-1
		Read x%, y%
		polyX(i) = x + oX
		polyY(i) = y + oY
	Next
	Color 240, 243, 230
	polyFill(numVerts, minX, minY, maxX, maxY)

	Restore eagle5
	Read numVerts%, oX%, oY%, wide%, high%
	minX = oX : maxX = oX+wide-1
	minY = oY : maxY = oY+high-1
	For i% = 0 To numVerts-1
		Read x%, y%
		polyX(i) = x + oX
		polyY(i) = y + oY
	Next
	Color 180, 180, 170
	polyFill(numVerts, minX, minY, maxX, maxY)

	Restore eagle6
	Read numVerts%, oX%, oY%, wide%, high%
	minX = oX : maxX = oX+wide-1
	minY = oY : maxY = oY+high-1
	For i% = 0 To numVerts-1
		Read x%, y%
		polyX(i) = x + oX
		polyY(i) = y + oY
	Next
	Color 240, 200, 65
	polyFill(numVerts, minX, minY, maxX, maxY)

	Restore eagle7
	Read numVerts%, oX%, oY%, wide%, high%
	minX = oX : maxX = oX+wide-1
	minY = oY : maxY = oY+high-1
	For i% = 0 To numVerts-1
		Read x%, y%
		polyX(i) = x + oX
		polyY(i) = y + oY
	Next
	Color 160, 160, 140
	polyFill(numVerts, minX, minY, maxX, maxY)

	Restore eagle8
	Read numVerts%, oX%, oY%, wide%, high%
	minX = oX : maxX = oX+wide-1
	minY = oY : maxY = oY+high-1
	For i% = 0 To numVerts-1
		Read x%, y%
		polyX(i) = x + oX
		polyY(i) = y + oY
	Next
	Color 240, 230, 100
	polyFill(numVerts, minX, minY, maxX, maxY)
	UnlockBuffer BackBuffer()
	et = MilliSecs()-st
	Color 0,0,0
	Text sw/2,15,"This is a composite drawing of 8 separate polygons",1
	Text sw/2,30,"totaling 607 vertices.",1
	Text sw/2,60,"It took "+et+"ms to render this drawing.",1
	Text sw/2,sh-30,"Press [ESC] to exit",1
	Flip
End Function

Function polyFill(numSides%, minX#, minY#, maxX#, maxY#)
	Local pixelY%, i%, j%, nodes%, b1%, b2%, b3%, b4%, temp%
	Local fpixY#, f1#, f2#, f3#
	
	For pixelY = minY To maxY
		nodes = 0
		j = numSides-1
		fpixY = Float(pixelY)
		;build node list
		For i = 0 To numSides-1
			b1 = polyY(i) < fpixY
			b2 = polyY(j) >= fpixY
			b3 = polyY(j) < fpixY
			b4 = polyY(i) >= fpixY
			If (b1 And b2) Or (b3 And b4) Then
				f1 = fpixY - polyY(i)
				f2 = polyY(j)-polyY(i)
				f3 = polyX(j)-polyX(i)
				nodeX(nodes) = Floor(f1/f2*f3+polyX(i))
				nodes = nodes + 1
			End If
			j = i
		Next
		;sort nodes
		i = 0
		While i < nodes-1
			If nodeX(i)>nodeX(i+1) Then
				temp = nodeX(i)
				nodeX(i) = nodeX(i+1)
				nodeX(i+1) = temp
				If i > 0 Then i = i - 1
			Else
				i = i + 1
			End If
		Wend
		;draw scanline(s)
		For i = 0 To nodes-1 Step 2
			If nodeX(i) <= maxX Then
				If nodeX(i+1) > minX Then
					If nodeX(i) < minX Then nodeX(i) = minX
					If nodeX(i+1) > maxX Then nodeX(i+1) = maxX
					Line nodeX(i),pixelY,nodeX(i+1),pixelY
				End If
			End If
		Next
	Next
End Function
	

Function min#(flt1#, flt2#)
	If flt1 < flt2 Then Return flt1 Else Return flt2
End Function

Function max#(flt1#, flt2#)
	If flt2 > flt1 Then Return flt2 Else Return flt1
End Function

Function drawGrid()
	Local x%, y%
    Color 144,144,144
    For x = 20 To sw-20 Step 20
        Line x,40,x, sh-40
    Next
    For y = 40 To sh-40 Step 20
        Line 20,y,sw-20,y
    Next
End Function

;Draw an outline of the polygon to highlight edges
Function drawPoly(r,g,b, numPoints%, penSize%)
	Local j%, i%
	
	;allow variable line width and color
	If penSize < 1 Then penSize = 1
	Color r,g,b
	
	;draw it!
	j = numPoints-1
	For i = 0 To numPoints-1
		If penSize > 1 Then 
			fatLine(polyX(j), polyY(j), polyX(i), polyY(i), penSize )
		Else
			Line polyX(j), polyY(j), polyX(i), polyY(i)
		End If
		j=i
	Next
End Function

Function starBackground()
	Local i%, starPoints%, penSize%, bigRad%, lilRad%
	Local x%, y%, minX#, minY#, maxX#, maxY#

	For i = 0 To 199
		starPoints = Rand(4,6) 				 ;random number of star points
		penSize = Rand(2, 5) 				 ;random number between 2 and 5 for line width
		bigRad = Rand(15, 20) 				 ;random overall radius of a star between 20 and 40
		lilRad = bigRad * Float((Rand(15, 40))/100.0) ;20 - 50% of big radius
		x = Rand(bigRad+20, sw-bigRad-20) 	 ;keep all of star on screen along X-axis
		y = Rand(bigRad+40, sh-bigRad-40)	 ;...do the same along the Y-axis
		minX = Float(x-bigRad): minY = Float(y-bigRad)
		maxX = Float(x+bigRad): maxY = Float(y+bigRad)
		star(x, y, bigRad, lilRad, starPoints, Rand(0,72))
		Color Rand(0,255), Rand(0,255), Rand(0,255)
		polyFill(starPoints*2, minX, minY, maxX, maxY)
	Next
End Function

Function mouseStar(r%, g%, b%, x%, y%, angle#)
	Local minX#, minY#, maxX#, maxY#
	
	Color r, g, b
	minX = Float(x-20): maxX = Float(x+20)
	minY = Float(y-20): maxY = Float(y+20)
	star(x, y, 20, 8, 5, angle#)
	polyFill(10, minX, minY, maxX, maxY)
End Function
	

Function star(centX#, centY#, outerRad%, innerRad%, points%, startAngle#)
	Local toggle%, element%
	Local inc#, i#

	If (points > 2 And points < 181) And (innerRad <= outerRad) Then
		inc = 360.0/(2.0 * points)
		toggle = 0
		element = 0
		i = startAngle
		While i <= (startAngle + 360.0)
			If toggle Then
				polyX(element) = Cos(i)* innerRad+ centX
				polyY(element) = Sin(i)* innerRad+ centY
			Else
				polyX(element) = Cos(i)* outerRad+ centX
				polyY(element) = Sin(i)* outerRad+ centY
			End If
			toggle = 1 - toggle
			element = element + 1
			i = i + inc
		Wend
	End If
End Function

Function fatLine(x1%, y1%, x2%, y2%, penSize%) 
	Local offset%, deltax%, deltay%, numovals%, d%, x%, y%
	Local dinc1%, dinc2%, xinc1%, xinc2%, yinc1%, yinc2%
	

	If penSize < 1 Then Return False 
	If penSize = 1 Then Line(x1, y1, x2, y2): Return 
	;  penSize% is thickness to draw line 
	offset = penSize / 2          ;offset needed to place Oval correctly

	; Calculate deltax and deltay for initialization
	deltax = Abs(x2 - x1)
	deltay = Abs(y2 - y1)
 
	; Initialize all vars based on which is the independent variable
	If deltax >= deltay Then
	; x is the independent variable
		numovals = deltax + 1
		d = (2 * deltay) - deltax
		dinc1 = deltay Shl 1
		dinc2 = (deltay - deltax) Shl 1
		xinc1 = 1
		xinc2 = 1
		yinc1 = 0
		yinc2 = 1
	Else 
		; y is the independent variable
		numovals = deltay + 1
		d = (2 * deltax) - deltay
		dinc1 = deltax Shl 1
		dinc2 = (deltax - deltay) Shl 1
		xinc1 = 0
		xinc2 = 1
		yinc1 = 1
		yinc2 = 1
	End If
	; Make sure x and y move in the right directions
	If x1 > x2 Then
		xinc1 = - xinc1
		xinc2 = - xinc2
	End If
	If y1 > y2 Then 
		yinc1 = - yinc1
		yinc2 = - yinc2
	End If
	; Start drawing at x, y
	x = x1 - offset
	y = y1 - offset
	; Draw the filled ovals
	For i = 1 To numovals
		Rect x, y, penSize, penSize, True
		If d < 0 Then 
			d = d + dinc1
			x = x + xinc1
			y = y + yinc1
		Else
			d = d + dinc2
			x = x + xinc2
			y = y + yinc2
		End If
	Next
End Function


.Dolph
;==========================================================
;Number of points used to define this polygon
Data 143
;==========================================================
;x & y offsets to place polygon anywhere on screen
Data 70, 50   ;change to appropriate on screen coords
;==========================================================
;width & height of the polygon;s bounding box
Data 651, 469
;==========================================================
;The 143 coordinate pairs are:
Data 650,161,641,162,629,160,621,157,609,152,594,143,580,134,567,127,535,124,535,126
Data 566,128,580,135,593,143,608,153,620,158,628,161,641,163,650,162,650,166
Data 646,171,640,173,633,170,619,167,606,164,586,161,566,159,510,160,493,160
Data 481,157,476,165,465,177,450,188,432,198,421,203,406,205,386,207,373,208
Data 366,209,360,203,364,200,373,196,382,192,392,183,406,168,414,157,419,153
Data 429,151,419,152,413,156,401,169,285,195,258,202,236,212,200,229,187,235
Data 176,246,164,257,146,282,136,297,126,315,116,330,104,346,98,359,103,364
Data 105,370,105,376,104,386,104,398,101,403,99,408,96,415,93,413,87,409
Data 81,409,78,413,75,418,72,423,67,419,66,410,63,387,64,410,65,423
Data 61,423,58,423,52,423,48,423,41,426,36,433,19,455,15,462,9,468
Data 3,465,4,459,10,443,15,426,26,401,53,367,55,361,56,319,60,289
Data 65,271,83,229,102,191,130,146,160,111,186,78,190,66,192,51,188,39
Data 180,31,173,25,164,19,159,16,158,7,163,2,173,1,192,1,214,4
Data 231,9,245,13,262,21,275,27,287,32,297,33,310,31,338,28,371,28
Data 396,28,435,29,466,32,501,42,526,50,552,62,573,75,588,88,599,100
Data 609,118,614,126,619,132,624,136,632,143,641,148,647,153

.Tree
;==========================================================
;Number of points used to define this polygon
Data 418
;==========================================================
;x & y offsets to place polygon anywhere on screen
Data 170, 30   ;change to appropriate on screen coords
;==========================================================
;width & height of the polygon;s bounding box
Data 442, 498
;==========================================================
;The 418 coordinate pairs are:
Data 125,494,133,493,144,487,158,484,166,484,161,490,158,494,163,497,180,490,198,482
Data 208,481,206,492,218,488,246,484,264,485,301,487,320,490,339,490,315,474
Data 297,468,268,416,265,404,270,399,279,397,279,382,268,359,265,333,277,323
Data 283,313,291,307,302,302,316,299,326,305,324,317,315,326,301,340,304,352
Data 310,359,324,359,335,355,342,350,340,341,332,349,327,348,322,342,324,329
Data 332,319,337,292,345,290,362,293,381,299,385,306,382,311,359,317,386,315
Data 394,292,388,287,372,284,379,276,389,272,403,272,416,275,428,280,434,289
Data 429,293,419,296,424,301,432,301,441,296,441,285,435,274,424,267,411,263
Data 400,260,405,256,414,255,423,250,430,243,428,237,421,228,409,224,403,226
Data 403,233,405,239,410,233,418,234,418,243,415,248,405,248,400,248,392,249
Data 387,255,386,261,379,263,360,264,346,266,329,278,311,284,279,300,265,307
Data 252,303,255,294,253,283,245,276,244,265,249,256,254,254,263,259,264,267
Data 262,269,257,267,259,276,270,277,277,266,269,251,258,245,243,245,239,257
Data 238,271,240,282,231,275,225,265,233,252,245,233,248,215,257,191,265,173
Data 276,159,288,157,300,161,314,168,322,176,329,194,329,207,325,215,312,218
Data 300,221,322,222,326,227,319,233,342,223,342,205,336,187,336,177,342,178
Data 353,183,359,193,360,202,361,211,372,201,373,186,361,177,351,167,334,162
Data 315,153,308,148,312,131,320,118,331,113,345,113,356,125,355,137,347,141
Data 333,139,344,146,356,145,368,141,371,129,366,117,340,92,326,91,318,99
Data 312,103,307,104,292,99,286,95,280,85,282,77,291,75,299,77,299,81
Data 294,84,307,79,307,71,302,67,292,67,281,67,276,70,271,78,272,89
Data 276,100,285,104,293,110,291,120,286,123,276,126,270,136,268,149,260,154
Data 246,152,241,148,234,142,233,135,237,123,245,119,253,121,253,126,250,131
Data 244,135,248,141,257,139,262,128,269,116,262,104,250,102,239,109,230,118
Data 223,128,223,137,226,148,229,158,239,168,239,181,239,204,231,212,223,222
Data 217,233,214,244,209,250,201,247,194,234,194,203,200,189,207,170,208,152
Data 204,132,197,114,192,93,190,78,196,65,204,55,207,39,214,32,220,33
Data 233,42,243,48,248,58,247,67,245,75,237,76,231,72,229,62,226,52
Data 222,59,224,72,229,82,238,88,249,84,255,74,261,64,255,45,244,31
Data 228,19,207,15,198,27,186,28,170,24,162,18,151,7,143,7,128,6
Data 121,8,121,13,117,19,113,23,109,22,100,17,89,17,95,23,114,28
Data 124,32,131,24,139,20,148,27,163,39,173,39,183,46,183,55,181,63
Data 176,70,173,77,182,100,186,113,189,131,192,155,188,159,177,156,162,141
Data 160,120,159,97,149,86,138,80,116,77,104,68,87,48,71,46,66,49
Data 56,46,52,40,57,33,63,22,46,34,45,46,53,55,61,57,72,57
Data 79,63,91,69,94,76,95,84,91,89,86,92,80,97,79,105,80,117
Data 84,125,85,133,83,143,86,146,95,151,103,151,96,142,95,134,101,133
Data 95,124,91,116,92,101,102,97,120,95,133,97,143,102,147,111,148,123
Data 152,150,172,170,174,191,172,259,167,265,161,257,149,247,138,241,125,235
Data 117,221,110,218,104,209,99,194,88,195,80,189,71,181,56,179,41,179
Data 25,186,15,195,9,205,7,221,8,239,14,244,29,247,38,241,42,227
Data 28,235,22,234,15,224,17,214,19,203,30,195,41,193,55,194,66,197
Data 80,210,89,222,87,231,76,240,64,251,62,259,58,275,63,282,70,292
Data 81,299,95,304,107,302,112,294,110,284,100,293,90,295,83,289,75,282
Data 75,271,78,260,86,253,98,249,109,249,123,250,148,278,165,292,186,304
Data 202,317,215,333,219,351,219,381,217,400,211,417,203,430,185,449,174,455
Data 158,463,139,466,127,475

.eagle1
;==========================================================
;Number of points used to define this polygon
Data 350
;==========================================================
;x & y offsets to place polygon anywhere on screen
Data -5, 3   ;change to appropriate on screen coords
;==========================================================
;width & height of the polygon;s bounding box
Data 792, 530
;==========================================================
;The 350 coordinate pairs are:
Data 81,228,73,226,59,214,45,202,33,184,25,167,17,142,11,123,9,108,6,102
Data 11,96,17,100,19,93,14,76,13,67,13,62,18,55,27,63,33,75
Data 37,83,39,88,44,89,51,104,54,99,53,95,57,90,60,87,73,101
Data 80,114,88,128,90,135,93,131,98,129,105,131,113,141,121,155,124,160
Data 130,157,138,157,148,162,154,169,160,173,162,180,166,181,171,181,180,187
Data 191,196,194,198,195,207,201,206,210,211,218,218,222,222,223,226,229,227
Data 237,232,244,236,247,241,258,250,271,256,281,266,293,276,294,279,305,288
Data 318,297,323,303,332,312,340,319,343,326,349,333,355,338,362,344,370,341
Data 382,332,399,318,420,301,448,280,472,262,481,249,488,240,499,229,514,213
Data 528,201,542,188,552,181,560,171,575,150,583,130,592,104,600,67,600,54
Data 604,39,607,29,609,32,614,39,614,52,614,73,614,88,609,107,606,118
Data 602,126,598,131,596,135,597,140,599,134,603,135,606,134,613,128,624,122
Data 634,110,660,78,689,42,693,39,700,37,708,32,707,39,701,44,693,54
Data 687,61,682,67,679,74,668,87,657,102,646,110,651,114,671,97,688,77
Data 706,54,722,29,732,13,736,4,740,9,741,18,737,29,733,40,727,52
Data 718,64,711,74,706,80,699,87,688,100,685,103,680,110,682,115,687,113
Data 698,105,762,53,768,50,773,47,774,50,773,52,769,54,769,59,766,65
Data 759,72,748,85,739,94,731,102,722,109,717,114,715,116,715,119,716,120
Data 727,116,739,110,752,105,761,100,772,96,781,90,791,86,790,92,782,101
Data 771,107,765,111,758,115,755,119,755,124,757,125,761,122,765,120,771,117
Data 778,117,780,120,778,126,775,132,770,137,768,141,768,146,773,149,772,155
Data 767,159,762,164,756,169,755,173,759,174,761,177,760,184,756,186,753,189
Data 751,191,753,194,755,199,757,205,753,209,746,213,740,212,736,214,732,218
Data 725,222,724,225,728,228,733,231,740,235,742,241,741,247,736,251,734,254
Data 735,257,729,260,724,262,730,271,728,275,725,280,722,283,715,284,707,285
Data 713,293,713,299,709,305,703,308,697,310,692,308,693,315,692,321,688,326
Data 682,327,678,328,675,331,672,336,668,338,664,339,658,341,651,340,646,341
Data 645,345,644,350,640,355,635,357,632,359,625,359,626,364,626,370,625,377
Data 621,381,618,386,615,389,609,388,606,390,605,395,601,401,593,403,588,402
Data 586,404,586,409,583,412,580,418,577,422,573,422,569,421,566,424,564,429
Data 559,430,552,430,546,429,544,431,541,437,535,439,528,438,523,443,519,447
Data 520,449,516,453,511,454,511,456,510,462,505,464,499,464,498,464,503,470
Data 511,477,523,482,540,492,571,502,624,517,634,523,619,517,573,505,534,491
Data 513,481,501,478,510,490,507,495,501,495,494,496,494,503,496,508,492,510
Data 486,510,477,510,472,512,476,520,469,521,473,529,467,528,462,523,457,519
Data 444,517,435,514,400,505,374,498,347,489,326,482,315,477,302,470,287,462
Data 273,458,256,448,248,441,242,434,291,400,300,402,310,418,317,423,332,404
Data 341,376,328,359,305,345,279,323,254,299,220,275,205,260,190,253,179,248
Data 174,236,160,225,148,214,135,203,135,189,129,176,117,187
.eagle2
;==========================================================
;Number of points used to define this polygon
Data 86
;==========================================================
;x & y offsets to place polygon anywhere on screen
Data -2, 6   ;change to appropriate on screen coords
;==========================================================
;width & height of the polygon;s bounding box
Data 477, 441
;==========================================================
;The 86 coordinate pairs are:
Data 228,354,177,305,166,295,154,290,147,279,131,267,119,260,100,242,94,236,77,228
Data 54,207,46,186,38,162,50,173,57,187,59,198,67,205,71,202,64,185
Data 58,164,59,150,66,159,71,176,68,175,73,182,79,193,78,179,71,153
Data 74,145,85,153,89,170,90,183,84,180,90,189,97,191,97,181,97,166
Data 101,161,113,166,113,172,120,169,130,171,133,186,135,197,141,208,161,219
Data 175,231,181,240,188,244,213,258,231,271,240,281,254,290,272,301,288,314
Data 301,329,313,342,323,351,342,358,354,352,473,258,476,263,470,270,358,357
Data 345,366,349,371,353,383,345,384,337,386,343,389,344,393,339,393,335,399
Data 340,404,346,410,347,416,340,416,329,413,331,422,332,428,328,427,317,423
Data 320,434,310,430,307,440,298,440
.eagle3
;==========================================================
;Number of points used to define this polygon
Data 45
;==========================================================
;x & y offsets to place polygon anywhere on screen
Data -9, -2   ;change to appropriate on screen coords
;==========================================================
;width & height of the polygon;s bounding box
Data 627, 559
;==========================================================
;The 45 coordinate pairs are:
Data 503,477,512,481,525,488,539,495,557,503,578,510,597,515,615,520,622,520,621,525
Data 626,531,618,532,622,539,618,543,625,554,618,551,614,555,605,558,594,558
Data 582,557,569,552,559,546,551,543,551,547,531,534,527,537,520,541,514,541
Data 511,539,507,541,497,539,491,539,485,537,475,532,468,527,467,519,477,519
Data 476,514,486,513,498,514,496,505,494,497,506,498,511,495,506,489
.eagle4
;==========================================================
;Number of points used to define this polygon
Data 49
;==========================================================
;x & y offsets to place polygon anywhere on screen
Data -12, 0   ;change to appropriate on screen coords
;==========================================================
;width & height of the polygon;s bounding box
Data 309, 440
;==========================================================
;The 49 coordinate pairs are:
Data 198,363,208,359,219,359,228,359,238,362,251,362,263,362,269,361,276,362,282,363
Data 289,367,294,367,292,371,291,376,298,376,294,379,303,381,304,386,308,388
Data 305,392,301,395,296,399,295,403,302,407,300,412,306,418,301,418,295,418
Data 294,423,298,429,297,431,292,431,284,431,289,438,281,437,274,435,275,439
Data 267,435,258,434,254,435,250,439,246,388,248,384,256,384,253,377,245,369
Data 235,365,226,363,217,363
.eagle5
;==========================================================
;Number of points used to define this polygon
Data 27
;==========================================================
;x & y offsets to place polygon anywhere on screen
Data -12, 0   ;change to appropriate on screen coords
;==========================================================
;width & height of the polygon;s bounding box
Data 274, 442
;==========================================================
;The 27 coordinate pairs are:
Data 216,365,229,365,236,365,242,371,250,372,248,377,250,381,245,384,252,389,257,394
Data 255,399,261,409,267,416,273,428,263,427,272,434,266,435,258,433,257,437
Data 254,435,251,441,238,429,233,421,229,415,221,411,209,404,201,398
.eagle6    ;beak
;==========================================================
;Number of points used to define this polygon
Data 25
;==========================================================
;x & y offsets to place polygon anywhere on screen
Data -21, -3   ;change to appropriate on screen coords
;==========================================================
;width & height of the polygon;s bounding box
Data 234, 396
;==========================================================
;The 25 coordinate pairs are:
Data 186,395,185,388,184,379,190,370,197,366,204,364,209,369,213,364,223,364,231,365
Data 231,367,227,369,224,369,222,376,228,381,233,386,230,388,225,390,221,390
Data 215,390,204,388,217,385,210,383,202,384,199,389
.eagle7    ;shadow around eye
;==========================================================
;Number of points used to define this polygon
Data 18
;==========================================================
;x & y offsets to place polygon anywhere on screen
Data -6, -4   ;change to appropriate on screen coords
;==========================================================
;width & height of the polygon;s bounding box
Data 250, 392
;==========================================================
;The 18 coordinate pairs are:
Data 216,368,220,367,224,367,227,369,232,366,236,366,242,369,247,371,247,377,249,384
Data 248,387,242,389,229,391,230,387,232,382,227,383,223,381,222,376
.eagle8    ;eye
;==========================================================
;Number of points used to define this polygon
Data 7
;==========================================================
;x & y offsets to place polygon anywhere on screen
Data -6, -4   ;change to appropriate on screen coords
;==========================================================
;width & height of the polygon;s bounding box
Data 233, 378
;==========================================================
;The 7 coordinate pairs are:
Data 232,373,232,376,227,377,224,375,226,373,227,374,230,374
