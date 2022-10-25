; ID: 255
; Author: Rob
; Date: 2002-02-28 20:32:41
; Title: perfect ball rolling
; Description: very easy to roll a ball in 3 lines of code

; JP ball turn by rob
;
;ball is parented to ballpos, which is a pivot.
;moving mouse moves the ball. mx and my are the balls speed. used to calculate turn too.

Global ball,ballpos

;display crap
HidePointer
Graphics3D 640,480,16,2
camera=CreateCamera()
MoveEntity camera,0,200,-200
TurnEntity camera,45,0,0
light=CreateLight(2)
MoveEntity light,1000,1000,-1000
p=CreatePlane():EntityAlpha p,0.5
EntityColor p,0,0,255
m=CreateMirror()

;set up your balls etc. each ball has a pivot for movement.
ballpos=CreatePivot()
ball=CreateSphere(6,ballpos)
ScaleEntity ball,20,20,20
MoveEntity ballpos,0,20,0
t=CreateTexture(16,16):SetBuffer TextureBuffer(t)
ClsColor 0,0,0
Cls
Color 255,255,255
Rect 0,0,8,8,1
Rect 8,8,8,8,1
ScaleTexture t,.2,.2
EntityTexture ball,t

;mainloop
While Not KeyHit(1)
	updateball()
	RenderWorld
	Flip
Wend
End

Function updateball()
	
	;control the ball o death
	mx#=MouseXSpeed()*0.5
	my#=-MouseYSpeed()*0.5
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
	MoveEntity ballpos,mx,0,my
	
	;reset turnentity
	RotateEntity ball,0,0,0
	
	;turn it how you want.
	TurnEntity ball,my*2,0,-mx*2
	
	;reset the coordinate system for the ball! (secret frosties recipe)
	RotateMesh ball,EntityPitch(ball),EntityYaw(ball),EntityRoll(ball)
End Function
