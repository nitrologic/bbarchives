; ID: 2264
; Author: boomboom
; Date: 2008-06-09 14:12:32
; Title: DateInt()
; Description: Converts date to int. Useful to stop a program running after a certain date.

;Stops program running after a certain date
If DateInt(CurrentDate()) >= DateInt("09 Jun 2008") End

;Stops Program running before certain date
;If DateInt(CurrentDate()) < DateInt("09 Jun 2008") End

;Program can only run on certain date
;If DateInt(CurrentDate()) <> DateInt("09 Jun 2008") End

Function DateInt%(Date$)

	;Local Variables -----
	Local D$
	Local M$
	Local Y$
	Local DateResult%
	;=====================

	;Get Day
	D = Left(Date,2)
	
	;Get Month
	Select Mid$(Date,4,3)
		Case "Jan" M = "01"
		Case "Feb" M = "02"
		Case "Mar" M = "03"
		Case "Apr" M = "04"
		Case "May" M = "05"
		Case "Jun" M = "06"
		Case "Jul" M = "07"
		Case "Aug" M = "08"
		Case "Sep" M = "09"
		Case "Oct" M = "10"
		Case "Nov" M = "11"
		Case "Dec" M = "12"
	End Select
	
	;Get Year
	Y = Right(Date,4)
	
	;Result Date
	DateResult = Int (Y + M + D)

	;Return Result Date
	Return DateResult

End Function
