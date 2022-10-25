; ID: 3277
; Author: Pakz
; Date: 2016-06-19 10:39:52
; Title: Fps Controls and Random indoor maps
; Description: A indoor random map generator and first person shooter controls.

Global scale1# = 5
Global scale2# = scale1*2
Global scale3# = 20

Global width = 800
Global height = 600
Graphics3D width,height,32,2
SetBuffer BackBuffer()

AppTitle "Map generator"

; mapwidthheight
Global mw=100
Global mh=100
;tilewidthheight
Global tw=GraphicsWidth()/mw
Global th=GraphicsHeight()/mh
;min/maxroomsizewh
Global minroomsize = 5
Global maxroomsize = 15

Dim map(mw,mh)

Const type_scenery=3
Const type_camera=1

; Camera position, angle values
Global cam_x#,cam_z#,cam_pitch#,cam_yaw#						; Current
Global dest_cam_x#,dest_cam_z#,dest_cam_pitch#,dest_cam_yaw#	; Destination

; Set up camera
Global camera=CreateCamera()					
CameraRange camera,1,300
EntityRadius camera,1.5
EntityType camera,type_camera

SeedRnd MilliSecs()

Dim entmap(mw,mh,2)

Global timer=CreateTimer(30)

;make the textures
Global texw = 256
Global texh = 256
Dim tex(10)
For ii=0 To 10
tex(ii) = CreateTexture(texw,texh)
SetBuffer TextureBuffer(tex(ii))
ClsColor 200,200,200
Cls
num=1000
If ii=2 Then num=3000
For i=0 To num
x = Rand(0,texw)
y = Rand(0,texh)
c = Rand(255)
Color c,c,c
Oval x,y,Rand(1,5),Rand(1,5)
Next
Next
SetBuffer BackBuffer()


remakelevel()
placeplayer

Global minmap = CreateImage(200,200)
makeminimap

Color 255,255,255
While KeyDown(1) = False
	WaitTimer timer	
	If KeyHit(2)=True
		remakelevel
		makeminimap		
		placeplayer

	End If
	UpdateWorld
	RenderWorld
	gameinput
	Text 0,0,"Press 1 - new level - mouse to turn - rmb/cursors move."
	DrawBlock minmap,width-200,0
	Color 255,255,0

	px = EntityX(camera)/scale1
	py = EntityZ(camera)/scale1
	Rect (width-200+(200-px)-5),(200-py)-5,10,10,False
	Flip
Wend
End

Function remakelevel()
	For y=0 To mh
	For x=0 To mw		
		FreeEntity entmap(x,y,0)		
		FreeEntity entmap(x,y,1)
		entmap(x,y,0) = 0
		entmap(x,y,1) = 0
		map(x,y) = 0
	Next
	Next
	makemap()
	makeents
	Collisions type_camera,type_scenery,2,3
End Function

;
; Here the cube map is made
;
;

Function makeents()
	For y=0 To mh
	For x=0 To mw
		If map(x,y) = 1
			entmap(x,y,0) = CreateCube()
			ScaleEntity entmap(x,y,0),scale1,1,scale1
			PositionEntity entmap(x,y,0),x*scale2,0,y*scale2
			EntityColor entmap(x,y,0),155,100,0
			EntityType entmap(x,y,0),type_scenery
			EntityTexture entmap(x,y,0),tex(0)
			
			entmap(x,y,1) = CreateCube()
			ScaleEntity entmap(x,y,1),scale1,1,scale1
			PositionEntity entmap(x,y,1),x*scale2,scale3,y*scale2
			EntityColor entmap(x,y,1),155,100,0
			EntityType entmap(x,y,1),type_scenery
			EntityTexture entmap(x,y,1),tex(1)

		End If
		If map(x,y) = 2
			entmap(x,y,0) = CreateCube()
			ScaleEntity entmap(x,y,0),scale1,scale3,scale1
			PositionEntity entmap(x,y,0),x*scale2,0,y*scale2
			EntityColor entmap(x,y,0),255,100,0
			EntityType entmap(x,y,0),type_scenery
			EntityTexture entmap(x,y,0),tex(2)
			
		End If
		If map(x,y) = 3
			entmap(x,y,0) = CreateCube()
			ScaleEntity entmap(x,y,0),scale1,1,scale1
			PositionEntity entmap(x,y,0),x*scale2,0,y*scale2
			EntityColor entmap(x,y,0),55,100,0
			EntityType entmap(x,y,0),type_scenery
			EntityTexture entmap(x,y,0),tex(3)


			entmap(x,y,1) = CreateCube()
			ScaleEntity entmap(x,y,1),scale1,1,scale1
			PositionEntity entmap(x,y,1),x*scale2,scale3,y*scale2
			EntityColor entmap(x,y,1),55,100,0
			EntityType entmap(x,y,1),type_scenery
			EntityTexture entmap(x,y,1),tex(4)

		End If
		If map(x,y) = 4 ; lava
			entmap(x,y,0) = CreateCube()
			ScaleEntity entmap(x,y,0),scale1,1,scale1
			PositionEntity entmap(x,y,0),x*scale2,0,y*scale2
			EntityColor entmap(x,y,0),155,10,1
			EntityType entmap(x,y,0),type_scenery
			EntityTexture entmap(x,y,0),tex(5)

			entmap(x,y,1) = CreateCube()
			ScaleEntity entmap(x,y,1),scale1,1,scale1
			PositionEntity entmap(x,y,1),x*scale2,scale3,y*scale2
			EntityColor entmap(x,y,1),155,100,0
			EntityType entmap(x,y,1),type_scenery
			EntityTexture entmap(x,y,1),tex(1)

		End If
		
	Next
	Next
End Function

Function newmap()
	For y=0 To mh
	For x=0 To mw
		map(x,y)=0
	Next
	Next
	makemap
End Function

Function makemap()
	map(mw/2,mh/2) = 3
	Local total=Rand(20000,150000)
	For i=0 To total
		x = Rand(maxroomsize+2,mw-(maxroomsize+2))
		y = Rand(maxroomsize+2,mh-(maxroomsize+2))
		If map(x,y) = 3
			a = Rand(0,4)
			w=Rand(minroomsize,maxroomsize)
			h=Rand(minroomsize,maxroomsize)
			Select a
				Case 0;nroom
				If fits(x-w/2,y-h,w,h-1) = True
					mr(x,y-h,x+w/2,y-h/2,x,y,x-w/2,y-h/2)
				EndIf
				Case 1;eroom
				If fits(x+1,y-h/2,w,h) = True
					mr(x+w/2,y-h/2,x+w,y,x+w/2,y+h/2,x,y)
				EndIf
				Case 2;sroom
				If fits(x-w/2,y+1,w,h) = True
					mr(x,y,x+w/2,y+h/2,x,y+h,x-w/2,y+h/2)
				EndIf
				Case 3;wroom
				If fits(x-w-1,y-h/2,w,h) = True
					mr(x-w/2,y-h/2,x,y,x-w/2,y+h/2,x-w,y)
				EndIf
			End Select
		End If
	Next
	; here we remove left over doors
	For y=2 To mh-2
	For x=2 To mw-2
		If map(x,y) = 3
			; if into darkness then remove
			If map(x-1,y) = 0 Or map(x+1,y) = 0
				map(x,y) = 2
			End If
			If map(x,y-1) = 0 Or map(x,y+1) = 0
				map(x,y) = 2
			End If
			cnt=0
			; every door if blocked remove
			For y1=y-1 To y+1
			For x1=x-1 To x+1
			If map(x1,y1) = 2 Then cnt=cnt+1
			Next
			Next
			If cnt>2 Then map(x,y)=2
		End If
	Next
	Next
	
End Function

; makeroom
Function mr(x1,y1,x2,y2,x3,y3,x4,y4)
	Local myw = x2-x1
	Local myh = y3-y1
	For y5=y1 To y3
	For x5=x4 To x2
		map(x5,y5) = 1
	Next
	Next
	For y5=y1 To y3
		map(x4,y5) = 2
		map(x2,y5) = 2		
	Next
	For x5=x4 To x2
		map(x5,y1) = 2
		map(x5,y3) = 2
	Next
	map(x1,y1) = 3
	map(x2,y2) = 3
	map(x3,y3) = 3
	map(x4,y4) = 3

	If Rand(2) = 1
	If Rand(2) = 1
		For y5 = y1+2 To y3-2
		For x5 = x4+2 To x2-2
			map(x5,y5) = 4
		Next
		Next	
	Else
		For y5 = y1+3 To y3-3
		For x5 = x4+3 To x2-3
			map(x5,y5) = 2
		Next
		Next
	End If	
	End If

End Function

; Is there anything in the map
Function fits(x,y,w,h)
	; if outside
	If x<0 Or y<0 Or x+w>mw Or y+h>mh Then Return False	
	; if inside
	For y1=y To y+h
	For x1=x To x+w
		If map(x1,y1)>0 Then Return False
	Next
	Next
	Return True
End Function

Function gameinput()


	; Mouse look
	; ----------

	; Mouse x and y speed
	mxs=MouseXSpeed()
	mys=MouseYSpeed()
	
	; Mouse shake (total mouse movement)
	mouse_shake=Abs(((mxs+mys)/2)/1000.0)

	; Destination camera angle x and y values
	dest_cam_yaw#=dest_cam_yaw#-mxs
	dest_cam_pitch#=dest_cam_pitch#+mys

	; Current camera angle x and y values
	cam_yaw=cam_yaw+((dest_cam_yaw-cam_yaw)/5)
	cam_pitch=cam_pitch+((dest_cam_pitch-cam_pitch)/5)
	
	RotateEntity camera,cam_pitch#,cam_yaw#,0
	
	; Rest mouse position to centre of screen
	MoveMouse width/2,height/2
	

	; Camera move
	; -----------
	
	; Forward/backwards - destination camera move z values
	If KeyDown(200)=True Or MouseDown(2)=True Then dest_cam_z=1
	If KeyDown(208)=True Then dest_cam_z#=-1

	; Strafe - destination camera move x values
	If KeyDown(205)=True Then dest_cam_x=1
	If KeyDown(203)=True Then dest_cam_x=-1
	
	; Current camera move x and z values
	cam_z=cam_z+((dest_cam_z-cam_z)/5)
	cam_x=cam_x+((dest_cam_x-cam_x)/5)

	; Move camera
	MoveEntity camera,cam_x,0,cam_z
	dest_cam_x=0 : dest_cam_z=0
	
;	; Gravity
	TranslateEntity camera,0,-1,0


End Function

Function placeplayer()
	Local exitloop = False
	Local cnt2=0
	While exitloop = False
		x = Rand(0,mw)
		y = Rand(0,mh)
		cnt=0
		For y1=-3 To 3
		For x1=-3 To 3
		If x+x1>0 And x+x1<mw And y+y1>0 And y+y1<mh
		If map(x+x1,y+y1) = 1 
			cnt=cnt+1
		End If
		End If
		Next
		Next
		If cnt>48
			exitloop = True
		End If
		cnt2=cnt2+1
		If cnt2>10000 Then remakelevel : cnt2=0
	Wend
	PositionEntity camera,x*scale2,8,y*scale2
	ResetEntity camera
End Function

Function makeminimap()
	SetBuffer ImageBuffer(minmap)
	ClsColor 0,0,0
	Cls
	For y=0 To mh
	For x=0 To mw
		If map(x,y)>0
		If map(x,y) = 1
			Color 150,150,150
		End If
		If map(x,y) = 2
			Color 250,250,250
		End If
		If map(x,y) = 3
			Color 50,50,50
		End If
		
		Plot 200-(x*200/mw),200-(y*200/mh)
		End If
	Next
	Next
	SetBuffer BackBuffer()
End Function
