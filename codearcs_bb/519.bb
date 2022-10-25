; ID: 519
; Author: Giano
; Date: 2002-12-05 04:24:34
; Title: Remove Annoying Backup files
; Description: A simple tool/utility to remove all files from directory that you don't like

;***********************************************************
;***	REMOVE ANNOYING BACK FILE - By Gianluca SCLANO
;***	sclano@hotmail.com
;***  Intensive Works
;***********************************************************
;*** 
;*** NOTES:
;*** This program permits to remove annoyng blitz 
;*** backup file from folders, uses command line and 
;*** drag & drop.
;*** Remove also 
;*** 
;***********************************************************
;***********************************************************
Type File
	Field name$
End Type
;***********************************************************
;*** FILTERS: here add/remove your personal filter
;***********************************************************
.FILTERS
Data 4 ; <- total number
Data ".bak",".bb_bak","thumbs.db",".tmp" ;<- filters 

;***********************************************************
;*** Scan directory for filtered files
;***********************************************************
Function scanDir(path$,filter$, recursive=True)
	If right$(path,1)<>"\" Then path$= path$+"\"
	If FileType(path$)=0 Then Return 0
	filter = Upper$(filter$)
	myDir=ReadDir(path$)
	counter=0
	; Let's loop forever until we run out of files/folders to list!
	Repeat
	; Assign the next entry in the folder to file$
	file$=NextFile$(myDir)
	; If there isn't another one, let's exit this loop
	If file$="" Then Exit
	; Use FileType to determine if it is a folder (value 2) or a file and print results
	filename$= path$+file$ 
	If FileType(filename) =1 
		If Instr(Upper$(file),filter,1)<>0 
			f.file = New file
			f\name = path$+file$ 
			counter = counter + 1 
		End If
	Else
		If recursive=True
			If file$<>"." And file$<>".." Then
				If FileType(filename+"\")
					dlog(">folder:" + filename+"\")
					scandir( filename$+"\", filter$)
				End If
			End If
		End If
	End If
	Forever
	; Properly close the open folder
	CloseDir myDir
	Return counter
End Function
;***********************************************************
;*** Write txt file of dumped results (opened via notepad)
;***********************************************************
Function writeTextFile(filename$, txt$)
	f = WriteFile(filename)
	start=1
	Repeat
	flagExit = Instr(txt,Chr$(13),start)
	If flagexit Then 
		WriteLine f, Mid$(txt$,start,flagExit-start)
	    start = flagExit + 1
	End If
	Until flagExit = 0
	CloseFile f
End Function

;***********************************************************
;*** The main loop routine
;***********************************************************
Function deletaAllBackFile(path$)
	txt$=""
	quotes$=Chr$(34)
	Repeat
		path$ = Replace(path,quotes,"")
	Until Instr(path,quotes)=0
	txt$ = txt$ + dLog("Scanning directory..." )
	txt$ = txt$ + dLog(path$, 255,0,0)
	Color 0,0,0
	Print
	Print "Press any key to continue"
	WaitInteraction
	Restore FILTERS
	Read howmany
	For t=1 To howmany
		scanDir(path$,".bak")
	Next
	filename$ = SystemProperty ("tempdir") + "tmp.txt"
;*** Dump founded files
	i=0
	For f.file = Each file
		txt = txt + dLog("Find "+i+": "+ f\name)
		i=i+1
	Next
	txt = txt + dLog("Founded "+i+" files to be deleted...",255,0,0)
	Color 0,0,0
;*** Write and open with notepad the result file 
	writeTextFile(filename$,txt)
	ExecFile("notepad " + filename$) 
	Print
	Print "Press any key to delete them or...quit!"
	WaitInteraction
;*** Delete the files
	i=0
	For f.file = Each file
		If FileType(f\name$)
			DeleteFile (f\name$)
			i=i+1
		End If
	Next
	Print "Removed "+i+" files..cya!"
	WaitInteraction
;*** Release resources
	Delete Each file
	DeleteFile filename$
End Function
;***********************************************************
;*** Debug log + print log
;***********************************************************
Function dlog$(txt$, r=0,g=0,b=0)
	DebugLog(txt)
	Color r,g,b
	Print txt
	Return txt + Chr$(13)
End Function
;***********************************************************
;*** Alternative to wait mouse and key. Esc quits
;***********************************************************
Function WaitInteraction()
	FlushMouse()
	FlushKeys()
	Repeat 
		If MouseDown(1) Then Repeat:Until MouseDown(1)=0 : Return
		If MouseDown(2) Then end
		If MouseDown(3) Then Repeat:Until MouseDown(3)=0 : Return
		v= GetKey()
		If v=27 Then End
		If v Then Return
	Until False
End Function
;***********************************************************
;*** Main Program
;***********************************************************
AppTitle "Remove Bak files V1.02 - By Gianluca Sclano (BitmaniaK)";"Okee Dokee!"
Graphics 800,600,16,2
ClsColor 255,255,255
Cls
Color 0,0,160
Print "REMOVE ANNOYING BACK FILE"
Print "By Gianluca SCLANO (BitmaniaK)"
Print "sclano@hotmail.com"
Print

If CommandLine$() <>"" Then
	deletaAllBackFile(CommandLine$())
Else
	deletaAllBackFile(CurrentDir$())
End If
End
