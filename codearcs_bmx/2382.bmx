; ID: 2382
; Author: Underwood
; Date: 2008-12-25 07:02:42
; Title: 2D Camera MouseLook
; Description: Use the mouse to 'look around' a player.

SuperStrict

Graphics(1024,768,1)
SeedRnd MilliSecs()
HideMouse()


Global CAMX#,CAMY#

Global cursorx#,cursory#
Global cursorox#,cursoroy#
Global mx#,my#
Global mox#,moy#

Global sensitivity# = 2.0
Global cursorradiusx# = 1024
Global cursorradiusy# = 768

Global zoom# = 1.0

Global playerx# = 512,playery# = 384
Global playerox#,playeroy#



SetCamSnap(playerx,playery)


While Not KeyHit(key_escape)
Cls

	UpdatePlayer()

		UpdateCursor(playerx,playery)
                      If MouseDown(1) ShakeCam(5)

		SetDrawCam()

	SetColor(255,0,0)
		DrawOval(playerx-4,playery-4,9,9)
	SetColor(0,0,255)
		DrawOval(CAMX-4,CAMY-4,9,9)
	SetColor(0,255,0)
		DrawOval(cursorx-4,cursory-4,9,9)	
	SetColor(255,255,255)
		DrawRect(512,384,15,67)

		SetDrawHUD()

	DrawText("CamX = " + Int CAMX,10,10)
	DrawText("CamY = " + Int CAMY,10,25)

Flip
Wend
End





'==================================================================================================







Function UpdateCursor(px:Float,py:Float)

	mx = MouseX()
	my = MouseY()

	If mx > 812										' create a infinite bounding box for the mouse
		MoveMouse(512,my)
		mox = (mox - 300)
	EndIf

	If mx < 212
		MoveMouse(512,my)
		mox = (mox + 300)
	EndIf

	If my > 684
		MoveMouse(mx,384)
		moy = (moy - 300)
	EndIf

	If my < 84
		MoveMouse(mx,384)
		moy = (moy + 300)
	EndIf

		cursorx = (cursorx + (mousexspeed() * sensitivity))			' create a cursor that acts like a mouse
		cursory = (cursory + (mouseyspeed() * sensitivity))

	While Not PointInOval(cursorx,cursory,px,py,cursorradiusx*zoom,cursorradiusy*zoom)
		cursorx = (cursorx - Cos(GetAngle(px,py,cursorx,cursory)))
		cursory = (cursory - Sin(GetAngle(px,py,cursorx,cursory)))
	Wend

	CAMX = ((px + cursorx) / 2)							' when using UpdateCursor, move cam...
	CAMY = ((py + cursory) / 2)

End Function



Function UpdatePlayer()

	If KeyDown(key_right)
		playerx = (playerx + 1)
	EndIf

	If KeyDown(key_left)
		playerx = (playerx - 1)
	EndIf

	cursorx = (cursorx + PlayerXVel())						' have to add differences for cursor as well...  it's attached.
	cursory = (cursory + PlayerYVel())

End Function



Function MouseXSpeed:Int()

	Local result:Int = (MouseX() - mox)
	mox = MouseX()

	Return(result)

End Function



Function MouseYSpeed:Int()

	Local result:Int = (MouseY() - moy)
	moy = MouseY()

	Return(result)

End Function



Function PlayerXVel:Float()

	Local result:Int = (playerx - playerox)
	playerox = playerx

	Return(result)

End Function



Function PlayerYVel:Float()

	Local result:Int = (playery - playeroy)
	playeroy = playery

	Return(result)

End Function









'==================================================================================================








Function SetDrawHUD()

	SetOrigin(0,0)													' for non-camera related drawing

EndFunction



Function SetDrawCam()

	SetOrigin(((GraphicsWidth() / 2) - CAMX),((GraphicsHeight() / 2) - CAMY))			' draw the camera

End Function



Function SetCamSnap(px:Float,py:Float)										' auto-set the camera

	CAMX = px
	CAMY = py

End Function



Function GetCamX:Float()

	Return(CAMX)

End Function



Function GetCamY:Float()

	Return(CAMY)

End Function



Function ShakeCam(radius:Float)

	CAMX = (CAMX + Rnd(-radius,radius))
	CAMY = (CAMY + Rnd(-radius,radius))

End Function





'==================================================================================================






' the distance formula

Function GetDistance:Float(x1:Float,y1:Float,x2:Float,y2:Float)

   Return(Sqr(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1))))

End Function



Function GetAngle:Float(x1:Float,y1:Float,x2:Float,y2:Float)

   Return((ATan2((y2 - y1),(x2 - x1)) + 360) Mod 360)

End Function



Function PointInOval:Int(px#,py#,ox#,oy#,width#,height#)

	Return((px - ox)^2 / width^2 + (py - oy)^2 / height^2 < 1)

End Function
