; ID: 2502
; Author: JoshK
; Date: 2009-06-10 12:38:08
; Title: Mouse commands
; Description: Replacement mouse commands for gadget mouse coordinates.

SuperStrict

Import maxgui.maxgui
Import pub.win32

Private

?win32
Extern "win32"
	Function GetCursorPos:Int(point:Byte Ptr)
	Function SetCursorPos:Int(x:Int,y:Int)
	Function ScreenToClient:Int(hwnd:Int,point:Byte Ptr) 
EndExtern
?

Public

Function MouseX:Int(gadget:TGadget=Null)
	Local hwnd:Int
	Local pos:Int[2]
	GetCursorPos pos
	If gadget screentoclient QueryGadget(gadget,QUERY_HWND),pos
	Return pos[0]	
EndFunction

Function MouseY:Int(gadget:TGadget=Null)
	Local hwnd:Int
	Local pos:Int[2]
	GetCursorPos pos
	If gadget screentoclient QueryGadget(gadget,QUERY_HWND),pos
	Return pos[1]	
EndFunction

Function MoveMouse(x:Int,y:Int,gadget:TGadget=Null)
	Local hwnd:Int
	Local pos:Int[2]
	pos[0]=x
	pos[1]=y
	If gadget clienttoscreen QueryGadget(gadget,QUERY_HWND),pos
	SetCursorPos pos[0],pos[1]
EndFunction
