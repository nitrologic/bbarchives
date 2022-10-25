; ID: 1956
; Author: Matt Merkulov
; Date: 2007-03-14 11:27:38
; Title: Hypercube - ribs and vertexes
; Description: Drawing hypercube (4D cube, tesseract) projection using cylinders as ribs and spheres as vertexes

;Drawing hypercube (4D cube, tesseract) projection using cylinders as ribs and spheres as vertexes by Matt Merkulov

;Control keys: 1-6 - rotate tesseract in one of 6 4D rotational surfaces

Graphics3D 640,480,32
p=CreatePivot()
PositionEntity CreateCamera(p), 0,0,-5
RotateEntity CreateLight(), 45,45,0

; Filling a file of tops. Thus if number of each top to present in
; Binary calculation everyone of bats will define(determine) corresponding(meeting)
; Coordinate: if bats it is switched off, the coordinate is equal-1, differently - +1
Dim v#(15,4)
For n1=0 To 15
 For n2=0 To 3
 v#(n1, n2)=Sgn(n1 And(1 Shl n2))*2-1
 Next
 ; Creation of the spheres representing tops
 v(n1,4)=CreateSphere(10)
 ScaleEntity v(n1,4), .2, .2, .2
Next

; Filling a file of planes of rotation(every possible variants of pairs axes)
Dim r(5,1)
For n1=0 To 2
 For n2=n1+1 To 3
 r(n, 0)=n1
 r(n, 1)=n2
 n=n+1
 Next
Next

; A file of addresses of cylinders - edges
Dim e(15,3)
; An auxiliary vector
Dim d#(2)

; Preliminary calculations
ang#=1
sina#=Sin(ang#)
cosa#=Cos(ang#)
Repeat

 ; Turn of a body(and is more exact-tops) around of planes by pressing keys 1-6
 For n3=0 To 5
 If KeyDown(n3+2) Then
  n1=r(n3,0)
  n2=r(n3,1)
  For n=0 To 15
  c1#=v(n, n1)*cosa#-v(n, n2)*sina#
  c2#=v(n, n1)*sina#+ v(n, n2)*cosa#
  v(n, n1)=c1#
  v(n, n2)=c2#
  Next
 End If
 Next

 ; Creation of edges. A cycle on all tops
 For n1=0 To 15
 For n=0 To 3
  ; Definition of adjacent top by inclusion one bat(that is changes
  ; One of coordinates). Thus occurs(happens) otsecheni duplicating edges: number
  ; The first coordinate there should be less the second.
  n2=n1 Or(1 Shl n)
  If n1 <> n2 Then
  ; Creation of the cylinder-edge(if it is not created yet)
  If e(n1, n)=0 Then e(n1, n)=CreateCylinder(8, False)
  a=e(n1, n)
  ; Calculation of the center of an edge and installation of the cylinder in the center
  For n3=0 To 2
   d#(n3)=.5*(v(n1, n3) +v(n2, n3))
  Next
  PositionEntity a, d#(0), d#(1), d#(2)
  ; Calculations of a vector of an edge and alignment of the cylinder on it(him,them)
  dd#=0
  For n3=0 To 2
   d#(n3)=v(n1, n3)-v(n2, n3)
   dd#=dd#+ d#(n3)*d#(n3)
  Next    
  AlignToVector a, d#(0), d#(1), d#(2), 2
  ; Scaling the cylinder on length of an edge
  ScaleEntity a, .1, .5*Sqr(dd#),.1
  End If
 Next
 Next

 For n3=0 To 2
 If KeyDown(n3+8) Then  
  TurnEntity p, ang#*(n3=0), ang#*(n3=1), ang#*(n3=2)
 End If
 Next

 ; Installation of spheres
 For n=0 To 15
 PositionEntity v(n, 4), v(n, 0), v(n, 1), v(n, 2)
 Next

 RenderWorld
 Flip
Until KeyHit(1)
