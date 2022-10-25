; ID: 2360
; Author: Ked
; Date: 2008-11-16 21:14:09
; Title: Extended Date and Time Functions
; Description: More functions for getting the time and date. (Sorry if this has already been done!)

Function CurrentHour:String(twelvehour=False)
	Local t:String=CurrentTime()
	Local hour:String=Left(t,2)
	
	If twelvehour=False
		Return hour
	Else
		Return String(Int(hour)-12)
	EndIf
EndFunction

Function CurrentMinute:String()
	Local t:String=CurrentTime()

	Return Mid(t,4,2)
EndFunction

Function CurrentSecond:String()
	Local t:String=CurrentTime()

	Return Mid(t,7,2)
EndFunction

Function IsPM:Int()
	Local t:String=CurrentTime()
	Local hour:String=Left(t,2)
	
	If Int(hour)>=12
		Return True
	Else
		Return False
	EndIf
EndFunction

Function CurrentDay:String()
	Local t:String=CurrentDate()

	Return Left(t,2)
EndFunction

Function CurrentMonth:String(full=False)
	Local t:String=CurrentDate()
	Local month:String=Mid(t,4,3)
	
	If full=True
		month=Lower(month)
		Select month
			Case "jan"
				Return "January"
			
			Case "feb"
				Return "February"
			
			Case "mar"
				Return "March"
			
			Case "apr"
				Return "April"
			
			Case "may"
				Return "May"
			
			Case "jun"
				Return "June"
			
			Case "jul"
				Return "July"
			
			Case "aug"
				Return "August"
			
			Case "sep"
				Return "September"
			
			Case "oct"
				Return "October"
			
			Case "nov"
				Return "November"
			
			Case "dec"
				Return "December"
		EndSelect
	Else
		Return month+"."
	EndIf
EndFunction

Function CurrentYear:String()
	Local t:String=CurrentDate()
	
	Return Mid(t,8,11)
EndFunction
