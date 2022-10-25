; ID: 2796
; Author: Chroma
; Date: 2010-12-10 18:43:19
; Title: Core2D Graphics Module
; Description: Handles resolution scaling and puts pillars where needed.

' Core2D Graphics Module

' Official Release 1.00

Strict

'Module Core2D.Graphics

Private

Global _width,_height,_depth,_hertz
Global _xOrigin#,_yOrigin#
Global _box1:TBox
Global _box2:TBox

Public

Function c2dGraphics( width,height,depth=0,hertz=60 )

	_width = width
	_height = height
	_depth = depth
	_hertz = hertz

	Select _depth
		Case 0
			Graphics _width,_height,_depth,_hertz

		Case 16,24,32
			
			Local deskRatio# = Float DesktopWidth() / DesktopHeight()
			Local gameRatio# = Float _width / _height
			Local scale#
			Local virtWidth#,virtHeight#

			Select True
			
				Case gameRatio < deskRatio		'Wide Screen
					scale 		= Float DesktopHeight() / _height
					virtWidth 	= Float DesktopWidth() / scale
					virtHeight	= _height
					_xOrigin 	= (virtWidth - _width) / 2.0
					_yOrigin 	= 0
					_box1		= TBox.Create(-_xOrigin,0,_xOrigin,_height)
					_box2 		= TBox.Create(_width,0,_xOrigin,_height)
				
				Case gameRatio > deskRatio		'Tall Screen
					scale 		= Float DesktopWidth() / _width
					virtWidth 	= _width
					virtHeight 	= Float DesktopHeight() / scale
					_xOrigin 	= 0
					_yOrigin 	= (virtHeight - _height) / 2.0
					_box1 		= TBox.Create(0,-_yOrigin,_width,_yOrigin)
					_box2 		= TBox.Create(0,_height,_width,_yOrigin)
				
				Default					'4:3 Screen
					virtWidth  	= _width
					virtHeight 	= _height
					_xOrigin 	= 0
					_yOrigin 	= 0

			End Select
			
			Graphics DesktopWidth(),DesktopHeight(),DesktopDepth(),DesktopHertz()		
			SetVirtualResolution virtWidth,virtHeight
			SetViewport 0,0,virtWidth,virtHeight
			SetOrigin _xOrigin,_yOrigin
		
		Default
			RuntimeError( "Invalid Graphics Mode." )
	End Select

End Function

Function c2dFlip( sync=-1 )
	SetColor 0,0,0
	SetTransform(0,1,1)
	If _box1 DrawRect(_box1.x,_box1.y,_box1.w,_box1.h)
	If _box2 DrawRect(_box2.x,_box2.y,_box2.w,_box2.h)
	SetColor 255,255,255
	Flip sync
End Function

Function c2dGraphicsWidth()
	Return _width
End Function

Function c2dGraphicsHeight()
	Return _height
End Function

Function c2dMouseX()
	Return VirtualMouseX() - _xOrigin
End Function

Function c2dMouseY()
	Return VirtualMouseY() - _yOrigin
End Function

Type TBox
	Field x#,y#,w#,h#
	Function Create:TBox(x#,y#,w#,h#)
		Local box:TBox = New TBox
		box.x = x
		box.y = y
		box.w = w
		box.h = h
		Return box
	End Function
End Type
