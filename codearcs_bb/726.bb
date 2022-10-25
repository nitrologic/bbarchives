; ID: 726
; Author: Jeppe Nielsen
; Date: 2003-06-24 18:09:05
; Title: Homing physics ( 2D )
; Description: Allow enemies to chase player, with velocity and acceleration.

;Homing example, by Jeppe Nielsen 2003

Global playerx#
Global playery#
Global distance#=100

Type enemy

Field x#,y#
Field vx#,vy#
Field ax#,ay#

Field vmax#
Field amax#

End Type

Graphics3D 800,600,16,2

;create ten enemies at random locations
For n=1 To 10				
;play with last number
;enemynew(x,y,vmax,amax)
enemynew(Rnd(800),Rnd(600),Rnd(0.5,4.5),Rnd(0.08,0.1))
Next


Repeat
Cls

Text 10,10,"Move player with mouse"
Text 10,30,"LMB - Resize allowed distance to player"
Text 10,50,"RMB - Add enemies"

If click=0
	
	playerx=MouseX()
	playery=MouseY()
	
EndIf

If MouseDown(1) And click=0
	
	click=1
	clickx=MouseX()
	clicky=MouseY()
	
EndIf

If MouseDown(1) And click=1
	
	dx=(MouseX()-clickx)
	dy=(MouseY()-clicky)
	
	distance#=Sqr(dx*dx+dy*dy)
		
EndIf

If MouseDown(1)=False And click=1
	click=0
EndIf

If MouseDown(2)>0
	enemynew(Rnd(800),Rnd(600),Rnd(2.5,2.5),Rnd(0.08,0.1))
EndIf


enemyupdate()
enemydraw()


Rect playerx-5,playery-5,10,10,0

Oval playerx-distance#,playery-distance#,distance#*2,distance#*2,0

Flip


Until KeyDown(1)
End

Function enemynew.enemy(x,y,vmax#,amax#)

e.enemy=New enemy

e\x#=x
e\y#=y

e\vmax#=vmax#
e\amax#=amax#

Return e

End Function

Function enemyupdate()

For e.enemy=Each enemy

dx#=(playerx-e\x)
dy#=(playery-e\y)

l#=Sqr(dx#*dx#+dy#*dy#)

dx#=(dx#/l#)*e\amax#
dy#=(dy#/l#)*e\amax#

;if close enough escape target
If l#<=distance#
dx#=-dx#
dy#=-dy#
EndIf

;check against all other enemies, to avoid them
dxx#=0
dyy#=0
co=0
For ee.enemy=Each enemy
	
	If ee<>e
		
		dex#=(e\x-ee\x)
		dey#=(e\y-ee\y)
		
		l#=Sqr(dex#*dex#+dey#*dey#)
		
		dxx#=dxx#+(dex#/l#)*e\amax#
		dyy#=dyy#+(dey#/l#)*e\amax#
		
		co=co+1
	EndIf
Next

dxx#=dxx#/Float(co)
dyy#=dyy#/Float(co)

dx#=(dx#+dxx#)/2
dy#=(dy#+dyy#)/2

e\ax#=e\ax#+dx#
e\ay#=e\ay#+dy#

acc#=Sqr(e\ax#*e\ax#+e\ay#*e\ay#)

;Check if current acceleration is more than allowed
If acc#>e\amax#
	
	e\ax#=(e\ax#/acc#)*e\amax
	e\ay#=(e\ay#/acc#)*e\amax
	
EndIf

e\vx#=e\vx#+e\ax#
e\vy#=e\vy#+e\ay#

vel#=Sqr(e\vx#*e\vx#+e\vy#*e\vy#)

;Check if current velocity is more than allowed
If vel#>e\vmax#
	e\vx#=(e\vx#/vel#)*e\vmax
	e\vy#=(e\vy#/vel#)*e\vmax
EndIf

; add velocity to position
e\x#=e\x#+e\vx#
e\y#=e\y#+e\vy#

Next
End Function

Function enemydraw()
For e.enemy=Each enemy
	Rect e\x-3,e\y-3,6,6,0
Next
End Function
