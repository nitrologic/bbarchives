; ID: 3093
; Author: RemiD
; Date: 2013-12-12 03:51:23
; Title: Snowflakes accumulation on the ground with pixels added on a texture
; Description: Once a snowflake reaches the ground, it is drawn on the texture of the terrain

Graphics3D(800,600,32,2)
SetBuffer(BackBuffer())
SeedRnd(MilliSecs())

Camera = CreateCamera()
CameraRange(Camera,0.1,1000)

GreySkyMesh = CreateCube()
FlipMesh(GreySkyMesh)
ScaleMesh(GreySkyMesh,256,256,256)
PositionEntity(GreySkyMesh,16,0,16)
EntityColor(GreySkyMesh,100,100,100)
EntityFX(GreySkyMesh,1)

TerrainB3D = CreateTerrain(32)
ScaleEntity(TerrainB3D,1,25.5,1)
TerrainDetail(TerrainB3D,2048,True)
TerrainShading(TerrainB3D,True)
PositionEntity(TerrainB3D,0,0,0)

For VX% = 0 To 31
 For VZ% = 0 To 31
  HeightRGB% = Rand(030,035)
  Height# = 1.0/255*HeightRGB
  ModifyTerrain(TerrainB3D,VX,VZ,Height,False) 
 Next
Next

TerrainTexColors = CreateTexture(256,256)
SetBuffer(TextureBuffer(TerrainTexColors))
 ClsColor(000,100,000)
 Cls()
 For PX% = 0 To 255
  For PY% = 0 To 255
   R% = 000 
   G% = Rand(090,110)
   B% = 000
   Color(R,G,B)
   Plot(PX,PY)
  Next
 Next
SetBuffer(BackBuffer())
ScaleTexture(TerrainTexColors,32,32)
EntityTexture(TerrainB3D,TerrainTexColors,0,0)

SnowFlakeXMesh = CreateSphere(2)
ScaleMesh(SnowFlakeXMesh,0.0625/2,0.0625/2,0.0625/2)
EntityFX(SnowFlakeXMesh,1)
HideEntity(SnowFlakeXMesh)

Const CIsStatic% = 1
Const CIsMoving% = 2

Global SnowFlakesCount% = 0
Type SnowFlake
 Field Mesh
 Field State%
 Field TY#
End Type

SLight = CreateLight(1)
LightColor(SLight,225,225,225)
PositionEntity(SLight,-1000,1000,-1000)
RotateEntity(SLight,45,-45,0)
AmbientLight(100,100,100)

PositionEntity(Camera,16,3.0+1.6,0)

Global SC#

Repeat

 MainLoopStart% = MilliSecs()

 If(KeyDown(17)=1)
  MoveEntity(Camera,0,0,0.1*SC)
 ElseIf(KeyDown(31)=1)
  MoveEntity(Camera,0,0,-0.1*SC)
 EndIf
 If(KeyDown(30)=1)
  TurnEntity(Camera,0,1*SC,0)
 ElseIf(KeyDown(32)=1)
  TurnEntity(Camera,0,-1*SC,0)
 EndIf

 If(SnowFlakesCount < 10000)
  For i% = 1 To 10*SC
   SnowFlakesCount = SnowFlakesCount + 1
   s.SnowFlake = New SnowFlake
   s\Mesh = CopyEntity(SnowFlakeXMesh)
   PositionEntity(s\Mesh,16+Rnd(-16.0,16.0),1.6+15,Rnd(0,32.0))
   s\TY# = TerrainY(TerrainB3D,EntityX(s\Mesh,True),0,EntityZ(s\Mesh,True))
   s\State = CIsMoving
  Next
 EndIf

 For s.SnowFlake = Each SnowFlake
  If(s\State = CIsMoving)
   If(EntityY(s\Mesh,True) > s\TY+0.025)
    TranslateEntity(s\Mesh,0,-0.1*SC,0)
   ElseIf(EntityY(s\Mesh,True) <= s\TY+0.025)
    PositionEntity(s\Mesh,EntityX(s\Mesh,True),s\TY+0.025,EntityZ(s\Mesh,True))
    s\State = CIsStatic
    PX% = Floor(EntityX(s\Mesh,True)*8)
    PY% = 256-1-Floor(EntityZ(s\Mesh,True)*8)
    SetBuffer(TextureBuffer(TerrainTexColors))
     Color(255,255,255)
     Plot(PX,PY)
    SetBuffer(BackBuffer())
    FreeEntity(s\Mesh)
    s\Mesh = 0
    Delete(s)
    SnowFlakesCount = SnowFlakesCount - 1
   EndIf
  EndIf
 Next

 If(KeyDown(2)=1)
  WireFrame(True)
 Else
  WireFrame(False)
 EndIf
 
 ClsColor(000,000,000)
 Cls()
 RenderWorld()

 Color(255,255,255)
 Text(0,0,"Triangles = "+TrisRendered())
 Text(0,20,"FPS = "+Str(FPS))
 Text(0,40,"SnowFlakesCount = "+Str(SnowFlakesCount))

 Flip(1)

 MainLoopTime% = MilliSecs() - MainLoopStart
 If(MainLoopTime = 0)
  MainLoopTime = 1
 EndIf

 FPS% = 1000/MainLoopTime
 SC# = Float(30) / 1000 * MainLoopTime 

Until(KeyDown(1)=1)

End()
