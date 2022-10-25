; ID: 3292
; Author: Andy_A
; Date: 2016-10-12 13:03:22
; Title: DLA seaweed
; Description: Make your own cartoonish seaweed

;http://psoup.math.wisc.edu/archive/alex_day.txt

sw% = 1152 : sh% = 720


AppTitle "Diffusion Limited Aggregation"
Graphics sw, sh, 32, 2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

Const white% = $FFFFFF

w% = 192: h% = 120 : m% = 24
multiplier% = 6

Dim hxc%(23), nc%(23,2)

palette

style% = 1
exitFlag% = 0
While exitFlag = 0
	Dim s(w+1, h+1)
	d = 1
	Cls
	While d <= h
		x = Rand(1,w-1)
		y = d
		c = 0
		While y <> 0 And c = 0
			c = s(x, y - 1)
			If c = 0 Then c = s(x - 1, y - 1)
			If c = 0 Then c = s(x + 1, y - 1)
			If Rand(0,1) = 1 And s(x + 1, y - 1) <> 0 Then c = s(x + 1, y - 1)
			If c = 0 Then y = y - 1
		Wend
		If KeyHit(1) Then End
		If c = 0 Then c = (x Mod m)
		s(x, y) = c
		If y = d - 1 Then d = d + 1
	Wend
	deltaClr# = 111.0/Float(sh)
	bGrad# = 112.0
	For i = 0 To sh-1
		Color 0,Int(bGrad*0.7), Int(bGrad)
		Line(0, i, sw-1, i)
		bGrad = bGrad - deltaClr
	Next
	For j = 0 To h-1
		jm% = sh-j*multiplier
		For i = 0 To w-1
			c = s(i,j)
			im% = i*multiplier
			If c <> 0 Then
				Select style
					Case 1
						radius% = Rand(4,7)
						ink(0): cfill(im, jm, radius + 2)
						Color nc(c,0), nc(c,1), nc(c,2)
						cfill(im, jm, radius)
						ink(white): cfill(im-2, jm-2, 2)
					Case 2
						radius% = Rand(6,9)
						xr = im-radius
						yr = jm-radius
						r2 = radius+radius
						ink(0): Rect xr,yr,r2,r2,True
						Color nc(c,0), nc(c,1), nc(c,2)
						Rect xr+2,yr+2,r2-4,r2-4,True
						ink(white)
						Rect xr+4,yr+4,3,3,True
					Case 3
						makeTris(im, jm, hxc(c))
				End Select
			End If
		Next
	Next
	box(380, 2, 90, 20, $90, $FFFFFF)
	box(480, 2, 90, 20, $90, $FFFFFF)
	box(580, 2, 90, 20, $90, $FFFFFF)
	box(680, 2, 90, 20, $FF0000, $FFFFFF)
	ink($FFFFFF)
	Text 425,6, "Circles",True
	Text 525,6, "Squares",True
	Text 625,6, "Triangles",True
	Text 725,6, "EXIT",True
	Flip
	
	While mouseExit = 0
		WaitMouse()
		mx = MouseX() : my = MouseY()
		If pnr(mx, my, 680, 2, 90, 20) Then
			mouseExit = 1
			exitFlag = 1
		End If
		If pnr(mx, my, 380, 2, 90, 20) Then style = 1: mouseExit = 1
		If pnr(mx, my, 480, 2, 90, 20) Then style = 2: mouseExit = 1
		If pnr(mx, my, 580, 2, 90, 20) Then style = 3: mouseExit = 1
		FlushMouse()
	Wend
	mouseExit = 0
Wend
End

Function ink%(hexVal%)
	Local red%, grn%, blu%
	red = (hexVal Shr 16) And 255
	grn = (hexVal Shr 8 ) And 255
	blu = hexVal And 255
	Color red, grn, blu
End Function

Function cfill%(x%, y%, r%)
	Oval x-r, y-r, r + r, r + r,True
End Function

Function box%(x%, y%, w%, h%, clr1%, clr2%)
	;main box color is clr1 (hex value)
	;box outline color is clr2 (hex value)
	ink(clr2)
	Rect x, y, w, h,True
	ink(clr1)
	Rect x+2,y+2,w-4,h-4,True
End Function

Function pnr(px, py, rx, ry, rw, rh)
	;===========================================
	;   Function "Point In Rectangle"
	;===========================================
	; This function checks to see if the point
	; (px,py) is within the specified rectangle.
	;
	; If the point is inside the rectangle
	; a value of 1 is returned.
	;
	; If the point is not inside the rectangle
	; a value of 0 is returned.
	;============================================
	; px = the X coord of the point in question
	; py = the Y coord of the point in question
	; rx = Upper  Left X coord of rectangle
	; ry = Upper  Left Y coord of rectangle
	; rw =  width of rectangle
	; rh = height of rectangle
	;=============================================
    Return ((px>=rx) And (px<=(rx+rw-1)) And (py>=ry) And (py<=(ry+rh-1)))
End Function

Function palette()
	Local i%
	hxc(0)=$0000FF: hxc(1)=$4000FF
	hxc(2)=$8000FF: hxc(3)=$C000FF             
	hxc(4)=$FF00FF: hxc(5)=$FF00C0
	hxc(6)=$FF0080: hxc(7)=$FF0040
	hxc(8)=$FF0000: hxc(9)=$FF4000
	hxc(10)=$FF8000: hxc(11)=$FFC000
	hxc(12)=$FFFF00: hxc(13)=$C0FF00
	hxc(14)=$80FF00: hxc(15)=$40FF00
	hxc(16)=$00FF00: hxc(17)=$00FF40
	hxc(18)=$00FF80: hxc(19)=$00FFC0
	hxc(20)=$00FFFF: hxc(21)=$00C0FF
	hxc(22)=$0080FF: hxc(23)=$0040FF
	For i = 0 To 23
		nc(i,0) = (hxc(i) Shr 16) And 255
		nc(i,1) = (hxc(i) Shr  8) And 255
		nc(i,2) = hxc(i) And 255
	Next
End Function

Function makeTris(cx%, cy%, hexClr%)
	Local a1%,a2%,a3%,mag1%,mag2%,mag3%,maga%,magb%,magc%
	Local x1%,y1%,x2%,y2%,x3%,y3%,xa%,ya%,xb%,yb%,xc%,yc%
    a1 = Rand(24,94)
    a2 = Rand(134,224)
    a3 = Rand(244,344)
    mag1 = Rand(8,14)
    mag2 = Rand(8,14)
    mag3 = Rand(8,14)
	maga = mag1-1
	magb = mag2-1
	magc = mag3-1
    x1 = Cos(a1)*mag1+cx
    y1 = Sin(a1)*mag1+cy
    x2 = Cos(a2)*mag2+cx
    y2 = Sin(a2)*mag2+cy
    x3 = Cos(a3)*mag3+cx
    y3 = Sin(a3)*mag3+cy
    xa = Cos(a1)*maga+cx
    ya = Sin(a1)*maga+cy
    xb = Cos(a2)*magb+cx
    yb = Sin(a2)*magb+cy
    xc = Cos(a3)*magc+cx
    yc = Sin(a3)*magc+cy
	Ink(hexClr)
    triFill(x1, y1, x2, y2, x3, y3)
	Color 0,0,0
	Line(x1,y1, x2,y2)
	Line(x3,y3, x1,y1)
	Line(xa,ya, xb,yb)
	Line(xc,yc, xa,ya)
	Color 255,255,255
	Line(x2,y2, x3,y3)
	Line(xb,yb, xc,yc)
End Function


Function triFill%(x1#,y1#,x2#,y2#,x3#,y3#)
	Local slope1#,slope2#,slope3#,x#,y#,length#
	If x2 < x1
 		x = x2: y = y2: x2 = x1: y2 = y1: x1 = x: y1 = y
	End If
	If x3 < x1
 		x = x3: y = y3: x3 = x1: y3 = y1: x1 = x: y1 = y
	End If
	If x3 < x2
		x = x3: y = y3: x3 = x2: y3 = y2: x2 = x: y2 = y
	End If
	If x1 <> x3 Then slope1 = (y3-y1)/(x3-x1)
		length = x2 - x1
		If length <> 0
			slope2 = (y2-y1)/(x2-x1)
			For x = 0 To length
				Line x+x1,x*slope1+y1,x+x1,x*slope2+y1
			Next
	End If
	y = length*slope1+y1
	length = x3-x2
	If length <> 0
		slope3 = (y3-y2)/(x3-x2)
		For x = 0 To length
			Line x+x2,x*slope1+y,x+x2,x*slope3+y2
		Next
	End If
End Function
