; ID: 2110
; Author: Diego
; Date: 2007-09-20 07:58:56
; Title: Datestamp
; Description: Get the number of days since 1.1. Year 1 | Bekomme die Anzahl der Tage seit dem 1.1. Jahr 1

Function Datestamp(Date$ = "")
If Date$ = "" Then Date$ = CurrentDate()
Day% = Left(Date$, 2)
Year% = Right(Date$, 4)
Select Upper(Mid(Date$, 4, 3))
	Case "JAN"
		Month% = 0
	Case "FEB"
		Month% = 31
	Case "MAR"
		Month% = 59 + IstSchaltjahr(Year%)
	Case "APR"
		Month% = 90 + IstSchaltjahr(Year%)
	Case "MAY"
		Month% = 120 + IstSchaltjahr(Year%)
	Case "JUN"
		Month% = 151 + IstSchaltjahr(Year%)
	Case "JUL"
		Month% = 181 + IstSchaltjahr(Year%)
	Case "AUG"
		Month% = 212 + IstSchaltjahr(Year%)
	Case "SEP"
		Month% = 243 + IstSchaltjahr(Year%)
	Case "OCT"
		Month% = 273 + IstSchaltjahr(Year%)
	Case "NOV"
		Month% = 304 + IstSchaltjahr(Year%)
	Case "DEC"
		Month% = 334 + IstSchaltjahr(Year%)
	End Select
Year% = Year% - 1 ; To get only the days of the past years | Um nur die Tage der Vergangenen Jahre zu bekommen
Return Day% + Month% + Year% * 365 + Int(Floor(Year% / 4)) - Int(Floor(Year% / 100)) + Int(Floor(Year% / 400))
End Function

Function IstSchaltjahr(Year%)
If(Year% Mod 400 = 0) Return True
If(Year% Mod 100 = 0) Return False
If(Year% Mod 4   = 0) Return True
Return False
End Function

; If date is empty, the curent date will be used.
; Wenn das Datum nicht angegeben ist, wird das aktuelle Datum verwendet.
Print Datestamp()
WaitKey
