; ID: 2649
; Author: Ked
; Date: 2010-01-26 18:12:42
; Title: TFrameManager
; Description: Simply manages frame rate.

SuperStrict

Framework BRL.GLMax2D
Import BRL.Retro

Include "TFrameManager.bmx"

Graphics 800,600

'CREATE FRAME MANAGER AND SPECIFY TARGET FPS.
Global fpsmanager:TFrameManager=CreateFrameManager(30)

Repeat
	If AppTerminate() Exit
	If KeyHit(KEY_ESCAPE) Exit
	
	'UPDATE FRAME MANAGER AND CHECK TO SEE IF WE SHOULD DRAW.
	fpsmanager.Update()
	If fpsmanager.ReadyToDraw()
		
		'ALL UPDATING GOES HERE!
		
		Cls
		
		SetBlend ALPHABLEND
		SetScale 1,1
		SetAlpha 1.0
		SetRotation 0
		SetColor 255,255,255
		
		DrawRect MouseX(),MouseY(),32,32
		
		DrawText "FPS: "+fpsmanager.GetFPS(),0,0
		
		'ALWAYS FLIP SINCE WE ARE CONTROLLING WHEN WE DRAW NOW.
		Flip(False)
		
	EndIf
Forever
fpsmanager=Null
End
