; ID: 3091
; Author: bloos_magoos
; Date: 2013-11-23 21:58:24
; Title: xinput device test
; Description: Test your xbox or other xinput controllers or whatever

;Xbox 360 Controller Library
;Programmed by xtremegamr

;All Functions have X360_ before them


Graphics 1024,768,0,2
AppTitle "Xinput Demo by bloos_magoos"

While Not KeyHit(1)
	Cls
	
	;Test left stick
	Text 0,120,"Left Stick X - "+Int(X360_LeftStickX(0))
	Text 0,130,"Left Stick Y - " + Int(X360_LeftStickY(0))
	Oval 0,0,100,100,0
	Oval 50+X360_LeftStickX(0)*50,50+X360_LeftStickY(0)*50,1,1,1
	
	;Right
	Text 200,120,"Right Stick X - "+Int(X360_RightStickX(0))
	Text 200,130,"Right Stick Y - " + Int(X360_RightStickY(0))
	Oval 100,0,100,100,0
	Oval 150+X360_RightStickX(0)/4,50+X360_RightStickY(0)/4,1,1,1
	
	Text 300,50,"<--- LOL (.)(.)"
	Text 300,60,"Right Analog Stick Graphic is not exact because the function returns a 180 instead of 1"
	Line 0,180,1024,180 ;separator
	;/\/\/\/\/\/\/\
	;testing triggers
	;right
	Rect 50,200,50,10,0
	Rect 50,200,50,Int(-X360_ReturnTrigger(0)*10),1
	;Left
	Rect 0,200,50,10,0
	Rect 0,200,50,Int(X360_ReturnTrigger(0)*10),1
	
	
	
	
	Text 0,230,"NOTICE - - Pressing both triggers makes trigger axis return zero :("
	Text 0,240,"  This is because the triggers are read as one axis, so pressing both adds 1+ -1 (which is zero.)"
	Text 0,250,"  If anybody knows how to fix this, lemme know"
	Text 0,270,"Trigger Axis ->" +Int(X360_ReturnTrigger(0))
	Line 0,300,1024,300 ;separator
	;/\/\/\/\/\/\/\/\
	;Buttons
	
	
	If Not X360_AButtonDown(0)
		Text 0,310,"A"
		Else Text 0,310,">>A<<"
	EndIf
	
	
	If Not X360_BButtonDown(0)
		Text 0,320,"B"
		Else Text 0,320,">>B<<"
	EndIf
	
	If Not X360_XButtonDown(0)
		Text 0,330,"X"
		Else Text 0,330,">>X<<"
	EndIf
	
	If Not X360_YButtonDown(0)
		Text 0,340,"Y"
		Else Text 0,340,">>Y<<"
	EndIf
	
	If Not X360_RBButtonDown(0)
		Text 0,360,"Right Bumper"
		Else Text 0,360,">>Right Bumper<<"
	EndIf
	
	If Not X360_LBButtonDown(0)
		Text 0,370,"Left Bumper"
		Else Text 0,370,">>Left Bumper<<"
	EndIf
	
	If Not X360_LStickButtonDown(0)
		Text 0,390,"Left Stick"
		Else Text 0,390,">>Left Stick<<"
	EndIf
	
	If Not X360_RStickButtonDown(0)
		Text 0,400,"Right Stick"
		Else Text 0,400,">>Right Stick<<"
	EndIf
	
	If Not X360_StartButtonDown(0)
		Text 0,420,"Start"
		Else Text 0,420,">>Start<<"
	EndIf
	
	If Not X360_BackButtonDown(0)
		Text 0,430,"Back"
		Else Text 0,430,">>Back<<"
	EndIf
	
	Line 0,450,1024,450 ;separator
	;/\/\/\/\/\/\/\/\/\/\/\/\
	;Dpad
	dir=X360_DPadDir(0)
	
	Text 0,500,"D-PAD"
	If dir=1 
		Text 0,520,"^ up"
	EndIf
	
	If dir=2
		Text 0,520,"^ + > up+right"
	EndIf
	
	If dir=3
		Text 0,520,"> right"
	EndIf
	
	If dir=4
		Text 0,520,"v + > down+right"
	EndIf
	
	If dir=5
		Text 0,520,"v down"
	EndIf
	
	If dir=6
		Text 0,520,"v + < down+left"
	EndIf
	
	If dir=7
		Text 0,520,"< left"
	EndIf
	
	If dir=8
		Text 0,520,"^ + < up+left"
	EndIf
	
	If dir=0
		Text 0,520," - - "
	EndIf
	
	If dir=-1
		RuntimeError "Something happened I guess"
	EndIf
		
	
	
	
	
	
	Flip
Wend


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
;LeftStickX#()
;returns the x position of the Left Stick
Function X360_LeftStickX#(port)
	
	If port=-1 Then
		returnvalue#=JoyX()
	Else
		returnvalue#=JoyX(port)
	End If
	
	Return returnvalue#
	
End Function

;LeftStickY#()
;returns the y position of the Left Stick
Function X360_LeftStickY#(port)
	
	If port=-1 Then
		returnvalue#=JoyY()
	Else
		returnvalue#=JoyY(port)
	End If
	
	Return returnvalue#
	
End Function

;LeftStickXDir()
;returns the x direction of the Left Stick
Function X360_LeftStickXDir(port)
	
	If port=-1 Then
		returnvalue=JoyXDir()
	Else
		returnvalue=JoyXDir(port)
	End If
	
	Return returnvalue
	
End Function

;LeftStickYDir()
;return the y direction of the Left Stick
Function X360_LeftStickYDir(port)
	
	If port=-1 Then
		returnvalue=JoyYDir()
	Else
		returnvalue=JoyYDir(port)
	End If
	
	Return returnvalue
	
End Function

;RightStickX#()
;returns the x position of the Right Stick
Function X360_RightStickX#(port)
	
	If port=-1 Then
		returnvalue=JoyPitch()
	Else
		returnvalue=JoyPitch(port)
	End If
	
	Return returnvalue
	
End Function

;RightStickY#()
;returns the y position of the Right Stick
Function X360_RightStickY#(port)
	
	If port=-1 Then
		returnvalue=JoyYaw()
	Else
		returnvalue=JoyYaw(port)
	End If
	
	Return returnvalue
	
End Function

;RightStickXDir#()
;returns the x direction of the Right Stick
Function X360_RightStickXDir(neutral,port)
	
	If port=-1 Then
		If JoyPitch()<neutral Then returnvalue=-1
		If JoyPitch()>neutral Then returnvalue=1
	Else
		If JoyPitch(port)<neutral Then returnvalue=-1
		If JoyPitch(port)>neutral Then returnvalue=1
	End If
	
	Return returnvalue
	
End Function

;RightStickYDir#()
;returns the y direction of the Right Stick
Function X360_RightStickYDir(neutral,port)
	
	If port=-1 Then
		If JoyYaw()<neutral Then returnvalue=-1
		If JoyYaw()>neutral Then returnvalue=1
	Else
		If JoyYaw(port)<neutral Then returnvalue=-1
		If JoyYaw(port)>neutral Then returnvalue=1
	End If
	
	Return returnvalue
	
End Function

;ReturnTrigger#()
;returns trigger value (joyz)
Function X360_ReturnTrigger#(port)
	
	If port=-1 Then
		returnvalue#=JoyZ()
	Else
		returnvalue#=JoyZ(port)
	End If
	
	Return returnvalue#
	
End Function

;TriggerDown()
;returns the value of the current trigger that is down
Function X360_TriggerDown(port)
	
	If port=-1 Then
		z=JoyZDir()
	Else
		z=JoyZDir(port)
	End If
	
;return
	Return z
	
End Function

;AButtonHit()
;returns true is the a button was hit
Function X360_AButtonHit(port)
	
	If port=-1 Then
		If JoyHit(1) Then Return True
	Else
		If JoyHit(1,port) Then Return True
	End If
	
	Return False
	
End Function

;BButtonHit()
;returns true is the b button was hit
Function X360_BButtonHit(port)
	
	If port=-1 Then
		If JoyHit(2) Then Return True
	Else
		If JoyHit(2,port) Then Return True
	End If
	
	Return False
	
End Function

;YButtonHit()
;returns true is the y button was hit
Function X360_YButtonHit(port)
	
	If port=-1 Then
		If JoyHit(4) Then Return True
	Else
		If JoyHit(4,port) Then Return True
	End If
	
	Return False
	
End Function

;XButtonHit()
;returns true is the x button was hit
Function X360_XButtonHit(port)
	
	If port=-1 Then
		If JoyHit(3) Then Return True
	Else
		If JoyHit(3,port) Then Return True
	End If
	
	Return False
	
End Function

;RBButtonHit()
;returns true is the rbumper was hit
Function X360_RBButtonHit(port)
	
	If port=-1 Then
		If JoyHit(6) Then Return True
	Else
		If JoyHit(6,port) Then Return True
	End If
	
	Return False
	
End Function

;LBButtonHit()
;returns true is the lbumper was hit
Function X360_LBButtonHit(port)
	
	If port=-1 Then
		If JoyHit(5) Then Return True
	Else
		If JoyHit(5,port) Then Return True
	End If
	
	Return False
	
End Function

;StartButtonHit()
;returns true is the start button was hit
Function X360_StartButtonHit(port)
	
	If port=-1 Then
		If JoyHit(8) Then Return True
	Else
		If JoyHit(8,port) Then Return True
	End If
	
	Return False
	
End Function

;BackButtonHit()
;returns true is the x button was hit
Function X360_BackButtonHit(port)
	
	If port=-1 Then
		If JoyHit(7) Then Return True
	Else
		If JoyHit(7,port) Then Return True
	End If
	
	Return False
	
End Function

;LStickButtonHit()
;returns true is the left stick pressed in (hit)
Function X360_LStickButtonHit(port)
	
	If port=-1 Then
		If JoyHit(9) Then Return True
	Else
		If JoyHit(9,port) Then Return True
	End If
	
	Return False
	
End Function

;RStickButtonHit()
;returns true is the right stick pressed in (hit)
Function X360_RStickButtonHit(port)
	
	If port=-1 Then
		If JoyHit(10) Then Return True
	Else
		If JoyHit(10,port) Then Return True
	End If
	
	Return False
	
End Function

;AButtonDown()
;returns true is the a button was Down
Function X360_AButtonDown(port)
	
	If port=-1 Then
		If JoyDown(1) Then Return True
	Else
		If JoyDown(1,port) Then Return True
	End If
	
	Return False
	
End Function

;BButtonDown()
;returns true is the b button was Down
Function X360_BButtonDown(port)
	
	If port=-1 Then
		If JoyDown(2) Then Return True
	Else
		If JoyDown(2,port) Then Return True
	End If
	
	Return False
	
End Function

;YButtonDown()
;returns true is the y button was Down
Function X360_YButtonDown(port)
	
	If port=-1 Then
		If JoyDown(4) Then Return True
	Else
		If JoyDown(4,port) Then Return True
	End If
	
	Return False
	
End Function

;XButtonDown()
;returns true is the x button was Down
Function X360_XButtonDown(port)
	
	If port=-1 Then
		If JoyDown(3) Then Return True
	Else
		If JoyDown(3,port) Then Return True
	End If
	
	Return False
	
End Function

;RBButtonDown()
;returns true is the rbumper was Down
Function X360_RBButtonDown(port)
	
	If port=-1 Then
		If JoyDown(6) Then Return True
	Else
		If JoyDown(6,port) Then Return True
	End If
	
	Return False
	
End Function

;LBButtonDown()
;returns true is the lbumper was Down
Function X360_LBButtonDown(port)
	
	If port=-1 Then
		If JoyDown(5) Then Return True
	Else
		If JoyDown(5,port) Then Return True
	End If
	
	Return False
	
End Function

;StartButtonDown()
;returns true is the start button was Down
Function X360_StartButtonDown(port)
	
	If port=-1 Then
		If JoyDown(8) Then Return True
	Else
		If JoyDown(8,port) Then Return True
	End If
	
	Return False
	
End Function

;BackButtonDown()
;returns true is the x button was Down
Function X360_BackButtonDown(port)
	
	If port=-1 Then
		If JoyDown(7) Then Return True
	Else
		If JoyDown(7,port) Then Return True
	End If
	
	Return False
	
End Function

;LStickButtonDown()
;returns true is the left stick pressed in (Down)
Function X360_LStickButtonDown(port)
	
	If port=-1 Then
		If JoyDown(9) Then Return True
	Else
		If JoyDown(9,port) Then Return True
	End If
	
	Return False
	
End Function

;RStickButtonDown()
;returns true is the right stick pressed in (Down)
Function X360_RStickButtonDown(port)
	
	If port=-1 Then
		If JoyDown(10) Then Return True
	Else
		If JoyDown(10,port) Then Return True
	End If
	
	Return False
	
End Function


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
;
;;xtremegamr 's code for the DPAD

;DPadDir()
;returns the direction of the d-pad
Function X360_DPadDir(port)
	
	If port=-1 Then
		dir=JoyHat()
	Else
		dir=JoyHat(port)
	End If
	
;check direction
	Select dir
		Case 0 ;up
			Return 1
		Case 45 ;up-right
			Return 2
		Case 90 ;right
			Return 3
		Case 135 ;down-right
			Return 4
		Case 180 ;down
			Return 5
		Case 225 ;down-left
			Return 6
		Case 270 ;left
			Return 7
		Case 315 ;up-left
			Return 8
		Case -1 ;no direction pressed
			Return 0
		Default ;just in case something goes VERY wrong
			Return -1
	End Select
	
End Function
