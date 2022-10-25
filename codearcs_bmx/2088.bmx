; ID: 2088
; Author: xlsior
; Date: 2007-08-06 03:41:56
; Title: Date Difference
; Description: Count the number of days between any two dates

' Compare two dates, and return how many days apart they are.
'
' Example: comparing "15 Sep 2007" with "17 Sep 2007" will Return a '2' day difference.
'
' By Marc van den Dikkenberg
' http://www.xlsior.org
'
' This defaults to calculating for dates after since 1900, but you can go back further by changing
' the BeginYear variable. Set it back as far as you need, but keep in mind that setting it back too
' far adds unnecesary processing cycles.
'
' Basically it converts the input dates to the day number since <BeginYear>, and compares the two
' dates with each other. Leap Years are accounted for.
'
'
Strict

Print DaysInBetween("","12 Aug 2007",True)    'Days in between current date and Aug 12th 2007


Function DaysInBetween(SomeDate:String,SomeDate2:String,Absolute=True)
	' The third parameter defines whether to show the absolute number of days difference, 
	' or to return negative numbers when the second date is in the past.
	If SomeDate="" Then	
		SomeDate = CurrentDate$() 		' If no date specified, use today's date
	End If
	If Somedate2="" Then
		SomeDate2 = CurrentDate$() 		' If no date specified, use today's date
	End If
	If Absolute=True Then
		Return Abs(DayNumber(SomeDate2)-DayNumber(Somedate))
	Else 
		Return (DayNumber(SomeDate2)-DayNumber(Somedate))
	End If
End Function


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
	Local BeginYear=1900

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
	
	If jaar > BeginYear Then
		For TempVar:Int=BeginYear To jaar-1
			If ((TempVar Mod 4) = 0 And (Tempvar Mod 100) <> 0) Or ((tempvar Mod 4) = 0 And (Tempvar Mod 400) = 0) Then 
			' It's a Leap Year
			Totaldays=Totaldays+366
			Else
			' It's not a Leap Year
			TotalDays=Totaldays+365
			End If			
		Next
	End If
	
	Return TotalDays
End Function
