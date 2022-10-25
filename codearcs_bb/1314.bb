; ID: 1314
; Author: n8r2k
; Date: 2005-03-07 19:52:08
; Title: Circle Madness
; Description: Cool stuff with ovals

Graphics 800,600,16,2
SeedRnd MilliSecs()
x = 399
y = 299
w = 3
l = 3
f = 1
r = 1
c = 1

While Not KeyHit(1)
.Red
If r >= 255
	x = 399
	y = 299
	w = 3
	l = 3
	r = 255
	c = -1
ElseIf r <= 0
	x = 399
	y = 299
	w = 3
	l = 3
	r = 0
	c = 1
EndIf 
Color r,0,0
Oval x,y,w,l,f
f = 0
x = x - 1
y = y - 1
w = w + 2
l = l + 2
r = r + c
Wend
