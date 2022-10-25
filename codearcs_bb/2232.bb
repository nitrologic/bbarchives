; ID: 2232
; Author: Diego
; Date: 2008-03-16 17:49:18
; Title: Probability_Calculation
; Description: The Functions Fac, Binom, Bernoulli and Cumulated Bernoulli

; Examples | Beispiele
Print Fac(6) ; = 720 = 6 * 5 * 4 * 3 * 2 * 1
Print Binom(49, 6)
Print CumulatedBernoulli(100, .5, 0, 100) ; = 1 (Sum of probability of all possibilities | Summe der Wahrscheinlichkeit aller Möglichkeiten)
WaitKey

Function Fac%(n%) ; Fakultät
Local I%, F%
If n% < 0 Return 0 ; Fac(n), n < 0 is undefined and retuns zero | ist undefiniert und gibt null zurück
F% = 1
For I% = 2 To n%
	F% = F% * I%
	Next
Return F%
End Function

Function Binom%(n%, k%) ; a über b
Local F# = BinomFloat(n%, k%)
If F# > 2147483647 Then RuntimeError "Das Ergebnis ist zu groß. Bitte benutzen Sie die Float-Version!"
Return Int(F#)
End Function

Function BinomFloat#(n%, k%) ; a über b
Local I%, F#
If n% < k% Then Return 0.0 ; is undefined and retuns zero | ist undefiniert und gibt null zurück
If k% > n% / 2 Then Return BinomFloat#(n%, n% - k%)
F# = 1.0
For I% = 0 To k% - 1
	F# = F# * (n% - I%) / (k% - I%)
	Next
Return F#
End Function

Function Bernoulli#(n%, p#, k%) ; 0 <= p# <= 1
Return BinomFloat#(n%, k%) * p# ^ k% * (1 - p#) ^ (n% - k%)
End Function

Function CumulatedBernoulli#(n%, p#, FirstK%, LastK%)
Local Pc#, K%
For K% = FirstK% To LastK%
	Pc# = Pc# + Bernoulli#(n%, p#, K%)
	Next
Return Pc#
End Function
