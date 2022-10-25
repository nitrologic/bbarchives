; ID: 2234
; Author: H. T. U.
; Date: 2008-03-25 01:16:43
; Title: Highspeed distort
; Description: Distorts camera view.

Graphics3D 640,480
SetBuffer BackBuffer()

camera=CreateCamera()
	PositionEntity camera,0,1,-5

light=CreateLight()

ship=CreateSphere()
	ScaleEntity ship,0.5,0.5,2

Dim back(1000)
	For a=1 To 1000
	back(a)=CreateSphere()
	PositionEntity back(a),Rand(-30,30),Rand(-30,30),Rand(-10,10000)
	EntityColor back(a),Rand(0,255),Rand(0,255),Rand(0,255)
Next

While Not KeyDown(1)

If KeyDown(200) speed#=speed#+0.1
If KeyDown(208) speed#=speed#-0.1
If KeyDown(13) Or KeyHit(78) amount#=amount#+0.1
If KeyDown(12) Or KeyHit(74) amount#=amount#-0.1


MoveEntity ship,0,0,speed#


If EntityZ(ship)>9000 PositionEntity ship,0,0,0
If EntityZ(ship)<-5 PositionEntity ship,0,0,9000

PositionEntity camera,EntityX(ship),EntityY(ship),EntityZ(ship)
MoveEntity camera,0,1,-5

CameraZoom camera,1/(amount#*speed#+1)

RenderWorld

Text  0,0,"Press + to increase distortion or - to decrease distortion (and zoom in).",False,False

Flip

Wend

End
