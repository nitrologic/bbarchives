; ID: 1736
; Author: Jesse B Andersen
; Date: 2006-06-20 03:54:41
; Title: Animation System
; Description: An animation system using types

;Animation System
;xmlspy | http://www.alldevs.com
;msn: xmlspy_gt@hotmail.com
;I have models which use seq style and I
;have models that don't. I want To be 
;able To use the same system on both.

Global Debug = True
Gosub example


Type model
	Field name$, File$, mesh
	Field speed#, mode, a.anim
	Field cnamed$, ocnamed$
End Type

Type anim
	Field name$, File$, cnamed$, h
	Field ifr, efr, mode
End Type

;mode
;1 - all animations in a single file
;2 - animations broken down to files

Function Load_AnimatedModel(name$, File$, speed# = 1, mode = 1 )
	If FileType(File$)
		m.model = New model
		m\name$ = name$
		m\File$ = File$
		m\mesh = LoadAnimMesh(File$)
		m\speed# = speed#
		m\cnamed$ = "idle"
		m\mode = mode
		Return m\mesh
	EndIf
End Function

Function Add_Animation(name$, cnamed$, ifr=0, efr=0, mode=1, File$="")
	Select mode
		Case 1
			a.anim = New anim
			a\name$ = name$
			a\cnamed$ = cnamed$
			a\ifr = ifr
			a\efr = efr
			a\mode = 1
			a\File$ = ""
			For m.model = Each model
				If m\name$ = a\name$ Then
					a\h = ExtractAnimSeq(m\mesh, ifr, efr)
					If Debug
						DebugLog "Added Animation: " + m\name$ + " + " + a\name$
						DebugLog a\cnamed$
					EndIf
					Exit
				EndIf
			Next
		Case 2
			If FileType(File$)
			a.anim = New anim
			a\name$ = name$
			a\cnamed$ = cnamed$
			a\ifr = ifr
			a\efr = efr
			a\mode = 2
			a\File$ = File$
			For m.model = Each model
				If m\name$ = a\name$ Then
					a\h = LoadAnimSeq(m\mesh,a\File$)
					If Debug
						DebugLog "Added Animation: " + m\name$ + " + " + a\name$
						DebugLog a\cnamed$
					EndIf
					Exit
				EndIf
			Next
			EndIf
	End Select
End Function

Function ChangeC_Animation(name$, cnamed$)
	For m.model = Each model
		If name$ = m\name$ Then
			m\cnamed$ = cnamed$
		EndIf
	Next
End Function

Function Animate_Model(mesh, moving = False)
	For m.model = Each model
		;work on the specified mesh
		If m\mesh = mesh Then
			For a.anim = Each anim
				If a\cnamed$ = m\cnamed$ Then
					;If the old does not match the new animation
					If m\ocnamed$ <> m\cnamed$ Then 
						Animate m\mesh, 3, m\speed#, a\h, 10
						m\ocnamed$ = m\cnamed$
						If Debug DebugLog "changed " + m\name$ + " | " + m\cnamed$
					Else
						;if the mesh is not doing any animation then do idle
						If Animating(m\mesh) = 0 And moving = False Then
							For a.anim = Each anim
								If a\cnamed$ = "idle" And a\name$ = m\name$ Then
									m\cnamed$ = "idle"
									Animate m\mesh, 3, m\speed#, a\h, 10
								EndIf
							Next
						;if the mesh is not animating but the mesh is moving then restart animation
						ElseIf Animating(m\mesh) = 0 And moving = True
							Animate m\mesh, 3, m\speed#, a\h
							If Debug DebugLog "starting anim " + m\name$ + " | " + m\cnamed$
						EndIf
					EndIf
					Exit
				EndIf
			Next
		EndIf
	Next
End Function

Function Get_CurrentAnimation$(name$)
	For m.model = Each model
		If m\name$ = name$
			Return m\cnamed$
		EndIf
	Next
End Function

Function Get_AnimatedModel(name$)
	For m.model = Each model
		If m\name$ = name$ Then
			Return m\mesh
		EndIf
	Next
End Function

Function Clear_Modelanims()
	For m.model = Each model
		FreeEntity m\mesh
		Delete m
	Next
	For a.anim = Each anim
		Delete a
	Next
End Function



.example
Graphics3D 640, 480, 0, 2
AppTitle "Animation system"

cam = CreateCamera()
light =CreateLight()
MoveEntity cam, 0, 3, -9

plane = CreatePlane()
CreateMirror()
EntityAlpha plane, .7

dwarf = Load_AnimatedModel("dwarf", "dwarf1.b3d", .2)
Add_Animation("dwarf", "walk", 2, 14)
Add_Animation("dwarf", "run", 16, 26)
Add_Animation("dwarf", "attack", 144, 160)
Add_Animation("dwarf", "block", 192,210)
Add_Animation("dwarf", "idle", 75, 88)

tech = Load_AnimatedModel("tech", "tech\L-Tech-Static.3DS", .5, 2)
Add_Animation("tech", "walk", 0, 0, 2, "tech\L-Tech-Move.3DS")
Add_Animation("tech", "attack", 0, 0, 2, "tech\L-Tech-Attack1.3DS")
Add_Animation("tech", "die", 0, 0, 2, "tech\L-Tech-Die.3DS" )
Add_Animation("tech", "impact", 0, 0, 2, "tech\L-Tech-Impact.3DS" )
Add_Animation("tech", "idle", 0, 0, 2, "tech\L-Tech-Idle.3DS" )


ScaleEntity dwarf, .1, .1, .1
TurnEntity dwarf, 0, 135, 0

ScaleEntity tech, 5, 5, 5
MoveEntity tech, 5, 0, 0


Repeat
	If KeyDown(2) Then
		ChangeC_Animation("dwarf", "walk") : move = True
		ChangeC_Animation("tech", "walk") : move2 = True
	EndIf
	
	If KeyDown(3) Then
		ChangeC_Animation("dwarf", "run") : move = True
		ChangeC_Animation("tech", "die") : move2 = True
	EndIf

	If KeyDown(4) Then
		ChangeC_Animation("dwarf", "attack") : move = True
		ChangeC_Animation("tech", "attack") : move2 = True
	EndIf
	
	If KeyDown(5) Then
		ChangeC_Animation("dwarf", "block") : move = True
		ChangeC_Animation("tech", "impact") : move2 = True
	EndIf

	

	Animate_Model(dwarf, move)
	Animate_Model(tech, move2)
	If move = True Then move = False
	If move2 = True Then move2 = False
	
	
	RenderWorld()
	UpdateWorld()
	Text 0, 0, "use 123"
	Text 0, 20, "current animation: " + Get_CurrentAnimation("dwarf")
	Text 0, 40, "tech: " + Get_CurrentAnimation("tech")
	Flip()
Until KeyHit(1)
End
Return
