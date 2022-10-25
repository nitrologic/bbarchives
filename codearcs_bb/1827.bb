; ID: 1827
; Author: kevin8084
; Date: 2006-09-28 19:33:58
; Title: Finite State Machine (FSM)
; Description: A simple FSM System

; **************************************************************************************************
; Finite State Machine tutorial by Kevin Lee Legge (kevin8084@gmail.com)
; **************************************************************************************************
;
; ************************************** DEFINITION ************************************************
;
; A Finite State Machine (FSM) is an artificial intelligence method where the AI is in a series
; of "states". For game playing some of the most common states are: IDLE, HUNGRY, SEARCHING
; ATTACKING, SUPPORTING, RUNNING, DYING.
; There are two stages in the FSM - the "transistion" stage and the "behavior" stage.
; The transistion stage is where the AI's state is checked to see if the AI is in transistion
; from one state to another. That is, if the AI is in the IDLE state and is being attacked, then
; its state will be changing to ATTACKING or RUNNING or, perhaps, DYING.
; The behavior stage is where the action is. This part of the code instructs the AI on
; what to do according to what state it's currently in. For example, if the AI is currently in the
; SEARCHING state, then the behavior stage calls whatever searching functions are set up in the
; code.
; There are currently two schools of thought regarding FSM. The first regards the Behavior stage as
; completely adequate by itself, without bothering with any transistions. The second regards
; the transistion AND behavior stages as part of a whole.
; I, personally, subscribe to the idea that the FSM must have both transistion and behavior stages.
;
; **************************************************************************************************
Graphics3D 800,600
SetBuffer BackBuffer()

SeedRnd MilliSecs()

; **************************************************************************************************
;                                          CONSTANTS
; **************************************************************************************************

Const IDLE      = 0
Const SEARCHING = 1
Const ATTACKING = 2
Const DYING     = 3

Const PLAYER_TYPE = 1
Const ENEMY_TYPE  = 2
Const CUBE_TYPE   = 3
Const GROUND_TYPE = 4


; **************************************************************************************************
;                                            TYPES
; **************************************************************************************************

Type enemy
	Field entity	  ; the AI's mesh
	Field state		  ; what state the AI is currently in
	Field life#		  ; how much life the AI has
	Field range#	  ; the AI's search range
	Field bad.player  ; use to store handle of AI's enemy (ie., the player)
	Field x#,y#,z#	  ; the AI's location
	Field vx#,vy#,vz# ; the AI's velocity
End Type

Type player
	Field entity
	Field x#,y#,z#
End Type

; **************************************************************************************************
;                                      GLOBAL VARIABLES
; **************************************************************************************************

Global age	; use to age the enemy so that it transistions into its DYING state
Global alpha# = 1
Global eState$ ; holds enemy's state for printing purposes

; Create the ground
Global plane = CreatePlane()
EntityType plane,GROUND_TYPE
EntityColor plane,85,85,85
PositionEntity plane,0,0,0

; Create the enemy
Global enemy.enemy = New enemy
enemy\entity = CreateSphere()
EntityRadius enemy\entity,1
EntityType enemy\entity,ENEMY_TYPE
EntityShininess enemy\entity,1
enemy\x# = Rnd(-200,200)
enemy\y# = 2
enemy\z# = Rnd(-200,200)
enemy\state = IDLE
enemy\life# = 100.0
enemy\range# = 50.0
age = MilliSecs()
PositionEntity enemy\entity,enemy\x#,enemy\y#,enemy\z#

; Create the player
Global player.player = New player
player\entity = CreateCamera()
EntityRadius player\entity,1
EntityType player\entity,PLAYER_TYPE
player\x#=0
player\y#=2
player\z#=0
PositionEntity player\entity,player\x#,player\y#,player\z#

; Create the light
Global light = CreateLight()
PositionEntity light,100,100,100
RotateEntity light,90,0,0

; Populate the world with some cubes
For c=1 To 25
	cube=CreateCube()
	EntityColor cube,0,87,0
	EntityType cube,CUBE_TYPE
	PositionEntity cube,Rand(-200,200),1,Rand(-200,200)
Next
HidePointer

Collisions ENEMY_TYPE,CUBE_TYPE,2,2
Collisions ENEMY_TYPE,GROUND_TYPE,2,2
Collisions PLAYER_TYPE,GROUND_TYPE,2,21
Collisions PLAYER_TYPE,CUBE_TYPE,2,2

MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

While Not KeyHit(1)
If KeyDown(200) Then MoveEntity player\entity,0,0,.3
If KeyDown(208) Then MoveEntity player\entity,0,0,-.3
If KeyDown(203) Then TurnEntity player\entity,0,1,0
If KeyDown(205) Then TurnEntity player\entity,0,-1,0

TranslateEntity player\entity,0,-1,0 ; give player some gravity
If enemy <> Null Then TranslateEntity enemy\entity,0,-1,0 ; same with the enemy

mxs#=-MouseXSpeed()*.25
mys#=MouseYSpeed()*.25
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
TurnEntity player\entity,mys#,mxs#,0
RotateEntity player\entity,EntityPitch#(player\entity),EntityYaw#(player\entity),0
If EntityPitch#(player\entity)>80 Then RotateEntity player\entity,80,0,0
If EntityPitch#(player\entity)<-80 Then RotateEntity player\entity,-80,0,0

FSM_Transistion() ; check to see if there are any state transistions
FSM_Behavior()    ; now do the code, depending on the state the AI is in

UpdateWorld
RenderWorld
Text 0,0,"I'm "+eState$
Flip
Delay 10
Wend
FreeEntity player\entity
Delete player
End


; **************************************************************************************************
;                                             FUNCTIONS
; **************************************************************************************************

Function FSM_Transistion()
; this function determines whether or not the AI is in transistion from one state to another
For this.enemy = Each enemy ; iterate through each enemy AI
	Select this\state
		Case IDLE           
			eState$="Idle"
			EntityColor this\entity,0,255,0
			p.player = getEnemyDistance(this) ; check to see if player is in sensory range
			If p <> Null  Then                ; we see the player!
				this\state = ATTACKING        ; we transistion to the attack state
				this\bad = p                  ; make sure that we store player internally
			ElseIf MilliSecs() > age+50000 Then ; are we about to die of old age (50 seconds)?
				this\state = DYING				; yes? Then transistion to dying state
				age = MilliSecs()               ; not needed, really, but what the heck :)
			End If
		Case SEARCHING
			EntityColor this\entity,2550,0,255
			p.player = getEnemyDistance(this) ; again, check to see if player is nearby
			If p <> Null Then                 ; yes? 
				this\state = ATTACKING        ; then transistion to attack state
				this\bad = p
			ElseIf MilliSecs() > age+50000 Then ; are we dying of old age?
				this\state = DYING              ; yep...then we transistion
				age = MilliSecs()
			End If
		Case ATTACKING
			EntityColor this\entity,255,0,0
			p.player = getEnemyDistance(this)   ; even though we are in attacking state, we need to
										        ; make sure that player is still in range
			If p <> Null Then                   ; yes?
				this\state = ATTACKING          ; Houston, we have liftoff...we attack!
				this\bad = p
			ElseIf MilliSecs() > age+50000 Then ; else, are we dying?
				this\state = DYING              ; if so, then die already!
				age = MilliSecs()
			Else
				this\state = SEARCHING          ; else let's go to the searching state
				this\bad = Null                 ; no bad player here, so make it null
			End If
		Case DYING                              ; we are dying
			EntityColor this\entity,45,45,45
	End Select
Next
End Function

Function FSM_Behavior()
; this function reroutes according to which state the AI is in
For this.enemy = Each enemy ; iterate through the enemy
	Select this\state
		Case IDLE
			this\state = SEARCHING ; idle is so boring...let's search, instead
		Case SEARCHING
			patrol(this) ; do our patrol
		Case ATTACKING
			attack(this) ; attack!
		Case DYING
			die(this)    ; well, it's been real
			alpha#=alpha# - .001 ; this is to fade out the enemy
	End Select
Next

End Function

Function patrol(e.enemy)
; put patrol code here
eState$ = "patrolling"
; let's have the enemy move around a little bit
MoveEntity e\entity,0,0,.2
If Rand(255)=17 Then RotateEntity e\entity,0,EntityYaw#(e\entity)+5,0
End Function

Function attack(e.enemy)
; put attack code here
eState$ = "attacking"
; just to show how to implement the attack state
; this is NOT my normal attack function! :)
PointEntity e\entity,player\entity
MoveEntity e\entity,0,0,.2
End Function

Function die(e.enemy)
; we are dying now
eState$ = "dying"
EntityAlpha e\entity,alpha#
If alpha# <= 0 Then
	FreeEntity e\entity
	Delete e
	eState$="Nothing...no more enemies left"
End If
End Function

Function getEnemyDistance.player(e.enemy)
; function to determine if player is in AI's sensory range
For p.player = Each player ; iterate through all the players
	If p <> Null Then      ; hey, do we have a player?
		If EntityDistance#(e\entity,p\entity)<=e\range# Then ; yep...is he/she in range?
			Return p   ; yes? Then return the player
		End If
	End If
Next
End Function
