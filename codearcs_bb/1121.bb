; ID: 1121
; Author: jfk EO-11110
; Date: 2004-07-31 07:01:59
; Title: Watch
; Description: Analog Watch that actually works

Graphics3D 640,480,32,2
SetBuffer BackBuffer()

camera=CreateCamera()
TranslateEntity camera,0,0,-30
CameraClsMode camera,0,1

light=CreateLight()
RotateEntity light,45,45,45

zeiger1=CreateCube() ; second
RotateMesh zeiger1,0,45,0
FitMesh zeiger1,-1,0,-1,2,2,2
ScaleMesh zeiger1,.3,10,.3
EntityColor zeiger1,200,200,255

zeiger2=CreateCube() ; minute
RotateMesh zeiger2,0,45,0
FitMesh zeiger2,-1,0,-1,2,2,2
ScaleMesh zeiger2,.6,8,.6
EntityColor zeiger2,100,100,255
TranslateEntity zeiger2,0,0,1

zeiger3=CreateCube() ; hour
RotateMesh zeiger3,0,45,0
FitMesh zeiger3,-1,0,-1,2,2,2
ScaleMesh zeiger3,1,6,1
EntityColor zeiger3,0,0,255
TranslateEntity zeiger3,0,0,2

ce=CreateSphere()
ScaleEntity ce,2,2,2

For i=0 To 59
 cu=CreateCube()
 PositionEntity cu,Sin(i*6)*20,Cos(i*6)*20,0
 RotateEntity cu,0,0,-i*6
 ScaleEntity cu,.1,.1,.1
 If (Float(i)/5.0)=(i/5) Then ScaleEntity cu,.3,.3,.3
 If (Float(i)/15.0)=(i/15) Then ScaleEntity cu,.5,.5,.5
Next

While KeyDown(1)=0
 t$=CurrentTime$()
 If t$<>old_t$
  old_t$=t$
  second#=0+(Right$(t$,2))
  minute#=0+(Mid$(t$,4,2))
  hour#=(0+(Left$(t$,2))) : hour#=hour#+(minute#/60.0)

  RotateEntity zeiger1,0,0,(-second*6.0)
  RotateEntity zeiger2,0,0,(-minute*6.0)
  RotateEntity zeiger3,0,0,(-hour*30.0)
  Cls
  Text GraphicsWidth()/2,GraphicsHeight()*.33,"RELAX",1,1
  Text GraphicsWidth()/2,GraphicsHeight()*.66,"QUARTZ",1,1
  RenderWorld()
  Flip
 EndIf
 Delay 5
Wend
