; ID: 579
; Author: jfk EO-11110
; Date: 2003-02-08 06:10:01
; Title: 3D Maze
; Description: Tiny Raycasting Engine

Graphics 320,240,16,2
SetBuffer BackBuffer()

; read level data
Dim grid(15,15)
For j=0 To 15
 For i=0 To 15
  Read grid(i,j)
 Next
Next
Dim rgb(9,2)
For i=1 To 9
 Read rgb(i,0):Read rgb(i,1):Read rgb(i,2)
Next

Global px#=8.0
Global pz#=8.0
Global a#, pxold#=0.0, pzold#=0.0, premind#, info$
;__________________________MAINLOOP_________________________
While KeyDown(1)=0
 Cls
 If KeyDown(200) Then ; up
  pxold=px
  pzold=pz
  px=px+Sin(a)/5.0
  pz=pz+Cos(a)/5.0
  sliding()
 EndIf
 If KeyDown(208) Then ; down
  pxold=px
  pzold=pz
  px=px-Sin(a)/5.0
  pz=pz-Cos(a)/5.0
  sliding()
 EndIf
 a=(a-mxs#) Mod 360 ; use mouse to steer
 raycast() ; do magic (well, actually not)
 Color 0,255,0 ; map info onscreen...
 Line (px*5-Sin(a)*2),(pz*5-Cos(a)*2),(px*5+Sin(a)*2),(pz*5+Cos(a)*2)
 Color 255,0,0
 Oval (px*5+(Sin(a)*2))-1,(pz*5+(Cos(a)*2))-1,2,2
 Color 127,127,127
 For j=0 To 15
  For i=0 To 15
   If grid(i,j)<>0
    Rect i*5-3,j*5-3,5,5,0
   EndIf
  Next
 Next
 If info$<>"" Then Text GraphicsWidth()/2,GraphicsHeight()/2,info$,1,1
 Flip
 mxs#=MouseXSpeed()/3.0 ; used by steer
 MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
Wend
End
;________________________eo mainloop__________________________

Function sliding()
  If grid(px,pz)<>0 ; sliding collision...
   premind=px
   px=pxold
   If grid(px,pz)<>0
    px=premind
    pz=pzold
    If grid(px,pz)<>0
     px=pxold
    EndIf
   EndIf
  EndIf
End Function

Function raycast()
 For i=-160 To 159
  rayx#=px
  rayz#=pz
  stepx#=Sin(a+0.125*i)/50.0  ; pretty small steps - would be way to slow on old machines
  stepz#=Cos(a+0.125*i)/50.0
  count=0
  touched=0
  While touched=0 And count<1000
   rayx#=rayx+stepx
   rayz#=rayz+stepz
   count=count+1
   If grid(rayx,rayz)<>0 Then
    touched=grid(rayx,rayz)
   EndIf
  Wend
  If touched<>0 Then
   h#=7000.0/count
   bright#=(h#+20.0)/127.0
   If bright>1.0 Then bright=1.0
   Color rgb(touched,0)*bright,rgb(touched,1)*bright,rgb(touched,2)*bright
   Line 160-i,120-h,160-i,120+h
   If touched=9
    If info$=""
     info$="Level Exit found!!! Congratzz!!!"
    EndIf
   EndIf
  EndIf
 Next
End Function

; map
Data 1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2
Data 2,9,0,0,1,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,0,0,0,0,4,3,5,6,5,6,0,0,0,2
Data 2,0,1,3,4,2,2,0,0,0,0,2,3,0,0,1
Data 1,2,1,0,0,0,2,1,4,0,0,1,2,2,0,2
Data 2,0,0,0,4,0,0,0,7,0,0,0,1,0,0,1
Data 1,0,8,4,3,0,0,0,0,0,0,0,1,2,0,2
Data 2,0,7,0,0,0,0,0,0,4,5,4,1,0,0,1
Data 1,0,6,0,0,1,0,0,0,1,0,0,2,2,0,2
Data 2,0,5,1,2,1,3,0,1,1,0,0,0,0,0,1
Data 1,0,4,0,0,0,0,0,2,4,2,4,2,2,0,2
Data 2,0,3,1,0,0,0,0,0,0,3,0,0,1,0,1
Data 1,0,2,1,1,0,0,0,0,0,0,0,0,1,0,2
Data 2,0,1,2,3,4,5,4,5,4,5,5,6,3,0,1
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2
Data 2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1

; wall colors: 1,2,3...
Data 127,127,255
Data 64,64,255
Data 255,127,255
Data 255,0,0
Data 0,255,0
Data 0,0,255
Data 255,0,255
Data 0,255,255
Data 255,255,0
Data 255,255,255
