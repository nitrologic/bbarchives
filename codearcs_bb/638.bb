; ID: 638
; Author: Markus Rauch
; Date: 2003-04-02 12:35:01
; Title: One Mountain on Terrain
; Description: One Mountain on Terrain

;Blitz Basic 3D Example from M.Rauch

;The Mountain :-)

;02.04.2003

Graphics3D 640,480,16,0
SetBuffer BackBuffer()

Global cpiv=CreatePivot()

Global cam=CreateCamera()
PositionEntity cam,0,200,-250
PointEntity cam,cpiv

;WireFrame 1

Global ter=32

Global tet=LoadTexture("Fels.bmp") ;<--- your own Texture !?

ScaleTexture tet,ter/4,ter/4 ;show the Texture 4*4 times on Terrain

Global te=CreateTerrain(ter)

Global teb=CreateBrush(255,255,255)

BrushTexture teb,tet

PaintEntity te,teb

ScaleEntity te,10,100,10
PositionEntity te,-ter*10/2.0,0.0,-ter*10/2.0

TerrainDetail te,2000,False

For x=-ter/4 To ter/4
For y=-ter/4 To ter/4
 x1=x+ter/2
 y1=y+ter/2
 e#=Sqr(x*x+y*y)
 h#=1.0-(e/Sqr( (ter/4)*(ter/4) + (ter/4)*(ter/4) ) )
 h1#=-Cos(h*180.0)/2.0+0.5
 DebugLog "x="+x+" y="+y+" e="+e+" h="+h+" h1="+h1
 If h1<0.0 Then h1=0.0 
 If h1>1.0 Then h1=1.0 

 ModifyTerrain te,x1,y1,h1
Next
Next

Local w#

While Not KeyHit(1)

 x=Sin(w)*200
 z=Cos(w)*200

 w=w+1.0:If w > 360.0 Then w=w-360.0 

 PositionEntity cam,x,200,z
 PointEntity cam,cpiv

 RenderWorld
 Flip
Wend
End
