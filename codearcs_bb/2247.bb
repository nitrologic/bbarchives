; ID: 2247
; Author: Diego
; Date: 2008-04-23 20:44:29
; Title: FormatOut
; Description: Print a string with colors, returns tabs an so on | Zeichenkette mit Farben, Return, Tabulator u.s.w. ausgeben

; Examples | Beispiele
FormatOut "This is a\#F00000 c\#D00020o\#B00040l\#900060o\#700080r\#5000A0f\#3000C0u\#1000E0l\#0000F0l\#FFFFFF output."
FormatOut "To insert a return use \\n\nand \\t\t to insert a tab."
FormatOut "And \$5C\$24\$48\$48 to insert every character you want."
WaitKey

Function FormatOut(OutStr$, NoReturnAtEnd% = 0, TabLength% = 6)
Local I%, Buffer$, LineLength%
For I% = 1 To Len(OutStr$)
	If Mid(OutStr$, I%, 1) = "\" Then
		I% = I% + 1
		Select Mid(OutStr$, I%, 1)
			Case "\"
				Buffer$ = Buffer$ + "\"
			Case "n"
				Print Buffer$
				LineLength% = 0
				Buffer$ = ""
			Case "t"
				Buffer$ = LSet(Buffer$, (LineLength% + Len(Buffer$) + TabLength%) / TabLength% * TabLength% - LineLength%)
			Case "#"
				Write Buffer$
				LineLength% = LineLength% + Len(Buffer$)
				Buffer$ = ""
				Color 0, 0, Hex2Dec(Mid(OutStr$, I% + 1, 6))
				I% = I% + 6
			Case "$"
				Buffer$ = Buffer$ + Chr(Hex2Dec(Mid(OutStr$, I% + 1, 2)))
				I% = I% + 2
			Default
				Buffer$ = Buffer$ + Mid(OutStr$, I% - 1, 2)
			End Select
		Else
		Buffer$ = Buffer$ + Mid(OutStr$, I%, 1)
		EndIf
	Next
Write Buffer$
If Not NoReturnAtEnd% Then Print ""
End Function

Function Hex2Dec(Number$)
Local Ret%, BaseCharset$ = " 0123456789ABCDEF"
For I% = 1 To Len(Number$) ; Wandle Zahl in Integer um | Convert number to integer
	Val% = Instr(BaseCharset$, Mid(Number$, I%, 1)) - 2
	If Val% < 0 Or Val% >= 16 Then Exit ; Falsches Zeichen - Aktion wird abgebrochen | Wrong character - action will be aborted
	Ret% = Ret% Or Val% Shl (Len(Number$) - I%) * 4
	Next
Return Ret%
End Function
