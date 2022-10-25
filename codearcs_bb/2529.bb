; ID: 2529
; Author: superStruct
; Date: 2009-07-14 08:32:16
; Title: Morse Code Converter
; Description: A program to take text and turn it into morse code

Graphics 1200,400,0,2
SetBuffer BackBuffer()

Global PriMsg$
Global SndMsg$
Global command$
Global TKey%
Global MorsMsg$

Const dot = 50
Const dash = 150
Const freq = 500

Dim textline$(100)

Print ""

While Not KeyDown(1)
	Cls 
	Check_Keys()
	Text 0,0,">:" + PriMsg$
	Text 0,24,MorsMsg
	Flip 
Wend 

Function Check_Keys()
	;Grab any keys that get pressed
	TKey% = GetKey()
	
	If TKey <> 0 Then 
		If Tkey <> 8 Then ;8 = backspace key
			;convert pressed key To the actual character
			TChr$ = Chr$(TKey)
			MorsMsg = ""
			;append String with Last key pressed
			PriMsg = PriMsg + TChr
		EndIf
	EndIf
	
	If KeyDown(14)
		If Len(PriMsg) > 0 Then PriMsg = Left(PriMsg,Len(PriMsg)-1)
		Delay(100)
	EndIf
	
	If KeyDown(200) Or KeyDown(208)
		PriMsg = PriMsg + ""
	EndIf
	
	;hitting enter key will send contents of sndmsg to client/server
	If KeyHit(28) Then ;Enter key
		SndMsg = PriMsg
		MorseConvert()
		MorseSound()
		PriMsg = ""
	EndIf
	
End Function

Function MorseConvert()
	For i = 1 To Len(PriMsg)
		temp$ = Right(Left(PriMsg,i),1)
		If temp = "a" Or temp = "A"
			MorsMsg = MorsMsg + ".- "
		ElseIf temp = "b" Or temp = "B"
			MorsMsg = MorsMsg + "-... "
		ElseIf temp = "c" Or temp = "C"
			MorsMsg = MorsMsg + "-.-. "
		ElseIf temp = "d" Or temp = "D"
			MorsMsg = MorsMsg + "-.. "
		ElseIf temp = "e" Or temp = "E"
			MorsMsg = MorsMsg + ". "
		ElseIf temp = "f" Or temp = "F"
			MorsMsg = MorsMsg + "..-. "
		ElseIf temp = "g" Or temp = "G"
			MorsMsg = MorsMsg + "--. "
		ElseIf temp = "h" Or temp = "H"
			MorsMsg = MorsMsg + ".... "
		ElseIf temp = "i" Or temp = "I"
			MorsMsg = MorsMsg + ".. "
		ElseIf temp = "j" Or temp = "J"
			MorsMsg = MorsMsg + ".--- "
		ElseIf temp = "k" Or temp = "K"
			MorsMsg = MorsMsg + "-.- "
		ElseIf temp = "l" Or temp = "L"
			MorsMsg = MorsMsg + ".-.. "
		ElseIf temp = "m" Or temp = "M"
			MorsMsg = MorsMsg + "-- "
		ElseIf temp = "n" Or temp = "N"
			MorsMsg = MorsMsg + "-. "
		ElseIf temp = "o" Or temp = "O"
			MorsMsg = MorsMsg + "--- "
		ElseIf temp = "p" Or temp = "P"
			MorsMsg = MorsMsg + ".--. "
		ElseIf temp = "q" Or temp = "Q"
			MorsMsg = MorsMsg + "--.- "
		ElseIf temp = "r" Or temp = "R"
			MorsMsg = MorsMsg + ".-. "
		ElseIf temp = "s" Or temp = "S"
			MorsMsg = MorsMsg + "... "
		ElseIf temp = "t" Or temp = "T"
			MorsMsg = MorsMsg + "- "
		ElseIf temp = "u" Or temp = "U"
			MorsMsg = MorsMsg + "..- "
		ElseIf temp = "v" Or temp = "V"
			MorsMsg = MorsMsg + ".-- "
		ElseIf temp = "w" Or temp = "W"
			MorsMsg = MorsMsg + ".--.. "
		ElseIf temp = "x" Or temp = "X"
			MorsMsg = MorsMsg + "-..- "
		ElseIf temp = "y" Or temp = "Y"
			MorsMsg = MorsMsg + "-.-- "
		ElseIf temp = "z" Or temp = "Z"
			MorsMsg = MorsMsg + "--.. "
		ElseIf temp = " "
			MorsMsg = MorsMsg + "| "
		ElseIf temp = "1"
			MorsMsg = MorsMsg + ".---- "
		ElseIf temp = "2"
			MorsMsg = MorsMsg + "..--- "
		ElseIf temp = "3"
			MorsMsg = MorsMsg + "...-- "
		ElseIf temp = "4"
			MorsMsg = MorsMsg + "....- "
		ElseIf temp = "5"
			MorsMsg = MorsMsg + "..... "
		ElseIf temp = "6"
			MorsMsg = MorsMsg + "-.... "
		ElseIf temp = "7"
			MorsMsg = MorsMsg + "--... "
		ElseIf temp = "8"
			MorsMsg = MorsMsg + "---.. "
		ElseIf temp = "9"
			MorsMsg = MorsMsg + "----. "
		ElseIf temp = "0"
			MorsMsg = MorsMsg + "----- "
		EndIf		
	Next
End Function

Function MorseSound()
  For i = 0 To Len(MorsMsg)
	temp$ = Right(Left(MorsMsg,i),1) 
	If temp = "-"
		SystemBeep(freq, dash)
	ElseIf temp = "."
		SystemBeep(freq, dot)
	ElseIf temp = " "
		Delay(50)
	EndIf
	Delay(50)
  next
End Function
