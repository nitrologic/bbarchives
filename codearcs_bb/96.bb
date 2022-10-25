; ID: 96
; Author: TFT (der Falke)
; Date: 2001-10-10 23:43:54
; Title: 3D Wire Frame Speed Demo
; Description: 3D with Matrix Mul, Not BB3D, 1.place from OptimaCode Code of fame

Print" BlitzBasic V 1.55"
Print
Print GameName$
Print
Print" Code date 23.8.2001 / 30.9.2001"
Print" SerNr: 2001.0001-0"
Print" EMail tft@optima-code.de"
Print" Inter http://www.optima-code.de"
Print

.re
a$=Input$("Bitte Objecteanzahl eingeben 100-100000:")
If a$="" Then Goto re
If Int(a$)<=0 Then Goto re
anzo=Int(a$)

Graphics 640,480,16
AppTitle GameName$
SetBuffer BackBuffer() ;-- Doublebuffer einrichten
;--------------------------- Monitoring
Dim m_(10),m_st$(10)
m_st$(0)="Anzahl Sichtbare Dreiecke :"
m_st$(1)="Anzahl Sichtbare Objecte :"
m_st$(2)="Anzahl Sichtbare Punkte :"
;--------------------------- Speicher für Punkt Koordinaten
Dim x1(anzo,10),y1(anzo,10),z1(anzo,10)
Dim x3(anzo,10),y3(anzo,10),z3(anzo,10)
;--------------------------- Speicher für Flächen/punkt Liste
Dim f1(anzo,12),f2(anzo,12),f3(anzo,12)
;--------------------------- Speicher für neue Rotations weite
Dim x_o#(anzo)
Dim y_o#(anzo)
Dim z_o#(anzo)
;--------------------------- Speicher für object position
Dim p_x1#(anzo)
Dim p_y1#(anzo)
Dim p_z1#(anzo)
Dim p_z3#(anzo)
;--------------------------- Matrix Felder
Dim tm0#(2,2) ; Berechnungs Matrix
Dim tm1#(2,2) ; Kammera Matrix
Dim tme#(2,2) ; Einheits Matrix
Dim tmr#(anzo,2,2) ; Rotations weiten Matrix für Relative bewegung
Dim tmo#(anzo,2,2) ; Object matrix
; einheits matrix
tme#(0,0)=1
tme#(1,1)=1
tme#(2,2)=1
; Matrix übertragen
For j=0To 2
For k=0To 2
tm0#(j,k)=tme#(j,k)
tm1#(j,k)=tme#(j,k)
Next
Next
For i1=0To anzo
For j=0To 2
For k=0To 2
tmr#(i1,j,k)=tme#(j,k)
tmo#(i1,j,k)=tme#(j,k)
Next
Next
Next
;--------------------------- Object daten lesen
For i1=0 To anzo
;--------------------------- Object punkte
Restore dat01
Read anzp
For i=0To anzp
Read x1(i1,i),y1(i1,i),z1(i1,i)
Next
;--------------------------- Object Flächen/punkte lesen
Restore dat02
Read anzf
For i=0To anzf
Read f1(i1,i),f2(i1,i),f3(i1,i)
Next
Next
;--------------------------- Object position
For i=1 To anzo ; 0=pl
p_x1(i)=4000-40*Rnd(200)
p_y1(i)=4000-40*Rnd(200)
p_z1(i)=-12000;-200*Rnd(200)
p_z3(i)=0;-Rnd(100)
Next
For i=1 To anzo
x_o(i)=Rnd(2000)*0.001
y_o(i)=Rnd(300)*0.001
Next
;--------------------------- Grund positionen berechen
For i1=0To anzo
For i=0To anzp
X=x1(i1,i)
Y=y1(i1,i)
Z=z1(i1,i)
;Object Rotation
X_1#=X*tm0#(0,0)+Y*tm0#(1,0)+Z*tm0#(2,0)
Y_1#=X*tm0#(0,1)+Y*tm0#(1,1)+Z*tm0#(2,1)
Z_1#=X*tm0#(0,2)+Y*tm0#(1,2)+Z*tm0#(2,2)
Next
Next
;--------------------------- Main Loop
Repeat
t1=MilliSecs()
t2=MilliSecs()
Cls
;------------------------------- Invertierte Matrix für betrachter (Kammera)
invert_matrix()
;------------------------------- Position des betrachters
pp_x=p_x1(pl);+4000*tmo(pl,2,0)
pp_y=p_y1(pl);+4000*tmo(pl,2,1)
pp_z=p_z1(pl);+4000*tmo(pl,2,2)
;------------------------------- Bewegung des betrachter objectes
steuerung_m(pl) ; mous
;----
;----
;----
;----
t2=MilliSecs()
rotate()
t3=MilliSecs()
Color $99,$99,$99
;----
;----
;----
;--------------------------- Object darstellung
t4=MilliSecs()
zeichne_object()
;---------------------------
Color $FF,$ff,0
Text 10,40,Str$(anzo)+" Objecte berechnen = "+Str$(t3-t2)
Text 10,55,Str$(anzo)+" Objecte Zeichnen = "+Str$(MilliSecs()-t4)
Text 10,70,"Loop Time "+Str$(MilliSecs()-t1)+"/1000 sec
t5=MilliSecs()-t1
Text 10,85,Str$(1000/(MilliSecs()-t1))+" FPS"
;--------------------------- Monitoring ausgabe
For i=0To 3
Text 10,115+i*15,m_st$(i)+Str$(m_(i))
Next
;--------------------------- Page wechsel
Flip
;--------------------------- Beenden wen ECS gedrückt.
Until KeyHit(1)
;--------------------------- Ende
End




; Dieser Teil entspricht der Herrausforderung -------------------------------
Function Rotate()

m_(2)=0
m_(1)=0
anzo1=-1
;-------------------------------
For i1=0To anzo

make_rot_mat(i1)

;--------------------------- Clear Matrix
For l=0To 2
For k=0To 2
tm0#(l,k)=0
Next
Next
;--------------------------- Mul Matrix
For l = 0 To 2
For j = 0 To 2
For k = 0 To 2
tm0#(l,j)=tm0#(l,j)+tmo#(i1,k,j)*tmr#(i1,l,k)
Next
Next
Next
For l = 0 To 2
For j = 0 To 2
tmo#(i1,l,j) = tm0#(l,j)
Next
Next

;--------------------------- Object Position im verhältnis zum betrachter
po_x=p_x1(i1)-pp_x
po_y=p_y1(i1)-pp_y
po_z=p_z1(i1)-pp_z

;--------------------------- Ist das object zu sehen?
rz=po_x*tm1(0,2)+po_y*tm1(1,2)+po_z*tm1(2,2)
If rz<-2000 And rz>-120000 ; Sichtweite for dem Spieler

anzo1=anzo1+1 ; wiefiel objecte sollen dargestellt werden
m_(1)=m_(1)+1 ; monitoring
;--------------------------- Punkt berechnung
For i=1To anzp
X=x1(i1,i)
Y=y1(i1,i)
Z=z1(i1,i)
;Object Rotation
X_1#=X*tm0#(0,0)+Y*tm0#(1,0)+Z*tm0#(2,0)
Y_1#=X*tm0#(0,1)+Y*tm0#(1,1)+Z*tm0#(2,1)
Z_1#=X*tm0#(0,2)+Y*tm0#(1,2)+Z*tm0#(2,2)
;Object position
x_1#=x_1#-po_x
Y_1#=y_1#-po_y
Z_1#=z_1#-po_z
;Kammera drehung
X_t#=X_1#*tm1#(0,0)+Y_1#*tm1#(1,0)+Z_1#*tm1#(2,0)
Y_t#=X_1#*tm1#(0,1)+Y_1#*tm1#(1,1)+Z_1#*tm1#(2,1)
Z_t#=X_1#*tm1#(0,2)+Y_1#*tm1#(1,2)+Z_1#*tm1#(2,2)
;Perspective
RX=X_t#*512
RY=y_t#*512
RZ=Z_t# ; entfernung betrachter /bildschirm
;Ferhindert DIV by 0
If RZ=0 Then RZ=1
;3D Nach 2D Transformation
RX=RX/RZ
RY=RY/RZ
x3(anzo1,i)=hscrx+RX ; zentrieren
y3(anzo1,i)=hscry+RY ; zentrieren
m_(2)=m_(2)+1 ; monitoring
Next
EndIf
Next

End Function

;--------------------------- Funktionen -------------------------------------
Function Make_Rot_Mat(ob)
Local cx#,cy#,cz#,sx#,sy#,sz#
;Drehung des Objectes
cx#=Cos(x_o(ob))
sx#=Sin(x_o(ob))
cy#=Cos(y_o(ob))
sy#=Sin(y_o(ob))
cz#=Cos(z_o(ob))
sz#=Sin(z_o(ob))
;Rotations Matrix
tmr#(ob,0,0)=cy#*cz#
tmr#(ob,0,1)=cy#*sz#
tmr#(ob,0,2)=-sy#
tmr#(ob,1,0)=sx#*sy#*cz#-cx#*sz#
tmr#(ob,1,1)=sx#*sy#*sz#+cx#*cz#
tmr#(ob,1,2)=sx#*cy#
tmr#(ob,2,0)=cx#*sy#*cz#+sx#*sz#
tmr#(ob,2,1)=cx#*sy#*sz#-sx#*cz#
tmr#(ob,2,2)=cx#*cy#
End Function
;-------------------------------
Function Zeichne_object()
Local i2,i1,i,nz
m_(0)=0
If anzo1>-1
For i2=0To anzo1
i1=z3(i2,0)
For i=0To anzf
;--------------------------- Berechnung ob fläche sichtbar
xx1=x3(i2,f1(i1,i))
yy1=y3(i2,f1(i1,i))
xx2=x3(i2,f2(i1,i))
yy2=y3(i2,f2(i1,i))
xx3=x3(i2,f3(i1,i))
yy3=y3(i2,f3(i1,i))
v2X=xx3-xx1
v1Y=yy2-yy1
v2Y=yy3-yy1
v1X=xx2-xx1
nz=v2X*v1Y-v2Y*v1X
If nz < 0 ; nur sichtbare flächen zeichnen
;----------------------- Object Zeichnen
Line xx1,yy1,xx2,yy2
Line xx1,yy1,xx3,yy3
Line xx2,yy2,xx3,yy3
m_(0)=m_(0)+1
EndIf
Next
Next
EndIf
End Function
;-----------------------------
Function invert_matrix()
tm1#(0,0) = tmo#(pl,0,0)
tm1#(0,1) = tmo#(pl,1,0)
tm1#(0,2) = tmo#(pl,2,0)
tm1#(1,0) = tmo#(pl,0,1)
tm1#(1,1) = tmo#(pl,1,1)
tm1#(1,2) = tmo#(pl,2,1)
tm1#(2,0) = tmo#(pl,0,2)
tm1#(2,1) = tmo#(pl,1,2)
tm1#(2,2) = tmo#(pl,2,2)
End Function
;
Function steuerung_m(t5)
x_o(pl)=MouseYSpeed()*0.1
y_o(pl)=MouseXSpeed()*0.1
MoveMouse hscrx,hscry

If MouseDown(1)
z_o(pl)=10*t5*0.001
Else If MouseDown(2)
z_o(pl)=-10*t5*0.001
Else
z_o(pl)=0
EndIf

If KeyDown(74) Then p_z3(pl)=p_z3(pl)+1
If KeyDown(78) Then p_z3(pl)=p_z3(pl)-1

End Function
;--------------------------- Data bereich
; der erste punkt definiert den berechnungs point für sort etc
.dat01
Data 8
Data 0,0,0
Data -500,0,800
Data 500,0,800
Data 1000,0,-300
Data 500,-300,-300
Data -500,-300,-300
Data -1000,0,-300
Data -500,300,-300
Data 500,300,-300
.dat02
Data 11
Data 5,1,2
Data 2,4,5
Data 2,3,4
Data 1,5,6
Data 5,7,6
Data 5,4,7
Data 4,8,7
Data 4,3,8
Data 2,8,3
Data 1,6,7
Data 1,7,2
Data 2,7,8

