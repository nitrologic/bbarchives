; ID: 2693
; Author: Luke111
; Date: 2010-04-06 17:18:37
; Title: Random Word Generator
; Description: Not All That Finished, But Works!!!

Global g$ = ""
Function getLetter(i%)
	If i% = 1 Then
		a$ = "a"
	ElseIf i% = 2 Then
		a$ = "b"
	ElseIf i% = 3 Then
		a$ = "c"
	ElseIf i% = 4 Then
		a$ = "d"
	ElseIf i% = 5 Then
		a$ = "e"
	ElseIf i% = 6 Then
		a$ = "f"
	ElseIf i% = 7 Then
		a$ = "g"
	ElseIf i% = 8 Then
		a$ = "h"
	ElseIf i% = 9 Then
		a$ = "i"
	ElseIf i% = 10 Then
		a$ = "j"
	ElseIf i% = 11 Then
		a$ = "k"
	ElseIf i% = 12 Then
		a$ = "l"
	ElseIf i% = 13 Then
		a$ = "m"
	ElseIf i% = 14 Then
		a$ = "n"
	ElseIf i% = 15 Then
		a$ = "o"
	ElseIf i% = 16 Then
		a$ = "p"
	ElseIf i% = 17 Then
		a$ = "q"
	ElseIf i% = 18 Then
		a$ = "r"
	ElseIf i% = 19 Then
		a$ = "s"
	ElseIf i% = 20 Then
		a$ = "t"
	ElseIf i% = 21 Then
		a$ = "u"
	ElseIf i% = 22 Then
		a$ = "v"
	ElseIf i% = 23 Then
		a$ = "w"
	ElseIf i% = 24 Then
		a$ = "x"
	ElseIf i% = 25 Then
		a$ = "y"
	ElseIf i% = 26 Then
		a$ = "z"
	EndIf
	g$ = g$+a$
End Function
SeedRnd MilliSecs()
.loop
l% = 0
h% = Input$("Words? - ")
While h% <> l%
l% = l%+1
a% = Rand(1,26)
b% = Rand(1,26)
c% = Rand(1,26)
d% = Rand(1,26)
e% = Rand(1,26)
f% = Rand(1,26)
.loop1
If a% = b% Then
	b% = Rand(1,26)
	Goto loop1
EndIf
If b% = c% Then
	c% = Rand(1,26)
	Goto loop1
EndIf
If c% = d% Then
	d% = Rand(1,26)
	Goto loop1
EndIf
If d% = e% Then
	e% = Rand(1,26)
	Goto loop1
EndIf
If e% = f% Then
	f% = Rand(1,26)
	Goto loop1
EndIf
getLetter(a%)
getLetter(b%)
getLetter(c%)
getLetter(d%)
getLetter(e%)
getLetter(f%)
Print g$
g$ = ""
Wend
Goto loop
