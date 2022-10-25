; ID: 2622
; Author: Krischan
; Date: 2009-12-03 10:03:08
; Title: Homekeeping
; Description: Keeps the player in a given distance while the world is moving

Global localx#,localy#,localz#
Global globalx#,globaly#,globalz#
Global simx#,simy#,simz#

; keeps the player in a given distance while the world can move far away
Function HomeKeeping(player%,world%,homesize%=100)
	
	; store local player position
	localx=EntityX(player)
	localy=EntityY(player)
	localz=EntityZ(player)
	
	; check X axis
	While localx>homesize
		globalx=globalx+homesize
		localx=localx-homesize
		PositionEntity player,localx,localy,localz
		MoveEntity world,-homesize,0,0
	Wend
	While localx<-homesize
		globalx=globalx-homesize
		localx=localx+homesize
		PositionEntity player,localx,localy,localz
		MoveEntity world,homesize,0,0
	Wend
	
	; check Y axis
	While localy>homesize
		globaly=globaly+homesize
		localy=localy-homesize
		PositionEntity player,localx,localy,localz
		MoveEntity world,0,-homesize,0
	Wend
	While localy<-homesize
		globaly=globaly-homesize
		localy=localy+homesize
		PositionEntity player,localx,localy,localz
		MoveEntity world,0,homesize,0
	Wend
	
	; check Z axis
	While localz>homesize
		globalz=globalz+homesize
		localz=localz-homesize
		PositionEntity player,localx,localy,localz
		MoveEntity world,0,0,-homesize
	Wend
	While localz<-homesize
		globalz=globalz-homesize
		localz=localz+homesize
		PositionEntity player,localx,localy,localz
		MoveEntity world,0,0,homesize
	Wend
	
	; store simulated player position
	simx=localx+globalx
	simy=localy+globaly
	simz=localz+globalz
	
End Function
