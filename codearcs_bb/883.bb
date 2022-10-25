; ID: 883
; Author: ford escort
; Date: 2004-01-11 13:47:06
; Title: lightning fast color replace
; Description: replace a color by another in a part of a buffer

; 
;usage: 
; 
;zone_replacecolor(x,y,width,height,red,green,blue,red,green,blue,buffer) 
; 
;buffer is the buffer to draw 
;ex: zone_replacecolor(0,0,100,100,0,0,0,255,255,255,imagebuffer(mypicture)) 
; zone_replacecolor(0,0,100,100,0,0,0,255,255,255,backbuffer()) 
; 
Function zone_replacecolor(x,y,w,h,rz,gz,bz,rr,gg,bb,buffer) 
usedbuffer=GraphicsBuffer() 
temp=CreateImage(w,h,1,2) 
SetBuffer buffer 
GrabImage temp,x,y 
Color rr,gg,bb 
Rect x,y,w,h,1 
MaskImage temp,rz,gz,bz 
DrawImage temp,x,y 
SetBuffer usedbuffer 
FreeImage temp 
End Function
