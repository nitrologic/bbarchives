; ID: 2095
; Author: xtremegamr
; Date: 2007-08-22 16:22:23
; Title: Xbox 360 Controller Input
; Description: A controller library for the X360 Controller

;Xbox 360 Controller Library
;Programmed by xtremegamr

;All Functions have X360_ before them

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
