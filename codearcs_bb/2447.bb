; ID: 2447
; Author: CloseToPerfect
; Date: 2009-03-29 18:15:58
; Title: darken or lighten a color
; Description: This function lets you set a base color value then darken or lighten it. w/example

Function cc_set_color(ccl,ccr,ccg,ccb,ccset=1)
;usage cc_set_color(ccl,ccr,ccg,ccb,ccset=0)
;ccl is brightness level 128 is the normal color
;a lower value will make the color shades darker
;a hight value will make the color brighter
;ccset=1 will set the current drawing color
;and return the integer value of the rgb color
;ccset=0 will only return the integer value of the rgb
;completly dark is black, sets colors to 0,0,0 or int 0.
;completly bright is white, sets colors to 255,255,255 or a int 16777216
;a grey color 100,100,100 for example will be 100,100,100 if ccl set to 128
;and will be 99,99,99 when ccl is set to 127 
;or will be 101,101,101 when ccl is set to 129
;to return a int value usage, integer_varible = cc_set_color(ccl,ccr,ccg,ccb,ccset=0)
;128 is normal color
;127-0 darkens
;128-255 brightens
If ccl<128  
	rs#=Float ccr/127
	gs#=Float ccg/127
	bs#=Float ccb/127
	ccred=ccl*rs#
	ccgreen=ccl*gs#
	ccblue=ccl*bs#
ElseIf ccl>127
	ccl=ccl-128
	rs#=Float (255-ccr)/127
	gs#=Float (255-ccg)/127
	bs#=Float (255-ccb)/127
	ccred=ccr+ccl*rs#
	ccgreen=ccg+ccl*gs#
	ccblue=ccb+ccl*bs#
EndIf 
If ccset=1 Then Color ccred,ccgreen,ccblue
Return ccblue Or (ccgreen Shl 8) Or (ccred Shl 16)
End Function


;example usage
Graphics 510,510,16,2
For i=50 To 250
cc_set_color(i,50,90,200)
Oval i/1.25,i/3,255-i,255-i
cc_set_color(i,205,50,70)
Oval 220+i/1.25,i/3,255-i,255-i
cc_set_color(i,55,127,155)
Oval i/1.25,220+i/3,255-i,255-i
cc_set_color(i,250,90,200)
Oval 220+i/1.25,220+i/3,255-i,255-i
Flip
Next
For i=0 To 255
cc_set_color(i,90,30,150)
Line 0,i,GraphicsWidth(),i
Line 0,510-i,GraphicsWidth(),510-i
Flip
Next 
For i=0 To 255
cc_set_color(255-i,90,130,50)
Line 510-i,0,510-i,GraphicsHeight()
cc_set_color(255-i,90,130,50)
Line i,0,i,GraphicsHeight()
Flip
Next 
For i = 0 To 255
Color 0,0,0
Rect 255-i,255-i,i*2,i*2,0
Flip
Next

For i=50 To 250
cc_set_color(i/2,250,90,200)
Oval i/1.25,i/3,255-i,255-i
Next
image=CreateImage(255,255)
GrabImage image,0,0
x=110:y=-50
height = 0
dir=0
Color 255,255,255
Repeat
Cls
DrawBlock image,x,y
If dir = 0 Then y=y+g# Else y=y-g#
If dir=0 And y>285 Then dir=1 
If dir=1 And g#<0 Then dir=0 
If dir = 0 Then g#=g#+.1 
If dir = 1 Then g#=g#-.15
Text 0,0,"hit esc to quit"
Flip
Until KeyHit(1)
