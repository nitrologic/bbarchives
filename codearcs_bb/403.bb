; ID: 403
; Author: BlitzSupport
; Date: 2002-08-23 01:32:40
; Title: CPU Info
; Description: A DLL plus Blitz functions for retrieving CPU information

; [This code is in the ZIP file above, which you'll need!]

; You can change this if you want to rename the DLL :)

Global dll$ = "blitzcpu.dll"

; -----------------------------------------------------------------------------
; CPUName$: Returns a string containing the hardware-encoded name of the CPU...
; -----------------------------------------------------------------------------

; NOTES: Just call it once, and store the result in a string, as it makes 2
; calls to the DLL per call (necessary, unfortunately).

; Uses a modified procedure by RINGS (from http://www.reelmediaproductions.com/pb/ ).

Function CPUName$ ()
	size = CallDLL (dll$, "_FindCPUNameLength")
	If size
		bank = CreateBank (size)
		result = CallDLL (dll$, "_CPUName", bank)
		If result
			For a = 1 To size
				cpu$ = cpu$ + Chr (PeekByte (bank, a - 1))
			Next
		EndIf
	EndIf
	FreeBank bank
	Return cpu$
End Function

; -----------------------------------------------------------------------------
; CPUSpeed: Returns the speed of the CPU, in MHz (takes 1 second to return)...
; -----------------------------------------------------------------------------

; NOTES: This takes 1 second to return, so just call it once and store the
; result in an integer variable. It works correctly on 99% of CPUs.

; Code adapted from public C/C++/C# source posted in various articles by ZEESHAN AMJAD.

Function CPUSpeed ()
	Return CallDLL (dll$, "_CPUSpeed")
End Function

; -----------------------------------------------------------------------------
; CPUName$: Returns the current CPU usage, as percentage of CPU time used...
; -----------------------------------------------------------------------------

; NOTES: Returns current CPU usage % and apparently works properly on both
; 9x and 2000 (I can confirm it gets the correct amount -/+ a couple of percent
; in 2000). Note that in Blitz, you'll generally be running at 100% all the time,
; but this will allow you to judge appropriate delays in your loop so that
; windowed games can play nicely with other desktop programs... probably best
; called on a timer (eg. once every second).

; Uses a PB library created by DANILO (from http://www.reelmediaproductions.com/pb/ ).

Function CPUUsage ()
	Return CallDLL (dll$, "_CPUPercent")
End Function

; D E M O . . .

AppTitle "CPU Information"

Graphics 640, 480, 0, 2
SetBuffer BackBuffer ()

cpu$ = CPUName () ; Get hard-coded CPU name...
mhz = CPUSpeed () ; CPUSpeed () takes 1 second to return information (so don't call in a loop ;)

Repeat

	Cls
	
	Color 0, 255, 0
	Locate 20, 20: Write "CPU: "
	Color 255, 255, 255
	Write cpu$
	Color 0, 255, 0
	Write " running at "
	Color 255, 255, 255
	Write mhz
	Color 0, 255, 0
	Write " MHz"
	
	Locate 20, 60: Write "Current CPU usage: "
	Color 255, 255, 255
	Write CPUUsage ()
	Color 0, 255, 0
	Write" %"
	
	Delay 1000 ; Allows our program to avoid running at 100%
	Flip
	
Until (KeyHit (1)) Or (MouseHit (1))

End
