; ID: 1880
; Author: Shambler
; Date: 2006-12-15 19:04:35
; Title: Doors!
; Description: Open/Close/Lock/Unlock Doors

;Modified Machine handler from SARE engine, shows door handling only For ease of readability

;Some Constants
Const sOpening=1
Const sClosing=2
Const sOpen=3
Const sClosedLockNext=4
Const sClosedOpenNext=5
Const sLocked=6
Const sUnlocked=7


;Our Door Type
Type doortype
Field id%
Field mesh% ; the mesh
Field xs#,ys#,zs# ;starting position
Field xe#,ye#,ze# ;ending position
Field ws#,ps#,rs# ;starting rotation
Field we#,pe#,re# ;ending rotation
Field as#,ae#,ca# ;alpha#
Field f# ;0.0-1.0 where inbetween start and end is it?
Field speed# ;speed
Field state%
End Type


;Create a door
;id%= this doors ID number, doors that share the same ID will all work off the same lock
;xs#,ys#,zs# start position
;xe#,ye#,ze# end position
;ws#,ps#,rs# start rotation
;we#,pe#,re# end rotation
;as#,ae# alpha start and end for fading doors
;f# between 0.0 and 1.0, describes how far between start and end we are
;s# speed of the door
;st current state the door is in

Function Create_Door(id%,xs#,ys#,zs#,xe#,ye#,ze#,ws#,ps#,rs#,we#,pe#,re#,as#,ae#,f#,s#,st)
door.doortype=New doortype
door\id%=id%
door\xs#=xs#:door\ys#=ys#:door\zs#=zs#
door\xe#=xe#:door\ye#=ye#:door\ze#=ze#
door\ws#=ws#:door\ps#=ps#:door\rs#=rs#
door\we#=we#:door\pe#=pe#:door\re#=re#
door\as#=as#:door\ae#=ae#
door\f#=f#
door\ca#=as#+(ae#-as#)*f#
door\speed#=s#
door\state=st

;you would load your door mesh in here
;but for simplicity...
door\mesh%=CreateCube()
ScaleEntity door\mesh%,1,1,0.1

RotateEntity door\mesh,ws#+(we#-ws#)*f#,ps#+(pe#-ps#)*f#,rs#+(re#-rs#)*f#
PositionEntity door\mesh,xs#+(xe#-xs#)*f#,ys#+(ye#-ys#)*f#,zs#+(ze#-zs#)*f#


Return door\mesh


End Function

Function Update_Doors()

For door.doortype=Each doortype

Select door\state
	Case sOpening
	door\f#=door\f#+door\speed#
	If door\f#>=1.0
	door\f#=1.0
	door\state=sOpen
	someinfo$="Door is Open"
	EndIf
	

Case sClosedLockNext
	If gLockTrigID%=door\id
	door\state=sLocked
	someinfo$="Door is locked"
	EndIf
	
Case sClosedOpenNext
If gLockTrigID%=door\id
door\state=sOpening
someinfo$="Door Opening"
EndIf 

Case sClosing
	door\f#=door\f#-door\speed#
	If door\f#<=0.0
	door\f#=0.0
	door\state=sClosedLockNext
	someinfo$="Door is Closed, next click will lock the door"
	EndIf

Case sOpen
	If gLockTrigID%=door\id
	door\state=sClosing
	someinfo$="Door is closing..."
	ShowEntity door\mesh
	EndIf
	
Case sClosed
	If gLockTrigID%=door\id
	door\state=sOpening
	someinfo$="Door is opening..."
	EndIf
	
Case sLocked
	If gLockTrigID%=door\id
	door\state=sClosed
	someinfo$="Door is now unlocked but still closed"
	EndIf
End Select

RotateEntity door\mesh,door\ws+(door\we-door\ws)*door\f#,door\ps+(door\pe-door\ps)*door\f#,door\rs+(door\re-door\rs)*door\f#
PositionEntity door\mesh,door\xs+(door\xe-door\xs)*door\f#,door\ys+(door\ye-door\ys)*door\f#,door\zs+(door\ze-door\zs)*door\f#
door\ca#=door\as#+(door\ae#-door\as#)*door\f#
EntityAlpha door\mesh,door\ca#


Next

gLockTrigID%=0

End Function



Graphics3D 800,600,32,2
SetBuffer BackBuffer()

Global cam=CreateCamera()
Global look=CreatePivot()
Global someinfo$="click left mouse button"
PositionEntity cam,0,8,-8
PointEntity cam,look

Global gLockTrigID%=0


;Just use one of these Create_Door function calls to see the different
;Opening/Closing options at work
;Uncomment more than one to see multiple door control

;Movement
Create_Door(1,0,0,0,-5,0,0,0,0,0,0,0,0,1,1,0,0.02,sClosedOpenNext)
;Rotation
;Create_Door(1,0,0,0,0,0,0,0,0,0,0,90,0,1,1,0,0.04,sClosedOpenNext)
;Movement and Rotation
;Create_Door(1,0,0,0,0,5,0,0,0,0,90,0,0,1,1,0,0.06,sClosedOpenNext)
;Alpha
;Create_Door(1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0.08,sClosedOpenNext)




While Not KeyHit(1)

If MouseHit(1)=1 Then gLockTrigID%=1

Update_Doors()
UpdateWorld()
RenderWorld()
Text 0,0,"Door handling demo by Shambler"
Text 0,10,someinfo$
Flip 

Wend
