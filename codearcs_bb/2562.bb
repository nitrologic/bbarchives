; ID: 2562
; Author: Festay
; Date: 2009-08-15 17:30:24
; Title: Blitz3D Game Framework
; Description: Game framework to help making Blitz3D games.

Type TGame

	Field Timer.TGameTime
	Field FramesPerSecond%
	Field AnimationSpeed#
	Field Components.TGameComponent
	Field IsRunning%
	Field GraphicsMode.TGraphicsMode 
	Field WindowMode%
	
End Type

	Const GFXMODE_FULLSCREEN = 1
	Const GFXMODE_WINDOWED = 2

	Function CreateGame.TGame(gfx.TGraphicsMode,title$,windowMode=GFXMODE_WINDOWED,FPS%=60)
	
		AppTitle title
		HidePointer()
		
		Graphics3D(gfx\Width,gfx\Height,gfx\Depth,windowMode)
		SetBuffer(BackBuffer())
		
		Local G.TGame = New TGame
			G\GraphicsMode = gfx
			G\WindowMode = windowMode
			G\FramesPerSecond = FPS
			G\AnimationSpeed = 1
			G\Timer = CreateGameTime(FPS)
			Return G
			
	End Function

	Function RunGame(game.TGame)
	
		Local i
		
		; Initialise game components --------------------------------------
		For game\Components = Each TGameComponent 
			CallFunction(game\Components\Initialise) 
		Next
		
		; Start the main game loop ----------------------------------------
		game\IsRunning = True
		
		While game\IsRunning = True
		
			UpdateGameTime(game\Timer)
			
			For i=1 To game\Timer\Ticks
				UpdateFrameTime(game\Timer)
				If i=game\Timer\Ticks Then
					CaptureWorld()
				End If
				For game\Components = Each TGameComponent 
					CallFunction(game\Components\Update)
				Next
				UpdateWorld(game\AnimationSpeed)
			Next
			RenderWorld(game\Timer\Tween)
			
			UpdateRemainingTime(game\Timer)
			If game\Timer\RemainingTime > 1 Then 
				Delay (game\Timer\RemainingTime-1)
			End If
			
			For game\Components = Each TGameComponent
				CallFunction(game\Components\Draw)
			Next
			
			VWait
			Flip(False)
			
		Wend
		
		; Dispose game components ----------------------------------------
		For game\Components = Each TGameComponent
			CallFunction(game\Components\Dispose)
		Next
		
	End Function

	Function DisposeGame(game.TGame)
		Delete(game\Timer)
		For game\Components = Each TGameComponent 
			Delete game\Components 
		Next	
		Delete(game)
		ClearWorld()
		End
	End Function

;-------------------------------------------------------------------------

Type TGameTime

	Field Period%
	Field FrameTime%
	Field StartTime%
	Field ElapsedTime%
	Field Ticks%
	Field Tween#
	Field RemainingTime%
	
End Type

	Function CreateGameTime.TGameTime(FPS%=60)
		Local t.TGameTime = New TGameTime 
			t\Period = 1000 / FPS 
			t\FrameTime = MilliSecs() - t\Period 
			Return t
	End Function

	Function UpdateGameTime(GT.TGameTime)
		GT\StartTime = MilliSecs()
		GT\ElapsedTime = MilliSecs() - GT\FrameTime
		GT\Ticks = (GT\ElapsedTime) / (GT\Period)
		GT\Tween = Float(GT\ElapsedTime Mod GT\Period) / Float(GT\Period)
	End Function

	Function UpdateFrameTime(GT.TGameTime)
		GT\FrameTime = GT\FrameTime + GT\Period
	End Function

	Function UpdateRemainingTime(GT.TGameTime)
		GT\RemainingTime = GT\Period - (MilliSecs() - GT\StartTime)
	End Function

;-------------------------------------------------------------------------
	
Type TGameComponent
	Field Initialise
	Field Update
	Field Draw
	Field Dispose
End Type

;-------------------------------------------------------------------------

Type TGraphicsMode
	Field Width
	Field Height
	Field Depth
End Type

;-------------------------------------------------------------------------
; Main program

Const KEY_ESCAPE = 1
Const KEY_ENTER = 28

Global gfxMode.TGraphicsMode = New TGraphicsMode 
	gfxMode\Width = 800
	gfxMode\Height = 600
	gfxMode\Depth = 32

Global ExampleGame.TGame = CreateGame(gfxMode,"Game Framework Demo")

Global cam = CreateCamera()
Global Cube = CreateCube()
Global Light = CreateLight()

CreateSpinningCubeComponent(ExampleGame)
CreatecolorChangerComponent(ExampleGame)
RunGame(ExampleGame)
DisposeGame(ExampleGame)

;-------------------------------------------------------------------------
; Spinning cube component
Function CreateSpinningCubeComponent(g.TGame)
	
	g\Components = New TGameComponent
	
	g\Components\Initialise = FunctionPointer()
		Goto skipInitialise
		initialiseSpinningCube()
		.skipInitialise
	
	g\Components\Update = FunctionPointer()
		Goto skipUpdate
		UpdateSpinningCube()
		.skipUpdate
	
	g\Components\Draw = FunctionPointer()
		Goto skipDraw
		DrawSpinningCube()
		.skipDraw
		
	g\Components\Dispose = FunctionPointer()
		Goto SkipDispose
		DisposeSpinningCube()
		.SkipDispose
	
End Function

Function initialiseSpinningCube()
	CameraClsColor(cam,100,149,237)
	PositionEntity(Cube,0,0,5)
End Function

Function UpdateSpinningCube()
	If KeyHit(KEY_ESCAPE) Then ExampleGame\IsRunning = False
	TurnEntity(Cube,0.1,0.2,0.3)
End Function

Function DrawSpinningCube()
	Text(10,10,"Cube Spinner Component Loaded.")
End Function

Function DisposeSpinningCube()
	FreeEntity(Cube)
	FreeEntity(Light)
End Function

;-------------------------------------------------------------------------
; Color changer component.
Function CreatecolorChangerComponent(g.TGame)
	
	Local C.TGameComponent = New TGameComponent 
	
	C\Initialise = FunctionPointer()
		Goto skipInitialise 
		initialiseColorChanger()
		.skipInitialise
	
	C\Update = FunctionPointer()
		Goto skipUpdate
		UpdateColorChanger()
		.skipUpdate
	
	C\Draw = FunctionPointer()
		Goto skipDraw
		DrawColorChanger()
		.skipDraw
		
	g\Components = C	
	
End Function

Function initialiseColorChanger()
	SeedRnd(MilliSecs())
	EntityColor(Cube,Rand(0,255),Rand(0,255),Rand(0,255))
End Function

Function UpdateColorChanger()
	If KeyHit(KEY_ENTER) EntityColor(Cube,Rand(0,255),Rand(0,255),Rand(0,255))
End Function

Function DrawColorChanger()
	Text(10,20,"Cube color changer component loaded")
End Function
