; ID: 2549
; Author: RifRaf
; Date: 2009-07-28 00:20:52
; Title: Tracking multiplayer objects
; Description: Easy way to track objects over a network

Function Create_UniqueName$(time=5,header$="")
unq$=Mid$(Str$(MilliSecs()),time,time)
NowLen=Len(unq$)
needed=time-nowlen
If needed>0 Then 
	For i=1 To needed
		unq$=unq$+Chr$(Rand(32,120))
	Next
EndIf
Return header$+unq$
End Function
