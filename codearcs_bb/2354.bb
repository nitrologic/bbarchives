; ID: 2354
; Author: Ryudin
; Date: 2008-11-02 04:58:04
; Title: High Score System
; Description: A simple little high score system you can use or modify for most games.

Global hinum[10]
Global hinam$[10]
Global hitnm$

Function CreateNewHighScoreFile(FILENAME$,TABLENAME$)
	w = WriteFile(FILENAME$ + ".dat")
		WriteString(w,TABLENAME$)
		WriteInt(w,1000)
		WriteString(w,"H")
		WriteInt(w,900)
		WriteString(w,"I")
		WriteInt(w,800)
		WriteString(w,"G")
		WriteInt(w,700)
		WriteString(w,"H")
		WriteInt(w,600)
		WriteString(w,"S")
		WriteInt(w,500)
		WriteString(w,"C")
		WriteInt(w,400)
		WriteString(w,"O")
		WriteInt(w,300)
		WriteString(w,"R")
		WriteInt(w,200)
		WriteString(w,"E")
		WriteInt(w,100)
		WriteString(w,"S")
	CloseFile(w)
End Function

Function DisplayLastLoadedHighScoresInBasicText() ;Longest function name ever ;)
	Print hinum[10] + "         " + hinam[10]
	Print hinum[9] + "         " + hinam[9]
	Print hinum[8] + "         " + hinam[8]
	Print hinum[7] + "         " + hinam[7]
	Print hinum[6] + "         " + hinam[6]
	Print hinum[5] + "         " + hinam[5]
	Print hinum[4] + "         " + hinam[4]
	Print hinum[3] + "         " + hinam[3]
	Print hinum[2] + "         " + hinam[2]
	Print hinum[1] + "         " + hinam[1]
End Function

Function ReadHighScoreFile(FILENAME$)
	r = ReadFile(FILENAME$ + ".dat")
		hitnm$ = ReadString(r)
		hinum[10] = ReadInt(r)
		hinam[10] = ReadString(r)
		hinum[9] = ReadInt(r)
		hinam[9] = ReadString(r)
		hinum[8] = ReadInt(r)
		hinam[8] = ReadString(r)
		hinum[7] = ReadInt(r)
		hinam[7] = ReadString(r)
		hinum[6] = ReadInt(r)
		hinam[6] = ReadString(r)
		hinum[5] = ReadInt(r)
		hinam[5] = ReadString(r)
		hinum[4] = ReadInt(r)
		hinam[4] = ReadString(r)
		hinum[3] = ReadInt(r)
		hinam[3] = ReadString(r)
		hinum[2] = ReadInt(r)
		hinam[2] = ReadString(r)
		hinum[1] = ReadInt(r)
		hinam[1] = ReadString(r)
	CloseFile(r)
End Function
