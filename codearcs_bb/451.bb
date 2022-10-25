; ID: 451
; Author: Imphenzia
; Date: 2002-10-06 18:34:02
; Title: Julian Days conversion
; Description: Very useful for day calculations

Dim Months$(12)
Months(1)="JAN":Months(2)="FEB":Months(3)="MAR":Months(4)="APR":Months(5)="MAY":Months(6)="JUN": Months(7)="JUL": Months(8)="AUG": Months(9)="SEP": Months(10)="OCT": Months(11)="NOV": Months(12)="DEC"

Print "Days between today and 23 Aug 2008: " + (JulianDays("23 AUG 2008") - JulianDays(CurrentDate()))

Function FindMonth(fm$)
  For i=1 To 12
  If Upper(fm$)=months$(i) Then Return i
  Next
End Function

Function JulianDays(txt$)
  d=Int(Left(txt$,2))
  m=Int(FindMonth(Mid(txt$,4,3)))
  y=Int(Right(txt$,4))
  jd=( 1461 * ( y + 4800 + ( m - 14 ) / 12 ) ) / 4 + ( 367 * ( m - 2 - 12 * ( ( m - 14 ) / 12 ) ) ) / 12 - ( 3 * ( ( y + 4900 + ( m - 14 ) / 12 ) / 100 ) ) / 4 + d - 32075
  Return jd
End Function
