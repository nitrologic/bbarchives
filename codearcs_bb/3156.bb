; ID: 3156
; Author: Pakz
; Date: 2014-10-27 20:11:56
; Title: Blocks Game
; Description: Simple Tetris game.

;
; Simple Blocks game by Pakz (Rudy van Etten)
;

Graphics 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

Dim block(21,4,4)
Dim map(32,32)

makeblocks()

Global bx = 5 ; block x
Global by = 0 ; block y
Global cb = Rand(1,21); current block


timer = CreateTimer(60)
While KeyDown(1) = False
	WaitTimer timer
	Cls
	drawmap
	Color 255,255,255
	Line 0,16*3,14*16,16*3 ; End line
	Text GraphicsWidth()/2,15,"Space = rotate block - Cur left = move left - Cur right = move right",1,1
	Text GraphicsWidth()/2,25,"Cur down = Drop Block",1,1


	
 	; Move left and right
	If KeyHit(203) And bx => 0 Then bx = bx - 1
	If KeyHit(205)
		cont = True
		For y1 = 1 To 4
		For x1 = 1 To 4
			If block(cb,x1,y1) = 1
				If bx+x1 > 12 Then cont = False
			End If
		Next
		Next
		If cont = True Then bx=bx+1
	End If
	
	; Change block 
	If KeyHit(57)
		Select cb
			Case 1
			Case 2 : If mapob(3) = False Then cb = 3
			Case 3 : If mapob(2) = False Then cb = 2
			Case 4 : If mapob(5) = False Then cb = 5
			Case 5 : If mapob(6) = False Then cb = 6
			Case 6 : If mapob(7) = False Then cb = 7
			Case 7 : If mapob(4) = False Then cb = 4
			Case 8 : If mapob(9) = False Then cb = 9
			Case 9 : If mapob(10) = False Then cb = 10
			Case 10: If mapob(11) = False Then cb = 11
			Case 11: If mapob(8) = False Then cb = 8
			Case 12: If mapob(13) = False Then cb = 13
			Case 13: If mapob(12) = False Then cb = 12
			Case 14: If mapob(15) = False Then cb = 15
			Case 15: If mapob(16) = False Then cb = 16
			Case 16: If mapob(17) = False Then cb = 17
			Case 17: If mapob(14) = False Then cb = 14
			Case 18: If mapob(19) = False Then cb = 19
			Case 19: If mapob(18) = False Then cb = 18
			Case 20: If mapob(21) = False Then cb = 21
			Case 21: If mapob(20) = False Then cb = 20  
		End Select
	End If
	
	; See if the block is not right outside the map
	For y1=1 To 4
	For x1=1 To 4
		If block(cb,x1,y1) = 1 
			If bx + x1 > 13 Then bx=bx-1
		End If
	Next
	Next
	
	; Draw the block
	For y1=1 To 4
	For x1=1 To 4
		If block(cb,x1,y1) = 1
			Rect bx*16+x1*16,by*16+y1*16,16,16,True
		End If
	Next
	Next
	
	; Fall speed
	t=t+1
	If t > 20
		by=by+1
		t = 0
	End If
	
	; Fall collision
	For y2=1 To 4
	For x2=1 To 4
		If block(cb,x2,y2) = 1
			If y2+by > 21 Then
				by=by-1
				fillblockinmap()
				by = 0 : bx = 5 : cb = Rand(1,21)				
			End If
		End If
		done=False

		If done = False
		If block(cb,x2,y2) = 1
		If map(bx+x2,by+y2) = 1
			If by <2  Then End
			by=by-1
			fillblockinmap
			by = 0 : bx = 5: cb= Rand(1,21)
			done=True
		End If
		End If
		End If

	Next
	Next
	
	If MouseDown(1) = True
		map(MouseX()/16,MouseY()/16) = 1
	End If
	; Line fill remove
	For y1=2 To 22
		fl = True
		For x1=0 To 13
			If map(x1,y1) = 0 Then fl=False
		Next
		If fl = True
			For x2 = 0 To 13
				map(x2,y1) = 0
			Next
			For y2=y1-1 To 2 Step -1
			For x2=0 To 14
				map(x2,y2+1) = map(x2,y2)
			Next
			Next
		End If
	Next

	; Move down all the way (cursor down)
	If KeyHit(208)
		exitloop = False
		While exitloop = False
			by = by + 1
			For y1=1 To 4
			For x1=1 To 4
				If exitloop = False
				If block(cb,x1,y1) = 1
					If map(bx+x1,by+y1) = 1
						by=by-1
						fillblockinmap()
						by = 0 : bx = 5: cb = Rand(1,21)
						exitloop = True						
					End If
					If by+y1>20 Then 
						fillblockinmap()
						by = 0 : bx = 5: cb = Rand(1,21)
					 	exitloop = True
					End If
				End If
				End If
			Next
			Next		
		Wend
	End If
	
	
	If KeyDown(2) = True Then
		Cls
		Color 255,255,255
		cnt = 1
		For y=0 To 4
		For x=0 To 5
			If cnt<22
				Dblock(cnt,x*96,y*96)
				Text x*96,y*96,cnt
			End If
			cnt=cnt+1
		Next
		Next
	End If
	Flip
	
Wend
End

; Check if block(nb) is ontop of block
Function mapob(nb)
	For y=1 To 4
	For x=1 To 4
		If block(nb,x,y) = 1
			If map(bx+x-1,by+y-1) = 1 Then Return True
		End If
	Next
	Next
	Return False
End Function

Function fillblockinmap()
	For y=1 To 4
	For x=1 To 4
		If block(cb,x,y) = 1
			map(bx+x,by+y) = 1
		End If
	Next
	Next
End Function

Function drawmap()
	Color 255,255,255
	Line 0,0,0,22*16
	Line 0,22*16,14*16,22*16
	Line 14*16,0,14*16,22*16
	For y=0 To 22
	For x=0 To 14
		If map(x,y) = 1 Then
			Rect x*16,y*16,16,16,True
		End If
	Next
	Next
End Function

Function dblock(b,x,y)
	Color 255,255,255
	For y1=1 To 4
	For x1=1 To 4
		If block(b,x1,y1) = 1
			Rect x1*16+x,y1*16+y,16,16
		End If
	Next
	Next
	
End Function

Function makeblocks()
	Restore blockdata
	For i=1 To 21
		For y=1 To 4
		For x=1 To 4
			Read a
			If a = 1 
				block(i,x,y) = 1
			End If
		Next
		Next
	Next
End Function

.blockdata
Data 1,1,0,0
Data 1,1,0,0
Data 0,0,0,0
Data 0,0,0,0

Data 1,0,0,0
Data 1,0,0,0
Data 1,0,0,0
Data 1,0,0,0

Data 1,1,1,1
Data 0,0,0,0
Data 0,0,0,0
Data 0,0,0,0

Data 1,0,0,0
Data 1,0,0,0
Data 1,1,0,0
Data 0,0,0,0

Data 1,1,1,0
Data 1,0,0,0
Data 0,0,0,0
Data 0,0,0,0

Data 1,1,0,0
Data 0,1,0,0
Data 0,1,0,0
Data 0,0,0,0

Data 0,0,1,0
Data 1,1,1,0
Data 0,0,0,0
Data 0,0,0,0

Data 0,1,0,0
Data 0,1,0,0
Data 1,1,0,0
Data 0,0,0,0

Data 1,0,0,0
Data 1,1,1,0
Data 0,0,0,0
Data 0,0,0,0

Data 1,1,0,0
Data 1,0,0,0
Data 1,0,0,0
Data 0,0,0,0

Data 1,1,1,0
Data 0,0,1,0
Data 0,0,0,0
Data 0,0,0,0

Data 1,1,0,0
Data 0,1,1,0
Data 0,0,0,0
Data 0,0,0,0

Data 0,1,0,0
Data 1,1,0,0
Data 1,0,0,0
Data 0,0,0,0

Data 0,1,0,0
Data 1,1,1,0
Data 0,0,0,0
Data 0,0,0,0

Data 1,0,0,0
Data 1,1,0,0
Data 1,0,0,0
Data 0,0,0,0

Data 1,1,1,0
Data 0,1,0,0
Data 0,0,0,0
Data 0,0,0,0

Data 0,1,0,0
Data 1,1,0,0
Data 0,1,0,0
Data 0,0,0,0

Data 0,1,1,0
Data 1,1,0,0
Data 0,0,0,0
Data 0,0,0,0

Data 1,0,0,0
Data 1,1,0,0
Data 0,1,0,0
Data 0,0,0,0

Data 1,1,0,0
Data 0,1,1,0
Data 0,0,0,0
Data 0,0,0,0

Data 0,1,0,0
Data 1,1,0,0
Data 1,0,0,0
Data 0,0,0,0
