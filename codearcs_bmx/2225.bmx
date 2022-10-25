; ID: 2225
; Author: nawi
; Date: 2008-03-09 15:18:55
; Title: Game of life
; Description: A simple game

SuperStrict
AppTitle = "Game of life"
Const GfxWidth:Int = 640,GfxHeight:Int = 480
Graphics GfxWidth,GfxHeight,0

Type Game
	Field Width:Int,Height:Int
	Field Array:Int[1,1]

	Method New()
		Width = GfxWidth*0.5; Height = GfxHeight*0.5
		Array = New Int[Width,Height]
		For Local x:Int = 0 To Width-1
			For Local y:Int = 0 To Height-1
				Array[x,y] = 0
			Next
		Next
	End Method

	Method Update()
		Local NewArray:Int[Width,Height]
		For Local x:Int = 0 To Width-1
			For Local y:Int = 0 To Height-1
				NewArray[x,y] = 0
			Next
		Next
		For Local x:Int = 0 To Width-1
			For Local y:Int = 0 To Height-1
				Local NCount:Int = Neighbours(x,y)
				If (Array[x,y] And (NCount = 2)) Or NCount = 3 Then NewArray[x,y] = 1
			Next
		Next
		Array = NewArray
	End Method

	Method Draw()
		For Local x:Int = 0 To Width-1
			For Local y:Int = 0 To Height-1
				If Array[x,y] Then DrawRect x*2,y*2,2,2
			Next
		Next
	End Method

	Method Neighbours:Int(x:Int,y:Int)
		Local NCount:Int = 0
		For Local t:Int = x-1 To x+1
			For Local s:Int = y-1 To y+1
				If t=>0 And s=>0 And t<Width And s<Height Then
					If Array[t,s] And Not (t=x And s=y) Then NCount:+1
				EndIf
			Next
		Next
		Return NCount
	End Method
End Type

Local Life:Game = New Game
Local Updating:Int = 0

Repeat
	If KeyHit(KEY_SPACE) Then Updating=1-Updating
	If MouseDown(1) Then
		Local MX:Int = MouseX()*0.5,MY:Int = MouseY()*0.5
		If MX>=0 And MY>=0 And MX<Life.Width And MY<Life.Height Then Life.Array[MX,MY] = 1
	EndIf
	If MouseDown(2) Then
		For Local t:Int = 0 To 50
			Local MX:Int = MouseX()*0.5+Rand(-50,50),MY:Int = MouseY()*0.5+Rand(-50,50)
			If MX>=0 And MY>=0 And MX<Life.Width And MY<Life.Height Then Life.Array[MX,MY] = 1
		Next
	EndIf
	If Updating Then 
		Life.Update()
	Else
		DrawText "Game paused. Press space",0,0
	EndIf
	Life.Draw()
	Flip
	Cls
Until KeyHit(KEY_ESCAPE)
