; ID: 3022
; Author: RemiD
; Date: 2013-02-03 05:16:51
; Title: Sight filter : for damage taken | blood loss
; Description: An example to add a filter to the sight of the user so that it intensifies the sensation of damage | blood loss when hurt by something

Global GWidth% = 800
Global GHeight% = 600
Global GColors% = 32
Global GMode% = 2
Graphics3D(GWidth,GHeight,GColors,GMode)

SeedRnd(MilliSecs())

Const CDecrease% = 1
Const CIncrease% = 2

Global Camera = CreateCamera() 
CameraRange(Camera,0.1,100)

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

PositionEntity(DebugFeet,5,0,-3)

Global SightFilterMesh = CreateMesh() 
Surface = CreateSurface(SightFilterMesh)
AddVertex(Surface, -0.5, 0.5, 0.0, 0.000, 0.000)
AddVertex(Surface, 0.5, 0.5, 0.0, 1.000, 0.000)
AddVertex(Surface, -0.5, -0.5, 0.0, 0.000, 1.000)
AddVertex(Surface, 0.5, -0.5, 0.0, 1.000, 1.000)
AddTriangle(Surface,0,1,2)
AddTriangle(Surface,2,1,3)
UpdateNormals(SightFilterMesh)
EntityFX(SightFilterMesh,1)
EntityOrder(SightFilterMesh,-2)
PositionEntity(SightFilterMesh,EntityX(Camera,True),EntityY(Camera,True),EntityZ(Camera,True))
RotateEntity(SightFilterMesh,EntityPitch(Camera,True),EntityYaw(Camera,True),EntityRoll(Camera,True))
EntityParent(SightFilterMesh,Camera,True)
MoveEntity(SightFilterMesh,0,0,1)
ScaleEntity(SightFilterMesh,2.0,2.0,1)
EntityColor(SightFilterMesh,200,000,000) ;Blood color
EntityFX(SightFilterMesh,1) ;Fullbright
EntityAlpha(SightFilterMesh,1.0) ;Alpha 1.0
EntityOrder(SightFilterMesh,-1) ;To make sure the SightFilterMesh is drawn after all others meshes so that it is always a filter in front of the user eyes
;HideEntity(SightFilterMesh)
SightFilterAlpha# = 0.0 ;The variable that will be used to hold the value of the transparency of the filter
SightFilterState% = CIncrease

For i% = 1 To 20
 Bubble = CreateSphere(16)
 WHD# = Rnd(0.1,1.0)
 ScaleMesh(Bubble,WHD,WHD,WHD)
 PositionEntity(Bubble,Rnd(0,10),Rnd(0,3),Rnd(0,10))
 R% = Rand(000,255)
 G% = Rand(000,255)
 B% = Rand(000,255)
 EntityColor(Bubble,R,G,B)
 EntityAlpha(Bubble,0.1)
 EntityShininess(Bubble,0.5)
Next

DLight = CreateLight(1)
LightColor(DLight,240,240,240)
PositionEntity(DLight,-1024,1024,-1024)
RotateEntity(DLight,45,-45,0)

AmbientLight(120,120,120)

Repeat

 CameraModeSelect() 

 DebugUpdate()

 If(SightFilterState = CIncrease)
  If(SightFilterAlpha < 1.0)
   SightFilterAlpha = SightFilterAlpha + 0.01
   EntityAlpha(SightFilterMesh,SightFilterAlpha)
  ElseIf(SightFilterAlpha => 1.0)
   SightFilterAlpha = 1.0
   EntityAlpha(SightFilterMesh,SightFilterAlpha)
   SightFilterState = CDecrease
  EndIf
 ElseIf(SightFilterState = CDecrease)
  If(SightFilterAlpha > 0)
   SightFilterAlpha = SightFilterAlpha - 0.01
   EntityAlpha(SightFilterMesh,SightFilterAlpha)
  ElseIf(SightFilterAlpha <= 0)
   SightFilterAlpha = 0
   EntityAlpha(SightFilterMesh,SightFilterAlpha)
   SightFilterState = CIncrease
  EndIf
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

 Flip(1) 

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
 ElseIf(CameraMode = CEditView)
  MXSpeed = MouseXSpeed()  
  MYSpeed = MouseYSpeed()   
  ;MoveMouse(GWidth/2,GHeight/2)
 EndIf
End Function
