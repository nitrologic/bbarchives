; ID: 2487
; Author: superStruct
; Date: 2009-05-23 13:51:41
; Title: Key Logging Noise
; Description: Program that uses the SystemBeep.dll

Graphics 80,100,0,2
SetBuffer BackBuffer()
AppTitle "KeyNoise"

Global x
Global y
Global counter = 0
Global Key

Dim keymem(1000)

While Not KeyDown(1)
	Cls
	Key = GetKey()
	If Key <> 0 
		keymem(counter) = Key
		SystemBeep(Key*5,Key)
		counter = counter + 1
	EndIf
	If KeyHit(14)
		keymem(counter) = 0
		keymem(counter - 1) = 0
		counter = counter - 2
	EndIf 
	If KeyHit(28) = 1
		For i = 0 To counter
			SystemBeep(keymem(i)*5,keymem(i))
		Next
	EndIf
	If KeyHit(211)
		For i = 0 To counter
			keymem(i) = 0
		Next
	EndIf
Wend
