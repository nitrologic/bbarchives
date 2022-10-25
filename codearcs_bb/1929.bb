; ID: 1929
; Author: Leon Drake
; Date: 2007-02-19 01:44:05
; Title: Currentday Currenttime Currentyear
; Description: 3 easy functions for getting the date seperated

Function currentyear(date$=Currentdate())

Local tstr$,tyear
tstr$ = Mid(date$,8,4)
tstr$ = Trim(tstr$)
tyear = Int(tstr$)
Return tyear


End Function

Function currentday(date$=Currentdate())
Local tstr$,tday
tstr$ = Mid(date$,1,2)
tstr$ = Trim(tstr$)
tday = Int(tstr$)
Return tday

End Function


Function currentmonth(date$=Currentdate())

Local tstr$,tmonth

tstr$ = Mid(date$,4,3)
tstr$ = Trim(tstr$)
	Select tstr$
	
	Case "Jan"
	tmonth = 01
	
	Case "Feb"
	tmonth = 02
	
	Case "Mar"
	tmonth = 03

	Case "Apr"
	tmonth = 04

	Case "May"
	tmonth = 05

	Case "Jun"
	tmonth = 06

	Case "Jul"
	tmonth = 07

	Case "Aug"
	tmonth = 08

	Case "Sep"
	tmonth = 09

	Case "Oct"
	tmonth = 10

	Case "Nov"
	tmonth = 11

	Case "Dec"
	tmonth = 12
	
	Default 
	tmonth = 0
	
	End Select
Return tmonth

End Function
