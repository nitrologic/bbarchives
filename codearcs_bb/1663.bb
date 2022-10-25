; ID: 1663
; Author: CS_TBL
; Date: 2006-04-12 11:01:23
; Title: Box, Box2 (bb &amp; bmax)
; Description: extended modulos

bmax:

Function Box:Int(value:Int,modulo:Int)
	If modulo<1 modulo=1
	Return ((value Mod modulo)+modulo) Mod modulo
End Function

Function Box2:Int(value:Int,lo:Int,hi:Int)
	Local o:Int
	If lo>hi
		o=lo
		lo=hi
		hi=o
	EndIf
	Local Modulo:Int=hi-lo
	value:-lo
	If modulo<1 modulo=1
	Return lo+((value Mod modulo)+modulo) Mod modulo
End Function


non-bmax:

Function Box(value,modulo)
	If modulo<1 modulo=1
	Return ((value Mod modulo)+modulo) Mod modulo
End Function

Function Box2(value,lo,hi)
	If lo>hi
		o=lo
		lo=hi
		hi=o
	EndIf
	Modulo=hi-lo
	value=value-lo
	If modulo<1 modulo=1
	Return lo+((value Mod modulo)+modulo) Mod modulo
End Function
