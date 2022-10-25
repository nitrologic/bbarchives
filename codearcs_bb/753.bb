; ID: 753
; Author: Xenon
; Date: 2003-07-30 06:41:06
; Title: B3D and B+
; Description: Code a Program with WinAIP and 3D Functions!

#------ Userlib *.decls

.lib "User32.dll"
FindWindow%(class$, fenster$):"FindWindowA"
SetWindowRgn%(hwnd,hrgn,redraw)
GetActiveWindow%()
SetParent%(hWndChild,hWndNewParent)
SetWindowPos%(hWnd, hWndINsertAfter,x,y,cx,cy,flags)

.lib "Gdi32.dll"
CreateRectRgn%(left,top,right,bottom)

#------ Blitz Plus Source

;
; Constants
;
Const windowwidth = 116
Const windowheight = 200
Const windowtitle$ = "Modeller"

Const dummy_m = 1
Const beenden_m = 19

;
; Globals
;
Global window = CreateWindow(windowtitle$,(ClientWidth(Desktop())-windowwidth)/2,(ClientHeight(Desktop())-windowheight)/2,windowwidth,windowheight,Desktop(),1)
Global menu = WindowMenu(window)
Global datei_m = CreateMenu("&Datei",dummy_m,menu)
Global beenden__m = CreateMenu("&Beenden	Alt+F4",beenden_m,datei_m)
Global button = CreateButton("Farbe",5,130,100,30,window)

;
; Fuctions
;
Function resize(hwnd,xwd,yhg)
	i=1
	j=1
	Repeat
		i=i+1
		SetGadgetShape hwnd,0,0,i,j
	Until ClientWidth(hwnd) = xwd
	Repeat
		j=j+1
		SetGadgetShape hwnd,0,0,i,j
	Until ClientHeight(hwnd) = yhg
	Notify GadgetWidth(hwnd)+" x "+GadgetHeight(hwnd)
End Function

Function writemessage(name,id,evdata,source,x,y)
	Repeat
	Until FileType("comunicate.tmp") = 0
	file = WriteFile("comunicate.tmp")
	WriteInt file,name
	WriteInt file,id
	WriteInt file,evdata
	WriteInt file,source
	WriteInt file,x
	WriteInt file,y
	CloseFile file
End Function

;
; Startup
;
UpdateWindowMenu window
MainHwnd = GetActiveWindow()
ExecFile("Viewport.exe")
tmp=MilliSecs()
Repeat
Until MilliSecs()-tmp > 500
Repeat
	vwprt = FindWindow("Blitz Runtime Class","Viewport")
	If WaitEvent() = $803 Then End
Until vwprt <> 0
SetParent(vwprt,mainhwnd)
SetWindowPos(vwprt,0,2,-25,ClientWidth(window),ClientHeight(window)-20,0)
ActivateWindow window
SetGadgetLayout button,1,0,1,0

;
; Mainloop
;
Repeat
event = WaitEvent()
Select event
	Case $401
		Select EventSource()
		Case button
			RequestColor(255,255,255)
			writemessage(event,EventID(),RequestedRed(),1,RequestedGreen(),RequestedBlue())
;		Case 
;			writemessage(event,EventID(),EventData(),EventSource(),EventX(),EventY())
		End Select
	Case $803
		Select EventSource()
		Case window
			writemessage(event,EventID(),EventData(),EventSource(),EventX(),EventY())
			End
		End Select
	Case $1001
		Select EventData()
		Case beenden_m
			writemessage($803,0,0,0,0,0)
			End
		End Select
	End Select
Forever

#------ Blitz 3D Source

;
; Constants
;
Const screenwidth = 160
Const screenheight = 120
Const colormode = 32

Graphics3D screenwidth,screenheight,colormode,2
AppTitle("Viewport")
SetBuffer BackBuffer()

;
; Globals
;
; GUI
Global eventname
Global eventid
Global eventdata
Global eventsource
Global eventx
Global eventy

; 3D
Global camera = CreateCamera()
Global light = CreateLight()
Global cube=CreateCube()

;
; Startup
;
CameraViewport camera,0,0,100,100
PositionEntity light,-3,0,3
PositionEntity camera,-3,5,-1
ScaleEntity cube,2,2,2
PointEntity camera,cube
hwnd = GetActiveWindow()
result = CreateRectRgn(3,30,103,130)
SetWindowRgn(hwnd,result,1)
;
; Mainloop
;
Repeat
	file = ReadFile("comunicate.tmp")
	If file <> 0 Then
		eventname = ReadInt(file)
		eventid = ReadInt(file)
		eventdata = ReadInt(file)
		eventsource = ReadInt(file)
		eventx = ReadInt(file)
		eventy = ReadInt(file)
		CloseFile(file)
		DeleteFile "comunicate.tmp"
		Select eventname
		Case $401
			Select eventsource
			Case 1
				EntityColor cube,eventdata,eventx,eventy
			End Select
		End Select
	End If
	TurnEntity cube,0.2,1,2
	UpdateWorld
	RenderWorld
	Flip
Until FindWindow("BlitzMax_Window_Class","Modeller") = 0
End
