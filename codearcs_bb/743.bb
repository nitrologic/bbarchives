; ID: 743
; Author: zawran
; Date: 2003-07-10 12:08:07
; Title: Lines around ball
; Description: Just some fun with lines and types

;	Created on July 10th 2003 by Zawran
;	feel free to use and change

Graphics 800,600,32,2

SeedRnd MilliSecs()

Type lines
	Field a#,c,z#,d
	End Type

SetBuffer BackBuffer() 

While Not KeyHit(1) 

If linesnew + 70 <= MilliSecs() Then
	linesnew = MilliSecs()
	newLines()
	End If

For l.lines = Each lines
	Color 0,0,l\c
	Line 400+Sin(l\a)*40,300+Cos(l\a)*40,400+Sin(l\a)*l\z,300+Cos(l\a)*l\z
Next

If linesupd + 13 <= MilliSecs() Then
	linesupd = MilliSecs()
	For l.lines = Each lines
		If l\d = 1 Then l\a = l\a + Rnd(1,2)
		If l\d = 2 Then l\a = l\a - Rnd(1,2)
		If l\a < 0 Then l\a = l\a + 360
		If l\a > 359 Then l\a = l\a + 360
		l\z = l\z - 1.8
		l\c = l\c - 2
		If l\c < 0 Then Delete l
	Next
	End If

Color 255,255,255
If Timer + 1000 <= MilliSecs() Timer = MilliSecs() : FPS_Real = FPS_Temp : FPS_Temp = 0
FPS_Temp = FPS_Temp + 1 : Text 0,580,"FPS: " + FPS_Real

Flip
Cls
Wend
Delete Each lines
End

Function newLines()
ang = Rnd(359)
For a=0 To 29
	l.lines = New lines
	l\a = ang+Rnd(-5,5)
	l\c = Rnd(150,255)
	l\z = Rnd(200,256)
	l\d = Rnd(1,2)
Next
End Function
