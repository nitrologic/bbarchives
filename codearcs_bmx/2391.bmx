; ID: 2391
; Author: JoshK
; Date: 2009-01-03 22:59:34
; Title: GUI Application Template
; Description: Template to get a GUI application started, the right way

SuperStrict

Framework maxgui.win32maxgui
Import brl.eventqueue

AppTitle=StripAll(AppFile)

Global MainWindow:TGadget

MainWindow=CreateWindow(AppTitle,100,100,640,480,Null,WINDOW_TITLEBAR|WINDOW_STATUS|WINDOW_MENU|WINDOW_CLIENTCOORDS)

Local root:TGadget
Local menu:TGadget

root=WindowMenu(MainWindow)
menu=CreateMenu("File",0,root)
CreateMenu("Open",0,menu,KEY_O,MODIFIER_COMMAND)
CreateMenu("Save",0,menu)
CreateMenu("",0,menu)
CreateMenu("Exit",0,menu)
UpdateWindowMenu(MainWindow)

AddHook EmitEventHook,MainHook
AddHook EmitEventHook,MenuHook

Repeat; WaitEvent() ; Forever

Function MainHook:Object(id:Int,data:Object,context:Object)
	If data=Null Return Null
	Local event:TEvent=TEvent(data)
	Select event.id
		Case EVENT_WINDOWCLOSE
			Select event.source
				Case MainWindow
					CloseProgram()
			EndSelect
	End Select
	Return data
EndFunction

Function MenuHook:Object(id:Int,data:Object,context:Object)
	If data=Null Return Null
	Local event:TEvent=TEvent(data)
	Select event.id
		Case EVENT_MENUACTION
			Select GadgetText(TGadget(event.source))
				Case "Exit"
					CloseProgram()
			EndSelect
	End Select
	Return data
EndFunction

Function CloseProgram()
	Select Proceed("Are you sure you want to quit?")
		Case 1
			End
		Case 0,- 1
			Return
	EndSelect
EndFunction
