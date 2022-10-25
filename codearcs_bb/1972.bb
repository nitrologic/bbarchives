; ID: 1972
; Author: Diego
; Date: 2007-03-21 18:47:56
; Title: Base Converter
; Description: Converts a Number from any Base to any other Base

Const BaseCharset$ = " 0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"

 ; Base Converter by Diego Semmler
 ; www.dsemmler.de
 ; Leider funktioniert der Zahlenumwandler z.Z. nur im Bereich 0 bis unter 2^31
 ; I'm sorry the base converter works currently ony between 0 and less 2^31

 ; Beispiel | Example
Print ConvertBase$("4311", 5, 14)
Print ConvertNumber$("0xFF")
WaitKey

Function ConvertNumber$(Number$)
If Left(Number$, 2) = "0x" Then ; Hexadezimale Zahl | Hexadecimal number
	Base% = 16
	Number$ = Mid(Number$, 3, 8)
	EndIf
If Left(Number$, 1) = "#" Then
	Base% = 16
	Number$ = Mid(Number$, 2, 8)
	EndIf
If Left(Number$, 1) = "0" Then ; Oktalzahl | Octal number
	Base% = 8
	Number$ = Mid(Number$, 2, 11)
	EndIf
If Base% <> 0 Then Number$ = ConvertBase(Number$, Base%, 10)
Return Number$
End Function

Function ConvertBase$(Number$, FromBase%, ToBase%)
If FromBase% < 2 Or FromBase% > 36 Or ToBase% < 2 Or ToBase% > 36 Return Number$ ; Falsche Basis | Wrong Base
For I% = 1 To Len(Number$) ; Wandle Zahl in Integer um | Convert number to integer
	Val% = Instr(BaseCharset$, Mid(Number$, I%, 1)) - 2
	If Val% < 0 Or Val% >= FromBase% Then Exit ; Falsches Zeichen - Aktion wird abgebrochen | Wrong character - action will be aborted
	Buffer% = Buffer% + Val% * FromBase% ^ (Len(Number$) - I%)
	Next
If Buffer% < 0 Return Number$
Number$ = ""
Repeat ; Wandle Integer in Zahl um | Convert integer to number
	Val% = Buffer% Mod ToBase%
	Buffer% = Floor(Buffer% / ToBase%)
	Number$ = Mid(BaseCharset$, Val% + 2, 1) + Number$
	Until Buffer% <= 0
Return Number$
End Function
