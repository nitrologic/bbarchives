; ID: 2969
; Author: Matt Merkulov
; Date: 2012-08-07 20:01:07
; Title: Nasty Tetris
; Description: this tetris always give you most unconvenient figure, try to beat it and fail miserably

SuperStrict

Framework brl.glmax2d
Import brl.random

SeedRnd Millisecs()

Const SquareSize:Int = 24

Global World:Int[ 21, 23 ]
Global TempWorld:Int[ 21, 23 ]
Global Figure:Int[ 7, 4, 4, 4 ]
Global Phases:Int[ 7 ]
Global XShift:Int[][] = [ [ 0 ], [ 0, 0 ], [ 0, 0 ], [ 1, 0 ], [ 0, 1, 0, 0 ], [ 0, 1, 0, 0 ], [ 0, 1, 0, 0 ] ]
Global YShift:Int[][] = [ [ 2 ], [ 2, 2 ], [ 3, 1 ], [ 4, 2 ], [ 2, 3, 2, 3 ], [ 2, 3, 3, 1 ], [ 2, 3, 2, 2 ] ]

Local M:Int = 0
For Local S:String = Eachin "ABEF,AEFJEFBC,IEFBABFG,BFJNEFGH,AEFGCBFJEFGKIJFB,EFGCBFJKIEFGABFJ,EFBGBFGJEFGJBEFJ".Split(",")
	Phases[ M ] = S.Length / 4 - 1
	For Local I:Int = 0 To Phases[ M ]
		For Local J:Int = 0 To 3
			Local A:Int = S[ I * 4 + J ] - 65
			Figure[ M, I, A & 3, A Shr 2 ] = True
		Next
	Next
	M :+ 1
Next

Graphics SquareSize * 14, SquareSize * 21 + 16

For Local I:Int = 0 To 20
	World[ 1, I ] = True
	World[ 12, I ] = True
	World[ I, 20 ] = True
Next

Local X:Int = 0, Y:Int = 19, R:Int = 0, T:Int = 0, N:Int = 0, Score:Int = 0

Repeat
	Cls
	For Local I:Int = 0 To 20
		For Local J:Int = 1 To 12
			If World[ J, I ] Then DrawRect J * SquareSize, I * SquareSize, SquareSize, SquareSize
		Next
	Next
	
	For Local I:Int = 0 To 3 
		For Local J:Int = 0 To 3
			If Figure[ N, R, I, J ] Then DrawRect ( X + I ) * SquareSize, ( Y + J ) * SquareSize, SquareSize, SquareSize
		Next
	Next
	
	DrawText "Score: " + Score, SquareSize * 7 - 4 * ( 7 + String( Score ).Length ), SquareSize * 21
	
	Flip
	
	Local A:Int = X + KeyHit( KEY_RIGHT ) - KeyHit( KEY_LEFT )
	Local B:Int = Y + KeyDown( KEY_DOWN )
	Local Q:Int = ( R + KeyHit( KEY_W ) - KeyHit( KEY_Q ) ) & Phases[ N ]
	
	IF Not CheckPosition( A, B, N, Q ) Then
		X = A
		Y = B
		R = Q
	End If
	
	Local M:Int = Millisecs()
	
	If M > T Then
		T = M + 999
		
		If CheckPosition( X, Y + 1, N, R ) Then
			If Y = 0 Then End
			
			For Local I:Int = 0 To 3
				For Local J:Int = 0 To 3
					If Figure[ N, R, I, J ] Then World[ X + I, Y + J ] = True
				Next
			Next
			
			Local Counter:Int = 0
			Local D:Int = 0
			For Local J:Int = 19 To 0 Step -1
				Local Quantity:Int = 0
				For Local I:Int = 2 To 11
					If World[ I, J ] Then Quantity :+ 1
					World[ I, J + D ] = World[ I, J ]
				Next
				If Quantity > 9 Then
					D :+ 1
					Counter :+ 1
					Score :+ Counter
				End If
			Next
			
			X = 5
			Y =  - 1
			N = Decide()
		EndIf
		
		Y :+ 1
	End If
Until KeyHit( KEY_ESCAPE )

Function CheckPosition:Int( X:Int, Y:Int, N:Int, R:Int )
	For Local I:Int = 0 To 3
		For Local J:Int = 0 To 3
			If Figure[ N, R, I, J ] * World[ I + X, J + Y ] Then Return True
		Next
	Next
EndFunction

Function Decide:Int()
	Local CurrentScore:Int = 0
	Local CurrentN:Int = 0
	For Local N:Int = 0 To 6
		Local Score:Int = 999
		For Local R:Int = 0 To Phases[ N ]
			For Local X:Int = 2 To 11
				For Local Y:Int = 4 To 19
					If Not World[ X, Y ] And World[ X, Y + 1 ] Then
						Local XX:Int = X - XShift[ N ][ R ]
						Local YY:Int = Y + 1 - YShift[ N ][ R ]
						If CheckPosition( XX, YY, N, R ) Then Continue
						Score = Min( Score, Calculate( XX, YY, N, R ) )
					End If
				Next
			Next
		Next
		
		If Score >= CurrentScore Then
			If Score = CurrentScore Then If Rand( 0, 1 ) Then Continue
			CurrentScore = Score
			CurrentN = N
		End If
	Next
	
	Return CurrentN
End Function



Function Calculate:Int( X:Int, Y:Int, N:Int, R:Int )
	For Local X:Int = 2 To 11
		For Local Y:Int = 0 To 20
			TempWorld[ X, Y ] = World[ X, Y ]
		Next
	Next
	
	For Local I:Int = 0 To 3
		For Local J:Int = 0 To 3
			If Figure[ N, R, I, J ] Then TempWorld[ X + I, Y + J ] = True
		Next
	Next

	Local Score:Int = 0
	
	Local CurrentHeight:Int
	For Local I:Int = 2 To 11
		For Local J:Int = 0 To 20
			If TempWorld[ I, J ] Then
				If I > 2 Then Score :+ Abs( J - 1 - CurrentHeight )
				CurrentHeight = J - 1
				Exit
			End If
		Next
		
		For Local J:Int = 1 To 19
			If Not TempWorld[ I, J ] And TempWorld[ I, J - 1 ] Then Score :+ 16
		Next
	Next
	
	Return Score
End Function
