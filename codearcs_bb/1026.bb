; ID: 1026
; Author: Eole
; Date: 2004-05-11 14:43:04
; Title: KBS  Key Binding System like commercial game
; Description: Key Binding System like commercial game 'Joystick / Mouse / Keyboard )

;==================================================================
; Project Title	: Key Binding System
;
; File			: KBS_V100.bb
;
; Author		: Poursin Nicolas
; Email			: Nicolas.poursin@3dgametool.com
;
; Version		: 1.0.0          
; Date			: 07.05.2004
;         
;==================================================================

; Constante to identify the type of action
Const KBS_Key			= 1
Const KBS_JoystickHit	= 2
Const KBS_JoystickAxis	= 3
Const KBS_MouseHit		= 4
Const KBS_MouseAxis		= 5


; Global to retreive some informations for mouse
Global KBS_MWheel#	; mousezspped() if you don't use the BCF 3.0
Global KBS_MX#		; mousexspeed()
Global KBS_MY#      ; mouseyspeed()

; Global to retreive some informations for joystick (all joyx(), joyy() etc ...
Global KBS_JX#		
Global KBS_JY#	
Global KBS_JZ#		
Global KBS_JU#		
Global KBS_JV#
Global KBS_JRoll#
Global KBS_JPitch#
Global KBS_JYaw#

; Global to set the dead zone of each joystick axis
Global KBS_JDeadZone_X# 	= 0.1
Global KBS_JDeadZone_Y# 	= 0.1
Global KBS_JDeadZone_Z# 	= 0.1
Global KBS_JDeadZone_U# 	= 0.1
Global KBS_JDeadZone_V# 	= 0.1
Global KBS_JDeadZone_Pitch#	= 0.1
Global KBS_JDeadZone_Yaw# 	= 0.1
Global KBS_JDeadZone_Roll# 	= 0.1

; internat global for filter axis don't use it
Global KBS_joyvx1#,KBS_joyvx2#,KBS_joyvx3#
Global KBS_joyvy1#,KBS_joyvy2#,KBS_joyvy3#
Global KBS_joyvz1#,KBS_joyvz2#,KBS_joyvz3#
Global KBS_joyvu1#,KBS_joyvu2#,KBS_joyvu3#
Global KBS_joyvv1#,KBS_joyvv2#,KBS_joyvv3#
Global KBS_joyvp1#,KBS_joyvp2#,KBS_joyvp3#
Global KBS_joyvyy1#,KBS_joyvyy2#,KBS_joyvyy3#
Global KBS_joyvr1#,KBS_joyvr2#,KBS_joyvr3#


; The TYPE :-)
Type KBS_Action

	Field Label$
	Field state
	Field Value#
	Field key1
	Field Key2
	
End Type




Function KBS_Action( action$ )

	For c.KBS_Action = Each KBS_Action

		If c\label$ = action$ Then
			Return c\state
		EndIf
	Next

	Return False
	
End Function

Function KBS_TypeAction( action$, key )

	For c.KBS_Action = Each KBS_Action

		If c\label$ = action$ Then

			If key=1 Then
				ak = c\key1
			Else
				ak = c\key2
			EndIf

			If ak<1000 Then
				Return KBS_Key
			Else If ak>=1000 And ak<1100 Then
				Return KBS_MouseHit
			Else If ak>=1100 And ak<2000 Then
				Return KBS_MouseAxis
			Else If ak>=2000 And ak<2100 Then
				Return KBS_JoystickHit
			Else If ak>=2100 Then
				Return KBS_JoystickAxis
			EndIf
			
		End If
	Next

End Function

Function KBS_ActionValue#( action$ )

	For c.KBS_Action = Each KBS_Action
		If c\label$ = action$ Then
			Return c\value#
		EndIf
	Next
	
End Function


;==================================================================
; init the action
;==================================================================
Function KBS_AddAction( label$ , key1 , key2 = -1)

	For c.KBS_Action = Each KBS_Action
		If c\label$ = label$ Then
			Return False
		EndIf
	Next

	c.KBS_Action = New KBS_Action

	c\Label$ = label$
	c\state = False
	c\key1 = key1
	c\key2 = key2

	Return True
	
End Function

Function KBS_ChangeAction( label$, key1 , key2  )

	For c.KBS_Action = Each KBS_Action
		If c\label$ = label$
			c\key1 = key1
			c\key2 = key2
					
		EndIf
	Next
End Function

Function KBS_ChangeActionKey1( label$ , key1 )

	For c.KBS_Action = Each KBS_Action
		If c\label$ = label$
			c\key1=key1
		EndIf 
	Next
	
End Function

Function KBS_ChangeActionKey2( label$ , key2 )

	For c.KBS_Action = Each KBS_Action
		If c\label$ = label$
			c\key2=key2
		EndIf 
	Next
	
End Function


;==================================================================
; The main update fonction, put it in the main loop
;==================================================================
Function KBS_Update( BCF=False )

	; Get the mouse informations
	If BCF Then
		KBS_MWheel# = BCF_MouseZSpeed#
	Else
		KBS_MWheel#	= MouseZSpeed()
	EndIf
	
	KBS_MX#		= MouseXSpeed()
	KBS_MY#		= MouseYSpeed()
	
	For c.KBS_Action = Each KBS_Action

		c\value# = KBS_CheckKeyboard#( c\key1)
		state = 1
		If c\value#=0 Then
			
			c\value# =  KBS_CheckKeyboard#( c\key2)
			state = 2
		EndIf

		If c\Value# = 0 Then
			c\state=False
		Else
			c\state=state
		EndIf
		
	Next
	
End Function

Function KBS_CheckKeyboard#( key )

	
	If key<1000 Then
		If KeyDown(key) Then 
			Return 1
		Else
			Return 0
		EndIf
	Else
		If KBS_CheckJoyMous(key) Then
			Return 1
		Else
			Return KBS_CheckJoyMousAxis(key)

		EndIf
	EndIf

	
End Function

Function KBS_CheckJoyMous(key)

	Select key
	
		; Souris
		Case 1001
			If MouseDown(1) Then
				
				Return True
			Else
				Return False
			EndIf
		Case 1002
			If MouseDown(2) Then
				
				Return True
			Else
				Return False
			EndIf
		Case 1003
			If MouseDown(3) Then
				Return True
			Else
				Return False
			EndIf
		Case 1004
			If KBS_MWheel#>=1 Then
				Return True
			Else
				Return False
			EndIf
		Case 1005
			If KBS_MWheel#<=-1 Then
				Return True
			Else
				Return False
			EndIf

		; Joystick
		Case 2001
			If JoyDown(1) Then
				Return True
			Else
				Return False
			EndIf
		Case 2002
			If JoyDown(2) Then
				Return True
			Else
				Return False
			EndIf
		Case 2003
			If JoyDown(3) Then
				Return True
			Else
				Return False
			EndIf
		Case 2004
			If JoyDown(4) Then
				Return True
			Else
				Return False
			EndIf
		Case 2005
			If JoyDown(5) Then
				Return True
			Else
				Return False
			EndIf
		Case 2006
			If JoyDown(6) Then
				Return True
			Else
				Return False
			EndIf
		Case 2007
			If JoyDown(7) Then
				Return True
			Else
				Return False
			EndIf
		Case 2008
			If JoyDown(8) Then
				Return True
			Else
				Return False
			EndIf
		Case 2009
			If JoyDown(9) Then
				Return True
			Else
				Return False
			EndIf

	End Select

End Function

Function KBS_CheckJoyMousAxis#(key)

	Select Key
		Case KBS_Joy_X
		
			KBS_JX# = KBS_GetJoyX()

			Return KBS_JX#

			
		Case KBS_Joy_Y
		
			KBS_JY# = KBS_GetJoyY()

			Return KBS_JY#


		Case KBS_Joy_Z
		
			KBS_JZ# = KBS_GetJoyZ()

			Return KBS_JZ#

			
		Case KBS_Joy_U

			KBS_JU# = KBS_GetJoyU()

			Return KBS_JU#
			
		Case KBS_Joy_V
		
			KBS_JV# = KBS_GetJoyV()
			
			Return KBS_JV#
			
		Case KBS_Joy_Roll
		
			KBS_JRoll# = KBS_GetJoyRoll()
			
			Return KBS_JRoll#

		Case KBS_Joy_Pitch
		
			KBS_Jpitch# = KBS_GetJoyPitch()

			Return KBS_Jpitch#

		Case KBS_Joy_Yaw
		
			KBS_JYaw# = KBS_GetJoyYaw()

			Return KBS_JYaw#

		Case KBS_Mous_Mou_X

			Return KBS_MX#	
			
		Case KBS_Mous_Mou_Y

			Return KBS_MY#	
	End Select

End Function


;==================================================================
; Function to create a Control Binding
;==================================================================
Function KBS_GetLabelCode$( KeyCode )

	Select KeyCode

		; -------------
		; La souris
		; -------------
		Case 1001
			Return "Right Click"
		Case 1002
			Return "Left Click"
		Case 1003
			Return "Wheel Click "
		Case 1004
			Return "Wheel Up"
		Case 1005
			Return "Wheel Down"
		Case 1100
			Return "Mouse X Axis"
		Case 1101
			Return "Mouse Y Axis"
			
		; -------------
		; Le joystick
		; -------------
		Case 2001
			Return "Button 1"
		Case 2002
			Return "Button 2"
		Case 2003
			Return "Button 3"
		Case 2004
			Return "Button 4"
		Case 2005
			Return "Button 5"
		Case 2006
			Return "Button 6"
		Case 2007
			Return "Button 7"
		Case 2008
			Return "Button 8"
		Case 2009
			Return "Button 9"

		Case 2101
			Return "Joy X axis"
		Case 2102
			Return "Joy Y axis"
		Case 2103
			Return "Joy Z axis"
		Case 2104
			Return "Joy U axis"
		Case 2105
			Return "Joy V axis"
		Case 2106
			Return "Joy Yaw axis"
		Case 2107
			Return "Joy Pitch axis"
		Case 2108
			Return "Joy Roll axis"


		; -----------------------------
		; Touches de direction
		; -----------------------------
		Case 203
			Return "Left"
		Case 205
			Return "Right"
		Case 200
			Return "Up"
		Case 208
			Return "Down"

		; -----------------------------------------------------------------------
		; Pave de touche au dessus des touches de direction
		; -----------------------------------------------------------------------
		Case 210
			Return "Insert"
		Case 211
			Return "Delete"
		Case 201
			Return "Page Up"
		Case 209
			Return "Page Down"
		Case 207
			Return "End"
		Case 199
			Return "Home"

		; ----------------------------
		; Touches de fonction
		; ----------------------------
		Case 59
			Return "F1"
		Case 60
			Return "F2"
		Case 61
			Return "F3"
		Case 62
			Return "F4"
		Case 63
			Return "F5"
		Case 64
			Return "F6"
		Case 65
			Return "F7"
		Case 66
			Return "F8"
		Case 67
			Return "F9"
		Case 68
			Return "F10"
		Case 87
			Return "F11"
		Case 88
			Return "F12"

		; ------------------------
		; Touches de 0 a 9
		; ------------------------
		Case 2
			Return "1"
		Case 3
			Return "2"
		Case 4
			Return "3"
		Case 5
			Return "4"
		Case 6
			Return "5"
		Case 7
			Return "6"
		Case 8
			Return "7"
		Case 9
			Return "8"
		Case 10
			Return "9"
		Case 11
			Return "0"
		Case 12
			Return "-"
		Case 13
			Return "="
		Case 14
			Return "Backspace"

		; ------------------------------------------------------------------------------------
		; Touches de controle ENTER + RETURN + ESCAPE + SPACE
		; ------------------------------------------------------------------------------------
		Case 1
			Return "ESC"
		Case 57
			Return "Space"
		Case 28
			Return "Enter"
		Case 156
			Return "Return"
		Case 15
			Return "Tab"

		; -----------------------------------------------
		; Touches Ctrl + Alt + shift + window
		; -----------------------------------------------
		Case 29
			Return "Left Control"
		Case 157
			Return "Right Control"
		Case 56
			Return "Left Alt"
		Case 184
			Return "Right Alt"
		Case 42
			Return "Left Shift"
		Case 54
			Return "Right Shift"
		Case 219
			Return "Left Windows"
		Case 220
			Return "Right Windows"

		; -------------------------------
		; Les lettres + symboles
		; -------------------------------
		Case 16
			Return "Q"
		Case 17
			Return "W"
		Case 18
			Return "E"
		Case 19
			Return "R"
		Case 20
			Return "T"
		Case 21
			Return "Y"
		Case 22
			Return "U"
		Case 23
			Return "I"
		Case 24
			Return "O"
		Case 25
			Return "P"
		Case 26
			Return "["
		Case 27
			Return "]"
		Case 30
			Return "A"
		Case 31
			Return "S"
		Case 32
			Return "D"
		Case 33
			Return "F"
		Case 34
			Return "G"
		Case 35
			Return "H"
		Case 36
			Return "J"
		Case 37
			Return "K"
		Case 38
			Return "L"
		Case 39
			Return ";"
		Case 40
			Return "'"
		Case 41
			Return "Grave"
		Case 44
			Return "Z"
		Case 45
			Return "X"
		Case 46
			Return "C"
		Case 47
			Return "V"
		Case 48
			Return "B"
		Case 49
			Return "N"
		Case 50
			Return "M"
		Case 51
			Return ","
		Case 52
			Return "."
		Case 53
			Return "/"

		; -----------------------
		; Pave numerique
		; -----------------------
		Case 82
			Return "Numpad 0"
		Case 79
			Return "Numpad 1"
		Case 80
			Return "Numpad 2"
		Case 81
			Return "Numpad 3"
		Case 75
			Return "Numpad 4"
		Case 76
			Return "Numpad 5"
		Case 77
			Return "Numpad 6"
		Case 71
			Return "Numpad 7"
		Case 72
			Return "Numpad 8"
		Case 73
			Return "Numpad 9"
		Case 83
			Return "."
		Case 74
			Return "-"
		Case 78
			Return "+"
		Case 69
			Return "Lock"
		Case 181
			Return "/"
		Case 55
			Return "*"
	End Select
	
End Function

Function KBS_GetActionCode(keyclick=True,mouse=True,joy=True)


	FlushKeys()
	FlushMouse()
	FlushJoy()

	
 	x# = KBS_JDeadZone_X# 	
 	y# = KBS_JDeadZone_Y# 	
 	z# = KBS_JDeadZone_Z# 	
 	u# = KBS_JDeadZone_U# 	
 	v# = KBS_JDeadZone_V# 	
 	p# = KBS_JDeadZone_Pitch#
 	yy# = KBS_JDeadZone_Yaw# 	
 	r# = KBS_JDeadZone_Roll# 
	
 	KBS_JDeadZone_X# 	= 0.7
 	KBS_JDeadZone_Y# 	= 0.7
 	KBS_JDeadZone_Z# 	= 0.7
 	KBS_JDeadZone_U# 	= 0.7
 	KBS_JDeadZone_V# 	= 0.7
 	KBS_JDeadZone_Pitch#= 0.7
 	KBS_JDeadZone_Yaw# 	= 0.7
 	KBS_JDeadZone_Roll# = 0.7

	ju# = KBS_GetJoyU#()
	jv# = KBS_GetJoyV#()
	jp# = KBS_GetJoyPitch#()
	jyw# = KBS_GetJoyYaw#()
	jr# = KBS_GetJoyRoll#()

	ju# = KBS_GetJoyU#()
	jv# = KBS_GetJoyV#()
	jp# = KBS_GetJoyPitch#()
	jyw# = KBS_GetJoyYaw#()
	jr# = KBS_GetJoyRoll#()
	
	scancode=-1
	
	While scancode=-1


	
		For i=1 To 237

			If keyclick Then
				; -------------
				; Clavier
				; -------------
				If KeyDown(i) Then
					scancode=i
					Exit
				EndIf

				; -------------
				; Souris
				; -------------
				If MouseDown(2) Then
					scancode=KBS_Mous_RightClick
					Exit
				EndIf
			
				If MouseDown(1) Then
					scancode=KBS_Mous_LeftClick
					Exit
				EndIf
			
				If MouseDown(3) Then
					scancode=KBS_Mous_WheelClick
					Exit
				EndIf
				
				w=MouseZSpeed()
				
				If w>=1 Then
					scancode=KBS_Mous_WheelUp
					Exit
				EndIf
				
				If w<=-1 Then
					scancode=KBS_Mous_WheelDown
					Exit
				EndIf

				; -------------
				; Joystick
				; -------------
				If JoyDown(1) Then
					scancode=KBS_Joy_button1
					Exit
				EndIf
	
				If JoyDown(2) Then
					scancode=KBS_Joy_button2
					Exit
				EndIf
	
				If JoyDown(3) Then
					scancode=KBS_Joy_button3
					Exit
				EndIf
	
				If JoyDown(4) Then
					scancode=KBS_Joy_button4
					Exit
				EndIf
	
				If JoyDown(5) Then
					scancode=KBS_Joy_button5
					Exit
				EndIf
	
				If JoyDown(6) Then
					scancode=KBS_Joy_button6
					Exit
				EndIf
	
				If JoyDown(7) Then
					scancode=KBS_Joy_button7
					Exit
				EndIf
	
				If JoyDown(8) Then
					scancode=KBS_Joy_button8
					Exit
				EndIf
	
				If JoyDown(9) Then
					scancode=KBS_Joy_button9
					Exit
				EndIf

			EndIf

			If mouse Then
				If MouseXSpeed()<>0 Then
					scancode=KBS_Mous_Mou_X
					Exit
				EndIf

				If MouseYSpeed()<>0 Then
					scancode=KBS_Mous_Mou_Y
					Exit
				EndIf
			EndIf


			If joy Then
			
				If KBS_GetJoyX#()<>0 Then
					scancode= KBS_Joy_X
					Exit
				EndIf

				If KBS_GetJoyY#()<>0 Then
					scancode= KBS_Joy_Y
					Exit
				End If

				If KBS_GetJoyZ#()<>0 Then
					scancode= KBS_Joy_Z
					Exit
				End If

				tmp# = KBS_GetJoyU#()
				If tmp#<>ju# Then
					scancode= KBS_Joy_U
					Exit
				End If

				tmp# = KBS_GetJoyV#()
				If tmp#<>jv# Then
					scancode= KBS_Joy_V
					Exit
				End If

				tmp# = KBS_GetJoyYaw#()
				If tmp#<>jyw# Then
					scancode= KBS_Joy_Yaw
					Exit
				End If

				tmp# = KBS_GetJoyPitch#()
				If tmp#<>jp# Then
					scancode= KBS_Joy_Pitch
					Exit
				End If

				tmp# = KBS_GetJoyRoll#()
				If tmp#<>jr# Then
					scancode= KBS_Joy_Roll
					Exit
				End If

			End If
			
		Next
		
	Wend

 	x# = KBS_JDeadZone_X# 	
 	y# = KBS_JDeadZone_Y# 	
 	z# = KBS_JDeadZone_Z# 	
 	u# = KBS_JDeadZone_U# 	
 	v# = KBS_JDeadZone_V# 	
 	p# = KBS_JDeadZone_Pitch#
 	yy# = KBS_JDeadZone_Yaw# 	
 	r# = KBS_JDeadZone_Roll#
	
	Return scancode
	
End Function

Function KBS_WaitOldKey()

	bOk=False
	While bOk=False
		bOk=True
		For i=1 To 237
			z = MouseZSpeed()
			If KeyDown(i) Or MouseDown(1) Or MouseDown(2) Or MouseDown(3) Or z<>0 Then
				bOk=False
			EndIf
			If JoyDown(1) Or JoyDown(2) Or JoyDown(3) Or JoyDown(4) Or JoyDown(5) Or JoyDown(6) Or JoyDown(7) Or JoyDown(8) Or JoyDown(9) Then
				bOk=False
			EndIf
		Next
		
	Wend
	
End Function



;==================================================================
; Internal Joystick fonction
;==================================================================
Function KBS_GetJoyX#()

	joy# = KBS_FilterJoyX#()

	If Abs(joy#)<KBS_JDeadZone_X# Then joy#=0

	Return joy# 
	
End Function

Function KBS_GetJoyY#()

	joy# = KBS_FilterJoyY#()

	If Abs(joy#)<KBS_JDeadZone_Y# Then joy#=0

	Return joy#
	
End Function

Function KBS_GetJoyZ#()

	joy# = KBS_FilterJoyZ#()

	If Abs(joy#)<KBS_JDeadZone_Z# Then joy#=0

	Return joy#
	
End Function

Function KBS_GetJoyU#()

	
	joy# = KBS_FilterJoyU#()

	joy# = (joy#-1)*-0.5
	
	If Abs(joy#)<KBS_JDeadZone_U# Then joy#=0
	
	Return joy#  

End Function

Function KBS_GetJoyV#()

	
	joy# = KBS_FilterJoyV#()

	joy# = (joy#-1)*-0.5
	
	If Abs(joy#)<KBS_JDeadZone_V# Then joy#=0
	
	
	Return joy#  

End Function

Function KBS_GetJoyPitch#()

	joy# = KBS_FilterJoyPitch#()

	joy# = joy#/180
	
	If Abs(joy#)<KBS_JDeadZone_Pitch# Then joy#=0
	
	Return joy#
	
End Function

Function KBS_GetJoyYaw#()

	joy# = KBS_FilterJoyYaw#()

	joy# = joy#/180
	
	If Abs(joy#)<KBS_JDeadZone_Yaw# Then joy#=0
	
	Return joy#
	
End Function

Function KBS_GetJoyRoll#()

	joy# = KBS_FilterJoyRoll#()

	joy# = joy#/180
	
	If Abs(joy#)<KBS_JDeadZone_Roll# Then joy#=0
	
	Return joy#
	
End Function




Function KBS_FilterJoyX#()

 jl#=JoyX()
 
 jst$=Str$(jl#)
 jle=Len(jst$)
 
 If jle>10 Then
  	If Mid$(jst$,jle-4,1)="e"
   		jl=0.0
  	EndIf
 EndIf
 
 KBS_joyvx3 = KBS_joyvx2
 KBS_joyvx2 = KBS_joyvx1
 KBS_joyvx1 = jl
 
 If (Abs(KBS_joyvx3-KBS_joyvx2)>0.1) And (Abs(KBS_joyvx2-KBS_joyvx1)>0.1)
  	KBS_joyvx2=(KBS_joyvx3+KBS_joyvx1)/2
 EndIf
 
 Return KBS_joyvx2
 
End Function

Function KBS_FilterJoyY#()

 jl#=JoyY()
 
 jst$=Str$(jl#)
 jle=Len(jst$)
 
 If jle>10 Then
  	If Mid$(jst$,jle-4,1)="e"
   		jl=0.0
  	EndIf
 EndIf
 
 KBS_joyvy3 = KBS_joyvy2
 KBS_joyvy2 = KBS_joyvy1
 KBS_joyvy1 = jl
 
 If (Abs(KBS_joyvy3-KBS_joyvy2)>0.1) And (Abs(KBS_joyvy2-KBS_joyvy1)>0.1)
  	KBS_joyvy2=(KBS_joyvy3+KBS_joyvy1)/2
 EndIf
 
 Return KBS_joyvy2
 
End Function

Function KBS_FilterJoyZ#()

 jl#=JoyY()
 
 jst$=Str$(jl#)
 jle=Len(jst$)
 
 If jle>10 Then
  	If Mid$(jst$,jle-4,1)="e"
   		jl=0.0
  	EndIf
 EndIf
 
 KBS_joyvz3 = KBS_joyvz2
 KBS_joyvz2 = KBS_joyvz1
 KBS_joyvz1 = jl
 
 If (Abs(KBS_joyvz3-KBS_joyvz2)>0.1) And (Abs(KBS_joyvz2-KBS_joyvz1)>0.1)
  	KBS_joyvz2=(KBS_joyvz3+KBS_joyvz1)/2
 EndIf
 
 Return KBS_joyvz2
 
End Function

Function KBS_FilterJoyU#()

 jl#=JoyU#()
 
 jst$=Str$(jl#)
 jle=Len(jst$)
 
 If jle>10 Then
  	If Mid$(jst$,jle-4,1)="e"
   		jl=1.0
  	EndIf
 EndIf
 
 KBS_joyvu3 = KBS_joyvu2
 KBS_joyvu2 = KBS_joyvu1
 KBS_joyvu1 = jl
 
 If (Abs(KBS_joyvu3-KBS_joyvu2)>0.1) And (Abs(KBS_joyvu2-KBS_joyvu1)>0.1)
  	KBS_joyvu2=(KBS_joyvu3+KBS_joyvu1)/2
 EndIf
 
 Return KBS_joyvu2
 
End Function

Function KBS_FilterJoyV#()

 jl#=JoyV#()
 
 jst$=Str$(jl#)
 jle=Len(jst$)
 
 If jle>10 Then
  	If Mid$(jst$,jle-4,1)="e"
   		jl=1.0
  	EndIf
 EndIf
 
 KBS_joyvv3 = KBS_joyvv2
 KBS_joyvv2 = KBS_joyvv1
 KBS_joyvv1 = jl
 
 If (Abs(KBS_joyvv3-KBS_joyvv2)>0.1) And (Abs(KBS_joyvv2-KBS_joyvv1)>0.1)
  	KBS_joyvv2=(KBS_joyvv3+KBS_joyvv1)/2
 EndIf
 
 Return KBS_joyvv2
 
End Function

Function KBS_FilterJoyPitch#()

 jl#=JoyPitch()
 
 jst$=Str$(jl#)
 jle=Len(jst$)
 
 If jle>10 Then
  	If Mid$(jst$,jle-4,1)="e"
   		jl=0.0
  	EndIf
 EndIf
 
 KBS_joyvp3 = KBS_joyvp2
 KBS_joyvp2 = KBS_joyvp1
 KBS_joyvp1 = jl
 
 If (Abs(KBS_joyvp3-KBS_joyvp2)>0.1) And (Abs(KBS_joyvp2-KBS_joyvp1)>0.1)
  	KBS_joyvr2=(KBS_joyvp3+KBS_joyvp1)/2
 EndIf
 
 Return KBS_joyvp2
 
End Function

Function KBS_FilterJoyYaw#()

 jl#=JoyYaw()
 
 jst$=Str$(jl#)
 jle=Len(jst$)
 
 If jle>10 Then
  	If Mid$(jst$,jle-4,1)="e"
   		jl=0.0
  	EndIf
 EndIf
 
 KBS_joyvyy3 = KBS_joyvyy2
 KBS_joyvyy2 = KBS_joyvyy1
 KBS_joyvyy1 = jl
 
 If (Abs(KBS_joyvyy3-KBS_joyvyy2)>0.1) And (Abs(KBS_joyvyy2-KBS_joyvyy1)>0.1)
  	KBS_joyvyy2=(KBS_joyvyy3+KBS_joyvyy1)/2
 EndIf
 
 Return KBS_joyvyy2
 
End Function

Function KBS_FilterJoyRoll#()

 jl#=JoyRoll#()
 
 jst$=Str$(jl#)
 jle=Len(jst$)
 
 If jle>10 Then
  	If Mid$(jst$,jle-4,1)="e"
   		jl=0.0
  	EndIf
 EndIf
 
 KBS_joyvr3 = KBS_joyvr2
 KBS_joyvr2 = KBS_joyvr1
 KBS_joyvr1 = jl
 
 If (Abs(KBS_joyvr3-KBS_joyvr2)>0.1) And (Abs(KBS_joyvr2-KBS_joyvr1)>0.1)
  	KBS_joyvr2=(KBS_joyvr3+KBS_joyvr1)/2
 EndIf
 
 Return KBS_joyvr2
 
End Function




;==================================================================
; 						CONSTANTE DECLARATION
;==================================================================

; the mouse
Const KBS_Mous_RightClick	= 1001
Const KBS_Mous_LeftClick 	= 1002
Const KBS_Mous_WheelClick 	= 1003
Const KBS_Mous_WheelUp	 	= 1004
Const KBS_Mous_WheelDown 	= 1005
Const KBS_Mous_Mou_X		= 1100
Const KBS_Mous_Mou_Y		= 1101

; Joystock
Const KBS_Joy_button1		= 2001
Const KBS_Joy_button2		= 2002
Const KBS_Joy_button3		= 2003
Const KBS_Joy_button4		= 2004
Const KBS_Joy_button5		= 2005
Const KBS_Joy_button6		= 2006
Const KBS_Joy_button7		= 2007
Const KBS_Joy_button8		= 2008
Const KBS_Joy_button9		= 2009
Const KBS_Joy_X				= 2101
Const KBS_Joy_Y				= 2102
Const KBS_Joy_z				= 2103
Const KBS_Joy_u				= 2104
Const KBS_Joy_v				= 2105
Const KBS_Joy_Yaw			= 2106
Const KBS_Joy_Pitch			= 2107
Const KBS_Joy_Roll			= 2108


; Directional KBS_Key
Const KBS_Key_Left			= 203
Const KBS_Key_Right 		= 205
Const KBS_Key_Up 			= 200
Const KBS_Key_Down 			= 208

; INSERT + DELETE + PAGE UP / DOWN + END + HOME
Const KBS_Key_Insert 		= 210
Const KBS_Key_Delete 		= 211
Const KBS_Key_PageUp 		= 201
Const KBS_Key_PageDown 		= 209
Const KBS_Key_End 			= 207
Const KBS_Key_Home			= 199

; F1 to F12
Const KBS_Key_F1 			= 59
Const KBS_Key_F2 			= 60
Const KBS_Key_F3 			= 61
Const KBS_Key_F4 			= 62
Const KBS_Key_F5 			= 63
Const KBS_Key_F6 			= 64
Const KBS_Key_F7 			= 65
Const KBS_Key_F8 			= 66
Const KBS_Key_F9 			= 67
Const KBS_Key_F10 			= 68
Const KBS_Key_F11 			= 87
Const KBS_Key_F12 			= 88

;  0 to 9
Const KBS_Key_1 			= 2
Const KBS_Key_2 			= 3
Const KBS_Key_3 			= 4
Const KBS_Key_4 			= 5
Const KBS_Key_5 			= 6
Const KBS_Key_6 			= 7
Const KBS_Key_7 			= 8
Const KBS_Key_8 			= 9
Const KBS_Key_9 			= 10
Const KBS_Key_0 			= 11
Const KBS_Key_Minus 		= 12
Const KBS_Key_Equal 		= 13
Const KBS_Key_Backspace 	= 14

; ENTER + RETURN + ESCAPE + SPACE + Tab
Const KBS_Key_ESC 			= 1
Const KBS_Key_Space 		= 57
Const KBS_Key_Enter 		= 28
Const KBS_Key_Return 		= 156
Const KBS_Key_Tab 			= 15

; Ctrl + Alt + shift + window
Const KBS_Key_LeftControl 	= 29
Const KBS_Key_RightControl 	= 157
Const KBS_Key_LeftAlt 		= 56
Const KBS_Key_RightAlt 		= 184
Const KBS_Key_LeftShift 	= 42
Const KBS_Key_RightShift 	= 54
Const KBS_Key_LeftWindows 	= 219
Const KBS_Key_RightWindows 	= 220

; A to Z etc ...
Const KBS_Key_Q 			= 16
Const KBS_Key_W 			= 17
Const KBS_Key_E 			= 18
Const KBS_Key_R 			= 19
Const KBS_Key_T 			= 20
Const KBS_Key_Y 			= 21
Const KBS_Key_U 			= 22
Const KBS_Key_I 			= 23
Const KBS_Key_O 			= 24
Const KBS_Key_P 			= 25
Const KBS_Key_Left_Bracket 	= 26
Const KBS_Key_Right_Bracket = 27

Const KBS_Key_A 			= 30
Const KBS_Key_S 			= 31
Const KBS_Key_D 			= 32
Const KBS_Key_F 			= 33
Const KBS_Key_G 			= 34
Const KBS_Key_H 			= 35
Const KBS_Key_J 			= 36
Const KBS_Key_K 			= 37
Const KBS_Key_L 			= 38
Const KBS_Key_SemiColon		= 39
Const KBS_Key_Apostrophe 	= 40
Const KBS_Key_Grave 		= 41

Const KBS_Key_Z 			= 44
Const KBS_Key_X 			= 45
Const KBS_Key_C 			= 46
Const KBS_Key_V 			= 47
Const KBS_Key_B 			= 48
Const KBS_Key_N 			= 49
Const KBS_Key_M 			= 50
Const KBS_Key_Comma 		= 51
Const KBS_Key_Period 		= 52
Const KBS_Key_Slash 		= 53

; Number
Const KBS_Key_Numpad0 		= 82
Const KBS_Key_Numpad1 		= 79
Const KBS_Key_Numpad2 		= 80
Const KBS_Key_Numpad3 		= 81
Const KBS_Key_Numpad4 		= 75
Const KBS_Key_Numpad5 		= 76
Const KBS_Key_Numpad6 		= 77
Const KBS_Key_Numpad7 		= 71
Const KBS_Key_Numpad8 		= 72
Const KBS_Key_Numpad9 		= 73

Const KBS_Key_Decimal		= 83
Const KBS_Key_Substract		= 74
Const KBS_Key_Add 			= 78
Const KBS_Key_Lock 			= 69
Const KBS_Key_Div 			= 181
Const KBS_Key_Mult			 = 55
