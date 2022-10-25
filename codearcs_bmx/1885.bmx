; ID: 1885
; Author: Curtastic
; Date: 2006-12-22 14:54:35
; Title: Fire 2D (BlitzMax library)
; Description: Realtime fire effects.

'FIRE 2D Example.
Strict
'Include "fire_include.bmx" 'The include file is attached as part of this file.


Graphics 800, 600, 16
MoveMouse 300, 300
SeedRnd MilliSecs()



Local F:TFire = TFire.Make(290, 85)
Global FireBall:TFire = TFire.Make(80, 100, False)


'Place the big fire at the bottom-middle of the screen.
F.X = GraphicsWidth() / 2 - F.SizeX / 2
F.Y = GraphicsHeight() - F.SizeY

Repeat
	If KeyHit(key_escape) Then End
	
	
	'Click to make fireballs!
	If MouseDown(1) Then
		FireBall.X = MouseX()
		FireBall.Y = MouseY() - 50
		FireballRemake()
	EndIf
	
	If MouseHit(2) Then F.Clear()
	
	TFire.UpdateAll()
	TFire.DrawAll()
	
	
	Flip 0
	Cls
Forever



'This is just normal code to draw a circle to an image.
Function FireballRemake()
	Local pixmap:TPixmap
	Local x, y
	Local r
	Local tx#
	Local rr
	Local dotx, doty
	
	x = 40
	y = 80
	r = 15
	rr = r * r
	
	pixmap = LockImage(FireBall.Image)
	For doty = y - r To y + r
		tx = Sqr(rr - (doty - y) * (doty - y)) * 1.1
		For dotx = x - tx To x + tx
			WritePixel pixmap, dotx, doty, $FFFF9900
		Next
	Next
	UnlockImage(FireBall.Image)
EndFunction








'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''
'FIRE 2D. A BlitzMax Library.
'Made by Curtastic, 2006.
'This code is public domain.
'Please credit "Curtastic" if you use this in a product.
'Special Thanks to:
' Mark Sibly for making the Blitz Languages.
' Doreen Steinly for inspiring me to code again.
'''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''''


'Each fire has a single TImage that us updated realtime to make fire.
Type TFire
	
'Public:
	'List of all fires created.
	Global List:TList = CreateList()
	'Where the top-left of the image is.
	Field X:Float, Y:Float
	'The realtime image of the fire. Transparent color is black.
	Field Image:timage
	
'Private:
	'Size of image
	Field SizeX, SizeY
	Field MoveXLow, MoveXHigh
	Field DarkenXLow, DarkenXHigh
	'Holds SizeX/2
	Field SizeX2
	'How much the fire updates, each time update() is called.
	Field Loops
	'How much it moves
	Field MoveLoops
	'True if fire always comes from the bottom.
	Field Base
	
	
	
	'NOTE:
	'If base=false then
	' you need to draw things to the image yourself to make anything happen!
	'If base=true then the fire will start going itself.
	Function Make:TFire(SizeX = 120, SizeY = 170, Base = True)
		Local Fire:TFire
		
		Fire = New TFire
		Fire.Base = Base
		Fire.SizeX = SizeX
		Fire.SizeX2 = SizeX / 2
		Fire.SizeY = SizeY
		Fire.MoveXLow = - 7
		Fire.MoveXHigh = Fire.SizeX - 1
		Fire.DarkenXLow = 0
		Fire.DarkenXHigh = Fire.SizeX - 1
		
		Fire.Loops = (Fire.SizeX2 + Fire.SizeY) / 2.0 / 15
		If Fire.Loops < 1 Then Fire.Loops = 1
		Fire.MoveLoops = 25 + Fire.SizeY * 8 / Fire.SizeX2
		If Fire.MoveLoops < 1 Then Fire.MoveLoops = 1
		
		Fire.Image = CreateImage(Fire.SizeX, Fire.SizeY, DYNAMICIMAGE | MASKEDIMAGE)
		If Base Then Fire.BaseDraw()
		
		ListAddLast List, Fire
		
		Return Fire
	EndFunction
	
	
	Method Kill()
		ListRemove List, Self
	EndMethod
	
	
	'Call this to clear the fire image.
	Method Clear()
		If Base Then
			BaseDraw(True)
		Else
			Cls
			GrabImage Image, 0, 0
		EndIf
	EndMethod
	
	
	'Draws the orange rect at the base.
	'If clear=True, it also clears the fire with the CLSCOLOR.
	Method BaseDraw(Clear = False)
		Cls
		DrawImage Image, 0, 0
		
		If Clear Then Cls
		SetColor 250, 50, 0
		DrawRect SizeX2 * .25, SizeY - 1, SizeX2 * .75 * 2, 1
		SetColor 255, 255, 255
		
		GrabImage Image, 0, 0
	EndMethod
	
	
	'Draws every fire image at its x,y
	Function DrawAll()
		Local Fire:TFire
		
		For Fire = EachIn List
			DrawImage Fire.Image, Fire.X, Fire.Y
		Next
	EndFunction
	
	'Updates each fire.
	Function UpdateAll()
		Local Fire:TFire
		
		For Fire = EachIn List
			Fire.Update()
		Next
	EndFunction
	
	
	'Makes the fire go.
	Method Update()
		
		Local X, Y, AddX
		Local C, R, G, GoodR
		Local Pixmap:TPixmap
		Local cx, cy
		Local poo
		Local go
		
		Cls
		
		Pixmap = LockImage(Image)
		
		For Local loopdeloops = 1 To Loops
			
			'Darken the fire.
			For Local darkensomepixelswithcurtasticfun = 1 To 65
				
				X = Rand(DarkenXLow, DarkenXHigh)
				
				For Local someysfromthesamexwhynot = 1 To 8
					Y = Rand(0, SizeY - 2)
					
					C = ReadPixel(Pixmap, X, Y) & $FFFFFF
					If C <> 0 Then
						'Calculate the desired color.
						'The higher up the darker.
						GoodR = (Y - SizeY / 2) * 2 * 235 / SizeY
						'The farther away from the center x the darker.
						GoodR = GoodR - Abs(X - SizeX2) * 135 / SizeX2
						If GoodR < 0 Then GoodR = 0
						
						'Color gets closer to desired color.
						R = (C Shr 16) & $FF
						R:- (R - GoodR) / 3.0
						If R < 10 Then
							WritePixel Pixmap, X, Y, 0
						Else
							G = Min(R, 20 - Abs(X - SizeX2) / (SizeX2 / 10.0))
							WritePixel Pixmap, X, Y,..
							 $FF000000 | (R Shl 16) | (G Shl 8) | Int(G * .7)
						EndIf
					EndIf
				Next
			Next
			
			'Make the fire move.
			For Local imfeelingfatandsassy = 1 To MoveLoops
				
				X = Rand(MoveXLow, MoveXHigh)
				
				'Move toward the center if near the edge of the image.
				If X < MoveXLow + 5 Then
					AddX = 1
				ElseIf X > MoveXHigh - 5 Then
					AddX = - 1
				Else
					'Move random(-1,+1)
					AddX = Rand(0, 2) - 1
				EndIf
				
				'Like CopyRect, but it copies a diagnol shape.
				For Local someysfromthesamexwhynot = 1 To 2
					Y = Rnd(1, SizeY)
					poo = 1
					go = 1
					For cy = Y To Min(Y + 8, Image.height - 2)
						poo:+ go
						If poo > 3 Then go = - 1
						For cx = Max(X - poo, 1) To Min(X + poo, Image.width - 2)
							WritePixel Pixmap, cx + AddX, cy - 1, ReadPixel(Pixmap, cx, cy)
						Next
					Next
				Next
			Next
		Next
		
		UnlockImage(Image)
		
		
	EndMethod
	
EndType
