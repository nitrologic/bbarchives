; ID: 633
; Author: Wiebo
; Date: 2003-03-23 03:47:18
; Title: Foldable menus
; Description: 3dsMax style foldable menus

; foldable menus for B+ by Wiebo

; how to use: create a window, add a menu, add panels to menu, add your gadgets to subpanels

main = CreateWindow ("Fold me", 0,0, 400, 400)

; window, panel amount, [rightside]

menu.collapse = CreateMainMenu ( main, 3, FALSE )

; menu, ysize, index, state, title$, titlestringlength

AddPanelToMenu( menu, 200, 1, OPEN, " Menu 1 ", 50 )
AddPanelToMenu( menu, 100, 2, OPEN, " Menu 2 ", 50 )
AddPanelToMenu( menu, 100, 3, OPEN, " Menu 3 ", 50 )

a = CreateButton( "Button 1", 25,25,50,25, menu\subpanel[1],0)
SetGadgetLayout a, 1,0,1,0

a = CreateButton( "Button 2", 25,25,50,25, menu\subpanel[2],0)
SetGadgetLayout a, 1,0,1,0

a = CreateButton( "Button 3", 25,25,50,25, menu\subpanel[3],0)
SetGadgetLayout a, 1,0,1,0

RedrawMenu( menu )

Repeat

	id = WaitEvent()

	Select id
		Case $201	; mouse down

			Select EventSource()

				Case menu\panel[1]
					menu\state[1]  = Not menu\state[1]
					RedrawMenu( menu )

				Case menu\panel[2]
					menu\state[2]  = Not menu\state[2]
					RedrawMenu( menu )

				Case menu\panel[3]
					menu\state[3]  = Not menu\state[3]
					RedrawMenu( menu )

			End Select

		Case $101

			Select EventData()

				Case 1
					End

			End Select


	End Select

Forever

; -------------------------------------------------------

Const OPEN = 0
Const CLOSED = 1
Const MENUWIDTH = 198;214
Const MAXPANELS = 10

Type Collapse
	Field MainPanel				; main panel to attach subpanels to goes here
	Field MainWidth				; width of main panel
	Field MainSlider			; slider attached to main panel
	Field Rightside	; true or false. false means panel is drawn at left side of window
	Field MainParent			; parent window
	Field PanelAmount			; amount of actiual panels in menu
	Field Panel[MAXPANELS]			; resizeable panels
	Field SubPanel[MAXPANELS]		; add your GADGETS to this panel
	Field Label[MAXPANELS]			; name to display
	Field State[MAXPANELS]			; open or closed
	Field Ysize[MAXPANELS]			; size when opened
End Type

; -------------------------------------------

Function CreateMainMenu.collapse( window, amount, rightside = TRUE )

	If amount > 0 And amount <= MAXPANELS

		; attach a menu bar to the correct side of a window

		collapse.collapse = New collapse

		collapse\MainParent = window
		collapse\MainWidth = MENUWIDTH
		collapse\PanelAmount = amount
		collapse\Rightside = rightside

		collapse\MainPanel = CreatePanel ( 0, 0, MENUWIDTH+16, 10, window )

		; determine position

		If rightside = TRUE

			SetGadgetLayout collapse\MainPanel, 0,1,1,0

			collapse\MainSlider = CreateSlider ( ClientWidth( window )-16, 0, 16, ClientHeight( window ), window, 2 )
			SetGadgetLayout collapse\MainSlider, 0,1,1,1
		Else
			SetGadgetLayout collapse\MainPanel, 1,0,1,0

			collapse\MainSlider = CreateSlider ( 0, 0, 16, ClientHeight( window ), window, 2 )
			SetGadgetLayout collapse\MainSlider, 1,0,1,1
		EndIf

;		SetPanelColor collapse\MainPanel, 255,0,255  ; indication of main panel

		Return collapse
	Else
		RuntimeError "Use index 1 to " + MAXPANELS + "!"
	EndIf

End Function


Function AddPanelToMenu( c.collapse, height, index, state, name$, length )

	If index > 0 And index <= c\panelamount

		c\Panel[index] = CreatePanel ( 0, 0, c\MainWidth, 10, c\MainPanel, 1 )
		SetGadgetLayout c\Panel[index], 1,1,1,1
		SetPanelColor c\panel[index], 190,190,190	; click on this panel to open or close menu

		c\SubPanel[index] = CreatePanel ( 0, 10, c\MainWidth, height, c\Panel[index],0 )
		SetGadgetLayout c\SubPanel[index], 1,1,1,1

		c\Ysize[index] = height + 8
		c\State[index] = state

		If state = CLOSED Then HideGadget c\SubPanel[index]

		c\Label[index] = CreateLabel ( name$, 24, 0, length, 15, c\MainPanel, 2 )
		SetGadgetLayout c\Label[index], 1,0,1,0
	Else
		RuntimeError "Use index 1 to " + c\panelamount + "!"
	EndIf

End Function


Function RedrawMenu( c.collapse )

	; get vertical size of menu and adjust main panel

	ysize = 8

	For count = 1 To c\panelamount
		If c\state[count] = CLOSED
			ysize = ysize + 10
		Else
			ysize = ysize + c\Ysize[count]
		EndIf

		ysize = ysize + 16
	Next

	If c\rightside = TRUE
		SetGadgetShape c\MainPanel, ClientWidth( c\mainparent )-32-MENUWIDTH, 0, MENUWIDTH+16, ysize
	Else
		SetGadgetShape c\MainPanel, 16, 0, MENUWIDTH+16, ysize
	EndIf

	; organise panel vertical positions

	Ypos = 8

	For index = 1 To c\panelamount

		If c\State[index] = CLOSED
			ysize = 10
		Else
			ysize = c\Ysize[index]
		EndIf

		; move panels and labels

		SetGadgetShape c\Panel[index], 8, Ypos, GadgetWidth( c\Panel[index] ), ysize
		SetGadgetShape c\Label[index], 16, Ypos - 8, GadgetWidth( c\Label[index] ), GadgetHeight( c\Label[index] )

		Ypos = Ypos + ysize + 16
	Next

	; adjust slider

	SetSliderRange c\MainSlider, ClientHeight(c\MainParent), Ypos

End Function
