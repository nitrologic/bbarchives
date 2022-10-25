; ID: 1428
; Author: lonnieh
; Date: 2005-07-24 05:30:44
; Title: Text input
; Description: Text input type for graphic mode that uses getchar()

Graphics 640, 480

Local text:GInput = GInput.Create(10, 30, ":-D ")

While Not KeyHit(KEY_ESCAPE)
	Cls
	DrawText "Check it out. Press [ESC] to exit.", 10, 10
	text.Update
	Flip
	FlushMem
Wend
	
Type GInput
	Field text:String, enabled:Int = True
	Field width:Int, cursor:Int = False, x:Int, y:Int, time:Long = MilliSecs()
	
	Method Update()
		If enabled Then
			Local q:Int = GetChar()
			If q > 0 Then
				Select q
					Case KEY_BACKSPACE
						text = Left(text, Len(text) - 1)
					Case KEY_ENTER
						'I don't know what you'd want to do with this, so I
						'left it blank, perhaps its time for function pointers.
					Case KEY_TAB
						text:+ "     "
					Default
						If Not KeyDown(KEY_ALT) And Not KeyDown(KEY_CONTROL) Then text:+ Chr(q)
				End Select
				width = TextWidth(text)
				cursor = True
				time = MilliSecs()
			End If
			If MilliSecs() > time + 400 Then
				cursor = Not cursor
				time = MilliSecs()
			End If
			If cursor Then
				DrawLine x + width, y + 1, x + width, y + TextHeight("1") - 1
			End If
		End If
		DrawText text, x, y
	End Method
	
	Function Create:GInput(x:Int, y:Int, text:String = "")
		Local ret:GInput = New GInput
		ret.text = text
		ret.x = x
		ret.y = y
		ret.width = TextWidth(text)
		Return ret
	End Function
End Type
