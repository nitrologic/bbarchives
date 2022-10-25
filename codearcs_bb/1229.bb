; ID: 1229
; Author: Rob Farley
; Date: 2004-12-11 10:00:04
; Title: Auto code indenter
; Description: Indents all your code 'correctly'

; Code indenter written by Rob Farley (Dec 2004)
;
; Additional If-Then specials by Damien Sturdy

Function Entry$(number,List$,delimeter$=",")
n=1
count = 1
found = False
start = 1

If number > 1
Repeat
If Mid(List,n,1)=delimeter
count = count + 1
If count = number
found=True
start = n + 1
Exit
EndIf
EndIf
n=n+1
Until n >= Len(List)
If found = False Then RuntimeError("List Element out of Range")
EndIf
Endof = Instr(List,delimeter,start)
If endof = 0 Then endof = Len(List)+1
Return Mid(List,start,endof-start)
End Function

Function countentries(List$,delimeter$=",")
t$ = Replace(List$,delimeter,"")
Return (Len(List)-Len(t))+1
End Function


Function removeindents$(l$)
ret$=""
For n=1 To Len(l$)
If Asc(Mid(l,n,1))>31 Then ret=ret+Mid(l,n,1)
Next
Return ret
End Function


; add addindent or decindent commands if I've missed any
addindent$="repeat,while,function,type,for"
addindentfuncs = countentries(addindent)
DebugLog addindentfuncs
decindent$="until,wend,end function,end type,next,endif"
decindentfuncs = countentries(decindent)

indent = 0

inputfilename$="Autoindent.bb"
outputfilename$="Autoindent.txt"


filein = ReadFile(inputfilename)
fileout = WriteFile(outputfilename)

addone=False

Repeat

l$ = removeindents(ReadLine(filein))

For n=1 To addindentfuncs
funky$ = Lower(entry(n,addindent))
If Left(Lower(l),Len(funky))=funky Then addone = True

; special if statement, checks if there's a 'then' in the line
If Left(Lower(l),2)="if" And Instr(Lower(l),"then")=0 Then addone = True
If Left(Lower(l),2)="if" And Instr(Lower(l),"then:")=1 Then addone = True
If Left(Lower(l),2)="if" And Instr(Lower(l),"then :")=1 Then addone = True
If Left(Lower(l),2)="if" And Right$(Lower(l),4)="then" Then addone = True
If Left(Lower(l),2)="if" And Instr(l,";")>1 Then
nn=Instr(l,";")
Repeat:nn=nn-1:Until Mid$(l,nn,1)<>";":nn=nn+1
Repeat:nn=nn-1:Until Mid$(l,nn,1)>" " Or nn<2
If nn>6 Then
;Print Lower(Mid$(l,nn-4,4))
If Lower(Mid$(l,nn-3,4))="then" Then addone=True
If Lower(Mid$(l,nn-4,5))="then:" Then addone=True
If Lower(Mid$(l,nn-5,6))="then :" Then addone=True
EndIf
EndIf

Next

For n=1 To decindentfuncs
funky$ = Lower(entry(n,decindent))
If Left(Lower(l),Len(funky))=funky Then indent = indent - 1
Next

tab$ = Chr(9)

indenter$ = ""
If indent > 0
For n=1 To indent
indenter = indenter + tab
Next
EndIf

If addone = True
indent = indent + 1
addone = False
EndIf

l = indenter + l

WriteLine fileout,l

Until Eof(filein)

CloseFile filein
CloseFile fileout
ExecFile outputfilename$
