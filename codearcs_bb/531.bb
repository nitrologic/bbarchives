; ID: 531
; Author: riotvan
; Date: 2002-12-18 06:26:32
; Title: BSOD
; Description: This is just a test piece of code

Function BSOD()
	Cls
	Flip
	bsodimage=LoadImage(BSOD$)
	ResizeImage bsodimage,GraphicsWidth(),GraphicsHeight()
	DrawImage bsodimage,0,0
	Flip
	While Not KeyHit(1)
	Wend
	FreeImage bsodimage
	End
End Function
