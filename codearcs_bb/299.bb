; ID: 299
; Author: King Dave
; Date: 2002-04-20 15:29:53
; Title: BB Source Code Counter
; Description: Counts the number of lines in all .bb files

Graphics 520,340,16,2

dir.Dir=New Dir
dir\loc$=CurrentDir$()

Print "Counting lines..."
While dir<>Null
	lis=ReadDir(dir\loc$)
	If Not lis Then RuntimeError "Failed to read the '"+dir\loc$+"' directory"
	filen$=NextFile(lis)
	While filen$<>""
		If KeyHit(1) Then End
		Select FileType(dir\loc$+filen$)
			Case 1	If Right$(filen$,3)=".bb"
						Size=Size+FileSize(dir\loc$+filen$)
						file=ReadFile(dir\loc$+filen$)
						While Not Eof(file)
							txt$=ReadLine(file)
							If txt$<>"" And Left$(txt$,1)<>";" Then Lines=Lines+1 Else Blanks=Blanks+1
						Wend
						CloseFile file
					EndIf
			Case 2	If filen$<>"." And filen$<>".."
						ndir.Dir=New Dir
						ndir\loc$=dir\loc$+filen$+"\"
					EndIf
		End Select
		filen$=NextFile(lis)
	Wend
	CloseDir lis
	Delete dir
	dir.Dir=First Dir
Wend

Cls
Locate 0,0
Print "Lines of code: "+Lines
Print "Blank or comment lines: "+Blanks
Print "---"
Print "Total lines: "+(Lines+Blanks)
Print:Print
kb#=Float#(Size)/1024
mb#=Float#(Size)/1048576
Print "Total file size: "+Size+" bytes ("+Left$(kb#,Len(kb#)-4)+" kb / "+Left$(mb#,Len(mb#)-4)+" mb)"
Print:Print
Print "Press any key to close."
FlushKeys
WaitKey
End

Type Dir
	Field loc$
End Type
