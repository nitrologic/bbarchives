; ID: 2288
; Author: _33
; Date: 2008-07-19 18:49:32
; Title: ROR/ROL
; Description: ROR and ROL for Blitz3D :)

Function ROR%(Value% = 0, Bits% = 0)
      Return ((Value Shr Bits) Or (Value Shl (32-Bits))) ;Rotate integer To the Right
End Function

Function ROL%(Value% = 0, Bits% = 0)
      Return ((Value Shl Bits) Or (Value Shr (32-Bits))) ;Rotate integer To the Left
End Function
