; ID: 478
; Author: col
; Date: 2002-11-04 14:06:40
; Title: Project mouse 2D to Orthoganol 3D
; Description: Converts the mouse 2D position into an Orthoganol 3D position

Graphics3D 800,600,32,2

;Set up a camera
cz#=0.1
Global cam=CreateCamera()
CameraViewport cam,100,100,600,400
CameraClsColor cam,100,100,100
CameraProjMode cam,2
CameraZoom cam,cz

;We want to look down the Z Axis:
PositionEntity cam,0,0,-500 ; position as far back so as to see all objects
                            ; could use camera furthest distance parameter

;An object to show
s=CreateSphere()

;Max light
AmbientLight 255,255,255

;Wireframe
WireFrame True

While Not KeyDown(1)
	Cls
	
	;Adjust the camera zoom
        cz=cz+(KeyDown(78)*0.01)-(KeyDown(74)*0.01)
	CameraZoom cam,cz

	;move camera using arrow keys
	TranslateEntity cam,(KeyDown(205)-KeyDown(203))*0.10,(KeyDown(200)-KeyDown(208))*0.1,0	
	
	RenderWorld
	
	Project2DTo3DOrtho( MouseX() , MouseY() )

	Flip
Wend
End

Function Project2DTo3DOrtho(mx,my)
	;Check the mouse is in the 3d window
	If mx>=100 And mx<=700 And my=>100 And my<=500
	
		;Get mouse position in relation to the 3d viewport
		vx=mx-100
		vy=my-100
		
		;Get the 2d position of the origin
		CameraProject cam,0,0,0
		x#=ProjectedX()
		y#=ProjectedY()
				
		;Get the 2d position of 1,1,1
		CameraProject cam,1,1,1
		x1#=ProjectedX()
		y1#=ProjectedY()

		;Take one from the other to find the distance
		dx#=x1-x
		dy#=y1-y

		;World position is :
		wpx#=(vx-x)/dx
		wpy#=(vy-y)/dy
                
                ;Looking down the Z Axis
		Text 0,0,"Cursor is at World position X : "+wpx
		Text 0,12,"                            Y : "+wpy

	EndIf
End Function
