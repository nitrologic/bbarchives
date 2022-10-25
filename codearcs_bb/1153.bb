; ID: 1153
; Author: Nilium
; Date: 2004-09-03 19:21:25
; Title: Push/Pop Data Functions
; Description: Functions to push and pop data to and from banks

Function PushObject(Bank,Value)
	Position = BankSize(Bank)
	ResizeBank(Bank,Position+4)
	PokeInt(Bank,Position,Value)
	Return Position/4
End Function

Function PopObject(Bank)
	R = PeekInt(Bank,BankSize(Bank)-4)
	PokeInt(Bank,BankSize(Bank)-4,0)
	ResizeBank(Bank,BankSize(Bank)-4)
	Return R
End Function

Function PushFloat(Bank,Value#)
	Position = BankSize(Bank)
	ResizeBank(Bank,Position+4)
	PokeFloat(Bank,Position,Value)
	Return Position/4
End Function

Function PopFloat#(Bank)
	R# = PeekFloat(Bank,BankSize(Bank)-4)
	PokeInt(Bank,BankSize(Bank)-4,0)
	ResizeBank(Bank,BankSize(Bank)-4)
	Return R#
End Function

Function PushString(Bank,Text$)
	Position% = BankSize(Bank)
	ResizeBank(Bank,BankSize(Bank)+(Len(Text$)+1))
	PokeByte(Bank,Position,0)
	For n = 1 To Len(Text$)
		PokeByte(Bank,Position+n,Asc(Mid(Text$,n,1)))
	Next
	Return Position/4
End Function

Function PopString$(Bank)
	n = BankSize(Bank)-1
	Repeat
		K = PeekByte(Bank,n)
		If K > 0 Then Text$ = Chr(K)+Text$
		n = n - 1
	Until K = 0
	ResizeBank(Bank,n+1)
	Return Text$
End Function

Function EraseObject(Bank,Index)
	Size = BankSize(Bank)
	If Index <= Size Then
		R% = GetObject(Bank,Index)
		If Index = Size Then
			PokeInt(Bank,(Index)*4,0)
			ResizeBank(Bank,Size-4)
		Else
			n = Index * 4
			While n < Size - 4
				PokeInt(Bank,n,PeekInt(Bank,n+4))
				n = n + 4
			Wend
			ResizeBank(Bank,Size-4)
		EndIf
	EndIf
	Return R
End Function

Function GetObject(Bank,Index)
	Return PeekInt(Bank,Index*4)
End Function

Function GetFloat(Bank,Index)
	Return PeekFloat(Bank,Index*4)
End Function

;; The name on this one is a bit vague- it returns the amount of 4-byte objects in a bank.  Of course, it's just a BankSize()/4, but it saves time typing
;; Does not work if you've pushed a string onto the bank
Function Objects(Bank)
	Return BankSize(Bank)/4
End Function
