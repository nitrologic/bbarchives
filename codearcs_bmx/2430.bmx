; ID: 2430
; Author: Festay
; Date: 2009-03-08 16:36:26
; Title: OOP Blitz3dSDK framework
; Description: OOP Blitz3D Game Framework

' *******************************************************************
' Blitz3D SDK framework
' -------------------------------------------------------------------
' An object oriented framework for creating Blitz3dSDK apps in
' Blitzmax.
' -------------------------------------------------------------------
' Author: Ben Gale 2009
' Written in Blitzmax using the Blitz3D SDK.
' -------------------------------------------------------------------
' Credit to: Mark sibly for the Blitz3D render tweening code (Even 
'            though I dont fully understand it - yet) 
' Credit to: Rob Hutchinson for the framework / component idea (See
'            his Digesteroids code for further information)
' Thanks to: The Blitz community.            
' *******************************************************************
SuperStrict

Rem
bbdoc: Import required modules and files.
End Rem
Framework blitz3d.blitz3dsdk

Import brl.random

?debug
bbSetBlitz3DDebugMode(True)
?Not debug
bbSetBlitz3DDebugMode(False)
?

Rem
bbdoc: Represents a Blitz3D application.
about: Provides methods and properties to manage a Blitz3D application such as initialising
       the runtime environment and running the main application loop.
End Rem
Type TBlitz3DApplication

	Field FramesPerSecond:Int = 30			' The maximum desired frames per second.
	Field IsActive:Int = False				' Is the application main loop running?
	Field WindowMode:Int = GFX_DEFAULT		' The window mode - default = fullscreen in release builds & windowed in debug build.
	Field ActiveComponent:TComponent = Null	' The current active component.

	'#Region Default constructor / destructor

	Rem
	bbdoc: Default Constructor: Initialises the Blitz3D runtime environment.
	about: Throws a runtime error if the runtime fails to initialise.
	Endrem
	Method New()
		If bbBeginBlitz3D() = False Then
			Throw "Failed to initialise Blitz3D runtime environment. Ensure that b3d.dll is correctly installed"
		End If
		DebugLog("Blitz3D runtime initialised.")
	End Method
	
	Rem
	bbdoc: Default destructor: Closes the BLitz3D runtime environment and frees
	       up any resources that are being used.
	End Rem
	Method Delete()
		bbEndBlitz3D()
		DebugLog("Blitz3D runtime disposed")
	End Method
	
	'#End Region
	
	Rem
	bbdoc: Sets the Blitz3D graphics mode.
	about: Pass a valid TDisplayMode object to initialise 3D graphics mode.
	       A runtime error will be thrown if the display mode specified is not 3D capable.
	End Rem
	Method Set3DGraphicsMode(d:TB3DDisplayMode)
		If bbGfxMode3DExists(d.Width, d.Height, d.ColourDepth) = False Then
			Throw "The graphics mode " + d.Width + "x" + d.Height + "x" + d.ColourDepth + " is not 3D capable on this system."
		Else
			bbGraphics3D(d.Width, d.Height, d.ColourDepth, Self.WindowMode)
			DebugLog("Initialised 3D graphics mode " + d.Width + "x" + d.Height + "x" + d.colourDepth)
		End If
	End Method
	
	Rem
	bbdoc: Begins running the current Blitz3D application using the component specified.
	End Rem
	Method Run(c:TComponent)
	
		' Render tweenig variables.
		Local period:Int
		Local frameTime:Int
		Local Ticks:Int
		Local i:Int
		Local Remaining:Int
		Local StartTime:Int
		Local Elapsed:Int
		Local Tween:Float
	
		' Ensure a valid component has been passed.
		If c = Null Then Throw "A component needs to be registered before a Blitz3D application can run."
		
		' Initialise the component and prepare the application to run the main loop.
		Self.RegisterComponent(c)
		Self.IsActive = True
		
		period = 1000 / Self.FramesPerSecond
		frameTime = MilliSecs() - period
		
		DebugLog("Period: " + period)
		DebugLog("Frametime: " + FrameTime)
		
		' Main loop.
		While Self.IsActive
			
			StartTime = MilliSecs()
		
			Repeat
				Elapsed = MilliSecs() - FrameTime
				DebugLog("elapsed: " + elapsed)
			Until Elapsed
			
			Ticks = Elapsed / Period
			Tween = Float(Elapsed Mod(Period)) / Float(Period)
			
			DebugLog("Ticks: " + Ticks)
			DebugLog("Tween: " + Tween)
			
			For i = 1 To Ticks
				FrameTime = FrameTime + Period
				DebugLog("FrameTime" + FrameTime)
				If i = Ticks Then
					Self.ActiveComponent.UpdateWithoutTweening()
					bbCaptureWorld()
					DebugLog("World Captured!")
				End If
				Self.ActiveComponent.Update()
				bbUpdateWorld()
				DebugLog("World Updated")
			Next
			
			bbRenderWorld(Tween)
			DebugLog("World Rendered")
	
			Remaining = Period - (MilliSecs() - StartTime)
			DebugLog("Remaining: " + remaining)
			If Remaining > 1 Then
				Delay (Remaining - 1)
			End If
			
			Self.ActiveComponent.Render2D()
			bbFlip()
			DebugLog("2D rendering complete")
		
		Wend
		
		' The application is no longer running so dispose the
		' current active component.
		Self.ActiveComponent.Dispose()
	
	End Method
	
	Rem
	bbdoc: Registers a component with the application.
	about: When a component is registered the following occurs:
	       1) If a component is already in use it is disposed.
		   2) The new component is made the current component of the application.
		   3) The application is registered with the component (So that the component
		      has access to the applications properties and methods).
		   4) The new component is initialised.  
	End Rem
	Method RegisterComponent(c:TComponent)
		If Self.ActiveComponent <> Null Then
			Self.ActiveComponent.Dispose()
		End If
		Self.ActiveComponent = c
		Self.ActiveComponent.Application = Self
		Self.ActiveComponent.Initialise()
	End Method
	
	Rem
	bbdoc: Sets the Blitz3D application title.
	End Rem
	Method SetTitle(str:String)
		bbSetBlitz3DTitle str, ""
		DebugLog("Application title set")
	End Method
	
End Type

Rem
bbdoc: Provides a modular interface for Blitz3D application components.
End Rem
Type TComponent Abstract

	Rem
	bbdoc: Reference to the Blitz3D application that owns this component.
	End Rem
	Field Application:TBlitz3DApplication = Null
	
	Method Initialise() Abstract			' Initialise the component.
	Method UpdateWithoutTweening() Abstract	' Update entities that do NOT need to be tweened.
	Method Update() Abstract				' Use to update a component.
	Method Render2D() Abstract				' Use this method for rendering any 2-dimensional graphics.
	Method Dispose() Abstract				' Clean up any resources used by the component.
	
End Type

Rem
bbdoc: Represents a blitz3D graphics display mode.
End Rem
Type TB3DDisplayMode

	Field Width:Int
	Field Height:Int
	Field ColourDepth:Int
	
	Rem
	bbdoc: Creates a new display mode using the specified parameters.
	about: If no parameters are supplied then a default graphics display of
	       640x480 pixels with a colour depth of 16-bits is created.
	End Rem
	Function Create:TB3DDisplayMode(w:Int = 640, h:Int = 480, d:Int = 16)
		Local out:TB3DDisplayMode = New TB3DDisplayMode
		out.Width = w
		out.Height = h
		out.ColourDepth = d
		Return out
	End Function
	
End Type

' Create a new Blitz3D application object.
Local App:TBlitz3DApplication = New TBlitz3DApplication

' Set some application properties and start the application running.
App.WindowMode = GFX_DEFAULT
App.Set3DGraphicsMode(TB3DDisplayMode.Create(800,600, 32))
App.SetTitle "Test Blitz3D Framework Application"
App.Run(New TCubeComponent)

' Test component for development purposes only.
' Renders two cubes & some text to the screen.
Type TCubeComponent Extends TComponent

	Field cam:Int
	Field cube:Int
	Field Light:Int
	Field Cube2:Int
	Field Counter:Int = 0

	Method initialise()
		cam = bbCreateCamera()
		cube = bbCreateCube()
		cube2 = bbCreateCube()
		light = bbCreateLight()
		bbPositionEntity(cube, 0, - 1, 5)
		bbEntityColor(cube, 123, 67, 43)
		bbEntityColor(cube2, 67, 223, 156)
		bbEntityShininess(cube, 1)
		bbEntityShininess(cube2,0.5)
		DebugLog("Camera, cubes and light setup ok")
	End Method
	
	Method UpdateWithoutTweening()
		If counter > 100 Then
			bbPositionEntity(cube2, Rand(- 7, 7), 5, 10)
			DebugLog("Cube 2 Moved")
			counter = 0
		End If
	End Method
	
	Method Update()
		If bbKeyHit(1) Then Application.IsActive = False
		bbTurnEntity(cube, 0.1, 0.2, 0.3)
		counter = counter + 1
	End Method
	
	Method Render2D()
		bbText(0, 0, "Cube Rendering component")
	End Method
	
	Method Dispose()
		bbFreeEntity(cube)
		bbFreeEntity(cube2)
		bbFreeEntity(light)
		bbFreeEntity(cam)
		Self.Application = Null
		DebugLog("Cube component disposed")
	End Method

End Type
