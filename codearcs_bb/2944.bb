; ID: 2944
; Author: RemiD
; Date: 2012-05-12 13:30:03
; Title: Record, Write, Read, Play, the position and rotation of an entity
; Description: An example on how to have a replay in a video game

;Record, Write, Read, Play, the position and rotation of an entity
;F1 to switch from Idle mode to Record mode and from Record mode to Idle mode
;F2 to switch from Idle mode to Play mode and from Play mode to Idle mode
;The characters move with their AI when in Idle Mode or in Record Mode, and they move with the Replay data when in Play mode.

Graphics3D(800,600,32,2)

HidePointer()

SeedRnd(MilliSecs())

Camera = CreateCamera()
CameraRange(Camera,0.1,1000)
PositionEntity(Camera,0,0,0)

GroundMesh = CreateCube()
ScaleEntity(GroundMesh,64.0/2,0.1/2,64.0/2)
PositionEntity(GroundMesh,64.0/2,-0.1/2,64.0/2)
EntityColor(GroundMesh,025,075,025)

CharacterXMesh = CreateCube()
ScaleMesh(CharacterXMesh,0.5/2,1.8/2,0.25/2)
PositionMesh(CharacterXMesh,0,1.8/2,0)
HideEntity(CharacterXMesh)

Dim CharacterMesh(5)
Global CharactersCount% = 0

For CId% = 1 To 5
 CharactersCount% = CharactersCount% + 1
 CId% = CharactersCount%
 CharacterMesh(CId%) = CopyEntity(CharacterXMesh)
 PositionEntity(CharacterMesh(CId%),Rnd(0,64),0,Rnd(0,64))
 RotateEntity(CharacterMesh(CId%),0,Rnd(-180,180),0)
 EntityColor(CharacterMesh(CId%),Rand(000,255),Rand(000,255),Rand(000,255))
Next

Const Idle% = 0
Const Record% = 1
Const Play% = 2

Global TrackingMode% = Idle%
Global File
Global FileName$ = "Tracking.dat"

PositionEntity(Camera,32,32,-16)
RotateEntity(Camera,45,0,0)

SunLight = CreateLight(1)
LightColor(SunLight,255,255,255)
PositionEntity(SunLight,32,1024,-1024)
RotateEntity(SunLight,45,0,0)
AmbientLight(125,125,125)

Repeat

 Cls()
 
 CharactersUpdate()
 
 TrackingModeUpdate()

 SetBuffer(BackBuffer())
 RenderWorld()

 Text(0,0,"Triangles : "+TrisRendered())
 Text(0,20,"LoopTime% : "+LoopTime%)
 Text(0,40,"TrackingMode% : "+TrackingMode%)

 Flip(1)

 ;Calculates the time of a loop  
 EndTime% = MilliSecs()    
 LoopTime% = EndTime% - StartTime%  
 StartTime% = MilliSecs()

Until(KeyDown(1)=True)

Function CharactersUpdate()
 If( TrackingMode% = Idle% Or TrackingMode% = Record%)
  For CId% = 1 To CharactersCount%
   If( EntityX(CharacterMesh(CId%)) > 0 And EntityX(CharacterMesh(CId%)) < 64 And EntityZ(CharacterMesh(CId%)) > 0 And EntityZ(CharacterMesh(CId%)) < 64 )
    MoveEntity(CharacterMesh(CId%),0,0,0.1)
   Else
    RotateEntity(CharacterMesh(CId%),0,Rnd(-180,180),0)
    MoveEntity(CharacterMesh(CId%),0,0,0.1)
   EndIf
  Next
 EndIf
End Function

Function TrackingModeUpdate()
 If(KeyHit(59)=True)
  If(TrackingMode% = Idle%)
   TrackingMode% = Record%
   File = WriteFile(FileName$)
  ElseIf(TrackingMode% = Record%)
   TrackingMode% = Idle%
   CloseFile(File)
  EndIf
 EndIf 
 If(KeyHit(60)=True)
  If(TrackingMode% = Idle%)
   TrackingMode% = Play%
   File = ReadFile(FileName$)
  ElseIf(TrackingMode% = Play%)
   TrackingMode% = Idle%
   CloseFile(File)
  EndIf
 EndIf
 If(TrackingMode% = Record%)
  TrackingRecordWrite()
 ElseIf(TrackingMode% = Play%)
  TrackingReadPlay()
 EndIf
End Function

Function TrackingRecordWrite()
 For CId% = 1 To CharactersCount%
  WriteFloat(File,EntityX(CharacterMesh(CId%)))
  WriteFloat(File,EntityY(CharacterMesh(CId%)))
  WriteFloat(File,EntityZ(CharacterMesh(CId%)))
  WriteFloat(File,EntityPitch(CharacterMesh(CId%)))
  WriteFloat(File,EntityYaw(CharacterMesh(CId%)))
  WriteFloat(File,EntityRoll(CharacterMesh(CId%)))
 Next
End Function

Function TrackingReadPlay()
 If(Not Eof(File))
  For CId% = 1 To CharactersCount%
   X# = ReadFloat(File)
   Y# = ReadFloat(File)
   Z# = ReadFloat(File)		
   Pitch# = ReadFloat(File)
   Yaw# = ReadFloat(File)
   Roll# = ReadFloat(File)
   PositionEntity(CharacterMesh(CId%),X#,Y#,Z#)
   RotateEntity(CharacterMesh(CId%),Pitch#,Yaw#,Roll#)
  Next
 Else
  CloseFile(File)
  TrackingMode% = Idle%
 EndIf		
End Function

End
