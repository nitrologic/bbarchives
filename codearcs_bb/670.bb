; ID: 670
; Author: DarkEagle
; Date: 2003-05-04 06:38:05
; Title: generic bouncing code
; Description: want your objects to bounce? come right in...

For i = 1 To CountCollisions(entity)
	; Calculate bounce: 

	; Get the normal of the surface collided with. 
	Nx# = CollisionNX#(entity, i) 
	Ny# = CollisionNY#(entity, i) 
	Nz# = CollisionNZ#(entity, i) 
	
	; Compute the dot product of the ball's motion vector and the normal of the surface collided with. 
	VdotN# = xvel#*Nx# + yvel#*Ny# + zvel#*Nz# 
	
	; Calculate the normal force. 
	NFx# = -2.0 * Nx# * VdotN# 
	NFy# = -2.0 * Ny# * VdotN# 
	NFz# = -2.0 * Nz# * VdotN# 
	
	; Add the normal force to the motion vector. 
	xvel#=(xvel# + NFx#)*bounce#
	yvel#=(yvel# + NFy#)*bounce#
	zvel#=(zvel# + NFz#)*bounce#
Next
