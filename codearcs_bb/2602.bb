; ID: 2602
; Author: Malice
; Date: 2009-11-01 09:21:38
; Title: Safer Millisecs() Interval
; Description: Obtains time intervals even over the 'Integer-Limit rollover'

While Not KeyDown(1)
	If (KeyHit(57))
		Print Str(ElapsedSince%(nLast%))
		nLast%=MilliSecs()
	End If
Wend

Function ElapsedSince%(nLast%)
	FlushKeys()
	Local nMilli%=MilliSecs()
	Local nReturn%
	
	If ((Sgn(nLast%))<>(Sgn(nMilli)))
		nReturn=Abs(nLast)+Abs(nMilli)
	Else
		nReturn=nMilli-nLast%
	End If
	Return Abs(nReturn%)
End Function
