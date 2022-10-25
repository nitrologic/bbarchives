; ID: 3264
; Author: RemiD
; Date: 2016-04-17 08:11:40
; Title: B3D mesh/entity debbugger
; Description: to analyze the surfaces, vertices, triangles, joints/bones, in your mesh/entity

Graphics3D(800,600,32,2) 

HidePointer() 
 
SeedRnd(MilliSecs())

Global Camera = CreateCamera()
CameraRange(Camera,0.01,100)
CameraClsColor(Camera,025,025,025)

Origine = CreateCube()
ScaleMesh(Origine,0.01/2,0.01/2,0.01/2)
EntityColor(Origine,255,000,255)
EntityFX(Origine,1)

DLight = CreateLight(1)
LightColor(DLight,224,224,224)
PositionEntity(DLight,-1000,1000,-1000,True)
RotateEntity(DLight,45,-45,0,True)

AmbientLight(064,064,064)

Global MPX%
Global MPY%
Global MXDiff%
Global MYDiff%

Global InteractionMode%
Const C2D% = 1
Const C3D% = 2
InteractionMode = C3D
HidePointer()

Global GhostRoot
Global GhostRootYaw#
Global GhostEyes
Global GhostEyesPitch#
BuildGhost()

Cube1w1h1dMesh = CreateCube()
ScaleMesh(Cube1w1h1dMesh,1.0/2,1.0/2,1.0/2)
PositionMesh(Cube1w1h1dMesh,0,1.0/2,0)
EntityAlpha(Cube1w1h1dMesh,0.25)

;for a static mesh :
;Global B3DMesh = LoadMesh("zombie.b3d")
;ScaleMesh(B3DMesh,0.1,0.1,0.1)
;NameEntity(B3DMesh,"B3DRoot")

;for a rigged skinned animated mesh :
Global B3DMesh = LoadAnimMesh("zombie.b3d")
ScaleEntity(B3DMesh,0.1,0.1,0.1)
NameEntity(B3DMesh,"B3DRoot")

;to debug surfaces, vertices, triangles :
SCount% = CountSurfaces(B3DMesh)
DebugLog("Mesh has "+SCount+" surface(s).")
For SI% = 1 To SCount Step 1
 Surface = GetSurface(B3DMesh,SI)
 VCount% = CountVertices(Surface)
 TCount% = CountTriangles(Surface)
 DebugLog("Surface"+SI+" has "+VCount+" vertices, "+TCount+" triangles.")
 VTotalCount% = VTotalCount + VCount
 TTotalCount% = TTotalCount + TCount
Next
DebugLog("Mesh has "+VTotalCount+" vertices, "+TTotalCount+" triangles.")

;to debug joints/bones :
Type JB
 Field JBH%
 Field ParentH%
 Field ChildsCount%
 Field ChildH%[10]
 Field State%
End Type

AnalyzeEntity(B3DMesh)

;markers to debug the joints/bones
For i.JB = Each JB
 Marker = CreateSphere(8)
 ScaleMesh(Marker,0.03/2,0.03/2,0.03/2)
 PositionEntityLikeOtherEntity(Marker,i\JBH)
 EntityParent(Marker,i\JBH,True)
Next

PositionEntity(GhostRoot,0,1.65,-3,True)

Global Timer = CreateTimer(30)

Main()

End()

Function Main()

 While( KeyDown(1)<>1 )

  MPX = MouseX()
  MPY = MouseY()
  MXDiff = MouseXSpeed()
  MYDiff = MouseYSpeed()

  UpdateInteractionMode()
  If( InteractionMode = C2D )
   ;
  Else If( InteractionMode = C3D )
   UpdateGhost()
   PositionRotateEntityLikeOtherEntity(Camera,GhostEyes) 	   
  EndIf

  Wireframe(False)
  If( KeyDown(2)=1 )
   Wireframe(True)
  EndIf

  SetBuffer(BackBuffer())
  RenderWorld()

  Color(255,255,255)
  CText("Triangles = "+TrisRendered(),0,0)

  WaitTimer(Timer)
  VWait():Flip(False)

 Wend

End Function

Function CText(TextStr$,PX%,PY%)
 Text(PX,PY,TextStr,False,False)
End Function

Function PositionEntityLikeOtherEntity(Entity,OEntity)
 PositionEntity(Entity,EntityX(OEntity,True),EntityY(OEntity,True),EntityZ(OEntity,True),True)
End Function

Function RotateEntityLikeOtherEntity(Entity,OEntity)
 RotateEntity(Entity,EntityPitch(OEntity,True),EntityYaw(OEntity,True),EntityRoll(OEntity,True),True)
End Function

Function PositionRotateEntityLikeOtherEntity(Entity,OEntity)
 PositionEntity(Entity,EntityX(OEntity,True),EntityY(OEntity,True),EntityZ(OEntity,True),True)
 RotateEntity(Entity,EntityPitch(OEntity,True),EntityYaw(OEntity,True),EntityRoll(OEntity,True),True)
End Function

Function UpdateInteractionMode() 

 If( KeyHit(15)=1 )
  If( InteractionMode = C2D )
   InteractionMode = C3D
   HidePointer()
  Else If( InteractionMode = C3D )
   InteractionMode = C2D
   ShowPointer()
  EndIf
 EndIf

End Function

Function BuildGhost()
 
 GhostRoot = CreatePivot()

 GhostEyes = CreatePivot()
 PositionRotateEntityLikeOtherEntity(GhostEyes,GhostRoot)
 EntityParent(GhostEyes,GhostRoot,True)

End Function

Function UpdateGhost()

 MoveMouse(GraphicsWidth()/2,GraphicsHeight()/2)
 GhostRootYaw = GhostRootYaw - Float(MXDiff)/10
 RotateEntity(GhostRoot,0,GhostRootYaw,0,False)
 GhostEyesPitch = GhostEyesPitch + Float(MYDiff)/10
 If( GhostEyesPitch < -89 )
  GhostEyesPitch = -89
 Else If( GhostEyesPitch > 89 )
  GhostEyesPitch = 89
 EndIf
 RotateEntity(GhostEyes,GhostEyesPitch,0,0,False)

 If( KeyDown(42) = 0 And KeyDown(29) = 0 )
  Speed# = 0.1
 Else If( KeyDown(42) = 1 And KeyDown(29) = 0 )
  Speed# = 1
 Else If( KeyDown(42) = 0 And KeyDown(29) = 1 )
  Speed# = 0.01
 EndIf

 If( KeyDown(17)=1 )
  MoveEntity(GhostRoot,0,0,Speed)
 Else If( KeyDown(31)=1 )
  MoveEntity(GhostRoot,0,0,-Speed)
 EndIf
 If( KeyDown(30)=1 )
  MoveEntity(GhostRoot,-Speed,0,0)
 Else If( KeyDown(32)=1 )
  MoveEntity(GhostRoot,Speed,0,0)
 EndIf
 If( KeyDown(16)=1 )
  MoveEntity(GhostRoot,0,-Speed,0)
 Else If( KeyDown(18)=1 )
  MoveEntity(GhostRoot,0,Speed,0)
 EndIf

End Function

Function AnalyzeEntity(EntityH)

 JBsCount% = 0

 JBH% = EntityH

 JBsCount = JBsCount + 1
 i.JB = New JB
 i\JBH = JBH
 i\ParentH = 0
 i\ChildsCount = CountChildren(i\JBH)
 For c% = 1 To i\ChildsCount Step 1
  i\ChildH[c] = GetChild(i\JBH,c)
 Next
 i\State = True
 CurrentH% = i\JBH

 .LineAnalyzeCurrent
 DebugLog(CurrentH+"|"+EntityName(CurrentH)+"|"+CountChildren(CurrentH))
 For CI% = 1 To CountChildren(CurrentH) Step 1
  JBH% = GetChild(CurrentH,CI)
  JBsCount = JBsCount + 1
  i.JB = New JB
  i\JBH = JBH 
  i\ParentH = CurrentH
  i\ChildsCount = CountChildren(i\JBH)
  For c% = 1 To i\ChildsCount Step 1
   i\ChildH[c] = GetChild(i\JBH,c)
  Next
  i\State = False
 Next

 For i.JB = Each JB
  If( i\State = False )
   CurrentH = i\JBH
   i\State = True
   Goto LineAnalyzeCurrent
  EndIf
 Next

 DebugLog("Entity has "+JBsCount+" joint(s)/bone(s)")

End Function
