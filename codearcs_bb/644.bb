; ID: 644
; Author: DarkEagle
; Date: 2003-04-07 12:04:24
; Title: Gradient Line
; Description: gradient line

Function GradLine(x1,y1,x2,y2,r1,g1,b1,r2,g2,b2)

dx# = x2-x1
dy# = y2-y1

m# = dy/dx

l# = Sqr(dx^2 + dy^2)

rinc# = (r2-r1)/l
ginc# = (g2-g1)/l
binc# = (b2-b1)/l

r# = r1
g# = g1
b# = b1

For x = x1 To x2
	argb = (b Or (g Shl 8) Or (r Shl 16) Or ($ff000000))
	WritePixel x,(m*x)+y1,argb
	r = r + rinc
	g = g + ginc
	b = b + binc
Next

End Function
