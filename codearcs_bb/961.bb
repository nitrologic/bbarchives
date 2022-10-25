; ID: 961
; Author: Klaas
; Date: 2004-03-10 07:29:56
; Title: Weather effects
; Description: constant particle density snow

Graphics3D 800,600,32,2
SetBuffer(BackBuffer())

;------ camera stuff
Global ML_cam_pivot = CreatePivot()
Global ML_cam_pivot2 = CreatePivot(ML_cam_pivot)
Global ML_camera = CreateCamera(ML_cam_pivot2)

;cameracone for the topview
cone = CreateCone(12,1,ml_camera)
EntityColor(cone,255,0,0)
TurnEntity cone,-90,0,0

Global x_speed#,y_speed#,z_speed#

;------ load the snowflake
extractdata("snow.jpg")
Global snow = LoadSprite("snow.jpg",2)
ScaleSprite(snow,0.5,0.5)
DeleteFile("snow.jpg")
HideEntity(snow)

;----- the particle type
Type snow
	Field entity
	Field x#,y#,z#
	Field vx#,vy#,vz#
End Type

;topview camera
topview = CreateCamera(ml_cam_pivot)
CameraViewport topview,10,10,320,240
CameraProjMode(topview,2)
CameraZoom(topview,0.02)
MoveEntity(topview,0,10,10)
TurnEntity(topview,90,0,0)

;a plane
plane = CreatePlane()
MoveEntity(plane,0,-8,0)
EntityColor(plane,30,30,30)

;----- the dimension of the weather box
Global weather_x# = 25
Global weather_y# = 20
Global weather_z# = 40

;----- particles must only be created once on startup ... they will be recycled
createSnow(200)

;main loop
While Not KeyHit(1)
	mousemove()
	mouselook()
	doSnow()

	RenderWorld()
	
	;border for topview
	Color 255,255,255
	Rect 10,10,320,240,0
	
	Flip()
Wend
End

Function doSnow()

	For s.snow = Each snow
		
		;this is just the tumbling of the flakes ...
		;all movements should be applied To the virtual position
		s\vx = s\vx + Rnd(-0.01,0.01)
		If s\vx > 0.051
			s\vx = 0.051
		ElseIf s\vx < -0.051
			s\vx = - 0.051
		EndIf
		
		s\vz = s\vz + Rnd(-0.01,0.01)
		If s\vz > 0.051
			s\vz = 0.051
		ElseIf s\vz < -0.051
			s\vz = - 0.051
		EndIf
		
		;---- now lets correct the position of the flake 
		PositionEntity s\entity,s\x,s\y,s\z,1
		
		;---- if the flake leaves the weatherbox ... move them to the other side of the box
		If EntityX(s\entity) < -weather_x
			TranslateEntity s\entity,weather_x*2,0,0
		ElseIf EntityX(s\entity) > weather_x
			TranslateEntity s\entity,-weather_x*2,0,0
		EndIf

		If EntityY(s\entity) < -weather_y
			TranslateEntity s\entity,0,weather_y*2,0
		ElseIf EntityY(s\entity) > weather_y
			TranslateEntity s\entity,0,-weather_y*2,0
		EndIf

		If EntityZ(s\entity) < 0
			TranslateEntity s\entity,0,0,weather_z
		ElseIf EntityZ(s\entity) > weather_z
			TranslateEntity s\entity,0,0,-weather_z
		EndIf
		
		;---- now correct the virtual position of the flake and aplly the movements
		s\x = EntityX(s\entity,1) + s\vx
		s\y = EntityY(s\entity,1) - 0.051
		s\z = EntityZ(s\entity,1) + s\vz
	Next
End Function

Function createSnow(amount)
	For i=1 To amount
		s.snow = New snow
		
		;the snow must be parented to the camera
		s\entity = CopyEntity(snow,ML_camera)
		
		s\x = Rnd(-weather_x,weather_x)
		s\y = Rnd(-weather_y,weather_y)
		s\z = Rnd(0,weather_z)
		PositionEntity(s\entity,s\x,s\y,s\z)
		EntityAutoFade(s\entity,weather_z/2,weather_z)
		RotateSprite(s\entity,Rnd(0,360))
	Next
End Function

;----------------- navigation
Function mouselook()
	x_speed# = x_speed * 0.3 + MouseXSpeed() * 0.3 
	y_speed# = y_speed * 0.3 + MouseYSpeed() * 0.3 
	z_speed# = z_speed * 0.3 + MouseZSpeed() * 0.3

	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
		
	TurnEntity ML_cam_pivot,0,-x_speed,0	;turn player left/right
	TurnEntity ML_cam_pivot2,y_speed,0,0	;tilt camera
End Function

Function mousemove()
	If KeyDown(200) ;forward
		MoveEntity ML_cam_pivot,0,0,0.5
	End If
	If KeyDown(208) ;backward
		MoveEntity ML_cam_pivot,0,0,-0.5
	End If
	If KeyDown(203) ;left
		MoveEntity ML_cam_pivot,-0.5,0,0
	End If
	If KeyDown(205) ;right
		MoveEntity ML_cam_pivot,0.5,0,0
	End If
	If KeyDown(30) ;UP
		MoveEntity ML_cam_pivot,0,0.5,0
	End If
	If KeyDown(44) ;DOWN
		MoveEntity ML_cam_pivot,0,-0.5,0
	End If
End Function

;--------- the data for the snowflake
Function extractdata(filename$)
	file = WriteFile(filename)
	d = True
	While d > -1
		Read d
		WriteByte(file,d)
	Wend
	CloseFile(file)
End Function

.snowData
Data 255,216,255,224,0,16,74,70,73,70,0,1,2,0,0,100,0,100,0,0,255

Data 236,0,17,68,117,99,107,121,0,1,0,4,0,0,0,50,0,0,255,238,0,33
Data 65,100,111,98,101,0,100,192,0,0,0,1,3,0,16,3,3,6,9,0,0,1,235
Data 0,0,2,53,0,0,2,140,255,219,0,132,0,8,6,6,6,6,6,8,6,6,8,12,8
Data 7,8,12,14,10,8,8,10,14,16,13,13,14,13,13,16,17,12,14,13,13
Data 14,12,17,15,18,19,20,19,18,15,24,24,26,26,24,24,35,34,34,34
Data 35,39,39,39,39,39,39,39,39,39,39,1,9,8,8,9,10,9,11,9,9,11,14
Data 11,13,11,14,17,14,14,14,14,17,19,13,13,14,13,13,19,24,17,15
Data 15,15,15,17,24,22,23,20,20,20,23,22,26,26,24,24,26,26,33,33
Data 32,33,33,39,39,39,39,39,39,39,39,39,39,255,194,0,17,8,0,32
Data 0,32,3,1,34,0,2,17,1,3,17,1,255,196,0,141,0,1,0,3,1,0,0,0,0
Data 0,0,0,0,0,0,0,0,8,4,6,7,5,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,16,0,2,2,2,3,1,0,0,0,0,0,0,0,0,0,0,3,4,1,2,5,6,0,32,17,18
Data 17,0,2,2,2,2,3,1,0,0,0,0,0,0,0,0,0,0,1,17,2,18,3,33,49,65,81
Data 97,34,18,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,32,19,1,1,1,1,0,3
Data 1,0,0,0,0,0,0,0,0,0,1,17,0,33,32,49,65,97,255,218,0,12,3,1
Data 0,2,17,3,17,0,0,0,194,111,18,17,65,206,130,214,47,28,37,88
Data 214,244,111,199,168,149,99,255,218,0,8,1,2,0,1,5,1,233,255
Data 218,0,8,1,3,0,1,5,1,233,255,218,0,8,1,1,0,1,5,1,10,229,61,177
Data 250,237,152,171,122,193,71,7,5,215,190,164,64,85,165,214,23
Data 133,24,190,54,129,64,220,195,22,4,240,93,164,137,220,152,151
Data 30,125,218,56,90,218,107,40,236,37,90,175,230,8,228,204,204
Data 243,255,218,0,8,1,2,2,6,63,1,31,255,218,0,8,1,3,2,6,63,1,31
Data 255,218,0,8,1,1,1,6,63,1,199,85,114,102,91,83,79,209,58,211
Data 29,46,161,142,187,123,125,18,171,8,106,11,66,225,154,219,234
Data 74,226,252,13,221,153,208,149,217,141,255,0,71,195,147,255
Data 218,0,8,1,2,3,1,63,16,240,255,218,0,8,1,3,3,1,63,16,240,255
Data 218,0,8,1,1,3,1,63,16,178,30,196,59,157,254,12,153,115,192
Data 105,210,31,184,165,17,149,162,153,59,132,130,243,57,65,20,153
Data 215,144,174,25,39,143,88,130,217,234,226,169,123,220,77,224
Data -1
