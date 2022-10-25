; ID: 2514
; Author: JoshK
; Date: 2009-06-28 15:26:45
; Title: Spinner Proxygadget
; Description: Advanced number editor

SuperStrict

Import MaxGui.Drivers
Import brl.retro


Local window:TGadget

window=CreateWindow("Spinner Example",40,40,320,240)

Local spinner:TSpinner
Local label:TGadget
Local x:Int=2,y:Int=2

label=CreateLabel("Float spinner:",x,y,200,18,window)
SetGadgetLayout label,1,0,1,0
y:+18
spinner=CreateSpinner(x,y,50,20,window)
SetSpinnerRange(spinner,0.11,1.2,2)
SetSpinnerValue(spinner,0.1)

SetGadgetLayout spinner,1,1,1,0
y:+22

label=CreateLabel("Integer spinner with trackbar:",x,y,200,18,window)
SetGadgetLayout label,1,0,1,0
y:+18
spinner=CreateSpinner(x,y,300,20,window,SPINNER_TRACKBAR)
SetSpinnerRange(spinner,-2,2,0)
SetGadgetLayout spinner,1,1,1,0
y:+22


While True
	WaitEvent 
	Print CurrentEvent.ToString()
	Select EventID()
		Case EVENT_WINDOWCLOSE
			End
	End Select
Wend


'-------------------------------------------------------------------

Const SPINNER_TRACKBAR:Int=1

Type TSpinner Extends TProxygadget
	
	Const SliderWidth:Int=12
	Const Div:Int=52
	
	Field panel:TGadget
	Field textfield:TGadget
	Field slider:TGadget
	Field trackbar:TGadget
	Field value:Int
	Field range:Int[2]
	Field accuracy:Int=1
	Field floatitemmultiplier:Double=10

	Method SetTextColor(r:Int,g:Int,b:Int)
		Local gadget:TGadget
		Super.SetTextColor(r,g,b)
		textfield.SetTextColor(r,g,b)
		slider.SetTextColor(r,g,b)
		If trackbar trackbar.SetTextColor(r,g,b)
	EndMethod
	
	Method SetColor(r:Int,g:Int,b:Int)
		Local gadget:TGadget
		Super.SetColor(r,g,b)
		textfield.SetColor(r,g,b)
		slider.SetColor(r,g,b)
		If trackbar trackbar.SetColor(r,g,b)
	EndMethod
	
	Method SetRange_(minimum:Double,maximum:Double,accuracy:Int=1)
		Self.accuracy=accuracy
		floatitemmultiplier=10.0^Double(accuracy)
		range[0]=Round(minimum*floatitemmultiplier)
		range[1]=Round(maximum*floatitemmultiplier)-range[0]
		SetSliderRange slider,1,range[1]+1
		If trackbar
			SetSliderRange trackbar,1,range[1]+1
		EndIf
		SetValue_(value)
	EndMethod

	Method SetValue_(f:Double)
		Local i:Int
		i=Round(f*floatitemmultiplier)
		i=Max(i,range[0])
		i=Min(i,range[0]+range[1])
		'Notify i+", "+range[0]+", "+range[1]
		SetGadgetText textfield,FloatToString(i/floatitemmultiplier,accuracy)
		SetSliderValue slider,range[1]-(i-range[0])
		If trackbar SetSliderValue trackbar,i-range[0]+1
		value=i
	EndMethod
		
	Method Cleanup()
		RemoveHook(EmitEventHook,EventHook,Self)
		Super.Cleanup()
	EndMethod
	
	Function Create:TSpinner(x:Int,y:Int,width:Int,height:Int,group:TGadget,flags:Int=0)
		Local spinner:TSpinner
		Local w:Int
		
		spinner=New TSpinner
				
		spinner.panel=CreatePanel(x,y,width,height,group)
		spinner.setproxy(spinner.panel)
		
		w=spinner.panel.ClientWidth()
		If (SPINNER_TRACKBAR & flags)
			w=div
			If w>spinner.panel.ClientWidth() w=spinner.panel.ClientWidth()
		EndIf
		
		spinner.textfield=CreateTextField(0,0,w-SliderWidth,spinner.panel.ClientHeight(),spinner.panel)
		If (SPINNER_TRACKBAR & flags)
			SetGadgetLayout spinner.textfield,1,0,1,1
		Else
			SetGadgetLayout spinner.textfield,1,1,1,1
		EndIf
		
		spinner.slider=CreateSlider(w-SliderWidth,0,SliderWidth,spinner.panel.ClientHeight(),spinner.panel,SLIDER_VERTICAL)		
		If (SPINNER_TRACKBAR & flags)
			SetGadgetLayout spinner.slider,1,0,1,1
		Else
			SetGadgetLayout spinner.slider,0,1,1,1
		EndIf
		AddHook(EmitEventHook,EventHook,spinner)
		
		If (SPINNER_TRACKBAR & flags)
			spinner.trackbar=CreateSlider(w,0,spinner.panel.ClientWidth()-w,spinner.panel.ClientHeight(),spinner.panel,SLIDER_TRACKBAR|SLIDER_HORIZONTAL)
			SetGadgetLayout spinner.trackbar,1,1,1,1
		EndIf
		
		spinner.SetRange_(0,1)
		Return spinner
	EndFunction

	Function EventHook:Object(id:Int,data:Object,context:Object)
		Local event:TEvent
		Local spinner:TSpinner
		Local i:Int
		
		event=TEvent(data)
		If event
			spinner=TSpinner(context)
			If spinner
				Select event.id
				Case EVENT_GADGETLOSTFOCUS
					If event.source=spinner.textfield
						Local old:Double=SpinnerValue(spinner)
						spinner.SetValue_(Double(GadgetText(spinner.textfield)))
						If old<>SpinnerValue(spinner)
							EmitEvent CreateEvent(EVENT_GADGETACTION,spinner)
						EndIf
						Return Null
					EndIf				
				Case EVENT_GADGETACTION
					Select event.source
					Case spinner.trackbar
						If spinner.trackbar
							i=(SliderValue(spinner.trackbar)+spinner.range[0]-1)
							spinner.SetValue_(i/spinner.floatitemmultiplier)
							EmitEvent CreateEvent(EVENT_GADGETACTION,spinner)
						EndIf
					Case spinner.textfield
						Return Null
					Case spinner.slider
						i=spinner.range[1]-(SliderValue(spinner.slider)-spinner.range[0])
						spinner.SetValue_(Double(i)/spinner.floatitemmultiplier)
						EmitEvent CreateEvent(EVENT_GADGETACTION,spinner)
						Return Null
					EndSelect
				EndSelect
			EndIf
		EndIf
		Return data
	EndFunction
	
	Function Round:Int(val:Double)
		Local dec#
		dec#=val-Floor(val)
		If dec<0.5 Return Floor(val) Else Return Ceil(val)
	EndFunction	
	
	Function FloatToString:String(value:Float,places:Int=3)
		Local sign:Int=Sgn(value)
		value=Abs(value)
		Local i:Int=Round(value*10^places)
		Local ipart:Int=Int(i/10^places)
		Local dpart:Int=i-ipart*10^places
		Local si$=ipart
		Local di$
		If dpart>0
			di=dpart
			While di.length<places
				di="0"+di
			Wend
			di="."+di
		EndIf
		While Right(di,1)="0"
			di=Left(di,di.length-1)
		Wend
		If places
			If di="" di=".0"
		EndIf
		If sign=-1 si="-"+si
		Return si+di
	EndFunction	
	
EndType

Rem
bbdoc:
EndRem
Function CreateSpinner:TSpinner(x:Int,y:Int,width:Int,height:Int,group:TGadget,flags:Int=0)
	Return TSpinner.Create(x,y,width,height,group,flags)
EndFunction

Rem
bbdoc:
EndRem
Function SetSpinnerRange(spinner:TSpinner,minimum:Double,maximum:Double,accuracy:Int=1)
	spinner.SetRange_(minimum,maximum,accuracy)
EndFunction

Rem
bbdoc:
EndRem
Function SetSpinnerValue(spinner:TSpinner,value:Double)
	spinner.SetValue_(value)
EndFunction

Rem
bbdoc:
EndRem
Function SpinnerValue:Double(spinner:TSpinner)
	Return spinner.value/spinner.floatitemmultiplier
EndFunction
