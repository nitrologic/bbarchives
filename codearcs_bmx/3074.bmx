; ID: 3074
; Author: col
; Date: 2013-08-29 03:56:35
; Title: Word wrap
; Description: Turn word wrap on and off for MaxGUI - Windows

Strict

Import MaxGUI.Drivers

Global Window:TGadget = CreateWindow("Word wrap",200,0,500,500)
Global Text:TGadget = CreateTextArea(0,0,ClientWidth(Window),ClientHeight(Window)-60,Window)

' Some text
SetGadgetText Text,"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Nulla eget mauris quis dolor "+..
"ullamcorper dapibus. Duis facilisis ullamcorper metus. Pellentesque eget enim. Vivamus auctor hendrerit turpis. " + ..
"Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Vivamus tincidunt leo quis urna."

Global WordWrapChoice:TGadget = CreateButton("Word wrap",10,GadgetHeight(Text)+10,100,30,Window,BUTTON_CHECKBOX)

Repeat
	WaitEvent
	
	Select EventSource()
		Case Window
			If EventID() = EVENT_WINDOWCLOSE End
			
		Case WordWrapChoice
			Local hWnd = QueryGadget(Text,QUERY_HWND)
			
			Select ButtonState(WordWrapChoice)
				Case True
					SendMessageW(hWnd,EM_SETTARGETDEVICE,0,0)
					
				Case False
					SendMessageW(hWnd,EM_SETTARGETDEVICE,0,1)
			EndSelect
	EndSelect
Forever
