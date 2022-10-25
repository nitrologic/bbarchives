; ID: 2093
; Author: mindstorms
; Date: 2007-08-17 02:47:17
; Title: screensaver code
; Description: preview window works!

; ID: 2093
; Author: mindstorms
; Date: 2007-08-17 02:47:17
; Title: screensaver code
; Description: preview window works!

Global title$ = ""
Const GWL_STYLE	= -16
Const GWL_HWNDPARENT = -8
Const WS_CHILD = $40000000
Const WM_DESTROY                = $0002
Const WM_CLOSE                  = $0010
Const WM_KEYDOWN                = $0100
Const WM_SYSKEYDOWN             = $0104
Const WM_MOUSEMOVE              = $0200
Const WM_LBUTTONDOWN            = $0201
Const WM_RBUTTONDOWN            = $0204
Const WM_MBUTTONDOWN            = $0207
Const WS_VISIBLE          		= $10000000

Global g_width,g_height,hParent
Global w_message.MSG = New MSG
Global orig_pt.POINT = New POINT

Type tRECT
	Field x, y, w, h
End Type

Type MSG
	Field hwnd
	Field message
	Field wParam
	Field lParam
	Field time$
	Field pt.POINT
End Type

Type POINT
	Field x
	Field y
End Type

;call to start the screensaver
Function main(mtitle$="base screensaver")
	title = mtitle 
	AppTitle title
	If CommandLine$() <> "" Then                                ; If Parameter is present then 
		If Upper(Left$(CommandLine$(),2)) = "/S" Then Start()     ;  or Screensaver itself should be started
		If Upper(Left$(CommandLine$(),2)) = "/P" Then Start()		;or preview window  
	EndIf
	;I have noticed that sometimes windows will not give /C...it will give nothing.  
	;It does not give nothing otherwise, so this works (make sure start ends...)
	Configure() 
End Function

;********************************User Overwrites these functions********************************************
;use the function name without the _, these just examples
Function Configure_() ;for settings button
	;put your configure screen here
	End
End Function   

Function ExitScreensaver_()
	End
End Function

Function ScreenSaverMainLoop_()
	Repeat 		;main loop
		ClsColor(255,0,0)
		Cls
		eventHandler()				;must call this at least once each loop...
		;screensaver code here
		Flip 
		Delay 1	
	Forever
End Function
;*************************************************************************************************************



;***************************************internal functions****************************************************
Function Start()	;actual screen saver
	Local c$ = CommandLine()
	hParent = Int(Right(c,Len(c)-Instr(c," ")))
	blitz_hnd = api_FindWindow("Blitz Runtime Class", title$)
	;if there is a parent waiting...
	If api_IsWindow(hParent)
		Local r.tRECT = New tRECT
		api_GetClientRect(hParent,r)
		g_width = r\w
		g_height = r\h
		Graphics3D g_width,g_height,0,2
		api_SetWindowLong(blitz_hnd, GWL_STYLE, WS_CHILD)
		api_SetParent(blitz_hnd,hParent)
		api_MoveWindow(blitz_hnd,r\x,r\y,g_width,g_height,1)
		
	;otherwise regular screensaver stuff
	Else
		hParent = 0
		g_width = api_GetSystemMetrics(0)
		g_height = api_GetSystemMetrics(1)
		Graphics3D g_width, g_height, 0, 2
		api_SetWindowLong(blitz_hnd, GWL_STYLE, WS_VISIBLE)
		api_MoveWindow(blitz_hnd,0, 0, g_width, g_height, 1)
		
		FlushKeys()                   ; clean keyboardbuffer
		FlushMouse()                  ; clean mousebuffer
		HidePointer()  
		api_GetCursorPos(orig_pt)		;set mouse point
	EndIf
	
	ScreenSaverMainLoop()
End Function

Function eventHandler()
	While api_PeekMessage(w_message,0,0,0,1)
		api_TranslateMessage(w_message)
		
		If hParent Then
			Select w_message\message
				Case WM_DESTROY:
					api_PostQuitMessage(0)
				Case WM_CLOSE:
					ExitScreensaver()
				Default:
					api_DefWindowProc(w_message\hwnd,w_message\message,w_message\wParam,w_message\lParam)		
			End Select	
		Else
			Select w_message\message
				Case WM_DESTROY:
					api_PostQuitMessage(0)
				Case WM_CLOSE:
					ExitScreensaver()
				Case WM_MOUSEMOVE:
					pt.POINT = New POINT
					api_GetCursorPos(pt)
					If Abs(pt\x-orig_pt\x)>10 Or Abs(pt\y-orig_pt\y)>10 Then 
						api_PostMessage(w_message\hwnd,WM_CLOSE,0,0)
					EndIf
				Case WM_LBUTTONDOWN, WM_RBUTTONDOWN, WM_MBUTTONDOWN, WM_KEYDOWN, WM_SYSKEYDOWN:
					api_PostMessage(w_message\hwnd,WM_CLOSE,0,0) 
				Default:
					api_DefWindowProc(w_message\hwnd,w_message\message,w_message\lParam,w_message\wParam)						
			End Select	
		EndIf
	Wend
	Return True
End Function
