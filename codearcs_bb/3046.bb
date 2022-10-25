; ID: 3046
; Author: Mr Snidesmin
; Date: 2013-03-27 13:31:30
; Title: XKCD Time Castle Generator
; Description: Guy builds castles like on http://xkcd.com/1190/

Global baseImage= LoadImage("C:\start.png")
Global sizeX = ImageWidth(baseImage)
Global sizeY = ImageHeight(baseImage)
Graphics sizeX, sizeY
baseImage= LoadImage("C:\start.png")
Global imgClumps = LoadAnimImage("C:\clumps.png", 30,30,0,4)
Global imgSquareClumps = LoadAnimImage("C:\squareclumps.png", 30,30,0,4)
MaskImage imgClumps, 255,255,255
MaskImage imgSquareClumps, 255,255,255


Const stateWandering = 0
Const statePlanning = 1
Const stateBuildingClumps = 2
Const stateBuildingSquareClumps = 3
Const stateBuildingSmooth = 4
Const stateBuildingBattlements = 5
Const stateBuildingFinished = 6


Type Person
	Field dir
	Field x, y
	Field cx, cy, cw, ch
	Field state
	Field bTimer
End Type


Function Render()
	SetBuffer BackBuffer()
	DrawImage baseImage, 0,0
	For p.Person = Each Person
		RenderPerson p
	Next
	VWait
	Flip
End Function


Function RenderPerson(p.Person)
	Color 0,0,0
	waveA# = Rnd(0.5, 1.2)
	waveB# = Rnd(2, 3.5)
	offSetA# = Rnd(360)
	offSetB# = Rnd(360)
	
	For a# = 0 To 360.0	Step 0.2
		r = 8 + 0.5 * Sin(waveA*a+offSetA) + 0.3 * Sin(waveB*a+offSetB)
		
		For r2# = r To r+0.6 Step 0.3
			x = p\x + r2 * Cos(a)
			y = p\y + r2 * Sin(a)
			Plot x, y
		Next
	Next
	
	x0 = p\x
	y0 = p\y + 10
	x1 = p\x + Rnd(-5,5)
	y1 = p\y + 30 + Rnd(-2,2)
	WLine x0,y0,x1,y1
	
	x2 = x1 + Rnd(5,8)
	x3 = x1 - Rnd(5,8)	
	y2 = y1 + 30 + Rnd(-2,2)
	
	WLine x0,y0,x2,y1
	WLine x0,y0,x3,y1	
	
	WLine x2,y2,x1,y1
	
	WLine x3,y2,x1,y1	
End Function



Function WLine(x0,y0,x1,y1)
	waveA# = Rnd(0.5, 2) * 360
	offsetA = Rnd(360)
	waveB# = Rnd(0.5, 2) * 360
	offsetB = Rnd(360)
	
	For n# = 0 To 1.0 Step 0.01
		wobble1# = Sin(n*180) * (0.5+ Sin(n*waveA+offsetA))
		wobble2# = Sin(n*180) * (0.5+ Sin(n*waveB+offsetB))
		
		dX# = x1 - x0
		dY# = y1 - y0
		
		x# = x0 + (n+0.05*wobble1) * dX
		y# = y0 + (n+0.05*wobble2) * dY
		
		For x2# = x To x+0.6 Step 0.3
		For y2# = y To y+0.6 Step 0.3
			Plot x2, y2
		Next
		Next		
	Next
End Function


Function UpdatePeople()
	SetBuffer ImageBuffer(baseImage)

	For p.Person = Each Person		
		
		If p\state >= stateBuildingClumps Then
			p\x = p\cx + p\cw*0.5
			p\y = p\cy - 50
			p\bTimer = p\bTimer + p\state -1
			If p\bTimer > 6 Then 
				p\state = p\state + 1
				p\bTimer = 0
			End If			
		End If	
		
		If p\state = stateBuildingSquareClumps Or p\state = stateBuildingSmooth Or p\state = stateBuildingBattlements Then
			x = Rnd(p\cx, p\cx+p\cw-30)
			y = p\cy-p\ch
			f = Floor(Rnd(4))			
			DrawImage imgSquareClumps, x, y+2, f
			Color 2,2,2
			Rect x, y+30, 30, sizeY		
		End If
	
		If p\state = stateWandering Then
			
			p\x = sizeX * Rnd(0.1, 0.7)
			p\y = sizeY * 0.52 + 0.11 * p\x
			
			If Rnd(10) > 8 Then 
				p\state = statePlanning
			End If
			
		ElseIf p\state = statePlanning Then
			If Rnd(10) > 8 Then 
				p\state = stateWandering 
			End If			
			
			For i = 1 To 100
				If (p\state = statePlanning)
					FindGoodSandCastlePlace(p)
				End If
			Next	
		
		
		ElseIf p\state = stateBuildingClumps Then	
			x = Rnd(p\cx, p\cx+p\cw-30)
			y = p\cy-p\ch
			f = Floor(Rnd(4))			
			DrawImage imgClumps, x, y, f
			Color 2,2,2
			Rect x, y+30, 30, sizeY			
					
		ElseIf p\state = stateBuildingSmooth Then	
			Color 1,1,1			
			bwL = Rnd(1, 3)
			bwR = Rnd(1, 3)			
			bh = -Rnd(5, 15)			
			Rect p\cx-bwL, p\cy-bh, p\cw+bwL+bwR, sizeY
			
			th = 7
			x0 = Rnd(p\cx-2.5, p\cx+p\cw)
			x1 = x0 + Rnd(10, 20)
			If x1 > p\cx+ p\cw+2.5 Then x1 = p\cx+ p\cw+2.5
			
			Rect x0, p\cy-p\ch, x1-x0, th
		
		ElseIf p\state = stateBuildingBattlements Then
							
			Color 255,255,255
			For x = p\cx + 4 To p\cx+p\cw-2 Step 7
				If Rnd(10) > 7 And measureBlack(x,p\cy-p\ch-1, x+2, p\cy-p\ch-1) = 0 Then
					Rect x,p\cy-p\ch, 2, 2
				End If			
			Next
		
		ElseIf p\state = stateBuildingFinished Then		

			Color 1,1,1
			
			bwL = Rnd(1, 3)
			bwR = Rnd(1, 3)
			
			bh = -Rnd(5, 15)
			
			Rect p\cx-bwL, p\cy-bh, p\cw+bwL+bwR, sizeY
			
				
			
			Rect p\cx, p\cy-p\ch, p\cw, sizeY
						
			th = Rnd(7,10)
			Rect p\cx-2.5, p\cy-p\ch, p\cw+5, th
			Rect p\cx-1.3, p\cy-p\ch, p\cw+2.7, th +2
			
						
			Color 255,255,255
			For x = p\cx + 4 To p\cx+p\cw-2 Step 7
				If measureBlack(x,p\cy-p\ch-1, x+2, p\cy-p\ch-1) = 0 Then
					Rect x,p\cy-p\ch, 2, 2
				End If			
			Next
			
			SetBuffer BackBuffer()
			p\state = stateWandering
		End If
	Next
	
End Function


Function FindGoodSandCastlePlace(p.Person)
	x = sizeX * 0.4 + 0.3 * sizeX * Rnd(-1,1) * Rnd(1)
		
	y = sizeY * Rnd(0.5, 0.9)
	
	w = Rnd(1,10)	
	h = 16 - w*2
	w = w * 7 + 10
	h = h + 18 + Rnd(10)
			
	x2 = x + Rnd(20)
	
	If measureBlack(x, y, x+w, y) < 40 And measureBlack(x, y+5, x+w, y+5) > 90 Then
		p\cx = x
		p\cy = y+8
		p\cw = w
		p\ch = h
		p\state = stateBuildingClumps 
	End If	
End Function


Function measureBlack(x0, y0, x1, y1)
	SetBuffer ImageBuffer(baseImage)
	black = 0
	For n# = 0 To 1.0 Step 0.01
		dX# = x1 - x0
		dY# = y1 - y0
		
		x = x0 + n * dX
		y = y0 + n * dY
		
		GetColor x, y
		If ColorRed() < 20 Then
			black = black + 1
		End If
	Next
	Return black
End Function


SeedRnd MilliSecs()

p.Person = New Person

Global iframe = 1
Global iSequence = 1
While Not KeyHit(1)	
	UpdatePeople()
	Render()
	If (KeyHit(57)) Or iframe > 100 Then
		FreeImage baseImage
		baseImage= LoadImage("C:\start.png")
		p\state = stateWandering
		iSequence = iSequence + 1
		CreateDir "C:\out\" + iSequence 
		iframe = 1
	End If
	
	SaveBuffer BackBuffer(), "C:\out\" + iSequence + "\" + iframe  + ".bmp"
	iframe  = iframe  + 1
Wend
End
