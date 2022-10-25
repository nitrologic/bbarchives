; ID: 2151
; Author: Mark Tiffany
; Date: 2007-11-07 14:38:22
; Title: Memory Monitor
; Description: A simple memory monitor for use with BlitzMax & MaxGUI

Global memcheckwin:Tgadget
Global memchecktimer:TTimer

memchecktimer=CreateTimer(10)
AddHook EmitEventHook , _memcheck_hook,memchecktimer

Function ShowMem()
	Local m:Long
	If memcheckwin = Null Then
		memcheckwin = CreateWindow("Memory Usage" , GadgetWidth(Desktop() ) - 250 , 0 , 250 , 30 , Null , WINDOW_TITLEBAR)
	End If
	GCCollect() 
	m = GCMemAlloced() 
	SetGadgetText memcheckwin , "Memory Usage: " + m
End Function

Function _memcheck_hook:Object(id:Int , data:Object , context:Object) 
	Local e:TEvent
	e=TEvent(data)
	If e.source = memchecktimer And e.id=EVENT_TIMERTICK Then
		ShowMem() 
		Return Null
	Else
		Return data
	End If
End Function
