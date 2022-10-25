; ID: 1149
; Author: Jeremy Alessi
; Date: 2004-08-30 01:29:25
; Title: FPSNetUpdate
; Description: An Updated NETWORKED FPS Example

;====== FPSNet: A NETWORKED FIRST PERSON SHOOTER EXAMPLE ==================
;====== UTILIZING ONLY BUILT IN BLITZ3D FEATURES ==========================
;====== A CODESKETCH BY JEREMY ALESSI =====================================
;====== THANKS MARK SIBLY FOR GNET ========================================

;====== WASD OR CURSORS MOVE, MOUSE LOOKS, LEFT MOUSE BUTTON SHOOTS =======
;====== SPACE BAR OR 0 ON NUMPAD JUMPS, PRESS C TO CHAT ===================
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
Global chatKeyPressed

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
Global level,copySprite, lightPivot

;Camera Var
Global camera

;Player Gameplay Vars
Global health#, shot, shot2, shot3, shotTimer#, positionUpdateTimer#, serverRefreshTimer#, hosting
Global vX#, vY#, vZ#, oldX#, oldY#, oldZ#, centerMouse = True, enteringChat = False, chat$
Global copyPowerUp, weapon$ = "Semi-Automatic", powerUpSpawned, ammo# = 50, randPos, randNum
Global justDied#
;==========================================================================



;====== INCOMING ==========================================================

Dim incoming$(100)

;==========================================================================



;====== CONTENTS OF A KEY =================================================

Dim keys$(237)
LoadKeyboardContent()

;==========================================================================



;====== SHOT ARRAY ========================================================

Dim pickedArray(3,3)

;==========================================================================



;====== TYPES =============================================================

Type Player

	Field iD, name$, mesh[10], score
	
End Type

Type Particle
	
	Field mesh, name$, alpha#
	Field vX#, vY#, vZ#
	
End Type

Type chatMsg
	
	Field msg$, born#, from
	
End Type

Type powerUp

	Field mesh, name$
	
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
			If Not GNET_RemoveServer("FPSNetUpdate")
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
			UpdateTitleInput()
			UpdateTitleScreen()
		Case "Game"
			DetectPowerUpCollision()
			UpdateGameInput()
			ControlChat()
			UpdateLights()
			UpdateCamera()
			UpdatePlayerMovement()
			HostSpawnPowerups()
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



;====== UPDATE TITLE INPUT =================================================

Function UpdateTitleInput()

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



;====== UPDATE GAME INPUT ==================================================

Function UpdateGameInput()

	chatKeyPressed = KeyHit(46)
	
	enterKeyPressed = KeyHit(28)
		
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
			
			If GNET_AddServer("FPSNetUpdate", localPlayerName$)
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

	If enteringChat Return

	p.Player = Object.Player(localPlayerPointer)
	
	oldX# = EntityX(p\mesh[1])
	oldY# = EntityY(p\mesh[1])
	oldZ# = EntityZ(p\mesh[1])
	
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
	
	If numColls = 0
		vY# = vY# - .01
	Else
		vY# = 0
	EndIf
	
	If (KeyDown(57) Or KeyDown(82)) And numColls > 0
		vY# = .2
	EndIf
		
	TranslateEntity(p\mesh[1],0, vY#, 0)		
	RotateEntity(p\mesh[1], 0, EntityYaw(p\mesh[1]) - MouseXSpeed(), 0)
	
	vX# = EntityX(p\mesh[1]) - oldX#
	vY# = EntityY(p\mesh[1]) - oldY#
	vZ# = EntityZ(p\mesh[1]) - oldZ#
			
End Function

;===========================================================================



;====== UPDATE LIGHTS ======================================================

Function UpdateLights()

	TurnEntity(lightPivot, 0, 10, 0)
	
End Function

;===========================================================================
	


;====== UPDATE CAMERA ======================================================

Function UpdateCamera()

	p.Player = Object.Player(localPlayerPointer)

	shot = 0
	shot2 = 0
	shot3 = 0
	If MouseDown(1)
		Select weapon$
		
			Case "Semi-Automatic"
				If shotTimer# + 200 < MilliSecs() And ammo# > 0
					ammo# = ammo# - 1
					shot = CameraPick(camera, CorrectX(320), CorrectY(240))
					shotTimer# = MilliSecs()
					If shot 
						CreateSomeParticles(PickedX(), PickedY(), PickedZ(), 50, 50, 50, 10)
						SendNetMsg(4,"1," + PickedX() + "," + PickedY() + "," + PickedZ() + "-", p\iD, 0, 0)
					EndIf
				EndIf
			Case "Shotgun"
				If shotTimer# + 750 < MilliSecs() And ammo# >= 3
					ammo# = ammo# - 3
					For i = 1 To 3
						
						Select i
							Case 1
								shot = CameraPick(camera, CorrectX(320 - 20), CorrectY(240))
							Case 2
								shot2 = CameraPick(camera, CorrectX(320), CorrectY(240))
							Case 3
								shot3 = CameraPick(camera, CorrectX(320 + 20), CorrectY(240))
						End Select
					
						pickedArray(i,1) = PickedX()
						pickedArray(i,2) = PickedY()
						pickedArray(i,3) = PickedZ()
					
					Next
					shotTimer# = MilliSecs()
					If shot Or shot2 Or shot3
						For i = 1 To 3
							If (i = 1 And shot <> 0) Or (i = 2 And shot2 <> 0) Or (i = 3 And shot3 <> 0)
								CreateSomeParticles(pickedArray(i,1), pickedArray(i,2), pickedArray(i,3), 200, 200, 200, 10)
							EndIf
						Next
						SendNetMsg(4,"2," + pickedArray(1,1) + "," + pickedArray(1,2) + "," + pickedArray(1,3) + "," + pickedArray(2,1) + "," + pickedArray(2,2) + "," + pickedArray(2,3) + "," + pickedArray(3,1) + "," + pickedArray(3,2) + "," + pickedArray(3,3) + "-", p\iD, 0, 0)
					EndIf
				EndIf
			Case "Flamethrower"
				If shotTimer# + 50 < MilliSecs() And ammo# >= .1
					ammo# = ammo# - .1
					pvX# = vX# + (EntityX(p\mesh[3], True) - EntityX(p\mesh[2], True)) / 10
					pvY# = vY# + (EntityY(p\mesh[3], True) - EntityY(p\mesh[2], True)) / 10
					pvZ# = vZ# + (EntityZ(p\mesh[3], True) - EntityZ(p\mesh[2], True)) / 10
					
					SendNetMsg(9, pvX# + "," + pvY# + "," + pvZ# + "-", p\iD, 0, 0)
					CreateSomeParticles(EntityX(p\mesh[2],True), EntityY(p\mesh[2], True), EntityZ(p\mesh[2], True), 255, 200, 100, 10,  pvX# + Rnd(-.05, .05), pvY# + Rnd(-.05, .05), pvZ#, "Flame")
					shot = LinePick(EntityX(camera, True), EntityY(camera, True), EntityZ(camera, True), EntityX(p\mesh[3], True) - EntityX(camera, True), EntityY(p\mesh[3], True) - EntityY(camera, True), EntityZ(p\mesh[3], True) - EntityZ(camera, True))
					shotTimer# = MilliSecs()
					If shot
						CreateSomeParticles(PickedX(), PickedY(), PickedZ(), 255, 200, 100, 10, "Flame")
						SendNetMsg(4,"3," + PickedX() + "," + PickedY() + "," + PickedZ() + "-", p\iD, 0, 0)
					EndIf
				EndIf
		
		End Select
					
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
				
				If justDied# + 500 < MilliSecs()
					DecodeIncomingMessage(NetMsgData$())
					Select incoming$(1)
						Case 1
							health# = health# - 5
						Case 2
							count = 0
							If incoming$(2) <> 0
								count = count + 1
							EndIf
							If incoming$(3) <> 0
								count = count + 1
							EndIf
							If incoming$(4) <> 0
								count = count + 1
							EndIf
							health# = health# - (5 * count)
						Case 3
							health# = health# - 25
					End Select
				EndIf
				
				If health# <= 0
					health# = 100
					PositionEntity(p\mesh[1],5,1,Rand(0, 50))
					SendNetMsg(3, "1", p\iD, NetMsgFrom(), 1)
					CreateSomeParticles(EntityX(p\mesh[1]), EntityY(p\mesh[1]), EntityZ(p\mesh[1]), 255, 0, 0, 100)
					SendNetMsg(5, "1",p\iD, 0, 0)
					justDied# = MilliSecs()
				EndIf
			
			Case 3
			
				p.Player = Object.Player(localPlayerPointer)
				p\score = p\score + 1
			
			Case 4
			
				DecodeIncomingMessage(NetMsgData$())
				
				Select incoming$(1)
					
					Case 1
						CreateSomeParticles(incoming$(2), incoming$(3), incoming$(4), 50, 50, 50, 10)
					Case 2
						CreateSomeParticles(incoming$(2), incoming$(3), incoming$(4), 255, 255, 255, 10)
						CreateSomeParticles(incoming$(5), incoming$(6), incoming$(7), 255, 255, 255, 10)
						CreateSomeParticles(incoming$(8), incoming$(9), incoming$(10), 255, 255, 255, 10)
					Case 3
						CreateSomeParticles(incoming$(2), incoming$(3), incoming$(4), 255, 200, 100, 10)
				
				End Select
			
			Case 5
			
				For p.Player = Each Player
					If p\iD = NetMsgFrom()
						CreateSomeParticles(EntityX(p\mesh[1]), EntityY(p\mesh[1]), EntityZ(p\mesh[1]), 255, 0, 0, 100)
					EndIf
				Next
			
			Case 6
			
				c.chatMsg = New chatMsg
				c\msg = NetMsgData$()
				c\born# = MilliSecs()
				c\from = NetMsgFrom()
			
			Case 7
			
				DecodeIncomingMessage(NetMsgData$())
				pup.powerUp = New powerUp
				pup\mesh = CopyEntity(copyPowerup)
				powerUpSpawned = True
								
				PositionEntity(pup\mesh, EntityX(GetChild(level, incoming$(1)), True), EntityY(GetChild(level, incoming$(1)), True) + 2, EntityZ(GetChild(level, incoming$(1)), True))
				
				Select incoming$(2)
			
					Case 1
						pup\name$ = "Semi-Automatic"
						EntityColor(pup\mesh, 255, 255, 255)
					Case 2
						pup\name$ = "Shotgun"
						EntityColor(pup\mesh, 100, 100, 255)
					Case 3
						pup\name$ = "Flamethrower"
						EntityColor(pup\mesh, 200, 150, 100)
							
				End Select
				randPos = incoming$(1)
				randNum = incoming$(2)
			
			Case 8
			
				For pup.powerUp = Each powerUP
					FreeEntity(pup\mesh)
					Delete(pup)
				Next
				powerUpSpawned = False
			
			Case 9
			
				For op.Player = Each Player
					If op\iD = NetMsgFrom()
						DecodeIncomingMessage(NetMsgData$())
						CreateSomeParticles(EntityX(op\mesh[2],True), EntityY(op\mesh[2], True), EntityZ(op\mesh[2], True), 255, 200, 100, 10,  incoming$(1) + Rnd(-.05, .05), incoming$(2) + Rnd(-.05, .05), incoming$(3), "Flame")
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
				
				p\mesh[3] = CreatePivot(p\mesh[1])
				PositionEntity(p\mesh[3], 0, 0, 5)
				
				If hosting
					p.Player = Object.Player(localPlayerPointer)
					SendNetMsg(7, randPos + "," + randNum + "-", p\iD#, NetMsgFrom(), 1)
				EndIf
			
			Case 101
			
				For p.Player = Each Player
					If p\iD = NetMsgFrom()
						FreeEntity(p\mesh[1])
						Delete(p)
					EndIf
				Next
			
			Case 102
			
				GNET_AddServer("FPSNetUpdate", localPlayerName$)
				hosting = True
		
		End Select
	
	Wend
	
	p.Player = Object.Player(localPlayerPointer)
	If positionUpdateTimer# + 50 < MilliSecs()
		SendNetMsg(1, EntityX(p\mesh[1]) + "," + EntityY(p\mesh[1]) + "," + EntityZ(p\mesh[1]) + "," + EntityPitch(camera) + "," + EntityYaw(p\mesh[1]) + "," + p\score + "-", p\iD, 0, 0)
		positionUpdateTimer# = MilliSecs()
	EndIf
	
	For op.Player = Each Player
		Select weapon$
		
			Case "Semi-Automatic"
				If shot = op\mesh[1]
					CreateSomeParticles(PickedX(), PickedY(), PickedZ(), 255, 0, 0, 10)
					SendNetMsg(2, "1-", p\iD, op\iD, 0)
				EndIf
			Case "Shotgun"
				If shot = op\mesh[1] Or shot2 = op\mesh[1] Or shot3 = op\mesh[1]
					Select op\mesh[1]
						Case shot
							CreateSomeParticles(pickedArray(1, 1), pickedArray(1, 2), pickedArray(1, 3), 255, 0, 0, 10)
						Case shot2
							CreateSomeParticles(pickedArray(2, 1), pickedArray(2, 2), pickedArray(2, 3), 255, 0, 0, 10)
						Case shot3
							CreateSomeParticles(pickedArray(3, 1), pickedArray(3, 2), pickedArray(3, 3), 255, 0, 0, 10)
					End Select
					SendNetMsg(2, "2," + Left(Str(shot), 1) + "," + Left(Str(shot2), 1) + "," + Left(Str(shot3), 1) + "-", p\iD, op\iD, 0)
				EndIf
			Case "Flamethrower"
				If shot = op\mesh[1]
					CreateSomeParticles(PickedX(), PickedY(), PickedZ(), 255, 0, 0, 10)
					SendNetMsg(2, "3-", p\iD, op\iD, 0)
				EndIf
		
		End Select
	Next	 
	
	If hosting = True
		If serverRefreshTimer# + 250000 < MilliSecs()
			GNET_RefreshServer( "FPSNetUpdate",localPlayerName$ )
			serverRefreshTimer# = MilliSecs()
		EndIf
	EndIf
	
End Function

;===========================================================================



;====== UPDATE PARTICLES ===================================================

Function UpdateParticles()

	For p.Particle = Each Particle
		If p\alpha# <= 0
			FreeEntity(p\mesh)
			Delete(p)
		Else
		
			Select p\name$
				Case "Flame"
					TranslateEntity(p\mesh,p\vX#, p\vY#, p\vZ# )
					p\alpha# = p\alpha# - Rnd(.3)
					EntityAlpha(p\mesh, p\alpha#)
				Default
					p\vY# = p\vY# - .01
					TranslateEntity(p\mesh,p\vX#, p\vY#, p\vZ#)
					p\alpha# = p\alpha# - .02
					EntityAlpha(p\mesh, p\alpha#)
		
			End Select
		
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

	Select weapon$
		
		Case "Semi-Automatic"
			FPSNetText(font, 320, 240, "+", 1, 1, 255, 255, 255)
		Case "Shotgun"
			FPSNetText(font, 300, 240, "+", 1, 1, 255, 255, 255)
			FPSNetText(font, 320, 240, "+", 1, 1, 255, 255, 255)
			FPSNetText(font, 340, 240, "+", 1, 1, 255, 255, 255)
		Case "Flamethrower"
			FPSNetText(font, 320, 240, "()", 1, 1, 255, 200, 100)
	
	End Select
			
	p.Player = Object.Player(localPlayerPointer)
	FPSNetText(font, 0, 0, "Your Score: " + p\score, 0, 0, 255, 0, 0)
	
	FPSNetText(font, 150, 0, "Health: ", 0, 0, 0, 255, 0)
	Color(255, 0, 0)
	Rect(CorrectX(221), CorrectY(4), CorrectX(health#), CorrectY(10), 1)
	Color(255, 255, 255)
	Rect(CorrectX(220), CorrectY(2), CorrectX(102), CorrectY(14), 0)
	
	FPSNetText(font, 400, 0, "Ammo: " + Int(ammo#), 0, 0, 255, 255, 255)

	
	printI = 50
	For c.chatMsg = Each chatMsg
		
		secondsAround = (MilliSecs() - c\born#) / 50
		FPSNetText(font, 0, 0 + printI, NetPlayerName(c\from) + " - " + c\msg$, 0, 0, 255 - secondsAround, 255 - secondsAround, 255 - secondsAround)
		printI = printI + 15
		
	Next
	
	If enteringChat = True
	
		FPSNetText(font, 0, 460, localPlayerName$ + " - " + chat$, 0, 0, 255, 255, 255)
	
	EndIf
	
	For p.Player = Each Player
	
		If Handle(p.Player) <> localPlayerPointer
			CameraProject(camera, EntityX(p\mesh[1]), EntityY(p\mesh[1]), EntityZ(p\mesh[1]))
			FPSNetText(font, ProjectedX(), ProjectedY() - 20, p\name$ + "  -  " + p\score, 1, 1, 255, 255, 255)
		EndIf

	Next	
	
End Function

;===========================================================================



;====== DETECT POWERUP COLLISION ===========================================

Function DetectPowerUpCollision()

	If Not powerUpSpawned Return
	
	p.Player = Object.Player(localPlayerPointer)
	For pup.powerUp = Each powerUp
		
		If EntityDistance(p\mesh[1], pup\mesh) < 3
			
			If weapon$ = pup\name$
				Select weapon$
					Case "Semi-Automatic"	
						ammo# = ammo# + 50
					Case "Shotgun"
						ammo# = ammo# + 45
					Case "Flamethrower"
						ammo# = ammo# + 30
				End Select
			Else
				Select pup\name$
					Case "Semi-Automatic"
						ammo# = 50
					Case "Shotgun"
						ammo# = 45
					Case "Flamethrower"
						ammo# = 30
				End Select 
			EndIf
			weapon$ = pup\name$
			powerUpSpawned = False
			FreeEntity(pup\mesh)
			Delete(pup)
			SendNetMsg(8, "", p\iD, 0, 1)
		
		EndIf
	
	Next
	
End Function

;===========================================================================



;====== HOST SPAWN POWERUPS ================================================

Function HostSpawnPowerups()

	If (Not hosting) Or (powerUpSpawned) 
		Return
	EndIf
	
	randPos = Rand(1, CountChildren(level))
	
	pup.powerUp = New powerUp
	pup\mesh = CopyEntity(copyPowerUp)
	PositionEntity(pup\mesh, EntityX(GetChild(level, randPos), True), EntityY(GetChild(level, randPos), True) + 2, EntityZ(GetChild(level, randPos), True))
	randNum = Rand(1,3)
	Select randNum
		
		Case 1
			pup\name$ = "Semi-Automatic"
			EntityColor(pup\mesh, 255, 255, 255)
		Case 2
			pup\name$ = "Shotgun"
			EntityColor(pup\mesh, 100, 100, 255)
		Case 3
			pup\name$ = "Flamethrower"
			EntityColor(pup\mesh, 200, 150, 100)
			
	End Select
	p.Player = Object.Player(localPlayerPointer)
	SendNetMsg(7, randPos + "," + randNum + "-", p\iD#, 0, 1)
	powerUpSpawned = True
	
	
End Function

;===========================================================================		


;====== CONTROL CHAT =======================================================

Function ControlChat()

	If chatKeyPressed
		
		enteringChat = True
		FlushKeys
				
	EndIf
	
	If enterKeyPressed And enteringChat = True
	
		p.Player = Object.Player(localPlayerPointer)
		
		c.chatMsg = New chatMsg
		c\msg$ = chat$
		c\born# = MilliSecs() 
		c\from = p\iD
		
		SendNetMsg(6, chat$, p\iD , 0, 0)
				
		chat$ = ""
		enteringChat = False
				
	EndIf

	If enteringChat = True
		
		EnterChat()

	EndIf
	
	For c.chatMsg = Each chatMsg
		
		If c\born# + 10000 < MilliSecs()
		
			Delete(c)
		
		EndIf
	
	Next
	
End Function
	
;===========================================================================



;====== CREATE SOME ParticleS ==============================================

Function CreateSomeParticles(x#, y#, z#, r, g, b, numParticles, optvX# = 0, optvY# = 0, optvZ# = 0, passedName$ = "")

	For i = 1 To numParticles
		p.Particle = New Particle
		p\mesh = CopyEntity(copySprite)
		If passedName$ <> ""
			p\name$ = passedName$
		EndIf
		PositionEntity(p\mesh,x#, y#, z#, True)
		EntityColor(p\mesh, r, g, b)
		If optvX# = 0 
			p\vX# = Rnd(-.2, .2)
		Else
			p\vx# = optvX#
		EndIf
		If optvY# = 0
			p\vY# = Rnd(.2)
		Else
			p\vY# = optvY#
		EndIf
		If optvZ# = 0
			p\vZ# = Rnd(-.2, .2)
		Else
			p\vZ# = optvZ#
		EndIf
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
			health# = 100
			p\score = 0
			p\name$ = localPlayerName$
			
			camera = CreateCamera(p\mesh[1])
			CameraRange(camera,.1, 500)
			
			p\mesh[2] = CreateCube(camera)
			PositionEntity(p\mesh[2], 1, -.5, 1)
			ScaleEntity(p\mesh[2], .1, .1, .4)
			EntityColor(p\mesh[2], 20, 20, 20)
			
			p\mesh[3] = CreatePivot(camera)
			PositionEntity(p\mesh[3], 0, 0, 5)
						
			LoadLevel()
			PointEntity(p\mesh[1], level)
			MoveMouse(CorrectX(320), CorrectY(240))
			

			
					
	End Select
	
End Function

;===========================================================================



;====== LOAD LEVEL =========================================================

Function LoadLevel()

	copyPowerUp = CreateSphere()
	HideEntity(copyPowerUp)

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
	
	lightPivot = CreatePivot()
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

;====== ENTER CHAT ========================================================

Function EnterChat()

	For i = 1 To 237
		If (i >= 2 And i <= 11) Or (i >= 16 And i <= 25) Or (i >= 30 And i <= 38) Or (i >= 44 And i <= 50)
			If KeyHit(i)
				If Len(chat$) <= 25
					chat$ = chat$ + keys$(i)
				EndIf
			EndIf
		EndIf
	Next
	
	If KeyHit(14) And Len(chat$) >= 1 And  keytimer# + 100 < MilliSecs()
		chat$ = Left(chat$,Len(chat$) - 1)
		keytimer# = MilliSecs()
	EndIf
	If KeyHit(211) And Len(chat$) >= 1 And  keytimer# + 100 < MilliSecs()
		chat$ = Right(chat$,Len(chat$) - 1)
		keytimer# = MilliSecs()
	EndIf
	
	If KeyHit(57) And Len(chat$) <= 25 And  keytimer# + 100 < MilliSecs()
		chat$ = chat$ + " "
		keytimer# = MilliSecs()
	EndIf
	
	If KeyHit(53) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "?"
	EndIf
	
	If KeyHit(2) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "!"
	EndIf
	
	If KeyHit(3) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "@"
	EndIf
	
	If KeyHit(4) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "#"
	EndIf
	
	If KeyHit(5) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "$"
	EndIf
	
	If KeyHit(6) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "%"
	EndIf
	
	If KeyHit(7) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "^"
	EndIf
	
	If KeyHit(8) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "&"
	EndIf
	
	If KeyHit(9) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "*"
	EndIf
	
	If KeyHit(10) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "("
	EndIf
	
	If KeyHit(11) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + ")"
	EndIf
	
	If KeyHit(12) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "-"
	EndIf
	
	If KeyHit(13) And Len(chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		chat$ = chat$ + "+"
	EndIf
	
	If KeyHit(40)
		chat$ = chat$ + "'"
	EndIf
	
	If KeyHit(51)
		chat$ = chat$ + ","
	EndIf	  

End Function

;==========================================================================

;===== KEYS CONTENT =======================================================

Function LoadKeyboardContent()

	keys$(1) = "ESCAPE"
	keys$(2) = "1"
	keys$(3) = "2"
	keys$(4) = "3"
	keys$(5) = "4"
	keys$(6) = "5"
	keys$(7) = "6"
	keys$(8) = "7"
	keys$(9) = "8"
	keys$(10) = "9"
	keys$(11) = "0"
	keys$(12) = "-"
	keys$(13) = "="
	keys$(14) = "BACKSPACE"
	keys$(15) = "TAB"
	keys$(16) = "Q"
	keys$(17) = "W"
	keys$(18) = "E"
	keys$(19) = "R"
	keys$(20) = "T"
	keys$(21) = "Y"
	keys$(22) = "U"
	keys$(23) = "I"
	keys$(24) = "O"
	keys$(25) = "P"
	keys$(26) = "["
	keys$(27) = "]"
	keys$(28) = "ENTER"
	keys$(29) = "LEFTCONTROL"
	keys$(30) = "A"
	keys$(31) = "S"
	keys$(32) = "D"
	keys$(33) = "F"
	keys$(34) = "G"
	keys$(35) = "H"
	keys$(36) = "J"
	keys$(37) = "K"
	keys$(38) = "L"
	keys$(39) = ";"
	keys$(40) = "'"
	keys$(41) = "GRAVE"
	keys$(42) = "LEFTSHIFT"
	keys$(43) = "BACKSLASH(\)"
	keys$(44) = "Z"
	keys$(45) = "X"
	keys$(46) = "C"
	keys$(47) = "V"
	keys$(48) = "B"
	keys$(49) = "N"
	keys$(50) = "M"
	keys$(51) = "COMMA(,)"
	keys$(52) = "PERIOD(.)"
	keys$(53) = "SLASH(/)"
	keys$(54) = "RIGHTSHIFT"
	keys$(55) = "MULTIPLY(*)NUMPAD"
	keys$(56) = "LEFTALT/MENU"
	keys$(57) = "SPACE"
	keys$(58) = "CAPITAL"
	keys$(59) = "F1"
	keys$(60) = "F2"
	keys$(61) = "F3"
	keys$(62) = "F4"
	keys$(63) = "F5"
	keys$(64) = "F6"
	keys$(65) = "F7"
	keys$(66) = "F8"
	keys$(67) = "F9"
	keys$(68) = "F10"
	keys$(69) = "NUMLOCK"
	keys$(70) = "SCROLLLOCK"
	keys$(71) = "NUMPAD7"
	keys$(72) = "NUMPAD8"
	keys$(73) = "NUMPAD9"
	keys$(74) = "SUBTRACT(-)NUMPAD"
	keys$(75) = "NUMPAD4"
	keys$(76) = "NUMPAD5"
	keys$(77) = "NUMPAD6"
	keys$(78) = "ADD(+)NUMPAD"
	keys$(79) = "NUMPAD1"
	keys$(80) = "NUMPAD2"
	keys$(81) = "NUMPAD3"
	keys$(82) = "NUMPAD0"
	keys$(83) = "DECIMAL(.)NUMPAD"
	keys$(84) = "-"
	keys$(85) = "-"
	keys$(86) = "OEM_102"
	keys$(87) = "F11"
	keys$(88) = "F12"
	keys$(89) = "-"
	keys$(90) = "-"
	keys$(91) = "-"
	keys$(92) = "-"
	keys$(93) = "-"
	keys$(94) = "-"
	keys$(95) = "-"
	keys$(96) = "-"
	keys$(97) = "-"
	keys$(98) = "-"
	keys$(99) = "-"
	keys$(100) = "F13"
	keys$(101) = "F14"
	keys$(102) = "F15"
	keys$(103) = "-"
	keys$(104) = "-"
	keys$(105) = "-"
	keys$(106) = "-"
	keys$(107) = "-"
	keys$(108) = "-"
	keys$(109) = "-"
	keys$(110) = "-"
	keys$(111) = "-"
	keys$(112) = "KANA"
	keys$(113) = "-"
	keys$(114) = "-"
	keys$(115) = "ABNT_C1"
	keys$(116) = "-"
	keys$(117) = "-"
	keys$(118) = "-"
	keys$(119) = "-"
	keys$(120) = "-"
	keys$(121) = "CONVERT"
	keys$(122) = "-"
	keys$(123) = "NOCONVERT"
	keys$(124) = "-"
	keys$(125) = "YEN"
	keys$(126) = "ABNT_C2"
	keys$(127) = "-"
	keys$(128) = "-"
	keys$(129) = "-"
	keys$(130) = "-"
	keys$(131) = "-"
	keys$(132) = "-"
	keys$(133) = "-"
	keys$(134) = "-"
	keys$(135) = "-"
	keys$(136) = "-"
	keys$(137) = "-"
	keys$(138) = "-"
	keys$(139) = "-"
	keys$(140) = "-"
	keys$(141) = "EQUALS"
	keys$(142) = "-"
	keys$(143) = "-"
	keys$(144) = "PREVTRACK"
	keys$(145) = "AT"
	keys$(146) = "COLON(:)"
	keys$(147) = "UNDERLINE"
	keys$(148) = "KANJI"
	keys$(149) = "STOP"
	keys$(150) = "AX"
	keys$(151) = "UNLABELED"
	keys$(152) = "-"
	keys$(153) = "NEXTTRACK"
	keys$(154) = "-"
	keys$(155) = "-"
	keys$(156) = "ENTER (NUMPAD)"
	keys$(157) = "RIGHTCONTROL"
	keys$(158) = "-"
	keys$(159) = "-"
	keys$(160) = "MUTE"
	keys$(161) = "CALCULATOR"
	keys$(162) = "PLAY/PAUSE"
	keys$(163) = "-"
	keys$(164) = "MEDIASTOP"
	keys$(165) = "-"
	keys$(166) = "-"
	keys$(167) = "-"
	keys$(168) = "-"
	keys$(169) = "-"
	keys$(170) = "-"
	keys$(171) = "-"
	keys$(172) = "-"
	keys$(173) = "-"
	keys$(174) = "VOLUMEDOWN"
	keys$(175) = "-"
	keys$(176) = "VOLUMEUP"
	keys$(177) = "-"
	keys$(178) = "WEBHOME"
	keys$(179) = "COMMA(,)"
	keys$(180) = "-"
	keys$(181) = "DIVIDE(/)"
	keys$(182) = "-"
	keys$(183) = "SYSREQ"
	keys$(184) = "RIGHTALT/MENU"
	keys$(185) = "-"
	keys$(186) = "-"
	keys$(187) = "-"
	keys$(188) = "-"
	keys$(189) = "-"
	keys$(190) = "-"
	keys$(191) = "-"
	keys$(192) = "-"
	keys$(193) = "-"
	keys$(194) = "-"
	keys$(195) = "-"
	keys$(196) = "-"
	keys$(197) = "PAUSE"
	keys$(198) = "-"
	keys$(199) = "HOME"
	keys$(200) = "UP"
	keys$(201) = "PAGEUP/PRIOR"
	keys$(202) = "-"
	keys$(203) = "LEFT"
	keys$(204) = "-"
	keys$(205) = "RIGHT"
	keys$(206) = "-"
	keys$(207) = "END"
	keys$(208) = "DOWN"
	keys$(209) = "NEXT"
	keys$(210) = "INSERT"
	keys$(211) = "DELETE"
	keys$(212) = "-"
	keys$(213) = "-"
	keys$(214) = "-"
	keys$(215) = "-"
	keys$(216) = "-"
	keys$(217) = "-"
	keys$(218) = "-"
	keys$(219) = "LEFTWINDWOS"
	keys$(220) = "RIGHTWINDOWS"
	keys$(221) = "APPS"
	keys$(222) = "POWER"
	keys$(223) = "SLEEP"
	keys$(224) = "-"
	keys$(225) = "-"
	keys$(226) = "-"
	keys$(227) = "WAKE"
	keys$(228) = "-"
	keys$(229) = "WEBSEARCH"
	keys$(230) = "WEBFAVORITES"
	keys$(231) = "WEBREFRESH"
	keys$(232) = "WEBSTOP"
	keys$(233) = "WEBFORWARD"
	keys$(234) = "WEBBACK"
	keys$(235) = "MYCOMPUTER"
	keys$(236) = "MAIL"
	keys$(237) = "MEDIASELECT"

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
