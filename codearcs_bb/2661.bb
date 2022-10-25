; ID: 2661
; Author: Streaksy
; Date: 2010-03-08 15:42:42
; Title: Curve#() - Apply a curve to a value based on a minimum and maxiumum and curve amplitude
; Description: No need to go from A to B in a rigid line anymore.

;EXAMPLE
Graphics 1024,768,32,2
SetBuffer BackBuffer()

MoveMouse 300,0
typ=3
Repeat
Cls
	If KeyHit(2) Then typ=1
	If KeyHit(3) Then typ=2
	If KeyHit(4) Then typ=3
	Color 255,255,255
	Text 0,0,"1-Smooth out"
	Text 0,20,"2-Smooth in"
	Text 0,40,"3-Smooth in and out"
		For x=1 To GraphicsWidth()
		y=(x*GraphicsHeight())/GraphicsWidth()
		y2=curve(y,0,GraphicsHeight(),typ,(Float(MouseX())/300))
		y=GraphicsHeight()-y
		y2=GraphicsHeight()-y2
		y=y/2+(GraphicsHeight()*.25)
		y2=y2/2+(GraphicsHeight()*.25)
		Color 0,0,255
		Plot x,y
		Color 255,0,0
		Plot x,y2
		Next
Flip
Until KeyHit(1)






;value, value min, value max, curve type (1=smooth out, 2=smooth in, 3=smooth both (default)), curve amplitute
Function Curve#(val#,min#,max#,typ=3,amp#=1)
val=val-min
max=max-min
If amp<>1 Then olval#=val
tween#=((val/max)*90)
If typ=<1 Then cos1#=Cos(tween-90):val=cos1*max					;smooth out
If typ=2 Then cos1#=1-Cos((tween)):val=cos1*max					;smooth in
If typ=3 Then cos1#=Cos(tween-90)*Sin(tween):val=cos1*max		;smooth in and out
If amp<>1 Then dif#=olval-val:val#=olval-(dif*amp) ;amplify
Return val+min
End Function
