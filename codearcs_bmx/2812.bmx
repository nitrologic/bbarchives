; ID: 2812
; Author: Galaxy613
; Date: 2011-01-18 11:48:22
; Title: RequestText
; Description: A simple small window that lets users input just a line of text.

Import maxgui.Drivers
Import maxgui.proxyGadgets
?Win32
Import maxgui.win32maxguiex
?MacOS
Import maxgui.CocoaMaxGui
?Linux
Import MaxGui.FLTKMaxGui
?

Function RequestText$(titleText$, questionText$, defaultTxt$="", doesGameHideMouse = True)
	Local rWindow:TGadget = CreateWindow(titleText,64,64,350,125,Null,WINDOW_TITLEBAR|WINDOW_CENTER)
	CreateLabel(questionText,8,8,325,16,rWindow)
	Local rTextfield:TGadget = CreateTextField(8,8+24,325,24,rWindow)
	Local rOkButton:TGadget = CreateButton("Ok",325-200-8,32+32,100,24,rWindow)
	Local rCancelButton:TGadget = CreateButton("Cancel",325-100,32+32,100,24,rWindow)
	
	rTextfield.SetText defaultTxt
	
	ActivateWindow rWindow
	If doesGameHideMouse Then ShowMouse
	
	While Not AppTerminate( ) 
	    WaitEvent()
			
		Select CurrentEvent.id
			Case EVENT_WINDOWCLOSE, EVENT_APPTERMINATE
				rTextfield.SetText defaultTxt
				Exit
			Case EVENT_GADGETACTION
				If CurrentEvent.source = rOkButton Then
					Exit
				ElseIf CurrentEvent.source = rCancelButton
					rTextfield.SetText defaultTxt
					Exit
				End If
		End Select
	Wend
	
	Local returnText$ = rTextfield.GetText()
	
	HideGadget rWindow
	FreeGadget rWindow
	If doesGameHideMouse Then HideMouse
	
	Return returnText
End Function
