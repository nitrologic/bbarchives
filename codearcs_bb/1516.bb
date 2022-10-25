; ID: 1516
; Author: jfk EO-11110
; Date: 2005-11-02 20:25:35
; Title: headbanger
; Description: Wobble the camera when the FPS player is walking.

Graphics3D 640,480,16,1
SetBuffer BackBuffer()

player=CreatePivot()
PositionEntity player,0,3,0
EntityRadius player,.9
EntityType player,1

camera=CreateCamera( player )
TranslateEntity camera,0,0.9,0
CameraRange camera,.1,200

tex=CreateDummyTexture(256,256)
ScaleTexture tex,10,10

ground=CreatePlane() ; or the map mesh etc.
EntityTexture ground,tex
EntityType ground,2


sp#=.05 ; main walking speed (not connected with wobbling)
shoe_size#=7.0 ; stepspeed for wobbling camera (eg. 7=running, 4=walking)
head_bang_X#=0.1 ; amount of wobbling
head_bang_Y#=0.1

Collisions 1,2,2,2

While Not KeyHit(1)


 mxs#=MouseXSpeed()/4.0
 mys#=MouseYSpeed()/4.0
 MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
 camxa#=camxa-mxs Mod 360
 camya#=camya+mys
 If camya<-90 Then camya=-90
 If camya>90 Then camya=90
 RotateEntity player,0,camxa,0
 RotateEntity camera,camya,0,0

 MoveEntity player,0,-.05,0 ; simplified gravity

 walking=0
 If KeyDown(203) Then: MoveEntity player,-sp,0,0 : walking=1: EndIf
 If KeyDown(205) Then: MoveEntity player, sp,0,0 : walking=1: EndIf
 If KeyDown(200) Then: MoveEntity player,0,0, sp : walking=1: EndIf
 If KeyDown(208) Then: MoveEntity player,0,0,-sp : walking=1: EndIf


 ; >>>>>>>>>wobble camera
 If walking=1
  a1#=(a1#+shoe_size) Mod 360
  Else
  ;a1#=a1#*0.8
 EndIf
 PositionEntity camera,Cos(a1#)*head_bang_X#,Sin(90+a1#*2)*head_bang_Y#,0,0
; PositionEntity camera,Cos(a1#)*head_bang_X#,Sin(270+a1#*2)*head_bang_Y#,0,0 ; or try this one instead!

 UpdateWorld
 RenderWorld

 ; >>>>>>>>>control footstep sound
 If Sin(90+a1*2)<-.85
  If  footstep_needed<>0
   Color 255,255,255
   Text 50,50, "Tap!" ; play a footstep sound here!
   footstep_needed=0
  EndIf
 Else
  footstep_needed=1
 EndIf




 VWait:Flip 0
Wend

End

Function CreateDummyTexture(w,h)
 tex=CreateTexture(w,h)
 SetBuffer TextureBuffer(tex)
  Color 255,255,255
  For i=0 To 1000
   Color Rand(255),Rand(255),Rand(255)
   Line Rand(0,w-1),Rand(0,h-1),Rand(0,w-1),Rand(0,h-1)
  Next
 SetBuffer BackBuffer()
 Return tex
End Function
