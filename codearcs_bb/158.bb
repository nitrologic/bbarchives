; ID: 158
; Author: Rob
; Date: 2001-12-07 00:06:46
; Title: Mad Scientist lightning function!
; Description: Raise the dead or electrocute your mates.

;quick lightning bolt hack by rob cummings (rob@redflame.net)

Graphics 640,480,16,2
SetBuffer BackBuffer()
While Not KeyHit(1)
	Cls
	
	;bolt(x,y,x2,y2,points,multiplier) - this is all you need.
	bolt(x1,y1,x2,y2,32,scale)
	
	;simple randomiser to see the function working (to demonstrate)
	If timer<0
		timer=100
		x1=Rnd(640)
		y1=Rnd(480)
		x2=Rnd(640)
		y2=Rnd(480)
		scale=8+Rnd(16)
	Else
		timer=timer-1
	EndIf
	Color 255,255,255
	Oval x1,y1,8,8
	Oval x2,y2,8,8
	Flip
Wend
End

Function bolt(bx1,by1,bx2,by2,s,bscale)
	Color 90,90,255
	x=bx2
	y=by2
	xstep=(bx1-bx2)/s
	ystep=(by1-by2)/s
	LockBuffer BackBuffer()
	For i=1 To s-1
		r1=Rnd(-bscale,bscale)
		r2=Rnd(-bscale,bscale)
		x2=(x+xstep)+r1
		y2=(y+ystep)+r2
		Line x,y,x2,y2
		x=x2
		y=y2
	Next
	Line x,y,bx1,by1
	UnlockBuffer BackBuffer()
End Function
