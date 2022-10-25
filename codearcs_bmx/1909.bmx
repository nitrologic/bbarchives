; ID: 1909
; Author: Cygnus
; Date: 2007-02-01 03:32:45
; Title: High Resolution Timers
; Description: A short class for using high resolution timers in BlitzMax (WIN32 only)

SuperStrict


'Demo:

Global Counter:TPerfCounter=TperfCounter.Create(1000000) 'Create a Counter.
'My PC can handle 1000,000,000 BUT I have been warned it's prob not accurate :)

counter.Start()	'Start the counter.
Global MS:Int=MilliSecs()
Repeat
	Print Counter.Stop()+"  "+(MilliSecs()-ms)/1000	'test counter by checking it next to millisecs.
Until (MilliSecs()-ms)>10000 'do it for 10 secs.

End	'  XD I wonder what this does...







'--------------------------------------- the real timer lib:

Extern "win32"
	Function QueryPerformanceFrequency(LARGE_INTEGER:Long Var)
	Function QueryPerformanceCounter(LARGE_INTEGER:Long Var)
EndExtern


Type TPerfCounter
	Field Frequency:Double
	Field basefrequency:Long
	Field Started:Long
	Field LastValue:Long
	Global perfTime:Long	'Global to the type so that a new Local variable doesnt get created
				'in every function/method. Slight speed increase.
							
							
	Function GetCounter:Long()				'Calls QueryPerformanceCounter directly.
		QueryPerformanceCounter(PerfTime)
		Return PerfTime
	End Function

	
	Function Create:TPerfCounter(Freq:Long)			'Creates a Performance Counter using the Frequency
		Local obj:TPerfCounter=New TPerfCounter
		obj.frequency=Freq
		QueryPerformanceFrequency(obj.baseFrequency)
		obj.Frequency=(obj.basefrequency/obj.Frequency)
		Return OBJ
	End Function

	Method GetTicks:Long()					'Get this counter's ticks (uses Frequency)
		QueryPerformanceCounter(PerfTime)
		Return perftime/frequency
	End Method

	Method Start()						'Start this counter. (Actually, just set its Started property....)
		QueryPerformanceCounter(PerfTime)
		Started=perfTime
	End Method
	
	Method Stop:Long()					'Stop the counter and return the amount of ticks.
		QueryPerformanceCounter(PerfTime)
		LastValue=Long((perftime-Started)/Frequency)
		Return LastValue
	End Method
End Type
