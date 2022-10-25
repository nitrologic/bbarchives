; ID: 595
; Author: cyberseth
; Date: 2003-02-19 13:59:14
; Title: Hotspots!
; Description: MouseOver() events come into being at last, with Hotspots!

Type hotspot
	Field x,y,width,height
	Field clx,cly,win
End Type

win = CreateWindow("Testing!",100,100,200,200,0)
h.hotspot = CreateHotSpot(10,10,150,40,win)
lbltest = CreateTextField(10,10,150,40,win)
lbltest2 = CreateLabel("BOO!",10,60,100,40,win)
SetGadgetText lbltest,"Move your mouse here!"

Repeat
	If WaitEvent(1)=$803 Or KeyHit(1) Then End
	If MouseOverHotSpot(h)
		ShowGadget lbltest2
	Else
		HideGadget lbltest2
	End If
Forever


Function CreateHotSpot.hotspot(x,y,width,height,window)
	; -- In the blink of an instant.. Find out the client X,Y offsets --
	tmpcan = CreateCanvas(0,0,1,1,window)
	xx=MouseX() yy=MouseY()
	MoveMouse 0,0,tmpcan
	clx=MouseX() cly=MouseY()
	MoveMouse GadgetX(window),GadgetY(window)
	clx=clx-MouseX() cly=cly-MouseY()
	MoveMouse xx,yy
	FreeGadget tmpcan
	; -- Now make the hotspot area and save the X,Y offsets --
	h.hotspot = New hotspot
	h\x=x  h\y=y
	h\width=width h\height=height
	h\clx=clx  h\cly=cly
	h\win = window
	Return h
End Function

Function MouseOverHotSpot(h.hotspot)
	x=GadgetX(h\win)+h\clx+h\x ;Window x,y + border\client offset + hotspot x,y
	y=GadgetY(h\win)+h\cly+h\y
	Return RectsOverlap(MouseX(),MouseY(),1,1,x,y,h\width,h\height)
End Function
