; ID: 298
; Author: Craig Kiesau
; Date: 2002-04-19 07:48:30
; Title: Julian day functions
; Description: Calculate the Julian day for use with date calculations

Print BlitzDateToJulianDay("12 Dec 2002") - BlitzDateToJulianDay(CurrentDate$()) + " Days until my birthday"
WaitKey()

Function BlitzDateToJulianDay(datestr$)
    ;break up a blitz style date into d/m/y then get the JD
    dy = Left(datestr$, Instr(datestr$, " ") - 1)
    yr = Mid(datestr, Instr(datestr, " ",Instr(datestr$, " ")+1)+1, 4)
    mon$ = Mid(datestr, Instr(datestr, " ")+1, 3)

    Select Upper(mon)
        Case "JAN"
            mn = 1
        Case "FEB"
            mn = 2
        Case "MAR"
            mn = 3
        Case "APR"
            mn = 4
        Case "MAY"
            mn = 5
        Case "JUN"
            mn = 6
        Case "JUL"
            mn = 7
        Case "AUG"
            mn = 8
        Case "SEP"
            mn = 9
        Case "OCT"
            mn = 10
        Case "NOV"
            mn = 11
        Case "DEC"
            mn = 12
    End Select

 	Return JulianDay%(dy, mn, yr)	
End Function

Function JulianDay%(d%, m%, y%)

	If m% < 3 Then
      m% = m% + 12
      y% = y% - 1
	End If
	
	Return d% + Floor(153 * m% - 457) / 5 + 365 * y% + Floor(y% / 4) - Floor(y% / 100) + Floor(y% / 400) + 1721118.5
End Function
