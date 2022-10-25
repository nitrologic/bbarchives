; ID: 2570
; Author: JoshK
; Date: 2009-08-25 18:05:58
; Title: Search Tool
; Description: Windows search tool that actually works

Framework maxgui.drivers
Import brl.eventqueue
Import "search.bmx"
Import "PathEdit.bmx"

AppTitle="Search Tool"

Global window:TGadget=CreateWindow(AppTitle,0,0,600,400,,WINDOW_TITLEBAR|WINDOW_STATUS|WINDOW_RESIZABLE|WINDOW_CENTER)

Local panel:TGadget=CreatePanel(0,0,182,400,window)
SetGadgetLayout panel,1,0,1,0

Local x:Int=2
Local y:Int=2

Global Gadget_Token:TGadget
Global Gadget_Folder:TGadget
Global Gadget_Extensions:TGadget
Global Gadget_Search:TGadget
Global Gadget_Results:TGadget
Global Gadget_Recursive:TGadget
Global Gadget_Contents:TGadget
Global Gadget_CaseSensitive:TGadget

CreateLabel("Search for:",x,y,120,16,panel)
y:+16
Gadget_Token=CreateTextField(x,y,180,18,panel)
y:+22
ActivateGadget Gadget_Token

CreateLabel("File extensions:",x,y,180,16,panel)
y:+16
Gadget_Extensions=CreateTextField(x,y,180,18,panel)
y:+22

CreateLabel("In folder:",x,y,180,16,panel)
y:+16
Gadget_Folder=CreatePathEdit(x,y,180,18,panel,PATHEDIT_DIR|PATHEDIT_READONLY)

y:+22
Gadget_Recursive=CreateButton("Search subfolders",x,y,180,18,panel,BUTTON_CHECKBOX)
SetButtonState Gadget_Recursive,1

y:+22
Gadget_Contents=CreateButton("Search file contents",x,y,180,18,panel,BUTTON_CHECKBOX)

y:+22
Gadget_CaseSensitive=CreateButton("Case-sensitive",x,y,180,18,panel,BUTTON_CHECKBOX)

y:+22
Gadget_Search=CreateButton("Search",x,y,180,26,panel,BUTTON_OK)

y=2
Gadget_Results=CreateListBox(x+180,y,window.ClientWidth()-x-(x+180),window.ClientHeight()-2*y,window)
SetGadgetLayout Gadget_Results,1,1,1,1

'SearchFolder:String[](path:String,token:String,extensions:String[]=Null,options:Int=SEARCH_RECURSIVE,results:String[]=Null)

Global popupmenu:TGadget=CreateMenu("",0,Null)
CreateMenu("Open file",0,popupmenu)
CreateMenu("Open containing folder",0,popupmenu)

Repeat

	WaitEvent()
	Select EventID()
	Case EVENT_WINDOWCLOSE
		End

	Case EVENT_MENUACTION
		Select String(GadgetText(TGadget(EventSource())))
		Case "Open file"
			OpenURL String(EventExtra())
		Case "Open containing folder"
			OpenURL ExtractDir(String(EventExtra()))
		EndSelect
		
	Case EVENT_GADGETMENU
		Select EventSource()
		Case Gadget_Results
			PopupWindowMenu window,popupmenu,GadgetItemExtra(Gadget_results,EventData())
			
		EndSelect
		
	Case EVENT_GADGETACTION
		Select EventSource()

		Case Gadget_Results
			OpenURL String(GadgetItemExtra(Gadget_results,EventData()))
			
		Case Gadget_Search
			Local results:String[]
			Local path:String
			Local token:String
			Local extensions:String[]
			Local options:Int
			Local n:Int
			
			ClearGadgetItems(Gadget_Results)
			
			If ButtonState(Gadget_Recursive) options=options|SEARCH_RECURSIVE
			If ButtonState(Gadget_Contents) options=options|SEARCH_CONTENTS
			If ButtonState(Gadget_CaseSensitive) options=options|SEARCH_CASESENSITIVE
			
			path=GadgetText(Gadget_Folder)
			
			token=GadgetText(Gadget_Token)
			
			extensions=GadgetText(Gadget_Extensions).split(",")
			For n=0 To extensions.length-1
				extensions[n]=extensions[n].Trim()
			Next
			If extensions.length=1
				If extensions[0]="" extensions=Null
			EndIf
			
			SetStatusText window,"Searching..."
			results=SearchFolder(path,token,extensions,options)
			SetStatusText window,results.length+" files found"
			
			
			For n=0 To results.length-1
				AddGadgetItem Gadget_Results,StripDir(results[n]),0,-1,results[n],results[n]
			Next
			
			ActivateGadget Gadget_Results
			
		EndSelect
	EndSelect

Forever
