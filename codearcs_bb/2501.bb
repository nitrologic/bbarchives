; ID: 2501
; Author: Malice
; Date: 2009-06-10 07:42:32
; Title: Duration Timers
; Description: Some functions to monitor and set timers that run for a specific duration

[code]
Global Total_Timers=0	;Limit of 15 Timers. Referenced by an integer 1 - 15.

Type Timer
	Field Reference
	Field Start=0
	Field Duration
	Field Active=False
	Field Repeat_On_End=False
	Field InstantRepeat=False
	Field RunCount=0
	Field SetTime
End Type

Function KillTimer(Reference)
	;Destroys Timer, even if Activve and removes any Run-count info.
	DeadTimer.Timer=GetTimerByRef.Timer(Reference)
	If (DeadTimer <> Null)
		Delete DeadTimer
	End If
End Function

Function UpdateTimers()
	Local Now=MilliSecs()
	For iterTimer.Timer = Each Timer
		If (iterTimer=Null)
			; Safety Check
			Exit
		End If
		If ((Now-(iterTimer\Start+iterTimer\SetTime))>0)
			If (((iterTimer\Start+iterTimer\SetTime)+iterTimer\Duration)<Now)
				If (Not(iterTimer\Active))
					iterTimer\Active=True
					iterTimer\RunCount=iterTimer\RunCount+1
				End If
			Else
				If (iterTimer\Repeat_On_End)
					If (InstantRepeat)
					; Note - Instant_Repeat will continue repeating indefinitely until KILLED or Repeat switched OFf.
						iterTimer\Start=0
						iterTimer\RunCount=iterTimer\RunCount+1
					Else
						iterTimer\Active=False
						
					End If
					iterTimer\SetTime=Now
				Else
					Delete iterTimer
				End If
			End If
		End If
	Next
End Function

Function RetrieveTimerBeginsIn(Reference)
	; Returns the time left (in milliseconds) until timer 'Reference' becomes Active. If timer is already active, errors may occur (untested)
	Local CheckTimer.Timer=GetTimerByRef(Reference)
	Return (((CheckTimer\Start+CheckTimer\SetTime))-MilliSecs())
End Function

Function RetrieveDurationRemaining(Reference)
	; Returns the time left (in milliseconds) until timer 'Reference' finishes. If timer is not already active, errors may occur (untested).
	Local CheckTimer.Timer=GetTimerByRef(Reference)
	Return (((CheckTimer\SetTime+CheckTimer\Start+CheckTimer\Start+Duration))-MilliSecs())
End Function

Function GetTimerByRef.Timer(Reference)
	For iterTimer.Timer = Each Timer
		If (iterTimer\Reference = Reference)
			Return iterTimer.Timer
		End If
	Next
	Return Null
End Function

Function GetActiveTimers(Specific=False)
	; Accepts and Returns bitwise operators.
	; This is the reason for the hard limit on number of Timers
	Local nReturn=0
	For iterTimer.Timer = Each Timer
		If (iterTimer\Active)
			nReturn=nReturn+(2^iterTimer\Reference)
		End If
	Next
	If (Specific)
		nReturn=(nReturn And Specific)
	End If
	Return nReturn
End Function

Function QuickstarttAllTimers()
	; Has no effect on currently active Timers.
	Local Now=MilliSecs()
	For iterTimer.Timer = Each Timer
		If (iterTimer.Timer <> Null)
			If (Not(iterTimer\Active))
				iterTimer\SetTime=(MilliSecs()-(iterTimer\Start))
				iterTimer\Active=True
				iterTimer\RunCount=iterTimer\RunCount+1
			End If
		End If
	Next
End Function

Function StartTimerNow(Reference,Repeat_On_End=-1)
	; RepeatOnEnd will override existing if set, even for Currently Active Timers. Duration is NOT affected.
	StartTimer.Timer=GetTimerByRef.Timer(Reference)
	If (StartTimer = Null)
		; Invalid Reference
		Return
	Else
		If (StartTimer\Active)
			; Already Running
		Else
			StartTimer\SetTime=(MilliSecs()-(StartTimer\Start))
			StartTimer\Active=True
			If (Repeat_On_End>-1)
				StartTimer\Repeat_On_End=Repeat_On_End
			End If
		End If
	End If
End Function

Function NewDuration(Reference,Duration)
	; This WILL affect currently active timers.
	DurationTimer.Timer=GetTimerByRef.Timer(Reference)
	If (DurationTimer.Timer = Null)
		; Invalid Reference
		Return
	Else					
		DurationTimer\Duration=Duration
	End If
End Function

Function StopTimerNow(Reference,Repeat_On_End=-1,InstantRepeat=-1)
	StopTimer.Timer=GetTimerByRef.Timer
	If (StopTimer = Null)
		; Invalid Reference
		Return
	Else
		If (Repeat_On_End>-1)
			StopTimer\Repeat_On_End=Repeat_On_End
		End If
		If ((StopTimer\Repeat_On_End))
			If ((InstantRepeat>-1))
				StopTimer\InstantRepeat=InstantRepeat
			End If
			If (InstantRepeat)
				;Note - Instant_Repeat will continue repeating indefinitely until KILLED or Repeat switched OFf.
				;The Duration is unaffected. Please use 'NewDuration' to change if required. Instant Repeat is perhaps
				;best set to False for the purposes of this function, though the choice is still available.
				StopTimer\Start=0
				StopTimer\SetTime=(MilliSecs())
				StopTimer\RunCount=StopTimer\RunCount+1
			Else
				StopTimer\Active=False
				StopTimer\SetTime=(MilliSecs())
			End If
		Else
			Delete StopTimer
		End If
	End If
End Function

Function EnsureRepeat(Reference,InstantRepeat=False,NewDelay=-1)
	; Note - Instant_Repeat will continue repeating indefinitely until KILLED or Repeat switched OFf.
	; The Duration is unaffected. Please use 'NewDuration' to change if required.
	; If InstantRepeat has ever been used for this timer, NewDelay can be set to restore a delay between repetitions.
	RepeatTimer.Timer=GetTimerByRef.Timer
	If (RepeatTimer = Null)
		; Invalid Reference
		Return
	Else
		RepeatTimer\Repeat_On_End=True
		RepeatTimer\InstantRepeat=InstantRepeat
		If (Not(InstantRepeat))
			RepeatTimer\Start=NewDelay
		Else
			RepeatTimer\Start=0
		End If
	End If
End Function

Function CancelRepeat(Reference)
	; No Repeat means timer will be destroyed once it has finished running.
	RepeatTimer.Timer=GetTimerByRef.Timer
	If (RepeatTimer = Null)
		; Invalid Reference
		Return
	Else
		RepeatTimer\Repeat_On_End=False
		RepeatTimer\InstantRepeat=False
	End If
End Function

Function ResetRunCount(Reference)
	CountTimer.Timer=GetTimerByRef.Timer
	If (CountTimer = Null)
		; Invalid Reference
		Return 
	Else
		CountTimer\RunCount=0
	End If
End Function

Function CountTimesRun(Reference)
	CountTimer.Timer=GetTimerByRef.Timer
	If (CountTimer = Null)
		; Invalid Reference
		Return 
	Else
		Return CountTimer\RunCount
	End If
	
	; Just In Case (Safety Check) - This shouldn't occur ever
	Return-1
End Function

Function AddTimer(Reference,Duration=1,StartNow=False,StartIn=0,Repeat_On_End=False,Instant_Repeat=False)
	; It is up to the coder to keep track of reference numbers. using integers (or constants equivalent) 1 - 15 is advised.
	
	; Reference:		A uniqque reference number for the timer.
	; Duration:			How long the timer remains 'active' once started (in Milliseconds)
	; Start Now:		Set this Flag to True if you want to make the Timer Active immediately. If False, 'Start In' value will override.
	; StartIn:			Time in (Milliseconds) from the call of this function until the Timer becomes Active.
	; Repeat_On_End:	Set to True if you wish the timer to commence again once it has finished. (Also see 'InstantRepeat' below)
	; InstantRepeat:	If Repeat_On_End is set as True, this flag will cause the timer to re-commence as soon as it is finished. If False, 'Start_In' value will be used again.
	
	For iterTimer.Timer = Each Timer
		If iterTimer\Reference=Reference
			If (iterTimer=Null)
				; Safety Check.
				Exit
			Else	
				If iterTimer\Active=True
					; Cannot amend an Active Timer.
					Return
				Else
				; Timer in use. Amend Values insterad of making New. This method resets RunCount and Repeat info.
					Exit
				End If
			End If
			iterTimer.Timer = New Timer
			Total_Timers=Total_Timers+1
			Exit
		End If
	Next
	If (iterTimer=Null)
		If (Total_Timers>15)
			; Too Many Timers. Cannot Add New.
			Return
		Else
			iterTimer\Reference = Reference
			iterTimer\Repeat_On_End=Repeat_On_End
			iterTimer\Duration=Duration
			iterTimer\RunCount=0
			iterTimer\SetTime=MilliSecs()
			If (StartNow)
				iterTimer\Start=0
				iterTimer\Active=True
				iterTimer\RunCount=iterTimer\RunCount+1
			Else
				iterTimer\Start=(Start_In)
			End If
		End If
	EndIf
End Function
[/code]
