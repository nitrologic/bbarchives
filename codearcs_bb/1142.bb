; ID: 1142
; Author: Jeremy Alessi
; Date: 2004-08-22 15:01:05
; Title: FPSNet
; Description: A NETWORKED FPS Example

;====== FPSNet: A NETWORKED FIRST PERSON SHOOTER EXAMPLE ==================
;====== UTILIZING ONLY BUILT IN BLITZ3D FEATURES ==========================
;====== A CODESKETCH BY JEREMY ALESSI =====================================
;====== THANKS MARK SIBLY FOR GNET ========================================

;====== WASD OR CURSORS MOVE, MOUSE LOOKS, LEFT MOUSE BUTTON SHOOTS =======
;====== SPACE BAR OR 0 ON NUMPAD JUMPS ====================================
;====== PRESS 2 TO UNLOCK MOUSE FROM CENTER (USEFUL TO PLAY ===============
;====== AND ALSO BE ABLE TO MULTITASK IF NEED BE) PRESS 1 TO RELOCK =======
;====== MOUSE BACK TO CENTER OF SCREEN ====================================
;====== YEAH EVERYONE HAS THE SOURCE ... DON'T CHEAT! =====================


;====== INCLUDES ==========================================================

;Include "gnet_inc.bb" This is included at the bottom of this source!

;==========================================================================



;====== GRAPHICS ==========================================================

Graphics3D(640, 480, 16, 2)
SetBuffer BackBuffer()
HidePointer

;==========================================================================



;====== COLLISIONS ========================================================

Global BODY = 1
Global SCENE = 2
Collisions(BODY,SCENE, 2, 2)
Collisions(BODY,BODY, 2, 2)

;==========================================================================



;====== GLOBALS ===========================================================

Global gameState$ = "Title Screen"

;Mousehit Vars
Global mb1pressed, mb2pressed

;Keyhit Vars
Global upKeyPressed, downKeyPressed, leftKeyPressed, rightKeyPressed, enterKeyPressed, spaceKeyPressed

;Font Vars
Global font, bigFont

;Menu Var
Global menuCount

;Server Vars
Global serverCount
Global serverPointer

;Local Player Network Vars
Global localPlayerName$, localPlayerPointer

;Scenery Vars
Global level,copySprite

;Camera Var
Global camera

;Player Gameplay Vars
Global health#, shot, shotTimer#, positionUpdateTimer#, serverRefreshTimer#, hosting
Global vY#,centerMouse = True

;==========================================================================



;====== INCOMING ==========================================================

Dim incoming$(100)

;==========================================================================



;====== TYPES =============================================================

Type Player

	Field iD, name$, mesh[10], score
	
End Type

Type particle
	
	Field mesh, name$, alpha#
	Field vX#, vY#, vZ#
	
End Type

;==========================================================================



;====== INITIAL SETUP =====================================================

Setup("Title Screen")

;==========================================================================



;==========================================================================
.Main
;====== RUN ===============================================================
period=1000 / 30
tweentime = MilliSecs()-period
While 1
;====== CALCULATE TWEEN ===================================================
	Repeat
		elapsed = MilliSecs() - tweentime
	Until elapsed
	ticks = elapsed/period
	tween# = Float(elapsed Mod period) / Float(period)
;==========================================================================

;====== MAIN LOOP =========================================================
	For k=1 To ticks
		tweentime = tweentime+period
		If k = ticks Then CaptureWorld
		If KeyDown(1)
			If Not GNET_RemoveServer("FPSNet")
				If hosting = True
					RuntimeError "Failed to remove server"
				EndIf
			EndIf
			ClearWorld()
			End
		EndIf
		UpdateLogic()
		UpdateWorld
	Next
	RenderWorld(tween#)
	Draw()
	Flip
Wend
;===========================================================================



;====== UPDATE LOGIC =======================================================

Function UpdateLogic()
	
	Select gameState$
	
		Case "Title Screen"
			UpdateInput()
			UpdateTitleScreen()
		Case "Game"
			UpdateLights()
			UpdateCamera()
			UpdatePlayerMovement()
			UpdateParticles()
			UpdateNetwork()
			UpdateCenterMouse()
	End Select
	
End Function

;===========================================================================



;====== DRAW ===============================================================

Function Draw()

	Select gameState$
	
		Case "Title Screen"
			DrawTitleScreen()
		Case "Game"
			DrawGamePlay()
		
	End Select
	
End Function

;===========================================================================



;====== UPDATE INPUT =======================================================

Function UpdateInput()

	mb1pressed = MouseHit(1)
	
	mb2pressed = MouseHit(2)
	
	upKeyPressed = KeyHit(17) Or KeyHit(200)
	
	downKeyPressed = KeyHit(31) Or KeyHit(208)
	
	leftKeyPressed = KeyHit(30) Or KeyHit(203)
	
	rightKeyPressed = KeyHit(32) Or KeyHit(205)
	
	enterKeyPressed = KeyHit(28)
	
	spaceKeyPressed = KeyHit(57)
	
		
End Function

;===========================================================================



;====== UPDATE CENTER MOUSE ================================================

Function UpdateCenterMouse()

	If centerMouse = True
		MoveMouse(CorrectX(320), CorrectY(240))
	EndIf
	If KeyHit(2)
		centerMouse = True
	EndIf
	If KeyHit(3)
		centerMouse = False
	EndIf

End Function

;===========================================================================



;====== UPDATE TITLE SCREEN ================================================

Function UpdateTitleScreen()

	If upKeyPressed And menuCount > 0
		menuCount = menuCount - 1
	EndIf
	
	If downKeyPressed And menuCount < serverCount - 1
		menuCount = menuCount + 1
	EndIf
	
	If enterKeyPressed
		gns.GNET_Server = Object.GNET_Server(serverPointer)
		If JoinNetGame(gns\server$, gns\ip$)
			FlushKeys
			Color(255,0,0)
			Locate(200,130)
			localPlayerName$ = Input("Name - ")
			Delay(500)
			FlushKeys
			p.Player = New Player
			localPlayerPointer = Handle(p.Player)
			p\iD = CreateNetPlayer(localPlayerName$)
			Setup("Game")
			hosting = False
		EndIf
	EndIf
	
	If spaceKeyPressed
		FlushKeys
		Color(255,0,0)
		Locate(200,130)
		localPlayerName$ = Input("Name - ")
		Delay(500)
		FlushKeys
		If HostNetGame(localPlayerName$) = 2
			
			If GNET_AddServer("FPSNet", localPlayerName$)
				p.Player = New Player
				localPlayerPointer = Handle(p.Player)
				p\iD = CreateNetPlayer(localPlayerName$)
				Setup("Game")
				hosting = True
				serverRefreshTimer# = MilliSecs()
			Else
				RuntimeError("Failed to Add Server!")
			EndIf
				
		Else
			
			RuntimeError("Failed to Create Net Game!")	
				
		EndIf
			
		
	EndIf
		
		

End Function

;===========================================================================



;====== UPDATE PLAYER MOVEMENT =============================================

Function UpdatePlayerMovement()

	p.Player = Object.Player(localPlayerPointer)
	If KeyDown(17) Or KeyDown(200)
		MoveEntity(p\mesh[1], 0, 0, .5)
	EndIf
	If KeyDown(31) Or KeyDown(208)
		MoveEntity(p\mesh[1], 0, 0, -.5)
	EndIf
	If KeyDown(30) Or KeyDown(203)
		MoveEntity(p\mesh[1], -.5, 0, 0)
	EndIf
	If KeyDown(32) Or KeyDown(205)
		MoveEntity(p\mesh[1], .5, 0, 0)
	EndIf
	
	numColls = CountCollisions(p\mesh[1])
	If (KeyDown(57) Or KeyDown(82)) And numColls > 0
		vY# = .2
		TranslateEntity(p\mesh[1],0, vY#, 0)
	EndIf
	If numColls = 0
		TranslateEntity(p\mesh[1],0, vY#, 0)
		vY# = vY# - .01
	EndIf
			
	RotateEntity(p\mesh[1], 0, EntityYaw(p\mesh[1]) - MouseXSpeed(), 0)
			
End Function

;===========================================================================



;====== UPDATE LIGHTS ======================================================

Function UpdateLights()

	TurnEntity(FindChild(level,"Light Pivot"), 0, 10, 0)
	
End Function

;===========================================================================
	


;====== UPDATE CAMERA ======================================================

Function UpdateCamera()

	shot = 0
	If MouseDown(1)
		If shotTimer# + 200 < MilliSecs()
			shot = CameraPick(camera, CorrectX(320), CorrectY(240))
			shotTimer# = MilliSecs()
			If shot 
				CreateSomeParticles(PickedX(), PickedY(), PickedZ(), 50, 50, 50, 10)
				p.Player = Object.Player(localPlayerPointer)
				SendNetMsg(4,PickedX() + "," + PickedY() + "," + PickedZ() + "-",p\iD,0,0)
			EndIf
		EndIf
	EndIf
	
	RotateEntity(camera,EntityPitch(camera) + MouseYSpeed(),0,0)
	If EntityPitch(camera) > 90
		RotateEntity(camera, 90, 0, 0)
	EndIf
	If EntityPitch(camera) < - 90
		RotateEntity(camera, -90, 0, 0)
	EndIf
		
End Function

;===========================================================================



;====== UPDATE NETWORK =====================================================

Function UpdateNetwork()

	While RecvNetMsg()
		
		Select NetMsgType()
			
			Case 1
				For p.Player = Each Player
					If p\iD = NetMsgFrom()
						DecodeIncomingMessage(NetMsgData$())
						PositionEntity(p\mesh[1], incoming$(1), incoming$(2), incoming$(3))
						RotateEntity(p\mesh[1], incoming$(4), incoming$(5), 0)
						If incoming$(6) <> ""	
							p\score = incoming$(6)
						EndIf
					EndIf
				Next
			Case 2
				p.Player = Object.Player(localPlayerPointer)
				CreateSomeParticles(EntityX(p\mesh[1]), EntityY(p\mesh[1]), EntityZ(p\mesh[1]), 255, 0, 0, 10)
				health = health - 5
				If health <= 0
					health = 100
					PositionEntity(p\mesh[1],5,1,Rand(0, 50))
					SendNetMsg(3, "1", p\iD, NetMsgFrom(), 1)
					CreateSomeParticles(EntityX(p\mesh[1]), EntityY(p\mesh[1]), EntityZ(p\mesh[1]), 255, 0, 0, 100)
					SendNetMsg(5, "1",p\iD, 0, 0)
				EndIf
			Case 3
				p.Player = Object.Player(localPlayerPointer)
				p\score = p\score + 1
			Case 4
				DecodeIncomingMessage(NetMsgData$())
				CreateSomeParticles(incoming$(1), incoming$(2), incoming$(3), 50, 50, 50, 10)
			Case 5
				For p.Player = Each Player
					If p\iD = NetMsgFrom()
						CreateSomeParticles(EntityX(p\mesh[1]), EntityY(p\mesh[1]), EntityZ(p\mesh[1]), 255, 0, 0, 100)
					EndIf
				Next
			Case 100
				p.Player = New Player
				p\iD = NetMsgFrom()
				p\mesh[1] = CreateSphere(8)
				p\name$ = NetPlayerName$(p\iD)
				EntityColor(p\mesh[1], 0, 255, 0)
				PositionEntity(p\mesh[1], 5, 1.5, Rand(50,100))
				EntityType(p\mesh[1],BODY)
				EntityRadius(p\mesh[1], 1.5)
				EntityPickMode(p\mesh[1], 2)
								
				p\mesh[2] = CreateCube(p\mesh[1])
				PositionEntity(p\mesh[2], 1, -.5, 1)
				ScaleEntity(p\mesh[2], .1, .1, .4)
				EntityColor(p\mesh[2], 20, 20, 20)
			Case 101
				For p.Player = Each Player
					If p\iD = NetMsgFrom()
						FreeEntity(p\mesh[1])
						Delete(p)
					EndIf
				Next
			Case 102
				GNET_AddServer("FPSNet", localPlayerName$)
				hosting = True
		
		End Select
	
	Wend
	
	p.Player = Object.Player(localPlayerPointer)
	If positionUpdateTimer# + 50 < MilliSecs()
		SendNetMsg(1, EntityX(p\mesh[1]) + "," + EntityY(p\mesh[1]) + "," + EntityZ(p\mesh[1]) + "," + EntityPitch(camera) + "," + EntityYaw(p\mesh[1]) + "," + p\score + "-", p\iD, 0, 0)
		positionUpdateTimer# = MilliSecs()
	EndIf
	
	For op.Player = Each Player
		If shot = op\mesh[1]
			CreateSomeParticles(PickedX(), PickedY(), PickedZ(), 255, 0, 0, 10)
			SendNetMsg(2, "", p\iD, op\iD, 0)
		EndIf
	Next	 
	
	If hosting = True
		If serverRefreshTimer# + 250000 < MilliSecs()
			GNET_RefreshServer( "FPSNet",localPlayerName$ )
			serverRefreshTimer# = MilliSecs()
		EndIf
	EndIf
	
End Function

;===========================================================================



;====== UPDATE PARTICLES ===================================================

Function UpdateParticles()

	For p.particle = Each particle
		If p\alpha# <= 0
			FreeEntity(p\mesh)
			Delete(p)
		Else
			p\vY# = p\vY# - .01
			TranslateEntity(p\mesh,p\vX#, p\vY#, p\vZ#)
			p\alpha# = p\alpha# - .02
			EntityAlpha(p\mesh, p\alpha#)
		EndIf
	Next
	
End Function

;===========================================================================


;====== DRAW TITLE SCREEN ==================================================

Function DrawTitleScreen()

	Cls
	FPSNetText(font, 320, 0, String$("+",80), 1, 1, 100, 100, 100)
	FPSNetText(font, 320, 480, String$("+",80), 1, 1, 100, 100, 100)
	
	For i = 0 To 640 Step 640
		For j = 10 To 470 Step 10
			FPSNetText(font, i, j, "+", 1, 1, 100, 100, 100)
		Next
	Next

	FPSNetText(bigFont, 320, 30, "FPSNet:", 1, 1, 255, 0, 0)
	FPSNetText(font, 320, 60, "A NETWORKED First Person Shooter Example", 1, 1, 100, 100, 100)
	FPSNetText(font, 320, 80, "Press Up/Down to Select a Game to Join!", 1, 1, 100, 100, 100)
	FPSNetText(font, 320, 100, "Press Space to Host a New Game!", 1, 1, 100, 100, 100)
	FPSNetText(font, 320, 120, "Games In Progress:" , 1, 1, 200, 255, 150)
	
	serverCount = 0
	For gns.GNET_Server = Each GNET_Server
	
		Select serverCount
			
			Case menuCount
				FPSNetText(font, 320, 160 + 20 * serverCount, gns\server$ + " - Press Enter to Join ...", 1, 1, 255, 255, 255)
				serverPointer = Handle(gns)
			Default
				FPSNetText(font, 320, 160 + 20 * serverCount, gns\server$, 1, 1, 100, 100, 100)
		
		End Select
		serverCount = serverCount + 1
		
	Next	
	
End Function

;===========================================================================



;====== DRAW GAMEPLAY ======================================================

Function DrawGamePlay()

	FPSNetText(font, 320, 240, "+", 1, 1, 255, 255, 255)
	p.Player = Object.Player(localPlayerPointer)
	FPSNetText(font, 0, 0, "Your Score - " + p\score, 0, 0, 255, 0, 0)
	
	For p.Player = Each Player
		If Handle(p.Player) <> localPlayerPointer
			CameraProject(camera, EntityX(p\mesh[1]), EntityY(p\mesh[1]), EntityZ(p\mesh[1]))
			FPSNetText(font, ProjectedX(), ProjectedY() - 20, p\name$ + "  -  " + p\score, 1, 1, 255, 255, 255)
		EndIf
	Next	
	
End Function

;===========================================================================



;====== CREATE SOME PARTICLES ==============================================

Function CreateSomeParticles(x, y, z, r, g, b, numParticles)

	For i = 1 To numParticles
		p.Particle = New Particle
		p\mesh = CopyEntity(copySprite)
		PositionEntity(p\mesh,x, y, z)
		EntityColor(p\mesh, r, g, b)
		p\vX# = Rnd(-.2, .2)
		p\vY# = Rnd(.2)
		p\vZ# = Rnd(-.2, .2)
		p\alpha = 1
	Next
	
End Function

;===========================================================================


;====== SETUP ==============================================================

Function Setup(mode$)

	Select mode$
	
		Case "Title Screen"
		
			font = LoadFont("Arial",20,1)
			bigFont = LoadFont("Arial",50,1)
			GNET_ListServers()
			
		Case "Game"
		
			gameState$ = "Game"
			
			p.Player = Object.Player(localPlayerPointer)
			p\mesh[1] = CreatePivot()
			PositionEntity(p\mesh[1], 5, 1.5, Rand(0, 50))
			EntityType(p\mesh[1],BODY)
			EntityRadius(p\mesh[1], 1.5)
			health = 100
			p\score = 0
			p\name$ = localPlayerName$
			
			camera = CreateCamera(p\mesh[1])
			CameraRange(camera,.1, 500)
			
			p\mesh[2] = CreateCube(camera)
			PositionEntity(p\mesh[2], 1, -.5, 1)
			ScaleEntity(p\mesh[2], .1, .1, .4)
			EntityColor(p\mesh[2], 20, 20, 20)
			
			LoadLevel()
			PointEntity(p\mesh[1], level)
			MoveMouse(CorrectX(320), CorrectY(240))
			

			
					
	End Select
	
End Function

;===========================================================================



;====== LOAD LEVEL =========================================================

Function LoadLevel()

	copySprite = CreateSprite()
	ScaleSprite(copySprite, .1, .1)
	EntityFX(copySprite, 1+8)
	HideEntity(copySprite)
		
	level = CreatePlane(16)
	EntityPickMode(level, 2)	
	
	For i = -1 To 1 Step 2
		For j = -4 To 4
			cube = CreateCube(level)
			EntityType(cube, SCENE)
			EntityPickMode(cube, 2)
			EntityColor(cube, 100, 100, 100)
			PositionEntity(cube, i * 10, 1, j * 10)
		Next
	Next
	
	For i = 1 To 8
		cube = CreateCube(level)
		EntityType(cube, SCENE)
		EntityPickMode(cube, 2)
		EntityColor(cube,100,100,100)
		PositionEntity(cube, 0, i * 2, i * 5)
	Next
	
	For i = 1 To 8
		cube = CreateCube(level)
		EntityType(cube, SCENE)
		EntityPickMode(cube, 2)
		EntityColor(cube,100,100,100)
		PositionEntity(cube, 0, i * 2, i * -5)
	Next	
	
	EntityType(level,SCENE)
	
	lightPivot = CreatePivot(level)
	NameEntity(lightPivot,"Light Pivot")
	
	light = CreateLight(3,lightPivot)
	NameEntity(light, "light1")
	PositionEntity(light,25, 25, 0)
	RotateEntity(light, 90, 0, 0)
	LightRange(light, 500)
	LightColor(light, 255, 0, 0)
	
	light = CreateLight(3,lightPivot)
	NameEntity(light, "light2")
	PositionEntity(light,-25, 25, 0)
	RotateEntity(light, 90, 0, 0)
	LightRange(light, 500)
	LightColor(light, 0, 0, 255)
	
	light = CreateLight(3,lightPivot)
	NameEntity(light, "light3")
	PositionEntity(light,0, 25, 25)
	RotateEntity(light, 90, 0, 0)
	LightRange(light, 500)
	LightColor(light, 0, 255, 0)
	
End Function

;===========================================================================
	


;====== FPSNET TEXT ========================================================

Function FPSNetText(fontType, X, Y, theText$, centerX = 0, centerY = 0,r = 255, g = 255, b = 255)

	Color(r, g, b)
	SetFont(fontType)
	Text(CorrectX(X), CorrectY(Y), theText$, centerX, centerY)
	
End Function

;==========================================================================	
	
	

;====== CORRECTX ==========================================================

Function CorrectX(pixel)
	
	Return (pixel * GraphicsWidth() / 640)

End Function

;==========================================================================



;====== CORRECTY ==========================================================

Function CorrectY(pixel)
	
	Return (pixel * GraphicsHeight() / 480) 

End Function 

;==========================================================================



;====== DECODE INCOMING MESSAGE ===========================================

Function DecodeIncomingMessage(message$)

	For i = 1 To 100
		incoming$(i) = ""
	Next
	i = 1
	part = 1
	incoming$(part) = Mid(message$, i, 1)
	i = i + 1
	While Mid(message$, i, 1)<>"-"
		While Mid(message$, i, 1)<>","
			incoming$(part)=incoming$(part)+Mid(message$, i, 1)
			i = i + 1
			If Mid(message$, i, 1)="-"
				Exit
			EndIf
		Wend
		If Mid(message$, i, 1)=","
			i = i + 1
			part = part + 1
			incoming$(part) = Mid(message$, i, 1)
			i = i + 1
		EndIf
	Wend
	
End Function

;==========================================================================



;====== GNET SOURCE CODE ==================================================


Const GNET_HOST$="www.blitzbasic.com"
Const GNET_PORT=80
Const GNET_GET$="/gnet/gnet.php"

Type GNET_Server
	Field game$,server$,ip$
End Type

Function GNET_Esc$( t$ )
	t$=Replace$( t$,"&","" )
	t$=Replace$( t$,"%","" )
	t$=Replace$( t$,"'","" )
	t$=Replace$( t$,Chr$(34),"" )
	t$=Replace$( t$," ","_" )
	Return t$
End Function

Function GNET_Open( opt$ )
	t=OpenTCPStream( GNET_HOST$,GNET_PORT )
	If Not t Return 0
	
	WriteLine t,"GET "+GNET_GET$+"?opt="+opt$+" HTTP/1.0"
	WriteLine t,"HOST: "+GNET_HOST$
	WriteLine t,""
	
	While ReadLine$(t)<>""
	Wend
	
	Return t
End Function

Function GNET_Exec( opt$,game$,server$ )
	opt$=opt$+"&game="+GNET_Esc$(game$)
	If server$<>"" opt$=opt$+"&server="+GNET_Esc$(server$)
	t=GNET_Open( opt$ )
	If Not t Return 0
	
	ok=False
	If( ReadLine$(t)="OK" ) ok=True
	
	CloseTCPStream t
	Return ok
End Function

Function GNET_Ping$()
	t=GNET_Open( "ping" )
	If Not t Return 0
	
	ip$=ReadLine$(t)
	
	CloseTCPStream t
	Return ip$
End Function

Function GNET_AddServer( game$,server$="" )
	Return GNET_Exec( "add",game$,server$ )
End Function

Function GNET_RefreshServer( game$,server$="" )
	Return GNET_Exec( "ref",game$,server$ )
End Function

Function GNET_RemoveServer( game$ )
	Return GNET_Exec( "rem",game$,"" )
End Function

Function GNET_ListServers( game$="" )
	Delete Each GNET_Server
	t=GNET_Open( "list" )
	If Not t Return 0
	
	Repeat
		t_game$=ReadLine$(t)
		If t_game$="" Exit
		t_server$=ReadLine$(t)
		t_ip$=ReadLine(t)
		If game$="" Or game$=t_game$
			p.GNET_Server=New GNET_Server
			p\game$=t_game$
			p\server$=t_server$
			p\ip$=t_ip$
		EndIf
	Forever
	
	CloseTCPStream t
	Return 1
	
End Function

;==========================================================================
