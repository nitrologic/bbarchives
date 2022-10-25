; ID: 2663
; Author: Streaksy
; Date: 2010-03-08 16:08:35
; Title: Timescale
; Description: Keep things moving in realtime regardless of framerate and lag

Global TimeScale#,LagScale#,TimeScaleMul#=1,TimeScaleLastTime ;TIMESCALING
Global TimeScaleLibPresent=1


Function SyncLagScale()
tim=MilliSecs()
timepassed#=tim-TimeScaleLastTime
If TimeScaleLastTime=0 Then lagscale=1 Else lagscale=(timepassed/1000)*50
TimeScaleLastTime=tim
timescale=lagscale*timescalemul:If timescale>6 Then timescale=6
If timescale>20 Then timescale=1  ;okay thats alot... lets treat it like a delay rather than an example of framerate
End Function

Function SetTimeScale(s#)
timescalemul=s
End Function
