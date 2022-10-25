; ID: 1633
; Author: CS_TBL
; Date: 2006-03-05 07:59:14
; Title: TVUmeter
; Description: (MaxGUI) actions in VUmeter fashion

'SuperStrict

'---------------------------------------------------------------------------
Type TVUmeter

Rem

        .------------------------------.
	| TVumeter v1.0 (BMaxGui 1.14) |
	`------------------------------'
	
	about: VUmeter gadget, displays triggers in VUmeter fashion at 60 FPS updates
	
	by   : CS_TBL
	
	usage: Local MyVU:TVUmeter=CreateVUmeter(x,y,width,height,parent,canvasstyle,vustyle)
	
		       vustyle: 0: vertical
		                1: horizontal
		                2: vertical + peak
		                3: horizontal + peak
			
		   MyVU.trigger value#
		       value: 0..1
		   
		   MyVU.SetBackcolor r,g,b
		
		   MyVU.SetFrontcolor r,g,b
		
		   MyVU.SetDecay value
		       value: in pixels
	
		   MyVU.SetAttack value
		       value: in pixels
	
EndRem

	Field timer:TTimer
	Field canvas:TGadget
	Field backR:Byte=32
	Field backG:Byte=32
	Field backB:Byte=128
	Field frontR:Byte=64
	Field frontG:Byte=64
	Field frontB:Byte=255
	Field attack:Byte=32
	Field decay:Byte=4
	Field range:Short
	Field vustyle:Byte=0
	Field value:Int=0
	Field newvalue:Int=0
	Field move:Byte=0 ' 0: decay, 1:attack
	Field peak:Byte=0 ' 0: no peak, 1: peak
	Field showpeak:Byte

	Field NewEvent:TEvent=New TEvent
	
	Function eventhook:Object(id:Int,data:Object,context:Object)
		If TVUmeter(context) TVUmeter(context).ev TEvent(data);Return data	
	EndFunction
	
	Method New()
		AddHook EmitEventHook,eventhook,Self
	End Method
	
	Method Free()
		RemoveHook EmitEventHook,eventhook
		timer=Null
		canvas=Null
		GCCollect()
	End Method
	
	Method ev(event:TEvent)
		If Event.source=canvas
			If Event.id=EVENT_GADGETPAINT update
			
		EndIf
		If Event.source=timer
			If Event.id=EVENT_TIMERTICK
				If move=0
					value:-decay
					If value<0
						value=0
						peak=0
					EndIf
				Else
					value:+attack
					If value>newvalue
						value=newvalue
						move=0
						If showpeak peak=1
					EndIf
				EndIf
				update
			EndIf
		EndIf
	End Method
	
	
	Method update()
		SetGraphics CanvasGraphics(canvas)
			SetClsColor backR,backG,backB;Cls
			SetColor frontR,frontG,frontB
			Select vustyle
				Case 0 ' vertical
					DrawRect 0,range-value,ClientWidth(canvas),value
					If peak DrawLine 0,range-1-newvalue,ClientWidth(canvas),range-1-newvalue
				Case 1 ' horizontal
					DrawRect 0,0,value,ClientHeight(canvas)
					If peak DrawLine newvalue,0,newvalue,ClientHeight(canvas)
			End Select
			
		Flip
	End Method
	
	Method Trigger(v:Float=1)
		If v>1 v=1
		newvalue=range*v-1
		move=1
	End Method
	
	Method SetBackcolor(r:Byte,g:Byte,b:Byte)
		backR=r
		backG=g
		backB=b
	End Method
	
	Method SetFrontcolor(r:Byte,g:Byte,b:Byte)
		frontR=r
		frontG=g
		frontB=b
	End Method
	
	Method SetAttack(a:Byte)
		attack=a
	End Method
		
	Method SetDecay(d:Byte)
		decay=d
	End Method
	
End Type

Function CreateVUmeter:TVUmeter(x:Int,y:Int,w:Int,h:Int,parent:TGadget,canvasstyle:Byte=0,vustyle:Byte=0)
	Local a:TVUmeter=New TVUmeter
	
	a.canvas=CreateCanvas(x,y,w,h,parent,canvasstyle)
	
	If vustyle&1
		a.vustyle=1 ' horizontal
		a.range=ClientWidth(a.canvas)
	Else
		a.vustyle=0 ' vertical
		a.range=ClientHeight(a.canvas)
	EndIf
	
	If vustyle&2
		a.showpeak=1
	Else
		a.showpeak=0
	EndIf
	
	a.timer=CreateTimer(60)
	Return a
End Function
'---------------------------------------------------------------------------





' example:


Local win:TGadget=CreateWindow("TVUmeter - CS_TBL",0,0,600,400)
Local but:TGadget=CreateButton("X",32,160,32,32,win)

Local vu:TVumeter=CreateVUmeter(32,2,16,128,win,1,0)
Local vu2:TVumeter=CreateVUmeter(128,2,256,8,win,1,3)
Local vu3:TVumeter=CreateVUmeter(128,24,16,8,win,1,3)


vu2.SetBackcolor 128,64,16
vu2.SetFrontcolor 255,128,32
vu2.SetAttack 64

vu3.SetBackcolor 16,128,64
vu3.SetFrontcolor 32,255,128
vu3.SetAttack 8
vu3.SetDecay 1

Local canvas:TGadget=CreateCanvas(32,228,100,16,win)
Repeat
	WaitEvent()
	
	If EventSource()=canvas And EventID()=EVENT_GADGETPAINT
		SetGraphics CanvasGraphics(canvas);Cls;Flip
	EndIf
	
	If EventID()=EVENT_GADGETACTION
		vu.trigger 1
		vu2.trigger 1
		vu3.trigger 1
	EndIf
	
	If EventID()=EVENT_MOUSEMOVE And EventSource()=canvas
		vu.trigger EventX()/100.0
		vu2.trigger EventX()/100.0
		vu3.trigger EventX()/100.0
	EndIf
	
	If EventID()=EVENT_WINDOWCLOSE End
Forever
