; ID: 2629
; Author: neos300
; Date: 2009-12-14 15:47:05
; Title: Another Console
; Description: Part of my soon to be game engine

Type TConsole
Field Text:TList
Field cvars:TList

Method cPrint(msg$)
ListAddLast(Text, msg$)
End Method

Method RegisterCVAR(cvar$, val$)
For Local C:CVAR = EachIn cvars
	If C.name = cvar
		C.value = val
		Return 1
	EndIf
Next
Local B:CVAR
B.name = cvar
B.value = val
ListAddLast(cvars, B)
End Method

Method GetCVAR:String(nam$)
For Local C:CVAR = EachIn cvars
	If C.name = nam
		Return C.value
	EndIf
Next
Return "0"
End Method

End Type


Type CVAR
Field name$, value$
Function C:CVAR()
Return New CVAR
End Function
End Type
