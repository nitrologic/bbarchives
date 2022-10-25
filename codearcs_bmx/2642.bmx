; ID: 2642
; Author: Arowx
; Date: 2010-01-18 08:21:11
; Title: Random Password Generator
; Description: Generates a grid of random ascii characters...

'Arowx : Password Generator - 
' ascii characters from 33(!) to 126(~) randomly shuffled in a 10x10 grid

Framework brl.max2d
Import brl.random
Import BRL.D3D9Max2D

Const number = 3473992
seed = MilliSecs()*number

SeedRnd(seed)

AppTitle = "Codegrid Generator"

Graphics 230,230

Repeat

Cls
	SeedRnd(seed)
	
	For x = 0 To 9
		For y = 0 To 9
			DrawText Chr(Rand(33,126)), x*20+20, y*20+20
		Next
	Next

	If MouseDown(1)
		seed = MilliSecs()*number
	EndIf

Flip

Until AppTerminate()
