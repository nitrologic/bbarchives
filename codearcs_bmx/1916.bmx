; ID: 1916
; Author: grable
; Date: 2007-02-05 01:55:44
; Title: Gadget Tooltips (Win32)
; Description: Tooltips/Balloon tips for MaxGUI gadgets

SuperStrict

?Win32
Import BRL.MaxGUI
Import BRL.Win32MaxGUI 
Import PUB.Win32
?

Const TTS_ALWAYSTIP:Int = 1
Const TTS_NOPREFIX:Int = 2
Const TTS_NOANIMATE:Int = $10
Const TTS_NOFADE:Int = $20
Const TTS_BALLOON:Int = $40
Const TTS_CLOSE:Int = $80	

?Win32
Const TTM_ACTIVATE:Int = WM_USER+1
Const TTM_SETDELAYTIME:Int = WM_USER+3
Const TTM_ADDTOOLA:Int = WM_USER+4
Const TTM_ADDTOOLW:Int = WM_USER+50
Const TTM_DELTOOLA:Int = WM_USER+5
Const TTM_DELTOOLW:Int = WM_USER+51
Const TTM_NEWTOOLRECTA:Int = WM_USER+6
Const TTM_NEWTOOLRECTW:Int = WM_USER+52
Const TTM_RELAYEVENT:Int = WM_USER+7
Const TTM_GETTOOLINFOA:Int = WM_USER+8
Const TTM_GETTOOLINFOW:Int = WM_USER+53
Const TTM_SETTOOLINFOA:Int = WM_USER+9
Const TTM_SETTOOLINFOW:Int = WM_USER+54
Const TTM_HITTESTA:Int = WM_USER+10
Const TTM_HITTESTW:Int = WM_USER+55
Const TTM_GETTEXTA:Int = WM_USER+11
Const TTM_GETTEXTW:Int = WM_USER+56
Const TTM_UPDATETIPTEXTA:Int = WM_USER+12
Const TTM_UPDATETIPTEXTW:Int = WM_USER+57
Const TTM_GETTOOLCOUNT:Int = WM_USER+13
Const TTM_ENUMTOOLSA:Int = WM_USER+14
Const TTM_ENUMTOOLSW:Int = WM_USER+58
Const TTM_GETCURRENTTOOLA:Int = WM_USER+15
Const TTM_GETCURRENTTOOLW:Int = WM_USER+59
Const TTM_WINDOWFROMPOINT:Int = WM_USER+16
Const TTM_TRACKACTIVATE:Int = WM_USER+17
Const TTM_TRACKPOSITION:Int = WM_USER+18
Const TTM_SETTIPBKCOLOR:Int = WM_USER+19
Const TTM_SETTIPTEXTCOLOR:Int = WM_USER+20
Const TTM_GETDELAYTIME:Int = WM_USER+21
Const TTM_GETTIPBKCOLOR:Int = WM_USER+22
Const TTM_GETTIPTEXTCOLOR:Int = WM_USER+23
Const TTM_SETMAXTIPWIDTH:Int = WM_USER+24
Const TTM_GETMAXTIPWIDTH:Int = WM_USER+25
Const TTM_SETMARGIN:Int = WM_USER+26
Const TTM_GETMARGIN:Int = WM_USER+27
Const TTM_POP:Int = WM_USER+28
Const TTM_UPDATE:Int = WM_USER+29
Const TTM_GETBUBBLESIZE:Int = WM_USER+30
Const TTM_ADJUSTRECT:Int = WM_USER+31
Const TTM_SETTITLEA:Int = WM_USER+32
Const TTM_SETTITLEW:Int = WM_USER+33
Const TTM_ADDTOOL:Int = TTM_ADDTOOLA
Const TTM_DELTOOL:Int = TTM_DELTOOLA
Const TTM_NEWTOOLRECT:Int = TTM_NEWTOOLRECTA
Const TTM_GETTOOLINFO:Int = TTM_GETTOOLINFOA
Const TTM_SETTOOLINFO:Int = TTM_SETTOOLINFOA
Const TTM_HITTEST:Int = TTM_HITTESTA
Const TTM_GETTEXT:Int = TTM_GETTEXTA
Const TTM_UPDATETIPTEXT:Int = TTM_UPDATETIPTEXTA
Const TTM_ENUMTOOLS:Int = TTM_ENUMTOOLSA
Const TTM_GETCURRENTTOOL:Int = TTM_GETCURRENTTOOLA	

Private
Type TToolInfoA
	Field cbSize:Int
	Field uFlags:Int
	Field hwnd:Int
	Field uID:Int
	Field rect:Int,_rect2:Int,_rect3:Int,_rect4:Int
	Field hInst:Int
	Field lpszText:Byte Ptr
	Field lParam:Int
EndType

Const TOOLTIPS_CLASS:String = "tooltips_class32"

Global TooltipHandle:Int
Global BalloontipHandle:Int

Extern "C"
	Function strncpy:Int( dest:Byte Ptr, src$z, length:Int)
EndExtern
Public

' automaticly initialize tooltips
InitTooltips()
?

Function InitTooltips()	
	?Win32
	' regular tooltips
	If TooltipHandle = 0 Then
		TooltipHandle = CreateWindowExA( WS_EX_TOPMOST, TOOLTIPS_CLASS, Null,..
	                            WS_POPUP | TTS_NOPREFIX | TTS_ALWAYSTIP,..
	                            CW_USEDEFAULT, CW_USEDEFAULT,..
	                            CW_USEDEFAULT, CW_USEDEFAULT,..
	                            0, Null, 0, Null)
	
		SetWindowPos( TooltipHandle, HWND_TOPMOST,0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE | SWP_NOACTIVATE)
		SendMessageA( TooltipHandle, TTM_ACTIVATE, True, 0)
	EndIf
	' balloon tips
	If BalloontipHandle = 0 Then
		BalloontipHandle = CreateWindowExA( WS_EX_TOPMOST, TOOLTIPS_CLASS, Null,..
	                            WS_POPUP | TTS_NOPREFIX | TTS_ALWAYSTIP | TTS_BALLOON,..
	                            CW_USEDEFAULT, CW_USEDEFAULT,..
	                            CW_USEDEFAULT, CW_USEDEFAULT,..
	                            0, Null, 0, Null)
	
		SetWindowPos( BalloontipHandle, HWND_TOPMOST,0, 0, 0, 0, SWP_NOMOVE | SWP_NOSIZE | SWP_NOACTIVATE)
		SendMessageA( BalloontipHandle, TTM_ACTIVATE, True, 0)	
	EndIf
	?
EndFunction

Function SetGadgetTooltip( gadget:TGadget, text:String, flags:Int = 0, id:Int = 0)
	If Not gadget Then Return
	?Win32
	Local ti:TToolInfoA = New TToolInfoA
	ti.cbSize = SizeOf(TToolInfoA)
	ti.uFlags = TTF_SUBCLASS | flags 
	ti.hwnd = gadget.Query( QUERY_HWND)
	ti.uID = id
	ti.rect = 0
	ti._rect2 = 0
	ti._rect3 = gadget.Width
	ti._rect4 = gadget.Height	
	If text.Length > 0 Then		
		Local buff:Byte[text.Length+1]
		strncpy( buff, text, buff.Length)	
		ti.lpszText = buff
		SendMessageA( TooltipHandle, TTM_ADDTOOL, 0, Int Byte Ptr ti)	
	Else
		SendMessageA( TooltipHandle, TTM_DELTOOL, 0, Int Byte Ptr ti)	
	EndIf
	?
EndFunction

Function SetGadgetBalloontip( gadget:TGadget, text:String, flags:Int = TTF_CENTERTIP, id:Int = 0)
	If Not gadget Then Return
	?Win32
	Local ti:TToolInfoA = New TToolInfoA
	ti.cbSize = SizeOf(TToolInfoA)
	ti.uFlags = TTF_SUBCLASS | flags
	ti.hwnd = gadget.Query( QUERY_HWND)
	ti.uID = id
	ti.rect = 0
	ti._rect2 = 0
	ti._rect3 = gadget.Width
	ti._rect4 = gadget.Height	
	If text.Length > 0 Then
		Local buff:Byte[text.Length+1]
		strncpy( buff, text, buff.Length)	
		ti.lpszText = buff
		SendMessageA( BalloontipHandle, TTM_ADDTOOL, 0, Int Byte Ptr ti)	
	Else
		SendMessageA( BalloontipHandle, TTM_DELTOOL, 0, Int Byte Ptr ti)	
	EndIf
	?
EndFunction
