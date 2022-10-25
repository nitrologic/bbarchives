; ID: 1878
; Author: Nebula
; Date: 2006-12-11 22:03:38
; Title: Random ip generator
; Description: Generates ip numbers (-1. flag)

;
; Ip generator ()
; Spider ping list builder
; Made for a server simulation game
; Cromdesign
DebugLog ip_generator(-1,0,0,0)
DebugLog ip_generator(-1,Rand(0,999),7,0)
;
Function ip_generator$(a,b,c,d)
	; -1 = random
	Select a
		Case -1	
		a = Rand(0,999)
	End Select
	Select b	
		Case -1
		b = Rand(0,999)
	End Select
	Select c	
		Case -1
		c = Rand(0,999)
	End Select	
	Select d	
			Case -1
			d = Rand(0,999)
	End Select	
	out$ = a+"."+b+"."+c+"."+d
	Return out$
End Function
