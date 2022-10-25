; ID: 104
; Author: Unknown
; Date: 2001-10-15 01:37:58
; Title: Val function
; Description: Converts a string to an integer (Decimal)

; Val Function v0.1 By The Prof - For Blitzers Everywhere!
;
T$="109876":V=Val(T$):Print V     ; example 1

V=Val("12345"):Print V            ; example 2

Print Val("02468")                ; example 3

Print Val("Blitz Basic")          ; example 4

Print Val("Blitz 123 Basic")      ; example 5

WaitKey
End

; **************************************************************
;
Function val(txt$)
  ; Val v0.1 By The Prof - Last Compiled 14-10-01 - 22:00
  ; String to Integer function (Decimal).
  ; 
  t$=Upper$(txt$):l=Len(t$):Power=1
  For Pos=L To 1 Step-1
      V=Asc(Mid$(t$,Pos,1))-48
      If (V>-1) And (V<10)        ; Ensure a Zero for letters
         Value=Value+(V*Power)
         Power=Power*10
      End If
  Next
  Return Value
End Function
;
; **************************************************************
