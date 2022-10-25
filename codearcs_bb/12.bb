; ID: 12
; Author: BlitzSupport
; Date: 2001-08-16 22:07:57
; Title: Alpha cubes demo
; Description: Little demo of cubes changing translucency

; Use left mouse button... note lack of frame-limiting code!

Type Timer
	Field start
	Field timeOut
End Type

Function SetTimer.Timer (timeOut)
	t.Timer = New Timer
	t\start   = MilliSecs ()
	t\timeOut = t\start + timeOut
	Return t
End Function

Function TimeOut (test.Timer)
	If test <> Null
		If test\timeOut < MilliSecs ()
			Delete test
			Return 1
		EndIf
	EndIf
End Function

Graphics3D 640, 480

SetBuffer BackBuffer ()

SeedRnd (MilliSecs ())

Global piv = CreatePivot ()
cam = CreateCamera (piv)
PositionEntity cam, 0, 0.1, -5
PointEntity cam, piv
CameraZoom cam, 5

AmbientLight 32, 32, 32

light = CreateLight ()
PositionEntity light, -5, 0, -5

Global cube = CreateCube ()
HideEntity cube

Type cubes
	Field entity
	Field alpha#
	Field incdec
	Field kill
	Field zacc#
End Type

Global spawn = 4000
Global newCube.Timer = SetTimer (spawn)
Global alpha.Timer   = SetTimer (50)

Repeat

	If KeyHit (17) Then w = 1 - w: WireFrame w

	UpdateGame ()
	UpdateWorld
	RenderWorld
	Flip
	
Until KeyHit (1)

End

Function UpdateGame ()

	TurnEntity piv, 0, 0, 0.1
	
	For a.cubes = Each cubes
		TurnEntity a\entity, a\zacc, 2, a\zacc
		TranslateEntity a\entity, 0, 0, a\zacc * (a\kill * -1)
	Next

	If MouseHit (1)

		cubelist.cubes = New cubes
		cubelist\entity  = CopyEntity (cube)
		cubelist\alpha = 0
		cubelist\incdec = 1
		cubelist\kill = -1
		cubelist\zacc = Rnd (0.025, 1)
		EntityAlpha cubelist\entity, cubelist\alpha
		EntityColor cubelist\entity, Rnd (100, 255), Rnd (100, 255), Rnd (100, 255)
		EntityShininess cubelist\entity, Rnd (0.01, 1)

	EndIf

	If TimeOut (newCube)

		cubelist.cubes = New cubes
		cubelist\entity  = CopyEntity (cube)
		cubelist\alpha = 0
		cubelist\incdec = 1
		cubelist\kill = -1
		cubelist\zacc = Rnd (0.01, 0.5)
		EntityAlpha cubelist\entity, cubelist\alpha
		EntityColor cubelist\entity, Rnd (100, 255), Rnd (100, 255), Rnd (100, 255)
		EntityShininess cubelist\entity, Rnd (0.01, 1)

		newCube.Timer = SetTimer (spawn)

	EndIf

	If TimeOut (alpha)

		For a.cubes = Each cubes
		
			If a\incdec
				a\alpha = a\alpha + a\zacc / 10
			Else
				a\alpha = a\alpha - a\zacc / 10
			EndIf
			
			If a\alpha => 1
				a\incdec = 0
				a\kill = 1
			EndIf

			EntityAlpha a\entity, a\alpha

			If a\alpha <= 0
				If a\kill = 1
					FreeEntity a\entity
					Delete a
				EndIf
			EndIf

		Next

		alpha.Timer = SetTimer (50)

	EndIf

End Function
