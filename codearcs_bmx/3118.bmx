; ID: 3118
; Author: GW
; Date: 2014-04-04 10:06:31
; Title: Weighted Random Numbers
; Description: 3 simple methods for weighted random numbers.

SuperStrict 
Framework brl.Basic
Import brl.map


Print("wrand1")
	Dump(wrand1, 1,10)
Print("wrand2")
	Dump(wrand2, 1,10)
Print("wrand3")
	Dump(wrand3, 1,10)


'-------------------------------------------------------------------------------------
Function wRand1%(a%,b%)
	Const SCALE#=2	'1.1 .. 2 is good
	Return (Rnd()^scale) * (b - a + 1) + a
End Function
'-------------------------------------------------------------------------------------
Function wRand2%(a%,b%)
	'Return Rand(a,Rand(a,Rand(a,b))) '//More Weight
	Return Rand(a,Rand(a,b))
End Function
'-------------------------------------------------------------------------------------
Function wRand3%(a%,b%)
	'Return Min(Min(Rand(a,b),Rand(a,b)),Rand(a,b)) '//More weight
	Return Min(Rand(a,b),Rand(a,b))
End Function
'-------------------------------------------------------------------------------------
Function Dump(r%(a%,b%),a#,b#)
	Local map:tmap = CreateMap()
	For Local i% = 0 Until 1000
		Local rs$ = r(a,b)
		If MAP.contains(rs) Then
			map.insert(String(rs), String(Int(String(map.valueforkey(rs)))+1))
		Else
			map.insert(rs,"1")
		EndIf
	Next
	For Local i% = a To b
		Print i + "~t:" + String(map.valueforkey(String(i)))
	Next	 
	Print "~n"
End Function
'-------------------------------------------------------------------------------------
