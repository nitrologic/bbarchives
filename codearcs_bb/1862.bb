; ID: 1862
; Author: Nebula
; Date: 2006-11-15 07:55:46
; Title: 3D Engine (Clay3D)
; Description: Copy Paste Play

; Standalone source code - 
; - procedural textures
; - GLaced Lighting not included. 
;
; Blitz Basic 3D . www.bitzbasic.com
; Kid Clay Heaven - The adventures of.
;
;
; press console key to edit the map - use right mouse button
; press console key to edit the map - use left mouse button
; bottom two colors are the left and mouse edit colors
; 
; (Public domain)

Global lset1 = 100
Global lset2 = 160
Global lset3 = 160
Global lset4 = 160

Graphics3D 640,480,16,2
SetBuffer BackBuffer()
HidePointer()

Global textur1 = CreateImage(64,64)
Global textur2 = CreateImage(64,64)
savetexture1
savetexture2
saveheightmap()
; == Setup ==


; -- Create a light;
;RotateEntity CreateLight (), 45.0, 45.0, 0.0, True
;^^^^^^

; -- Create user entity.
Global user = CreatePivot()
PositionEntity user, 0.0, USER_CENTERPOINT_HEIGHT#, 0.0, True
EntityRadius user, USER_CENTERPOINT_HEIGHT#
EntityType user, COLLTYPE_user
;^^^^^^

; -- Create user's camera.
Global user_camera = CreateCamera( user )
CameraRange user_camera, 0.5, 1000.0
CameraClsColor user_camera, 0.0, 106.0, 213.0
CameraZoom user_camera, 1.6 ; Set the camera focus to the correct value for the human eye.
PositionEntity user_camera, 0.0, USER_CAMERA_HEIGHT#, 0.0, True
;^^^^^^

;

; == Declarations ==

;
; Crom said - 
;
; The jaw is the part of the face/screen that moves up and down. The 
; jaw on your face. 
; The pitch is probably the nose/screen that moves left und right. mkay
;

; -- World.
;Global gravity# = 9.8
;^^^^^^

; -- Viewport.
Global viewport_center_x = GraphicsWidth () / 2
Global viewport_center_y = GraphicsHeight () / 2
;^^^^^^


; -- Mouselook.
Global mouselook_x_inc# = 0.4 ; This sets both the sensitivity and direction (+/-) of the mouse on the X axis.
Global mouselook_y_inc# = 0.4 ; This sets both the sensitivity and direction (+/-) of the mouse on the Y axis.
Global mouse_left_limit = 250 ; Used to limit the mouse movement to within a certain number of pixels (250 is used here) from the center of the screen. This produces smoother mouse movement than continuously moving the mouse back to the center each loop.
Global mouse_right_limit = GraphicsWidth () - 250 ; As above.
Global mouse_top_limit = 250 ; As above.
Global mouse_bottom_limit = GraphicsHeight () - 250 ; As above.
;^^^^^^

; -- Mouse smoothing que.
Global mouse_x_speed_1#
Global mouse_x_speed_2#
Global mouse_x_speed_3#
Global mouse_x_speed_4#
Global mouse_x_speed_5#
Global mouse_y_speed_1#
Global mouse_y_speed_2#
Global mouse_y_speed_3#
Global mouse_y_speed_4#
Global mouse_y_speed_5#
;^^^^^^

; -- User.
Global user_movement_speed# = 4.0 ; The user's movement speed. This produces a movement speed of 4 metres per second.
Global user_camera_pitch#
Const USER_CENTERPOINT_HEIGHT# = 0.94
Const USER_CAMERA_HEIGHT# = USER_CENTERPOINT_HEIGHT# * 2.0 - 0.1
;^^^^^^

; -- Timing.
Global milli_secs ; Holds the value of the Millisecs() timer. Set at the start of the main loop.
Global old_time ; This must be set to the current 'MilliSecs()' time at the start of a new game and when returning from a pause.
Global game_time ; This holds a relative game time value which is used with timeouts so that they can be paused.
Global Delta_Time# ; Use this as a multiplier for all continuous game world events, to regulate game speed.
;^^^^^^

; -- Collision.
Const COLLTYPE_geometry = 1
Const COLLTYPE_user = 2
;^^^^^^



; == Main Code ==


; -- Initialize the loop.
old_time = MilliSecs ()
;Collisions COLLTYPE_user, COLLTYPE_geometry, 2, 2
;^^^^^^



Global myfps,myfpscounter,myfpstimer

;
Global rmpixelcolor = 114
Global lmpixelcolor = 195
Global pxedcnt = 0
;
Dim pixelmap1(32,32)
Dim pixelmap2(32,32)
Dim light(8)
Dim lpiv(8)
;
;

;
CopyFile "heightmap2.bmp","bufferheightmap.bmp"

;

;
Global plaf = CreateCube()
;
;2d
Global mouse = CreateImage(16,16)
readmouse() ; load the mouse for the Console Sprite editor
;
;Include "cam_module_0.1.bb"
;Include "console_module.bb"

Global grasstex
Global dirt1tex
Global ter1
Global ter2
Global movietrailer = CreateImage(GraphicsWidth(),GraphicsHeight())
Global makemorrowind = CreateImage(32,32)
Global ii# ; 
Global editthemapsir
Global curx = 0
Global cury = 0

grasstex = LoadTexture("dirt2.bmp")
dirt1tex = LoadTexture("dirt1.bmp")
editthemapsir = LoadImage("editbuffermap.bmp")
;
;

;
updateheightmap()

ms = MilliSecs()

resetlevel()

Local wrl# = 0.001


While KeyDown(1) = False
	Cls
	RenderWorld()
	CalculateDeltatimeAndGametime()
	mouselook
	moveuser:moveuser:moveuser
	;
	If KeyHit(60) = True Then lset3=lset3+10:freelights:updateheightmap
	;If KeyHit(62) = True Then 
	;
	If KeyHit(201) = True Then PositionEntity user,EntityX(user),EntityY(user)+15,EntityZ(user)
	If KeyHit(209) = True Then PositionEntity user,EntityX(user),EntityY(user)-15,EntityZ(user)
	;	
	;
	If KeyDown(41) = True Then paintconsole
	If KeyDown(59) = True Then makeabumpyheightmap
	If KeyDown(56) = True Then updateheightmap();f1
	;		
	If ii>20 Then resetlevel() ; if your dead then reset			
	;	
	If KeyHit(12) = True Then wrl = -0.001
	If KeyHit(13) = True Then wrl = 0.001
	If ii > .4 And ii < 18 Then  
		ii = ii + wrl ; raise water/ice/fog
		If ii < .4 Then ii = 3.9 : wrl = 0.001
		If ii > 18 Then ii = 17.9 : wrl = -0.001
	End If
	PositionEntity ter2,0,2+ii,0
	;
			
	;	
	UpdateWorld()
	Text 150,0,"Page Up / Page Down"
	Text 400,0,"Press Tilde"
	;DebugLog TrisRendered()
	;
	If KeyHit(203)=True Then curx = curx - 1
	If KeyHit(205)=True Then curx = curx + 1
	If KeyHit(200)=True Then cury = cury - 1
	If KeyHit(208)=True Then cury = cury + 1
	;
	Color 255,255,255
	Text 150,GraphicsHeight()-50," Tris : " + TrisRendered() + " - Fps : " + myfps
		;
	fpscounter = fpscounter + 1
	If fpstimer < MilliSecs()
		;
		fpstimer = MilliSecs() + 1000
		myfps = fpscounter
		fpscounter = 0
		;
	End If
	;
	Flip
Wend
End


FreeTexture grasstex 
FreeTexture dirt1tex 
FreeTexture textur1
FreeTexture textur2

Function resetlevel()
	PositionEntity user,132,24,65
	MoveMouse 0,0
	ii#=0.5
End Function


Function updateheightmap(load$ = "heightmap2.bmp") ; reload it with another ( press doorbutton)
	FreeEntity ter1
	ter1 = LoadTerrain(load$)
	ScaleEntity ter1,10,50,10

	EntityTexture ter1,dirt1tex
	PositionEntity ter1,0,-20,0
	FreeEntity ter2
	ter2 = CreateTerrain(32)

	EntityTexture ter2,grasstex
	PositionEntity ter2,0,20,0
	ScaleEntity ter2,10,50,10

	ms = MilliSecs() - ms

	turnlights(lset1,lset2,lset3,lset4)		

	;
	EntityTexture plaf,dirt1tex
	ScaleEntity plaf,170,2,170
	PositionEntity plaf,140,32,140
	UpdateNormals plaf
End Function

Function freelights()
For i=0 To 7
FreeEntity light(i) = CreateLight(2)
FreeEntity lpiv(i)
Next
End Function

Function turnlights(a,b,c,d)
	AmbientLight 10,21,21
	For i=0 To 3
		light(i) = CreateLight(2)
		lpiv(i) = CreatePivot()
		LightRange light(i),a;200
		LightColor light(i),b,c,d;20,20,20
	Next
	PositionEntity light(0),0	,20,0
	PositionEntity light(1),300	,20,0
	PositionEntity light(2),0	,20,300
	PositionEntity light(3),300	,20,300
;	PositionEntity light(4),0	,20,0
;	PositionEntity light(5),150	,20,0
;	PositionEntity light(6),0	,20,150
;	PositionEntity light(7),150	,20,150

	For i=0 To 3
		PositionEntity lpiv(i),EntityX(light(i)),0,EntityZ(light(i))
	Next

	For i=0 To 3
		PointEntity light(i),lpiv(i)
	Next	
	UpdateNormals plaf
End Function

Function spritemapeditor(load$="heightmap2.bmp")	
	;
	For x=0 To 31
	For y=0 To 31
		r = pixelmap1(x,y)
		Color r,r,r
		Rect x*5,y*5,5,5,True
	Next:Next
	;
	;	
	;DrawBlock editthemapsir,300,0		
	;
	;
	Color lmpixelcolor,lmpixelcolor,lmpixelcolor ; left mouse button color
	
	If MouseDown(1) = True Then
		SetBuffer ImageBuffer(editthemapsir)
		Plot MouseX()/5,MouseY()/5
		SetBuffer BackBuffer()
		SaveImage (editthemapsir,"bufferheightmap.bmp")
		loadspritemap("bufferheightmap.bmp")
	End If
	;
	
	Color rmpixelcolor,rmpixelcolor,rmpixelcolor ; right mouse button color
	
	If MouseDown(2) = True Then
		SetBuffer ImageBuffer(editthemapsir)
		Plot MouseX()/5,MouseY()/5
		SetBuffer BackBuffer()
		SaveImage (editthemapsir,"bufferheightmap.bmp")
		loadspritemap("bufferheightmap.bmp")
	End If
	;
	For y=0 To 7
		ca = (y*10)*1.76
		Color ca,ca,ca
		Rect 200,y*14,10,10,True
		ca = (y*10)*(1.76)+(114)
		Color ca,ca,ca
		Rect 220,y*14,10,10,True
		;
		If MouseDown(1) = True Or MouseDown(2) = True
			If RectsOverlap(200,y*14,10,10,MouseX(),MouseY(),1,1) = True 
				If MouseDown(1) = True Then lmpixelcolor = (y*10)*1.76	
				If MouseDown(2) = True Then rmpixelcolor = (y*10)*1.76
				pxedcnt = cnt	
			End If
		If RectsOverlap(220,y*14,10,10,MouseX(),MouseY(),1,1) = True 
			If MouseDown(1) = True Then lmpixelcolor = (y*10)*(1.76)+(114)
			If MouseDown(2) = True Then rmpixelcolor = (y*10)*(1.76)+(114)
			Rect 220,y*14,10,10,True
			pxedcnt = cnt*2
		End If
		End If
		cnt=cnt+1
		; left and right mouse button color (below the color bar in the console)
		Color lmpixelcolor,lmpixelcolor,lmpixelcolor
		Rect 200,120,10,10,True
		Color rmpixelcolor,rmpixelcolor,rmpixelcolor		
		Rect 220,120,10,10,True		
	Next
	;
	Color 50,100,120
	Rect curx*5,cury*5,5,5,False
	;
	imamouse
	;
End Function

Function imamouse()
	DrawImage mouse,MouseX(),MouseY()
End Function

Function readmouse()
	SetBuffer ImageBuffer(mouse)	
	z=255
	For x1=-2 To 2
	For y1=-2 To 2	
	Restore mousy1
	For y=0 To 17
	For x=0 To 15
		Read a
		If a = 1 Then Color z,z,z
		If a = 0 Then Color 0,0,0
		Plot x+x1,y+y1
	Next
	Next
	z=z-40
	Next
	Next
	SetBuffer BackBuffer()
End Function

.mousy1
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,1,1,1,1,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,1,1,1,1,0,0,0,0,0,0,0,0,0
Data 0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0
Data 0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0
Data 0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0
Data 0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0
Data 0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0
Data 0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0
Data 0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,0
Data 0,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0
Data 0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,0
Data 0,0,0,0,0,0,0,0,0,0,1,1,0,0,1,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0

Function makeabumpyheightmap()
	CopyFile "bumpyheightmap.bmp","bufferheightmap.bmp"
	updateheightmap("bufferheightmap.bmp")
	SaveImage editthemapsir,"bufferheightmap.bmp"
End Function
Function savetexture1()
	SetBuffer ImageBuffer(textur1)
	For x=0 To 9
	For y=0 To 9
		;
		Color 255,255,255
		Rect x*10,y*10,10,10,True
		;
	Next:Next
	;
	For x=0 To 9
	For y=0 To 9
		;
		For xx=0 To 10 Step 2
		For yy=0 To 10 Step 2
			Color 150,150,150
			Plot x*10+xx,y*10+xx
		Next:Next
		;
	Next:Next
	;
	;
	For x=0 To 100
	For y=0 To 100
		;
		r=Rand(200,255)
		Color 200,200,r
		If Rand(0,2) = True
			Plot x,y
		End If
		;
	Next:Next	SetBuffer BackBuffer()
	SaveImage(textur1,"dirt1.bmp")
End Function

Function savetexture2()
	SetBuffer ImageBuffer(textur2)
	For x=0 To 9
	For y=0 To 9
		;
		Color 15,155,255
		Rect x*10,y*10,10,10,True
		;
	Next:Next
	;
	For x=0 To 9
	For y=0 To 9
		;
		For xx=0 To 10 Step 2
		For yy=0 To 10 Step 2
			Color 150,150,150
			Plot x*10+xx,y*10+xx
		Next:Next
		;
	Next:Next
	;
	;
	For x=0 To 100
	For y=0 To 100
		;
		r=Rand(230,255)
		Color 160,160,r
		If Rand(0,2) = True
			Plot x,y
		End If
		;
	Next:Next
	SetBuffer BackBuffer()
	SaveImage(textur2,"dirt2.bmp")
End Function























; == Functions ==


Function MouseLook()

	; -- Update the smoothing que to smooth the movement of the mouse.
	mouse_x_speed_5# = mouse_x_speed_4#
	mouse_x_speed_4# = mouse_x_speed_3#
	mouse_x_speed_3# = mouse_x_speed_2#
	mouse_x_speed_2# = mouse_x_speed_1#
	mouse_x_speed_1# = MouseXSpeed ( )
	mouse_y_speed_5# = mouse_y_speed_4#
	mouse_y_speed_4# = mouse_y_speed_3#
	mouse_y_speed_3# = mouse_y_speed_2#
	mouse_y_speed_2# = mouse_y_speed_1#
	mouse_y_speed_1# = MouseYSpeed ( )
	the_yaw# = ( ( mouse_x_speed_1# + mouse_x_speed_2# + mouse_x_speed_3# + mouse_x_speed_4# + mouse_x_speed_5# ) / 5.0 ) * mouselook_x_inc#
	the_pitch# = ( ( mouse_y_speed_1# + mouse_y_speed_2# + mouse_y_speed_3# + mouse_y_speed_4# + mouse_y_speed_5# ) / 5.0 ) * mouselook_y_inc#
	;^^^^^^

	TurnEntity user, 0.0, -the_yaw#, 0.0 ; Turn the user on the Y (yaw) axis.
	user_camera_pitch# = user_camera_pitch# + the_pitch#
	; -- Limit the user's camera to within 180 degrees of pitch rotation. 'EntityPitch()' returns useless values so we need to use a variable to keep track of the camera pitch.
	If user_camera_pitch# > 90.0 Then user_camera_pitch# = 90.0
	If user_camera_pitch# < -90.0 Then user_camera_pitch# = -90.0
	;^^^^^^
	RotateEntity user_camera, user_camera_pitch#, 0.0, 0.0 ; Pitch the user's camera up and down.	
	; -- Limit the mouse's movement. Using this method produces smoother mouselook movement than centering the mouse each loop.
	If ( MouseX() > mouse_right_limit ) Or ( MouseX() < mouse_left_limit ) Or ( MouseY() > mouse_bottom_limit ) Or ( MouseY() < mouse_top_limit )
		MoveMouse viewport_center_x, viewport_center_y
	EndIf
	;^^^^^^

End Function


Function MoveUser()

	If KeyDown( 32 ) Or KeyDown( 205 ) Or KeyDown(52) ; Right. The 'D' and 'CURSOR RIGHT' keys.
		x#= 4.0
	Else If KeyDown( 30 ) Or KeyDown( 203 ) Or KeyDown(51); Left. The 'A' and 'CURSOR LEFT' keys.
		x#=-4.0
	EndIf

	If KeyDown( 33 ) Or KeyDown( 54 ) ; Up. The 'F' and 'RIGHT SHIFT' keys.
		y#= 4.0
	Else If KeyDown( 46 ) Or KeyDown( 157 ) ; Down. The 'C' and 'RIGHT CONTROL' keys.
		y#=-4.0
	EndIf

	If KeyDown( 17 ) Or KeyDown( 200 ) Or MouseDown(2); Forward. The 'W' and 'CURSOR UP' keys.
		z#= 4.0
	Else If KeyDown( 31 ) Or KeyDown( 208 ) Or KeyDown(53); Backward. The 'S' and 'CURSOR DOWN' keys.
		z#=-4.0
	EndIf

	MoveEntity user, x# * user_movement_speed# * Delta_Time#, y# * user_movement_speed# * Delta_Time#, z# * user_movement_speed# * Delta_Time# ; Move the user.

End Function


Function CalculateDeltatimeAndGametime()
; NOTES:
; The variable 'old_time' must be set to the current millisecs time at the start of a new game and when returning from a pause.

	milli_secs = MilliSecs () ; Store the 'MilliSecs ()' time in the 'milli_secs' variable so that you can use the stored time value without calling 'MilliSecs ()' again in this loop.

	the_time_taken = milli_secs - old_time ; Calculate the time the last loop took to execute.
	If the_time_taken > 100 Then the_time_taken = 100 ; This stops disk accesses and the like from causing jumps in position.
	game_time = game_time + the_time_taken ; Calculate the value for the 'game_time' variable used with timeouts, etc.
	Delta_Time# = the_time_taken / 1000.0 ; Calculate the value for the 'Delta_Time#' variable used to regulate game speed.
	old_time = milli_secs ; Update the 'old_time' variable with the current time.

End Function


Function InitializeNewMap()
; NOTES:
 ; This function should only need to be run when a new map is first loaded. It should normally be executed by the map file loader routine.

	; -- Precache the game's 3D graphics media. This forces the media to be uploaded to the video card. This should be done before any of the media is hidden or has its alpha set to zero to turn off rendering. Note that the camera will end up pointing back to where it started, so there is no need to reset its orientation.
	For i = 1 To 4
		TurnEntity user_camera, 0.0, 90.0, 0.0, True
		RenderWorld
	Next
	;^^^^^^
End Function

Function updatecam2()
	CalculateDeltatimeAndGametime()
	mouselook()
	moveuser()
End Function

Function setplayercamspeed(speed#)
	user_movement_speed = speed#
End Function

Function paintconsole()
	Local seen
	seen = CreateImage(GraphicsWidth(),GraphicsHeight())
	GrabImage seen,0,0
	Delay 150
	y1=-200
	While KeyDown(41) = False
		Cls
		DrawBlock seen,0,0
		Color 150,150,150
		Rect 0,0,GraphicsWidth(),200+y1,True
		Flip
		y1=y1+4
		If y1 >0 Then Exit
	Wend
	
	loadspritemap("bufferheightmap.bmp")
	

	Delay(200)
	ShowPointer()
	While KeyDown(41) = False
		Cls
		DrawBlock seen,0,0
		Color 150,150,150
		Rect 0,0,GraphicsWidth(),200,True
		spritemapeditor
		Flip
	Wend
	SaveImage editthemapsir,"bufferheightmap.bmp"
	updateheightmap("bufferheightmap.bmp")
	
	Delay(200)
	MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
End Function




Function loadspritemap(load$="heightmap1.bmp")
	Local abc
	abc = LoadImage(load$)
	SetBuffer ImageBuffer(abc)
	For x=0 To 31
	For y=0 To 31
		GetColor x,y
		r = ColorRed()
		g = ColorGreen()
		b = ColorBlue()
		pixelmap1(x,y) = r
	Next:Next
	SetBuffer BackBuffer()
End Function


Function saveheightmap()
	Restore hmap
	Local myhmap1= CreateImage(32,32)
	Local myhmap2= CreateImage(32,32)
	For x=0 To 31
	For y=0 To 31
		Read a
		Select a
			Case 0:Color 100,100,100
			Case 1:Color 105,105,105
			Case 2:Color 110,110,110
			Case 3:Color 120,120,120
			Case 4:Color 140,140,140
			Case 5:Color 160,160,160
			Case 6:Color 180,180,180
			Case 7:Color 200,200,200
			Case 8:Color 230,230,230
			Case 9:Color 255,255,255
		End Select
		SetBuffer ImageBuffer(myhmap1)
		Plot x,y
		SetBuffer BackBuffer()
		SetBuffer ImageBuffer(myhmap2)
		Color 150,150,150
		Plot x,y
		SetBuffer BackBuffer()
	Next:Next
	
	SaveImage(myhmap1,"editheightmap.bmp")
	SaveImage(myhmap2,"bumpyheightmap.bmp")
	SaveImage(myhmap1,"editbuffermap.bmp")
	SaveImage(myhmap1,"heightmap2.bmp")
End Function

.hmap;generator? WTF Ballz
Data 9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9
Data 9,0,0,9,7,6,6,0,0,0,0,0,0,6,0,0,0,0,6,6,4,4,4,5,6,7,6,6,6,6,6,9
Data 9,8,0,9,7,6,6,0,0,0,0,0,6,6,6,7,0,0,6,2,3,3,3,1,6,6,6,6,6,6,6,9
Data 9,6,6,9,7,6,6,1,0,3,0,6,6,6,6,7,0,0,6,1,2,2,2,1,6,6,6,6,6,6,6,9
Data 9,6,6,9,7,6,6,1,2,2,6,6,6,0,0,0,0,6,6,6,1,1,1,6,6,6,6,6,6,6,6,9
Data 9,9,6,9,6,6,6,1,2,1,6,6,0,0,0,0,0,0,9,9,9,1,9,9,6,6,0,0,7,7,7,9
Data 9,6,6,6,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,0,0,0,8,8,9
Data 9,6,6,6,6,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,0,0,0,0,0,9
Data 9,0,0,0,0,7,8,8,8,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,0,0,0,0,0,9
Data 9,0,0,0,0,7,9,9,9,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,0,0,0,0,0,9
Data 9,3,4,5,5,6,9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,6,0,0,0,0,0,9
Data 9,0,0,0,0,9,9,0,0,0,9,9,9,9,9,9,9,9,9,9,9,9,9,0,6,5,0,0,0,0,0,9
Data 9,9,0,0,0,0,0,0,1,2,3,4,5,6,6,6,7,7,8,8,8,9,9,6,6,6,5,0,0,0,0,9
Data 9,0,9,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,6,6,6,6,0,0,0,0,9
Data 9,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,6,6,6,6,0,0,0,0,9
Data 9,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,3,6,6,6,0,0,0,0,9
Data 9,0,0,0,0,2,3,7,6,5,4,9,9,9,9,9,9,9,9,9,9,9,9,0,0,0,0,0,0,0,0,9
Data 9,0,0,0,0,0,5,5,5,5,5,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9
Data 9,0,0,0,0,0,0,0,5,5,5,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9
Data 9,0,0,0,0,0,0,0,0,0,5,5,0,0,0,0,0,0,0,0,0,0,0,0,0,0,9,0,0,0,0,9
Data 9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,9,9,9,9,0,0,9,9
Data 9,2,2,0,0,4,4,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,4,9,9,9,9,9,0,0,9,9
Data 9,0,1,0,0,0,4,4,4,0,0,0,0,0,0,0,0,0,0,4,4,4,9,9,0,0,0,0,0,0,0,9
Data 9,0,1,1,0,4,4,4,0,0,0,0,0,0,0,0,0,0,0,4,4,9,9,0,0,0,0,0,0,0,0,9
Data 9,0,1,1,0,0,0,2,2,2,3,0,0,0,4,4,4,4,4,4,9,9,0,0,0,0,0,0,0,0,0,9
Data 9,0,0,2,2,2,2,2,2,0,0,3,3,3,4,4,4,4,4,4,9,9,0,0,0,0,0,0,0,0,0,9
Data 9,0,0,0,2,2,2,0,0,0,4,4,0,0,0,0,0,0,4,4,9,9,0,0,0,2,3,4,5,6,6,9
Data 9,0,0,3,3,3,0,0,0,0,5,5,0,0,0,0,0,0,4,4,9,9,0,0,0,2,3,4,5,6,6,9
Data 9,0,0,0,4,4,5,0,0,6,6,6,0,0,0,0,0,4,4,4,9,9,0,0,0,2,3,4,5,3,3,9
Data 9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,9,9,9,0,0,0,2,3,3,3,3,3,9
Data 9,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4,4,9,9,9,0,0,0,2,3,3,3,3,3,9
Data 9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9,9
