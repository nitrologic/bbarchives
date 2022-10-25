; ID: 1536
; Author: Chroma
; Date: 2005-11-17 19:33:37
; Title: MouseXSpeed() / MouseYSpeed()
; Description: Brings the functionality of MouseXSpeed/MouseYSpeed to BMax.

'MouseSpeed Setup
Global MouseXSpeed:Int	'MouseXSpeed() Component
Global MouseYSpeed:Int	'MouseYSpeed() Component
Global CenterX:Int = GraphicsWidth() / 2	'Find CenterX
Global CenterY:Int = GraphicsHeight() / 2	'Find CenterY
MoveMouse CenterX,CenterY	'Move Mouse to Center of Screen

Function MouseSpeed()
	Local XM:Int = MouseX()
	Local YM:Int = MouseY()
	MouseXSpeed = XM - CenterX
	MouseYSpeed = YM - CenterY
	MoveMouse CenterX,CenterY
End Function
