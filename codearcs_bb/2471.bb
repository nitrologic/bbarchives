; ID: 2471
; Author: Adam Novagen
; Date: 2009-05-06 15:14:39
; Title: PickColor()
; Description: (Almost) entirely self-contained in a single function, it's a lot like BlitPlus' RequestColor() command... Only nicer!

;!These three globals MUST be included, or PickColor() won't work.
Global PickedRed,PickedGreen,PickedBlue


;Now let's set up a nice, easy-to-understand demo.
AppTitle "PickColor(): a nice, easy-to-understand demo.","Are you sure you want to quit this nice, easy-to-understand demo??"


Graphics 1024,768,0,2


ClsColor 128,128,128


Global TestImage = CreateImage(GraphicsWidth() - 200,GraphicsHeight() - 200)
SetBuffer ImageBuffer(TestImage)
Rect 0,0,GraphicsWidth() - 200,GraphicsHeight() - 200

Global PaintRed,PaintGreen,PaintBlue


Global Font = LoadFont("Arial",24,True)
SetFont Font


SetBuffer BackBuffer()


MoveMouse GraphicsWidth() / 2,GraphicsHeight() / 2
HidePointer;We don't want the Windows cursor getting in our way, do we?


While Not KeyHit(1)


Cls


DrawBlock TestImage,100,100


Color 0,0,0
Text (GraphicsWidth() / 2) + 1,26,"Use the mouse to draw, Spacebar to clear the canvas, Enter to call PickColor().",True
Color 255,255,255
Text GraphicsWidth() / 2,25,"Use the mouse to draw, Spacebar to clear the canvas, Enter to call PickColor().",True


If MouseDown(1)
	SetBuffer ImageBuffer(TestImage)
	Color     PaintRed,PaintGreen,PaintBlue
	Oval      MouseX() - 110,MouseY() - 110,21,21
	SetBuffer BackBuffer()
EndIf

If KeyHit(57);Space
	SetBuffer ImageBuffer(TestImage)
	Color     255,255,255
	Rect      0,0,GraphicsWidth() - 200,GraphicsHeight() - 200
	SetBuffer BackBuffer()
EndIf

If KeyHit(28);Enter
	Color 192,192,192;Typical, boring Windows grey
	PickColor PaintRed,PaintGreen,PaintBlue
	PaintRed   = PickedRed
	PaintGreen = PickedGreen
	PaintBlue  = PickedBlue
	
	;If you're using a different font, you'll need to reset it after you call PickColor().
	SetFont Font
EndIf


;Draw a makeshift cursor
Color 255,255,255
Rect MouseX() - 20,MouseY() - 1,20,3
Rect MouseX() - 1,MouseY() - 20,3,20
Rect MouseX() + 1,MouseY() - 1,20,3
Rect MouseX() - 1,MouseY() + 1,3,20
Color 0,0,0
Line MouseX() - 19,MouseY(),MouseX() - 2,MouseY()
Line MouseX(),MouseY() - 19,MouseX(),MouseY() - 2
Line MouseX() + 19,MouseY(),MouseX() + 2,MouseY()
Line MouseX(),MouseY() + 19,MouseX(),MouseY() + 2


Flip


Wend


End


Function PickColor(DefaultRed = 255,DefaultGreen = 255,DefaultBlue = 255)


;Record the current buffer, so it can be re-activated later
Local CurrentBuffer = GraphicsBuffer()


;This allows the programmer to change the color of the GUI elements.
Local PickColor_GUIRed   = ColorRed()
Local PickColor_GUIGreen = ColorGreen()
Local PickColor_GUIBlue  = ColorBlue()


;Set up the "background", which is basically a screenshot of the program.
Local CurrentScreen = CreateImage(GraphicsWidth(),GraphicsHeight())
CopyRect 0,0,GraphicsWidth(),GraphicsHeight(),0,0,FrontBuffer(),ImageBuffer(CurrentScreen)


;Most of the drawing commands are based around the center of the screen, hence these two variables
Local PickColor_CenterX = GraphicsWidth() / 2
Local PickColor_CenterY = GraphicsHeight() / 2


;Create a makeshift cursor image
Local PickColor_Cursor = CreateImage(12,20)
SetBuffer ImageBuffer(PickColor_Cursor)
;Draw the outline...
Color 0,0,10
Line 0,0,0,15
Line 1,0,11,10
Line 1,14,3,12
Line 4,11,7,18
Line 7,11,10,18
Line 8,19,9,19
Line 7,10,10,10
;... And fill it in.
Color 255,255,255
Line 1,1,1,13
Line 2,2,2,12
Line 3,3,3,11
Line 4,4,4,10
Line 5,5,5,12
Line 6,6,6,14
Line 7,7,7,9
Line 7,13,7,16
Line 8,8,8,9
Line 8,15,8,18
Plot 9,9
Line 9,17,9,18


;Create a slider image, for quick, efficient reusability.
Local PickColor_Slider = CreateImage(17,9)
SetBuffer ImageBuffer(PickColor_Slider)
Color PickColor_GUIRed,PickColor_GUIGreen,PickColor_GUIBlue
Rect 0,0,17,9
;Color 0,0,0
;Rect 7,3,3,3
Color 255,255,255
Line 0,0,16,0
Line 0,0,0,8
Line 6,6,10,6
Line 10,2,10,6
Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
Line 6,2,6,6
Line 6,2,10,2
Line 0,8,16,8
Line 16,0,16,8
Color 0,0,10
Line 1,4,5,4
Line 11,4,15,4

MidHandle PickColor_Slider


;This is used to show various pickable colors
Local PickColor_ColorPanel = CreateImage(256,256)

;These are used solely to draw the color panel's gradient
Local PickColor_RedI
Local PickColor_GreenI


;This holds the timers for the six R+/R-/G+/G-/B+/B- buttons. This is so that they work like Windows keys do,
;if the user click-and-holds: react once, pause, then react continuously, giving that Boop...Boooooop effect.
;Er... Yeah. Just like that. Boop...Boooooop... Heh... Um... Yeah... I'm gonna shut up now.
Local PickColor_ButtonRepeatTimer[5]


;These are the color values that will ultimately be used.
Local PickColor_Red   = DefaultRed
Local PickColor_Green = DefaultGreen
Local PickColor_Blue  = DefaultBlue


;This is the default font used by Blitz.
Local PickColor_Font = LoadFont("Courier",14)
SetFont PickColor_Font


SetBuffer BackBuffer()


;This is used to keep track of click-and-hold stuff, like on the sliders. Basically, if you click and hold on
;something, like the Red slider, then as long as you hold the mouse button down, it doesn't matter where you
;move the cursor, it'll still only affect the Red slider.
Local PickColor_GUIGrab


Local PickColor_Help$[3];PickColor() features a four-line help system, stored in this variable


Local PickColor_Continue = True
Local PickColor_MZS;MZS = Mouse Z Speed, used for tracking the mouse wheel


While PickColor_Continue


If KeyHit(1) Then PickColor_Continue = False


If Not MouseDown(1) Then PickColor_GUIGrab = False


PickColor_MZS = MouseZSpeed()


DrawBlock CurrentScreen,0,0


;Draw the "window" of the color picker - basic, tedious primitives stuff
;Main body
Color PickColor_GUIRed,PickColor_GUIGreen,PickColor_GUIBlue
Rect PickColor_CenterX - 200,PickColor_CenterY - 200,400,400
;Highlights
Color 255,255,255
Line PickColor_CenterX - 200,PickColor_CenterY - 200,PickColor_CenterX + 199,PickColor_CenterY - 200
Line PickColor_CenterX - 200,PickColor_CenterY - 200,PickColor_CenterX - 200,PickColor_CenterY + 199
;Shading
Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
Line PickColor_CenterX + 199,PickColor_CenterY - 199,PickColor_CenterX + 199,PickColor_CenterY + 199
Line PickColor_CenterX - 199,PickColor_CenterY + 199,PickColor_CenterX + 199,PickColor_CenterY + 199
Color 0,0,0
Line PickColor_CenterX + 200,PickColor_CenterY - 200,PickColor_CenterX + 200,PickColor_CenterY + 200
Line PickColor_CenterX - 200,PickColor_CenterY + 200,PickColor_CenterX + 200,PickColor_CenterY + 200


;The red and green values are shown as flat X and Y coordinates, but the "invisible" Z coordinate is used
;for blue. Rolling the mouse wheel changes it, as does pressing the "+" and "-" keys.
PickColor_Blue = PickColor_Blue + (PickColor_MZS * Abs(PickColor_MZS))
;You may be wondering why the formula (PickColorMZS * Abs(PickColor_MZS) is used, instead of just calling
;MouseZSpeed(). This is done so that the blue value doesn't move irritatingly slowly, but also doesn't move
;too fast. By recording MouseZSpeed() and multiplying it by itself (positively, hence the use of Abs()), the
;following results are achieved, depending on MouseZSpeed():
;1 * 1 = 1
;2 * 2 = 4
;3 * 3 = 9
;4 * 4 = 16
;And so on. This way, a slow, single-click scroll of the mouse wheel changes the blue level by only one
;degree, while fast, multi-click scrolls can zip along. The same principle holds in reverse, if MouseZSpeed()
;is less than zero.

;These scancodes cover the "+" and "-" keys on both the main keyboard and the number pad.
If KeyDown(13) Or KeyDown(78);+
	PickColor_Blue = PickColor_Blue + 1
ElseIf KeyDown(12) Or KeyDown(74);-
	PickColor_Blue = PickColor_Blue - 1
EndIf

;Keep the blue within range
If PickColor_Blue < 0 Then PickColor_Blue = 0 ElseIf PickColor_Blue > 255 Then PickColor_Blue = 255


;If the mouse is clicked over the color chart, set the colors accordingly
If MouseX() => PickColor_CenterX - 190 And MouseX() < PickColor_CenterX - 190 + 256 And MouseY() => PickColor_CenterY - 190 And MouseY() < PickColor_CenterY - 190 + 256 And PickColor_GUIGrab = False
	If MouseDown(1) Then PickColor_GUIGrab = 4
	;Since the color chart's bounding box is already being checked, might as well update the help text...
	PickColor_Help[0] = "Click anywhere on the chart to select that color."
	PickColor_Help[1] = ""
	PickColor_Help[2] = "TIP: You can quickly change the blue level using"
	PickColor_Help[3] = "the mouse wheel or the +/- keys."
Else;... Otherwise, just reset it
	PickColor_Help[0] = "Color Picker"
	PickColor_Help[1] = ""
	PickColor_Help[2] = "Designed by Adam Novagen, May 2009"
	PickColor_Help[3] = ""
EndIf

If PickColor_GUIGrab = 4
	If MouseDown(1)
		PickColor_Red   = MouseX() - (PickColor_CenterX - 190)
		PickColor_Green = MouseY() - (PickColor_CenterY - 190)
	EndIf
	
	If PickColor_Red < 0 Then PickColor_Red = 0 ElseIf PickColor_Red > 255 Then PickColor_Red = 255
	If PickColor_Green < 0 Then PickColor_Green = 0 ElseIf PickColor_Green > 255 Then PickColor_Green = 255
	
	PickColor_Help[0] = "Click anywhere on the chart to select that color."
	PickColor_Help[1] = ""
	PickColor_Help[2] = "TIP: You can quickly change the blue level using"
	PickColor_Help[3] = "the mouse wheel or the +/- keys."
EndIf


;Now for the fun part: drawing the color gradient to the color panel.
;Red 0-255 is X 0-255, green 0-255 is Y 0-255, and blue 0-255 is the invisible Z 0-255.
;WritePixelFast() is used here, to... Well... Write the pixels, fast!

;Ready...
SetBuffer ImageBuffer(PickColor_ColorPanel)
;...Set...
LockBuffer
;... GO!!!
For PickColor_GreenI = 0 To 255
	For PickColor_RedI = 0 To 255
		WritePixelFast PickColor_RedI,PickColor_GreenI,PickColor_Blue + (PickColor_GreenI * 256) + (PickColor_RedI * 65536)
	Next
Next
UnlockBuffer
SetBuffer BackBuffer()
;And BOOM, 65,536 pixels have been slammed into place! Bow before the almighty power of Blitz.

;Now just draw the color panel onto the picker window, with a few lines to give an "indented" appearance.
DrawBlock PickColor_ColorPanel,PickColor_CenterX - 190,PickColor_CenterY - 190
Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
Line PickColor_CenterX - 191,PickColor_CenterY - 191,PickColor_CenterX - 191 + 257,PickColor_CenterY - 191
Line PickColor_CenterX - 191,PickColor_CenterY - 191,PickColor_CenterX - 191,PickColor_CenterY - 191 + 257
Color 255,255,255
Line PickColor_CenterX - 191,PickColor_CenterY - 191 + 257,PickColor_CenterX + 66,PickColor_CenterY - 191 + 257
Line PickColor_CenterX + 66,PickColor_CenterY - 191,PickColor_CenterX + 66,PickColor_CenterY + 66

;A little square to show the selected color, slightly enlarged
Color 255,255,255
Rect PickColor_CenterX - 193 + PickColor_Red,PickColor_CenterY - 193 + PickColor_Green,7,7,0
Color PickColor_Red,PickColor_Green,PickColor_Blue
Rect PickColor_CenterX - 192 + PickColor_Red,PickColor_CenterY - 192 + PickColor_Green,5,5


;Okay, enough with the chart for now. Let's move on to a secondary form of color selection: sliders! (Weee)
;Power Programmer rule of thumb: update first, render later.

;Remember, if something's already been "grabbed," nothing else works until the user lets go
If PickColor_GUIGrab = False
	;Check the mouse against the Red slider
	If MouseX() > PickColor_CenterX + 90 And MouseX() < PickColor_CenterX + 110 And MouseY() > PickColor_CenterY - 185 And MouseY() < PickColor_CenterY - 185 + 256
		If MouseDown(1) Then PickColor_GUIGrab = 1
		PickColor_Help[0] = "Click and drag to move the slider and change the"
		PickColor_Help[1] = " red level."
		PickColor_Help[2] = ""
		PickColor_Help[3] = ""
	EndIf
	;Check the mouse against the Green slider
	If MouseX() > PickColor_CenterX + 120 And MouseX() < PickColor_CenterX + 140 And MouseY() > PickColor_CenterY - 185 And MouseY() < PickColor_CenterY - 185 + 256
		If MouseDown(1) Then PickColor_GUIGrab = 2
		PickColor_Help[0] = "Click and drag to move the slider and change the"
		PickColor_Help[1] = " green level."
		PickColor_Help[2] = ""
		PickColor_Help[3] = ""
	EndIf
	;Check the mouse against the Blue slider
	If MouseX() > PickColor_CenterX + 150 And MouseX() < PickColor_CenterX + 170 And MouseY() > PickColor_CenterY - 185 And MouseY() < PickColor_CenterY - 185 + 256
		If MouseDown(1) Then PickColor_GUIGrab = 3
		PickColor_Help[0] = "Click and drag to move the slider and change the"
		PickColor_Help[1] = " blue level."
		PickColor_Help[2] = ""
		PickColor_Help[3] = ""
	EndIf
EndIf

;Update any grabbed slider accordingly
If PickColor_GUIGrab = 1;Red slider
	PickColor_Red = 255 - (MouseY() - (PickColor_CenterY - 190))
	If PickColor_Red < 0 Then PickColor_Red = 0 ElseIf PickColor_Red > 255 Then PickColor_Red = 255
	PickColor_Help[0] = "Click and drag to move the slider and change the"
	PickColor_Help[1] = " red level."
	PickColor_Help[2] = ""
	PickColor_Help[3] = ""
ElseIf PickColor_GUIGrab = 2;Green slider
	PickColor_Green = 255 - (MouseY() - (PickColor_CenterY - 190))
	If PickColor_Green < 0 Then PickColor_Green = 0 ElseIf PickColor_Green > 255 Then PickColor_Green = 255
	PickColor_Help[0] = "Click and drag to move the slider and change the"
	PickColor_Help[1] = " green level."
	PickColor_Help[2] = ""
	PickColor_Help[3] = ""
ElseIf PickColor_GUIGrab = 3;Blue slider
	PickColor_Blue = 255 - (MouseY() - (PickColor_CenterY - 190))
	If PickColor_Blue < 0 Then PickColor_Blue = 0 ElseIf PickColor_Blue > 255 Then PickColor_Blue = 255
	PickColor_Help[0] = "Click and drag to move the slider and change the"
	PickColor_Help[1] = " blue level."
	PickColor_Help[2] = ""
	PickColor_Help[3] = ""
EndIf

;Now, it's time to actually DRAW the sliders. First, render the colored strips that the sliders will move on.
;Again, WritePixelFast() is used to keep things moving at maximum pace.
LockBuffer
;Red
For i = 0 To 255
	WritePixelFast PickColor_CenterX + 99,(PickColor_CenterY - 190) + (255 - i),PickColor_Blue + (PickColor_Green * 256) + (i * 65536)
	WritePixelFast PickColor_CenterX + 100,(PickColor_CenterY - 190) + (255 - i),PickColor_Blue + (PickColor_Green * 256) + (i * 65536)
	WritePixelFast PickColor_CenterX + 101,(PickColor_CenterY - 190) + (255 - i),PickColor_Blue + (PickColor_Green * 256) + (i * 65536)
Next
;Green
For i = 0 To 255
	WritePixelFast PickColor_CenterX + 129,(PickColor_CenterY - 190) + (255 - i),PickColor_Blue + (i * 256) + (PickColor_Red * 65536)
	WritePixelFast PickColor_CenterX + 130,(PickColor_CenterY - 190) + (255 - i),PickColor_Blue + (i * 256) + (PickColor_Red * 65536)
	WritePixelFast PickColor_CenterX + 131,(PickColor_CenterY - 190) + (255 - i),PickColor_Blue + (i * 256) + (PickColor_Red * 65536)
Next
;Blue
For i = 0 To 255
	WritePixelFast PickColor_CenterX + 159,(PickColor_CenterY - 190) + (255 - i),i + (PickColor_Green * 256) + (PickColor_Red * 65536)
	WritePixelFast PickColor_CenterX + 160,(PickColor_CenterY - 190) + (255 - i),i + (PickColor_Green * 256) + (PickColor_Red * 65536)
	WritePixelFast PickColor_CenterX + 161,(PickColor_CenterY - 190) + (255 - i),i + (PickColor_Green * 256) + (PickColor_Red * 65536)
Next
UnlockBuffer

;Next, draw the sliders themselves. (GEEZ, I love this GUI stuff!!!)
Color PickColor_Red,PickColor_Green,PickColor_Blue
;Red
DrawImage PickColor_Slider,PickColor_CenterX + 100,(PickColor_CenterY - 190) + (255 - PickColor_Red)
Rect PickColor_CenterX + 99,(PickColor_CenterY - 191) + (255 - PickColor_Red),3,3
;Green
DrawImage PickColor_Slider,PickColor_CenterX + 130,(PickColor_CenterY - 190) + (255 - PickColor_Green)
Rect PickColor_CenterX + 129,(PickColor_CenterY - 191) + (255 - PickColor_Green),3,3
;Blue
DrawImage PickColor_Slider,PickColor_CenterX + 160,(PickColor_CenterY - 190) + (255 - PickColor_Blue)
Rect PickColor_CenterX + 159,(PickColor_CenterY - 191) + (255 - PickColor_Blue),3,3

;And finally, draw the labels.; & color information.
Color 0,0,0
Text PickColor_CenterX + 100,PickColor_CenterY + 70,"R",True
Text PickColor_CenterX + 130,PickColor_CenterY + 70,"G",True
Text PickColor_CenterX + 160,PickColor_CenterY + 70,"B",True
;Text PickColor_CenterX + 88,PickColor_CenterY + 85,RSet(PickColor_Red + ",",4)
;Text PickColor_CenterX + 118,PickColor_CenterY + 85,RSet(PickColor_Green + ",",4)
;Text PickColor_CenterX + 148,PickColor_CenterY + 85,RSet(PickColor_Blue,3)


;Now draw some buttons to allow precision control of the RGB levels

Color 255,255,255
;Here, the "update first, render later" rule is broken, because of the way input is checked. The buttons are
;all drawn first, in their unpressed "up" state. Then, if the mouse is clicking on one of them, the pressed
;"down" state is drawn over that.
Line PickColor_CenterX + 90,PickColor_CenterY + 90,PickColor_CenterX + 99,PickColor_CenterY + 90
Line PickColor_CenterX + 90,PickColor_CenterY + 90,PickColor_CenterX + 90,PickColor_CenterY + 99
Line PickColor_CenterX + 100,PickColor_CenterY + 90,PickColor_CenterX + 109,PickColor_CenterY + 90
Line PickColor_CenterX + 100,PickColor_CenterY + 90,PickColor_CenterX + 100,PickColor_CenterY + 99
Line PickColor_CenterX + 120,PickColor_CenterY + 90,PickColor_CenterX + 129,PickColor_CenterY + 90
Line PickColor_CenterX + 120,PickColor_CenterY + 90,PickColor_CenterX + 120,PickColor_CenterY + 99
Line PickColor_CenterX + 130,PickColor_CenterY + 90,PickColor_CenterX + 139,PickColor_CenterY + 90
Line PickColor_CenterX + 130,PickColor_CenterY + 90,PickColor_CenterX + 130,PickColor_CenterY + 99
Line PickColor_CenterX + 150,PickColor_CenterY + 90,PickColor_CenterX + 159,PickColor_CenterY + 90
Line PickColor_CenterX + 150,PickColor_CenterY + 90,PickColor_CenterX + 150,PickColor_CenterY + 99
Line PickColor_CenterX + 160,PickColor_CenterY + 90,PickColor_CenterX + 169,PickColor_CenterY + 90
Line PickColor_CenterX + 160,PickColor_CenterY + 90,PickColor_CenterX + 160,PickColor_CenterY + 99

Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
Line PickColor_CenterX + 90,PickColor_CenterY + 99,PickColor_CenterX + 99,PickColor_CenterY + 99
Line PickColor_CenterX + 99,PickColor_CenterY + 90,PickColor_CenterX + 99,PickColor_CenterY + 99
Line PickColor_CenterX + 100,PickColor_CenterY + 99,PickColor_CenterX + 109,PickColor_CenterY + 99
Line PickColor_CenterX + 109,PickColor_CenterY + 90,PickColor_CenterX + 109,PickColor_CenterY + 99
;AAAAAGH!!! Walls of boring, tedious, repetitive Line() statements!!! @_@
Line PickColor_CenterX + 120,PickColor_CenterY + 99,PickColor_CenterX + 129,PickColor_CenterY + 99
Line PickColor_CenterX + 129,PickColor_CenterY + 90,PickColor_CenterX + 129,PickColor_CenterY + 99
Line PickColor_CenterX + 130,PickColor_CenterY + 99,PickColor_CenterX + 139,PickColor_CenterY + 99
Line PickColor_CenterX + 139,PickColor_CenterY + 90,PickColor_CenterX + 139,PickColor_CenterY + 99
Line PickColor_CenterX + 150,PickColor_CenterY + 99,PickColor_CenterX + 159,PickColor_CenterY + 99
Line PickColor_CenterX + 159,PickColor_CenterY + 90,PickColor_CenterX + 159,PickColor_CenterY + 99
Line PickColor_CenterX + 160,PickColor_CenterY + 99,PickColor_CenterX + 169,PickColor_CenterY + 99
Line PickColor_CenterX + 169,PickColor_CenterY + 90,PickColor_CenterX + 169,PickColor_CenterY + 99

Color 0,0,0
Rect PickColor_CenterX + 91,PickColor_CenterY + 94,8,2
Rect PickColor_CenterX + 94,PickColor_CenterY + 91,2,8
Rect PickColor_CenterX + 101,PickColor_CenterY + 94,8,2
Rect PickColor_CenterX + 121,PickColor_CenterY + 94,8,2
Rect PickColor_CenterX + 124,PickColor_CenterY + 91,2,8
Rect PickColor_CenterX + 131,PickColor_CenterY + 94,8,2
Rect PickColor_CenterX + 151,PickColor_CenterY + 94,8,2
Rect PickColor_CenterX + 154,PickColor_CenterY + 91,2,8
Rect PickColor_CenterX + 161,PickColor_CenterY + 94,8,2

;R+
If MouseX() > PickColor_CenterX + 89 And MouseX() < PickColor_CenterX + 100 And MouseY() > PickColor_CenterY + 89 And MouseY() < PickColor_CenterY + 100
	PickColor_Help[0] = "Click to increase the red level by 1."
	PickColor_Help[1] = ""
	PickColor_Help[2] = ""
	PickColor_Help[3] = ""
	
	If MouseDown(1)
		PickColor_ButtonRepeatTimer[0] = PickColor_ButtonRepeatTimer[0] + 1
		If PickColor_ButtonRepeatTimer[0] = 1 Or PickColor_ButtonRepeatTimer[0] > 45 Then PickColor_Red = PickColor_Red + 1

		Color 255,255,255
		Line PickColor_CenterX + 90,PickColor_CenterY + 99,PickColor_CenterX + 99,PickColor_CenterY + 99
		Line PickColor_CenterX + 99,PickColor_CenterY + 90,PickColor_CenterX + 99,PickColor_CenterY + 99
		Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
		Line PickColor_CenterX + 90,PickColor_CenterY + 90,PickColor_CenterX + 99,PickColor_CenterY + 90
		Line PickColor_CenterX + 90,PickColor_CenterY + 90,PickColor_CenterX + 90,PickColor_CenterY + 99
	Else
	
		;Reset the button's repeat timer
		PickColor_ButtonRepeatTimer[0] = 0
	EndIf
;R-
ElseIf MouseX() > PickColor_CenterX + 99 And MouseX() < PickColor_CenterX + 110 And MouseY() > PickColor_CenterY + 89 And MouseY() < PickColor_CenterY + 100
	PickColor_Help[0] = "Click to decrease the red level by 1."
	PickColor_Help[1] = ""
	PickColor_Help[2] = ""
	PickColor_Help[3] = ""
	
	If MouseDown(1)
		PickColor_ButtonRepeatTimer[1] = PickColor_ButtonRepeatTimer[1] + 1
		If PickColor_ButtonRepeatTimer[1] = 1 Or PickColor_ButtonRepeatTimer[1] > 45 Then PickColor_Red = PickColor_Red - 1

		Color 255,255,255
		Line PickColor_CenterX + 100,PickColor_CenterY + 99,PickColor_CenterX + 109,PickColor_CenterY + 99
		Line PickColor_CenterX + 109,PickColor_CenterY + 90,PickColor_CenterX + 109,PickColor_CenterY + 99
		Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
		Line PickColor_CenterX + 100,PickColor_CenterY + 90,PickColor_CenterX + 109,PickColor_CenterY + 90
		Line PickColor_CenterX + 100,PickColor_CenterY + 90,PickColor_CenterX + 100,PickColor_CenterY + 99
	Else
		PickColor_ButtonRepeatTimer[1] = 0
	EndIf
;G+
ElseIf MouseX() > PickColor_CenterX + 119 And MouseX() < PickColor_CenterX + 130 And MouseY() > PickColor_CenterY + 89 And MouseY() < PickColor_CenterY + 100
	PickColor_Help[0] = "Click to increase the green level by 1."
	PickColor_Help[1] = ""
	PickColor_Help[2] = ""
	PickColor_Help[3] = ""
	
	If MouseDown(1)
		PickColor_ButtonRepeatTimer[2] = PickColor_ButtonRepeatTimer[2] + 1
		If PickColor_ButtonRepeatTimer[2] = 1 Or PickColor_ButtonRepeatTimer[2] > 45 Then PickColor_Green = PickColor_Green + 1

		Color 255,255,255
		Line PickColor_CenterX + 120,PickColor_CenterY + 99,PickColor_CenterX + 129,PickColor_CenterY + 99
		Line PickColor_CenterX + 129,PickColor_CenterY + 90,PickColor_CenterX + 129,PickColor_CenterY + 99
		Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
		Line PickColor_CenterX + 120,PickColor_CenterY + 90,PickColor_CenterX + 129,PickColor_CenterY + 90
		Line PickColor_CenterX + 120,PickColor_CenterY + 90,PickColor_CenterX + 120,PickColor_CenterY + 99
	Else
		PickColor_ButtonRepeatTimer[2] = 0
	EndIf
;G-
ElseIf MouseX() > PickColor_CenterX + 129 And MouseX() < PickColor_CenterX + 140 And MouseY() > PickColor_CenterY + 89 And MouseY() < PickColor_CenterY + 100
	PickColor_Help[0] = "Click to decrease the green level by 1."
	PickColor_Help[1] = ""
	PickColor_Help[2] = ""
	PickColor_Help[3] = ""
	
	If MouseDown(1)
		PickColor_ButtonRepeatTimer[3] = PickColor_ButtonRepeatTimer[3] + 1
		If PickColor_ButtonRepeatTimer[3] = 1 Or PickColor_ButtonRepeatTimer[3] > 45 Then PickColor_Green = PickColor_Green - 1

		Color 255,255,255
		Line PickColor_CenterX + 130,PickColor_CenterY + 99,PickColor_CenterX + 139,PickColor_CenterY + 99
		Line PickColor_CenterX + 139,PickColor_CenterY + 90,PickColor_CenterX + 139,PickColor_CenterY + 99
		Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
		Line PickColor_CenterX + 130,PickColor_CenterY + 90,PickColor_CenterX + 139,PickColor_CenterY + 90
		Line PickColor_CenterX + 130,PickColor_CenterY + 90,PickColor_CenterX + 130,PickColor_CenterY + 99
	Else
		PickColor_ButtonRepeatTimer[3] = 0
	EndIf
;B+
ElseIf MouseX() > PickColor_CenterX + 149 And MouseX() < PickColor_CenterX + 160 And MouseY() > PickColor_CenterY + 89 And MouseY() < PickColor_CenterY + 100
	PickColor_Help[0] = "Click to increase the blue level by 1."
	PickColor_Help[1] = ""
	PickColor_Help[2] = ""
	PickColor_Help[3] = ""
	
	If MouseDown(1)
		PickColor_ButtonRepeatTimer[4] = PickColor_ButtonRepeatTimer[4] + 1
		If PickColor_ButtonRepeatTimer[4] = 1 Or PickColor_ButtonRepeatTimer[4] > 45 Then PickColor_Blue = PickColor_Blue + 1

		Color 255,255,255
		Line PickColor_CenterX + 150,PickColor_CenterY + 99,PickColor_CenterX + 159,PickColor_CenterY + 99
		Line PickColor_CenterX + 159,PickColor_CenterY + 90,PickColor_CenterX + 159,PickColor_CenterY + 99
		Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
		Line PickColor_CenterX + 150,PickColor_CenterY + 90,PickColor_CenterX + 159,PickColor_CenterY + 90
		Line PickColor_CenterX + 150,PickColor_CenterY + 90,PickColor_CenterX + 150,PickColor_CenterY + 99
	Else
		PickColor_ButtonRepeatTimer[4] = 0
	EndIf
;B-
ElseIf MouseX() > PickColor_CenterX + 159 And MouseX() < PickColor_CenterX + 170 And MouseY() > PickColor_CenterY + 89 And MouseY() < PickColor_CenterY + 100
	PickColor_Help[0] = "Click to decrease the blue level by 1."
	PickColor_Help[1] = ""
	PickColor_Help[2] = ""
	PickColor_Help[3] = ""
	
	If MouseDown(1)
		PickColor_ButtonRepeatTimer[5] = PickColor_ButtonRepeatTimer[5] + 1
		If PickColor_ButtonRepeatTimer[5] = 1 Or PickColor_ButtonRepeatTimer[5] > 45 Then PickColor_Blue = PickColor_Blue - 1

		Color 255,255,255
		Line PickColor_CenterX + 160,PickColor_CenterY + 99,PickColor_CenterX + 169,PickColor_CenterY + 99
		Line PickColor_CenterX + 169,PickColor_CenterY + 90,PickColor_CenterX + 169,PickColor_CenterY + 99
		Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
		Line PickColor_CenterX + 160,PickColor_CenterY + 90,PickColor_CenterX + 169,PickColor_CenterY + 90
		Line PickColor_CenterX + 160,PickColor_CenterY + 90,PickColor_CenterX + 160,PickColor_CenterY + 99
	Else
		PickColor_ButtonRepeatTimer[5] = 0
	EndIf
EndIf

;Keep the RGB levels within the 0-255 boundaries
If PickColor_Red < 0 Then PickColor_Red = 0 ElseIf PickColor_Red > 255 Then PickColor_Red = 255
If PickColor_Green < 0 Then PickColor_Green = 0 ElseIf PickColor_Green > 255 Then PickColor_Green = 255
If PickColor_Blue < 0 Then PickColor_Blue = 0 ElseIf PickColor_Blue > 255 Then PickColor_Blue = 255


;Show the color information
;Selected color preview
Color PickColor_Red,PickColor_Green,PickColor_Blue
Rect PickColor_CenterX - 190,PickColor_CenterY + 80,50,50
Color 255,255,255
Line PickColor_CenterX - 140,PickColor_CenterY + 79,PickColor_CenterX - 140,PickColor_CenterY + 130
Line PickColor_CenterX - 191,PickColor_CenterY + 130,PickColor_CenterX - 140,PickColor_CenterY + 130
Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
Line PickColor_CenterX - 191,PickColor_CenterY + 79,PickColor_CenterX - 140,PickColor_CenterY + 79
Line PickColor_CenterX - 191,PickColor_CenterY + 79,PickColor_CenterX - 191,PickColor_CenterY + 130
Color 0,0,0
Text PickColor_CenterX - 130,PickColor_CenterY + 80,"SELECTED COLOR"
Text PickColor_CenterX - 130,PickColor_CenterY + 97,"RGB: " + PickColor_Red + "," + PickColor_Green + "," + PickColor_Blue
Text PickColor_CenterX - 130,PickColor_CenterY + 114,"HEX: " + Right(Hex(PickColor_Red),2) + Right(Hex(PickColor_Green),2) + Right(Hex(PickColor_Blue),2)


;Now update & display the "DONE" button.
If MouseX() > PickColor_CenterX + 3 And MouseX() < PickColor_CenterX + 56 And MouseY() > PickColor_CenterY + 78 And MouseY() < PickColor_CenterY + 131
;						"|-----------------------------------------------|"
	PickColor_Help[0] = "Found the color you want? Click here to finish."
	PickColor_Help[1] = ""
	PickColor_Help[2] = ""
	PickColor_Help[3] = ""
	If MouseDown(1) And PickColor_GUIGrab = 0 Then PickColor_Continue = False;Sayonara!
EndIf

Text PickColor_CenterX + 29,PickColor_CenterY + 104,"DONE",True,True
Color 255,255,255
Line PickColor_CenterX + 4,PickColor_CenterY + 79,PickColor_CenterX + 55,PickColor_CenterY + 79
Line PickColor_CenterX + 4,PickColor_CenterY + 79,PickColor_CenterX + 4,PickColor_CenterY + 130
Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
Line PickColor_CenterX + 55,PickColor_CenterY + 79,PickColor_CenterX + 55,PickColor_CenterY + 130
Line PickColor_CenterX + 4,PickColor_CenterY + 130,PickColor_CenterX + 55,PickColor_CenterY + 130
Color 0,0,0


;Display the context-sensitive help
Color 0,0,0
Text PickColor_CenterX,PickColor_CenterY + 140,PickColor_Help[0],True
Text PickColor_CenterX,PickColor_CenterY + 155,PickColor_Help[1],True
Text PickColor_CenterX,PickColor_CenterY + 170,PickColor_Help[2],True
Text PickColor_CenterX,PickColor_CenterY + 185,PickColor_Help[3],True


;Now, back to the color chart. This had to come last, since it draws a preview on top of everything else.
;If the mouse is over the color chart, show a nice little preview box, otherwise just draw the cursor
If MouseX() => PickColor_CenterX - 190 And MouseX() < PickColor_CenterX - 190 + 256 And MouseY() => PickColor_CenterY - 190 And MouseY() < PickColor_CenterY - 190 + 256 And PickColor_GUIGrab = False

	;Show the color data in RGB and Hexadecimal form, on a little floating "panel"
	Color PickColor_GUIRed,PickColor_GUIGreen,PickColor_GUIBlue
	Rect MouseX() - 61,MouseY() + 24,122,31
	Color 255,255,255
	Line MouseX() - 61,MouseY() + 24,MouseX() + 60,MouseY() + 24
	Line MouseX() - 61,MouseY() + 24,MouseX() - 61,MouseY() + 54
	Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
	Line MouseX() + 60,MouseY() + 25,MouseX() + 60,MouseY() + 54
	Line MouseX() - 60,MouseY() + 54,MouseX() + 60,MouseY() + 54
	Color 0,0,0
	Line MouseX() - 61,MouseY() + 55,MouseX() + 61,MouseY() + 55
	Line MouseX() + 61,MouseY() + 24,MouseX() + 61,MouseY() + 55

	Text MouseX() - 60,MouseY() + 25,"RGB " + (MouseX() - (PickColor_CenterX - 190)) + "," + (MouseY() - (PickColor_CenterY - 190)) + "," + PickColor_Blue
	Text MouseX() - 60,MouseY() + 40,"HEX " + Right(Hex(MouseX() - (PickColor_CenterX - 190)),2) + Right(Hex(MouseY() - (PickColor_CenterY - 190)),2) + Right(Hex(PickColor_Blue),2)

	;If the cursor is over the currently selected color, highlight the preview box
	If MouseX() - (PickColor_CenterX - 190) = PickColor_Red And MouseY() - (PickColor_CenterY - 190) = PickColor_Green Then Color 255,255,255
	Rect MouseX() - 21,MouseY() - 21,43,43,0
	Color MouseX() - (PickColor_CenterX - 190),MouseY() - (PickColor_CenterY - 190),PickColor_Blue
	Rect MouseX() - 20,MouseY() - 20,41,41
Else
	If PickColor_GUIGrab = 4
		Color PickColor_GUIRed,PickColor_GUIGreen,PickColor_GUIBlue
		Rect PickColor_CenterX - 190 + PickColor_Red - 61,PickColor_CenterY - 190 + PickColor_Green + 24,122,31
		Color 255,255,255
		Line PickColor_CenterX - 190 + PickColor_Red - 61,PickColor_CenterY - 190 + PickColor_Green + 24,PickColor_CenterX - 190 + PickColor_Red + 60,PickColor_CenterY - 190 + PickColor_Green + 24
		Line PickColor_CenterX - 190 + PickColor_Red - 61,PickColor_CenterY - 190 + PickColor_Green + 24,PickColor_CenterX - 190 + PickColor_Red - 61,PickColor_CenterY - 190 + PickColor_Green + 54
		Color PickColor_GUIRed / 1.5,PickColor_GUIGreen / 1.5,PickColor_GUIBlue / 1.5
		Line PickColor_CenterX - 190 + PickColor_Red + 60,PickColor_CenterY - 190 + PickColor_Green + 25,PickColor_CenterX - 190 + PickColor_Red + 60,PickColor_CenterY - 190 + PickColor_Green + 54
		Line PickColor_CenterX - 190 + PickColor_Red - 60,PickColor_CenterY - 190 + PickColor_Green + 54,PickColor_CenterX - 190 + PickColor_Red + 60,PickColor_CenterY - 190 + PickColor_Green + 54
		Color 0,0,0
		Line PickColor_CenterX - 190 + PickColor_Red - 61,PickColor_CenterY - 190 + PickColor_Green + 55,PickColor_CenterX - 190 + PickColor_Red + 61,PickColor_CenterY - 190 + PickColor_Green + 55
		Line PickColor_CenterX - 190 + PickColor_Red + 61,PickColor_CenterY - 190 + PickColor_Green + 24,PickColor_CenterX - 190 + PickColor_Red + 61,PickColor_CenterY - 190 + PickColor_Green + 55
	
		Text PickColor_CenterX - 190 + PickColor_Red - 60,PickColor_CenterY - 190 + PickColor_Green + 25,"RGB " + PickColor_Red + "," + PickColor_Green + "," + PickColor_Blue
		Text PickColor_CenterX - 190 + PickColor_Red - 60,PickColor_CenterY - 190 + PickColor_Green + 40,"HEX " + Right(Hex(PickColor_Red),2) + Right(Hex(PickColor_Green),2) + Right(Hex(PickColor_Blue),2)

		Color 255,255,255
		Rect PickColor_CenterX - 190 + PickColor_Red - 21,PickColor_CenterY - 190 + PickColor_Green - 21,43,43,0
		Color PickColor_Red,PickColor_Green,PickColor_Blue
		Rect PickColor_CenterX - 190 + PickColor_Red - 20,PickColor_CenterY - 190 + PickColor_Green - 20,41,41
	Else
		DrawImage PickColor_Cursor,MouseX(),MouseY()
	EndIf
EndIf


Flip


Wend


;Delete all the created images to free up memory again
FreeImage CurrentScreen
FreeImage PickColor_ColorPanel
FreeImage PickColor_Cursor
FreeImage PickColor_Slider
;Wooo, in alphabetical order even! Fancy! :D


;Scrap the font
FreeFont PickColor_Font


;Restore the original buffer & color settings
SetBuffer CurrentBuffer
Color PickColor_GUIRed,PickColor_GUIGreen,PickColor_GUIBlue


;And finally, what this function is really all about: record the picked color.
PickedRed   = PickColor_Red
PickedGreen = PickColor_Green
PickedBlue  = PickColor_Blue


;That's all folks! Ta-daaa!!! :D


End Function
