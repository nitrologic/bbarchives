; ID: 1690
; Author: Beaker
; Date: 2006-05-02 13:27:05
; Title: [bmax] Interval system
; Description: Call any function(s) at regular intervals - set & go

SuperStrict


Type Tinterval
	Field _func()
	Field timer:TTimer
	Field hz#

	Global _list:TList

	Method New()
		If _list = Null
			_list = New TList
			AddHook EmitEventHook,_EventHandler
		EndIf
		_list.addlast(Self)
	End Method

	Function Set:Tinterval(func(),hz#)
		Local interval:Tinterval = New Tinterval
		interval.timer = CreateTimer(hz)
		interval.hz = hz
		interval._func = func
		Return interval
	End Function

	Method Clear()
		StopTimer Self.timer
		_list.remove(Self)
	EndMethod

	Function ClearAll()
		If _list <> Null
			_list.clear()
			_list = Null
			RemoveHook EmitEventHook,_EventHandler
		EndIf
	EndFunction

	Function _EventHandler:Object(id%, data:Object, context:Object)
		Local ev:TEvent=TEvent(data)
		If ev.id = EVENT_TIMERTICK 
			If _list <> Null
				For Local interval:Tinterval = EachIn _list
					If ev.source = interval.timer
						interval._func()
						Exit
					EndIf
				Next
			EndIf
		EndIf 
	End Function


End Type

'Example use
Graphics 320,240

Function test()
	DebugLog "TIMER1"
End Function

Function test2()
	Global cnt%
	cnt :+1
	If cnt > 4
		myint.Clear()
	EndIf
	DebugLog "TIMER2"
End Function


Tinterval.Set(test,1)
Global myint:Tinterval = Tinterval.Set(test2,0.5)


While Not AppTerminate()
	Delay 1
Wend
End
