; ID: 228
; Author: Rhodan
; Date: 2002-02-10 00:00:00
; Title: Sin() and Cos() demo 
; Description: Tutorial on using Sin() and Cos() to determine movement

[code]
;============================================
;     Demostration of Sin() and Cos()       =
;     (good for ship movement like in)      =
;               ASTEROOIDS!                 =
;============================================



Graphics 640,480

shipspeed#=0
shipangle#=0

orig_x#=GraphicsWidth()/2 ; get center of screen for everything
orig_y#=GraphicsHeight()/2
ship_x#=orig_x#
ship_y#=orig_y#

SetBuffer BackBuffer()

Repeat

Cls

If KeyDown(200) Then				; up arrow

	shipspeed#=shipspeed#+1
	If shipspeed#>200 Then shipspeed#=200

ElseIf KeyDown(208) Then			; down arrow

	shipspeed#=shipspeed#-1
	If shipspeed#<0 Then shipspeed#=0

EndIf

;================================================
;                 Ship Angle                    =
; The first thing you have to do is give your   =
; ship a rotation. You rotate in a circle so    =
; you need a number between 0 and 359. Wrap the =
; value by adding or subtracting 360 from it    =
; when it goes out of range. The example below  =
; only steps by 1s so its would work if I just  =
; made it go to 359 if it was less than zero    =
; but, you'll probably be using floats so it    =
; wouldn't work quite right. Better this way    =
;================================================

If KeyDown(203) Then				; left arrow

	shipangle#=shipangle#-1
	If shipangle#<0 Then shipangle#=shipangle#+360

ElseIf KeyDown(205) Then			; right arrow

	shipangle#=shipangle#+1
	If shipangle#>359 Then shipangle#=shipangle#-360

EndIf

; Now that we have a ship angle, we know where its
; pointing. Look at the chart below. If the ship is
; at 45 degrees, it would be pointing down and to
; the left. To move in that direction you need a
; negative change in its X coord and a positive
; change in its Y coord. This is where Sin() and
; Cos() come in. In your windows calculator (in
; start menu/programs/accessories), choose
; scientific mode. Near the lower left side are
; Sin and Cos buttons. Type in 45 then hit the
; Sin button. This will give you the change in
; X you'll need. Now do the same for Cos - which
; is the change in Y. You'll see its the same
; value. Both are about .707. Thats the amount
; you'd have to change each coordinate by to move
; 1 unit of distance at 45 degrees. Try some
; other angles. You'll notice that this chart and
; those numbers disagree about the sign of the
; Sin results. Where this chart shows negative,
; the calculator shows positive etc. Thats
; because computer graphics are the opposite
; in how they number horizontaly. Thats why the
; formula below subtracts Sin and adds Cos.
;
; Anyway, an angle of 45 degrees would need to
; move -.707 in the X sin(45) and +0.707 in the
; y Cos(45) planes. Imagine a little arrow
; from 0,0 to -0.707,0.707. That would be what
; people refer to as a "vector". If you measured
; its length, you'd see it was exactly the same
; length as it would be If it went +1 at 90
; degrees.

; Here's the world according to 2D graphics
;                   180 degrees
;           -x,-y       | -y    +x,-y
;                       |
;                       |
;                       |
;           -x          |          +x
;90 degrees ------------+------------ 270 degrees
;                       |
;                       |
;                       |
;                       |
;           -x,+y       | +y    +x,+y

;                  0/360 Degrees




; Sin() Returns the horizontal component of an angle
; Cos() Returns the vertical component of an angle

component_x#=Sin(shipangle#)
component_y#=Cos(shipangle#)

; Now that you know how much you need to move in each
; direction, multiply that by the speed of your ship.
; Lets assume you're going speed 10 at 120 degrees.

displaced_x#=component_x#*shipspeed# ; 0.866 * 10 = 8.66 
displaced_y#=component_y#*shipspeed# ; -0.5 * 10 = -5


speeddir_x#=orig_x#-displaced_x# ; Now subtract from the ship's X position
speeddir_y#=orig_y#+displaced_y# ; and add to the Y position

; Now to show things on the screen.

Color 255,255,255
Line orig_x#,orig_y#,speeddir_x#,speeddir_y# ; Shows direction and speed of ship
Text speeddir_x#,speeddir_y#,"Ship speed: "+Int shipspeed#

; Draw X component size
If displaced_x#<0 Then Color 0,0,255 Else Color 255,0,0
Line orig_x#,orig_y#,speeddir_x#,orig_y#
Color 255,255,255
Text speeddir_x#,orig_y#,"DX "+Int (0-displaced_x#) ; Int them, too many numbers flying around =)

; Draw Y component size
If displaced_y#>0 Then Color 0,0,255 Else Color 255,0,0
Line orig_x#,orig_y#,orig_x#,speeddir_y#
Color 255,255,255
Text orig_x#,speeddir_y#,"YX "+ Int displaced_y#

Color 255,255,255
Text 20,GraphicsHeight()-40,"Ship angle: "+shipangle#+" Sin: "+Sin(shipangle#)+" Cos: "+Cos(shipangle#)

Flip

Until KeyDown(1)

End


[/code]
