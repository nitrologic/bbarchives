; ID: 1863
; Author: Nebula
; Date: 2006-11-16 05:39:11
; Title: Energy / Health / Hitpoint Bar
; Description: Display graphical information

Graphics 640,480,16,2
SetBuffer BackBuffer()
;
hp = 100
While KeyDown(1) = False
	Cls
	energybar(100,100,123,10,hp,100)
	If hp>0 Then hp = hp - 1 Else Text 0,0,"Exit (esc)"
	Flip
Wend
;
Function Energybar(x,y,width,height,hitpoints,hitpoints_total)
	width = hitpoints / Float(hitpoints_total) * width
	Rect x,y,width,height
End Function
;
