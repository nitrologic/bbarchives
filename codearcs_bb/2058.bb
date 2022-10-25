; ID: 2058
; Author: Nebula
; Date: 2007-07-07 11:32:27
; Title: Win 3.11 style window
; Description: Empty Windows 3.11 style window.

Global mywin
Global wintitle$	= "My window"
Global winx			= 100
Global winy			= 100
Global winwidth 	= 320
Global winheight 	= 200
Global myox,myoy
Global buttonmin
Global buttonclose
Global mytbar
Global mylock

makemywin
While myExit = False
	we = WaitEvent()
	mywin we
Wend
End

Function makemywin()
	mywin		= CreateWindow	(wintitle$	,winx,winy,winwidth,winheight,Desktop(),0)
	buttonmin  	=	CreateButton("--"		,GadgetWidth(mywin)-64,+1,32,16-2,mywin)
	buttonclose = 	CreateButton("X"		,GadgetWidth(mywin)-32,+1,32,16-2,mywin)
	mytbar		=	CreateCanvas(			0,0,GadgetWidth(mywin),16,mywin)	
	SetBuffer CanvasBuffer(mytbar)
	ClsColor 0,0,200
	Cls
	Color 255,255,255
	Text 10,2,wintitle$
	FlipCanvas mytbar	
End Function

Function mywin(action)
	Select action
		Case $401 ; button
			If EventSource() = buttonmin Then MinimizeWindow(mywin)
			If EventSource() = buttonclose Then End
		Case $201 ; mousedown
			If EventSource() = mytbar
				mylock = True
				myox = EventX()
				myoy = EventY()
			End If
		Case $202 ; mouseup
			If EventSource() = mytbar
				mylock = False
			End If
		Case $203 ; mousemove
			If EventSource() = mytbar
				If mylock = True Then
					SetGadgetShape mywin,MouseX()-myox,MouseY()-myoy,ClientWidth(mywin),ClientHeight(mywin)
				End If
			End If
		Case $206;mouseleave
			If EventSource() = mytbar
				mylock=False
			End If
	End Select
End Function
