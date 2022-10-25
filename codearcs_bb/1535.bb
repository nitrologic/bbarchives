; ID: 1535
; Author: Boiled Sweets
; Date: 2005-11-17 03:01:58
; Title: Create a ladder
; Description: Create a ladder

; CreateCube Example
; ------------------

Graphics3D 1024,768,32
SetBuffer BackBuffer()

p = CreatePivot()

SeedRnd (MilliSecs()) 
camera=CreateCamera(p)
PositionEntity camera, 0,0, -4

light=CreateLight()
RotateEntity Light,45,0,0

height$=Input$("Ladder height?       1-100    :") 
wonky$=Input$("Ladder wonnkyness?     0-10    :") 

createLadder(Int(height), Int(wonky))

While Not KeyDown( 1 )

If KeyDown(203)
	TurnEntity p, 0, -3, 0
EndIf

If KeyDown(205)
	TurnEntity p, 0, 3, 0
EndIf

If KeyDown(200)
	TurnEntity p, -3, 0, 0
EndIf

If KeyDown(208)
	TurnEntity p, 3, 0, 0
EndIf

If KeyDown(30)
	MoveEntity camera, 0, 0, .1
EndIf

If KeyDown(44)
	MoveEntity camera, 0, 0, -.1
EndIf


RenderWorld
Text 10,10, "Cursor keys to turn, A / Z to zoom, escape to quit"
Flip
Wend

End

Function createLadder(length#, wonky#)

	rungs = length * 2
	rung_distance# = (length / (rungs+1)) 
	length = length / 2

	; legs
	
	leg_left=CreateCube()
	ScaleMesh leg_left, .1, length, .1
	PositionMesh leg_left,-.4, 0, 0
	
	leg_right=CreateCube()
	ScaleMesh leg_right, .1, length, .1
	PositionMesh leg_right, .4, 0, 0
	
	;rungs
		
	rung_start# = -(rung_distance#/2) - (((rungs-2)/2) * rung_distance#)

	For i = 1 To rungs 
		rung=CreateCube()
		ScaleMesh rung, .6, .07, 0.05
		RotateMesh rung, 0, 0, Rnd(-wonky, wonky)
		PositionMesh rung, 0, rung_start#, -0.1
		rung_start# = rung_start# + rung_distance#
		AddMesh rung, leg_left
		FreeEntity rung
	Next

	AddMesh leg_right, leg_left	
	FreeEntity leg_right
	
	;Tex = LoadTexture ("wood02.jpg")
	;EntityTexture leg_left, Tex
	
End Function
