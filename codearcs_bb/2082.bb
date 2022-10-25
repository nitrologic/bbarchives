; ID: 2082
; Author: Ked
; Date: 2007-07-28 19:11:04
; Title: Animate Windows
; Description: A cool way to animate windows

Function CreateAnimWindow(title$,width,height,x,y,group,style=1+2)
	Local curw=0
	Local curh=0
	window=CreateWindow(title$,curw,curh,x,y,group,style)
	
	Repeat
		curw=curw+5
		If curw>width
			curw=width
		EndIf
		SetGadgetShape window,x,y,curw,curh 
	Until curw=width
	
	Repeat
		curh=curh+5
		If curh>height
			curh=height
		EndIf
		SetGadgetShape window,x,y,curw,curh 
	Until curh=height
	
	Return window
End Function

Function DeleteAnimWindow(wnd)
	Local curw=GadgetWidth(wnd)
	Local curh=GadgetHeight(wnd)
	Local x=GadgetX(wnd),y=GadgetY(wnd)
	
	Repeat
		curh=curh-5
		If curh<0
			curh=0
		EndIf
		SetGadgetShape wnd,x,y,curw,curh
	Until curh=0
	
	Repeat
		curw=curw-5
		If curw<0
			curw=0
		EndIf
		SetGadgetShape wnd,x,y,curw,curh
	Until curw=0
	
	FreeGadget wnd
End Function

wnd=CreateAnimWindow("TEST",640,480,50,50,Desktop())

Repeat

Until WaitEvent()=$803
DeleteAnimWindow(wnd)
