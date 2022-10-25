; ID: 35
; Author: AL90
; Date: 2001-09-01 20:39:55
; Title: Get-Integer V1.1
; Description: Converts Hex-Strings (e.g. "$FF" or "$FCE2") in a Variable (Integer)


; GetInteger V1.1 - Written by Harald Wagner (AL90)
; Converts Hex-Strings (e.g. "$FF" or "$FCE2") in a Variable (Integer)
; You can use a String in 4,8,16 or 32-Bit form.
; (e.g. $F $FF $FFFF $FFFFFFFF And so on)
; Unremark "****" Lines for Example!


; **** value=BBGetInteger("$FCE2")
; **** Print Hex$(value)
; **** Print value

Function BBGetInteger(a$)
If Left$(a$,1)="$" Then a$=Mid$(a$,2)

For i=1 To Len(a$)
  b$=Upper$(Mid$(a$,i,1)):c=b$
  If c=0 And b$<>"0" Then c=Asc(b$)-55
  e=e*16:e=e+c
Next

Return e
End Function

