; ID: 1437
; Author: Mirko
; Date: 2005-08-05 16:42:07
; Title: Webcam-class for Windows
; Description: A small bmx class to capture pictures from a webcam into a pixmap

'Application: webcam - class
'Author: 		Mirko 'NAPALM' Tocchella
'Description: 	I little wrapper to capture pictures from a webcam and put
'				them into a pixmap	
'				At the end of the source there is a little example
'

Import BRL.System

Import PUB.Win32


extern "Win32"
	function webcam_GetActiveWindow:int () "Win32" = "GetActiveWindow"
	Function webcam_SendMessage( hWnd,MSG,wParam,lParam) "Win32" = "SendMessageA"
	Function webcam_FreeLibrary ( hnd:Int ) "Win32" = "FreeLibrary"
end extern

const WM_CAP_START = WM_USER

const WM_CAP_SET_CALLBACK_ERROR = WM_CAP_START + 2
const WM_CAP_SET_CALLBACK_STATUS = WM_CAP_START + 3
const WM_CAP_SET_CALLBACK_YIELD = WM_CAP_START + 4
const WM_CAP_SET_CALLBACK_FRAME = WM_CAP_START + 5
const WM_CAP_SET_CALLBACK_VIDEOSTREAM = WM_CAP_START + 6
const WM_CAP_SET_CALLBACK_WAVESTREAM = WM_CAP_START + 7

const WM_CAP_DRIVER_CONNECT = WM_CAP_START + 10
const WM_CAP_DRIVER_GET_CAPS = WM_CAP_START + 14

const WM_CAP_DLG_VIDEOFORMAT = WM_CAP_START + 41
const WM_CAP_DLG_VIDEOSOURCE = WM_CAP_START + 42
const WM_CAP_DLG_VIDEODISPLAY = WM_CAP_START + 43

const WM_CAP_GET_VIDEOFORMAT = WM_CAP_START +44
const WM_CAP_SET_VIDEOFORMAT = WM_CAP_START +45

const WM_CAP_SET_PREVIEW = WM_CAP_START + 50
const WM_CAP_SET_OVERLAY = WM_CAP_START + 51
const WM_CAP_SET_PREVIEWRATE = WM_CAP_START + 52
const WM_CAP_SET_SCALE = WM_CAP_START +53
const WM_CAP_GET_STATUS = WM_CAP_START + 54

const WM_CAP_SET_CALLBACK_CAPCONTROL = WM_CAP_START + 85


type TWebcam
	field  Handle:Int
	field  DeviceID:Int
	field  FrameRate:Int
	field  LibHandle:Int
	global Width:Int
	global Height:Int
	global PMWidth:Int
	global PMHeight:Int
	global Image:TPixmap 
	field webcam_CreateCaptureWindow:Int (WName:Byte Ptr, Style:int, X:int, y:int, w:int, h:Int, ..
									  ParentWnd:int, ID:int) "Win32"
	
	method New ()
		Image = CreatePixmap(16,16,PF_RGB888)
		FrameRate = 15
		Handle = NULL
		DeviceID = -1
		' Get the library handle
		LibHandle = LoadLibraryA("AVICAP32.DLL")
		If Not LibHandle Then Throw "Can't load AVICAP32.DLL"
		
		' Get the Adress of the capCreateCaptureWindow function
		webcam_CreateCaptureWindow = GetProcAddress( LibHandle, "capCreateCaptureWindowA" ) 
'		If Not webcam_CreateCaptureWindow Then Throw "Can't get adress of capCreateCaptureWindowA"			
	End Method
	
	method Delete ()
		DeInit()
		FreeLibrary(LibHandle)
		Image = NULL
	End Method
	
	method Init:Int (WHnd:Int, x:int, y:int, w:int, h:Int)
		local res:int = false
		if (Handle <> NULL) DeInit()
		DeviceID = -1
		Handle = Webcam_CreateCaptureWindow (NULL, WS_VISIBLE+WS_CHILD, x, y, w, h, WHnd, 1)
		if (Handle <> NULL)
			res = ConnectDevice()
			if (res) EnablePreviewMode(true)
			if (res) SetFrames(Framerate)			
		endif
		return (res)
	End Method
	
	method DeInit ()
		if (handle <> NULL)
			DestroyWindow (Handle)
			Handle = NULL			
		end if		
		DeviceID = -1
	End Method
	
	Method ConnectDevice:Int ()
		if (deviceID = -1)
  		  for local i:int = 0 to 9
			local res:Int = Webcam_SendMessage(Handle, WM_CAP_DRIVER_CONNECT, i, 0)
			if (res <> 0)
			  DeviceID = i
			  exit
			endif
		  Next
		endif
		return (DeviceID <> -1)		
	End Method
	
	method EnablePreviewMode (enable:Int)
		if (DeviceID <> -1)
			Webcam_SendMessage (Handle, WM_CAP_SET_PREVIEW, enable, 0)
		end if		
	End Method
	
	method EnableOverlayMode (enable:Int)
		if (DeviceID <> -1)
			Webcam_SendMessage (Handle, WM_CAP_SET_OVERLAY, enable, 0)
		end if		
	End Method
	
	method SetFrames (XFrames:Int)
		FrameRate = XFrames
		if (DeviceID <> -1)
			Webcam_SendMessage (Handle, WM_CAP_SET_PREVIEWRATE, FrameRate, 0)
		end if		
	End Method
	
	method ShowSettings (nr:Int) 'use 0 to 2	
		if (DeviceID <> -1)
			Webcam_SendMessage (Handle, WM_CAP_DLG_VIDEOFORMAT+nr, 0, 0)
		end if		
	End Method
	
	method SetScale (DoScale:Int)
		if (DeviceID <> -1)
			Webcam_SendMessage (Handle, WM_CAP_SET_SCALE, DoScale, 0)
		end if		
	End Method
	
	method SetFrameCallbackRoutine ()
		if (DeviceID <> -1)		   
			local Routine:Byte Ptr 
			Routine = FrameCallback
			Webcam_SendMessage (Handle, WM_CAP_SET_CALLBACK_FRAME, 0, int(Routine))
		end if		
	End Method
	
	method SetPMSize (w:int, h:int)
		PMWidth = w
		PMHeight = h
	End Method
	
	method SetSize (w:int, h:int)
		local res:Int = false
		if (DeviceID <> -1)		   
			' Get The size of the structure first
			local size:Int = Webcam_SendMessage (Handle, WM_CAP_GET_VIDEOFORMAT, 0, NULL)
			if (size > 0)
				' Get The Space for the Data
				local Bank:TBank = createBank(size)
				Webcam_SendMessage (Handle, WM_CAP_GET_VIDEOFORMAT, size, int(BankBuf(Bank)))
				'Manipulate the buffer
				Width  = PeekInt(Bank, 4)
				Height = PeekInt(Bank, 8)
				DebugLog "Width: "+Width+" Height: "+Height
				PokeInt(Bank, 4, w)
				PokeInt(Bank, 8, h)
				'Write it back
				res = Webcam_SendMessage (Handle, WM_CAP_SET_VIDEOFORMAT, size, int(BankBuf(Bank)))
				if (res)
					Width  = w
					Height = h	
				end if
				Bank = NULL
			end if			
		end if		
		return(res)
	End Method
	
function FrameCallback (lwnd:Int, lpVHdr:Byte Ptr)
	local VideoHeader:Tbank = CreateStaticBank(lpVHdr, 40)
	local VideoMemoryAdress:Byte ptr	= Byte Ptr(PeekInt(VideoHeader, 0))
	local dwBytesUsed:Int				= PeekInt(VideoHeader, 4)	

	if (dwBytesUsed = (Width*Height*3)) ' 640 * 480 * 24bit
		local TempMap:TPixMap = CreateStaticPixMap (VideomemoryAdress, Width, Height, Width*3,PF_BGR888)		
'		WebCamImage = LoadImage(TempMap)
		Image = YFlipPixmap(TempMap)
		if (PMWidth <> Width) or (PMHeight <> Height)
			Image = ResizePixMap (Image, PMWidth,PMHeight)
		endif
	else
		debuglog "Wrong Picture Size. Expected "+Width+"/"+Height	
	end if
End Function
	
	
End type

'rem 

' TEST
graphics 800,600,0'32,60
local whnd = Webcam_GetActiveWindow()
local Webcam:TWebcam = new TWebcam
local rot:Int

if (Webcam.Init(whnd, 0,0,640,480))
'	Webcam.ShowSettings(0)
	Webcam.SetFrames(30)
	Webcam.SetSize(320,240)
	Webcam.SetPMSize(800,600)
	Webcam.SetScale(false)
	Webcam.EnablePreviewMode(false)
	Webcam.EnableOverlayMode(false)
	Webcam.SetFrameCallbackRoutine()
	while not keyhit(key_escape)
		cls 
		setcolor 255,255,255
		DrawPixMap (Webcam.Image, 0,0)
		drawtext "Memory usage:"+MemAlloced(), 0,580
		flip
		flushmem
	wend
else
	RuntimeError ("Cant Initialize Webcam")	
end if
endgraphics()
'end rem
