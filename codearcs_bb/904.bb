; ID: 904
; Author: Techlord
; Date: 2004-02-03 12:41:53
; Title: Project PLASMA FPS 2004: Camera.bb
; Description: Standard Mouselook Camera Code Module

;Control
Global camera.camera

Type camera
	Field entity%
	Field x#
	Field z#
	Field pitch#
	Field yaw#
	Field speed#	; Current
	Field destx#
	Field destz#
	Field destpitch#
	Field destyaw#	; Destination
	Field keymap%[10]
End Type 

Function cameraCreate.camera()
	HidePointer()
	this.camera=New camera
	this\entity=CreateCamera()
	this\speed#=.3
	this\speed#=.3
	CreateListener(this\entity%)
	camerakeymap(this,6)
	Return this
End Function

Function cameraUpdate()
	For this.camera =  Each camera
		cameraControl(this)
	Next
End Function

Function cameraControl(this.camera)
	; Mouse look
	; ----------

	CameraClsColor(camera\entity%,255,127,63)	
			
	; Mouse x and y speed
	mxs#=MouseXSpeed()
	mys#=MouseYSpeed()
	
	; Mouse shake (total mouse movement)
	mouse_shake=Abs(((mxs#+mys#)/2)/1000.0)

	; Destination camera angle x and y values
	this\destyaw#=this\destyaw#-mxs#
	this\destpitch#=this\destpitch#+mys#

	; Current camera angle x and y values
	this\yaw#=this\yaw#+((this\destyaw#-this\yaw#)/5)
	this\pitch#=this\pitch#+((this\destpitch#-this\pitch#)/5)
	
	RotateEntity this\entity%,this\pitch#,this\yaw#,0
	;RotateEntity this\entity%mxs#,mys#,0
		
	; Rest mouse position to centre of screen
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2

	If KeyDown(this\keymap[1]) x#=this\speed#*-1.
	If KeyDown(this\keymap[2]) x#=this\speed#
	If KeyDown(this\keymap[3]) z#=this\speed#*-1.
	If KeyDown(this\keymap[4]) z#=this\speed#
	If KeyDown(this\keymap[5]) y#=this\speed#*-1.
	If KeyDown(this\keymap[6]) y#=this\speed#
	
	; Move camera using movement values
	MoveEntity this\entity%,x#,y#,z#
		
End Function

Function camerakeymap(this.camera,keys%)
	Restore camerakeymapdata
	For loop = 1 To keys%
		Read key%
		this\keymap[loop]=key%
	Next	
End Function

.camerakeymapdata
;    lft rgt rwd fwd up dwn       
;    a   d   s   w  q  z
Data 30,32,31,17,44,16
