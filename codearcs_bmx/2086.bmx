; ID: 2086
; Author: xlsior
; Date: 2007-08-06 02:31:05
; Title: Leap years
; Description: Recognize Leap Years

'
' IsLeap - Returns TRUE for Leap Years, FALSE for non-Leap Years
'
' By Marc van den Dikkenberg
'
Strict

For Local Year:Int=1890 To 1920
	Print "Year "+year+" "+IsLeap(Year)
Next

Function IsLeap(SomeDate:String)   ' Pass either just the year, or a "DD MMM YYYY" BlitzMax data string
	Local Jaar:Int=Int(Right$(SomeDate,4))
		If ((Jaar Mod 4) = 0 And (Jaar Mod 100) <> 0) Or ((Jaar Mod 4) = 0 And (Jaar Mod 400) = 0) Then 
		' Leap Year	-- Any year divisible by 4, except the centuries unless they are multiples of 400
		Return True
	Else
		' Not a Leap Year
		Return False
	End If
End Function
