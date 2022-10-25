; ID: 1843
; Author: mindstorms
; Date: 2006-10-20 20:22:12
; Title: timers
; Description: blitzplus like timers

Type timer
	Field milliFrequency#
	Field current
	Field fCurrent#
	Field millis
	Field paused
End Type

Function create_timer.timer(frequency%)
	t.timer = New timer
	t\millifrequency = 1.0/Float(frequency)*1000.0
	t\current = 0
	t\fCurrent = 0.0
	t\millis = MilliSecs()
	t\paused = False
	Return t
End Function

Function update_timers()
cmillis = MilliSecs()
For t.timer = Each timer
	If Not t\paused Then 
		temp# = (cmillis-t\millis)/t\millifrequency
		t\fcurrent = t\fcurrent + temp
		t\current = Floor(t\fcurrent)
		t\millis = cmillis
	EndIf
Next
End Function

Function timer_ticks(t.timer)
	Return t\current
End Function

Function pause_timer(t.timer)
	t\paused = True
End Function

Function resume_timer(t.timer)
	t\paused = False
	t\millis = millisecs()
End Function

Function reset_timer(t.timer)
	t\current = 0
	t\fCurrent = 0.0
	t\millis = MilliSecs()
End Function

Function free_timer(t.timer)
	Delete t
End Function

Function wait_timer(t.timer)
	Repeat
		cmillis = MilliSecs()
		temp# = (cmillis-t\millis)/t\millifrequency
		t\fcurrent = t\fcurrent + temp
		ocurrent = t\current
		t\current = Floor(t\fcurrent)
		t\millis = cmillis
	until t\current > ocurrent
End Function
