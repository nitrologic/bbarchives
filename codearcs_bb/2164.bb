; ID: 2164
; Author: Nebula
; Date: 2007-12-02 21:45:47
; Title: Blok Bewerkscherm naar 3d wereld
; Description: Vrije rondbewegen in een eigen wereld.

Type fps
	Field fps
	Field fpstimer
	Field fpscounter
End Type
Global fps.fps = New fps

Graphics3D 640,480,16,2
SetBuffer BackBuffer()

apptitle "R toets = run , E toets = bewerk."

Const edit_arrowup = 2
Const edit_arrowdown = 3
Const edit_arrowleft = 4
Const edit_arrowright = 5

;Global tex = LoadTexture("dirt2.png")
;Global grass = LoadTexture("grass.png")
Global tex = CreateTexture(96,96)
Global grass = CreateTexture(96,96)

SetBuffer TextureBuffer(Tex)
ClsColor 200,0,0
Cls
SetBuffer TextureBuffer(grass)
ClsColor 0,200,0
Cls
SetBuffer BackBuffer()

Dim editmap(100,100) ; Build map <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Map data

Global mylevel

Type edit
	Field arrowup,arrowdown,arrowleft,arrowright
	Field cursorx,cursory
	Field displaywidth,displayheight
	Field w,h
	Field cellwidth,cellheight
	Field mousecursorx
	Field mousecursory
	Field editvalue
	Field state
End Type

Global edit.edit = New edit
edit\state = 0
edit\w = 100
edit\h = 100
edit\cellwidth = 16
edit\cellheight = 16
edit\editvalue=1

Include "cammodule.bb"
Include "mesh.bb"

HidePointer
MoveMouse 0,0



Global terrain = CreateTerrain(128)
EntityTexture terrain,grass

Global tex2 = CreateTexture(128,512)

;tmptex = LoadImage("dirt2.png")
tmptex = CreateImage(96,96)

SetBuffer TextureBuffer(tex2)
	For y=0 To 512 Step 128
		DrawBlock tmptex,0,y
	Next


SetBuffer BackBuffer()

edit_readarrows

;WireFrame True
While KeyDown(1) = False
	Cls
	If KeyHit(18) = True And edit\state = 1 Then  ; e
		FlushKeys()
		FreeEntity mylevel
		edit\state = 0 
	End If
	;
	Select edit\state	
	Case 0
		edit_leveledit()                                      ; EDIT Default start
	Case 1
		RenderWorld
		UpdateWorld		
		updatecam
		dofps
		Text 0,0,TrisRendered()
		Text 200,0,fps\fps		
	End Select
	Flip False
Wend
End


Function edit_makelevel()										; Maak het level
Local outmesh = CreateMesh()
For z=0 To 100 
For x=0 To 100
;top,bottom,left,right,front,back
If editmap(x,z) = 1 Then
	a = False
	b = False
	c = True
	d = True
	e = True
	f = True
	If leftwall(x,z) = True Then c = False 
	If rightwall(x,z) = True Then d = False
	If frontwall(x,z) = True Then e = False
	If backwall(x,z) = True Then f = False
	a = makeCube(a,b,c,d,e,f)
	ScaleMesh a,1,8,1
	PositionMesh a,x*4,0,z*4
	AddMesh a,outmesh
	FreeEntity a
End If

Next
Next
EntityTexture outmesh,tex2
edit\state = 1
MoveMouse 0,0
Return outmesh
End Function

Function backwall(x,z)											;Muur
	If outbounds(x,z+1) = True Then Return False
	If editmap(x,z+1) = 1 Then Return True
End Function

Function frontwall(x,z)
	If outbounds(x,z-1) = True Then Return False
	If editmap(x,z-1) = 1 Then Return True
End Function

Function rightwall(x,z)
	If outbounds(x+1,z) = True Then Return False
	If editmap(x+1,z) = 1 Then Return True
End Function

Function leftwall(x,z)
	If outbounds(x-1,z) = True Then Return False
	If editmap(x-1,z) = 1 Then Return True
End Function

Function outbounds(x,z)
	If x<0 Or z<0 Then Return True
	If x>100 Or z>100 Then Return True 
End Function

Function edit_leveledit()
ShowPointer
edit_drawlevel
edit_menu
edit_keys
edit_Selecticon
edit_updatemousecoordinates
edit_myedit
End Function

Function edit_keys()
If KeyHit(46) = True Then edit_clsmap ; 
If KeyHit(19) = True Then mylevel = edit_makelevel() ; r

End Function

Function edit_clsmap()
For x=0 To edit\w
For y=0 To edit\h
editmap(x,y) = 0
Next
Next
End Function

Function edit_menu()											; Bewerk scherm
	Color 255,255,255
	rside = GraphicsWidth()
	lside = rside-130
	;
	cnt = 0
	For y=20 To GraphicsHeight()-20 Step 32
	Select cnt
		Case 0:Rect lside,y,20,20					:Text lside+32,y,"Wall"
		Case 1:DrawBlock edit\arrowup,lside,y		:Text lside+32,y,"Level Up"
		Case 2:DrawBlock edit\arrowdown,lside,y		:Text lside+32,y,"Level Up"
		Case 3:DrawBlock edit\arrowleft,lside,y		:Text lside+32,y,"Level Up"
		Case 4:DrawBlock edit\arrowright,lside,y	:Text lside+32,y,"Level Up"
		Default
	End Select
	cnt=cnt+1
	Next
End Function

Function edit_selecticon()
	If MouseDown(1) = False Then Return
	rside = GraphicsWidth()
	lside = rside-130

	cnt = 1
	For y=20 To GraphicsHeight()-20 Step 32
		If RectsOverlap(MouseX(),MouseY(),1,1,lside,y,140,20) = True Then
			edit\editvalue = cnt
		End If
		cnt=cnt+1
	Next
End Function

Function edit_myedit()
	If MouseDown(1) = True Then
		editmap(edit\mousecursorx,edit\mousecursory) = edit\editvalue
	End If
	If MouseDown(2) = True Then
		editmap(edit\mousecursorx,edit\mousecursory) = 0
	End If
End Function

Function edit_updatemousecoordinates()
	edit\mousecursorx = MouseX() / edit\cellwidth
	edit\mousecursory = MouseY() / edit\cellheight
End Function

Function edit_drawmapicon(x,y)										; Bewerk teken kaart
	x2 = x*edit\cellwidth
	y2 = y*edit\cellheight
	Select editmap(x,y)
		Case 1
			Color 255,255,255
			Rect x2,y2,edit\cellwidth,edit\cellheight,True
		Case edit_arrowup		:DrawBlock edit\arrowup,x2,y2
		Case edit_arrowdown		:DrawBlock edit\arrowdown,x2,y2
		Case edit_arrowleft		:DrawBlock edit\arrowleft,x2,y2
		Case edit_arrowright	:DrawBlock edit\arrowright,x2,y2
	End Select
End Function

Function edit_drawlevel()
	Color 255,255,255
	edit\displaywidth = (GraphicsWidth()-160)/edit\cellwidth
	edit\displayheight = (GraphicsHeight()-40)/edit\cellheight
	For x=0 To edit\displaywidth
		For y=0 To edit\displayheight
			Rect x*edit\cellwidth,y*edit\cellheight,edit\cellwidth+1,edit\cellheight+1,False
			edit_drawmapicon(x,y)
	Next:Next
End Function

Function edit_readarrows()
	Local im = CreateImage(16,16)
	SetBuffer ImageBuffer(im)
	Color 255,255,255

	; Read up arrow
	Restore arrowup	
	For y=0 To 7
	For x=0 To 7
		Read a
		If a = 1 Then 
		Rect x*2,y*2,2,2,True
		End If
	Next:Next
	edit\arrowup = CopyImage(im)
	Cls
	; Read down arrow
	Restore arrowdown	
	For y=0 To 7
	For x=0 To 7
		Read a
		If a = 1 Then 
		Rect x*2,y*2,2,2,True
		End If
	Next:Next
	edit\arrowdown = CopyImage(im)
	Cls
	; Read left arrow
	Restore arrowleft	
	For y=0 To 7
	For x=0 To 7
		Read a
		If a = 1 Then 
		Rect x*2,y*2,2,2,True
		End If
	Next:Next
	edit\arrowleft = CopyImage(im)
	Cls
	; Read right arrow
	Restore arrowright
	For y=0 To 7
	For x=0 To 7
		Read a
		If a = 1 Then 
		Rect x*2,y*2,2,2,True
		End If
	Next:Next
	edit\arrowright = CopyImage(im)
	SetBuffer BackBuffer()
End Function

.arrowup
Data 0,0,0,1,0,0,0,0
Data 0,0,1,1,1,0,0,0
Data 0,1,1,1,1,1,0,0
Data 1,1,1,1,1,1,1,0
Data 0,0,1,1,1,0,0,0
Data 0,0,1,1,1,0,0,0
Data 0,0,1,1,1,0,0,0
Data 0,0,1,1,1,0,0,0
.arrowleft
Data 0,0,0,1,0,0,0,0
Data 0,0,1,1,0,0,0,0
Data 0,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,1
Data 0,1,1,1,1,1,1,1
Data 0,0,1,1,0,0,0,0
Data 0,0,0,1,0,0,0,0
Data 0,0,0,0,0,0,0,0
.arrowright
Data 0,0,0,0,1,0,0,0
Data 0,0,0,0,1,1,0,0
Data 1,1,1,1,1,1,1,0
Data 1,1,1,1,1,1,1,1
Data 1,1,1,1,1,1,1,0
Data 0,0,0,0,1,1,0,0
Data 0,0,0,0,1,0,0,0
Data 0,0,0,0,0,0,0,0
.arrowdown
Data 0,0,1,1,1,0,0,0
Data 0,0,1,1,1,0,0,0
Data 0,0,1,1,1,0,0,0
Data 0,0,1,1,1,0,0,0
Data 1,1,1,1,1,1,1,0
Data 0,1,1,1,1,1,0,0
Data 0,0,1,1,1,0,0,0
Data 0,0,0,1,0,0,0,0

Function dofps()
	fps\fpscounter = fps\fpscounter + 1
	If fps\fpstimer < MilliSecs() Then

		fps\fps = fps\fpscounter
		fps\fpscounter = 0
		fps\fpstimer = MilliSecs() + 1000
	EndIf
End Function


; Bewaar dit bestand hieronder apart in een folder.
; De naam ; cammodule


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

; -- Initialize the loop.
old_time = MilliSecs ()
;Collisions COLLTYPE_user, COLLTYPE_geometry, 2, 2
;^^^^^^



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

Function updatecam()
	CalculateDeltatimeAndGametime()
	mouselook()
	moveuser()
End Function

Function setplayercamspeed(speed#)
	user_movement_speed = speed#
End Function








;
; Bewaar dit deel bestand apart in een folder als
;
;
; mesh.bb



; top,bottom,left,right,front,back
Function makecube(a=True,b=True,c=True,d=True,e=True,f=True)
	z=CreateMesh() 

	surf=CreateSurface(z) 

	v0 = AddVertex(surf,0,0,0,	0,1)
	v3 = AddVertex(surf,4,0,0,	1,1)	
	v4 = AddVertex(surf,0,4,0,	0,0)
	v7 = AddVertex(surf,4,4,0,	1,0)

	v1 = AddVertex(surf,0,0,4	,0,1)
	v2 = AddVertex(surf,4,0,4	,1,1)	
	v5 = AddVertex(surf,0,4,4	,0,0)
	v6 = AddVertex(surf,4,4,4	,1,0)

	v8  = AddVertex(surf,0,0,0	,0,1)
	v9  = AddVertex(surf,0,0,4	,1,1)	
	v10 = AddVertex(surf,0,4,0	,0,0)
	v11 = AddVertex(surf,0,4,4	,1,0)

	v12 = AddVertex(surf,4,0,0  ,0,1)
	v13 = AddVertex(surf,4,0,4  ,1,1)	
	v14 = AddVertex(surf,4,4,0	,0,0)
	v15 = AddVertex(surf,4,4,4	,1,0)

	v16 = AddVertex(surf,0,4,0  ,0,1)
	v17 = AddVertex(surf,0,4,4	,1,1)	
	v18 = AddVertex(surf,4,4,0	,0,0)
	v19 = AddVertex(surf,4,4,4	,1,0)

	v20 = AddVertex(surf,0,0,0  ,0,1)
	v21 = AddVertex(surf,0,0,4	,1,1)	
	v22 = AddVertex(surf,4,0,0	,0,0)
	v23 = AddVertex(surf,4,0,4	,1,0)

	If a = True
		AddTriangle(Surf,v16,v17,v18) ;top
		AddTriangle(surf,v18,v17,v19)
	End If
	If b = True
		AddTriangle(surf,v21,v20,v22) ; bottom
		AddTriangle(surf,v21,v22,v23)
	End If
	If f = True Then
		AddTriangle(surf,v5,v1,v2) ; back
		AddTriangle(surf,v5,v2,v6)
	End If
	If e = True Then
		AddTriangle(surf,v0,v4,v3) ; front
		AddTriangle(surf,v3,v4,v7) ; 
	End If
	If d = True
		AddTriangle(surf,v15,v13,v12) ; left
		AddTriangle(surf,v15,v12,v14)
	End If
	If c = True
		AddTriangle(surf,v9,v11,v8) ; right
		AddTriangle(surf,v8,v11,v10)
	End If
	Return z
End Function

; top,bottom,left,right,front,back
Function make3dfrontdown(a=True,b=True,c=True,d=True,e=True)
	
	;    /|
	;  /  |
	;/____| 

	z=CreateMesh() 

	surf=CreateSurface(z) 

;	v16 = AddVertex(surf,0,4,0  ,0,1) ; top
;	v17 = AddVertex(surf,0,4,4	,1,1)	
;	v18 = AddVertex(surf,4,0,0	,0,0)
;	v19 = AddVertex(surf,4,0,4	,1,0)

	v20 = AddVertex(surf,0,0,0  ,0,1) ; bottom
	v21 = AddVertex(surf,0,0,4	,1,1)	
	v22 = AddVertex(surf,4,0,0	,0,0)
	v23 = AddVertex(surf,4,0,4	,1,0)

	v1 = AddVertex(surf,0,0,4	,0,1) ; back
	v2 = AddVertex(surf,4,0,4	,1,1)	
	v5 = AddVertex(surf,0,4,0	,0,0)
	v6 = AddVertex(surf,4,4,0	,1,0)
		
	v0 = AddVertex(surf,0,0,0,	0,1) ; front
	v3 = AddVertex(surf,4,0,0,	1,1)	
	v4 = AddVertex(surf,0,4,0,	0,0)
	v7 = AddVertex(surf,4,4,0,	1,0)
	
;	v12 = AddVertex(surf,4,0,0  ,0,1) ; right
;	v13 = AddVertex(surf,4,0,4  ,1,1)	
;	v14 = AddVertex(surf,4,4,0	,0,0)
;	v15 = AddVertex(surf,4,4,4	,1,0)

	v12 = AddVertex(surf,4,0,0  ,0,1) ; right
	v13 = AddVertex(surf,4,0,4  ,1,1)	
	v14 = AddVertex(surf,4,4,0	,0,0)
	v15 = AddVertex(surf,4,4,0	,1,0)



	v8  = AddVertex(surf,0,0,0	,0,1) ; left
	v9  = AddVertex(surf,0,0,4	,1,1)	
	v10 = AddVertex(surf,4,4,0	,0,0)
	v11 = AddVertex(surf,4,4,4	,1,0)


If a = True
		AddTriangle(surf,v21,v20,v22) ; bottom
		AddTriangle(surf,v21,v22,v23)
End If
If e = True Then
		AddTriangle(surf,v6,v1,v2) ; back
		AddTriangle surf,v5,v1,v6
End If
If d = True Then ; front
		AddTriangle surf,v3,v0,v6
		AddTriangle surf,v6,v0,v5
	End If
If c = True Then
		AddTriangle(surf,v15,v13,v12) ; right
		;AddTriangle(surf,v15,v12,v14)
	End If
If b = True Then
		AddTriangle(surf,v5,v8,v9) ; left
	End If
	Return z

End Function




; top,bottom,left,right,front,back
Function make3dfrontup(a=True,b=True,c=True,d=True,e=True)
	
	;    /|
	;  /  |
	;/____| 

	z=CreateMesh() 

	surf=CreateSurface(z) 

;	v16 = AddVertex(surf,0,4,0  ,0,1) ; top
;	v17 = AddVertex(surf,0,4,4	,1,1)	
;	v18 = AddVertex(surf,4,0,0	,0,0)
;	v19 = AddVertex(surf,4,0,4	,1,0)

	v20 = AddVertex(surf,0,0,0  ,0,1) ; bottom
	v21 = AddVertex(surf,0,0,4	,1,1)	
	v22 = AddVertex(surf,4,0,0	,0,0)
	v23 = AddVertex(surf,4,0,4	,1,0)

	v1 = AddVertex(surf,0,0,4	,0,1) ; back
	v2 = AddVertex(surf,4,0,4	,1,1)	
	v5 = AddVertex(surf,0,4,4	,0,0)
	v6 = AddVertex(surf,4,4,4	,1,0)
		
	v0 = AddVertex(surf,0,0,0,	0,1) ; front
	v3 = AddVertex(surf,4,0,0,	1,1)	
	v4 = AddVertex(surf,0,4,0,	0,0)
	v7 = AddVertex(surf,4,4,0,	1,0)
	
	v12 = AddVertex(surf,4,0,0  ,0,1) ; right
	v13 = AddVertex(surf,4,0,4  ,1,1)	
	v14 = AddVertex(surf,4,4,0	,0,0)
	v15 = AddVertex(surf,4,4,4	,1,0)

	v8  = AddVertex(surf,0,0,0	,0,1) ; left
	v9  = AddVertex(surf,0,0,4	,1,1)	
	v10 = AddVertex(surf,4,4,0	,0,0)
	v11 = AddVertex(surf,4,4,4	,1,0)


If a = True
		AddTriangle(surf,v21,v20,v22) ; bottom
		AddTriangle(surf,v21,v22,v23)
End If
If e = True Then
		AddTriangle(surf,v6,v1,v2) ; back
		AddTriangle surf,v5,v1,v6
End If
If d = True Then ; front
		AddTriangle surf,v3,v0,v6
		AddTriangle surf,v6,v0,v5
	End If

If c = True Then
		AddTriangle(surf,v15,v13,v12) ; right
		;AddTriangle(surf,v15,v12,v14)
	End If
	
If b = True Then
		AddTriangle(surf,v5,v8,v9) ; left
	End If
	Return z

End Function



; top,bottom,left,right,front,back
Function make3leftup(a=True,b=True,c=True,d=True,e=True)
	
	;    /|
	;  /  |
	;/____| 

	z=CreateMesh() 

	surf=CreateSurface(z) 

;	v16 = AddVertex(surf,0,4,0  ,0,1) ; top
;	v17 = AddVertex(surf,0,4,4	,1,1)	
;	v18 = AddVertex(surf,4,0,0	,0,0)
;	v19 = AddVertex(surf,4,0,4	,1,0)

	v20 = AddVertex(surf,0,0,0  ,0,1) ; bottom
	v21 = AddVertex(surf,0,0,4	,1,1)	
	v22 = AddVertex(surf,4,0,0	,0,0)
	v23 = AddVertex(surf,4,0,4	,1,0)

	v1 = AddVertex(surf,0,0,4	,0,1) ; back
	v2 = AddVertex(surf,4,0,4	,1,1)	
	v5 = AddVertex(surf,0,4,4	,0,0)
	v6 = AddVertex(surf,4,4,4	,1,0)
		
	v0 = AddVertex(surf,0,0,0,	0,1) ; front
	v3 = AddVertex(surf,4,0,0,	1,1)	
	v4 = AddVertex(surf,0,4,0,	0,0)
	v7 = AddVertex(surf,4,4,0,	1,0)
	
	v12 = AddVertex(surf,4,0,0  ,0,1) ; right
	v13 = AddVertex(surf,4,0,4  ,1,1)	
	v14 = AddVertex(surf,4,4,0	,0,0)
	v15 = AddVertex(surf,4,4,4	,1,0)

	v8  = AddVertex(surf,0,0,0	,0,1) ; left
	v9  = AddVertex(surf,0,0,4	,1,1)	
	v10 = AddVertex(surf,4,4,0	,0,0)
	v11 = AddVertex(surf,4,4,4	,1,0)


If a = True
		AddTriangle(surf,v21,v20,v22) ; bottom
		AddTriangle(surf,v21,v22,v23)
End If
If e = True Then
		AddTriangle(surf,v6,v1,v2) ; back
End If
If d = True Then ; front
		AddTriangle surf,v3,v0,v7
	End If
If c = True Then
		AddTriangle(surf,v15,v13,v12) ; right
		AddTriangle(surf,v15,v12,v14)
	End If
If b = True Then
		AddTriangle(surf,v9,v11,v8) ; left
		AddTriangle(surf,v8,v11,v10)
	End If
	Return z

End Function


Function make3rightup(a=True,b=True,c=True,d=True,e=True)

; |\
; |  \
; |____\


	z=CreateMesh() 

	surf=CreateSurface(z) 

;	v16 = AddVertex(surf,0,4,0  ,0,1) ; top
;	v17 = AddVertex(surf,0,4,4	,1,1)	
;	v18 = AddVertex(surf,4,0,0	,0,0)
;	v19 = AddVertex(surf,4,0,4	,1,0)

	v20 = AddVertex(surf,0,0,0  ,0,1) ; bottom
	v21 = AddVertex(surf,0,0,4	,1,1)	
	v22 = AddVertex(surf,4,0,0	,0,0)
	v23 = AddVertex(surf,4,0,4	,1,0)

	v1 = AddVertex(surf,0,0,4	,0,1) ; back
	v2 = AddVertex(surf,4,0,4	,1,1)	
	v5 = AddVertex(surf,0,4,4	,0,0)
	v6 = AddVertex(surf,4,4,4	,1,0)
		
	v0 = AddVertex(surf,0,0,0,	0,1) ; front
	v3 = AddVertex(surf,4,0,0,	1,1)	
	v4 = AddVertex(surf,0,4,0,	0,0)
	v7 = AddVertex(surf,4,4,0,	1,0)
	
	v12 = AddVertex(surf,4,0,0  ,0,1) ; right
	v13 = AddVertex(surf,4,0,4  ,1,1)	
	v14 = AddVertex(surf,0,4,0	,0,0)
	v15 = AddVertex(surf,0,4,4	,1,0)

	v8  = AddVertex(surf,0,0,0	,0,1) ; left
	v9  = AddVertex(surf,0,0,4	,1,1)	
	v10 = AddVertex(surf,0,4,0	,0,0)
	v11 = AddVertex(surf,0,4,4	,1,0)


If a = True
		AddTriangle(surf,v21,v20,v22) ; bottom
		AddTriangle(surf,v21,v22,v23)
End If
If e = True Then
		AddTriangle(surf,v5,v1,v2) ; back
End If
If d = True Then
		AddTriangle(surf,v0,v4,v3) ; front
;		AddTriangle(surf,v3,v4,v7) ; 
	End If
If c = True Then
		AddTriangle(surf,v15,v13,v12) ; right
		AddTriangle(surf,v15,v12,v14)
	End If
If b = True Then
		AddTriangle(surf,v9,v11,v8) ; left
		AddTriangle(surf,v8,v11,v10)
	End If
	Return z

End Function
