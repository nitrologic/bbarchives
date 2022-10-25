; ID: 1647
; Author: Jim Brown
; Date: 2006-03-26 12:16:39
; Title: Date functions: JulianDay() , JulianDate()  , DayFromDate()
; Description: Julian day functions

' JuilanDay() / JulianDate() functions
' converted to BlitzMax by Jim Brown


' *******************************************************

' EXAMPLES

SuperStrict

Framework BRL.System
Import BRL.StandardIO


Global fromdate$=CurrentDate$()
Global todate$="27 MAY 2009"

Global JDay%=JulianDay(fromdate$)

Print ""
Print "Todays date is "+DayFromDate$(fromdate$)+","+fromdate$
Print ""
Print "JulianDay for TODAY = "+JDay
Print "Date returned from JulianDate("+String(JDay)+") = "+JulianDate(JDay)
Print ""
Print "Days between TODAY and "+todate$+" = "+(JulianDay(todate$) - JulianDay(fromdate$))
Print "In 7 days time the date will be "+JulianDate(JDay+7)
Print "2 days ago it was a "+DayFromDate$(JulianDate(JDay-2))


' *******************************************************

' FUNCTIONS


' Returns a Julian day from given date.
' date$ format should be "DD MMM YYYY" - E.G, "15 MAR 2006"
Function JulianDay%(date$)
	Const Month$="JANFEBMARAPRMAYJUNJULAUGSEPOCTNOVDEC"
	' extract first 2 digits from date$
	Local d%=Int(date[..2])				' Left(date$,2)
	' extract month from date$
	Local mnt$=date[3..6].ToUpper()		' Mid$(date$,4,3)
	' extract year from date$
	Local y%=Int(date[date.Length-4..]) ' Right$(date$,4)
	' find month in the Month string
	For Local m%=1 To 12
		If mnt=Month[(m-1)*3..(m-1)*3+3]
			Return (1461*(y+4800+(m-14)/12))/4+(367*(m-2-12*((m-14)/12)))/12-(3*((y+4900+(m-14)/12)/100))/4+d-32075
		EndIf
	Next	
End Function

' Returns date$ from Julian day
' Format returned = "DD MMM YYYY". EG, "15 MAR 2006"
Function JulianDate$(jd%)
	Const Month$="JANFEBMARAPRMAYJUNJULAUGSEPOCTNOVDEC"
	Local a% = jd + 32044
	Local b% = (4*a+3)/146097
	Local c% = a - (b*146097)/4
	Local d% = (4*c+3)/1461
	Local e% = c - (1461*d)/4
	Local m% = (5*e+2)/153
	Local Day% = e - (153*m+2)/5 + 1
	Local MonthIndex% = (m + 3 - 12*(m/10))-1
	Local Year% = b*100 + d - 4800 + m/10
	Local mnt$=Month[MonthIndex*3..MonthIndex*3+3]
	Local dy$=String(Day)
	If Day<10 dy$="0"+dy$
	Return dy$+" "+mnt$+" "+String(Year)
End Function

' Returns a day from given date$
' date$ format should be "DD MMM YYYY" - E.G, "15 MAR 2006"
' Ref: Uses Zeller's Congruence
Function DayFromDate$(date$)
	Const Month$="JANFEBMARAPRMAYJUNJULAUGSEPOCTNOVDEC"
	Const dayname$="Satur  Sun    Mon    Tues   Wednes Thurs  Fri    "
	' extract first 2 digits from date$
	Local d%=Int(date[..2])				' Left(date$,2)
	' extract month from date$
	Local mnt$=date[3..6].ToUpper()		' Mid$(date$,4,3)
	' extract year from date$
	Local y%=Int(date[date.Length-4..]) ' Right$(date$,4)
	' find month in the Month string
	For Local m%=1 To 12
		If mnt=Month[(m-1)*3..(m-1)*3+3]
			If m < 3
    			m:+ 12 ; y:- 1
			End If
			Local century% = y / 100
			y:- 100 * century
			Local z% = d + ( (m+1) * 26 ) / 10 + y + y/4 + century/4 - 2*century
			z :Mod 7
			If z<0 z:+ 7
			Return dayname[z*7..z*7+7].Trim() +"day"
		EndIf
	Next
End Function
