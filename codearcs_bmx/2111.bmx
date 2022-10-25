; ID: 2111
; Author: D4NM4N
; Date: 2007-09-23 17:24:05
; Title: MINIB3D Mouselook workaround for linux
; Description: A dimple mouselook workaround for linux BMAX

Type T_MouseLook
	Field resetX%
	Field resetY%
	Field speedX%  'use these if using turnentity
	Field speedY%  
        Field pitch:float  'use these if using rotateentity
        Field yaw:float

	Method Init(rX% , rY%)
		resetX = rx
		resetY = ry
	End Method
	
	Method Pollmouse()
		speedX = resetX - MouseX()
		speedY = resetY - MouseY()
              	yaw :- Float(-speedX) / 15
         	pitch :+ Float(-speedY) / 15
		MoveMouse(resetX,resetY)	
	End Method
	
	Method flush()
		speedX = 0
		speedY = 0
		MoveMouse(resetX,resetY)
	End Method	
End Type

'Initialise mouselook
local mlook:T_MouseLook=New t_mouselook
mlook.init(512,350) ' or width & height*.5 etc
mlook.flush() 'kills any initial movements

While Not KeyDown(KEY_ESCAPE)		

	;Poll mouse and adjust rotation values.
	mlook.pollmouse()
	RotateEntity cam, mlook.pitch, mlook.yaw, 0
        (......rest of main loop)
