; ID: 699
; Author: Jim Brown
; Date: 2003-05-21 14:30:46
; Title: Clipboard - Text Copy &amp; Paste
; Description: Two functions to read & write to through the clipboard

; Clipboard Text Read / Write
; ===========================
; Syntax Error & Ed from Mars


; userlib declarations - 'user32.decls'
; *********************************************
; .lib "user32.dll"
; OpenClipboard%(hwnd%):"OpenClipboard"
; CloseClipboard%():"CloseClipboard"
; ExamineClipboard%(format%):"IsClipboardFormatAvailable"
; EmptyClipboard%():"EmptyClipboard"
; GetClipboardData$(format%):"GetClipboardData"
; SetClipboardData%(format%,txt$):"SetClipboardData"
; *********************************************


Print "Clipboard Test." 
Print "~~~~~~~~~~~~~~~" 
Print "Enter a message for the clipboard." 
Print "Alternatively, leave BLANK to read clipboard." 
a$=Input$(">") 

If a$="" 
	a$=ReadClipboardText$() 
	Print a$
Else 
	WriteClipboardText a$ 
	Print "Text sent to clipboard. Open NotePad and paste!"
EndIf

Print Chr$(13)+"---------------------------------"

a$=Input$("Press RETURN to end ...") 

End

;-----------------------------------

Function WriteClipboardText(txt$)
	Local cb_TEXT=1
	If txt$="" Then Return 
	If OpenClipboard(0)
		EmptyClipboard
		SetClipboardText cb_TEXT,txt$
		CloseClipboard
	EndIf
End Function

;-----------------------------------

Function ReadClipboardText$()
	Local cb_TEXT=1
	Local txt$=""
	If OpenClipboard(0)
		If ExamineClipboard(cb_TEXT) 
			txt$=GetClipboardText$(cb_TEXT)
		EndIf
		CloseClipboard
	EndIf
	Return txt$
End Function
