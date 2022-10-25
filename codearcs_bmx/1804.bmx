; ID: 1804
; Author: WendellM
; Date: 2006-09-01 14:50:36
; Title: MaxGUI graphics mode chooser
; Description: Pre-game selection from available graphics modes.  Can restrict options to specified resolutions, etc.  and save mode choice to file.

Rem

	PreGame module by WendellM - released September 1, 2006, updated Sept. 5, 2006
	Allows choice of graphics driver, screen mode, and settings
	Saves config file to preserve choices for future use, but works without one
	Defaults to OpenGL without displaying choice in Linux & OS X
	
	Example of use within a calling program:
	
		Import "pregame.bmx"
		Local pre:TPregame
		
		' whenever you want to set graphics:
		pre = TPregame.Create()
		pre.UserChoice
		If pre.driver = "OpenGL" Then SetGraphicsDriver GLMax2DDriver()
		If pre.driver = "DirectX" Then SetGraphicsDriver D3D7Max2DDriver()
		If pre.screen = "Windowed" Then
			Graphics pre.width, pre.height
		Else
			Graphics pre.width, pre.height, pre.depth, pre.hertz
		EndIf
		' Save graphics settings here if desired.
		TPreGame.Destroy pre
		
		WaitKey
		
End Rem

Strict
TPreGame.Test ' <- Self-contained example of use. Uncomment to use, comment out to ignore.

Type TPreGame

	Field driver$, screen$, mode$
	Field width, height, depth, hertz
	Field preGame:TGadget
		Field pgPanelDriver:TGadget
			Field pgDriverDX:TGadget, pgDriverGL:TGadget
		Field pgPanelScreen:TGadget
			Field pgScreenFull:TGadget, pgScreenWindowed:TGadget
		Field pgPanelRes:TGadget
			Field pgMode:TGadget
		Field pgButtonOK:TGadget, pgButtonQuit:TGadget

	Function Create:TPreGame()
		Local t:TPreGame = New TPreGame
		Local dw = GadgetWidth( Desktop() )
		Local dh = GadgetHeight( Desktop() )
		Local w = 220
		Local h = 350
		Local in:TStream = ReadFile("graphics.dat")
		
		? MacOS
		w = 250
		?

		? Linux
		w = 250
		?
		
		If in Then
			t.driver = ReadLine(in)
			t.screen = ReadLine(in)
			t.mode = ReadLine(in)
			CloseFile in
		Else
			t.driver = "DirectX"
			t.screen = "Fullscreen"
			t.mode = "1024 x   768, 32-bit, 85 Hz *" ' try this as default if none other specified
		EndIf

		t.preGame = CreateWindow( "PreGame", (dw-w)/2,(dh-h)/2, w,h, Null, WINDOW_TITLEBAR )

		t.pgPanelDriver = CreatePanel( 10,10, w-25,47, t.preGame,PANEL_GROUP, "Driver" )
		t.pgDriverDX = CreateButton( "DirectX", 10,3, 80,19, t.pgPanelDriver, BUTTON_RADIO )
		t.pgDriverGL = CreateButton( "OpenGL", 100,3, 80,19, t.pgPanelDriver, BUTTON_RADIO )
		If t.Driver = "DirectX" Then
			SetButtonState t.pgDriverDX, True
		ElseIf t.Driver = "OpenGL" Then
			SetButtonState t.pgDriverGL, True
		Else
			SetButtonState t.pgDriverDX, True
		EndIf

		? Linux
		SetButtonState t.pgDriverGL, True
		HideGadget t.pgPanelDriver
		?
		
		? MacOS		
		SetButtonState t.pgDriverGL, True
		HideGadget t.pgPanelDriver
		?

		t.pgPanelScreen = CreatePanel( 10,65, w-25,47, t.preGame,PANEL_GROUP, "Screen Mode" )
		t.pgScreenFull = CreateButton( "Fullscreen", 10,3, 80,19, t.pgPanelScreen, BUTTON_RADIO )
		t.pgScreenWindowed = CreateButton( "Windowed", 100,3, 80,19, t.pgPanelScreen, BUTTON_RADIO )
		If t.Screen = "Fullscreen" Then
			SetButtonState t.pgScreenFull, True
		ElseIf t.Screen = "Windowed" Then
			SetButtonState t.pgScreenWindowed, True
		Else
			SetButtonState t.pgScreenFull, True		
		EndIf
		

		Local PanelResY = 120
		? Linux
			PanelResY = 150
		?

		t.pgPanelRes = CreatePanel( 10,PanelResY, w-25,70, t.preGame,PANEL_GROUP,..
			"Resolution, Color, Refresh" )
		
		CreateLabel "( * = recommended )", 10,0, w-52,15, t.pgPanelRes
		t.pgMode = CreateComboBox( 8,19, w-48, 23, t.pgPanelRes )
		Local List:TList = New TList
		Local numModes = CountGraphicsModes()
		Local index = 0, setIndex
		Local gw, ght, gd, ghz, rec$ ' graphic width, height, depth, hz, + recommended
		Local spcW$, spcH$
		Local selected  = False
		
		For Local x = 0 To numModes - 1
			GetGraphicsMode( x, gw, ght, gd, ghz )
			If gw < 1000 Then spcW = "  " Else spcW = "" ' for nice columns
			If ght < 1000 Then spcH = "  " Else spcH = ""
			' put recommendation asterisk if 1024x768 or better and near 4:3 (1.333) aspect ratio
			If gw >= 1024 And ght >= 768 And Float(gw) / ght > 1.3 And Float(gw) / ght < 1.3667 And ..
			  gd >= 24 Then rec = " *" Else rec = ""
			Local a$ = spcW + gw + " x " + spcH + ght + ", " + gd +"-bit, " + ghz + " Hz" + rec
			' don't display resolutions below 800x600 or above 1600x1200
			If gw >= 800 And gw <= 1600 And ght >= 600 And ght <= 1200 Then
				ListAddLast List, a
				AddGadgetItem t.pgMode, a
				If a = t.mode Then setIndex = index
				index :+ 1
			EndIf
		Next
		
		If setIndex Then
			SelectGadgetItem t.pgMode, setIndex
			selected = True
		EndIf
		
		If Not selected Then SelectGadgetItem t.pgMode, 0
		t.pgButtonOK = CreateButton( "OK", 20+(w-220)/2,289, 80,24, t.preGame, BUTTON_OK )
		t.pgButtonQuit = CreateButton( "Quit", 110+(w-220),289, 80,24, t.preGame )
		
		Return t
		
	End Function
	
	Function Destroy(t:TPreGame Var)
		FreeGadget t.preGame
		t = Null
		GCCollect
	End Function
	
	Method UserChoice:Int() ' updates driver, screen, width, height, depth, and hertz fields
		ActivateGadget pgButtonOK ' default focus
		Repeat
			WaitEvent
		Until ( EventID() = EVENT_WINDOWCLOSE And EventSource() = preGame ) or..
		      ( EventID() = EVENT_GADGETACTION And EventSource() = pgButtonOK ) or..
		      ( EventID() = EVENT_GADGETACTION And EventSource() = pgButtonQuit )
		If EventSource() = preGame Or EventSource() = pgButtonQuit Then End
		If EventSource() = pgButtonOK Then
			If ButtonState(pgDriverDX) Then Driver = "DirectX"
			If ButtonState(pgDriverGL) Then Driver = "OpenGL"
			If ButtonState(pgScreenFull) Then Screen = "Fullscreen"
			If ButtonState(pgScreenWindowed) Then Screen = "Windowed"
			mode = GadgetItemText( pgMode, SelectedGadgetItem(pgMode) ) ' same as: GadgetText(pgMode)
			width = Int(mode[0..5])
			height = Int(mode[7..13])
			Local comma = mode.find(",")
			depth = Int(mode[comma+2..comma+4])
			comma = mode.find(",", comma+3)
			hertz = Int(mode[comma+2..comma+4])
			Local out:TStream = WriteFile("graphics.dat")	
			WriteLine out, driver
			WriteLine out, screen
			WriteLine out, mode
			CloseFile out
			Return True
		EndIf
		Notify "Event handling exception in PreGame:UserChoice", True; End
	End Method

	Function Test()
		Local e:TPreGame = TPreGame.Create()
		If e.UserChoice() Then Notify e.driver +", "+ e.screen +", "+ ..
		                              e.width +","+ e.height +","+ e.depth +","+ e.hertz
		TPreGame.Destroy e
	End Function

End Type
