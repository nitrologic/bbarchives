; ID: 2787
; Author: JoshK
; Date: 2010-11-24 02:34:39
; Title: Prevent multiple process
; Description: Makes it so only one instance of your app can run

SuperStrict

Import bah.interprocess

Private

OnEnd cleanup

Function Cleanup()
	Local applock:TApplock
	For applock=EachIn TApplock.map.values()
		applock.Free()
	Next
EndFunction

Public

New TApplock

Type TAppLock
	
	Const TIMEOUT:Int = 1000*20
	
	Global map:TMap=New TMap
	
	Field shareddata:TBank
	Field timer:TTimer
	
	Method New()
		shareddata=CreateSharedBank("AppLockData_"+StripDir(AppFile),4)
		If MilliSecs()-shareddata.PeekInt(0)<TIMEOUT
			Notify "Another copy of "+StripDir(AppFile)+" is already running.  You may only run one copy of this program.",1
			End
		EndIf
		Update()
		timer=CreateTimer(1000.0/(TIMEOUT/2.0))
		map.insert(timer,Self)
		AddHook EmitEventHook,EventHook,Self
	EndMethod
	
	Method Delete()
		Free()
	EndMethod
	
	Method Update()
		shareddata.PokeInt(0,MilliSecs())
	EndMethod
	
	Function EventHook:Object(id:Int,data:Object,context:Object)
		Local applock:TApplock
		Local event:TEvent=TEvent(data)
		If event
			If event.id = EVENT_TIMERTICK
				applock = TAppLock(map.valueforkey(event.source))
				If applock applock.Update()
			EndIf
		EndIf
		Return data
	EndFunction
	
	Method Free()
		shareddata.PokeInt(0,0)
		map.remove(timer)
		timer.stop()
	EndMethod
	
EndType
