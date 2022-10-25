; ID: 792
; Author: simonh
; Date: 2003-09-09 16:36:30
; Title: Sphere-Box Intersection Routine
; Description: Tests to see whether a solid sphere intersects a solid axis-aligned box

; Sphere-Box Intersection Routine

; Blitz version by si@si-design.co.uk, based on code found at http://www.acm.org/tog/GraphicsGems/gems/BoxSphere.c
; See also http://www.realtimerendering.com/int/ and http://www.magic-software.com/Intersection.html for some more (non-Blitz flavoured) intersection routines

; This routine is a fast, accurate way of testing to see whether a solid sphere intersects a solid axis-aligned box.
; Useful to act as an activation zone in games - i.e. if sphere is in invisible zone then activate event.

; Use mouse to rotate camera, left and right mouse buttons to move camera forward/backward
; Move sphere by using cursor keys and r/f for rise/fall
; When the sphere touches the box, a 'collision' message should appear in the top-left hand corner of the screen

; Initialise
width=640
height=480

Graphics3D width,height,0,2
SetBuffer BackBuffer()

MoveMouse width/2,height/2

cam=CreateCamera()
PositionEntity cam,0,100,-100
RotateEntity cam,30,0,0

light=CreateLight()

; Set dimensions for box/sphere, then create them using these dimensions
bx#=0 ; box x
by#=0 ; box y
bz#=0 ; box z
bw#=50 ; box width
bh#=50 ; box height
bd#=50 ; box depth
sx#=0 ; sphere x
sy#=0 ; sphere y
sz#=0 ; sphere z
sr#=5 ; sphere radius

box=CreateCube()
EntityColor box,255,255,0
FitMesh box,0,0,0,bw,bd,bh
PositionEntity box,bx,by,bz

sphere=CreateSphere()
EntityColor sphere,255,0,0
ScaleEntity sphere,sr,sr,sr

PositionEntity sphere,sx#,sy#,sz#

While Not KeyDown(1)

	; Move camera
	mxs=mxs+MouseXSpeed()
	mys=mys+MouseYSpeed()

	RotateEntity cam,mys,-mxs,0
	MoveEntity cam,0,0,MouseDown(1)-MouseDown(2)

	; Move sphere
	If KeyDown(203) Then sx#=sx#-1
	If KeyDown(205) Then sx#=sx#+1
	If KeyDown(19) Then sy#=sy#+1
	If KeyDown(33) Then sy#=sy#-1
	If KeyDown(200) Then sz#=sz#+1
	If KeyDown(208) Then sz#=sz#-1

	sx#=sx#+mx#
	sy#=sy#+my#
	sz#=sz#+mz#

	PositionEntity sphere,sx#,sy#,sz#

	RenderWorld
	
	; Test to see if sphere intersects box
	If SphereBoxIntersection(sx#,sy#,sz#,sr#,bx#,by#,bz#,bw#,bh#,bd#)=True Then Text 0,0,"Collision"
	
	Flip

Wend


Function SphereBoxIntersection(sx#,sy#,sz#,sr#,bx#,by#,bz#,bw#,bh#,bd#)

	; sx#,sy#,sz# = sphere x,y,z centre co-ordinates
	; sr# = sphere radius
	; bx#,by#,bz# = box x,y,z corner co-ordinates
	; bw#,bh#,bd# = box width,height,depth

	Local dmin#=0
	Local sr2#=sr*sr
	
	; x axis
	If sx < bx

		dmin=dmin+((sx-bx)*(sx-bx))
		
	Else If sx>(bx+bw)

		dmin=dmin+(((sx-(bx+bw)))*((sx-(bx+bw))))

	EndIf

	; y axis
	If sy < by

		dmin=dmin+((sy-by)*(sy-by))
		
	Else If sy>(by+bh)

		dmin=dmin+(((sy-(by+bh)))*((sy-(by+bh))))

	EndIf

	; z axis
	If sz < bz

		dmin=dmin+((sz-bz)*(sz-bz))

	Else If sz>(bz+bd)

		dmin=dmin+(((sz-(bz+bd)))*((sz-(bz+bd))))

	EndIf

	If dmin#<=sr2# Then Return True Else Return False

End Function
