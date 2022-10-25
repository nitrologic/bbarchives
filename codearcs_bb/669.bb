; ID: 669
; Author: ShadowTurtle
; Date: 2003-05-04 04:40:56
; Title: MathToString$(...)
; Description: NEW version 1.5

Print MathToString("3")
Print MathToString("2/(3+2)")
Print MathToString("5>1 And 5<100")

Function MathToString$(TheMath$, unit = 0, divnow = 0)
  Local MyParam$ = "*/^+-=<>&|%@", MyNumbs$ = "0123456789.", MyDivParam$ = "*/^"
  Local Ziffer$, ScanPos, MathAnswer#, MathArt$, MathPower#, OldMathPower#
  Local Scan, ScanNumber$, OldScanNumber$, MathScan$, MyScanText$

  Local bscan, bscannow, bscanhave, ScanPosA, ScanPosB

  Local deScan, deMathScan$, deMath

  Local debsScan

  TheMath$ = Lower(TheMath$)
  TheMath$ = Replace(TheMath$, "and", "&")
  TheMath$ = Replace(TheMath$, "xor", "@")
  TheMath$ = Replace(TheMath$, "or", "|")
  TheMath$ = Replace(TheMath$, "mod", "%")

  MathScan$ = Replace(TheMath$, " ", "") : debsScan = 1

  While bscan < Len(MathScan$) 
    bscan = bscan + 1 
    If Mid$(MathScan$, bscan, 1) = "(" Then 
      ScanPosA = bscan : bscannow = 1 
      While bscannow 
       If Mid$(MathScan$, bscan, 1) = "(" Then bscanhave = bscanhave + 1 
       If Mid$(MathScan$, bscan, 1) = ")" Then bscanhave = bscanhave - 1 
       If bscanhave = 0 Then bscannow = 0 
       bscan = bscan + 1 
       If KeyDown(1) Then End 
      Wend 

      ScanPosB = bscan 

      MyScanText$ = Mid$(MathScan$, ScanPosA+1, ScanPosB - ScanPosA - 2)

      MyScanText$ = MathToString$(MyScanText$, unit + 1) 
      MathScan$ = Replace(MathScan$, Mid$(MathScan$, ScanPosA, ScanPosB - ScanPosA), MyScanText$)
      bscan = 0
    End If 

    If KeyDown(1) Then End 
  Wend 

  .NewMathScan

  deMathScan$ = MathScan$

  Scan = InMid$(MathScan$, MyParam$)
  If Scan Then
    ScanNumber$ = Mid$(MathScan$, 1, Scan-1)
    MathScan$ = Mid$(MathScan$, Scan)
    MathAnswer = val2(ScanNumber$)
  Else
    Return MathScan$
  End If

  deScan = 1

  While Not MathScan$ = ""
    uu$ = MathScan$

    MathArt$ = Mid$(MathScan$, 1, 1)
    MathScan$ = Mid$(MathScan$, 2)

    If Mid$(MathScan$,1,1) = "-" Then
      MathPower# = -1
      MathScan$ = Mid$(MathScan$, 2)
    Else
      MathPower# = 1
    End If

    Scan = InMid$(MathScan$, MyParam$)
    OldScanNumber$ = ScanNumber$
    OldMathPower# = MathPower#
    ScanNumber$ = Mid$(MathScan$, 1, Scan-1)

    MathScan$ = Mid$(MathScan$, Len(ScanNumber$)+1)

    If MathArt$ = "+" Then
       MathAnswer# = MathAnswer# + (val2(ScanNumber$)*MathPower#)
    ElseIf MathArt$ = "-" Then
       MathAnswer# = MathAnswer# - (val2(ScanNumber$)*MathPower#)
    ElseIf MathArt$ = "*" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) * (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "*-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "*" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "/" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) / (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "/-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "/" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "^" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) ^ (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "^-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "^" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "=" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) = (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "=-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "=" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "<" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) < (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "<-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "<" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = ">" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) > (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + ">-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + ">" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "&" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) And (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "&-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "&" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "|" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) Or (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "|-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "|" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "%" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) Mod (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "%-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "%" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    ElseIf MathArt$ = "@" Then
       MathAnswer# = (val2(OldScanNumber$)*OldMathPower#) Xor (val2(ScanNumber$)*MathPower#)
       If MathPower# = -1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "@-" + ScanNumber$, "-" + Str$(MathAnswer))
       ElseIf MathPower# = 1 Then
         MathScan$ = Replace(deMathScan$, OldScanNumber$ + "@" + ScanNumber$, Str$(MathAnswer))
       End If
       Goto NewMathScan
    Else
       Return "SYNTAX ERROR"
    End If
  Wend

  Return Str(MathAnswer)
End Function

Function InMid$(A$, B$) ; in benutzung
  Local C, Q, W
  C = 0
  For Q = 1 To Len(A$)
    For W = 1 To Len(B$)
      If (Mid$(A$, Q, 1) = Mid$(B$, W, 1)) And C = 0 Then C = Q : Exit
    Next
    If C>0 Then Exit
  Next
  Return C
End Function

Function val2#(sstring$)
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

If negative = -1 Then
  Return -(temp#*sign)
Else
  Return temp#*sign
End If
End Function
