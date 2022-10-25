; ID: 2059
; Author: TomToad
; Date: 2007-07-09 09:22:04
; Title: High Resolution Timer Module
; Description: Module using high resolution timers

Module toad.counter
Extern "Win32"

Function QueryPerformanceCounter(lpPerformanceCount:Long Var)
Function QueryPerformanceFrequency(lpFrequency:Long Var)

End Extern

Function GetTicks:Long()
	Local Ticks:Long
	
	If QueryPerformanceCounter(Ticks)
		Return Ticks
	Else
		Return 0
	End If
End Function

Function GetMillisecs:Double()
	Global Firstcall:Int = True
	Global Frequency:Long
	Local Ticks:Long
	
	If Firstcall
		QueryPerformanceFrequency(Frequency)
		Firstcall = False
	End If
	
	If QueryPerformanceCounter(Ticks)
		Return (Double(Ticks * 1000) / Frequency)
	Else
		Return 0
	End If
End Function
