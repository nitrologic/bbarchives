; ID: 2384
; Author: UnderwoodNullium
; Date: 2008-12-25 17:05:25
; Title: Crude 3D Engine Using 2D
; Description: My attempt at making a 3D world of lines with BlitzMax.

' initiate stuff

	SuperStrict				' make blitzmax mean!  (and tidy!)
	SeedRnd MilliSecs()					' make randomly generated numbers to choose from.

	Graphics(1024,768,32)					' set up a graphics object

	SetBlend(3)							' set the blend mode
		SetAlpha(1)


' types / lists

	Type point
		Field x!,y!,z!,rotx!,roty!,rotz!,angle!,distance!
	End Type

	Global pointlist:TList = CreateList()


' engine globals

	Global camerax! = 0					' set up camera
	Global cameray! = 0
	Global cameraz! = -400

	Global x!,y!

	Global cameraxangle! = 0
	Global camerayangle! = 0

	Global lensxangle! = -90
	Global lensyangle! = -90



	For Local loop:Int = 0 To 70
		CreatePoint(Rnd(-100,100),Rnd(-100,100),Rnd(-100,100))
	Next









	While Not KeyHit(key_escape)
	Cls

		If KeyDown(key_a) cameraxangle!:+1
		If KeyDown(key_d) cameraxangle!:-1
		If KeyDown(key_w) camerayangle!:+1
		If KeyDown(key_s) camerayangle!:-1

		If KeyDown(key_right) camerax!:-1
		If KeyDown(key_left)  camerax!:+1
		If KeyDown(key_up)    cameray!:+1
		If KeyDown(key_down)  cameray!:-1
		If KeyDown(key_1)     cameraz!:-1
		If KeyDown(key_2)     cameraz!:+1

		For Local p:point = EachIn pointlist
		For Local p2:point = EachIn pointlist
			DrawLine(Get3DX!(p.x!,p.roty!,p.rotz!),Get3DY!(p.x!,p.roty!,p.rotz!),Get3DX!(p2.x!,p2.roty!,p2.rotz!),..
			Get3DY!(p2.x!,p2.roty!,p2.rotz!))
		Next
		Next
		
		DrawPoints()

	Flip
	Wend
	End







Function RotateCameraY!(p:point)

	p.distance! = GetDistance!(p.y!,p.z!,cameray!,cameraz!)
	p.angle!    = GetAngle!(p.y!,p.z!,cameray!,cameraz!)

	p.roty! = (cameray! + (Cos(p.angle! + camerayangle!) * p.distance!))
	p.rotz! = (cameraz! + (Sin(p.angle! + camerayangle!) * p.distance!))

End Function




Function GetAngle!(x1!,y1!,x2!,y2!)

	Return(ATan2((y2! - y1!),(x2! - x1!)))

End Function



Function GetDistance!(x1!,y1!,x2!,y2!)

	Return(Sqr((x2! - x1!)^2 + (y2! - y1!)^2))

End Function









Function CreatePoint(x!,y!,z!)

	Local p:point

	p:point = New point
		p.x! = x!
		p.y! = y!
		p.z! = z!

	ListAddLast(pointlist:TList,p:point)


End Function





Function DrawPoints()

	Local p:point

	For p:point = EachIn pointlist:TList

		RotateCameraY(p:point)

		If p.rotz! > cameraz!
			Plot(Get3DX!(p.x!,p.roty!,p.rotz!),Get3DY!(p.x!,p.roty!,p.rotz!))
		EndIf

	Next

End Function





Function Get3DX!(x!,y!,z!)

 	Return((camerax! - x!) + (Double Tan(lensxangle! / 2) * (z! - cameraz!)) / 2) * GraphicsWidth() / (Double Tan(lensxangle! / 2) * (z! - cameraz!))

End Function



Function Get3DY!(x!,y!,z!)

 	Return((cameray! - y!) + (Double Tan(lensyangle! / 2) * (z! - cameraz!)) / 2) * GraphicsHeight() / (Double Tan(lensyangle! / 2) * (z! - cameraz!))

End Function
