; ID: 2179
; Author: Filax
; Date: 2008-01-08 09:43:07
; Title: MaxGUI Template
; Description: This template allow the user to make maxgui apps :)

' -----------------------------------------------
' Max GUI template by  Filax, u se as you want :)
' -----------------------------------------------
SuperStrict

Local w:Int=400
Local h:Int=400
Local x:Int=(GadgetWidth(Desktop())-w)/2
Local y:Int=(GadgetHeight(Desktop())-h)/2

Global MyWindow:TGadget=CreateWindow("EventHook Example", x,y,w,h,Null,WINDOW_TITLEBAR|WINDOW_ACCEPTFILES)
Global MyCanvas:TGadget=CreateCanvas(0,0,380,360,MyWindow)
SetGadgetAlpha( MyCanvas,0.5 )


Global Button:TGadget=CreateButton("Test",10,10,80,20,MyCanvas,BUTTON_PUSH)
Global Listbox:TGadget=CreateListBox(10,50,100,100,MyCanvas)
AddGadgetItem(Listbox,"TRTERTER")

Global SmallWindow:TGadget=CreateWindow("Move Me", x+125, y+150, 150, 100, MyWindow, WINDOW_TITLEBAR)

AddHook EmitEventHook,UpdateHook
CreateTimer(60) ' Refresh the graphics to 60 frames per seconds

' ---------
' Main loop
' ---------
Repeat
	UpdateApp()
Forever

' --------------------
' App refresh function
' --------------------
Function UpdateApp()
  WaitEvent()
	
  Select EventID()
	Case EVENT_TIMERTICK
	UpdateGraphics()
   End Select
End Function

' ------------------
' Gadget events hook
' ------------------
Function UpdateHook:Object(iId:Int,tData:Object,tContext:Object)
	Local Event:TEvent=TEvent(tData)

	Select Event.ID
	Case EVENT_APPSUSPEND
		Print "APP SUSPEND"

	Case EVENT_APPRESUME
		Print "APP RESUME"
		
	Case EVENT_WINDOWMOVE
		Print "WIN MOVE"
		
	Case EVENT_MENUACTION
		Print "MENU ACTION"
		
	Case EVENT_WINDOWACCEPT
		Print "DRAG N DROP"
				
	Case EVENT_WINDOWCLOSE
		End
		
	Case EVENT_GADGETPAINT
		UpdateGraphics()
				
	Case EVENT_GADGETACTION
		Select Event.Source
		Case Button
			Notify "Yearrr"
		Case Listbox
			Notify "Listbox double click"
		EndSelect
		
	Case EVENT_GADGETSELECT
		Select Event.Source
		Case Listbox
			Notify "Listbox single click"
		EndSelect
					
	EndSelect
	
	Return tData
End Function

' -------------------------
' Update graphics if needed
' -------------------------
Function UpdateGraphics:Int()
	SetGraphics CanvasGraphics(MyCanvas)
	Cls
	SetColor Rnd(255),Rnd(255),Rnd(255)
	DrawRect 0,0,GraphicsWidth(),GraphicsHeight()
	Flip False
End Function
