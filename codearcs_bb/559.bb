; ID: 559
; Author: jfk EO-11110
; Date: 2003-01-25 20:37:32
; Title: Shoot'em
; Description: Simple FPS Shooting / Impact Example

; Simple FPS Shooting Example
Graphics3D 1024,768,16,2
SetBuffer BackBuffer()

Collisions 1,2,2,2
Collisions 2,1,1,1
Collisions 3,2,2,1 ; for bullets > enviroment check

player=CreatePivot()
PositionEntity player,-15,2,-30
EntityRadius player,.6
EntityType player,1

camera=CreateCamera( player )
CameraRange camera,.1,200
CameraClsColor camera,0,0,30
PositionEntity camera,0,1,0
TranslateEntity camera,0,2,0 ; eyes height

light=CreateLight()
LightColor light,32,32,32
TurnEntity light,45,45,0

light=CreateLight()
LightColor light,32,32,32
TurnEntity light,45,-45,0

;mesh=LoadMesh( "level.b3d" )

;------replace this block by the above Level-Loading ; probalby adjust scaling etc.
mesh=CreateCube(): ScaleEntity mesh,100,1,100
testtex=CreateTexture(128,128)
SetBuffer TextureBuffer(testtex)
For i=0 To 10000 ; draw 10001 dots on a texture created on the fly
  Color Rand(255),Rand(255),Rand(255) ; in random colors
  Plot 4+Rand(120),4+Rand(120)
Next
SetBuffer BackBuffer()
EntityTexture mesh,testtex
Dim fillers(10)
For i=0 To 10
  fillers(i)=CreateCube()
  EntityTexture fillers(i),testtex
  ScaleEntity fillers(i),Rand(20),Rand(100),Rand(20)
  PositionEntity fillers(i),Rand(-50,50),0,Rand(-50,50)
  EntityType fillers(i),2
Next
;------end block


EntityFX mesh,1
EntityType mesh,2

sp#=.5
ey#=EntityY(player)

; load mesh
;gun=LoadMesh("weapons-ammo/h-pistol-static.3ds")


;------replace this block by the above Gun-Loading
gun=CreateCube(camera):ScaleEntity gun,1,1,10
;---

;..PositionEntity gun,-14.5,2.3,-29.5 ; adjust these as you want
PositionEntity gun,.3,-.2,.5
RotateEntity gun,0,20,20

ScaleEntity gun,.15,.15,.5
EntityParent gun,camera
EntityRadius gun,1
EntityOrder gun,-1
;
;sky=LoadSkyBox( "grass" )
;EntityParent sky,camera

;create bullets
maxbull=100 ; max. number of bullets active
currbull=0
bulletspeed#=3.0
reload_after=30
Dim bullet(maxbull),bulljob(maxbull),mustset(maxbull) ; bullets array, bullet job flag array, collision reset flag array
For i=0 To maxbull
  bullet(i)=CreateCylinder() ; <<
  HideEntity bullet(i)
  EntityType bullet(i),3
  EntityRadius bullet(i),.05
  ScaleEntity bullet(i),.2,.2,.5
  RotateMesh bullet(i),90,0,0
Next
; so now we have bullet(0) to bullet(100) - these are 101 bullets

boom=CreateSphere() ; used as a impact mark
EntityAlpha boom,0.5

;make ammo
;pistolammo=LoadMesh("weapons-ammo/H-A_Shot-Static.3DS")
pistolammo=CreateCylinder() ; use yours

PositionEntity pistolammo,-0.5,2.5,-26.8

p_angle#=0 ; player angle
reloading=0
; ----------------------------------------MAINLOOP----------------------------------------
While Not KeyHit(1)
  
  If KeyHit(17)
    wire=1-wire
    WireFrame wire
  EndIf
  
  yv#=EntityY(player)-ey
  ey=EntityY(player)
  
  If KeyHit(57) Then yv=.1
  
  MoveEntity player,0,yv-.005,0 ; gravity
  
  p_angle#=(p_angle#-mxs#)  ; angle...
  If p_angle#>360 Then
    p_angle#=p_angle#-360
    p_smooth_angle#=p_smooth_angle#-360
  EndIf
  If p_angle#<0 Then
    p_angle#=p_angle#+360
    p_smooth_angle#=p_smooth_angle#+360
  EndIf
  
  c_angle#=(c_angle#+mys#)
  If c_angle#>360 Then
    c_angle#=c_angle#-360
    c_smooth_angle#=c_smooth_angle#-360
  EndIf
  If c_angle#<0 Then
    c_angle#=c_angle#+360
    c_smooth_angle#=c_smooth_angle#+360
  EndIf
  
  p_smooth_angle#=p_smooth_angle#-((p_smooth_angle#-p_angle#)/2.5)
  c_smooth_angle#=c_smooth_angle#-((c_smooth_angle#-c_angle#)/2.5)
  
  RotateEntity player,0,p_smooth_angle#,0
  RotateEntity camera,c_smooth_angle#,0,0
  
  If KeyDown(200) Then MoveEntity player,0,0,sp
  If KeyDown(208) Then MoveEntity player,0,0,-sp
  
  ; fire
  If MouseDown(1)=1 And MilliSecs()>=time_for_a_bullet
    time_for_a_bullet=MilliSecs()+133
    mag=mag+1
    If mag>=reload_after Then ; need to reload?
      time_for_a_bullet=MilliSecs()+1000 ; reload - no fire for 1 second
      mag=0
      reloading=1
    Else
      reloading=0
    EndIf
    ; launch bullet, probably play a gun sound
    EntityType bullet(currbull),0 ; force no collision to position at the guns xyz
    mustset(currbull)=1; remember this
    PositionEntity bullet(currbull),EntityX(gun,1),EntityY(gun,1),EntityZ(gun,1),1
    RotateEntity bullet(currbull),EntityPitch(gun,1),EntityYaw(gun,1),EntityRoll(gun,1),1
    MoveEntity bullet(currbull),0,0,-1.8
    ShowEntity bullet(currbull)
    bulljob(currbull)=1
    currbull=currbull+1
    If currbull>maxbull Then
      currbull=0
    EndIf
  EndIf
  If MilliSecs()>=time_for_a_bullet
    reloading=0 ; end reload phase anyway
  EndIf
  
  ; bullets in the air?
  For i=0 To maxbull
    If bulljob(i)<>0 ; is this bullet flying?
      If mustset(i)=1
        EntityType bullet(i),3 ; re-enable collison if required
        mustset(i)=0
      EndIf
      MoveEntity bullet(i),0,0,bulletspeed#
      If CountCollisions(bullet(i))>0
        hit_here=CollisionEntity(bullet(i),1) ; bullet hit wich entity?
        If hit_here<>0
          ; did you hit a lame multyplayer mesh? hit_here has its entity handle (that mesh needs EntityType 2)
          ; probalby play impact sound, start a particle  effect ...
          boomx#=CollisionX(bullet(i),1)
          boomy#=CollisionY(bullet(i),1)
          boomz#=CollisionZ(bullet(i),1)
          bulljob(i)=0 ; deactivate bullet
          EntityType bullet(i),0 ; collison off
          HideEntity bullet(i)
          ;impact happened Right here: (using a sphere to mark this point temporary - could be exlosion...)
          PositionEntity boom,boomx#,boomy#,boomz#,1
        EndIf
      EndIf
      If EntityDistance(bullet(i),gun)>100; bullet far away?
        HideEntity bullet(i)
        bulljob(i)=0 ; deactivate bullet
        EntityType bullet(i),0 ; collison off
      EndIf
    EndIf
  Next
  
  ;RotateEntity sky,0,0,0,True
  UpdateWorld
  RenderWorld
  Text 0,16,"Triangles Rendered:"+TrisRendered()
  Text 0,32,"Camera Position: "+EntityX(camera,1)+" "+EntityY(camera,1)+" "+EntityZ(camera,1)
  If reloading=1 Then Text GraphicsWidth()/2,GraphicsHeight()/2,"RELOADING",1,1
  mxs#=MouseXSpeed()/4.0
  mys#=MouseYSpeed()/4.0
  MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
  Flip
Wend

End
