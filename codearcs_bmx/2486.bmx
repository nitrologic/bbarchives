; ID: 2486
; Author: BlitzSupport
; Date: 2009-05-23 10:27:42
; Title: Rumble effects for Xbox 360 controller
; Description: Example of rumble effect on Xbox 360 controller

' -----------------------------------------------------------------------------
' Xbox 360 controller vibration - demo at bottom of code!
' -----------------------------------------------------------------------------

' Public domain!

' -----------------------------------------------------------------------------
' Available functions...
' -----------------------------------------------------------------------------

' InitXInput ()

'	You must call this before any of the other functions! You should also
'	check the returned result. If 0, the other functions should not be called
'	or your app will die. This usually means the DLL isn't installed and you
'	should reinstall the Xbox 360 controller drivers.

' Rumble (port, rumble_left, rumble_right)

'	Pass port number (0-3) and vibration values from 0-65535. The left motor
'	handles low-frequency vibration and the right motor handles high-
'	frequency vibration.

' DisableRumble ()

'	Call this before exiting your program, to make sure all vibration is
'	disabled, or when your app is suspended.

'	On XInput version 1.0, this just stops all vibrations. On newer versions,
'	the controller outputs no vibration in response to Vibration () calls, and
'	also returns neutral values for buttons, sticks, etc.

' EnableRumble (state)

'	This can be used to re-enable controller input/output, including vibrations.
'	Call this when your program is reactivated after having been suspended.
'	Vibration is enabled by default.

'	NOTE: This is not available in the first release of XInput, so will simply be
'	ignored if called against version 1.0.

' -----------------------------------------------------------------------------
' Support functions. Most of this just checks carefully for the right DLL!
' -----------------------------------------------------------------------------

Function GetSystemFolder$ ()

?Win32 ' Windows only!

'	Function ArrayFromString:Byte [] (source$)
'		Local newarray:Byte [Len (source$)]
'		MemCopy (newarray, source.ToCString (), Len (source$))
'		Return newarray
'	End Function

	Function StringFromArray$ (source:Byte [])
		Return String.FromCString (source)
	End Function

	Local GetSystemDirectory_ (location:Byte Ptr, pathsize) "win32"
	kernel32 = LoadLibraryA ("kernel32.dll")

	Local patharray:Byte [260]

	If kernel32
		GetSystemDirectory_ = GetProcAddress (kernel32, "GetSystemDirectoryA")
		GetSystemDirectory_ (patharray, 260)
	EndIf

	Return StringFromArray (patharray)

?

End Function

' -----------------------------------------------------------------------------
' Find most recent XInput DLL...
' -----------------------------------------------------------------------------

Function GetXInputDLL$ ()

	x$ = "xinput9_1_0.dll"

	If FileType (GetSystemFolder () + "\" + x$) = 0
		x$ = "" ' Old DLL Not found...
	EndIf

	dir = ReadDir (GetSystemFolder ())

	If dir

		Repeat

			f$ = Lower (NextFile (dir))

			If f$.StartsWith ("xinput") And f$.EndsWith (".dll")
				
				' Should probably check Mid values are 0-9 here...
				
				version = Int (Mid (f$, 7, 1) + Mid (f$, 9, 1))
				
				' Result is 10 for 1.0, 11 for 1.1, 12 for 1.2, etc...
				
				If version = 91 ' Likely to be 1.0 -- see below!
					
					' Non-public versions might return 9, 8, etc for 0.9, 0.8, etc,
					' but 91 is the earliest public version! Exercise for the reader...
					
					If Mid (f$, 11, 1) = "0"
						version = 10 ' Ha! Stupid old "xinput9_1_0.dll" found -- really version 1.0
					EndIf
				EndIf
	
				If version > hiversion
					hiversion = version
					xinput$ = f$
				EndIf

			EndIf

		Until f$ = ""

		CloseDir dir

	EndIf

	Return xinput$
	
End Function

' -----------------------------------------------------------------------------
' XInput function pointers...
' -----------------------------------------------------------------------------

' For more information:

' <a href="http://msdn.microsoft.com/en-us/library/bb173048(VS.85).aspx" target="_blank">http://msdn.microsoft.com/en-us/library/bb173048(VS.85).aspx</a>

Global XInput_SetState (port, hilo:Byte Ptr) "win32"
Global XInput_Enable (enable) "win32"

' -----------------------------------------------------------------------------
' Wrapper functions...
' -----------------------------------------------------------------------------

Global XInput10 = 0

Function InitXInput ()

	x$ = GetXInputDLL ()
	xinput = LoadLibraryA (x$)
	
	If xinput

		XInput_SetState = GetProcAddress (xinput, "XInputSetState")
		XInput_Enable = GetProcAddress (xinput, "XInputEnable")

		ok = True
		
		If XInput_Enable = Null
		
			If x$ = "xinput9_1_0.dll"
			
				' Not in 1.0. Can continue but EnableController won't do anything...
				XInput10 = True ' Can check this from other parts of the program if necessary...
				
				Print ""
				Print "XInputEnable not available in XInput 1.0 -- update Xbox controller driver!~nContinuing, but EnableController will have no effect..."
				
			Else
				ok = False ' Not found at all!
			EndIf
			
		EndIf
		
		If XInput_SetState = Null
			ok = False
		EndIf
	
	Else
		ok = False
	EndIf

	If ok Return xinput
		
End Function

' Vibration continues until next Rumble call!

Function Rumble (port, rumble_left:Short, rumble_right:Short)

	' From <a href="http://msdn.microsoft.com/en-us/library/bb174835(VS.85).aspx" target="_blank">http://msdn.microsoft.com/en-us/library/bb174835(VS.85).aspx</a> :
	
	' "The left motor is the low-frequency rumble motor. The right motor is the high-frequency rumble motor."
	' "The two motors are not the same, and they create different vibration effects."

	' Combine two shorts into an integer...

	freqs = (rumble_right Shl 16) | rumble_left

	XInput_SetState (port, Varptr freqs)
	
End Function

' Call DisableRumble before exiting program, or when app is suspended, to stop all vibrations. 

' NOTE! This also affects return results for controller inputs! Makes everything return
' as 'neutral', ie. no buttons pressed, no stick movement, etc, regardless of what
' the player may be doing with the controller. (Doesn't apply to XInput 1.0.)

Function DisableRumble ()

	' The For loop below is intended to stop the vibrations for XInput 1.0, but although
	' XInput_Enable (0) stops vibration for later versions under normal usage, it appears to
	' have no effect if the controller is still vibrating after a program has terminated
	' abnormally, hence I'm calling this regardless...
	
	For port = 0 To 3
		Rumble (port, 0, 0)
	Next

	' For later versions, to disable ALL controller input/output...
	
	If Not XInput10
		XInput_Enable (0)
	EndIf
	
End Function

' Call this to re-enable vibration effects, input values, etc, if you have called
' DisableRumble while your app is suspended.

' NOTE! This also affects return results for controller inputs! Makes everything return
' as 'neutral', ie. no buttons pressed, no stick movement, etc, regardless of what
' the player may be doing with the controller. (Doesn't apply to XInput 1.0.)

' M$ recommend using this when the program is suspended (eg. Alt-Tab, etc)...

Function EnableRumble ()
	If Not XInput10 ' Only available if XInput version > 1.0!
		XInput_Enable (1)
	EndIf
End Function

' -----------------------------------------------------------------------------
' D E M O . . .
' -----------------------------------------------------------------------------

' Change port to 1, 2 or 3 if this doesn't work for you!

port = 0

' Always check result of InitXInput! Exit if 0 -- usually means DLL not found.

x = InitXInput ()

' Uncomment next line if rumble continues when program is terminated early! Always
' call StopControllers before ending program in normal usage!

'DisableRumble (); End

If x = 0
	Notify "Failed to initiate XInput!~n~nCheck XInput DLL is installed and up to date."
	End
EndIf

' Uncomment next line for Rumble to have no effect...

Print ""
Print "Xbox 360 controller rumble test..."
Print ""

Print "Low frequency motor (left)!"

Rumble (port, 25000, 0)
Delay (2000)

Print "High frequency motor (right)!"

Rumble (port, 0, 25000)
Delay (2000)

Print "Both motors!"

Rumble (port, 25000, 25000)
Delay (2000)

Print "Low frequency upwards, high frequency downwards!"

For v = 0 To 65535 Step 100
	Rumble (port, v, 65535 - v)
	Delay 1
Next

DisableRumble () ' Stop input/output, just to make sure...

End
