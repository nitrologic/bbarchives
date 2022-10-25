; ID: 259
; Author: bradford6
; Date: 2002-03-03 16:42:44
; Title: arrows on a terrain
; Description: texturing a terrain in real time

; scorch marks on a terrain Example
; ----------------


Graphics3D 640,480
SetBuffer BackBuffer()

HidePointer
Global piv=CreatePivot()
Global camera=CreateCamera(piv)
CameraClsColor camera ,100,100,200

AmbientLight 20,20,20
light=CreateLight(2,piv)
MoveEntity light,0,20,0
LightRange light,200



Global speed#
Global latspeed#

Global floorfriction#=.98
Global jumpstrength#=1
Global mx#,my#
Global midw=GraphicsWidth()/2
Global midh=GraphicsHeight()/2
Global jumping
Global gravity#=.03
Global yvel#
Global terrain
Global arrow_inflight
Global arrow_velocity#
Global air_friction#=.999
Global playerx# 
Global playery# 
Global playerz#
Global shottimer
Global tertex2 ; make it a global texture

Const w=17 
Const s=31 
Const a=30 
Const d=32 
Const space=57 
Const keyZ=44 
Const keyX=45 
Const C=46
Const e=18



cylinder=CreateCylinder()
EntityColor cylinder,255,0,0
ScaleMesh cylinder,.2,2,.2
cone=CreateCone(7,1)
EntityColor cone,0,255,0
;AddMesh cone,cylinder
PositionMesh cone,0,3,0
AddMesh cone,cylinder


terrain=create_a_terrain(128) ; calls a function that creates a terrain

Global arrow = CopyEntity(cylinder)
;ScaleMesh arrow,3,8,3

RotateMesh arrow,90,0,0 ; make the mesh face in the direction of the arrow's TIP
EntityColor arrow,255,0,0


Type arrows
Field entity
Field inflight
Field velocity#
End Type

For x=1 To 20
b.arrows = New arrows
b\entity=CopyEntity(arrow)
EntityColor b\entity,Rnd(0,255),Rnd(0,255),Rnd(0,255)

Next

HideEntity arrow







HideEntity cylinder
HideEntity cone

PositionEntity piv,128,100,128

SetBuffer BackBuffer()

While Not KeyDown( 1 )

move_player()
move_arrow()

RenderWorld
Color 0,200,0
Rect midw-5,midh-4,10,8,False
Text 20,20,"x= "+playerx/16+" z= "+playerz#/16
Text 40,60,TerrainSize(terrain)
Flip

Wend

End


;---------------------------------------------------------------------

Function Create_a_Texture(size)

texture = CreateTexture(size,size)
SetBuffer TextureBuffer(texture)
For x=1 To size
For y =1 To size
roll=roll+1
Colred= Sin(x*3)*128 * Cos(y*6)*16
Colgreen= Cos(y*16)*64 
Colblue= Sin(x*3)*128 

Color colred,colgreen,colblue
Plot p,y
Next
Next
Return texture
End Function

;-----------------------------------------------------------------------

Function create_a_terrain(size)

imsize=size


hmap = CreateImage(imsize,imsize)
SetBuffer ImageBuffer(hmap)
For z=1 To imsize
For x=0 To imsize
col=50.0-(Sin((x*12))*50)
col=col+(50.0-(Sin((z*12))*50))
;col=col/2.0
Color col,col,col
Plot z,x
Next 
Next 


SaveImage(hmap,"heightmap"+Str(imsize)+".bmp")

ter=LoadTerrain("heightmap"+Str(imsize)+".bmp")

ScaleEntity ter,16,128,16

terraintex = CreateTexture(size,size)
SetBuffer TextureBuffer(terraintex)
For x=1 To size
For y =1 To size
roll=roll+1
Col= Sin(x*3)*128 * Cos(y*3)*128
Color col,col,col
Plot x,y
Next
Next

EntityTexture ter,terraintex,0,0

tertex2 = CreateTexture(size,size)
SetBuffer TextureBuffer(tertex2)
ClsColor 150,150,150
Cls

ScaleTexture tertex2,size,size
RotateTexture tertex2,90

EntityTexture ter,tertex2,0,1
TextureBlend tertex2,2

TerrainShading ter,True


ScaleTexture terraintex,2,2
Return ter

End Function

; ---------------------------------------------------------

Function move_player()

; If KeyDown(e)=1 Then show_inventory
; If KeyDown(z)=1 Then zoomin()
; If KeyDown(x)=1 Then zoomout()
shottimer=shottimer-1
If shottimer<-1 Then shottimer=-1

If MouseDown(1)=1 And shottimer<0 Then shoot_arrow() 
If KeyDown(w)=1 Then speed#=speed#+.09 ; .4 for release
If KeyDown(a)=1 Then latspeed# = latspeed# - .08
If KeyDown(s)=1 Then speed# = speed# *.9
If KeyDown(d)=1 Then latspeed# = latspeed# + .08




latspeed#=latspeed#*.9
speed#=speed#* floorfriction# ; friction


MY#=interpolate#(MouseYSpeed(),MY#,camspeed# )
MX#=interpolate#(MouseXSpeed(),MX#,camspeed# )

; limit pitch
campitch# = EntityPitch(camera)
If (campitch#+my < -60 ) Or (campitch#+my > 60)
turn=False
Else
TurnEntity camera,MY#,0,0 ; turn camera up and down
EndIf

MoveEntity piv,latspeed#,0,speed#
TurnEntity piv,0,-MX#,0 ; turn pivot left --right

playerx# =EntityX(piv,True)
playery# =EntityY(piv,True)
playerz# =EntityZ(piv,True)

ground#=TerrainY(terrain,playerx#,playery#,playerz#)+10
PositionEntity piv,playerx#,ground#,playerz#
;If playery#<ground#+2 Then yvel#=Abs(yvel#*.3)

;yvel# = yvel# - gravity#
;If playery#<ground#+2 Then yvel#=yvel#+.7
MoveMouse midw,midh






End Function

Function interpolate#(newvalue#,oldvalue#,increments# )
If increments>1 Then oldvalue#=oldvalue#-(oldvalue#-newvalue#)/increments
If increments<=1 Then oldvalue=newvalue
Return oldvalue#
End Function


Function shoot_arrow()
For b.arrows = Each arrows
If b\inflight=0 Then Exit
Next

If b\inflight=0 ; 0 = false (not flying)
PositionEntity b\entity,playerx#,playery#,playerz#
b\velocity#=5
arrowx#=EntityX(b\entity)
arrowy#=EntityY(b\entity)
arrowz#=EntityZ(b\entity)
arrowpitch#=EntityPitch(camera)
arrowyaw#=EntityYaw(piv)
arrowroll#=EntityRoll(piv)

PositionEntity b\entity,arrowx,arrowy,arrowz
RotateEntity b\entity,arrowpitch,arrowyaw,arrowroll

b\inflight=1
EndIf

shottimer=5


End Function

Function move_arrow()
For b.arrows = Each arrows
If b\inflight=1 ; 1 = true
b\velocity#=b\velocity# * air_friction#
TurnEntity b\entity,1,0,0
MoveEntity b\entity,0,0,b\velocity

arrowx#=EntityX(b\entity)
arrowy#=EntityY(b\entity)
arrowz#=EntityZ(b\entity)
If arrowy#<TerrainY(terrain,arrowx#,arrowy#,arrowz#)+4 
     b\inflight=0
    scorch_terrain(arrowx#,arrowz#)
EndIf
If arrowy#<-100 Then b\inflight=0
; move arrow

EndIf
Next 
End Function


Function scorch_terrain(scorchx#,scorchz#)

SetBuffer TextureBuffer(tertex2)
Color 0,0,5
Plot scorchz#/16,scorchx#/16

SetBuffer BackBuffer()
End Function
