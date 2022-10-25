; ID: 769
; Author: DH
; Date: 2003-08-18 18:58:08
; Title: Great Timers!!!!
; Description: Debuging Timers that work!

;=========Timer Control Library (TCL)==========
;By Dark Half (masterprompt@kcstudios.net)
;
;
;Functions and Usages
;   KCS_CreateTimer()                          Creates a timer and returns its id
;   KCS_GetTimer(TimerID)                      Gets Time of Timer, and Deletes It
;   KCS_KillTimers()                           Timer Clean-up
;===============================================

;++++++++++Do Not Change Between Here++++++++++++++
Type TimerDB
     Field TimerID
     Field TimerTime
End Type

Function KCS_CreateTimer()
         local TempTimer.TimerDB
         TempTimer.TimerDB      = new TimerDB           ;Create A New Timer
         TempTimer\TimerID      = Handle(TempTimer)     ;Get Timer ID As Object
         TempTimer\TimerTime    = millisecs()           ;Get Current Time
         Return TempTimer\TimerID                       ;Return Timer ID For Refference
End Function

Function KCS_GetTimer(TimerID)
         local TempTimer.TimerDB, KCS_Time
         TempTimer = Object.TimerDB(TimerID)            ;Get Timer Object From ID
         KCS_Time  = Millisecs() - TempTimer\TimerTime  ;Get Timer Difference
         Delete TempTimer.TimerDB                       ;Delete The Timer
         Return KCS_Time                                ;Return The Timer Difference
End Function

Function KCS_KillTimers()
         local TempTimer.TimerDB
         For TempTimer.TimerDB     = Each TimerDB
                Delete TempTimer.TimerDB                ;Delete Each Timer
         Next
End Function
;+++++++++And Here, End DO NOT CHANGE++++++++++++++++

;====Example Code=================
             graphics3d 800,600,16,2
             setbuffer backbuffer()
             
             Camera = createcamera()
             Light = createlight()

             while not keydown(1)

                   RenderTimer = KCS_CreateTimer()              ;Assigns a timer to RenderTimer
                   Renderworld
                   RenderTimerCount = KCS_GetTimer(RenderTimer) ;Uses Rendertimer ID to return time to Rendertimer

                   TextTimer = KCS_CreateTimer()
                   Text 0,0,"Render Time:"+RenderTimerCount
                   text 0,10,"Text Time:"+TextTimerCount
                   text 0,20,"Flip Time:"+FlipTimerCount
                   TextTimerCount = KCS_GetTimer(TextTimer)

                   FlipTimer = KCS_CreateTimer()
                   Flip
                   FlipTimerCount = KCS_GetTimer(FlipTimer)

             Wend
             KCS_KillTimers()
End
;====End Example Code==============
