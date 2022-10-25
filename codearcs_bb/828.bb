; ID: 828
; Author: Rob Farley
; Date: 2003-11-19 17:51:00
; Title: Race Timer Functions
; Description: Allows you to start/stop and reset a timer returning a formatted time

; Race timer functions by Rob Farley
; 2003 Mentalillusion
; http://www.mentalillusion.co.uk
; rob@mentalillusion.co.uk

Graphics 400,300,0,2


; globals necessary for the timer to work
Global t_clock=0
Global t_offset=0
Global t_total_offset=0
Global t_clock_mode=0



SetBuffer BackBuffer()

; reset clock function to zero the clock
reset_clock()


Repeat									; main loop


; get the current time and put it into a variable
timer$=get_clock$()

Color 255,255,255
Text 200,0,timer$,True
Text 200,40,"Space to Stop/Start, R to reset clock",True

Flip

; clock control
If KeyHit(57) Then start_stop_clock()	;start/stop clock on spacebar
If KeyHit(19) Then reset_clock()		;reset clock if you hit R


Cls

Until KeyHit(1)							;until you hit ESC




Function get_clock$()
	If t_clock_mode=1 Then t=MilliSecs()-t_clock-t_total_offset
	If t_clock_mode=0 Then t=t_offset-t_clock-t_total_offset
	h=t/10
	s=h/100
	h=h-(s*100)
	m=s/60
	s=s-(m*60)
	
	Return Right("00"+m,2)+":"+Right("00"+s,2)+":"+Right("00"+h,2)
	; return the clock in MM:SS:HH format

End Function

Function reset_clock()
	t_clock=MilliSecs()
	t_offset=MilliSecs()
	t_total_offset=0
End Function

Function start_stop_clock()
	If t_clock_mode=0
		t_clock_mode=1
		t_total_offset=t_total_offset+MilliSecs()-t_offset
		Else
		t_clock_mode=0
		t_offset=MilliSecs()
		EndIf
End Function
