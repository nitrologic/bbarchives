; ID: 124
; Author: Oldefoxx
; Date: 2001-11-04 11:54:42
; Title: A flexable VAL() Function
; Description: This is an updated version

AppTitle "Emulating a VA() Function
;  by Donald R. Darden, 2001
;  The main program generates sample strings of
;  digits and shows what happens when you convert
;  these values to a float value.

While Not KeyHit(1)
  sample$=""
  For a=1 To 5
    sample$=sample$+Chr$(Rand(48,57))
  Next
  If Rnd(0,1) And Len(sample)>3 Then
    sample$=Left$(sample$,Len(sample$)-2)+"."+Right$(sample$,2)
  EndIf
  Print Chr$(34)+sample$+Chr$(34)+" = "+val#(sample$)
  WaitKey
Wend
End

Function val#(sstring$)
Local temp#=0
Local decimal=0
Local sign=1
Local a
Local b
Local c
Local base=10
a=Instr(sstring$,"-",1)
If a Then negative=-1
b=Instr(sstring$,"&",a+1)
If b Then
  Select Mid$(sstring$,a+1,1)
  Case "B", "b"
    base=2
    a=b+1
  Case "O", "o"
    base=8
    a=b+1
  Case "H", "h"
    base=16
    a=b+1
  Default
    base=10
  End Select
End If
decimal=0
For b=a+1 To Len(sstring$)
  c=Asc(Mid(sstring$,b,1))
  Select c
  Case 44          ;"," 
    Goto skip
  Case 45          ;"-" 
    sign=-sign
  Case 46          ;"."
    decimal=1
  Case 48,49,50,51,52,53,54,55,56,57   ;"0" To "9"
    temp#=temp*base+c-48
    If decimal Then decimal=decimal*base
  Case 65,66,67,68,69,60    ;"A" to "F"
    If base=16 Then
      temp#=temp#*base+c-55
      If decimal Then decimal=decimal*base
    Else
      Goto fini 
    EndIf
  Case 97,98,99,100,101,102   ;"a" to "f"
    If base=16 Then
      temp#=temp#*base+c-87
      If decimal Then decimal=decimal*base
    Else
      Goto fini
    EndIf 
  Default
    Goto fini
  End Select
.skip
Next
.fini
If decimal Then temp#=temp#/decimal
Return temp#*sign
End Function
