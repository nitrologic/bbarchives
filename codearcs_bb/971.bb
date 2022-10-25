; ID: 971
; Author: Malice
; Date: 2004-03-19 12:16:53
; Title: Simpler TForm 3D Radar
; Description: Displays objects in relative 3D positions

Graphics3D 800,600,32,0
SetBuffer BackBuffer()

lighting=CreateLight(2)

PositionEntity lighting,-20,20,-30

ship=CreateCone(3,1)
ScaleMesh ship,2,5,1
RotateMesh ship,-90,180,0

red=CreateCube()
blue=CreateCube()
green=CreateCube()

EntityColor red,255,0,0
EntityColor green,0,255,0
EntityColor blue,0,0,255

cam=CreateCamera()

MoveEntity cam,0,0,-20
PointEntity cam,ship

PositionEntity red,Rand(-10,10),Rand(-10,10),Rand(-10,10)
PositionEntity blue,Rand(-10,10),Rand(-10,10),Rand(-10,10)
PositionEntity green,Rand(-10,10),Rand(-10,10),Rand(-10,10)

While Not KeyDown(1) 

If KeyDown(208) Then TurnEntity ship,5,0,0 
If KeyDown(200) Then TurnEntity ship,-5,0,0 

If KeyDown(203) Then TurnEntity ship,0,5,0 
If KeyDown(205) Then TurnEntity ship,0,-5,0 

If MouseDown(1) MoveEntity ship,0,0,1 

UpdateWorld 
RenderWorld 

;Radar 
Color 255,255,255 
Rect 400,500,3,3,0 

;Red Dot 
TFormPoint (EntityX(red),EntityY(red),EntityZ(red),0,ship) 
red_relativeX=(0-TFormedX())
red_relativey=TFormedY()
red_relativez=TFormedZ()
Color 255,0,0

If red_relativeY>0 Then Rect 400-red_relativeX,500-red_relativeZ,1,red_relativeY,1 
If red_relativeY<0 Then Rect 400-red_relativeX,500-red_relativeZ+Abs(red_relativeY),1,Abs(red_relativeY),1 

;Green Dot 
TFormPoint (EntityX(green),EntityY(green),EntityZ(green),0,ship) 
green_relativeX=(0-TFormedX()) 
green_relativey=TFormedY()
green_relativez=TFormedZ()
Color 0,255,0 

If green_relativeY>0 Then Rect 400-green_relativeX,500-green_relativeZ,1,green_relativeY,1 
If green_relativeY<0 Then Rect 400-green_relativeX,500-green_relativeZ+Abs(green_relativeY),1,Abs(green_relativeY),1 


;Blue Dot 
TFormPoint (EntityX(blue),EntityY(blue),EntityZ(blue),0,ship) 
blue_relativeX=(0-TFormedX()) 
blue_relativey=TFormedY()
blue_relativez=TFormedZ()
Color 0,0,255

If blue_relativeY>0 Then Rect 400-blue_relativeX,500-blue_relativeZ,1,blue_relativeY+1,1 
If blue_relativeY<0 Then Rect 400-blue_relativeX,500-blue_relativeZ+Abs(blue_relativeY),1,Abs(blue_relativeY),1 

Flip 

Wend 

End
