; ID: 1006
; Author: skn3[ac]
; Date: 2004-04-27 20:33:14
; Title: b+ CreateDelayTimer(length)
; Description: Creates a timer that will trigger after a specified time period.

Function CreateDelayTimer(length)
	Local timerlength#
	If length > 1000
		timerlength# = 1.0 / (length / 1000.0)
		Return CreateTimer(timerlength#)
	Else
		timerlength# = 1000.0 / length
		Return CreateTimer(timerlength#)
	End If
End Function
