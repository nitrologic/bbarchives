; ID: 989
; Author: Filax
; Date: 2004-04-06 11:36:14
; Title: Put some string in bank !
; Description: You can use memory like file, only for text file !

Type Abk_Bank
	Field Bank
	Field Size
	Field WritePointer
	Field ReadPointer
End Type


MyBank=ABK_CreateBank()


fileinput=ReadFile("Readme.txt")

While neo eof(fileinput)
a$=ReadLine(fileinput)
ABK_AddStringToBank(MyBank,a$)
wend

closefile fileinput


For i=1 To 10
Print "["+ABK_ReadBankLine$(MyBank)+"]"
Next

WaitKey 

Function ABK_CreateBank()
	B.Abk_Bank=New Abk_Bank
	B\Bank=CreateBank(0)
	B\Size=0
	B\WritePointer=0
	B\ReadPointer=0

	Return Handle(B)
End Function

Function ABK_AddStringToBank(BankID,StringLine$)
	B.Abk_Bank=Object.Abk_Bank(BankID)

	Local Tmp_Pointeur=B\WritePointer

	B\Size=BankSize (B\Bank)
	StringLine$=StringLine$+Chr$(13)
	
	ResizeBank B\Bank,B\Size+Len(StringLine$)*2
	
	For i=1 To Len(StringLine$)
		B\WritePointer=B\WritePointer+1
		PokeByte( B\Bank,Tmp_Pointeur+B\WritePointer,Asc(Mid$(StringLine$,I,1)))
	Next 
End Function

Function ABK_ReadBankLine$(BankID)
	B.Abk_Bank=Object.Abk_Bank(BankID)

	Local Tmp_Pointeur=B\ReadPointer

	Repeat
		B\ReadPointer=B\ReadPointer+1
		TmpByte=PeekByte(B\Bank,Tmp_Pointeur+B\ReadPointer)

		If TmpByte<>13 Then
			Extract$=Extract$+Chr$(TmpByte)
		Else
			Return Extract$
		EndIf
	Forever
End Function

Function ABK_ClearBank(BankID)
	B.Abk_Bank=Object.Abk_Bank(BankID)
	ResizeBank B\Bank,0
End Function

Function ABK_KillBank(BankID)
	B.Abk_Bank=Object.Abk_Bank(BankID)
	FreeBank B\Bank
	Delete B
End Function
