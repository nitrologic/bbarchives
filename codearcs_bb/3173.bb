; ID: 3173
; Author: Jimmy
; Date: 2015-01-06 07:30:08
; Title: Customize icon
; Description: Customize your app icon without fuss

;
; Customize app icon
;
; Description:
; Changes your app icon everywhere inside an app with minimal effort, no dll´s and no recompiling.
; It changes the icon at the Titlebar, taskbar and and shift tabbing.
; How to use:
; 1) Use any app (eg resource hacker) to edit the exe file´s icon as you see fit.
; 2) Put this code into your app
; ------------------------------------------------------------
; You need these changes, and notice the % in lParam
;
; In file user32.decls ADD:
; api_SendMessage% (hwnd%, wMsg%, wParam%, lParam%) : "SendMessageA"
; 
; In file Shell32.decls ADD:
; api_ExtractIcon% ( hWnd%, File$, Index% ) : "ExtractIconA"
; ------------------------------------------------------------

; Changes icon, the short version
; Hwnd=QueryObject(createwindow("appname",100,100,400,300,0,5),1) 
; icon=api_ExtractIcon(hwnd,"myapp.exe",0)
; api_SendMessage(hwnd, $80 , 0, icon) : api_SendMessage(hwnd, $80 , 1, icon)

; Working BlitzPlus example follows
window=CreateWindow( "Test",ClientWidth(Desktop())/2-96,ClientHeight(Desktop())/2-96,192,192,0,7)
appname$="appname" : AppTitle appname$
hwnd=QueryObject(window,1)
icon=api_ExtractIcon(hwnd,"myapp.exe",0)
api_SendMessage(hwnd, $80 , 0, icon) : api_SendMessage(hwnd, $80 , 1, icon)

width=ClientWidth(window) : height=ClientHeight(window)
canvas=CreateCanvas( 0,0,width,height,window )
SetGadgetLayout canvas,1,1,1,1
SetBuffer CanvasBuffer(canvas)

While WaitEvent(10)<>$803
	mxMouseX()-width/2: my=MouseY()-height/2
	Cls
	CopyRect mx,my,width,height,0,0,DesktopBuffer()
	FlipCanvas canvas
Wend
