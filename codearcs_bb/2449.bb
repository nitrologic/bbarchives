; ID: 2449
; Author: GIB3D
; Date: 2009-03-30 22:06:38
; Title: Angling Models
; Description: Example of how to angle your model, without need for a pivot.

InitGraphics()

Const PC = 1, WC = 2

Collisions PC,WC,2,2

Global Light = CreateLight()
	RotateEntity Light,10,20,0
	
Global Camera = CreateCamera()
	RotateEntity Camera,40,90,0
	CameraClsColor Camera,60,60,60
	
Global Model = CreateCone()
	ScaleEntity Model,1,1,2
	PositionEntity Model,0,4,0
	EntityType Model,PC

Global Y_Acceleration# = -.15

Local Width=64,Height=64
Local Texture = CreateTexture(Width,Height)
	SetBuffer TextureBuffer(Texture)
		Color 255,255,255
		Rect 0,0,Width,Height
		Color 0,0,0
		Rect 0,0,Width,Height,0
		Rect 1,1,Width-2,Height-2,0
	SetBuffer BackBuffer()
	ScaleTexture Texture,.2,.2

EntityTexture Model,Texture

Global wall[6]
For c = 0 To 6
	wall[c] = CreateCube()
	EntityColor wall[c],100,60,0
	EntityType wall[c],WC
	EntityTexture wall[c],Texture
Next

PositionEntity wall[0],-16,8,16
ScaleEntity wall[0],1,12,32

PositionEntity wall[1],16,8,16
ScaleEntity wall[1],1,12,32

PositionEntity wall[2],0,8,-17
ScaleEntity wall[2],16,12,1

PositionEntity wall[3],0,8,49
ScaleEntity wall[3],16,12,1

PositionEntity wall[4],-10,16,16
ScaleEntity wall[4],16,1,32
RotateEntity wall[4],0,0,40

PositionEntity wall[5],0,-5,16
ScaleEntity wall[5],16,1,32

PositionEntity wall[6],0,-6.5,16
ScaleEntity wall[6],7,7,16
RotateEntity wall[6],0,90,30

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	
Color 255,255,255
While Not KeyDown(1)
	Local YPoint# = Sqr(EntityY(Model,1)^2)
	Local YPoint2# = YPoint
	
	Local Pitch#=EntityPitch(Model,1)
	Local Roll#=EntityRoll(Model,1)
	
	Local CountCol = CountCollisions(Model)
	
	If CountCol > 0
		Local NX# = CollisionNX(Model,1)
		Local NY# = CollisionNY(Model,1)
		Local NZ# = CollisionNZ(Model,1)
		
		If NY => 0.5 ; 1 Means it's flat, 0 means it's a wall, everything inbetween is slanted
			AlignToVector Model,NX,NY,NZ,2,.1
			Y_Acceleration = -.15
				Else
					Y_Acceleration = Y_Acceleration - .01
		EndIf
			Else
				Y_Acceleration = Y_Acceleration - .01
	EndIf
	
	TurnEntity Model,0,-(MouseXSpeed()*.5),0
	MoveEntity Model,(KeyDown(32)-KeyDown(30))*.2,0,(KeyDown(17)-KeyDown(31))*.2
	If KeyDown(57) TranslateEntity Model,0,.4,0
	TranslateEntity Model,0,Y_Acceleration,0
	
	If CountCol = 0
		YPoint2 = Sqr(EntityY(Model,1)^2)
		TurnEntity Model,-(Pitch+(((YPoint2-YPoint)*Sgn(KeyDown(17)-KeyDown(31)))*180))*.05,0,-Roll*.05 ; Slowly brings the pitch back to flat, but angles you down depending on your Y speed and which key your pressing
			ElseIf NY < 0.5
				TurnEntity Model,-Pitch*.05,0,-Roll*.05
	EndIf
	
	MoveMouse GraphicsWidth()*.5,GraphicsHeight()*.5
	
	PositionEntity Camera,15,15+EntityY(Model,1),EntityZ(Model,1),1
	PointEntity Camera,Model
	
	UpdateWorld
	RenderWorld
	
	Text 0,0,"NX= "+NX+" : NY= "+NY+" : NZ= "+NZ
	Text 0,20,"Point1 = " + YPoint
	Text 0,40,"Point2 = " + YPoint2
	Text 0,60,"Speed = " + (YPoint2-YPoint)
	
	Flip
	Delay(2)
Wend
End

Function InitGraphics(w = 1024, h = 768,title$="Blitz3D Program",exit_message$="")
	Graphics3D w, h, 32, 2
	SetBuffer BackBuffer()
	SeedRnd MilliSecs()
	
	If exit_message <> ""
		AppTitle title,exit_message
			Else
				AppTitle title
	EndIf
End Function
