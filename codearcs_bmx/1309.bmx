; ID: 1309
; Author: Eikon
; Date: 2005-03-06 19:20:19
; Title: Snow effect &amp; image wave
; Description: An image wave effect along with color changing snow

Strict
' // Framework & Modules //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
Framework BRL.GLMax2D 
Import BRL.Basic
Import BRL.System
Import BRL.Pngloader

' // Win32 API //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
Extern "win32"
	Function SetWindowTextA:Int(hWnd:Int, lpString:Byte Ptr)
	Function GetActiveWindow:Int()
End Extern

Const GFX_WIDTH = 800, GFX_HEIGHT = 600, BIT_DEPTH = 32, HERTZ = 60


Graphics GFX_WIDTH, GFX_HEIGHT, BIT_DEPTH, HERTZ
Local hWnd:Int = GetActiveWindow()
SetWindowTextA hWnd, "Devilette by Eikon 03.03.05"
SeedRnd MilliSecs()
SetBlend LIGHTBLEND
HideMouse

' // IncBin  //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
Incbin "red.png"   
Incbin "flake.png"

' // Images //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
SetMaskColor 255, 0, 255
Global imgRed:TImage = LoadImage("incbin::red.png")
Global imgFlake:TImage = LoadImage("incbin::flake.png"); MidHandleImage imgFlake

' // Locals //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
Local fpsCount:Int = 0, fpsCurrent:Int = 0, fpsTime:Int = MilliSecs() + 1 
Local imgW:Int = ImageWidth(imgRed), imgH:Int = ImageHeight(imgRed)
Local offset:Float, frame:Int, waves:Float = 1, i:Int
Local colors:Float[3], colorD:Int[3], colorI:Float[3]
Local waveD:Int = 0, waveI:Float = Rnd(.001, .005)

For i = 0 To 2
	colors[i] = 255; colorD[i] = 0; colorI[i] = Rnd(.1, .5)
Next

Global imgX:Int = 400 - imgW / 2, imgY:Int = 400 - imgH / 2
Global counter:Int, o:Obj
Global objList:TList = New TList
Local oldMouse:Int = MouseX() + MouseY()

' // Types //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
Type Obj
	Field x:Float, y:Float, g:Float, rot:Int, alpha:Float, s:Float, t:Byte
	Field w:Int, iner:Float, inerD:Float, d:Int, scl:Float

	Method Render()
		SetAlpha alpha
		SetRotation rot
		SetScale 1, 1

		If d = 0 ' left
			x:-iner
			iner:-.01
			If iner <= inerD d = 1; iner = 1
		Else
			x:+iner
			iner:-.01
			If iner <= inerD d = 0; iner = 1
		EndIf

		If t = 0 
			SetScale scl, scl
			DrawImage imgFlake, x, y 
		Else 
			SetHandle w / 2, w / 2
			DrawRect x, y, w, w
		EndIf

		rot:+s; y:+g

	End Method

	Function Create:Obj()
		Local o:Obj = New Obj
		o.x = Rand(0, 800); o.y = -5; o.rot = 0; o.alpha = Rnd(.2, 1); o.g = Rnd(.5, 4)
		o.s = Rnd(1, 3); o.t = Rand(0, 1); o.w = Rand(2, 5); o.d = Rand(0, 1); o.inerD = Rnd(-.5, -1)
		o.scl = Rnd(.7, 1.3)
		counter:+1
		Return o
	End Function

End Type

Local delFlake:Byte = False

Repeat
	Cls
	fpsCount = fpsCount + 1
	If MilliSecs() >= fpsTime fpsCurrent = fpsCount; fpsCount = 0; fpsTime = fpsTime + 1000

	SetColor colors[0], colors[1], colors[2]; SetAlpha .7
	For i = 0 To imgH
		offset = Cos(frame + i * waves) * 48
		DrawImageRect2 imgRed, imgX + offset, imgY + i, 0, i, imgW, 1
	Next
	'For i = 0 To imgW
	'	offset = Sin(frame + i * waves) * 16
	'	DrawImageRect2 imgRed, imgX + i, imgY + offset, i, 0, 1, imgH
	'Next

	For i = 0 To 3; objList.AddLast obj.Create(); Next
		
	For o = EachIn objList
		delFlake = False
		o.Render
		If o.y >= 610 delFlake = True
		If delFlake = True objList.Remove o; counter:-1
	Next

	SetColor 255, 255, 255
	SetAlpha 1; SetHandle 0, 0; SetRotation 0; SetScale 1, 1

	frame:+1

	If waveD = 0
		If waves > .1 waves:-waveI Else waveD = 1; waveI = Rnd(.001, .005)
	Else
		If waves < 2 waves:+waveI Else waveD = 0; waveI = Rnd(.001, .005)
	EndIf
		
	If Rand(1, 1000) = 500 ' Randomly change wave directions
		If waveD = 1 waveD = 0 Else waveD = 1
	EndIf

	For i = 0 To 2
		If colorD[i] = 0
			If colors[i] > 78 colors[i]:-colorI[i] Else colorI[i] = Rnd(.1, .5); colorD[i] = 1
		Else
			If colors[i] < 255 colors[i]:+colorI[i] Else colorI[i] = Rnd(.1, .5); colorD[i] = 0
		EndIf
	Next
	
	DrawText "FPS: " + fpsCurrent, 1, 546
	'DrawText "Image Slices: " + imgH, 1, 558
	DrawText "Particles: " + counter, 1, 572
	DrawText GCMemAlloced() / 1024 + "kbs ", 1, 586

	Flip
Until KeyDown(KEY_ESCAPE)

Function DrawImageRect2(img:TImage, x#, y#, rx#, ry#, rw#, rh#)
	SetViewport x, y, rw, rh
	DrawImage img, x - rx, x - ry
	
	SetViewport 0, 0, GFX_WIDTH, GFX_HEIGHT
End Function
