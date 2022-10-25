; ID: 2007
; Author: Matt Merkulov
; Date: 2007-05-04 19:33:51
; Title: Smoothly moving object in 3D
; Description: Object in 3D (sphere) smoothly passing thru random set of points (cubes) visiting them in certain time (triple cubic spline interpolation)

;Object in 3D (sphere) smoothly passing thru random set of points
; (cubes) visiting them in certain time (triple cubic spline interpolation - by Matt Merkulov

SeedRnd MilliSecs()

Const q=10

Dim ptc#(q+2,2)
Dim a#(2)
Dim b#(2)
Dim c#(2)
Dim d#(2)
Dim tim(q+2)
Dim oc#(2)

Graphics3D 800,600
cam = CreateCamera()
RotateEntity CreateLight(),45,45,0
SetFont LoadFont ("Arial",16)
sphere = CreateSphere()
ScaleEntity sphere,2,2,2

x#=0
y#=300
Color 255,0,0
For n=1 To q
 x#=Rnd(-20,20)
 y#=Rnd(-20,20)
 z#=Rnd(20,50)
 ptc#(n,0)=x#
 ptc#(n,1)=y#
 ptc#(n,2)=z#
 tim(n)=t
 PositionEntity CreateCube(), x#, y#, z#
 t=t+Rand(1000,3000)
Next

For nn=0 To 2
 ptc#(0,nn)=ptc#(q,nn)
 ptc#(q+1,nn)=ptc#(1,nn)
 ptc#(q+2,nn)=ptc#(2,nn)
Next
tim(q+1)=t
tim(q+2)=t+tim(2)
tim(0)=tim(q)-t

Color 255,255,255
SetBuffer BackBuffer()

t=t+1
n=q+1
Repeat
 If t>tim(n+1) Then
  n=n+1
  If n>q Then
   n=1
   ms=0
   tbeg=MilliSecs()
  End If
  For nn=0 To 2
   d#(nn)=ptc#(n,nn)
   c#(nn)=(ptc#(n+1,nn)-ptc#(n-1,nn))/(tim(n+1)-tim(n-1))
   dy2#=(ptc#(n+2,nn)-ptc#(n,nn))/(tim(n+2)-tim(n))
   x3#=tim(n+1)-tim(n)
   xx3#=x3#*x3#
   b#(nn)=(3*ptc#(n+1,nn)-dy2#*x3#-2*c#(nn)*x3#-3*d#(nn))/xx3#
   a#(nn)=(dy2#-2*b#(nn)*x3#-c#(nn))/(3*xx3#)
  Next
 End If

 For nn=0 To 2
  v#=t-tim(n)
  vv#=v#*v#
  oc#(nn)=a#(nn)*vv#*v#+b#(nn)*vv#+c#(nn)*v#+d#(nn)
 Next

 PositionEntity sphere, oc#(0),oc#(1), oc#(2)
 RenderWorld
 Color 0,0,255
 Text 0,0,"Time:"+(.001*t)+"s"
 For nn=1 To q
  CameraProject cam, ptc#(nn, 0), ptc#(nn, 1), ptc#(nn, 2)
  Text ProjectedX(), ProjectedY(), (.001*tim(nn)), True, True
 Next
 Color 255,255,255
 Flip
 t=MilliSecs()-tbeg
Until KeyHit(1)
