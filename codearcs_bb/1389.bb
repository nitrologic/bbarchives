; ID: 1389
; Author: Nicstt
; Date: 2005-06-03 09:01:19
; Title: Start Screen
; Description: mini welcome sreen to choose resolution, windowed and players

; ************************************************************************************************************************
; Written by Nicholas Tindall
; ************************************************************************************************************************
AppTitle "version 0"
SeedRnd MilliSecs()

Include "Opening Screen.bb"

;---------------------------------------------------------------------------------------
; constants that should be adjusted depending on requirements - see: Opening Screen.bb
;---------------------------------------------------------------------------------------

; SETUP_Help			; used to inform Opening Screen if addition help/instructions available - true or false - disables the radio button if false
; SETUP_Prefs			; not used atm,  true or false - disables the radio button if false
; SETUP_TwoPlayer		; true or false - disables the radio button if false
; SETUP_CompPlayer		; true or false - disables the radio button if false
; SETUP_Full			; true of false - disables the radio button if false
; SETUP_Windowed		; true of false - disables the radio button if false
; SETUP_Psudo			; true of false - disables the radio button if false

;---------------------------------------------------------------------------------------
; info passed to these from Opening Screen.bb 
;---------------------------------------------------------------------------------------

; screenWidth			; width of game screen
; screenHeight			; height of game screen
; screenDepth			; depth of game screen
; screenType			; type is windowed or full screen - 1 = full screen, 2 = windowed, 3 = Psuedo, will be reset to 1 by main game loop
; playerNumber			; 1 = one, 2 = two, 3 = computer oponent

;---------------------------------------------------------------------------------------
; Opening Screen.bb uses them to determine actions - .StartGameSetup is where game starts
;---------------------------------------------------------------------------------------
; COMBO_position		= 0		; what number the combo box should begin, ie first in list or further

; HELP_frame			= 18	; how many lines of text per page of help displayed
; HELP_title			= True	; set to false if no title lines - otherwise displays first line of help centered and in a larger font
									; displays first page with title line regardless

.StartGameSetup			; main game starts from here
HidePointer
;---------------------------------------------------------------------------------------------------------------------------------------
; ... Constants and Globals ...
;---------------------------------------------------------------------------------------------------------------------------------------


;---------------------------------------------------------------------------------------------------------------------------------------
; ... Type Setup ...
;---------------------------------------------------------------------------------------------------------------------------------------


;---------------------------------------------------------------------------------------------------------------------------------------
; ... Sound Setup ...
;---------------------------------------------------------------------------------------------------------------------------------------


;---------------------------------------------------------------------------------------------------------------------------------------
; ... Graphics Setup ...
;---------------------------------------------------------------------------------------------------------------------------------------

If (screenType > 0) And (screenType < 3)
	Graphics screenWidth, screenHeight, screenDepth, screenType
ElseIf screenType = 3
	screenType = 2
	Graphics screenWidth, screenHeight, screenDepth, screenType
ElseIf screenType = 0
	RuntimeError "ERROR on screenType - Problem with depth"
EndIf


SetBuffer(BackBuffer()) : ClsColor 0, 0, 0 : Cls : Flip
;*************************************************************************************************************************************************************************
; * * * * * * * * *  M A I N  L O O P  * * * * * * * * * *
;*************************************************************************************************************************************************************************
Global delay_timer = CreateTimer(60)
Repeat




	

	If KeyHit(1) Then Exit
Forever

Gosub clearandfreestuff: EndGraphics
FlushKeys : FlushEvents()
Goto startoptionscreen
End
;*************************************************************************************************************************************************************************
; * * * * * * * * * *  F U N C T I O N S  * * * * * * * * * *
;*************************************************************************************************************************************************************************









;*************************************************************************************************************************************************************************
; * * * * * * * * * *  S U B - R O U T I N E S  * * * * * * * * * *
;*************************************************************************************************************************************************************************





; clear and free stuff
.clearandfreestuff


Return

;---------------------------------------------------------------------------------------------------------------------------------------
; ... Data ...
;---------------------------------------------------------------------------------------------------------------------------------------

***************************************************************************************************
***************************************************************************************************
***************************************************************************************************
SEPERATE CODE FILE - - - SEPERATE CODE FILE - - - SEPERATE CODE FILE - - - SEPERATE CODE FILE - - - 
***************************************************************************************************
.startoptionscreen
If CountGfxModes()=0 RuntimeError "No fullscreen gfx modes detected"

; -----------------------------------------------------------------------------
; ...Button Controls and globals for game/program, some info to be passed to main program...
; -----------------------------------------------------------------------------
; constants that should be adjusted depending on requirements
Const SETUP_Help			= False		; used to inform Opening Screen if addition help/instructions available - true or false - NOT disabled
Const SETUP_Prefs			= False		; not used atm,  true or false - disables the radio button if false
Const SETUP_TwoPlayer		= False		; true or false - disables the radio button if false
Const SETUP_CompPlayer		= False		; true or false - disables the radio button if false
Const SETUP_Full			= True		; true of false - disables the radio button if false
Const SETUP_Windowed		= True		; true of false - disables the radio button if false
Const SETUP_Psudo			= True		; true of false - disables the radio button if false - will be disabled if current Windows mode not one of options available
; info passed to Main Game.bb
Global screenWidth			= 0		; width of game screen
Global screenHeight			= 0		; height of game screen
Global screenDepth			= 0		; depth of game screen
Global screenType			= 0		; type is windowed or full screen - 1 = full screen, 2 = windowed, 3 = Psuedo, will be reset to 1 by main program
Global playerNumber			= 1		; 1 = one, 2 = two, 3 = computer oponent
		
; instructions and/or help, 
Const HELP_frame			= 18	; how many lines of text per page of help displayed
Const HELP_title			= True	; set to false if no title lines - otherwise displays first line of help centered and in a larger font
									; displays first page with title line regardless

; useful during game creation to always start at specific resolution, i think so anyway :)
Const COMBO_position		= 0		; what number the combo box should begin, ie first in list or further

; -----------------------------------------------------------------------------
; ...Event definitions...
; -----------------------------------------------------------------------------
Const EVENT_None		= $0		; No event (eg. a WaitEvent timeout)
Const EVENT_KeyDown		= $101		; Key pressed
Const EVENT_KeyUp		= $102		; Key released
Const EVENT_ASCII		= $103		; ASCII key pressed
Const EVENT_MouseDown	= $201		; Mouse button pressed
Const EVENT_MouseUp		= $202		; Mouse button released
Const EVENT_MouseMove	= $203		; Mouse moved
Const EVENT_Gadget		= $401		; Gadget clicked
Const EVENT_Move		= $801		; Window moved
Const EVENT_Size		= $802		; Window resized
Const EVENT_Close		= $803		; Window closed
Const EVENT_Front		= $804		; Window brought to front
Const EVENT_Menu		= $1001		; Menu item selected
Const EVENT_LostFocus	= $2001		; App lost focus
Const EVENT_GotFocus	= $2002		; App got focus
Const EVENT_Timer		= $4001		; Timer event occurred


Type gfxTypes
	Field width
	Field height
	Field depth
	Field count
End Type

Type helptext
	Field linetext$
	Field page
	Field linenumber
End Type


; -----------------------------------------------------------------------------
; ...Main Window Setup...
; -----------------------------------------------------------------------------
; help window
Global helpwindow, canvasPreview, panelButtonHelp, buttonQuitHelp, buttonPreviousHelp, buttonNextHelp
; * * * info for main window in the function
Global mainwindow = CenterWindow ("Setup Screen", 390, 200, 0, 3) ; name, width, height, group, style
SetMinWindowSize mainwindow, 390, 200

;icon=ExtractIconA(QueryObject(mainwindow,1),"OWN ICON HERE.ico",0) ; used to display own icon if have the required userlibs in blitz
;SetClassLongA(QueryObject(mainwindow,1),-14,icon) ; thx to Grey Alien for that:)
; ******

; * * * CreateButton( text$,x,y,width,height,group[,style] )
Global panelButton = CreatePanel ( 260, 5, 114, 159, mainwindow, 0 )
Global buttonQuit  = CreateButton( "'Q'uit",   0, 0,  110, 35, panelButton , 1 )
Global buttonStart = CreateButton( "'S'tart",  0, 41, 110, 35, panelButton , 1 ) ; start game
Global buttonHelp  = CreateButton( "'H'elp",   0, 82, 110, 35, panelButton , 1 ) ; show instructions / help
Global buttonPrefs= CreateButton( "'P'references",   0, 123, 110, 35, panelButton , 1 ) ; 
SetGadgetLayout panelButton, 2, 2, 2, 2 : SetGadgetLayout buttonQuit, 2, 2, 2, 2 : SetGadgetLayout buttonStart, 2, 2, 2, 2
SetGadgetLayout buttonHelp, 2, 2, 2, 2  : SetGadgetLayout buttonPrefs, 2, 2, 2, 2
; ******

; * * * panel and label for full screen or windowed mode
Global panelRadio = CreatePanel ( 10, 5, 119, 120, mainwindow, 1 ) ; 0 = no border, 1 = sunken border
Global labelRadio =	CreateLabel ( "     Screen Options", 1, 0, 100, 15, panelRadio, 0 ) ; 0 = no border, 1 = border, 3 = sunken border
Global radioWind =	CreateButton( "Window Mode",         2, 22, 94, 20, panelRadio, 3 )
Global radioFull =	CreateButton( "Full Screen",         2, 53, 94, 20, panelRadio, 3 )
Global radioPsudo =	CreateButton( "Psuedo Full Screen",  2, 84, 114, 20, panelRadio, 3 )
SetGadgetLayout panelRadio, 2, 2, 2, 2 : SetGadgetLayout labelRadio, 2, 2, 2, 2 : SetGadgetLayout radioWind, 2, 2, 2, 2
SetGadgetLayout radioFull, 2, 2, 2, 2 : SetGadgetLayout radioPsudo, 2, 2, 2, 2 : SetButtonState radioWind, 1
; ******

; * * * panel and label for one or two players
Global panelRadio2 = 		CreatePanel ( 140, 5, 110, 120, mainwindow, 1 ) ; 0 = no border, 1 = sunken border
Global labelRadio2 =		CreateLabel ( "  Number of Players", 1, 0, 100, 15, panelRadio2, 0 ) ; 0 = no border, 1 = border, 3 = sunken border
Global radio2OnePlayer = 	CreateButton( "One Player",     	2, 22, 94, 20, panelRadio2, 3 )
Global radio2TwoPlayer =	CreateButton( "Two Players",		2, 53, 94, 20, panelRadio2, 3 )
Global radio2CompPlayer =	CreateButton( "Computer Player",	2, 84, 104, 20, panelRadio2, 3 )
SetGadgetLayout panelRadio2, 2, 2, 2, 2 : SetGadgetLayout labelRadio2, 2, 2, 2, 2 : SetGadgetLayout radio2OnePlayer, 2, 2, 2, 2
SetGadgetLayout radio2TwoPlayer, 2, 2, 2, 2 : SetGadgetLayout radio2CompPlayer, 2, 2, 2, 2 : SetButtonState radio2OnePlayer, 1
; ******

; * * * adds combo box then checks for graphics modes and adds them to combo box
Global comboGraphics = CreateComboBox( 10, 140, 240, 24, mainwindow )
GfxModesWindowed()	; adds the gfx modes to combo box
SetGadgetLayout comboGraphics, 2, 2, 2, 2 ; SelectGadgetItem comboGraphics, 0 - sets combo box To display specific entry - 0 is First entry - is set in specific function
; ******


; -----------------------------------------------------------------------------
; ...Disable the buttons not required...
; -----------------------------------------------------------------------------

If SETUP_Prefs = False Then DisableGadget buttonPrefs
If SETUP_TwoPlayer = False Then DisableGadget radio2TwoPlayer 
If SETUP_CompPlayer = False Then DisableGadget radio2CompPlayer 
If SETUP_Full = False Then DisableGadget radioFull 
If SETUP_Windowed = False Then DisableGadget radioWind 
If SETUP_Psudo = False Then DisableGadget radioPsudo
If SETUP_TwoPlayer = False And SETUP_CompPlayer = False Then DisableGadget radio2OnePlayer
If SETUP_Full = False And SETUP_Psudo = False Then DisableGadget radioWind 



;*************************************************************************************************************************************************************************
; * * * * * * * * *  M A I N  L O O P  * * * * * * * * * *
;*************************************************************************************************************************************************************************
Repeat
	Select WaitEvent(1)
		Case EVENT_Gadget	; gadget clicked $401
			Select EventSource() 
				Case buttonStart
					GameStart()
					Gosub freeallgadgets : EndGraphics
					FlushKeys : FlushEvents()
					Goto StartGameSetup
				Case buttonHelp
					HideGadget mainwindow 
					helpwindow = CenterWindow ("Help", 485, 514 + 60, 0, 17)
					ShowHelp() ; handles help window
					ShowGadget mainwindow
					ActivateGadget mainwindow
				Case buttonPrefs
					; nothing setup
					ActivateGadget mainwindow
				Case buttonQuit
					If Confirm( "Confirm Quit?" ) = True
						Gosub freeallgadgets : EndGraphics : End
					EndIf
					ActivateGadget mainwindow
				Case radioWind
					GfxModesWindowed()
					ActivateGadget mainwindow
				Case radioFull
					GfxModesFull()
					ActivateGadget mainwindow
				Case radioPsudo
					GfxModesPsudo()
					ActivateGadget mainwindow
				Case comboGraphics 
					ActivateGadget mainwindow
			End Select
		Case EVENT_Close	;window close
			Gosub freeallgadgets : EndGraphics : End
		Case EVENT_LostFocus	;suspend!
			While WaitEvent() <> EVENT_GotFocus
			Wend
			ActivateGadget mainwindow
		Case EVENT_KeyUp
			If EventData() = 16 ; quit
				If Confirm( "Confirm Quit?" ) = True
					Gosub freeallgadgets : EndGraphics : End
				EndIf
				ActivateGadget mainwindow
			EndIf
			If EventData() = 35 ; help
				HideGadget mainwindow 
				helpwindow = CenterWindow ("Help", 485, 514 + 60, 0, 17)
				ShowHelp() ; handles help window
				ShowGadget mainwindow
				ActivateGadget mainwindow
			EndIf
			If EventData() = 31 ; start
				GameStart()
				Gosub freeallgadgets : EndGraphics
				FlushKeys : FlushEvents()
				Goto StartGameSetup
			EndIf
			If EventData() = 25 ; preferences

			EndIf
	End Select
Forever

Gosub freeallgadgets : EndGraphics : End

;*************************************************************************************************************************************************************************
; * * * * * * * * * *  F U N C T I O N S  * * * * * * * * * *
;*************************************************************************************************************************************************************************
Function GameStart()
	mode1=SelectedGadgetItem(comboGraphics) + 1
	For mode.gfxTypes = Each gfxTypes
		If mode\count = mode1
			screenWidth = mode\width
			screenHeight = mode\height
			screenDepth = mode\depth
		EndIf
	Next
	If ( ButtonState (radioPsudo) = 1 ) 
		screenType = 3
	ElseIf ( ButtonState (radioWind) = 1 )
		screenType = 2
	ElseIf ( ButtonState (radioFull) = 1 )
		screenType = 1
	EndIf
	If ( ButtonState (radio2OnePlayer) = 1 )
		playerNumber = 1
	ElseIf ( ButtonState (radio2TwoPlayer) = 1 )
		playerNumber = 2
	ElseIf ( ButtonState (radio2CompPlayer) = 1 )
		playerNumber = 3
	EndIf
End Function

Function GfxModesPsudo()
	ClearGadgetItems(comboGraphics)
	For mode.gfxTypes = Each gfxTypes : Delete mode : Next
	count = 1
	For k1 = 1 To CountGfxModes() ; 640, 480 / 800, 600 / 1024, 768 / 1280, 1024 / 1600, 1200
		t1$ = GfxModeWidth(k1) + " width   x   " + GfxModeHeight(k1) + " height   x   " + GfxModeDepth(k1) + " depth"
;		If ( GfxModeWidth(k1) = 640 Or GfxModeWidth(k1) = 800 Or GfxModeWidth(k1) = 1024 Or GfxModeWidth(k1) = 1280 Or GfxModeWidth(k1) = 1600 ) And ( GfxModeHeight(k1) = 480 Or GfxModeHeight(k1) = 600 Or GfxModeHeight(k1) = 768 Or GfxModeHeight(k1) = 1024 Or GfxModeHeight(k1) = 1200 )
		If ( GfxModeWidth(k1) = 640 And GfxModeHeight(k1) = 480 ) Or ( GfxModeWidth(k1) = 800 And GfxModeHeight(k1) = 600 ) Or ( GfxModeWidth(k1) = 1024 And GfxModeHeight(k1) = 768 ) Or ( GfxModeWidth(k1) = 1280 And GfxModeHeight(k1) = 1024 ) Or ( GfxModeWidth(k1) = 1600 And GfxModeHeight(k1) = 1200 )
			If GraphicsWidth() = GfxModeWidth(k1) And GraphicsHeight() =  GfxModeHeight(k1) And GraphicsDepth() = GfxModeDepth(k1)
				AddGadgetItem comboGraphics, t1$
				mode.gfxTypes = New gfxTypes
				mode\width = GfxModeWidth(k1)
				mode\height = GfxModeHeight(k1)
				mode\depth = GfxModeDepth(k1)
				mode\count = count
				count = count + 1
				SelectGadgetItem comboGraphics, COMBO_position
			EndIf
		EndIf
	Next
	count = 0
	If CountGadgetItems(comboGraphics) = 0 Then SetButtonState radioPsudo, 0 : DisableGadget radioPsudo : DisableGadget buttonStart
	If CountGadgetItems(comboGraphics) > 0 Then EnableGadget buttonStart
End Function

Function GfxModesWindowed()
	ClearGadgetItems(comboGraphics)
	For mode.gfxTypes = Each gfxTypes : Delete mode : Next
	count = 1
	For k1 = 1 To CountGfxModes() ; 640, 480 / 800, 600 / 1024, 768 / 1280, 1024 / 1600, 1200
		t1$ = GfxModeWidth(k1) + " width   x   " + GfxModeHeight(k1) + " height   x   " + GfxModeDepth(k1) + " depth"
;		If ( GfxModeWidth(k1) = 640 Or GfxModeWidth(k1) = 800 Or GfxModeWidth(k1) = 1024 Or GfxModeWidth(k1) = 1280 Or GfxModeWidth(k1) = 1600 ) And ( GfxModeHeight(k1) = 480 Or GfxModeHeight(k1) = 600 Or GfxModeHeight(k1) = 768 Or GfxModeHeight(k1) = 1024 Or GfxModeHeight(k1) = 1200 )
		If ( GfxModeWidth(k1) = 640 And GfxModeHeight(k1) = 480 ) Or ( GfxModeWidth(k1) = 800 And GfxModeHeight(k1) = 600 ) Or ( GfxModeWidth(k1) = 1024 And GfxModeHeight(k1) = 768 ) Or ( GfxModeWidth(k1) = 1280 And GfxModeHeight(k1) = 1024 ) Or ( GfxModeWidth(k1) = 1600 And GfxModeHeight(k1) = 1200 )
			If GraphicsWidth() >= GfxModeWidth(k1) And GraphicsHeight() >=  GfxModeHeight(k1) And GraphicsDepth() >= GfxModeDepth(k1)
				AddGadgetItem comboGraphics, t1$
				mode.gfxTypes = New gfxTypes
				mode\width = GfxModeWidth(k1)
				mode\height = GfxModeHeight(k1)
				mode\depth = GfxModeDepth(k1)
				mode\count = count
				count = count + 1
				SelectGadgetItem comboGraphics, COMBO_position
			EndIf
		EndIf
	Next
	count = 0
	If CountGadgetItems(comboGraphics) = 0 Then DisableGadget radioWind : SetButtonState radioWind, 0 : DisableGadget buttonStart
	If CountGadgetItems(comboGraphics) > 0 Then EnableGadget buttonStart
End Function

Function GfxModesFull()
	ClearGadgetItems(comboGraphics)
	For mode.gfxTypes = Each gfxTypes : Delete mode : Next
	count = 1
	For k1 = 1 To CountGfxModes() ; 640, 480 / 800, 600 / 1024, 768 / 1280, 1024 / 1600, 1200
		t1$ = GfxModeWidth(k1) + " width   x   " + GfxModeHeight(k1) + " height   x   " + GfxModeDepth(k1) + " depth"
;		If ( GfxModeWidth(k1) = 640 Or GfxModeWidth(k1) = 800 Or GfxModeWidth(k1) = 1024 Or GfxModeWidth(k1) = 1280 Or GfxModeWidth(k1) = 1600 ) And ( GfxModeHeight(k1) = 480 Or GfxModeHeight(k1) = 600 Or GfxModeHeight(k1) = 768 Or GfxModeHeight(k1) = 1024 Or GfxModeHeight(k1) = 1200 )
		If ( GfxModeWidth(k1) = 640 And GfxModeHeight(k1) = 480 ) Or ( GfxModeWidth(k1) = 800 And GfxModeHeight(k1) = 600 ) Or ( GfxModeWidth(k1) = 1024 And GfxModeHeight(k1) = 768 ) Or ( GfxModeWidth(k1) = 1280 And GfxModeHeight(k1) = 1024 ) Or ( GfxModeWidth(k1) = 1600 And GfxModeHeight(k1) = 1200 )
			AddGadgetItem comboGraphics, t1$
			mode.gfxTypes = New gfxTypes
			mode\width = GfxModeWidth(k1)
			mode\height = GfxModeHeight(k1)
			mode\depth = GfxModeDepth(k1)
			mode\count = count
			count = count + 1
			SelectGadgetItem comboGraphics, COMBO_position
		EndIf
	Next
	count = 0
	If CountGadgetItems(comboGraphics) = 0 Then DisableGadget radioFull : SetButtonState radioFull, 0 : DisableGadget buttonStart
	If CountGadgetItems(comboGraphics) > 0 Then EnableGadget buttonStart
End Function



Function ShowHelp()
	;SETUP_Help - if true extra info GFX_HelpFrames - 15 lines of text
	canvasPreview = CreateCanvas (0, 0, ClientWidth (helpwindow), ClientHeight(helpwindow) - 40, helpwindow)
	panelButtonHelp = CreatePanel ( 5, ClientHeight (helpwindow) - 30, ClientWidth(helpwindow) - 10, 25, helpwindow, 0 )
	buttonQuitHelp  = CreateButton( "Exit Help",    200, 1,  60, 20, panelButtonHelp , 1 )
	buttonPreviousHelp = CreateButton( "Previous",  1, 1, 50, 20, panelButtonHelp , 1 )
	buttonNextHelp  = CreateButton( "Next",         415, 1, 50, 20, panelButtonHelp , 1 )
	SetBuffer CanvasBuffer (canvasPreview)
	ClsColor 255, 255, 255 : Cls  ; maskimage set to 254, 254, 254
	Color 0, 0, 0
	fntTimes30B = LoadFont("Times New Roman", 30, True, False, False) : If fntTimes30B = False Then RuntimeError "Can't locate the 'Font File'."
	fntTimes20 = LoadFont("Times New Roman", 20, False, False, False) : If fntTimes20 = False Then RuntimeError "Can't locate the 'Font File'."

	Restore anyhelpdata
	Read helpdata$
	count = 1 : page = 1
	While helpdata$ <> "END OF HELP DATA"
		If helpdata$ = "" RuntimeError "Insufficient Help Data Available"
		help.helptext = New helptext
			help\linetext$ 		= helpdata$
			help\page			= page
			help\linenumber		= count
		Read helpdata$
		count = count + 1
		If count > HELP_frame Then count = 1 : page = page + 1	; HELP_frame is how many lines per page
		If help\page = 1
			If help\linenumber = 1
				SetFont fntTimes30B : Text ClientWidth (helpwindow) / 2, 5 + help\linenumber * 18, help\linetext$, True, False
			Else
				SetFont fntTimes20 : Text 50, 25 + help\linenumber * 25, help\linetext$, False, False
			EndIf
		EndIf
	Wend
	HelpTotalFrames = help\page
	If HelpTotalFrames = 1
		DisableGadget buttonPreviousHelp : DisableGadget buttonNextHelp
	Else
		DisableGadget buttonPreviousHelp
	EndIf
	FlipCanvas canvasPreview
	maxpage = page : page = 1
	Repeat
		Select WaitEvent(1)
			Case EVENT_Gadget	; gadget clicked $401
				Select EventSource() 
					Case buttonQuitHelp
						Exit
					Case buttonPreviousHelp
						page = page - 1 : EnableGadget buttonNextHelp
						If  page = 1
							DisableGadget buttonPreviousHelp : EnableGadget buttonNextHelp : page = 1
						EndIf
						Cls
						For help.helptext = Each helptext
							If help\page = page
								If help\linenumber = 1 And HELP_title = True
									SetFont fntTimes30B : Text ClientWidth (helpwindow) / 2, 5 + help\linenumber * 18, help\linetext$, True, False
								Else
									SetFont fntTimes20 : Text 50, 25 + help\linenumber * 25, help\linetext$, False, False
								EndIf
							EndIf
						Next
						FlipCanvas canvasPreview
					Case buttonNextHelp
						page = page + 1 : EnableGadget buttonPreviousHelp
						If  page + 1 = maxpage 
							DisableGadget buttonNextHelp : EnableGadget buttonPreviousHelp
						EndIf
						Cls
						For help.helptext = Each helptext
							If help\page = page
								If help\linenumber = 1 And HELP_title = True
									SetFont fntTimes30B : Text ClientWidth (helpwindow) / 2, 5 + help\linenumber * 18, help\linetext$, True, False
								Else
									SetFont fntTimes20 : Text 50, 25 + help\linenumber * 25, help\linetext$, False, False
								EndIf
							EndIf
						Next
						FlipCanvas canvasPreview
				End Select
			Case EVENT_Close	;window close - returns to main window
				Exit
			Case EVENT_LostFocus	;suspend!
				While WaitEvent() <> EVENT_GotFocus
				Wend
		End Select
		If KeyHit(1) Then Exit
	Forever
	FreeGadget canvasPreview : FreeGadget buttonQuitHelp : FreeGadget buttonPreviousHelp : FreeGadget buttonNextHelp
	FreeGadget panelButtonHelp : FreeGadget helpwindow
	FreeFont fntTimes30B : 	FreeFont fntTimes20
End Function


Function CenterWindow (title$, width, height, group, style)
	;CreateWindow( title$,x,y,width,height[,group[,style]] )
	Return CreateWindow (title$, (ClientWidth (Desktop ()) / 2) - (width / 2), (ClientHeight (Desktop ()) / 2) - (height / 2), width, height, group, style)
End Function


;*************************************************************************************************************************************************************************
; * * * * * * * * * *  S U B - R O U T I N E S  * * * * * * * * * *
;*************************************************************************************************************************************************************************
; tidies up after option screen window
.freeallgadgets 
If gfxHelp = True Then FreeImage gfxHelp
FreeGadget buttonQuit : FreeGadget buttonStart : FreeGadget buttonHelp : FreeGadget buttonPrefs
FreeGadget comboGraphics : FreeGadget labelRadio : FreeGadget radioWind : FreeGadget radioFull : FreeGadget radioPsudo
FreeGadget labelRadio2 : FreeGadget radio2OnePlayer : FreeGadget radio2TwoPlayer : FreeGadget radio2CompPlayer


FreeGadget panelButton
FreeGadget panelRadio
FreeGadget panelRadio2


FreeGadget mainwindow

FreeFont fntTimes30B : FreeFont fntTimes20

Return

.anyhelpdata
; * * * help info page one * * *
Data "I N S T R U C T I O N S", "Select Windowed or Full Screen Mode.", "Psuedo Full Screen uses Desktop graphics settings,"
Data "but plays without the normal sized windows border.", "The three Screen Options will affect the Graphics"
Data "Modes available.", "Select the required Screen Resolution, ideally", "after the required Screen Option has been chosen."
Data "Number of Players when highlighted allows for One", "Player, Two Players or for One Player to compete", "against a Computer Opponent."
Data "Quit Button quits to Desktop.", "Start Button starts the Game with the requied settings.", "Help Button displays the instructions here, plus the"
Data "possible option to view further instructions or help", "if it'savailable.", "Preferences Button when highlighted allows for various"
Data "Game Customisation options."
; * * * help info page two * * *


Data "END OF HELP DATA" ; use this to denote no more data to be read, place at end of data
;.StartGameSetup
;End
