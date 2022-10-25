; ID: 2488
; Author: JoshK
; Date: 2009-05-24 18:44:08
; Title: Color picker proxy gadget
; Description: Self-contained color picker proxy gadget

SuperStrict 

Import MaxGui.Drivers

AddHook(EmitEventHook,TColorPicker.EventHandler,Null,1)

Type TColorPicker Extends TProxyGadget
	
	Global list:TList=New TList
	
	Field panel:TGadget
	Field textfield:TGadget
	Field button:TGadget
	Field r:Int,g:Int,b:Int
	Field link:TLink
	
	Method CleanUp()
		link.remove()
		Super.CleanUp()
	EndMethod
	
	Function Create:TColorPicker(x:Int,y:Int,width:Int,height:Int,group:TGadget)
		Local colorpicker:TColorPicker=New TColorPicker
		colorpicker.panel=CreatePanel(x,y,width,height,group)
		colorpicker.setproxy(colorpicker.panel)
		colorpicker.textfield=CreateTextField(0,0,colorpicker.panel.ClientWidth()-colorpicker.panel.ClientHeight(),colorpicker.panel.ClientHeight(),colorpicker.panel)
		colorpicker.button=CreateButton("",colorpicker.panel.ClientWidth()-colorpicker.panel.ClientHeight(),0,colorpicker.panel.ClientHeight(),colorpicker.panel.ClientHeight(),colorpicker.panel)
		SetGadgetLayout colorpicker.textfield,1,1,1,1
		SetGadgetLayout colorpicker.button,0,1,1,1
		SetGadgetText colorpicker.textfield,"255,255,255"
		SetGadgetColor colorpicker.button,255,255,255
		SetGadgetFilter colorpicker.textfield,filter
		colorpicker.link=list.addlast(colorpicker)
		Return colorpicker
	EndFunction
	
	Function Filter:Int(event:TEvent,context:Object)
		If event.ID=EVENT_KEYCHAR
			If event.data=KEY_BACKSPACE Return 1
			If event.data=KEY_COMMA Return 1
			If event.data<48 Or event.data>57 Return 0
		EndIf
		Return 1
	EndFunction
	
	Method SetColor(r:Int,g:Int,b:Int)
		Self.r=r
		Self.g=g
		Self.b=b
		SetGadgetText textfield,r+","+g+","+b
		SetGadgetColor button,r,g,b
	EndMethod
	
	Method EventHook:TEvent(event:TEvent)
		If event.id=EVENT_GADGETLOSTFOCUS And event.source=textfield
			Local sarr:String[]=GadgetText(textfield).split(",")
			If sarr.length=3
				SetColor Int(sarr[0]),Int(sarr[1]),Int(sarr[2])
			Else
				SetColor r,g,b
			EndIf
			Return CreateEvent(EVENT_GADGETACTION,Self,b+(g Shl 8)+(r Shl 16)+(255 Shl 24))
		EndIf
		If event.id=EVENT_GADGETACTION And event.source=button
			If RequestColor(r,g,b)
				SetColor(RequestedRed(),RequestedGreen(),RequestedBlue())
				Return CreateEvent(EVENT_GADGETACTION,Self,b+(g Shl 8)+(r Shl 16)+(255 Shl 24))
			Else
				Return Null
			EndIf
		EndIf
		Return null
	EndMethod
	
	Function EventHandler:Object(id:Int,data:Object,context:Object)
		If list.isempty() Return data
		Local event:TEvent
		Local colorpicker:TColorPicker
		event=TEvent(data)
		If event
			For colorpicker=EachIn list
				If colorpicker.button=event.source Or colorpicker.textfield=event.source
					Return colorpicker.EventHook(event)
				EndIf
			Next
		EndIf
		Return data
	EndFunction
	
EndType

Function CreateColorPicker:TColorPicker(x:Int,y:Int,width:Int,height:Int,group:TGadget)
	Return TColorPicker.Create(x,y,width,height,group)
EndFunction


'----------------------------------------------------------------------------------------------------

Local window:TGadget

window=CreateWindow("My Window",40,40,320,240)

Local cp:TGadget=CreateColorPicker(20,20,200,20,window)
SetGadgetLayout cp,1,1,1,0
SetGadgetColor cp,255,0,0


While True
	WaitEvent 
	Print CurrentEvent.ToString()
	Select EventID()
		Case EVENT_WINDOWCLOSE
			End
	End Select
Wend
