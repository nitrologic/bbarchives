; ID: 2228
; Author: JoshK
; Date: 2008-03-10 19:02:46
; Title: Gadget Helper
; Description: Transforms screen coordinates from one gadget to another, or to the screen

Strict

Import pub.win32
Import brl.maxgui

Extern "win32"
	Function ScreenToClient:Int(hwnd,lpPoint:Byte Ptr)="ScreenToClient@8"
	Function ClientToScreen:Int(hwnd,lpPoint:Byte Ptr)="ClientToScreen@8"
	Function SetCursorPos:Int(x,y)="SetCursorPos@8"
	Function GetCursorPos:Int(lpPoint:Byte Ptr)="GetCursorPos@4"
EndExtern

Function coord:TCoord(x,y)
	Local screencoord:TCoord=New TCoord
	screencoord.x=x
	screencoord.y=y
	Return screencoord
EndFunction

Type TCoord
	Field x,y
	
	Method pointer:Byte Ptr()
		Return Varptr x
	EndMethod
	
	Method copy:TCoord()
		Return Coord(x,y)
	EndMethod
	
EndType

Function GetGadgetCoord:TCoord(gadget:TGadget,glob=0)
	If glob
		Return TFormCoord( Coord(GadgetX(gadget),GadgetY(gadget)),GadgetGroup(gadget),Null )
	Else
		Return Coord(GadgetX(gadget),GadgetY(gadget))
	EndIf
EndFunction

Function SetMouseCoord(c:TCoord,gadget:TGadget=Null)
	If gadget c=TFormCoord(c,gadget,Null)
	setcursorpos c.x,c.y
EndFunction

Function TFormCoord:TCoord(c:TCoord,src:TGadget,dst:TGadget)
	c=c.copy()
	If src ClientToScreen QueryGadget(src,QUERY_HWND),c.pointer()
	If dst ScreenToClient QueryGadget(dst,QUERY_HWND),c.pointer()
	Return c
EndFunction
