; ID: 1320
; Author: MErren
; Date: 2005-03-10 08:00:09
; Title: Screenshot Util
; Description: Screenshot Util

;Blitz 3D
;How to make a Stringcounter (up to 99) 
;and a nice Steel texture
;and a nice Screenshot tool.
;
;www.erren 3D.de
;
; Press S to save Shot and W for Wire Frame
;
;Ein netter Stringzähler (wandelt Echtzahl in String)
;und ne schöne Stahltextur
;und ein Bildschirminhaltsspeicherwerkzeug :o)
;


Graphics3D 1024,768
SetBuffer BackBuffer()


Global pz=1

Camera = CreateCamera( )
PositionEntity camera,0,4,-6
RotateEntity camera,30,0,0
CameraFogMode camera,1
CameraFogRange camera,0,110
CameraFogColor camera,0,0,0

AmbientLight 191,191,191						
light=CreateLight()
LightColor light,31,31,31
RotateEntity light,45,0,0

grid_tex=CreateTexture( 16,16,2 )
ScaleTexture grid_tex,4,4
SetBuffer TextureBuffer( grid_tex )
Color 50,50,200:Rect 0,0,16,16,False

Boden=CreatePlane()
EntityTexture Boden,grid_tex
EntityColor Boden,30,30,130
EntityAlpha Boden,.6
 
mirror=CreateMirror ()
PositionEntity mirror,0,0,0

auf=512
stahl=CreateTexture(auf,auf,1)
SetBuffer TextureBuffer(stahl)
Color 200,200,200
Rect 0,0,auf,auf
For sta = 0 To auf
co=Int(Rnd(1)*50)+180
	Color co,co,co
	y=(Int(Rnd(1)*auf))
	y2=Rnd(10)-10  
	Line 1,y+1,auf,y+y2+1
	Line 1,y+y2+1,auf,y+1  	
Next
SetBuffer BackBuffer()

 
box=CreateCube()
PositionEntity box,0,2.53,0
RotateEntity box,0,0,90
ScaleEntity box,1.3,1.3,1.3
EntityTexture box,stahl


While Not KeyHit(1)
If KeyDown (31) Then 
SetBuffer FrontBuffer()
gfxGrab=CreateImage(1024,768)
GrabImage gfxGrab,0,0
GN$="PicShot"+pz+".BMP"
SaveImage (gfxGrab,GN$)
FreeImage gfxGrab
pz=pz+1
EndIf

If KeyHit(17) Then wire=Not wire:WireFrame wire
rw=rw+1
tw#=Sin(rw)*30
hw#=Cos(rw)*20
If rw= 360 Then 
rw=0
as=Rnd(360)
EndIf

 
RotateEntity box,0+hw/2,tw,90+hw
;TurnEntity box,.3,tw,tw

Dither True
AntiAlias True
WBuffer True

UpdateWorld

RenderWorld
Flip
Wend

WaitKey()
End
