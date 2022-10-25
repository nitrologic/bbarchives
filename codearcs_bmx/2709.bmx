; ID: 2709
; Author: BlitzSupport
; Date: 2010-05-06 16:35:23
; Title: Xbox 360 avatar loader
; Description: It loads your Xbox 360 avatar image... or anyone else's!

' Amazing Xbox360 avatar thing...

av$ = "AVATAR_NAME_HERE" ' INSERT YOUR AVATAR NAME HERE! Or anyone else's...

Graphics 640, 480

SetClsColor 80, 32, 120
SetBlend ALPHABLEND
AutoMidHandle True

image:TImage = LoadImage (LoadBank ("http::avatar.xboxlive.com/avatar/" + av$ + "/avatar-body.png"))

xs = 4
ys = 4

Repeat

	Cls
	
	x = x + xs; y = y + ys; ang# = ang + 2
	If x < 0 Or x > GraphicsWidth () Then xs = -xs
	If y < 0 Or y > GraphicsHeight () Then ys = -ys

	SetRotation ang
	DrawImage image, x, y
	
	Flip
	
Until KeyHit (KEY_ESCAPE)

End
