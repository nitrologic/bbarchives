; ID: 1772
; Author: Devils Child
; Date: 2006-08-02 11:23:59
; Title: Image shader
; Description: Turns images into negative, greyscale, blur, etc.

Graphics 1024, 768, 32, 2
SetBuffer BackBuffer()
SetFont LoadFont("Arial", 15)
HidePointer

BG = LoadImage("Forest.jpg")
Img = CreateImage(100, 100)

num = 1
While Not KeyHit(1)
	DrawBlock BG, 0, 0
	Color 0, 0, 0
	Rect 0, 0, GraphicsWidth(), 20
	Color 255, 255, 255
	Text 10, 2, "Press keys 1-10 and move the mouse to see the effects."
	mx = MouseX() - 50
	my = MouseY() - 50
	CopyRect mx, my, 100, 100, 0, 0, BackBuffer(), ImageBuffer(Img)
	For i = 1 To 4
		If KeyHit(i + 1) Then num = i
	Next
	Select num
		Case 1: ShadeImage(Img, "Negative")
		Case 2: ShadeImage(Img, "Greyscale")
		Case 3: ShadeImage(Img, "1Bit")
		Case 4: ShadeImage(Img, "Blur", 3)
	End Select
	DrawBlock Img, mx, my
	Rect mx, my, 100, 100, False
	Flip
Wend
End

Dim Pix(-1, -1, -1)
Function ShadeImage(img, effect$, param1 = 0)
w = ImageWidth(img) - 1
h = ImageHeight(img) - 1
ib = ImageBuffer(img)
LockBuffer ib
Select Lower(effect$)
	Case "negative" ;Negative
		For x = 0 To w
			For y = 0 To h
				rgb = ReadPixelFast(x, y, ib)
				WritePixelFast x, y, (255 - (rgb And $FF0000) / $10000) * $10000 + (255 - (rgb And $FF00) / $100) * $100 + (255 - rgb And $FF), ib
			Next
		Next
	Case "greyscale" ;Greyscale
		For x = 0 To w
			For y = 0 To h
				rgb = ReadPixelFast(x, y, ib)
				col = Float((rgb And $FF0000) / $10000 + (rgb And $FF00) / $100 + (rgb And $FF)) / 3.0
				WritePixelFast x, y, col * $10000 + col * $100 + col, ib
			Next
		Next
	Case "1bit" ;Black/white
		For x = 0 To w
			For y = 0 To h
				rgb = ReadPixelFast(x, y, ib)
				col = (Float((rgb And $FF0000) / $10000 + (rgb And $FF00) / $100 + (rgb And $FF)) / 3.0 > 127) * 255
				WritePixelFast x, y, col * $10000 + col * $100 + col, ib
			Next
		Next
	Case "blur" ;Blur - param1 is the blur radius.
		Dim Pix(w, h, 2)
		For x = 0 To w
			For y = 0 To h
				rgb = ReadPixelFast(x, y, ib)
				Pix(x, y, 0) = (rgb And $FF0000) / $10000
				Pix(x, y, 1) = (rgb And $FF00) / $100
				Pix(x, y, 2) = rgb And $FF
			Next
		Next
		For x = 0 To w
			For y = 0 To h
				r = 0
				g = 0
				b = 0
				For x2 = -param1 To param1
					For y2 = -param1 To param1
						rx = x + x2
						ry = y + y2
						If rx < 0 Then rx = 0
						If rx > w Then rx = w
						If ry < 0 Then ry = 0
						If ry > h Then ry = h
						r = r + Pix(rx, ry, 0)
						g = g + Pix(rx, ry, 1)
						b = b + Pix(rx, ry, 2)
					Next
				Next
				div = (param1 * 2 + 1) ^ 2
				r = r / div
				g = g / div
				b = b / div
				WritePixelFast x, y, r * $10000 + g * $100 + b, ib
			Next
		Next
	Default
		RuntimeError "Image shading effect not found."
End Select
UnlockBuffer ib
End Function
