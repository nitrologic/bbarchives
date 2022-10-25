; ID: 855
; Author: xlsior
; Date: 2003-12-13 19:38:37
; Title: Paralax Circles
; Description: Two circles moving in paralax

; Paralax -- Sample paralax circle demo
; 12/13/2003, by Marc van den Dikkenberg / xlsior
; 
;       The code below is just a simple example of the general principle.
;       This effect was used in many demo's during the 80's.
;
; Note: The black bars could be removed by using multiple tiles images that are
;       have a combined size of larger than the full screen.
;       

Graphics 640,480,16,3
SetBuffer BackBuffer()
circle1=CreateImage(640,480)
circle2=CreateImage(640,480)

For t=(640*1.5) To 0 Step -40
   Color 255,0,0
   Oval 320-(t/2),240-(t/2),t,t
   Color 0,255,0
   Oval 320-(t/2)+10,240-(t/2)+10,t-20,t-20
Next 

GrabImage circle1,0,0
Color 0,0,0

Rect 0,0,640,480
For t=840 To 0 Step -40
Color 0,0,255
Oval 320-(t/2),240-(t/2),t,t
Color 0,255,0
Oval 320-(t/2)+10,240-(t/2)+10,t-20,t-20
Next 
GrabImage circle2,0,0

MaskImage circle1,0,255,0
MaskImage circle2,0,255,0

While Not KeyDown(1)
   aa=MilliSecs()

   ; Make both circles move at different speeds
   t=t+3
   t2=t2+2
   Cls

   ; Make both circles move in a different pattern
   x#=Sin(t)*10
   y#=Cos(t)*10
   x2#=Sin(t2)*15
   y2#=Cos(t2)*15

   ; SIN/COS gone full circle, reset
   If t>359 Then t=t-360
   If t2>359 Then t2=t2-360


   ; Draw the first circle
   DrawImage circle1,x#,y#
   ; Have circle 2's X-value be influenced by its Y position, to differentiate 
   DrawImage circle2,x2#+y2#,y2#*3

   ; Put black bars around the image, so the edges look better
   Color 0,0,0
   Rect 0,0,640,50
   Rect 0,430,640,50
   Rect 0,0,32,480
   Rect 608,0,32,480

   ; Update Screen
   Flip
Wend
