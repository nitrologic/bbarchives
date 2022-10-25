; ID: 2081
; Author: _33
; Date: 2007-07-27 15:15:49
; Title: Some date functions
; Description: Date functions I created for my console project

Function Convert_FileDate_To_Expanded$(fd$,mode%=2)
   m% = Int(Mid$(fd$,1,2))
   d% = Int(Mid$(fd$,4,2))
   y% = Int(Mid$(fd$,7,4))
   If m%>0 And d%>0 And y%>0 Then 
      Return (GetDayOfWeek$(d%,m%,y%,mode%)+ " " + GetMonthAlpha$(m%,mode%) + " " + Str$(d%) + " " + Str$(y%))
   Else
      Return (Str$(d%) + " " + Str$(y%))
   EndIf
End Function

Const Months$ = "January  Febuary  March    April    May      June     July     August   SeptemberOctober  November December "
Function GetMonthNumeric(month$)
   For i% = 1 To 12
      If Mid$(Months$,(i% * 9 - 8),3) = month$ Then Return i%
   Next
   Return 0
End Function

Function GetMonthAlpha$(month%,mode% = 2)
   If mode% = 1 Then
      Return Mid$(Months$,(month% * 9 - 8),3)
   ElseIf mode% = 2 Then
      Return Trim$(Mid$(Months$,(month% * 9 - 8),9))
   ElseIf mode% = 3 Then
      Return Mid$(Months$,(month% * 9 - 8),1)
   Else
      Return Mid$(Months$,(month% * 9 - 8),9)
   EndIf
End Function

Const Weekdays$ = "Sunday   Monday   Tuesday  WednesdayThursday Friday   Saturday "
Function GetDayOfWeek$(day,month,year, mode% = 2)
   d%=GetDayOfWeekVal%(day,month,year)
   If mode% = 1 Then
      Return Mid$(Weekdays$,(d% * 9 - 8),3)
   ElseIf mode% = 2 Then
      Return Trim$(Mid$(Weekdays$,(d% * 9 - 8),9))
   ElseIf mode% = 3 Then
      Return Mid$(Weekdays$,(d% * 9 - 8),1)
   Else
      Return Mid$(Weekdays$,(d% * 9 - 8),9)
   EndIf
End Function

Function GetDayOfWeekName$(d%, mode% = 1)
   If mode% = 1 Then
      Return Mid$(Weekdays$,(d% * 9 - 8),3)
   ElseIf mode% = 2 Then
      Return Trim$(Mid$(Weekdays$,(d% * 9 - 8),9))
   Else
      Return Mid$(Weekdays$,(d% * 9 - 8),1)
   EndIf
End Function

Const DaysInMonth$ = "312831303130313130313031"
Function GetDaysInMonth%(month%, year%)
   If month% = 2 And LeapYear(year%) Then
      Return 29
   Else
      Return Int(Mid$(DaysInMonth$,(month% * 2 - 1),2))
   EndIf
End Function

Function LeapYear(year%)
   If (year Mod 400) = 0 Then Return True
   If (year Mod 4) = 0 And (year Mod 100) <> 0 Then Return True
   Return False
End Function

Function GetDayOfWeekVal%(day,month,year)
   a = (14 - month)/12
   y = year - a
   m = month + 12*a - 2
   d = (day + y + y/4 - y/100 + y/400 + (31*m)/12)
   Return ((d Mod 7) + 1)
End Function
