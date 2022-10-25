; ID: 1859
; Author: Zenn Lee
; Date: 2006-11-09 20:00:33
; Title: LtoRLook()
; Description: Super Simple. Great for use with RequestFile()

Function LtoRLook:String(Str$,Char$)
 L = Len(Str$)
 While Not L = 0 
  L = L - 1
  ReturnStr$ = Left(Str$,L)
  If Mid(ReturnStr$,L,1) = Char$
   Return ReturnStr$
   L = 0
  End If
 Wend
End Function
