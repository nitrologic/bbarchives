; ID: 758
; Author: Nebula
; Date: 2003-08-09 11:33:53
; Title: Get Filename/Folder/Extension
; Description: 3 useful file functions

Function getfilename$(filename$) ; Returns the filename and extension
	lastdir = 1
	For i=1 To Len(filename$)
		If Mid$(filename$,i,1) = "\" Then Lastdir = i
	Next
	If Lastdir > 1 Then Lastdir = Lastdir + 1
	For i=Lastdir To Len(filename$)
		a$ = a$ + Mid(filename$,i,1)
	Next
	Return a$
End Function

Function getextension$(filename$) ; Returns the extension minus the .
	lastdir = 1
	For i=1 To Len(filename$)
		If Mid$(filename$,i,1) = "." Then Lastdir = i
	Next
	If Lastdir > 1 Then Lastdir = Lastdir + 1
	For i=Lastdir To Len(filename$)
		a$ = a$ + Mid(filename$,i,1)
	Next
	Return a$
End Function

Function getdirectory$(filename$) ; Returns the complete directory including drive
	lastdir = 1
	For i=1 To Len(filename$)
		If Mid$(filename$,i,1) = "\" Then Lastdir = i
	Next
	For i=1 To Lastdir
		a$ = a$ + Mid(filename$,i,1)
	Next
	Return a$
End Function
