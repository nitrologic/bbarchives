; ID: 613
; Author: jfk EO-11110
; Date: 2003-03-04 20:25:47
; Title: VScroll
; Description: Virtual Scrollbar Gadget

; Example: how to use VScroll Gadgets to position/rotate a Mesh
Graphics3D 640,480,16,2
SetBuffer BackBuffer()

font=LoadFont("Tahoma",13)
SetFont font

camera=CreateCamera()
MoveEntity camera,0,0,-10
li=CreateLight()
RotateEntity li,30,30,30

cube=CreateCube()

Dim par#(6),par_bk#(6),pr$(6)
pr$(0)="X"
pr$(1)="Y"
pr$(2)="Z"
pr$(3)="Pitch"
pr$(4)="Yaw"
pr$(5)="Roll"

par(3)=1:par(4)=1:par(5)=1

Global ex,old_ex,ey,old_ey,drag,drag_this,dragy,win_drag,win_xo,win_yo
Global tooltip1,tooltip2

ex=110:ey=230

While KeyDown(1)=0
 check_editor()
 If drag=1 Then
  PositionEntity cube,par(0),par(1),par(2)
  RotateEntity cube,par(3),par(4),par(5)
 EndIf
 RenderWorld()
 draw_editor()
 Flip
Wend
End

Function draw_editor()
 Color 127,127,127
 Rect ex,ey,200,16+(16*6),1
 Color 0,0,0
 For i=3 To 12 Step 2
  Line ex+4,ey+i,ex+196,ey+i
 Next
 tooltip1=0
 For i=0 To 5
  If i=drag_this
   Color 0,0,0
   tooltip1=1
  Else
   Color 255,255,255
  EndIf
  Text ex+100,ey+16+(i*16),par(i),1,0
  Text ex+4,ey+16+(i*16),pr$(i),0,0
  Color 200,200,200
  Rect ex+2,ey+15+(i*16),196,15,0
  Color 255,255,255
  If tooltip1=1 Then Text 4,GraphicsHeight()-16,"Press LMB and drag up or down..."
  If tooltip2=1 Then Text 4,GraphicsHeight()-16,"Press LMB and drag Editor Window..."
 Next
End Function

Function check_editor()
 mx=MouseX()
 my=MouseY()
 ; window mover
 tooltip2=0
 If drag=0 And win_drag=0
  If mx>ex And mx<ex+200 And my>ey And my<ey+15
   tooltip2=1
   If MouseDown(1)=1
    win_drag=1
    old_ex=ex
    old_ey=ey
    win_xo=mx
    win_yo=my
   EndIf
  EndIf
 EndIf
 If win_drag
  If MouseDown(1)
   ex=old_ex+(mx-win_xo)
   ey=old_ey+(my-win_yo)
  Else
   win_drag=0
  EndIf
 EndIf
 ; parameter edit
 If drag=0
  If my<ey+16 Or my>ey+16+(6*16) Or mx<ex Or mx>ex+200
   drag_this=-1 ; no highlighting
  EndIf
  If mx>=ex And mx <=ex+200
   For i=0 To 5
    If (my>ey+16+(i*16)) And (my<ey+32+(i*16))
     drag_this=i ; highlighting
     If MouseDown(1)=1
      drag=1
      dragy=my
      For i2=0 To 6
       par_bk(i2)=par(i2)
      Next
     EndIf
    EndIf
   Next
  EndIf
 Else
  If MouseDown(1)=1
   If drag_this>=3 And drag_this<=5
    par(drag_this)=par_bk(drag_this)+(dragy-MouseY()) ; rotation: 1.0-steps
   EndIf
   If drag_this>=0 And drag_this<=2
    par(drag_this)=par_bk(drag_this)+(.1*(dragy-MouseY())) ; position: 0.1-steps
   EndIf
   If drag_this>=3 And drag_this<=5
    If par(drag_this)> 360 Then par(drag_this)=par(drag_this)-360 ; truncate angle parameters
    If par(drag_this)<   0 Then par(drag_this)=par(drag_this)+360
   EndIf
  Else
   drag=0
   ;here you might copy all the par() values to a Type out of a large collection of loaded object
  EndIf
 EndIf
End Function
