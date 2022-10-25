; ID: 997
; Author: Prof
; Date: 2004-04-15 06:05:10
; Title: Get the day of the week
; Description: Returns the day of the week

; Get the day of the week example by Prof.
;
Graphics 640,480,32,2
SetBuffer BackBuffer()

Day$=GetDayOfTheWeek(15,04,2004) ; <- Put any date in here

Text 10,10,Day$
Flip
WaitKey()
End


Function GetDayOfTheWeek$(day,month,year)
 ; Returns the day of the week.
 ; day, month & year are integers i.e. 15 04 2004
  a=(14-month)/12
  y=year-a
  m=month+(12*a)-2
  d=(day+y+(y/4)-(y/100)+(y/400)+((31*m)/12))Mod 7  ;Ooouch!
  Select d
    Case 0:Weekday$="Sunday"
    Case 1:Weekday$="Monday"
    Case 2:Weekday$="Tuesday"
    Case 3:Weekday$="Wednesday"
    Case 4:Weekday$="Thursday"
    Case 5:Weekday$="Friday"
    Case 6:Weekday$="Saturday"
    Default:Weekday=""
  End Select
  Return Weekday$
End Function
