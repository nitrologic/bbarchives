; ID: 616
; Author: jfk EO-11110
; Date: 2003-03-09 22:09:48
; Title: Outlines
; Description: Cell Shader Black Outlines scaled Distance Independently

Graphics3D 640,480,16,2
SetBuffer BackBuffer()

camera=CreateCamera()
MoveEntity camera,0,0,-3
CameraRange camera,1,2000
CameraClsColor camera,20,50,100
light=CreateLight()
RotateEntity light,30,30,30

n=200
Dim cubei(n),cubeo(n),sx#(n),sy#(n),sz#(n),x#(n),y#(n),z#(n)

min100=-30
max100=30
For i=0 To n
 cubei(i)=CreateSphere(7)
 EntityFX cubei(i),8 Or 4
 cubeo(i)=CreateSphere(7,cubei(i))
 EntityFX cubeo(i),1
 FlipMesh cubeo(i)
 EntityColor cubeo(i),0,0,0
 x(i)=Rnd(min100,max100)
 y(i)=Rnd(min100,max100)
 z(i)=Rnd(max100)
 sx(i)=Rnd(.1,.2)
 sy(i)=Rnd(.1,.2)
 sz(i)=Rnd(.1,.2)
 EntityColor cubei(i),Rand(20,255),Rand(20,255),Rand(20,255)
Next

correct=1
While KeyDown(1)=0
 If KeyHit(57)
  correct=correct Xor 1
 EndIf

 For i=0 To n
  x(i)=x(i)+sx(i)
  If x(i)>max100 Then sx(i)=-Rnd(.1,.2)
  If x(i)<min100 Then sx(i)=Rnd(.1,.2)  

  y(i)=y(i)+sy(i)
  If y(i)>max100 Then sy(i)=-Rnd(.1,.2)
  If y(i)<min100 Then sy(i)=Rnd(.1,.2)

  z(i)=z(i)+sz(i)
  If z(i)>max100 Then sz(i)=-Rnd(.1,.2)
  If z(i)<1 Then sz(i)=Rnd(.1,.2)

  PositionEntity cubei(i),x(i),y(i),z(i),1

  TurnEntity cubei(i),1,1,1
  ; d#=EntityDistance(camera,cubei(i)) ; for a mobile camera
  d#=Abs(EntityZ(camera)-EntityZ(cubei(i))) ; pretty accureate, but only for a north-pointing camera
  s#=1.01+(d/80.0)
  If s>2 Then s=2
  If correct=1
   ScaleEntity cubeo(i),s,s,s
  Else
   ScaleEntity cubeo(i),1.2,1.2,1.2
  EndIf
 Next

 RenderWorld()
 Text 0,0,"Hit Space to toggle Scaling Correcture"
 If correct
  Text 0,16,"Correcture is on"
 Else
  Text 0,16,"Correcture is off"
 EndIf
 Flip
Wend
End
