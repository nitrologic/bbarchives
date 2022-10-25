; ID: 2692
; Author: SebHoll
; Date: 2010-04-05 11:12:14
; Title: Determine Text Selection Color
; Description: Function call with implementations for Win32/Mac OS X/FLTK Linux.

Strict

' Sample App

Local red:Byte, green:Byte, blue:Byte

GetSelectionColor( red, green, blue )

Print "Selection Color: RGB( " + red + ", " + green + ", " + blue + " )"
End

' Selection Color Function

?Win32
	
	Import Pub.Win32
	
	Extern "win32"
		Function GetSysColor:Int( nIndex:Int )
	EndExtern

?MacOS

	Import "color.m"
	
	Extern "C"
		Function NSGetTextSelectionColor( red:Int Ptr, green:Int Ptr, blue:Int Ptr )
	EndExtern

?Linux

	Import MaxGUI.FLTKMaxGUI
	Import "flcolor.cpp"
	
	Extern "C"
		Function fl_get_color( color )
	EndExtern

?

Function GetSelectionColor( pRed:Byte Var, pGreen:Byte Var, pBlue:Byte Var )
	?Win32
		Local tmpColour:Int = GetSysColor(COLOR_HIGHLIGHT)
		pRed = tmpColour & $FF
		pGreen = (tmpColour Shr 8) & $FF
		pBlue = (tmpColour Shr 16) & $FF
	?MacOs
		Local red, green, blue
		NSGetTextSelectionColor( Varptr red, Varptr green, Varptr blue )
		pRed = red
		pGreen = green
		pBlue = blue
	?Linux
		Local color = fl_get_color( 15 )  'FL_SELECTION_COLOR: 15
		pRed = color Shr 24
		pGreen = (color Shr 16) & $FF
		pBlue = (color Shr 8) & $FF
	?
EndFunction
