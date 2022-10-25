; ID: 2916
; Author: RemiD
; Date: 2012-02-03 05:41:07
; Title: Speed of the turns/moves/animations automatically adjusted to appear constant whatever the FPS
; Description: This allows your program to run at the same speed whatever the FPS, whatever is drawn on screen, whatever the computer hardware

;Speed of the turns/moves/animations automatically adjusted to appear constant whatever the FPS
; 
;This code shows a way to have an automatic adjustment of the speed of the turns/moves/animations 
;so that the program appears to run at the same speed, whatever the FPS, whatever is drawn on screen, whatever the hardware of the computer/graphics card. 
;The variable SC# must be applied to all turns/moves/animations. See the example.
;I have tested this code on several computers with different hardwares and os and it seems to work as expected.
;There may be inaccuracies if there is less than 10fps but overall it works well.

Graphics3D(800,600,32,2)     
   
HidePointer()  

SeedRnd(MilliSecs())
    
Origine = CreateCube()
ScaleMesh(Origine,0.1/2,100,0.1/2) 
EntityColor(Origine,125,000,000) 
EntityAlpha(Origine,0.5)
EntityFX(Origine,1)
PositionEntity(Origine,0,0,0)

Target = CreateCube() 
ScaleMesh(Target,0.1/2,100,0.1/2) 
EntityColor(Target,250,000,000) 
EntityAlpha(Target,0.5)
EntityFX(Target,1)
PositionEntity(Target,0,0,128) 

GroundMesh = CreatePlane()     
PositionEntity(GroundMesh,0,0,0)    
EntityColor(GroundMesh,050,050,050)         
     
Camera = CreateCamera()   
CameraRange(Camera,0.1,1000) 
CameraClsColor(Camera,063,125,250)   
PositionEntity(Camera,0,0,0)  
 
CharacterMesh = CreateCube()
ScaleMesh(CharacterMesh,0.5/2,1.7/2,0.25/2)
PositionMesh(CharacterMesh,0,1.7/2,0)
HideEntity(CharacterMesh)

Global PlayerFeet = CreatePivot()
Global PlayerEyes = CreatePivot()
MoveEntity(PlayerEyes,0,1.6,0.1+0.1)
EntityParent(PlayerEyes,PlayerFeet,True)
Global PlayerMesh = CopyMesh(CharacterMesh)
EntityColor(PlayerMesh,000,000,250)
EntityParent(PlayerMesh,PlayerFeet,True)

PositionEntity(Camera,EntityX(PlayerEyes,True),EntityY(PlayerEyes,True),EntityZ(PlayerEyes,True))
RotateEntity(Camera,EntityPitch(PlayerEyes,True),EntityYaw(PlayerEyes,True),EntityRoll(PlayerEyes,True))
MoveEntity(Camera,0,0,-1.5)
EntityParent(Camera,PlayerEyes,True)
TurnEntity(PlayerEyes,22.5,0,0)

Global BotsCount% = 0	
Dim BotFeet(10000)
Dim BotMesh(10000)

For i% = 1 To 1000 ;change this value to 1 or 10 or 100 or 1000 or more to see how the speed of the turns/moves are automatically adjusted so that it appears to run at the same speed whatever the FPS, whatever is drawn on screen.
 BotsCount = BotsCount + 1
 BId% = BotsCount
 BotFeet(BId) = CreatePivot()
 BotMesh(BId) = CopyMesh(CharacterMesh)
 EntityColor(BotMesh(BId),Rand(025,250),Rand(025,250),Rand(025,250))
 EntityParent(BotMesh(BId),BotFeet(BId),True)
 PositionEntity(BotFeet(BId),Rnd(-30,30),0,Rnd(10,90))
 RotateEntity(BotFeet(BId),0,Rnd(-180,180),0)
Next

SLight = CreateLight(1) 
LightColor(SLight,255,255,255)  
PositionEntity(SLight,-1000,1000,-1000) 
RotateEntity(SLight,45,-45,0) 
 
AmbientLight(050,050,050)

MillisecOld% = MilliSecs()
MillisecCur% = MilliSecs()
SecsCount% = 0
FramesCount% = 0
FramesTotalCount% = 0
FramesPerSecond% = 0
Global SC# = 1.0   
   
While(KeyDown(1)<>1)    
 
 MainLoopStart% = MilliSecs()      
         
 UpdateBots()

 UpdatePlayer() 
	 
 If( EntityZ(PlayerFeet) >= EntityZ(Target) ) 
  ClsColor(000,000,000) 
  Cls()
  Locate(0,0)
  Print("Average FramesPerSecond (FPS) = "+FramesTotalCount/SecsCount) 
  Print("Seconds passed during the travel = "+SecsCount) 
  FlushKeys()
  WaitKey()
  Goto LineExitGame 
 EndIf 
	 
 SetBuffer(BackBuffer())

 RenderWorld()     
	 	 
 MillisecCur = MilliSecs()
 If(MillisecCur < MillisecOld + 1000)
  FramesCount = FramesCount + 1
 ElseIf(MillisecCur >= MillisecOld + 1000)
  FramesPerSecond = FramesCount
  FramesTotalCount = FramesTotalCount + FramesCount
  FramesCount = 0   
  SecsCount = SecsCount + 1 
  MillisecOld = MillisecCur    			     
 EndIf
	  	        
 Text(0,0,"Triangles = "+TrisRendered())   
 Text(0,20,"MainLoopTime = "+MainLoopTime)   
 Text(0,40,"FramesCount = "+FramesCount)
 Text(0,60,"FramesPerSecond (FPS) = "+FramesPerSecond)
 Text(0,80,"FramesTotalCount = "+FramesTotalCount)     
 Text(0,100,"SC = "+SC) 
 Text(0,120,"SecsCount = "+SecsCount)    	 
 Text(0,140,"See how the speed of the turns/moves are automatically adjusted so that ")
 Text(0,160,"it appears to run at the same speed, whatever the FPS, whatever is drawn on screen.")
	 
 Flip(True) 
 
 MainLoopTime% = MilliSecs() - MainLoopStart
 If(MainLoopTime < 1)
  MainLoopTime = 1
 EndIf	  
    
 SC = Float(30) / 1000 * MainLoopTime	
	
Wend 
.LineExitGame 

EndGraphics()

End()
 
Function UpdatePlayer() 
 MoveEntity(PlayerFeet,0,0,0.2*SC) 
End Function 
 
Function UpdateBots() 
 For BId% = 1 To BotsCount 
  TurnEntity(BotFeet(BId),0,2.0*SC,0) 
  MoveEntity(BotFeet(BId),0,0,0.2*SC) 
 Next 
End Function
