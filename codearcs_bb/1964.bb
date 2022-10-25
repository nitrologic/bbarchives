; ID: 1964
; Author: Matt Merkulov
; Date: 2007-03-15 11:00:57
; Title: 3D chessboard
; Description: Rotational solids editor using example

;3D chessboard - rotational solids editor using example by Matt Merkulov

Const xres=800, yres=600, stp#=.001, sen#=4, maxr#=30
Const txp=8, txres=1 Shl txp, txcp=txp-6, txcell=1 Shl txcp-1

Dim fg(5), a#(1), b#(1), c#(1), d#(1), oc#(1)

Graphics3D xres, yres, 32,2
AntiAlias True
; WireFrame True

; Pivot for convenient management of the chamber
p=CreatePivot()
cam=CreateCamera(p)
PositionEntity cam, 0,3.5,-7
RotateEntity cam, 30,0,0

; This line is necessary, that the background was not erased before rendering of a 3D-stage
CameraClsMode cam, 0,1

l=CreateLight()
RotateEntity l, 45,0,0

; Creation of patterns of models
For n1=0 To 5
 Read q, x2, y2, dx21, dy21, dx22, dy22
 rbeg=0
 fg(n1) =CreateMesh()
 ; The pattern should be invisible
 HideEntity fg(n1)
 s=CreateSurface(fg(n1))
 For n2=2 To q
 x1=x2:y1=y2:dx11=dx21:dy11=dy21:dx12=dx22:dy12=dy22
 Read x2, y2, dx21, dy21, dx22, dy22

 r#=.05*Sqr((x1-x2)*(x1-x2) +(y1-y2)*(y1-y2))
 For n3=0 To 1
  If n3 Then
  ux1=y1
  ux2=y2
  c#(n3) =r#*dy12
  dy2#=r#*dy21
  Else
  ux1=x1
  ux2=x2
  c#(n3) =r#*dx12
  dy2#=r#*dx21
  End If
  d#(n3) =ux1
  b#(n3) =3.0*ux2-dy2#-2.0*c#(n3)-3.0*d#(n3)
  a#(n3) =(dy2#-2*b#(n3)-c#(n3))/3.0
 Next

 t#=0
 tt#=0
 oc(0) =d#(0)
 oc(1) =d#(1)
 If n2=2 Then r1=vertexes(s)
 Repeat
  x#=oc(0)
  y#=oc(1)
  la#=-3.0*a#(1)*tt#-2.0*b#(1)*t#-c#(1)
  lb#=3.0*a#(0)*tt#+2.0*b#(0)*t#+c#(0)
  If la#=0 And lb#=0 Then
  la#=y1-y2
  lb#=x2-x1
  End If
  lc#=-la#*x#-lb#*y#
  sen2#=Sqr(la#*la#+lb#*lb#)*sen#
  Repeat
  t#=t#+stp
  If t#>1 Then t#=1
  tt#=t#*t#
  For n3=0 To 1
   oc(n3) =a#(n3)*tt#*t#+b#(n3)*tt#+c#(n3)*t#+d#(n3)
  Next
  If Sqr((x#-oc(0))*(x#-oc(0)) +(y#-oc(1))*(y#-oc(1)))>maxr Then Exit
  Until t#=1 Or Abs(la#*oc(0) +lb#*oc(1) +lc#)>=sen2#
  r2=vertexes(s)

  rbeg2=rbeg+r1
  r10=0
  r20=0
  Repeat
  If(r10+1)*r2 <(r20+1)*r1 Then
   r10=r10+1
   r11=r12
   r12=r10 Mod r1
   AddTriangle s, rbeg2+r22, rbeg+r11, rbeg+r12
  Else
   r20=r20+1
   r21=r22
   r22=r20 Mod r2
   AddTriangle s, rbeg+r12, rbeg2+r22, rbeg2+r21
  End If
  Until r12=0 And r22=0

  rbeg=rbeg2
  r1=r2
  If t#=1 Then Exit
 Forever

 Next
 UpdateNormals fg(n1)
Next

; Loading and installation of a head of a horse
m=LoadMesh("knight.3ds")
ScaleMesh m, .5,1, .5
PositionMesh m, 0,1,-.05
UpdateNormals m
AddMesh m, fg(3)
FreeEntity m

; Creation and arrangement of figures
For x=0 To 7
 For y=0 To 7
 e =-1
 ; Definition of type of a figure
 If y=0 Or y=7 Then e=Mid $("43210234", x+1,1)
 If y=1 Or y=6 Then e=5
 If e>=0 Then
  ; Creation of a copy of a pattern
  e=CopyEntity(fg(e))
  PositionEntity e, x-3.5,0, y-3.5
  ; Definition of color of a figure
  col=128*(y<4) +48
  ; Rotation of a black horse on 180 degrees(as it(he) is asymmetrical)
  If y <4 Then RotateEntity e, 0,180,0
  EntityColor e, col, col, col
  EntityShininess e, 1
 End If
 Next
Next

; Creation of a background(a gradient from blue to dark blue)
bg=CreateImage(xres, yres)
SetBuffer ImageBuffer(bg)
For y=0 To yres
 Color 0,255-255*y/yres, 255
 Line 0, y, xres, y
Next

; Otrisovka structures of a board
tex=CreateTexture(txres, txres)
SetBuffer TextureBuffer(tex)
For x=0 To 63
 ux=x Shl txcp
 For y=0 To 63
 uy=y Shl txcp
 col=1
 ; Definition of color of section
 If x>=4 And x <60 And y>=4 And y <60 Then
  If Floor((x-4)/7) +Floor((y-4)/7) And 1 Then col=0
 ElseIf x>=3 And x <61 And y>=3 And y <61 Then
  col=0
 End If
 
 ; A shading of section with addition of color noise
 For xx=ux To ux+txcell
  For yy=uy To uy+txcell
  Color Rand(-8,8) +192*col+16, Rand(-8,8) +128*col+16, Rand(-8,8) +16
  Plot xx, yy
  Next
 Next  
 Next
Next

; Creation of a board
brd=CreateCube()
s=GetSurface(brd, 1)
; Updating textural coordinates of a board
For n=0 To 23
 If VertexY(s, n) <0 Then VertexTexCoords s, n, VertexU(s, n), 3.0/64
Next
ScaleEntity brd, 32.0/7, .25,32.0/7
PositionEntity brd, 0,-.25,0
EntityTexture brd, tex

SetBuffer BackBuffer()
Repeat
 ; Turn of a board
 TurnEntity p, 0, .4,0
 ; Change of a direction of light(so that it(he) did not shine upwards)
 TurnEntity l, .2, .3, .3
 ; A press(seal) of a background
 DrawBlock bg, 0,0
 RenderWorld
 Text 0,0, " Triangles rendered: " +TrisRendered()
 Flip
Until KeyHit(1)

; Data for plotting:

; 0-Король
Data 24
Data 400,0,0,0,0,0
Data 391,0,0,0,0,0
Data 391,26,0,0,0,0
Data 377,26,0,0,0,0
Data 377,36,0,0,0,0
Data 391,36,0,0,0,0
Data 391,62,0,0,0,0
Data 352,79,-5,31,0,1
Data 331,74,0,0,0,0
Data 355,162,0,0,-53,0
Data 355,177,53,0,0,0
Data 355,190,0,0,-53,0
Data 355,205,53,0,0,0
Data 326,205,0,0,-69,0
Data 326,219,69,0,0,0
Data 373,219,0,0,0,0
Data 341,462,-10,28,0,0
Data 351,473,0,0,-33,14
Data 295,573,0,28,0,0
Data 305,573,0,0,0,0
Data 305,584,0,0,0,0
Data 295,584,0,0,0,0
Data 295,599,0,0,0,0
Data 400,599,0,0,0,0

; 1-Ферзь
Data 20
Data 400,45,0,0,0,0
Data 380,53,0,0,0,0
Data 391,62,0,0,0,0
Data 352,79,-5,31,0,1
Data 331,74,0,0,30,39
Data 355,162,0,0,-53,0
Data 355,177,53,0,0,0
Data 355,190,0,0,-53,0
Data 355,205,53,0,0,0
Data 326,205,0,0,-69,0
Data 326,219,69,0,0,0
Data 373,219,0,0,0,0
Data 341,462,-10,28,0,0
Data 351,473,0,0,-33,14
Data 295,573,0,28,0,0
Data 305,573,0,0,0,0
Data 305,584,0,0,0,0
Data 295,584,0,0,0,0
Data 295,599,0,0,0,0
Data 400,599,0,0,0,0

; 2-Слон
Data 18
Data 400,84,0,0,-46,1
Data 392,107,24,8,6,13
Data 369,162,-29,33,-29,33
Data 366,253,0,0,-53,0
Data 366,266,53,0,0,0
Data 366,292,0,0,-53,0
Data 366,304,53,0,0,0
Data 346,304,0,0,-69,0
Data 346,317,69,0,0,0
Data 380,317,0,0,0,0
Data 352,497,-10,28,0,0
Data 359,505,0,0,-19,15
Data 309,577,-5,20,0,0
Data 321,577,0,0,0,0
Data 321,586,0,0,0,0
Data 309,586,0,0,0,0
Data 309,599,0,0,0,0
Data 400,599,0,0,0,0

; 3-Конь
Data 9
Data 400,475,0,0,0,0
Data 333,475,0,0,0,0
Data 345,493,0,0,0,0
Data 297,578,-2,20,0,0
Data 310,578,0,0,0,0
Data 310,586,0,0,0,0
Data 297,586,0,0,0,0
Data 297,599,0,0,0,0
Data 400,599,0,0,0,0

; 4-Ладья
Data 20
Data 400,181,0,0,0,0
Data 332,181,0,0,0,0
Data 332,161,0,0,0,0
Data 316,161,0,0,0,0
Data 316,193,0,0,0,0
Data 332,193,0,0,0,0
Data 332,215,0,0,0,0
Data 344,215,0,0,0,0
Data 344,238,0,0,0,0
Data 359,238,0,0,0,0
Data 359,268,0,0,0,0
Data 346,454,0,0,0,0
Data 333,460,0,0,0,0
Data 342,473,0,0,0,0
Data 309,577,-2,20,0,0
Data 321,577,0,0,0,0
Data 321,586,0,0,0,0
Data 309,586,0,0,0,0
Data 309,599,0,0,0,0
Data 400,599,0,0,0,0

; 5-Пешка
Data 13
Data 400,209,0,0,-37,0
Data 375,283,31,11,0,0
Data 339,334,0,0,0,0
Data 382,334,0,0,-1,46
Data 355,501,0,0,0,0
Data 339,498,0,0,0,0
Data 346,512,0,0,0,0
Data 309,577,-2,20,0,0
Data 321,577,0,0,0,0
Data 321,586,0,0,0,0
Data 309,586,0,0,0,0
Data 309,599,0,0,0,0
Data 400,599,0,0,0,0

Function vertexes(s)
vx#=1.0*(400-oc(0))/300
vy#=1.0*(600-oc(1))/300
r1=Abs(400-oc(0))

If sen>=r1*2 Then
 r1=1
 AddVertex s, 0, vy#, 0
Else
 r1=Ceil(360.0/ACos(1.0-sen#/r1))
 ang#=0
 dang#=360.0/r1
 Repeat
 AddVertex s, Cos(ang#)*vx#, vy#, Sin(ang#)*vx#
 ang#=ang#+dang#
 If ang#>359.9 Then Exit
 Forever
End If
Return r1
End Function

Function max(v1, v2)
If v1>v2 Then Return v1 Else Return v2
End Function
