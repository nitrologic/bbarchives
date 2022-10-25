; ID: 1759
; Author: BMA
; Date: 2006-07-22 12:29:29
; Title: Area Sound
; Description: Alternative to BB's 3DSound Functions

; ************************************************************************************************************
; AreaSound - BMA 2006
; ************************************************************************************************************

Type tAreaSound
  Field emitter%
  Field listener%
  Field sound%
  Field emitVolume#
  Field range#
  Field channel%
  Field volume#
End Type


Function AreaSound_create(emitter%, listener%, sound%, range#=10.0, emitVolume#=1.0, loop%=False, play%=True)
  Local s.tAreaSound  = New tAreaSound
  s\emitter     = emitter
  s\listener      = listener
  s\range       = range
  s\emitVolume    = emitVolume
  s\sound       = sound%
  If loop Then LoopSound(s\sound)
  If play Then s\channel = PlaySound(s\sound)
  AreaSound_calc(s)
  If Not loop Then Delete s
  Return Handle(s)
End Function

;call this method in your game loop, when using looped sounds
Function AreaSound_update()
  For s.tAreaSound = Each tAreaSound
    AreaSound_calc(s)
  Next
End Function

Function AreaSound_getCurrentVolume#(soundID%)
  s.tAreaSound = Object.tAreaSound(soundID)
  Return s\volume
End Function

Function AreaSound_setEmitVolume(soundID%, volume#)
  s.tAreaSound = Object.tAreaSound(soundID)
  s\emitVolume = volume
  AreaSound_calc(s)
End Function

Function AreaSound_setEmitterRange(soundID%, range#)
  s.tAreaSound = Object.tAreaSound(soundID)
  s\range = range
  AreaSound_calc(s)
End Function

Function AreaSound_play(soundID%, loop%=False)
  s.tAreaSound = Object.tAreaSound(soundID)
  If loop Then LoopSound(s\sound)
  s\channel = PlaySound(s\sound)
  AreaSound_calc(s)
End Function

Function AreaSound_stop(soundID%)
  s.tAreaSound = Object.tAreaSound(soundID)
  StopChannel(s\sound)
End Function

Function AreaSound_cleanUp()
  Delete Each tAreaSound 
End Function

; ************************************************************************************************************
; Private Method
; ************************************************************************************************************

Function AreaSound_calc(s.tAreaSound)
  d# = EntityDistance(s\listener, s\emitter)
  If d < s\range Then
    s\volume = s\emitVolume - d * s\emitVolume / (s\range)
    ChannelVolume s\channel, s\volume
  Else
    s\volume = 0.0
    ChannelVolume s\channel, 0.0
  EndIf
End Function




; ************************************************************************************************************
; ************************************************************************************************************
; Test Stuff
; ************************************************************************************************************
; ************************************************************************************************************

Graphics3D 640,480,32,2
SetBuffer BackBuffer()
SeedRnd MilliSecs()

camera=CreateCamera()
PositionEntity camera,0,20,-10

light=CreateLight()
RotateEntity light,90,0,0

listener  = CreateCube()
EntityColor listener,255,0,0

emitter   = CreateCube()
range   = CreateSphere(32,emitter)

EntityColor emitter,255,255,0
EntityColor range,0,255,255
EntityAlpha range,0.2

PointEntity camera,listener

PositionEntity listener,0,1,0
PositionEntity emitter,Rand(-10,10),1,Rand(-10,10)

;define emitter range
r# = 8.0

soundFile% = LoadSound("your_sound_here.mp3")

aSound% = AreaSound_create(emitter, listener, soundFile, r, 1.0, True)

While Not KeyHit(1)
   If KeyDown(205) Then TurnEntity listener,0,-1,0
   If KeyDown(203) Then TurnEntity listener,0,1,0
   If KeyDown(208) Then MoveEntity listener,0,0,-0.1
   If KeyDown(200) Then MoveEntity listener,0,0,0.1
   
   If KeyDown(49) Then
      r = r -0.05
      AreaSound_setEmitterRange(aSound, r)
   EndIf
   If KeyDown(50) Then
      r# = r# +0.05
      AreaSound_setEmitterRange(aSound, r)
   EndIf
   
   Xscale#  = ((100/1)  * r) / 100.0
   Yscale#  = ((100/1)  * r) / 100.0
   Zscale#  = ((100/1)  * r) / 100.0

   ScaleEntity range,Xscale#,Yscale#,Zscale#
   
   ;update looped sounds
   AreaSound_update()
   
   RenderWorld
   Text 0,0,"Move Listener with Cursor-Keys"
   Text 0,20,"Set Emitter Range with 'N' and 'M' Keys - Range: "+r
   Text 0,40,"Emitter Sound Volume: "+AreaSound_getCurrentVolume(aSound)
   Flip
Wend
End
