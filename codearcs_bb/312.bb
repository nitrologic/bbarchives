; ID: 312
; Author: Doggie
; Date: 2002-04-27 18:35:37
; Title: Radar with True Bearing
; Description: Radar example with sweep display of targets

;             RADAR EXAMPLE                 *
;     (The "Cube n' Missile" crisis)        *
;       Doggie at BlitzBasic Forums         *
;                                           *
;              Controls                     *
;       , .       turn the ship             *
;       lft ctrl  fire missile              * 
;        t        Cycle Targets             *   
;********************************************

; Set The Graphic Mode
Graphics3D 800,600,32,1 
SetBuffer BackBuffer()

Type missile
Field entity, tobedeleted
End Type
Global crs#
AmbientLight 193,224,255
plane=CreatePlane(16)
EntityColor plane,5,5,200

;### Make Our Ship ##
Global ship=CreateCube()
ScaleEntity ship,6,6,60
EntityColor ship,150,150,150
PositionEntity ship,900,10,600
; Create a camera  
Global camera=CreateCamera()
CameraClsColor camera,0,0,0    
CameraRange camera,1,1500
CameraViewport camera,0,0,GraphicsWidth(),400
PositionEntity camera,EntityX(ship),35,EntityZ(ship)+250
PointEntity camera,ship
CameraFogMode camera,1
CameraFogRange camera,750,1600
CameraFogColor camera,60,60,65
;### Create Target Cam ###
Global targcam=CreateCamera()
CameraRange targcam,1,1500
PositionEntity targcam,EntityX(ship),35,EntityZ(ship)-50
PointEntity targcam,ship
CameraViewport targcam,36,425,210,150
CameraFogMode targcam,1
CameraFogRange targcam,750,1600
CameraFogColor targcam,60,60,65

;##### Make Some Targets ###
Global destroyer1=CreateCube()
ScaleEntity destroyer1,15,15,15
PositionEntity destroyer1,855,3,800
EntityColor destroyer1,255,50,50
Global destroyer2=CopyEntity(destroyer1)
PositionEntity destroyer2,875,3,842
EntityColor destroyer2,255,255,255
Global f151=CreateCube()
ScaleEntity f151,12,12,12
PositionEntity f151,840,30,800
EntityColor f151,150,150,255
Global f152=CopyEntity(f151)
PositionEntity f152,855,30,842
EntityColor f152,70,255,50
Global Missile1=CreateCube()
ScaleEntity Missile1,4,4,12
PositionEntity Missile1,455,30,2242
RotateEntity Missile1,0,180,0
EntityColor Missile1,150,150,150
trg=1


;################  Loop ##############

While Not KeyHit(1)
Cls

GameInput()

;#### Track All Targets ###
If KeyHit(20)
    trg=trg+1
    If trg>4 Then trg=1
    EndIf
If trg=1
rb=EntityDistance(ship,destroyer1)
If rb<=1500 Then
Color 255,50,50
radar(destroyer1,ship)
CameraViewport targcam,36,425,210,150
PositionEntity targcam,EntityX(destroyer1),35,EntityZ(destroyer1)-50 
EndIf
EndIf
If trg=2
rb2=EntityDistance(ship,destroyer2)
If rb2<=1500 Then
Color 255,255,255
radar(destroyer2,ship)
CameraViewport targcam,36,425,210,150
PositionEntity targcam,EntityX(destroyer2),35,EntityZ(destroyer2)-50 
EndIf
EndIf
If trg=3
rb3=EntityDistance(ship,f151)
If rb3<=1500 Then
Color 70,255,50
radar(f151,ship)
CameraViewport targcam,36,425,210,150
PositionEntity targcam,EntityX(f151),35,EntityZ(f151)-50 
EndIf
EndIf
If trg=4
rb4=EntityDistance(ship,f151)
If rb4<=1500 Then
Color 70,255,50
radar(f152,ship)
CameraViewport targcam,36,425,210,150
PositionEntity targcam,EntityX(f152),35,EntityZ(f152)-50 
EndIf
EndIf
;### Missile Control ####
If trg=1 Then rtarget=destroyer1
If trg=2 Then rtarget=destroyer2
If trg=3 Then rtarget=f151
If trg=4 Then rtarget=f152
If KeyHit(29)
d.Missile = New Missile
d\entity=CopyEntity(Missile1)
PositionEntity d\entity,EntityX(ship,True),25,EntityZ(ship,True)
PointEntity d\entity,rtarget
EndIf

;### Move Targets ###

spd=2
MoveEntity destroyer1,0,0,-spd
TurnEntity destroyer1,0,.5,0
MoveEntity destroyer2,0,0,-spd
TurnEntity destroyer2,0,.5,0
MoveEntity f151,0,0,-3
TurnEntity f151,0,.5,0
MoveEntity f152,0,0,-3
TurnEntity f152,0,.5,0

For d.Missile = Each Missile
MoveEntity d\entity,0,0,20
Next

UpdateWorld
RenderWorld
If crs#<10 Then Text 670,440,"00"+Int(crs#)
If crs#<100 And crs#>9 Then Text 670,440,"0"+Int(crs#)
If crs#>99 Then Text 670,440,Int(crs#)
;#### Create Radar Display - 408 and 502 are x and y coords of display center ####
time=time-1
Color 150,255,150
Oval 324,418,170,170,0
Oval 354,448,110,110,0
Oval 384,478,50,50,0
Line 408,502,408+Sin(time)*86,502+Cos(time)*86
Color 255,55,55
Line 400,502,322,502
Line 416,502,494,502
Line 408,494,408,416
Line 408,510,408,588
Color 255,255,255
Text 470,420,"Target "+trg
Flip
Wend
End

;***********************************************************************
 
Function GameInput()
    If KeyDown(51) Then
	camyaw#=.5
    TurnEntity ship ,0,camyaw#,0 ;left
	crs#=crs#-.5:If crs#<0 Then crs#=360
	EndIf
	If KeyDown(52) Then
	camyaw#=-.5
	TurnEntity ship ,0,camyaw#,0 ;right
	crs#=crs#+.5:If crs#>360 Then crs#=0 
	EndIf
End Function

;#### halo's wrapangle function ####
Function wrapangle#(value#)
a#=Floor(value/360)
Return value-a*360
End Function

;#### Doggies Spiffed Up Radar function ####
Function radar(target,centerobject)
diffx=EntityX(centerobject,True)-EntityX(target)
diffz=EntityZ(centerobject,True)-EntityZ(target)
bng=ATan2(diffx,diffz)-90
bng2=wrapangle(bng) 
d=EntityDistance(target,centerobject)/10
screenpointx=Cos(bng2)*d
screenpointy=Sin(bng2)*d
screenpointx=screenpointx+408
screenpointy=screenpointy+502
Color 255,250,150
Plot screenpointx,screenpointy
angle=ATan2(diffx,diffz)
angle2=wrapangle(angle)
dtis=EntityDistance(centerobject,target)
Text 280,580,"BRNG "+angle2
Text 460,580,"RNG "+dtis
Color 255,255,255
End Function
