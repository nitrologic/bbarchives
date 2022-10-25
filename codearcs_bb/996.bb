; ID: 996
; Author: Rob Farley
; Date: 2004-04-13 08:56:35
; Title: Custom functions Decl file
; Description: Creates a Decl file so you can hit F1 on your own functions

Graphics 800,500,0,2

Type functions
Field filename$
Field name$
Field description$
End Type

Type includes
Field filename$
Field root
End Type

Global root$="g:\library\"
blitzlocation$="g:\blitz3d\"
filename$="rgb_functions.bb"

find(filename)


; recursively find all functions in includes
Repeat

	; find functions in includes
	For a.includes = Each includes
	find(a\filename,a\root)
	Delete a
	Next
	
	; are there any more includes to check?
	count=0
	For a.includes = Each includes
	count=count+1
	Next

Until count=0

; write the functions to userlib
Print "Creating "+Left(filename,Len(filename)-3)+"_functions.decls"
Print
fileout=WriteFile(blitzlocation+"userlibs\"+Left(filename,Len(filename)-3)+"_functions.decls")
WriteLine(fileout,".lib "+Chr(34)+" "+Chr$(34))
For n.Functions = Each functions

WriteLine(fileout,n\name)


; create helpfile

; remove bad filename characters
remove$="( $#%"
functionname$=n\name
For r=1 To Len(remove)
	If Instr(functionname$,Mid(remove,r,1))>0 Then functionname$=Left(n\name,Instr(n\name,Mid(remove,r,1))-1)
Next
functionname=Lower(functionname)

Print "Writing Helpfile for "+functionname
helpfile=WriteFile(blitzlocation+"help\commands\2d_commands\"+functionname+".htm")

Restore html_template

Read in$
While in<>"**"

	written=False
	If in$="@functionname@" Then WriteLine helpfile,n\name:written=True
	If in$="@location@" Then WriteLine helpfile,n\filename:written=True
	If in$="@description@" Then WriteLine helpfile,n\description:written=True
	If written=False Then WriteLine helpfile,in
	Read in$
Wend

CloseFile helpfile

Next
CloseFile fileout
Print
Print "===================================================================================================="
Print


Print
Print "All done! Press any key."
Print
Print 


WaitKey

End

Function find(filename$,useroot=True)

If useroot
	filein=ReadFile(root+filename)
	Else
	filein=ReadFile(filename)
	EndIf

Repeat

temp$=ReadLine(filein)

;unindent text
If Len(temp)>2
	Repeat
	If Asc(Left(temp,1))<33 Then temp=Right(temp,Len(temp)-1)
	Until Left(temp,1)>32 Or Len(temp)<2
	ntemp$=""
	;remove crappy text
	temp=remove_crap(temp)
	EndIf

command$=Left(temp,8)

If command="Function"
	n.Functions = New Functions
	n\filename=filename
	temp=Right(temp,Len(temp)-9)
	ntemp=""
	
	;remove defaulting as this causes an error starting blitz
	
	If Instr(temp,"(")>Instr(temp,".") And Instr(temp,".")>0
		temp$=Left(temp,Instr(temp,".")-1)+Right(temp,Len(temp)-Instr(temp,"(")+1)
		EndIf
	
	Repeat
	nn=Instr(temp,"=")
	If nn>0
		nnn=Instr(temp,",",nn)
		If nnn=0 Then nnn=Instr(temp,")",nn)
		temp=Left(temp,nn-1)+Right(temp,Len(temp)-nnn+1)
		EndIf
	Until nn=0
	
	;capitalise function
	temp=uppercase(temp,1)
	For nn=2 To Instr(temp,"(")
	If Mid(temp,nn-1,1)="_" Then temp=uppercase(temp,nn)
	Next
	
	n\name=temp
	
	temp$=ReadLine(filein)
	
	Repeat
	
	If Left(temp,1)=";"
		n\description=n\description+Right(temp,Len(temp)-1)+"<br>"
		EndIf
	temp$=ReadLine(filein)

	Until Left(temp,1)<>";"
	
	End If
	
If command="Include "
	a.includes= New includes
	a\filename=Mid(temp,10,Len(temp)-10)
	; check if the include has full path or not
	If Mid(a\filename,2,1)=":" Then a\root=False Else a\root=True
	EndIf

Until Eof(filein)

End Function

Function uppercase$(temp$,pos)
t$=""
t$=t$+Left(temp,pos-1)
t$=t$+Upper(Mid(temp,pos,1))
t$=t$+Right(temp,Len(temp)-pos)
Return t$
End Function

Function remove_crap$(S$)
	tmp$=""
	For x=1 To Len(s)
	If Asc(Mid(s,x,1))>31 And Asc(Mid(smp,x,1))<128 Then tmp=tmp+Mid(s,x,1)
	Next
	Return tmp$
End Function

; Help File HTML Template
.html_template
Data "<html><head><title>Blitz Docs</title><link rel=stylesheet href=../css/commands.css Type=Text/css></head>"
Data "<body><h1>"
Data "@functionname@"
Data "</h1><h1>Command Location</h1><table><tr><td>"
Data "@location@"
Data "</td></tr></table><h1>Description</h1><table><tr><td>"
Data "@description@"
Data "</td></tr></table><br><br><a target=_top href=../index.htm>Index</a><br><br>"
Data "Click <a href=http://www.blitzbasic.com target=_blank>here</a>"
Data "to view the latest version of this page online with user comments</body></html>"
Data "**"
