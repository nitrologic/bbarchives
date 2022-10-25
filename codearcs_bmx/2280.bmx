; ID: 2280
; Author: jankupila
; Date: 2008-06-26 13:48:42
; Title: Menu
; Description: menu example

Graphics 800,600,16,60,1

Const x:Int=50
Const y:Int=100	'	place of the list

t=1

Repeat
	Cls
	
	SetColor 255,255,255
	If t=1 Then 
		SetColor 255,0,0
		DrawText "Chosen number 1",200,200
	End If
	DrawText "Choice 1",x,y
	
	SetColor 255,255,255
	If t=2 Then 
		SetColor 255,0,0
		DrawText "Chosen number 2",200,200
	End If
	DrawText "Choice 2",x,y+20
	
	SetColor 255,255,255
	If t=3 Then 
		SetColor 255,0,0
		DrawText "Chosen number 3",200,200
	End If
	DrawText "Choice 3",x,y+40
	
	SetColor 255,255,255
	If t=4 Then 
		SetColor 255,0,0
		DrawText "Chosen number 4",200,200
	End If
	DrawText "Choice 4",x,y+60
	
	SetColor 255,255,255
	If t=5 Then 
		SetColor 255,0,0
		DrawText "Chosen number 5",200,200
	End If
	DrawText "Choice 5",x,y+80
	
	If KeyHit(KEY_UP) Then t=t-1
	If KeyHit(KEY_DOWN) Then t=t+1
	
	If t=0 Then t=5
	If t=6 Then t=1
	
	Flip

Until KeyDown(KEY_ESCAPE)

End
