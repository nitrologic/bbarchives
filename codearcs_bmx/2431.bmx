; ID: 2431
; Author: xlsior
; Date: 2009-03-09 19:31:41
; Title: Console or GUI?
; Description: Checks whether the current (or other) program is compiled in GUI or Console mode

' This function will check whether or not a blitzmax executable is a console or GUI program.
' Tested with BlitzMax 1.26 and 1.30
'
' By Marc van den Dikkenberg - http://www.xlsior.org
'
' Can be useful to merge both behaviours into a single program, so a single
' source can be maintained and just compiled twice
' This will only work with windows .exe files.
' Note that this will not work with UPX compressed executables
'

SuperStrict

If CheckConsole()=True Then
'	Print "This is a console program"
	Notify "This is a Console program"

Else
	Notify "This is a GUI program"
End If 

Function CheckConsole:Int(CurrentFile:String="")
	' If no filename specified, use the running file name
	If currentfile="" Then 
		currentfile=AppArgs[0]
		' If launched from the command prompt, the .exe suffix will be missing, so lets check for that
		If Instr(Upper(currentfile),".EXE")=0 Then
			currentfile=currentfile+".exe"
		End If
	End If
	Local myfile1:TStream=ReadFile(currentfile)
	SeekStream(myfile1,$A8)
	Local CheckVar:Int=Asc(ReadString(myfile1,1))
	If CheckVar=176
		' This is a GUI application
		Return False
	Else
		' This is a Console application
		Return True
	End If
	CloseFile myfile1
End Function
