; ID: 2968
; Author: Black3D
; Date: 2012-08-07 17:15:14
; Title: Color ASCII routines
; Description: Drawing ASCII in CGA, EGA and VGA character sets

Global text_font:TImage
Global text_r:Int
Global text_g:Int
Global text_b:Int
Global text_width:Int
Global text_height:Int
Global text_charw:Int
Global text_charh:Int
Global text_typeset:Int				'1=8x16 font, 2=8x8 font, 3=8x8 thin font, 4=12x12 font, 5=8x14 font
Global text_array:Int[240, 150, 4]	'0=asc,1=fc,2=bc,3=blink
Global text_blink:Int = MilliSecs()
Global text_mouse:Int				'use text mouse cursor, 1=on, 0=off
Global text_scalex:Float
Global text_scaley:Float
Global text_xoffset:Int
Global text_yoffset:Int

Function Text_Init(mode:Int = 1, bc:Int = 0, mouse:Int = 0)
	If text_width = 0 And (mode < 1 Or mode > 5) Then mode = 1
	Select mode
		Case 1	'vga, 640x400
			text_width = 80; text_height = 25; text_charw = 8; text_charh = 16; text_typeset = 1
		Case 2	'vga50, 640x400
			text_width = 80; text_height = 50; text_charw = 8; text_charh = 8; text_typeset = 2
		Case 3	'cga, 320x200
			text_width = 40; text_height = 25; text_charw = 8; text_charh = 8; text_typeset = 2
		Case 4	'cga thin, 320x200
			text_width = 40; text_height = 25; text_charw = 8; text_charh = 8; text_typeset = 3
		Case 5	'ega, 640x350
			text_width = 80; text_height = 25; text_charw = 8; text_charh = 14; text_typeset = 5
		Default
			'If using custom values, set mode to 0 (or 6, etc)
	End Select
	Text_LoadFont()
	GetScale (text_scalex, text_scaley)
	bc = Text_Sanitize(bc)
	Text_CLS(bc)
	If mouse = 1 Then Text_ShowMouse() Else Text_HideMouse()
End Function

Function Text_AutoInit(mode:Int = 1, bc:Int = 0, mouse:Int = 0)
	If mode < 1 Or mode > 5 Then mode = 1
	Select mode
		Case 1;text_charw = 8; text_charh = 16; text_typeset = 1
		Case 2;text_charw = 8; text_charh = 8; text_typeset = 2
		Case 3;text_charw = 8; text_charh = 8; text_typeset = 2
		Case 4;text_charw = 8; text_charh = 8; text_typeset = 3
		Case 5;text_charw = 8; text_charh = 14; text_typeset = 5
	End Select
	GetScale (text_scalex, text_scaley)
	text_width = Floor(GraphicsWidth() / (text_charw * text_scalex))
	text_height = Floor(GraphicsHeight() / (text_charh * text_scaley))
	text_xoffset = (GraphicsWidth() - (text_width * (text_charw * text_scalex))) / (2 * text_scalex)
	text_yoffset = (GraphicsHeight() - (text_height * (text_charh * text_scaley))) / (2 * text_scaley)
	Text_LoadFont()
	bc = Text_Sanitize(bc)
	Text_CLS(bc)
	If mouse = 1 Then Text_ShowMouse() Else Text_HideMouse()
End Function

Function Text_ShowMouse()
	text_mouse = 1
	HideMouse()
End Function

Function Text_HideMouse()
	text_mouse = 0
	ShowMouse()
End Function

Function Text_CLS(bc:Int = 0)
	bc = Text_Sanitize(bc)
	text_GetColor bc
	Local x:Int, y:Int
	For x = 0 To text_width - 1
		For y = 0 To text_height - 1
			text_array:Int[x, y, 0] = 32
			text_array:Int[x, y, 1] = 7
			text_array:Int[x, y, 2] = bc
			text_array:Int[x, y, 3] = 0
		Next
	Next
End Function

Function Text_Refresh()
	Local x:Int, y:Int
	'Draw Backgrounds first using GL_QUADS for speed
	'(drawrect reduced performance by around 1500%)
	Begin2D(GL_QUADS)
	For x = 0 To text_width - 1
		For y = 0 To text_height - 1
			Text_GetColor text_array:Int[x, y, 2]
			FastRect ((x * text_charw) + text_xoffset) * text_scalex, ((y * text_charh) + text_yoffset) * text_scaley, text_charw * text_scalex, text_charh * text_scaley
		Next
	Next
	End2D
	'Draw Text
	For x = 0 To text_width - 1
		For y = 0 To text_height - 1
			Text_GetColor text_array:Int[x, y, 1]
			If text_array:Int[x, y, 3] = 1 Then		'Blinking Text
				If MilliSecs() - text_blink < 500 Then
					DrawSubImageRect text_font, ((x * text_charw) + text_xoffset) * text_scalex, ((y * text_charh) + text_yoffset) * text_scaley, text_charw, text_charh, 0, text_array:Int[x, y, 0] * text_charh, text_charw, text_charh
					Else
					If MilliSecs() - text_blink > 1000 Then text_blink = MilliSecs()
				End If
				Else		'Non-Blinking Text
				DrawSubImageRect text_font, ((x * text_charw) + text_xoffset) * text_scalex, ((y * text_charh) + text_yoffset) * text_scaley, text_charw, text_charh, 0, text_array:Int[x, y, 0] * text_charh, text_charw, text_charh
			End If
		Next
	Next
	If text_mouse = 1 Then Text_DrawCursor()
End Function

Function Text_Sanitize:Int(c:Int)
	If c < 0 Then c = 0
	If c > 15 Then c = 15
	Return c
End Function
	
Function Text(t:String, x:Int, y:Int, fc:Int = 7, bc:Int = -1, blink:Int = 0)
	bc = Text_Sanitize(bc)
	fc = Text_Sanitize(fc)
	Local lessx:Int
	Local i:Int
	For i = 1 To Len(t)
		If (x + (i - 1) < text_width) And (x + (i) > 0) And (y > - 1) And (y < text_height) Then
			'Uncomment the next line and following 38 lines in order to enable in-line coloring
'			If Asc(Mid(t, i, 1)) <> 94 Then
				text_array:Int[x + (i - 1) + lessx, y, 0] = Asc(Mid(t, i, 1))
				text_array:Int[x + (i - 1) + lessx, y, 1] = fc
'				Else
'				If i < Len(t) Then
'					If Asc(Mid(t, i + 1, 1)) = 94 Then
'						text_array:Int[x + (i - 1) + lessx, y, 0] = Asc(Mid(t, i, 1))
'						text_array:Int[x + (i - 1) + lessx, y, 1] = fc
'						i = i + 1;lessx:-1
'						Else
'						Select Asc(Mid(t, i + 1, 1))
'							Case 48;fc = 0		'0
'							Case 49;fc = 1		'1
'							Case 50;fc = 2		'2
'							Case 51;fc = 3		'3
'							Case 52;fc = 4		'4
'							Case 53;fc = 5		'5
'							Case 54;fc = 6		'6
'							Case 55;fc = 7		'7
'							Case 56;fc = 8		'8
'							Case 57;fc = 9		'9
'							Case 81;fc = 10		'q
'							Case 87;fc = 11		'w
'							Case 69;fc = 12		'e
'							Case 82;fc = 13		'r
'							Case 84;fc = 14		't
'							Case 89;fc = 15		'y
'							Case 113;fc = 10	'Q
'							Case 119;fc = 11	'W
'							Case 101;fc = 12	'E
'							Case 114;fc = 13	'R
'							Case 116;fc = 14	'T
'							Case 121;fc = 15	'Y
'							Default
'						End Select
'						i = i + 1;lessx:-2
'					End If
'					Else
'					text_array:Int[x + (i - 1) + lessx, y, 0] = Asc(Mid(t, i, 1))
'					text_array:Int[x + (i - 1) + lessx, y, 1] = fc
'				End If
'			End If
			If bc > (-1) Then text_array:Int[x + (i - 1) + lessx, y, 2] = bc
			If blink Then
				text_array:Int[x + (i - 1) + lessx, y, 3] = 1
				Else
				text_array:Int[x + (i - 1) + lessx, y, 3] = 0
			End If
		End If
	Next
End Function

Function Text_DrawCursor()
	Local x:Int, y:Int
	x = MouseX() / (text_charw * text_scalex)
	y = MouseY() / (text_charh * text_scaley)
	If y > text_height - 1 Then y = text_height - 1
	If y < 0 Then y = 0
	If x > text_width - 1 Then x = text_width - 1
	If x < 0 Then x = 0
	Begin2D(GL_QUADS)
	text_GetColor text_reversecolor:Int(text_array:Int[x, y, 2])
	FastRect ((x * text_charw) + text_xoffset) * text_scalex, ((y * text_charh) + text_yoffset) * text_scaley, text_charw * text_scalex, text_charh * text_scaley
	End2D
	text_GetColor text_reversecolor:Int(text_array:Int[x, y, 1])
	If text_array:Int[x, y, 3] = 1 Then
		If MilliSecs() - text_blink < 500 Then
			DrawSubImageRect text_font, ((x * text_charw) + text_xoffset) * text_scalex, ((y * text_charh) + text_yoffset) * text_scaley, text_charw, text_charh, 0, text_array:Int[x, y, 0] * text_charh, text_charw, text_charh
			Else
			If MilliSecs() - text_blink > 1000 Then text_blink = MilliSecs()
		End If
		Else
		DrawSubImageRect text_font, ((x * text_charw) + text_xoffset) * text_scalex, ((y * text_charh) + text_yoffset) * text_scaley, text_charw, text_charh, 0, text_array:Int[x, y, 0] * text_charh, text_charw, text_charh
	End If
End Function

Function Text_ReverseColor:Int(c:Int)
	c = 15 - c
	Return c
End Function

Function Text_GetColor(c:Int)
	c = Text_Sanitize(c)
	Select c
		Case 0;text_r = 001;text_g = 001;text_b = 001
		Case 1;text_r = 000;text_g = 000;text_b = 170
		Case 2;text_r = 000;text_g = 170;text_b = 000
		Case 3;text_r = 000;text_g = 170;text_b = 170
		Case 4;text_r = 170;text_g = 000;text_b = 000
		Case 5;text_r = 170;text_g = 000;text_b = 170
		Case 6;text_r = 170;text_g = 085;text_b = 000
		Case 7;text_r = 170;text_g = 170;text_b = 170
		Case 8;text_r = 085;text_g = 085;text_b = 085
		Case 9;text_r = 085;text_g = 085;text_b = 255
		Case 10;text_r = 085;text_g = 255;text_b = 085
		Case 11;text_r = 085;text_g = 255;text_b = 255
		Case 12;text_r = 255;text_g = 085;text_b = 085
		Case 13;text_r = 255;text_g = 085;text_b = 255
		Case 14;text_r = 255;text_g = 255;text_b = 085
		Case 15;text_r = 255;text_g = 255;text_b = 255
		Case 16;text_r = 000;text_g = 000;text_b = 000
	End Select
	SetColor text_r, text_g, text_b
End Function

Function TextStr:String(t:String, c:Int)
	Local i:Int, a:String
	For i = 1 To c
		a = a + t
	Next
	Return a
End Function

Function Text_GetString:String(x:Int, y:Int, l:Int = 1)
	Local i:Int, a:String
	For i = x To x + (l - 1)
		If i > text_width - 1 Exit
		If y > text_height - 1 Exit
		If i < 0 Then Exit
		If y < 0 Then Exit
		a = a + Chr:String(text_array[i, y, 0])
	Next
	Return a
End Function

Function Text_GetFC:Int(x:Int, y:Int)
	Return text_array[x, y, 1]
	End Function

Function Text_GetBC:Int(x:Int, y:Int)
	Return text_array[x, y, 2]
End Function

Function Text_GetBlink:Int(x:Int, y:Int)
	Return text_array[x, y, 3]
End Function

Function text_offset(x:Int, y:Int)
	text_xoffset = x
	text_yoffset = y
End Function

Function Text_MouseX:Int()
	Local i:Int = MouseX() / (text_charw * text_scalex)
	If i < 0 Then i = 0
	If i > text_width - 1 Then i = text_width - 1
	Return i
End Function

Function Text_MouseY:Int()
	Local i:Int = MouseY() / (text_charh * text_scaley)
	If i < 0 Then i = 0
	If i > text_height - 1 Then i = text_height - 1
	Return i
End Function

Function text_DrawBox(x:Int, y:Int, w:Int, h:Int, fc:Int, bc:Int, strong:Int = 0, shadow:Int = 0, direction:Int = 1, tc:Int = 0)
	Local i:Int, m:Int
	Local sx:Int = x, sy:Int = y
	Local stext:String, sc:Int
	If shadow = 1 Then
		Select direction
			Case 1;sx:-1;sy:+1
			Case 2;sy:+1
			Case 3;sx:+1;sy:+1
			Case 4;sx:+1
			Case 5;sx:+1;sy:-1
			Case 6;sy:-1
			Case 7;sx:-1;sy:-1
			Case 8;sx:-1
		End Select
		For i = sx To sx + w - 1
			For m = sy To sy + h - 1
				stext:String = Text_GetString(i, m)
				If tc = 1 Then
					sc = Text_GetFC(i, m)
					If sc < 9 Then sc = 8 Else sc = sc - 8
					Else
					sc = 8
				End If
				Text (stext, i, m, sc, 0, Text_GetBlink(i, m))
			Next
		Next
	End If
	If strong = 1 Then
		Text "É" + textstr("Í", w - 2) + "»", x, y, fc, bc
		Text "È" + textstr("Í", w - 2) + "¼", x, y + (h - 1), fc, bc
		For i = y + 1 To y + (h - 2)
			Text "º" + textstr(" ", w - 2) + "º", x, i, fc, bc
		Next
		Else
		Text "Ú" + textstr("Ä", w - 2) + "¿", x, y, fc, bc
		Text "À" + textstr("Ä", w - 2) + "Ù", x, y + (h - 1), fc, bc
		For i = y + 1 To y + (h - 2)
			Text "³" + textstr(" ", w - 2) + "³", x, i, fc, bc
		Next
	End If
End Function

Function Text_LoadFont()
	text_font:TImage = CreateImage(text_charw, text_charh * 255,, 0)
	Local y:Int, d:Int, x:Int
	Local temp:TPixmap
	If text_typeset = 1 Then RestoreData font8x16
	If text_typeset = 2 Then RestoreData font8x8
	If text_typeset = 3 Then RestoreData font8x8thin
	'If text_typeset = 4 Then RestoreData font12x12
	If text_typeset = 5 Then RestoreData font8x14
	temp = LockImage(text_font)
	While y < (text_charh * 255)
		ReadData d
		x = text_charw - 1
		While d <> 0
			If (d Mod 2) Then WritePixel(temp, x, y, -1) Else WritePixel(temp, x, y, 16777215)
			d = d / 2
			x = x - 1
		Wend
		y = y + 1
	Wend
	UnlockImage text_font
	temp = Null
End Function

Function FastRect(x:Float, y:Float, w:Int, h:Int)
	glVertex2f x,y ; glVertex2f x+w,y
	glVertex2f x+w,y+h ; glVertex2f x,y+h
End Function

Function Begin2D(mode:Int)
	glDisable GL_TEXTURE_2D ; glBegin mode
End Function

Function End2D()
	glEnd ; glEnable GL_TEXTURE_2D	
End Function

#font8x16
DefData 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 126, 129, 165, 129, 129, 189, 153, 129, 129, 126, 0, 0, 0, 0
defdata 0, 0, 126, 255, 219, 255, 255, 195, 231, 255, 255, 126, 0, 0, 0, 0
defdata 0, 0, 0, 0, 108, 254, 254, 254, 254, 124, 56, 16, 0, 0, 0, 0
defdata 0, 0, 0, 0, 16, 56, 124, 254, 124, 56, 16, 0, 0, 0, 0, 0
defdata 0, 0, 0, 24, 60, 60, 231, 231, 231, 24, 24, 60, 0, 0, 0, 0
defdata 0, 0, 0, 24, 60, 126, 255, 255, 126, 24, 24, 60, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 24, 60, 60, 24, 0, 0, 0, 0, 0, 0
defdata 255, 255, 255, 255, 255, 255, 231, 195, 195, 231, 255, 255, 255, 255, 255, 255
defdata 0, 0, 0, 0, 0, 60, 102, 66, 66, 102, 60, 0, 0, 0, 0, 0
defdata 255, 255, 255, 255, 255, 195, 153, 189, 189, 153, 195, 255, 255, 255, 255, 255
defdata 0, 0, 30, 14, 26, 50, 120, 204, 204, 204, 204, 120, 0, 0, 0, 0
defdata 0, 0, 60, 102, 102, 102, 102, 60, 24, 126, 24, 24, 0, 0, 0, 0
defdata 0, 0, 63, 51, 63, 48, 48, 48, 48, 112, 240, 224, 0, 0, 0, 0
defdata 0, 0, 127, 99, 127, 99, 99, 99, 99, 103, 231, 230, 192, 0, 0, 0
defdata 0, 0, 0, 24, 24, 219, 60, 231, 60, 219, 24, 24, 0, 0, 0, 0
defdata 0, 128, 192, 224, 240, 248, 254, 248, 240, 224, 192, 128, 0, 0, 0, 0
defdata 0, 2, 6, 14, 30, 62, 254, 62, 30, 14, 6, 2, 0, 0, 0, 0
defdata 0, 0, 24, 60, 126, 24, 24, 24, 126, 60, 24, 0, 0, 0, 0, 0
defdata 0, 0, 102, 102, 102, 102, 102, 102, 102, 0, 102, 102, 0, 0, 0, 0
defdata 0, 0, 127, 219, 219, 219, 123, 27, 27, 27, 27, 27, 0, 0, 0, 0
defdata 0, 124, 198, 96, 56, 108, 198, 198, 108, 56, 12, 198, 124, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 254, 254, 254, 254, 0, 0, 0, 0
defdata 0, 0, 24, 60, 126, 24, 24, 24, 126, 60, 24, 126, 0, 0, 0, 0
defdata 0, 0, 24, 60, 126, 24, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0
defdata 0, 0, 24, 24, 24, 24, 24, 24, 24, 126, 60, 24, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 24, 12, 254, 12, 24, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 48, 96, 254, 96, 48, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 192, 192, 192, 254, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 40, 108, 254, 108, 40, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 16, 56, 56, 124, 124, 254, 254, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 254, 254, 124, 124, 56, 56, 16, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 24, 60, 60, 60, 24, 24, 24, 0, 24, 24, 0, 0, 0, 0
defdata 0, 102, 102, 102, 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 108, 108, 254, 108, 108, 108, 254, 108, 108, 0, 0, 0, 0
defdata 24, 24, 124, 198, 194, 192, 124, 6, 6, 134, 198, 124, 24, 24, 0, 0
defdata 0, 0, 0, 0, 194, 198, 12, 24, 48, 96, 198, 134, 0, 0, 0, 0
defdata 0, 0, 56, 108, 108, 56, 118, 220, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 48, 48, 48, 96, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 12, 24, 48, 48, 48, 48, 48, 48, 24, 12, 0, 0, 0, 0
defdata 0, 0, 48, 24, 12, 12, 12, 12, 12, 12, 24, 48, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 102, 60, 255, 60, 102, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 24, 24, 126, 24, 24, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 24, 48, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 254, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 0, 0, 0, 0
defdata 0, 0, 0, 0, 2, 6, 12, 24, 48, 96, 192, 128, 0, 0, 0, 0
defdata 0, 0, 56, 108, 198, 198, 214, 214, 198, 198, 108, 56, 0, 0, 0, 0
defdata 0, 0, 24, 56, 120, 24, 24, 24, 24, 24, 24, 126, 0, 0, 0, 0
defdata 0, 0, 124, 198, 6, 12, 24, 48, 96, 192, 198, 254, 0, 0, 0, 0
defdata 0, 0, 124, 198, 6, 6, 60, 6, 6, 6, 198, 124, 0, 0, 0, 0
defdata 0, 0, 12, 28, 60, 108, 204, 254, 12, 12, 12, 30, 0, 0, 0, 0
defdata 0, 0, 254, 192, 192, 192, 252, 6, 6, 6, 198, 124, 0, 0, 0, 0
defdata 0, 0, 56, 96, 192, 192, 252, 198, 198, 198, 198, 124, 0, 0, 0, 0
defdata 0, 0, 254, 198, 6, 6, 12, 24, 48, 48, 48, 48, 0, 0, 0, 0
defdata 0, 0, 124, 198, 198, 198, 124, 198, 198, 198, 198, 124, 0, 0, 0, 0
defdata 0, 0, 124, 198, 198, 198, 126, 6, 6, 6, 12, 120, 0, 0, 0, 0
defdata 0, 0, 0, 0, 24, 24, 0, 0, 0, 24, 24, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 24, 24, 0, 0, 0, 24, 24, 48, 0, 0, 0, 0
defdata 0, 0, 0, 6, 12, 24, 48, 96, 48, 24, 12, 6, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 126, 0, 0, 126, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 96, 48, 24, 12, 6, 12, 24, 48, 96, 0, 0, 0, 0
defdata 0, 0, 124, 198, 198, 12, 24, 24, 24, 0, 24, 24, 0, 0, 0, 0
defdata 0, 0, 0, 124, 198, 198, 222, 222, 222, 220, 192, 124, 0, 0, 0, 0
defdata 0, 0, 16, 56, 108, 198, 198, 254, 198, 198, 198, 198, 0, 0, 0, 0
defdata 0, 0, 252, 102, 102, 102, 124, 102, 102, 102, 102, 252, 0, 0, 0, 0
defdata 0, 0, 60, 102, 194, 192, 192, 192, 192, 194, 102, 60, 0, 0, 0, 0
defdata 0, 0, 248, 108, 102, 102, 102, 102, 102, 102, 108, 248, 0, 0, 0, 0
defdata 0, 0, 254, 102, 98, 104, 120, 104, 96, 98, 102, 254, 0, 0, 0, 0
defdata 0, 0, 254, 102, 98, 104, 120, 104, 96, 96, 96, 240, 0, 0, 0, 0
defdata 0, 0, 60, 102, 194, 192, 192, 222, 198, 198, 102, 58, 0, 0, 0, 0
defdata 0, 0, 198, 198, 198, 198, 254, 198, 198, 198, 198, 198, 0, 0, 0, 0
defdata 0, 0, 60, 24, 24, 24, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0
defdata 0, 0, 30, 12, 12, 12, 12, 12, 204, 204, 204, 120, 0, 0, 0, 0
defdata 0, 0, 230, 102, 102, 108, 120, 120, 108, 102, 102, 230, 0, 0, 0, 0
defdata 0, 0, 240, 96, 96, 96, 96, 96, 96, 98, 102, 254, 0, 0, 0, 0
defdata 0, 0, 198, 238, 254, 254, 214, 198, 198, 198, 198, 198, 0, 0, 0, 0
defdata 0, 0, 198, 230, 246, 254, 222, 206, 198, 198, 198, 198, 0, 0, 0, 0
defdata 0, 0, 124, 198, 198, 198, 198, 198, 198, 198, 198, 124, 0, 0, 0, 0
defdata 0, 0, 252, 102, 102, 102, 124, 96, 96, 96, 96, 240, 0, 0, 0, 0
defdata 0, 0, 124, 198, 198, 198, 198, 198, 198, 214, 222, 124, 12, 14, 0, 0
defdata 0, 0, 252, 102, 102, 102, 124, 108, 102, 102, 102, 230, 0, 0, 0, 0
defdata 0, 0, 124, 198, 198, 96, 56, 12, 6, 198, 198, 124, 0, 0, 0, 0
defdata 0, 0, 126, 126, 90, 24, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0
defdata 0, 0, 198, 198, 198, 198, 198, 198, 198, 198, 198, 124, 0, 0, 0, 0
defdata 0, 0, 198, 198, 198, 198, 198, 198, 198, 108, 56, 16, 0, 0, 0, 0
defdata 0, 0, 198, 198, 198, 198, 214, 214, 214, 254, 238, 108, 0, 0, 0, 0
defdata 0, 0, 198, 198, 108, 124, 56, 56, 124, 108, 198, 198, 0, 0, 0, 0
defdata 0, 0, 102, 102, 102, 102, 60, 24, 24, 24, 24, 60, 0, 0, 0, 0
defdata 0, 0, 254, 198, 134, 12, 24, 48, 96, 194, 198, 254, 0, 0, 0, 0
defdata 0, 0, 60, 48, 48, 48, 48, 48, 48, 48, 48, 60, 0, 0, 0, 0
defdata 0, 0, 0, 128, 192, 224, 112, 56, 28, 14, 6, 2, 0, 0, 0, 0
defdata 0, 0, 60, 12, 12, 12, 12, 12, 12, 12, 12, 60, 0, 0, 0, 0
defdata 16, 56, 108, 198, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 0, 0
defdata 48, 48, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 120, 12, 124, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 0, 224, 96, 96, 120, 108, 102, 102, 102, 102, 124, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 124, 198, 192, 192, 192, 198, 124, 0, 0, 0, 0
defdata 0, 0, 28, 12, 12, 60, 108, 204, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 124, 198, 254, 192, 192, 198, 124, 0, 0, 0, 0
defdata 0, 0, 56, 108, 100, 96, 240, 96, 96, 96, 96, 240, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 118, 204, 204, 204, 204, 204, 124, 12, 204, 120, 0
defdata 0, 0, 224, 96, 96, 108, 118, 102, 102, 102, 102, 230, 0, 0, 0, 0
defdata 0, 0, 24, 24, 0, 56, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0
defdata 0, 0, 6, 6, 0, 14, 6, 6, 6, 6, 6, 6, 102, 102, 60, 0
defdata 0, 0, 224, 96, 96, 102, 108, 120, 120, 108, 102, 230, 0, 0, 0, 0
defdata 0, 0, 56, 24, 24, 24, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 236, 254, 214, 214, 214, 214, 198, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 220, 102, 102, 102, 102, 102, 102, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 124, 198, 198, 198, 198, 198, 124, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 220, 102, 102, 102, 102, 102, 124, 96, 96, 240, 0
defdata 0, 0, 0, 0, 0, 118, 204, 204, 204, 204, 204, 124, 12, 12, 30, 0
defdata 0, 0, 0, 0, 0, 220, 118, 102, 96, 96, 96, 240, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 124, 198, 96, 56, 12, 198, 124, 0, 0, 0, 0
defdata 0, 0, 16, 48, 48, 252, 48, 48, 48, 48, 54, 28, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 204, 204, 204, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 102, 102, 102, 102, 102, 60, 24, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 198, 198, 214, 214, 214, 254, 108, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 198, 108, 56, 56, 56, 108, 198, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 198, 198, 198, 198, 198, 198, 126, 6, 12, 248, 0
defdata 0, 0, 0, 0, 0, 254, 204, 24, 48, 96, 198, 254, 0, 0, 0, 0
defdata 0, 0, 14, 24, 24, 24, 112, 24, 24, 24, 24, 14, 0, 0, 0, 0
defdata 0, 0, 24, 24, 24, 24, 0, 24, 24, 24, 24, 24, 0, 0, 0, 0
defdata 0, 0, 112, 24, 24, 24, 14, 24, 24, 24, 24, 112, 0, 0, 0, 0
defdata 0, 0, 118, 220, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 16, 56, 108, 198, 198, 198, 254, 0, 0, 0, 0, 0
defdata 0, 0, 60, 102, 194, 192, 192, 192, 194, 102, 60, 12, 6, 124, 0, 0
defdata 0, 0, 204, 0, 0, 204, 204, 204, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 12, 24, 48, 0, 124, 198, 254, 192, 192, 198, 124, 0, 0, 0, 0
defdata 0, 16, 56, 108, 0, 120, 12, 124, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 0, 204, 0, 0, 120, 12, 124, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 96, 48, 24, 0, 120, 12, 124, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 56, 108, 56, 0, 120, 12, 124, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 0, 0, 0, 60, 102, 96, 96, 102, 60, 12, 6, 60, 0, 0, 0
defdata 0, 16, 56, 108, 0, 124, 198, 254, 192, 192, 198, 124, 0, 0, 0, 0
defdata 0, 0, 198, 0, 0, 124, 198, 254, 192, 192, 198, 124, 0, 0, 0, 0
defdata 0, 96, 48, 24, 0, 124, 198, 254, 192, 192, 198, 124, 0, 0, 0, 0
defdata 0, 0, 102, 0, 0, 56, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0
defdata 0, 24, 60, 102, 0, 56, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0
defdata 0, 96, 48, 24, 0, 56, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0
defdata 0, 198, 0, 16, 56, 108, 198, 198, 254, 198, 198, 198, 0, 0, 0, 0
defdata 56, 108, 56, 0, 56, 108, 198, 198, 254, 198, 198, 198, 0, 0, 0, 0
defdata 24, 48, 96, 0, 254, 102, 96, 124, 96, 96, 102, 254, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 204, 118, 54, 126, 216, 216, 110, 0, 0, 0, 0
defdata 0, 0, 62, 108, 204, 204, 254, 204, 204, 204, 204, 206, 0, 0, 0, 0
defdata 0, 16, 56, 108, 0, 124, 198, 198, 198, 198, 198, 124, 0, 0, 0, 0
defdata 0, 0, 198, 0, 0, 124, 198, 198, 198, 198, 198, 124, 0, 0, 0, 0
defdata 0, 96, 48, 24, 0, 124, 198, 198, 198, 198, 198, 124, 0, 0, 0, 0
defdata 0, 48, 120, 204, 0, 204, 204, 204, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 96, 48, 24, 0, 204, 204, 204, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 0, 198, 0, 0, 198, 198, 198, 198, 198, 198, 126, 6, 12, 120, 0
defdata 0, 198, 0, 124, 198, 198, 198, 198, 198, 198, 198, 124, 0, 0, 0, 0
defdata 0, 198, 0, 198, 198, 198, 198, 198, 198, 198, 198, 124, 0, 0, 0, 0
defdata 0, 24, 24, 60, 102, 96, 96, 96, 102, 60, 24, 24, 0, 0, 0, 0
defdata 0, 56, 108, 100, 96, 240, 96, 96, 96, 96, 230, 252, 0, 0, 0, 0
defdata 0, 0, 102, 102, 60, 24, 126, 24, 126, 24, 24, 24, 0, 0, 0, 0
defdata 0, 248, 204, 204, 248, 196, 204, 222, 204, 204, 204, 198, 0, 0, 0, 0
defdata 0, 14, 27, 24, 24, 24, 126, 24, 24, 24, 24, 24, 216, 112, 0, 0
defdata 0, 24, 48, 96, 0, 120, 12, 124, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 12, 24, 48, 0, 56, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0
defdata 0, 24, 48, 96, 0, 124, 198, 198, 198, 198, 198, 124, 0, 0, 0, 0
defdata 0, 24, 48, 96, 0, 204, 204, 204, 204, 204, 204, 118, 0, 0, 0, 0
defdata 0, 0, 118, 220, 0, 220, 102, 102, 102, 102, 102, 102, 0, 0, 0, 0
defdata 118, 220, 0, 198, 230, 246, 254, 222, 206, 198, 198, 198, 0, 0, 0, 0
defdata 0, 60, 108, 108, 62, 0, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 56, 108, 108, 56, 0, 124, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 48, 48, 0, 48, 48, 96, 192, 198, 198, 124, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 254, 192, 192, 192, 192, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 254, 6, 6, 6, 6, 0, 0, 0, 0, 0
defdata 0, 192, 192, 194, 198, 204, 24, 48, 96, 220, 134, 12, 24, 62, 0, 0
defdata 0, 192, 192, 194, 198, 204, 24, 48, 102, 206, 158, 62, 6, 6, 0, 0
defdata 0, 0, 24, 24, 0, 24, 24, 24, 60, 60, 60, 24, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 54, 108, 216, 108, 54, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 216, 108, 54, 108, 216, 0, 0, 0, 0, 0, 0
defdata 17, 68, 17, 68, 17, 68, 17, 68, 17, 68, 17, 68, 17, 68, 17, 68
defdata 85, 170, 85, 170, 85, 170, 85, 170, 85, 170, 85, 170, 85, 170, 85, 170
defdata 221, 119, 221, 119, 221, 119, 221, 119, 221, 119, 221, 119, 221, 119, 221, 119
defdata 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24
defdata 24, 24, 24, 24, 24, 24, 24, 248, 24, 24, 24, 24, 24, 24, 24, 24
defdata 24, 24, 24, 24, 24, 248, 24, 248, 24, 24, 24, 24, 24, 24, 24, 24
defdata 54, 54, 54, 54, 54, 54, 54, 246, 54, 54, 54, 54, 54, 54, 54, 54
defdata 0, 0, 0, 0, 0, 0, 0, 254, 54, 54, 54, 54, 54, 54, 54, 54
defdata 0, 0, 0, 0, 0, 248, 24, 248, 24, 24, 24, 24, 24, 24, 24, 24
defdata 54, 54, 54, 54, 54, 246, 6, 246, 54, 54, 54, 54, 54, 54, 54, 54
defdata 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54
defdata 0, 0, 0, 0, 0, 254, 6, 246, 54, 54, 54, 54, 54, 54, 54, 54
defdata 54, 54, 54, 54, 54, 246, 6, 254, 0, 0, 0, 0, 0, 0, 0, 0
defdata 54, 54, 54, 54, 54, 54, 54, 254, 0, 0, 0, 0, 0, 0, 0, 0
defdata 24, 24, 24, 24, 24, 248, 24, 248, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 248, 24, 24, 24, 24, 24, 24, 24, 24
defdata 24, 24, 24, 24, 24, 24, 24, 31, 0, 0, 0, 0, 0, 0, 0, 0
defdata 24, 24, 24, 24, 24, 24, 24, 255, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 255, 24, 24, 24, 24, 24, 24, 24, 24
defdata 24, 24, 24, 24, 24, 24, 24, 31, 24, 24, 24, 24, 24, 24, 24, 24
defdata 0, 0, 0, 0, 0, 0, 0, 255, 0, 0, 0, 0, 0, 0, 0, 0
defdata 24, 24, 24, 24, 24, 24, 24, 255, 24, 24, 24, 24, 24, 24, 24, 24
defdata 24, 24, 24, 24, 24, 31, 24, 31, 24, 24, 24, 24, 24, 24, 24, 24
defdata 54, 54, 54, 54, 54, 54, 54, 55, 54, 54, 54, 54, 54, 54, 54, 54
defdata 54, 54, 54, 54, 54, 55, 48, 63, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 63, 48, 55, 54, 54, 54, 54, 54, 54, 54, 54
defdata 54, 54, 54, 54, 54, 247, 0, 255, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 255, 0, 247, 54, 54, 54, 54, 54, 54, 54, 54
defdata 54, 54, 54, 54, 54, 55, 48, 55, 54, 54, 54, 54, 54, 54, 54, 54
defdata 0, 0, 0, 0, 0, 255, 0, 255, 0, 0, 0, 0, 0, 0, 0, 0
defdata 54, 54, 54, 54, 54, 247, 0, 247, 54, 54, 54, 54, 54, 54, 54, 54
defdata 24, 24, 24, 24, 24, 255, 0, 255, 0, 0, 0, 0, 0, 0, 0, 0
defdata 54, 54, 54, 54, 54, 54, 54, 255, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 255, 0, 255, 24, 24, 24, 24, 24, 24, 24, 24
defdata 0, 0, 0, 0, 0, 0, 0, 255, 54, 54, 54, 54, 54, 54, 54, 54
defdata 54, 54, 54, 54, 54, 54, 54, 63, 0, 0, 0, 0, 0, 0, 0, 0
defdata 24, 24, 24, 24, 24, 31, 24, 31, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 31, 24, 31, 24, 24, 24, 24, 24, 24, 24, 24
defdata 0, 0, 0, 0, 0, 0, 0, 63, 54, 54, 54, 54, 54, 54, 54, 54
defdata 54, 54, 54, 54, 54, 54, 54, 255, 54, 54, 54, 54, 54, 54, 54, 54
defdata 24, 24, 24, 24, 24, 255, 24, 255, 24, 24, 24, 24, 24, 24, 24, 24
defdata 24, 24, 24, 24, 24, 24, 24, 248, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 31, 24, 24, 24, 24, 24, 24, 24, 24
defdata 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255, 255
defdata 0, 0, 0, 0, 0, 0, 0, 255, 255, 255, 255, 255, 255, 255, 255, 255
defdata 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240
defdata 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15
defdata 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 118, 220, 216, 216, 216, 220, 118, 0, 0, 0, 0
defdata 0, 0, 120, 204, 204, 204, 216, 204, 198, 198, 198, 204, 0, 0, 0, 0
defdata 0, 0, 254, 198, 198, 192, 192, 192, 192, 192, 192, 192, 0, 0, 0, 0
defdata 0, 0, 0, 0, 254, 108, 108, 108, 108, 108, 108, 108, 0, 0, 0, 0
defdata 0, 0, 0, 254, 198, 96, 48, 24, 48, 96, 198, 254, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 126, 216, 216, 216, 216, 216, 112, 0, 0, 0, 0
defdata 0, 0, 0, 0, 102, 102, 102, 102, 102, 124, 96, 96, 192, 0, 0, 0
defdata 0, 0, 0, 0, 118, 220, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0
defdata 0, 0, 0, 126, 24, 60, 102, 102, 102, 60, 24, 126, 0, 0, 0, 0
defdata 0, 0, 0, 56, 108, 198, 198, 254, 198, 198, 108, 56, 0, 0, 0, 0
defdata 0, 0, 56, 108, 198, 198, 198, 108, 108, 108, 108, 238, 0, 0, 0, 0
defdata 0, 0, 30, 48, 24, 12, 62, 102, 102, 102, 102, 60, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 126, 219, 219, 219, 126, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 3, 6, 126, 219, 219, 243, 126, 96, 192, 0, 0, 0, 0
defdata 0, 0, 28, 48, 96, 96, 124, 96, 96, 96, 48, 28, 0, 0, 0, 0
defdata 0, 0, 0, 124, 198, 198, 198, 198, 198, 198, 198, 198, 0, 0, 0, 0
defdata 0, 0, 0, 0, 254, 0, 0, 254, 0, 0, 254, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 24, 24, 126, 24, 24, 0, 0, 255, 0, 0, 0, 0
defdata 0, 0, 0, 48, 24, 12, 6, 12, 24, 48, 0, 126, 0, 0, 0, 0
defdata 0, 0, 0, 12, 24, 48, 96, 48, 24, 12, 0, 126, 0, 0, 0, 0
defdata 0, 0, 14, 27, 27, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24
defdata 24, 24, 24, 24, 24, 24, 24, 24, 216, 216, 216, 112, 0, 0, 0, 0
defdata 0, 0, 0, 0, 24, 24, 0, 126, 0, 24, 24, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 118, 220, 0, 118, 220, 0, 0, 0, 0, 0, 0
defdata 0, 56, 108, 108, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 24, 24, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0
defdata 0, 15, 12, 12, 12, 12, 12, 236, 108, 108, 60, 28, 0, 0, 0, 0
defdata 0, 216, 108, 108, 108, 108, 108, 0, 0, 0, 0, 0, 0, 0, 0, 0
DefData 0, 112, 216, 48, 96, 200, 248, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 124, 124, 124, 124, 124, 124, 124, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0

#Font8x8
DefData 0, 0, 0, 0, 0, 0, 0, 0, 126, 129, 165, 129, 189, 153, 129, 126
defdata 126, 255, 219, 255, 195, 231, 255, 126, 108, 254, 254, 254, 124, 56, 16, 0
defdata 16, 56, 124, 254, 124, 56, 16, 0, 56, 124, 56, 254, 254, 214, 16, 56
defdata 16, 16, 56, 124, 254, 124, 16, 56, 0, 0, 24, 60, 60, 24, 0, 0
defdata 255, 255, 231, 195, 195, 231, 255, 255, 0, 60, 102, 66, 66, 102, 60, 0
defdata 255, 195, 153, 189, 189, 153, 195, 255, 15, 7, 15, 125, 204, 204, 204, 120
defdata 60, 102, 102, 102, 60, 24, 126, 24, 63, 51, 63, 48, 48, 112, 240, 224
defdata 127, 99, 127, 99, 99, 103, 230, 192, 24, 219, 60, 231, 231, 60, 219, 24
defdata 128, 224, 248, 254, 248, 224, 128, 0, 2, 14, 62, 254, 62, 14, 2, 0
defdata 24, 60, 126, 24, 24, 126, 60, 24, 102, 102, 102, 102, 102, 0, 102, 0
defdata 127, 219, 219, 123, 27, 27, 27, 0, 62, 99, 56, 108, 108, 56, 204, 120
defdata 0, 0, 0, 0, 126, 126, 126, 0, 24, 60, 126, 24, 126, 60, 24, 255
defdata 24, 60, 126, 24, 24, 24, 24, 0, 24, 24, 24, 24, 126, 60, 24, 0
defdata 0, 24, 12, 254, 12, 24, 0, 0, 0, 48, 96, 254, 96, 48, 0, 0
defdata 0, 0, 192, 192, 192, 254, 0, 0, 0, 36, 102, 255, 102, 36, 0, 0
defdata 0, 24, 60, 126, 255, 255, 0, 0, 0, 255, 255, 126, 60, 24, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 48, 120, 120, 48, 48, 0, 48, 0
defdata 108, 108, 108, 0, 0, 0, 0, 0, 108, 108, 254, 108, 254, 108, 108, 0
defdata 48, 124, 192, 120, 12, 248, 48, 0, 0, 198, 204, 24, 48, 102, 198, 0
defdata 56, 108, 56, 118, 220, 204, 118, 0, 96, 96, 192, 0, 0, 0, 0, 0
defdata 24, 48, 96, 96, 96, 48, 24, 0, 96, 48, 24, 24, 24, 48, 96, 0
defdata 0, 102, 60, 255, 60, 102, 0, 0, 0, 48, 48, 252, 48, 48, 0, 0
defdata 0, 0, 0, 0, 0, 48, 48, 96, 0, 0, 0, 252, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 48, 48, 0, 6, 12, 24, 48, 96, 192, 128, 0
defdata 124, 198, 206, 222, 246, 230, 124, 0, 48, 112, 48, 48, 48, 48, 252, 0
defdata 120, 204, 12, 56, 96, 204, 252, 0, 120, 204, 12, 56, 12, 204, 120, 0
defdata 28, 60, 108, 204, 254, 12, 30, 0, 252, 192, 248, 12, 12, 204, 120, 0
defdata 56, 96, 192, 248, 204, 204, 120, 0, 252, 204, 12, 24, 48, 48, 48, 0
defdata 120, 204, 204, 120, 204, 204, 120, 0, 120, 204, 204, 124, 12, 24, 112, 0
defdata 0, 48, 48, 0, 0, 48, 48, 0, 0, 48, 48, 0, 0, 48, 48, 96
defdata 24, 48, 96, 192, 96, 48, 24, 0, 0, 0, 252, 0, 0, 252, 0, 0
defdata 96, 48, 24, 12, 24, 48, 96, 0, 120, 204, 12, 24, 48, 0, 48, 0
defdata 124, 198, 222, 222, 222, 192, 120, 0, 48, 120, 204, 204, 252, 204, 204, 0
defdata 252, 102, 102, 124, 102, 102, 252, 0, 60, 102, 192, 192, 192, 102, 60, 0
defdata 248, 108, 102, 102, 102, 108, 248, 0, 254, 98, 104, 120, 104, 98, 254, 0
defdata 254, 98, 104, 120, 104, 96, 240, 0, 60, 102, 192, 192, 206, 102, 62, 0
defdata 204, 204, 204, 252, 204, 204, 204, 0, 120, 48, 48, 48, 48, 48, 120, 0
defdata 30, 12, 12, 12, 204, 204, 120, 0, 230, 102, 108, 120, 108, 102, 230, 0
defdata 240, 96, 96, 96, 98, 102, 254, 0, 198, 238, 254, 254, 214, 198, 198, 0
defdata 198, 230, 246, 222, 206, 198, 198, 0, 56, 108, 198, 198, 198, 108, 56, 0
defdata 252, 102, 102, 124, 96, 96, 240, 0, 120, 204, 204, 204, 220, 120, 28, 0
defdata 252, 102, 102, 124, 108, 102, 230, 0, 120, 204, 96, 48, 24, 204, 120, 0
defdata 252, 180, 48, 48, 48, 48, 120, 0, 204, 204, 204, 204, 204, 204, 252, 0
defdata 204, 204, 204, 204, 204, 120, 48, 0, 198, 198, 198, 214, 254, 238, 198, 0
defdata 198, 198, 108, 56, 56, 108, 198, 0, 204, 204, 204, 120, 48, 48, 120, 0
defdata 254, 198, 140, 24, 50, 102, 254, 0, 120, 96, 96, 96, 96, 96, 120, 0
defdata 192, 96, 48, 24, 12, 6, 2, 0, 120, 24, 24, 24, 24, 24, 120, 0
defdata 16, 56, 108, 198, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255
defdata 48, 48, 24, 0, 0, 0, 0, 0, 0, 0, 120, 12, 124, 204, 118, 0
defdata 224, 96, 96, 124, 102, 102, 220, 0, 0, 0, 120, 204, 192, 204, 120, 0
defdata 28, 12, 12, 124, 204, 204, 118, 0, 0, 0, 120, 204, 252, 192, 120, 0
defdata 56, 108, 96, 240, 96, 96, 240, 0, 0, 0, 118, 204, 204, 124, 12, 248
defdata 224, 96, 108, 118, 102, 102, 230, 0, 48, 0, 112, 48, 48, 48, 120, 0
defdata 12, 0, 12, 12, 12, 204, 204, 120, 224, 96, 102, 108, 120, 108, 230, 0
defdata 112, 48, 48, 48, 48, 48, 120, 0, 0, 0, 204, 254, 254, 214, 198, 0
defdata 0, 0, 248, 204, 204, 204, 204, 0, 0, 0, 120, 204, 204, 204, 120, 0
defdata 0, 0, 220, 102, 102, 124, 96, 240, 0, 0, 118, 204, 204, 124, 12, 30
defdata 0, 0, 220, 118, 102, 96, 240, 0, 0, 0, 124, 192, 120, 12, 248, 0
defdata 16, 48, 124, 48, 48, 52, 24, 0, 0, 0, 204, 204, 204, 204, 118, 0
defdata 0, 0, 204, 204, 204, 120, 48, 0, 0, 0, 198, 214, 254, 254, 108, 0
defdata 0, 0, 198, 108, 56, 108, 198, 0, 0, 0, 204, 204, 204, 124, 12, 248
defdata 0, 0, 252, 152, 48, 100, 252, 0, 28, 48, 48, 224, 48, 48, 28, 0
defdata 24, 24, 24, 0, 24, 24, 24, 0, 224, 48, 48, 28, 48, 48, 224, 0
defdata 118, 220, 0, 0, 0, 0, 0, 0, 0, 16, 56, 108, 198, 198, 254, 0
defdata 120, 204, 192, 204, 120, 24, 12, 120, 0, 204, 0, 204, 204, 204, 126, 0
defdata 28, 0, 120, 204, 252, 192, 120, 0, 126, 195, 60, 6, 62, 102, 63, 0
defdata 204, 0, 120, 12, 124, 204, 126, 0, 224, 0, 120, 12, 124, 204, 126, 0
defdata 48, 48, 120, 12, 124, 204, 126, 0, 0, 0, 120, 192, 192, 120, 12, 56
defdata 126, 195, 60, 102, 126, 96, 60, 0, 204, 0, 120, 204, 252, 192, 120, 0
defdata 224, 0, 120, 204, 252, 192, 120, 0, 204, 0, 112, 48, 48, 48, 120, 0
defdata 124, 198, 56, 24, 24, 24, 60, 0, 224, 0, 112, 48, 48, 48, 120, 0
defdata 198, 56, 108, 198, 254, 198, 198, 0, 48, 48, 0, 120, 204, 252, 204, 0
defdata 28, 0, 252, 96, 120, 96, 252, 0, 0, 0, 127, 12, 127, 204, 127, 0
defdata 62, 108, 204, 254, 204, 204, 206, 0, 120, 204, 0, 120, 204, 204, 120, 0
defdata 0, 204, 0, 120, 204, 204, 120, 0, 0, 224, 0, 120, 204, 204, 120, 0
defdata 120, 204, 0, 204, 204, 204, 126, 0, 0, 224, 0, 204, 204, 204, 126, 0
defdata 0, 204, 0, 204, 204, 124, 12, 248, 195, 24, 60, 102, 102, 60, 24, 0
defdata 204, 0, 204, 204, 204, 204, 120, 0, 24, 24, 126, 192, 192, 126, 24, 24
defdata 56, 108, 100, 240, 96, 230, 252, 0, 204, 204, 120, 252, 48, 252, 48, 48
defdata 248, 204, 204, 250, 198, 207, 198, 199, 14, 27, 24, 60, 24, 24, 216, 112
defdata 28, 0, 120, 12, 124, 204, 126, 0, 56, 0, 112, 48, 48, 48, 120, 0
defdata 0, 28, 0, 120, 204, 204, 120, 0, 0, 28, 0, 204, 204, 204, 126, 0
defdata 0, 248, 0, 248, 204, 204, 204, 0, 252, 0, 204, 236, 252, 220, 204, 0
defdata 60, 108, 108, 62, 0, 126, 0, 0, 56, 108, 108, 56, 0, 124, 0, 0
defdata 48, 0, 48, 96, 192, 204, 120, 0, 0, 0, 0, 252, 192, 192, 0, 0
defdata 0, 0, 0, 252, 12, 12, 0, 0, 195, 198, 204, 222, 51, 102, 204, 15
defdata 195, 198, 204, 219, 55, 111, 207, 3, 24, 24, 0, 24, 24, 24, 24, 0
defdata 0, 51, 102, 204, 102, 51, 0, 0, 0, 204, 102, 51, 102, 204, 0, 0
defdata 34, 136, 34, 136, 34, 136, 34, 136, 85, 170, 85, 170, 85, 170, 85, 170
defdata 219, 119, 219, 238, 219, 119, 219, 238, 24, 24, 24, 24, 24, 24, 24, 24
defdata 24, 24, 24, 24, 248, 24, 24, 24, 24, 24, 248, 24, 248, 24, 24, 24
defdata 54, 54, 54, 54, 246, 54, 54, 54, 0, 0, 0, 0, 254, 54, 54, 54
defdata 0, 0, 248, 24, 248, 24, 24, 24, 54, 54, 246, 6, 246, 54, 54, 54
defdata 54, 54, 54, 54, 54, 54, 54, 54, 0, 0, 254, 6, 246, 54, 54, 54
defdata 54, 54, 246, 6, 254, 0, 0, 0, 54, 54, 54, 54, 254, 0, 0, 0
defdata 24, 24, 248, 24, 248, 0, 0, 0, 0, 0, 0, 0, 248, 24, 24, 24
defdata 24, 24, 24, 24, 31, 0, 0, 0, 24, 24, 24, 24, 255, 0, 0, 0
defdata 0, 0, 0, 0, 255, 24, 24, 24, 24, 24, 24, 24, 31, 24, 24, 24
defdata 0, 0, 0, 0, 255, 0, 0, 0, 24, 24, 24, 24, 255, 24, 24, 24
defdata 24, 24, 31, 24, 31, 24, 24, 24, 54, 54, 54, 54, 55, 54, 54, 54
defdata 54, 54, 55, 48, 63, 0, 0, 0, 0, 0, 63, 48, 55, 54, 54, 54
defdata 54, 54, 247, 0, 255, 0, 0, 0, 0, 0, 255, 0, 247, 54, 54, 54
defdata 54, 54, 55, 48, 55, 54, 54, 54, 0, 0, 255, 0, 255, 0, 0, 0
defdata 54, 54, 247, 0, 247, 54, 54, 54, 24, 24, 255, 0, 255, 0, 0, 0
defdata 54, 54, 54, 54, 255, 0, 0, 0, 0, 0, 255, 0, 255, 24, 24, 24
defdata 0, 0, 0, 0, 255, 54, 54, 54, 54, 54, 54, 54, 63, 0, 0, 0
defdata 24, 24, 31, 24, 31, 0, 0, 0, 0, 0, 31, 24, 31, 24, 24, 24
defdata 0, 0, 0, 0, 63, 54, 54, 54, 54, 54, 54, 54, 255, 54, 54, 54
defdata 24, 24, 255, 24, 255, 24, 24, 24, 24, 24, 24, 24, 248, 0, 0, 0
defdata 0, 0, 0, 0, 31, 24, 24, 24, 255, 255, 255, 255, 255, 255, 255, 255
defdata 0, 0, 0, 0, 255, 255, 255, 255, 240, 240, 240, 240, 240, 240, 240, 240
defdata 15, 15, 15, 15, 15, 15, 15, 15, 255, 255, 255, 255, 0, 0, 0, 0
defdata 0, 0, 118, 220, 200, 220, 118, 0, 0, 120, 204, 248, 204, 248, 192, 192
defdata 0, 252, 204, 192, 192, 192, 192, 0, 0, 254, 108, 108, 108, 108, 108, 0
defdata 252, 204, 96, 48, 96, 204, 252, 0, 0, 0, 126, 216, 216, 216, 112, 0
defdata 0, 102, 102, 102, 102, 124, 96, 192, 0, 118, 220, 24, 24, 24, 24, 0
defdata 252, 48, 120, 204, 204, 120, 48, 252, 56, 108, 198, 254, 198, 108, 56, 0
defdata 56, 108, 198, 198, 108, 108, 238, 0, 28, 48, 24, 124, 204, 204, 120, 0
defdata 0, 0, 126, 219, 219, 126, 0, 0, 6, 12, 126, 219, 219, 126, 96, 192
defdata 56, 96, 192, 248, 192, 96, 56, 0, 120, 204, 204, 204, 204, 204, 204, 0
defdata 0, 252, 0, 252, 0, 252, 0, 0, 48, 48, 252, 48, 48, 0, 252, 0
defdata 96, 48, 24, 48, 96, 0, 252, 0, 24, 48, 96, 48, 24, 0, 252, 0
defdata 14, 27, 27, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 216, 216, 112
defdata 48, 48, 0, 252, 0, 48, 48, 0, 0, 118, 220, 0, 118, 220, 0, 0
defdata 56, 108, 108, 56, 0, 0, 0, 0, 0, 0, 0, 24, 24, 0, 0, 0
defdata 0, 0, 0, 0, 24, 0, 0, 0, 15, 12, 12, 12, 236, 108, 60, 28
DefData 120, 108, 108, 108, 108, 0, 0, 0, 112, 24, 48, 96, 120, 0, 0, 0
DefData 0, 0, 0, 0, 0, 0, 0, 0

#font8x8thin
DefData 0, 0, 0, 0, 0, 0, 0, 0, 126, 129, 165, 129, 189, 153, 129, 126
defdata 126, 255, 219, 255, 195, 231, 255, 126, 108, 254, 254, 254, 124, 56, 16, 0
defdata 16, 56, 124, 254, 124, 56, 16, 0, 56, 124, 56, 254, 254, 214, 16, 56
defdata 16, 16, 56, 124, 254, 124, 16, 56, 0, 0, 24, 60, 60, 24, 0, 0
defdata 255, 255, 231, 195, 195, 231, 255, 255, 0, 60, 102, 66, 66, 102, 60, 0
defdata 255, 195, 153, 189, 189, 153, 195, 255, 15, 3, 5, 125, 132, 132, 132, 120
defdata 60, 66, 66, 66, 60, 24, 126, 24, 63, 33, 63, 32, 32, 96, 224, 192
defdata 63, 33, 63, 33, 35, 103, 230, 192, 24, 219, 60, 231, 231, 60, 219, 24
defdata 128, 224, 248, 254, 248, 224, 128, 0, 2, 14, 62, 254, 62, 14, 2, 0
defdata 24, 60, 126, 24, 24, 126, 60, 24, 36, 36, 36, 36, 36, 0, 36, 0
defdata 127, 146, 146, 114, 18, 18, 18, 0, 62, 99, 56, 68, 68, 56, 204, 120
defdata 0, 0, 0, 0, 126, 126, 126, 0, 24, 60, 126, 24, 126, 60, 24, 255
defdata 16, 56, 124, 84, 16, 16, 16, 0, 16, 16, 16, 84, 124, 56, 16, 0
defdata 0, 24, 12, 254, 12, 24, 0, 0, 0, 48, 96, 254, 96, 48, 0, 0
defdata 0, 0, 64, 64, 64, 126, 0, 0, 0, 36, 102, 255, 102, 36, 0, 0
defdata 0, 16, 56, 124, 254, 254, 0, 0, 0, 254, 254, 124, 56, 16, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 16, 56, 56, 16, 16, 0, 16, 0
defdata 36, 36, 36, 0, 0, 0, 0, 0, 36, 36, 126, 36, 126, 36, 36, 0
defdata 24, 62, 64, 60, 2, 124, 24, 0, 0, 98, 100, 8, 16, 38, 70, 0
defdata 48, 72, 48, 86, 136, 136, 118, 0, 16, 16, 32, 0, 0, 0, 0, 0
defdata 16, 32, 64, 64, 64, 32, 16, 0, 32, 16, 8, 8, 8, 16, 32, 0
defdata 0, 68, 56, 254, 56, 68, 0, 0, 0, 16, 16, 124, 16, 16, 0, 0
defdata 0, 0, 0, 0, 0, 16, 16, 32, 0, 0, 0, 126, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 16, 16, 0, 0, 2, 4, 8, 16, 32, 64, 0
defdata 60, 66, 70, 74, 82, 98, 60, 0, 16, 48, 80, 16, 16, 16, 124, 0
defdata 60, 66, 2, 12, 48, 66, 126, 0, 60, 66, 2, 28, 2, 66, 60, 0
defdata 8, 24, 40, 72, 254, 8, 28, 0, 126, 64, 124, 2, 2, 66, 60, 0
defdata 28, 32, 64, 124, 66, 66, 60, 0, 126, 66, 4, 8, 16, 16, 16, 0
defdata 60, 66, 66, 60, 66, 66, 60, 0, 60, 66, 66, 62, 2, 4, 56, 0
defdata 0, 16, 16, 0, 0, 16, 16, 0, 0, 16, 16, 0, 0, 16, 16, 32
defdata 8, 16, 32, 64, 32, 16, 8, 0, 0, 0, 126, 0, 0, 126, 0, 0
defdata 16, 8, 4, 2, 4, 8, 16, 0, 60, 66, 2, 4, 8, 0, 8, 0
defdata 60, 66, 94, 82, 94, 64, 60, 0, 24, 36, 66, 66, 126, 66, 66, 0
defdata 124, 34, 34, 60, 34, 34, 124, 0, 28, 34, 64, 64, 64, 34, 28, 0
defdata 120, 36, 34, 34, 34, 36, 120, 0, 126, 34, 40, 56, 40, 34, 126, 0
defdata 126, 34, 40, 56, 40, 32, 112, 0, 28, 34, 64, 64, 78, 34, 30, 0
defdata 66, 66, 66, 126, 66, 66, 66, 0, 56, 16, 16, 16, 16, 16, 56, 0
defdata 14, 4, 4, 4, 68, 68, 56, 0, 98, 36, 40, 48, 40, 36, 99, 0
defdata 112, 32, 32, 32, 32, 34, 126, 0, 99, 85, 73, 65, 65, 65, 65, 0
defdata 98, 82, 74, 70, 66, 66, 66, 0, 24, 36, 66, 66, 66, 36, 24, 0
defdata 124, 34, 34, 60, 32, 32, 112, 0, 60, 66, 66, 66, 74, 60, 3, 0
defdata 124, 34, 34, 60, 40, 36, 114, 0, 60, 66, 64, 60, 2, 66, 60, 0
defdata 127, 73, 8, 8, 8, 8, 28, 0, 66, 66, 66, 66, 66, 66, 60, 0
defdata 65, 65, 65, 65, 34, 20, 8, 0, 65, 65, 65, 73, 73, 73, 54, 0
defdata 65, 34, 20, 8, 20, 34, 65, 0, 65, 34, 20, 8, 8, 8, 28, 0
defdata 127, 66, 4, 8, 16, 33, 127, 0, 120, 64, 64, 64, 64, 64, 120, 0
defdata 128, 64, 32, 16, 8, 4, 2, 0, 120, 8, 8, 8, 8, 8, 120, 0
defdata 16, 40, 68, 130, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255
defdata 16, 16, 8, 0, 0, 0, 0, 0, 0, 0, 60, 2, 62, 66, 63, 0
defdata 96, 32, 32, 46, 49, 49, 46, 0, 0, 0, 60, 66, 64, 66, 60, 0
defdata 6, 2, 2, 58, 70, 70, 59, 0, 0, 0, 60, 66, 126, 64, 60, 0
defdata 12, 18, 16, 56, 16, 16, 56, 0, 0, 0, 61, 66, 66, 62, 2, 124
defdata 96, 32, 44, 50, 34, 34, 98, 0, 16, 0, 48, 16, 16, 16, 56, 0
defdata 2, 0, 6, 2, 2, 66, 66, 60, 96, 32, 36, 40, 48, 40, 38, 0
defdata 48, 16, 16, 16, 16, 16, 56, 0, 0, 0, 118, 73, 73, 73, 73, 0
defdata 0, 0, 92, 98, 66, 66, 66, 0, 0, 0, 60, 66, 66, 66, 60, 0
defdata 0, 0, 108, 50, 50, 44, 32, 112, 0, 0, 54, 76, 76, 52, 4, 14
defdata 0, 0, 108, 50, 34, 32, 112, 0, 0, 0, 62, 64, 60, 2, 124, 0
defdata 16, 16, 124, 16, 16, 18, 12, 0, 0, 0, 66, 66, 66, 70, 58, 0
defdata 0, 0, 65, 65, 34, 20, 8, 0, 0, 0, 65, 73, 73, 73, 54, 0
defdata 0, 0, 68, 40, 16, 40, 68, 0, 0, 0, 66, 66, 66, 62, 2, 124
defdata 0, 0, 124, 8, 16, 32, 124, 0, 12, 16, 16, 96, 16, 16, 12, 0
defdata 16, 16, 16, 0, 16, 16, 16, 0, 48, 8, 8, 6, 8, 8, 48, 0
defdata 50, 76, 0, 0, 0, 0, 0, 0, 0, 8, 20, 34, 65, 65, 127, 0
defdata 60, 66, 64, 66, 60, 12, 2, 60, 0, 68, 0, 68, 68, 68, 62, 0
defdata 12, 0, 60, 66, 126, 64, 60, 0, 60, 66, 56, 4, 60, 68, 62, 0
defdata 66, 0, 56, 4, 60, 68, 62, 0, 48, 0, 56, 4, 60, 68, 62, 0
defdata 16, 0, 56, 4, 60, 68, 62, 0, 0, 0, 60, 64, 64, 60, 6, 28
defdata 60, 66, 60, 66, 126, 64, 60, 0, 66, 0, 60, 66, 126, 64, 60, 0
defdata 48, 0, 60, 66, 126, 64, 60, 0, 36, 0, 24, 8, 8, 8, 28, 0
defdata 124, 130, 48, 16, 16, 16, 56, 0, 48, 0, 24, 8, 8, 8, 28, 0
defdata 66, 24, 36, 66, 126, 66, 66, 0, 24, 24, 0, 60, 66, 126, 66, 0
defdata 12, 0, 124, 32, 56, 32, 124, 0, 0, 0, 51, 12, 63, 68, 59, 0
defdata 31, 36, 68, 127, 68, 68, 71, 0, 24, 36, 0, 60, 66, 66, 60, 0
defdata 0, 66, 0, 60, 66, 66, 60, 0, 32, 16, 0, 60, 66, 66, 60, 0
defdata 24, 36, 0, 66, 66, 66, 60, 0, 32, 16, 0, 66, 66, 66, 60, 0
defdata 0, 66, 0, 66, 66, 62, 2, 60, 66, 24, 36, 66, 66, 36, 24, 0
defdata 66, 0, 66, 66, 66, 66, 60, 0, 8, 8, 62, 64, 64, 62, 8, 8
defdata 24, 36, 32, 112, 32, 66, 124, 0, 68, 40, 124, 16, 124, 16, 16, 0
defdata 248, 76, 120, 68, 79, 68, 69, 230, 28, 18, 16, 124, 16, 16, 144, 96
defdata 12, 0, 56, 4, 60, 68, 62, 0, 12, 0, 24, 8, 8, 8, 28, 0
defdata 4, 8, 0, 60, 66, 66, 60, 0, 0, 4, 8, 66, 66, 66, 60, 0
defdata 50, 76, 0, 124, 66, 66, 66, 0, 52, 76, 0, 98, 82, 74, 70, 0
defdata 60, 68, 68, 62, 0, 126, 0, 0, 56, 68, 68, 56, 0, 124, 0, 0
defdata 16, 0, 16, 32, 64, 66, 60, 0, 0, 0, 0, 126, 64, 64, 0, 0
defdata 0, 0, 0, 126, 2, 2, 0, 0, 66, 196, 72, 246, 41, 67, 140, 31
defdata 66, 196, 74, 246, 42, 95, 130, 2, 0, 16, 0, 16, 16, 16, 16, 0
defdata 0, 18, 36, 72, 36, 18, 0, 0, 0, 72, 36, 18, 36, 72, 0, 0
defdata 34, 136, 34, 136, 34, 136, 34, 136, 85, 170, 85, 170, 85, 170, 85, 170
defdata 219, 119, 219, 238, 219, 119, 219, 238, 16, 16, 16, 16, 16, 16, 16, 16
defdata 16, 16, 16, 16, 240, 16, 16, 16, 16, 16, 240, 16, 240, 16, 16, 16
defdata 20, 20, 20, 20, 244, 20, 20, 20, 0, 0, 0, 0, 252, 20, 20, 20
defdata 0, 0, 240, 16, 240, 16, 16, 16, 20, 20, 244, 4, 244, 20, 20, 20
defdata 20, 20, 20, 20, 20, 20, 20, 20, 0, 0, 252, 4, 244, 20, 20, 20
defdata 20, 20, 244, 4, 252, 0, 0, 0, 20, 20, 20, 20, 252, 0, 0, 0
defdata 16, 16, 240, 16, 240, 0, 0, 0, 0, 0, 0, 0, 240, 16, 16, 16
defdata 16, 16, 16, 16, 31, 0, 0, 0, 16, 16, 16, 16, 255, 0, 0, 0
defdata 0, 0, 0, 0, 255, 16, 16, 16, 16, 16, 16, 16, 31, 16, 16, 16
defdata 0, 0, 0, 0, 255, 0, 0, 0, 16, 16, 16, 16, 255, 16, 16, 16
defdata 16, 16, 31, 16, 31, 16, 16, 16, 20, 20, 20, 20, 23, 20, 20, 20
defdata 20, 20, 23, 16, 31, 0, 0, 0, 0, 0, 31, 16, 23, 20, 20, 20
defdata 20, 20, 247, 0, 255, 0, 0, 0, 0, 0, 255, 0, 247, 20, 20, 20
defdata 20, 20, 23, 16, 23, 20, 20, 20, 0, 0, 255, 0, 255, 0, 0, 0
defdata 20, 20, 247, 0, 247, 20, 20, 20, 16, 16, 255, 0, 255, 0, 0, 0
defdata 20, 20, 20, 20, 255, 0, 0, 0, 0, 0, 255, 0, 255, 16, 16, 16
defdata 0, 0, 0, 0, 255, 20, 20, 20, 20, 20, 20, 20, 31, 0, 0, 0
defdata 16, 16, 31, 16, 31, 0, 0, 0, 0, 0, 31, 16, 31, 16, 16, 16
defdata 0, 0, 0, 0, 31, 20, 20, 20, 20, 20, 20, 20, 255, 20, 20, 20
defdata 16, 16, 255, 16, 255, 16, 16, 16, 16, 16, 16, 16, 240, 0, 0, 0
defdata 0, 0, 0, 0, 31, 16, 16, 16, 255, 255, 255, 255, 255, 255, 255, 255
defdata 0, 0, 0, 0, 255, 255, 255, 255, 240, 240, 240, 240, 240, 240, 240, 240
defdata 15, 15, 15, 15, 15, 15, 15, 15, 255, 255, 255, 255, 0, 0, 0, 0
defdata 0, 0, 49, 74, 68, 74, 49, 0, 0, 60, 66, 124, 66, 124, 64, 64
defdata 0, 126, 66, 64, 64, 64, 64, 0, 0, 63, 84, 20, 20, 20, 20, 0
defdata 126, 66, 32, 24, 32, 66, 126, 0, 0, 0, 62, 72, 72, 72, 48, 0
defdata 0, 68, 68, 68, 122, 64, 64, 128, 0, 51, 76, 8, 8, 8, 8, 0
defdata 124, 16, 56, 68, 68, 56, 16, 124, 24, 36, 66, 126, 66, 36, 24, 0
defdata 24, 36, 66, 66, 36, 36, 102, 0, 28, 32, 24, 60, 66, 66, 60, 0
defdata 0, 98, 149, 137, 149, 98, 0, 0, 2, 4, 60, 74, 82, 60, 64, 128
defdata 12, 16, 32, 60, 32, 16, 12, 0, 60, 66, 66, 66, 66, 66, 66, 0
defdata 0, 126, 0, 126, 0, 126, 0, 0, 16, 16, 124, 16, 16, 0, 124, 0
defdata 16, 8, 4, 8, 16, 0, 126, 0, 8, 16, 32, 16, 8, 0, 126, 0
defdata 12, 18, 18, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 144, 144, 96
defdata 24, 24, 0, 126, 0, 24, 24, 0, 0, 50, 76, 0, 50, 76, 0, 0
defdata 48, 72, 72, 48, 0, 0, 0, 0, 0, 0, 0, 24, 24, 0, 0, 0
defdata 0, 0, 0, 0, 24, 0, 0, 0, 15, 8, 8, 8, 8, 200, 40, 24
defdata 120, 68, 68, 68, 68, 0, 0, 0, 48, 72, 16, 32, 120, 0, 0, 0
DefData 0, 0, 0, 0, 0, 0, 0, 0

#font8x14
DefData 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 126, 129, 165, 129, 129, 189, 153, 129, 126, 0, 0, 0, 0, 0, 126, 255
defdata 219, 255, 255, 195, 231, 255, 126, 0, 0, 0, 0, 0, 0, 108, 254, 254
defdata 254, 254, 124, 56, 16, 0, 0, 0, 0, 0, 0, 16, 56, 124, 254, 124
defdata 56, 16, 0, 0, 0, 0, 0, 0, 24, 60, 60, 231, 231, 231, 24, 24
defdata 60, 0, 0, 0, 0, 0, 24, 60, 126, 255, 255, 126, 24, 24, 60, 0
defdata 0, 0, 0, 0, 0, 0, 0, 24, 60, 60, 24, 0, 0, 0, 0, 0
defdata 255, 255, 255, 255, 255, 231, 195, 195, 231, 255, 255, 255, 255, 255, 0, 0
defdata 0, 0, 60, 102, 66, 66, 102, 60, 0, 0, 0, 0, 255, 255, 255, 255
defdata 195, 153, 189, 189, 153, 195, 255, 255, 255, 255, 0, 0, 30, 14, 26, 50
defdata 120, 204, 204, 204, 120, 0, 0, 0, 0, 0, 60, 102, 102, 102, 60, 24
defdata 126, 24, 24, 0, 0, 0, 0, 0, 63, 51, 63, 48, 48, 48, 112, 240
defdata 224, 0, 0, 0, 0, 0, 127, 99, 127, 99, 99, 99, 103, 231, 230, 192
defdata 0, 0, 0, 0, 24, 24, 219, 60, 231, 60, 219, 24, 24, 0, 0, 0
defdata 0, 0, 128, 192, 224, 248, 254, 248, 224, 192, 128, 0, 0, 0, 0, 0
defdata 2, 6, 14, 62, 254, 62, 14, 6, 2, 0, 0, 0, 0, 0, 24, 60
defdata 126, 24, 24, 24, 126, 60, 24, 0, 0, 0, 0, 0, 102, 102, 102, 102
defdata 102, 102, 0, 102, 102, 0, 0, 0, 0, 0, 127, 219, 219, 219, 123, 27
defdata 27, 27, 27, 0, 0, 0, 0, 124, 198, 96, 56, 108, 198, 198, 108, 56
defdata 12, 198, 124, 0, 0, 0, 0, 0, 0, 0, 0, 0, 254, 254, 254, 0
defdata 0, 0, 0, 0, 24, 60, 126, 24, 24, 24, 126, 60, 24, 126, 0, 0
defdata 0, 0, 24, 60, 126, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0, 0
defdata 24, 24, 24, 24, 24, 24, 126, 60, 24, 0, 0, 0, 0, 0, 0, 0
defdata 24, 12, 254, 12, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 48, 96
defdata 254, 96, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 192, 192, 192
defdata 254, 0, 0, 0, 0, 0, 0, 0, 0, 0, 40, 108, 254, 108, 40, 0
defdata 0, 0, 0, 0, 0, 0, 0, 16, 56, 56, 124, 124, 254, 254, 0, 0
defdata 0, 0, 0, 0, 0, 254, 254, 124, 124, 56, 56, 16, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 24, 60, 60, 60, 24, 24, 0, 24, 24, 0, 0, 0, 0, 102, 102, 102
defdata 36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 108, 108, 254, 108
defdata 108, 108, 254, 108, 108, 0, 0, 0, 24, 24, 124, 198, 194, 192, 124, 6
defdata 134, 198, 124, 24, 24, 0, 0, 0, 0, 0, 194, 198, 12, 24, 48, 102
defdata 198, 0, 0, 0, 0, 0, 56, 108, 108, 56, 118, 220, 204, 204, 118, 0
defdata 0, 0, 0, 48, 48, 48, 96, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 12, 24, 48, 48, 48, 48, 48, 24, 12, 0, 0, 0, 0, 0
defdata 48, 24, 12, 12, 12, 12, 12, 24, 48, 0, 0, 0, 0, 0, 0, 0
defdata 102, 60, 255, 60, 102, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24
defdata 126, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 24, 24, 24, 48, 0, 0, 0, 0, 0, 0, 0, 0, 254, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 24, 24, 0
defdata 0, 0, 0, 0, 2, 6, 12, 24, 48, 96, 192, 128, 0, 0, 0, 0
defdata 0, 0, 124, 198, 206, 222, 246, 230, 198, 198, 124, 0, 0, 0, 0, 0
defdata 24, 56, 120, 24, 24, 24, 24, 24, 126, 0, 0, 0, 0, 0, 124, 198
defdata 6, 12, 24, 48, 96, 198, 254, 0, 0, 0, 0, 0, 124, 198, 6, 6
defdata 60, 6, 6, 198, 124, 0, 0, 0, 0, 0, 12, 28, 60, 108, 204, 254
defdata 12, 12, 30, 0, 0, 0, 0, 0, 254, 192, 192, 192, 252, 6, 6, 198
defdata 124, 0, 0, 0, 0, 0, 56, 96, 192, 192, 252, 198, 198, 198, 124, 0
defdata 0, 0, 0, 0, 254, 198, 6, 12, 24, 48, 48, 48, 48, 0, 0, 0
defdata 0, 0, 124, 198, 198, 198, 124, 198, 198, 198, 124, 0, 0, 0, 0, 0
defdata 124, 198, 198, 198, 126, 6, 6, 12, 120, 0, 0, 0, 0, 0, 0, 24
defdata 24, 0, 0, 0, 24, 24, 0, 0, 0, 0, 0, 0, 0, 24, 24, 0
defdata 0, 0, 24, 24, 48, 0, 0, 0, 0, 0, 6, 12, 24, 48, 96, 48
defdata 24, 12, 6, 0, 0, 0, 0, 0, 0, 0, 0, 126, 0, 0, 126, 0
defdata 0, 0, 0, 0, 0, 0, 96, 48, 24, 12, 6, 12, 24, 48, 96, 0
defdata 0, 0, 0, 0, 124, 198, 198, 12, 24, 24, 0, 24, 24, 0, 0, 0
defdata 0, 0, 124, 198, 198, 222, 222, 222, 220, 192, 124, 0, 0, 0, 0, 0
defdata 16, 56, 108, 198, 198, 254, 198, 198, 198, 0, 0, 0, 0, 0, 252, 102
defdata 102, 102, 124, 102, 102, 102, 252, 0, 0, 0, 0, 0, 60, 102, 194, 192
defdata 192, 192, 194, 102, 60, 0, 0, 0, 0, 0, 248, 108, 102, 102, 102, 102
defdata 102, 108, 248, 0, 0, 0, 0, 0, 254, 102, 98, 104, 120, 104, 98, 102
defdata 254, 0, 0, 0, 0, 0, 254, 102, 98, 104, 120, 104, 96, 96, 240, 0
defdata 0, 0, 0, 0, 60, 102, 194, 192, 192, 222, 198, 102, 58, 0, 0, 0
defdata 0, 0, 198, 198, 198, 198, 254, 198, 198, 198, 198, 0, 0, 0, 0, 0
defdata 60, 24, 24, 24, 24, 24, 24, 24, 60, 0, 0, 0, 0, 0, 30, 12
defdata 12, 12, 12, 12, 204, 204, 120, 0, 0, 0, 0, 0, 230, 102, 108, 108
defdata 120, 108, 108, 102, 230, 0, 0, 0, 0, 0, 240, 96, 96, 96, 96, 96
defdata 98, 102, 254, 0, 0, 0, 0, 0, 198, 238, 254, 254, 214, 198, 198, 198
defdata 198, 0, 0, 0, 0, 0, 198, 230, 246, 254, 222, 206, 198, 198, 198, 0
defdata 0, 0, 0, 0, 56, 108, 198, 198, 198, 198, 198, 108, 56, 0, 0, 0
defdata 0, 0, 252, 102, 102, 102, 124, 96, 96, 96, 240, 0, 0, 0, 0, 0
defdata 124, 198, 198, 198, 198, 214, 222, 124, 12, 14, 0, 0, 0, 0, 252, 102
defdata 102, 102, 124, 108, 102, 102, 230, 0, 0, 0, 0, 0, 124, 198, 198, 96
defdata 56, 12, 198, 198, 124, 0, 0, 0, 0, 0, 126, 126, 90, 24, 24, 24
defdata 24, 24, 60, 0, 0, 0, 0, 0, 198, 198, 198, 198, 198, 198, 198, 198
defdata 124, 0, 0, 0, 0, 0, 198, 198, 198, 198, 198, 198, 108, 56, 16, 0
defdata 0, 0, 0, 0, 198, 198, 198, 198, 214, 214, 254, 124, 108, 0, 0, 0
defdata 0, 0, 198, 198, 108, 56, 56, 56, 108, 198, 198, 0, 0, 0, 0, 0
defdata 102, 102, 102, 102, 60, 24, 24, 24, 60, 0, 0, 0, 0, 0, 254, 198
defdata 140, 24, 48, 96, 194, 198, 254, 0, 0, 0, 0, 0, 60, 48, 48, 48
defdata 48, 48, 48, 48, 60, 0, 0, 0, 0, 0, 128, 192, 224, 112, 56, 28
defdata 14, 6, 2, 0, 0, 0, 0, 0, 60, 12, 12, 12, 12, 12, 12, 12
defdata 60, 0, 0, 0, 16, 56, 108, 198, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255, 0
defdata 48, 48, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 120, 12, 124, 204, 204, 118, 0, 0, 0, 0, 0, 224, 96
defdata 96, 120, 108, 102, 102, 102, 124, 0, 0, 0, 0, 0, 0, 0, 0, 124
defdata 198, 192, 192, 198, 124, 0, 0, 0, 0, 0, 28, 12, 12, 60, 108, 204
defdata 204, 204, 118, 0, 0, 0, 0, 0, 0, 0, 0, 124, 198, 254, 192, 198
defdata 124, 0, 0, 0, 0, 0, 56, 108, 100, 96, 240, 96, 96, 96, 240, 0
defdata 0, 0, 0, 0, 0, 0, 0, 118, 204, 204, 204, 124, 12, 204, 120, 0
defdata 0, 0, 224, 96, 96, 108, 118, 102, 102, 102, 230, 0, 0, 0, 0, 0
defdata 24, 24, 0, 56, 24, 24, 24, 24, 60, 0, 0, 0, 0, 0, 6, 6
defdata 0, 14, 6, 6, 6, 6, 102, 102, 60, 0, 0, 0, 224, 96, 96, 102
defdata 108, 120, 108, 102, 230, 0, 0, 0, 0, 0, 56, 24, 24, 24, 24, 24
defdata 24, 24, 60, 0, 0, 0, 0, 0, 0, 0, 0, 236, 254, 214, 214, 214
defdata 198, 0, 0, 0, 0, 0, 0, 0, 0, 220, 102, 102, 102, 102, 102, 0
defdata 0, 0, 0, 0, 0, 0, 0, 124, 198, 198, 198, 198, 124, 0, 0, 0
defdata 0, 0, 0, 0, 0, 220, 102, 102, 102, 124, 96, 96, 240, 0, 0, 0
defdata 0, 0, 0, 118, 204, 204, 204, 124, 12, 12, 30, 0, 0, 0, 0, 0
defdata 0, 220, 118, 102, 96, 96, 240, 0, 0, 0, 0, 0, 0, 0, 0, 124
defdata 198, 112, 28, 198, 124, 0, 0, 0, 0, 0, 16, 48, 48, 252, 48, 48
defdata 48, 54, 28, 0, 0, 0, 0, 0, 0, 0, 0, 204, 204, 204, 204, 204
defdata 118, 0, 0, 0, 0, 0, 0, 0, 0, 102, 102, 102, 102, 60, 24, 0
defdata 0, 0, 0, 0, 0, 0, 0, 198, 198, 214, 214, 254, 108, 0, 0, 0
defdata 0, 0, 0, 0, 0, 198, 108, 56, 56, 108, 198, 0, 0, 0, 0, 0
defdata 0, 0, 0, 198, 198, 198, 198, 126, 6, 12, 248, 0, 0, 0, 0, 0
defdata 0, 254, 204, 24, 48, 102, 254, 0, 0, 0, 0, 0, 14, 24, 24, 24
defdata 112, 24, 24, 24, 14, 0, 0, 0, 0, 0, 24, 24, 24, 24, 0, 24
defdata 24, 24, 24, 0, 0, 0, 0, 0, 112, 24, 24, 24, 14, 24, 24, 24
defdata 112, 0, 0, 0, 0, 0, 118, 220, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 16, 56, 108, 198, 198, 254, 0, 0, 0, 0
defdata 0, 0, 60, 102, 194, 192, 192, 194, 102, 60, 12, 6, 124, 0, 0, 0
defdata 204, 204, 0, 204, 204, 204, 204, 204, 118, 0, 0, 0, 0, 12, 24, 48
defdata 0, 124, 198, 254, 192, 198, 124, 0, 0, 0, 0, 16, 56, 108, 0, 120
defdata 12, 124, 204, 204, 118, 0, 0, 0, 0, 0, 204, 204, 0, 120, 12, 124
defdata 204, 204, 118, 0, 0, 0, 0, 96, 48, 24, 0, 120, 12, 124, 204, 204
defdata 118, 0, 0, 0, 0, 56, 108, 56, 0, 120, 12, 124, 204, 204, 118, 0
defdata 0, 0, 0, 0, 0, 0, 60, 102, 96, 102, 60, 12, 6, 60, 0, 0
defdata 0, 16, 56, 108, 0, 124, 198, 254, 192, 198, 124, 0, 0, 0, 0, 0
defdata 204, 204, 0, 124, 198, 254, 192, 198, 124, 0, 0, 0, 0, 96, 48, 24
defdata 0, 124, 198, 254, 192, 198, 124, 0, 0, 0, 0, 0, 102, 102, 0, 56
defdata 24, 24, 24, 24, 60, 0, 0, 0, 0, 24, 60, 102, 0, 56, 24, 24
defdata 24, 24, 60, 0, 0, 0, 0, 96, 48, 24, 0, 56, 24, 24, 24, 24
defdata 60, 0, 0, 0, 0, 198, 198, 16, 56, 108, 198, 198, 254, 198, 198, 0
defdata 0, 0, 56, 108, 56, 0, 56, 108, 198, 198, 254, 198, 198, 0, 0, 0
defdata 24, 48, 96, 0, 254, 102, 96, 124, 96, 102, 254, 0, 0, 0, 0, 0
defdata 0, 0, 204, 118, 54, 126, 216, 216, 110, 0, 0, 0, 0, 0, 62, 108
defdata 204, 204, 254, 204, 204, 204, 206, 0, 0, 0, 0, 16, 56, 108, 0, 124
defdata 198, 198, 198, 198, 124, 0, 0, 0, 0, 0, 198, 198, 0, 124, 198, 198
defdata 198, 198, 124, 0, 0, 0, 0, 96, 48, 24, 0, 124, 198, 198, 198, 198
defdata 124, 0, 0, 0, 0, 48, 120, 204, 0, 204, 204, 204, 204, 204, 118, 0
defdata 0, 0, 0, 96, 48, 24, 0, 204, 204, 204, 204, 204, 118, 0, 0, 0
defdata 0, 0, 198, 198, 0, 198, 198, 198, 198, 126, 6, 12, 120, 0, 0, 198
defdata 198, 56, 108, 198, 198, 198, 198, 108, 56, 0, 0, 0, 0, 198, 198, 0
defdata 198, 198, 198, 198, 198, 198, 124, 0, 0, 0, 0, 24, 24, 60, 102, 96
defdata 96, 102, 60, 24, 24, 0, 0, 0, 0, 56, 108, 100, 96, 240, 96, 96
defdata 96, 230, 252, 0, 0, 0, 0, 0, 102, 102, 60, 24, 126, 24, 126, 24
defdata 24, 0, 0, 0, 0, 248, 204, 204, 248, 196, 204, 222, 204, 204, 198, 0
defdata 0, 0, 0, 14, 27, 24, 24, 24, 126, 24, 24, 24, 24, 216, 112, 0
defdata 0, 24, 48, 96, 0, 120, 12, 124, 204, 204, 118, 0, 0, 0, 0, 12
defdata 24, 48, 0, 56, 24, 24, 24, 24, 60, 0, 0, 0, 0, 24, 48, 96
defdata 0, 124, 198, 198, 198, 198, 124, 0, 0, 0, 0, 24, 48, 96, 0, 204
defdata 204, 204, 204, 204, 118, 0, 0, 0, 0, 0, 118, 220, 0, 220, 102, 102
defdata 102, 102, 102, 0, 0, 0, 118, 220, 0, 198, 230, 246, 254, 222, 206, 198
defdata 198, 0, 0, 0, 0, 60, 108, 108, 62, 0, 126, 0, 0, 0, 0, 0
defdata 0, 0, 0, 56, 108, 108, 56, 0, 124, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 48, 48, 0, 48, 48, 96, 198, 198, 124, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 254, 192, 192, 192, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 254, 6, 6, 6, 0, 0, 0, 0, 0, 192, 192, 198, 204, 216
defdata 48, 96, 220, 134, 12, 24, 62, 0, 0, 192, 192, 198, 204, 216, 48, 102
defdata 206, 158, 62, 6, 6, 0, 0, 0, 24, 24, 0, 24, 24, 60, 60, 60
defdata 24, 0, 0, 0, 0, 0, 0, 0, 54, 108, 216, 108, 54, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 216, 108, 54, 108, 216, 0, 0, 0, 0, 0
defdata 17, 68, 17, 68, 17, 68, 17, 68, 17, 68, 17, 68, 17, 68, 85, 170
defdata 85, 170, 85, 170, 85, 170, 85, 170, 85, 170, 85, 170, 221, 119, 221, 119
defdata 221, 119, 221, 119, 221, 119, 221, 119, 221, 119, 24, 24, 24, 24, 24, 24
defdata 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 248
defdata 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 248, 24, 248, 24, 24
defdata 24, 24, 24, 24, 54, 54, 54, 54, 54, 54, 54, 246, 54, 54, 54, 54
defdata 54, 54, 0, 0, 0, 0, 0, 0, 0, 254, 54, 54, 54, 54, 54, 54
defdata 0, 0, 0, 0, 0, 248, 24, 248, 24, 24, 24, 24, 24, 24, 54, 54
defdata 54, 54, 54, 246, 6, 246, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54
defdata 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 0, 0, 0, 0, 0, 254
defdata 6, 246, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 246, 6, 254
defdata 0, 0, 0, 0, 0, 0, 54, 54, 54, 54, 54, 54, 54, 254, 0, 0
defdata 0, 0, 0, 0, 24, 24, 24, 24, 24, 248, 24, 248, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 0, 248, 24, 24, 24, 24, 24, 24
defdata 24, 24, 24, 24, 24, 24, 24, 31, 0, 0, 0, 0, 0, 0, 24, 24
defdata 24, 24, 24, 24, 24, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 255, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24
defdata 24, 31, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 255
defdata 0, 0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 24, 24, 255, 24, 24
defdata 24, 24, 24, 24, 24, 24, 24, 24, 24, 31, 24, 31, 24, 24, 24, 24
defdata 24, 24, 54, 54, 54, 54, 54, 54, 54, 55, 54, 54, 54, 54, 54, 54
defdata 54, 54, 54, 54, 54, 55, 48, 63, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 63, 48, 55, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54
defdata 54, 247, 0, 255, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 255
defdata 0, 247, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 55, 48, 55
defdata 54, 54, 54, 54, 54, 54, 0, 0, 0, 0, 0, 255, 0, 255, 0, 0
defdata 0, 0, 0, 0, 54, 54, 54, 54, 54, 247, 0, 247, 54, 54, 54, 54
defdata 54, 54, 24, 24, 24, 24, 24, 255, 0, 255, 0, 0, 0, 0, 0, 0
defdata 54, 54, 54, 54, 54, 54, 54, 255, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 255, 0, 255, 24, 24, 24, 24, 24, 24, 0, 0, 0, 0
defdata 0, 0, 0, 255, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54, 54
defdata 54, 63, 0, 0, 0, 0, 0, 0, 24, 24, 24, 24, 24, 31, 24, 31
defdata 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 31, 24, 31, 24, 24
defdata 24, 24, 24, 24, 0, 0, 0, 0, 0, 0, 0, 63, 54, 54, 54, 54
defdata 54, 54, 54, 54, 54, 54, 54, 54, 54, 255, 54, 54, 54, 54, 54, 54
defdata 24, 24, 24, 24, 24, 255, 24, 255, 24, 24, 24, 24, 24, 24, 24, 24
defdata 24, 24, 24, 24, 24, 248, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
DefData 0, 0, 0, 31, 24, 24, 24, 24, 24, 24, 255, 255, 255, 255, 255, 255
defdata 255, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0, 255
defdata 255, 255, 255, 255, 255, 255, 240, 240, 240, 240, 240, 240, 240, 240, 240, 240
defdata 240, 240, 240, 240, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15
defdata 15, 15, 255, 255, 255, 255, 255, 255, 255, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 0, 118, 220, 216, 216, 220, 118, 0, 0, 0, 0, 0
defdata 0, 0, 124, 198, 252, 198, 198, 252, 192, 192, 64, 0, 0, 0, 254, 198
defdata 198, 192, 192, 192, 192, 192, 192, 0, 0, 0, 0, 0, 0, 0, 254, 108
defdata 108, 108, 108, 108, 108, 0, 0, 0, 0, 0, 254, 198, 96, 48, 24, 48
defdata 96, 198, 254, 0, 0, 0, 0, 0, 0, 0, 0, 126, 216, 216, 216, 216
defdata 112, 0, 0, 0, 0, 0, 0, 0, 102, 102, 102, 102, 124, 96, 96, 192
defdata 0, 0, 0, 0, 0, 0, 118, 220, 24, 24, 24, 24, 24, 0, 0, 0
defdata 0, 0, 126, 24, 60, 102, 102, 102, 60, 24, 126, 0, 0, 0, 0, 0
defdata 56, 108, 198, 198, 254, 198, 198, 108, 56, 0, 0, 0, 0, 0, 56, 108
defdata 198, 198, 198, 108, 108, 108, 238, 0, 0, 0, 0, 0, 30, 48, 24, 12
defdata 62, 102, 102, 102, 60, 0, 0, 0, 0, 0, 0, 0, 0, 126, 219, 219
defdata 126, 0, 0, 0, 0, 0, 0, 0, 3, 6, 126, 219, 219, 243, 126, 96
defdata 192, 0, 0, 0, 0, 0, 28, 48, 96, 96, 124, 96, 96, 48, 28, 0
defdata 0, 0, 0, 0, 0, 124, 198, 198, 198, 198, 198, 198, 198, 0, 0, 0
defdata 0, 0, 0, 254, 0, 0, 254, 0, 0, 254, 0, 0, 0, 0, 0, 0
defdata 0, 24, 24, 126, 24, 24, 0, 0, 255, 0, 0, 0, 0, 0, 48, 24
defdata 12, 6, 12, 24, 48, 0, 126, 0, 0, 0, 0, 0, 12, 24, 48, 96
defdata 48, 24, 12, 0, 126, 0, 0, 0, 0, 0, 14, 27, 27, 24, 24, 24
defdata 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 216, 216
defdata 112, 0, 0, 0, 0, 0, 0, 24, 24, 0, 126, 0, 24, 24, 0, 0
defdata 0, 0, 0, 0, 0, 0, 118, 220, 0, 118, 220, 0, 0, 0, 0, 0
defdata 0, 56, 108, 108, 56, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 0, 24, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
defdata 0, 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 15, 12, 12, 12, 12
defdata 12, 236, 108, 60, 28, 0, 0, 0, 0, 216, 108, 108, 108, 108, 108, 0
defdata 0, 0, 0, 0, 0, 0, 0, 112, 216, 48, 96, 200, 248, 0, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 124, 124, 124, 124, 124, 124, 0, 0
defdata 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
