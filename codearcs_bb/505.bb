; ID: 505
; Author: Techlord
; Date: 2002-11-23 20:46:04
; Title: Project PLASMA FPS 2004: Engine Source
; Description: Engine Source

;====== FPSNet: A NETWORKED FIRST PERSON SHOOTER EXAMPLE ==================
;====== UTILIZING ONLY BUILT IN BLITZ3D FEATURES ==========================
;====== A CODESKETCH BY JEREMY ALESSI =====================================
;====== THANKS MARK SIBLY FOR GNET ========================================

;====== BOT CODE ADDED BY SMIFF 20/09/2004

;====== WASD OR CURSORS MOVE, MOUSE LOOKS, LEFT MOUSE BUTTON SHOOTS =======
;====== SPACE BAR OR 0 ON NUMPAD JUMPS, PRESS C TO CHAT ===================
;====== PRESS 2 TO UNLOCK MOUSE FROM CENTER (USEFUL TO PLAY ===============
;====== AND ALSO BE ABLE TO MULTITASK IF NEED BE) PRESS 1 TO RELOCK =======
;====== MOUSE BACK TO CENTER OF SCREEN ====================================
;====== YEAH EVERYONE HAS THE SOURCE ... DON'T CHEAT! =====================


;====== INCLUDES ==========================================================

Include "gnet_v1\gnet_inc.bb"; http://www.blitzbasic.com/toolbox/toolbox.php?tool=61
;==========================================================================



;====== GRAPHICS ==========================================================

Graphics3D(640, 480, 16, 2)
SetBuffer BackBuffer()
HidePointer

;==========================================================================



;====== COLLISIONS ========================================================

Global ppfps_BODY = 1
Global ppfps_SCENE = 2
Collisions(ppfps_BODY,ppfps_SCENE, 2, 2)
Collisions(ppfps_BODY,ppfps_BODY, 2, 2)

;==========================================================================



;====== GLOBALS ===========================================================

Global ppfps_gameState$ = "Title Screen"

;Mousehit Vars
Global ppfps_mb1pressed, ppfps_mb2pressed

;Keyhit Vars
Global ppfps_upKeyPressed, ppfps_downKeyPressed, ppfps_leftKeyPressed, ppfps_rightKeyPressed, ppfps_enterKeyPressed, ppfps_spaceKeyPressed
Global ppfps_chatKeyPressed

;Font Vars
Global ppfps_font, ppfps_bigFont

;Menu Var
Global ppfps_menuCount

;Server Vars
Global ppfps_serverCount
Global ppfps_serverPointer

;Local ppfps_Player Network Vars
Global ppfps_localPlayerName$, ppfps_localPlayerPointer

;Scenery Vars
Global ppfps_level,ppfps_copySprite, ppfps_lightPivot

;Camera Var
Global ppfps_camera

;ppfps_Player Gameplay Vars
Global ppfps_health#, ppfps_shot, ppfps_shot2, ppfps_shot3, ppfps_shotTimer#, ppfps_positionUpdateTimer#, ppfps_serverRefreshTimer#, ppfps_hosting
Global ppfps_vX#, ppfps_vY#, ppfps_vZ#, ppfps_oldX#, ppfps_oldY#, ppfps_oldZ#, ppfps_centerMouse = True, ppfps_enteringChat = False, ppfps_chat$
Global ppfps_copyPowerUp, ppfps_weapon$ = "Semi-Automatic", ppfps_powerUpSpawned, ppfps_ammo# = 50, ppfps_randPos, ppfps_randNum
Global ppfps_justDied#,ppfps_RespawnDelay#=7000
;ppfps_Bot globals
Global ppfps_Bots=1,ppfps_Botskillppfps_Bots=1,ppfps_Botppfps_positionUpdateTimer#
Global ppfps_giro=CreatePivot()
;ppfps_Bot movement constants
Const PPFPS_WALK=1
Const PPFPS_BACKUP=2
Const PPFPS_STRAFEL=4
Const PPFPS_STRAFER=8
Const ppfps_jump=16
Const PPFPS_ROTATE=32

;ppfps_Bot state constants

Const ppfps_scan=1
Const PPFPS_ATTACK=2
Const PPFPS_GETAMMO=4
Const PPFPS_RESPAWN=8

;==========================================================================



;====== INCOMING ==========================================================

Dim ppfps_incoming$(100)

;==========================================================================



;====== CONTENTS OF A KEY =================================================

Dim ppfps_keys$(237)
ppfps_LoadKeyboardContent()

;==========================================================================



;====== SHOT ARRAY ========================================================

Dim ppfps_pickedArray(3,3)

;==========================================================================

Type ppfps_Player
	Field iD
	Field  name$
	Field  mesh[10]
	Field  score
End Type

Global Host.ppfps_Player

Type ppfps_Bot
	Field iD
	Field name$
	Field mesh[10]
	Field health#
	Field score
	Field weapon
	Field ammo#
	Field skill#
	Field range#
	Field state 
	Field seq
	Field firing
	Field shot
	Field shot2
	Field shot3
	Field shotTimer#
	Field target
	Field targetItem
	Field justDied#
	Field scantime#
	Field timer#
	Field vX#
	Field vY#
	Field vZ#
	Field oldX#
	Field oldY#
	Field oldZ#
End Type

Dim ppfps_Botnames$(3,10)

Type ppfps_Particle
	Field mesh
	Field name$
	Field alpha#
	Field vX#
	Field vY#
	Field vZ#
End Type

Type ppfps_chatMsg
	Field msg$
	Field  born#
	Field  from
End Type

Type ppfps_powerUp
	Field mesh
	Field  name$
	Field iD
End Type

;==========================================================================



;====== INITIAL SETUP =====================================================

ppfps_Setup("Title Screen")

;==========================================================================



;==========================================================================
.Main
;====== RUN ===============================================================
ppfps_period=1000 / 30
ppfps_tweentime = MilliSecs()-ppfps_period
While 1
;====== CALCULATE TWEEN ===================================================
	Repeat
		ppfps_elapsed = MilliSecs() - ppfps_tweentime
	Until ppfps_elapsed
	ppfps_ticks = ppfps_elapsed/ppfps_period
	ppfps_tween# = Float(ppfps_elapsed Mod ppfps_period) / Float(ppfps_period)
;==========================================================================

;====== MAIN LOOP =========================================================
	For ppfps_k=1 To ppfps_ticks
		ppfps_tweentime = ppfps_tweentime+ppfps_period
		If ppfps_k = ppfps_ticks Then CaptureWorld
		If KeyDown(1)
			If Not GNET_RemoveServer("BOT-FPSNetUpdate")
				If ppfps_hosting = True
					RuntimeError "Failed to remove server"
				EndIf
			EndIf
			ClearWorld()
			End
		EndIf
		ppfps_UpdateLogic()
		UpdateWorld
	Next
	RenderWorld(ppfps_tween#)
	ppfps_Draw()
	Flip
Wend
;===========================================================================



;====== UPDATE LOGIC =======================================================

Function ppfps_UpdateLogic()
	
	Select ppfps_gameState$
		
		Case "Title Screen"
			ppfps_UpdateTitleInput()
			ppfps_UpdateTitleScreen()
		Case "Game"
			ppfps_DetectPowerUpCollision()
			ppfps_UpdateGameInput()
			ppfps_ControlChat()
			ppfps_UpdateLights()
			ppfps_UpdateBots()
			ppfps_UpdateCamera()
			ppfps_UpdatePlayerMovement()
			ppfps_HostSpawnPowerups()
			ppfps_UpdateParticles()
			ppfps_UpdateNetwork()
			ppfps_UpdateCenterMouse()
	End Select
	
End Function

;===========================================================================



;====== DRAW ===============================================================

Function ppfps_Draw()

	Select ppfps_gameState$
	
		Case "Title Screen"
			ppfps_DrawTitleScreen()
		Case "Game"
			ppfps_DrawGamePlay()
		
	End Select
	
End Function

;===========================================================================



;====== UPDATE TITLE INPUT =================================================

Function ppfps_UpdateTitleInput()

	ppfps_mb1pressed = MouseHit(1)
	
	ppfps_mb2pressed = MouseHit(2)
	
	ppfps_upKeyPressed = KeyHit(17) Or KeyHit(200)
	
	ppfps_downKeyPressed = KeyHit(31) Or KeyHit(208)
	
	ppfps_leftKeyPressed = KeyHit(30) Or KeyHit(203)
	
	ppfps_rightKeyPressed = KeyHit(32) Or KeyHit(205)
	
	ppfps_enterKeyPressed = KeyHit(28)
	
	ppfps_spaceKeyPressed = KeyHit(57)
	
		
End Function

;===========================================================================



;====== UPDATE GAME INPUT ==================================================

Function ppfps_UpdateGameInput()

	ppfps_chatKeyPressed = KeyHit(46)
	
	ppfps_enterKeyPressed = KeyHit(28)
		
End Function

;===========================================================================



;====== UPDATE CENTER MOUSE ================================================

Function ppfps_UpdateCenterMouse()

	If ppfps_centerMouse = True
		MoveMouse(ppfps_CorrectX(320), ppfps_CorrectY(240))
	EndIf
	If KeyHit(2)
		ppfps_centerMouse = True
	EndIf
	If KeyHit(3)
		ppfps_centerMouse = False
	EndIf

End Function

;===========================================================================



;====== UPDATE TITLE SCREEN ================================================

Function ppfps_UpdateTitleScreen()

	If ppfps_upKeyPressed And ppfps_menuCount > 0
		ppfps_menuCount = ppfps_menuCount - 1
	EndIf
	
	If ppfps_downKeyPressed And ppfps_menuCount < ppfps_serverCount - 1
		ppfps_menuCount = ppfps_menuCount + 1
	EndIf
	
	If ppfps_enterKeyPressed
		ppfps_gns.GNET_Server = Object.GNET_Server(ppfps_serverPointer)
		If JoinNetGame(ppfps_gns\server$, ppfps_gns\ip$)
			FlushKeys
			Color(255,0,0)
			Locate(200,130)
			ppfps_localPlayerName$ = Input("Name - ")
			Delay(500)
			FlushKeys
			ppfps_player.ppfps_Player = New ppfps_Player
			ppfps_localPlayerPointer = Handle(ppfps_player.ppfps_Player)
			ppfps_player\iD = CreateNetPlayer(ppfps_localPlayerName$)
			ppfps_Setup("Game")
			ppfps_hosting = False
		EndIf
	EndIf
	
	If ppfps_spaceKeyPressed
		FlushKeys
		Color(255,0,0)
		Locate(150,130)
		ppfps_localPlayerName$ = Input("Name - ")
		Locate(150,150)
		ppfps_Bots=Input("Number of Bots (Recommended Max 8)(0=None) - ")
		If ppfps_Bots>8 ppfps_Bots=8
		If ppfps_Bots>0 Then 
		ppfps_BotNamesFill()
			Locate(150,170)
			ppfps_Botskill=Input("ppfps_Bot Skill 1-10 (0=Random) - ")
		End If
		If ppfps_Botskill>10 ppfps_Botskill=10
		Delay(500)
		FlushKeys
		If HostNetGame(ppfps_localPlayerName$) = 2
			
			If GNET_AddServer("BOT-FPSNetUpdate", ppfps_localPlayerName$)
				ppfps_player.ppfps_Player = New ppfps_Player
				ppfps_localPlayerPointer = Handle(ppfps_player.ppfps_Player)
				ppfps_player\iD = CreateNetPlayer(ppfps_localPlayerName$)
				ppfps_Setup("Game")
				ppfps_hosting = True
				ppfps_serverRefreshTimer# = MilliSecs()
				Host.ppfps_Player=ppfps_player.ppfps_Player
			Else
				RuntimeError("Failed to Add Server!")
			EndIf
				
		Else
			
			RuntimeError("Failed to Create Net Game!")	
				
		EndIf
		
		For x=1 To ppfps_Bots 
		ppfps_BotsHostSpawn()
		Next
		
	EndIf
		
		

End Function



;===========================================================================



;====== UPDATE PLAYER MOVEMENT =============================================

Function ppfps_UpdatePlayerMovement()

	If ppfps_enteringChat Return

	ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
	
	ppfps_oldX# = EntityX(ppfps_player\mesh[1])
	ppfps_oldY# = EntityY(ppfps_player\mesh[1])
	ppfps_oldZ# = EntityZ(ppfps_player\mesh[1])
	
	If KeyDown(17) Or KeyDown(200)
		MoveEntity(ppfps_player\mesh[1], 0, 0, .5)
	EndIf
	If KeyDown(31) Or KeyDown(208)
		MoveEntity(ppfps_player\mesh[1], 0, 0, -.5)
	EndIf
	If KeyDown(30) Or KeyDown(203)
		MoveEntity(ppfps_player\mesh[1], -.5, 0, 0)
	EndIf
	If KeyDown(32) Or KeyDown(205)
		MoveEntity(ppfps_player\mesh[1], .5, 0, 0)
	EndIf
	
	ppfps_numColls = CountCollisions(ppfps_player\mesh[1])
	
	If ppfps_numColls = 0
		ppfps_vY# = ppfps_vY# - .01
	Else
		ppfps_vY# = 0
	EndIf
	
	If (KeyDown(57) Or KeyDown(82)) And ppfps_numColls > 0
		ppfps_vY# = .2
	EndIf
		
	TranslateEntity(ppfps_player\mesh[1],0, ppfps_vY#, 0)		
	RotateEntity(ppfps_player\mesh[1], 0, EntityYaw(ppfps_player\mesh[1]) - MouseXSpeed(), 0)
	
	ppfps_vX# = EntityX(ppfps_player\mesh[1]) - ppfps_oldX#
	ppfps_vY# = EntityY(ppfps_player\mesh[1]) - ppfps_oldY#
	ppfps_vZ# = EntityZ(ppfps_player\mesh[1]) - ppfps_oldZ#
			
End Function


;===========================================================================



;====== UPDATE BOTS =======================================================
Function ppfps_UpdateBots()
	ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)

	If Not ppfps_hosting Then Return
	
	
	
	For b.ppfps_Bot=Each ppfps_Bot
	
	; State Defaults
	If b\state=0 b\state=ppfps_scan
	If b\seq=0 b\seq=PPFPS_ROTATE
	If b\ammo#<.1 Then
		  b\state=PPFPS_GETAMMO
	End If
	
	Select b\state
	
	Case PPFPS_RESPAWN
		b\seq=PPFPS_ROTATE
		If b\timer#+ppfps_RespawnDelay# < MilliSecs()
			EntityAlpha b\mesh[1],1
			EntityAlpha b\mesh[2],1
			b\state=ppfps_scan
			SendNetMsg(96,b\iD+",1-",ppfps_player\iD,0,1)
		End If
	
	Case Scan
	
		b\seq=PPFPS_ROTATE
		If b\scantime# + 1000 <MilliSecs()
		
			d1=10000
			d2=0
			
			For op.ppfps_Player=Each ppfps_Player
				d2= EntityDistance(b\mesh[1],op\mesh[1])
				If d2=<d1 Then 
					If ppfps_LineOfSight3D(b\mesh[1],op\mesh[1],1000.00)
						b\target=op\mesh[1]
						b\state=PPFPS_ATTACK
					End If
					d1=d2
				End If
			Next
			
			
			
			b\scantime#=MilliSecs()
		End If	
		
	Case PPFPS_ATTACK
		
		If b\target >0
			PositionEntity ppfps_giro,EntityX(b\mesh[1]),EntityY(b\mesh[1]),EntityZ(b\mesh[1])
			PointEntity ppfps_giro,b\target
					
					turn#=20-b\skill#
					tx#=ppfps_curveangle#(EntityPitch(ppfps_giro),EntityPitch(b\mesh[1]),turn#)
					ty#=ppfps_curveangle#(EntityYaw(ppfps_giro),EntityYaw(b\mesh[1]),turn#)
					
					RotateEntity b\mesh[1],tx#,ty#,0
					If EntityDistance(b\mesh[1],b\target)<b\range# 
						b\seq=ppfps_jump
						If ppfps_LineOfSight3D(b\mesh[1],b\target,b\range,90-b\skill)
							b\firing=True;not yet used !
							shoot=Rand(1,15-b\skill)
						If shoot=1 Then	ppfps_Botshoot(b.ppfps_Bot)
						Else
							b\firing=False
						End If
					Else
						b\seq=PPFPS_WALK
						b\firing=False
					End If
		Else
					b\state=ppfps_scan
					b\firing=False
					
		End If	
		
		
		Case PPFPS_GETAMMO
		b\seq=PPFPS_WALK
		If ppfps_powerUpSpawned
			pup.ppfps_powerUp=First ppfps_powerUp
			b\targetItem=pup\mesh
			PositionEntity ppfps_giro,EntityX(b\mesh[1]),EntityY(b\mesh[1]),EntityZ(b\mesh[1])
			PointEntity ppfps_giro,b\targetItem
					
					turn#=10-b\skill#;allow more agility when collectimg items
					tx#=ppfps_curveangle#(EntityPitch(ppfps_giro),EntityPitch(b\mesh[1]),turn#)
					ty#=ppfps_curveangle#(EntityYaw(ppfps_giro),EntityYaw(b\mesh[1]),turn#)
					
					RotateEntity b\mesh[1],tx#,ty#,0
					If ppfps_BotPowerUpCollisionDetect(b.ppfps_Bot) 
						b\state=PPFPS_ATTACK						
					End If
		
		End If			
		



	End Select
	Next
	
	
	
.moveppfps_Bot	
	
	For b.ppfps_Bot = Each ppfps_Bot
	ppfps_numColls = CountCollisions(b\mesh[1])
	b\oldX# = EntityX(b\mesh[1])
	b\oldY# = EntityY(b\mesh[1])
	b\oldZ# = EntityZ(b\mesh[1])
	
	If (b\seq And PPFPS_WALK) ;KeyDown(17) Or KeyDown(200)
		If b\target>0
			d#=EntityDistance(b\mesh[1],b\target)
				
			 
					speed#=ppfps_curvevalue#(d,b\vZ#,20)
					If speed#>.5 speed#=.5
					
					MoveEntity(b\mesh[1], 0, 0, speed#)
				
		Else
			MoveEntity(b\mesh[1], 0, 0, .5)
		End If
	EndIf
	If b\seq And PPFPS_BACKUP;KeyDown(31) Or KeyDown(208)
		MoveEntity(b\mesh[1], 0, 0, -.5)
	EndIf
	If b\seq And PPFPS_STRAFEL;KeyDown(30) Or KeyDown(203)
		MoveEntity(b\mesh[1], -.5, 0, 0)
	EndIf
	If b\seq And PPFPS_STRAFER;KeyDown(32) Or KeyDown(205)
		MoveEntity(b\mesh[1], .5, 0, 0)
	EndIf
	If b\seq And PPFPS_ROTATE
		TurnEntity (b\mesh[1],0,.5+(b\skill/20),0)
	End If
	
	;ppfps_numColls = CountCollisions(b\mesh[1])
	
	If ppfps_numColls = 0
		b\vY# = b\vY# - .01
	Else
		b\vY# = 0
	EndIf
	
	If b\seq = ppfps_jump And (EntityCollided(b\mesh[1],ppfps_SCENE)>0)
		b\vY# = .2
	EndIf
		
	TranslateEntity(b\mesh[1],0, b\vY#, 0)		
	
	
	b\vX# = EntityX(b\mesh[1]) - b\oldX#
	b\vY# = EntityY(b\mesh[1]) - b\oldY#
	b\vZ# = EntityZ(b\mesh[1]) - b\oldZ#
			


	Next


	If ppfps_Botppfps_positionUpdateTimer# + 50 < MilliSecs()

		ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
		For b.ppfps_Bot = Each ppfps_Bot
			SendNetMsg(91, b\iD+","+EntityX(b\mesh[1]) + "," + EntityY(b\mesh[1]) + "," + EntityZ(b\mesh[1]) + "," + EntityPitch(b\mesh[1]) + "," + EntityYaw(b\mesh[1]) + "," + b\score+ "," +b\ammo+ "," +b\state + "," +b\seq + "," +b\health + "," + b\weapon +" ," + b\range +"-", ppfps_player\iD, 0, 0)
		
		Next
		ppfps_Botppfps_positionUpdateTimer# = MilliSecs()
	EndIf


End Function

Function ppfps_BotShoot(b.ppfps_Bot)


ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)

		TFormVector 0,0,100,b\mesh[1],0
		Local dx# = TFormedX()
		Local dy# = TFormedY()
		Local dz# = TFormedZ()


	b\shot = 0
	b\shot2 = 0
	b\shot3 = 0
			Select b\weapon
		
			Case 1;"Semi-Automatic"
				If b\shotTimer# + 200 < MilliSecs() And b\ammo# > 0
					b\ammo# = b\ammo# - 1
					b\shot = LinePick(EntityX(b\mesh[1]),EntityY(b\mesh[1]),EntityZ(b\mesh[1]), dx#,dy#,dz#,0.1)
					b\shotTimer# = MilliSecs()
					If b\shot 
						ppfps_ParticlesCreate(PickedX(), PickedY(), PickedZ(), 50, 50, 50, 10)
						SendNetMsg(4,"1," + PickedX() + "," + PickedY() + "," + PickedZ() + "-", ppfps_player\iD, 0, 0)
					EndIf
				EndIf
			Case 2;"Shotgun"
				If b\shotTimer# + 750 < MilliSecs() And b\ammo# >= 3
					b\ammo# = b\ammo# - 3
					For i = 1 To 3
						
						Select i
							Case 1
								b\shot = LinePick(EntityX(b\mesh[1])-0.5,EntityY(b\mesh[1]),EntityZ(b\mesh[1]), dx#-0.5,dy#,dz#,0.01)
							Case 2
								b\shot2 = LinePick(EntityX(b\mesh[1]),EntityY(b\mesh[1]),EntityZ(b\mesh[1]), dx#,dy#,dz#,0.01)
							Case 3
								b\shot3 = LinePick(EntityX(b\mesh[1])+0.5,EntityY(b\mesh[1]),EntityZ(b\mesh[1]), dx#+0.5,dy#,dz#,0.01)
						End Select
					
						ppfps_pickedArray(i,1) = PickedX()
						ppfps_pickedArray(i,2) = PickedY()
						ppfps_pickedArray(i,3) = PickedZ()
					
					Next
					b\shotTimer# = MilliSecs()
					If b\shot Or b\shot2 Or b\shot3
						For i = 1 To 3
							If (i = 1 And b\shot <> 0) Or (i = 2 And b\shot2 <> 0) Or (i = 3 And b\shot3 <> 0)
								ppfps_ParticlesCreate(ppfps_pickedArray(i,1), ppfps_pickedArray(i,2), ppfps_pickedArray(i,3), 200, 200, 200, 10)
							EndIf
						Next
						SendNetMsg(4,"2," + ppfps_pickedArray(1,1) + "," + ppfps_pickedArray(1,2) + "," + ppfps_pickedArray(1,3) + "," + ppfps_pickedArray(2,1) + "," + ppfps_pickedArray(2,2) + "," + ppfps_pickedArray(2,3) + "," + ppfps_pickedArray(3,1) + "," + ppfps_pickedArray(3,2) + "," + ppfps_pickedArray(3,3) + "-", ppfps_player\iD, 0, 0)
					EndIf
				EndIf
			Case 3;"Flamethrower"
				If b\shotTimer# + 50 < MilliSecs() And b\ammo# >= .1
					b\ammo# = b\ammo# - .1
					pppfps_vX# = b\vX# + (EntityX(b\mesh[3], True) - EntityX(b\mesh[2], True)) / 10
					pppfps_vY# = b\vY# + (EntityY(b\mesh[3], True) - EntityY(b\mesh[2], True)) / 10
					pppfps_vZ# = b\vZ# + (EntityZ(b\mesh[3], True) - EntityZ(b\mesh[2], True)) / 10
					
					SendNetMsg(90, pppfps_vX# + "," + pppfps_vY# + "," + pppfps_vZ# + ","+b\iD+ "-", ppfps_player\iD, 0, 0)
					ppfps_ParticlesCreate(EntityX(b\mesh[2],True), EntityY(b\mesh[2], True), EntityZ(b\mesh[2], True), 255, 200, 100, 10,  pppfps_vX# + Rnd(-.05, .05), pppfps_vY# + Rnd(-.05, .05), pppfps_vZ#, "Flame")
					b\shot = LinePick(EntityX(b\mesh[1], True), EntityY(b\mesh[1], True), EntityZ(b\mesh[1], True), EntityX(b\mesh[3], True) - EntityX(b\mesh[1], True), EntityY(b\mesh[3], True) - EntityY(b\mesh[1], True), EntityZ(b\mesh[3], True) - EntityZ(b\mesh[1], True))
					ppfps_shotTimer# = MilliSecs()
					If b\shot
						ppfps_ParticlesCreate(PickedX(), PickedY(), PickedZ(), 255, 200, 100, 10, "Flame")
						SendNetMsg(4,"3," + PickedX() + "," + PickedY() + "," + PickedZ() + "-", ppfps_player\iD, 0, 0)
					EndIf
				EndIf
		
		End Select
					
	



;;;
	For op.ppfps_Player = Each ppfps_Player
		Select b\weapon
		
			Case 1;"Semi-Automatic"
				If b\shot = op\mesh[1]
					
					ppfps_ParticlesCreate(PickedX(), PickedY(), PickedZ(), 255, 0, 0, 10)
					
					If Handle(op.ppfps_Player)=ppfps_localPlayerPointer 
						ppfps_health#=ppfps_health#-5
						SendNetMsg(5, "1,10-", ppfps_player\iD, 0, 0)
					Else
						SendNetMsg(2, "1,"+b\iD+"-", ppfps_player\iD, op\iD, 0)	
					End If
				EndIf
			Case 2;"Shotgun"
				If b\shot = op\mesh[1] Or ppfps_shot2 = op\mesh[1] Or ppfps_shot3 = op\mesh[1]
					Select op\mesh[1]
						Case b\shot
								If Handle(op.ppfps_Player)=ppfps_localPlayerPointer 
									ppfps_health#=ppfps_health#-5
									SendNetMsg(5, "2,10-", ppfps_player\iD, 0, 0)
									
								End If

							ppfps_ParticlesCreate(ppfps_pickedArray(1, 1), ppfps_pickedArray(1, 2), ppfps_pickedArray(1, 3), 255, 0, 0, 10)
						Case b\shot2
							If Handle(op.ppfps_Player)=ppfps_localPlayerPointer 
									ppfps_health#=ppfps_health#-5
									SendNetMsg(5, "2,10-", ppfps_player\iD, 0, 0)
									
								End If
							ppfps_ParticlesCreate(ppfps_pickedArray(2, 1), ppfps_pickedArray(2, 2), ppfps_pickedArray(2, 3), 255, 0, 0, 10)
						Case b\shot3
							If Handle(op.ppfps_Player)=ppfps_localPlayerPointer 
									ppfps_health#=ppfps_health#-5
									SendNetMsg(5, "2,10-", ppfps_player\iD, 0, 0)
								
								End If
							ppfps_ParticlesCreate(ppfps_pickedArray(3, 1), ppfps_pickedArray(3, 2), ppfps_pickedArray(3, 3), 255, 0, 0, 10)
					End Select
					SendNetMsg(2, "2," + Left(Str(ppfps_shot), 1) + "," + Left(Str(ppfps_shot2), 1) + "," + Left(Str(ppfps_shot3), 1)+","+b\iD+"-", ppfps_player\iD, op\iD, 0)
				EndIf
			Case 3;"Flamethrower"
				If b\shot = op\mesh[1]
					ppfps_ParticlesCreate(PickedX(), PickedY(), PickedZ(), 255, 0, 0, 10)
							If Handle(op.ppfps_Player)=ppfps_localPlayerPointer 
									ppfps_health#=ppfps_health#-10
									SendNetMsg(5, "3,10-", ppfps_player\iD, 0, 0)
								Else
									SendNetMsg(2, "3,"+b\iD+"-", ppfps_player\iD, op\iD ,0)	
							End If
					
				EndIf
		
		End Select
		
		
		If ppfps_health# <= 0;PPFPS_RESPAWN
					ppfps_health# = 100
					
				;	SendNetMsg(3, "1", ppfps_player\iD, NetMsgFrom(), 1)
					b\score=b\score+1
					b\target=0
					b\state=ppfps_scan
					ppfps_ParticlesCreate(EntityX(ppfps_player\mesh[1]), EntityY(ppfps_player\mesh[1]), EntityZ(ppfps_player\mesh[1]), 255, 0, 0, 100)
					SendNetMsg(5, "1-",ppfps_player\iD, 0, 0)
					ppfps_justDied# = MilliSecs()
					PositionEntity(ppfps_player\mesh[1],5,1,Rand(0, 50))
		EndIf

		
		
	 	 
	Next
	
	
	
	

	
	
	
	
	
	
	
	


End Function



;===========================================================================



;====== UPDATE LIGHTS ======================================================

Function ppfps_UpdateLights()

	TurnEntity(ppfps_lightPivot, 0, 10, 0)
	
End Function

;===========================================================================
	


;====== UPDATE CAMERA ======================================================

Function ppfps_UpdateCamera()

	ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)

	ppfps_shot = 0
	ppfps_shot2 = 0
	ppfps_shot3 = 0
	If MouseDown(1)
		Select ppfps_weapon$
		
			Case "Semi-Automatic"
				If ppfps_shotTimer# + 200 < MilliSecs() And ppfps_ammo# > 0
					ppfps_ammo# = ppfps_ammo# - 1
					ppfps_shot = CameraPick(ppfps_camera, ppfps_CorrectX(320), ppfps_CorrectY(240))
					ppfps_shotTimer# = MilliSecs()
					If ppfps_shot 
						ppfps_ParticlesCreate(PickedX(), PickedY(), PickedZ(), 50, 50, 50, 10)
						SendNetMsg(4,"1," + PickedX() + "," + PickedY() + "," + PickedZ() + "-", ppfps_player\iD, 0, 0)
					EndIf
				EndIf
			Case "Shotgun"
				If shotTimer# + 750 < MilliSecs() And ppfps_ammo# >= 3
					ppfps_ammo# = ppfps_ammo# - 3
					For i = 1 To 3
						
						Select i
							Case 1
								ppfps_shot = CameraPick(ppfps_camera, ppfps_CorrectX(320 - 20), ppfps_CorrectY(240))
							Case 2
								ppfps_shot2 = CameraPick(ppfps_camera, ppfps_CorrectX(320), ppfps_CorrectY(240))
							Case 3
								ppfps_shot3 = CameraPick(ppfps_camera, ppfps_CorrectX(320 + 20), ppfps_CorrectY(240))
						End Select
					
						ppfps_pickedArray(i,1) = PickedX()
						ppfps_pickedArray(i,2) = PickedY()
						ppfps_pickedArray(i,3) = PickedZ()
					
					Next
					ppfps_shotTimer# = MilliSecs()
					If ppfps_shot Or ppfps_shot2 Or ppfps_shot3
						For i = 1 To 3
							If (i = 1 And ppfps_shot <> 0) Or (i = 2 And ppfps_shot2 <> 0) Or (i = 3 And ppfps_shot3 <> 0)
								ppfps_ParticlesCreate(ppfps_pickedArray(i,1), ppfps_pickedArray(i,2), ppfps_pickedArray(i,3), 200, 200, 200, 10)
							EndIf
						Next
						SendNetMsg(4,"2," + ppfps_pickedArray(1,1) + "," + ppfps_pickedArray(1,2) + "," + ppfps_pickedArray(1,3) + "," + ppfps_pickedArray(2,1) + "," + ppfps_pickedArray(2,2) + "," + ppfps_pickedArray(2,3) + "," + ppfps_pickedArray(3,1) + "," + ppfps_pickedArray(3,2) + "," + ppfps_pickedArray(3,3) + "-", ppfps_player\iD, 0, 0)
					EndIf
				EndIf
			Case "Flamethrower"
				If ppfps_shotTimer# + 50 < MilliSecs() And ppfps_ammo# >= .1
					ppfps_ammo# = ppfps_ammo# - .1
					pppfps_vX# = ppfps_vX# + (EntityX(ppfps_player\mesh[3], True) - EntityX(ppfps_player\mesh[2], True)) / 10
					pppfps_vY# = ppfps_vY# + (EntityY(ppfps_player\mesh[3], True) - EntityY(ppfps_player\mesh[2], True)) / 10
					pppfps_vZ# = ppfps_vZ# + (EntityZ(ppfps_player\mesh[3], True) - EntityZ(ppfps_player\mesh[2], True)) / 10
					
					SendNetMsg(9, pppfps_vX# + "," + pppfps_vY# + "," + pppfps_vZ# + "-", ppfps_player\iD, 0, 0)
					ppfps_ParticlesCreate(EntityX(ppfps_player\mesh[2],True), EntityY(ppfps_player\mesh[2], True), EntityZ(ppfps_player\mesh[2], True), 255, 200, 100, 10,  pppfps_vX# + Rnd(-.05, .05), pppfps_vY# + Rnd(-.05, .05), pppfps_vZ#, "Flame")
					ppfps_shot = LinePick(EntityX(ppfps_camera, True), EntityY(ppfps_camera, True), EntityZ(ppfps_camera, True), EntityX(ppfps_player\mesh[3], True) - EntityX(ppfps_camera, True), EntityY(ppfps_player\mesh[3], True) - EntityY(ppfps_camera, True), EntityZ(ppfps_player\mesh[3], True) - EntityZ(ppfps_camera, True))
					ppfps_shotTimer# = MilliSecs()
					If ppfps_shot
						ppfps_ParticlesCreate(PickedX(), PickedY(), PickedZ(), 255, 200, 100, 10, "Flame")
						SendNetMsg(4,"3," + PickedX() + "," + PickedY() + "," + PickedZ() + "-", ppfps_player\iD, 0, 0)
					EndIf
				EndIf
		
		End Select
					
	EndIf
	
	RotateEntity(ppfps_camera,EntityPitch(ppfps_camera) + MouseYSpeed(),0,0)
	If EntityPitch(ppfps_camera) > 90
		RotateEntity(ppfps_camera, 90, 0, 0)
	EndIf
	If EntityPitch(ppfps_camera) < - 90
		RotateEntity(ppfps_camera, -90, 0, 0)
	EndIf
		
End Function

;===========================================================================



;====== UPDATE NETWORK =====================================================

Function ppfps_UpdateNetwork()

	While RecvNetMsg()
		
		Select NetMsgType()
			
			Case 1;move player
			
				For ppfps_player.ppfps_Player = Each ppfps_Player
					If ppfps_player\iD = NetMsgFrom()
						ppfps_DecodeIncomingMessage(NetMsgData$())
						PositionEntity(ppfps_player\mesh[1], ppfps_incoming$(1), ppfps_incoming$(2), ppfps_incoming$(3))
						RotateEntity(ppfps_player\mesh[1], ppfps_incoming$(4), ppfps_incoming$(5), 0)
						If ppfps_incoming$(6) <> ""	
							ppfps_player\score = ppfps_incoming$(6)
						EndIf
					EndIf
				Next
			
			Case 2;hit,blood & PPFPS_RESPAWN
				ppfps_shotbyppfps_Bot=0
				ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
				ppfps_ParticlesCreate(EntityX(ppfps_player\mesh[1]), EntityY(ppfps_player\mesh[1]), EntityZ(ppfps_player\mesh[1]), 255, 0, 0, 10)
				
				If ppfps_justDied# + 500 < MilliSecs()
					ppfps_DecodeIncomingMessage(NetMsgData$())
					Select ppfps_incoming$(1)
						Case 1
							ppfps_health# = ppfps_health# - 5
							If ppfps_incoming$(2)<>""
								ppfps_shotbyppfps_Bot=ppfps_incoming$(2)
							End If
						Case 2
							If ppfps_incoming$(5)<>""
								ppfps_shotbyppfps_Bot=ppfps_incoming$(5)
							End If

							count = 0
							If ppfps_incoming$(2) <> 0
								count = count + 1
							EndIf
							If ppfps_incoming$(3) <> 0
								count = count + 1
							EndIf
							If ppfps_incoming$(4) <> 0
								count = count + 1
							EndIf
							ppfps_health# = ppfps_health# - (5 * count)
						Case 3
							ppfps_health# = ppfps_health# - 25
							If ppfps_incoming$(2)<>""
								ppfps_shotbyppfps_Bot=ppfps_incoming$(2)
							End If

					End Select
				EndIf
				
				If ppfps_health# <= 0;PPFPS_RESPAWN
					ppfps_health# = 100
					
					SendNetMsg(3, ppfps_shotbyppfps_Bot+"-", ppfps_player\iD, NetMsgFrom(), 1)
					ppfps_ParticlesCreate(EntityX(ppfps_player\mesh[1]), EntityY(ppfps_player\mesh[1]), EntityZ(ppfps_player\mesh[1]), 255, 0, 0, 100)
					SendNetMsg(5, "1-",ppfps_player\iD, 0, 0)
					ppfps_justDied# = MilliSecs()
					
					
					
					PositionEntity(ppfps_player\mesh[1],5,1,Rand(0, 50))
				EndIf
			
			Case 3;increase score for kill: update local player or ppfps_Bot if ppfps_hosting
				ppfps_DecodeIncomingMessage(NetMsgData$())
				If ppfps_incoming$(1)<>"0"
					For b.ppfps_Bot=Each ppfps_Bot
						If b\iD=ppfps_incoming$(1)
							b\score=b\score+1
							b\target=0
							b\state=ppfps_scan
						End If
					Next
				Else
				ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
				ppfps_player\score = ppfps_player\score + 1
				End If
			Case 4;bullet hit particles
			
				ppfps_DecodeIncomingMessage(NetMsgData$())
				
				Select ppfps_incoming$(1)
					
					Case 1
						ppfps_ParticlesCreate(ppfps_incoming$(2), ppfps_incoming$(3), ppfps_incoming$(4), 50, 50, 50, 10)
					Case 2
						ppfps_ParticlesCreate(ppfps_incoming$(2), ppfps_incoming$(3), ppfps_incoming$(4), 255, 255, 255, 10)
						ppfps_ParticlesCreate(ppfps_incoming$(5), ppfps_incoming$(6), ppfps_incoming$(7), 255, 255, 255, 10)
						ppfps_ParticlesCreate(ppfps_incoming$(8), ppfps_incoming$(9), ppfps_incoming$(10), 255, 255, 255, 10)
					Case 3
						ppfps_ParticlesCreate(ppfps_incoming$(2), ppfps_incoming$(3), ppfps_incoming$(4), 255, 200, 100, 10)
				
				End Select
			
			Case 5;player  blood
				ppfps_DecodeIncomingMessage(NetMsgData$())
				If ppfps_incoming(2)<>""
					numparticles=ppfps_incoming$(2)
				Else
					numparticles=100
				End If
				For ppfps_player.ppfps_Player = Each ppfps_Player
					If ppfps_player\iD = NetMsgFrom()
						ppfps_ParticlesCreate(EntityX(ppfps_player\mesh[1]), EntityY(ppfps_player\mesh[1]), EntityZ(ppfps_player\mesh[1]), 255, 0, 0, numparticles)
					EndIf
				Next
			
			Case 6
			
				c.ppfps_chatMsg = New ppfps_chatMsg
				c\msg = NetMsgData$()
				c\born# = MilliSecs()
				c\from = NetMsgFrom()
			
			Case 7;host spawns pup
			
				ppfps_DecodeIncomingMessage(NetMsgData$())
				pup.ppfps_powerUp = New ppfps_powerUp
				pup\mesh = CopyEntity(copyPowerup)
				ppfps_powerUpSpawned = True
								
				PositionEntity(pup\mesh, EntityX(GetChild(ppfps_level, ppfps_incoming$(1)), True), EntityY(GetChild(ppfps_level, ppfps_incoming$(1)), True) + 2, EntityZ(GetChild(ppfps_level, ppfps_incoming$(1)), True))
				
				Select ppfps_incoming$(2)
			
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
				ppfps_randPos = ppfps_incoming$(1)
				ppfps_randNum = ppfps_incoming$(2)
			
			Case 8;pup taken
			
				For pup.ppfps_powerUp = Each ppfps_powerUP
					FreeEntity(pup\mesh)
					Delete(pup)
				Next
				ppfps_powerUpSpawned = False
			
			Case 9;flame particles
			
				For op.ppfps_Player = Each ppfps_Player
					If op\iD = NetMsgFrom()
						ppfps_DecodeIncomingMessage(NetMsgData$())
						ppfps_ParticlesCreate(EntityX(op\mesh[2],True), EntityY(op\mesh[2], True), EntityZ(op\mesh[2], True), 255, 200, 100, 10,  ppfps_incoming$(1) + Rnd(-.05, .05), ppfps_incoming$(2) + Rnd(-.05, .05), ppfps_incoming$(3), "Flame")
					EndIf
				Next
				
			Case 90;ppfps_Bot flame particles
				ppfps_DecodeIncomingMessage(NetMsgData$())
				For b.ppfps_Bot = Each ppfps_Bot
					If b\iD = ppfps_incoming$(4)
						
						ppfps_ParticlesCreate(EntityX(b\mesh[2],True), EntityY(b\mesh[2], True), EntityZ(b\mesh[2], True), 255, 200, 100, 10,  ppfps_incoming$(1) + Rnd(-.05, .05), ppfps_incoming$(2) + Rnd(-.05, .05), ppfps_incoming$(3), "Flame")
					EndIf
				Next
	
				
			Case 91;move & update ppfps_Bot - extra ppfps_Bot info only sent incase host drops out !
				ppfps_DecodeIncomingMessage(NetMsgData$())
				For b.ppfps_Bot=Each ppfps_Bot
						If b\iD=ppfps_incoming$(1)
						PositionEntity(b\mesh[1], ppfps_incoming$(2), ppfps_incoming$(3), ppfps_incoming$(4))
						RotateEntity(b\mesh[1], ppfps_incoming$(5), ppfps_incoming$(6), 0)
						If ppfps_incoming$(7) <> ""	
							b\score = ppfps_incoming$(7)
						EndIf
						;; Only really needed by ppfps_hosting machine
						If ppfps_incoming$(8) <> ""	
							b\ammo# = ppfps_incoming$(8)
						EndIf
						If ppfps_incoming$(9) <> ""	
							b\state = ppfps_incoming$(9)
						EndIf
						If ppfps_incoming$(10) <> ""	
							b\seq = ppfps_incoming$(10)
						EndIf
						If ppfps_incoming$(11) <> ""	
							b\health# = ppfps_incoming$(11)
						EndIf
						If ppfps_incoming$(12) <> ""	
							b\weapon = ppfps_incoming$(12)
						EndIf
						If ppfps_incoming$(13) <> ""	
							b\range = ppfps_incoming$(13)
						EndIf




					EndIf
				Next

			
				
			Case 92; ppfps_Bot hit & resspawn 
				
				ppfps_DecodeIncomingMessage(NetMsgData$())
				
			For b.ppfps_Bot= Each ppfps_Bot
		
				If b\iD=ppfps_incoming$(5)
					
				ppfps_ParticlesCreate(EntityX(b\mesh[1]), EntityY(b\mesh[1]), EntityZ(b\mesh[1]), 255, 0, 0, 10)
			; End Of Client Section	
			If ppfps_hosting
				
				If b\justDied# + 500 < MilliSecs()
					
					Select ppfps_incoming$(1)
						Case 1
							b\health# = b\health# - 5
						Case 2
							count = 0
							If ppfps_incoming$(2) <> 0
								count = count + 1
							EndIf
							If ppfps_incoming$(3) <> 0
								count = count + 1
							EndIf
							If ppfps_incoming$(4) <> 0
								count = count + 1
							EndIf
							b\health# = b\health# - (5 * count)
						Case 3
							b\health# = b\health# - 25
					End Select
				EndIf
				
				
				If b\ammo#>0  Then
					For op.ppfps_player=Each ppfps_player
						If op\iD=NetMsgFrom()
							b\target=op\mesh[1]
							b\state=PPFPS_ATTACK
						End If
					Next
				End If
					
					
					
				If b\health# <= 0;PPFPS_RESPAWN
					ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
					b\health# = 100
					b\target=0
					b\state=PPFPS_RESPAWN
					RotateEntity b\mesh[1],0,Rand(0,360),0
					SendNetMsg(3, "0-", ppfps_player\iD, NetMsgFrom(), 1)
					ppfps_ParticlesCreate(EntityX(b\mesh[1]), EntityY(b\mesh[1]), EntityZ(b\mesh[1]), 255, 0, 0, 100)
					b\justDied# = MilliSecs()
					PositionEntity(b\mesh[1],5,1,Rand(0, 50))
					EntityAlpha b\mesh[1],0
					EntityAlpha b\mesh[2],0
					b\timer#=MilliSecs()
					SendNetMsg(96,b\iD+",0-",ppfps_player\iD,0,1)
				EndIf
				End If
				
		     End If ;ppfps_hosting
			Next

				
				
			Case 95;ppfps_Bot die blood
				
				ppfps_DecodeIncomingMessage(NetMsgData$())
				For b.ppfps_Bot = Each ppfps_Bot
					If b\iD = ppfps_incoming$(1)
						ppfps_ParticlesCreate(EntityX(b\mesh[1]), EntityY(b\mesh[1]), EntityZ(b\mesh[1]), 255, 0, 0, 100)
					EndIf
				Next
	
				
			Case 99; spawn client ppfps_Bots
			
				ppfps_DecodeIncomingMessage(NetMsgData$())

				b.ppfps_Bot = New ppfps_Bot
				b\iD = ppfps_incoming$(1)
				b\mesh[1] = CreateSphere(8)
				b\name$ = ppfps_incoming$(2)
				EntityColor(b\mesh[1], 255, 255, 0)
				PositionEntity(b\mesh[1],ppfps_incoming$(3), ppfps_incoming$(4), ppfps_incoming$(5))
				EntityType(b\mesh[1],ppfps_BODY)
				EntityRadius(b\mesh[1], 1.5)
				EntityPickMode(b\mesh[1], 2)
								
				b\mesh[2] = CreateCube(b\mesh[1])
				PositionEntity(b\mesh[2], 1, -.5, 1)
				ScaleEntity(b\mesh[2], .1, .1, .4)
				EntityColor(b\mesh[2], 20, 20, 20)
				
				b\mesh[3] = CreatePivot(b\mesh[1])
				PositionEntity(b\mesh[3], 0, 0, 5)
				b\weapon=ppfps_incoming$(6)
				b\health=100
		

			Case 96;ppfps_Bot hide/show
				ppfps_DecodeIncomingMessage(NetMsgData$())
				For b.ppfps_Bot = Each ppfps_Bot
					If b\iD = ppfps_incoming$(1)
						Select  ppfps_incoming$(2)
							Case 1
								ShowEntity b\mesh[1]
							Case 0
								HideEntity b\mesh[1]
						End Select
						
						
					EndIf
				Next

			
			Case 100; ppfps_NEW player joined 
			
				ppfps_player.ppfps_Player = New ppfps_Player
				ppfps_player\iD = NetMsgFrom()
				ppfps_player\mesh[1] = CreateSphere(8)
				ppfps_player\name$ = NetPlayerName$(ppfps_player\iD)
				EntityColor(ppfps_player\mesh[1], 0, 255, 0)
				PositionEntity(ppfps_player\mesh[1], 5, 1.5, Rand(50,100))
				EntityType(ppfps_player\mesh[1],ppfps_BODY)
				EntityRadius(ppfps_player\mesh[1], 1.5)
				EntityPickMode(ppfps_player\mesh[1], 2)
								
				ppfps_player\mesh[2] = CreateCube(ppfps_player\mesh[1])
				PositionEntity(ppfps_player\mesh[2], 1, -.5, 1)
				ScaleEntity(ppfps_player\mesh[2], .1, .1, .4)
				EntityColor(ppfps_player\mesh[2], 20, 20, 20)
				
				ppfps_player\mesh[3] = CreatePivot(ppfps_player\mesh[1])
				PositionEntity(ppfps_player\mesh[3], 0, 0, 5)
				
				If ppfps_hosting
					ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
					SendNetMsg(7, ppfps_randPos + "," + ppfps_randNum + "-", ppfps_player\iD#, NetMsgFrom(), 1)
					For b.ppfps_Bot=Each ppfps_Bot
						SendNetMsg(99, b\iD+ "," +b\name$+ "," +EntityX(b\mesh[1],True) + "," + EntityY(b\mesh[1],True)  + "," +EntityZ(b\mesh[1],True) +","+ b\weapon+"-", ppfps_player\iD#, NetMsgFrom(), 1)
					Next
				EndIf
			
			Case 101;player left
			
				For ppfps_player.ppfps_Player = Each ppfps_Player
					For b.ppfps_Bot=Each ppfps_Bot
						If b\target=ppfps_player\mesh[1]
							b\target=0
						End If
					Next

					If ppfps_player\iD = NetMsgFrom()
				
						FreeEntity(ppfps_player\mesh[1])
						Delete(ppfps_player)
					EndIf
				Next
							
			Case 102;host left
			
				GNET_AddServer("FPSNetUpdate", ppfps_localPlayerName$)
				ppfps_hosting = True
				host.ppfps_Player=Object.ppfps_Player(ppfps_localPlayerPointer)
			
		
		End Select
	
	Wend
	
	ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
	If ppfps_positionUpdateTimer# + 50 < MilliSecs()
		SendNetMsg(1, EntityX(ppfps_player\mesh[1]) + "," + EntityY(ppfps_player\mesh[1]) + "," + EntityZ(ppfps_player\mesh[1]) + "," + EntityPitch(ppfps_camera) + "," + EntityYaw(ppfps_player\mesh[1]) + "," + ppfps_player\score + "-", ppfps_player\iD, 0, 0)
		ppfps_positionUpdateTimer# = MilliSecs()
	EndIf
	
.detect_player_shoots_player	
	For op.ppfps_Player = Each ppfps_Player
		Select ppfps_weapon$
		
			Case "Semi-Automatic"
				If ppfps_shot = op\mesh[1]
					ppfps_ParticlesCreate(PickedX(), PickedY(), PickedZ(), 255, 0, 0, 10)
					SendNetMsg(2, "1-", ppfps_player\iD, op\iD, 0)
				EndIf
			Case "Shotgun"
				If ppfps_shot = op\mesh[1] Or ppfps_shot2 = op\mesh[1] Or ppfps_shot3 = op\mesh[1]
					Select op\mesh[1]
						Case ppfps_shot
							ppfps_ParticlesCreate(ppfps_pickedArray(1, 1), ppfps_pickedArray(1, 2), ppfps_pickedArray(1, 3), 255, 0, 0, 10)
						Case ppfps_shot2
							ppfps_ParticlesCreate(ppfps_pickedArray(2, 1), ppfps_pickedArray(2, 2), ppfps_pickedArray(2, 3), 255, 0, 0, 10)
						Case ppfps_shot3
							ppfps_ParticlesCreate(ppfps_pickedArray(3, 1), ppfps_pickedArray(3, 2), ppfps_pickedArray(3, 3), 255, 0, 0, 10)
					End Select
					SendNetMsg(2, "2," + Left(Str(ppfps_shot), 1) + "," + Left(Str(ppfps_shot2), 1) + "," + Left(Str(ppfps_shot3), 1) + "-", ppfps_player\iD, op\iD, 0)
				EndIf
			Case "Flamethrower"
				If ppfps_shot = op\mesh[1]
					ppfps_ParticlesCreate(PickedX(), PickedY(), PickedZ(), 255, 0, 0, 10)
					SendNetMsg(2, "3-", ppfps_player\iD, op\iD, 0)
				EndIf
		
		End Select
	Next
.detect_player_shoots_ppfps_Bots
	If  ppfps_Bots
		
		For b.ppfps_Bot = Each ppfps_Bot
		ret=0
		Select ppfps_weapon$
			
			Case "Semi-Automatic"
				If ppfps_shot = b\mesh[1]
					ppfps_ParticlesCreate(PickedX(), PickedY(), PickedZ(), 255, 0, 0, 10)
					SendNetMsg(92, "1,0,0,0,"+b\iD+"-", ppfps_player\iD, 0, 0)
					If ppfps_hosting b\health# = b\health# - 5:ret=1

				EndIf
			Case "Shotgun"
				If ppfps_shot = b\mesh[1] Or ppfps_shot2 = b\mesh[1] Or ppfps_shot3 = b\mesh[1]
					Select b\mesh[1]
						Case ppfps_shot
						If ppfps_hosting	b\health# = b\health# - 5:ret=1

							ppfps_ParticlesCreate(ppfps_pickedArray(1, 1), ppfps_pickedArray(1, 2), ppfps_pickedArray(1, 3), 255, 0, 0, 10)
						Case ppfps_shot2
						If ppfps_hosting	b\health# = b\health# - 5:ret=1

							ppfps_ParticlesCreate(ppfps_pickedArray(2, 1), ppfps_pickedArray(2, 2), ppfps_pickedArray(2, 3), 255, 0, 0, 10)
						Case ppfps_shot3
						If ppfps_hosting	b\health# = b\health# - 5:ret=1

							ppfps_ParticlesCreate(ppfps_pickedArray(3, 1), ppfps_pickedArray(3, 2), ppfps_pickedArray(3, 3), 255, 0, 0, 10)
					End Select
					SendNetMsg(92, "2," + Left(Str(ppfps_shot), 1) + "," + Left(Str(ppfps_shot2), 1) + "," + Left(Str(ppfps_shot3), 1)+ "," + b\iD+ "-", ppfps_player\iD, 0, 0)
				EndIf
			Case "Flamethrower"
				If ppfps_shot = b\mesh[1]
					If ppfps_hosting	b\health# = b\health# - 25:ret=1
					ppfps_ParticlesCreate(PickedX(), PickedY(), PickedZ(), 255, 0, 0, 10)
					SendNetMsg(92, "3,0,0,0,"+b\iD+"-", ppfps_player\iD, 0, 0)
				EndIf
		
		End Select
		
		
		If ppfps_hosting
		ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
		
		
		
		
			If ret And (b\ammo#>0) 
				b\target=ppfps_player\mesh[1]
				b\state=PPFPS_ATTACK
				b\seq=PPFPS_WALK 
					
			End If

		
				If b\health# <= 0;PPFPS_RESPAWN
					b\health# = 100
					
					
					ppfps_player\score=ppfps_player\score+1
					ppfps_ParticlesCreate(EntityX(b\mesh[1]), EntityY(b\mesh[1]), EntityZ(b\mesh[1]), 255, 0, 0, 100)
					SendNetMsg(95, b\iD+"-",ppfps_player\iD, 0, 0)
					b\justDied# = MilliSecs()
					PositionEntity(b\mesh[1],Rand(0,50),1.5,Rand(0, 50))
					b\target=0
					b\state=PPFPS_RESPAWN
					RotateEntity b\mesh[1],0,Rand(0,360),0
					EntityAlpha b\mesh[1],0
					EntityAlpha b\mesh[2],0
					b\timer#=MilliSecs()
					SendNetMsg(96,b\iD+",0-",ppfps_player\iD,0,1)
				EndIf
		End If
		
	Next	 

		
	End If ;ppfps_Bots	 
	
	;ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
	
	;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	

	
	
	
	;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;	
	
	If ppfps_hosting = True
		
		If ppfps_serverRefreshTimer# + 250000 < MilliSecs()
		;	GNET_RefreshServer( "FPSNetUpdate",ppfps_localPlayerName$ )
			ppfps_serverRefreshTimer# = MilliSecs()
		EndIf
	EndIf
	
End Function

;===========================================================================



;====== UPDATE ppfps_ParticleS ===================================================

Function ppfps_UpdateParticles()

	For ppfps_player.ppfps_Particle = Each ppfps_Particle
		If ppfps_player\alpha# <= 0
			FreeEntity(ppfps_player\mesh)
			Delete(ppfps_player)
		Else
		
			Select ppfps_player\name$
				Case "Flame"
					TranslateEntity(ppfps_player\mesh,ppfps_player\vX#, ppfps_player\vY#, ppfps_player\vZ# )
					ppfps_player\alpha# = ppfps_player\alpha# - Rnd(.3)
					EntityAlpha(ppfps_player\mesh, ppfps_player\alpha#)
				Default
					ppfps_player\vY# = ppfps_player\vY# - .01
					TranslateEntity(ppfps_player\mesh,ppfps_player\vX#, ppfps_player\vY#, ppfps_player\vZ#)
					ppfps_player\alpha# = ppfps_player\alpha# - .02
					EntityAlpha(ppfps_player\mesh, ppfps_player\alpha#)
		
			End Select
		
		EndIf
	Next
	
End Function

;===========================================================================


;====== DRAW TITLE SCREEN ==================================================

Function ppfps_DrawTitleScreen()

	Cls
	ppfps_FPSNetText(ppfps_font, 320, 0, String$("+",80), 1, 1, 100, 100, 100)
	ppfps_FPSNetText(ppfps_font, 320, 480, String$("+",80), 1, 1, 100, 100, 100)
	
	For i = 0 To 640 Step 640
		For j = 10 To 470 Step 10
			ppfps_FPSNetText(ppfps_font, i, j, "+", 1, 1, 100, 100, 100)
		Next
	Next

	ppfps_FPSNetText(ppfps_bigFont, 320, 30, "BOT-FPSNet:", 1, 1, 255, 0, 0)
	ppfps_FPSNetText(ppfps_font, 320, 60, "A NETWORKED First Person Shooter Example", 1, 1, 100, 100, 100)
	ppfps_FPSNetText(ppfps_font, 320, 80, "Press Up/Down to Select a Game to Join!", 1, 1, 100, 100, 100)
	ppfps_FPSNetText(ppfps_font, 320, 100, "Press Space to Host a New Game!", 1, 1, 100, 100, 100)
	ppfps_FPSNetText(ppfps_font, 320, 120, "Games In Progress:" , 1, 1, 200, 255, 150)
	
	ppfps_serverCount = 0
	For ppfps_gns.GNET_Server = Each GNET_Server
	
		Select ppfps_serverCount
			
			Case ppfps_menuCount
				ppfps_FPSNetText(ppfps_font, 320, 160 + 20 * ppfps_serverCount, ppfps_gns\server$ + " - Press Enter to Join ...", 1, 1, 255, 255, 255)
				ppfps_serverPointer = Handle(ppfps_gns)
			Default
				ppfps_FPSNetText(ppfps_font, 320, 160 + 20 * ppfps_serverCount, ppfps_gns\server$, 1, 1, 100, 100, 100)
		
		End Select
		ppfps_serverCount = ppfps_serverCount + 1
		
	Next	
	
End Function

;===========================================================================



;====== DRAW GAMEPLAY ======================================================

Function ppfps_DrawGamePlay()

	Select ppfps_weapon$
		
		Case "Semi-Automatic"
			ppfps_FPSNetText(ppfps_font, 320, 240, "+", 1, 1, 255, 255, 255)
		Case "Shotgun"
			ppfps_FPSNetText(ppfps_font, 300, 240, "+", 1, 1, 255, 255, 255)
			ppfps_FPSNetText(ppfps_font, 320, 240, "+", 1, 1, 255, 255, 255)
			ppfps_FPSNetText(ppfps_font, 340, 240, "+", 1, 1, 255, 255, 255)
		Case "Flamethrower"
			ppfps_FPSNetText(ppfps_font, 320, 240, "()", 1, 1, 255, 200, 100)
	
	End Select
			
	ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
	ppfps_FPSNetText(ppfps_font, 0, 0, "Your Score: " + ppfps_player\score, 0, 0, 255, 0, 0)
	
	ppfps_FPSNetText(ppfps_font, 150, 0, "Health: ", 0, 0, 0, 255, 0)
	Color(255, 0, 0)
	Rect(ppfps_CorrectX(221), ppfps_CorrectY(4), ppfps_CorrectX(ppfps_health#), ppfps_CorrectY(10), 1)
	Color(255, 255, 255)
	Rect(ppfps_CorrectX(220), ppfps_CorrectY(2), ppfps_CorrectX(102), ppfps_CorrectY(14), 0)
	
	ppfps_FPSNetText(ppfps_font, 400, 0, "Ammo: " + Int(ppfps_ammo#), 0, 0, 255, 255, 255)

	
	printI = 50
	For c.ppfps_chatMsg = Each ppfps_chatMsg
		
		secondsAround = (MilliSecs() - c\born#) / 50
		ppfps_FPSNetText(ppfps_font, 0, 0 + printI, NetPlayerName(c\from) + " - " + c\msg$, 0, 0, 255 - secondsAround, 255 - secondsAround, 255 - secondsAround)
		printI = printI + 15
		
	Next
	
	If ppfps_enteringChat = True
	
		ppfps_FPSNetText(ppfps_font, 0, 460, ppfps_localPlayerName$ + " - " + ppfps_chat$, 0, 0, 255, 255, 255)
	
	EndIf
	
	For ppfps_player.ppfps_Player = Each ppfps_Player
	
		If Handle(ppfps_player.ppfps_Player) <> ppfps_localPlayerPointer
			CameraProject(ppfps_camera, EntityX(ppfps_player\mesh[1]), EntityY(ppfps_player\mesh[1]), EntityZ(ppfps_player\mesh[1]))
			ppfps_FPSNetText(ppfps_font, ProjectedX(), ProjectedY() - 20, ppfps_player\name$ + "  -  " + ppfps_player\score, 1, 1, 255, 255, 255)
		EndIf

	Next
	
	For b.ppfps_Bot = Each ppfps_Bot
	
			If b\state<>PPFPS_RESPAWN
				CameraProject(ppfps_camera, EntityX(b\mesh[1]), EntityY(b\mesh[1]), EntityZ(b\mesh[1]))
				ppfps_FPSNetText(ppfps_font, ProjectedX(), ProjectedY() - 20, b\name$ + "  -  " + b\score, 1, 1, 255, 255, 255)
			End If

	Next	
	
End Function

;===========================================================================



;====== DETECT POWERUP COLLISION ===========================================

Function ppfps_DetectPowerUpCollision()

	If Not ppfps_powerUpSpawned Return
	
	ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
	For pup.ppfps_powerUp = Each ppfps_powerUp
		
		If EntityDistance(ppfps_player\mesh[1], pup\mesh) < 3
			
			If ppfps_weapon$ = pup\name$
				Select ppfps_weapon$
					Case "Semi-Automatic"	
						ppfps_ammo# = ppfps_ammo# + 50
					Case "Shotgun"
						ppfps_ammo# = ppfps_ammo# + 45
					Case "Flamethrower"
						ppfps_ammo# = ppfps_ammo# + 30
				End Select
			Else
				Select pup\name$
					Case "Semi-Automatic"
						ppfps_ammo# = 50
					Case "Shotgun"
						ppfps_ammo# = 45
					Case "Flamethrower"
						ppfps_ammo# = 30
				End Select 
			EndIf
			ppfps_weapon$ = pup\name$
			ppfps_powerUpSpawned = False
			FreeEntity(pup\mesh)
			Delete(pup)
			SendNetMsg(8, "", ppfps_player\iD, 0, 1)
		
		EndIf
	
	Next
	
End Function

;===========================================================================

;====== DETECT BOT POWERUP COLLISION ===========================================

Function ppfps_BotPowerUpCollisionDetect(b.ppfps_Bot)

	
	
	ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
	
	
		For pup.ppfps_powerUp = Each ppfps_powerUp
		If EntityDistance(b\mesh[1], pup\mesh) < 3
			
			If b\weapon = pup\iD
				Select b\weapon
					Case 1;"Semi-Automatic"	
						b\ammo# = b\ammo# + 50
					Case 2;"Shotgun"
						b\ammo# = b\ammo# + 45
					Case 3;"Flamethrower"
						b\ammo# = b\ammo# + 30
				End Select
			Else
				Select pup\iD
					Case 1;"Semi-Automatic"
						b\ammo# = 50
						b\range#=40
					Case 2;"Shotgun"
						b\ammo# = 45
						b\range#=10
					Case 3;"Flamethrower"
						b\ammo# = 30
						b\range=5
				End Select 
				b\weapon = pup\iD

			EndIf
			b\weapon = pup\iD
			ppfps_powerUpSpawned = False
			FreeEntity(pup\mesh)
			Delete(pup)
			SendNetMsg(8, "", ppfps_player\iD, 0, 1)
			Return 1
		EndIf
	
		Return 0

	Next
End Function

;===========================================================================

;====== HOST SPAWN POWERUPS ================================================

Function ppfps_HostSpawnPowerups()

	If (Not ppfps_hosting) Or (ppfps_powerUpSpawned) 
		Return
	EndIf
	
	ppfps_randPos = Rand(1, CountChildren(ppfps_level))
	
	pup.ppfps_powerUp = New ppfps_powerUp
	pup\mesh = CopyEntity(ppfps_copyPowerUp)
	PositionEntity(pup\mesh, EntityX(GetChild(ppfps_level, ppfps_randPos), True), EntityY(GetChild(ppfps_level, ppfps_randPos), True) + 2, EntityZ(GetChild(ppfps_level, ppfps_randPos), True))
	ppfps_randNum = Rand(1,3)
	Select ppfps_randNum
		
		Case 1
			pup\name$ = "Semi-Automatic"
			pup\iD=1
			EntityColor(pup\mesh, 255, 255, 255)
		Case 2
			pup\name$ = "Shotgun"
			pup\iD=2
			EntityColor(pup\mesh, 100, 100, 255)
		Case 3
			pup\name$ = "Flamethrower"
			pup\iD=3
			EntityColor(pup\mesh, 200, 150, 100)
			
	End Select
	ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
	SendNetMsg(7, ppfps_randPos + "," + ppfps_randNum + "-", ppfps_player\iD#, 0, 1)
	ppfps_powerUpSpawned = True
	
	
End Function

;+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

;++++++Spawn ppfps_Bots+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


Function ppfps_BotsHostSpawn()

If (Not ppfps_hosting) Return
	
	
	ppfps_randPosX = Rand(1, 40)
	ppfps_randPosZ = Rand(1, 40)
	ppfps_randPosY=1.5
	
	b.ppfps_Bot = New ppfps_Bot
	b\iD = Handle(b.ppfps_Bot)
	
	b\mesh[1] = CreateSphere(8)
	PositionEntity(b\mesh[1],ppfps_randPosX, 1.5,ppfps_randPosZ)
	EntityColor(b\mesh[1], 255, 255, 0)
	EntityType(b\mesh[1],ppfps_BODY)
	EntityRadius(b\mesh[1], 1.5)
	EntityPickMode(b\mesh[1], 2)
	b\mesh[2] = CreateCube(b\mesh[1])
	PositionEntity(b\mesh[2], 1, -.5, 1)
	ScaleEntity(b\mesh[2], .1, .1, .4)
	EntityColor(b\mesh[2], 20, 20, 20)
				
	b\mesh[3] = CreatePivot(b\mesh[1])
	PositionEntity(b\mesh[3], 0, 0, 5)
	b\seq=PPFPS_ROTATE
	b\target=0
	b\state=ppfps_scan
	b\health=100
	b\name$=ppfps_Botnames$(0,Rand(0,9))+ppfps_Botnames$(1,Rand(0,9))+ppfps_Botnames$(2,Rand(0,9))
	If ppfps_Botskill=0 Then
		b\skill=Rand(1,10)
		Else
		b\skill#=ppfps_Botskill
	End If
	ppfps_randNum = Rand(1,3)
	Select ppfps_randNum
		
		Case 1
			b\weapon = 1;"Semi-Automatic"
			b\ammo#=50
			b\range#=40
		
		Case 2
			b\weapon = 2;"Shotgun"
			b\ammo=45
			b\range#=10
		
		Case 3
			b\weapon = 3;"Flamethrower"
			b\ammo#=30
			b\range#=5
		
			
	End Select
	ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
	SendNetMsg(99, b\iD+ "," +b\name$+ "," +ppfps_randPosX + "," +ppfps_randPosY+ "," + ppfps_randPosZ + "," +b\weapon+"-", ppfps_player\iD#, 0, 1)
	ppfps_BotSpawned = True


End Function





;===========================================================================		


;====== CONTROL CHAT =======================================================

Function ppfps_ControlChat()

	If ppfps_chatKeyPressed
		
		ppfps_enteringChat = True
		FlushKeys
				
	EndIf
	
	If ppfps_enterKeyPressed And ppfps_enteringChat = True
	
		ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
		
		c.ppfps_chatMsg = New ppfps_chatMsg
		c\msg$ = ppfps_chat$
		c\born# = MilliSecs() 
		c\from = ppfps_player\iD
		
		SendNetMsg(6, ppfps_chat$, ppfps_player\iD , 0, 0)
				
		ppfps_chat$ = ""
		ppfps_enteringChat = False
				
	EndIf

	If ppfps_enteringChat = True
		
		ppfps_EnterChat()

	EndIf
	
	For c.ppfps_chatMsg = Each ppfps_chatMsg
		
		If c\born# + 10000 < MilliSecs()
		
			Delete(c)
		
		EndIf
	
	Next
	
End Function
	
;===========================================================================



;====== CREATE SOME ppfps_ParticleS ==============================================

Function ppfps_ParticlesCreate(x#, y#, z#, r, g, b, ppfps_ParticlesNum, optppfps_vX# = 0, optppfps_vY# = 0, optppfps_vZ# = 0, passedName$ = "")

	For i = 1 To ppfps_ParticlesNum
		ppfps_player.ppfps_Particle = New ppfps_Particle
		ppfps_player\mesh = CopyEntity(ppfps_copySprite)
		If passedName$ <> ""
			ppfps_player\name$ = passedName$
		EndIf
		PositionEntity(ppfps_player\mesh,x#, y#, z#, True)
		EntityColor(ppfps_player\mesh, r, g, b)
		If optppfps_vX# = 0 
			ppfps_player\vX# = Rnd(-.2, .2)
		Else
			ppfps_player\vx# = optppfps_vX#
		EndIf
		If optppfps_vY# = 0
			ppfps_player\vY# = Rnd(.2)
		Else
			ppfps_player\vY# = optppfps_vY#
		EndIf
		If optppfps_vZ# = 0
			ppfps_player\vZ# = Rnd(-.2, .2)
		Else
			ppfps_player\vZ# = optppfps_vZ#
		EndIf
		ppfps_player\alpha = 1
	Next
	
End Function

;===========================================================================



;====== SETUP ==============================================================

Function ppfps_Setup(mode$)
	SeedRnd MilliSecs()
	Select mode$
	
		Case "Title Screen"
		
			ppfps_font = LoadFont("Arial",20,1)
			ppfps_bigFont = LoadFont("Arial",50,1)
			GNET_ListServers()
			
		Case "Game"
		
			ppfps_gameState$ = "Game"
			
			ppfps_player.ppfps_Player = Object.ppfps_Player(ppfps_localPlayerPointer)
			ppfps_player\mesh[1] = CreatePivot()
			PositionEntity(ppfps_player\mesh[1], 5, 1.5, Rand(0, 50))
			EntityType(ppfps_player\mesh[1],ppfps_BODY)
			EntityRadius(ppfps_player\mesh[1], 1.5)
			EntityPickMode(ppfps_player\mesh[1], 1)
			ppfps_health# = 100
			ppfps_player\score = 0
			ppfps_player\name$ = ppfps_localPlayerName$
			
			ppfps_camera = CreateCamera(ppfps_player\mesh[1])
			CameraRange(ppfps_camera,.1, 500)
			
			ppfps_player\mesh[2] = CreateCube(ppfps_camera)
			PositionEntity(ppfps_player\mesh[2], 1, -.5, 1)
			ScaleEntity(ppfps_player\mesh[2], .1, .1, .4)
			EntityColor(ppfps_player\mesh[2], 20, 20, 20)
			
		
			
			ppfps_player\mesh[3] = CreatePivot(ppfps_camera)
			PositionEntity(ppfps_player\mesh[3], 0, 0, 5)
						
			ppfps_LoadLevel()
			PointEntity(ppfps_player\mesh[1], ppfps_level)
			MoveMouse(ppfps_CorrectX(320), ppfps_CorrectY(240))
			

			
					
	End Select
	
End Function

;===========================================================================



;====== LOAD LEVEL =========================================================

Function ppfps_LoadLevel()

	ppfps_copyPowerUp = CreateSphere()
	HideEntity(ppfps_copyPowerUp)

	ppfps_copySprite = CreateSprite()
	ScaleSprite(ppfps_copySprite, .05, .05)
	EntityFX(ppfps_copySprite, 1+8)
	HideEntity(ppfps_copySprite)
		
	ppfps_level = CreatePlane(16)
	EntityPickMode(ppfps_level, 2)	
	
	For i = -1 To 1 Step 2
		For j = -4 To 4
			cube = CreateCube(ppfps_level)
			EntityType(cube, ppfps_SCENE)
			EntityPickMode(cube, 2)
			EntityColor(cube, 100, 100, 100)
			PositionEntity(cube, i * 10, 1, j * 10)
		
		Next
	Next
	
	For i = 1 To 8
		cube = CreateCube(ppfps_level)
		EntityType(cube, ppfps_SCENE)
		EntityPickMode(cube, 2)
		EntityColor(cube,100,100,100)
		PositionEntity(cube, 0, i * 2, i * 5)
	
	Next
	
	For i = 1 To 8
		cube = CreateCube(ppfps_level)
		EntityType(cube, ppfps_SCENE)
		EntityPickMode(cube, 2)
		EntityColor(cube,100,100,100)
		PositionEntity(cube, 0, i * 2, i * -5)
	
	Next	
	
	EntityType(ppfps_level,ppfps_SCENE)
	
	ppfps_lightPivot = CreatePivot()
	NameEntity(ppfps_lightPivot,"Light Pivot")
	
	light = CreateLight(3,ppfps_lightPivot)
	NameEntity(light, "light1")
	PositionEntity(light,25, 25, 0)
	RotateEntity(light, 90, 0, 0)
	LightRange(light, 500)
	LightColor(light, 255, 0, 0)
	
	light = CreateLight(3,ppfps_lightPivot)
	NameEntity(light, "light2")
	PositionEntity(light,-25, 25, 0)
	RotateEntity(light, 90, 0, 0)
	LightRange(light, 500)
	LightColor(light, 0, 0, 255)
	
	light = CreateLight(3,ppfps_lightPivot)
	NameEntity(light, "light3")
	PositionEntity(light,0, 25, 25)
	RotateEntity(light, 90, 0, 0)
	LightRange(light, 500)
	LightColor(light, 0, 255, 0)
	
End Function

;===========================================================================
	


;====== FPSNET TEXT ========================================================

Function ppfps_FPSNetText(ppfps_fontType, X, Y, theText$, centerX = 0, centerY = 0,r = 255, g = 255, b = 255)

	Color(r, g, b)
	SetFont(ppfps_fontType)
	Text(ppfps_CorrectX(X), ppfps_CorrectY(Y), theText$, centerX, centerY)
	
End Function



;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;
; ppfps_LineOfSight3D()
;
; Usage:
;	observer	= Entity that is looking
;	target		= Entity that the observer is looking for
;	viewrange	= How far can the observer see (in units)
;	viewangle	= How wide is the view of the observer (in degrees)
;
; Created by Mikkel Fredborg - Use as you please
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
Function ppfps_LineOfSight3D(observer,target,viewrange#=10.0,viewangle# = 90.0)

	;distance between observer and target
	Local dist# = EntityDistance(observer,target)

	;check if the target is within viewrange 
	If dist<=viewrange
		
		;observer vector
		TFormVector 0,0,1,observer,0
		Local ox# = TFormedX()
		Local oy# = TFormedY()
		Local oz# = TFormedZ()
	
		;pick vector
		Local dx# = (EntityX(target,True)-EntityX(observer,True))/dist#
		Local dy# = (EntityY(target,True)-EntityY(observer,True))/dist#
		Local dz# = (EntityZ(target,True)-EntityZ(observer,True))/dist#

		;dot product
		Local dot# = ox*dx + oy*dy + oz*dz

		;check if the target is within the viewangle
		If dot => Cos(viewangle/2.0)
			; check if something is blocking the view
		 	If LinePick(EntityX(observer,True),EntityY(observer,True),EntityZ(observer,True),dx*viewrange,dy*viewrange,dz*viewrange,0.01)=target
				; observer can see target so shoot the m'fucker
				Return True
			End If
		End If
		
	End If

	; observer cannot see target	
	Return False

End Function


Function ppfps_curvevalue#(ppfps_NEWvalue#,oldvalue#,increments# ) 

If increments>1 Then oldvalue#=oldvalue#-(oldvalue#-ppfps_NEWvalue#)/increments 
If increments<=1 Then oldvalue=ppfps_NEWvalue 
Return oldvalue# 

End Function

;------------------------------------------------------- 
; function to move one angle to another with acceleration 
;------------------------------------------------------- 

Function ppfps_curveangle#( ppfps_NEWangle#,oldangle#,increments#) 
If increments>1 
If (oldangle+360)-ppfps_NEWangle<ppfps_NEWANGLE-ppfps_OLDANGLE Then ppfps_OLDANGLE=360+ppfps_OLDANGLE 
If (ppfps_NEWangle+360)-oldangle<ppfps_OLDANGLE-ppfps_NEWANGLE Then ppfps_NEWANGLE=360+ppfps_NEWANGLE 
oldangle=oldangle-(oldangle-ppfps_NEWangle)/increments 
EndIf 

If increments<=1 oldangle=ppfps_NEWangle 
Return oldangle 
End Function



;==========================================================================	
	
	

;====== CORRECTX ==========================================================

Function ppfps_CorrectX(pixel)
	
	Return (pixel * GraphicsWidth() / 640)

End Function

;==========================================================================



;====== CORRECTY ==========================================================

Function ppfps_CorrectY(pixel)
	
	Return (pixel * GraphicsHeight() / 480) 

End Function 

;==========================================================================



;====== DECODE INCOMING MESSAGE ===========================================

Function ppfps_DecodeIncomingMessage(message$)

	For i = 1 To 100
		ppfps_incoming$(i) = ""
	Next
	i = 1
	part = 1
	ppfps_incoming$(part) = Mid(message$, i, 1)
	i = i + 1
	While Mid(message$, i, 1)<>"-"
		While Mid(message$, i, 1)<>","
			ppfps_incoming$(part)=ppfps_incoming$(part)+Mid(message$, i, 1)
			i = i + 1
			If Mid(message$, i, 1)="-"
				Exit
			EndIf
		Wend
		If Mid(message$, i, 1)=","
			i = i + 1
			part = part + 1
			ppfps_incoming$(part) = Mid(message$, i, 1)
			i = i + 1
		EndIf
	Wend
	
End Function

;==========================================================================

;====== ENTER CHAT ========================================================

Function ppfps_EnterChat()

	For i = 1 To 237
		If (i >= 2 And i <= 11) Or (i >= 16 And i <= 25) Or (i >= 30 And i <= 38) Or (i >= 44 And i <= 50)
			If KeyHit(i)
				If Len(ppfps_chat$) <= 25
					ppfps_chat$ = ppfps_chat$ + ppfps_keys$(i)
				EndIf
			EndIf
		EndIf
	Next
	
	If KeyHit(14) And Len(ppfps_chat$) >= 1 And  keytimer# + 100 < MilliSecs()
		ppfps_chat$ = Left(ppfps_chat$,Len(ppfps_chat$) - 1)
		keytimer# = MilliSecs()
	EndIf
	If KeyHit(211) And Len(ppfps_chat$) >= 1 And  keytimer# + 100 < MilliSecs()
		ppfps_chat$ = Right(ppfps_chat$,Len(ppfps_chat$) - 1)
		keytimer# = MilliSecs()
	EndIf
	
	If KeyHit(57) And Len(ppfps_chat$) <= 25 And  keytimer# + 100 < MilliSecs()
		ppfps_chat$ = ppfps_chat$ + " "
		keytimer# = MilliSecs()
	EndIf
	
	If KeyHit(53) And Len(ppfps_chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		ppfps_chat$ = ppfps_chat$ + "?"
	EndIf
	
	If KeyHit(2) And Len(ppfps_chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		ppfps_chat$ = ppfps_chat$ + "!"
	EndIf
	
	If KeyHit(3) And Len(ppfps_chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		ppfps_chat$ = ppfps_chat$ + "@"
	EndIf
	
	If KeyHit(4) And Len(ppfps_chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		ppfps_chat$ = ppfps_chat$ + "#"
	EndIf
	
	If KeyHit(5) And Len(ppfps_chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		ppfps_chat$ = ppfps_chat$ + "$"
	EndIf
	
	If KeyHit(6) And Len(ppfps_chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		ppfps_chat$ = ppfps_chat$ + "%"
	EndIf
	
	If KeyHit(7) And Len(ppfps_chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		ppfps_chat$ = ppfps_chat$ + "^"
	EndIf
	
	If KeyHit(8) And Len(ppfps_chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		ppfps_chat$ = ppfps_chat$ + "&"
	EndIf
	
	If KeyHit(9) And Len(ppfps_chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		ppfps_chat$ = ppfps_chat$ + "*"
	EndIf
	
	If KeyHit(10) And Len(ppfps_chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		ppfps_chat$ = ppfps_chat$ + "("
	EndIf
	
	If KeyHit(11) And Len(ppfps_chat$) <= 25 And (KeyDown(42) Or KeyDown(54))
		ppfps_chat$ = ppfps_chat$ + ")"
	EndIf
	
	If KeyHit(12
