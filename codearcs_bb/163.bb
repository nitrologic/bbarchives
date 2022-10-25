; ID: 163
; Author: NobodyInParticular
; Date: 2001-12-14 16:29:51
; Title: Simple Projectile Motion
; Description: Simple example of Projectile Motion

;Basic Projectile Motion Example
;coded by:  James Profitt (NobodyInParticular)
;---------------------------------------------
;BOS = Bottom of Screen
;XSP = X start point
;G = Gravity (Earth's gravity = 9.80m/s^2)
;V = Velocity (Power)
;A = Angle (in degrees)
;T# = Time
;---------------------------------------------

AppTitle "Basic Projectile Motion Example"
Graphics 800,600,32,2
SetBuffer BackBuffer()

BOS = GraphicsHeight()
XSP = 0
G# = 9.80
T# = 0
Cls
V = Input("Enter Velocity/Power:")
A = Input("Enter Angle:")
Cls
While Y < BOS
	T# = T# + .1
	X = XSP + ((V * Cos(A)) * t#)
	Y = BOS - ((V * Sin(A)) * t# - .5 * G# * T#^2)
	Plot X,Y
	Flip
Wend
Text 0,0,"Press any key to exit..."
Flip
FlushKeys()
WaitKey()
End
