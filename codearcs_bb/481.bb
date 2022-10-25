; ID: 481
; Author: BlitzSupport
; Date: 2002-11-09 11:07:57
; Title: Text print queue
; Description: Handles display of console-like text messages

Type PrintQ
	Field message$
	Field time
End Type

Function PrintToQ (message$)
	p.PrintQ = New PrintQ
	p\message = message$
	p\time = MilliSecs ()
End Function

Function UpdatePrintQ (timeout = 2000)
	Locate 0, 0
	For p.PrintQ = Each PrintQ
		Print p\message$
		If p\time < MilliSecs () - timeout Then Delete p
	Next
End Function
