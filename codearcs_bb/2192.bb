; ID: 2192
; Author: Nebula
; Date: 2008-01-20 12:18:27
; Title: Folder information (b+)
; Description: Read Folder and make info file

;
; Update Contents.txt - By Nebula
;
; This sourcecode is a example of how you can automate processes.
; The code Reads the current directory and creates a custom textfile
; with information about all the files present.
;
;
;
;
;
;

Dim dir_array$(256)

Global path$ = SystemProperty("appdir")
If CommandLine$() = "-editor" Then path$ = "d:\blitzbasic\blitzplustutorials\"

Read_dir(path$)
sortarray()
ComposeFile()

Notify "Current directory processed. Read Contents.txt in folder for updates."
End

Function Read_Dir(directory$)
mydir = ReadDir(directory$)
Repeat
	a$ = NextFile$(mydir)
	If a$="" Then Exit
	dir_array$(counter) = a$
	counter = counter +1
	If counter > 256 Then Notify "Directory (" + path$ + ") is to big to read." : Exit
Forever
CloseDir(mydir)
End Function
Function Countdirs()
Repeat
	If dir_array$(counter) = "" Then Exit
	If FileType(dir_array$(counter)) = 2 Then numdirs = numdirs + 1
	counter = counter + 1
Forever
Return numdirs-2
End Function
Function Countfiles()
Repeat
	If dir_array$(counter) = "" Then Exit
	If FileType(dir_array$(counter)) = 1 Then numfiles = numfiles + 1
	counter = counter + 1
Forever
Return numfiles 
End Function
Function Printdirinfo$(filename$)

	If FileType(filename$) = 2 Then a$ = "Folder"
	If FileType(filename$) = 1 Then a$ = "File"
	b$ = FileSize(filename$)
	c$ = filename$
	For i=Len(c$) To 32
		c$ = c$ + " "
	Next
	c$ = c$ + b$
	For i = Len(c$) To 48
		c$ = c$ + " "
	Next
	c$ = c$ + a$
	Return c$
End Function
Function Printstatistics$(ext$,extensionstring$)
	For i=0 To 256
		If getextension$(dir_array$(i)) = ext$ Then
			File_size = File_Size + FileSize(dir_array$(i))
		End If
	Next
	Return "Combined Size Of " + extensionstring$ + " (" + ext$ + ") files : " + file_size + " bytes."
End Function
Function PrintLineStatistics$(ext$,extensionstring$)
	For i=0 To 256
		If getextension$(dir_array$(i)) = ext$ And FileType(dir_array$(i)) = 1 Then
			fu = OpenFile(dir_array$(i))
			Repeat
			ReadLine(fu)
			nolines = nolines + 1
			Until Eof(fu)
			CloseFile(fu)
		End If
	Next

	Return "Total amount of lines in " + extensionstring$ + " (" + ext$ + ") files : " + nolines + " Lines."

End Function
Function ComposeFile()
	f = WriteFile(path$+"contents.txt")
	;
	WriteLine f,"Contents of the BlitzPlus Example/Tutorial Folder"
	WriteLine f,""
	WriteLine f,"Created By Nebula in 2003"
	WriteLine f,""
	WriteLine f,""
	;
	counter = 2
	Repeat
		If dir_array$(counter) = "" Then Exit
		WriteLine f,printdirinfo$(dir_array$(counter))
		counter = counter + 1
		If counter > 256 Then Exit
	Forever

	WriteLine f,""
	WriteLine f,Printstatistics("bb","Blitz Basic")
	WriteLine f,Printlinestatistics("bb","Blitz Basic")
	
	CloseFile(f)
End Function
Function Sortarray()
While Exitloop=False
	Exitloop=True
	For i=0 To 255
		If dir_array$(i+1) <> "" Then
			If dir_array$(i) > dir_array$(i+1) Then
				a$ = dir_array$(i)
				b$ = dir_array$(i+1)
				dir_array$(i+1) = a$
				dir_array$(i) = b$
				Exitloop =False
			End If
		End If
	Next
Wend
End Function
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





;End
