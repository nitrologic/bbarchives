; ID: 2683
; Author: Charrua
; Date: 2010-03-31 07:20:11
; Title: Multiple Keyboards Handling
; Description: Use many keyboards and identify from which each key were pressed. Windows send to the application the sum of all, with this code you can read input selectivelly,

Strict

'	Multiple Keyboard Handling
'	by Juan Ignacio Odriozola
'
'	based on 2 great works:
'		http://www.codeproject.com/KB/system/rawinput.aspx			<< started with
'		http://blitzbasic.com/Community/posts.php?topic=85660#969498	<< looking for "how to get a handle to my window find this!!!!
'
'	thak's sswift for your exelent work!!, i used much of your code here!
'
'  		probably you better than i, could modify your RAWINPUT.BMX to Registry both KeyBoard and Mice devices
' 		and the WinProc to handle both kind of devices (the struct RAWINPUTKeyBoard has one modification from your RAWINPUT)
'
'
'	One Computer, One Screen (probably big), many users: many Keyboards..., many mice too, but not implemented yet!
'	Why not to have a cursor for each mouse instaled, the same for keyboards
'
'	see: http://www.wunderworks.com/
'
'	I test: KeyBoards, NumericKeypads, WirelessPresenters (for PowerPoint and the like)
'	please advice me about other devices
'
'	I will use this idea for manage multiple keypads to get answers from the audience in congress,
'	at this time i have implemented one system of this kind with dedicated microcontroler based hardware
'	(200 kepads), time to time i redesign the system (hard or soft) but now i decided to use something already done!
'
'	i hope that this has an application in game programming
'
'what the code does:
'	First: 	signals the system to send us KeyBoard events (RegisterRID_KEYBOARD(hWnd:Int))
'	Second:	Obtain the list of Keyboards on the system    ()
'	wait for keyboard events and show what its received


'types and variables


	Extern "Win32"
		Function RegisterRawInputDevices:Int(pRawInputDevices:Byte Ptr, uiNumDevices:Int, cbSize:Int)
		Function GetRawInputData:Int(hRawInput:Byte Ptr, uiCommand:Int, pData:Byte Ptr, pcbSize:Int Ptr, cbSizeHeader:Int)
		Function GetRawInputDeviceList:Int(pRawInputDeviceList:Byte Ptr, puiNumDevices:Int Ptr, cbSize:Int)
		Function GetRawInputDeviceInfoA:Int( hDevice:Int, uiCommand:Int, pData:Byte Ptr, pcbSize:Int Ptr)
	End Extern

	Global OldWinProc:Int ' This is the old blitzmax WinProc() function pointer.  This is set when you call HookWinProc().  

	Const HID_USAGE_PAGE_GENERIC%  = $1
	Const HID_USAGE_GENERIC_MOUSE% = $2
	Const RIDEV_INPUTSINK% = $100
	Const RIM_TYPEMOUSE% = 0
	Const RID_INPUT% = $10000003
	
	Const RIDI_PREPARSEDDATA = $20000005
	Const RIDI_DEVICEINFO = $2000000B
	
	Type RAWINPUTDEVICE
	   Field usUsagePage:Short
	   Field usUsage:Short
	   Field dwFlags:Int
	   Field hwndTarget:Int
	End Type
	
	Global Rid:RAWINPUTDEVICE = New RAWINPUTDEVICE
	
	'constants not used in RawInput
	Const HID_USAGE_GENERIC_KEYBOARD% = $6
	Const RIM_TYPEKEYBOARD%  = 1	
	Const RIM_TYPEHID%       = 2	
	Const RIDI_DEVICENAME%   = $20000007
	Const WM_KEYDOWN		= $0100
	Const WM_SYSKEYDOWN	= $0104
	Const WM_INPUT		= $00FF
	
	'RAWINPUT modified
	Type RAWINPUTKeyBoard 
		' RAWINPUTHEADER:
			Field dwType:Int
			Field dwSize:Int
			Field hDevice:Int		'byte ptr	i changed this!!!!  <<<<<<<<<<<<<<<<<
			Field WPARAM:Int Ptr 
		' RAWINPUTKEYBOARD:	
			Field MakeCode:Short
			Field Flags:Short
			Field Reserved:Short
			Field VKey:Short
			Field Message:Int
			Field ExtraInformation:Int
	End Type 
	
	Type RID_DEVICE_INFO		'32 bytes in total
		Field cbSize:Int
		Field dwType:Int
		'particulary for keyboards info
		Field dw_Type:Int
		Field dwSubType:Int
		Field dwKeyboardMode:Int
		Field dwNumberOfFunctionKeys:Int
		Field dwNumberOfIndicators:Int
		Field dwNumberOfKeysTotal:Int
	End Type
	
	Type Keyboard
		Field deviceName:String
		Field hDevice:Int
		Field dwType:Int
		Field ClassCode:String		'sub strings of deviceName
		Field SubClassCode:String	'Vid= Vendor Id, Pid=Product Id	important to find specific hardware!
		Field Protocol:String		
		Field Guid:String
		Field KeyPressed:Int		'has been pressed?
		Field Key:Short			'the one that has been if any (ascii code if any)
		Field ScanCode:Short		'the key number in the keyboard matrix
	End Type
	
	Global lstKeyboards:TList = New TList
	Global KeyboardsCount
	Local Item:Keyboard

'end Types and variables

' from RAWINPUT.BMX
' ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
' This function registers the KEYBOARD as a raw input device, so that WM_INPUT events will be generated for it.
' hWnd is the pointer to your window.  See HookWinProc() above for more details.
' ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


	Function RegisterRID_KEYBOARD(hWnd:Int)
		
		Rid.usUsagePage = HID_USAGE_PAGE_GENERIC
		Rid.usUsage = HID_USAGE_GENERIC_KEYBOARD
		Rid.dwFlags = RIDEV_INPUTSINK
		Rid.hwndTarget = hWnd
		RegisterRawInputDevices(Rid, 1, SizeOf(Rid))

	End Function


' ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
'	fills the list: lstKeyboards with the Keyboards find in the system
' ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	Function GetKeyboardList()
	
		Local Result:Int, i:Int
		Local hDevice:Int		'field of DeviceInfoList struct not defined
		Local dwType:Int		'DeviceList should be an Array of DeviceInfoList
		
		Local uiNumDevices:Int, puiNumDevices:Int Ptr = Varptr uiNumDevices
		Local cbSize:Int, Data:TBank, pData:Byte Ptr, deviceName:String, split:String[]
		Local Item:Keyboard
		
		Result = GetRawInputDeviceList(Null,puiNumDevices,8)	'if first param = null, this func return the number of Devices in the list
		
		Local List:TBank = CreateBank(uiNumDevices*8)		'strictly an array of unNumberDevices of Type RawInputDeviceList : 2 Int vars: dwType,hDevice
		Local DeviceList:Byte Ptr = LockBank(List)
		
		Result = GetRawInputDeviceList(DeviceList, puiNumDevices, 8)	'this second call populates the Array (Bank) wiht the device list
		
		'the devicename could be used to get additional information from the windows registry
		'some tipical strings: 
		'	/??/HID#VID_05A4&PID_9840#6&145a460c&0&0000#{884b96c3-56ef-11d1-bc8c-00a0c91405dd}
		'	/??/ACPI#PNP0303#4&7989e7a&0#{884b96c3-56ef-11d1-bc8c-00a0c91405dd}
		'
		'the "#" character separates "fields" inside that devicename
		'the following code, separates the DeviceName in 4 Fields, they may be used concatenated later to reach the registry
		'or individually to find the VendorID and ProductID if you want to manage specific hardware
		'
		' HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Enum\ ClassCode \ SubClassCode \ Protocol	'guid isnt needed!
		' the Class key confirm if it is a KeyBoard
		'{ "HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Enum\" + Item.ClassCode + "\" + Item\SubClassCode + "\" + Item.Protocol }
		'
		'more about Registry	(i guess you have to have some administrator level to reach this variables from code
		'please, i some one could reach them, post the solution, i used the following code wihtout success
		'
		
		'get the devicename of each device
		
		For i=0 To uiNumDevices-1
			hDevice = PeekInt(List,i*8+0)	'calculates the offset inside the DeviceList of the individual items
			dwType  = PeekInt(List,i*8+4)
						
			GetRawInputDeviceInfoA( hDevice, RIDI_DEVICENAME, Null, Varptr cbSize)	'get the lenght of the DeviceName string in cbSize
		
			If cbSize>0 Then
		
				Data = CreateBank(cbSize)	'allocates cbSize bytes to hold the DeviceName string
				pData= LockBank(Data)
				GetRawInputDeviceInfoA( hDevice, RIDI_DEVICENAME, pData, Varptr cbSize)	'second call to get the DeviceName in pData
				DeviceName = String.frombytes(pData,cbSize)
				'pad the firs 4	"\??\"
				DeviceName = Right$(DeviceName,Len(DeviceName)-4)
				Local temp:Int = DeviceName.find("}",1)
				DeviceName=Left$(DeviceName,temp+1)
				UnlockBank(Data)

				Print "Detected: "+String.fromint(hDevice)+", "+DeviceName

				If dwType=RIM_TYPEKEYBOARD Then	'only keyboards
					
		
					If (deviceName.toupper()).find("ROOT")=-1 Then	'exclude Root device
						Print "      Added to list: "
						KeyboardsCount = KeyboardsCount + 1
						split = devicename.split("#")
						Item = New Keyboard
						lstKeyboards.AddLast(Item)
						Item.hDevice = hDevice
						Item.dwType  = dwType
						Item.deviceName = DeviceName
						item.ClassCode = split[0]
						item.SubClassCode = split[1]
						item.Protocol = split[2]
						item.Guid = split[3]
						Item.KeyPressed=False
						
					Else
						Print "root device: "+devicename	
					End If
				End If
			End If
		Next
		
		UnlockBank(List)
		
	End Function


' from RAWINPUT.BMX

' ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
' hWnd is the pointer to your window.  hWnd is not the gadget pointer! 
' You can get hWnd with hWnd = GetActiveWindow(), or with hWnd = QueryGadget(Window, QUERY_HWND), where Window is your window's gadget pointer.
' ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	Function HookWinProc(hWnd:Int)
		Local GWL_WNDPROC:Int = -4
		OldWinProc = SetWindowLongA(hWnd, GWL_WNDPROC, Int(Byte Ptr(WinProc)))
	End Function  


' from RAWINPUT.BMX but with modifications.
' ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
' This function is called automatically.  It grabs raw windows events before Blitzmax processes them, so that you can handle events that Blitzmax doesn't provide an interface for.
'
' Blitzmax has its own function like this internally, and this one calls that when it is done processing.
' Presumably, events handled by this function will still be passsed onto BlitzMax, so avoid processing the same data in both event handlers!
' ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

	Function WinProc:Int(hWnd:Int, Msg:Int, wParam:Int, lParam:Int) "win32"
	
		Const WM_INPUT% = $00FF
	
		Function LWord:Short(I:Int) ' Returns the lower 16 bits of a 32 bit integer.
			Return I & $FFFF
		End Function
	
		Function HWord:Short(I:Int) ' Returns the upper 16 bits of a 32 bit integer.
			Return I Shr 16
		End Function
		
		
		Select Msg
	
		    Case WM_INPUT

	    			Local dwSize:Int = 32
				Local Raw:RAWINPUTKeyBoard = New RAWINPUTKeyBoard
				Local Item:Keyboard
    
				GetRawInputData(Byte Ptr(lParam), RID_INPUT, Raw, Varptr dwSize, 16) ' Get data in RAWINPUT structure.

				If Raw.dwType = RIM_TYPEKEYBOARD
					If (raw.Message = WM_KEYDOWN Or raw.Message = WM_SYSKEYDOWN) Then
						If raw.vkey < 254 Then
							For Item:Keyboard = EachIn lstKeyboards
								If raw.hDevice = Item.hDevice Then
									Item.KeyPressed = True
									Item.Key = raw.VKey
									Item.ScanCode = raw.MakeCode
									Print String.fromint(raw.hDevice)+", "+String.fromint(raw.MakeCode)+", "+String.fromint(raw.VKey)+", KeyPressed: ("+Chr(raw.vkey)+")" + Item.DeviceName
									Exit
								End If
							Next
						Else
							'DebugLog("extended code")
						End If
					End If
				EndIf
				
			'Default
			'	DebugLog("Unknown Msg: " + Msg)
					
		End Select
	
		If OldWinProc <> 0 Then Return CallWindowProcA(Byte Ptr(OldWinProc), hWnd, Msg, wParam, lParam)   
		
	End Function

' ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
'	MAIN PROGRAM
' ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


Graphics 640,480

Local hWnd:Int

hWnd = GetActiveWindow()

'First
RegisterRID_KEYBOARD(hWnd)

'Second
GetKeyboardList()


Print "Keyboards List:"
Print
Print "Number of keyboards detected: "+KeyboardsCount

For Item:Keyboard = EachIn lstKeyboards
	Print "hDevice: "+String.fromint(item.hDevice)+" = " + item.ClassCode + "\" + item.SubClassCode + "\" + item.Protocol + "\" + item.Guid
Next
Print

'now and finally: RawInput!

HookWinProc(hWnd)

Local char:Int

'one important note: with or without the DeviceList, GetRawInputData knows de hDevice, so identification is posible, but
'without the list, we haven't the knowledge of which devices are installed, if one doesn't press a key, will never appear in
'hDevice from GetRawInputData.

While Not KeyHit(key_escape)
	'really not much here!!
	'ScanCodes are better, when special keys are used: F1, NumPadKeys etc
	char = GetChar()
	If char Then Print "in the main loop, blitz :"+ String.fromint(char)
	'as you see, blitz still get the characters, windows combine the imput of all the HID's in the system
	'but looking in the list of KeyBoards we can identify from who the hit were done
	For Item:Keyboard = EachIn lstKeyboards
		If Item.KeyPressed Then
			Item.KeyPressed = False
			Print "Identified source: "+String.fromint(Item.hDevice)+", "+String.fromint(Item.ScanCode)+", "+String.fromint(Item.Key)
			Exit
		End If
	Next
Wend

End
