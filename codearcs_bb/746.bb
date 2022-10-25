; ID: 746
; Author: Ken Lynch
; Date: 2003-07-15 10:07:01
; Title: Spinning platforms
; Description: How to do spinning platforms

;
; Spinning platform example
;

Graphics3D 800, 600

light = CreateLight()

camera = CreateCamera()
PositionEntity camera, 0, 2, -10

ball = CreateSphere()
PositionEntity ball, 4, 5, 0

table = CreateCube()
ScaleEntity table, 8, 0.1, 8
EntityPickMode table, 2				; Make table pickable

yv# = 0

Repeat

	;
	; Do the line pick
	;
	x# = EntityX(ball)
	y# = EntityY(ball)
	z# = EntityZ(ball)
	lp = LinePick(x, y, z, 0, -1.1, 0)
	
	;
	; Check if table is directly under the ball
	;
	If lp = 0 Then
		yv = yv - 0.01
	ElseIf yv < 0 Then
		PositionEntity ball, PickedX(), PickedY()+1, PickedZ()
		yv = 0
	End If

	;
	; Hit space to jump
	;
	If KeyHit(57) Then yv = 0.5

	;
	; Move ball
	;
	TranslateEntity ball, 0, yv, 0

	;
	; Parent entity to whatever is picked
	;
	EntityParent ball, lp
	
	;
	; Turn the table
	;
	TurnEntity table, 0, 2, 0
	
	;
	; Reparent ball back to main world
	;
	EntityParent ball, 0

	RenderWorld
	Flip
Until KeyHit(1)
