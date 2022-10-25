; ID: 591
; Author: cyberseth
; Date: 2003-02-17 05:44:46
; Title: Window/Canvas frame-timing setup
; Description: Sets up a window and canvas with frame-timing

Global FPS# = 60, GameTimer
Global deskw=ClientWidth(Desktop()), deskh=ClientHeight(Desktop())
Global gwidth# = 640, gheight# = 480 ;graphicswidth,height
Global win, can

winw = 640
winh = 480
win = CreateWindow("Resized to gwidth,gheight",deskw/2-gwidth/2,deskh/2-gheight/2,gwidth,gheight,0,15+32)

; Create drawing canvas
can = CreateCanvas(0,0,ClientWidth(win),ClientHeight(win),win)
SetGadgetLayout can,1,1,1,1 ;resizable with window


GameTimer = MilliSecs()
AutoSuspend 1

Repeat
	; Window events
    Select WaitEvent(1)
    Case $803  ; app close
        End
    End Select
	
	; Timing
	intv = 1000/FPS
	loop = (MilliSecs()-gametimer)/intv
	For k=1 To loop
		gametimer=gametimer+intv
		UpdateGame()
	Next
    DrawGame()
Forever

Function UpdateGame()
    If KeyHit(1) Then End
End Function

Function DrawGame()
    SetBuffer CanvasBuffer(can)
    Cls

    FlipCanvas can

End Function
