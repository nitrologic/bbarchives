; ID: 1538
; Author: markcw
; Date: 2005-11-18 17:30:56
; Title: CameraMouseView
; Description: Camera controls similar to those found in a modeller.

;CameraMouseView, on 10/10/06

Graphics3D 640,480,0,2
SetBuffer BackBuffer()

camerapivot=CreatePivot() ;pivot
camera=CreateCamera(camerapivot) ;camera on pivot
camerabank=CreateBank(48) ;camera bank variables

light=CreateLight()
RotateEntity light,90,0,0

cube=CreateCube()

While Not KeyHit(1)
 UpdateWorld
 RenderWorld

 CameraMouseView(camerapivot,camera,camerabank)

 Text 0,0,"Press LShift to Zoom, LCtrl to Drag"

 Flip
Wend

Function CameraMouseView(camerapivot,camera,camerabank,speed#=0.05)
 ;camerapivot,camera,camerabank=valid handles, speed#=speed of view

 Local movex,movey,camx#,camy#,camz#,pivx#,pivz#,angle#,xaxis#,zaxis#

 ;error messages for invalid handles
 If camerapivot=0 Then RuntimeError "Pivot entity is zero!"
 If camera=0 Then RuntimeError "Camera entity is zero!"
 If camerabank=0 Then RuntimeError "Bank handle is zero!"

 ;set default values
 If speed#<0.01 Then speed#=0.01 ;min speed
 If PeekFloat(camerabank,36)=0 Then PokeFloat camerabank,36,-10 ;no camz

 ;update mouse variables
 PokeInt camerabank,0,PeekInt(camerabank,8) ;lastx=msex
 PokeInt camerabank,4,PeekInt(camerabank,16) ;lasty=msey
 PokeInt camerabank,8,MouseX() ;msex
 PokeInt camerabank,16,MouseY() ;msey
 PokeInt camerabank,20,MouseX()-PeekInt(camerabank,0) ;movex=msex-lastx
 PokeInt camerabank,24,MouseY()-PeekInt(camerabank,4) ;movey=msey-lasty

 ;fill in the position/rotation variables
 movex=PeekInt(camerabank,20)
 movey=PeekInt(camerabank,24)
 camx#=PeekFloat(camerabank,28)
 camy#=PeekFloat(camerabank,32)
 camz#=PeekFloat(camerabank,36)
 pivx#=PeekFloat(camerabank,40)
 pivz#=PeekFloat(camerabank,44)

 If MouseDown(1) ;Left Mouse button
  If KeyDown(29) ;Left Ctrl key, camera drag
   angle#=camy# : xaxis#=speed# : zaxis#=speed# ;init xz axis
   If angle#>90 And angle#<270 Then zaxis#=-zaxis# ;-z if 90..270
   If angle#>180 Then angle#=360-angle# : xaxis#=-xaxis# ;-x if 180..360
   If angle#>90 Then angle#=180-angle# ;reduce y angle to 90
   angle#=angle#*0.011 ;y as fraction of 1, ie. 90->100
   ;set mouse xy movements by y fraction and add to pivot xz positions
   pivx#=pivx#-(movex*(1-angle#)*zaxis#)-(movey*angle#*xaxis#)
   pivz#=pivz#-(movex*angle#*xaxis#)+(movey*(1-angle#)*zaxis#)
  ElseIf KeyDown(42) ;Left Shift key, camera zoom
   camz#=camz#-movey*speed#  ;-msey sets camera z mt
   If camz#>-3 Then camz#=-3 ;limit zoom to 3mt
  Else ;camera rotation
   camx#=camx#+(movey*10*speed#) ;mousey sets camera x dg
   If camx#>90 Then camx#=90   ;limit x to -90..90
   If camx#<-90 Then camx#=-90
   camy#=camy#-(movex*10*speed#) ;-mousex sets camera y dg
   If camy#>359 Then camy#=0   ;limit y to 0..360
   If camy#<0 Then camy#=359
  EndIf
 EndIf

 ;update position/rotation variables
 PokeFloat camerabank,28,camx#
 PokeFloat camerabank,32,camy#
 PokeFloat camerabank,36,camz#
 PokeFloat camerabank,40,pivx#
 PokeFloat camerabank,44,pivz#

 ;move pivot, rotate and zoom camera
 PositionEntity camerapivot,pivx#,0,pivz# ;drag
 RotateEntity camerapivot,camx#,camy#,0 ;rotate
 PositionEntity camera,0,0,camz# ;zoom

End Function
