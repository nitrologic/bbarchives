; ID: 1592
; Author: Pongo
; Date: 2006-01-08 12:56:00
; Title: Dual Stick Config
; Description: Configure any dual axis joystick to work with your game

Graphics 640,480,0,2
SetBuffer BackBuffer()
HidePointer

Global Controller_port = 0
Global debug = 0

Global info$ = " "

Type joypad
	Field x1id
	Field y1id
	Field x2id
	Field y2id

	Field x1invert
	Field y1invert
	Field x2invert
	Field y2invert
End Type 

Dim j.joypad(3)

For port = 0 To 3
	j(port) = New joypad
	j(port)\x1id = 0
	j(port)\y1id = 0
	j(port)\x2id = 0
	j(port)\y2id = 0
	j(port)\x1invert = 1 ;toggles between 1 and -1 
	j(port)\y1invert = 1
	j(port)\x2invert = 1
	j(port)\y2invert = 1
Next 


Dim joy_label$(8)
joy_label(0) = "null"
joy_label(1) = "JoyX()"
joy_label(2) = "JoyY()"
joy_label(3) = "JoyZ()"
joy_label(4) = "JoyPitch()"
joy_label(5) = "JoyRoll()"
joy_label(6) = "JoyYaw()"
joy_label(7) = "JoyU()"
joy_label(8) = "JoyV()"

While Not KeyHit(16) ; Q for Quit
	Cls
	
	drawstuff()
	checkinput()

	; map the input values so the red dots can be drawn in position
	x1 = FitValueToRange#( MapAxis(j(Controller_port)\x1id,j(Controller_port)\x1invert), -1, 1, 50, 150 )
	y1= 	FitValueToRange#( MapAxis(j(Controller_port)\y1id,j(Controller_port)\y1invert), -1,1, 275, 375 )
	x2 = FitValueToRange#( MapAxis(j(Controller_port)\x2id,j(Controller_port)\x2invert), -1, 1, 250, 350 )
	y2= 	FitValueToRange#( MapAxis(j(Controller_port)\y2id,j(Controller_port)\y2invert), -1,1, 275, 375 )

	;draw the control dots
	Color 185,0,0
	Oval  x1-5,y1-5,10,10
	Oval  x2-5,y2-5,10,10

	;debug and cursor
	;Text MouseX()+5,MouseY()-15,MouseX()+","+MouseY()
	Line MouseX(),MouseY()-5,MouseX(),MouseY()+5
	Line MouseX()-5,MouseY(),MouseX()+5,MouseY()

	Flip

Wend

End

Function FitValueToRange#( InValue#, RangeIn_Start#, RangeIn_End#, RangeOut_Start#, RangeOut_End# )
	
	OldRange# = RangeIn_End#-RangeIn_Start#
	NewRange# = RangeOut_End# - RangeOut_Start#	
	
	OutValue# = ((InValue#-RangeIn_Start) / OldRange#) * NewRange#	+ RangeOut_Start		

	Return OutValue#

End Function

Function drawstuff()
	Color 255,255,255
	Text 10,10,"Dual stick configuration utility"
	Text 10,25,"Directions for use:"

	Color 35,65,115
	Select controller_port ; draw current port selected
		Case 0
			Rect 10,45,45,25
		Case 1
			Rect 60,45,45,25
		Case 2
			Rect 110,45,45,25
		Case 3
			Rect 160,45,45,25
	End Select

	Color 0,0,255
	Rect 10,45,45,25,0 ; draw ports (outlines)
	Rect 60,45,45,25,0
	Rect 110,45,45,25,0
	Rect 160,45,45,25,0

	Color 255,255,255	
	Text 27,53,"0" 
	Text 77,53,"1"
	Text 127,53,"2"
	Text 177,53,"3"

	Text 220,45,"select controller port (0 for  single joypad)"
	Text 220,60,"others are only needed for multiple joypads"

	Color 185,0,0
	Rect 10,100,195,25

	Color 255,255,255
	Rect 10,100,195,25,0
	Text 25,105,"assign axis controls"

	Text 220,105,"click to assign the custom controls"

	Rect 10,135,600,100,0 ; large text info box outline
	Color 128,128,128
	Text 15,140,"After assigning the controls, test stick movement in the boxes below"
	Text 15,155,"To invert an axis, right click on it's blue box"
	Text 15,180,"when done, save the config file and exit, or repeat with the other ports"

	;draw boxes for axis controls
	Color 50,65,220	
	If j(Controller_port)\x1invert =-1 Then Color 255,128,0
	Rect 50,395,175,14,1

	Color 50,65,220
	If j(Controller_port)\y1invert =-1 Then Color 255,128,0
	Rect 50,415,175,14,1

	Color 50,65,220
	If j(Controller_port)\x2invert =-1 Then Color 255,128,0
	Rect 250,395,175,14,1

	Color 50,65,220
	If j(Controller_port)\y2invert =-1 Then Color 255,128,0
	Rect 250,415,175,14,1

	Color 255,255,255
	Text 50,380,"Stick 1" : 	Text 250,380,"Stick 2"
	Text 60,396,"X axis: " + joy_label(j(Controller_port)\x1id) : Text 260,396,"X axis: " + joy_label(j(Controller_port)\x2id)
	Text 60,416,"Y axis: " + joy_label(j(Controller_port)\y1id) : Text 260,416,"Y axis: " + joy_label(j(Controller_port)\y2id)

	Rect 50,275, 100,100,0 ;draw boxes for controllers
	Rect 250,275, 100,100,0

	Color 64,64,64
	Rect 450,275,110,20
	Rect 450,300,110,20
	Rect 450,325,110,20
	Rect 450,350,110,20

	Color 255,255,255
	Text 453,277,"(Tab) Debug"
	Text 453,302,"(S)ave Config"
	Text 453,327,"(L)oad Config"
	Text 453,352,"(Q)uit"

Text 220,250,info$ 

	If debug = 1
			Color 16,16,16
			Rect 0,25,640,217
			Color 48,48,48
			Rect 40,30,300,200
			Color 64,64,64 ; draw grey boxes
			For loop = 50 To 190 Step 20
				Rect 180,loop,150,15
			Next
			Rect 40,30,300,200,0
			Rect 39,29,302,202,0
	
			Color 128,128,128 ; draw boxes showing movement of axis
			Rect 180,50,FitValueToRange#( JoyX(Controller_port), -1, 1, 0, 150 ),15
			Rect 180,70,FitValueToRange#( JoyY(Controller_port), -1, 1, 0, 150 ),15
			Rect 180,90,FitValueToRange#( JoyZ(Controller_port), -1, 1, 0, 150 ),15
			Rect 180,110,FitValueToRange#( JoyPitch(Controller_port), -180, 180, 0, 150 ),15
			Rect 180,130,FitValueToRange#( JoyRoll(Controller_port), -180, 180, 0, 150 ),15
			Rect 180,150,FitValueToRange#( JoyYaw(Controller_port), -180, 180, 0, 150 ),15
			Rect 180,170,FitValueToRange#( JoyU(Controller_port), -1, 1, 0, 150 ),15
			Rect 180,190,FitValueToRange#( JoyV(Controller_port), -1, 1, 0, 150 ),15
		
			Color 160,160,160 ; show values of axis
			Text 50,50, "1. JoyX()      : " + JoyX(Controller_port) 
			Text 50,70, "2. JoyY()      : " + JoyY(Controller_port) 
			Text 50,90, "3. JoyZ()      : " + JoyZ(Controller_port) 
			Text 50,110,"4. JoyPitch()  : " + JoyPitch(Controller_port)
			Text 50,130,"5. JoyRoll()   : " + JoyRoll(Controller_port)
			Text 50,150,"6. JoyYaw()    : " + JoyYaw(Controller_port)
			Text 50,170,"7. JoyU()      : " + JoyU(Controller_port)
			Text 50,190,"8. JoyV()      : " + JoyV(Controller_port) 
	EndIf 
End Function 

Function checkinput()
	Color 255,0,0
	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,50,395,175,14) ; joy x 1
		Rect 49,394,177,16,0 ; draw a highlight rectangle
		j(Controller_port)\x1id = j(Controller_port)\x1id +MouseZSpeed ()
		If MouseHit (2) Then j(Controller_port)\x1invert = - j(Controller_port)\x1invert
		If j(Controller_port)\x1id > 8 Then j(Controller_port)\x1id =0
		If j(Controller_port)\x1id < 0 Then j(Controller_port)\x1id =8
	EndIf

	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,50,415,175,14) ; joy y 1
		Rect 49,414,177,16,0	; draw a highlight rectangle
		j(Controller_port)\y1id = j(Controller_port)\y1id +MouseZSpeed ()
		If MouseHit (2) Then j(Controller_port)\y1invert = - j(Controller_port)\y1invert
		If j(Controller_port)\y1id > 8 Then j(Controller_port)\y1id =0
		If j(Controller_port)\y1id < 0 Then j(Controller_port)\y1id =8
	EndIf

	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,250,395,175,14) ; joy x 2
		Rect 249,394,177,16,0	; draw a highlight rectangle
		j(Controller_port)\x2id = j(Controller_port)\x2id +MouseZSpeed ()
		If MouseHit (2) Then j(Controller_port)\x2invert = - j(Controller_port)\x2invert
		If j(Controller_port)\x2id > 8 Then j(Controller_port)\x2id =0
		If j(Controller_port)\x2id < 0 Then j(Controller_port)\x2id =8
	EndIf

	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,250,415,175,14) ; joy y 2
		Rect 249,414,177,16,0	; draw a highlight rectangle
		j(Controller_port)\y2id = j(Controller_port)\y2id +MouseZSpeed ()
		If MouseHit (2) Then j(Controller_port)\y2invert = - j(Controller_port)\y2invert
		If j(Controller_port)\y2id > 8 Then j(Controller_port)\y2id =0
		If j(Controller_port)\y2id < 0 Then j(Controller_port)\y2id =8
	EndIf

	;joy port selection
	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,10,45,45,25)
		Rect 10,45,45,25,0
		If MouseHit (1) Then controller_port = 0
	EndIf

	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,60,45,45,25)
		Rect 60,45,45,25,0
		If MouseHit (1) Then controller_port = 1
	EndIf

	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,110,45,45,25)
		Rect 110,45,45,25,0
		If MouseHit (1) Then controller_port = 2
	EndIf

	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,160,45,45,25) 
		Rect 160,45,45,25,0
		If MouseHit (1) Then controller_port = 3
	EndIf 

	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,10,100,195,25) ; assign all axis button
		Rect 10,100,195,25,0
		If MouseHit (1) Then assign_all_joy_axis()
	EndIf 

	If KeyHit (59) Then Controller_port = 0 ;F1 key
	If KeyHit (60) Then Controller_port = 1 ;F2 key
	If KeyHit (61) Then Controller_port = 2 ;F3 key
	If KeyHit (62) Then Controller_port = 3 ;F4 key

	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,450,275,110,20) ; debug toggle
		Rect 450,275,110,20,0
		If MouseHit (1) Then debug = 1 - Debug
	EndIf

	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,450,300,110,20) ; save button
		Rect 450,300,110,20,0
		If MouseHit (1) Then writeconfig()
	EndIf

	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,450,325,110,20) ;load button
		Rect 450,325,110,20,0
		If MouseHit (1) Then readconfig()
	EndIf

	If RectsOverlap  (MouseX()-2,MouseY()-2,4,4,450,350,110,20) ; quit button
		Rect 450,350,110,20,0
		If MouseHit (1) Then End
	EndIf

	If KeyHit (15) Then debug = 1 - Debug ; tab key,... turns on debug

	If KeyHit (31) ; S key
		writeconfig()
	EndIf

	If KeyHit (38) ; L key
		readconfig()
	EndIf

End Function

Function getjoyaxis()
;why the double check?
; on some joysticks, when an axis is not present, the value is the max, so  I needed to get around that.
Repeat
	If Abs(JoyX(Controller_port)) > .5 And Abs(JoyX(Controller_port)) < .9 Return 1
	If Abs(JoyY(Controller_port)) > .5 And Abs(JoyY(Controller_port)) < .9 Return 2
	If Abs(JoyZ(Controller_port)) > .5 And Abs(JoyZ(Controller_port)) < .9 Return 3
	If Abs(JoyPitch(Controller_port)/180) > .5 And Abs(JoyPitch(Controller_port)/180) < .9 Return 4
	If Abs(JoyRoll(Controller_port)/180) > .5 And Abs(JoyRoll(Controller_port)/180) < .9 Return 5
	If Abs(JoyYaw(Controller_port)/180) > .5 And Abs(JoyYaw(Controller_port)/180) < .9 Return 6
	If Abs(JoyU(Controller_port)) > .5 And Abs(JoyU(Controller_port)) < .9 Return 7
	If Abs(JoyV(Controller_port)) > .5 And Abs(JoyV(Controller_port)) < .9 Return 8
	If KeyHit(1)	Return 0
Forever
End Function

Function MapAxis#( axis, invert )
   Select axis
      Case 1
         joy#=JoyX(Controller_port)
          Case 2
         joy#=JoyY(Controller_port)
      Case 3
         joy#=JoyZ(Controller_port)
      Case 4
         joy#=JoyPitch(Controller_port)/180
      Case 5
         joy#=JoyRoll(Controller_port)/180
      Case 6
         joy#=JoyYaw(Controller_port)/180
      Case 7
         joy#=JoyU(Controller_port)
      Case 8
         joy#=JoyV(Controller_port)
   End Select
   Return (joy# * invert)
End Function

Function Assign_All_Joy_Axis()
SetBuffer FrontBuffer()

Color 0,0,0
Rect 11,136,598,98 ;clear info box

Color 0,255,0
	Text 15,140,"move stick 1 left or right"
 	j(Controller_port)\x1id = getjoyaxis()
	Text 250,140,"Stick 1 X axis assigned to " + joy_label(j(Controller_port)\x1id)

	Delay (800)
	
	Text 15,155,"move stick 1 up or down" 
	j(Controller_port)\y1id = getjoyaxis()
	Text 250,155,"Stick 1 Y axis assigned to " + joy_label(j(Controller_port)\y1id)

	Delay (800)

	Text 15,170,"move stick 2 left or right"
	 j(Controller_port)\x2id = getjoyaxis()
	Text 250,170,"Stick 2 X axis assigned to " + joy_label(j(Controller_port)\x2id)

	Delay (800)


	Text 15,185,"move stick 2 up or down" 
	 j(Controller_port)\y2id = getjoyaxis()
	Text 250,185,"Stick 2 Y axis assigned to " + joy_label(j(Controller_port)\y2id)

	Delay (500)

SetBuffer BackBuffer()

End Function

Function writeconfig()
filename = WriteFile ("joypad.cfg")
For port = 0 To 3
	WriteLine (filename,"; Joypad port " + port)
	WriteLine (filename,j(port)\x1id)
	WriteLine (filename,j(port)\y1id)
	WriteLine (filename,j(port)\x2id)
	WriteLine (filename,j(port)\y2id)
	WriteLine (filename,j(port)\x1invert)
	WriteLine (filename,j(port)\y1invert)
	WriteLine (filename,j(port)\x2invert)
	WriteLine (filename,j(port)\y2invert)
Next 
CloseFile (filename)

info$ = "config file written"

End Function

Function readconfig()
filename = ReadFile ("joypad.cfg")
If filename <> 0
	For port = 0 To 3
		tmp = ReadLine (filename) ; skip this line in the file
		j(port)\x1id = ReadLine (filename)
		j(port)\y1id = ReadLine (filename)
		j(port)\x2id  = ReadLine (filename)
		j(port)\y2id = ReadLine (filename)
		j(port)\x1invert = ReadLine (filename)
		j(port)\y1invert = ReadLine (filename)
		j(port)\x2invert = ReadLine (filename)
		j(port)\y2invert = ReadLine (filename)
	Next 

	CloseFile (filename)

	info$ = "config file opened"
Else
	info$ = "load failed"
EndIf 
End Function
