; ID: 928
; Author: ralphy
; Date: 2004-02-10 05:01:58
; Title: Multiple Event triggers
; Description: Handle multiple events without halting program flow

;Multiple Event Trigger demo
;Useful for handling frame animation for multiple objects instead of using
;the delay or timer functions (they halt program flow).

;Michael Ralph (ralphy)

Graphics 640,480,16,2
SetBuffer BackBuffer()

;Instead of Globals you could set up your event as a Type collection
;Type Event
;	Field eID
;	Field clockDelay
;	Field lastclock
;	Field status
;End Type

Global event_status = 0
Global event_lastclock = 0
Global event2_status = 0
Global event2_lastclock = 0
Global event3_status = 0
Global event3_lastclock = 0


While KeyHit(1) = 0

	FrameTimer() ; called only once every display update
	
	If event_status = 1 Then
		Text 0,y, "Event 1 fired"
		y=y+10
	EndIf
	
	If event2_status = 1 Then
		Text 200,y2, "Event 2 fired"
		y2=y2+10
	EndIf
	
	If event3_status = 1 Then
		Text 400,y3, "Event 3 fired"
		y3=y3+10
	EndIf	
	
	;etc....
	
	Flip
Wend
End


Function FrameTimer()

	event_status = 0
	event2_status = 0
	event3_status = 0	
	
	iclock = MilliSecs()
	
; you could iterate your event collection instead of the following...
	If iclock > event_lastclock + 1000 Then
		event_lastclock = iclock
		event_status = 1
	EndIf

	If iclock > event2_lastclock + 500 Then
		event2_lastclock = iclock
		event2_status = 1
	EndIf
	
	If iclock > event3_lastclock + 300 Then
		event3_lastclock = iclock
		event3_status = 1
	EndIf
		
		
End Function
