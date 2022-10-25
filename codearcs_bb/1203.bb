; ID: 1203
; Author: Strider Centaur
; Date: 2004-11-22 08:43:58
; Title: Simple Fog
; Description: 3D fog simulation

; The Aquatic Demo, a demo to experiment with underwater effects.
;
; Would like to grow this into a mer based game, in this demo the goals are:
;
; 1) Creat a pleasing underwater experiance in sight, sound and movement
; 2) Support multiple player access to allow it to be shared
; 3) incorperate the 3D tile system based using a 2D tile system to layout the sea floor
; 4) for comparison, try alternate terrain builds using other tool.
; 5) experement heavily with strong lighting affects and LOD in relation to intensity and fog.
; 6) create models and animation sequances that match the movement to produce a smooth integrated feel.
; 7) Get pats on the back from my Blitz Pals.  Uhh, huh, thats it yea.  LOL
;
;

; A few things we never want to change
Const WALL_TYPE = 3
Const SEA_FLOOR_TYPE = 2
Const PLAYER_TYPE = 1
; Keeping it simple
; Where possible I will beg barrow and umm barrow as mutch from other peoples work ( with their permisions ) as possible
; I will also use the refinement process for development, that is, first get it all working, then make it look good and
; optimize as needed.


; Step 1  the Player type needed for the Network thingy
Type pdata
	Field playerID		; The NetID of this player
	Field name$			; the Name of this player shown over head.
	Field x#			; Now if Blitz supported Vectors this would be so much easier
	Field y#			; these are movement variable not positional ones
	Field z#
	Field wx#			; world postion These on the otherhand, are postional variables
	Field wy#			
	Field wz#
	Field yaw#			; ya know this 3D world stuff is gona really take some experimenting to get these right
	Field pitch#
	Field roll#
	Field speed# 		; yea see, its like this, things underwater do not instantly start and stop like in most FPSs.
	Field accell#		; so we need to know how fast things can speed up
	Field decell#		; and slow down
	Field mesh			; give you one guess what goes here, if you guess the mesh Entity handle, you'd be right.
	Field pivot			; used for movemement
End Type

Type timer
	Field timerID
	Field tdelay
	Field lasttime
	Field mode			; 0 off, 1 + number of times to triger, -1 allways on.
	Field triggered		; 1+ = number of times triggered, should be cleared to 0 when tested. max value will never exceed 32000
End Type 
; So Im going to make some assumptions on screen size and modes here, I like windowed
; but if you one of those full screen types, go ahead, change it.
; Why globals, Well sure I can get this info from inside blitz, but to set the data this is how I like to do it.
; no constants this time, its nice to be able to define defaults that can be changed.  :)
Global viewwidth = 800
Global viewheight = 600
Global viewmode = 2
Global viewcolors = 32


;Never sure if this is needed or not, but I do it anyway
Global ppivot
Global pcammera
Global pfog1
Global pfog2
Global pfog3
Global pfogtex
Global plight1
Global plight2
Global surfacedownplane
Global surfaceupplane
Global seafloorplane
Global surfacedowntex
Global surfaceuptex
Global seafloortex


; and our dimmed arrays
Dim cube%(100,100)
Dim sphere%(100,100)


;Timers?  We don't need no stinking timers!  Well maybe just a few
;

					
Global fratetimer   ; this will hold the frame rate timer handel
Global frdelay = 1000/60	; this is sort of 60FPS  adjust to taste

Global gamestatus	; used to track the return status codes from functions

; Main Loops should be very clean, no uneeded logic, no variables that are not predefined above
; easy and clean.
;============= MAIN GAME LOOP =================
gamestatus = Init_Game()		; make a non-stop timer for FPS and game controle
If gamestatus = 1 Then RuntimeError ("Could not INITIALIZE GRAPHICSMODE")
If gamestatus = 2 Then RuntimeError ("PDATA type does not exist, not created")

Init_World("demo")				; Load the game world sceen data, we make the basice in init_game, this loads
								; models for details in terrain and sturctures.
fratetimer = Init_Timer( frdelay, -1 )
Init_Collisions() 
While Not KeyHit(1)
	If TimerCheck(fratetimer) Then 
		InputUpdate()
		;NetHandler()
		CollisionHandler()
		MovementUpdate()
		RenderUpdate()
		;NetUpdate()
	EndIf
	;AIUpdate()
Wend

End

;=============  END MAIN GAME LOOP ============



; you will find Im function crazy, I think a game or any code should have a main section of almost purely function
; calls and the main game loop and thats it.

; Setup all the pre demo stuff and screen modes
Function Init_Game%()
	AppTitle " The Merfolk Demo "
	If Not GfxMode3DExists(viewwidth, viewheight, viewcolors) Then Return (1)
	Graphics3D viewwidth, viewheight, viewcolors, viewmode
	SetBuffer BackBuffer()
	
	;creat the first pdata so we can load up the player info
	p.pdata = New pdata
	If p = Null Then Return (2)

	; ok lets set up the player pivot parent and camera ( going FP here )
	p\pivot = CreatePivot ()
	pcammera = CreateCamera(p\pivot) 
	EntityType p\pivot, PLAYER_TYPE
	EntityRadius p\pivot, 1.4
	
	;light?  Yea the spot light its so handing to see things.
	; but Blitz is do kind to give us a default one we simply need to change the color
	; nice semi-dark blue for now
	CameraFogMode pcammera,1
	CameraFogColor pcammera, 0,0,100
	CameraFogRange pcammera, 1,55			; about 100 meters
	
	; Now we all know that the cammera fog is not Volumetric so we will emmulate this with a few large boxes.
	; how this works is they are centered on us, and we flip them so we can see the texture on them from the inside.
	; we then set the alpha levels from low to high as they radiate out from us.  We use 10 such bozes with a .10 alpha
	; each, the first box in placed right in front of the cammera, so everything is shaded 10% by that one, the others
	; stretch out from there till 100% opacity is reached at max range.  We can increase viewing range by simple
	; decreasing the alpha as needed.  Above water is would be reasonable to all layers to 0 opacity, or for those fogy
	; surfuce days, the first 3 or so to 0 and the rest to propper levels for the fog desired.
	;pfog1 = CreateCube(p\pivot)
	;pfog2 = CreateCube(p\pivot)
	;pfog3 = CreateCube(p\pivot)
	;pfog4 = CreateCube(p\pivot)
	;pfog5 = CreateCube(p\pivot)
	;pfog6 = CreateCube(p\pivot)
	;pfog7 = CreateCube(p\pivot)
	;pfog8 = CreateCube(p\pivot)
	;pfog9 = CreateCube(p\pivot)
	;pfog10 = CreateCube(p\pivot)
	
	pfog1 = CreateSphere(8,p\pivot)
	pfog2 = CreateSphere(8,p\pivot)
	pfog3 = CreateSphere(8,p\pivot)
	pfog4 = CreateSphere(8,p\pivot)
	pfog5 = CreateSphere(8,p\pivot)
	pfog6 = CreateSphere(8,p\pivot)
	pfog7 = CreateSphere(8,p\pivot)
	pfog8 = CreateSphere(8,p\pivot)
	pfog9 = CreateSphere(8,p\pivot)
	pfog10 = CreateSphere(8,p\pivot)


	FlipMesh pfog1
	FlipMesh pfog2
	FlipMesh pfog3
	FlipMesh pfog4
	FlipMesh pfog5
	FlipMesh pfog6
	FlipMesh pfog7
	FlipMesh pfog8
	FlipMesh pfog9
	FlipMesh pfog10
	pfogtex = LoadTexture ("textures\fog.bmp")
	EntityTexture pfog1, pfogtex
	EntityTexture pfog2, pfogtex
	EntityTexture pfog3, pfogtex
	EntityTexture pfog4, pfogtex
	EntityTexture pfog5, pfogtex
	EntityTexture pfog6, pfogtex
	EntityTexture pfog7, pfogtex
	EntityTexture pfog8, pfogtex
	EntityTexture pfog9, pfogtex
	EntityTexture pfog10, pfogtex

	; since the alphas tend to add up we try to keep them change at each dispance the same
	; finally reaching opaque at the tenth box
	EntityAlpha pfog1,.10
	EntityAlpha pfog2,.10
	EntityAlpha pfog3,.10
	EntityAlpha pfog4,.10
	EntityAlpha pfog5,.10
	EntityAlpha pfog6,.10
	EntityAlpha pfog7,.10
	EntityAlpha pfog8,.10
	EntityAlpha pfog9,.10
	EntityAlpha pfog10,.10

	; scale the boxes to cover the range we need
	ScaleEntity pfog1,15,1,1
	ScaleEntity pfog2,15,5,5
	ScaleEntity pfog3,15,10,10
	ScaleEntity pfog4,15,15,15
	ScaleEntity pfog5,20,20,20
	ScaleEntity pfog6,25,25,25
	ScaleEntity pfog7,30,30,30
	ScaleEntity pfog8,35,35,35
	ScaleEntity pfog9,45,45,45
	ScaleEntity pfog10,55,55,55
	
	
	; more light out there in front of us for that cool distant glow feel
	; Thats right our water will be a magical happy glowing kind of place
	; not very realistic but hey we are merfolk, we can do this kind of stuff.  :)
	plight1 = CreateLight(2,p\pivot)
	LightColor plight1, 0,100,100
	LightRange plight1, 150
	PositionEntity plight1, 0,20,200
	
	; and some more light, placed at point of view
	plight2 = CreateLight(3,p\pivot)
	LightColor plight2, 0,50,50
	LightRange plight2, 200.0
	
	; Ok we need the Sea, well 3 planes, yea we will make a sky orb and use it when you get close to surface
	; The top plane is the furface of the water as seen from above the water, hey we want to jump out of water too.
	; this one is less transparant than the one from looking up.
	surfacedownplane = CreatePlane()
	PositionEntity surfacedownplane, 0,0.01, 0 ; minor adjustment over plane below it.
	surfacedowntex = LoadTexture("textures\water.bmp")
	EntityAlpha surfacedownplane,.75
	EntityTexture surfacedownplane, surfacedowntex
	
	; The next one down is the bottom a mostly transparant( hey isn't that translucent? ) plane that we can see the
	; sky throuh.  Also know that Fog is turned off when we are less than 100 meters from the surface, well in reality
	; the closer we get the further the max range of the fog raises so its still there, when we are very need the surface
	; it will extend well beyond the skysphere and thus appear non-present.  Ahh, you ask what about if you look down while 
	; near the surface?  Well silly, since your location in the sphere is very near the upper side of it, the fog will 
	; still be blocking the extreams of the toward the hurizon and bellow the surface, Genius I know, but as a friend 
	; said, why not just use a 5 sided sky box centered on the character and placed above the water? To witch I said, "umm".
	;surfaceupplane = CreatePlane()
	;PositionEntity surfaceupplane, 0,0,0
	;surfaceuptex = LoadTexture("texture\water.bmp")  ; in future will be using 2 sepereate texture for up/down
	;EntityAlpha surfaceupplane, .30
	;FlipMesh surfaceupplane
	; of coarse we cant flip planes, what was I thinking, no big deal will simply make a surface mesh, that way I can 
	; animate it with waves and stuff anyway.  So there.
	
	; Finally the sea floor, yea, its a big flat sea with a sandy texture, dont like it?  Me either so will be adding
	; stuff down there real soon.
	seafloorplane = CreatePlane()
	PositionEntity seafloorplane, 0, -300, 0
	seafloortex = LoadTexture("textures\seafloor.bmp")
	EntityTexture seafloorplane, seafloortex
	EntityType seafloorplane, SEA_FLOOR_TYPE
	
	;initialize the skybox, using my friend idea, see also move_skybox 
	;init_skybox()
	
	;Set the starting info
	; sence we don't have spawn points in our wourld yet we get the good ole 0,250,0
	p\wx# = 0.0
	p\wy# = -299.0	;we starting out at close to 300 meters down, hows that For working under preasure?  
	p\wz# = 0.0
	p\yaw# = 0.0
	
	
	;p\mesh = LoadAnimMesh("merperson1.3ds", ppivot);  Hey when I have a model I get to uncomment this.  :)
	
	
	; position the player in starting location.
	PositionEntity p\pivot, p\wx, p\wy, p\wz
	;PositionEntity pcammera, p\wx, p\wy, p\wz
	;PositionEntity pfog1, p\wx, p\wy, p\wz
	;PositionEntity pfog2, p\wx, p\wy, p\wz
	;PositionEntity pfog3, p\wx, p\wy, p\wz
	;PositionEntity pfog4, p\wx, p\wy, p\wz
	;PositionEntity pfog5, p\wx, p\wy, p\wz
	;PositionEntity pfog6, p\wx, p\wy, p\wz
	;PositionEntity pfog7, p\wx, p\wy, p\wz
	;PositionEntity pfog8, p\wx, p\wy, p\wz
	;PositionEntity pfog9, p\wx, p\wy, p\wz
	;PositionEntity pfog10, p\wx, p\wy, p\wz

	
	Return (0) ; if not true our function was completed without a problem
End Function
	
;setup collisions
Function Init_Collisions()
	Collisions PLAYER_TYPE, SEA_FLOOR_TYPE,2,3
	Collisions PLAYER_TYPE, WALL_TYPE,2,3
End Function

; Handle collisions	
Function CollisionHandler()
	
End Function

; get input from player
; Using 3D free flight controls for all 6 degrees of rotation.
Function InputUpdate()
	p.pdata = First pdata
	p\yaw# = 0	; lets make sure we only move if a key is down 
	p\pitch# = 0;
	p\roll# = 0;
	p\z# = 0;
	p\y# = 0;
	p\x# = 0;
	; yaw ( Turn on Y Axis control)
	If KeyDown( 30 ) Then
		p\yaw#=+1
	EndIf
	If KeyDown( 32 )  Then 
		p\yaw#=-1
	EndIf 
	; move backward
	If KeyDown( 31 ) Then 
		p\z#=-0.4
	EndIf
	; move forward
	If KeyDown( 17 ) Then
	  	p\z#=+0.4
	EndIf 
	; strafe Left
	If KeyDown( 16  ) Then
		p\x# = -0.4
	End If
	;strafe right 
	If KeyDown( 18 ) Then
		p\x# = +0.4
	End If
	;ascend ( move on Y axis )
	If KeyDown( 57 ) Then
		p\y# = +0.4
	EndIf
	;descend ( on Y axis )
	If KeyDown( 45 ) Then
		p\y# = -0.4
	EndIf
	; pitch control ( turn on X Axis control)
	If KeyDown( 208 ) Then
		p\pitch# =+1
	EndIf
	If KeyDown( 200 ) Then
		p\pitch# = -1
	End If
	; roll control  ( Turn on Z axis control )
	If KeyDown( 203 ) Then
		p\roll# = +1
	End If
	If KeyDown( 205 ) Then
		p\roll# = -1
	End If

End Function


; Basicly set up BlitzPlay the totally awesome network lib for Blitz.  Woot BP rocks
; Mode is either Host 0 or client 1
; port is the port to listen/connect on
; players is the max number of players
; ServerIP is the IP address of the game server(host) defaults to localhost at 128.0.0.1
; return status codes 0 if all went ok.
Function Network_Init%( mode%, port, players%, ServerIP$ = "128.0.0.1" )


	Return (0)
End Function

; update the network, basicly sends network data and recieves it from network
; while I use BlitzPlay for this, I use my own wrappers so the netcode 
; stays independant of the game, incase I want to use something else later.
; returns 0 if all went as planed or errorcode otherwise.
Function NetUpdate%()

	Return (0)
End Function

;The heart of the network portion of the demo/game
; This function handles all incomming messages and sorts/stores/posts the data to the right variables used
; by other functions like ChatUpdate, MovementUpdate and so forth.
Function NetHandler%()

	Return (0)
End Function

; Called after input and NetHandler this function updates all movable objects in the world space
; basicly cycles through the pdata's and other similar types and adjust there postion to speed ratio
; based on the timer datas for each object. This will position all players in the game/demo
Function MovementUpdate()
	For p.pdata = Each pdata
		TurnEntity p\pivot, p\pitch#, p\yaw#, p\roll#
		MoveEntity p\pivot, p\x#, p\y#, p\z#
		p\wx# = EntityX(p\pivot)
		p\wy# = EntityY(p\pivot)
		p\wz# = EntityZ(p\pivot)
	Next	
End Function

; This function calles all the updateworld and render functions, also handles the HUD information
Function RenderUpdate()
	UpdateWorld
	RenderWorld
	Draw2DHUD()
	Flip
End Function


;Create a new timer useing the Delay provided
; the mode is -1 non-stop, 1+ count down timer, 0 stopped( usually cause countdown has happened )
; use countdown for cases where you want effects to last for a specific time but dont need to keep
; updating the effect after that.  powerup durations for example durations for example.
; return the timer id from the timer type created	
Function Init_Timer%(timeDelay%, mode ="-1")
	utimer = 0
	For t.timer = Each timer
		If t <> Null Then 
			tiemid = t\timerID
			If timerid > utimer Then utimer = timerid   ; lets get the highest timer ID so far 
		End If
	Next
	utimer = utimer + 1; lets make this ID one greater than the highest ID so far and thus unique
	t.timer = New timer
		t\timerID = utimer
		t\tdelay = timeDelay
		t\lasttime = MilliSecs ()
		t\triggered = 0
	
	Return t\timerID
End Function

;Checks the a given timer( by ID ) to see if it has triggered, clearmode 0 (default) will clear the timer if trigger and
; if so return the number of times it triggerd since last check. Setting the clearmode to none zero will 
; simply return the timer triggered data and not clear it, regardless of if it triggered or not.
; the default is to clear the trigger data if triggered.
Function TimerCheck%(timerID%, clearmode = 0)
	For t.timer = Each timer
		If tiemrID% = t\timerID Then
			If t\triggered Then
				ttemp = t\triggered
				If clearmode = 0 Then t\triggered = 0
				t\lasttime = MilliSecs()
				Return (ttemp)
			End If
		End If
	Next
	Return (-101)		;Invalid trigerID
End Function

; loop through all timers and update there triggered stats
Function TimerUpdate()
	For t.timer = Each timer
		If t\mode > -1 Then									; is it non-stop or other?
			If t\lasttime + t\tdelay > MilliSecs() Then    ; did the time expire?
				t\triggered = t\triggered + 1				; yep, so increment triggered
				t\lasttime = MilliSecs()					; reset timer.
				If t\mode > 1 Then 					; its a count down timer amd it hasnt counted down yet?
					t\mode = t\mode -1				; yep, so count down
					If t\mode < 0 Then t\mode = 0   ; prevent it from any chance of crossing over into a non-stop timer
				End If
			End If
		Else 												; so its a non-stop timer
			If t\lasttime + t\tdelay > MilliSecs() Then    ; did the time expire?
				t\triggered = t\triggered + 1				; yep, so increment triggered
				t\lasttime = MilliSecs()					; reset timer.
			End If
		End If
	Next		
End Function

; TimerResetMode lets you change the mode or count of a timer, so that for instance you can restart a stopped
; countdown timer.  It does no other testing, so if you should use TimerCheck before a reset if you are interested
; in the timer status before the reset.
Function TimerResetMode( id%, modecount% )
	For t.timer = Each timer
		If id = t\timerID Then 
			t\mode = modecount
			Return
		End If
	Next
End Function

; Reset the delay setting of a timer, used for fine tuning a timer at run time, like when else silly?
; ID is the timer ID and tdelay is the delay for the timer.  It does no other timer opperation, see also TimerResetMode	
Function TimerResetDelay( id% , tdelay% )
	For t.timer = Each timer
		If id = t\timerID Then
			t\tdelay = tdelay
			Return
		EndIf
	Next
End Function


; this handles the zone specific data for this area of the world, it loads all models and terrains
; repositions player to the propper zone entry point in relations to players present zone settings
; sets all lighting and rendering options and updates the network to let other players know that this 
; player has left one zone and entered the one specified, including the new players starting location.
; of coarse in the Demo there is only one zone, but I figured what they heck, better to plan ahead.
; The data read comes from the zone file, named __zonename__.zon.  These files are located in the zones directory
; there format???? well to be done still.  But probably very INI in format.
Function Init_World( zone$ )
	zonefile$ = "zones\"+ zone$ +".zon"
	
	; this is just a referance set of shapes set out there in the sea
	; a bit of random city code by Killwood 04
	For x = 0 To 30
	 For y = 0 To 49

	  Read map
 
	   If map = 1

	    dice = Rand(0,1)
	
	    Select dice

	     Case 0
	      cube(x,y) = CreateCube()
	      PositionEntity cube(x,y),y * 10,-299,x * 10
    	  EntityType cube(x,y), WALL_TYPE
 	    Case 1
 	     sphere(x,y) = CreateSphere()
 	     PositionEntity sphere(x,y),y * 10,-299,x * 10
		 EntityType sphere(x,y), WALL_TYPE

 	   End Select
  
	EndIf

	 Next
	Next
End Function


; draws some debugging data to screen.  Will be using surfaces in 3D later, but for now this will do.
; called by RenderUpdate
Function draw2DHUD()
	linenum = 0		
	For p.pdata = Each pdata
		linenum = linenume+1
		Text 10,2+(FontHeight()*linenum),"Player: "+p\name+" at location: x:"+p\wx# +" y:"+p\wy# +"z:"+p\wz#
	Next
	Text 10, GraphicsHeight()-((FontHeight()+2)*3), "W/S Move Forward / Back, A/D Turn Left / Right, Q/E Strafe Left/Right"
	Text 10, GraphicsHeight()-((FontHeight()+1)*2),"Arrow UP/DOWN Pitch Up/Down, Arrow Left/Right Roll Left/Right"
	Text 10, GraphicsHeight()-((FontHeight()-1)), "Space Bar/X ascend/descend"
End Function



.map


Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
