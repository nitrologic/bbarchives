; ID: 1848
; Author: Devils Child
; Date: 2006-10-24 23:15:49
; Title: String parsing functions
; Description: Little string parsing library.

Function Parse$(msg$, item, sep$ = ",")
Repeat
	fas = Instr(msg$, sep$, fas + 1)
	count = count + 1
Until fas = 0 Or count = item
If fas = 0 And item > 0 Then Return
spos = fas + 1
epos = Instr(msg$ + sep$, sep$, fas + 1)
Return Mid(msg$, spos, epos - spos)
End Function

Function GetParam$(path$, p$)
file = ReadFile(path$)
While Not Eof(file)
	l$ = Replace(ReadLine(file), " ", "")
	If GUI_Parse(l$, 0, "=") = p$ Then
		txt$ = GUI_Parse(l$, 1, "=")
		txt$ = Replace(txt$, Chr(34), "")
		txt$ = Replace(txt$, "~", " ")
		Exit
	EndIf
Wend
CloseFile file
If txt$ = "" Then RuntimeError "GUI_GetParam$() failed."
Return txt$
End Function

Function LoadFont(fnt$)
Return LoadFont(GUI_Parse(fnt$, 0), GUI_Parse(fnt$, 1), GUI_Parse(fnt$, 2), GUI_Parse(fnt$, 3), GUI_Parse(fnt$, 4))
End Function
