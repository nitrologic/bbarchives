; ID: 1872
; Author: Andy_A
; Date: 2006-12-04 23:42:15
; Title: FAST Sieve of Eratosthenes
; Description: Find prime numbers

;Fast Sieve of Eratosthenes
;Andy Amaya
;Nov 16,2006


	Print "Sieve of Eratosthenes"
	Print ""

	limit% = 16000000 ;Sixteen million
	
	If limit >= 16000000 Then limit = 16000000

	st% = MilliSecs()
	Dim prime%(limit)
	prime(1) = 1
	For n% = 4 To limit Step 2
		prime(n) = 1
	Next 
	For n = 3 To Sqr(limit) Step 2
		If prime(n) = 0 Then
			inc% = n+n
			i% = n*n
			While i <= limit
				prime(i) = 1
				i = i + inc
			Wend
			
		End If
	Next
	et = MilliSecs()-st
	
	
	For n = 1 To limit
		If prime(n) = 0 Then pcount% = pcount + 1
	Next




;================================================
; set showPrimes to 1 to print out primes in rows
showPrimes% = 0
numPerRow% = 10  ;ten primes per row
;************************************************
;*******  DO NOT USE WITH LARGE NUMBERS!  *******
;************************************************

;================================================
If showPrimes = 1 Then
	count = 1
	For n = 1 To limit
		If prime(n) = 0 Then
			count = count + 1
			If count <= numPerRow Then
				temp$ = temp$ + Str(n)+", "
			Else
				temp$ = temp$ + Str(n)
				Print temp$
				temp$ = ""
				count = 1
			End If
		End If
	Next
	If count < numPerRow Then
		Print temp$
	End If
End If
;================================================

	Print "There are "+pcount+" primes between 1 and "+limit
		
	
	Print ""
	Print "ET = "+et+" milliseconds"
	Print ""
	a$ = Input("Press [Enter] to Exit")
