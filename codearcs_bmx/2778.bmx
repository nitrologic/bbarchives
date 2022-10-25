; ID: 2778
; Author: jankupila
; Date: 2010-10-14 05:12:27
; Title: Grid
; Description: Grid

Strict

Graphics 1024,768

Type cells
	Field x
	Field y
	Method piirra()
		SetColor 255,2552,255
		DrawLine x,y,x,y+20
		DrawLine x,y,x+20,y
		SetColor 80,80,80
		DrawLine x+20,y,x+20,y+20
		DrawLine x,y+20,x+20,y+20
		DrawRect x+1,y+1,18,18
	End Method
End Type



Local cell:cells=New cells

For Local t=100 To 924 Step 22
	For Local i=100 To 668 Step 22
		cell.x=t
		cell.y=i
		cell.piirra()
	Next
Next

Flip

WaitKey
