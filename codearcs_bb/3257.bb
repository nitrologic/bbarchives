; ID: 3257
; Author: RemiD
; Date: 2016-02-13 06:39:11
; Title: pixel precise xoffset yoffset of a screenmesh+screentexture
; Description: This shows how to add a pixel precise xoffset or yoffset to a screenmesh+screentexture (useful for fullscreen effects)

Graphics3D(1000,625,32,2)
InitDX7Hack()
DisableTextureFilters()

SeedRnd(MilliSecs())

Camera = CreateCamera()
CameraRange(Camera,0.1,100)
CameraClsColor(Camera,000,000,000)

Global ThingsCount%
Dim ThingMesh(1000)

For n% = 1 To 1000 Step 1
 ThingsCount = ThingsCount + 1
 I% = ThingsCount
 ThingMesh(I) = CreateCube()
 ScaleMesh(ThingMesh(I),1.0/2,1.0/2,1.0/2)
 EntityColor(ThingMesh(I),Rand(025,255),Rand(025,255),Rand(025,255))
 PositionEntity(ThingMesh(I),Rnd(-15,15),Rnd(-15,15),Rnd(-15,15),True)
Next

OLight = CreateLight(2)
LightRange(OLight,3.0)
LightColor(OLight,224,224,224)

AmbientLight(032,032,032)

ScreenMesh = CreateMesh()
Surface = CreateSurface(ScreenMesh)
AddVertex(Surface,-1.0,0.625,0.0)
VertexTexCoords(Surface,0,Float(0)/1024,Float(0)/1024)
AddVertex(Surface,1.0,0.625,0.0)
VertexTexCoords(Surface,1,Float(1000)/1024,Float(0)/1024)
AddVertex(Surface,-1.0,-0.625,0.0)
VertexTexCoords(Surface,2,Float(0)/1024,Float(625)/1024)
AddVertex(Surface,1.0,-0.625,0.0)
VertexTexCoords(Surface,3,Float(1000)/1024,Float(625)/1024)
AddTriangle(Surface,0,1,2)
AddTriangle(Surface,2,1,3)
UpdateNormals(ScreenMesh)
EntityColor(ScreenMesh,255,255,255)
EntityFX(ScreenMesh,1)

ScreenTexture = CreateTexture(1024,1024,16+32+256)
SetBuffer(TextureBuffer(ScreenTexture))
 ClsColor(255,255,255)
 Cls()
;ScaleTexture(ScreenTexture,1.0,1.0)
TextureBlend(ScreenTexture,1)
EntityTexture(ScreenMesh,ScreenTexture)

PositionEntity(Camera,0,0,-16,True)

Const CScene% = 1
Const CScreen% = 2
Global RenderMode% = CScreen

Global XOffset% = 0
Global YOffset% = 0

Repeat

 MXDiff% = MouseXSpeed()
 MYDiff% = MouseYSpeed()
 MoveMouse(GraphicsWidth()/2,GraphicsHeight()/2)
 TurnEntity(Camera,MYDiff*0.1,-MXDiff*0.1,0)

 If( KeyDown(203)=1 )
  XOffset = XOffset - 1
 Else If( KeyDown(205)=1 )
  XOffset = XOffset + 1
 Else If( KeyDown(208)=1 )
  YOffset = YOffset - 1
 Else If( KeyDown(200)=1 )
  YOffset = YOffset + 1
 EndIf

; If( KeyHit(57)=1 )
;  If( RenderMode = CScene )
;   RenderMode = CScreen
;  Else If( RenderMode = CScreen )
;   RenderMode = CScene
;  EndIf
; EndIf

 PositionRotateEntityLikeOtherEntity(ScreenMesh,Camera)
 MoveEntity(ScreenMesh,1.0/Float(1000)*2*XOffset,0.625/Float(625)*2*YOffset,1.0)

 WireFrame(False)
 If( KeyDown(2)=1 )
  WireFrame(True)
 EndIf

 If( RenderMode = CScene )

  For I% = 1 To ThingsCount Step 1
   ShowEntity(ThingMesh(I))
  Next
  HideEntity(ScreenMesh)
  SetBuffer(BackBuffer())
  RenderWorld()

  Color(255,255,255)
  CText("RenderMode = Scene",0,0)

 Else If( RenderMode = CScreen )

  For I% = 1 To ThingsCount Step 1
   ShowEntity(ThingMesh(I))
  Next
  HideEntity(ScreenMesh)
  SetBuffer(BackBuffer())
  RenderWorld()
  CopyRect(0,0,GraphicsWidth(),GraphicsHeight(),0,0,BackBuffer(),TextureBuffer(ScreenTexture))

  For I% = 1 To ThingsCount Step 1
   HideEntity(ThingMesh(I))
  Next
  ShowEntity(ScreenMesh)
  SetBuffer(BackBuffer())
  RenderWorld()

  Color(255,255,255)
  CText("RenderMode = ScreenMesh+ScreenTexture",0,0)
  CText("XOffset = "+XOffset,0,15)
  CText("YOffset = "+YOffset,0,30)

 EndIf

 Flip(1)

Until( KeyDown(1)=1 )

End()

Const D3DTSS_MAGFILTER      = 16
Const D3DTSS_MINFILTER      = 17
Const D3DTSS_MIPFILTER      = 18
Const D3DTSS_MIPMAPLODBIAS  = 19
Const D3DTSS_MAXMIPLEVEL    = 20

Const D3DTFG_POINT        = 1
Const D3DTFG_LINEAR       = 2
Const D3DTFP_NONE         = 1
Const D3DTFN_POINT        = 1
Const D3DTFN_LINEAR       = 2

Function InitDX7Hack()
	DX7DBF_SetSystemProperties( SystemProperty("Direct3D7"), SystemProperty("Direct3DDevice7"), SystemProperty("DirectDraw7"), SystemProperty("AppHWND"), SystemProperty("AppHINSTANCE") )
End Function

Function DisableTextureFilters()
	;DX7DBF_SetMipmapLODBias( -10.0, 0 )
	For Level = 0 To 7
		DX7DBF_SetTextureStageState( Level, D3DTSS_MAGFILTER, D3DTFG_POINT )
		DX7DBF_SetTextureStageState( Level, D3DTSS_MINFILTER, D3DTFN_POINT )
		DX7DBF_SetTextureStageState( Level, D3DTSS_MIPFILTER, D3DTFP_NONE  )
	Next
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
