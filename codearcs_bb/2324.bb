; ID: 2324
; Author: Devils Child
; Date: 2008-10-02 10:16:16
; Title: Sudoku solver
; Description: Solves a sudoku riddle in 1 millisec

Graphics 900, 900, 32, 2
SetBuffer BackBuffer()
AppTitle "Sudoku solver"
SetFont LoadFont("Arial Black", 90, True)

Dim Grid(9, 9), Pos(9, 9, 9)
LoadLevel("Level1.txt")

Select 1 ;<========== set to '2' to see slow motion!
	Case 1
		ms = MilliSecs()
		Repeat
			UpdatePos()
			ApplyPos()
		Until IsSolved()
		ms = MilliSecs() - ms
		AppTitle "SOLVED in " + ms + " ms."
		DrawLevel()
	Case 2
		Repeat
			Cls
			DrawLevel()
			UpdatePos()
			ApplyPos()
			Delay 300
		Until IsSolved()
		AppTitle "SOLVED."
		DrawLevel()
End Select

WaitKey()
End

Function LoadLevel(path$)
file = ReadFile(path$)
For y = 1 To 9
	l$ = ReadLine(file)
	For x = 1 To 9
		Grid(x, y) = Mid(l$, x, 1)
		For i = 1 To 9
			Pos(x, y, i) = True
		Next
	Next
Next
CloseFile file
End Function

Function DrawLevel()
Cls
Color 255, 255, 255
For x = 1 To 9
	For y = 1 To 9
		ch$ = Grid(x, y)
		If ch$ = 0 Then ch$ = " "
		Text x * 90 - 30, y * 90 - 30, ch$
	Next
Next
Color 127, 127, 127
For x = 0 To 2
	For y = 0 To 2
		Rect 40 + x * 270, 55 + y * 270, 271, 271, False
	Next
Next
Flip 0
End Function

Function UpdatePos()
;Rows
For y = 1 To 9
	For x = 1 To 9
		For i = 1 To 9
			If Grid(i, y) Then
				Pos(x, y, Grid(i, y)) = False
			EndIf
		Next
	Next
Next
;Cols
For x = 1 To 9
	For y = 1 To 9
		For i = 1 To 9
			If Grid(x, i) Then
				Pos(x, y, Grid(x, i)) = False
			EndIf
		Next
	Next
Next
;Fields
For y = 1 To 9
	Select y
		Case 1, 2, 3: fy = 1
		Case 4, 5, 6: fy = 4
		Case 7, 8, 9: fy = 7
	End Select
	For x = 1 To 9
		Select x
			Case 1, 2, 3: fx = 1
			Case 4, 5, 6: fx = 4
			Case 7, 8, 9: fx = 7
		End Select
		For i = fx To fx + 2
			For a = fy To fy + 2
				If Grid(i, a) Then
					Pos(x, y, Grid(i, a)) = False
				EndIf
			Next
		Next
	Next
Next
End Function

Function ApplyPos()
For x = 1 To 9
	For y = 1 To 9
		cnt = 0
		For i = 1 To 9
			If Pos(x, y, i) Then
				cnt = cnt + 1
				res = i
			EndIf
		Next
		If cnt = 1 And Grid(x, y) = 0 Then
			Grid(x, y) = res
		EndIf
	Next
Next
End Function

Function IsSolved()
For x = 1 To 9
	For y = 1 To 9
		If Grid(x, y) = 0 Then Return False
	Next
Next
Return True
End Function
