; ID: 1092
; Author: Cronos
; Date: 2004-06-20 13:48:49
; Title: BlitzPluz + Blitz3D
; Description: BlitzPluz + Blitz3D

;>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
;  3D Test: BlitzPlus + Blitz3D
;       by: Luis Enrique Braga Ramirez
;>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

-------------- decls CODE --------------
--------- Blitz3d+BlitzPluz.decls ------
 
.lib "User32.dll"

FindWindow%(class$, fenster$):"FindWindowA"
GetActiveWindow%()
SetParent%(hWndChild,hWndNewParent)
ShowWindow% (hwnd%, nCmdShow%): "ShowWindow"

-------------- decls CODE END -------------- 


-------------- BlitzPlus CODE -------------- 
---------- BlitzPlus_Blitz3D.EXE -----------

Global Window   = CreateWindow("BlitzPlus + Blitz3D" , 0 , 0 , 640 , 520 , Desktop()  , 1+4+8 )
Global MainHwnd = GetActiveWindow();User32.dll
SetStatusText(Window,"BlitzPlus + Blitz3D") 

Global Menu  = WindowMenu( Window ) 
Global File  = CreateMenu( "File"  , 0 , Menu ) 
Global Close = CreateMenu( "Close" , 1 , File ) 
UpdateWindowMenu Window
 

ExecFile("Window3D.exe")

Repeat
	vwprt = FindWindow("Blitz Runtime Class" , "Window3D");User32.dll
	If WaitEvent() = $803 Then End
Until vwprt <> 0

SetParent(vwprt,MainHwnd);User32.dll
SetWindowPos( vwprt , 0 , -2 , -30 , 640 , 480 , 0);User32.dll
ShowWindow% (vwprt ,1) ;User32.dll


Repeat 

If WaitEvent()>0 Then

  Select EventID()

         Case $803
               End 
         Case $1001
              Select EventData()
              Case 1
                   End   
              End Select ;Select EventData()

  End Select ;Select EventID()


EndIf 

Forever 

End ;

-------------- BlitzPlus CODE END -------------- 



-------------- Blitz3D CODE -------------- 
-------------- Window3D.EXE --------------

If Windowed3D () 

 Graphics3D 640 , 480 , 16 , 2
 AppTitle("Window3D")
 hwnd = GetActiveWindow() ;User32.dll
 ShowWindow% (hwnd ,0)    ;User32.dll

 SetBuffer BackBuffer()

Else 

  End 

EndIf 

Global camera = CreateCamera()
Global light  = CreateLight()
Global cube   = CreateCube()

CameraViewport camera,0 ,0 , 640 , 480

PositionEntity light,-3,0,3
PositionEntity camera,-3,5,-1
ScaleEntity cube,2,2,2
PointEntity camera,cube


Repeat
  	   TurnEntity cube,0.5,0.5,0.5

	   UpdateWorld
	   RenderWorld
	   Flip
	
Until FindWindow( "BlitzMax_Window_Class" , "BlitzPlus + Blitz3D" ) = 0 ;User32.dll

End

-------------- Blitz3D CODE END -------------- 

PLEASE HELP ME TO IMPROVE IT!!!
