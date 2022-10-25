; ID: 1631
; Author: Mr. Bean
; Date: 2006-03-03 14:39:46
; Title: Watch
; Description: a simple watch-animation

Graphics 640,480
I =360/2
I1=360/2
I2=360/2
v=Input("Speed (1=Normal)  ")

If v<1 v=1
If v>101 v=100
SEC=1000 /v
MIN=SEC*60
STD=MIN*60
tim =MilliSecs()
tim1=tim
tim2=tim

C=0
C1=0
C2=0

Repeat
Color 180,180,180
If C Or C1 Or C2 Cls
Oval 320-100,240-100,200,200,0
Oval 320-130,240-130,260,260,0 
Text 320,240-115,"XII",1,1
Text 320+115,240,"III",1,1
Text 320,240+115,"VI",1,1
Text 320-115,240,"IX",1,1

LX= Sin(150)*109+320
LY= Cos(150)*109+240
LX1=Sin(150)*121+320
LY1=Cos(150)*121+240
Line LX,LY,LX1,LY1

LX= Sin(120)*109+320
LY= Cos(120)*109+240
LX1=Sin(120)*121+320
LY1=Cos(120)*121+240
Line LX,LY,LX1,LY1

LX= Sin(60)*109+320
LY= Cos(60)*109+240
LX1=Sin(60)*121+320
LY1=Cos(60)*121+240
Line LX,LY,LX1,LY1

LX= Sin(30)*109+320
LY= Cos(30)*109+240
LX1=Sin(30)*121+320
LY1=Cos(30)*121+240
Line LX,LY,LX1,LY1

LX= Sin(210)*109+320
LY= Cos(210)*109+240
LX1=Sin(210)*121+320
LY1=Cos(210)*121+240
Line LX,LY,LX1,LY1

LX= Sin(240)*109+320
LY= Cos(240)*109+240
LX1=Sin(240)*121+320
LY1=Cos(240)*121+240
Line LX,LY,LX1,LY1

LX= Sin(300)*109+320
LY= Cos(300)*109+240
LX1=Sin(300)*121+320
LY1=Cos(300)*121+240
Line LX,LY,LX1,LY1

LX= Sin(330)*109+320
LY= Cos(330)*109+240
LX1=Sin(330)*121+320
LY1=Cos(330)*121+240
Line LX,LY,LX1,LY1


If MilliSecs()-tim>SEC Then
tim=MilliSecs()
I=I-6
If I=0 I=360
C=1
Else 
C=0
EndIf

If MilliSecs()-tim1>MIN Then
tim1=MilliSecs()
I1=I1-6
If I1=0 I1=360
C1=1
Else
C1=0
EndIf

If MilliSecs()-tim2>STD Then
tim2=MilliSecs()
I2=I2-6
If I2=0 I2=360
C2=1
Else
C2=0
EndIf





Color 100,100,100
X=Sin(I)*85
Y=Cos(I)*85   
X=X+320      
Y=Y+240
Line X,Y,320,240

Color 150,150,150
X1=Sin(I1)*80
Y1=Cos(I1)*80   
X1=X1+320      
Y1=Y1+240
Line X1,Y1,320,240

Color 255,255,255
X2=Sin(I2)*65
Y2=Cos(I2)*65   
X2=X2+320      
Y2=Y2+240
Line X2,Y2,320,240




Until KeyHit(1)

WaitKey
End
