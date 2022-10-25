; ID: 2402
; Author: Plash
; Date: 2009-01-26 06:52:23
; Title: Number sequences and special ratios
; Description: Algorithms/Formulas for number sequences (Fibonacci, Lucas, etc.), the Golden and Silver ratios, and an IsPrime function

Rem
Description: Some number sequence functions (Lucas, Fibonacci, Perrin, Pell and Padovan), a fast IsPrime function
			and the Golden and Silver ratios.
Author: Plash
Credits: Toby Herring for the IsPrime function (see function header down yonder)
		All other formulae is taken from Wikipedia.
End Rem

' (Phi) The Golden Ratio; "extreme and mean ratio"
' http://en.wikipedia.org/wiki/Golden_ratio
Global golden_ratio:Double = 1.6180339887498949		' Algorithm: (1 + Sqr(5)) / 2

' (DeltaS) The Silver Ratio
' http://en.wikipedia.org/wiki/Silver_ratio
Global silver_ratio:Double = 2.4142135623730949		' Algorithm: (1 + Sqr(2))
' The inverse (?) of the Silver Ratio (used in the closed form Pell numbers formula)
Global isilver_ratio:Double = -0.41421356237309503	' Algorithm: (1 - Sqr(2))


' Fast prime-finder algorithm by Toby Herring; converted and adopted from: 
' http://www.freevbcode.com/ShowCode.asp?ID=1059
Function IsPrime:Int(testprime:Long)
	' Going by the Wiki prime number list, and eliminating even numbers
	If (testprime < 2) Or (testprime Mod 2) = 0 Then Return False Else If testprime = 2 Then Return True
	
	' Loop through odd numbers starting with 3
	Local testnum:Long = 3
	Local testlimit:Long = testprime
	While (testlimit > testnum)
		If (testprime Mod testnum) = 0    
			Return False
		End If
		testlimit = testprime / testnum ' There's logic to this. Think about it.
		testnum:+ 2 ' Only check odd numbers
	End While
	Return True
End Function

' The Lucas numbers (http://en.wikipedia.org/wiki/Lucas_number)
Function LucasSequence:Long[](count:Int)
	If count = 0 Then Return Null
	Local L:Long[] = New Long[count]
	For Local n:Int = 0 Until count
		If n = 0
			L[n] = 2
		Else If n = 1
			L[n] = 1
		Else
			L[n] = L[n - 1] + L[n - 2]
			' Or L[n] = (golden_ratio^n) + ((1 - golden_ratio)^n)
		End If
	Next
	Return L
End Function

' The Fibonacci numbers (http://en.wikipedia.org/wiki/Fibonacci_number)
Function FibonacciSequence:Long[](count:Int)
	If count = 0 Then Return Null
	Local F:Long[] = New Long[count]
	For Local n:Int = 0 Until count
		If n < 2
			F[n] = n ' F[0] = 0; F[1] = 1
		Else
			F[n] = F[n - 1] + F[n - 2]
		End If
	Next
	Return F
End Function

' The Perrin numbers (http://en.wikipedia.org/wiki/Perrin_number)
Function PerrinSequence:Long[](count:Int)
	If count = 0 Then Return Null
	Local P:Long[] = New Long[count]
	For Local n:Int = 0 Until count
		' P[0] = 3, P[1] = 0, P[2] = 2
		If n = 0
			P[n] = 3
		Else If n = 1
			P[n] = 0
		Else If n = 2
			P[n] = 2
		Else
			P[n] = P[n - 2] + P[n - 3]
		End If
	Next
	Return P
End Function

' The Pell numbers (http://en.wikipedia.org/wiki/Pell_number)
Function PellSequence:Long[](count:Int)
	If count = 0 Then Return Null
	Local P:Long[] = New Long[count]
	For Local n:Int = 0 Until count
		' P[0] = 0, P[1] = 1
		If n < 2
			P[n] = n
		Else
			P[n] = 2 * (P[n - 1]) + P[n - 2]
			' Or P[n] = ((silver_ratio^n) - (isilver_ratio^n)) / 2^2
		End If
	Next
	Return P
End Function

' The Padovan sequence (http://en.wikipedia.org/wiki/Padovan_sequence)
Function PadovanSequence:Long[](count:Int)
	If count = 0 Then Return Null
	Local P:Long[] = New Long[count]
	For Local n:Int = 0 Until count
		If n < 3
			P[n] = 1 ' P[0 to 2] = 1
		Else
			P[n] = P[n - 2] + P[n - 3]
		End If
	Next
	Return P
End Function
