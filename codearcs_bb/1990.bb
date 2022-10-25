; ID: 1990
; Author: Mr Snidesmin
; Date: 2007-04-16 19:20:04
; Title: Pixel explosion effect
; Description: Good for creating explosion anim/sprites

Const gXd=256
Const gYd=256



Graphics gXd, gYd, 0, 2



Type bPix
	Field T	;Temperature (0-768)
	Field x#, y#
	Field decay%
	Field dX#, dY#
End Type


Dim bPix.bPix(gXd, gYd)




While Not KeyHit(1)
	If KeyHit(57) Then
		Cls
		x = gXd/2 
		y = gYd/2 
		bPix(x, y) = New bPix
		bPix(x, y)\x = x
		bPix(x, y)\y = y
		bPix(x, y)\T = 865
		bPix(x, y)\decay = (bPix(x, y)\T/100) ^ 2+8
	End If
	
	Update
Wend
End


Function Update()
	For p.bPix = Each bPix
		setcol p\T
		Plot p\x, p\y	
		
		p\x = p\x + p\dX
		p\y = p\y + p\dY

				
		For dx=-1 To 1
		For dy=-1 To 1
			x = p\x+dx	
			y = p\y+dy	
			
			If x>0 And x<=gXd And y>0 And y<=gYd Then
				If bPix(x, y) = Null And Rnd(10)>8 Then
					bPix(x, y) = New bPix
					bPix(x, y)\x = x
					bPix(x, y)\y = y
					bPix(x, y)\T = p\T-Rnd(20)
					spd# = Rnd(1.5)
					dir# = Rnd(360)
					bPix(x, y)\dX = spd*Cos(dir)
					bPix(x, y)\dY = spd*Sin(dir)
					
					
					If bPix(x, y)\T > 0 Then
						bPix(x, y)\decay = (bPix(x, y)\T/100) ^ 2+8
					Else
						Delete bPix(x, y)
					End If
				End If
			End If
		Next
		Next
		
			
		p\T = p\T - Rnd(p\decay) 
		If p\T <= 0 Then Delete p
	Next
End Function



Function SetCol(t%)
	If t >= 256*3 Then
		Color 255, 255, 255
	ElseIf t >= 256*2 Then
		Color 255, 255, t-256*2
	
	ElseIf t >= 256
		Color 255, t-256, 0
		
	ElseIf t > 0
		If t > 64 Then
			gb = (256-t)/3	
		Else
			gb = t
		End If
		
		Color t, gb, gb
		
	Else
		Color 0, 0, 0
	End If	
End Function
