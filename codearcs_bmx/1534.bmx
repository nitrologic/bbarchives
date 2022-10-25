; ID: 1534
; Author: Beaker
; Date: 2005-11-16 10:13:21
; Title: [maxgui] Custom Image Button
; Description: Simple custom image button (with toggle/cycle and mouseover)

' Custom Image Button 
' ===================
' A custom "gadget" that can be used like web gadgets, 
' i.e. showing an alternate image on mouseover, and
' on single click cycling through a series of options.
'
' Originally created by Beaker
' Improved upon by Mark Tiffany

Strict 
Type TApplet 

	Method OnEvent(Event:TEvent) Abstract

	Method New()
		AddHook EmitEventHook,eventhook,Self
	End Method

	Function eventhook:Object(id,data:Object,context:Object)
		Local	event:TEvent
		Local	app:TApplet
		event=TEvent(data)
		If Not event Then Return
		app=TApplet(context)
		app.OnEvent event	
		Return data
	End Function

End Type


Type TImageButton Extends TApplet
	Field index
	Field lastind
	Field depressed
	Field mouseover

	Field pix:TPixmap[]
	Field rollpix:TPixmap[]
	Field pan:TGadget
		
	Method OnEvent(Event:TEvent)
		'make sure this was intended for us!
		If event.source<>pan Then Return
		
		Select event.id
			Case EVENT_MOUSEDOWN
				If event.data=1
					Local i:Int=(index + 1) Mod lastind
					' set the image to the new image or rollover image
					If rollpix[i]=Null Then
						SetPanelPixmap pan,pix[i]	
					Else
						SetPanelPixmap pan,rollpix[i]									
					End If
					depressed=True					
				EndIf
		
			Case EVENT_MOUSEUP
				If event.data=1 And depressed And mouseover Then
					index = (index + 1) Mod lastind
					' set the image to the new image or rollover image
					If rollpix[index]=Null Or event.x<0 Or event.x>pan.width Or event.y<0 Or event.y>pan.height Then
						SetPanelPixmap pan,pix[index]	
					Else
						SetPanelPixmap pan,rollpix[index]									
					End If
					
					'and emit an event for someone else to catch
					Local ev:TEvent = New TEvent.Create(EVENT_GADGETACTION,Self,index,event.mods,event.x,event.y)
					ev.Emit
				EndIf
				
			Case EVENT_MOUSEENTER
				If rollpix[index] <> Null
					SetPanelPixmap pan,rollpix[index]
				EndIf
				mouseover=True
				
			Case EVENT_MOUSELEAVE
				SetPanelPixmap pan,pix[index]				
				mouseover=False
				
		End Select
		' Print event.tostring() ' for debugging
	End Method

	Method Create:TImageButton(images$[],rollover$[],x,y,w,h,group:TGadget,style=0)
		index = 0
		lastind = images.length
		pix = New TPixmap[lastind]
		For Local f = 0 Until lastind
			pix[f] = LoadPixmap(images[f])
			' if load fails, then give up on creating the gadget
			If pix[f]=Null Then Return Null
		Next
		pan = CreatePanel(x,y,w,h,group,style|PANEL_ACTIVE)
		SetPanelPixmap pan,pix[0]	

		rollpix = New TPixmap[lastind]
		If rollover <> Null
			For Local f = 0 Until rollover.length
				rollpix[f] = LoadPixmap(rollover[f])
				' if load fails, then give up on creating the gadget
				If rollpix[f]=Null Then Return Null
			Next
		EndIf
		Return Self
	End Method
	
End Type

Function CreateImageButton:TImageButton(image$[],rollover$[],x,y,w,h,group:TGadget,style=0)
	Return New TImageButton.Create(image$,rollover$,x,y,w,h,group,style)
End Function

' create test GUI
Local window:TGadget = CreateWindow("My Window",40,40,320,240)

Local lbl1:TGadget = CreateLabel("This gadget cycles on single click through 3 values.",30,10,250,15,window)
Local butt1:TImageButton = CreateImageButton(["test1.png","test2.png","test3.png"],["","",""],30,30,28,28,window,PANEL_BORDER)

Local lbl2:TGadget = CreateLabel("This gadget is highlighted on mouse over.",30,60,250,15,window)
Local butt2:TImageButton = CreateImageButton(["test1.png","test2.png"],["test1h.png","test2h.png"],30,80,26,26,window)
Local txt2:TGadget = CreateTextField(100,80,60,30,window)
SetGadgetText txt2,0

' main loop
While True
	WaitEvent() 
	Select EventID()
		Case EVENT_WINDOWCLOSE
			End
		Case EVENT_GADGETACTION
			Select EventSource()
				Case butt2 ; SetGadgetText txt2,EventData()
			End Select
	End Select
Wend

End
