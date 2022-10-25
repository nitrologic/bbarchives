; ID: 1694
; Author: Devils Child
; Date: 2006-05-03 14:06:05
; Title: Recirsive ScanDir() func
; Description: a little scandir() func!

Graphics 1280, 1024, 32, 1
SetBuffer BackBuffer()
AppTitle "ScanDir"

ScanDir("c:\Windows\")
WaitKey()
End

Function ScanDir(path$)
dir = ReadDir(path$)
Repeat
	file$ = NextFile(dir)
	If file$ = "" Then Exit
	If file$ <> "." And file$ <> ".." Then
		Select FileType(path$ + file$)
			Case 1
				Color 0, 255, 0
				Print "File: " + path$ + file$
			Case 2
				Color 255, 0, 0
				Print "Dir: " + path$ + file$ + "\"
				ScanDir(path$ + file$ + "\")
		End Select
	EndIf
Forever
CloseDir dir
End Function
