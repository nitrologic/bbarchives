; ID: 2761
; Author: ClayPigeon
; Date: 2010-09-01 17:01:28
; Title: Realistic Camera Shake
; Description: Using smoothed noise to simulate shakey hands or movement

Graphics3D 640,480,0,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

Global period# = 8
Global timer% = 0
Global amp# = 1
Global x0#,x1#,x2#,x3#,y0#,y1#,y2#,y3#
x0 = Rnd#(-amp,amp)
x1 = Rnd#(-amp,amp)
x2 = Rnd#(-amp,amp)
x3 = Rnd#(-amp,amp)
y0 = Rnd#(-amp,amp)
y1 = Rnd#(-amp,amp)
y2 = Rnd#(-amp,amp)
y3 = Rnd#(-amp,amp)

Global camera% = CreateCamera()
PositionEntity camera,0,0,-3

Global cube% = CreateCube()

While Not KeyHit(1)
	Cls
	
	If timer = period
		timer = 0
		x0 = x1
		x1 = x2
		x2 = x3
		x3 = Rnd#(-amp,amp)
		y0 = y1
		y1 = y2
		y2 = y3
		y3 = Rnd#(-amp,amp)
	Else
		timer = timer+1
	EndIf
	
	RotateEntity camera,CubicInterpolate(y0,y1,y2,y3,Float(timer/period)),CubicInterpolate(x0,x1,x2,x3,Float(timer/period)),0
	
	UpdateWorld
	RenderWorld
	
	Flip
Wend

End

Function CubicInterpolate#(x0#,x1#,x2#,x3#,mu#)
   Local a0#,a1#,a2#,a3#,mu2#

   mu2 = mu*mu
   a0 = x3-x2-x0+x1
   a1 = x0-x1-a0
   a2 = x2-x0
   a3 = x1
   
   Return a0*mu*mu2+a1*mu2+a2*mu+a3
End Function
