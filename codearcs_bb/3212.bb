; ID: 3212
; Author: RemiD
; Date: 2015-06-28 11:21:09
; Title: window (windowed mode) without titlebar and with force redraw
; Description: This allows to have a window (windowed mode) without a titlebar and which prevents a bug with WindowsVista Windows7 where some part of the window is not redrawn properly even if it is active. Can also be used for fake fullscreen (windowed mode)

;-----------------------------
;user32.decls
;-----------------------------
;.lib "user32.dll"
;
;user32_GetSystemMetrics%(nIndex%) : "GetSystemMetrics"
;
;user32_FindWindow%(lpClassName$, lpWindowName$) : "FindWindowA"
;
;user32_GetActiveWindow%() : "GetActiveWindow"
;
;user32_GetWindowLong%(hwnd%, nIndex%) : "GetWindowLongA"
;user32_SetWindowLong%(hwnd%, nIndex%, dwNewLong%) : "SetWindowLongA"
;
;user32_SetWindowPos%(hwnd%, hWndInsertAfter%, x%, y%, cx%, cy%, wFlags%) : "SetWindowPos"
;
;user32_MoveWindow%(hwnd%, x%, y%, nWidth%, nHeight%, bRepaint%) : "MoveWindow"
;-----------------------------

Const CStart% = 1
Const CUpdate% = 2
Const CEnd% = 3
Global ProgramState%

ProgramState = Start

AppTitle "TestProgram"
WPWidth% = 854 ;window PWidth
WPHeight% = 480 ;window PHeight
Graphics3D(WPWidth,WPHeight,32,2)

WH% = SystemProperty ("AppHWND") ;get this window handle
DebugLog(WH)
;Global WHapi% = user32_FindWindow( "Blitz Runtime Class", "TestProgram" ) ;get this window handle
;DebugLog(WHapi)

DPWidth% = user32_GetSystemMetrics(0) ;desktop PWidth
DPHeight% = user32_GetSystemMetrics(1) ;desktop PHeight

Const GWL_STYLE = -16

Const WS_VISIBLE = $10000000

Const HWND_NOTOPMOST = -2
Const HWND_TOP = 0
Const HWND_TOPMOST = -1

Const SWP_FRAMECHANGED = $0020

user32_SetWindowLong(WH, GWL_STYLE, WS_VISIBLE) ;remove titlebar
user32_SetWindowPos(WH,HWND_TOP,DPWidth/2-WPWidth/2,DPHeight/2-WPHeight/2,WPWidth,WPHeight,SWP_FRAMECHANGED) ;put window on top

AWH% = user32_GetActiveWindow() ;get the active window handle

SeedRnd(MilliSecs())

Origine = CreateCube()
ScaleMesh(Origine,0.01/2,0.01/2,0.01/2)
EntityColor(Origine,255,000,255)
EntityFX(Origine,1)

Camera = CreateCamera()
CameraRange(Camera,0.1,100)
CameraClsColor(Camera,000,000,000)

GroundMesh = CreateCube()
ScaleMesh(GroundMesh,100.0/2,0.1/2,100.0/2)
PositionMesh(GroundMesh,100.0/2,-0.1/2,100.0/2)
EntityColor(GroundMesh,125,125,125)

For i% = 1 To 300 Step 1
 Cube1x1x1 = CreateCube()
 ScaleMesh(Cube1x1x1,1.0/2,1.0/2,1.0/2)
 EntityColor(Cube1x1x1,Rand(025,255),Rand(025,255),Rand(025,255))
 PositionEntity(Cube1x1x1,Rnd(0,99)+0.5,Rnd(0,29)+0.5,Rnd(0,99)+0.5,True)
Next

SLight = CreateLight(1)
LightColor(SLight,240,240,240)
PositionEntity(SLight,0,1000,-1000,True)
RotateEntity(SLight,45,0,0,True)

PositionEntity(Camera,0,1.65,-5,True)

MainTimer = CreateTimer(20)

Global PastMs% = MilliSecs()
Global NowMs% = MilliSecs()
Global Secs% = 0
Global SC# = 0.05

ProgramState = CUpdate

While( ProgramState = CUpdate )

 MainMsStart% = MilliSecs()

 AWH% = user32_GetActiveWindow() ;get the active window handle
 If( AWH = WH )
  WindowActiveState = True
  WindowStateStr$ = "This window is the active window"
  user32_SetWindowPos( WH, HWND_TOP, DPWidth/2-WPWidth/2, DPHeight/2-WPHeight/2, WPWidth, WPHeight, SWP_FRAMECHANGED ) ;put window on top
 Else If( AWH <> WH )
  WindowActiveState = False
  WindowStateStr$ = "This window is not the active window"
  ;RuntimeError("This window is not the active window")
 EndIf

 If( WindowActiveState = True)

  If( KeyDown(30)=1 )
   TurnEntity(Camera,0,1,0)
  Else If( KeyDown(32)=1 )
   TurnEntity(Camera,0,-1,0)
  EndIf
  If( KeyDown(17)=1 )
   MoveEntity(Camera,0,0,0.1)
  Else If( KeyDown(31)=1 )
   MoveEntity(Camera,0,0,-0.1)
  EndIf

  If( KeyDown(1)=1 )
   ProgramState = CEnd
  EndIf

  If( KeyDown(2)=0 )
   WireFrame(False)
  Else If(KeyDown(2)=1 )
   WireFrame(True)
  EndIf

 EndIf

 SetBuffer(BackBuffer())
 RenderWorld()

 NowMs% = MilliSecs()
 If( NowMs - PastMs >= 1000 )
  Secs% = Secs + 1
  PastMs = NowMs
 EndIf

 CText("FPS = "+FPS,0,0)
 CText("SC = "+SC,0,10)
 CText("Secs = "+Secs,0,20)

 CText(WindowStateStr,0,GraphicsHeight()/2)

 WaitTimer(MainTimer):Flip()

 MainMsTime% = MilliSecs() - MainMsStart

 If( MainMsTime <= 0 )
  MainMsTime = 1
 EndIf

 FPS% = 1000.0/Float(MainMsTime)
 SC# = Float(MainMsTime)/1000.0

Wend

ClearWorld(True,True,True)

End()

Function CText(TextStr$,PX%,PY%)
 Text(PX,PY,TextStr,fals,False)
End Function
