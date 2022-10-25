; ID: 2034
; Author: spacerat
; Date: 2007-06-10 10:51:28
; Title: PC Beep!
; Description: PC Speaker beep, with timer

Framework BRL.StandardIO
SuperStrict

'/------------------------\'
'|''''''BEEPER TYPE'''''''|'
'\------------------------/'
Type TBeep
	'Beep Timer Type
	Field Timer:Int[254]
	Field time:Int=0
	Field notes:Byte=0
	Method SetTune(tune:Byte)
		''Sets tune, you can add tunes to this yourself with ease.
		''argument=tune number
		Select tune
			Case 0
				Timer[0]=1
				Timer[1]=300
				Timer[2]=500				
				Timer[3]=600
				Timer[4]=900				
				Timer[5]=1500
				Timer[6]=1800
				Notes=6  ''SET NOTES TO THE END NOTE NUMBER (6 in this case)
			Default
				Timer[0]=0
		EndSelect
	EndMethod
	Method Beep()
		''''''''''''''''''''''''''''''''''''''''''''''''''
		'''''''''''''''''''''Beep!''''''''''''''''''''''''
		''''''''''''''''''''''''''''''''''''''''''''''''''
		Print Chr(7)
	EndMethod
	Method tick:Byte()
		''Call this function to continue one step through the beep sequence
		''Returns 1 if the sequence is not fineshed, returns 0 if it is.
		time:+1
		If time>=Timer[0]
			Beep()
			If Not Timer[1]=0
				For Local i:Byte=1 To notes
					Timer[i-1]=Timer[i]
					'Print timer[i]  <-- Some debug thing for debuging etc
					Timer[i]=0
					
				Next
			Else
				Timer[0]=0
				Return 0
			EndIf
		EndIf
		Return 1
	EndMethod
	Method TimerReset()
		''Reset the tune.
		time=0
	EndMethod
EndType

'/------------------------\'
'|'''''EXAMPLE PROGRAM''''|'
'\------------------------/'
Global Beeper:TBeep=New TBeep
Beeper.SetTune(0)
Repeat
	Delay 1
Until Beeper.tick()=0
