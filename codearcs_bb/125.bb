; ID: 125
; Author: Oldefoxx
; Date: 2001-11-04 20:56:29
; Title: Parsing Strings
; Description: Functions for parsing strings based on separation chars.

Graphics 640,480
ClsColor 0,0,128
Cls
example$="Now is the time for all good men to come to the aid of their party."
Print example$
a=parsecount(example$," ",0)
Print a+" separate parsed elements found in example$"
Print
For b=1 To a
  Print "Element #"+b+" = "+parse$(example$," ",b,0)
Next
WaitKey
End

Function parsecount(sstring$,tstring$,flag)
;  sstring$ -- source string to be parse counted
;  tstring$ -- target string containing parsing characters
;  flag     -  flag to identify how parsed characters are grouped
;              0 -- use as a group
;              1 -- use singularly (same as ANY in other BASICs
;              2 -- use in pairs
;              3 -- use as triplets (and so on for 4 or more)
Local count=0
Local a=0
Local b, c, d
If tstring$>"" Then
  Repeat
    count=count+1
    If flag>0 Then
      b=0 
      c=1
      While c<=Len(tstring$)
        d=Instr(sstring$,Mid(tstring$,c,flag),a+1)
        If b=0 Or d And d<b Then b=d
        c=c+flag
      Wend
    Else
      b=Instr(sstring$,tstring$,a+1)
    End If
    a=b
  Until b=0
EndIf
Return count
End Function

Function parse$(sstring$,tstring$,offset,flag)
;  sstring$ -- source string to be searched
;  tstring$ -- target string with parsing character(s)
;  flag     -- determines how target string is to be applied:
;              0 -- all at once
;              1 -- individually (same as ANY in other BASICs)
;              2 -- in pairs
;              3 -- in triplets (and so on for 4 and more)
Local count=0
Local a=0
Local b, c, d
If tstring$="" Then Return sstring$
Repeat
  If flag>0 Then
    b=0
    c=1
    While c<=Len(tstring$)
      d=Instr(sstring$,Mid(tstring$,c,flag),a+1)
      If b=0 Or d And d<b Then b=d
      c=c+flag
    Wend
  Else
    b=Instr(sstring$,tstring$,a+1)
  End If
  count=count+1
  If count=offset Then
    If b=0 Then
      Return Mid$(sstring$,a+1,Len(sstring$)-a)
    Else
      Return Mid$(sstring$,a+1,b-a-1)
    End If
  End If
  If b=0 Then Return ""
  a=b
Forever
End Function
