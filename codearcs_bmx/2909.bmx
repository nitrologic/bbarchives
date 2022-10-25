; ID: 2909
; Author: JoshK
; Date: 2012-01-10 11:45:43
; Title: CheckBox
; Description: CheckBox proxy gadget with automatic True/False label

SuperStrict

Import maxgui.drivers

Type TCheckBox Extends TProxyGadget
	
	Field button:TGadget
	
	Method Cleanup()
		RemoveHook(EmitEventHook,EventHook,Self)
		Super.cleanup()
	EndMethod	
	
	Method UpdateText(state:Int)
		If state
			SetGadgetText button,"True"
		Else
			SetGadgetText button,"False"
		EndIf
	EndMethod
	
	Function EventHook:Object(id:Int,data:Object,context:Object)
		Local event:TEvent
		Local checkbox:TCheckBox
		
		event=TEvent(data)
		If event
			Select event.id			
			Case EVENT_GADGETACTION		
				checkbox=TCheckBox(context)
				If checkbox
					If event.source=checkbox
						checkbox.UpdateText event.data
					EndIf
				EndIf
			EndSelect
		EndIf
		Return data
	EndFunction	
	
	Method SetSelected(state:Int)
		button.SetSelected state
		UpdateText state
	EndMethod
	
	Function Create:TCheckBox(x:Int,y:Int,width:Int,height:Int,group:TGadget,style:Int=0)
		Local checkbox:TCheckBox=New TCheckBox
		checkbox.button=CreateButton("False",x,y,width,height,group,BUTTON_CHECKBOX)
		checkbox.SetProxy checkbox.button
		AddHook EmitEventHook,EventHook,checkbox
		Return checkbox
	EndFunction
	
EndType

Function CreateCheckBox:TCheckBox(x:Int,y:Int,width:Int,height:Int,group:TGadget,style:Int=0)
	Return TCheckBox.Create(x,y,width,height,group,style)
EndFunction


'Example
Rem
Global window:TGadget = CreateWindow("MaxGUI Buttons",40,40,400,330,Null,WINDOW_TITLEBAR|WINDOW_CLIENTCOORDS)
Local checkbox:TGadget = CreateCheckBox(20,20,60,22,window)
SetButtonState checkbox,True

Repeat
	Select WaitEvent()
		Case EVENT_WINDOWCLOSE, EVENT_APPTERMINATE
			End
		Case EVENT_GADGETACTION
			Print "EVENT_GADGETACTION~n" + ..
			"GadgetText(): ~q" + GadgetText(TGadget(EventSource())) + "~q ~t " + ..
			"ButtonState(): "+ ButtonState(TGadget(EventSource()))
	EndSelect
Forever
EndRem
