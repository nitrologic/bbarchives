; ID: 3099
; Author: zoqfotpik
; Date: 2014-01-24 11:56:34
; Title: Quick and Dirty Graphic Console
; Description: Self-linefeeding drawtext

Global currenttextline:Int = 0
Graphics 640,480
While Not KeyDown(KEY_ESCAPE)
	Cls
	currenttextline = 0
	consoleprint "Just a quick and dirty console"
	consoleprint "This is in case you don't like using the slow-ass blitzmax output"
	consoleprint "console for text output when writing tools or whatever."
	consoleprint "Don't expect this to solve world hunger."	
	consoleprint ""
	consoleprint "Drawtext is slow."
	consoleprint "If you really wanted to you could speed this up massively"
	consoleprint "by using masking and grabimage to memoize your console into a bitmap"
	consoleprint "every time the text you are displaying changes."
	consoleprint "and then every frame draw one bitmap as opposed to x number of characters."
	consoleprint "If I remember right that's the way the Quake console did it..."

	Flip
Wend

Function consoleprint(toprint$)
	currenttextline:+12
	DrawText(toprint$,10,currenttextline)
End Function
