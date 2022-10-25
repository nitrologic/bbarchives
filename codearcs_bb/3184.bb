; ID: 3184
; Author: Matty
; Date: 2015-01-31 06:14:42
; Title: General solution to the lead target problem
; Description: Function which calculates the direction to shoot a bullet in to hit a moving target from a moving platform with fixed muzzle speed

;Function to find the velocity needed to hit a moving target, from a moving platform for a fixed muzzle velocity.
;Because of the constraint of a fixed muzzle velocity we need a degree of freedom, in this case I have chosen the 
;time to hit the target, though another possibility would have been the velocity of the moving platform - however in 
;practice this is less easy to control for.
;


;Note only calculateMuzzleVelocity is useful for the end user....the other functions are for internal use only.....


Dim MuzzleVelocity#(3) ;unfortunately because I cannot return an array with a blitz3d/plus function I have to define an array to receive the values from the function...if there is 
						; a better solution I don't know it.....banks are nice but lead to memory leaks if forgotten about, types are overkill for this.....alternatively returning
						;a comma separated string would work but be highly inefficient......


;simple example... Works in both 2D and 3D.....
example2D() 
					
						
Function calculateMuzzleVelocity(posx#,posy#,posz#,velx#,vely#,velz#,targetx#,targety#,targetz#,targetvelx#,targetvely#,targetvelz#,maximummuzzlevel#)
;Pass in the values above....should make perfect sense....
;
;Positions of platform (posx,posy,posz), velocities of platform (velx,vely,velz)
;Positions of target (targetx,targety,targetz), velocities of target (targetvelx,targetvely,targetvelz)
;
;Maximum muzzle velocity - ie the maximum speed the bullet is allowed to travel at - not including any acquired velocity from the moving platform.....
;
;Returns:
;
;false if unable to hit target (and does not alter contents of array)
;
;true if able to hit target (and populates array with velocity in x,y,z directions...)
;Oh...the 3rd index in muzzle velocity is actually the time - not a spatial coordinate....useful number to have
;



T# = getT(getD(targetx,posx),getD(targety,posy),getD(targetz,posz),getVD(targetvelx,velx),getVD(targetvely,vely),getVD(targetvelz,velz),maximummuzzlevel)
If(T=-1 Or T=0) Then 
	;;not possible to hit target....
	Return False
EndIf

MuzzleVelocity(0) = getVm(targetx,posx,targetvelx,velx,T)
MuzzleVelocity(1) = getVm(targety,posy,targetvely,vely,T)
MuzzleVelocity(2) = getVm(targetz,posz,targetvelz,velz,T)
MuzzleVelocity(3) = T
Return True;


End Function 						





Function getVm#(target#,platform#,vtarget#,vplatform#,T#)
;used in all 3 coordinate axes...hence no specific axis mentioned...
;do not call for T=0....
;
;Technically - you shouldn't need to call this - it is another helper function like the ones below..
;
;
;
;Parameters
;
;target = position of target on axis
;platform = position of target on axis
;vtarget = velocity of target on axis
;vplatform = velocity of target on axis
;
;Return value
;
;Muzzle velocity along the axis required to hit target at time T. (T is calculated elsewhere...see further down)
;



If(T<>0) Then 
	Return (target - platform + (vtarget - vplatform)*T)/T
EndIf 
	
End Function 


Function getD#(target#,platform#)
;helper function...no need to call this...effectively a private method

Return target - platform

End Function 
Function getVD#(vtarget#,vplatform#)
;unfortunate name for a function but oh well...don't worry you won't need to call this...another private method...

Return vtarget - vplatform

End Function 




Function getT#(Dx#,Dy#,Dz#,VDx#,VDy#,VDz#,L#)
;function to calculate the appropriate time needed to satisfy the equation.
;Note - sometimes there is no solution, and sometimes there is multiple solutions and sometimes there is a single solution.
;When there is more than one solution there may be a nonsensical solution - this is ignored.
;In the event there are two realistic solutions then the earlier one is returned...
;
;Note - you should never have to call this function - if blitz had a way of describing private methods - this would be a private method.




DxDx# = Dx*Dx
DyDy# = Dy*Dy
DzDz# = Dz*Dz

DxVDx# = Dx*VDx
DyVDy# = Dy*VDy
DzVDz# = Dz*VDz

VDxVDx# = VDx*VDx
VDyVDy# = VDy*VDy
VDzVDz# = VDz*VDz

LL# = L*L

If(LL = VDxVDx + VDyVDy + VDzVDz)
	;impossible to hit.....ie will take eternity to do so....
	Return -1
EndIf

BB# = (DxVDx + DyVDy + DzVDz)*(DxVDx + DyVDy + DzVDz)
AC# = (LL - VDxVDx - VDyVDy - VDzVDz)*(DxDx + DyDy + DzDz)

If(BB+AC<0) Then 
	Return -1
	;impossible to hit....various possible reasons....
EndIf

T1# = ((DxVDx + DyVDy + DzVDz) + Sqr(BB+AC))/(LL-(VDxVDx+VDyVDy+VDzVDz))
T2# = ((DxVDx + DyVDy + DzVDz) - Sqr(BB+AC))/(LL-(VDxVDx+VDyVDy+VDzVDz))

If(T1<0) Then 
	If(T2>0) Then 
		Return T2
	EndIf
EndIf
If(T2<0) Then 
	If(T1>0) Then
		Return T1
	EndIf
EndIf

If(T1<T2) Then Return T1

Return T2

End Function 


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;EXAMPLE 2D

Function example2D()
Graphics 512,512,0,2
SetBuffer BackBuffer()

;let's do some little interactive demo ...have a couple of 'dots' fly around and shoot at each other......

Local px#,py#,pvx#,pvy#,pax#,pay#
Local qx#,y#,qvx#,qvy#,qax#,qay#
Local spd#
Local maxspd# = 4.0 ;arbitrary

Local bx#,by#,bvx#,bvy#
Local blife = 0
Local maxmuzzlevel# = 10.0 ;arbitrary

Local startchase = False
px = Rnd(128)+256
py = Rnd(128)+256

qx = Rnd(128)+256
qy = Rnd(128)+256

pvx = 0
pvy = 0

qvx = 0
qvy = 0



MoveMouse 256,256

Repeat
Cls

px = px + pvx
py = py + pvy

pvx = pvx + pax
pvy = pvy + pay


spd = Sqr(pvx*pvx+pvy*pvy)
If(startchase = True) Then 
	;;;;
	pax = (qx - px) * 0.01
	pay = (qy - py) * 0.01
	
EndIf

If(spd>maxspd) Then
	pvx = pvx * maxspd/spd
	pvy = pvy * maxspd/spd
EndIf


qx = qx + qvx
qy = qy + qvy

qvx = qvx + qax
qvy = qvy + qay


spd = Sqr(qvx*qvx+qvy*qvy)
If(MouseDown(1)) Then 
	startchase = True
	qax = (MouseX() - qx) * 0.01
	qay = (MouseY() - qy) * 0.01
EndIf
If(spd>maxspd) Then
	qvx = qvx * maxspd/spd
	qvy = qvy * maxspd/spd
EndIf


If(bx<0 Or by<0 Or bx>511 Or by>511) Then blife = 0

If(blife>0) Then 
	blife = blife-1
	bx = bx + bvx
	by = by + bvy
	Color 255,255,0
	Rect bx-1,by-1,3,3,1
Else
	;see if we should shoot....and see where we should at!
	If(calculateMuzzleVelocity(px,py,0,pvx,pvy,0,qx,qy,0,qvx,qvy,0,maxmuzzlevel))
		bx = px
		by = py
		bvx = pvx + MuzzleVelocity(0) ;VX
		bvy = pvy + MuzzleVelocity(1) ;VY
		blife = MuzzleVelocity(3) ;TIME
	EndIf	
EndIf 

Color 255,0,255
Rect px-1,py-1,3,3,1

Color 0,255,0
Rect qx-1,qy-1,3,3,1

Flip

Until KeyHit(1)
EndGraphics



End Function
