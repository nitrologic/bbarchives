; ID: 1888
; Author: Subirenihil
; Date: 2006-12-24 16:01:03
; Title: Calculating Primes
; Description: The fastest way to calculate prime numbers

;If you want the primes for something, they are stored in the same folder as the program under "Primes.dat"
;The primes are stored as Ints with an 8-byte header
;The header consists of two Ints, the first is the last number tested, the second is the prime that is being searched for.
;Sorry for not commenting although it should be fairly simple.
;
;I can calculate the first 10,000,000 prime numbers within half an hour
;The data file can get somewhat large if you calculate large quantities of primes - 10,000,000 primes takes up about 40MB of harddrive

Graphics 1024,768,16,1
SetBuffer BackBuffer()
Global primestofind=100000000
If primestofind>2000000000 Then primestofind=2000000000

p=2

n=4
Text 512,384,"Loading...",1,1
Flip
file=ReadFile("Primes.dat")
If file<>0
	n=ReadInt(file)
	p=ReadInt(file)
EndIf
;txt$=ReadLine(file)
;primestofind=primestofind+p
Dim primes(primestofind+p)
If n=0 Or p=0 Or file=0
	p=2
	n=4
	primes(0)=2
	primes(1)=3
Else
	For a=0 To p-1
		primes(a)=ReadInt(file)
	Next
EndIf
CloseFile file

If primestofind>p
	x=False
	pp=-1
	t=0
	Repeat
		If p Mod 1000=0 And pp<p
			Cls
			Text 512,384,"I have found "+p+" primes so far and checked through "+(n-1)+".",1,1
			If MilliSecs()-t>=30 Then Flip
			t=MilliSecs()
			pp=p
		EndIf
		sqrt=Floor(Sqr#(n))
		prime=True
		For a=0 To sqrt
			If primes(a)>sqrt
				Exit
			ElseIf n Mod primes(a)=0
				prime=False
				Exit
			EndIf
			If KeyHit(1) Then x=True
			If x=True Then Exit
		Next
		If x=True
			prime=False
			n=n-1
			Exit
		EndIf
		If prime=True
			primes(p)=n
			p=p+1
		EndIf
		n=n+1
		If n=2147483647
			Cls
			Text 512,384,"The "+p+"th prime number ("+primes(p-1)+") is at the maximum for the int variable type.  Hit Enter to save and exit."
			Flip
			Repeat:Until KeyHit(28) Or KeyHit(156)
			Exit
		EndIf
	
	Until p=primestofind Or x=True
	
	Cls
	Text 512,384,"Saving...",1,1
	Flip
	file=WriteFile("Primes.dat")
	WriteInt file,n
	WriteInt file,p
	;WriteInt file,primes(p-1)
	For a=0 To p-1
		WriteInt file,primes(a)
	Next
	CloseFile file
EndIf

Cls
If p>=10 Then 			Text 0,0,	"The tenth prime number is:              "+primes(9),0,0
If p>=100 Then 			Text 0,20,	"The hundredth prime number is:          "+primes(99),0,0
If p>=1000 Then 		Text 0,40,	"The thousandth prime number is:         "+primes(999),0,0
If p>=10000 Then 		Text 0,60,	"The ten-thousandth prime number is:     "+primes(9999),0,0
If p>=100000 Then 		Text 0,80,	"The hundred-thousandth prime number is: "+primes(99999),0,0
If p>=1000000 Then 		Text 0,100,	"The millionth prime number is:          "+primes(999999),0,0
If p>=10000000 Then		Text 0,120,	"The ten-millionth prime number is:      "+primes(9999999),0,0
If p>=100000000 Then 	Text 0,140,	"The hundred-millionth prime number is:  "+primes(99999999),0,0
If p>=1000000000 Then 	Text 0,160,	"The billionth prime number is:          "+primes(999999999),0,0
Flip
WaitKey
End
