; ID: 1547
; Author: Andy
; Date: 2005-12-01 00:50:28
; Title: Asteroids FPS
; Description: Demonstrates how to make an asteroids game in 3D with a First Person View

Graphics3D 640,480 
SetBuffer BackBuffer() 

camera=CreateCamera() 
light=CreateLight() 
tex=LoadTexture( "coolgrass2.bmp") 
ScaleTexture tex,1000.0,1000.0 

brush=CreateBrush() 
BrushTexture brush,tex 

plane=CreatePlane(8) 
PaintEntity plane,brush 
PositionEntity camera,0.0,50.0,0.0 
RotateEntity camera,90,0.0,0.0 

dist#=0.1 
x#=0 
y#=0 
z#=0 

While Not KeyDown( 1 ) 
If KeyDown( 203 )=True Then angle#=angle#+1.0 
If KeyDown( 205 )=True Then angle#=angle#-1.0 

If KeyDown( 200 )=True Then 
x#=x#+Cos(angle#+90.0)*dist# 
z#=z#+Sin(angle#+90.0)*dist# 
EndIf 

RotateEntity camera,0.0,angle#,0.0 
TranslateEntity camera,x#,0.0,z# 

RenderWorld 

Text 0,20,"X Position: "+x# 
Text 0,40,"Y Position: "+y# 
Text 0,60,"Z Position: "+z# 

Flip 
Wend 

End
