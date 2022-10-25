; ID: 1332
; Author: Howitzer
; Date: 2005-03-18 16:05:19
; Title: Control Brightness and Contrast
; Description: Adjusting the screen's contrast can be difficult, here is how

Print "             Low------High"
Print "Brightness < 0   50   100 (high # =high brightness"
Print "                          low # =low brightness)  "
Print
Print "Contrast   > 8   4    2   (low # =high contrast   "
Print "                          high # =low contrast)   "

brit=Input("Brightness(0<100)   =") 
cont=Input("Contrast  (8>2)     =")
If cont=0 Then cont=255; cannot divide by zero- sets contrast to default
level$=Input$("Load Level (level.b3d)=")


; Starting 3d
Graphics3D 800,600,0,1 
SetBuffer BackBuffer() 


; ******************** *******************************************************
; * Gamma Controller * * cont=Contrast brit=Brightness (cont scale reversed) *
; ******************** *******************************************************
For x=0 To 255                                                              ;*
SetGamma x,x,x,(x+x/cont)+brit,(x+x/cont)+brit,(x+x/cont)+brit              ;*
Next                                                                        ;*
UpdateGamma                                                                 ;*
; ****************************************************************************


; Enviroment
camera=CreateCamera() 
PositionEntity camera,0,0,0
CameraRange camera,0.01,10000

LoadMesh level$

While Not KeyDown( 1 ) 

If KeyDown( 205 )=True Then TurnEntity camera,0,-1,0 
If KeyDown( 203 )=True Then TurnEntity camera,0,1,0
If KeyDown( 200 )=True Then MoveEntity camera,0,0.05,0
If KeyDown( 208 )=True Then MoveEntity camera,0,-0.05,0
If KeyDown( 17 )=True Then MoveEntity camera,0,0,0.05 
If KeyDown( 31 )=True Then MoveEntity camera,0,0,-0.05
If KeyDown( 32 )=True Then MoveEntity camera,0.05,0,0
If KeyDown( 30 )=True Then MoveEntity camera,-0.05,0,0

UpdateWorld
RenderWorld 
Flip 

Wend 

End
