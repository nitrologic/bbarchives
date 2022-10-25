; ID: 330
; Author: jfk EO-11110
; Date: 2002-05-25 20:59:32
; Title: Joystick Correction
; Description: JoyX() and JoyY() Spikes Correction

; JOYSTICK CORRECTION
;
; On some Joysticks you get fancy values at random times. 
; If you had this Problem too then you might try this:
; These Functions will filter and smooth the values of JoyX() and JoyY()
; simply use "my_joyy()" instead of "joyy()". Don't forget to set the globals.
; you should use this with framerates up to about 60 Hz. If it's running too fast then
; the filter don't works perfect anymore. 
;
; this Method has a One-Call-Lag: the functions return the previous Value, not the current.
; This is neccessary to detect and filter potential Random Peeks aka 'spikes'.
; If such spikes are detected they will be replaced by the interpolation of the two surrounding
; peeks.
; You should not call the functions more than one time in your Loop.
Graphics3D 320,240,0,2
Global joyvx1#,joyvx2#,joyvx3#
Global joyvy1#,joyvy2#,joyvy3#

;--------------------
Color 255,255,255
While KeyDown(1)=0
 jx# = my_joyx#()
 jy# = my_joyy#()
 ;---
 ; compare for yourself:
 Print jx#+" "+jy#+" "+JoyX()+" "+JoyY()+" "
 Color 0,0,0
 Rect 0,0,320,17,1
 Color 255,0,0
 Text 0,0,"my_hoyx(),my_joyy(), JoyX(),JoyY()"
 Color 255,255,255
 ;---
 Flip            ; note: if you turn off sync - the faster the 
Wend             ; FPS the more Random Peeks you still get
End
;--------------------

Function my_joyx#()
 jl#=JoyX()
 jst$=Str$(jl#)
 jle=Len(jst$)
 If jle>10 Then
  If Mid$(jst$,jle-4,1)="e"
   jl=0.0
  EndIf
 EndIf
 joyvx3 = joyvx2
 joyvx2 = joyvx1
 joyvx1 = jl
 If (Abs(joyvx3-joyvx2)>0.1) And (Abs(joyvx2-joyvx1)>0.1)
  joyvx2=(joyvx3+joyvx1)/2
 EndIf
 Return joyvx2
End Function

Function my_joyy#()
 jl#=JoyY()
 jst$=Str$(jl#)
 jle=Len(jst$)
 If jle>10 Then
  If Mid$(jst$,jle-4,1)="e"
   jl=0.0
  EndIf
 EndIf
 joyvy3 = joyvy2
 joyvy2 = joyvy1
 joyvy1 = jl
 If (Abs(joyvy3-joyvy2)>0.1) And (Abs(joyvy2-joyvy1)>0.1)
  joyvy2=(joyvy3+joyvy1)/2
 EndIf
 Return joyvy2
End Function
