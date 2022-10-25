; ID: 3015
; Author: Cruis.In
; Date: 2013-01-10 14:36:05
; Title: Camera Pan Effect
; Description: Pan your camera

Strict

Graphics 800,600

Global newplayer:tplayer = New tplayer
Global starsx%[500]
Global starsy%[500]

For Local I= 0 To 499
	starsx[i]=Rnd(-1600, 1600)
	starsy[i]=Rnd(-1600, 1600)
Next

Type TPlayer

	Field X:Float 
	Field Y:Float 
	
	
	Method Render()
		SetColor(235,110,214)
		SetRotation (0)
		SetAlpha 1.0
		
		Rem
		variables To hold the value of MOUSE X & Y POSITION. Since my intention is To focus the 'cam' on the 
		'player at the centre of the screen, I want To start out at the centre, hence the division of Graphics
		with resolution
		End Rem
		
		Local tx = GraphicsWidth()/2 - x 
		Local ty = GraphicsHeight()/2 - y
		
		Local PanX:Float = MouseX() - GraphicsWidth()/2
		Local panY:Float = MouseY() - GraphicsHeight()/2
		
			
		'setting the origin
		SetOrigin(tx - panx , ty - pany)
		
		
	End Method
	
	'standard input	
	Method GetInput()
		If KeyDown(Key_left)
			x:-1
		End If
		
		If KeyDown(key_right)
			x:+1
		End If
		
		If KeyDown(key_UP)
			y:-1
		End If
		
		If KeyDown(Key_Down)	
			y:+1
		End If
	End Method

End Type

	
	While Not KeyHit(key_ESCAPE)
		Cls
	
		
			
		newplayer.getInput()
		newplayer.render()
		
		DrawRect(newplayer.x, newplayer.y, 20,20)
		
		DrawText("X= "+newplayer.x, 100,100)
		DrawText("Y= "+newplayer.y, 100,120)
	
	
		For Local I% = 0 To 499
			DrawOval starsx[i], starsy[i], 3, 3
		Next
		
		Flip
	Wend
