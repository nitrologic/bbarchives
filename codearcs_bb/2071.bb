; ID: 2071
; Author: grable
; Date: 2007-07-17 12:49:30
; Title: [MaxGUI/Win32]: Transparent Window Gadgets
; Description: Transparent / Alpha Window Gadgets

Rem
	transparent windows in windows ;)
	
	author: grable
	email : grable0@gmail.com
EndRem

SuperStrict

Import BRL.MaxGUI

?MacOS
DebugLog "LayeredGadgets.bmx doesnt support MacOS"
?Linux
DebugLog "LayeredGadgets.bmx doesnt support Linux"
?

Private

?Win32
Extern "Win32"
	Const GWL_EXSTYLE:Int = -20
	Const GWL_STYLE:Int = -16
	
	Const WS_EX_TRANSPARENT:Int = $20
	Const WS_EX_LAYERED:Int = $80000
	
	Const LWA_COLORKEY:Int = $1
	Const LWA_ALPHA:Int = $2

	Function GetWindowLong:Int( hwnd:Int, index:Int) = "GetWindowLongA@8"
	Function SetWindowLong:Int( hwnd:Int, index:Int, value:Int) = "SetWindowLongA@12"
	Function SetLayeredWindowAttributes:Int( hwnd:Int, ckey:Int, alpha:Byte, flags:Int)

'	typedef struct _BLENDFUNCTION {
'	  	Byte     BlendOp;
'	  	Byte     BlendFlags;
'	  	Byte     SourceConstantAlpha;
'	  	Byte     AlphaFormat;
'	}BLENDFUNCTION, *PBLENDFUNCTION, *LPBLENDFUNCTION;
'	Function 	UpdateLayeredWindow:Int( hwnd:Int, destdc:Int, destpoint:Long Var, size:Long Var, srcdc:Int, srcpoint:Long Var, ckey:Int, blendfunc:Byte Ptr, flags:Int)
EndExtern
?

Public

Function SetGadgetLayered( gadget:TGadget, layered:Int, transparent:Int = False)
	?Win32
	Local handle:Int = QueryGadget( gadget, QUERY_HWND)
	Local style:Int = GetWindowLong( handle, GWL_EXSTYLE)
	If layered Then 
		style :| WS_EX_LAYERED
	Else
		style :& ~WS_EX_LAYERED
	EndIf
	If transparent  Then 
		style:| WS_EX_TRANSPARENT
	Else
		style :& ~WS_EX_TRANSPARENT
	EndIf
	SetWindowLong( handle, GWL_EXSTYLE, style)
	?
EndFunction

Function SetGadgetColorKey( gadget:TGadget, ckey:Int)
	?Win32
	Local handle:Int = QueryGadget( gadget, QUERY_HWND)
	SetLayeredWindowAttributes( handle, ckey, 0, LWA_COLORKEY)
	?
EndFunction

?Win32
Function SetGadgetAlpha( gadget:TGadget, alpha:Float)
	Local handle:Int = QueryGadget( gadget, QUERY_HWND)
	SetLayeredWindowAttributes( handle, 0, Byte(alpha * 255), LWA_ALPHA)
EndFunction
?

Function SetGadgetLayeredAttribs( gadget:TGadget, ckey:Int, alpha:Byte)
	?Win32	
	Local handle:Int = QueryGadget( gadget, QUERY_HWND)
	SetLayeredWindowAttributes( handle, ckey, alpha, LWA_COLORKEY | LWA_ALPHA)
	?	
EndFunction
