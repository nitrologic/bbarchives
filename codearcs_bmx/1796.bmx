; ID: 1796
; Author: Fabian.
; Date: 2006-08-27 07:26:48
; Title: MaxGUI: Window positioning/resizing with a canvas
; Description: Allows the user to position/resize a window by clicking on a canvas

Strict
Framework brl.blitz

Import brl.d3d7max2d
Import brl.glmax2d
Import brl.win32maxgui
Import brl.fltkmaxgui
Import brl.cocoamaxgui

Global Stop
Global MX , MY , MD
Global Window:TGadget = CreateWindow ( "TestWin" , 400 , 400 , 400 , 400 , Desktop ( ) , 0 )
Global Canvas:TGadget = CreateCanvas ( 0 , 0 , ClientWidth ( Window ) , ClientHeight ( Window ) , Window )
SetGadgetLayout Canvas , 1 , 1 , 1 , 1
AddHook EmitEventHook , Hook
While Not Stop
  WaitSystem
Wend

Function Hook:Object ( ID , Data:Object , Context:Object )
  Local Event:TEvent = TEvent ( Data )
  Select Event.source
    Case Window
      If Event.id = EVENT_WINDOWCLOSE
        Stop = True
      EndIf
    Case Canvas
      If Event.id = EVENT_GADGETPAINT
        SetGraphics CanvasGraphics ( Canvas )
        SetViewport 0 , 0 , ClientWidth ( Canvas ) , ClientHeight ( Canvas )
        SetClsColor 200 , 200 , 200
        Cls
        SetViewport 10 , 30 , ClientWidth ( Canvas ) - 20 , ClientHeight ( Canvas ) - 40
        SetClsColor 100 , 100 , 100
        Cls
        SetViewport 10 , 10 , ClientWidth ( Canvas ) - 20 , 20
        SetClsColor 0 , 0 , 255
        Cls
        SetColor 255 , 255 , 255
        DrawText "Window Title" , 12 , 12
        SetColor 200 , 200 , 200
        DrawRect ClientWidth ( Canvas ) - 28 , 12 , 16 , 16
        SetColor 0 , 0 , 0
        DrawLine ClientWidth ( Canvas ) - 28 , 12 , ClientWidth ( Canvas ) - 13 , 27
        DrawLine ClientWidth ( Canvas ) - 13 , 12 , ClientWidth ( Canvas ) - 28 , 27
        Flip
      EndIf
      If Event.id = EVENT_MOUSEDOWN And Event.data = 1
        If Event.x >= ClientWidth ( Canvas ) - 28 And Event.x < ClientWidth ( Canvas ) - 12 And Event.y >= 12 And Event.y < 28
          Stop = True
        Else
          MX = Event.x
          MY = Event.y
          MD = ( MY > 10 ) + ( MY >= ClientHeight ( Canvas ) - 10 )
          MD :* 3
          MD :+ ( MX > 10 ) + ( MX >= ClientWidth ( Canvas ) - 10 )
          MD :+ 1
        EndIf
      EndIf
      If Event.id = EVENT_MOUSEMOVE And MD
        Local DX = Event.x - MX
        Local DY = Event.y - MY
        Local X = GadgetX ( Window )
        Local Y = GadgetY ( Window )
        Local W = GadgetWidth ( Window )
        Local H = GadgetHeight ( Window )
        Select MD
          Case 1 ; X :+ DX ; W :- DX ; Y :+ DY ; H :- DY
          Case 2 ; Y :+ DY ; H :- DY
          Case 3 ; W :+ DX ; MX :+ DX ; Y :+ DY ; H :- DY
          Case 4 ; X :+ DX ; W :- DX
          Case 5 ; X :+ DX ; Y :+ DY
          Case 6 ; W :+ DX ; MX :+ DX
          Case 7 ; X :+ DX ; W :- DX ; H :+ DY ; MY :+ DY
          Case 8 ; H :+ DY ; MY :+ DY
          Case 9 ; W :+ DX ; MX :+ DX ; H :+ DY ; MY :+ DY
        EndSelect
        SetGadgetShape Window , X , Y , W , H
      EndIf
      If Event.id = EVENT_MOUSEUP And Event.data = 1
        MD = 0
      EndIf
  EndSelect
  Return Data
EndFunction
