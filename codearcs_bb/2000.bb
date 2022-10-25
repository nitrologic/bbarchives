; ID: 2000
; Author: Matt Merkulov
; Date: 2007-04-21 06:07:37
; Title: Convenient menu programming
; Description: Example: simple text editor

;Convenient menu programming by Matt Merkulov

Type menuitem
	Field name$
End Type

Function Menu_Create(w)
	wmenu = WindowMenu(w)
	parent = wmenu
	Repeat
		Read m$
		Select m$
			Case"{": parent = old
			Case"}": parent = wmenu
			Case"===The end===": Exit
		Default
			If parent <> wmenu Then
				i = i + 1
				mi.menuitem = New menuitem
				mi\name$ = m$
			End If
			old = CreateMenu(m$, i, parent)
		End Select
	Forever
	UpdateWindowMenu(w)
End Function

Function menuitem_name$(n)
	mi.menuitem = First menuitem
	For nn = 2 To n
		If mi = Null Then Notify"Menuitem N" + n + "does not exist": Return
		mi = After mi
	Next
	Return mi\name$
End Function



Global ent$ = Chr$(13) + Chr$(10), filename$

Global win = CreateWindow("", ClientWidth(Desktop()) / 2 - 300, ClientHeight(Desktop()) / 2 - 200, 600, 400, 0, 7)
filename_change "untitled.txt"
Global ta = CreateTextArea(0, 0, 10, 10, win)
ta_resize()

Data"File", "{", "New", "Open...", "Save...", "Save as...", "", "Exit", "}"
Data"Info", "{", "Help", "About...", "}"
Data"===The end==="
Menu_Create win

Repeat
	Select WaitEvent()
		Case$1001
			Select menuitem_name(EventData())
				Case"New"
					filename_change"untitled.txt"
					SetTextAreaText ta, ""
				Case"Open..."
					file$ = RequestFile("Open file...", "txt,*")
					If file$ <> ""Then doc_load file$
				Case"Save..."
					doc_save
				Case"Save as..."
					file$ = RequestFile("Save file as...", "txt,*", True)
					If file$ <> ""Then
						filename_change file$
						doc_save
					End If
				Case"Exit": Exit
				Case"Help": Notify "Really need help? Come on..."
				Case"About...": Notify"Simple text editor" + ent$ + "Author: Matt Merkulov"
			End Select
		Case$802:ta_resize()
		Case$803:Exit
	End Select
Forever

Function ta_resize()
	SetGadgetShape ta, 0, 0, ClientWidth(win), ClientHeight(win)
End Function

Function filename_change(file$)
	filename$ = file$
	SetGadgetText win, "Simple text editor - " + filename$
End Function

Function doc_load(file$)
	filename_change file$
	f = ReadFile(file$)
	LockTextArea ta
	SetTextAreaText ta, ""
	While Not Eof(f)
		AddTextAreaText ta, ReadLine$(f)
		If Not Eof(f) Then AddTextAreaText ta, ent$
	Wend
	CloseFile f
	UnlockTextArea ta
End Function

Function doc_save()
	f = WriteFile(filename$)
	WriteLine f, TextAreaText$(ta)
	CloseFile f
End Function
