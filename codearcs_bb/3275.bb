; ID: 3275
; Author: Dan
; Date: 2016-06-15 19:00:34
; Title: B3D Screensaver with working preview
; Description: Screensaver with working preview, from MANIAK_dobrii

;Athor: MANIAK_dobrii

;Based at Grisu-s tutorial
;I made it real

;Known Isues: Minimisable, ctr-alt-delABLE, don't sent message to Windows, that screensaver is active

;P.S. There are a lot of different things to do, but that sourse just shows, that you can drow your 3D scene at any window you want
;There will be some updates, i think
;P.P.S. Sorry of my English:)
;
;###################################################################################################
;		User32.decls		 needed for Preview:					   (this are the working ones !)
;###################################################################################################
;.lib "user32.dll"
;
;api_BeginDeferWindowPos%(nNumWindows%):"BeginDeferWindowPos"
;api_EndDeferWindowPos%(hWinPosInfo%):"EndDeferWindowPos"
;api_GetWindowLong%(hWnd%,nIndex%):"GetWindowLongA"
;api_IsIconic%(window%):"IsIconic"
;api_IsWindow% (hwnd%) : "IsWindow"
;api_SetActiveWindow%(hWnd%):"SetActiveWindow"
;api_SetWindowLong%(hWnd%,nIndex%,dwNewLong%):"SetWindowLongA"
;api_SetParent%(hWndChild%,hWndNewParent%):"SetParent"
;api_ShowCursor% (bShow%) : "ShowCursor"
;api_ShowWindow%(hWnd%,nCmdShow%):"ShowWindow"
;
;APISCR_InvalidateRect%(hWnd%,lpRect%,bErase%):"InvalidateRect"
;APISCR_DeferWindowPos%(hWinPosInfo%,hWnd%,hWndInsertAfter%,x%,y%,cx%,cy%,uFlags%):"DeferWindowPos"
;APISCR_FindWindow%(lpClassName,lpWindowName$):"FindWindowA"           
;APISCR_GetClassName%(hWnd%,lpClassName*,nMaxCount%):"GetClassNameA" 
;####################################################################################################
;		Kernel32.decls		needed for mutex
;####################################################################################################
; lib kernel32.decls:
;
; CreateMutex%(lpMutexAttributes%,bInitialOwner%,lpName$):"CreateMutexA"
; api_GetLastError% () : "GetLastError"
; api_ReleaseMutex%(Handle%):"ReleaseMutex"
;####################################################################################################

Global ScreenX% =  1024 
Global ScreenY% =  768
Global ScreenD% =  32

Const ERROR_ALREADY_EXISTS=183			;For mutex
Global hMutex

Const WS_VISIBLE = $10000000
Const WS_CHILD = $40000000
Const GWL_STYLE = (-16)
Const SW_SHOW = $5
Const SWP_FRAMECHANGED = $20
Const SWP_SHOWWINDOW = $40
Const SW_HIDE = $0
Const GWL_HINSTANCE = (-6)

ChangeDir SystemProperty$("appdir") ; If you have any outside resourses

Global curwindow
Global hModule
Global preview_window%

Global timer=MilliSecs() , z=1	;You can Delete this, as it is used for demo text purpose.

If CommandLine$() <> "" Then
    CL$=Upper(Left$(CommandLine$(),2))
	Select True
		Case cl$="/C" 
		    If Mutex("My Saver C")=1 Then Mutex() ;Prevents multiple instances of this Screensaver (but allows config/Preview and Normal start)
			Configure() ;Configure the screensaver
			
		Case CL$="/S" 
		    If Mutex("My Saver S")=1 Then Mutex() 
			Start()     ;Start the screensaver itself
			
		Case CL$="/P"
			If Mutex("My Saver P")=1 Then Mutex() 
			Preview()   ;Render Screensaver in preview window
			
    End Select
	
EndIf 

Mutex() ; Free it

End		;The End



;Just render(i use function, instead the body code, 'cos there will be a half-size program(Render() calls twice: in Preview() and Start()))
Function Render(previev=1)
;////////////////////////////////////////////////////
	If previev=1 Then Cls
	Color $ff,$ff,$ff
	txt$="Screensaver with working preview window ..."
	
	Local l=6
	Local lol$=String(" ",l)
	
	If MilliSecs()-timer>250
		timer=MilliSecs()
		z=z+1 : If z=> Len(txt$)+l Then z=1
	EndIf
	
	txt1$=Mid$(lol$+txt$+lol$,z,l)
	
	For y=-1 To 1
		For x=-1 To 1
			Text 10+x,10+y,txt1$
		Next
	Next
	
	Color 0,0,0
	Text 10,10,txt1$
	Flip
	If previev=1 Then APISCR_InvalidateRect curwindow,0,True
;\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
End Function

;Initialises the Screen,settings etc
Function CreateEnv(Mode=2)
	;Mode 2 (windowed) for preview, Mode 0 (Fullscreen) - Screensaver
	
	Graphics ScreenX%,ScreenY%,ScreenD%,Mode
	
	;If Mode=2 Then Graphics ScreenX%,ScreenY%,ScreenD%,3  ;Resizeable window, for fullscreen preview
	
	SetBuffer BackBuffer()
End Function


Function Configure()
;Configuration of your Screensaver goes here.

End Function


;    Edit the Above functions, no need to edit below !

;////////////////////////////////////////////////////////////////////////////////////////////////////////////////////|
;\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\|

Function Preview()
;The "meat" of the Preview code
	wnd%=Int(Mid(CommandLine(),3))
	
	BlitzClass$ = GetBlitzWindowClass$()
	
	hModule = api_GetWindowLong(curwindow,GWL_HINSTANCE)
	
	preview_window% =wnd%
	
	CreateEnv()
	
	BB_3D_View = GraphicPreview(preview_window%,0,0,152,112,0)
	
	ActivateWindow preview_window%
	
	While api_IsWindow(preview_window)
		Delay 1
		Render()
	Wend
End Function

Function Start()
;Normal Main loop - Fullscreen
	CreateEnv(1)					;
	
	FlushKeys()						;Clear Keys and mouse
	FlushMouse()
	MoveMouse 0,0
	
	api_ShowCursor(0)						;Hides the cursor
	
	Repeat									;Main Loop
	    Cls
		Delay 1
		Render(0)							;Screensaver code
	Until GetMouse() <> 0 Or MouseX() <> 0 Or MouseY() <> 0 Or GetKey() <> 0
	
	;Code to release the images
	api_ShowCursor(1)						;Show the cursor 
End Function


;////////////////////////////////////////
;Sets BB Graphic window onto the window(obj) with x&y coordinates(obj's local) and width&height with style(0 or 1)
Function GraphicPreview(obj,x,y,width,height,style=0)	;Set style to 0 or else the saver preview window wont stop, when switching to other screensaver
	Select style
		Case 0 
			Cstyle = WS_VISIBLE Or WS_CHILD 
		Case 1
			Cstyle = 0
	End Select
	api_SetWindowLong curwindow,GWL_STYLE,Cstyle
	api_SetParent curwindow,obj
	
	SetWindowPosL(curwindow,x,y,width,height)
	ActivateWindow(obj)
	
	Return curwindow	
End Function

Function ActivateWindow(hWnd)
	api_ShowWindow(hWnd,SW_SHOW)
	api_SetActiveWindow(hWnd)
End Function

Function SetWindowPosL(obj,x,y,width,height)
	r = api_BeginDeferWindowPos(1);
	t = APISCR_DeferWindowPos(r,obj,0,x,y,width,height,SWP_FRAMECHANGED Or SWP_SHOWWINDOW);
	api_EndDeferWindowPos(r);
	
	api_ShowWindow(obj,SW_SHOW);
	api_SetActiveWindow(obj);
End Function

Function GetBlitzWindowClass$()
	RUNTIME_Window$ = "MSE"+Rand(10000,99999)
	
	AppTitle RUNTIME_Window
	
	curwindow = APISCR_FindWindow("",RUNTIME_Window$)
	api_ShowWindow curwindow,SW_HIDE
	
	CLASS_NAME = CreateBank(256)
	BLITZ_CLASS_NAME_BANK = CreateBank(0)
	StringLen = APISCR_GetClassName(curwindow,CLASS_NAME,BankSize(CLASS_NAME))
	ResizeBank BLITZ_CLASS_NAME_BANK,StringLen
	CopyBank CLASS_NAME,0,BLITZ_CLASS_NAME_BANK,0,StringLen
	
	FreeBank CLASS_NAME
	
	
	BLITZ_CLASS$ = ""
	For loop = 0 To StringLen - 1
		BLITZ_CLASS$ = BLITZ_CLASS$ + Chr(PeekByte(BLITZ_CLASS_NAME_BANK,loop))
	Next
	
	Return BLITZ_CLASS$	
End Function

Function Mutex(MutexName$="")
;To set a mutex, enter a name
;call it again without parameters to end the duplicate start!

;Copy following lines at the beginning of your program and uncomment
;Const ERROR_ALREADY_EXISTS=183			;For mutex
;Global hMutex

;example run:
;If Mutex("My Saver C")=1 Then Mutex() ;Prevents multiple instances 
    
	If Len(MutexName$)>0
		hMutex=CreateMutex(0,1,MutexName$) ; Mutex accessible to any program, change it for other programs 
		If api_GetLastError() = ERROR_ALREADY_EXISTS Then Return 1
	Else
		api_ReleaseMutex(hMutex)
		End 						;To end or not to end ?!?
	EndIf
End Function
