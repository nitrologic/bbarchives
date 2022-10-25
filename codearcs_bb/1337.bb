; ID: 1337
; Author: Rob Farley
; Date: 2005-03-24 14:35:19
; Title: Debug Onscreen Text thingy
; Description: Builds up a bunch of text to display on screen in one hit.

Function debugtext(txt$)
	If Right(txt,1) <> "|" Then txt = txt + "|"
	ty=0
	Repeat
		tx=Instr(txt,"|")
		If tx>0
			Text 0,ty,Left(txt,tx-1)
			txt = Right(txt,Len(txt)-tx)
			ty=ty + 10
		EndIf
	Until tx = 0
	debugtxt = ""
End Function
