; ID: 1595
; Author: Grisu
; Date: 2006-01-13 15:06:11
; Title: Minimize / Maximize buttons
; Description: Minimize / Maximize buttons for windowed App [Non MAXGUI]

' --------------------------------------------------------------
' BMX: Minimize / Maximize buttons for windowed App [Non MAXGUI].
' Very special thanks go to Diablo & Zawran!
' --------------------------------------------------------------
Strict 

Framework BRL.D3D7Max2D ' Framework, minimal stuff you need
'  Import brl.glmax2d
  Import BRL.EventQueue
  Import BRL.Event

Extern "win32" ' Crazy WinAPI stuff
	Function GetActiveWindow%()
	Function IsZoomed%(hwnd%)
End Extern

AppTitle = " Grisu and his crazy buttons - Version 1.0"
Graphics 800,600,0
Global hWnd% = GetActiveWindow() ' Save current Window handle

' Init Buttons
enableMinimize( hwnd% )
enableMaximize( hwnd% )

' --------------------------------------------------------------
' Main Loop
While Not KeyHit(KEY_ESCAPE) 

	Cls
		DrawText "BMX: Minimize / Maximize buttons [Non MAXGUI].",20,20
		DrawText "Very special thanks go to Diablo & Zawran!",20,40

		DrawText "Push a button or hit ESC to exit....",20,560
	Flip
	
    If AppTerminate() Then
       Notify("Close button clicked. App killed. :)")
       ' insert garbage clear up here <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
       End 
    EndIf 
    
    If AppSuspended() Then
       Notify("Minimize button clicked. App suspended.")
       ' insert idle mode here <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    EndIf 

	If iszoomed(hWnd) Then
        Notify("Maximize button clicked. App getting large.")
        ' insert screenmode change here <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
 		EndGraphics()
		Graphics(800, 600, 32, 60)
	EndIf
    GCCollect() ' Garbage Collect, just to be 100% sure  
Wend
End
' End of a little cute App!
' --------------------------------------------------------------

Function enableMaximize(hWnd:Long)
' Adds the Maximize Button "[]"
	Local tmp:Int = GetWindowLongA( hWnd, GWL_STYLE )
	tmp = tmp | WS_MAXIMIZEBOX
	SetWindowLongA( hWnd, GWL_STYLE, tmp )
	DrawMenuBar( hWnd )
End Function

Function enableMinimize(hWnd:Long)
' Adds the Minimize Button "_"
	Local tmp:Long = GetWindowLongA( hWnd, GWL_STYLE )
	tmp = tmp | WS_MINIMIZEBOX
	SetWindowLongA( hWnd, GWL_STYLE, tmp )
	DrawMenuBar( hWnd )
End Function
