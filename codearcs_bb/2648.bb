; ID: 2648
; Author: mark1110
; Date: 2010-01-25 19:23:13
; Title: system status
; Description: computer check

AppTitle "deh - system status2.0"
Graphics 600,600,16,2
tgd = CountGfxDrivers ()
tgm = CountGfxModes ()

Repeat 
	Text 10,80,"time : " + CurrentTime()
	Text 10,100,"date : " + CurrentDate()
	Text 10,120, "mouseX : " + MouseX()
	Text 10,140,"mouseY : " + MouseY()
	Text 10,160,"mousez : " + MouseZ()
	Text 10,180,"mousexspeed : " + MouseXSpeed()
	Text 10,200,"mouseyspeed : " + MouseYSpeed()
	Text 10,220,"mousezspeed : " + MouseZSpeed()
	If JoyType =0 Then Text 10,240,"no joystick"
	If JoyType =1 Then Text 10,240,"digital joystick"
	If JoyType =2 Then Text 10,240,"analog joystick"
	If JoyType >0 Then Text 10,290,"joy x : " + JoyX()
	If JoyType >0 Then Text 10,310,"joy y : " + JoyY()
	If JoyType >0 Then Text 10,330,"joy z : " + JoyZ()
	tvm = TotalVidMem () / 1000000
	avm = AvailVidMem () / 1000000
	Text 10,350,"you have " + tgd + " graphics card(s) on your computer"
	Text 10,370,"with a total of " + tgm + " modes and a total memory of "
	Text 10,390, tvm + " megabytes of which " + avm + " megabytes are available"
	k = GetKey()
	If k > 0 Then t = True
	If t = True Then Text 10,260,"keypress : " + k 
	If t = True Then Delay 499
	If t = True Then t = False 
	Delay 101
	Cls

Until KeyHit (1)

End
