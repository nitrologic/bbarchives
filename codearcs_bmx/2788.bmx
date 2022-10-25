; ID: 2788
; Author: JoshK
; Date: 2010-11-28 19:06:08
; Title: GetGadgetAtPosition
; Description: Retrieve the gadget at a screen coordinate

SuperStrict

Import maxgui.drivers

?win32
Import pub.win32

Extern "win32"
	Function WindowFromPoint:Int(x:Int,y:Int)
EndExtern

Function GetGadgetFromHwnd:TGadget(hwnd:Int)
	Return TGadget(TWindowsGUIDriver.GadgetMap.valueforkey(TIntWrapper.Create(hwnd)))
EndFunction

Function GetGadgetAtPosition:TGadget(x:Int,y:Int)
	Return GetGadgetFromHwnd(WindowFromPoint(x,y))
EndFunction
?
