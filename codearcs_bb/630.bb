; ID: 630
; Author: elias_t
; Date: 2003-03-18 19:18:07
; Title: Antialias image
; Description: Antialias image function

;Image Antialiasing routine
;
;by elias_t 
;
;[using also the rgb2hsv function by Ghost Dancer]

;------------------------------
;Globals needed
Global rgb_h#,rgb_s#,rgb_v#;
Dim px(8),rav(8),gav(8),bav(8);
;------------------------------



;==========================================================
;example
Graphics 640,480,32,2

in=LoadImage("test.bmp")

DrawImage in,10,10

Text 0,0,"Press any key to antialias image"

WaitKey()
FlushKeys()

;the [.2] value represents the sensitivity on hue difference between pixels
;should be between .15-.5 [default=.2]
;the [25] value represents the brightness value between pixels
;should be between 15-75  [default=25]


out=anti_alias(in,.2,25)


DrawImage out,10+ImageWidth(in),10

WaitKey()

End

;==========================================================







;FUNCTIONS


;[img] is the input image
;[h#] value represents the sensitivity on hue difference between pixels
;should be between .15-.5 [default=.2]
;[v] value represents the brightness value between pixels to be checked
;should be between 15-75  [default=25]

Function anti_alias(img,h#,v)


h#=Abs(h#)
If h#<.15 Then h#=.15
If h#>.5 Then h#=.5
v=Abs(v)
If v<15 Then v=15
If v>75 Then v=75

out=CopyImage(img)

LockBuffer(ImageBuffer(out))

For x=0 To ImageWidth(out)
For y=0 To ImageHeight(out)

;--------------
If y=0 Or y>ImageHeight(out)-2
a=0
Else
a=1
EndIf
If x=0 Or x>ImageWidth(out)-2
b=0
Else
b=1
EndIf
;--------------

;read the pixels       
px(0)=ReadPixelFast(x,y-a,ImageBuffer(out) )
px(1)=ReadPixelFast(x,y+a,ImageBuffer(out) )
px(2)=ReadPixelFast(x+b,y,ImageBuffer(out) )  
px(3)=ReadPixelFast(x-b,y,ImageBuffer(out) )
px(4)=ReadPixelFast(x,y,ImageBuffer(out) );<-center pixel
px(5)=ReadPixelFast(x+b,y-a,ImageBuffer(out) )
px(6)=ReadPixelFast(x-b,y+a,ImageBuffer(out) )
px(7)=ReadPixelFast(x+b,y-a,ImageBuffer(out) )  
px(8)=ReadPixelFast(x-b,y+a,ImageBuffer(out) )



rgb_hsv(px(4))
z4# = rgb_v#
h4# = rgb_h#-180
rgb_hsv(px(1))
z1# = rgb_v#
h1# = rgb_h#-180
rgb_hsv(px(2))
z2# = rgb_v#
h2# = rgb_h#-180


If ( px(4)<>px(1) And (Abs(z4#-z1#)>h#) Or Abs(h4#-h1#)>v ) Or (px(4)<>px(2) And (Abs(z4#-z2#)>h# Or Abs(h4#-h2#)>v ))


For f=0 To 8
    rav(f)=(px(f) Shr 16) And $ff
    gav(f)=(px(f) Shr 8) And $ff
    bav(f)=px(f) And $ff
Next

For f=0 To 8
    rax=rax+rav(f)
    gax=gax+gav(f)
    bax=bax+bav(f)
Next

rax=rax/9
gax=gax/9
bax=bax/9
    
rgb=((rax Shl 16)+(gax Shl 8)+bax)

WritePixelFast x,y,rgb,ImageBuffer(out)

rax=0:gax=0:bax=0

EndIf;

Next
Next

UnlockBuffer(ImageBuffer(out))

Return out

End Function


;------------------------------

Function rgb_hsv(rgb#);from the colour space library Ghost Dancer

	;RGB components in  range 0 to 1
	r# = (rgb# Shr 16 And $ff) / 255.0
	g# = (rgb# Shr 8 And $ff) / 255.0
	b# = (rgb# And $ff) / 255.0
	;min value	
	If r < g Then minVal# = r Else minVal# = g
	If b < minVal Then minVal = b
	;max value	
	If r > g Then maxVal# = r Else maxVal# = g
	If b > maxVal Then maxVal = b
	;calculate difference
	diff# = maxVal - minVal
	
	rgb_v = maxVal
	
	If maxVal = 0 Then
		rgb_v = 0
		rgb_h = -1
	Else
		rgb_s = diff / maxVal
	
		If r = maxVal Then
			rgb_h = (g - b) / diff
		ElseIf g = maxVal Then
			rgb_h = 2 + (b - r) / diff
		Else
			rgb_h = 4 + (r - g) / diff
		EndIf
	
		rgb_h = rgb_h * 60
		If rgb_h < 0 Then rgb_h = rgb_h + 360
	EndIf

End Function

;------------------------------
