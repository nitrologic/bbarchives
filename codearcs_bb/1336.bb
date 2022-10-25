; ID: 1336
; Author: Andres
; Date: 2005-03-23 11:26:56
; Title: Allowed letters
; Description: How to avoid unwanted letters/symbols

Print "Waiting for correct letter to end..."

AllowedLetters$ = "ABCDEFGHIJKLMNOPQRSTUVWXY"

While Not Instr(AllowedLetters$, key$)
    key$ = Chr$(GetKey())
Wend
End
