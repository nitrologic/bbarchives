; ID: 890
; Author: Techlord
; Date: 2004-01-16 04:53:55
; Title: Project PLASMA FPS 2004: How To Get Started Guide.
; Description: How To Get Started Guide.

****************************************************** 
CODE MODULE CONVENTIONS
******************************************************
When submitting code modules for incorporation into PLASMA 2004 
please ensure that the following standard is used when naming your 
module and public functions.  

Syntax: 
[Object][Purpose] (ie: ClockSet) 
[Object][Property][Purpose] (ie: ActionIntSet(myaction,index,255)) 
[Object][SubObject][Purpose] (ie: DatabaseRowNext(db)) 
[Object][SubObject][Property][Purpose](ie: CameraRumbleXSet(camera,2.0)) 

This naming convention applies to all Constants, Global Variables
, Arrays, Types, Functions, and Labels to prevent code conflict.

****************************************************** 
CODE MODULE INTEGRATION
******************************************************
All code modules should be stored in a independent *.bb and provide 
a Function for Initialization, Game Loop Update, and Termination 
(Memory Removal) to be used in the Main.bb. These are the only functions 
allowed in the main.bb. 

Main.bb Example:

.ENGINE_INCLUDE
Include "player.bb"
Include "bots.bb"

.ENGINE_START
playerStart()
botsStart()

.ENGINE_UPDATE
While Not KeyHit(1)
	botsUpdate()
	playerUpdate()
	UpdateWorld()
	RenderWorld()
Wend

.ENGINE_STOP
playerStop()
botsStop()

****************************************************** 
CODE/MEDIA FILE STRUCTURE
******************************************************
TBD. Proposed File Structure. Need your input.

<Root>
    <Engine><Application> (source code)
        Main.bb
        OtherCodeModules.bb
    <Game> (media, scripts, dat files)
        Engine.exe
        <Data>
            <2D>
                <Textures>
            <3D>
                <Maps>
                    map.b3d;*.3ds
                    map.dat
                    prop.b3d;*.3ds
                    prop.script;*.dat
                    map.jpg;*.png,*.jpg (textures)
                <Players>
                    player.b3d;*.3ds
                    player.jpg;*.png,*.jpg (textures)
                    player.script; *.dat
                <Weapons>
                    Weapon.b3d;*.3ds
                    Weapon.jpg;*.png,*.jpg (textures)
                    Weapon.script; *.dat
                <PowerUps>
                    powerup.b3d;*.3ds
                    powerup.jpg;*.png,*.jpg (textures)
                    powerup.script; *.dat
            <Audio>
                <Music>
                    musicfiles.wav;*.ogg,*.mp3,*.mid
                <SoundFX>
                    soundfiles.wav;*.ogg,*.mp3,*.mid
        <Server>
            Server.scripts
        <Client>
            Client.scripts
            <UI>
                ui.jpg;*.png,*.jpg (images)
                ui.script;*.dat
    <Tools>
        Tool.exe; Tool.bb
    <Docs> (.html format)
        <Engine>
        <Game>
        <Tools>

****************************************************** 
CODE MODULE REMARKS
******************************************************
Functions should provide remarks that describe their purpose, parameters, 
and return value. This information will be used to produce documentation.

Example:

Function botScan(this.bot)
	;Purpose: Performs collision check between the bots scancone
	; and all the players bodies. If TRUE the bot will attack.
	;Parameters: bot object
	;Return: None
	For player.player = Each player
		If MeshesIntersect (this\scancone%,player\entity %) 
			this\stateofmind%=BOT_AI_STATE_ATTACK%
			this\target=player\entity%
			Exit
		EndIf
	Next
End Function
