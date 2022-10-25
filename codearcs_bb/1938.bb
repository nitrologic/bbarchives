; ID: 1938
; Author: kochOn
; Date: 2007-03-04 07:57:32
; Title: alarm.bb
; Description: A simple alarm library to easily deal with multiple timers

;::::::::::::::::::::::::::::::
;:::::::::: alarm.bb ::::::::::
;::::::::::::::::::::::::::::::
;
; A simple alarm library to
; easily deal with multiple
; timers
;
; by Nicolas ATEK aka kochOn
; 01/03/2007 
;
; www.kochonet.com

Const ALARM_TRIGGERED = 0
Const ALARM_ACTIVED   = 1
Const ALARM_PAUSED    = 2
Const ALARM_SECONDS   = 3
Const ALARM_MILLISECS = 4
Const ALARM_INIT      = 5

Type TAlarm
	Field set%, time%, active%, overflow%
	Field paused%, stime%
End Type

; CreateAlarm%(time% = 0)
;
; time: period in millisecs before alarm starts 'ringing'
;	  Setting time parameter empty, lesser or equal To 0 will desactive the alarm
;
; Return a handle of the newly created alarm

Function CreateAlarm%(time% = 0)
	Local alarm.TAlarm
	
	alarm = New TAlarm
	
	If time <= 0 Then
		alarm\set    = 0
		alarm\time   = 0
		alarm\active = False
	Else
		alarm\set    = time
		alarm\time   = MilliSecs() + time
		alarm\active = True
	EndIf
	
	alarm\overflow = 0
	alarm\paused = False
	alarm\stime  = 0
	
	Return Handle(alarm)
End Function

; SetAlarm(halarm%, time% = 0, overflow% = False)
;
; halarm  : handle of an existing alarm
; time    : period in millisecs before alarm starts 'ringing'
;	  Setting time parameter empty, lesser or equal to 0 will desactive the alarm
; overflow: True to keep an accurate alarm when you perpetually set or reset it in a loop  

Function SetAlarm%(halarm%, time% = 0, overflow% = False)
	Local alarm.TAlarm
	
	alarm = Object.TAlarm(halarm)
	If alarm = Null Then Return -1
	
	If overflow = False Or alarm\paused = True Then alarm\overflow = 0
	If time <= 0 Then
		alarm\set    = 0
		alarm\time   = 0
		alarm\active = False
	Else
		alarm\set    = time
		alarm\time   = MilliSecs() + time + alarm\overflow
		alarm\active = True
	EndIf
End Function

; ResetAlarm(halarm%, overflow% = False)
;	  Reset the alarm to its last time initialized with CreateAlarm or SetAlarm
;
; halarm  : handle of an existing alarm
; overflow: True to keep an accurate alarm when you perpetually set or reset it in a loop  

Function ResetAlarm%(halarm%, overflow% = False)
	Local alarm.TAlarm
	
	alarm = Object.TAlarm(halarm)
	If alarm = Null Then Return -1
	
	If overflow = False Or alarm\paused = True Then alarm\overflow = 0
	If alarm\set > 0 Then
		alarm\time   = MilliSecs() + alarm\set + alarm\overflow
		alarm\active = True
	EndIf
End Function

; DesactiveAlarm%(halarm%)
;
; halarm: handle of an existing alarm

Function DesactiveAlarm(halarm%)
	Local alarm.TAlarm
	
	alarm = Object.TAlarm(halarm)
	If alarm = Null Then Return -1
	
	alarm\active = False
	alarm\paused = False
	alarm\stime  = 0
	alarm\time   = 0	
End Function

; PauseAlarm%(halarm%)
;
; halarm: handle of an existing alarm

Function PauseAlarm%(halarm%)
	Local alarm.TAlarm
	
	alarm = Object.TAlarm(halarm)
	If alarm = Null Then Return -1
	
	If alarm\paused = True Or alarm\active = False Then Return
	
	alarm\paused = True
	alarm\stime  = alarm\time - MilliSecs()
End Function

; ResumeAlarm%(halarm%)
;
; halarm: handle of an existing alarm

Function ResumeAlarm%(halarm%)
	Local alarm.TAlarm
	
	alarm = Object.TAlarm(halarm)
	If alarm = Null Then Return -1
	
	If alarm\paused = False Or alarm\active = False Then Return
	
	alarm\paused = False
	alarm\time = MilliSecs() + alarm\stime
End Function

; PauseAlarms()

Function PauseAlarms()
	Local alarm.TAlarm
	
	For alarm = Each TAlarm
		If alarm\active = True Then
			If alarm\paused = False Then
				alarm\paused = True
				alarm\stime  = alarm\time - MilliSecs()
			EndIf
		EndIf
	Next
End Function

; ResumeAlarms()

Function ResumeAlarms()
	Local alarm.TAlarm
	
	For alarm = Each TAlarm
		If alarm\active = True Then
			If alarm\paused = True Then
				alarm\paused = False
				alarm\time = MilliSecs() + alarm\stime
			EndIf
		EndIf
	Next
End Function

; GetAlarm%(halarm%, state% = 0)
;
; halarm: handle of an existing alarm
; state : use one of 0:ALARM_TRIGGERED, 1:ALARM_ACTIVED, 2:ALARM_PAUSED, 3:ALARM_SECONDS, 4:ALARM_MILLISECS, 5:ALARM_INIT
;	  Default is 0 so you just need to use GetAlarm(halarm) without state parameter to know if an alamr is triggered
;
; Return the value according to the selected state or -1 if the alarm does not exist 

Function GetAlarm%(halarm%, state% = 0)
	Local alarm.TAlarm
	
	alarm = Object.TAlarm(halarm)
	If alarm = Null Then Return -1
		
	Select state
		Case ALARM_TRIGGERED:
			If alarm\active = True Then
				If alarm\paused = False Then
					If MilliSecs() >= alarm\time Then
						alarm\overflow = alarm\time - MilliSecs()
						alarm\time   = 0
						alarm\active = False
						Return True
					EndIf
				Else
					Return False
				EndIf
			EndIf
			Return False
		
		Case ALARM_ACTIVED:
			Return alarm\active
		
		Case ALARM_PAUSED:
			Return alarm\paused
			
		Case ALARM_SECONDS:
			If alarm\active = True Then
				If alarm\paused = True Then
					Return alarm\stime / 1000
				Else
					Return (alarm\time - MilliSecs()) / 1000
				EndIf
			Else
				Return 0
			EndIf
			
		Case ALARM_MILLISECS:
			If alarm\active = True Then
				If alarm\paused = True Then
					Return alarm\stime
				Else
					Return (alarm\time - MilliSecs())
				EndIf
			Else
				Return 0
			EndIf
		
		Case ALARM_INIT:
			Return alarm\set
		
	End Select
End Function

; DestroyAlarm(halarm%)
;	  Destroy the specified alarm
;
; halarm: handle of an existing alarm

Function DestroyAlarm(halarm%)
	Local alarm.TAlarm
	
	alarm = Object.TAlarm(halarm)
	If alarm = Null Then Return -1
	
	Delete alarm	
End Function

; DestroyAlarms()
;	  Destroy all previously created alarms

Function DestroyAlarms()
	Local alarm.TAlarm
	
	For alarm = Each TAlarm
		Delete alarm
	Next	
End Function

;:::::::::: a little demo ::::::::::

;	Graphics 640, 480, 32, 2
;	SetBuffer BackBuffer()
;	
;	; Create 3 alarms with different values (test with other values)
;	Global alarm1 = CreateAlarm(500)
;	Global alarm2 = CreateAlarm(1000)
;	Global alarm3 = CreateAlarm(1500)
;
;	; for alarms triggered message to be posted an amount of time
;	Global alarm1_triggered = CreateAlarm()
;	Global alarm2_triggered = CreateAlarm()
;	Global alarm3_triggered = CreateAlarm()
;	
;	While Not KeyDown(1)
;		Cls
;		
;		Color 255, 255, 255
;		Text 160, 200, "alarm1:" + GetAlarm(alarm1, ALARM_INIT) + " mil.", True, True
;		Text 160, 240, GetAlarm(alarm1, ALARM_MILLISECS), True, True    
;		Text 320, 200, "alarm2:" + GetAlarm(alarm2, ALARM_INIT) + " mil.", True, True
;		Text 320, 240, GetAlarm(alarm2, ALARM_MILLISECS), True, True    
;		Text 480, 200, "alarm3:" + GetAlarm(alarm3, ALARM_INIT) + " mil.", True, True
;		Text 480, 240, GetAlarm(alarm3, ALARM_MILLISECS), True, True    
;		
;		; If alarms are triggered, reset them to their initial values
;		; and set alarms_triggered for informations posting
;		; ResetAlarm use the overflow parameter to keep accuracy (try False and compare)
;		If GetAlarm(alarm1) Then SetAlarm(alarm1_triggered, 250) : ResetAlarm(alarm1, True)
;		If GetAlarm(alarm2) Then SetAlarm(alarm2_triggered, 250) : ResetAlarm(alarm2, True)
;		If GetAlarm(alarm3) Then SetAlarm(alarm3_triggered, 250) : ResetAlarm(alarm3, True)
;		
;		Color 255, 0, 0
;		; Check if alarms used to post triggered message are active
;		; and so post informations	
;		If GetAlarm(alarm1_triggered, ALARM_ACTIVED) Then Text 160, 280, "Triggered", True, True
;		If GetAlarm(alarm2_triggered, ALARM_ACTIVED) Then Text 320, 280, "Triggered", True, True
;		If GetAlarm(alarm3_triggered, ALARM_ACTIVED) Then Text 480, 280, "Triggered", True, True
;		
;		; Necessary to know when alarms will be triggered and so will become inactive
;		GetAlarm(alarm1_triggered)  
;		GetAlarm(alarm2_triggered)
;		GetAlarm(alarm3_triggered)
;		
;		Color 255, 255, 0
;		If GetAlarm(alarm1, ALARM_PAUSED) = False Then
;			Text 320, 80, "PRESS SPACE TO PAUSE ALARMS", True, True	
;		Else
;			Text 320, 80, "PRESS SPACE TO RESUME ALARMS", True, True	
;		EndIf
;		
;		If KeyHit(57) Then
;			If GetAlarm(alarm1, ALARM_PAUSED) Then
;				ResumeAlarms()
;			Else
;				PauseAlarms()
;			EndIf	
;		EndIf
;		
;		Flip
;	Wend
