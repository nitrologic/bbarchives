; ID: 1224
; Author: WolRon
; Date: 2004-12-05 23:05:48
; Title: Pitfall II
; Description: Source code for Pitfall II remake

;Pitfall II remake
;by WolRon
;Copyright 2003


;;;;;;;;;;;;;;;;;;   Initialize Arrays and Types   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Type Level
	Field graphic			;0=nothing 1=ladderhole 2=hole-in-middle 3=left 4=right 5=ladderhole+2holes
							;6=flat 7=ground 8=treetops 9=water 10=skyline 11=other-treetops 12=other-skyline
	Field water				;0=not 1=true(graphic needs to be set to 3 or 4)
	Field left_blocked		;0=not 1=solid 2=checkered
	Field right_blocked		;0=not 1=solid 2=checkered
	Field animal			;0=not 1=bat 2=bird 3=scorpion 4=frog 5=eel 6=mouse(treasure has to be 6 also)
	Field ladder			;0=not 1=ladder in middle
	Field checkpoint		;0=not 1=true
	Field background_image	;0=not 1=water 2=trees 3=treetops 4=ground 5=other-trees
	Field treasure			;0=not 1=gold(right) 2=gold(left) 3=cat 4=girl 5=ring 6=mouse(animal has to be 6 also)
End Type

Dim Board.Level(8, 30)
;8 screens across x 30 levels high (including sky as a level)


Type Animal
	Field animaltype		;1=bat, 2=bird, 3=scorpion, 4=frog, 5=eel, 6=mouse
	Field levelX
	Field levelY
	Field x
	Field y
	Field dir
	Field state
End Type

Type Coordinate
	Field levelX
	Field levelY
	Field x
	Field y
End Type

PlayerLastCheckPoint.Coordinate = New Coordinate

Dim LevelGraphic(13)
Dim BgImage(10)

Dim FileLength(11)


;;;;;;;;; Check command line ;;;;;;;;;;
cl$ = CommandLine$()
If cl$ = "/MLC"
	UnlockMoreLostCaverns()
EndIf
If cl$ = "/Cheat"
	Cheat = True
EndIf
If Cheat
	Font = LoadFont("FixedSys", 10)
EndIf

;;;;;;;;;  Check if Game Unlocked  ;;;;;;;;;;;;
file = OpenFile("Unlocked.txt")
If Not file
	Unlocked = False
Else
	Unlocked = True
	CloseFile(file)
EndIf

;;;;;;;;   Setup title screen   ;;;;;;;;;;;;;;;
AppTitle " Pitfall II ","Are you sure you want to exit?"

Const FrameRate = False

Mode320200 = GfxModeExists(320, 200, GraphicsDepth())
If Not Mode320200
	Mode320200 = GfxModeExists(320, 200, 16)
	If Mode320200 Then Mode320200 = 16
EndIf	

.TitleScreen

Graphics 640,480, 0, 2
SetBuffer(BackBuffer())
ClsColor 83, 28, 16
Cls
SplashGraphic = LoadImage("levelgraphics\splash")
If Not SplashGraphic Then RuntimeError("Pitfall II - Error: Can't load graphic: splash. Reinstall program.")
MidHandle SplashGraphic
FullscreenGraphic = LoadImage("levelgraphics\fullscreen")
If Not FullscreenGraphic Then RuntimeError("Pitfall II - Error: Can't load graphic: fullscreen. Reinstall program.")
HandleImage FullscreenGraphic, ImageWidth(FullscreenGraphic)/2, 0
WindowedGraphic = LoadImage("levelgraphics\windowed")
If Not WindowedGraphic Then RuntimeError("Pitfall II - Error: Can't load graphic: windowed. Reinstall program.")
HandleImage WindowedGraphic, ImageWidth(WindowedGraphic)/2, 0
ArrowGraphic = LoadImage("levelgraphics\arrow")
If Not ArrowGraphic Then RuntimeError("Pitfall II - Error: Can't load graphic: arrow. Reinstall program.")
NotGraphic = LoadImage("levelgraphics\not")
If Not NotGraphic Then RuntimeError("Pitfall II - Error: Can't load graphic: not. Reinstall program.")
CRGraphic = LoadImage("levelgraphics\crm")
If Not CRGraphic Then RuntimeError("Pitfall II - Error: Can't load graphic: crm. Reinstall program.")
MLCGraphic = LoadImage("levelgraphics\mlc")
If Not MLCGraphic Then RuntimeError("Pitfall II - Error: Can't load graphic: mlc. Reinstall program.")
HandleImage MLCGraphic, ImageWidth(MLCGraphic)/2, 0
OCGraphic = LoadImage("levelgraphics\oc")
If Not OCGraphic Then RuntimeError("Pitfall II - Error: Can't load graphic: oc. Reinstall program.")
HandleImage OCGraphic, ImageWidth(OCGraphic)/2, 0
MCGraphic = LoadImage("levelgraphics\mc")
If Not MCGraphic Then RuntimeError("Pitfall II - Error: Can't load graphic: mc. Reinstall program.")
HandleImage MCGraphic, ImageWidth(MCGraphic)/2, 0


If Mode320200 = 0
	LockBuffer(ImageBuffer(FullscreenGraphic))
	For xiter = 0 To ImageWidth(FullscreenGraphic)
		For yiter = 0 To ImageHeight(FullscreenGraphic)
			pixelcolor = ReadPixel(xiter, yiter, ImageBuffer(FullscreenGraphic))
			If pixelcolor <> -11330544
				red = (pixelcolor Shl 8) Shr 24
				green = (pixelcolor Shl 16) Shr 24
				blue = (pixelcolor Shl 24) Shr 24
				red = red/3
				green = green/3
				blue = blue/3
				pixelcolor = red*2^16 + green*2^8 + blue
			EndIf
			WritePixel(xiter, yiter, pixelcolor, ImageBuffer(FullscreenGraphic))
		Next
	Next
	UnlockBuffer(ImageBuffer(FullscreenGraphic))
EndIf

GameLevel = 1
Selected = False
Selection = 1
If Unlocked = True
	Menu = 1
Else
	Menu = 2
EndIf
; Create timer to set framerate
frameTimer=CreateTimer(30)

While Menu < 3
	While Not Selected
		WaitTimer(frameTimer)
		Cls
		DrawImage SplashGraphic, 320, 137
		If (Unlocked And Selection = 2 And Menu = 1) Or (GameLevel = 2 And Menu = 2)
			DrawImage MLCGraphic, 320, 202
		EndIf
		
		If Menu = 1
			DrawImage OCGraphic, 320, 300
			DrawImage MCGraphic, 320, 350
		Else
			DrawImage FullscreenGraphic, 320, 300
			DrawImage WindowedGraphic, 320, 350
		EndIf
	
		DrawImage CRGraphic, 0, 456
		
		DrawImageRect ArrowGraphic, 50, 300 + 50 * (Selection = 2), 0, 0, ImageWidth(ArrowGraphic)/2, ImageHeight(ArrowGraphic)
		If Menu = 1
			Width1 = ImageWidth(OCGraphic)/2
			Width2 = ImageWidth(MCGraphic)/2
		Else
			Width1 = ImageWidth(FullscreenGraphic)/2
			Width2 = ImageWidth(WindowedGraphic)/2
		EndIf
		If Selection = 1
			xoffset = 320 - ImageWidth(ArrowGraphic)/2 - Width1
		Else
			xoffset = 320 - ImageWidth(ArrowGraphic)/2 - Width2
		EndIf
		If Selection = 1 And Mode320200 = 0 And Menu = 2
			DrawImage NotGraphic, xoffset, 300
		Else
			DrawImageRect ArrowGraphic, xoffset, 300 + 50 * (Selection = 2), ImageWidth(ArrowGraphic)/2, 0, ImageWidth(ArrowGraphic)/2, ImageHeight(ArrowGraphic)
		EndIf
		If KeyHit(1) Then Menu = Menu - 1
		If Menu = 1 And Unlocked = False Then End
		If Menu = 0 Then End
		If KeyHit(200) Then Selection = 1
		If KeyHit(208) Then Selection = 2
		If KeyHit(28) Or KeyHit(57)
			If Menu = 1 Or (Selection = 2 Or Mode320200 > 0)
				Selected = True
				Menu = Menu + 1
			EndIf
		EndIf
		Flip
	Wend
	If Menu = 3
		If Selection = 1
			GraphicMode = 1
		Else
			GraphicMode = 3
		EndIf
		
		If GraphicMode = 1
			If Mode320200 = 1 Then ColorDepth = 0	
		Else
			ColorDepth = 0
		EndIf
	Else
		If Selection = 1
			GameLevel = 1
		Else
			GameLevel = 2
		EndIf
	EndIf
	Selected = False
	Selection = 1
Wend


If Unlocked
	Menu = 1
Else
	Menu = 2
EndIf

FreeTimer frametimer
NumTreasures = LoadLevelData(GameLevel)


;;;;;;;;;;;;;;;;;;   Initialize Game   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

Graphics 320, 200, ColorDepth, GraphicMode
;Original game screen appears to be 152 x 200
;All horizontal pixels are grouped in twos and offset by 4 pixels (actually 8) from the left.

SetBuffer(BackBuffer())
ClsColor 0, 0, 0

If GameLevel = 2
	l$ = "2"
Else
	l$ = ""
EndIf

For iter = 1 To 13
	LevelGraphic(iter) = LoadImage("levelgraphics\lg" + iter)
	If Not LevelGraphic(iter) Then RuntimeError("Pitfall II - Error: Can't load graphic: lg" + iter + ". Reinstall program.")
Next
For iter = 1 To 5
	BgImage(iter) = LoadImage("levelgraphics\bg" + iter)
	If Not BgImage(iter) Then RuntimeError("Pitfall II - Error: Can't load graphic: bg" + iter + ". Reinstall program.")
Next
BgImage(6) = LoadAnimImage("levelgraphics\wf", 34, 42, 0, 2)
If Not BgImage(6) Then RuntimeError("Pitfall II - Error: Can't load graphic: wf. Reinstall program.")
If GameLevel = 2
	BgImage(7) = LoadAnimImage("level2graphics\wf2", 34, 42, 0, 2)
	If Not BgImage(7) Then RuntimeError("Pitfall II - Error: Can't load graphic: wf2. Reinstall program.")
	BgImage(8) = LoadAnimImage("level2graphics\wft", 36, 46, 0, 2)
	If Not BgImage(8) Then RuntimeError("Pitfall II - Error: Can't load graphic: wft. Reinstall program.")
	BgImage(9) = LoadAnimImage("level2graphics\wfm", 36, 46, 0, 2)
	If Not BgImage(9) Then RuntimeError("Pitfall II - Error: Can't load graphic: wfm. Reinstall program.")
	BgImage(10) = LoadAnimImage("level2graphics\wfb", 36, 42, 0, 2)
	If Not BgImage(10) Then RuntimeError("Pitfall II - Error: Can't load graphic: wfb. Reinstall program.")
EndIf

Checkpoint = LoadImage("levelgraphics\cp")
If Not Checkpoint Then RuntimeError("Pitfall II - Error: Can't load graphic: cp. Reinstall program.")

Left_block = LoadImage("levelgraphics\bgl")
If Not Left_block Then RuntimeError("Pitfall II - Error: Can't load graphic: bgl. Reinstall program.")

Left_block_striped = LoadImage("levelgraphics\bgls")
If Not Left_block_striped Then RuntimeError("Pitfall II - Error: Can't load graphic: bgls. Reinstall program.")

Right_block = LoadImage("levelgraphics\bgr")
If Not Right_block Then RuntimeError("Pitfall II - Error: Can't load graphic: bgr. Reinstall program.")

Right_block_striped = LoadImage("levelgraphics\bgrs")
If Not Right_block_striped Then RuntimeError("Pitfall II - Error: Can't load graphic: bgrs. Reinstall program.")

Ladder = LoadImage("level"+l$+"graphics\ladder")
If Not Ladder Then RuntimeError("Pitfall II - Error: Can't load graphic: ladder. Reinstall program.")

Girl = LoadImage("levelgraphics\Rhonda")
If Not Girl Then RuntimeError("Pitfall II - Error: Can't load graphic: Rhonda. Reinstall program.")

Ring = LoadImage("levelgraphics\ring")
If Not Ring Then RuntimeError("Pitfall II - Error: Can't load graphic: ring. Reinstall program.")

Logo = LoadImage("levelgraphics\logo")
If Not Logo Then RuntimeError("Pitfall II - Error: Can't load graphic: logo. Reinstall program.")

Pause = LoadImage("levelgraphics\pause")
If Not Pause Then RuntimeError("Pitfall II - Error: Can't load graphic: pause. Reinstall program.")

Reset = LoadImage("levelgraphics\reset")
If Not Reset Then RuntimeError("Pitfall II - Error: Can't load graphic: reset. Reinstall program.")


Wave = LoadImage("levelgraphics\wave")
If Not Wave Then RuntimeError("Pitfall II - Error: Can't load graphic: wave. Reinstall program.")


Player = LoadAnimImage("level"+l$+"graphics\player", 16, 21, 0, 12)
If Not Player Then RuntimeError("Pitfall II - Error: Can't load graphic: player. Reinstall program.")

Playl = LoadAnimImage("level"+l$+"graphics\playl", 12, 21, 0, 2)
If Not Playl Then RuntimeError("Pitfall II - Error: Can't load graphic: playl. Reinstall program.")

Playlbo = LoadAnimImage("level"+l$+"graphics\playlbo", 20, 13, 0, 2)
If Not Playlbo Then RuntimeError("Pitfall II - Error: Can't load graphic: playlbo. Reinstall program.")

Plays = LoadAnimImage("level"+l$+"graphics\plays", 16, 15, 0, 8)
If Not Plays Then RuntimeError("Pitfall II - Error: Can't load graphic: plays. Reinstall program.")

Playb = LoadAnimImage("level"+l$+"graphics\playb", 16, 21, 0, 2)
If Not Playb Then RuntimeError("Pitfall II - Error: Can't load graphic: playb. Reinstall program.")

Balloon = LoadAnimImage("levelgraphics\balloon", 14, 28, 0, 2)
If Not Balloon Then RuntimeError("Pitfall II - Error: Can't load graphic: balloon. Reinstall program.")

Gold = LoadAnimImage("levelgraphics\gold", 14, 14, 0, 2)
If Not Gold Then RuntimeError("Pitfall II - Error: Can't load graphic: gold. Reinstall program.")

Goldtemplate = LoadImage("levelgraphics\goldt")
If Not Goldtemplate Then RuntimeError("Pitfall II - Error: Can't load graphic: goldt. Reinstall program.")

Cat = LoadAnimImage("levelgraphics\cat", 14, 23, 0, 2)
If Not Cat Then RuntimeError("Pitfall II - Error: Can't load graphic: cat. Reinstall program.")

Mouse = LoadAnimImage("levelgraphics\rat", 16, 8, 0, 2)
If Not Mouse Then RuntimeError("Pitfall II - Error: Can't load graphic: rat. Reinstall program.")

Bat = LoadAnimImage("levelgraphics\bat", 14, 10, 0, 2)
If Not Bat Then RuntimeError("Pitfall II - Error: Can't load graphic: bat. Reinstall program.")

Bird = LoadAnimImage("levelgraphics\bird", 16, 11, 0, 4)
If Not Bird Then RuntimeError("Pitfall II - Error: Can't load graphic: bird. Reinstall program.")

Scorpion = LoadAnimImage("levelgraphics\scorpion", 16, 11, 0, 4)
If Not Scorpion Then RuntimeError("Pitfall II - Error: Can't load graphic: scorpion. Reinstall program.")

Frog = LoadAnimImage("levelgraphics\frog", 16, 9, 0, 4)
If Not Frog Then RuntimeError("Pitfall II - Error: Can't load graphic: frog. Reinstall program.")

Eel = LoadAnimImage("levelgraphics\eel", 16, 3, 0, 4)
If Not Eel Then RuntimeError("Pitfall II - Error: Can't load graphic: eel. Reinstall program.")
MaskImage Eel, 0, 0, 255

Digits = LoadAnimImage("levelgraphics\digits", 16, 8, 0, 10)
If Not Digits Then RuntimeError("Pitfall II - Error: Can't load graphic: digits. Reinstall program.")


sndJump = LoadSound("levelsounds\jump")
If Not sndJump Then RuntimeError("Pitfall II - Error: Can't load sound: jump. Reinstall program.")

sndTreasure = LoadSound("levelsounds\treasure")
If Not sndTreasure Then RuntimeError("Pitfall II - Error: Can't load sound: treasure. Reinstall program.")

sndHit = LoadSound("levelsounds\hit")
If Not sndHit Then RuntimeError("Pitfall II - Error: Can't load sound: hit. Reinstall program.")

sndCheckpoint = LoadSound("levelsounds\cp")
If Not sndCheckpoint Then RuntimeError("Pitfall II - Error: Can't load sound: cp. Reinstall program.")

sndBPop = LoadSound("levelsounds\bpop")
If Not sndBPop Then RuntimeError("Pitfall II - Error: Can't load sound: bpop. Reinstall program.")

sndSPop = LoadSound("levelsounds\spop")
If Not sndSPop Then RuntimeError("Pitfall II - Error: Can't load sound: spop. Reinstall program.")


mscChorus = LoadSound("levelmusic\chorus")
If Not mscChorus Then RuntimeError("Pitfall II - Error: Can't load music: chorus. Reinstall program.")

mscHappy = LoadSound("levelmusic\happy")
If Not mscHappy Then RuntimeError("Pitfall II - Error: Can't load music: happy. Reinstall program.")

mscSad = LoadSound("levelmusic\sad")
If Not mscSad Then RuntimeError("Pitfall II - Error: Can't load music: sad. Reinstall program.")

mscBalloon = LoadSound("levelmusic\balloon")
If Not mscBalloon Then RuntimeError("Pitfall II - Error: Can't load music: balloon. Reinstall program.")



;These need to be declared before gameloop can begin
DisplayScore = False
GameAlmostRunning = False
GameWon = 0
PlayerX = 68
PlayerY = 0
ScreenY = 107
PlayerScreenY = 107
PlayerLevelX = 1
PlayerLevelY = 2
PlayerAction = 0 ;0=standing, 1=running, 2=swimming, 3=climbing ladder, 4=climbing into ladder, 5=standing on ladder
				 ;6=jumping off of ladder, 7=falling, 8=jumping or uncontrolled (hor.) fall, 9=jumping out of water
				 ;10=falling into water, 11=hanging onto balloon, 12=balloon jump
PlayerLastAction = 0 ;0 = nothing, 1=jumping off ladder to left, 2=jumping off ladder to right
PlayerDir = 0    ;0 = right, 1 = left
PlayerFrame# = 0 ;declaring here to declare variable type
curFrame = 0
PlayerHurt = False
PlayerLastCheckPoint\LevelX = 1
PlayerLastCheckPoint\LevelY = 2
PlayerLastCheckPoint\x = 70
PlayerPauseTime = 0
PlayerJustJumped = False
PlayerJustHoppedOnLadder = False
PlayerJustHoppedOffLadder = False
PlayerBob = -1
Score = 4000
GoldBars = 0
Music = 0		;1=chorus 2=happy 3=sad 4=balloon
SubLevel = False
HalfUpdate = -1
FourthUpdate = 0
batY = 11
batDir = 1
birdY = 11
birdDir = 1
eelY = 11
eelDir = 1
frogX = 128
frogY = 15
frogDir = 1
frogState = 0
frogInc = 0
eelState = 0
MouseStopped = False
MouseXPos = 64
MouseCollide = False
MouseLevelX = 0
MouseAnim = 0
BalloonBoardX = 0
BalloonBoardY = 0
Balloon2BoardX = 0
Balloon2BoardY = 0
curWave = -1
WaveInc = 1

;Erase any animals that may exist from last game
For curAnimal.Animal = Each Animal
	Delete curAnimal 
Next

GameRunning = False

; Create the timer to set framerate
frameTimer=CreateTimer(60)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;  GameLoop  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
.GameLoop

WaitTimer(frameTimer)

If FrameRate
	curTime=MilliSecs()
EndIf

;;;;;;testing purposes only;;;;;;;
If Cheat
	If KeyDown(30) ;a key
		ChannelPitch chnlMusic, 22050
		ChannelPitch chnlCP, 24000
		ChannelPitch chnlJump, 24000
		ChannelPitch chnlTreasure, 24000
		ChannelPitch chnlHit, 24000
		Delay(33)
		ChannelPitch chnlMusic, 44100
		ChannelPitch chnlCP, 48000
		ChannelPitch chnlJump, 48000
		ChannelPitch chnlTreasure, 48000
		ChannelPitch chnlHit, 48000
	EndIf
EndIf
;While KeyDown(31)			   ;s key
;Wend

If GameRunning And (Not GamePaused)

	;;;;;;;;;;;   Check Keys   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	If KeyDown(200)
		upkey = True
	Else
		upkey = False
	EndIf
	If KeyDown(203)
		leftkey = True
		If PlayerJustHoppedOffLadder = 2 Then PlayerJustHoppedOffLadder = False
	Else
		leftkey = False
	EndIf
	If KeyDown(205)
		rightkey = True
		If PlayerJustHoppedOffLadder = 1 Then PlayerJustHoppedOffLadder = False
	Else
		rightkey = False
	EndIf
	If KeyDown(208)
		downkey = True
	Else
		downkey = False
	EndIf
	If KeyDown(57)
		spacekey = True
	Else
		spacekey = False
	EndIf
	
	;;;;;;;   Determine what elements to update   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	HalfUpdate = HalfUpdate * -1			;used for most actions (30 fps)
	FourthUpdate = FourthUpdate + 1			;used for moving bat&eel ver. & scorpion hor. and swimming (15 fps)
	If FourthUpdate > 3 Then FourthUpdate = 0

	
	;;;;; update variables   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	If PlayerPauseTime = 0
		;Cat, gold, waterfall
		Animflip1 = Animflip1 + 1
		If animflip1 > 1
			animflip1 = 0
			Animation1 = Animation1 + 1
			If Animation1 > 1 Then Animation1 = 0
		EndIf
		
		;bat, scorpion, eel
		Animflip2 = Animflip2 + 1
		If animflip2 > 6
			animflip2 = 0
			Animation2 = Animation2 + 1
			If Animation2 > 1 Then Animation2 = 0
		EndIf
		
		;wave
		Animflip5 = Animflip5 + 1
		If animflip5 > 7
			animflip5 = 0
			curWave = curWave + WaveInc
			If curWave = 2
				curWave = 0
				WaveInc = -1
			EndIf
			If curWave = -2
				curWave = 0
				WaveInc = 1
			EndIf
		EndIf
		
		;bird
		Animflip3 = Animflip3 + 1
		If animflip3 > 15
			animflip3 = 0
			Animation3 = Animation3 + 1
			If Animation3 > 1 Then Animation3 = 0
		EndIf
	EndIf
	If PlayerHurt
		Animflip4 = Animflip4 + 1
		If animflip4 > 6
			animflip4 = 0
			Animation4 = Animation4 + 1
			If Animation4 > 1 Then Animation4 = 0
		EndIf
		crash = 0
	EndIf
	
	PlayerPrevY = PlayerY

	If HalfUpdate = 1 And (Not PlayerHurt)
		If rightkey = False And leftkey = False Then PlayerJustHoppedOnLadder = False
		If upkey = False And downkey = False Then PlayerJustHoppedOffLadder = False
		If spacekey = False And PlayerAction < 2 Then PlayerJustJumped = False
	
		If PlayerAction = 1 Then PlayerAction = 0
		
		If PlayerFallDist > 0 Then PlayerFallDist = PlayerFallDist - 1
		
		If crash > 0
			If (crash Mod 4) = 0 Then ScreenY = ScreenY + 1
			If (crash Mod 4) = 2 Then ScreenY = ScreenY - 1
			crash = crash - 1
		EndIf
		
	EndIf ;HalfUpdate
	
	If PlayerPauseTime > 0 Then PlayerPauseTime = PlayerPauseTime - 1

	If Music > 0
		If Not ChannelPlaying(chnlMusic)
			If Music = 4
				chnlMusic = PlaySound(mscBalloon)
			Else
				chnlMusic = PlaySound(mscChorus)
				Music = 1
			EndIf
		EndIf
	EndIf
	
	;;;;;;;   Process keys   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	
	If FourthUpdate = 1
		If (Not crash) And (Not PlayerHurt)
			If leftkey And (Not rightkey) ;left
				If PlayerAction = 2
					If Board(PlayerLevelX, PlayerLevelY - 1)\graphic = 3
						If PlayerY > 24
							If PlayerX > 58 And PlayerX < 66
								PlayerJustJumped = True
								PlayerAction = 9
								PlayerJumpDist = 14
								PlayerJumpDir = -1
								PlayerX = PlayerX - 2 ;to adjust for initial jumping movement
							EndIf
						EndIf
					EndIf
					If PlayerX > 20 Or Board(PlayerLevelX, PlayerLevelY)\left_blocked = 0 Then PlayerX = PlayerX - 2
					PlayerDir = 1
					If PlayerX = 18
						If downkey And PlayerY = 0 Then PlayerX = 20
					EndIf
				EndIf
			EndIf
			If rightkey And (Not leftkey);right
				If PlayerAction = 2
					If Board(PlayerLevelX, PlayerLevelY - 1)\graphic = 4
						If PlayerY > 24
							If PlayerX > 228 And PlayerX < 236
								PlayerJustJumped = True
								PlayerAction = 9
								PlayerJumpDist = 14
								PlayerJumpDir = 1
								PlayerX = PlayerX + 2 ;to adjust for initial jumping movement
							EndIf
						EndIf
					EndIf
					If PlayerX < 284 Or Board(PlayerLevelX, PlayerLevelY)\right_blocked = 0 Then PlayerX = PlayerX + 2
					PlayerDir = 0
					If PlayerX = 286
						If downkey And PlayerY = 0 Then PlayerX = 284
					EndIf
				EndIf
			EndIf
			If downkey And (Not upkey);down
				If PlayerAction = 2
					If PlayerY > 1
						PlayerY = PlayerY - 1
					ElseIf PlayerLevelY + 1 < 31
						If board(PlayerLevelX, PlayerLevelY + 1)\water = 1
							PlayerY = 45
							PlayerPrevY = PlayerPrevY + 45
							PlayerLevelY = PlayerLevelY + 1
							ScreenY = ScreenY + 45
						EndIf
					EndIf
				EndIf
			EndIf
			If upkey And (Not downkey);up
				If PlayerAction = 2
					If PlayerY > 24
						If board(PlayerlevelX, PlayerLevelY - 1)\ladder = 1
							If PlayerJustHoppedOffLadder = False
								If PlayerX > 122 And PlayerX < 166
									If PlayerX > 146
										PlayerJustHoppedOnLadder = 2 ;right
									Else
										PlayerJustHoppedOnLadder = 1 ;left
									EndIf
									PlayerAction = 3
									If PlayerDir = 0
										curFrame = 1
									Else
										curFrame = 0
									EndIf
									PlayerX = 146
									PlayerY = 40
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf ;upkey
		EndIf ;not crash and not hurt
	EndIf ;FourthUpdate
	
	If HalfUpdate = 1
		If (Not crash) And (Not PlayerHurt)
			If leftkey And (Not rightkey);left
				If PlayerAction < 2
					PlayerAction = 1
					PlayerDir = 1
				EndIf
				If PlayerAction = 1
					If PlayerX > 20 Or board(PlayerLevelX, PlayerLevelY)\left_blocked = 0 Then PlayerX = PlayerX - 2
					If PlayerX < 122 Then PlayerJustHoppedOffLadder = False
				EndIf
				If PlayerAction = 3 Or PlayerAction = 5
					If PlayerJustHoppedOnLadder = False
						If (PlayerY < 7 And PlayerY > 0) Or (PlayerY > 37)
							PlayerAction = 6
							PlayerDir = 1
							PlayerClimb = 0
							PlayerJumpDist = 6
							PlayerJustHoppedOffLadder = 1 ;left
						EndIf
					EndIf
					If PlayerAction = 6
						PlayerFrame = 3
						PlayerX = PlayerX - 2 ;to adjust for transition from climbing to jumping?
					Else
						PlayerFrame = 0
					EndIf
				EndIf
				If PlayerAction = 7
					If PlayerFallDist = 0
						PlayerX = PlayerX - 2
						PlayerFallDist = Rnd(20, 65)
						PlayerDir = 1
					EndIf
				EndIf
				If PlayerAction = 11
					If PlayerX > 20 Then PlayerX = PlayerX - 2
					PlayerDir = 1
				EndIf
			EndIf ;leftkey
		
			If rightkey And (Not leftkey);right
				If PlayerAction < 2
					PlayerAction = 1
					PlayerDir = 0
				EndIf
				If PlayerAction = 1
					If PlayerX < 284 Or board(PlayerLevelX, PlayerLevelY)\right_blocked = 0 Then PlayerX = PlayerX + 2
					If PlayerX > 166 Then PlayerJustHoppedOffLadder = False
				EndIf
				If PlayerAction = 3 Or PlayerAction = 5
					If PlayerJustHoppedOnLadder = False
						If (PlayerY < 7 And PlayerY > 0) Or (PlayerY > 37)
							PlayerAction = 6
							PlayerDir = 0
							PlayerClimb = 0
							PlayerJumpDist = 6
							PlayerJustHoppedOffLadder = 2 ;right
						EndIf
					EndIf
					If PlayerAction = 6
						PlayerFrame = 3
					Else
						PlayerFrame = 0
					EndIf
				EndIf
				If PlayerAction = 7
					If PlayerFallDist = 0
						PlayerX = PlayerX + 2
						PlayerFallDist = Rnd(20, 65)
						PlayerDir = 0
					EndIf
				EndIf
				If PlayerAction = 11
					If PlayerX < 284 Then PlayerX = PlayerX + 2
					PlayerDir = 0
				EndIf
			EndIf ;rightkey
		
			If upkey And (Not downkey);up
				If PlayerAction = 3 Or PlayerAction = 5
					If PlayerClimb > 3
						PlayerClimb = 0
						If PlayerY < 34 Or board(PlayerLevelX, PlayerLevelY - 1)\ladder = 1
							PlayerAction = 3
							PlayerX = 146
							PlayerY = PlayerY + 4
							If PlayerY > 45
								PlayerY = 2
								PlayerPrevY = PlayerPrevY - 45
								PlayerLevelY = PlayerLevelY - 1
								ScreenY = ScreenY - 45
							EndIf
							curFrame = curFrame + 1
							If CurFrame > 1 Then curFrame = 0
						Else
							PlayerAction = 5
							PlayerY = 38
							PlayerX = 144
						EndIf
						If PlayerY = 10 Then PlayerJustHoppedOnLadder = False
					EndIf
				EndIf
				If PlayerAction < 2
					If PlayerY = 0
						If board(PlayerlevelX, PlayerLevelY)\ladder = 1
							If PlayerJustHoppedOffLadder = False
								If PlayerX > 122 And PlayerX < 166
									If PlayerX > 146
										PlayerJustHoppedOnLadder = 2 ;right
									Else
										PlayerJustHoppedOnLadder = 1 ;left
									EndIf
									PlayerAction = 3
									If PlayerDir = 0
										curFrame = 1
									Else
										curFrame = 0
									EndIf
									PlayerX = 146
									PlayerY = 2
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf ;upkey
			
			If downkey And (Not upkey);down
				If PlayerAction = 3 Or PlayerAction = 5
					If PlayerClimb > 3
						PlayerClimb = 0
						If PlayerLevelY + 1 < 31
							lad = Board(PlayerLevelX, PlayerLevelY + 1)\ladder
						Else
							lad = False
						EndIf
						If PlayerY > 4 Or lad = True
							If Board(PlayerLevelX, PlayerLevelY)\ladder = 1
								PlayerAction = 3
								PlayerX = 146
								PlayerY = PlayerY - 4
								If PlayerY < 0
									PlayerY = 42
									PlayerPrevY = PlayerPrevY + 45
									PlayerLevelY = PlayerLevelY + 1
									ScreenY = ScreenY + 45
								EndIf
								curFrame = curFrame + 1
								If CurFrame > 1 Then curFrame = 0
							Else
								If Board(PlayerLevelX, PlayerLevelY)\water = 1
									PlayerAction = 10
									PlayerJumpDir = 0
									PlayerJumpDist = 10
									PlayerFrame = 3
								Else
									PlayerAction = 0
								EndIf
								PlayerX = 144
							EndIf
						Else
							PlayerAction = 0
							PlayerY = 0
							PlayerX = 144
						EndIf
					EndIf
				EndIf
				If PlayerAction < 2
					If PlayerLevelY < 30
						If board(PlayerLevelX, PlayerLevelY+1)\ladder = 1
							If PlayerJustHoppedOffLadder = False
								If PlayerX > 122 And PlayerX < 166
									PlayerAction = 4
									PlayerClimb = 0
									If PlayerX < 144
										curFrame = 0
										PlayerDir = 1
									Else
										curFrame = 1
										PlayerDir = 0
									EndIf
									PlayerX = 144
									PlayerY = 36
									PlayerPrevY = PlayerPrevY + 45
									PlayerLevelY = PlayerLevelY + 1
									ScreenY = ScreenY + 45
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
				If PlayerAction = 11
					BalloonClimb = BalloonClimb - 3
				EndIf
			EndIf ;downkey
			
			If spacekey
				If PlayerAction < 2
					If board(PlayerLevelX, PlayerLevelY)\graphic > 0
						If board(PlayerLevelX, PlayerLevelY)\graphic < 7 Or board(PlayerLevelX, PlayerLevelY)\graphic = 13
							If Not CheckForHole(PlayerX, PlayerLevelX, PlayerLevelY)
								If Not PlayerAction = 8 ;??????????????????????????????????????????????
									If PlayerJustJumped = False
										PlayerJustJumped = True
										PlayerFrame = 3
										PlayerJumpDir = 0
										chnlJump = PlaySound(sndJump)
										If Board(PlayerLevelX, PlayerLevelY - 1)\animal = 7 And (Not leftkey) And (Not rightkey)
											PlayerAction = 12
											PlayerJumpDist = 25
										Else
											PlayerAction = 8
											PlayerJumpDist = 15
											If leftkey
												PlayerJumpDir = -1
												PlayerX = PlayerX + 2 ;to adjust for jumping movement
											EndIf
											If rightkey
												PlayerJumpDir = 1
												PlayerX = PlayerX - 2 ;to adjust for jumping movement
											EndIf
											If (leftkey And rightkey) Then PlayerJumpDir = 0
										EndIf
									EndIf
								EndIf
							EndIf
						EndIf
					EndIf
				EndIf
			EndIf ;spacekey
			
			;Make player jump if collided with mouse
			If MouseCollide
				If PlayerLevelX <> MouseLevelX
					PlayerJustJumped = True
					PlayerAction = 8
					PlayerJumpDist = 15
					PlayerFrame = 3
					PlayerDir = 0
					PlayerJumpDir = 1
					PlayerX = PlayerX + 2 ;to make him clear ledge
					MouseCollide = False
				EndIf
			EndIf
		EndIf ;not crash and not hurt
	EndIf ;HalfUpdate
	
	;If AllUpdate
		If upkey
			If PlayerAction = 11
				BalloonClimb = BalloonClimb + 5
			EndIf
		EndIf
	;EndIf ;AllUpdate
		
	;;;;;;;;;;   Process Actions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	
	If FourthUpdate = 1 And (Not PlayerHurt)
		If PlayerAction = 2
			PlayerFrame = PlayerFrame + .5
			If PlayerFrame > 3.5 Then PlayerFrame = 0
		EndIf
		
		If PlayerAction = 2
			If PlayerY > 27
				If Board(PlayerLevelX, PlayerLevelY - 1)\water = 0
					PlayerY = PlayerY - 1
				EndIf
			EndIf
			If PlayerY < 24 Or Board(PlayerLevelX, PlayerLevelY - 1)\water = 1
				If (Not downkey) Or upkey
					PlayerY = PlayerY + 1
					If PlayerY > 44
						PlayerY = 0
						PlayerPrevY = PlayerPrevY - 45
						PlayerLevelY = PlayerLevelY - 1
						ScreenY = ScreenY - 45	
					EndIf
				EndIf
			EndIf
			If Board(PlayerLevelX, PlayerLevelY - 1)\water = 0
				If PlayerY < 28 And PlayerY > 23 And ((Not downkey) Or upkey)
					PlayerY = PlayerY + PlayerBob
					If PlayerY = 28
						PlayerY = 27
						PlayerBob = -1
					EndIf
					If PlayerY = 23
						PlayerY = 24
						PlayerBob = 1
					EndIf
				EndIf
			EndIf
			If Board(PlayerLevelX, PlayerLevelY)\water = 0
				PlayerFrame = 3
				PlayerAction = 8
				If PlayerDir = 0 Then PlayerJumpDir = 1
				If PlayerDir = 1 Then PlayerJumpDir = -1
				PlayerJumpDist = 1
			EndIf
		EndIf
	EndIf ;FourthUpdate and not hurt
	
	If HalfUpdate = 1 And (Not PlayerHurt)
		If PlayerAction = 1
			PlayerFrame = PlayerFrame + .5
			If PlayerFrame > 4.5 Then PlayerFrame = 0
		ElseIf PlayerAction = 0
			PlayerFrame = 0
		EndIf
		
		If PlayerAction = 6
			If PlayerY = 38
				PlayerY = PlayerY + 3
			EndIf
		EndIf
		
		If PlayerAction = 6
			If PlayerDir = 0
				PlayerX = PlayerX + 2
			Else
				PlayerX = PlayerX - 2
			EndIf
			PlayerJumpDist = PlayerJumpDist - 1
			If PlayerY > 37
				PlayerY = PlayerY + 1
				If PlayerY > 44
					PlayerY = 0
					PlayerPrevY = PlayerPrevY - 45
					PlayerLevelY = PlayerLevelY - 1
					ScreenY = ScreenY - 45
				EndIf
			EndIf
			If PlayerY < 7 And PlayerY > 0
				PlayerY = PlayerY - 1
			EndIf
			If PlayerJumpDist = 0 ;PlayerY should be 0!
				If CheckForHole(PlayerX, PlayerLevelX, PlayerLevelY)
					PlayerY = 45
					PlayerPrevY = PlayerPrevY + 45
					PlayerLevelY = PlayerLevelY + 1
					ScreenY = ScreenY + 45
					If Board(PlayerLevelX, PlayerLevelY)\water = 1
						PlayerAction = 10
						PlayerJumpDir = 0
						PlayerJumpDist = 10
						PlayerFrame = 3
					Else
						graphic = Board(PlayerLevelX, PlayerLevelY)\graphic
						If graphic > 0 And graphic <> 3 And graphic <> 4
							If PlayerAction <> 7
								PlayerFrame = 0
								PlayerFallDist = Rnd(20, 65)
								PlayerAction = 7
							EndIf
						Else
							PlayerFrame = 3
							PlayerAction = 8
							If PlayerDir = 0
								PlayerJumpDir = 1
								PlayerX = PlayerX - 2 ;adjust for new action
							EndIf
							If PlayerDir = 1
								PlayerJumpDir = -1
								PlayerX = PlayerX + 2 ;adjust for new action
							EndIf
							PlayerJumpDist = 1
						EndIf
					EndIf
				Else
					PlayerAction = 1
				EndIf
			EndIf
		EndIf
	
		If PlayerAction < 2 Or PlayerAction = 7
			If PlayerY <> 0
				PlayerY = PlayerY - 2
				If PlayerY < 0 Then PlayerY = 0
			EndIf
			If PlayerY = 0
				If CheckForHole(PlayerX, PlayerLevelX, PlayerLevelY)
					PlayerY = 45
					PlayerPrevY = PlayerPrevY + 45
					PlayerLevelY = PlayerLevelY + 1
					ScreenY = ScreenY + 45
					If Board(PlayerLevelX, PlayerLevelY)\water = 1
						PlayerAction = 10
						PlayerJumpDir = 0
						PlayerJumpDist = 10
						PlayerFrame = 3
					Else
						graphic = Board(PlayerLevelX, PlayerLevelY)\graphic
						If graphic > 0 And graphic <> 3 And graphic <> 4
							If PlayerAction <> 7
								PlayerFrame = 0
								PlayerFallDist = Rnd(20, 65)
								PlayerAction = 7
							EndIf
						Else
							PlayerFrame = 3
							PlayerAction = 8
							PlayerJumpDir = 0
							PlayerJumpDist = 1
						EndIf
					EndIf
				Else
					If PlayerAction = 7
						PlayerAction = 0
						score = score - 100
						If score < 0 Then score = 0
						crash = 12
						chnlHit = PlaySound(sndHit)
					EndIf
				EndIf
			EndIf
		EndIf
		
		If PlayerAction = 4
			If PlayerClimb > 3
				PlayerClimb = 0
				If downkey
					PlayerAction = 3
					PlayerY = 34
					PlayerX = 146
				Else
					PlayerAction = 5
					PlayerY = 38
					PlayerX = 144
				EndIf
			EndIf
		EndIf

		If PlayerAction = 8
			If PlayerJumpDir = 1
				PlayerX = PlayerX + 2
				If PlayerX > 282 And board(PlayerLevelX, PlayerLevelY)\right_blocked > 0
					PlayerX = PlayerX - 4
					PlayerDir = 1
					PlayerJumpDir = -1
				EndIf
			Else If PlayerJumpDir = -1
				PlayerX = PlayerX - 2
				If PlayerX < 22 And board(PlayerLevelX, PlayerLevelY)\left_blocked > 0
					PlayerX = PlayerX + 4
					PlayerDir = 0
					PlayerJumpDir = 1
				EndIf
			EndIf
			PlayerJumpDist = PlayerJumpDist - 1
			Select PlayerJumpDist
				;Case 15
				;	PlayerY = PlayerY + 2
				Case 14
					PlayerY = PlayerY + 4
				Case 13
					PlayerY = PlayerY + 2
				Case 12
					PlayerY = PlayerY + 1
				Case 11
					PlayerY = PlayerY + 1
				Case 10
					PlayerY = PlayerY + 0
				Case 9
					PlayerY = PlayerY + 0
				Case 8
					PlayerY = PlayerY + 0
				Case 7
					PlayerY = PlayerY + 0
				Case 6
					PlayerY = PlayerY - 1
				Case 5
					PlayerY = PlayerY - 0
				Case 4
					PlayerY = PlayerY - 2
				Case 3
					PlayerY = PlayerY - 1
				Case 2
					PlayerY = PlayerY - 2
				Case 1
					PlayerY = PlayerY - 1
			End Select
			If PlayerJumpDist = 0
				For iter = 1 To 2
					If PlayerY = 0
						If Not CheckForHole(PlayerX, PlayerLevelX, PlayerLevelY)
							PlayerAction = 1
						Else
							PlayerY = 45
							PlayerPrevY = PlayerPrevY + 45
							PlayerLevelY = PlayerLevelY + 1
							ScreenY = ScreenY + 45
							PlayerJumpDist = 1
							If Board(PlayerLevelX, PlayerLevelY)\water = 1
								PlayerAction = 10
								PlayerJumpDist = 10
								PlayerFrame = 3
								If PlayerJumpDir = 1
									PlayerX = PlayerX - 2 ;adjust for new action
								ElseIf PlayerJumpDir = -1
									PlayerX = PlayerX + 2 ;adjust for new action
								EndIf
							Else
								graphic = board(PlayerLevelX, PlayerLevelY - 1)\graphic
								If graphic > 0 And graphic <> 3 And graphic <> 4 And graphic <> 13
									PlayerFallDist = Rnd(20, 65)
									PlayerAction = 7
									PlayerJumpDir = 0
									PlayerFrame = 0
								EndIf
								graphic = board(PlayerLevelX, PlayerLevelY)\graphic
								If graphic = 0 Or graphic = 3 Or graphic = 4 Or graphic = 13
									PlayerAction = 8
									PlayerFrame = 3
								EndIf
								If graphic > 0 And graphic <> 3 And graphic <> 4 And graphic <> 13
									If PlayerAction <> 7
										PlayerFallDist = Rnd(20, 65)
										PlayerAction = 7
										PlayerJumpDir = 0
										PlayerFrame = 0
									EndIf
								EndIf
							EndIf
						EndIf
					Else
						PlayerJumpDist = 1
						PlayerY = PlayerY - 1
					EndIf
				Next
			EndIf
		EndIf ;playeraction=8
		
		If PlayerAction = 9
			If PlayerJumpDir = 1
				PlayerX = PlayerX + 2
			Else If PlayerJumpDir = -1
				PlayerX = PlayerX - 2
			EndIf
			PlayerJumpDist = PlayerJumpDist - 1
			Select PlayerJumpDist
				Case 13
					PlayerY = 44
				Case 12
					PlayerY = 44
					PlayerFrame = 3
				Case 11
					PlayerPrevY = PlayerPrevY - 45
					PlayerLevelY = PlayerLevelY - 1
					ScreenY = ScreenY - 45
					PlayerY = 0
				Case 10
					PlayerY = 1
				Case 9
					PlayerY = 1
				Case 8
					PlayerY = 2
				Case 7
					PlayerY = 2
				Case 6
					PlayerY = 3
				Case 5
					PlayerY = 2
				Case 4
					PlayerY = 2
				Case 3
					PlayerY = 1
				Case 2
					PlayerY = 0
				Case 1
					PlayerY = 0
				Case 0
					PlayerY = 0
					PlayerAction = 1
			End Select
		EndIf ;playeraction=9
		
		If PlayerAction = 10
			If PlayerJumpDir = 1
				PlayerX = PlayerX + 2
			Else If PlayerJumpDir = -1
				PlayerX = PlayerX - 2
			EndIf
			For iter = 1 To 2
				PlayerJumpDist = PlayerJumpDist - 1
				If PlayerJumpDist > 0
					PlayerY = PlayerY - 1
				Else
					PlayerAction = 2
					iter = 2
				EndIf
			Next
		EndIf ;playeraction=10
		
		If PlayerAction = 12
			PlayerJumpDist = PlayerJumpDist - 1
			Select PlayerJumpDist
				Case 24
					PlayerY = PlayerY + 2
				Case 23
					PlayerY = PlayerY + 2
				Case 22
					PlayerY = PlayerY + 2
				Case 21
					PlayerY = PlayerY + 2
				Case 20
					PlayerY = PlayerY + 2
				Case 19
					PlayerY = PlayerY + 2
				Case 18
					PlayerY = PlayerY + 2
				Case 17
					PlayerY = PlayerY + 2
				Case 16
					PlayerY = PlayerY + 2
				Case 15
					PlayerY = PlayerY + 2
				Case 14
					PlayerY = PlayerY + 2
				Case 13
					PlayerY = PlayerY + 2
				Case 12
					PlayerY = PlayerY + 2
				Case 11
					PlayerY = PlayerY - 2
				Case 10
					PlayerY = PlayerY - 2
				Case 9
					PlayerY = PlayerY - 2
				Case 8
					PlayerY = PlayerY - 2
				Case 7
					PlayerY = PlayerY - 2
				Case 6
					PlayerY = PlayerY - 2
				Case 5
					PlayerY = PlayerY - 2
				Case 4
					PlayerY = PlayerY - 2
				Case 3
					PlayerY = PlayerY - 2
				Case 2
					PlayerY = PlayerY - 2
				Case 1
					PlayerY = PlayerY - 2
					PlayerAction = 0
			End Select
		EndIf ;playeraction=12
							
		;Check if player went offscreen
		If PlayerX > 284
			PlayerX = 26
			PlayerLevelX = PlayerLevelX + 1
		EndIf
		If PlayerX < 20
			PlayerX = 280
			PlayerLevelX = PlayerLevelX - 1
		EndIf
	EndIf ;HalfUpdate and not hurt
	
	;If AllUpdate
		If PlayerAction = 11
			If BalloonClimb > 4
				BalloonClimb = 0
				PlayerY = PlayerY + 1
				If PlayerY > 44
					PlayerPrevY = PlayerPrevY - 45
					PlayerLevelY = PlayerLevelY - 1
					ScreenY = ScreenY - 45
					PlayerY = PlayerY - 45
				EndIf
			EndIf
			If Board(PlayerLevelX, PlayerLevelY - 1)\background_image = 4
				If PlayerY > 3
					PlayerAction = 8
					PlayerJumpDist = 1
					PopBalloon = True
					If HalfUpdate <> 1
						PlayerScreenY = PlayerScreenY - (PlayerY - PlayerPrevY)
					EndIf
				EndIf
			EndIf
		EndIf ;playeraction=11
	;endif ;AllUpdate	


	;;;;;;;;   update variables   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

	If HalfUpdate = 1 Or (FourthUpdate = 1 And (PlayerAction = 2 Or PlayerAction = 10)) Or (PlayerAction = 11 And BalloonClimb = 0)
		PlayerScreenY = PlayerScreenY - (PlayerY - PlayerPrevY)
	EndIf
	
	If HalfUpdate = 1
		If PlayerLevelY > 2
			If ScreenY <> 152
				SubLevel = True
			EndIf
		EndIf
		
		If PlayerAction > 2
			PlayerClimb = PlayerClimb + 1
		EndIf
	EndIf ;HalfUpdate

	If PlayerAction < 3
		curFrame = Floor(PlayerFrame)
	EndIf

	BalloonClimb = BalloonClimb + 3
	
	If PlayerHurt
		If PlayerPauseTime = 1
			PlayerAction = 0
			PlayerFrame = 0
			chnlMusic = PlaySound(mscSad)
			Music = 3
		EndIf
		If PlayerPauseTime = 0
			If PlayerLevelX = PlayerLastCheckPoint\levelX
				If PlayerX > PlayerLastCheckPoint\x
					PlayerDir = 1
					PlayerX = PlayerX - 2
				EndIf
				If PlayerX < PlayerLastCheckPoint\x
					PlayerDir = 0
					PlayerX = PlayerX + 2
				EndIf
			EndIf
			If PlayerLevelX > PlayerLastCheckPoint\levelX
				PlayerDir = 1
				PlayerX = PlayerX - 2
			EndIf
			If PlayerX < 20
				PlayerX = 282
				PlayerLevelX = PlayerLevelX - 1
			EndIf
			If PlayerLevelX < PlayerLastCheckPoint\levelX
				PlayerDir = 0
				PlayerX = PlayerX + 2
			EndIf
			If PlayerX > 284
				PlayerX = 22
				PlayerLevelX = PlayerLevelX + 1
			EndIf
			If PlayerLevelY = PlayerLastCheckPoint\levelY
				If PlayerY > 0
					PlayerY = PlayerY - 1
				EndIf
			EndIf
			If PlayerLevelY < PlayerLastCheckPoint\levelY
				PlayerY = PlayerY - 1
				If PlayerY < 0 Then PlayerY = 0
				If PlayerY = 0
					PlayerY = 45
					PlayerPrevY = PlayerPrevY + 45
					PlayerLevelY = PlayerLevelY + 1
					ScreenY = ScreenY + 45
				EndIf
			EndIf
			If PlayerLevelY > PlayerLastCheckPoint\levelY
				PlayerY = PlayerY + 1
				If PlayerY > 45 Then PlayerY = 45
				If PlayerY = 45
					PlayerY = 0
					PlayerPrevY = PlayerPrevY - 45
					PlayerLevelY = PlayerLevelY - 1
					ScreenY = ScreenY - 45
				EndIf			
			EndIf
			If PlayerLevelX = PlayerLastCheckPoint\levelX
				If PlayerX = PlayerLastCheckPoint\x
					If PlayerLevelY = PlayerLastCheckPoint\levelY
						If PlayerY = 0
							PlayerHurt = False
						EndIf
					EndIf
				EndIf
			EndIf
			score = score - 7
			If score < 0 Then score = 0
		EndIf ;playerpausetime
		PlayerScreenY = PlayerScreenY - (PlayerY - PlayerPrevY)
	EndIf ;playerhurt
	
	;testing purposes only -----------------------------------------------------------------
	If FrameRate
		time = MilliSecs()-curTime
		Text 5 + 8 * (time < 10), 0, MilliSecs()-curTime
	EndIf
	;testing purposes only------------------------------------------------------------------

	If KeyHit(1)
		GamePaused = True
		PauseChannel chnlMusic
		FlushKeys()
	EndIf
	
	;scroll screen if necessary
	If PlayerHurt
		If PlayerScreenY > 140
			scrollScreen = scrollscreen - 2
		EndIf
		If PlayerScreenY < 61
			scrollScreen = scrollscreen + 2
		EndIf
		
		If scrollscreen < 0
			scrollscreen = scrollscreen + 2
			ScreenY = ScreenY - 2
			PlayerScreenY = PlayerScreenY - 2
		EndIf
		If scrollscreen > 0
			scrollscreen = scrollscreen - 2
			ScreenY = ScreenY + 2
			PlayerScreenY = PlayerScreenY + 2
		EndIf	
	Else
		If PlayerScreenY > 137
			If PlayerAction = 3
				If scrollscreen > -2 Then scrollscreen = scrollscreen - 45
			Else
				scrollScreen = scrollscreen - 1			
			EndIf
		EndIf
		If PlayerScreenY < 116
			If PlayerScreenY < 105 And Board(PlayerLevelX, PlayerLevelY - 1)\animal = 7 And PlayerAction <> 12
				scrollscreen = scrollscreen + 1
			ElseIf PlayerScreenY < 61
				If PlayerAction = 3
					If scrollscreen < 2 scrollscreen = scrollscreen + 45
				Else
					scrollScreen = scrollScreen + 1
				EndIf
			Else
				If PlayerAction = 11
					scrollscreen = scrollscreen + 1
				EndIf
			EndIf
		EndIf
		
		If scrollscreen < 0
			scrollscreen = scrollscreen + 1
			ScreenY = ScreenY - 1
			PlayerScreenY = PlayerScreenY - 1
		EndIf
		If scrollscreen > 0
			scrollscreen = scrollscreen - 1
			ScreenY = ScreenY + 1
			PlayerScreenY = PlayerScreenY + 1
		EndIf
	EndIf ;playerhurt


	;;;;;;;;;;;;;;   Manage Animals   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	;Erase offscreen animals
	For curAnimal.Animal = Each Animal
		deleteAnimal = False
		If curAnimal\levelX <> PlayerLevelX Then deleteAnimal = True
		If curAnimal\levelY < PlayerLevelY - 3 Then deleteAnimal = True
		If curAnimal\levelY > PlayerLevelY + 3 Then deleteAnimal = True
		If deleteAnimal = True Then Delete curAnimal 
	Next
	
	;Replace balloons if they are offscreen
	If BalloonBoardX <> 0
		If PlayerAction <> 11
			If BalloonBoardX <> PlayerLevelX Or (BalloonBoardY < PlayerLevelY - 2) Or (BalloonBoardY > PlayerLevelY + 2)
				Board(BalloonBoardX, BalloonBoardY)\animal = 7
				BalloonBoardX = 0
				BalloonBoardY = 0
				If Balloon2BoardX <> 0
					BalloonBoardX = Balloon2BoardX
					BalloonBoardY = Balloon2BoardY
					Balloon2BoardX = 0
					Balloon2BoardY = 0
				EndIf
			EndIf
		EndIf	
	EndIf
	;Create new onscreen animals
	For yiter = (PlayerLevelY + 3) To (PlayerLevelY - 3) Step -1
		If yiter > 0 And yiter < 31
			If board(PlayerLevelX, yiter)\animal
				newAnimal = True
				If yiter = PlayerLevelY + 3 Or yiter = PlayerLevelY - 3
					If board(PlayerLevelX, yiter)\animal <> 4 Then newAnimal = False
				EndIf
				For curAnimal.Animal = Each Animal
					If curAnimal\levelY = yiter Then newAnimal = False
				Next
				If newAnimal
					curAnimal.Animal = New Animal
					curAnimal\animaltype = board(PlayerLevelX, yiter)\animal
					curAnimal\levelX = PlayerLevelX
					curAnimal\levelY = yiter
					Select curAnimal\animaltype
						Case 1 ;bat
							curAnimal\x = 56
							curAnimal\y = batY
							curAnimal\dir = 1
							curAnimal\state = 0 ;has no state
						Case 2 ;bird
							curAnimal\x = 268
							curAnimal\y = birdY
							curAnimal\dir = -1
							curAnimal\state = 0 ;has no state
						Case 3 ;scorpion
							curAnimal\x = 120
							curAnimal\y = 10
							curAnimal\dir = 1
							curAnimal\state = 0 ;has no state
						Case 4 ;frog
							curAnimal\x = frogX
							curAnimal\y = frogY
							curAnimal\dir = frogDir ;1=right, -1=left
							curAnimal\state = frogState ;0=sitting, 1=jumping
						Case 5 ;eel
							curAnimal\x = 56
							curAnimal\y = -batY + 2 - 1*(batY > 8)
							curAnimal\dir = 0 ;0=tail up, 1=tail down
							curAnimal\state = 0;0=not shocking, 1=shocking
						Case 6 ;mouse
							curAnimal\x = 64 ;needs work
							curAnimal\y = 0
							curAnimal\dir = 1
							curAnimal\state = 1 ;0=stopped, 1=running
							MouseStopped = False
							MouseXPos = 64
						Case 7 ;bat that can become balloon
							curAnimal\x = 56
							curAnimal\y = batY
							curAnimal\dir = 1
							curAnimal\state = 0 ;0=bat 1=balloon
						;Case 8 ;bat that has become balloon
						;	curAnimal\x = PlayerX + 2 * (PlayerDir = 0)
						;	curAnimal\y = PlayerY + 28
						;	curAnimal\dir = PlayerDir
						;	curAnimal\state = 0 ;0=alone 1=attached to player
					End Select
				EndIf
			EndIf
		EndIf
	Next
	
	;update animal variables
	If FourthUpdate = 1 And PlayerPauseTime = 0
		;bat
		batY = batY + batDir
		If batY > 12
			batY = 12
			batDir = -1
		EndIf
		If batY < 5
			batY = 5
			batDir = 1
		EndIf
		;bird
		birdY = birdY + birdDir
		If birdY > 3
			birdY = 3
			birdDir = -1
		EndIf
		If birdY < -12
			birdY = -12
			birdDir = 1
		EndIf
		;eel
		eelY = eelY + eelDir
		If eelY > 11
			eelY = 11
			eelDir = -1
		EndIf
		If eelY < 4
			eelY = 4
			eelDir = 1
		EndIf
	EndIf ;fourthupdate
	If HalfUpdate = 1 And PlayerPauseTime = 0
		;frog
		frogInc = frogInc + 1
		Select frogInc
			Case 1
				frogY = 12
				frogState = 1
			Case 2
				frogY = 11
			Case 3
				frogY = 10
				frogX = frogX + frogDir * 2
			Case 4
				frogY = 8
				frogX = frogX + frogDir * 2
			Case 5
				frogY = 7
			Case 6
				frogY = 6
				frogX = frogX + frogDir * 2
			Case 7
				frogY = 5
			Case 8
				frogY = 4
				frogX = frogX + frogDir * 2
			Case 9
				frogY = 3
			Case 10
				frogY = 2
				frogX = frogX + frogDir * 2
			Case 11
				frogY = 1
			Case 12
				frogY = 0
				frogX = frogX + frogDir * 2
			Case 13
				frogY = -1
			Case 14
				frogY = -2
				frogX = frogX + frogDir * 2
			Case 15
				frogY = -3
			Case 16
				frogY = -2
				frogX = frogX + frogDir * 2
			Case 17
				frogY = -1
			Case 18
				frogY = 0
				frogX = frogX + frogDir * 2
			Case 19
				frogY = 1
				frogX = frogX + frogDir * 2
			Case 20
				frogY = 2
				frogX = frogX + frogDir * 2
			Case 21
				frogY = 3
			Case 22
				frogY = 4
				frogX = frogX + frogDir * 2
			Case 23
				frogY = 5
			Case 24
				frogY = 6
				frogX = frogX + frogDir * 2
			Case 25
				frogY = 7
			Case 26
				frogY = 8
				frogX = frogX + frogDir * 2
			Case 27
				frogY = 10
			Case 28
				frogY = 11
				frogX = frogX + frogDir * 2
			Case 29
				frogY = 12
			Case 30
				frogY = 15
				frogState = 0
			Case 46
				frogDir = frogDir * -1
		End Select
		If frogInc = 62 Then frogInc = 0
	EndIf ;halfupdate
	
	;Move animals
	If FourthUpdate = 1 And PlayerPauseTime = 0
		For curAnimal.Animal = Each Animal
			Select curAnimal\animaltype
				Case 3 ;scorpion
					curAnimal\x = curAnimal\x + curAnimal\dir * 2
					If curAnimal\x > 312 Then curAnimal\x = -6
					If curAnimal\x < -6 Then curAnimal\x = 312
			End Select
		Next
	EndIf ;fourthupdate
	If HalfUpdate = 1 And PlayerPauseTime = 0
		For curAnimal.Animal = Each Animal
			Select curAnimal\animaltype
				Case 1 ;bat
					curAnimal\x = curAnimal\x + 2
					If curAnimal\x > 312 Then curAnimal\x = -6
				Case 2 ;bird
					curAnimal\x = curAnimal\x + curAnimal\dir * 2
					If curAnimal\x > 312 Then curAnimal\x = -6
					If curAnimal\x < -6 Then curAnimal\dir = 1
				Case 3 ;scorpion
					chase = False
					If PlayerLevelY = curAnimal\levelY And PlayerY = 0 Then chase = True
					If PlayerAction = 3 Or PlayerAction = 5
						If PlayerLevelY = curAnimal\levelY And PlayerY < 36 Then chase = True
						If PlayerLevelY = curAnimal\levelY + 1 And PlayerY > 30 Then chase = True
					EndIf
					If chase
						prevDir = curAnimal\dir
						If PlayerX < curAnimal\x + 4 Then curAnimal\dir = -1
						If PlayerX > curAnimal\x + 4 Then curAnimal\dir = 1
						If curAnimal\dir <> prevDir Then curAnimal\x = curAnimal\x + curAnimal\dir * 2
					EndIf
				Case 4 ;frog
					curAnimal\x = frogX
					curAnimal\y = frogY
					curAnimal\dir = frogDir
					curAnimal\state = frogState
				Case 5 ;eel
					curAnimal\x = curAnimal\x + 2
					If curAnimal\x > 312 Then curAnimal\x = -6
					curAnimal\y = -eelY + 1*(eelY > 7)
					curAnimal\dir = Animation2
					eelshock = eelshock + 1
					If eelshock > 1
						curAnimal\state = Rnd(0, 1)
						eelshock = 0
					EndIf
				Case 6 ;mouse
					If curAnimal\levelY = PlayerLevelY
						If curAnimal\x < 288
							curAnimal\x = curAnimal\x + 4
							MouseXPos = MouseXPos + 4
							MouseAnim = MouseAnim + 1
							If MouseAnim > 1 Then MouseAnim = 0
						Else
							curAnimal\state = 0
							MouseStopped = True
							MouseAnim = 0
						EndIf
					EndIf
				Case 7 ;bat that can become balloon
					curAnimal\x = curAnimal\x + 2
					If curAnimal\x > 312
						curAnimal\x = -6
						If curAnimal\levelY = PlayerLevelY - 1
							If (PlayerAction < 2 Or PlayerAction = 12) And (Not PlayerHurt)
								curAnimal\animaltype = 8 ;balloon
								curAnimal\dir = 1
							EndIf
						EndIf
					EndIf
				Case 8 ;bat that has become balloon
					If curAnimal\state = 0
						curAnimal\x = curAnimal\x + 2
						If curAnimal\x > 312 Then curAnimal\x = -6
						curAnimal\y = baty - 7
					EndIf
			End Select
		Next
	EndIf ;HalfUpdate
	;If AllUpdate
		For curAnimal.Animal = Each Animal
			If curAnimal\animaltype = 8
				If curAnimal\state = 1
					curAnimal\x = PlayerX + 2 * (PlayerDir = 0)
					curAnimal\dir = PlayerDir
					curAnimal\y = PlayerY + 28
					curAnimal\LevelY = PlayerLevelY
				EndIf
			EndIf
		Next
	;EndIf ;AllUpdate

	
	
	;;;;;;;;;;;;;;   Check for Collisions   ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
	If Not PlayerHurt
		image1 = Player
		x1 = PlayerX
		y1 = ScreenY - PlayerY
		frame1 = curFrame
		Select PlayerAction
			Case 0
				frame1 = 0 + 6*(PlayerDir = 1)
			Case 1
				frame1 = curFrame + 1 + 6*(PlayerDir = 1)
			Case 2
				image1 = Plays
				frame1 = curFrame + 4*(PlayerDir = 1)
			Case 3
				image1 = Playl
			Case 4
				image1 = Playlbo
				x1 = PlayerX - 2
			Case 5
				frame1 = 0 + 6*(PlayerDir = 1)
			Case 6
				frame1 = 3 + 6*(PlayerDir = 1)
			Case 7
				frame1 = 0 + 6*(PlayerDir = 1)
			Case 8
				frame1 = PlayerFrame + 6*(PlayerDir = 1)
			Case 9
				frame1 = PlayerFrame + 6*(PlayerDir = 1)
			Case 10
				frame1 = PlayerFrame + 6*(PlayerDir = 1)
			Case 11
				image1 = Playb
				frame1 = PlayerDir
			Case 12
				frame1 = PlayerFrame + 6*(PlayerDir = 1)
		End Select
		;collide with animal
		For curAnimal.Animal = Each Animal
			YOffset = ((PlayerLevelY - curAnimal\levelY) * -45) + ScreenY
			Select curAnimal\animaltype
				Case 1 ;bat
					If ImagesCollide(image1, x1, y1, frame1, Bat, curAnimal\x, -batY + YOffset, Animation2)
						PlayerHurt = True
						PlayerPauseTime = 100
					EndIf
				Case 2 ;bird
					If ImagesCollide(image1, x1, y1, frame1, Bird, curAnimal\x, birdY + YOffset, Animation3 + 2*(curAnimal\dir = 1))
						PlayerHurt = True
						PlayerPauseTime = 100
					EndIf
				Case 3 ;scorpion
					If ImagesCollide(image1, x1, y1, frame1, Scorpion, curAnimal\x, curAnimal\y + YOffset, Animation2 + 2*(curAnimal\dir = 1))
						PlayerHurt = True
						PlayerPauseTime = 100
					EndIf
				Case 4 ;frog
					If ImagesCollide(image1, x1, y1, frame1, Frog, curAnimal\x, curAnimal\y + YOffset, 2 - 2*(curAnimal\dir = -1) + curAnimal\state)
						PlayerHurt = True
						PlayerPauseTime = 100
					EndIf
				Case 5 ;eel
					If ImagesCollide(image1, x1, y1, frame1, Eel, curAnimal\x, curAnimal\y + YOffset, curAnimal\dir * 2)
						PlayerHurt = True
						PlayerPauseTime = 100
					EndIf
				Case 6 ;mouse
					If PlayerLevelY = curAnimal\levelY
						If x1 < curAnimal\x + 10 And x1 > curAnimal\x - 4
							If curAnimal\state = 1
								PlayerX = curAnimal\x + 10
								PlayerDir = 0
								MouseCollide = True
								MouseLevelX = curAnimal\levelX
							EndIf
						EndIf
					EndIf
				Case 7 ;bat that can become balloon
					If ImagesCollide(image1, x1, y1, frame1, Bat, curAnimal\x, -batY + YOffset, Animation2)
						PlayerHurt = True
						PlayerPauseTime = 100
					EndIf
				Case 8 ;balloon
					If curAnimal\state = 0
						If ImagesCollide(image1, x1, y1, frame1, Balloon, curAnimal\x, -curAnimal\y + YOffset, curAnimal\dir)
							PlayerAction = 11
							If BalloonBoardX = 0
								BalloonBoardX = curAnimal\LevelX
								BalloonBoardY = curAnimal\LevelY
								Board(BalloonBoardX, BalloonBoardY)\animal = 0
							Else
								Balloon2BoardX = curAnimal\LevelX
								Balloon2BoardY = curAnimal\LevelY
								Board(Balloon2BoardX, Balloon2BoardY)\animal = 0
							EndIf								
							curAnimal\levelY = PlayerLevelY
							curAnimal\x = PlayerX + 2 * (PlayerDir = 0)
							curAnimal\y = PlayerY + 28
							curAnimal\dir = PlayerDir
							curAnimal\state = 1
							StopChannel chnlMusic
							chnlMusic = PlaySound(mscBalloon)
							Music = 4
						EndIf
					EndIf
			End Select
		Next
		If PlayerHurt = True
			StopChannel chnlMusic
			StopChannel chnlCP
			StopChannel chnlJump		
			Music = 0
			If PlayerAction = 11
				PlayerAction = 8
				PlayerJumpDist = 1
				PopBalloon = True
			EndIf		
		EndIf
		;balloon collide with animal
		For ThisBalloon.Animal = Each Animal
			If ThisBalloon\animaltype = 8
				BallYOff = ((PlayerLevelY - ThisBalloon\levelY) * -45) + ScreenY
				For curAnimal.Animal = Each Animal
					YOffset = ((PlayerLevelY - curAnimal\levelY) * -45) + ScreenY
					Select curAnimal\animaltype
						Case 1
							If ImagesCollide(Balloon, ThisBalloon\x, -ThisBalloon\y + BallYOff, ThisBalloon\dir, Bat, curAnimal\x, -batY + YOffset, Animation2)
								PlayerAction = 8
								PlayerJumpDist = 1
								PopBalloon = True
							EndIf
						Case 2
							If ImagesCollide(Balloon, ThisBalloon\x, -ThisBalloon\y + BallYOff, ThisBalloon\dir, Bird, curAnimal\x, birdY + YOffset, Animation3 + 2*(curAnimal\dir = 1))
								PlayerAction = 8
								PlayerJumpDist = 1
								PopBalloon = True
							EndIf
						Case 7
							If ImagesCollide(Balloon, ThisBalloon\x, -ThisBalloon\y + BallYOff, ThisBalloon\dir, Bat, curAnimal\x, -batY + YOffset, Animation2)
								PlayerAction = 8
								PlayerJumpDist = 1
								PopBalloon = True
							EndIf
					End Select
				Next
			EndIf
		Next
		;Pop Balloon if necessary
		If PopBalloon = True
			PopBalloon = False
			For curAnimal.Animal = Each Animal
				If curAnimal\animaltype = 8 Then Delete curAnimal
			Next
			If PlayerHurt
				PlaySound sndSPop
			Else
				PlaySound sndBPop
				StopChannel chnlMusic
				chnlMusic = PlaySound(mscChorus)
				Music = 1
			EndIf
		EndIf
		;collide with checkpoint
		If Board(PlayerLevelX, PlayerLevelY)\Checkpoint = True
			If PlayerLastCheckpoint\levelX <> PlayerLevelX Or PlayerLastCheckpoint\levelY <> PlayerLevelY
				YOffset = ScreenY
				If ImagesCollide(image1, x1, y1, frame1, Checkpoint, PlayerLastCheckpoint\x, 15 + YOffset, 0)
					PlayerLastCheckpoint\LevelX = PlayerLevelX
					PlayerLastCheckpoint\LevelY = PlayerLevelY
					chnlCP = PlaySound(sndCheckpoint)
				EndIf
			EndIf
		EndIf
		;collide with treasure
		treasure = Board(PlayerLevelX, PlayerLevelY)\treasure
		If treasure > 0
			YOffset = ScreenY
			Select treasure
				Case 1 ;gold
					If ImagesCollide(image1, x1, y1, frame1, Goldtemplate, 248, 8 + YOffset, 0)
						Score = Score + 5000
						Board(PlayerLevelX, PlayerLevelY)\treasure = 0
						chnlTreasure = PlaySound(sndTreasure)
						If Music <> 2
							StopChannel chnlMusic
							chnlMusic = PlaySound(mscHappy)
							Music = 2
						EndIf
						GoldBars = GoldBars + 1
					EndIf
				Case 2 ;gold
					If ImagesCollide(image1, x1, y1, frame1, Goldtemplate, 56, 8 + YOffset, 0)
						Score = Score + 5000
						Board(PlayerLevelX, PlayerLevelY)\treasure = 0
						chnlTreasure = PlaySound(sndTreasure)
						If Music <> 2
							StopChannel chnlMusic
							chnlMusic = PlaySound(mscHappy)
							Music = 2
						EndIf
						GoldBars = GoldBars + 1
					EndIf
				Case 3 ;Cat
					If ImagesCollide(image1, x1, y1, frame1, Cat, 54, -1 + YOffset, Animation1)
						Score = Score + 10000
						Board(PlayerLevelX, PlayerLevelY)\treasure = 0
						chnlTreasure = PlaySound(sndTreasure)
						If Music <> 2
							StopChannel chnlMusic
							chnlMusic = PlaySound(mscHappy)
							Music = 2
						EndIf
						NumTreasures = NumTreasures - 1
					EndIf
				Case 4 ;girl
					If ImagesCollide(image1, x1, y1, frame1, Girl, 58, 2 + YOffset, 0)
						Score = Score + 10000
						Board(PlayerLevelX, PlayerLevelY)\treasure = 0
						chnlTreasure = PlaySound(sndTreasure)
						If Music <> 2
							StopChannel chnlMusic
							chnlMusic = PlaySound(mscHappy)
							Music = 2
						EndIf
						NumTreasures = NumTreasures - 1
					EndIf
				Case 5 ;ring
					If ImagesCollide(image1, x1, y1, frame1, Ring, 136, 5 + YOffset, 0)
						Score = Score + 20000
						Board(PlayerLevelX, PlayerLevelY)\treasure = 0
						chnlTreasure = PlaySound(sndTreasure)
						If Music <> 2
							StopChannel chnlMusic
							chnlMusic = PlaySound(mscHappy)
							Music = 2
						EndIf
						NumTreasures = NumTreasures - 1
					EndIf
				Case 6 ;mouse
					If MouseStopped
						For curAnimal.Animal = Each Animal
							If curAnimal\animaltype = 6
								MouseState = curAnimal\state
							EndIf
						Next
						If ImagesCollide(image1, x1, y1, frame1, Mouse, MouseXPos, 7 + YOffset, MouseAnim)
							Score = Score + 15000
							Board(PlayerLevelX, PlayerLevelY)\treasure = 0
							Board(PlayerLevelX, PlayerLevelY)\animal = 0
							;Erase mouse from animals
							For curAnimal.Animal = Each Animal
								If curAnimal\animaltype = 6 Then Delete curAnimal
							Next
							MouseCollide = False
							chnlTreasure = PlaySound(sndTreasure)
							If Music <> 2
								StopChannel chnlMusic
								chnlMusic = PlaySound(mscHappy)
								Music = 2
							EndIf
						EndIf
					EndIf
			End Select
		EndIf
		If NumTreasures = 0
			GameRunning = False
			GameWon = True
			;Erase animals on same level as player
			For curAnimal.Animal = Each Animal
				If curAnimal\levelY = PlayerLevelY Then Delete curAnimal 
			Next
		EndIf
	EndIf ;not playerhurt
EndIf ;if gamerunning

If GameWon
	HalfUpdate = HalfUpdate * -1
	If HalfUpdate = 1
		If PlayerAction = 6
			If PlayerDir = 0
				PlayerX = PlayerX + 2
			Else
				PlayerX = PlayerX - 2
			EndIf
			PlayerJumpDist = PlayerJumpDist - 1
			If PlayerY > 37
				PlayerY = PlayerY + 1
				If PlayerY > 44
					PlayerY = 0
					PlayerPrevY = PlayerPrevY - 45
					PlayerLevelY = PlayerLevelY - 1
					ScreenY = ScreenY - 45
				EndIf
			EndIf
			If PlayerY < 7 And PlayerY > 0
				PlayerY = PlayerY - 1
			EndIf
			If PlayerJumpDist = 0 ;PlayerY should be 0!
				PlayerAction = 1
			EndIf
		EndIf
		If PlayerAction < 2
			If board(PlayerLevelX, PlayerLevelY)\graphic > 0
				If board(PlayerLevelX, PlayerLevelY)\graphic < 7 Or Board(PlayerLevelX, PlayerLevelY)\graphic = 13
					If Not CheckForHole(PlayerX, PlayerLevelX, PlayerLevelY)
						PlayerAction = 8
						PlayerJumpDist = 15
						PlayerFrame = 3
						PlayerDir = 0
						chnlJump = PlaySound(sndJump)
					Else
						Repeat
							PlayerX = PlayerX - 2
							If PlayerX < 0 Then RuntimeError("Pitfall II - Error: Player off screen. Fatal error.")
						Until Not CheckForHole(PlayerX, PlayerLevelX, PlayerLevelY)
					EndIf
				EndIf
			EndIf
		EndIf
		If PlayerAction = 8
			PlayerJumpDist = PlayerJumpDist - 1
			Select PlayerJumpDist
				Case 15
					PlayerY = 2
				Case 14
					PlayerY = 4
				Case 13
					PlayerY = 6
				Case 12
					PlayerY = 7
				Case 11
					PlayerY = 8
				Case 10
					PlayerY = 8
				Case 9
					PlayerY = 8
					PlayerDir = 1
				Case 8
					PlayerY = 8
				Case 7
					PlayerY = 8
				Case 6
					PlayerY = 7
				Case 5
					PlayerY = 7
				Case 4
					PlayerY = 5
				Case 3
					PlayerY = 4
				Case 2
					PlayerY = 2
				Case 1
					PlayerY = 1
			End Select
			If PlayerJumpDist = 0
				PlayerY = 0
				PlayerAction = 0
				PlayerFrame = 0
				curFrame = 0
				PlayerDir = 0
			EndIf
		EndIf ;playeraction=8
		If (Not ChannelPlaying(chnlMusic)) And PlayerJumpDist = 0
			If GameLevel = 1
				UnlockMoreLostCaverns()
			EndIf
			GameWon = False
			FlushKeys()
		EndIf
	EndIf
EndIf ;gamewon


Cls

;Draw board
For yiter = (PlayerLevelY+3) To (PlayerLevelY-3) Step -1
	If yiter > 0 And yiter < 31
		YOffset = ((PlayerLevelY - yiter) * -45) + ScreenY
		backgroundimage = Board(PlayerLevelX, yiter)\background_image
		If backgroundimage = 0 Or backgroundimage = 6 Or backgroundimage = 7 Or backgroundimage = 10
			If yiter + 1 < 31
				If Board(PlayerLevelX, yiter + 1)\background_image = 1
					DrawBlockRect Wave, 8, 21 + YOffset, 0, 6 + (curWave * 6), 304, 6
				EndIf
			EndIf
		EndIf
		If backgroundimage <> 0
			If backgroundimage < 6
				DrawImage BgImage(backgroundimage), 8, -20 + YOffset
			Else
				If backgroundimage = 6 Or backgroundimage > 7
					extraYOffset = 0
					If backgroundimage = 10 Then extraYOffset = -1
					If backgroundimage = 9
						If yiter Mod 2 = 0 Then extraYOffset = 1
					EndIf
					DrawImage BgImage(backgroundimage), 8, -12 + YOffset + extraYOffset, Animation1
				EndIf
				If backgroundimage = 7
					DrawImage BgImage(7), 278, -12 + YOffset, Animation1
				EndIf
			EndIf
		EndIf
		If Board(PlayerLevelX, yiter)\graphic <> 0
			DrawImage LevelGraphic(Board(PlayerLevelX, yiter)\graphic), 8, 15 + YOffset
		EndIf
		If Board(PlayerLevelX, yiter)\checkpoint = True
			DrawImage Checkpoint, 70, 15 + YOffset
		EndIf
		If Board(PlayerLevelX, yiter)\left_blocked = 1
			DrawImage Left_block, 8, -11 + YOffset
		EndIf
		If Board(PlayerLevelX, yiter)\left_blocked = 2
			DrawImage Left_block_striped, 8, -11 + YOffset
		EndIf
		If Board(PlayerLevelX, yiter)\right_blocked = 1
			DrawImage Right_block, 280, -11 + YOffset
		EndIf
		If Board(PlayerLevelX, yiter)\right_blocked = 2
			DrawImage Right_block_striped, 280, -11 + YOffset
		EndIf
		If Board(PlayerLevelX, yiter)\ladder = 1
			If GameLevel = 2
				XOffset = -1
				Y2Offset = -3
				width = 2
			Else
				XOffset = 0
