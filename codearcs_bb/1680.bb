; ID: 1680
; Author: splinux
; Date: 2006-04-19 13:19:43
; Title: Graphics fonts library
; Description: A library to load/draw bitmap fonts (also not-monospace)

Const CONST_MAX_CHARS = 256
; Max num of characters in a font

Type tFont
	Field chars%[CONST_MAX_CHARS]
; Average of all characters' width
	Field avgWidth#
	Field height%
; First and last characters in the font ASCII codes:
; It speeds up the loading
	Field init%, finish%
End Type

Function font_create%(src$, init% = 32, finish% = 127, r% = 128, g% = 0, b% = 128)
	Local i%, avgW#, f.tFont = New tFont
	If finish > CONST_MAX_CHARS Or finish < init Return 0
	If Right(src, 1) = "/" Or Right(src, 1) = "\" src = Left(src, Len(src) - 1)
	For i = init To finish
		If LoadImage(src + "/" + src + "." + i + ".png")
			If\chars[i] = LoadImage(src + "/" + src + "." + i + ".png")
			MaskImage f\chars[i], r, g, b
		EndIf
	Next
	f\init = init
	f\finish = finish
	f\height = ImageHeight(f\chars[init])
	For i = init To finish
		If avgW = 0 avgW = ImageWidth(f\chars[i]) Else avgW = (avgW + ImageWidth(f\chars[i]))/2
	Next
	f\avgWidth = avgW
	Return Handle(f)
End Function

Function font_delete%(f%)
	If Object.tFont(f) = Null Return 0
	Local g.tFont = Object.tFont(f)
	g = Null
	Delete g
	Return 1
End Function

Function font_getAVGW#(f%)
	If Object.tFont(f) = Null Return 0
	Local g.tFont = Object.tFont(f)
	Return g\avgWidth
End Function

Function font_getWidth%(f%, txt$)
; This function returns the width of a given string
	If Object.tFont(f) = Null Return 0
	Local i%, width%, g.tFont = Object.tFont(f)
	Local char%; lenght% = Len(txt)
	For i = 1 To lenght
		char = Asc(Mid(txt, i, 1))
		width = width + ImageWidth(g\chars[char])
	Next
	Return width
End Function

Function font_getHeight%(f%)
	If Object.tFont(f) = Null Return 0
	Local g.tFont = Object.tFont(f)
	Return g\height
End Function

Function font_draw%(x%, y%, txt$, f%, cx% = 0, cy% = 0)
	If Object.tFont(f) = Null Return 0
	Local g.tFont = Object.tFont(f)
	Local width = font_getWidth(f, txt)
	If cx x = x - Int(width/2)
	If cy y = y - Int(g\height/2)
	Local i%, image%, lenght% = Len(txt)
	For i = 1 To lenght
; Draw each character
		image = g\chars[Asc(Mid(txt, i, 1))]
		DrawImage image, x, y
		x = x + ImageWidth(image)
	Next
	Return width
End Function
