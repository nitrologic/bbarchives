; ID: 1073
; Author: Mr Brine
; Date: 2004-06-07 09:07:48
; Title: Homing Missile Algorithm
; Description: does what it says!

; (c)oded by Mr Brine
;

Graphics 640, 480

SetBuffer(BackBuffer())

Global o.gradobj = New gradobj

Repeat

	Cls

	Text 0, 0, "* = your ship"
	Text 0, 12, "# = homing missile"
	Text 0, 24, "click the lmb to make homing missile home in on your ship"
	Text 0, 36, "press esc to quit"


	If(MouseHit(1)) CalcGrad(o, MouseX(), MouseY())
	
	Text MouseX(), MouseY(), "*"
	Text o\x, o\y, "#"

	AddGrad(o, 2)

	Flip
	VWait
	
Until KeyHit(1)


; ----------------------------------------------------------------------------------------------------


Type GradObj
	
	Field x#, y#
	Field xg#, yg#
	Field xd#, yd#
	Field ld#
	
End Type



Function CalcGrad(o.GradObj, newx#, newy#)

	o\xd = newx - o\x
	o\yd = newy - o\y

	o\ld = Sqr(o\xd * o\xd + o\yd * o\yd)

	o\xg = o\xd / o\ld
	o\yg = o\yd / o\ld

End Function 



Function AddGrad(o.GradObj, speed#)

	o\x = o\x + o\xg * speed
	o\y = o\y + o\yg * speed
	
End Function
