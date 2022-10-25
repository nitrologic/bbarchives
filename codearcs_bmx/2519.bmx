; ID: 2519
; Author: JoshK
; Date: 2009-07-02 21:06:04
; Title: Path Edit Gadget
; Description: Gadget for choosing file and folder paths

SuperStrict

Import MaxGui.Drivers
Import brl.retro

Local window:TGadget

window=CreateWindow("pathedit Example",40,40,320,240)

Local label:TGadget
Local pathedit:TPathEdit
Local x:Int=4,y:Int=4

label=CreateLabel("Pick File:",x,y,60,18,window)
SetGadgetLayout label,1,0,1,0
pathedit=CreatePathEdit(x+60,y,200,20,window,PATHEDIT_FILE|PATHEDIT_STRIPDIR)
SetGadgetLayout pathedit,1,1,1,0


While True
	WaitEvent 
	Print CurrentEvent.ToString()
	Select EventID()
		Case EVENT_WINDOWCLOSE
			End
	End Select
Wend

'-------------------------------------------------------------------

Const PATHEDIT_FILE:Int=0
Const PATHEDIT_DIR:Int=1
Const PATHEDIT_STRIPDIR:Int=2

Type TPathEdit Extends TProxygadget
	
	Field panel:TGadget
	Field textfield:TGadget
	Field button:TGadget
	Field style:Int
	Field extensions:String="All Files:*"
	
	Method Cleanup()
		RemoveHook(EmitEventHook,EventHook,Self)
		Super.cleanup()
	EndMethod
	
	Function Create:TPathEdit(x:Int,y:Int,width:Int,height:Int,group:TGadget,style:Int=0)
		Local pathedit:TPathEdit
		
		pathedit=New TPathEdit
		
		pathedit.panel=CreatePanel(x,y,width,height,group)
		pathedit.textfield=CreateTextField(0,0,pathedit.panel.ClientWidth()-pathedit.panel.ClientHeight(),pathedit.panel.ClientHeight(),pathedit.panel)
		SetGadgetLayout pathedit.textfield,1,1,1,1
		
		pathedit.style=style
		
		pathedit.button=CreateButton("...",pathedit.panel.ClientWidth()-pathedit.panel.ClientHeight(),0,pathedit.panel.ClientHeight(),pathedit.panel.ClientHeight(),pathedit.panel)		
		SetGadgetLayout pathedit.button,0,1,1,1
		
		pathedit.setproxy(pathedit.panel)
		
		AddHook(EmitEventHook,EventHook,pathedit)
		Return pathedit
	EndFunction
	
	Function EventHook:Object(id:Int,data:Object,context:Object)
		Local event:TEvent
		Local pathedit:TPathEdit
		
		event=TEvent(data)
		If event
			pathedit=TPathEdit(context)
			If pathedit
				Select event.id
				Case EVENT_GADGETLOSTFOCUS
					If event.source=pathedit.textfield
						EmitEvent CreateEvent(EVENT_GADGETACTION,pathedit,0,0,0,0,GadgetText(pathedit.textfield))
						Return Null
					EndIf				
				Case EVENT_GADGETACTION
					Select event.source
					Case pathedit.textfield
						Return Null
					Case pathedit.button
						Local file$
						If PATHEDIT_DIR & pathedit.style
							file=RequestDir("Select Folder",GadgetText(pathedit.textfield))
						Else
							file=RequestFile("Open File",pathedit.extensions,0,GadgetText(pathedit.textfield))
							If PATHEDIT_STRIPDIR & pathedit.style
								file=StripDir(file)
							EndIf
						EndIf
						If file
							SetGadgetText pathedit.textfield,file
							EmitEvent CreateEvent(EVENT_GADGETACTION,pathedit,0,0,0,0,file)
						EndIf
						Return Null
					EndSelect
				EndSelect
			EndIf
		EndIf
		Return data
	EndFunction
	
EndType

Rem
bbdoc:
EndRem
Function CreatePathEdit:TPathEdit(x:Int,y:Int,width:Int,height:Int,group:TGadget,flags:Int=0)
	Return Tpathedit.Create(x,y,width,height,group,flags)
EndFunction

Rem
bbdoc:
EndRem
Function SetPathEditExtensions(pathedit:TPathEdit,extensions:String)
	pathedit.extensions=extensions
EndFunction
