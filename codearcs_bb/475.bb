; ID: 475
; Author: Neo Genesis10
; Date: 2002-11-02 13:11:09
; Title: Wiping transition
; Description: Wipes FrontBuffer to reveal BackBuffer

Const cstUp = 0
Const cstDown = 1
Const cstRight = 2
Const cstLeft = 3
Const cstHoriz = 4
Const cstVert = 5

Type trans_slide
	Field x, y
	Field img
	Field speed
	Field dir
End Type

Global trans_img = CreateImage(GraphicsWidth(), GraphicsHeight())

Function Transition(dir=cstRight, linear=False)

	buffer = GraphicsBuffer()
	
	GrabImage trans_img, 0, 0
	
	SetBuffer FrontBuffer()
	
	Select dir
		Case cstLeft, cstRight
			For y = 0 To GraphicsHeight()
				t.trans_slide = New trans_slide
				t\img = CreateImage(GraphicsWidth(), 1)
				GrabImage t\img, 0, y
				t\x = 0
				t\y = y
				If linear
					t\speed = 6
				Else
					t\speed = Rand(4, 8)
				EndIf
				t\dir = dir
			Next		
		Case cstUp, cstDown
			For x = 0 To GraphicsWidth()
				t.trans_slide = New trans_slide
				t\img = CreateImage(1, GraphicsHeight())
				GrabImage t\img, x, 0
				t\x = x
				t\y = 0
				If linear
					t\speed = 6
				Else
					t\speed = Rand(4, 8)
				EndIf
				t\dir = dir
			Next		
		Case cstHoriz
			tdir = False
			For y = 0 To GraphicsHeight()
				t.trans_slide = New trans_slide
				t\img = CreateImage(GraphicsWidth(), 1)
				GrabImage t\img, 0, y
				t\x = 0
				t\y = y
				If linear
					t\speed = 6
				Else
					t\speed = Rand(4, 8)
				EndIf
				If tdir
					t\dir = cstLeft
				Else
					t\dir = cstRight
				EndIf
				tdir = Not tdir
			Next
		Case cstVert
			tdir = False
			For x = 0 To GraphicsWidth()
				t.trans_slide = New trans_slide
				t\img = CreateImage(1, GraphicsHeight())
				GrabImage t\img, x, 0
				t\x = x
				t\y = 0
				If linear
					t\speed = 6
				Else
					t\speed = Rand(4, 8)
				EndIf
				If tdir
					t\dir = cstUp
				Else
					t\dir = cstDown
				EndIf
				tdir = Not tdir
			Next
	End Select
	
	buffer = GraphicsBuffer()
	
End Function

Function UpdateTransition()

	SetBuffer BackBuffer()
	DrawBlock trans_img, 0, 0
	count = 0
	For t.trans_slide = Each trans_slide
		Select t\dir
			Case cstUp
				t\y = t\y - t\speed
				If t\y < -GraphicsHeight()
					FreeImage t\img
					Delete t
				Else
					DrawBlock t\img, t\x, t\y
					count = count + 1
				EndIf
			Case cstDown
				t\y = t\y + t\speed
				If t\y > GraphicsHeight()
					FreeImage t\img
					Delete t
				Else
					DrawBlock t\img, t\x, t\y
					count = count + 1
				EndIf
			Case cstRight
				t\x = t\x + t\speed
				If t\x > GraphicsWidth()
					FreeImage t\img
					Delete t
				Else
					DrawBlock t\img, t\x, t\y
					count = count + 1
				EndIf
			Case cstLeft
				t\x = t\x - t\speed
				If t\x < -GraphicsWidth()
					FreeImage t\img
					Delete t
				Else
					DrawBlock t\img, t\x, t\y
					count = count + 1
				EndIf
		End Select
	Next
	
	If count = 0
		Return True
	EndIf
	
End Function
