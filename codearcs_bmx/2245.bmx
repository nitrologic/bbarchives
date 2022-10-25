; ID: 2245
; Author: Aelin
; Date: 2008-04-22 02:34:47
; Title: Dreamotion3D Panel
; Description: Initialize Dreamotion3D on a canvas/panel.

Import pub.dreamotion3d
Import maxgui.drivers
Import maxgui.win32maxguiex

Global Window:TGadget = CreateWindow("Dreammotion Test", 0, 0, 640, 480)
Global Panel:TGadget = CreatePanel(0, 0, 632, 412, Window, PANEL_ACTIVE|PANEL_BORDER)
Global Timer:TTimer = CreateTimer( 60 )
Global Camera:CCamera
Global sun_light:CLight
Global Canyon:CMD2

Global Font:CFont

SetGadgetLayout(Panel, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED, EDGE_ALIGNED)

DM_PanelGraphics3D(Panel, 640, 480)

Camera = DM_CreateCamera( )

Font = DM_LoadFont("Arial", 8)
DM_TextColor(Font, 255, 255, 255, 255)

Repeat
	WaitEvent( )
	
	'
	Select ( EventID( ) )
		Case EVENT_TIMERTICK
			DM_BeginScene( )
			DM_RenderWorld(Camera)
			DM_DrawText(Font, 0, 0, "FPS: " + DM_FPS( ))
			DM_EndScene( )
			
		Case EVENT_GADGETPAINT
			
			
		Case EVENT_APPTERMINATE, EVENT_WINDOWCLOSE
			End
	End Select
Forever

Function DM_PanelGraphics3D:CApplication(Panel:TGadget, Width:Int, Height:Int, Depth:Int = 32)
	' Local objects:
	Local hResult:CApplication
	
	' Get the gadget's hWnd.
	DM_HWND = Int(QueryGadget(Panel, QUERY_HWND))
	
	'
	hResult = DM_InitGraphics(DM_HWND, Width, Height, Depth, False, True, False)
	TransType_(Varptr DM_D3D, Getptr_(hresult,APP_D3D))
	TransType_(Varptr DM_D3DDEVICE, Getptr_(hresult,APP_DEVICE))
	
	DM_WIDTH = Width
	DM_HEIGHT = Height
	
	Return( hResult )
End Function
