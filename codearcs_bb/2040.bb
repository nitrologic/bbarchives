; ID: 2040
; Author: Devils Child
; Date: 2007-06-22 15:22:19
; Title: Draw Line
; Description: Draws a line using WritePixelFast

Graphics 640, 480, 32, 2
SetBuffer BackBuffer()

While Not KeyHit(1)
	LockBuffer()
	ms = MilliSecs()
	mx = MouseX()
	my = MouseY()
	For i = 1 To 10000
		Line2 320, 240, mx, my
	Next
	ms = MilliSecs() - ms
	UnlockBuffer()
	Text 10, 10, "Millisecs per line: " + Float(ms * .0001)
	Text 10, 30, "The line you see is drawn 10.000 times to measure the correct time!!"
	Flip 0
	Cls
Wend
End

Function Line2(p1x, p1y, p2x, p2y)
If Abs(p1y - p2y) <= Abs(p1x - p2x) Then
	If p1x > p2x Then
		ptx = p1x
		pty = p1y
		p1x = p2x
		p1y = p2y
		p2x = ptx
		p2y = pty
	EndIf
	For x = p1x To p2x
		y = p1y + (x - p1x) * (p2y - p1y) / (p2x - p1x)
		WritePixelFast x, y, $FFFFFF
	Next
Else
	If p1y > p2y Then
		ptx = p1x
		pty = p1y
		p1x = p2x
		p1y = p2y
		p2x = ptx
		p2y = pty
	EndIf
	x = p1x
	For y = p1y To p2y
		x = p1x + (y - p1y) * (p2x - p1x) / (p2y - p1y)
		WritePixelFast x, y, $FFFFFF
	Next
EndIf
End Function
