; ID: 1380
; Author: jfk EO-11110
; Date: 2005-05-21 10:35:13
; Title: RGB to HSB and back
; Description: Convert RGB to Hue, Saturation and Brightness

; sorry for the Gotos :P
Graphics 640,480,32,2
SetBuffer BackBuffer()

Global sat#,lumin#,hue#

; test demo
While KeyDown(1)=0
 sat#=0
 lumin#=0
 hue#=0
 r=Rand(255)
 g=Rand(255)
 b=Rand(255)
 Color r,g,b
 Cls
 Rect 100,100,100,100,1 ; draw in original rgb
 rgb2hsb(rgb(r,g,b))    ; convert to hsb
 rgb=hsb2rgb(hue,sat,lumin)  ; convert back to rgb
 Color getRed(rgb),getGreen(rgb),getBlue(rgb)  ; draw again
 Rect 100,220,100,100,1

 Locate 0,0
 Print "R: "+r
 Print "G: "+g
 Print "B: "+b
 Print
 Print "Hue:   "+hue
 Print "Sat:   "+sat
 Print "Lumin: "+lumin
 Print
 Print "Convert back"
 Print "R2: "+getRed(rgb)
 Print "G2: "+getGreen(rgb)
 Print "B2: "+getBlue(rgb)
 Locate 0,GraphicsHeight()-50
 Print "Press a key to continue, Esc to exit"
 Flip
 WaitKey()

Wend
End


Function hsb2rgb(h#,s#,l#) ; takes hue(0 to 360), saturation (0 to 1.0) and luminance (0 to 1.0)
 Local i,f#,p#,q#,t#,r#,g#,b#
 If s=0
  r=l*255.0:g=l*255.0:b=l*255.0
 Else
  h=h/60.0
  i=Floor(h)
  f=h-i
  p=l*(1.0-s)
  q=l*(1.0-s*f)
  t=l*(1.0-s*(1.0-f))
  Select i
   Case 0
    r=l
    g=t
    b=p
   Case 1
    r=q
    g=l
    b=p
   Case 2
    r=p
    g=l
    b=t
   Case 3
    r=p
    b=l
    g=q
   Case 4
    r=t
    b=l
    g=p
   Default
    r=l
    g=p
    b=q
  End Select
  r=r*255
  g=g*255
  b=b*255
 EndIf
 If r<0 Then r=0
 If r>255 Then r=255
 If g<0 Then g=0
 If g>255 Then g=255
 If b<0 Then b=0
 If b>255 Then b=255
 rgb=(r Shl 16)Or(g Shl 8)Or b
 Return rgb
End Function



Function rgb2hsb(rgb) ; takes 24 bit rgb color, returns (global) hue(0 to 360), saturation(0 to 1.0) and luminance(0 to 1.0)
 rgb=rgb And $FFFFFF
 r#=(rgb Shr 16) And $FF
 g#=(rgb Shr 8) And $FF
 b#=rgb And $FF
 my_min#=min#(r,g)
 my_max#=max#(r,g)
 my_min#=min#(my_min#,b)
 my_max#=max#(my_max#,b)
 delta#=(my_max#-my_min#)
 If my_max#<>0
  sat#=delta#/my_max#
  lumin#=my_max#/255.0
  If delta<>0
   If r=my_max Then 
    hue=(g-b)/delta
    Goto okii
   EndIf
   If g=my_max Then 
    hue=2+((b-r)/delta)
    Goto okii
   EndIf
   hue=4+((r-g)/delta)
   .okii
  EndIf
 EndIf
 hue=hue*60.0
 If hue<0 Then hue=hue+360.0
End Function



Function rgb(r,g,b)
 Return (r Shl 16) Or (g Shl 8) Or b
End Function

Function getRed(rgb)
 Return (rgb And $FF0000) Shr 16
End Function

Function getGreen(rgb)
 Return (rgb And $FF00) Shr 8
End Function

Function getBlue(rgb)
 Return (rgb And $FF)
End Function

Function min#(v1#,v2#)
 If v1<v2 Then 
  Return v1
 Else
  Return v2
 EndIf
End Function

Function max#(v1#,v2#)
 If v1>v2 Then 
  Return v1
 Else
  Return v2
 EndIf
End Function
