; ID: 2521
; Author: JoshK
; Date: 2009-07-05 08:57:59
; Title: Vector Editor
; Description: Edit a vector with any number of components

SuperStrict

Import MaxGui.Drivers
Import "Spinner.bmx"

Rem
Local window:TGadget

window=CreateWindow("vectoredit Example",40,40,320,240)

Local label:TGadget
Local vectoredit:Tvectoredit
Local x:Int=4,y:Int=4

label=CreateLabel("vec3 with negative numbers:",x,y,200,18,window)
SetGadgetLayout label,1,0,1,0
y:+18
vectoredit=CreatevectorEdit(x,y,180,20,window)
SetVectorEditRange(vectoredit,-1,1)
SetVectorEditValue(vectoredit,[-1.0,0.0,0.5])
SetGadgetLayout vectoredit,1,1,1,0
y:+22

label=CreateLabel("vec4:",x,y,120,18,window)
SetGadgetLayout label,1,0,1,0
y:+18
vectoredit=CreatevectorEdit(x,y,180,20,window,4)
SetGadgetLayout vectoredit,1,1,1,0
y:+22


While True
	WaitEvent 
	Print CurrentEvent.ToString()
	Select EventID()
		Case EVENT_WINDOWCLOSE
			End
	End Select
Wend

EndRem

'-------------------------------------------------------------------


Type TVectorEdit Extends TProxygadget
	
	Field panel:TGadget
	Field spinner:TSpinner[]
	Field style:Int
		
	Method Cleanup()
		RemoveHook(EmitEventHook,EventHook,Self)
		Super.cleanup()
	EndMethod

	Method SetRange_(minimum:Double,maximum:Double,accuracy:Int=1)
		Local n:Int
		For n=0 To spinner.length-1
			spinner[n].SetRange_(minimum,maximum,accuracy)
		Next		
	EndMethod

	Method SetColor(r:Int,g:Int,b:Int)
		Local n:Int
		panel.SetColor(r,g,b)
		For n=0 To spinner.length-1
			spinner[n].SetColor(r,g,b)
		Next
	EndMethod
	
	Method SetTextColor(r:Int,g:Int,b:Int)
		Local n:Int
		panel.SetTextColor(r,g,b)
		For n=0 To spinner.length-1
			spinner[n].SetTextColor(r,g,b)
		Next
	EndMethod
	
	Method SetValue_(t:Float[])
		Local n:Int
		For n=0 To spinner.length-1
			SetSpinnerValue(spinner[n],t[n])
		Next
	EndMethod

	Method GetValue_:Float[]()
		Local t:Float[spinner.length]
		Local n:Int
		For n=0 To spinner.length-1
			t[n]=SpinnerValue(spinner[n])
		Next
		Return t
	EndMethod
	
	Function Create:TVectorEdit(x:Int,y:Int,width:Int,height:Int,group:TGadget,components:Int=3)
		Local vectoredit:Tvectoredit
		Local sw:Int
		Local k:Int
				
		vectoredit=New Tvectoredit
		'vectoredit.style=style
		
		vectoredit.panel=CreatePanel(x,y,width,height,group)
		
		sw=vectoredit.panel.ClientWidth()/components
		
		vectoredit.spinner=New TSpinner[components]
		
		For Local n:Int=0 To components-1
			vectoredit.spinner[n]=CreateSpinner(sw*n,0,sw,vectoredit.panel.ClientHeight(),vectoredit.panel)	
			SetSpinnerRange vectoredit.spinner[n],0,1,1
			Select n
			Case 0 SetGadgetLayout vectoredit.spinner[n],1,2,1,1
			Case components-1 SetGadgetLayout vectoredit.spinner[n],2,1,1,1
			Default SetGadgetLayout vectoredit.spinner[n],2,2,1,1
			EndSelect
		Next
				
		vectoredit.setproxy(vectoredit.panel)
		
		AddHook(EmitEventHook,EventHook,vectoredit)
		Return vectoredit
	EndFunction

	Function EventHook:Object(id:Int,data:Object,context:Object)
		Local event:TEvent
		Local vectoredit:Tvectoredit
		Local n:Int
		
		event=TEvent(data)
		If event
			vectoredit=Tvectoredit(context)
			If vectoredit
				Select event.id			
				Case EVENT_GADGETACTION
					For n=0 To vectoredit.spinner.length-1
						If event.source=vectoredit.spinner[n]
							EmitEvent CreateEvent(EVENT_GADGETACTION,vectoredit)
							Return Null
						EndIf
					Next
				EndSelect
			EndIf
		EndIf
		Return data
	EndFunction
	
EndType

Rem
bbdoc:
EndRem
Function CreateVectorEdit:TVectorEdit(x:Int,y:Int,width:Int,height:Int,group:TGadget,components:Int=3)
	Return TVectorEdit.Create(x,y,width,height,group,components)
EndFunction

Rem
bbdoc:
EndRem
Function VectorEditValue:Float[](vectoredit:TVectorEdit)
	Return vectoredit.GetValue_()
EndFunction

Rem
bbdoc:
EndRem
Function SetVectorEditRange(vectoredit:TVectorEdit,minimum:Double,maximum:Double,accuracy:Int=1)
	vectoredit.SetRange_(minimum,maximum,accuracy)
EndFunction

Rem
bbdoc:
EndRem
Function SetVectorEditValue:Int(vectoredit:TVectorEdit,t:Float[])
	Return vectoredit.SetValue_(t)
EndFunction
