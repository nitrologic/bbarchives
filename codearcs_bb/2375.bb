; ID: 2375
; Author: Nate the Great
; Date: 2008-12-19 23:49:39
; Title: camera control in 3rd person
; Description: controls camera movement it 3rd person

Function Updatecamera(cam1,follow,mindist#,maxdist#)  ;This function updates the camera

PointEntity cam1,follow  ;You may need to edit this out depending on your program

x# = EntityX(cam1)
y# = EntityY(cam1)
z# = EntityZ(cam1)

x1# = EntityX(follow)
y1# = EntityY(follow)+10 ;the plus 10 is optional so your camera stays somewhat above your character... change if needed or remove completely
z1# = EntityZ(follow)

dx# = x#-x1#
dy# = y#-y1#
dz# = z#-z1#

dist# = Sqr((dx#*dx#) + (dy#*dy#) + (dz#*dz#))	;distance formula 3d (the reason I 'reinvented the wheel' here is because I needed the x y and z differences for later anyway so it is more efficient to use the variables twice than to have the computer do it for you)

If dist# > maxdist# Then
	fct# = maxdist#/dist#
	dx# = dx#*fct#
	dy# = dy#*fct#
	dz# = dz#*fct#
ElseIf dist# < mindist# Then
	fct# = mindist#/dist#
	dx# = dx#*fct#
	dy# = dy#*fct#
	dz# = dz#*fct#
EndIf

PositionEntity cam1, x1#+dx#,y1#+dy#,z1#+dz#		;This positions the camera where it needs to go.

End Function
