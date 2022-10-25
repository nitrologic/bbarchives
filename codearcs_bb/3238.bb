; ID: 3238
; Author: Dan
; Date: 2016-01-05 13:25:22
; Title: PrintF() function
; Description: Prints text to a file - the easy way

;====================================================================
; Project: PrintF function
; Version: 1.0
; Author: Dan
; Email: -.-
; Copyright:  PD
; Description:      
;     Prints Text To a file ! (easy way)
;     every time PrintF is called it writes the text to the end of the last file set with PrintF("c:\hey.txt,1) file.
;     Basic error checking is implemented,but you will need to ensure that the filename is valid
;     every PrintF("text") call adds CrLf ($0d and $0a bytes to the end of the end of the txt$)
;     so that the file can be opened with notepad
; Usage:
;       PrintF(Filename,1) to set the filename
;       PrintF("text")     to write to the file above
;       PrintF("")         to close the opened file !!!
;                          so that a new filename can be set
;===============================================================================================


Function PrintF(Txt$="",setf=0)
;Copy next 8 lines to the beginning of your program, and uncomment them (remove ; )
;	Type writeout
;		Field Filename$
;		Field OldFilename$
;		Field filestreamID
;		Field open
;		Field filenameset
;	End Type 
;	Global pfile.writeout = New writeout
;	Const loaddebug=0						;Used for CheckLoad + CheckAnimLoad functions
;	If loaddebug=1 Then PrintF("R:\filesize.txt",1)			;to make a list with filenames and filesize. useful for releasing the games, to check if the file has been modified (is it only a simple size check)
;usage:
;PrintF("r:\test.txt",1)	;to set the filename
;PrintF("test text")		;to write a line of text to the file 
;PrintF("")					;To close the file ! Important before using another file to write!
;							;else it writes to the same file again
	Select True
    Case setf=0 And pfile\filenameset=1
		If Len(Txt$)>0						;Is Length of the Text$ greater than 0
			If pfile\open=0					;has the file allready been opened ?
				If pfile\OldFilename$="" Then pfile\OldFilename$=pfile\Filename$	;if no, set the oldfilename as filename$ 
				
				If FileType(pfile\Filename$)=0			;Doesnt Exists, create new one
					pfile\filestreamID=WriteFile(pfile\Filename$)
				ElseIf FileType(pfile\Filename$)=2			;It is a directory, Stop the program
					RuntimeError "PrintF: The Filename is a directory, please check your script"
				ElseIf FileType(pfile\Filename$)=1			;File Exists, open it to make additions !
					pfile\filestreamID=OpenFile(pfile\Filename$)
				EndIf
				If pfile\filestreamID=0						;Check if the file could be opened 
					RuntimeError "PrintF: error cannot open "+pfile\Filename$
				Else										;The file exists, set the writing position to the end of the file !
					SeekFile (pfile\filestreamID,FileSize(pfile\Filename$))
				EndIf
				pfile\open=1								;Global flag to indicate that the file is open !
			ElseIf pfile\open=1								;File has allready been opened, check if the filename is same (to prevent writing data to a wrong file !)
				If pfile\Filename$<>pfile\OldFilename$ 
					If pfile\filestreamID>0 Then CloseFile pfile\filestreamID
					RuntimeError ("PrintF: Filename Missmatch "+pfile\Filename$+" is not "+pfile\OldFilename$)
				EndIf
			EndIf
		;Write text string into the FilestreamId
			For x=1 To Len(Txt$)
				WriteByte pfile\filestreamID,Asc(Mid$(Txt$,x,1))
			Next
		;And add cr+lf, so it can be readed in text editor as new line
			WriteByte pfile\filestreamID,$0d
			WriteByte pfile\filestreamID,$0a 
		Else									;if length of the text$ is 0 then the file should be closed !
			If pfile\filestreamID>0 Then CloseFile pfile\filestreamID
			pfile\open=0
			pfile\OldFilename=""
		EndIf 
	Case setf=1 And pfile\filenameset=0
	   If Txt$="" Then RuntimeError "PrintF (txt$,1) is used To set a filename, And txt$ cannot be empty!"
		pfile\Filename$=Txt$
		pfile\filenameset=1
	Case setf=0 And pfile\filenameset=0
	    RuntimeError "The Filename was not been set, use PrintF(''c:\Filename'',1) before calling PrintF(''text'') writing function"
	Case setf=1 And pfile\filenameset=1
	    If pfile\OldFilename="" And Txt$<>""
		   pfile\Filename=Txt$
		Else
		   RuntimeError " Close the filehandle with PrintF('''') before setting a new filename !"
		EndIf
	End Select
End Function
