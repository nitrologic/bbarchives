; ID: 1216
; Author: Chroma
; Date: 2004-12-01 13:43:34
; Title: Get what's on the other side of the equal sign even faster.
; Description: Shorter, faster than ever before.

Function ReadInfo$(txt$,sep$="=")
	Return Mid$(txt$,Instr(txt$,sep$)+1)
End Function
