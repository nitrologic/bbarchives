; ID: 3032
; Author: misth
; Date: 2013-02-17 08:30:26
; Title: GetWord() &amp; CountWords()
; Description: Functions for easy and accurate parsing.

SuperStrict

' FUNCTIONS

' Return word from sentence
Function GetWord:String(_line:String, index:Int, separ:String = " ")
	Return _line.Split(separ)[index - 1]
End Function

' Return word count in sentence
Function CountWords:Int(_line:String, separ:String = " ")
	Return _line.Split(separ).Length
End Function


' EXAMPLE PROGRAM as speed-test

Local line:String = "I am a sentence. You can split me any way you want."
Const TESTS:Int = 500000
Local start:Int, _end:Int

For Local i:Int = 1 To CountWords(Line)
	Print GetWord(line, i)
Next

Print "GetWord test start"
start = MilliSecs()
For Local i:Int = 1 To TESTS
	GetWord(Line, 1)
Next
_end = (MilliSecs() - start)
Print TESTS + " GetWord tests in " + _end + "ms"

Print "CountWords test start"
start = MilliSecs()
For Local i:Int = 1 To TESTS
	CountWords(line, 1)
Next
_end = (MilliSecs() - start)
Print TESTS + " CountWords tests in " + _end + "ms"
