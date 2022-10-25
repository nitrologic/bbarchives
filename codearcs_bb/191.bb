; ID: 191
; Author: wedoe
; Date: 2002-01-13 10:46:13
; Title: Light-trail
; Description: Simple light-trail using sprites

AppTitle "Light-trail"
; Einar Wedoe, January 2002

Graphics3D 640,480,16,1
Global timer=CreateTimer(60)

Global dx#,dy#,dz#
Global r1,g1,b1

Dim trail(100)
Dim x#(100)
Dim y#(100)
Dim z#(100)
Dim r(100)
Dim g(100)
Dim b(100)

AmbientLight 80,80,80                            
camera=CreateCamera()                                
CameraViewport camera,0,0,640,480                    
PositionEntity camera,0,0,-40                        
AntiAlias False

Global Light = LoadSprite("light1.png")
SpriteViewMode(Light , 1)
PositionEntity(Light , 0 , -1000 , 0)
EntityBlend(Light,3)
EntityAlpha(Light,1)

For a#=1 To 100 
    trail(a)=CopyEntity(Light)
    x(a)=-1000
Next
;--------------------------------
Repeat
light
UpdateWorld()
RenderWorld()
WaitTimer timer
Flip
Until KeyDown (1)
End
;--------------------------------
Function light()
dx=dx+1
If dx > 360 Then dx=dx-360
dy=dy+1
If dy > 360 Then dy=dy-360
dz=dz-5
If dz < 0 Then dz=dz+360
tmpx#=Sin(dx)*20
tmpy#=Cos(dy)*20
tmpz#=Cos(dz)*10
r1=r1+1
If r1 > 360 Then r1=r1-360
    tmpr#=50*Sin(r1)+150
g1=g1+2
If g1 > 360 Then g1=g1-360
    tmpg#=50*Cos(g1)+150
b1=b1+3
If b1 > 360 Then b1=b1-360
    tmpb#=50*Sin(b1)+150    
For a#=1 To 99
    x(a)=x(a+1)
    y(a)=y(a+1)
    z(a)=z(a+1)
    r(a)=r(a+1)
    g(a)=g(a+1)
    b(a)=b(a+1)
PositionEntity (trail(a),x(a),y(a),z(a))
     EntityColor(trail(a),r(a),g(a),b(a))
     EntityAlpha(trail(a),a/100) 
Next
r(100)=tmpr
g(100)=tmpg
b(100)=tmpb
x(100)=tmpx
y(100)=tmpy
z(100)=tmpz
End Function
;---------------------------------
