; ID: 2783
; Author: dmaz
; Date: 2010-11-01 19:38:11
; Title: TRL -Fixed rate update logic with Tweening
; Description: Fixed rate update logic with Tweening

SuperStrict
 
Rem
bbdoc: TRL - Fixed rate update logic with Tweening
about:
End Rem
Module dmaz.TRL


ModuleInfo "Version: 1.0"
ModuleInfo "Author: David Maziarka"
ModuleInfo "License: Public Domain 2007 David Maziarka"

ModuleInfo "History: 1.00 Release"


Rem
bbdoc: The TRL type
End Rem
Type TRL
	Field gameFPS			:Double	= 60.0:Double
	Field framePeriod		:Double	= 1000.0:Double / gameFPS
	Field frameTime		:Double	= MilliSecs() - framePeriod

	Field gamelogicframes	:Int
	Field catchUp		:Int = True
	
	Rem
	bbdoc: This goes in the main loop and should be called every graphic frame.
	about:
	2 functions will be called back to, one that capture the old position
	and one that performs the game update logic.
	returns: tween number that should be used for drawing.
	End Rem
	Method Update:Double( captureCallback(), updateCallback() )
		Global frameElapsed:Double
		
		Repeat
			frameElapsed = MilliSecs() - frameTime
		Until frameElapsed
		Local frameTicks:Int = frameElapsed / framePeriod
		Local frameTween:Double = Double(frameElapsed Mod framePeriod) / framePeriod

		' update game and world state
		If Not catchUp And frameTicks > 1 Then frameTicks = 1
		For Local frameLimit:Int = 1 To frameTicks
			If frameLimit = frameTicks
				captureCallback()
			End If
			frameTime = frameTime + framePeriod
		
			updateCallback()
			gamelogicframes = GetGameLogicFrames()
		Next

		Return frameTween
	End Method


	Rem
	bbdoc: Set the rate at which your "update" function will be run.
	about:
	Choose a logic rate at the start of your project, 30 or 60 work the best. For a game with lots of AI I'd go 30.
	End Rem
	Method SetUpdateRate( gameFPS:Double )
		Self.gameFPS = gameFPS
		framePeriod = 1000.0:Double / Self.gameFPS
		'ResetCounters
	End Method 


	
	Rem
	bbdoc: Get the number of logic updates being called per second.
	returns: Logic updates per second. 
	End Rem
	Method GetUPS:Int()
		Return gamelogicframes
	End Method


	Rem
	bbdoc: Get the actual frames being draw per second.
	returns: Frames per second.
	End Rem
	Method GetFPS:Int()
		Global counter:Int
		Global time:Int
		Global framerate:Int
	
	    counter=counter+1
	    If time=0 Then time=MilliSecs()
	    If time+1001<MilliSecs() Then
	        framerate=counter
	           counter=0
	        time=MilliSecs()
	        EndIf
	       
		Return framerate
	End Method 
	

	Method GetGameLogicFrames:Int()
		Global counter:Int
		Global time:Int
		Global framerate:Int
	
	    counter=counter+1
	    If time=0 Then time=MilliSecs()
	    If time+1001<MilliSecs() Then
	        framerate=counter
	           counter=0
	        time=MilliSecs()
	        EndIf
	       
		Return framerate
	End Method 	


	Rem
	bbdoc: Reset time counters.
	End Rem
	Method ResetCounters()
		framePeriod	= 1000 / gameFPS
		frameTime	= MilliSecs() - framePeriod
	End Method


	Rem
	bbdoc: Get the number inbetween the old and the new value.
	returns: The interpolated number.
	End Rem
	Method GetTween:Double( oldValue:Double, value:Double, tween:Double )
		Return oldValue + (value-oldValue)*tween 
	End Method

End Type
