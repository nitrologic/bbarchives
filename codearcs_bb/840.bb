; ID: 840
; Author: fredborg
; Date: 2003-11-24 12:25:07
; Title: GetJoyProperty$()
; Description: Get the Windows name of your joysticks, and much more!

; Winmm.decls
; Place in userlib folder and uncomment command
;
;.lib "winmm.dll"
;winmm_joyGetDevCaps%(id%,lpCaps*,uSize%):"joyGetDevCapsA"

;
; Joystick Property constants
; Do not change!
;
Const joy_Mid			= 0
Const joy_Pid			= 2
Const joy_Name			= 4
Const joy_Xmin			= 36
Const joy_Xmax			= 40
Const joy_Ymin			= 44
Const joy_Ymax			= 48
Const joy_Zmin			= 52
Const joy_Zmax			= 56
Const joy_NumButtons	= 60
Const joy_PeriodMin		= 64
Const joy_PeriodMax		= 68
Const joy_Rmin			= 72
Const joy_Rmax			= 76
Const joy_Umin			= 80
Const joy_Umax			= 84
Const joy_Vmin			= 88
Const joy_Vmax			= 92
Const joy_Caps			= 96
Const joy_MaxAxes		= 100
Const joy_NumAxes		= 104
Const joy_MaxButtons	= 108
Const joy_RegKey		= 112
Const joy_OEMVxD		= 144

;
; Use this function to get a joystick property
;
Function GetJoyProperty$(port,property)

	joybank = CreateBank(1024)

	ret$ = ""
	For i = 0 To 1023
		error = winmm_joyGetDevCaps(port,joybank,i)
		If error = 0 
			If (property = joy_Mid) Or (property = joy_Pid)
				ret$ = PeekShort(joybank,property)			
			ElseIf (property = joy_Name) Or (property = joy_RegKey) Or (property = joy_OEMVxD)
				For j = property To BankSize(joybank)-1
					ch = PeekByte(joybank,j)
					If ch = 0 Then Exit
					ret$ = ret$+Chr$(ch)
				Next
			Else
				ret$ = PeekInt(joybank,property)
			End If
			Exit
		End If
	Next
	
	FreeBank joybank
	
	If error <> 0
		Return "Joystick "+port+" Not Found!"
	Else
		Return ret$
	End If
	
End Function

;
; (Really useless) example
;

Graphics 400,400,0,2

joyport = 0
Print "Joystick "+joyport+" Properties:"
Print "  Mid        - "+GetJoyProperty(joyport,joy_Mid)
Print "  Pid        - "+GetJoyProperty(joyport,joy_Pid)
Print "  Name       - "+GetJoyProperty(joyport,joy_Name)
Print "  Xmin       - "+GetJoyProperty(joyport,joy_Xmin)
Print "  Xmax       - "+GetJoyProperty(joyport,joy_Xmax)
Print "  Ymin       - "+GetJoyProperty(joyport,joy_Ymin)
Print "  Ymax       - "+GetJoyProperty(joyport,joy_Ymax)
Print "  Zmin       - "+GetJoyProperty(joyport,joy_Zmin)
Print "  Zmax       - "+GetJoyProperty(joyport,joy_Zmax)
Print "  NumButtons - "+GetJoyProperty(joyport,joy_NumButtons)
Print "  PeriodMin  - "+GetJoyProperty(joyport,joy_PeriodMin)
Print "  PeriodMax  - "+GetJoyProperty(joyport,joy_PeriodMax)
Print "  Rmin       - "+GetJoyProperty(joyport,joy_Rmin)
Print "  Rmax       - "+GetJoyProperty(joyport,joy_Rmax)
Print "  Umin       - "+GetJoyProperty(joyport,joy_Umin)
Print "  Umax       - "+GetJoyProperty(joyport,joy_Umax)
Print "  Vmin       - "+GetJoyProperty(joyport,joy_Vmin)
Print "  Vmax       - "+GetJoyProperty(joyport,joy_Vmax)
Print "  Caps       - "+GetJoyProperty(joyport,joy_Caps)
Print "  MaxAxes    - "+GetJoyProperty(joyport,joy_MaxAxes)
Print "  NumAxes    - "+GetJoyProperty(joyport,joy_NumAxes)
Print "  MaxButtons - "+GetJoyProperty(joyport,joy_MaxButtons)
Print "  RegKey     - "+GetJoyProperty(joyport,joy_RegKey)
Print "  OEMVxD     - "+GetJoyProperty(joyport,joy_OEMVxD)
Print
Print "Press any key to quit!"
WaitKey()
End
