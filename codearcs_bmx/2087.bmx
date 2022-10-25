; ID: 2087
; Author: xlsior
; Date: 2007-08-06 02:33:13
; Title: Day of Year
; Description: Returns the day of the year for any date

' Day of the Year
'
' By Marc van den Dikkenberg
'
Strict
Print "Day of the year: "+DayNumber("05 Aug 2007")	' Specific Date
Print "Day of the year: "+DayNumber("")					' Returns Current Date


Function DayNumber(SomeDate:String)		' Date in form: "DD MMM YYYY", e.g.  "02 APR 2007"
	If SomeDate="" Then	
		SomeDate = CurrentDate$() 		' If no date specified, use today's date
	End If

	Local Dag:Int[]=[31,28,31,30,31,30,31,31,30,31,30,31,31,29,31,30,31,30,31,31,30,31,30,31]
	Local Maand:String[]=["JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"]
	Local YearStartPoint:Int=0
	Local Jaar:Int=Int(Right$(SomeDate,4))
	Local MonthCount:Int=0
	Local TotalDays:Int=0
	Local TempVar:Int=0

	If ((Jaar Mod 4) = 0 And (Jaar Mod 100) <> 0) Or ((Jaar Mod 4) = 0 And (Jaar Mod 400) = 0) Then 
		' It's a Leap Year
		YearStartPoint=12
	Else
		' It's not a Leap Year
		YearStartPoint=0
	End If

	For TempVar:Int=0 To 11
		If Upper(Mid$(SomeDate,4,3))=maand[TempVar] Then
			MonthCount:Int=TempVar:Int
			Exit
		End If
	Next
	For Tempvar:Int=0 To MonthCount:Int-1
		TotalDays:Int=TotalDays:Int+dag[TempVar+YearStartPoint]
	Next
	TotalDays:Int=TotalDays:Int+Int(Left$(SomeDate,2))
	Return TotalDays
End Function
