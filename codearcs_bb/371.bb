; ID: 371
; Author: Entity
; Date: 2002-07-27 12:37:17
; Title: Binary GCD
; Description: Fast GCD Algorithm

;
; Binary GCD Algorithm
;
; Ported from C, original source at:
; http://remus.rutgers.edu/~rhoads/Code/int_gcd.c
;
; This GCD algorithm is considerably faster than the classic Euclidean version
;
Function gcd( a, b )
	Local t = 0
	If a<=0 Then If a = 0 Then Return b: Else a = -a
	If b<=0 Then If b = 0 Then Return a: Else b = -b

	While( Not( ( a Or b )And 1 ) )
		a = a Shr 1
		b = b Shr 1
		t = t + 1
	Wend

	While( Not( a And 1 ) ): a = a Shr 1: Wend
	While( Not( b And 1 ) ): b = b Shr 1: Wend
	
	While a <> b
		If a>b
			a = a - b
			Repeat: a = a Shr 1: Until a And 1
		Else
			b = b - a
			Repeat: b = b Shr 1: Until b And 1
		EndIf
	Wend
	Return a Shl t
End Function
