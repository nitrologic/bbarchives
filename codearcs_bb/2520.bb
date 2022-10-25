; ID: 2520
; Author: JoshK
; Date: 2009-07-03 12:00:18
; Title: Color edit proxy gadget
; Description: Color gadget

SuperStrict

Import MaxGui.Drivers
Import "Spinner.bmx"
Import "VectorEdit.bmx"

Local window:TGadget

window=CreateWindow("Coloredit Example",40,40,320,240)

Local label:TGadget
Local coloredit:Tcoloredit
Local x:Int=4,y:Int=4

label=CreateLabel("Color:",x,y,60,18,window)
SetGadgetLayout label,1,0,1,0
y:+18
coloredit=CreateColorEdit(x,y,180,20,window)
SetColorEditColor(coloredit,255,220,0)
SetGadgetLayout coloredit,1,1,1,0
y:+22

label=CreateLabel("Color with alpha:",x,y,120,18,window)
SetGadgetLayout label,1,0,1,0
y:+18
coloredit=CreateColorEdit(x,y,180,20,window,COLOREDIT_ALPHA)
SetColorEditColor(coloredit,128,128,128,128)
SetGadgetLayout coloredit,1,1,1,0
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

Const COLOREDIT_ALPHA:Int=1

Type TColorEdit Extends TProxygadget
	
	Field panel:TGadget
	Field spinner:TSpinner[4]
	Field button:TGadget
	Field style:Int
	Field subpanel:TGadget
	Field r%=255,g%=255,b%=255,a%=255
	Field vectoredit:TVectorEdit
	
	Method Cleanup()
		RemoveHook(EmitEventHook,EventHook,Self)
		Super.cleanup()
	EndMethod

	Method SetColor(r:Int,g:Int,b:Int)
		Local gadget:TGadget
		panel.SetColor(r,g,b)
		vectoredit.SetColor(r,g,b)
		'subpanel.SetColor(r,g,b)
		'spinner[0].SetColor(r,g,b)
		'spinner[1].SetColor(r,g,b)
		'spinner[2].SetColor(r,g,b)
		'If spinner[3] spinner[3].SetColor(r,g,b)
		button.SetColor(Self.r,Self.g,Self.b)
	EndMethod
	
	Method SetTextColor(r:Int,g:Int,b:Int)
		Local gadget:TGadget
		panel.SetTextColor(r,g,b)
		vectoredit.SetTextColor(r,g,b)
		'subpanel.SetTextColor(r,g,b)
		'spinner[0].SetTextColor(r,g,b)
		'spinner[1].SetTextColor(r,g,b)
		'spinner[2].SetTextColor(r,g,b)
		'If spinner[3] spinner[3].SetTextColor(r,g,b)
	EndMethod
	
	Function Create:TColorEdit(x:Int,y:Int,width:Int,height:Int,group:TGadget,style:Int=0)
		Local coloredit:Tcoloredit
		Local sw:Int
		Local k:Int
				
		coloredit=New Tcoloredit
		coloredit.style=style
		
		coloredit.panel=CreatePanel(x,y,width,height,group)
		
		'coloredit.subpanel=CreatePanel(0,0,coloredit.panel.ClientWidth()-coloredit.panel.ClientHeight(),coloredit.panel.ClientHeight(),coloredit.panel)
		'SetGadgetLayout coloredit.subpanel,1,1,1,1
		
		If COLOREDIT_ALPHA & style
			k=4
		Else
			k=3
		EndIf
		
		
		coloredit.vectoredit=CreateVectorEdit(0,0,coloredit.panel.ClientWidth()-coloredit.panel.ClientHeight(),coloredit.panel.ClientHeight(),coloredit.panel,k)
		SetVectorEditRange(coloredit.vectoredit,0,255,0)
		SetVectorEditValue(coloredit.vectoredit,[255.0,255.0,255.0,255.0])
		
		Rem
		sw=coloredit.subpanel.ClientWidth()/k
		
		For Local n:Int=0 To k-1
			coloredit.spinner[n]=CreateSpinner(sw*n,0,sw,coloredit.subpanel.ClientHeight(),coloredit.subpanel)	
			SetSpinnerRange coloredit.spinner[n],0,255,0
		Next
		
		If COLOREDIT_ALPHA & style
			SetGadgetLayout coloredit.spinner[0],1,2,1,1
			SetGadgetLayout coloredit.spinner[1],2,2,1,1
			SetGadgetLayout coloredit.spinner[2],2,2,1,1
			SetGadgetLayout coloredit.spinner[3],2,1,1,1
		Else
			SetGadgetLayout coloredit.spinner[0],1,2,1,1
			SetGadgetLayout coloredit.spinner[1],2,2,1,1
			SetGadgetLayout coloredit.spinner[2],2,1,1,1
		EndIf
		EndRem
		
		coloredit.button=CreateButton("",coloredit.panel.ClientWidth()-coloredit.panel.ClientHeight(),0,coloredit.panel.ClientHeight(),coloredit.panel.ClientHeight(),coloredit.panel)		
		SetGadgetLayout coloredit.button,0,1,1,1
		
		coloredit.setproxy(coloredit.panel)
		
		coloredit.SetColor_(255,255,255)
		
		AddHook(EmitEventHook,EventHook,coloredit)
		Return coloredit
	EndFunction
	
	Method GetColor:Int()
		Local color:Int
		Local ch:Byte[4]
		ch[0]=r
		ch[1]=g
		ch[2]=b
		ch[3]=a
		MemCopy Varptr color,ch,4
		Return color
	EndMethod
	
	Method SetColor_(r%,g%,b%,a:Int=255)
		Self.r=r
		Self.g=g
		Self.b=b
		SetGadgetColor button,r,g,b
		SetVectorEditValue(vectoredit,[Float(r),Float(g),Float(b),Float(a)])
		'SetSpinnerValue(spinner[0],r)
		'SetSpinnerValue(spinner[1],g)
		'SetSpinnerValue(spinner[2],b)
		'If spinner[3] SetSpinnerValue(spinner[3],a)
	EndMethod
	
	Function EventHook:Object(id:Int,data:Object,context:Object)
		Local event:TEvent
		Local coloredit:Tcoloredit
		
		event=TEvent(data)
		If event
			coloredit=Tcoloredit(context)
			If coloredit
				Select event.id			
				Case EVENT_GADGETACTION
					Select event.source
					Case coloredit.vectoredit
						Local r%,g%,b%,a%=255
						Local t:Float[]
						t=VectorEditValue(coloredit.vectoredit)
						r=t[0]
						g=t[1]
						b=t[2]
						If (COLOREDIT_ALPHA & coloredit.style)
							a=t[3]
						EndIf
						coloredit.SetColor_(r,g,b,a)
						EmitEvent CreateEvent(EVENT_GADGETACTION,coloredit,coloredit.GetColor())
						Return Null
					'Case coloredit.spinner[0],coloredit.spinner[1],coloredit.spinner[2],coloredit.spinner[3]
					'	If event.source=coloredit.spinner[3] And coloredit.spinner[3]=Null Return data
					'	Local a%=0
					'	If coloredit.spinner[3] a=SpinnerValue(coloredit.spinner[3])
					'	coloredit.SetColor_(SpinnerValue(coloredit.spinner[0]),SpinnerValue(coloredit.spinner[1]),SpinnerValue(coloredit.spinner[2]),a)
					'	EmitEvent CreateEvent(EVENT_GADGETACTION,coloredit,coloredit.GetColor())
					'	Return Null
					Case coloredit.button
						If RequestColor(coloredit.r,coloredit.g,coloredit.b)
							coloredit.SetColor_(RequestedRed(),RequestedGreen(),RequestedBlue())
							EmitEvent CreateEvent(EVENT_GADGETACTION,coloredit,coloredit.GetColor())
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
Function CreateColorEdit:TColorEdit(x:Int,y:Int,width:Int,height:Int,group:TGadget,flags:Int=0)
	Return TColorEdit.Create(x,y,width,height,group,flags)
EndFunction

Rem
bbdoc:
EndRem
Function ColorEditColor:Int(coloredit:TColorEdit)
	Return coloredit.GetColor()
EndFunction

Rem
bbdoc:
EndRem
Function SetColorEditColor:Int(coloredit:TColorEdit,r:Int,g:Int,b:Int,a:Int=255)
	Return coloredit.SetColor_(r,g,b,a)
EndFunction
