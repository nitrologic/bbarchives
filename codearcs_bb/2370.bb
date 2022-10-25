; ID: 2370
; Author: Nate the Great
; Date: 2008-12-10 02:54:40
; Title: Tic tac toe that learns
; Description: Learns... slowly but surely

; ID: 2370
; Author: Nate the Great
; Date: 2008-12-10 02:54:40
; Title: Tic tac toe that learns
; Description: Learns... slowly but surely

SeedRnd(MilliSecs())
Graphics 160,120,0,2

Dim mov(9)
Dim grid(3,3)
Dim moved(9)

Global membankcount = 0, test = 0

Type game
	Field m[9],winner
End Type

cnt = 0
While ext <> 1
fileopn$ = "membank" + cnt + ".mem"
cnt = cnt + 1
file = OpenFile(fileopn$)
If Not file = 0 Then
	g.game = New game
	g\m[1] = ReadInt(file)
	g\m[2] = ReadInt(file)
	g\m[3] = ReadInt(file)
	g\m[4] = ReadInt(file)
	g\m[5] = ReadInt(file)
	g\m[6] = ReadInt(file)
	g\m[7] = ReadInt(file)
	g\m[8] = ReadInt(file)
	g\m[9] = ReadInt(file)
	g\winner = ReadInt(file)
	membankcount = membankcount + 1
	CloseFile(file)
	file = OpenFile(fileopn$)
	g.game = New game
	g\m[7] = ReadInt(file)
	g\m[4] = ReadInt(file)
	g\m[1] = ReadInt(file)
	g\m[8] = ReadInt(file)
	g\m[5] = ReadInt(file)
	g\m[2] = ReadInt(file)
	g\m[9] = ReadInt(file)
	g\m[6] = ReadInt(file)
	g\m[3] = ReadInt(file)
	g\winner = ReadInt(file)
	CloseFile(file)
	file = OpenFile(fileopn$)
	g.game = New game
	g\m[9] = ReadInt(file)
	g\m[8] = ReadInt(file)
	g\m[7] = ReadInt(file)
	g\m[6] = ReadInt(file)
	g\m[5] = ReadInt(file)
	g\m[4] = ReadInt(file)
	g\m[3] = ReadInt(file)
	g\m[2] = ReadInt(file)
	g\m[1] = ReadInt(file)
	g\winner = ReadInt(file)
	CloseFile(file)
	file = OpenFile(fileopn$)
	g.game = New game
	g\m[3] = ReadInt(file)
	g\m[6] = ReadInt(file)
	g\m[9] = ReadInt(file)
	g\m[2] = ReadInt(file)
	g\m[5] = ReadInt(file)
	g\m[8] = ReadInt(file)
	g\m[1] = ReadInt(file)
	g\m[4] = ReadInt(file)
	g\m[7] = ReadInt(file)
	g\winner = ReadInt(file)
	g.game = New game
	g\m[3] = ReadInt(file)
	g\m[2] = ReadInt(file)
	g\m[1] = ReadInt(file)
	g\m[6] = ReadInt(file)
	g\m[5] = ReadInt(file)
	g\m[4] = ReadInt(file)
	g\m[9] = ReadInt(file)
	g\m[8] = ReadInt(file)
	g\m[7] = ReadInt(file)
	g\winner = ReadInt(file)
	CloseFile(file)
	file = OpenFile(fileopn$)
	g.game = New game
	g\m[9] = ReadInt(file)
	g\m[6] = ReadInt(file)
	g\m[3] = ReadInt(file)
	g\m[8] = ReadInt(file)
	g\m[5] = ReadInt(file)
	g\m[2] = ReadInt(file)
	g\m[7] = ReadInt(file)
	g\m[4] = ReadInt(file)
	g\m[1] = ReadInt(file)
	g\winner = ReadInt(file)
	CloseFile(file)
	file = OpenFile(fileopn$)
	g.game = New game
	g\m[7] = ReadInt(file)
	g\m[8] = ReadInt(file)
	g\m[9] = ReadInt(file)
	g\m[4] = ReadInt(file)
	g\m[5] = ReadInt(file)
	g\m[6] = ReadInt(file)
	g\m[1] = ReadInt(file)
	g\m[2] = ReadInt(file)
	g\m[3] = ReadInt(file)
	g\winner = ReadInt(file)
	CloseFile(file)
	file = OpenFile(fileopn$)
	g.game = New game
	g\m[1] = ReadInt(file)
	g\m[4] = ReadInt(file)
	g\m[7] = ReadInt(file)
	g\m[2] = ReadInt(file)
	g\m[5] = ReadInt(file)
	g\m[8] = ReadInt(file)
	g\m[3] = ReadInt(file)
	g\m[6] = ReadInt(file)
	g\m[9] = ReadInt(file)
	g\winner = ReadInt(file)
CloseFile(file)
Else
	ext = 1
EndIf

Wend
tmprndnum = Rnd(100)
If tmprndnum > 50 Then
	tmprndnum = 1
Else
	tmprndnum = 0
EndIf

Global move = tmprndnum,num
Global start = move

SetBuffer BackBuffer()
Cls

AppTitle("Tic-Tac-Toe")
Text 1,1, "Tic-Tac-Toe"
;Text 1,20,"Press a key."

Flip

;WaitKey()

;Delay 1000
FlushKeys()

Cls
Text 1,1, "Tic-Tac-Toe"
Line 14,40,14,95
Line 34,40,34,95
Line 1,54,54,54
Line 1,74,54,74

Flip

ext = 0
While Not ext
Cls

If KeyDown(1) Then ext = True

Text 1,1, "Tic-Tac-Toe"

;If move = 0 Then
If move = 0 Then
Goto wngplc2
.wngplc
num = num - 1
.wngplc2
	WaitKey()
	
	num = num + 1
	
	If KeyHit(2) Then
		If moved(1) = 0 Then
			mov(num) = 1
			moved(1) = 1
		Else
			FlushKeys()
			Goto wngplc
		EndIf
	ElseIf KeyHit(3)
		If moved(2) = 0 Then
			mov(num) = 2
			moved(2) = 1
		Else
			FlushKeys()
			Goto wngplc
		EndIf

	ElseIf KeyHit(4)
		If moved(3) = 0 Then
			mov(num) = 3
			moved(3) = 1
		Else
			FlushKeys()
			Goto wngplc
		EndIf

	ElseIf KeyHit(16)
		If moved(4) = 0 Then
			mov(num) = 4
			moved(4) = 1
		Else
			FlushKeys()
			Goto wngplc
		EndIf

	ElseIf KeyHit(17)
		If moved(5) = 0 Then
			mov(num) = 5
			moved(5) = 1
		Else
			FlushKeys()
			Goto wngplc
		EndIf

	ElseIf KeyHit(18)
		If moved(6) = 0 Then
			mov(num) = 6
			moved(6) = 1
		Else
			FlushKeys()
			Goto wngplc
		EndIf

	ElseIf KeyHit(30)
		If moved(7) = 0 Then
			mov(num) = 7
			moved(7) = 1
		Else
			FlushKeys()
			Goto wngplc
		EndIf

	ElseIf KeyHit(31)
		If moved(8) = 0 Then
			mov(num) = 8
			moved(8) = 1
		Else
			FlushKeys()
			Goto wngplc
		EndIf

	ElseIf KeyHit(32)
		If moved(9) = 0 Then
			mov(num) = 9
			moved(9) = 1
		Else
			FlushKeys()
			Goto wngplc
		EndIf

	EndIf
		
Else
	Delay 400
	Goto wngplc4
.wngplc3
	num = num - 1
.wngplc4
	match = 0
	smmove = 0
	count = 0
	For g.game = Each game
		count = count + 1
		same = 0
		If g\winner = 1 And start = 1 And num > 0 Then
			same = 1
			For a = 1 To num
				If g\m[a] <> mov(a) Then same = 0
			Next
		ElseIf g\winner = 2 And start = 0 And num > 0 Then
			same = 1
			For a = 1 To num
				If g\m[a] <> mov(a) Then same = 0
			Next
		EndIf
		If num = 0 And g\winner = 1 Then
			same = 1
		EndIf
		
		If same = 1 Then
			chnce = Rnd(0,10)
			If chnce > 5 Then
				smmove = g\m[num+1]
				test = True
			EndIf
		Else
			Delete g.game
		EndIf
	Next
	If smmove = 0 Then
		tmppic = Rnd(1,9)
	Else
		tmppic = smmove
	EndIf
	
	num = num + 1
	
	If tmppic = 1 Then
		If moved(1) = 0 Then
			mov(num) = 1
			moved(1) = 1
		Else
			FlushKeys()
			Goto wngplc3
		EndIf
	ElseIf tmppic = 2 Then
		If moved(2) = 0 Then
			mov(num) = 2
			moved(2) = 1
		Else
			FlushKeys()
			Goto wngplc3
		EndIf

	ElseIf  tmppic = 3 Then
		If moved(3) = 0 Then
			mov(num) = 3
			moved(3) = 1
		Else
			FlushKeys()
			Goto wngplc3
		EndIf

	ElseIf  tmppic = 4 Then
		If moved(4) = 0 Then
			mov(num) = 4
			moved(4) = 1
		Else
			FlushKeys()
			Goto wngplc3
		EndIf

	ElseIf  tmppic = 5 Then
		If moved(5) = 0 Then
			mov(num) = 5
			moved(5) = 1
		Else
			FlushKeys()
			Goto wngplc3
		EndIf

	ElseIf  tmppic = 6 Then
		If moved(6) = 0 Then
			mov(num) = 6
			moved(6) = 1
		Else
			FlushKeys()
			Goto wngplc3
		EndIf

	ElseIf  tmppic = 7 Then
		If moved(7) = 0 Then
			mov(num) = 7
			moved(7) = 1
		Else
			FlushKeys()
			Goto wngplc3
		EndIf

	ElseIf  tmppic = 8 Then
		If moved(8) = 0 Then
			mov(num) = 8
			moved(8) = 1
		Else
			FlushKeys()
			Goto wngplc3
		EndIf

	ElseIf  tmppic = 9 Then
		If moved(9) = 0 Then
			mov(num) = 9
			moved(9) = 1
		Else
			FlushKeys()
			Goto wngplc3
		EndIf

	EndIf


EndIf

For a = 1 To 9
	If a = 1 Or a = 3 Or a = 5 Or a = 7 Or a = 9 Then
		Select mov(a)
		Case 1
			Text 1,40,"X"
		Case 2
			Text 20,40,"X"
		Case 3
			Text 40,40,"X"
		Case 4
			Text 1,60,"X"
		Case 5
			Text 20,60,"X"
		Case 6
			Text 40,60,"X"
		Case 7
			Text 1,80,"X"
		Case 8
			Text 20,80,"X"
		Case 9
			Text 40,80,"X"
		End Select
	Else
		Select mov(a)
		Case 1
			Text 1,40,"O"
		Case 2
			Text 20,40,"O"
		Case 3
			Text 40,40,"O"
		Case 4
			Text 1,60,"O"
		Case 5
			Text 20,60,"O"
		Case 6
			Text 40,60,"O"
		Case 7
			Text 1,80,"O"
		Case 8
			Text 20,80,"O"
		Case 9
			Text 40,80,"O"
		End Select
	EndIf
Next

Line 14,40,14,95
Line 34,40,34,95
Line 1,54,54,54
Line 1,74,54,74

move = Not move

If checkwin() Then ext = 1

If num = 9 Then ext = 1
Text 1,20,count
Flip
Wend

winner = checkwin()
Cls
If winner = 1
	If start = 0
		Text 1,1,"Player Wins"
	Else
		Text 1,1,"Computer Wins"
	EndIf
ElseIf winner = 2
	If start = 1
		Text 1,1,"Player Wins"
	Else
		Text 1,1,"Computer Wins"
	EndIf
EndIf


fileopn$ = "membank" + membankcount + ".mem"
filetmp = WriteFile(fileopn$)

For a = 1 To 9
	WriteInt(filetmp,mov(a))
Next
WriteInt(filetmp,winner)

Delay 1000
Flip
Delay 1000
End








Function checkwin()
win = 0

For x = 1 To 3
	For y = 1 To 3
		grid(x,y) = 0
	Next
Next

For a = 1 To 9
	If mov(a) > 0 Then
	If a = 1 Or a = 3 Or a = 5 Or a = 7 Or a = 9 Then
		If mov(a) > 3 And mov(a) < 7
			grid(2,mov(a)-3) = 1
		ElseIf mov(a) > 6
			grid(3,mov(a)-6) = 1
		ElseIf mov(a) < 4
			grid(1,mov(a)) = 1
		EndIf
	Else
		If mov(a) > 3 And mov(a) < 7
			grid(2,mov(a)-3) = 2
		ElseIf mov(a) > 6
			grid(3,mov(a)-6) = 2
		ElseIf mov(a) < 4
			grid(1,mov(a)) = 2
		EndIf
	EndIf
	EndIf
Next

For a = 1 To 3
	If grid(a,1) = 1 And grid(a,2) = 1 And grid(a,3) = 1 Then win = 1
	If grid(a,1) = 2 And grid(a,2) = 2 And grid(a,3) = 2 Then win = 2
	If grid(1,a) = 1 And grid(2,a) = 1 And grid(3,a) = 1 Then win = 1
	If grid(1,a) = 2 And grid(2,a) = 2 And grid(3,a) = 2 Then win = 2
Next


If grid(1,1) = 1 And grid(2,2) = 1 And grid(3,3) = 1 Then win = 1
If grid(1,3) = 1 And grid(2,2) = 1 And grid(3,1) = 1 Then win = 1
If grid(1,1) = 2 And grid(2,2) = 2 And grid(3,3) = 2 Then win = 2
If grid(1,3) = 2 And grid(2,2) = 2 And grid(3,1) = 2 Then win = 2

Return win
End Function
