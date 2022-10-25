; ID: 2823
; Author: JoshK
; Date: 2011-02-11 20:36:57
; Title: System Requestors for MaxGUI
; Description: brl.system requestors that disable all program windows while open

SuperStrict

Import brl.system
Import maxgui.maxgui

Private

Global disabledwindowslist:TList

Function TopMostGadget:TGadget(gadget:TGadget)
	While gadget.parent
		gadget=gadget.parent
	Wend
	Return gadget
EndFunction

Function DisableWindows(gadget:TGadget)
	If Not disabledwindowslist disabledwindowslist=New TList
	If Not GadgetDisabled(gadget)
		DisableGadget gadget
		disabledwindowslist.addlast(gadget)
	EndIf
	For gadget=EachIn gadget.kids
		If GadgetClass(gadget)=GADGET_WINDOW
			DisableWindows gadget
		EndIf
	Next
EndFunction

Public

Function Notify:Int(text:String,serious:Int=False)
	Local result:Int
	Local gadget:TGadget
	
	gadget=ActiveGadget()
	If gadget
		gadget=TopMostGadget(gadget)
		DisableWindows(gadget)
	EndIf
	
	result=brl.system.Notify(text,serious)
	
	If disabledwindowslist
		For gadget=EachIn disabledwindowslist
			EnableGadget gadget
		Next
		disabledwindowslist=Null
	EndIf	
	
	Return result
EndFunction

Function Confirm:Int(text:String,serious:Int=False)
	Local result:Int
	Local gadget:TGadget
	
	gadget=ActiveGadget()
	If gadget
		gadget=TopMostGadget(gadget)
		DisableWindows(gadget)
	EndIf
		
	result=brl.system.Confirm(text,serious)
	
	If disabledwindowslist
		For gadget=EachIn disabledwindowslist
			EnableGadget gadget
		Next
		disabledwindowslist=Null
	EndIf	
	
	Return result
EndFunction

Function Proceed:Int(text:String,serious:Int=False)
	Local result:Int
	Local gadget:TGadget
	
	gadget=ActiveGadget()
	If gadget
		gadget=TopMostGadget(gadget)
		DisableWindows(gadget)
	EndIf
	
	result=brl.system.Proceed(text,serious)
	
	If disabledwindowslist
		For gadget=EachIn disabledwindowslist
			EnableGadget gadget
		Next
		disabledwindowslist=Null
	EndIf	
	
	Return result
EndFunction

Function RequestDir:String(text:String,initial_path:String="")
	Local result:String
	Local gadget:TGadget
	
	gadget=ActiveGadget()
	If gadget
		gadget=TopMostGadget(gadget)
		DisableWindows(gadget)
	EndIf
	
	result=brl.system.RequestDir(text,initial_path)
	
	If disabledwindowslist
		For gadget=EachIn disabledwindowslist
			EnableGadget gadget
		Next
		disabledwindowslist=Null
	EndIf	
	
	Return result
EndFunction

Function RequestFile:String(text:String,extensions:String="",save_flag:Int=False,initial_path:String="")
	Local result:String
	Local gadget:TGadget
	
	gadget=ActiveGadget()
	If gadget
		gadget=TopMostGadget(gadget)
		DisableWindows(gadget)
	EndIf
	
	result=brl.system.RequestFile(text,extensions,save_flag,initial_path)
	
	If disabledwindowslist
		For gadget=EachIn disabledwindowslist
			EnableGadget gadget
		Next
		disabledwindowslist=Null
	EndIf	
	
	Return result
EndFunction
