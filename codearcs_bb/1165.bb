; ID: 1165
; Author: Cygnus
; Date: 2004-09-24 18:39:43
; Title: Source Code CLeaner
; Description: Removes

;Source file cleaner: By Damien Sturdy (Cygnus Games) 2004!
;USAGE EXAMPLE:
;		 cleansource("supernova 2d.bb","supernova2d clean.bb",0)   cleans the code and leaves NO empty lines
;		 cleansource("supernova 2d.bb","supernova2d clean.bb",1)   cleans the code and leaves ONE empty line
;				where one or a bunch of empty lines were before. Useful for keeping things looking nice.
;Removes all unnecesary Tabs and remarks...
;Leaves all ";" signs when in brackets....


Function cleansource(infile$,outfile$,leave_single_gaps)
Local fs,fs2,n,m$,m2$,stp,doagn,ok,oneline=1
If infile$=outfile$ Then
CopyFile infile$,infile$+"_nbak"
CopyFile infile$,"temp.bb"
infile$="temp.bb"
EndIf
fs=ReadFile(infile$)
fs2=WriteFile(outfile$)

Repeat

m$=ReadLine(fs)
m2$=""
For n=1 To Len(m$)
If Mid$(m$,n,1)<>Chr$(9) Then m2$=m2$+Mid$(m$,n,1)
Next
ok=1

 m$=m2$
 stp=1
.doitagain
inst= Instr(m$,";",stp)
If inst>0 Then
doagn=0
For n=stp To inst
If Mid$(m$,n,1)=Chr$(34) Then ok=1-ok
Next
If ok=1 Then m$=Mid$(m$,1,inst-1) Else doagn=1:stp=inst+1
EndIf
If doagn=1 Then Goto doitagain
.dagn

If (Right$(m$,1)=" " Or Right$(m$,1)="	") And Len(m$)>0 Then m$=Mid$(m$,1,Len(m$)-1):Goto dagn
If leave_single_gaps=0 Then oneline=1
If inst<>1 And m$<>" " And m$<>"	" And (m$<>"" Or oneline=0) Then
	;Print m$
	If Instr(m$,"Function")<>0 And Instr(m$,"End")=0 Then WriteLine fs2,";;;;;;;;;;;;;;;;;;;;;;;"
	WriteLine fs2,m$
EndIf
If m$<>"" Then oneline=0
If m$="" Then oneline=1

Until Eof(fs) Or KeyDown(1)
CloseFile fs
CloseFile fs2
End
End Function
