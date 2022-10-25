; ID: 2152
; Author: JoshK
; Date: 2007-11-08 23:52:51
; Title: AppTiming module
; Description: Functions for controlling application timing

Module leadwerks.apptiming

Private

Global AppTime_UPS
Global AppTime_Iterator
Global AppTime_CurrentTime
Global AppTime_PauseStart=0
Global AppTime_Speed:Float=1.0
Global AppTime_DesiredLoopTime#=1000.0/60.0
Global AppTime_LastUpdateTime=0
Global AppTime_LastUPSTime

Public

Rem
bbdoc:
EndRem
Function UpdateAppTime(framerate=60)
	Local time
	Local elapsed
	
	If AppTime_PauseStart Return
	
	AppTime_DesiredLoopTime=1000.0/framerate

	time=MilliSecs()

	If AppTime_LastUpdateTime=0
		AppTime_Speed#=1.0
		AppTime_LastUpdateTime=time
		AppTime_CurrentTime=time
		AppTime_LastUPSTime=time
	Else
		elapsed=time-AppTime_LastUpdateTime
		If Not elapsed
			elapsed=1
			Delay 1
			time:+1
		EndIf
		AppTime_Speed=Float(elapsed)/Float(AppTime_DesiredLoopTime)
		AppTime_CurrentTime=time
		AppTime_LastUpdateTime=time
	EndIf
	
	AppTime_Iterator:+1
	If AppTime_CurrentTime-AppTime_LastUPSTime>=1000
		AppTime_UPS=Float(AppTime_Iterator)/(Float(AppTime_CurrentTime-AppTime_LastUPSTime)/1000.0)
		AppTime_LastUPSTime=AppTime_CurrentTime
		AppTime_Iterator=0
	EndIf
	
EndFunction

Rem
bbdoc:
EndRem
Function AppTime()
	Return AppTime_CurrentTime
EndFunction

Rem
bbdoc:
EndRem
Function AppSpeed#()
	Return AppTime_Speed
EndFunction

Rem
bbdoc:
EndRem
Function UPS:Int()
	Return AppTime_UPS
EndFunction

Rem
bbdoc:
EndRem
Function PauseApp()
	If AppTime_PauseStart Return
	AppTime_PauseStart=MilliSecs()
EndFunction

Rem
bbdoc:
EndRem
Function ResumeApp()
	If Not AppTime_PauseStart Return
	If AppTime_LastUpdateTime
		Local elapsed=MilliSecs()-AppTime_PauseStart
		AppTime_LastUpdateTime:+elapsed
	EndIf
	AppTime_PauseStart=0
EndFunction
