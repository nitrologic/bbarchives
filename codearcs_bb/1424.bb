; ID: 1424
; Author: Jesse B Andersen
; Date: 2005-07-19 18:06:09
; Title: Progress Bars
; Description: Simple Rect Progress Bars

;Jesse Andersen - xmlspy - http://www.xmlspy.tk
;July 19, 2005 : Progress bars
;jesse_andersengt@yahoo.com
;http://www.alldevs.com

Graphics 640, 480, 0, 2
SetBuffer BackBuffer()

b1value# = 25
Bar1 = Create_Bar( 0, 0, GraphicsWidth(), 20 , b1value#)

b2value# = 50
Bar2 = Create_Bar( 0, 21, GraphicsHeight() - 21, 20, b2value#, 0)

b3value# = 75
Bar3 = Create_Bar( GraphicsWidth() - 21, 21, 20, GraphicsHeight() - 21, b3value#, 2)

Bar4 = Create_Bar( 21, GraphicsHeight() - 21, GraphicsWidth() - 43, 20 , b1value#)


timer = CreateTimer(60)
Repeat
	Cls
	
	If KeyDown(16) Then b1value# = b1value# - 1 ; Q
	If KeyDown(17) Then b1value# = b1value# + 1 ; W
	If KeyDown(30) Then b2value# = b2value# - 1 ; A
	If KeyDown(44) Then b2value# = b2value# + 1 ; Z
	If KeyDown(45) Then b3value# = b3value# - 1 ; S
	If KeyDown(31) Then b3value# = b3value# + 1 ; X
	
	
	If ob1value# <> b1value# Then
		b1value# = Update_BarValue( Bar1, b1value# )
		Update_BarValue( Bar4, b1value# )
		ob1value# = b1value#
	EndIf
	
	If ob2value# <> b2value# Then
		b2value# = Update_BarValue( Bar2, b2value# )
		ob2value# = b2value#
	EndIf
	
	If ob3value# <> b3value# Then
		b3value# = Update_BarValue( Bar3, b3value# )
		ob3value# = b3value#
	EndIf
	
	WaitTimer( timer )
	Render_Bar( )
	
	Color 255, 255, 255
	Text 100, 100, "Press QW for horizontal  | Value= " + b1value#
	Text 100, 120, "Press AZ for vertical 1  | Value= " + b2value#
	Text 100, 140, "Press SX for vertical 2  | Value= " + b3value#
	
	Flip(0)

Until KeyHit(1)
End


Type Bar
	Field x#, y#, width#, height#
	Field value#, horizontal
	Field Red, Green, Blue
	Field bRed, bGreen, bBlue
	Field bStyle
End Type

Function Create_Bar( x#, y#, width# = 100, height# = 20, value# = 100, horizontal = 1, Red = 0, Green = 255, Blue = 0, bRed = 255, bGreen = 255, bBlue = 255, bStyle = 0 )
	Bar.Bar = New Bar
	Bar\x# = x#
	Bar\y# = y#
	Bar\width# = width#
	Bar\height# = height#
		If value# <= 0 Then
			value# = 0
		ElseIf value# >= 100 Then
			value# = 100
		EndIf
	Bar\value# = value# : Bar\horizontal = horizontal
	Bar\Red = Red : Bar\Green = Green : Bar\Blue = Blue
	Bar\bRed = bRed : Bar\bGreen = bGreen : Bar\bBlue = bBlue : 
	Bar\bStyle = bStyle
	Return Handle( Bar )
End Function


Function Delete_Bar( ID )
	Bar.Bar = Object.Bar( ID )
	If Bar.Bar <> Null Then
		Delete Bar
	EndIf
End Function

Function Render_Bar( )
	For Bar.Bar = Each Bar
		If Bar\horizontal = 0
			Color Bar\bRed, Bar\bGreen, Bar\bBlue
			Rect Bar\x#, Bar\y#, Bar\height#, Bar\width#, Bar\bStyle
			Color Bar\Red, Bar\Green, Bar\Blue
			Rect Bar\x#+1, Bar\y#+1, Bar\height# - 2, ((Bar\value# * .01) * Bar\width#) - 2
		ElseIf Bar\horizontal = 1
			Color Bar\bRed, Bar\bGreen, Bar\bBlue
			Rect Bar\x#, Bar\y#, Bar\width#, Bar\height#, Bar\bStyle
			Color Bar\Red, Bar\Green, Bar\Blue
			Rect Bar\x#+1, Bar\y#+1, ((Bar\value# * .01) * Bar\width#) - 2, Bar\height# - 2
		ElseIf Bar\horizontal = 2
			Color Bar\bRed, Bar\bGreen, Bar\bBlue
			Rect Bar\x#, Bar\y#, Bar\width#, Bar\height#, Bar\bStyle
			Color Bar\Red, Bar\Green, Bar\Blue
			tTotal1# = Bar\height# + Bar\y# + 1
			val1# = tTotal1 - ((Bar\value# * .01) * Bar\height#)
			val2# = (tTotal1 - val1#) - 2		
			Rect Bar\x#+1, val1#, Bar\width# - 2, val2#
		EndIf
	Next
End Function

Function Update_BarValue( ID, value# )
	Bar.Bar = Object.Bar( ID )
	If Bar.Bar <> Null Then
		If value# <= 0.0 Then value# = 0.0
		If value# >= 100.0 Then value# = 100.0
		Bar\value# = value#
		Return value#
	EndIf
End Function
