; ID: 477
; Author: Rob
; Date: 2002-11-04 11:04:45
; Title: Project 2D coords to 3D
; Description: Your 2D mouse can pick any 3D position or Entity without annoying kludges

; 2D to 3D projection by Rob Cummings
; rob@redflame.net

Global camera,cursor,ball,gw,gh

Graphics3D 640,480,16,2
camera=CreateCamera()

;orth for the sake of it
CameraProjMode camera,2
CameraZoom camera,.5

;width and height of display
gw=GraphicsWidth()
gh=GraphicsHeight()

;important. Create a 3D mouse position using a pivot.
;you need two pivots for this to work. In our case, we're
;using a ball to see where our pick took place.
cursor=CreatePivot() ; or a sprite...
MoveEntity cursor,0,0,2


; so you can see the effect. Replace with a pivot for speed.
ball=CreateSphere()
ScaleEntity ball,.2,.2,.2
EntityAlpha ball,0.5
EntityColor ball,100,100,255

;load your entity here
ent=CreateSphere(16)
EntityPickMode ent,2 ;pickable...
ScaleEntity ent,2,1,1
MoveEntity ent,0,0,1

;misc
light=CreateLight(2,camera)
LightRange light,8
MoveEntity light,10,10,-10
PositionEntity camera,0,0,-2

While Not KeyHit(1)
	TurnEntity ent,.1,.1,.1
	
	;positions	
	x#=MouseX()
	y#=MouseY()
	
	;half of screen dimensions needed
	w=GraphicsWidth()/2
	h=GraphicsHeight()/2
	
	;3D cursor positioned at 2D cursor
	PositionEntity cursor,(x-w)/w*2,-(y-h)/w*2,2

	;orient to camera
	RotateEntity cursor,EntityPitch(camera),EntityYaw(camera),0

	;pick from 3D cursor into 3D space
	cx#=EntityX(cursor)
	cy#=EntityY(cursor)
	cz#=EntityZ(cursor)
	picked=LinePick( cx,cy,cz,cx,cy,-cz*2000 ) ;2000 - distance to "cast our line"


	;show result for fun
	If picked
		ShowEntity ball
		PositionEntity ball,PickedX(),PickedY(),PickedZ()
	Else
		HideEntity ball
	EndIf	

	UpdateWorld
	RenderWorld
	Flip
Wend
End
