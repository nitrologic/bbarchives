; ID: 1963
; Author: Matt Merkulov
; Date: 2007-03-15 10:59:26
; Title: Rotational solids editor
; Description: Rotational solids editor with curves

;Rotational solids editor with curves by Matt Merkulov

;Controls:
; Right mouse button - set new point
; Left mouse button - select point
; 1-3 - additional points for curves
; Enter - render mesh
; F2 - save
; F3 - load

Type dot
 Field x,y,dx1,dy1,dx2,dy2
End Type

Const xres=800, yres=600, stp#=.01, sen#=10, maxr#=50
Const xres2=xres Shr 1, yres2=yres Shr 1
Const tqx#=50,tqy#=5,mindot=5

Global seldot.dot, sel, mx, my, txp#
Dim a#(1),b#(1),c#(1),d#(1),oc#(1)

Graphics3D xres,yres,32
;Слаживание линий
AntiAlias True
;WireFrame True


PositionEntity CreateCamera(),-2.1,0,-4
RotateEntity CreateLight(),0,45,45
;Создание модели
m=CreateMesh()
s=CreateSurface(m)
;Создание блика на модели
EntityShininess m,1
TurnEntity m,90,-90,0

tex=CreateTexture(16,16)
SetBuffer TextureBuffer(tex)
Rect 0,0,7,7
Rect 8,8,15,15
SetBuffer FrontBuffer()
EntityTexture m,tex
;EntityAlpha m,.5

dt.dot=New dot
dt\x=.5*xres
dt\y=.15*yres
dt.dot=New dot
dt\x=.5*xres
dt\y=.85*yres

SetBuffer BackBuffer()
Repeat

 ;Вращаем модель
 ;TurnEntity m,.6,.4,0
 If KeyDown(203) TurnEntity m,0,0,-3
 If KeyDown(205) TurnEntity m,0,0,+3
 If KeyDown(200) TurnEntity m,-3,0,0
 If KeyDown(208) TurnEntity m,+3,0,0
 If KeyDown(30) TranslateEntity m,0,0,-.2
 If KeyDown(44) TranslateEntity m,0,0,+.2

	
 RenderWorld
 Text 0,0,"Triangles rendered:"+TrisRendered()
 
 ;Рисуем ось
 Color 128,128,128
 Line xres2,0,xres2,yres
 Color 255,255,255

 TurnEntity m,0,0.03 * (MilliSecs() - tim),0
 tim = MilliSecs()
 redraw
 Flip 

 mx=MouseX()
 my=MouseY()
 mb=MouseDown(1)+2*MouseDown(2)

 Select mb+sel*10
  Case 11
   sel=3
  Case 31
   sel=0
   redraw
   If sel=1 Then sel=3
  Case 12
   seldot\x=min(mx,xres2)
   seldot\y=my
  Case 22
   dt.dot=New dot
   dt\x=mx
   dt\y=my
   Insert dt After seldot
   seldot=dt
   sel=0
 End Select

 If sel=3 Then
  If KeyDown(3) Then
   seldot\dx1=seldot\x-mx
   seldot\dy1=seldot\y-my
  End If
  If KeyDown(2) Or KeyDown(4) Then
   seldot\dx2=mx-seldot\x
   seldot\dy2=my-seldot\y
   If KeyDown(4) Then
    seldot\dx1=mx-seldot\x
    seldot\dy1=my-seldot\y
   End If
  End If
 End If

 If sel=1 Or sel=3 Then
  If KeyDown(211) And After First dot<>Last dot Then
   Delete seldot
   sel=0
  End If
  If KeyDown(11) Then
   seldot\dx1=0
   seldot\dy1=0
   seldot\dx2=0
   seldot\dy2=0
  End If
 End If

 ;Формирование модели ("ENTER")
 If KeyHit(28) Then
  ;Очистка поверхности
  ClearSurface s
  rbeg=0
  For dt1.dot=Each dot
   dt2.dot=After dt1
   If dt2=Null Then Exit
   kcalc dt1,dt2
   t#=0
   tt#=0
   oc(0)=d#(0)
   oc(1)=d#(1)
   ;Формирование начального кольца вершин
   If dt1=First dot Then r1=vertexes(s)
   Repeat
    x#=oc(0)
    y#=oc(1)
    ;Вычисление коэффициентов касательной - производных многочлена
    la#=-3.0*a#(1)*tt#-2.0*b#(1)*t#-c#(1)
    lb#=3.0*a#(0)*tt#+2.0*b#(0)*t#+c#(0)
    ;Если A и B равны нулю - определяем грубым способом
    If la#=0 And lb#=0 Then
     la#=dt1\y-dt2\y
     lb#=dt2\x-dt1\x
    End If
    lc#=-la#*x#-lb#*y#
    ;Предварительные вычисления для условия выхода
    sen2#=Sqr(la#*la#+lb#*lb#)*sen#
    Repeat 
     t#=t#+stp
     If t#>1 Then t#=1
     tt#=t#*t#
     For nn=0 To 1
      oc(nn)=a#(nn)*tt#*t#+b#(nn)*tt#+c#(nn)*t#+d#(nn)
     Next
     ;Здесь определяется, превысила ли длина отрезка максимальную (тогда выходим)
	 l#=Sqr((x#-oc(0))*(x#-oc(0))+(y#-oc(1))*(y#-oc(1)))
     If l>maxr Then Exit
     ;Проверка на превышение расстояния до касательной в исходной точке
    Until t#=1 Or Abs(la#*oc(0)+lb#*oc(1)+lc#)>=sen2#
    ;Добавление кольца вершин для конечной точки отрезка
	txp#=txp#+l#
    r2=vertexes(s)

    ;Процедура создания усовершенствованного усеченного конуса
    rbeg2=rbeg+r1+1
    r10=0
    r20=0
    Repeat
     ;Выбор ближайшей вершины и задание соответствующего треугольника
     If (r10+1)*r2<(r20+1)*r1 Then
      r10=r10+1
      r11=r12
      r12=r10
	  ;If r11=0 Then
	  AddTriangle s,rbeg2+r22,rbeg+r11,rbeg+r12
     Else
      r20=r20+1
      r21=r22
      r22=r20
	  ;If r21=0 Then
	  AddTriangle s,rbeg+r12,rbeg2+r22,rbeg2+r21
     End If
    Until r12=r1 And r22=r2
    r12=0
	r22=0

    rbeg=rbeg2
    r1=r2
    If t#=1 Then Exit
   Forever

  Next
  UpdateNormals m
 End If

 If KeyHit(60) Then
  f=WriteFile("data.bb")
  For dt.dot=Each dot
   WriteLine f,"Data "+dt\x+","+dt\y+","+dt\dx1+","+dt\dy1+","+dt\dx2+","+dt\dy2   
  Next
  CloseFile f
 End If 

 If KeyHit(61) Then
  Delete Each dot
  f=ReadFile("data.bb")
  While Not Eof(f)
   dt.dot=New dot
   dat$=","+Mid$(ReadLine$(f),6)
   For n=1 To 6
    dat$=Mid$(dat$,Instr(dat$,",")+1)
    Select n
     Case 1:dt\x=dat$
     Case 2:dt\y=dat$
     Case 3:dt\dx1=dat$
     Case 4:dt\dy1=dat$
     Case 5:dt\dx2=dat$
     Case 6:dt\dy2=dat$
    End Select
   Next
  Wend
  CloseFile f
 End If 
 
Until KeyHit(1)

Function redraw()
If sel<3 Then sel=0

Color 255,255,255
If sel=0 Then
 For dt.dot=Each dot
  If Abs(mx-dt\x)<=3 And Abs(my-dt\y)<=3 Then seldot=dt: sel=1
 Next
End If
For dt.dot=Each dot
 Oval dt\x-1,dt\y-1,3,3
 drawcurve dt
Next
If sel Then Oval seldot\x-3,seldot\y-3,7,7
End Function

Function drawcurve(dt1.dot)
dt2.dot=After dt1
If dt2=Null Then Return

kcalc dt1,dt2

For t#=0 To 1 Step stp
 tt#=t#*t#
 For nn=0 To 1
  oc(nn)=a#(nn)*tt#*t#+b#(nn)*tt#+c#(nn)*t#+d#(nn)
 Next

 If t#>0 Then
  If sel=4 Then
   For xx=-1 To 1
    For yy=-1 To 1
     Line oc(0)+xx,oc(1)+yy,x+xx,y+yy
    Next
   Next
  Else
   Line oc(0),oc(1),x,y
  End If
 End If

 If sel=0 Then
  If mx>=min(x,oc(0))-3 And mx<=max(x,oc(0))+3 Then
   If my>=min(y,oc(1))-3 And my<=max(y,oc(1))+3 Then
    aa#=y-oc(1)
    bb#=oc(0)-x
    If Abs(aa#*(mx-x)+bb#*(my-y))<=3.0*Sqr(aa#*aa#+bb#*bb#) Then
     seldot=dt1
     sel=4
     t#=-stp
    End If
   End If
  End If
 End If

 x=oc(0)
 y=oc(1)

Next
If sel=4 Then sel=2

Color 0,255,255
If dt1\dx2<>0 Or dt1\dy2<>0 Then
 Line dt1\x,dt1\y,dt1\x+dt1\dx2,dt1\y+dt1\dy2
 Oval dt1\x+dt1\dx2-1,dt1\y+dt1\dy2-1,3,3
End If
If dt2\dx1<>0 Or dt2\dy1<>0 Then
 Line dt2\x,dt2\y,dt2\x-dt2\dx1,dt2\y-dt2\dy1
 Oval dt2\x-dt2\dx1-1,dt2\y-dt2\dy1-1,3,3
End If
Color 255,255,255

End Function

Function min(v1,v2)
If v1<v2 Then Return v1 Else Return v2
End Function

Function max(v1,v2)
If v1>v2 Then Return v1 Else Return v2
End Function

Function kcalc(dt1.dot,dt2.dot)
r#=.05*Sqr((dt1\x-dt2\x)*(dt1\x-dt2\x)+(dt1\y-dt2\y)*(dt1\y-dt2\y))
For nn=0 To 1
 If nn Then
  x1=dt1\y
  x2=dt2\y
  c#(nn)=r#*dt1\dy2
  dy2#=r#*dt2\dy1
 Else
  x1=dt1\x
  x2=dt2\x
  c#(nn)=r#*dt1\dx2
  dy2#=r#*dt2\dx1
 End If
 d#(nn)=x1
 b#(nn)=3.0*x2-dy2#-2.0*c#(nn)-3.0*d#(nn)
 a#(nn)=(dy2#-2*b#(nn)-c#(nn))/3.0
Next
End Function

Function vertexes(s)
vx#=1.0*(xres2-oc(0))/yres2
vy#=1.0*(yres2-oc(1))/yres2
r=Abs(xres2-oc(0))
If sen>=r*2 Then
 r=1
Else
 r=Ceil(360.0/ACos(1.0-sen#/r))
End If

If r<mindot Then r=mindot
 
 ang#=0
 dang#=360.0/r
 Repeat
  AddVertex s,Cos(ang#)*vx#,vy#,Sin(ang#)*vx#,txp#/tqx#,tqy#*ang#/360.0
  If ang#>359.9 Then Exit
  ang#=ang#+dang#
 Forever

Return r
End Function
