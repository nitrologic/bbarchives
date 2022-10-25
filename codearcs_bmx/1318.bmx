; ID: 1318
; Author: Eikon
; Date: 2005-03-09 08:34:27
; Title: BlitzMax Window Framework
; Description: Give your window a title and center it on screen

' //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
' // BlitzMax Window Framework by Eikon
' //                                modified by Grisu and GreyAlien
' // BMX 1.18
' //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
Strict
' // Framework & Modules //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
Framework BRL.Max2D
Import BRL.D3D7Max2D

' // Win32 API //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
Extern "win32"
   Function GetActiveWindow%()
   Function GetDesktopWindow%()
   Function GetWindowRect%(hWnd%, lpRect:Byte Ptr)
   Function SetWindowText%(hWnd%, lpString$z) = "SetWindowTextA@8"
   Function SetWindowPos%(hWnd%, after%, x%, y%, w%, h%, flags%)
End Extern

Type lpRECT
   Field l%, t%, r%, b%
End Type

' // Create Window %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%//
Const GFX_WIDTH = 1024, GFX_HEIGHT = 768, BIT_DEPTH = 0, HERTZ = -1

SetGraphicsDriver D3D7Max2DDriver()
Graphics GFX_WIDTH, GFX_HEIGHT, BIT_DEPTH, HERTZ
Local hWnd% = GetActiveWindow()
Local desk_hWnd% = GetDesktopWindow(), l:lpRect = New lpRECT
Local window:lpRect = New lpRect

GetWindowRect desk_hWnd, l:lpRECT ' Get Desktop Dimensions
SetWindowText hWnd, "My Window"  ' Set Window Text
' Get Window Dimensions because final window may have been resized to fit the desktop resolution! (Grey Alien)
GetWindowRect hWnd, window:lpRECT

' Center Window
SetWindowPos hWnd, -2, (l.r / 2) - ((window.r-window.l) / 2), (l.b / 2) - ((window.b-window.t) / 2), 0, 0, 1

Repeat

	SetColor 255,200,0
	DrawText "test",0,0
	DrawLine 100,0,210,0 'top
	DrawLine 100,767,210,767 'bottom	
	DrawLine 0,100,0,210 'left
	DrawLine 1023,100,1023,210 'right
	
Flip; Until KeyDown(KEY_ESCAPE) Or (AppTerminate()=True) ' also exit if windowbutton is used!!!
