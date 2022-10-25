; ID: 878
; Author: Filax
; Date: 2004-01-07 07:07:10
; Title: Extract a number under a string with SPACE
; Description: Use it for your lan coding !

Pos$="-455.12456 -23.52 12.2656"
 
Print Pos$
Print 
Print Proc_ValueDepack(Pos$,1)
Print Proc_ValueDepack(Pos$,2)
Print Proc_ValueDepack(Pos$,3)
WaitKey 
 
Function Proc_ValueDepack$(Dat$,Value)
	Local SearchValue$=""
	Local SpaceChar$=""
	Local String_Counter=1
 
 	For Decrunch = 1 To Len(Dat$)
		SpaceChar$ = Mid(Dat$,Decrunch,1)
 
		If SpaceChar$ = " " Then
			String_Counter= String_Counter + 1
  		Else If String_Counter = Value Then
	     	SearchValue$=SearchValue$+SpaceChar$
   		EndIf 
	Next
 
	Return SearchValue$
End Function
