; ID: 2303
; Author: schilcote
; Date: 2008-08-22 21:44:14
; Title: Factor Calculator
; Description: Calculates factors

Global FCBnk
Function factor (Num)
DebugLog"Factor calculating function called"
FCBnk=CreateBank (1024)
Fact2=1
Repeat
Rep=Rep+1
Fact=Fact+1
;Detect need for change in second number
If Fact=num Then
Fact2=Fact2+1
Fact=1
EndIf
DebugLog "Checking For Factor, "+Fact+" * "+Fact2
;Detect factor
Test=Fact*Fact2
If Test=Num Then 
PokeInt FCBnk,Ofst,Fact
DebugLog "Factor Calculated,"+Fact
ofst=ofst+3
EndIf
Until (Test=Num And Fact=1)
DebugLog"Factor of 1 reached, end computation."
End Function
