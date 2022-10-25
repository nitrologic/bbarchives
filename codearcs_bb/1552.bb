; ID: 1552
; Author: John J.
; Date: 2005-12-02 10:49:54
; Title: BattleTanks
; Description: Drive your tank around the city with your AI teammates, hunting down your enemies, receiving reinforcements as you progress through multiple waves of enemies.

;BattleTanks
Graphics3D 800,600,0,2
AppTitle "BattleTanks"

Const COLLISION_SHELL = 1
Const COLLISION_TARGET = 2
Const COLLISION_CAMERA = 3
Const COLlISION_TANK = 4

SeedRnd MilliSecs()

;Set-up
cam = CreateCamera()
EntityType cam, COLLISION_CAMERA
EntityRadius cam, .3

CameraRange cam, .1, 1000
CameraClsColor cam, 64,128,255
light = CreateLight()
RotateEntity light, 35,20,0

;Create scenery
ground = CreatePlane()
;EntityColor ground, 151, 103, 41

Dim fragment(10)
For i = 0 To 10
	fragment(i) = CreateMesh()
	s=CreateSurface(fragment(i))
	v1=AddVertex(s,-.5+Rnd(-.5,.5),0,-.5+Rnd(-.5,.5), 0,1)
	v2=AddVertex(s,.5+Rnd(-.5,.5),0,-.5+Rnd(-.5,.5), 1,1)
	v3=AddVertex(s,.5+Rnd(-.5,.5),0,.5+Rnd(-.5,.5), 1,0)
	v4=AddVertex(s,-.5+Rnd(-.5,.5),0,.5+Rnd(-.5,.5), 0,0)
	AddTriangle s, v1, v2, v3
	AddTriangle s, v1, v3, v4
	EntityType fragment(i), 0
	HideEntity fragment(i)
Next

;Global puff = CreateTexture(16,16,1+2+8)
;buff = TextureBuffer(puff)
;SetBuffer buff
;For x = 0 To 15
;For y = 0 To 15
;	xd# = 7-x
;	yd# = 7-y
;	dist# = Sqr(xd*xd+yd*yd)
;	co = Rnd(-50,0)
;	r = 255+co: g = 255+co: b = 255+co
;	a# = 1.0-(dist/8)
;	If a>1 Then a=1 Else If a<0 Then a=0
;	clr = (a*255 Shl 24)+(r Shl 16)+(g Shl 8)+(b)
;	WritePixel x,y, clr
;Next
;Next

tex = CreateTexture(16,16,1+8)
buff = TextureBuffer(tex)
SetBuffer buff
For x = 0 To 15
For y = 0 To 15
	r = 151: g = 103: b = 41
	co = Rnd(-50,50)
	r = r + co: g = g + co: b = b + co
	If b<0 Then b=0
	clr = (r Shl 16)+(g Shl 8)+(b)
	WritePixel x,y, clr
Next
Next

tex2 = CreateTexture(16,16,1+8)
buff = TextureBuffer(tex2)
SetBuffer buff
For x = 0 To 15
For y = 0 To 15
	r = 151: g = 103: b = 41
	co = Rnd(0,50)
	r = r + co: g = g + co: b = b + co
	If b<0 Then b=0
	clr = (r Shl 16)+(g Shl 8)+(b)
	WritePixel x,y, clr
Next
Next

Global metal = CreateTexture(16,16,1+8)
buff = TextureBuffer(metal)
SetBuffer buff
For x = 0 To 15
For y = 0 To 15
	r = 255: g = 255: b = 255
	co = Rnd(-100,0)
	r = r + co: g = g + co: b = b + co
	If b<0 Then b=0
	clr = (r Shl 16)+(g Shl 8)+(b)
	WritePixel x,y, clr
Next
Next
ScaleTexture metal, 2, 4

Global house = CreateTexture(16,16,1+8)
buff = TextureBuffer(house)
SetBuffer buff
For x = 0 To 15
For y = 0 To 15
	r = 255: g = 255: b = 255
	If x>13 Or y>13 Then
		co = Rnd(-255,-200)
	Else
		co = Rnd(-20,0)
	End If
	r = r + co: g = g + co: b = b + co
	If b<0 Then b=0
	clr = (r Shl 16)+(g Shl 8)+(b)
	WritePixel x,y, clr
Next
Next
ScaleTexture house, .2, .1

SetBuffer BackBuffer()

ScaleTexture tex2, 10,10
EntityTexture ground, tex, 0, 1
EntityTexture ground, tex2, 0, 0

MakeCity(0,0,7,7)
MakeCity(40,40,7,7)
MakeCity(40,0,7,7)
MakeCity(0,40,7,7)

;Create tanks
Type Tank
	Field mesh, turret
	Field Yturn#, speed#
	Field ammo, damage#
	Field AI
	Field firetime
	Field team
	
	Field id
	Field textdrawn
	
	;AI data
	Field state
	Field waypt_x#, waypt_z#, waypt_dist#
	Field timeout
	Field target.Tank
	Field leader.Tank
	Field missx#, missy#
	Field requestbackup
	Field AIframecount
End Type

Const TEAM_RED = 1
Const TEAM_GREEN = 2
Const TEAM_BLUE = 3
Const TEAM_YELLOW = 4

Const STATE_PATROL = 1
Const STATE_HUNT = 2
Const STATE_SNIPE = 3
Const STATE_RETREAT = 4
Const STATE_FOLLOW = 5

Global playertank.Tank, playerteam
Global score, timerdir = 1

Type Shell
	Field mesh
	Field xv#, yv#, zv#
	Field owner.Tank
End Type

Type Frag
	Field mesh
	Field dx#, dy#, dz#
	Field rx#, ry#, rz#
	Field life#
End Type

Type Explosion
	Field mesh[4]
	Field life#[4]
End Type

Type ChatText
	Field txt$
	Field name$
	Field displaytime
	Field speaker.Tank
End Type

Type HighScore
	Field Score
	Field Name$
End Type

;Cheat code vars
Global cheat1
Global cheat2
Local chcount

EqualizeSpeeds()
EqualizeSpeeds()

Collisions COLLISION_SHELL, COLLISION_TARGET, 2, 1
Collisions COLLISION_SHELL, COLLISION_TANK, 2, 1
Collisions COLLISION_CAMERA, COLLISION_TARGET, 2, 2
Collisions COLLISION_CAMERA, COLLISION_TANK, 2, 2
Collisions COLLISION_TANK, COLLISION_TARGET, 2, 2
Collisions COLLISION_TANK, COLLISION_TANK, 2, 2

big = LoadFont("Arial", 22)
small = LoadFont("Arial", 16)
SetFont small

.playagain ;Used to restart the game

;Intro
p = CreatePivot()
PositionEntity p, 30, 0, 30
EntityParent cam, p
PositionEntity cam, 0, 10, -30
done = False
While Not done
	EqualizeSpeeds()
	
	PointEntity cam, p
	TurnEntity p, 0, Eq(2), 0
	
	RenderWorld
	UpdateWorld
	
	SetFont big
	Color 0,0,0
	Text 401, 31, "BattleTanks", True
	Color 255,255,255
	Text 400, 30, "BattleTanks", True
	SetFont small
	
	Color 0,0,0
	Text 401, 51, "Choose your team:", True
	Color 255,255,255
	Text 400, 50, "Choose your team:", True
	
	Color 0,0,0
	Text 351, 71, "1. Red", True
	Color 255,0,0
	Text 350, 70, "1. Red", True
	
	Color 0,0,0
	Text 451, 71, "2. Green", True
	Color 0,255,0
	Text 450, 70, "2. Green", True
	
	Color 0,0,0
	Text 351, 91, "3. Blue", True
	Color 0,0,255
	Text 350, 90, "3. Blue", True
	
	Color 0,0,0
	Text 451, 91, "4. Yellow", True
	Color 255,255,0
	Text 450, 90, "4. Yellow", True
	
	If KeyHit(2) Then
		playerteam = TEAM_RED: done = True
	End If
	If KeyHit(3) Then
		playerteam = TEAM_GREEN: done = True
	End If
	If KeyHit(4) Then
		playerteam = TEAM_BLUE: done = True
	End If
	If KeyHit(5) Then
		playerteam = TEAM_YELLOW: done = True
	End If
	
	;Cheat codes
	If KeyHit(1) Then
		chcount = chcount + 1
		Select chcount
		Case 3: cheat1 = True
		Case 5: cheat2 = True
		End Select
	End If
	
	Flip True
Wend

Color 0,0,0
Text 321,121, "Initial Team Size (1-10): "
Color 255,255,255
Locate 320,120
FlushKeys()
Global level = Int(Input("Initial Team Size (1-10): "))-1
FlushKeys()
If level < 0 Then level = 0
If level > 9 Then level = 9

score = 0

Rtank.Tank = MakeTanks(TEAM_RED, 0, 0, level+1)
Gtank.Tank = MakeTanks(TEAM_GREEN, 60, 60, level+1)
Btank.Tank = MakeTanks(TEAM_BLUE, 60, 0, level+1)
Ytank.Tank = MakeTanks(TEAM_YELLOW, 0, 60, level+1)

Select playerteam
	Case TEAM_RED: playertank.Tank = Rtank.Tank
	Case TEAM_GREEN: playertank.Tank = Gtank.Tank
	Case TEAM_BLUE: playertank.Tank = Btank.Tank
	Case TEAM_YELLOW: playertank.Tank = Ytank.Tank
End Select

playertank\AI = False

EntityParent cam, 0
FreeEntity p

Global gpiv
gpiv = CreatePivot()

;Main Loop
HidePointer
MoveMouse 400,300
EqualizeSpeeds()
EqualizeSpeeds()
done = False
While Not done
	EqualizeSpeeds()
	UpdateWorld

	If playertank <> Null Then
		;Controls
		turn# = 0
		accelerate# = 0
		If KeyDown(208) Or KeyDown(31) Then accelerate = -1
		If KeyDown(200) Or KeyDown(17) Then accelerate = 1
		If KeyDown(203) Or KeyDown(30) Then turn = 1
		If KeyDown(205) Or KeyDown(32) Then turn = -1
		UpdateTank(playertank, turn#, accelerate#, MouseYSpeed()/5.0, -MouseXSpeed()/5.0, MouseDown(1))
		
		If KeyHit(1) Then playertank\AI = True: obmessage = 2: playertank = Null
		
		If KeyHit(15) Then
			tank.Tank = playertank
			playertank\AI = True
			Repeat
				tank = After tank
				If tank = Null Then tank = First Tank
				If tank\team = playerteam Then playertank = tank: Exit
			Forever
			playertank\AI = False
			playertank\leader = Null
		End If
		
		;If KeyHit(57) Then
			;commandscreen = 1 - commandscreen
			;FlushKeys()
		;End If
	End If
	
	;Advance through levels
	conquer = True
	For tank.Tank = Each Tank
		If tank\team <> playerteam Then conquer = False: Exit
	Next
	If conquer Then
		level = level + 1
		MakeTanks(TEAM_RED, 0, 0, level+1)
		MakeTanks(TEAM_GREEN, 60, 60, level+1)
		MakeTanks(TEAM_BLUE, 60, 0, level+1)
		MakeTanks(TEAM_YELLOW, 0, 60, level+1)
		advancemessage = MilliSecs() + 5000
		
		For tank.Tank = Each Tank
			tank\damage = 0
			tank\requestbackup = False
		Next
	End If

	
	;Camera
	If playertank = Null Then
		RotateEntity cam, EntityPitch(cam) + MouseYSpeed(), EntityYaw(cam) - MouseXSpeed(), 0
		If KeyDown(208) Or KeyDown(31) Then MoveEntity cam, 0, 0, -Eq(1)
		If KeyDown(200) Or KeyDown(17) Then MoveEntity cam, 0, 0, Eq(1)
		If KeyDown(203) Or KeyDown(30) Then MoveEntity cam, -Eq(1), 0, 0
		If KeyDown(205) Or KeyDown(32) Then MoveEntity cam, Eq(1), 0, 0

		If EntityY(cam) < .1 Then PositionEntity cam, EntityX(cam), .1, EntityZ(cam)
	Else
		PositionEntity cam, EntityX(playertank\mesh), EntityY(playertank\mesh)+1, EntityZ(playertank\mesh)
		;camyaw# = camyaw# - Eq((camyaw# - EntityYaw(playertank\turret, True)) * .3)
		camyaw# = EntityYaw(playertank\turret, True)
		RotateEntity cam, EntityPitch(playertank\turret, True), camyaw#, 0
		MoveEntity cam, 0, 0, -2
		If EntityY(cam) < .1 Then PositionEntity cam, EntityX(cam), .1, EntityZ(cam)
	End If
	
	MoveMouse 400,300
	
	;AI
	For tank.Tank = Each Tank
		UpdateAI(tank)
	Next
	
	;Update
	UpdateShells()
	UpdateFX()
	
	RenderWorld
	
	;Crosshairs
	x=400
	y=335
	Color 0,0,0
	Line x-10, y, x+10, y
	Line x, y-10, x, y+10
	;Color 0,0,0
	;Line 800-10, 600+1, 800+10, 600+1	
	;Line 800-1, 600-10, 800-1, 600+10	
	;Line 800-10, 600+1, 800+10, 600+1	
	;Line 800-1, 600-10, 800-1, 600+10	

	;Level
	SetFont big
	Color 0,0,0
	Text 700, 11, "Level: " + level
	Color 255,255,255
	Text 700, 10, "Level: " + level
	If Not Past(advancemessage) Then
		Color 0, 0, 0
		Text 521, 31, "Reinforcements Have Arrived!"
		Color 255, 0, 0
		Text 520, 30, "Reinforcements Have Arrived!"
	End If
	SetFont small
	
	;Score
	If cheat1 Or cheat2 Then
		score = 0
	End If
	SetFont big
	Color 0,0,0
	Text 10, 571, "Score: " + score
	Color 255,255,255
	Text 10, 570, "Score: " + score
	SetFont small
	
	;Pause
	If KeyHit(25) Then
		SetFont big
		ShowPointer
		While KeyHit(25) = False And KeyHit(1) = False
			RenderWorld
			Color 0,0,0
			Text 401, 301, "Game Paused", True, True
			Color 255,255,255
			Text 400, 300, "Game Paused", True, True
			Flip
			Delay 100
		Wend
		HidePointer
		SetFont small
	End If
	
	;FPS
	;SetFont small
	;Color 0,0,0
	;Text 700, 571, "MSecs: " + (MilliSecs() - lms)
	;Color 255,255,255
	;Text 700, 570, "MSecs: " + (MilliSecs() - lms)
	;lms = MilliSecs()
	;SetFont small
	
	;Damage
	If playertank <> Null Then
		SetFont big
		Color 0,0,0
		Text 10, 551, "Armor: " + 100*(1-playertank\damage)
		If playertank\damage > .6 Then Color 255,0,0 Else Color 255,255,255
		Text 10, 550, "Armor: " + 100*(1-playertank\damage)
		SetFont small
	End If

	;Command screen
	;If playertank <> Null And commandscreen = True Then
	If playertank <> Null Then

		;Color 0,0,0:		Text 201, 481, "Orders"
		;Color 255,255,255:	Text 200, 480, "Orders"
		
		Color 255,255,255
		Rect 200, 500, 295, 95, False
		Color 0,0,0
		Rect 201, 501, 293, 93, False
	
		Color 0,0,0:		Text 211, 461+50, "1. Request backup"
		Color 255,255,255:	Text 210, 460+50, "1. Request backup"
		
		Color 0,0,0:		Text 211, 481+50, "2. Follow me"
		Color 255,255,255:	Text 210, 480+50, "2. Follow me"
		
		Color 0,0,0:		Text 211, 501+50, "3. Spread out"
		Color 255,255,255:	Text 210, 500+50, "3. Spread out"
		
		Color 0,0,0:		Text 211, 521+50, "4. Hold position"
		Color 255,255,255:	Text 210, 520+50, "4. Hold position"
		
		
		Color 0,0,0:		Text 351, 461+50, "5. Retreat"
		Color 255,255,255:	Text 350, 460+50, "5. Retreat"
		
		Color 0,0,0:		Text 351, 481+50, "6. Attack!!!"
		Color 255,255,255:	Text 350, 480+50, "6. Attack!!!"
		
		Color 0,0,0:		Text 351, 501+50, "7. Patrol the area"
		Color 255,255,255:	Text 350, 500+50, "7. Patrol the area"
		
		Color 0,0,0:		Text 351, 521+50, "8. Move here"
		Color 255,255,255:	Text 350, 520+50, "8. Move here"

		If KeyHit(2) Then
			Select Rand(1,3)
			Case 1: TankSay(playertank, "I need backup.", False)
			Case 2: TankSay(playertank, "Requesting backup.", False)
			Case 3: TankSay(playertank, "Somebody help me out here!", False)
			End Select
			;playertank\requestbackup = True
			mindist# = 100000: mintank.Tank = Null
			For t.Tank = Each Tank
				xd# = EntityX(t\mesh) - EntityX(playertank\mesh)
				zd# = EntityZ(t\mesh) - EntityZ(playertank\mesh)
				If t\leader <> Null Then
					dist# = Sqr(xd*xd+zd*zd)*2
				Else
					dist# = Sqr(xd*xd+zd*zd)
				End If
				dist = dist + t\requestbackup*10
				If dist < mindist Then
					If t\team = playertank\team And t <> playertank Then
						If t\state <> STATE_FOLLOW Or t\leader <> playertank Then mindist = dist: mintank = t
					End If
				End If
			Next
			If mintank <> Null Then
				mintank\state = STATE_FOLLOW
				mintank\leader = playertank
				name$ = "Player"
				Select Rand(1,7)
				Case 1: TankSay(mintank, "I'll help you, "+name, True)
				Case 2: TankSay(mintank, "Wait for me, "+name, True)
				Case 3: TankSay(mintank, "Hold them off, "+name+" - I'm coming.", True)
				Case 4: TankSay(mintank, "Backup is on the way, "+name, True)
				Case 5: TankSay(mintank, name+", let me help you out", True)
				Case 6: TankSay(mintank, "I'm going to join you, "+name, True)
				Case 7: TankSay(mintank, "I'm on my way, "+name, True)
				End Select
				playertank\requestbackup = False
			Else
				playertank\requestbackup = True	;None availible right now - so put in a standard request for backup
			End If
		End If

		If KeyHit(3) Then
			Select Rand(1,3)
			Case 1: TankSay(playertank, "Get over here!", False)
			Case 2: TankSay(playertank, "Keep close to me.", False)
			Case 3: TankSay(playertank, "Follow me.", False)
			End Select
			For t.Tank = Each Tank
				If t\team = playerteam And t <> playertank Then
					t\leader = playertank
					t\state = STATE_FOLLOW
					t\timeout = MilliSecs()
				End If
			Next
		End If

		If KeyHit(4) Then
			Select Rand(1,3)
			Case 1: TankSay(playertank, "Spread out!", False)
			Case 2: TankSay(playertank, "Split up.", False)
			Case 3: TankSay(playertank, "Scatter!", False)
			End Select
			For t.Tank = Each Tank
				If t\team = playerteam And t <> playertank Then
					t\leader = Null
					t\timeout = MilliSecs()
				End If
			Next
		End If

		If KeyHit(5) Then
			Select Rand(1,4)
			Case 1: TankSay(playertank, "Hold position!", False)
			Case 2: TankSay(playertank, "Hold your ground!", False)
			Case 3: TankSay(playertank, "Stay where you are.", False)
			Case 4: TankSay(playertank, "Stay there and cover me!", False)
			End Select
			For t.Tank = Each Tank
				If t\team = playerteam And t <> playertank Then
					mindist# = 100000: mintank.Tank = Null
					For tank.Tank = Each Tank
						If tank\team <> playertank\team Then
							xd# = EntityX(tank\mesh) - EntityX(t\mesh)
							zd# = EntityZ(tank\mesh) - EntityZ(t\mesh)
							dist# = Sqr(xd*xd+zd*zd)
							If dist < mindist Then mindist = dist: mintank = tank
						End If
					Next
					t\state = STATE_SNIPE
					t\waypt_x = EntityX(t\mesh)
					t\waypt_z = EntityZ(t\mesh)
					t\target = mintank
					t\timeout = MilliSecs()
				End If
			Next
		End If

		If KeyHit(6) Then
			Select Rand(1,3)
			Case 1: TankSay(playertank, "RETREAT!", False)
			Case 2: TankSay(playertank, "Run for your lives!", False)
			Case 3: TankSay(playertank, "Lets get out of here!", False)
			End Select
			For t.Tank = Each Tank
				If t\team = playerteam And t <> playertank Then
					t\state = STATE_RETREAT
					t\timeout = MilliSecs()	
				End If
			Next
		End If

		If KeyHit(7) Then
			Select Rand(1,3)
			Case 1: TankSay(playertank, "CHARGE!!!", False)
			Case 2: TankSay(playertank, "Attack!", False)
			Case 3: TankSay(playertank, "Go get 'em!", False)
			End Select
			For t.Tank = Each Tank
				If t\team = playerteam And t <> playertank Then
					mindist# = 100000: mintank.Tank = Null
					For tank.Tank = Each Tank
						If tank\team <> playertank\team Then
							xd# = EntityX(tank\mesh) - EntityX(t\mesh)
							zd# = EntityZ(tank\mesh) - EntityZ(t\mesh)
							dist# = Sqr(xd*xd+zd*zd)
							If dist < mindist Then mindist = dist: mintank = tank
						End If
					Next
					t\state = STATE_HUNT
					t\target = mintank
					t\timeout = MilliSecs()
				End If
			Next
		End If

		If KeyHit(8) Then
			Select Rand(1,3)
			Case 1: TankSay(playertank, "Patrol the area.", False)
			Case 2: TankSay(playertank, "Scan for enemies.", False)
			Case 3: TankSay(playertank, "Comb the area for enemies!", False)
			End Select
			For t.Tank = Each Tank
				If t\team = playerteam And t <> playertank Then
					t\state = STATE_PATROL
					t\timeout = MilliSecs()
				End If
			Next
		End If

		If KeyHit(9) Then
			Select Rand(1,3)
			Case 1: TankSay(playertank, "Proceed to my position.", False)
			Case 2: TankSay(playertank, "Come here.", False)
			Case 3: TankSay(playertank, "Move to my position", False)
			End Select
			For t.Tank = Each Tank
				If t\team = playerteam And t <> playertank Then
					t\waypt_x = EntityX(playertank\mesh)
					t\waypt_z = EntityZ(playertank\mesh)
					t\state = STATE_RETREAT
					t\timeout = MilliSecs()
				End If
			Next
		End If

	End If

	;Chat window
	Color 255,255,255
	Rect 500, 450, 295, 145, False
	Color 0,0,0
	Rect 501, 451, 293, 143, False
	ch.ChatText = Last ChatText
	cnt=0
	For t.Tank = Each Tank
		t\textdrawn = False
	Next
	While ch <> Null
		If cnt > 8 Then
			Delete ch
		Else
			Color 0, 0, 0
			Text 511, 571-cnt*15, ch\name$ + ch\txt
			c = 300-cnt*30
			If c > 255 Then c = 255
			If Left(ch\name,3)="Red" Then Color c,0,0
			If Left(ch\name,5)="Green" Then Color 0,c,0
			If Left(ch\name,4)="Blue" Then Color 0,0,c
			If Left(ch\name,6)="Yellow" Then Color c,c,0
			If Left(ch\name,6)="Player" Then Color c,c,c
			If Left(ch\name,1)="-" Then Color c*.5,c*.5,c*.5
			Text 510, 570-cnt*15, ch\name$ + ch\txt
			cnt=cnt+1
			
			If Past(ch\displaytime)=False And ch\speaker <> Null Then
				xd# = EntityX(ch\speaker\mesh) - EntityX(cam)
				zd# = EntityZ(ch\speaker\mesh) - EntityZ(cam)
				dist# = Sqr(xd*xd+zd*zd)
				If dist < 15 And ch\speaker\textdrawn = False Then
					ch\speaker\textdrawn = True
					CameraProject cam, EntityX(ch\speaker\mesh), EntityY(ch\speaker\mesh)+.5, EntityZ(ch\speaker\mesh)
					xx = ProjectedX(): yy = ProjectedY()
					If xx = 0 And yy = 0 And ch\speaker = playertank Then xx = 400: yy = 300
					If xx <> 0 And yy <> 0 Then
						Color 0,0,0
						;width# = StringWidth(ch\txt)*.5: height# = StringHeight(ch\txt)*.5
						;Rect ProjectedX()-width-2, ProjectedY()-height-5-2, width*2+2, height*2+2, True
						Text xx+1, yy+1-5, Chr(34)+ch\txt+Chr(34), True, True
						Color 255,255,255
						Text xx, yy-5, Chr(34)+ch\txt+Chr(34), True, True
					End If
				End If
			End If

			ch = Before ch
		End If
	Wend

	;Score
	redtanks = 0
	greentanks = 0
	yellowtanks = 0
	bluetanks = 0
	For tank.Tank = Each Tank
		If tank\team = TEAM_RED Then redtanks = redtanks + 1
		If tank\team = TEAM_GREEN Then greentanks = greentanks + 1
		If tank\team = TEAM_BLUE Then bluetanks = bluetanks + 1
		If tank\team = TEAM_YELLOW Then yellowtanks = yellowtanks + 1
	Next
	
	Color 0,0,0
	Text 10, 11, "Red Team: " + redtanks + " Tanks"
	Color 255,0,0
	Text 10, 10, "Red Team: " + redtanks + " Tanks"
		
	Color 0,0,0
	Text 10, 31, "Green Team: " + greentanks + " Tanks"
	Color 0,255,0
	Text 10, 30, "Green Team: " + greentanks + " Tanks"

	Color 0,0,0
	Text 10, 51, "Blue Team: " + bluetanks + " Tanks"
	Color 0,0,255
	Text 10, 50, "Blue Team: " + bluetanks + " Tanks"

	Color 0,0,0
	Text 10, 71, "Yellow Team: " + yellowtanks + " Tanks"
	Color 255,255,0
	Text 10, 70, "Yellow Team: " + yellowtanks + " Tanks"
	
	If playertank = Null Then
		If obmessage = 0 Then
			Color 0,0,0
			Text 401, 31, "Your tank has been destroyed", True
			Color 255,255,255
			Text 400, 30, "Your tank has been destroyed", True
		Else
			Color 0,0,0
			Text 401, 31, "Observer Mode", True
			Color 255,255,255
			Text 400, 30, "Observer Mode", True
		End If
		
		found = False
		For tank.Tank = Each Tank
			If tank\team = playerteam Then found = True;: tank\leader = Null
		Next
		
		If found Then
			Color 0,0,0
			Text 401, 51, "Press [TAB] to switch to another tank", True
			Color 255,255,255
			Text 400, 50, "Press [TAB] to switch to another tank", True
		
			If KeyHit(15) Then
				obmessage = 0
				For tank.Tank = Each Tank
					If tank\team = playerteam Then playertank = tank: Exit
				Next
				playertank\AI = False
				playertank\leader = Null
			End If
		Else
			;Game over - exit the loop
			gameover = True
			done = True
		End If
		
		If KeyHit(1) Then done = True
	End If
	
	Flip True
Wend



;----------------- GAME OVER ------------------
	
LoadHighscores()
	
endscore = score

;Add to highscores
madeit = True
For sco.HighScore = Each HighScore
	madeit = False
	If score > sco\score Then
		madeitsco.HighScore = sco
		madeit = True
		Exit
	End If
Next
	
;If gameover Or madeit Then
	MoveMouse 400,300
	EqualizeSpeeds()
	While Not KeyHit(1)
		EqualizeSpeeds()
	
		;Camera controls
		RotateEntity cam, EntityPitch(cam) + MouseYSpeed(), EntityYaw(cam) - MouseXSpeed(), 0
		If KeyDown(208) Or KeyDown(31) Then MoveEntity cam, 0, 0, -Eq(1)
		If KeyDown(200) Or KeyDown(17) Then MoveEntity cam, 0, 0, Eq(1)
		If KeyDown(203) Or KeyDown(30) Then MoveEntity cam, -Eq(1), 0, 0
		If KeyDown(205) Or KeyDown(32) Then MoveEntity cam, Eq(1), 0, 0
		If EntityY(cam) < .1 Then PositionEntity cam, EntityX(cam), .1, EntityZ(cam)
		MoveMouse 400,300
	
		;AI
		For tank.Tank = Each Tank
			UpdateAI(tank)
		Next
		
		;Update
		UpdateShells()
		UpdateFX()
		
		UpdateWorld
		RenderWorld
		
		If gameover Then
			Color 0,0,0
			Text 401, 51, "Your team has been eliminated.", True
			Color 255,255,255
			Text 400, 50, "Your team has been eliminated.", True
			
			SetFont big
			Color 0,0,0
			Text 401, 301, "Game Over!", True, True
			Color 255,255,255
			Text 400, 300, "Game Over!", True, True
			SetFont small
		End If
		
		SetFont big
		Color 0,0,0
		Text 400, 330, "Score: " + endscore, True
		Color 255,255,255
		Text 400, 330, "Score: " + endscore, True
		SetFont small
		
		Color 0,0,0
		Text 401, 71, "Press [ESC] to exit.", True
		Color 255,255,255
		Text 400, 70, "Press [ESC] to exit.", True
		Color 0,0,0
		Text 401, 91, "Press [Enter] to play again.", True
		Color 255,255,255
		Text 400, 90, "Press [Enter] to play again.", True
		
		If KeyHit(28) Or KeyHit(156) Then
			For t.Tank = Each Tank
				TankDelete(t)
			Next
			For sh.Shell = Each Shell
				FreeEntity sh\mesh
				Delete sh
			Next
			For f.Frag = Each Frag
				FreeEntity f\mesh
				Delete f
			Next
			For f.Frag = Each Frag
				FreeEntity f\mesh
				Delete f
			Next
			For ch.ChatText = Each ChatText
				Delete ch
			Next
			For e.Explosion = Each Explosion
				For i = 1 To 4
					FreeEntity e\mesh[i]
				Next
				Delete e
			Next
			For h.HighScore = Each HighScore
				Delete h
			Next
			Goto playagain
		End If
	
		;Show highscores
		Color 0,0,0
		Text 21, 21, "High-Scores:"
		Color 255,255,255
		Text 20, 20, "High-Scores:"
		Color 0, 0, 0
		Rect 10,10,220,224,False
		Color 255, 255, 255
		Rect 11,11,218,222,False
		cnt = 1
		For sco.HighScore = Each HighScore
			If cnt > 10 Then
				Delete sco
			Else
				Color 0,0,0
				Text 21, 41 + cnt * 17, "#"+cnt+" - "+sco\name
				If madeitsco = sco And madeit = False Then Color 255,0,0 Else Color 255,255,255
				Text 20, 40 + cnt * 17, "#"+cnt+" - "+sco\name
				
				Color 0,0,0
				Text 171, 41 + cnt * 17, sco\score
				If madeitsco = sco And madeit = False Then Color 255,0,0 Else Color 255,255,255
				Text 170, 40 + cnt * 17, sco\score
				
				If cnt <> 10 Then
					Color 0,0,0
					Line 21, 57 + cnt * 17, 216, 57 + cnt * 17
					If madeitsco = sco And madeit = False Then Color 255,0,0 Else Color 255,255,255
					Line 20, 56 + cnt * 17, 215, 56 + cnt * 17
				End If
			End If
			
			cnt = cnt + 1
		Next
		
		;New highscore
		If madeit Then
			Nsco.HighScore = New HighScore
			If madeitsco <> Null Then Insert Nsco Before madeitsco
			madeitsco = Nsco
			Nsco\score = score
			Color 0,0,0
			Text 261, 131, "Congratulations! You made it into the top 10 highscores!"
			Color 255,255,255
			Text 260, 130, "Congratulations! You made it into the top 10 highscores!"
			Locate 260, 150
			FlushKeys()
			Nsco\name = Input("Enter your name: ")
			FlushKeys()
			SaveHighScores()
			madeit = False
		End If
		
		Flip
	Wend
	
;End If

End


Function TankSay(tank.Tank, txt$, private, colon = True)
	If private = True And tank\team <> playerteam Then Return
	ch.ChatText = New ChatText
	ch\displaytime = MilliSecs() + 3000*timerdir
	ch\txt = txt
	ch\speaker = tank
	If tank = playertank Then
		If colon Then ch\name$ = "Player: " Else ch\name$ = "- Player "
	Else
		If colon Then
			Select tank\team
			Case TEAM_RED
				ch\name$ = "Red Tank #"+tank\id+": "
			Case TEAM_GREEN
				ch\name$ = "Green Tank #"+tank\id+": "
			Case TEAM_BLUE
				ch\name$ = "Blue Tank #"+tank\id+": "
			Case TEAM_YELLOW
				ch\name$ = "Yellow Tank #"+tank\id+": "
			End Select
		Else
		Select tank\team
			Case TEAM_RED
				ch\name$ = "- Red tank #"+tank\id+" "
			Case TEAM_GREEN
				ch\name$ = "- Green tank #"+tank\id+" "
			Case TEAM_BLUE
				ch\name$ = "- Blue tank #"+tank\id+" "
			Case TEAM_YELLOW
				ch\name$ = "- Yellow tank #"+tank\id+" "
			End Select
		End If
	End If
End Function



Function UpdateTank(tank.Tank, turn#, accelerate#, turretx#, turrety#, fire)
	If turn > 1 Then turn = 1
	If turn < -1 Then turn = -1
	tank\Yturn = tank\Yturn + Eq(turn*1.7)
	tank\Yturn = tank\Yturn - Eq(Sgn(tank\Yturn)*.5)
	If tank\Yturn > 5 Then tank\Yturn = 5
	If tank\Yturn < -5 Then tank\Yturn = -5
	
	If accelerate > 1 Then accelerate = 1
	If accelerate < -1 Then accelerate = -1
	tank\speed = tank\speed + Eq(accelerate*1)
	tank\speed = tank\speed - Eq(Sgn(tank\speed)*.5)
	If tank\speed > 9 Then tank\speed = 9
	If tank\speed < -9 Then tank\speed = -9
	
	tank\speed = tank\speed - Eq(Sgn(tank\speed)*Abs(tank\Yturn)*.1)
	
	newpitch# = EntityPitch(tank\turret) + turretx
	If newpitch > 1 Then newpitch = 1
	RotateEntity tank\turret, newpitch#, EntityYaw(tank\turret) + turrety, 0
	
	RotateEntity tank\mesh, 0, EntityYaw(tank\mesh) + Eq(tank\Yturn), 0
	MoveEntity tank\mesh, 0, -Eq(1), Eq(tank\speed * .06)
	If EntityY(tank\mesh) < 0 Then PositionEntity tank\mesh, EntityX(tank\mesh), 0, EntityZ(tank\mesh)
	
	If fire Then
		If Past(tank\firetime) Then
			tank\missx = Rnd(-2,2): tank\missy = Rnd(-5,5)	;AI accuracy
			
			If cheat1 Then
				If tank = playertank Then tank\firetime = MilliSecs() + 100*timerdir Else tank\firetime = MilliSecs() + 1000*timerdir
			Else
				tank\firetime = MilliSecs() + 1000*timerdir
			End If
			shell.Shell = New Shell
			shell\owner = tank
			shell\mesh = CreateCube()
			EntityType shell\mesh, COLLISION_SHELL
			EntityRadius shell\mesh, .1
			ScaleEntity shell\mesh, .05, .05, .2
			EntityColor shell\mesh, 0, 0, 0
			RotateEntity gpiv, EntityPitch(tank\turret, True), EntityYaw(tank\turret, True), 0
			MoveEntity gpiv, 0, 0, 4
			shell\xv = EntityX(gpiv)
			shell\yv = EntityY(gpiv)
			shell\zv = EntityZ(gpiv)
			PositionEntity gpiv, 0, 0, 0
			PositionEntity shell\mesh, EntityX(tank\turret, True), EntityY(tank\turret, True), EntityZ(tank\turret, True)
			RotateEntity shell\mesh, EntityPitch(tank\turret, True), EntityYaw(tank\turret, True), 0
			MoveEntity shell\mesh, 0, .23, .60
		
			;ResetEntity tank\mesh
			;ResetEntity tank\turret
			ResetEntity shell\mesh
		End If
	End If
End Function



Function Past(time, offset=0)
	If timerdir = 1 Then Return (MilliSecs() > time+offset)
	If timerdir = -1 Then Return (MilliSecs() < time-offset)
End Function



Function UpdateShells()
	For i.Shell = Each Shell
		TranslateEntity i\mesh, Eq(i\xv), Eq(i\yv), Eq(i\zv)
		RotateEntity i\mesh, VectorPitch(i\xv, i\yv, i\zv), VectorYaw(i\xv, i\yv, i\zv), 0
		i\yv = i\yv - Eq(.2)
		collided = 0
		If CountCollisions(i\Mesh) > 0 Then collided = CollisionEntity(i\Mesh, 1)
		If EntityY(i\mesh) < 0 Then collided = -1
		If collided <> 0 Then
			tank.Tank = Null
			dontexplode = False
			For t.Tank = Each Tank
				If collided = t\mesh Or collided = t\turret Then
					ResetEntity i\mesh
					dontexplode = True
					;Don't allow AI bots to shoot team members
					If i\owner <> Null Then If t\team <> i\owner\team Then
					
						;Apply amage
						If cheat2 Then
							If t = playertank Then t\damage = t\damage + .05 Else t\damage = t\damage + .25
						Else
							t\damage = t\damage + .25
						End If
						tank = t
						If i\owner <> Null Then t\target = i\owner	;Defend self
						If t\damage > .5 And t <> playertank Then
							Select Rand(1,7)
							Case 1: TankSay(t, "I'm taking heavy damage!", False)
							Case 2: TankSay(t, "AAAAAAH!", False)
							Case 3: TankSay(t, "I need backup!", False)
							Case 4: TankSay(t, "My tank can't take much more of this!", False)
							Case 5: TankSay(t, "Noooo!", False)
							Case 6: TankSay(t, "Requesting backup.", False)
							Case 7: TankSay(t, "Somebody help me out here!", False)
							End Select
							If t\leader <> playertank Or playertank = Null Then t\state = STATE_RETREAT
							t\requestbackup = True
						Else
							If t\leader <> playertank Or playertank = Null Then t\state = STATE_HUNT
						End If
						
						;Scoring
						If i\owner\team = playerteam Then score = score + 5
						If i\owner = playertank Then score = score + 25
						
						;Explode tank
						If t\damage >= 1 Then
							TankSay(t, "has been destroyed.", False, False)
							If i\owner <> Null Then
								If Rand(1,2) = 1 Then
									Select Rand(1,5)
									Case 1: TankSay(i\owner, "Did you see that shot?", False)
									Case 2: TankSay(i\owner, "I got him!", False)
									Case 3: TankSay(i\owner, "I didn't miss that time!", False)
									Case 4: TankSay(i\owner, "He didn't even know what hit him.", False)
									Case 5: TankSay(i\owner, "Bulls-eye!", False)
									End Select
								End If
							End If
							Explosion(EntityX(i\mesh), EntityY(i\mesh), EntityZ(i\mesh))
							Select t\team
								Case TEAM_RED
									Fragments(EntityX(t\mesh), EntityY(t\mesh), EntityZ(t\mesh), 50, 255,0,0, .6)
								Case TEAM_GREEN
									Fragments(EntityX(t\mesh), EntityY(t\mesh), EntityZ(t\mesh), 50, 0,255,0, .6)
								Case TEAM_BLUE
									Fragments(EntityX(t\mesh), EntityY(t\mesh), EntityZ(t\mesh), 50, 0,0,255, .6)
								Case TEAM_YELLOW
									Fragments(EntityX(t\mesh), EntityY(t\mesh), EntityZ(t\mesh), 50, 255,255,0, .6)
							End Select
							
							;Scoring
							If i\owner\team = playerteam Then score = score + 100
							If i\owner = playertank Then score = score + 1000
							
							TankDelete(t)
							collided = -1
							tank = Null
						End If
						dontexplode = False
						Exit
					End If
				End If
			Next
			If Not dontexplode Then
				TranslateEntity i\mesh, Eq(-i\xv), Eq(-i\yv), Eq(-i\zv)
				If tank = Null And collided <> -1 Then
					MoveEntity collided, 0, -1, 0
					If EntityY(collided) <= .5 Then HideEntity collided
					Fragments(EntityX(i\mesh), EntityY(i\mesh), EntityZ(i\mesh), 20, 255,255,255, .5)
				Else
					If collided = -1 Then
						Fragments(EntityX(i\mesh), .2, EntityZ(i\mesh), 3, 151,103,41, .4)
					Else
						t = tank
						Select t\team
							Case TEAM_RED
								Fragments(EntityX(t\mesh), EntityY(t\mesh), EntityZ(t\mesh), 5, 255,0,0, .3)
							Case TEAM_GREEN
								Fragments(EntityX(t\mesh), EntityY(t\mesh), EntityZ(t\mesh), 5, 0,255,0, .3)
							Case TEAM_BLUE
								Fragments(EntityX(t\mesh), EntityY(t\mesh), EntityZ(t\mesh), 5, 0,0,255, .3)
							Case TEAM_YELLOW
								Fragments(EntityX(t\mesh), EntityY(t\mesh), EntityZ(t\mesh), 5, 255,255,0, .3)
						End Select
					End If
				End If
				FreeEntity i\mesh
				Delete i
			End If
		End If
	Next
End Function



Function Explosion(x#, y#, z#)
	ex.Explosion = New Explosion
	For i = 1 To 4
		ex\mesh[i] = CreateSphere()
		PositionEntity ex\mesh[i], x, y, z
		EntityFX ex\mesh[i], 1+16
		EntityShininess ex\mesh[i], 1
		Select i
			Case 1: EntityColor ex\mesh[i], 255, 0, 0
			Case 2: EntityColor ex\mesh[i], 255, 128, 0
			Case 3: EntityColor ex\mesh[i], 255, 255, 0
			Case 4: EntityColor ex\mesh[i], 255, 255, 0
		End Select
		ex\life[i] = 1
	Next
End Function

Function Fragments(x#, y#, z#, amount, r, g, b, intensity#)
	For i = 1 To amount
		ex.Frag = New Frag
		
		ex\mesh = CopyEntity(fragment(Rand(0,10)))
		EntityType ex\mesh, 0
		
		ScaleEntity ex\mesh, .3, .3, .3
		RotateEntity ex\mesh, Rnd(-180,180), Rnd(-180,180), Rnd(-180,180)
		PositionEntity ex\mesh, Rnd(x-.2,x+.2), Rnd(y-.2,y+.2), Rnd(z-.2,z+.2)
		ex\rx = Rnd(30,-30)
		ex\ry = Rnd(30,-30)
		ex\rz = Rnd(30,-30)
		ex\dx = Rnd(1,-1) * intensity#
		ex\dy = Rnd(1,0) * intensity#
		ex\dz = Rnd(1,-1) * intensity#
		c = Rnd(0,-55)
		EntityColor ex\mesh, r+c,g+c,b+c
		EntityFX ex\mesh, 1+16
		ex\life = 1
	Next
End Function



Function UpdateFX()
	For i.Explosion = Each Explosion
		For o = 1 To 4
			i\life[o] = i\life[o] - Eq(.2)
			If i\life[o] < 0 Then
				If i\mesh[o] <> 0 Then FreeEntity i\mesh[o]: i\mesh[o] = 0: del = True
			Else
				s# = (1-i\life[o]) * o * .3 + .3
				ScaleEntity i\mesh[o], s, s, s
				EntityAlpha i\mesh[o], i\life[o]
			End If	
		Next
	Next
	If del Then Delete i
	For x.Frag = Each Frag
		x\dy = x\dy - Eq(.1)
		RotateEntity x\mesh, EntityPitch(x\mesh)+Eq(x\rx), EntityYaw(x\mesh)+Eq(x\ry), EntityRoll(x\mesh)+Eq(x\rz)
		y# = EntityY(x\mesh)+Eq(x\dy)
		If y<.01 Then y=.01: x\dy = -.3 * x\dy;RotateEntity x\mesh, 0, 0, 0: x\dx = 0: x\dy = 0: x\dz = 0
		PositionEntity x\mesh, EntityX(x\mesh)+Eq(x\dx), y#, EntityZ(x\mesh)+Eq(x\dz)
		x\life = x\life - Eq(.1)
		If x\life < 0 Then
			FreeEntity x\mesh
			Delete x
		Else
			EntityAlpha x\mesh, x\life
		End If
	Next
End Function



Function TankDelete(tank.Tank)
	FreeEntity tank\mesh
	Delete tank
End Function


Function UpdateAI(tank.Tank)
	If tank\AI = False Then Return
	
	;Aim at target
	If tank\target <> Null Then
		xd# = EntityX(tank\mesh) - EntityX(tank\target\mesh)
		zd# = EntityZ(tank\mesh) - EntityZ(tank\target\mesh)
		dist# = Sqr(xd*xd+zd*zd)
		yaw# = VectorYaw(xd, 0, zd) + tank\missy
		pitch# = (-Sqr(dist) * 1) + tank\missx
		
		dyaw# = (EntityYaw(tank\turret, True) - yaw) + 180
		While dyaw > 180
			dyaw= dyaw - 360
		Wend
		While dyaw < -180
			dyaw = dyaw + 360
		Wend
		turrety# = -dyaw
		If turrety# > 10 Then turrety# = 10
		If turrety# < -10 Then turrety# = -10

		dpitch# = (EntityPitch(tank\turret, True) - pitch)
		While dpitch > 180
			dpitch = dpitch - 360
		Wend
		While dpitch < -180
			dpitch = dpitch + 360
		Wend
		
		turretx# = -dpitch
		If turretx# > 10 Then turretx# = 10
		If turretx# < -10 Then turretx# = -10
		
		;Fire!
		If Abs(dyaw) < 10 Then fire = True

	End If
	
	;Follow waypoint
	xd# = EntityX(tank\mesh) - tank\waypt_x
	zd# = EntityZ(tank\mesh) - tank\waypt_z
	dist# = Sqr(xd*xd+zd*zd)
	
	If dist > 2 Then
		dir# = VectorYaw(xd, 0, zd)
		dirdist# = (EntityYaw(tank\mesh) - dir) + 180
		While dirdist > 180
			dirdist = dirdist - 360
		Wend
		While dirdist < -180
			dirdist = dirdist + 360
		Wend
		If dirdist < 0 Then turn# = 1
		If dirdist > 0 Then turn# = -1
	
		If Abs(dirdist) < 10+tank\waypt_dist Then accelerate# = 1
	End If
	
	tank\AIframecount = tank\AIframecount + 1
	If tank\AIframecount > 10 Then
	tank\AIframecount = 0
	
	;Behaviour
	Select tank\state
		Case STATE_PATROL
			tank\requestbackup = False
			;Check if anyone needs help
			mindist# = 100000: mintank.Tank = Null
			For t.Tank = Each Tank
				xd# = EntityX(t\mesh) - EntityX(tank\mesh)
				zd# = EntityZ(t\mesh) - EntityZ(tank\mesh)
				dist# = Sqr(xd*xd+zd*zd)
				If t\team = tank\team And t <> tank And t\requestbackup = True Then mindist = dist: mintank = t
			Next
			If mintank <> Null Then
				tank\state = STATE_FOLLOW
				tank\leader = mintank
				If tank\leader = playertank Then
					name$ = "Player"
				Else
					name$ = "Tank "+tank\leader\id
				End If
				Select Rand(1,7)
				Case 1: TankSay(tank, "I'll help you, "+name, True)
				Case 2: TankSay(tank, "Wait for me, "+name, True)
				Case 3: TankSay(tank, "Hold them off, "+name+" - I'm coming.", True)
				Case 4: TankSay(tank, "Backup is on the way, "+name, True)
				Case 5: TankSay(tank, name+", let me help you out", True)
				Case 6: TankSay(tank, "I'm going to join you, "+name, True)
				Case 7: TankSay(tank, "I'm on my way, "+name, True)
				End Select
				If Rand(1,3) = 1 Then tank\leader\requestbackup = False
			End If
			;Next waypoint
			If dist <= 4 Then
				tank\waypt_x = Rnd(0,60)
				tank\waypt_z = Rnd(0,60)
				tank\waypt_dist = 1
			End If
			;Obstacles
			If CountCollisions(tank\mesh) > 0 Then
				;fire = True
				tank\waypt_x = tank\waypt_x + Rnd(-5,5)
				tank\waypt_z = tank\waypt_z + Rnd(-5,5)
			End If
			range# = 20
			;Timeout - change state
			If Past(tank\timeout,5000) Then
				;Choose a leader or choose an enemy
				If Rand(1,3) = 1 And tank\leader = Null Then
					mindist# = 100000: mintank.Tank = Null
					For t.Tank = Each Tank
						If t\team = tank\team Then
							xd# = EntityX(t\mesh) - EntityX(tank\mesh)
							zd# = EntityZ(t\mesh) - EntityZ(tank\mesh)
							dist# = Sqr(xd*xd+zd*zd)
							If t\leader <> tank And t <> tank Then mindist = dist: mintank = t
						End If
					Next
					tank\leader = mintank
					If tank\leader = Null Then
						range = 10000
						tank\timeout = MilliSecs()+Rand(-1000,1000)
					Else
						tank\timeout = MilliSecs()+Rand(-1000,1000)
						tank\state = STATE_FOLLOW
						If tank\leader = playertank Then
							name$ = "Player"
						Else
							name$ = "Tank "+tank\leader\id
						End If
						Select Rand(1,6)
						Case 1: TankSay(tank, "I'll help you, "+name, True)
						Case 2: TankSay(tank, "Wait for me, "+name, True)
						Case 3: TankSay(tank, "Hold them off, "+name+" - I'm coming.", True)
						Case 4: TankSay(tank, "Backup is on the way, "+name, True)
						Case 5: TankSay(tank, name+", let me help you out", True)
						Case 6: TankSay(tank, "I'm going to join you, "+name, True)
						End Select
					End If
				Else
					range = 10000
					tank\timeout = MilliSecs()+Rand(-1000,1000)
				End If
			End If
			;Attack near enemies
			mindist# = 100000: mintank.Tank = Null
			For t.Tank = Each Tank
				If t\team <> tank\team Then
					xd# = EntityX(t\mesh) - EntityX(tank\mesh)
					zd# = EntityZ(t\mesh) - EntityZ(tank\mesh)
					dist# = Sqr(xd*xd+zd*zd)
					If dist < mindist Then mindist = dist: mintank = t
				End If
			Next
			If mindist < range Then
				If tank\damage < .5 And Rand(1,4) <> 1 Then
					x = Rand(1,2)
					Select x
						Case 1: tank\state = STATE_HUNT
						Case 2: tank\state = STATE_SNIPE
						Case 3: tank\state = STATE_RETREAT
					End Select
					tank\target = mintank
				Else
					tank\state = STATE_RETREAT
					tank\target = mintank
				End If
			End If
			
		Case STATE_HUNT
			If Past(tank\timeout,15000) Then
				tank\state = STATE_PATROL
				tank\timeout = MilliSecs()+Rand(-1000,1000)
				Select Rand(1,3)
				Case 1: TankSay(tank, "Where did that guy go?", False)
				Case 2: TankSay(tank, "I lost him!", False)
				Case 3: TankSay(tank, "He got away.", False)
				Case 4: TankSay(tank, "Breaking off.", False)
				End Select
			End If
			If tank\target = Null Then
				tank\state = STATE_PATROL
			Else
				tank\waypt_x = EntityX(tank\target\mesh)
				tank\waypt_z = EntityZ(tank\target\mesh)
				tank\waypt_dist = 10
			End If
			
		Case STATE_FOLLOW
			If tank\leader = Null Then
				tank\state = STATE_PATROL
				Select Rand(1,7)
				Case 1: TankSay(tank, "Oh no.", False)
				Case 2: TankSay(tank, "This doesn't look good.", False)
				Case 3: TankSay(tank, "Retreat!!", False)
				Case 4: TankSay(tank, "Run for your lives!", False)
				Case 5: TankSay(tank, "We're doomed!", False)
				Case 6: TankSay(tank, "Spread out and get them!", False)
				Case 7: TankSay(tank, "Uh-oh", False)
				End Select
			Else
				If Past(tank\timeout, 15000) And (playertank = Null Or tank\leader <> playertank) Then
					tank\leader = Null
					tank\state = STATE_PATROL
					tank\timeout = MilliSecs()+Rand(-1000,1000)
					Select Rand(1,5)
					Case 1: TankSay(tank, "You're on you're own - I'm leaving.", True)
					Case 2: TankSay(tank, "Falling back", True)
					Case 3: TankSay(tank, "I'm getting out of here!", True)
					Case 4: TankSay(tank, "I'm going to go.", True)
					Case 5: TankSay(tank, "Breaking out of formation.", True)
					End Select
				End If
				If tank\state = STATE_FOLLOW Then
					mindist# = 100000: mintank.Tank = Null
					range# = 20
					For t.Tank = Each Tank
						If t\team <> tank\team Then
							xd# = EntityX(t\mesh) - EntityX(tank\mesh)
							zd# = EntityZ(t\mesh) - EntityZ(tank\mesh)
							dist# = Sqr(xd*xd+zd*zd)
							If dist < mindist Then mindist = dist: mintank = t
						End If
					Next
					If tank\target = Null Then
						If mindist < range Then
							tank\target = mintank
						End If
					Else
						xd# = EntityX(tank\target\mesh) - EntityX(tank\mesh)
						zd# = EntityZ(tank\target\mesh) - EntityZ(tank\mesh)
						dist# = Sqr(xd*xd+zd*zd)
						If dist > range * 2 Then
							tank\target = mintank
						End If
					End If
					tank\waypt_x = EntityX(tank\leader\mesh)
					tank\waypt_z = EntityZ(tank\leader\mesh)
					tank\waypt_dist = 100
				End If
			End If
			
		Case STATE_RETREAT
			If Past(tank\timeout,10000) Then
				tank\state = STATE_PATROL
				tank\timeout = MilliSecs()+Rand(-1000,1000)
			End If
			If dist <= 2 Then
				tank\waypt_x = Rnd(0,60)
				tank\waypt_z = Rnd(0,60)
				tank\waypt_dist = 1
			End If
			If tank\target = Null Then
				tank\state = STATE_PATROL
			End If
		
		Case STATE_SNIPE
			If Past(tank\timeout,10000) And (playertank = Null Or tank\leader <> playertank) Then
				tank\state = STATE_PATROL
				tank\timeout = MilliSecs()+Rand(-1000,1000)
			End If
			If tank\target = Null Then
				If  playertank = Null Or tank\leader <> playertank Then
					tank\state = STATE_PATROL
				Else
					mindist# = 100000: mintank.Tank = Null
					For t.Tank = Each Tank
						If t\team <> tank\team Then
							xd# = EntityX(t\mesh) - EntityX(tank\mesh)
							zd# = EntityZ(t\mesh) - EntityZ(tank\mesh)
							dist# = Sqr(xd*xd+zd*zd)
							If dist < mindist Then mindist = dist: mintank = t
						End If
					Next
					tank\target = mintank			
				End If
			End If
			
		Default
			RuntimeError "Illeagle AI state"
	End Select
	
	End If ;AIframecount
	
	;Update
	UpdateTank(tank, turn#, accelerate#, Eq(turretx#), Eq(turrety#), fire)
End Function


Function MakeCity(xp#,zp#,xt,zt)
	For x = 1 To xt
	For z = 1 To zt
		If Rand(0,10)>3 Then
			building = CreateCube()
			c = Rnd(100,255)
			EntityColor building, c, c, c
			ys# = Rnd(1,4)
			ScaleMesh building, Rnd(.5,1), ys#, Rnd(.5,1)
			PositionMesh building, 0, -ys, 0
			PositionEntity building, x*4 + xp, 2*ys, z*4 + zp
			EntityType building, COLLISION_TARGET	
			EntityTexture building, house
		End If
	Next
	Next
End Function




Function MakeTank.Tank(team)
	tank.Tank = New Tank
	tank\mesh = CreateCube(): EntityTexture tank\mesh, metal
	ScaleMesh tank\mesh, .3, .1, .5
	PositionMesh tank\mesh, 0, .1, 0
	tank\turret = CreateCylinder(6, True, tank\mesh): EntityTexture tank\turret, metal
	ScaleMesh tank\turret, .2, .1, .2
	PositionMesh tank\turret, 0, .25, 0
	tmp = CreateCylinder(3, True, tank\mesh): EntityTexture tank\turret, metal
	RotateMesh tmp, 90, 0, 0
	ScaleMesh tmp, .03, .03, .5
	PositionMesh tmp, 0, .25, .32
	EntityColor tmp, 100, 100, 100
	;AddMesh tmp, tank\turret
	EntityParent tmp, tank\turret
	EntityColor tank\turret, 0, 0, 0
	PositionEntity tank\mesh, 0, 0, 5
	
	tmp = CreateCube(tank\mesh): EntityTexture tank\turret, metal
	ScaleEntity tmp, .2, .1, .1
	PositionEntity tmp, 0, 0, .5
	EntityColor tmp, 0,0,0

	;EntityType tank\mesh, COLLISION_TARGET
	EntityType tank\mesh, COLLISION_TANK
	EntityRadius tank\mesh, .8

	tank\state = STATE_PATROL
	tank\AI = True
	tank\waypt_x = Rnd(0,60)
	tank\waypt_z = Rnd(0,60)
	tank\timeout = MilliSecs()
	
	tank\team = team
	
	cnt=0
	For i.Tank = Each Tank
		If i\team = tank\team Then cnt = cnt + 1
	Next
	tank\id = cnt
	
	Return tank
End Function



Function MakeTanks.Tank(team, x#, z#, tanks)
	p = CreatePivot()
	PositionEntity p, 30, 0, 30
	For i = 1 To tanks
		tank.Tank = MakeTank(team)
		Select team
			Case TEAM_RED: EntityColor tank\mesh, 255,0,0
			Case TEAM_GREEN: EntityColor tank\mesh, 0,255,0
			Case TEAM_BLUE: EntityColor tank\mesh, 0,0,255
			Case TEAM_YELLOW: EntityColor tank\mesh, 255,255,0
		End Select
		PositionEntity tank\mesh, x+i*1.5, 0, z
		PointEntity tank\mesh, p
		ResetEntity tank\mesh
	Next
	FreeEntity p
	Return tank
End Function



Function SaveHighscores()
	file = WriteFile("highscores.dat")
	For x.HighScore = Each HighScore
		WriteString file, x\name
		WriteInt file, x\score
	Next
	CloseFile file
End Function

Function LoadHighscores()
	If FileType("highscores.dat") = 0 Then Return
	file = ReadFile("highscores.dat")
	While Not Eof(file)
		x.HighScore = New HighScore
		x\name = ReadString(file)
		x\score = ReadInt(file)
	Wend
	CloseFile file
End Function

;Equalization
Global prevmillisecs, timestep#

Function EqualizeSpeeds()
	ms = MilliSecs()
	If prevmillisecs > ms Then timerdir = -1 Else timerdir = 1
	timestep# = (ms - prevmillisecs) * .01
	If timestep > 2 Then timestep = 0
	If timestep < 0 Then timestep = 0
	prevmillisecs = ms
End Function

Function Eq#(speed#)
	Return timestep * speed
End Function
