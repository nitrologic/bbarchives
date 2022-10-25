; ID: 1462
; Author: jfk EO-11110
; Date: 2005-09-14 21:09:19
; Title: Terrain shadows
; Description: Simple and fast shadows for blitz terrains

; shadows for terrains, by jfk of csp
Graphics3D 1024,768,32,1
SetBuffer BackBuffer()
WBuffer 1


; ----- main camera and player pivot -----
Global camera=CreateCamera()
CameraRange camera,0.01,2000
CameraClsColor camera,100,150,200
Global player=CreatePivot()
TranslateEntity camera,0,5,0
EntityParent camera,player
MoveEntity player,0,100,0
EntityRadius player,5
EntityType player,2




; -----shadow camema -----
Global ShadowCam=CreateCamera()
CameraProjMode ShadowCam,2
HideEntity Shadowcam


; ----- a lit terrain -----
Global terrain=LoadTerrain("myhmap1.png")
Global towel_scale_Y#=100
ScaleEntity terrain,10,towel_scale_Y#,10
PositionEntity terrain,-TerrainSize(terrain)*5,0,-TerrainSize(terrain)*5
terrtex=LoadTexture("rock1.jpg")
EntityTexture terrain,terrtex
ScaleTexture terrtex,10,10
EntityType terrain,1
light=CreateLight()
RotateEntity light,-45,45,0
TerrainShading terrain,1
TerrainDetail terrain,1000



; ----- init shadow texture -----
Global ShadowTexSize=512 ; defines the resolution of the shadow texture
Global ShadowTex=CreateTexture(ShadowTexSize,ShadowTexSize,256)
Global shadow_Towel_Yoffset#=0.7 ; used to prevent "blinking" towel
; NOTE to make sure the offset is not so obvious, you should use characters with dark shoes 
; or trees with a darkish trunk etc.



; ----- create Shadow Towel -----
Global towel_size#=32 ; defines the number of segments
Global cam_towel=CreateTerrain(towel_size#)
EntityFX cam_towel,1
Global cam_towel_piv=CreatePivot()
Global towel_scale_XZ#=12 ; defines the XZ scale factor
ScaleEntity cam_towel,towel_scale_XZ#,towel_scale_Y#,towel_scale_XZ#
PositionEntity cam_towel,(-TerrainSize(cam_towel)*towel_scale_XZ#)/2.0,0,(-TerrainSize(cam_towel)*towel_scale_XZ#)/2.0,1
EntityParent cam_towel,cam_towel_piv
EntityTexture cam_towel,ShadowTex
TextureBlend shadowtex,2
EntityBlend cam_towel,2
ScaleTexture shadowtex,-towel_size#,-towel_size#
TerrainDetail cam_towel,1000


; ----- create some dummy scene objects -----
testtex=LoadTexture("colors.jpg")
Global num_o=500
Dim c(num_o),c_brush(num_o)
For i=0 To num_o
 c(i)=CreateCube()
 ScaleEntity c(i),2,20,2
 PositionEntity c(i),Rnd(-1280,1280),0,Rnd(-1280,1280)
 y#=TerrainY(terrain,EntityX(c(i)),0,EntityZ(c(i)) )
 PositionEntity c(i),EntityX(c(i)),y+5+Rand(1),EntityZ(c(i))
 EntityTexture c(i),testtex
 RotateEntity c(i),Rand(360),Rand(360),Rand(360)
 EntityColor c(i),100,100,100
 c_brush(i)=GetEntityBrush(c(i))
Next
; NOTE: all meshes and their brushes etc. (but the terrain) need to be indexed in this arrays to make 
; them cast shadows!


; ---------
; several brushes used to paint background and shadow casters during shadow map rendering
Global whitebrush=CreateBrush(255,255,255)
;Global whitebrush=CreateBrush(255,0,255) ; use this to make the towel visible (for debugging)
BrushFX whitebrush,1
Global shadowbrush=CreateBrush(140,140,140) ; defines the darkness of the shadows
BrushFX shadowbrush,1
Global terrainbrush=GetEntityBrush(terrain)


Collisions 2,1,2,2
; ----- main loop -----
While KeyDown(1)=0
 For i=0 To num_o
  TurnEntity c(i),1,2,3 ; show that it's dynamic 
 Next

 If KeyDown(200)=1 ; mouselook controls
  MoveEntity player,0,0,1
 EndIf
 If KeyDown(208)=1
  MoveEntity player,0,0,-1
 EndIf
 If KeyDown(205)=1
  MoveEntity player,1,0,0
 EndIf
 If KeyDown(203)=1
  MoveEntity player,-1,0,0
 EndIf
 msx#=0.25*(-MouseXSpeed())
 msy#=0.25*MouseYSpeed()
 MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
 TurnEntity player,0,msx#,0
 TurnEntity camera,msy#,0,0


 If KeyDown(57)=0
  TranslateEntity player,0,-.5,0; gravity
 Else
  TranslateEntity player,0,.5,0; allow to fly with space key
 EndIf

 UpdateShadows(terrain,cam_towel,cam_towel_piv)
 UpdateWorld()
 RenderWorld()
 old_t=t
 t=MilliSecs()
 Text 0,0,(1000.0/(t-old_t))+" fps"
 Text 0,16,"Tris rendered: "+TrisRendered()
 Flip
Wend
End



Function UpdateShadows(ground,towel,piv)
 x#=EntityX(player,1)
 z#=EntityZ(player,1)
 PositionEntity piv,x,shadow_Towel_Yoffset#,z,1
 RotateEntity piv,0,180,0,1
 ; align the shadow towel to the underlying terrain
 For i#=0 To TerrainSize(towel)-1
  x#=EntityX(towel,1)-(i*towel_scale_XZ#)
  For j#=0 To TerrainSize(towel)-1
   z#=EntityZ(towel,1)-(j*towel_scale_XZ#)
   tyl#=TerrainY(ground,x,0,z)
   ty#=((TerrainY#(ground,x,0,z))/towel_scale_Y#)
   ModifyTerrain towel,i,j,ty#
  Next
 Next

 ; hide borders
 For i#=0 To TerrainSize(towel)
   ModifyTerrain towel,i,TerrainSize(towel),0
 Next
 For j#=0 To TerrainSize(towel)
   ModifyTerrain towel,TerrainSize(towel),j,0
 Next
 UpdateShadowmap()
End Function






Function UpdateShadowmap()
 ; hide the towel, make the terrain white and all meshes grey
 For i=0 To num_o
  PaintEntity c(i),shadowbrush
  EntityFX c(i),1
 Next
 HideEntity cam_towel
 PaintEntity terrain, whitebrush
 ; toggle shadow camera (orthographic)
 HideEntity camera
 CameraClsColor shadowcam,255,255,255
 CameraZoom shadowcam,(.031 /towel_scale_XZ#)/(towel_size#/64.0) ; .028
 ShowEntity ShadowCam
 CameraProjMode ShadowCam,2
 ; take a render from above
 PositionEntity shadowcam, EntityX(player,1),1000,EntityZ(player,1)
 RotateEntity shadowcam,90,0,0
 CameraViewport shadowcam,0,0,ShadowTexSize,ShadowTexSize
 RenderWorld()
 ; copy that render to the shadow texture buffer
 CopyRect 0,0,ShadowTexSize,ShadowTexSize,0,0,BackBuffer(),TextureBuffer(ShadowTex)
 ;restore camera
 ShowEntity camera
 HideEntity ShadowCam
 ShowEntity cam_towel
 ; restore object materials
 PaintEntity terrain, terrainbrush
 For i=0 To num_o
  PaintEntity c(i),c_brush(i)
  EntityFX c(i),0
 Next
End Function
