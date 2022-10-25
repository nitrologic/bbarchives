; ID: 112
; Author: Rob
; Date: 2002-02-25 15:47:23
; Title: Homing Missiles!
; Description: This code makes unlimited 3D curved path homing missiles!

; easy homing missiles - MATHS FREE (almost!) bit messy too... 
; by Rob Cummings 

;notes: 
;no collision - you handle that :) 
;therefore it'll circle the object when normally it would have blown up! (left in for effect!) 
;deadtime is time before it activates its targetting system 

;------------------------------------------------------- 
; declare variables 
;------------------------------------------------------- 

;globals because we use functions 
Global mxspd#,myspd#,camera,missile,pitch#,yaw#,camvx#,camvz#,FPS=60,campitch#,camyaw#,timer 

;10 enemies that do nothing 
Dim blob(9) 

; as many missiles as we want 
Type missiletype 
Field obj ; holds the 3d entity of the missile 
Field target ; references the target entity 
Field deadtime# ; how long after firing the missile is activated 
Field life ; how long it'll live 
Field pivot ; targeting system! (entity) 
End Type 

;fiddle with missile parameters here: 
Global missiledeadtime#=15,missilelife#=300,turnspeed#=20,missilespeed#=15 

;------------------------------------------------------- 
; setup 
;------------------------------------------------------- 

Graphics3D 640,480,16,2 
HidePointer 
camera=CreateCamera() 
PositionEntity camera,0,200,0 
light=CreateLight(2) 
PositionEntity light,800,800,800 

; a dummy object which we'll clone in the game when we need it. 
missile=CreateCube() 
EntityColor missile,255,0,0 
ScaleEntity missile,10,10,60 
HideEntity missile 

;create some things to fire at 
For i=0 To 9 
blob(i)=CreateSphere() 
PositionEntity blob(i),Rnd(800,-800),Rnd(800,-800),Rnd(800,-800) 
EntityColor blob(i),Rnd(128)+127,Rnd(128)+127,Rnd(128)+127 
ScaleEntity blob(i),25,25,25 
EntityRadius blob(i),temp 
Next 

;------------------------------------------------------- 
; mainloop with fps loop timing 
;------------------------------------------------------- 

period=1000/FPS 
time=MilliSecs()-period 
While Not KeyHit(1) 
Repeat 
elapsed=MilliSecs()-time 
Until elapsed 
tween#=Float(elapsed)/Float(period) 
While tween>=1 
tween=tween-1 
time=time+period 
If tween<1 Then CaptureWorld 
updategame() ; call the game 
UpdateWorld 
Wend 
RenderWorld tween 
Text 0,0,EntityPitch(camera) 
Flip 
Wend 
End 

;------------------------------------------------------- 
; function to update the game 
;------------------------------------------------------- 

Function updategame() 
freelook() 

;now handle the firing of the missile 
If MouseHit(1) firemissile() 

;update all missiles 
updatemissiles() 

End Function 

;------------------------------------------------------- 
; controls 
;------------------------------------------------------- 

Function freelook() 
mxspd=MouseXSpeed()*0.5 
myspd=MouseYSpeed()*0.5 
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2 
campitch=campitch+myspd 
camyaw=camyaw-mxspd 
If campitch<-89 Then campitch=-89 
If campitch>89 Then campitch=89 
RotateEntity camera,campitch,camyaw,0 
If KeyDown(203) camvx=camvx-0.1 ElseIf KeyDown(205) camvx=camvx+0.1 
If KeyDown(208) camvz=camvz-0.1 ElseIf KeyDown(200) camvz=camvz+0.1 
camvx=camvx/1.05 
camvz=camvz/1.05 
MoveEntity camera,camvx,0,camvz 
End Function 

;------------------------------------------------------- 
; fire missile 
;------------------------------------------------------- 

Function firemissile() 

;create a missile where we are 
m.missiletype=New missiletype 
m\obj=CopyEntity(missile) 
PositionEntity m\obj,EntityX(camera),EntityY(camera),EntityZ(camera) 
RotateEntity m\obj,campitch,camyaw,0 
m\life=missilelife 
m\deadtime=missiledeadtime 
m\pivot=CreatePivot() 
PositionEntity m\pivot,EntityX(m\obj),EntityY(m\obj),EntityZ(m\obj) 
RotateEntity m\pivot,campitch,camyaw,0 
m\target=camera 
End Function 

;------------------------------------------------------- 
; update all missiles 
;------------------------------------------------------- 

Function updatemissiles() 
For m.missiletype=Each missiletype 
MoveEntity m\obj,0,0,missilespeed 
PositionEntity m\pivot,EntityX(m\obj),EntityY(m\obj),EntityZ(m\obj) ; move with missile - parent? 
PointEntity(m\pivot,m\target) 



If m\deadtime=0 

temp_pitch# = curveangle#(EntityPitch(m\pivot),EntityPitch(m\obj),turnspeed#) 
temp_yaw# = curveangle#(EntityYaw(m\pivot),EntityYaw(m\obj),turnspeed#) 
RotateEntity m\obj,temp_pitch,temp_yaw,0 

Else 

If m\deadtime=1 ; nearly time to go hunting! 
; look for closest blob - optimise with custom sort routine if you want 
; shove any blob into the target to start us off 
olddist=EntityDistance(blob(1),camera) 
i=-1:dist=olddist:seek=blob(1) 

For k=0 To 9 
dist=EntityDistance(blob(k),m\obj) 
If dist<=olddist olddist=dist:seek=blob(k) 
Next 
m\target=seek ; seek this guy! 
EndIf 

m\deadtime=m\deadtime-1 

EndIf 

If m\life<0 FreeEntity m\obj:Delete m Else m\life=m\life-1 
Next 
End Function 


;------------------------------------------------------- 
; function to move one angle to another with acceleration 
;------------------------------------------------------- 

Function curveangle#( newangle#,oldangle#,increments#) 
	If increments>1 
		If (oldangle+360)-newangle<NEWANGLE-OLDANGLE Then OLDANGLE=360+OLDANGLE 
		If (newangle+360)-oldangle<OLDANGLE-NEWANGLE Then NEWANGLE=360+NEWANGLE 
		oldangle=oldangle-(oldangle-newangle)/increments 
	EndIf 

		If increments<=1 oldangle=newangle 
	Return oldangle 
End Function

;		If (newangle+360)-oldangle<OLDANGLE-NEWANGLE NEWANGLE=360+NEWANGLE 
