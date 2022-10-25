; ID: 2528
; Author: superStruct
; Date: 2009-07-14 08:30:49
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
Const freq = 400

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
	For m = 1 To Len(PriMsg)
		temp$ = Right(Left(PriMsg,m),1)
		If temp = "a" Or temp = "A"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "b" Or temp = "B"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "c" Or temp = "C"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "d" Or temp = "D"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "e" Or temp = "E"
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "f" Or temp = "F"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "g" Or temp = "G"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "h" Or temp = "H"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
		ElseIf temp = "i" Or temp = "I"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "j" Or temp = "J"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "k" Or temp = "K"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "l" Or temp = "L"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "m" Or temp = "M"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "n" Or temp = "N"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "o" Or temp = "O"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "p" Or temp = "P"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "q" Or temp = "Q"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "r" Or temp = "R"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "s" Or temp = "S"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "t" Or temp = "T"
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "u" Or temp = "U"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "v" Or temp = "V"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "w" Or temp = "W"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "x" Or temp = "X"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "y" Or temp = "Y"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "z" Or temp = "Z"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = " "
			Delay(100)
		ElseIf temp = "1"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "2"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "3"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "4"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		ElseIf temp = "5"
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "6"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "7"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "8"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "9"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dot)
			Delay(50)
		ElseIf temp = "0"
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
			SystemBeep(freq,dash)
			Delay(50)
		EndIf		
	Next
End Function
