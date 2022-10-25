; ID: 1610
; Author: REDi
; Date: 2006-02-07 16:42:31
; Title: MAXGUI - ScrollArea
; Description: A Fake ScrollArea TGadget

' FAKE SCROLLAREA TGADGET BY CLIFF

Rem
bbdoc: Create a ScrollArea gadget.
about:
A ScrollArea generates an EVENT_GADGETACTION #brl.event.Event whenever it is changed.<p>
You can't create gadgets using this object as a parent.
To add child gadgets to the ScrollArea you must call #GetScrollAreaClient and use the returned TGadget as the parent.
<p>
See Also:  #SetScrollAreaSize and #GetScrollAreaClient.
EndRem
Function CreateScrollArea:TGadget(x,y,w,h,group:TGadget)
	Local This:TScrollArea = New TScrollArea
	This.innerwidth = w-15
	This.innerheight= h-15
	This.Parent = Group
	This.Group = Group
	This.Create()
	This.SetShape(x,y,w,h)
	This.Update
	Return This
EndFunction

Rem
bbdoc: Set the size of the inner client panel of a ScrollArea gadget.
See Also: #CreateScrollArea and #GetScrollAreaClient.
EndRem
Function SetScrollAreaSize(ScrollArea:TGadget,width,height)
	Local Gadget:TScrollArea = TScrollArea(ScrollArea)
	If Gadget
		Gadget.innerwidth = width
		Gadget.innerheight= height
		Gadget.Update
	EndIf
EndFunction

Rem
bbdoc: Return the TGadget object of the inner client panel
about:
Use the returned TGadget object to add child gadgets to.<p>
<p>
See Also: #CreateScrollArea and #GetScrollAreaClient.
EndRem
Function GetScrollAreaClient:TGadget(ScrollArea:TGadget)
	Local Gadget:TScrollArea = TScrollArea(ScrollArea)
	If Gadget
		Return Gadget.ClientPanel
	EndIf
EndFunction

' ---------------------------------------------------------------------------------------

Type TScrollArea Extends TGadget

	Field group:TGadget
	Field backpanel:TGadget
	Field vslider:TGadget
	Field hslider:TGadget
	Field Panel:TGadget
	Field ClientPanel:TGadget
	Field innerwidth
	Field innerheight

	Method Create()
		backpanel:TGadget = CreatePanel(xpos,ypos,width,height,group)
		VSlider = CreateSlider(width-15,0,15,height-15,backpanel)
		VSlider.SetLayout(0,1,1,1)
		HSlider = CreateSlider(0,height-15,width-15,15,backpanel,SLIDER_HORIZONTAL)
		HSlider.SetLayout(1,1,0,1)
		Panel = CreatePanel(0,0,width-15,height-15,backpanel,Panel_Border)
		Panel.SetLayout(1,1,1,1)
		ClientPanel = CreatePanel(0,0,innerwidth,innerheight,Panel)
		ClientPanel.SetLayout(1,0,1,0)
		AddHook EmitEventHook,eventhook,Self
	EndMethod
	
	Method Free()
		RemoveHook EmitEventHook,eventhook
		FreeGadget(backpanel)
	EndMethod
	
	Method SetLayout(Left,Right,Top,Bottom)
		backpanel.SetLayout(Left,Right,Top,Bottom)
	EndMethod
	
	Method SetShape(x,y,w,h)
		backpanel.SetShape(x,y,w,h)
	EndMethod

	Method SetEnabled(bool)
		ClientPanel.SetEnabled(bool)
		VSlider.SetEnabled(bool)
		HSlider.SetEnabled(bool)
	End Method

	Method SetColor(r,g,b)
		Panel.SetColor(r,g,b)
		ClientPanel.SetColor(r,g,b)
	End Method

	Method Update()
		Local x,y, cw = .ClientWidth(Panel), ch = .ClientHeight(Panel)
		SetSliderRange(HSlider,cw,innerwidth) ; SetSliderRange(VSlider,ch,innerheight)
		If innerwidth<cw Then x=(cw-innerwidth)/2 Else x=-SliderValue(HSlider)
		If innerheight<ch Then y=(ch-innerheight)/2 Else y=-SliderValue(VSlider)
		ClientPanel.SetShape(x,y,innerwidth,innerheight)
	EndMethod

	' ----------------------------------------------------------------------------------
	
	Function eventhook:Object(id,data:Object,context:Object)
		If TScrollArea(context) Then TScrollArea(context).OnEvent TEvent(data)
		Return data	
	EndFunction
	
	Method OnEvent(event:TEvent)
		Select event.id
			Case EVENT_WINDOWSIZE
				Update
				
			Case EVENT_GADGETACTION
				Select Event.Source
					Case HSlider
						Update
					Case VSlider
						Update
				EndSelect
		EndSelect
	EndMethod

EndType
