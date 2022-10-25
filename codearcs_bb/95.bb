; ID: 95
; Author: Teddyfles
; Date: 2001-10-09 12:02:42
; Title: RefreshRate
; Description: Returns the current monitor refresh rate.

; RefreshRate()
; Returns the current monitor refresh rate.
; By Geert Jan Alsem
; http://geertjan.vze.com
;
; Updated version of a crappy RefreshRate() function I once wrote.
; Actually, this one is a bit strange too. Basicly it measures how
; long a VWait takes 100 times. However, it only uses the last 20
; times to calculate the result. Now the strange thing is that if
; I remove the seemingly useless first 80 runs, the function
; doesn't work as well as it does now. Apparently it needs to warm
; up first... or something...
;
; Anyway, it's a pretty useless function anyway. Just use
; WaitTimer if you want to make a game run at the same speed on
; any refresh rate.


Function RefreshRate()
  Repeat
    rr_timer = MilliSecs()
    VWait
    rr_timer = MilliSecs() - rr_timer
    If rr_count > 79 Then rr_total# = rr_total# + 1000 / Float(rr_timer)
    rr_count = rr_count + 1
  Until rr_count = 100
  Return rr_total#/20
End Function
