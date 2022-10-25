; ID: 1396
; Author: Lattyware
; Date: 2005-06-12 14:20:26
; Title: ! (Factorial) Finder
; Description: Finds the factorial of a number.

Print Fact%(0)

WaitKey()
End

Function Fact%(Num)

Perms = Num

For a = 1 To Num - 1

Perms = Perms * (Num - a)

Next

If Num = 0 Then
	Return 1
Else
	Return Perms
EndIf
End Function
