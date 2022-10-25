; ID: 2458
; Author: BlitzSupport
; Date: 2009-04-11 14:44:37
; Title: Millisecond timer without integer wrap problems
; Description: Replacement for MilliSecs () that returns a Long value which won't wrap to negative for (I believe) around 220 million years!

SuperStrict ' You can remove this if you don't want it!

' -----------------------------------------------------------------------------
' These globals MUST be included in your code!
' -----------------------------------------------------------------------------

Global GT_Start:Long	' Start of game time
Global GT_Last:Int		' Last INTEGER value of MilliSecs ()
Global GT_Current:Long	' LONG value updated by MilliSecs ()

' -----------------------------------------------------------------------------
' ResetGameTime MUST be called at the start of your game, or at least
' before you first try to use the GameTime function...
' -----------------------------------------------------------------------------

' You can also call this to reset GameTime on reaching a new level, starting
' a new game after the player dies, etc...

Function ResetGameTime:Int ()
	GT_Current	= 0
	GT_Start		= Long (MilliSecs ())
	GT_Last		= GT_Start
End Function

' -----------------------------------------------------------------------------
' Returns milliseconds from when ResetGameTime was called...
' -----------------------------------------------------------------------------

Function GameTime:Long ()

	Local msi:Int = MilliSecs ()

	GT_Current = GT_Current + (msi - GT_Last)
	GT_Last = msi

	Return GT_Current

End Function

' -----------------------------------------------------------------------------
' D E M O . . .
' -----------------------------------------------------------------------------

AppTitle = "Click mouse to reset game time!"

Graphics 640, 480

ResetGameTime ()

Local iticks:Int = MilliSecs ()
Local isecs:Int

Local lticks:Long = GameTime ()
Local lsecs:Long

Repeat

	If MouseHit (1)

		ResetGameTime ()

		iticks = MilliSecs ()
		isecs = 0
		
		lticks = GameTime ()
		lsecs = 0

	EndIf
	
	Cls
	
	If GameTime () => lticks + 1000
		lsecs = lsecs + 1
		lticks = GameTime ()
	EndIf
	
	DrawText "GameTime: " + lsecs + " [" + GameTime () + "]", 20, 20

	If MilliSecs () => iticks + 1000
		isecs = isecs + 1
		iticks = MilliSecs ()
	EndIf
	
	DrawText "MilliSecs: " + isecs + " [" + MilliSecs () + "]", 20, 40

	Flip
	
Until KeyHit (KEY_ESCAPE)

End
