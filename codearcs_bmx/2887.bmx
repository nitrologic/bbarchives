; ID: 2887
; Author: BlitzSupport
; Date: 2011-09-05 05:24:21
; Title: Monkey-style game framework
; Description: Basic framework for writing games in the Monkey style

' Monkey-style game framework...

Type App

	Method OnCreate () Abstract
	Method OnUpdate () Abstract
	Method OnRender () Abstract
	
End Type

Type Game Extends App

	' Crude approximation of what App does behind the scenes in Monkey,
	' so this function can be commented out when porting to Monkey, and
	' the MaxMain function below altered as per its comments...
	
	Function Create:Game ()

		Local g:Game = New Game
		
		g.OnCreate
	
		Repeat
	
			g.OnUpdate
			g.OnRender
			
			Flip
	
		Forever
	
	End Function
	
	Method OnCreate ()

		' Setup code goes here...
		
		Graphics 640, 480

	End Method
	
	Method OnUpdate ()

		' Game updates go here...

		If KeyHit (KEY_ESCAPE) Then End

	End Method
	
	Method OnRender ()
		
		' Drawing goes here (would be better in practise
		' to grab MouseX/Y into your own fields during OnUpdate)...

		Cls
		DrawRect MouseX () - 8, MouseY () - 8, 16, 16
		
	End Method
	
End Type

Function MaxMain ()

	' In Monkey, this would be replaced with "New Game"...
	
	New Game.Create
	
End Function

' MaxMain would be renamed to Main in Monkey, and Monkey would
' automatically call it, unlike here...

MaxMain
