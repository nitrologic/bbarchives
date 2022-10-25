; ID: 2617
; Author: Marcell
; Date: 2009-11-29 14:19:03
; Title: Retro numbers
; Description: Number printing

; 29.11.2009
;
; Coded by "Marcell"
;
; Public Domain
;

Graphics 800,600

Global offsetx

; We have numbers from 0 to 9;
; each index represents one number between 0-9
Dim bankki(10)


; Here we create banks for every number
For k = 0 To 9
	bankki(k) = CreateBank(3*5)
Next

; Here we read number-datas to banks
For k = 0 To 9
	For i = 0 To 3*5 - 1
		Read number
		PokeByte (bankki(k), i,number)
	Next
Next


; Here we create "block" for filling every "1"
; in the banks
Color 255,255,255
Global gfxblock = CreateImage(16,16)
SetBuffer ImageBuffer(gfxblock)
Rect 0,0,16,16

SetBuffer FrontBuffer()


While Not KeyHit(1)

	; This can be "any" number >= 0
	CNumber$ = "500"
	
	; Number length
	For i = 1 To Len(CNumber$)
		; Reads CNumber$ through for every digit of the CNumber
	 	n$ = Mid$(CNumber$,i,1)
		; Converts string to int value
		value = Int(n$)
		; Call function that draws a number
		printNumber(value)
	Next
	

	WaitKey
	
	VWait	
Wend
End


Function printNumber(index)


For j = 0 To 5 - 1
	For i = 0 To 3 - 1
		number = PeekByte(bankki(index),i + 3 * j)
		; If found 1, draw block
		If number = 1 Then DrawImage gfxBlock,i*15 + offsetx,j*15
	Next
Next

offsetx = offsetx + 50

End Function


; Datas for the numbers
.datas

; 0
Data 1,1,1
Data 1,0,1
Data 1,0,1
Data 1,0,1
Data 1,1,1
; 1
Data 0,0,1
Data 0,1,1
Data 0,0,1
Data 0,0,1
Data 0,0,1

; 2
Data 1,1,1
Data 0,0,1
Data 0,1,0
Data 1,0,0
Data 1,1,1

; 3
Data 1,1,1
Data 0,0,1
Data 1,1,1
Data 0,0,1
Data 1,1,1

; 4
Data 0,0,1
Data 0,1,1
Data 1,0,1
Data 1,1,1
Data 0,0,1

; 5
Data 1,1,1
Data 1,0,0
Data 1,1,1
Data 0,0,1
Data 1,1,1

; 6
Data 1,1,1
Data 1,0,0
Data 1,1,1
Data 1,0,1
Data 1,1,1

; 7
Data 1,1,1
Data 0,0,1
Data 0,1,1
Data 0,0,1
Data 0,0,1

; 8
Data 1,1,1
Data 1,0,1
Data 1,1,1
Data 1,0,1
Data 1,1,1

; 9
Data 1,1,1
Data 1,0,1
Data 1,1,1
Data 0,0,1
Data 1,1,1
