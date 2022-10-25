; ID: 1997
; Author: Matt Merkulov
; Date: 2007-04-21 05:20:48
; Title: Simple interactive tutorial using scripts
; Description: Learning controls of simple model viewer

;Simple interactive tutorial using scripts - learning controls of simple model viewer - by Matt Merkulov

Graphics3D 640, 480

cam = CreateCamera()
PositionEntity cam, 0, 0, -3
RotateEntity CreateLight(), 45, 0, 45

; The address, number and detail level of a figure
Global m, et = 1, det = 10
; Color of a figure
Global cr = 255, cg = 255, cb = 255
; Creating figure
recreate

CameraProject cam, 0, 0, 0

; Calculation of an increment on axises X and Y
d# = ProjectedX()
CameraProject cam, 1, 0, 0
d# = 1.0 / (ProjectedX() - d#)
esc# = 1.0

; Loading lesson script in memory
Dim cmd$(18, 1)
For n = 0 To 17
	Read cmd$(n, 0), cmd$(n, 1)
Next

SetFont LoadFont("Arial Cyr", 14)
SetBuffer BackBuffer()

Repeat
	; Calculation of increments of values for the mouse
	dx# = MouseX() -odx# 
	dy# = ody# - MouseY()
	dmz = MouseZ() -mz
	event = 0

	If MouseDown(1) Then
		; Moving a figure(event 1)
		ex# = ex# + d# * dx# 
		ey# = ey# + d# * dy# 
		event = 1
	ElseIf MouseDown(2) Then
		; Rotating of a figure(event 2)
		exang# = exang# + dx# 
		eyang# = eyang# + dy# 
		event = 2
	ElseIf MouseDown(3) Then
		; Scaling a figure(event ¹3)
		esc# = esc# * 1.01 ^ dy# 
		event = 3
	ElseIf dmz <> 0 Then
		If det + dmz > 2 And det + dmz < 20 Then
			; Change of detail level (event 6)
			det = det + dmz
			recreate
			event = 6
		End If
		mz = MouseZ()
	Else
		mb = 0
	End If

	odx# = MouseX()
	ody# = MouseY()

	; Processing pressed keys
	i = GetKey()
	If i >= 49 And i <= 52 Then
		; Changing a type of a figure(event 4)
		et = i - 48
		recreate
		If et = 2 Then event = 4
	End If
	If i = 48 Then
		; Changing color of a figure (event 5)
		cr = Rand(0, 255)
		cg = Rand(0, 255)
		cb = Rand(0, 255)
		EntityColor m, cr, cg, cb
		event = 5
	End If

	PositionEntity m, ex#, ey#, 0
	RotateEntity m, eyang#, exang#, 0
	ScaleEntity m, esc#, esc#, esc# 

	; Processing commands
	Select cmd$(cn, 0)
		Case"TEXT"
			; A conclusion of the text(only at inactivity)
			If event = 0 Then
			txt$ = cmd$(cn, 1)
			cn = cn + 1
			End If
		Case"WAITFOR"
			; Expectation of event
			If event = cmd$(cn, 1) Then cn = cn + 1
		Case"WAITKEY"
			; Expectation of pressing of space key
			If i = 32 Then cn = cn + 1
	End Select

	RenderWorld
	Text 0, 0, txt$
	Flip
Until KeyHit(1)

Function recreate()
	; Creating figure
	If m <> 0 Then FreeEntity m
	Select et
		Case 1:m = CreateCube()
		Case 2:m = CreateSphere(det)
		Case 3:m = CreateCylinder(det)
		Case 4:m = CreateCone(det)
	End Select
	EntityColor m, cr, cg, cb
End Function

Data "TEXT", "It is the program intended for viewing three - dimensional objects. Press space."
Data "WAITKEY", ""
Data "TEXT", "This lesson will help you to master it."
Data "WAITKEY", ""
Data "TEXT", "Having pressed left button of the mouse, you can move object. Try."
Data "WAITFOR", "1"
Data "TEXT", "With the right button, it is possible to rotate object. Turn object."
Data "WAITFOR", "2"
Data "TEXT", "Middle button - for scaling of object. Change the size of object."
Data "WAITFOR", "3"
Data "TEXT", "Digital keys 1 - 4 - selection of object. For proceeding, choose sphere(2)."
Data "WAITFOR", "4"
Data "TEXT", "You can change color of object with '0' key. Change Color To continue."
Data "WAITFOR", "5"
Data "TEXT", "Change detail level of object by rotating mouse wheel."
Data "WAITFOR", "6"
Data "TEXT", "That's all, the lesson is ended."
Data "WAITKEY", ""
