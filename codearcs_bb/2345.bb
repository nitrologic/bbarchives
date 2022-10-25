; ID: 2345
; Author: Pongo
; Date: 2008-10-25 02:30:52
; Title: simple object viewer
; Description: simple object viewer

Graphics3D 800,600,0,2

;create the camera rig
Global mxs,mys	;for saving mouse x and y speed
;Global current_pick	;for saving current picked object						
Global campiv_h=CreatePivot()			;This is the master camera pivot,... it controls position and horizontal rotation
Global campiv_v=CreatePivot(campiv_h)	;This controls vertical rotation
Global cam=CreateCamera(campiv_v)

Global hor_rot# = -45 					;Set horizontal orbit
Global ver_rot# = 20					;set Vert orbit

MoveEntity cam,0,0,-5					;move the camera back a bit
RotateEntity campiv_h,0,hor_rot,0		
RotateEntity campiv_v,ver_rot,0,0

;hide windows cursor and create a crosshair cursor
HidePointer()
Global cursor=CreateImage(16,16)
SetBuffer ImageBuffer(cursor)
Line 1,7,15,7
Line 1,9,15,9
Line 7,1,7,15
Line 9,1,9,15

SetBuffer BackBuffer() 

;###############
; create an axis icon 
Global axis_center = CreatePivot()

axis1 = CreateCube(axis_center)
ScaleEntity axis1,.005,.005,.5
EntityColor axis1,0,0,255 

axis2 = CreateCube(axis_center)
ScaleEntity axis2,.005,.5,.005
EntityColor axis2,0,255,0

axis3 = CreateCube(axis_center)
ScaleEntity axis3,.5,.005,.005
EntityColor axis3,255,0,0
;###############


;create some object in the scene
cube = CreateCube()
ScaleEntity cube,.25,.25,.25
MoveEntity cube,-1,0,0
sphere = CreateSphere()
ScaleEntity sphere,.25,.25,.25
MoveEntity sphere,1,0,0

;light!
lightpivot = CreatePivot()
light=CreateLight(2,lightpivot)
MoveEntity light,500,800,0

;********************************************************************************
;Main loop

While Not KeyDown(1)
	
	mxs=MouseXSpeed() ; store mouse x and y speeds
	mys=MouseYSpeed()

	If MouseDown(1) Then orbit_cam() 	;if alt and middle mouse button pressed,... do orbit
	If MouseDown(3) Then Pan_cam()		;if middle mouse button pressed,... do pan
						
	MoveEntity cam,0,0,MouseZSpeed() ; move the camera back/forth based on mouse wheel

	WireFrame  KeyDown(57)

	UpdateWorld()
	RenderWorld()
		
	Text 10,10,"X: " + EntityX(campiv_h)+"   Y: " + EntityY(campiv_h)+"   Z: " + EntityZ(campiv_h)
	Text 10,25,"mouse button to orbit,... middle button to pan/zoom,...space for wireframe"

	DrawImage cursor,MouseX(),MouseY()
	
	Flip

Wend ; end main loop

End ; end program

;********************************************************************************
Function orbit_cam()
	hor_rot=(hor_rot - (mxs*.5) ) ; get the mouse x and y speed,... adjust sensitivity by half
	ver_rot=(ver_rot + (mys*.5) ) 
	RotateEntity campiv_h,0,hor_rot,0 ; update the new camera pivot rotations
	RotateEntity campiv_v,ver_rot,0,0	
End Function

Function pan_cam()
	MoveEntity campiv_h,-mxs*.01,0,mys*.01
	PositionEntity axis_center,EntityX(campiv_h),EntityY(campiv_h),EntityZ(campiv_h) ;position the axis at the camera pivot
End Function
