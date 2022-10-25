; ID: 1941
; Author: Pantheon
; Date: 2007-03-07 22:53:25
; Title: ASynchronous Input Function
; Description: Get keyboard input without stalling your program

'/**
' * ASYNCHRONOUS INPUT USING POLLED INPUT
' * 
' *   this code will change the input string based
' *   on the current keyboard events, and is usefull
' *   for non blocking input
' *   return codes to handle special events
' *
' *	  these must be larger than a byte	  
' *
Const IAS_Normal	= $AAAAAAAA	'we're sweet
Const IAS_Return	= $BBBBBBBB	'carrage return hit
' *
' *   if anything else was returned then its the
' *   ASCII value of the unknown key press
' *
' *	  tab is at ascii <9>
' *   escape is at ascii <27>
' *
Function InputASync:Int( aStr:String Var )
	Local a:Int = GetChar( )
	' loop hack :)
	Repeat
		' * space numbers symbols letters
		If (a=>32 And a<=126) Then Exit
		' * pound symbol
		If (a=163) Then Exit
		' * carrage return
		If (a=13) Then Return IAS_Return
		' * backspace key
		If (a=8)
			Local b:Int = Len( aStr )
			If (b>0) Then aStr = Left( aStr, b-1 )
			Return IAS_Normal
		EndIf
		' * no input entered
		If (a=0) Then Return IAS_Normal
		' something else cought
		Return a
	Forever
	aStr = aStr+Chr( a )
	Return IAS_Normal
End Function
' *
' */


'/**
' * EXAMPLE CODE
' *
' *		just to show why its a bit different ;)
' */
Local myString:String = "type 'exit' to close!"
Local cursor:String = ""
Local time = 0
Graphics 320, 240, 0
Repeat
	Cls
	
	Local ret:Int = InputASync( myString )
	
	' what did we get back
	Select( ret )
	Case IAS_Normal
		' hack to warp between 1 and 360 for the sin wave
		time :+ ( 1- (time > 360) *360 )
		
	Case IAS_Return
		' what have we got
		If ( Lower( myString ) = "exit" ) Then Exit
		' no? well just erase
		myString = ""
		
	Default
		' found an undisplayable ascii value
		myString = "ASCII value <"+ret+"> not handled!"
		
		' is it the escape key
		If (ret = 27) Then Exit
		
	End Select
	
	' make a blinking cursor
	If ((time Mod 40) < 20)
		cursor = "_"
	Else
		cursor = ""
	EndIf
	
	' draw our string plus cursor
	DrawText myString+cursor, 10, 40+Sin( time )*20
	
	Flip
Forever
