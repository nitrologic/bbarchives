; ID: 2460
; Author: ziggy
; Date: 2009-04-12 14:15:47
; Title: Simple delta timing and jitter correction module
; Description: A module to handle automatically delta timing on games

Import brl.hook
Import brl.basic
Import brl.Graphics
rem
	bbdoc: Automatic Delta Timing Module.
end rem
Module blide.deltatiming
ModuleInfo "Author: Manel Ibáñez"
ModuleInfo "Version: 0.0.1 - First Release!"

Strict

Const DefaultMinimumTimeFactor:Double = 0.25
Const DefaultMaximumTimeFactor:Double = 5

Private
	Global Handler:Int = AddHook(FlipHook, DeltaHook)
	Global Paused:Int = False
	
	Global DeltaTime:Double
	Global LastTiming:Double = MilliSecs()
	Global MinimumTimeFactor:Double = DefaultMinimumTimeFactor
	Global MaximumTimeFactor:Double = DefaultMaximumTimeFactor
	
	Global LastMillisecs:Int = Millisecs()
	Global Counter:Int = 0
	
	Global FPS:Int = 0'MilliSecs()
	
	Global slow:Int = False
	Function DeltaHook:Object(id, data:Object, context:Object)
		If Not Paused UpdateTiming()
	End Function
	
	Function UpdateTiming()
		Local MLS:Double = MilliSecs()
		DeltaTime = Abs(MLS - LastTiming) / 10.0
		If DeltaTime < MinimumTimeFactor Then
			While DeltaTime < MinimumTimeFactor
				Delay(1)
				MLS = MilliSecs()
				DeltaTime = Abs(MLS - LastTiming) / 10.0
				Print DeltaTime + ", " + MLS
			Wend
		End If
		LastTiming = MLS
		If DeltaTime > MaximumTimeFactor Then
			DeltaTime = MaximumTimeFactor
			slow = True
		Else
			slow = False
		EndIf
		Counter:+1
		If Abs(LastMillisecs - MLS) > 1000 Then
			FPS = Counter
			Counter = 0
			LastMillisecs = MLS
		End If
	End Function
Public

rem
	bbdoc:This shared class provides an automated delta-timing factor to be used in games.
	about: this is a shared class, so there's no need to instantiate it, just use its functions.
end rem
Type Delta Abstract
	rem
		bbdoc: This function returns the current delta-timing factor.
	end rem
	Function Factor:Double()
		Return DeltaTime
	End Function
	rem
		bbdoc: This function sets the minimum time factor expected.
		about: so if the computer is too fast, the engine automatically adds the needed sleep calls, to avoid a too small delta factor, that could introduce some maths precision issues.<br>
			   Usually a minimum of 0.5 millisec of time per frame is good for calculations.
			   This has a pretended impact on FPS, making them more stable. After setting this, you should not be getting framerates superior to 100/Factor
	end rem
	Function SetMinimumTimeFactor(Factor:Double = DefaultMinimumTimeFactor)
		MinimumTimeFactor = Factor
	End Function
	rem
		bbdoc: This function returns the minimum time factor. Usually 1 millisec per frame is good for calculations. smalle values can introduce visualization glitches.
	end rem
	Function GetMinimumTimeFactor:Double()
		Return MinimumTimeFactor
	End Function
	rem
		bbdoc: This function sets the maximum time factor expected.
		about: This is important becouse if the computer is too slow, we avoid too many frames to be dropped, at a cost of the game being shown a bit slower.<br>Otherwise the game could drop too many frames, affecting calculations.
	end rem
	Function SetMaximumTimeFactor(Factor:Double = DefaultMaximumTimeFactor)
		MaximumTimeFactor = Factor
	End Function
	rem
		bbdoc: This function gets the maximum time factor expected
		about:This is important becouse if the computer too slow, the engine mimics the computer is faster, to avoid getting dropping too many frames, and having too big values on calculation. The game will then be slowed to prevent errors on calculations.
	end rem
	Function GetMaximumTimeFactor:Double()
		Return MaximumTimeFactor
	End Function
	rem
		bbdoc: This function returns TRUE if the game is being slowed due bad hardware, to prevent bad calculations.
	end rem
	Function IsComputerSlow:Int()
		Return slow
	End Function
	rem
		bbdoc: This function will return the curret frames per second.
	end rem
	Function GetFPS:Int()
		Return FPS
	End Function
	
	rem
		bbdoc: Pauses the automatic DeltaTiming engine
	end rem
	Function PauseDeltaTiming()
			Paused = True
	End Function
	
	rem
		bbdoc: Resumes the automatic DeltaTiming engine
	end rem
	Function ResumeDeltaTiming()
		If Paused = True Then
			Self.FlushTiming
			Paused = False
		End If
	End Function
	
	rem
		bbdoc: Resets the delta timing engine. Usefull after a 
	end rem
	Function FlushTiming()
			LastTiming = MilliSecs()
			LastMillisecs:Int = MilliSecs()
			Counter = 0		
	End Function
	
	Function IsPaused:Int()
		Return Paused
	End Function
	
	Function DebugString:String()
		Local Ret:String
		Ret = "Current delta time factor: " + DeltaTime + "~n"
		Ret:+"Current FPS: " + FPS + "~n"
		If Self.IsPaused() Then
			Ret:+"Delta time paused.~n"
		Else
			Ret:+"Delta time running.~n"
		EndIf
		Ret:+"Minimum time factor: " + MinimumTimeFactor + "~n"
		Ret:+"Maximum time factor: " + MaximumTimeFactor + "~n"
		If slow Then
			Ret:+"Application running slowly: TRUE~n"
		Else
			Ret:+"Application running slowly: FALSE~n"
		EndIf
		Return ret
	End Function
End Type
