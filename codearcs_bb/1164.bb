; ID: 1164
; Author: MErren
; Date: 2004-09-22 02:13:33
; Title: Kanonensimulation
; Description: a simple canon

AppTitle "Kanonensimulation VER 3.4"

;Tasten W,A,D,S und SPACE für die lenkung der Kanone
;Tasten 1,2 für die Windrichtung
;Tasten 4,5 für die Winstärke
;Maus zum Bewegen der Kamera 

Graphics3D 1024,768
SetBuffer BackBuffer()

Global KamAP = CreatePivot()
Global KamRP = CreatePivot(KamAP)

EntityRadius KamAP,1.6

Global Kamera = CreateCamera(KamRP)
PositionEntity Kamera,0,7,0
PositionEntity KamAP,10,0,-5

Licht=CreateLight()
RotateEntity Licht,90,0,0

pivot=CreatePivot()

z_cam=CreateCamera( pivot )
CameraViewport z_cam,700,0,200,130
PositionEntity z_cam,0,5,0
RotateEntity z_cam,30,90,0
Global Poscamx#=0

Wind=CreateCone()
PositionEntity wind,0,10,0
ScaleEntity wind,2,8,2
RotateEntity wind,90,0,0
EntityColor wind, 50,50,200
EntityAlpha wind,.5
 
;Kanone=LoadMesh("data/kanone.3ds")
Kanone=CreateCube()
PositionEntity kanone,0,0,0
RotateEntity kanone ,0,0,0
ScaleEntity kanone,.9,.2,.2

Kugel=CreateSphere()
EntityColor kugel,150,100,100
PositionEntity kugel,0,0,0
ScaleEntity kugel,.1,.1,.1
RotateEntity kugel,0,0,0
EntityBlend kugel,.5

grid_tex=CreateTexture( 16,16,8 )
ScaleTexture grid_tex,5,5
SetBuffer TextureBuffer( grid_tex )
Color 0,0,64:Rect 0,0,16,16
Color 0,0,128:Rect 0,0,16,16,False

grid_plane=CreatePlane()
EntityTexture grid_plane,grid_tex
EntityBlend grid_plane,1
EntityAlpha grid_plane,.65
EntityFX grid_plane,1
PositionEntity grid_plane,0,-3,0

mirror=CreateMirror()
PositionEntity mirror,0,-3,0 terra_detail=1000
 
Global posx# = 0
Global posy# = 0
Global posz# = 0
Global opx# = 0
Global opy# = 0
Global opz# = 0
 
windri = 10
windst# = 1
kanwink = 45
     
s#    = 0
While Not KeyDown( 1 )

If KeyDown(2) Then windri = windri + 1 
If KeyDown(3) Then windri = windri - 1 
If windri < 0 Then windri = 360
If windri > 360 Then windri = 0
If KeyDown(5) Then windst# = windst# - .1 
If KeyDown(6) Then windst# = windst# + .1 
If windst# < .1 Then windst# = 0
If windst# > 12 Then windst# = 12

RotateEntity wind,90,0,windri+90

If KeyDown(17) Then kanwink = kanwink + 1 
If KeyDown(31) Then kanwink = kanwink - 1 
If kanwink < 2 Then kanwink = 2
If kanwink > 80 Then kanwink = 80

If KeyDown(30) Then kanrich = kanrich + 1  
If KeyDown(32) Then kanrich = kanrich - 1 
If kanrich < -80 Then kanrich = -80
If kanrich > 80 Then kanrich = 80

If KeyDown(57) Then 

Repeat

s#=s#+.02				; Berechnungsinterval
Ges#=2     		 		; Geschoßgewicht 
ladung#=2				; Pulvermenge
f#=ladung#*130   		; Kraft in Newton bei der Zündung
g#=Ges# * (9.81*(s#^2))	; Gravitation
Fkx#=(Cos(kanwink)*f#)/100 
Fky#=(Sin(kanwink)*f#-g#)/100
fkz#=(Cos(kanwink)*f#)/100 

opx# = posx# : opy# = posy# : opz# = posz#
posx# = posx# + Cos(kanrich)*FKx# - ((Cos(windri)/10)*windst#/5) 
posy# = posy# + FKy#   
posz# = posz# + Sin(kanrich)*FKz# - ((Sin(windri)/10)*windst#/5) 

PositionEntity kugel,posx#,posy#,posz#
PositionEntity z_cam,posx#,posy#,posz#
 
spur (opx#,opy#,opz#)

UpdateScene()
RenderWorld	

Flip

Color 200,50,50
Locate 60,10 : Write "Winkel:" : Print kanwink: Locate 200,10 : Write "Richtung:" : Print kanrich
Locate 60,20 : Write "windrichtung:" : Print windri: Locate 200,20 :Write "Windstärke" : Print windst#
Locate 60,30 : Write "x:" : Print posx#:
Locate 60,40 :Write "y:": Print posy#:
Locate 60,50 :Write "z:": Print posz#

If posy# < -1.1 Then explosion

Until posy# < -1.2 Or KeyHit(1) 
Mesh = 0 
posx# = 0
posy# = 0
posz# = 0
f#    = 2     
s#    = 0
EndIf 

RotateEntity kanone,0,kanrich,kanwink

PositionEntity kugel,posx#,posy#,posz#

UpdateScene()
 
RenderWorld	

Flip

Color 200,50,50 
Locate 60,10 : Write "Winkel:" : Print kanwink: Locate 200,10 : Write "Richtung:" : Print kanrich
Locate 60,20 : Write "windrichtung:" : Print windri: Locate 200,20 :Write "Windstärke" : Print windst#
Locate 60,30 : Write "x:" : Print posx#:
Locate 60,40 :Write "y:": Print posy#:
Locate 60,50 :Write "z:": Print posz#

Wend

End

Function UpdateScene()	;Dieser Part stammt von Rob Hutchinson
    mXs# = MouseXSpeed()
	mYs# = MouseYSpeed()
	TurnEntity KamAP,0,-(mXs#/3),0,True
	TurnEntity Kamera,(mYs#/3),0,0
	If KeyDown(203) Then MoveEntity KamAP,-.2,0,0
	If KeyDown(205) Then MoveEntity KamAP,.2,0,0
	If KeyDown(200) Then MoveEntity KamAP,0,0,.5
	If KeyDown(208) Then MoveEntity KamAP,0,0,-.5
	If KeyDown(16) Then MoveEntity KamAP,0,1,0
	If KeyDown(44) Then MoveEntity KamAP,0,-1,0
	If MouseDown(1) Then MoveEntity KamAP,0,0,2
	If MouseDown(2) Then MoveEntity KamAP,0,0,-2	
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2	
End Function


Function explosion()
ex=CreateSphere()
PositionEntity ex,posx#,posy#,posz# 
radius=100:al#=1
EntityFX ex,.5
For t =1 To radius-1 Step 5  
ScaleEntity ex,t,t,t
EntityColor ex,150+t,100,100
al#=al#-.05
EntityAlpha ex,al# 
RenderWorld	
Flip
Next

Delay 10  
For z= 100 To 0 
EntityColor ex,150-z,z,z
RenderWorld	
Flip
Next

FreeEntity ex

mark=CreateSphere()
PositionEntity mark,posx#,0,posz#
EntityColor Mark,10,200,10
EntityAlpha mark,.8
End Function

Function spur(x1#,y1#,z1#)
	
	box=CreateCube()
	PositionEntity box,x1#,y1#,z1#
	ScaleEntity box,.05,.05,.05
	EntityColor box,10,210,10
	
End Function
