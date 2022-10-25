; ID: 647
; Author: Reaper
; Date: 2003-04-08 21:09:38
; Title: WaitTimer(timername)
; Description: Replaces the old WaitTimer( timername) command

Function WaitTimer(timer$)
	Repeat
	Select WaitEvent()
	Case $803 : End
	Case $4001 
		Select EventSource()
		Case timer$
			Return
		End Select
	End Select
	Forever
End Function
