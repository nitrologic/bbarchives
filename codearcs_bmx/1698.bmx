; ID: 1698
; Author: Nilium
; Date: 2006-05-05 19:46:12
; Title: FPS/Delta Timing
; Description: Import-and-use code for FPS and delta timing

SuperStrict

Private
Global deltat!=1!
Global ctime!=-1
Global ltime!
Global fps! = 1.0 / (1000!/60!)
Global fps_frames% = 60
Global fps_time% = 0
Global fpsTimer:TTimer = CreateTimer( 1 )

Public

Function SetGameSpeed( fps! )
    fps = 1.0/(1000!/fps)
    fps_frames = fps
End Function

Function GetTicks!( )
    Return deltat
End Function

Function GetFPS%( )
    Return fps_frames
End Function

Private
Function __flipHook:Object( i%, d:Object, c:Object )
    If i = EmitEventHook Then
        Local e:TEvent = TEvent( d )
        If Not e Then Return d
        If e.id = EVENT_TIMERTICK And e.source = fpsTimer Then
            fps_frames = fps_time
            fps_time = 0
        EndIf
    ElseIf i = FlipHook Then
        ltime = ctime
        ctime = Millisecs( )
        fps_time :+ 1
        If Int(ltime) = -1 Then Return d ' First flip?
        deltat = deltat*.005+((ctime-ltime)*fps)*.995
    EndIf
    Return d
End Function
AddHook( FlipHook, __flipHook, Null, 10000 )
AddHook( EmitEventHook, __flipHook, Null, 0 )
