; ID: 3029
; Author: RemiD
; Date: 2013-02-08 06:56:59
; Title: SpriteMesh instead of Blitz3d Sprite
; Description: Shows how to use a sprite mesh instead of a Blitz3d sprite

Global GWidth% = 800
Global GHeight% = 600
Global GColors% = 32
Global GMode% = 2
Graphics3D(GWidth,GHeight,GColors,GMode)

SeedRnd(MilliSecs()) 
  
Global Camera = CreateCamera() 
CameraRange(Camera,0.1,10000)

Origine = CreateCube()
ScaleMesh(Origine,0.01/2,0.01/2,0.01/2)
EntityColor(Origine,255,000,000)
EntityFX(Origine,1) 
 
Global DebugFeet = CreatePivot()  
  
Global DebugEyes = CreatePivot()
PositionEntity(DebugEyes,EntityX(DebugFeet,True),EntityY(DebugFeet,True),EntityZ(DebugFeet,True))
RotateEntity(DebugEyes,EntityPitch(DebugFeet,True),EntityYaw(DebugFeet,True),EntityRoll(DebugFeet,True))
MoveEntity(DebugEyes,0,1.7,0.25)
EntityParent(DebugEyes,DebugFeet,True)

DebugMesh = CreateCube()
ScaleMesh(DebugMesh,0.5/2,1.8/2,0.25/2)
PositionMesh(DebugMesh,0,1.8/2,0)
PositionEntity(DebugMesh,EntityX(DebugFeet,True),EntityY(DebugFeet,True),EntityZ(DebugFeet,True))
RotateEntity(DebugMesh,EntityPitch(DebugFeet,True),EntityYaw(DebugFeet,True),EntityRoll(DebugFeet,True))
EntityParent(DebugMesh,DebugFeet,True)
EntityColor(DebugMesh,000,000,255)

Global MXSpeed# = 0
Global MYSpeed# = 0
Global DebugFeetYaw# = 0
Global DebugEyesPitch# = 0

Const CEditView% = 1
Const CDebugView% = 2
Global CameraMode% = 0
CameraMode = CDebugView
EntityParent(Camera,0)
PositionEntity(Camera,EntityX(DebugEyes,True),EntityY(DebugEyes,True),EntityZ(DebugEyes,True))
RotateEntity(Camera,EntityPitch(DebugEyes,True),EntityYaw(DebugEyes,True),EntityRoll(DebugEyes,True))  
EntityParent(Camera,DebugEyes,True) 
 
PositionEntity(DebugFeet,0,0,-5) 
 
;ViewMode
;1: fixed (sprite always faces camera - Default) 
;2: free (sprite is independent of camera) 
;3: upright1 (sprite always faces camera, but rolls with camera as well, unlike mode no.1) 
;4: upright2 (sprite always remains upright. Gives a 'billboard' effect. Good For trees, spectators etc.) 
ViewMode% = 1

SpriteMesh = CreateMesh() 
Surface = CreateSurface(SpriteMesh) 
AddVertex(Surface, -0.5, 0.5, 0.0, 0.000, 0.000) 
AddVertex(Surface, 0.5, 0.5, 0.0, 1.000, 0.000) 
AddVertex(Surface, -0.5, -0.5, 0.0, 0.000, 1.000) 
AddVertex(Surface, 0.5, -0.5, 0.0, 1.000, 1.000) 
AddTriangle(Surface,0,1,2) 
AddTriangle(Surface,2,1,3) 
UpdateNormals(SpriteMesh)
PositionEntity(SpriteMesh,1,0,0) 
EntityFX(SpriteMesh,1)

SpriteOriginal = CreateSprite() 
ScaleSprite(SpriteOriginal,0.5,0.5) 
SpriteViewMode(SpriteOriginal,ViewMode)  
PositionEntity(SpriteOriginal,-1,0,0) 
 
SmileyTexDiffuse = CreateTexture(128,128)
SetBuffer(TextureBuffer(SmileyTexDiffuse))
ClsColor(125,125,125)
Cls()
Color(255,255,000)
Oval(0,0,128,128,1)
Color(001,001,001)
Oval(33,20,20,40)
Oval(73,20,20,40)
Plot(60,75)
Plot(66,75)
Line(63,95,43,95)
Line(43,95,33,80)
Line(64,95,84,95)
Line(84,95,94,80)
SetBuffer(BackBuffer())
TextureBlend(SmileyTexDiffuse,2)
EntityTexture(SpriteMesh,SmileyTexDiffuse,0,0)
EntityTexture(SpriteOriginal,SmileyTexDiffuse,0,0)

DLight = CreateLight(1)
LightColor(DLight,255,255,255)
PositionEntity(DLight,0,1024,-1024)
RotateEntity(DLight,45,0,0)
AmbientLight(125,125,125)
 
Repeat

 MainLoopStart% = MilliSecs()

 Cls()

 CameraModeSelect() 

 DebugUpdate()

 If(ViewMode = 1)
  RotateEntity(SpriteMesh,EntityPitch#(Camera,True),EntityYaw#(Camera,True),EntityRoll#(Camera,True))
 ElseIf(ViewMode = 2)		 
  ;
 ElseIf(ViewMode = 3)
  RotateEntity(SpriteMesh,EntityPitch#(Camera,True),EntityYaw#(Camera,True),0)
 ElseIf(ViewMode = 4)		 
  RotateEntity(SpriteMesh,0,EntityYaw#(Camera,True),0) 
 EndIf

 If(KeyDown(2)>0)  
  Wireframe(True)  
 Else  
  Wireframe(False)  
 EndIf

 SetBuffer(BackBuffer()) 
 RenderWorld()

 Color(255,255,255)
 Text(0,0,"Tris : "+TrisRendered())
 Text(0,20,"FPS = "+FPS)

 Flip(True) 

 MainLoopTime% = MilliSecs() - MainLoopStart	

 FPS = 1000/MainLoopTime

Until(KeyDown(1)>0) 

End

Function CameraModeSelect()
 ;Select between DebugView and EditView
 If(KeyHit(15)>0)
  If(CameraMode = CDebugView)
   CameraMode = CEditView
   EntityParent(Camera,0)
  ElseIf(CameraMode = CEditView)
   CameraMode = CDebugView
   EntityParent(Camera,0)
   PositionEntity(Camera,EntityX(DebugEyes,True),EntityY(DebugEyes,True),EntityZ(DebugEyes,True))
   RotateEntity(Camera,EntityPitch(DebugEyes,True),EntityYaw(DebugEyes,True),EntityRoll(DebugEyes,True))  
   EntityParent(Camera,DebugEyes,True)
  EndIf
 EndIf
End Function

Function DebugUpdate()
 If(CameraMode = CDebugView)
  MXSpeed = MouseXSpeed()  
  MYSpeed = MouseYSpeed()   
  MoveMouse(GraphicsWidth()/2,GraphicsHeight()/2) 
  DebugEyesPitch = DebugEyesPitch+MYSpeed*0.1 
  If(DebugEyesPitch > 89)
   DebugEyesPitch = 89
  EndIf
  If(DebugEyesPitch < -89)
   DebugEyesPitch = -89
  EndIf
  DebugFeetYaw = DebugFeetYaw-MXSpeed*0.1   
  RotateEntity(DebugEyes,DebugEyesPitch,0,0)
  RotateEntity(DebugFeet,0,DebugFeetYaw,0)  
  If(KeyDown(42)>0)
   SpeedCoeff# = 10.0 
  ElseIf(KeyDown(29)>0)
   SpeedCoeff# = 0.1
  Else
   SpeedCoeff# = 1.0
  EndIf 
  If(KeyDown(17)>0)  
   MoveEntity(DebugFeet,0,0,0.1*SpeedCoeff)  
  EndIf  
  If(KeyDown(31)>0)  
   MoveEntity(DebugFeet,0,0,-0.1*SpeedCoeff)  
  EndIf  
  If(KeyDown(30)>0)  
   MoveEntity(DebugFeet,-0.1*SpeedCoeff,0,0)  
  EndIf  
  If(KeyDown(32)>0)  
   MoveEntity(DebugFeet,0.1*SpeedCoeff,0,0)  
  EndIf  
  If(KeyDown(16)>0)  
   MoveEntity(DebugFeet,0,-0.1*SpeedCoeff,0)  
   EndIf 
  If(KeyDown(18)>0)  
   MoveEntity(DebugFeet,0,0.1*SpeedCoeff,0)  
  EndIf 
  If(KeyHit(201)>0)
   MoveEntity(Camera,0,0,0.1)
  EndIf
  If(KeyHit(209)>0)
   MoveEntity(Camera,0,0,-0.1)
  EndIf
  If(KeyDown(44)>0)
   TurnEntity(Camera,0,0,-1)
  EndIf
  If(KeyDown(45)>0)
   TurnEntity(Camera,0,0,1)
  EndIf
 ElseIf(CameraMode = CEditView)
  MXSpeed = MouseXSpeed()  
  MYSpeed = MouseYSpeed()   
  ;MoveMouse(GWidth/2,GHeight/2)
 EndIf
End Function
