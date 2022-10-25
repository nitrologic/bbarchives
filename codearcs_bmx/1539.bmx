; ID: 1539
; Author: WendellM
; Date: 2005-11-21 19:43:02
; Title: MaxGUI Panel Image Buttons
; Description: Panels serving as image buttons with color text (and color background if no image supplied)

' Panels serving as image buttons with color text (and color background if no image supplied)
' Wendell Martin, Jr. - December 26, 2005
' Written for BlitzMax 1.14.  Successfully tested on Windows XP SP2 and MacOS 10.3.9; not tried in Linux

Strict

Type TImageButton

	Field panel:TGadget, shadow:TGadget
	Field mouseover, primed

	Function Create:TImageButton ( x,y, w,h, group:TGadget, text$ = "", font:TImageFont = Null, ..
	         tr=0,tg=0,tb=0, image:TImage = Null, br=224,bg=224,bb=224, sr=0,sg=0,sb=0, ss=3,so=1)
	' X,Y, W,H, Group, Text, Font, Text RGB, Background Image, Background RGB, Shadow RGB, Shadow Size, Shadow Offset
	' The shadow size and offset are adjustable in case they need to be bigger for very large buttons
	' A canvas to draw button on
	
		Local t:TImageButton = New TImageButton
	
		'Do the underlying shadow first
		
		t.shadow = CreatePanel( x,y, w,h, group )
		SetPanelColor t.shadow, sr, sg, sb
		DisableGadget t.shadow ' if not disabled, it can interfere with events
	
		' And now the button itself
				
		Local canvas:TGadget = CreateCanvas( 0, 0, w - ss, h - ss, group )
		?Win32
			HideGadget canvas ' Looks nicer if hidden. Works in Windows, but fails in MacOS (Linux unknown)
		?
		SetGraphics CanvasGraphics( canvas )
		SetBlend ALPHABLEND
	
		' the button's background
		SetClsColor br, bg, bb
		Cls
	
		If image <> Null Then 
			Local sx# = Float( ( w - ss ) ) / ImageWidth( image )
			Local sy# = Float( ( h - ss ) ) / ImageHeight( image )
			SetScale sx, sy
			DrawImage image, 0, 0 ' stretch to fit
		EndIf
		
		' button's text
		SetScale 1,1
		SetColor tr, tg, tb
		If font <> Null Then SetImageFont font
		Local tw = TextWidth( text ), th = 	TextHeight( text )
		DrawText text, ( ( w - tw ) / 2 ) - 1, ( ( h - th ) / 2 ) ' center text horiz/vert (the -1 is a tweak)
		Flip
	
		' now grab what's been drawn and assign it to panel
		Local pix:tpixmap = CreatePixmap( w - ss, h - ss, PF_RGB888 )
		pix=GrabPixmap( 0, 0, w - ss, h - ss )
		t.panel:tgadget=CreatePanel( x + so, y + so, w - ss, h - ss, group, PANEL_ACTIVE )
		SetPanelPixmap t.panel, pix
		
		FreeGadget canvas
		Return t
	
	End Function
	
	Method clicked()
	' This emulates how buttons work, including being able to "change your mind" after pressing the mouse
	' button by moving the pointer away and releasing the button.  
		Select EventID()
			Case EVENT_MOUSEUP
				If mouseover And primed Then 
					primed = False
					SetGadgetShape panel, GadgetX(panel)-1, GadgetY(panel)-1, GadgetWidth(panel), GadgetHeight(panel) ' back to default
					Return True ' clicked
				EndIf
				If Not mouseover Then
					primed = False
				EndIf
			Case EVENT_MOUSEDOWN
				primed = True
				SetGadgetShape panel, GadgetX(panel)+1, GadgetY(panel)+1, GadgetWidth(panel), GadgetHeight(panel) ' 1 pixel down/right
			Case EVENT_MOUSEENTER
				mouseover = True
				If primed Then SetGadgetShape panel, GadgetX(panel)+1, GadgetY(panel)+1, GadgetWidth(panel), GadgetHeight(panel)
			Case EVENT_MOUSELEAVE
				mouseover = False
				If primed Then SetGadgetShape panel, GadgetX(panel)-1, GadgetY(panel)-1, GadgetWidth(panel), GadgetHeight(panel)
		End Select
	End Method
	
End Type


Local window:TGadget = CreateWindow( "Panel 'Image Buttons'", 100,100, 400,300, Null, WINDOW_TITLEBAR )

Local text0:TGadget = CreateTextField( 5,10, 95,20, window )
Local button0:TGadget = CreateButton( "Real Button", 5,30, 95,30, window, BUTTON_PUSH )

Local AFont:TImageFont = LoadImageFont( "Tymes Bold.ttf", 17 )
Local Texture:TImage = LoadImage( "wood.png" )

' As simple as possible - use default font and colors:
Local text1:TGadget = CreateTextField( 5,80, 95,20, window )
Local panel1:TImageButton = TImageButton.Create( 5,100, 95,30, window, "Test Panel" )

' With custom font, text color, background color, and shadow color:
Local text2:TGadget = CreateTextField( 105,80, 95,20, window )
Local panel2:TImageButton = TImageButton.Create( 105,100, 95,30, window, "Test Panel", AFont, 255,0,0, Null, 200,200,255, 120,120,120 )

'With custom font and image. The brown background color is unneeded, but is a fallback in case texture image not found:
Local text3:TGadget = CreateTextField( 205,80, 95,20, window )
Local panel3:TImageButton = TImageButton.Create( 205,100, 95,30, window, "Test Panel", AFont, 255,255,127, Texture, 127,127,0 )


Repeat
	WaitEvent
	Select EventSource()
		Case button0
			Select EventID()
				Case EVENT_GADGETACTION
					SetGadgetText text0, Int( TextFieldText(text0) ) + 1
			End Select
		Case panel1.panel
			If panel1.clicked() Then SetGadgetText text1, Int( TextFieldText( text1 ) ) + 1 ' increment count
		Case panel2.panel
			If panel2.clicked() Then SetGadgetText text2, Int( TextFieldText( text2 ) ) + 1
		Case panel3.panel
			If panel3.clicked() Then SetGadgetText text3, Int( TextFieldText( text3 ) ) + 1
	End Select
Until EventID() = EVENT_WINDOWCLOSE

End
