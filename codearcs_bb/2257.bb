; ID: 2257
; Author: chwaga
; Date: 2008-05-29 23:41:18
; Title: Round a number
; Description: Round x to the nearest y!

Function Round( Number , N )
   If ( Number Mod N ) >= ( N *.5 )
        Number  = ( Ceil( Number / N ) + 1 ) * N
   Else
        Number = Floor( Number / N ) * N
   EndIf
   Return Number
End Function
