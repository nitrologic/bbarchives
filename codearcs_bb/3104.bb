; ID: 3104
; Author: John Blackledge
; Date: 2014-02-06 06:24:35
; Title: GetFileFromShortcut
; Description: Retrieves the internal filename from a shortcut.

;---------------------------------
; zlibGetFileFromShortcut ; John Blackledge 06/02/2013
;---------------------------------
; for normal use just comment out Example()
; and call GetFileFromShortcut$(yourlnk$)
; which returns the internal filename
;---------------------------------

;Example()

;---------------------------------
Function Example()
;---------------------------------
	Graphics 1024,768
	
	lnk$ = "C:\Documents and Settings\John\Desktop\Lifestory.doc.lnk"
	Print "From file shortcut "+lnk$
	file$ = GetFileFromShortcut$(lnk$,1)
	Print "File = "+file$
	Print " "
	
	lnk$ = "C:\Documents and Settings\John\Desktop\Sys.lnk"
	Print "From folder shortcut "+lnk$
	file$ = GetFileFromShortcut$(lnk$)
	Print "File = "+file$
	Print " "
	
	WaitKey()
	End
	
End Function

;---------------------------------
Function GetFileFromShortcut$(lnk$, printfg=0)
;---------------------------------
	Local filein, m$,b,index,rindex,name$,search$
	Local startpos, endpos, i, char$
	Local length, path$
	
	If printfg=1 Then Print "Filesize "+FileSize(lnk$)
	
	filein = ReadFile(lnk$)
	m$ = ""
	While Not Eof(filein)
		b = ReadByte(filein)
		m$ = m$ + Chr$(b)
	Wend
	CloseFile(filein)
	If printfg=1 Then Print "Read in "+Len(m$)
	
; Isolate "\Lifestory.doc.lnk"
	rindex = JRinstr(lnk$,"\")
	name$ = Mid$(lnk$,rindex)
	If printfg=1 Then Print name$
	
; Isolate "\Lifestory.doc"
	rindex = JRinstr(name$,".")
	search$ = Left$(name$,rindex-1)
	If printfg=1 Then Print search$
	
; Get "\" pos in string
	index = Instr(m$,search$)
	If printfg=1 Then Print "Endpath = "+index
	endpos = index
	
; Work backwards from pos to get drive
	i = index
	While i > 0
		i = i - 1
		char$ = Mid$(m$,i,1)
		If char$ = ":"
			startpos = i-1
			i = 0
		EndIf
	Wend
	If printfg=1 Then Print "Startpath = "+startpos
	
; Get full path and add original search$
	length = endpos - startpos
	path$ = Mid$(m$,startpos,length)
	If printfg=1 Then Print path+search$
	
	Return path+search$
End Function

;---------------------------------
Function JRinstr(txt$, separator$)
;---------------------------------
	Local retval = 0, z
	
	For z = Len(txt$) To 1 Step -1
		If Mid$(txt$,z,1) = separator$
			retval = z
			z = 0
		EndIf
	Next
	
	Return retval
End Function

;-----------------------------------------------
;-----------------------------------------------
