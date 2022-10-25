; ID: 3013
; Author: apo
; Date: 2012-12-28 13:47:27
; Title: compact date time string
; Description: 14 digit string of year+month+day+hh+mm+ss

Function time_now$()  ' returns a 14 digit string of year+month+day+hour+min+sec

  Local cd$, ct$, month1$, month2$
		
  month1$ = "ANEBARPRAYUNULUGEPCTOVEC"  ' last 2 letters of each month name
  month2$ = "010203040506070809101112"  ' 2 digit equivalent
  cd$ = CurrentDate$()
  ct$ = CurrentTime$()
  ' the month is converted from CurrentDate$ to a 2 digit string
													
  Return Mid$(cd$, 8, 4) + Mid$(month2$, Instr(month1$, Upper$(Mid$(cd$, 5, 2))), 2) + ..
         Mid$(cd$, 1, 2) + Mid$(ct$, 1, 2) + Mid$(ct$, 4, 2) + Mid$(ct$, 7, 2)
EndFunction
