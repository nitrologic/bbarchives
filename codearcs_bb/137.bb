; ID: 137
; Author: dan_upright
; Date: 2001-11-14 15:19:30
; Title: new trim$ function
; Description: fixes the trim$ 'bug'

Function new_trim$(t$)

	If t$ = " " Then t$ = ""
	If Len(t$) > 0
		Repeat
			a = a + 1
			c$ = Mid$(t$,a,1)
		Until (c$ <> " ") Or (a = Len(t$))
		b = Len(t$) + 1
		Repeat
			b = b - 1
			c$ = Mid$(t$,b,1)
		Until (c$ <> " ") Or (b = 1)
		
		Return Mid$(t$,a,(b - (a - 1)))
	Else
		Return ""
	EndIf

End Function
