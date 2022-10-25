; ID: 2724
; Author: jfk EO-11110
; Date: 2010-06-07 20:25:56
; Title: Chasecam with Flashlight
; Description: Camera follows Player Character, plus a flashlight.

Graphics3D 800,600,32,2
SetBuffer BackBuffer()

AmbientLight 15,15,15

Global bounce_cam=1 ;  smooth camera motion? (0/1)
Global bounce_div#=10.0 ; the higher, the more sluggish the camera will follow. Recc: 3.0 to 20.0

; collision types
Global type_world=2
Global type_camera=3
Global type_player=4

; some globals used by ChaseCam
Global old_x#
Global old_y#
Global old_z#

; desired distance between camera and player mesh
Global x_dis#=5.0
Global y_dis#=2.5
Global z_dis#=5.0
Global y_dis_or#=y_dis#

; a dummy world enviroment ...
Global plane=CreateCube()
ScaleEntity plane,1000,0.1,1000

EntityColor plane,128,128,64
EntityPickMode plane,2
EntityType plane,type_world

; some test walls. ;
; PLEASE NOTE: extreme low poly objects like big cubes don't work well with
; directX point lights: the light simply doesn't get enough vertices to obtain a good "grip" on the
; normals. To illustrate this you may use the CreateCube command instead (see a few lines below).
Global n_c=10
Dim cube(n_c)
For i=0 To n_c
 cube(i)=CreateSphere()
 PositionEntity cube(i),Rnd(-20,20),0,Rnd(-20,20)
 ScaleEntity cube(i),1,10,Rand(4,10)
 RotateEntity cube(i),0,Rand(360),0
 EntityColor cube(i),Rand(255),Rand(255),Rand(255)
 EntityPickMode cube(i),2
 UpdateNormals cube(i)
 EntityType cube(i),type_world
Next



; a player character mesh dummy
; (Note: probably replace this by a pivot that is located at the characters head)
player=CreateCube()
FitMesh player,-.5,0,-.5,1,-2,1
TranslateEntity player,0,2,0
EntityType player, type_player
;EntityPickMode player,2  ; if picking on the player mesh is required, it needs to be deactivated temporarily in the UpdateChaseCam function!
EntityRadius player,1.0,2.0  ; radius needs to big enough to allow a camera between player and walls etc!


camera=CreateCamera()
CameraRange camera,0.05,300
EntityType camera,type_camera
EntityRadius camera,0.1


;light=CreateLight()
;RotateEntity light,45,45,0

sp#=0.1 ; nav speed
gravity#=0.1

Collisions type_player,type_world,2,2
Collisions type_camera,type_world,2,2

flashlite = CreateFlashLite(player)
TranslateEntity player,0,2,0
; ----------------------------------------- MAIN LOOP ------------------------------------------
While KeyDown(1)=0
 ; player controls...
 If KeyDown(200) Then:MoveEntity player,0,0,sp#:EndIf
 If KeyDown(208) Then:MoveEntity player,0,0,-sp#:EndIf
 If KeyDown(203) Then:MoveEntity player,-sp#,0,0:EndIf
 If KeyDown(205) Then:MoveEntity player,sp#,0,0:EndIf
 mxs#=MouseXSpeed()/4.0
 mys#=(MouseYSpeed()/100.0)
 y_dis=y_dis+mys
 If y_dis<-y_dis_or Then y_dis=-y_dis_or
 If y_dis>y_dis_or*2 Then y_dis=y_dis_or*2

 MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
 TurnEntity player,0,-mxs,0
 ;------
 
 UpdateChaseCam(camera,player)
 TranslateEntity player,0,-gravity#,0

 UpdateWorld()
 RenderWorld()
 ; Text 0,0, "cursor keys to navigate, mouse to turn/look"
 Text 0, 0,EntityX(camera)+" , "+EntityY(camera)+" , "+EntityZ(camera)
 Text 0,20,"Tris rendered: "+TrisRendered()
 VWait
 Flip 0
Wend

End




Function UpdateChaseCam(camera,player)
 PositionEntity camera,EntityX(player,1),EntityY(player,1),EntityZ(player,1)
 ResetEntity camera

 x#=EntityX(player)+Sin(-EntityYaw(player)+180)*x_dis#
 y#=EntityY(player)+y_dis#
 z#=EntityZ(player)+Cos(-EntityYaw(player)+180)*z_dis#
 obstacle=LinePick(EntityX(player),EntityY(player),EntityZ(player),   (x-EntityX(player)),  y_dis#,    (z-EntityZ(player))  ,0.1)

 If obstacle=0
  new_x#=x
  new_y#=y
  new_z#=z
 Else
  new_x#=PickedX()
  new_y#=PickedY()
  new_z#=PickedZ()
 EndIf

 If bounce_cam=0
  PositionEntity camera,new_x,new_y,new_z
 Else
  x=old_x+((New_x-old_x)/bounce_div#)
  y=old_y+((New_y-old_y)/bounce_div#)
  z=old_z+((New_z-old_z)/bounce_div#)
  PositionEntity camera,x,y,z
 EndIf

 PointEntity camera,player
 ;TurnEntity camera,-10,0,0     ; if you want the player mesh more on the bottom of the screen
 
 old_x#=EntityX(camera)
 old_y#=EntityY(camera)
 old_z#=EntityZ(camera)
End Function


Function CreateFlashLite(parent=0)
 Local n#,tex,piv,i,sz#
 tex=CreateFlashLiteTex()
 piv=CreatePivot()
 n=40
 m#=0.5
 m2#=0.2
 For i=0 To n
  iflo#=Float(i)
  s=CreateSprite(piv)
  EntityBlend s,3
  EntityTexture s,tex
  TranslateEntity s,0,0,m
  m=m*1.08
  EntityAlpha s, 0.05;13
  sz#=3.0
  ScaleSprite s,m2,m2
  m2=m2*1.07 ;085
 Next
 If parent<>0 Then
  TranslateEntity piv,0,3,0
  EntityParent piv,parent
 EndIf

 li=CreateLight(3,piv)
 LightConeAngles li,0,60 
 LightRange li,6.0
 LightColor li,255,255,255
 PositionEntity li,EntityX(parent),EntityY(parent),EntityZ(parent),1


 li2=CreateLight(3,piv)
 PositionEntity li2,EntityX(parent),EntityY(parent),EntityZ(parent),1
 LightConeAngles li2,0,180 
 LightRange li2,2.0
 MoveEntity li2,0,1,0
 LightColor li2,512,512,512



 Return piv
End Function

Function CreateFlashLiteTex()
 Local tex,gr,i,j,blue
 tex=CreateTexture(128,128,(2 Or 48))
 SetBuffer BackBuffer()
 For i=64 To 0 Step -1
  gr=(65-i)*8
  If gr>255 Then gr=255
  If gr<0 Then gr=0
  Color gr,gr,gr
  Oval 64-i,64-i,Abs(i+i),Abs(i+i),1
  Oval 63-i,64-i,Abs(i+i),Abs(i+i),1
  Oval 64-i,63-i,Abs(i+i),Abs(i+i),1
  Oval 63-i,63-i,Abs(i+i),Abs(i+i),1
 Next
 LockBuffer(BackBuffer())
 For j=0 To TextureHeight(tex)-1
  For i=0 To TextureWidth(tex)-1
   blue=(ReadPixelFast(i,j) And $ff)/3.0
   WritePixelFast i,j,(blue Shl 24) Or $ffffff
  Next
 Next
 UnlockBuffer(BackBuffer())
 SetBuffer BackBuffer()
 CopyRect 0,0,128,128,0,0,BackBuffer(),TextureBuffer(tex)
 Return tex
End Function
