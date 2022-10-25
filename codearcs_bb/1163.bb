; ID: 1163
; Author: TomToad
; Date: 2004-09-21 18:22:11
; Title: vertex normal effect
; Description: A nice little effect with vertex normals

Graphics3D 800,600,32,0
SetBuffer BackBuffer()
Const size = 128

grid = CreateMesh()
surface = CreateSurface(grid)


For y = -size/2 To (size/2 - 1)
 For x = -size/2 To (size/2 - 1)
  v = AddVertex(surface,x,0,y)
  DebugLog v
 Next
Next


For y = 0 To (size - 2)
 For x = 0 To (size - 1)
  If x <> 0 
   v3 = (x - 1) + y * size
   v2 = x + y * size
   v1 = x + (y + 1) * size
   AddTriangle(surface,v1,v2,v3)
  End If
  If x <> (size - 1)
   v3 = x + y * size
   v2 = x + 1 + (y + 1) * size
   v1 = x + (y + 1) * size
   AddTriangle(surface,v1,v2,v3)
  End If
 Next
Next

PositionEntity grid,0,0,0
EntityColor grid,127,127,255
camera = CreateCamera()

PositionEntity camera,0,5,-5

light = CreateLight()
RotateEntity light,45,0,0

angle = 0
mode = 0
timer = CreateTimer(30)

While Not KeyDown(1)
WaitTimer(timer)

For x = 0 To size - 1
 For y = 0 To size - 1
  Select mode
  Case 0
    VertexNormal(surface,x + y * size,Cos((angle + x*y)Mod 180),Sin((angle + x*y)Mod 180),0)
  Case 1
    VertexNormal(surface,x + y * size,Sin((angle + x*y)Mod 180),Cos((angle + x*y)Mod 180),0)
  Case 2
    VertexNormal(surface,x + y * size,0,Sin((angle + x*y)Mod 180),Cos((angle + x*y)Mod 180))
  Case 3
    VertexNormal(surface,x + y * size,0,Cos((angle + x*y)Mod 180),Sin((angle + x*y)Mod 180))
  Case 4
    VertexNormal(surface,x + y * size,Sin((angle + x*y)Mod 180),0,Cos((angle + x*y)Mod 180))

  End Select
 Next
Next


If KeyDown(200) Then MoveEntity camera,0,0,.1
If KeyDown(208) Then MoveEntity camera,0,0,-.1
If KeyDown(203) Then TurnEntity camera,0,.8,0
If KeyDown(205) Then TurnEntity camera,0,-.8,0
If KeyHit(2) Then mode = 0
If KeyHit(3) Then mode = 1
If KeyHit(4) Then mode = 2
If KeyHit(5) Then mode = 3
If KeyHit(6) Then mode = 4

RenderWorld
Flip False

angle = angle + 1
If angle = 360 Then angle = 0

Wend
