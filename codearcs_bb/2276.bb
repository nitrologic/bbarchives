; ID: 2276
; Author: GIB3D
; Date: 2008-06-19 19:09:57
; Title: Prime Number Finder
; Description: You put in a number and it returns 1 if it's a prime and 0 if it's not

Graphics 500,700,1,2

;For n = 0 To 21
;	Text 0,n*30,"n = " + n
;	If Prime(n) = 1
;		Text 20,(n*30)+10,"Is n("+n+") a prime number? Yes"
;			Else
;				Text 20,(n*30)+10,"Is n("+n+") a prime number? No"
;	EndIf
;Next

For n = 1 To 100000
	If Prime(n) = 1
		Print n
	EndIf
	If KeyHit(1) End
Next

WaitKey()

Function Prime(prime%)			
	For divide = 2 To Sqr(prime)
		If prime Mod divide = 0
			Return False
		EndIf
	Next
	
	If prime => 2
		Return True
			Else
				Return False
	EndIf
End Function

Function Prime2(prime%)
	Local Not_Primes% ; If the givin number is prime Ex: 5
					 ; then it will only return 2 Whole Numbers when it's divided by all of the numbers before it : 1 to 5
					
					 ; Everytime it returns a Whole Number, then Not_Primes = Not_Primes + 1
					 ; A prime number should only have 2 Whole Numbers and the rest are ones with a decimal point.
					
					 ; If Not_Primes only = 2 at the end, then the number is a prime number
					
		For divide% = 1 To prime
			divided_prime# = Float(prime)/divide
			If divided_prime = Int(divided_prime)
				Not_Primes = Not_Primes + 1
			EndIf
		Next
		
		If Not_Primes = 2
			Return True
				Else
					Return False
		EndIf
End Function
