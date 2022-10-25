; ID: 1824
; Author: daaan
; Date: 2006-09-27 07:22:00
; Title: Twin Primes
; Description: How fun!

Rem
**********************************************************
twin primes by daniel wooden
**********************************************************
End Rem

Local count:Int = 1
Local curnum:Int = 3

Local a:Int = 0
Local b:Int = 0

While count < 101 ' <-- twin primes upto the 100th place.
	
	If IsPrime( curnum ) And IsPrime( curnum+2 ) Then
		a = curnum
		b = curnum+2
		Print "Twin Prime Set: " + count + " (" + a + "," + b + ")"
		count :+ 1
	End If
	
	curnum :+ 1
	
Wend
End

Function IsPrime:Int( Num:Int )
	
	Local Prime:Int = True
	
	For i = 2 To Num/2
		If (Num Mod i) = 0 Then
			Prime = False
		End If
	Next
	
	Return Prime
	
End Function
