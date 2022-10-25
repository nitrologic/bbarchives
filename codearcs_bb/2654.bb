; ID: 2654
; Author: mark1110
; Date: 2010-02-05 23:32:00
; Title: ss4.0
; Description: 3d capable ckeck

AppTitle "deh - system status4.0"
Graphics 790,800,16,2
tgd = CountGfxDrivers ()
tgm = CountGfxModes ()
tvm = TotalVidMem () / 8388608
Repeat 
	Text 10,80,"time : " + CurrentTime()
	Text 10,100,"date : " + CurrentDate()
	Text 10,120, "mouseX : " + MouseX()
	Text 10,140,"mouseY : " + MouseY()
	Text 10,160,"mousez : " + MouseZ()
	Text 10,180,"mousexspeed : " + MouseXSpeed()
	Text 10,200,"mouseyspeed : " + MouseYSpeed()
	Text 10,220,"mousezspeed : " + MouseZSpeed()
	If KeyHit (32) Then Gosub drive
	If JoyType =0 Then Text 10,240,"no joystick"
	If JoyType =1 Then Text 10,240,"digital joystick"
	If JoyType =2 Then Text 10,240,"analog joystick"
	If JoyType >0 Then Text 10,290,"joy x : " + JoyX()
	If JoyType >0 Then Text 10,310,"joy y : " + JoyY()
	If JoyType >0 Then Text 10,330,"joy z : " + JoyZ()
	avm = AvailVidMem () / 8388606
	Text 10,350,"you have " + tgd + " graphics card(s) on your computer"
	Text 10,370,"with a total of " + tgm + " modes and a total memory of "
	Text 10,390, tvm + " megabytes of which " + avm + " megabytes are available"
	l = 410
	g = 0
		Repeat
			g=g+1
			If GfxDriver3D (g)
				Text 10,l,"the name of graphics card " + g + " is " + GfxDriverName (g) + " which is 3D-capable"
					Else
				Text 10,l,"the name of graphics card " + g + " is " + GfxDriverName (a) + " which is NOT 3D-capable"
					EndIf
			l=l+20
			
		Until tgd = g
	k = GetKey()
	If k > 0 Then 
		Text 10,260,"keypress : " + k 
		Delay 499
	EndIf
	Delay 101
	Cls
Until KeyHit (1)
End 
.drive
Cls
Delay 100
FlushKeys 
Repeat
	Locate 200,100
	Print "hidden harddrive checker"
	Locate 200,400
	d$ = Input$ ("What harddrive do you want to check? ")
	If d$ = "exit" Then Exit 
	If FileType (d$) = 0 Then Print "this harddrive does not exist"
	If FileType (d$) = 2 Then  
		Print "this harddrive does exist"
	EndIf 
	WaitKey 
Forever 
Return
