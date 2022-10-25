; ID: 1730
; Author: SebHoll
; Date: 2006-06-08 13:39:37
; Title: MaxGUI: Make a GUI Window Flash
; Description: *For Windows Only ATM

SuperStrict

Rem
######################################
'MARK: TYPE DECLARATION
######################################
EndRem

Type TFLASHWINFO
	
	Field chSize:Int	'Size of structure in bytes
	Field hwnd:Int		'hWND of Window to flash
	Field dwFlags:Int	'Flags for window flash
	Field uCount:Int	'How many times to flash
	Field dwTimeout:Int 'Flashrate (in millisecs). If "0" then default is used.
	
End Type

Rem
######################################
'MARK: GLOBAL DECLERATION
######################################
EndRem

Global FlashWindowEx(typTFlashWInfo:Byte Ptr)
Global user32:Int = LoadLibraryA("user32.dll") 

FlashWindowEx = GetProcAddress(user32:Int,"FlashWindowEx")


Global wndMain:TGadget = CreateWindow("Test Window" , 400 , 300 , 400 , 300 , Null , 15)

	Global gadFlash:TGadget = CreateButton("Flash Window" , 5 , 5 , 100 , 12 , wndMain:TGadget , BUTTON_CHECKBOX)

Rem
######################################
'MARK: EVENT BLOCK
######################################
EndRem

Repeat
	
	Select WaitEvent()
	
		Case EVENT_GADGETACTION
		
			Select EventSource()
			
				Case gadFlash ; FlashWindow(wndMain:TGadget, ButtonState(gadFlash:TGadget), 500)
			
			EndSelect
	
		Case EVENT_WINDOWCLOSE ; End
	
	EndSelect
	
Forever


Rem
######################################
'MARK: FUNCTION BLOCK
######################################
EndRem

Function FlashWindow(window:TGadget , flags:Byte = 1 , dwTimeout:Int = 0)
	
	Assert window:TGadget <> Null , "No window gadget passed to function."
	Assert flags:Byte <= 2 , "Invalid flag ("+flags+") passed: use only 0, 1 or 2."
	
	Local dwFlags:Int
	Local FLASHWINFO:TFLASHWINFO = New TFLASHWINFO
	
	Select flags:Byte
	
		Case 1 ; dwFlags:Int = 7 	'Flashes continuously until function called with flag 0
	
		Case 2 ; dwFlags:Int = 15	'Flashes until window has focus
		
		Case 0 ; dwFlags:Int = 0	'Stops flashing
		
	EndSelect
	
	FLASHWINFO.chSize:Int = SizeOf(FLASHWINFO:TFLASHWINFO)
	FLASHWINFO.hwnd:Int = QueryGadget(window:TGadget,1)
	FLASHWINFO.dwFlags:Int = dwFlags
	FLASHWINFO.uCount:Int = 0
	FLASHWINFO.dwTimeout:Int = dwTimeOut
	
	FlashWindowEx(FLASHWINFO)

EndFunction
