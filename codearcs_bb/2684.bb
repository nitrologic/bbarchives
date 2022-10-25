; ID: 2684
; Author: Charrua
; Date: 2010-03-31 16:31:16
; Title: Multiple Keyboards Handling, Blitz3D version
; Description: Use many keyboards and identify from which each key were pressed. Windows send to the application the sum of all, with this code you can read input selectivelly.

;	Multiple Keyboard Handling
;	by Juan Ignacio Odriozola
;
;	based on 2 great works:
;		http://www.codeproject.com/KB/system/rawinput.aspx			<< started with
;		http://blitzbasic.com/Community/posts.php?topic=85660#969498	<< looking for "how to get a handle to my window find this!!!!
;
;	thak's sswift for your exelent work!!, i used much of your code here!
;
;
;
;	One Computer, One Screen (probably big), many users: many Keyboards..., many mice too, but not implemented yet!
;	Why not to have a cursor for each mouse instaled, the same for keyboards
;
;	see: http://www.wunderworks.com/
;
;	I test: KeyBoards, NumericKeypads, WirelessPresenters (for PowerPoint and the like)
;	please advice me about other devices
;
;	I will use this idea for manage multiple keypads to get answers from the audience in congress,
;	at this time i have implemented one system of this kind with dedicated microcontroler based hardware
;	(200 kepads), time to time i redesign the system (hard or soft) but now i decided to use something already done!
;
;	i hope that this has an application in game programming
;
;what the code does:
;	First: 	signals the system to send us KeyBoard events (RegisterRID_KEYBOARD(hWnd:Int))
;	Second:	Obtain the list of Keyboards on the system    ()
;	wait for keyboard events and show what its received
;
;the bmax version is documented better
;
;needs some decls And the free FastPointer lib
;
;all came from User32.decls
;
;
;probably you have the original ones, there are some diferences depending on usage, so the postfix 1, 2..

;api_GetRawInputDeviceList%(pRawInputDeviceList*, uiNumDevices*, cbSize%) : "GetRawInputDeviceList"	;<====  this is called
;api_GetRawInputDeviceList1%(pRawInputDeviceList, uiNumDevices*, cbSize%) : "GetRawInputDeviceList"	;<====  this is called

;api_GetRawInputDeviceInfo%(hDevice%, uiCommand%, pData*, pcbSize%) : "GetRawInputDeviceInfoA"		;		not needed
;api_GetRawInputDeviceInfo1%(hDevice%, uiCommand%, pData%, pcbSize*) : "GetRawInputDeviceInfoA"		;<====  this is called
;api_GetRawInputDeviceInfo2%(hDevice%, uiCommand%, pData*, pcbSize*) : "GetRawInputDeviceInfoA"		;<====  this is called

;api_RegisterRawInputDevices%(pRawInputDevice*, uiNumDevices%, cbSize%) : "RegisterRawInputDevices"	;<====  this is called

;api_GetRawInputData%(hRawInput*, uiCommand%, pData*, pcbSize*, cbSizeHeader%) : "GetRawInputData"	;<====  not needed
;api_GetRawInputData1%(hRawInput%, uiCommand%, pData*, pcbSize*, cbSizeHeader%) : "GetRawInputData"	;<====  this is called

;api_SetWindowLong% (hwnd%, nIndex%, dwNewLong%) : "SetWindowLongA"									;<====  this is called
;api_CallWindowProc% (lpPrevWndFunc%, hWnd%, Msg%, wParam%, lParam%) : "CallWindowProcA"			;<====  this is called

Graphics 640,480,0,2

; ----------------------------------------------------------------------------------------------
;	constants
; ----------------------------------------------------------------------------------------------


Const RIDI_DEVICEINFO%	= $2000000B
Const RID_INPUT 		= $10000003

Const WM_KEYDOWN		= $0100
Const WM_SYSKEYDOWN		= $0104
Const WM_INPUT			= $00FF

Const RIDI_DEVICENAME% 	= $20000007

Const RIM_TYPEMOUSE% 	= 0
Const RIM_TYPEKEYBOARD% = 1	
Const RIM_TYPEHID%		= 2	

; ----------------------------------------------------------------------------------------------
;	type def
; ----------------------------------------------------------------------------------------------

Type tDevice
	Field DeviceNameAsIs$
	Field deviceName$		;sin los 4 del principio: \\?\
	Field hDevice
	Field dwType
	Field ClassCode$		;
	Field SubClassCode$
	Field Protocol$
	Field Guid$
End Type


Type tKeyboard
	Field deviceName$
	Field hDevice
	Field dwType
	Field ClassCode$
	Field SubClassCode$
	Field Protocol$
	Field Guid$
	Field DeviceNameAsIs$
	Field KeyPressed
	Field Key			;estas deberian ser Short!
	Field ScanCode
End Type

Type tRawIK	;Raw Input Keyboard		;c programmers uses unions, here the union is by fact.
	
	;RAWINPUTHEADER:	;first 16 bytes
		Field dwType
		Field dwSize
		Field hDevice
		Field WPARAM
	;RAWINPUTKEYBOARD:	;struct for keyboard
		Field MakeCodeFlags
		;Field Flags
		Field ReservedVKey
		;Field VKey
		Field Message
		Field ExtraInformation
End Type 

Type tRID	;Raw Input Device
	Field Usage	;blitz don't have 16 variables, in this field are 2 16 variables, be careful
				;the hight word must be $0001, the low word 0006 to register keyboard input
	Field Flags 
	Field hWnd	;my window
End Type


;------------------------------------------------------------------------------------------------


;Global raw.tRawIK = New tRawIK


Global OldWinProc								;old handler

Local hWnd = SystemProperty("AppHWND")				;my window

Global WinProcPointer = FunctionPointer()		;our_handler, we call old_handler from our handler to continue the chain
	Goto skip
	WinProc(0,0,0,0)
	.skip

varRid.tRID = New tRID
varRid\hWnd = hWnd
varRid\Flags = 256
varRid\Usage = $00060001

;------------------------------------------------------------------------------------------------
;	say: i want to receive raw input, please!
;------------------------------------------------------------------------------------------------

Local Result = api_RegisterRawInputDevices(varRid,1,12)

;------------------------------------------------------------------------------------------------
;	ok, tell me wich of your Funtions i should call
;------------------------------------------------------------------------------------------------

If Result Then HookWinProc(hWnd)

;------------------------------------------------------------------------------------------------


Print "RegisterRawInputDevices: "+Result


Local k.tKeyboard

;------------------------------------------------------------------------------------------------
;	Get the list of devices
;------------------------------------------------------------------------------------------------

nDevices = GetDeviceList()	;populates a list of tDevice objects

If nDevices Then

	For d.tDevice = Each tDevice
		If d\dwType = RIM_TYPEKEYBOARD Then	;we only want Keyboards 
			If d\ClassCode<>"ROOT" Then		;and not The Root device
				k = New tKeyboard			;simply translate the info from Device to Keyboard
				k\hDevice = d\hDevice		;this is the ID from wich we identify the keyboard
				k\dwType = d\dwType
				k\DeviceName = d\deviceName
				k\ClassCode = d\ClassCode$
				k\SubClassCode = d\SubClassCode$
				k\Protocol = d\Protocol$
				k\Guid = d\Guid$
				k\DeviceNameAsIs = d\DeviceNameAsIs
			End If
		End If
	Next		

	For k=Each tKeyboard
		Print k\hDevice
		Print k\DeviceName
		Print k\ClassCode
		Print k\SubClassCode
		Print k\Protocol
		Print k\Guid
		Print 
	Next
	
	Print hWnd + " - " + OldWinProc
		
End If

Print
Print "press any key...."
WaitKey		;this is only to see the list of detected keyboards


;in the main loop we see the ID, ascii and scan code for each device

If nDevices Then

	While Not KeyHit(1)
	
		Cls

		n=0
		For k=Each tKeyboard
			Text 30,30+n*20, k\hDevice + ": "+Asc(k\Key)+", Scan:"+k\ScanCode
			n=n+1
		Next
			
	Wend
	
End If

End


; ----------------------------------------------------------------------------------------------
; hWnd is the pointer To your window.
; ----------------------------------------------------------------------------------------------

Function HookWinProc(hWnd)
	Local GWL_WNDPROC = -4
	OldWinProc = api_SetWindowLong(hWnd, GWL_WNDPROC, WinProcPointer)
End Function  


; ----------------------------------------------------------------------------------------------
; This Function is called automatically.
; ----------------------------------------------------------------------------------------------


Function WinProc( hWnd, Msg, wParam, lParam )

	Local Item.tKeyboard
	Local raw.tRawIK = New tRawIK
	
	Select Msg

		Case WM_INPUT
		
			SizeBank = CreateBank(4)
			PokeInt SizeBank,0,32
			
			Result = api_GetRawInputData1(lParam, RID_INPUT, raw, SizeBank, 16)
			
			FreeBank SizeBank
			
			If LWord(Raw\Message) Then
				gwParam = LWord(Raw\Message)
				glParam = LWord(Raw\ReservedVKey)
			End If

			If ( LWord(Raw\Message = WM_KeyDown) Or LWord(Raw\Message = WM_SYSKEYDOWN) ) Then
				If HWord(Raw\ReservedVKey) < 254 Then
					For Item = Each tKeyboard
						If raw\hDevice = Item\hDevice Then
							Item\KeyPressed = True
							Item\Key = HWord(Raw\ReservedVKey)
							Item\ScanCode = LWord(Raw\MakeCodeFlags)
						End If
					Next
				End If
			End If
			
	End Select
	
	Delete raw
	
	If OldWinProc <> 0 Then
		Return api_CallWindowProc(OldWinProc, hWnd, Msg, wParam, lParam)
	End If
		
End Function

;--------------------------------------------------------------------------------


Function GetDeviceList()

	Local dCount, Result, DeviceList, Count
	Local i, ii, hDevice, dwType, cbSizeBank, cbSize
	Local pData, DeviceName$, Ultimo, Primero, Parte1$, Parte2$, Parte3$, Parte4$
	Local Pos, Root, DeviceNameAsIs$
	Local d.tDevice
	Local DeviceCount = CreateBank(4)
	
	
	PokeInt DeviceCount,0,0
	Result = api_GetRawInputDeviceList1(0,DeviceCount,8)
	
	If Result<>-1 Then
		DeviceList = CreateBank(DeviceCount*8)
		Count = api_GetRawInputDeviceList(DeviceList,DeviceCount,8)
		For i=0 To Count-1
			hDevice = PeekInt(DeviceList,i*8)
			dwType = PeekInt(DeviceList,i*8+4)
			cbSizeBank=CreateBank(4)
			api_GetRawInputDeviceInfo1(hDevice, RIDI_DEVICENAME, 0, cbSizeBank) 
			cbSize = PeekInt(cbSizeBank,0)
			If (cbSize > 0) Then
				pData = CreateBank(cbSize)
				Result = api_GetRawInputDeviceInfo2(hDevice, RIDI_DEVICENAME, pData, cbSizeBank)
				cbSize = PeekInt(cbSizeBank,0)
				DeviceName$ = ""
				For ii=0 To cbSize-1
					DeviceName = DeviceName + Chr(PeekByte(pData,ii))
				Next
				FreeBank pData
				
				Pos = Instr(DeviceName,"}",1)
				If Pos Then DeviceName = Left(DeviceName,Pos)
				DeviceNameAsIs$ = DeviceName
				DeviceName$ = Upper(Right(DeviceName,Len(DeviceName)-4))
				Root = Instr(DeviceName,"ROOT",1)
				Ultimo=0
				Primero=Ultimo+1
				Ultimo=Instr(DeviceName,"#",Primero)
				Parte1$ = Mid(DeviceName,Primero,Ultimo-Primero)
				Primero=Ultimo+1
				Ultimo=Instr(DeviceName,"#",Primero)
				Parte2$ = Mid(DeviceName,Primero,Ultimo-Primero)
				Primero=Ultimo+1
				Ultimo=Instr(DeviceName,"#",Primero)
				Parte3$ = Mid(DeviceName,Primero,Ultimo-Primero)
				Primero=Ultimo+1
				Ultimo=Instr(DeviceName,"#",Primero)
				Parte4$ = Mid(DeviceName,Primero,Ultimo-Primero)
				dCount = dCount+1
				d.tDevice = New tDevice
				d\deviceName = DeviceName
				d\hDevice = hDevice
				d\dwType = dwType
				d\ClassCode$ = Parte1
				d\SubClassCode$ = Parte2
				d\Protocol$ = Parte3
				d\Guid$ = Parte4
				d\DeviceNameAsIs = DeviceNameAsIs
				
			End If
			FreeBank cbSizeBank
		Next
		
		FreeBank DeviceList
		FreeBank DeviceCount
		Return dCount
		
	Else
		
		FreeBank DeviceCount
		Return -1
		
	End If
	
End Function


;--------------------------------------------------------------------------------

Function LWord(I) ; Returns the Lower 16 bits of a 32 bit integer.
	Return I And $FF
End Function

Function HWord(I) ; Returns the Upper 16 bits of a 32 bit integer.
	Return I Shr 16
End Function

;--------------------------------------------------------------------------------
