; ID: 1705
; Author: Tom Darby
; Date: 2006-05-09 16:36:28
; Title: Mouse as Pseudo-Proportional Controller (Smooth, Boundary-Free Mouse)
; Description: Mimics proportional controller input using existing BMAX mouse routines

SuperStrict

Const DEPTH:Int = 32
Const WIDTH:Int = 1024
Const HEIGHT:Int = 768
Const SAFEZONE:Int = 100 ' set this high to disable repositioning; set to 0 to always reposition

Global MouseXSpeed:Int,MouseYSpeed:Int, prevMouseX:Int, prevMouseY:Int, movementZone:Int
Global controllerX#, controllerY#, slowControllerX#, slowControllerY#

Function SampleMouse()
	
	Local curMouseX:Int, curMouseY:Int
	curMouseX = MouseX()
	curMouseY = MouseY()

	MouseXSpeed=curMouseX - prevMouseX
	MouseYSpeed=curMouseY - prevMouseY
	If Abs(centerX - curMouseX) > movementZone Or Abs(centerY - curMouseY) > movementZone Then
		MoveMouse centerX, centerY
		prevMouseX = centerX - MouseXSpeed
		prevMouseY = centerY - MouseYSpeed
	Else
		prevMouseX = curMouseX
		prevMouseY = curMouseY
	EndIf
End Function

Graphics WIDTH, HEIGHT, DEPTH

Global centerX:Int, centerY:Int

Function Reset()
	centerX = GraphicsWidth() / 2
	centerY = GraphicsHeight() / 2
	movementZone = SAFEZONE
	controllerX# = centerX
	controllerY# = centerY
	slowControllerX# = centerX
	slowControllerY# = centerY
	prevMouseX = centerX
	prevMouseY = centerY
	mouseXSpeed = 0
	mouseYSpeed = 0
	MoveMouse centerX, centerY
End Function

HideMouse

Reset()

While Not KeyHit(KEY_ESCAPE)

	If MouseHit(1) Then
		Reset()
	EndIf	
	SampleMouse()
	
	' reposition "controllers"
	controllerX# = controllerX# + Float(MouseXSpeed)
	controllerY# = controllerY# + Float(MouseYSpeed)

	slowControllerX# = slowControllerX# + (Float(MouseXSpeed) / 2)
	slowControllerY# = slowControllerY# + (Float(MouseYSpeed) / 2)
	
	Cls

	' draw 'movement zone'
	SetColor 50,0,0
	DrawRect(centerX - movementZone, centerY - movementZone, movementZone * 2, movementZone * 2)
	
	' draw actual mouse location
	SetColor 255, 0, 0
	DrawLine(prevMouseX - 4, prevMouseY, prevMouseX + 4, prevMouseY)
	DrawLine(prevMouseX, prevMouseY - 4, prevMouseX, prevMouseY + 4)
	
	' draw "controller" locations
	SetColor 0,255,0
	DrawLine(controllerX - 4, controllerY - 4,controllerX + 4, controllerY + 4)
	DrawLine(controllerX + 4, controllerY - 4, controllerX - 4, controllerY + 4)
	
	SetColor 0,255,255
	DrawLine(slowControllerX - 4, slowControllerY - 4, slowControllerX + 4, slowControllerY + 4)
	DrawLine(slowControllerX + 4, slowControllerY - 4, slowControllerX - 4, slowControllerY + 4)
	
	
	SetColor 255, 255, 255
	DrawText "Click to reset, [ESC] to exit.  Green x: full speed controlled object.",0,0
	DrawText "Blue x: 1/2 speed controlled object.  Red +: actual mouse position.",0, 12
	DrawText "Red square: mouse movement area.",0,24
	DrawText "MouseXSpeed="+MouseXSpeed,0,36
	DrawText "MouseYSpeed="+MouseYSpeed,0,48
	Flip
Wend
