; ID: 727
; Author: Jeppe Nielsen
; Date: 2003-06-24 19:34:01
; Title: Break the Glass
; Description: Shoot glass with the mouse.

;Break glass example, by Jeppe Nielsen 2003

Type glass

Field e

Field s

Field w#,h#

Field sx#,sy#

Field broken

End Type

Type glasspiece

Field p

Field pivot[3]

Field x#,y#,z#

Field vx#,vy#,vz#

Field pitch#,yaw#,roll#

Field g.glass

Field index

End Type


Const gravity#=0.004

Graphics3D 800,600,16,2

pivot=CreatePivot()
camera=CreateCamera(pivot)

CameraClsColor camera,255,255,255

MoveEntity camera,0,1,-4

;cube=CreateCube()
;PositionEntity cube,0,0,0
;FlipMesh cube

light=CreateLight(1)
RotateEntity light,-10,40,0

g.glass=glassnew(2,2,20,20)
PositionEntity g\e,0,0,-1
g.glass=glassnew(2,2,20,20)
PositionEntity g\e,0,0,1
g.glass=glassnew(2,2,20,20)
PositionEntity g\e,0,0,-1
TurnEntity g\e,0,90,0
g.glass=glassnew(2,2,20,20)
PositionEntity g\e,2,0,-1
TurnEntity g\e,0,90,0

Color 0,0,0

Repeat

TurnEntity pivot,0,1,0





If MouseHit(1)>0

p=CameraPick(camera,MouseX(),MouseY())
g.glass=Last glass
While g<>Null
If g\e=p
glassbreak(g)
Exit
EndIf
g=Before g
Wend


EndIf

glassupdate()


RenderWorld()
mx=MouseX()
my=MouseY()

Text 400,20,"SHOOT the glass cube :)",1

Oval mx-30,my-30,60,60,0
Plot mx,my


Flip

Until KeyDown(1)

End



Function glassnew.glass(wid#,hei#,secx,secy,parent=0)

g.glass=New glass

g\w#=wid#
g\h#=hei#


g\e=CreateMesh(parent)

g\s=CreateSurface(g\e)

dx#=1/Float(secx)
dy#=1/Float(secy)

g\sx#=dx#
g\sy#=dy#

y#=0
For yy=1 To secy
x#=0
For xx=1 To secx

gp.glasspiece=New glasspiece
gp\g=g

gp\index=AddVertex(g\s,x#,y#,0,x#,y#,0)
AddVertex(g\s,x#+dx#,y#,0,x#+dx#,y#,0)
AddVertex(g\s,x#,y#+dy#,0,x#,y#+dy#,0)
AddVertex(g\s,x#+dx#,y#+dy#,0,x#+dx#,y#+dy#,0)

AddTriangle g\s,gp\index,gp\index+3,gp\index+1
AddTriangle g\s,gp\index,gp\index+3,gp\index+2

x#=x#+dx#
Next
y#=y#+dy#
Next

ScaleMesh g\e,wid#,hei#,1


EntityFX g\e,16
EntityPickMode g\e,2
EntityColor g\e,0,0,255
EntityAlpha g\e,0.5

Return g

End Function

Function glassbreak(g.glass)
If g\broken=1 Then Return
g\broken=1

For gp.glasspiece=Each glasspiece
If gp\g=g

gp\p=CreatePivot()

gp\x#=(VertexX(g\s,gp\index)+VertexX(g\s,gp\index+3))/2
gp\y#=(VertexY(g\s,gp\index)+VertexY(g\s,gp\index+3))/2
gp\z#=(VertexZ(g\s,gp\index)+VertexZ(g\s,gp\index+3))/2
gp\vx#=Rnd(-.02,.02)
gp\vy#=Rnd(-.02,.02)
gp\vz#=Rnd(-.02,.02)
gp\pitch#=Rnd(-2,2)
gp\yaw#=Rnd(-2,2)
gp\roll#=Rnd(-2,2)

PositionEntity gp\p,gp\x,gp\y,gp\z

For n=0 To 3
gp\pivot[n]=CreatePivot(gp\p)
PositionEntity gp\pivot[n],VertexX(g\s,gp\index+n),VertexY(g\s,gp\index+n),VertexZ(g\s,gp\index+n),1
Next

EndIf
Next

End Function

Function glassupdate()

For g.glass=Each glass
If g\broken=1

For gp.glasspiece=Each glasspiece
If gp\g=g

gp\vy=gp\vy-gravity#

gp\x=gp\x+gp\vx
gp\y=gp\y+gp\vy
gp\z=gp\z+gp\vz

PositionEntity gp\p,gp\x+gp\g\sx#/2,gp\y+gp\g\sy#/2,gp\z,1

TurnEntity gp\p,gp\pitch,gp\yaw,gp\roll

For n=0 To 3
VertexCoords gp\g\s,gp\index+n,EntityX(gp\pivot[n],1),EntityY(gp\pivot[n],1),EntityZ(gp\pivot[n],1)
Next

If gp\y<-10 Then glassdelete(g)


EndIf
Next



EndIf
Next

End Function

Function glassdelete(g.glass)

For gp.glasspiece=Each glasspiece
If gp\g=g

For n=0 To 3
FreeEntity gp\pivot[n]
Next

FreeEntity gp\p

Delete gp

EndIf
Next

FreeEntity g\e

Delete g

End Function
