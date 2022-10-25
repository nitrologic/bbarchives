; ID: 1028
; Author: eBusiness
; Date: 2004-05-12 10:54:02
; Title: User input system
; Description: Supports keyboard, mouse and joystick. Have load and save functions, and will give you the name of a key.

Graphics 640,480



;A dynamic system for getting user input from keyboard, mouse and a simple joystick
;Written by Jacob Christian Munch-Andersen

;Copy the Initialization to the beginning of your code, then decide how many inputs you want to use,
;this can always be changed later. Also copy the six function to anywhere in your code.
;In order to configure an input, first run the preidsconfigure function, then run the
;idsconfigure function, with the id number of the input that you want to configure as parameter,
;in a loop, untill it return 0, this will happen when the user make an input.
;The hit and down functions work almost like the Blitz Basic Hit and Down function families,
;A difference is that the down function will return values depending on how much the user push the
;joystick, or how far the user have moved the mouse since last check. Check the array
;keynames$(input id) to get the name of a key/input, this should work almost correctly with any
;keyboard, you can alter the keynames if you like. The save and load functions will save/load all
;input settings, you can pass a filename/path yourself, or use the default.

;This code is public domain, you can use it, alter it and pass it to others, free of charge.



;Initialization
Global key_num=4 ;How many inputs do you need?
Dim ids(1,key_num)
Dim keynames$(key_num)
Global mx_keys
Global my_keys
Global mz_keys
Global last_mouse_x_pos
Global last_mouse_y_pos
Global last_mouse_z_pos
Global last_mouse_x_neg
Global last_mouse_y_neg
Global last_mouse_z_neg
Global last_mouse_x_hitp
Global last_mouse_y_hitp
Global last_mouse_x_hitn
Global last_mouse_y_hitn
Global mouse_factor_x#=1.0
Global mouse_factor_y#=1.0
Global mouse_factor_z#=1.0
Global joystick_factor_x#=1.0
Global joystick_factor_y#=1.0
Dim joystick_minmax#(7)
joystick_minmax(0)=-.08
joystick_minmax(1)=-.8
joystick_minmax(2)=.08
joystick_minmax(3)=.8
joystick_minmax(4)=-.08
joystick_minmax(5)=-.8
joystick_minmax(6)=.08
joystick_minmax(7)=.8
Dim joy_dir_list(3)
;End of initialization



;Demo code
For a=0 To key_num
	Write "Key "+a+" = "
	b=-1
	preidsconfigure()
	While b=-1
		b=idsconfigure(a)
	Wend
	Print keynames$(a)
Next
While Not KeyDown(1)
	For a=0 To key_num
		If hit(a) Print a+" "+down#(0)
	Next
	Delay 30
Wend
End



Function save_key_settings(file$="keysettings.dat")
	filehandle=WriteFile(file$)
	If filehandle=0 Then Return 0
	WriteFloat filehandle,mouse_factor_x#
	WriteFloat filehandle,mouse_factor_y#
	WriteFloat filehandle,mouse_factor_z#
	WriteFloat filehandle,joystick_factor_x#
	WriteFloat filehandle,joystick_factor_y#
	For a_keys=0 To 7
		WriteFloat filehandle,joystick_minmax(a_keys)
	Next
	For a_keys=0 To key_num
		WriteInt filehandle,ids(0,a_keys)
		WriteInt filehandle,ids(1,a_keys)
		WriteString filehandle,keynames(a_keys)
	Next
	CloseFile filehandle
	Return 1
End Function
Function load_key_settings(file$="keysettings.dat")
	filehandle=ReadFile(file$)
	If filehandle=0 Then Return 0
	mouse_factor_x#=ReadFloat(filehandle)
	mouse_factor_y#=ReadFloat(filehandle)
	mouse_factor_z#=ReadFloat(filehandle)
	joystick_factor_x#=ReadFloat(filehandle)
	joystick_factor_y#=ReadFloat(filehandle)
	For a_keys=0 To 7
		joystick_minmax(a_keys)=ReadFloat(filehandle)
	Next
	For a_keys=0 To key_num
		ids(0,a_keys)=ReadInt(filehandle)
		ids(1,a_keys)=ReadInt(filehandle)
		keynames(a_keys)=ReadString(filehandle)
	Next
	CloseFile filehandle
	Return 1
End Function
Function hit(id)
	If ids(0,id)=0 Then
		Return KeyHit(ids(1,id))
	Else If ids(0,id)=1 Then
		Return MouseHit(ids(1,id))
	Else If ids(0,id)=2 Then
		Return JoyHit(ids(1,id))
	Else If ids(0,id)=3 Then
		If ids(1,id)=0 Then
			a_keys=MouseX()-last_mouse_x_hitn
			If a_keys<-20 Then
				last_mouse_x_hitn=MouseX()
				Return 1
			Else If a_keys>20 Then
				last_mouse_x_hitn=MouseX()
				Return 0
			Else
				Return 0
			End If
		Else If ids(1,id)=1 Then
			a_keys=MouseX()-last_mouse_x_hitp
			If a_keys>20 Then
				last_mouse_x_hitp=MouseX()
				Return 1
			Else If a_keys<-20 Then
				last_mouse_x_hitp=MouseX()
				Return 0
			Else
				Return 0
			End If
		Else If ids(1,id)=2 Then
			a_keys=MouseY()-last_mouse_y_hitn
			If a_keys<-20 Then
				last_mouse_y_hitn=MouseY()
				Return 1
			Else If a_keys>20 Then
				last_mouse_y_hitn=MouseY()
				Return 0
			Else
				Return 0
			End If
		Else If ids(1,id)=3 Then
			a_keys=MouseY()-last_mouse_y_hitp
			If a_keys>20 Then
				last_mouse_y_hitp=MouseY()
				Return 1
			Else If a_keys<-20 Then
				last_mouse_y_hitp=MouseY()
				Return 0
			Else
				Return 0
			End If
		Else If ids(1,id)=4 Then
			a_keys=MouseZ()-last_mouse_z_neg
			last_mouse_z_neg=MouseZ()
			If a_keys<0 Then
				Return 1
			Else
				Return 0
			End If
		Else If ids(1,id)=5 Then
			a_keys=MouseZ()-last_mouse_z_pos
			last_mouse_z_pos=MouseZ()
			If a_keys>0 Then
				Return 1
			Else
				Return 0
			End If
		Else If ids(1,id)=6
			If JoyXDir()=-1 Then
				If joy_dir_list(0)=1 Then
					Return 0
				Else
					joy_dir_list(0)=1
					Return 1
				End If
			Else
				joy_dir_list(0)=0
				Return 0
			End If
		Else If ids(1,id)=7
			If JoyXDir()=1 Then
				If joy_dir_list(1)=1 Then
					Return 0
				Else
					joy_dir_list(1)=1
					Return 1
				End If
			Else
				joy_dir_list(1)=0
				Return 0
			End If
		Else If ids(1,id)=8
			If JoyYDir()=-1 Then
				If joy_dir_list(2)=1 Then
					Return 0
				Else
					joy_dir_list(2)=1
					Return 1
				End If
			Else
				joy_dir_list(2)=0
				Return 0
			End If
		Else If ids(1,id)=9
			If JoyYDir()=1 Then
				If joy_dir_list(3)=1 Then
					Return 0
				Else
					joy_dir_list(3)=1
					Return 1
				End If
			Else
				joy_dir_list(3)=0
				Return 0
			End If
		End If
	End If
End Function
Function down#(id)
	If ids(0,id)=0 Then
		Return KeyDown(ids(1,id))
	Else If ids(0,id)=1 Then
		Return MouseDown(ids(1,id))
	Else If ids(0,id)=2 Then
		Return JoyDown(ids(1,id))
	Else If ids(0,id)=3 Then
		If ids(1,id)=0 Then
			a_keys=MouseX()-last_mouse_x_neg
			last_mouse_x_neg=MouseX()
			If a_keys<0 Then
				Return -a_keys*mouse_factor_x#
			Else
				Return 0
			End If
		Else If ids(1,id)=1 Then
			a_keys=MouseX()-last_mouse_x_pos
			last_mouse_x_pos=MouseX()
			If a_keys>0 Then
				Return a_keys*mouse_factor_x#
			Else
				Return 0
			End If
		Else If ids(1,id)=2 Then
			a_keys=MouseY()-last_mouse_y_neg
			last_mouse_y_neg=MouseY()
			If a_keys<0 Then
				Return -a_keys*mouse_factor_y#
			Else
				Return 0
			End If
		Else If ids(1,id)=3 Then
			a_keys=MouseY()-last_mouse_y_pos
			last_mouse_y_pos=MouseY()
			If a_keys>0 Then
				Return a_keys*mouse_factor_y#
			Else
				Return 0
			End If
		Else If ids(1,id)=4 Then
			a_keys=MouseZ()-last_mouse_z_neg
			last_mouse_z_neg=MouseZ()
			If a_keys<0 Then
				Return -a_keys*mouse_factor_z#
			Else
				Return 0
			End If
		Else If ids(1,id)=5 Then
			a_keys=MouseZ()-last_mouse_z_pos
			last_mouse_z_pos=MouseZ()
			If a_keys>0 Then
				Return a_keys*mouse_factor_z#
			Else
				Return 0
			End If
		Else If ids(1,id)=6 Then
			a_keyss#=JoyX()
			If a_keyss>joystick_minmax(0) Then
				Return 0
			Else If a_keyss>joystick_minmax(1) Then
				Return joystick_factor_x*(joystick_minmax(0)-a_keyss)/(joystick_minmax(0)-joystick_minmax(1))
			Else
				Return 1
			End If
		Else If ids(1,id)=7 Then
			a_keyss#=JoyX()
			If a_keyss<joystick_minmax(2) Then
				Return 0
			Else If a_keyss<joystick_minmax(3) Then
				Return joystick_factor_x*(joystick_minmax(2)-a_keyss)/(joystick_minmax(2)-joystick_minmax(3))
			Else
				Return 1
			End If
		Else If ids(1,id)=8 Then
			a_keyss#=JoyY()
			If a_keyss>joystick_minmax(4) Then
				Return 0
			Else If a_keyss>joystick_minmax(5) Then
				Return joystick_factor_y*(joystick_minmax(4)-a_keyss)/(joystick_minmax(4)-joystick_minmax(5))
			Else
				Return 1
			End If
		Else If ids(1,id)=9 Then
			a_keyss#=JoyY()
			If a_keyss<joystick_minmax(6) Then
				Return 0
			Else If a_keyss<joystick_minmax(7) Then
				Return joystick_factor_y*(joystick_minmax(6)-a_keyss)/(joystick_minmax(6)-joystick_minmax(7))
			Else
				Return 1
			End If
		End If
	End If
End Function
Function preidsconfigure()
	FlushKeys
	FlushMouse
	MoveMouse(GraphicsWidth()/2,GraphicsHeight()/2) ;Optionanl
	mx_keys=MouseX()
	my_keys=MouseY()
	mz_keys=MouseZ()
	joy_dir_list(0)=(JoyXDir()=-1)
	joy_dir_list(1)=(JoyXDir()=1)
	joy_dir_list(2)=(JoyYDir()=-1)
	joy_dir_list(3)=(JoyYDir()=1)
End Function
Function idsconfigure(id)
;	Repeat
		If JoyXDir()<>-1 Then joy_dir_list(0)=0
		If JoyXDir()<>1 Then joy_dir_list(1)=0
		If JoyYDir()<>-1 Then joy_dir_list(2)=0
		If JoyYDir()<>1 Then joy_dir_list(3)=0
		If MouseX()-mx_keys<-50 Then
			ids(0,id)=3
			ids(1,id)=0
			keynames(id)="Move Mouse Left"
			Return
		Else If MouseX()-mx_keys>50 Then
			ids(0,id)=3
			ids(1,id)=1
			keynames(id)="Move Mouse Right"
			Return
		Else If MouseY()-my_keys<-50 Then
			ids(0,id)=3
			ids(1,id)=2
			keynames(id)="Move Mouse Up"
			Return
		Else If MouseY()-my_keys>50 Then
			ids(0,id)=3
			ids(1,id)=3
			keynames(id)="Move Mouse Down"
			Return
		Else If MouseZ()-mz_keys<0 Then
			ids(0,id)=3
			ids(1,id)=4
			keynames(id)="Roll Whell Backward"
			Return
		Else If MouseZ()-mz_keys>0 Then
			ids(0,id)=3
			ids(1,id)=5
			keynames(id)="Roll Whell Forward"
			Return
		Else If JoyXDir()=-1 And joy_dir_list(0)=0 Then
			ids(0,id)=3
			ids(1,id)=6
			keynames(id)="Joystick Left"
			Return
		Else If JoyXDir()=1 And joy_dir_list(1)=0 Then
			ids(0,id)=3
			ids(1,id)=7
			keynames(id)="Joystick Right"
			Return
		Else If JoyYDir()=-1 And joy_dir_list(2)=0 Then
			ids(0,id)=3
			ids(1,id)=8
			keynames(id)="Joystick Up"
			Return
		Else If JoyYDir()=1 And joy_dir_list(3)=0 Then
			ids(0,id)=3
			ids(1,id)=9
			keynames(id)="Joystick Down"
			Return
		End If
		For a=0 To 255
			If KeyHit(a) Then
				ids(0,id)=0
				ids(1,id)=a
				keys_midvar=GetKey()
;				If keys_midvar=0 Or a=57 Or a>70 And a<84 Or a=55 Or a=181 Or 1=1 Then
					If a=1 Then
						keynames(id)="Escape"
					Else If a=15 Then
						keynames(id)="Tab"
					Else If a=58 Then
						keynames(id)="Caps Lock"
					Else If a=42 Then
						keynames(id)="Left Shift"
					Else If a=29 Then
						keynames(id)="Left Control"
					Else If a=219 Then
						keynames(id)="Left Start"
					Else If a=56 Then
						keynames(id)="Left Alt"
					Else If a=57 Then
						keynames(id)="Space"
					Else If a=184 Then
						keynames(id)="Right Alt Gr"
					Else If a=220 Then
						keynames(id)="Right Start"
					Else If a=221 Then
						keynames(id)="Menu Button"
					Else If a=157 Then
						keynames(id)="Right Control"
					Else If a=54 Then
						keynames(id)="Right Shift"
					Else If a=28 Then
						keynames(id)="Enter"
					Else If a=14 Then
						keynames(id)="Backspace"
					Else If a=59 Then
						keynames(id)="F1"
					Else If a=60 Then
						keynames(id)="F2"
					Else If a=61 Then
						keynames(id)="F3"
					Else If a=62 Then
						keynames(id)="F4"
					Else If a=63 Then
						keynames(id)="F5"
					Else If a=64 Then
						keynames(id)="F6"
					Else If a=65 Then
						keynames(id)="F7"
					Else If a=66 Then
						keynames(id)="F8"
					Else If a=67 Then
						keynames(id)="F9"
					Else If a=68 Then
						keynames(id)="F10"
					Else If a=87 Then
						keynames(id)="F11"
					Else If a=88 Then
						keynames(id)="F12"
					Else If a=183 Then
						keynames(id)="Print Screen"
					Else If a=70 Then
						keynames(id)="Pause"
					Else If a=197 Then
						keynames(id)="Num Lock"
					Else If a=210 Then
						keynames(id)="Insert"
					Else If a=199 Then
						keynames(id)="Home"
					Else If a=201 Then
						keynames(id)="Page Up"
					Else If a=211 Then
						keynames(id)="Delete"
					Else If a=207 Then
						keynames(id)="End"
					Else If a=209 Then
						keynames(id)="Page Down"
					Else If a=200 Then
						keynames(id)="Arrow Up"
					Else If a=203 Then
						keynames(id)="Arrow Left"
					Else If a=208 Then
						keynames(id)="Arrow Down"
					Else If a=205 Then
						keynames(id)="Arrow Right"
					Else If a=69 Then
						keynames(id)="Num Lock"
					Else If a=181 Then
						keynames(id)="Numpad /"
					Else If a=55 Then
						keynames(id)="Numpad *"
					Else If a=71 Then
						keynames(id)="Numpad 7"
					Else If a=72 Then
						keynames(id)="Numpad 8"
					Else If a=73 Then
						keynames(id)="Numpad 9"
					Else If a=74 Then
						keynames(id)="Numpad -"
					Else If a=75 Then
						keynames(id)="Numpad 4"
					Else If a=76 Then
						keynames(id)="Numpad 5"
					Else If a=77 Then
						keynames(id)="Numpad 6"
					Else If a=78 Then
						keynames(id)="Munpad +"
					Else If a=79 Then
						keynames(id)="Numpad 1"
					Else If a=80 Then
						keynames(id)="Numpad 2"
					Else If a=81 Then
						keynames(id)="Numpad 3"
					Else If a=82 Then
						keynames(id)="Numpad 0"
					Else If a=83 Then
						If keys_midvar=0 Then
							keynames(id)="Numpad ."
						Else
							keynames(id)="Numpad "+Chr(keys_midvar)
						End If
					Else If a=156 Then
						keynames(id)="Numpad Enter"
					Else
;						keynames(id)="Unknown key "+a
						If keys_midvar>223 Then keys_midvar=keys_midvar-32
						If keys_midvar=0 Then
							keynames(id)="Unknown Key"
						Else
							keynames(id)=Upper(Chr(keys_midvar));+" Key"
						End If
					End If
;				Else
;					If keys_midvar>223 Then keys_midvar=keys_midvar-32
;					keynames(id)=Upper(Chr(keys_midvar));+" Key"
;				End If
				Return
			End If
		Next
		For a=0 To 15
			If JoyHit(a) Then
				ids(0,id)=2
				ids(1,id)=a
				keynames(id)="Joystick Button "+a
				Return
			End If
		Next
		For a=1 To 3
			If MouseHit(a) Then
				ids(0,id)=1
				ids(1,id)=a
				If a=1 Then
					keynames(id)="Left Mouse Key"
				Else If a=2 Then
					keynames(id)="Right Mouse Key"
				Else
					keynames(id)="Middle Mouse Key"
				End If
				Return
			End If
		Next
;		Delay 5
;	Forever
	Return -1
End Function
