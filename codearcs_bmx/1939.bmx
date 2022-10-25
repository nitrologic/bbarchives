; ID: 1939
; Author: ninjarat
; Date: 2007-03-04 08:05:39
; Title: Graphics Driver Import
; Description: Let the user select a graphics driver.

Import BRL.GLMax2D

?Win32
Import BRL.D3D7Max2D
Import BRL.Win32MaxGUI

Function GraphicsDriverSelector:TMax2DDriver()
	s_wdth=ClientWidth(Desktop());s_cx=s_wdth/2
	s_hght=ClientHeight(Desktop());s_cy=s_hght/2
	
	Local win:TGadget=..
	 CreateWindow("Select Graphics Driver",..
	  s_cx-120,s_cy-40,241,81,..
	   Null,WINDOW_TITLEBAR|WINDOW_CLIENTCOORDS)
	
	text$="Please select the graphics driver~n~r"
	text:+"you would like to use for this game."
	Local txt:TGadget=CreateLabel(text,18,12,216,48,win)
	
	Local dx7:TGadget=CreateButton("DirectX 7.x",18,48,102,20,win)
	Local ogl:TGadget=CreateButton("OpenGL 1.1.x",120,48,102,20,win)
	
	selecteddriver=-1
	Repeat
		WaitEvent
		Select CurrentEvent.ID
			Case EVENT_WINDOWCLOSE; End
		Case EVENT_GADGETACTION
			Select CurrentEvent.Source
				Case dx7 selecteddriver=1
				Case ogl selecteddriver=0
			End Select
		End Select
	Until selecteddriver>-1
	
	dx7.Free
	ogl.Free
	txt.Free
	win.Free
	
	If selecteddriver Then Return D3D7Max2DDriver() ..
	 Else Return GLMax2DDriver()
End Function
?
