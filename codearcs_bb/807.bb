; ID: 807
; Author: jfk EO-11110
; Date: 2003-10-07 07:47:58
; Title: Grass
; Description: Animated pretty realistic Grass Demo

;Download at http://www.melog.ch/dl/mygrass_nu.zip

;Here is a second Demo that is not included in the Zip. It 
;is using the same Media Files as the Demo in the Zip. This 
;is a bigger Terrain with a huge amount of grass:

; Simple, tiny Grass Demo 2 by JFK aka NORC of CSP
;Graphics3D 1024,768,32,1
Graphics3D 800,600,16,1
SetBuffer BackBuffer()

Global windwave#
Global grasscount

camera=CreateCamera()
TranslateEntity camera,0,20,0
CameraClsColor camera,80,110,120
CameraFogMode camera,1
CameraFogColor camera,80,110,120
CameraFogRange camera,100,200
CameraRange camera,1,202
light=CreateLight()

terrain=LoadTerrain("hmap.jpg")
ScaleEntity terrain,5,100,5
TranslateEntity terrain,-320,0,-320
floortex=LoadTexture("floor.jpg")
ScaleTexture floortex,32/5,32/5
EntityTexture terrain,floortex

quad=LoadMesh("smplquad.3ds")
FitMesh quad,-10,0,0,20,20,0
EntityFX quad,16 Or 1
grasstex=LoadBrush("grass5f.tga",2 Or 48) ; also try Mode 4 instead of 2!
PaintMesh quad,grasstex

Dim grass(100000)
Dim grassa#(100000)
grasscount=0
For j=-250 To 250 Step 10
 For i=-250 To 250 Step 10
  grass(grasscount)=CopyEntity(quad)
  x#=(i+Rnd(-5,5))
  z#=(j+Rnd(-5,5))
  y#=TerrainY(terrain,x,0,z)-Rnd(2)
  PositionEntity grass(grasscount),x,y,z
  grassa(grasscount)=Rand(-90,90)
  grasscount=grasscount+1
 Next
Next
HideEntity quad

;-------------------------Mainloop-----------------------
While KeyDown(1)=0
 If KeyDown(200) Then MoveEntity camera,0,0,.5
 If KeyDown(208) Then MoveEntity camera,0,0,-.5
 mxs#=-MouseXSpeed()/4
 mys#=MouseYSpeed()/4
 mxa#=mxa#+mxs#
 mya#=mya#+mys#
 MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
 PositionEntity camera,EntityX(camera),TerrainY(terrain,EntityX(camera),0,EntityZ(camera))+20,EntityZ(camera)
 RotateEntity camera,mya,mxa,0
 wind(15,1.0)
 UpdateWorld()
 RenderWorld()
 Text 0,0,"Quads in Scene: "+(grasscount-1)
 Text 0,12,"Tris rendered: "+TrisRendered()
 Flip
Wend
End

Function wind(force#,speed#)
 For i=0 To grasscount-1
  x#=EntityX(grass(i))
  z#=EntityZ(grass(i))
  RotateEntity grass(i),Sin(windwave+x+grassa(i))*force#,grassa(i),Cos(windwave+z-grassa(i))*(force#/2)
 Next
 windwave=(windwave+speed#)Mod 360
End Function
