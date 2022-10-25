; ID: 1101
; Author: AL90
; Date: 2004-07-02 14:55:00
; Title: Three-Scrollings
; Description: Three Scrolltexts in a Screen

;
;   Dreifach Scrolling by AL90
;

Graphics 800,600
SetFont LoadFont("arial",20,0,1)
Color 0,180,255:Rect 0,550,800,34,1
Color 0,0,255:Rect 0,551,800,32,1
Color 0,180,255:Rect 0,500,800,34,1
Color 0,0,255:Rect 0,501,800,32,1
Color 0,180,255:Rect 0,450,800,34,1
Color 0,0,255:Rect 0,451,800,32,1

lauf1$="Hallo ein Scrolltext!!! - Rechte Maustaste = Pause und linke Maustaste = Ende!                       "
lauf2$="Dies ist die zweite Scroll-Linie !!!         "
lauf3$="Hier ist die Erste Scroll-Zeile des Intros !!!               "
img1=CreateImage(800,20)
img2=CreateImage(800,20)
img3=CreateImage(800,20)
Delay 2000

Repeat
  s1=s1+1:s2=s2+1
  v=v+1:If v=2 Then v=0:VWait
  If i1=size1
    char1=char1+1
    If char1=Len(lauf1$) Then char1=1
    a1$=Mid$(lauf1$,char1,1)
    size1=StringWidth(a1$)
    i1=0
  EndIf
  i1=i1+1
  Color 0,0,255
  Rect 799,560,1,24
  Color 255,255,255
  Text 800-i1,558,a1$
  GrabImage img1,1,560
  DrawImage img1,0,560

  If s1=2
    s1=0
    If i2=size2
      char2=char2+1
      If char2=Len(lauf2$) Then char2=1
      a2$=Mid$(lauf2$,char2,1)
      size2=StringWidth(a2$)
      i2=0
    EndIf
    i2=i2+1
    Color 0,0,255
    Rect 799,510,1,24
    Color 255,255,255
    Text 800-i2,508,a2$
    GrabImage img2,1,510
    DrawImage img2,0,510
  EndIf

  If s2=4
    s2=0
    If i3=size3
      char3=char3+1
      If char3=Len(lauf3$) Then char3=1
      a3$=Mid$(lauf3$,char3,1)
      size3=StringWidth(a3$)
      i3=0
    EndIf
    i3=i3+1
    Color 0,0,255
    Rect 799,460,1,24
    Color 255,255,255
    Text 800-i3,458,a3$
    GrabImage img3,1,460
    DrawImage img3,0,460
  EndIf

  Repeat:Until MouseDown(2)=0

Until MouseDown(1)=1

FreeImage img1
FreeImage img2
FreeImage img3
