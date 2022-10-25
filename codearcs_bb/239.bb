; ID: 239
; Author: bradford6
; Date: 2002-02-13 14:53:41
; Title: pickname demo
; Description: simple picker with names

; Pickname Example
; ----------------

AppTitle "Pick a box"



Graphics3D 640,480

midh=GraphicsHeight()/2
midw=GraphicsWidth()/2

SetBuffer BackBuffer()

camera=CreateCamera()
CameraClsColor camera,50,40,50
MoveEntity camera,3,0,-10
light=CreateLight(2)

box=CreateCube() EntityColor box,155,0,0 ; red
box2=CreateCube() EntityColor box2,0,144,0 ; green
ball=CreateSphere() EntityColor ball,255,255,0 ; yellow

NameEntity box," a RED box "
NameEntity box2," a GREEN box "
NameEntity ball," a small YELLOW ball "


EntityRadius ball,1



EntityPickMode box,3
EntityPickMode box2,3
EntityPickMode ball,1

MoveEntity box,0,3,0
MoveEntity box2,3,0,0
MoveEntity ball,6,2,0



While Not KeyDown( 1 )
mx=MouseX()
my=MouseY()	
picked=CameraPick(camera,mx,my)

TurnEntity box,0,3,0
TurnEntity box2,1,0,1

TurnEntity ball,0,1,0
MoveEntity ball,0,0,.1



	RenderWorld
If picked>0
    Color 0,0,255
	Text mx,my-25,"you are picking",True,True
	Text mx,my-12,EntityName(picked),True,True




EndIf


	Flip

Wend

End
