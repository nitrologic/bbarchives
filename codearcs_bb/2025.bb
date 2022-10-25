; ID: 2025
; Author: Diego
; Date: 2007-06-01 15:53:55
; Title: Split
; Description: Splits a string into pices by a seperator / Teilt einen String in Stücke mithilfe eines Trenners

Global Split_String$, Split_Seperator$, Split_Position%

; Example / Beispiel
Print Split("123:56:789", ":")
Print SplitNext()
Print SplitNext()
WaitKey

Function Split$(SpStr$, SpSeperator$)
Split_String$ = SpSeperator$ + SpStr$
Split_Seperator$ = SpSeperator$
Split_Position% = 1
Return SplitNext()
End Function

Function SplitNext$()
Local Split_Old_Position% = Split_Position% + Len(Split_Seperator$)
If Split_Position% = 0 Then Return ""
Split_Position% = Instr(Split_String$, Split_Seperator$, Split_Old_Position%)
Return Mid(Split_String$, Split_Old_Position%, Split_Position% - Split_Old_Position%)
End Function
