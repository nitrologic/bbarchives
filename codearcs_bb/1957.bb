; ID: 1957
; Author: Matt Merkulov
; Date: 2007-03-14 11:30:34
; Title: 4D octahedron - ribs and vertexes
; Description: Drawing 4D octahedron projection using cylinders as ribs and spheres as vertexes

;Drawing 4D octahedron projection using cylinders as ribs and spheres as vertexes by Matt Merkulov

;Control keys: 1-6 - rotate octahedron in one of 6 4D rotational surfaces

Graphics3D 640,480,32
p=CreatePivot()
PositionEntity CreateCamera(p), 0,0,-2.5
RotateEntity CreateLight(), 45,45,0

Dim v#(7,4)
For n1=0 To 7
 v#(n1, n1 Shr 1)=(n1 And 1) Shl 1-1
 v(n1,4)=CreateSphere(10)
 ScaleEntity v(n1,4), .1, .1, .1
Next

Dim r(5,1)
For n1=0 To 2
 For n2=n1+1 To 3
 r(n, 0)=n1
 r(n, 1)=n2
 n=n+1
 Next
Next

Dim e(30)
Dim d#(2)

ang#= 1
sina#= Sin(ang#)
cosa#= Cos(ang#)
Repeat
 For n3=0 To 5
 If KeyDown(n3+2) Then
  n1=r(n3,0)
  n2=r(n3,1)
  For n=0 To 7
  c1#= v(n, n1)*cosa#-v(n, n2)*sina#
  c2#= v(n, n1)*sina# + v(n, n2)*cosa#
  v(n, n1)=c1#
  v(n, n2)=c2#
  Next
 End If
 Next

 num=0
 For n1=0 To 7  
 For n2=n1+2-(n1 And 1) To 7  
  If e(num)=0 Then e(num)=CreateCylinder(8)
  a=e(num)
  For n3=0 To 2
  d#(n3)= .5*(v(n1, n3) +v(n2, n3))
  Next    
  PositionEntity a, d#(0), d#(1), d#(2)
  dd#= 0
  For n3=0 To 2
  d#(n3)=v(n1, n3)-v(n2, n3)
  dd#= dd# + d#(n3)*d#(n3)
  Next    
  ScaleEntity a, .05, .5*Sqr(dd#),.05
  AlignToVector a, d#(0), d#(1), d#(2), 2   
  num=num+1
 Next
 Next

 For n3=0 To 2
 If KeyDown(n3+8) Then  
  TurnEntity p, ang#*(n3=0), ang#*(n3=1), ang#*(n3=2)
 End If
 Next

 For n=0 To 7
 PositionEntity v(n, 4), v(n, 0), v(n, 1), v(n, 2)
 Next

 RenderWorld
 Flip
Until KeyHit(1)
