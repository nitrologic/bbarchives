; ID: 470
; Author: bradford6
; Date: 2002-10-25 02:33:08
; Title: Moving Platforms demo
; Description: collisions with moving platforms

Graphics3D 800,600


; ********************************* GLOBALS ****************************

Global midw,midh,disctex,boxtex,grasstex
Global cam,player,player_piv,hat
Global camdistance#=10,camheight#=5
Global on_platform=0
Global picklength#=.5
Global pictentity	
Global gravity#=.005
Global turnspeed#,lateral_speed#,speed#,pxvel#,pyvel#,pzvel#,pyspin# ; player velocities
Global jumping=1

Global camdist_to_player#
Global fps


Type platform
	Field entity,brush
	Field startX#,starty#,startz#,xdist#,ydist#,zdist#,xspeed#,yspeed#,zspeed#,startdelay#,enddelay#
	Field yvel#,xvel#,zvel#,yspin#,mx#,my#
	Field xdamp#,ydamp#,zdamp#
	Field xdirection,ydirection,zdirection
	Field xstartdelaycounter,xenddelaycounter
	Field ystartdelaycounter,yenddelaycounter
	Field zstartdelaycounter,zenddelaycounter
End Type




SeedRnd MilliSecs

; CREATE OUR PLATFORMS

; (startXposition#,y#,z#,  xdist,Yd#,Zd#,  xspd#,yspd#,zspd#, xdamp#,ydamp#, zdamp, startdelay#,enddelay#, yspin# ,SHAPE_OF_PLATFORM$)

create_tex() ; go create some textures

; create the platforms for our little demo!


create_platform.platform(5,3,0,5,10,0,.001,.0005,0,.96,.96,.96,0,0,0,"box")

create_platform.platform(-6,6,0,0,30,0,.001,.001,0,.96,.96,.96,100,0,.5,"box")

create_platform.platform(-14,35,-4,0,0,10,0,0,.002,0,0,.96,0,0,0,"box")

create_platform.platform(10,2,10,0,40,0,0,.006,0,0,.9,0,200,50,0,"disc")

create_platform.platform(5,2,-12,0,20,0,0,.00005,0,00,.99,0,0,0,0,"disc")

create_world()

Collisions 1,2,2,2 ; set up collisions between the player and the world

fps=1 
HidePointer
EntityAlpha player,0
EntityAlpha hat,0

.main
; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% MAIN LOOP ###################################

While Not KeyDown( 1 )


If KeyHit(59) Then save_bitmap()
; CAMERA MODES
			If KeyHit(2) 
			 	fps=0
				ShowPointer 
				EntityAlpha player,1
				EntityAlpha hat,1
			EndIf	
			
			If KeyHit(3) 
				fps=1 
				HidePointer
				EntityAlpha player,0
				EntityAlpha hat,0
			EndIf


; Line pick up and down to see if we are on or below a platform

	pick_platform.platform()

; move the player

	If fps=1 Then fps_camera()
	If fps=0 Then move_player()
	
; check player to world collisions

	
	
; move the platforms
do_collisions.platform()
	move_platform.platform()


	UpdateWorld
	
	RenderWorld
		draw_overlay()	; text and messages to the screen	 
	Flip

Wend

End
; %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%



;=====================================
Function create_platform.platform(stX#,sty#,stz#,dX#,dY#,dZ#,xspd#,yspd#,zspd#,xdamp#,ydamp#,zdamp#,sdelay#,edelay#,yspin#,shape$)
; create a platform and position it in the world
foo.platform=New platform
	
	
	
	
	
	If shape$="disc" 
		foo\entity=CreateSphere()
		ScaleMesh foo\entity,3,.5,3
		foo\brush=CreateBrush()
		BrushColor foo\brush,Rnd(100,255),Rnd(100,255),Rnd(100,255)
		BrushTexture foo\brush,disctex
		PaintEntity foo\entity,foo\BRUSH

	EndIf
	If shape$="column" 
		foo\entity=CreateCylinder()
					
		ScaleMesh foo\entity,3,.25,3
		
		foo\brush=CreateBrush()
		BrushColor foo\brush,Rnd(0,255),Rnd(0,255),Rnd(0,255)
		
		BrushTexture foo\brush,boxtex
		PaintEntity foo\entity,foo\BRUSH

	EndIf

	If shape$="box" 
		foo\entity=CreateCube()
		ScaleMesh foo\entity,3,.5,3
		foo\brush=CreateBrush()
		BrushColor foo\brush,Rnd(100,255),Rnd(100,255),Rnd(100,255)
		BrushTexture foo\brush,boxtex
		PaintEntity foo\entity,foo\BRUSH
	EndIf

	
	
	
	SetBuffer BackBuffer()
	
	EntityPickMode foo\entity,2
	EntityType foo\entity,2
	
	foo\startx#=stx#
	foo\starty#=sty#
	foo\startz#=stz#
	foo\xdist#=dx#
	foo\ydist#=dy#
	
	
	foo\zdist#=dz#
	foo\xspeed#=xspd#
	foo\yspeed#=yspd#
	foo\zspeed#=zspd#
	foo\xdamp#=xdamp#
	foo\Ydamp#=Ydamp#
	foo\Zdamp#=Zdamp#

	foo\yspin#=yspin#
	
	foo\startdelay#=sdelay#
	foo\enddelay#=edelay#
	
	
	PositionEntity foo\entity,stx#,sty#,stz#
Return foo.platform



End Function

;=====================================

Function move_platform.platform()
For foo.platform = Each platform
	
	
	
	If foo\xdirection=0 ; if Platform is going down?
		foo\xstartdelaycounter=foo\xstartdelaycounter-1  ; reduce startdelay counter
		 
		If foo\xstartdelaycounter<1		; if startdelay counter is less than 1

				foo\xstartdelaycounter=0 	; set it to 0 (so it will be less than 1 until it is reset)

				foo\xvel#=foo\xvel#-foo\xspeed# 				; accelerate
				TranslateEntity foo\entity,foo\xvel#,0,0 
				If EntityX(foo\entity)<foo\startx#+(foo\xdist#*.25) Then foo\xvel#=foo\xvel#*FOO\xdamp
	
					If EntityX(foo\entity)<foo\startx# 		; if platform reaches the bottom
						foo\xdirection=1						; change direction to UP
						foo\xenddelaycounter=foo\enddelay# 	; reset end delay counter
													
					EndIf
		EndIf
	EndIf	
	If foo\xdirection=1 ; UP
		foo\xenddelaycounter=foo\xenddelaycounter-1
		If foo\xenddelaycounter<1
			
			foo\xenddelaycounter=0
			foo\xvel#=foo\xvel#+foo\xspeed#
	
			If EntityX(foo\entity)>foo\startx#+(foo\xdist#*.75) Then foo\xvel#=foo\xvel#*FOO\xdamp

	
			TranslateEntity foo\entity,foo\xvel#,0,0
				If EntityX(foo\entity)>foo\startx#+foo\xdist#
					;If on_platform=1 Then yvel#=0
					foo\xdirection=0
					
					foo\xstartdelaycounter=foo\startdelay#
	
				EndIf
		EndIf
	EndIf	

	
	
	
	
	
	
	
	
	
	
	
	
	If foo\ydirection=0 ; if Platform is going down?
		foo\yenddelaycounter=foo\yenddelaycounter-1  ; reduce startdelay counter
		 TurnEntity foo\entity,0,foo\yspin#,0
		If foo\yenddelaycounter<1		; if startdelay counter is less than 1
				
				
				
				foo\yenddelaycounter=0 	; set it to 0 (so it will be less than 1 until it is reset)

				foo\yvel#=foo\yvel#-foo\yspeed# 				; accelerate
				TranslateEntity foo\entity,0,foo\yvel#,0 
				
				
				
				If EntityY(foo\entity)<foo\startY#+(foo\ydist#*.25) Then foo\yvel#=foo\yvel#*FOO\ydamp

					
					If EntityY(foo\entity)<foo\starty# 		; if platform reaches the bottom
						foo\ydirection=1						; change direction to UP
						foo\ystartdelaycounter=foo\startdelay#
						
						; stop the platform
					EndIf
		EndIf
	EndIf	
	If foo\ydirection=1 ; UP
		foo\ystartdelaycounter=foo\ystartdelaycounter-1
		TurnEntity foo\entity,0,foo\yspin#,0
		
		If foo\ystartdelaycounter<1
			
			foo\ystartdelaycounter=0
			foo\yvel#=foo\yvel#+foo\yspeed#
			
			
			TranslateEntity foo\entity,0,foo\yvel#,0
				If EntityY(foo\entity)>foo\startY#+(foo\ydist#*.75) Then foo\yvel#=foo\yvel#*FOO\ydamp



				If EntityY(foo\entity)>foo\startY#+foo\ydist#
					;If on_platform=1 Then yvel#=0
					foo\ydirection=0
					;foo\yvel#=0
					
					foo\yenddelaycounter=foo\enddelay# 	; reset end delay counter
				EndIf
		EndIf
	EndIf	

		If foo\zdirection=0 ; 
				foo\zenddelaycounter=foo\zenddelaycounter-1  ; reduce startdelay counter
				 
				If foo\zenddelaycounter<1		; if startdelay counter is less than 1
		
						foo\zenddelaycounter=0 	; set it to 0 (so it will be less than 1 until it is reset)
		
						foo\zvel#=foo\zvel#-foo\zspeed# 				; accelerate
						TranslateEntity foo\entity,0,0,foo\zvel# 
							
							If EntityZ(foo\entity)<foo\startz#+(foo\zdist#*.25) Then foo\zvel#=foo\zvel#*FOO\zdamp


							If EntityZ(foo\entity)<foo\startz# 		; if platform reaches the bottom
								foo\zdirection=1						; change direction to UP
								foo\zstartdelaycounter=foo\startdelay# 	; reset end delay counter
														
							EndIf
				EndIf
			EndIf	
			If foo\zdirection=1 ; UP
				foo\zstartdelaycounter=foo\zstartdelaycounter-1
				If foo\zstartdelaycounter<1
					
					foo\zstartdelaycounter=0
					foo\zvel#=foo\zvel#+foo\zspeed#
			
					TranslateEntity foo\entity,0,0,foo\zvel#
					
					If EntityZ(foo\entity)>foo\startz#+(foo\zdist#*.75) Then foo\zvel#=foo\zvel#*FOO\zdamp


						If EntityZ(foo\entity)>foo\startz#+foo\zdist#
							;If on_platform=1 Then yvel#=0
							foo\zdirection=0
							
							foo\zenddelaycounter=foo\enddelay#
			
						EndIf
				EndIf
			EndIf	
		
Next


Return foo.platform
End Function

;=============================================================================================

Function create_world()

midw=GraphicsWidth()/2
midh=GraphicsHeight()/2

cam=CreateCamera()
MoveEntity cam,0,10,-15

player=CreateSphere(24)
hat=CreateCube(player)
MoveEntity hat,0,1,0
ScaleMesh hat,.1,.1,1

player_piv=CreatePivot(player)

MoveEntity player_piv,0,0,-camdistance

;AddMesh hat,player
MoveEntity player,0,15,-5
EntityColor player,200,0,0
EntityShininess player,.9
EntityRadius player,.7
EntityType player,1		; type 1 = player ------ type 2 = world objects


AmbientLight 50,50,50
lite=CreateLight()

bulb=CreateSphere(6,lite)
EntityFX bulb,1
EntityColor bulb,255,255,0
PositionEntity lite,-20,40,-20

;level = LoadMesh("level.b3d")
level=CreateCube()
EntityFX level,1
EntityType level,2


mirror=CreateMirror()
ptex=CreateTexture(256,256,8)
SetBuffer TextureBuffer(ptex)
Color 40,40,80
Rect 0,0,256,256

Color 255,255,230
Rect 0,0,128,128,1
Color 0,60,0

Rect 1,1,126,126,0

Color 60,60,100

Rect 128,128,128,128,1
Color 0,0,90


Rect 129,129,127,127,0


EntityTexture level,ptex
ScaleTexture ptex,.25,.25
EntityType level,2
EntityAlpha level,.7
SetBuffer BackBuffer()

FlipMesh level
ScaleEntity level,25,25,25
MoveEntity level,0,25,0






End Function
;=============================================================================================

Function move_player()

	; camera smoothing
	smoothcam(player_piv,player,25)

	If KeyDown(57) And jumping=0
		jumping=1
		pyvel#=.2
	EndIf
 
	; boost# could be if you pick up an object it would increase speed for a time

   	If KeyDown(17)=1 Or KeyDown(200) Then speed#=speed#+.005 +boost#
	If KeyDown(30)=1 Or KeyDown(203) Then turnspeed# = turnspeed# + .2 +boost#
	If KeyDown(31)=1 Or KeyDown(208) Then speed# = speed# -(.005 +boost#)
	If KeyDown(32)=1 Or KeyDown(205) Then turnspeed# = turnspeed# - .2 +boost#

	; FRICTION FOR SMOOTH MOVEMENT
	
	turnspeed#=turnspeed#*.94
	speed#=speed#*.95
		
	TurnEntity player,0,turnspeed#+pyspin#,0 ;  turn pivot left --right
	If on_platform=0 Then pyvel#=pyvel#-gravity#
	
	;move the player 
	MoveEntity player,0,pyvel#,speed#
	TranslateEntity player,pxvel#,0,pzvel#
	pxvel#=pxvel#*.9
	pzvel#=pzvel#*.9
	
	PointEntity cam,player
	
	alpha_platforms.platform() ; so we can see the player
	
	
FlushKeys
FlushMouse
	
End Function


;=============================================================================================
Function fps_camera()

	
 	If KeyDown(57) And jumping=0 ; SPACE to JUMP
		jumping=1
		pyvel#=.2
	EndIf
 
   	If KeyDown(17)=1 Or KeyDown(200) Then speed#=speed#+.005 +boost#
	If KeyDown(30)=1 Or KeyDown(203) Then lateral_speed# = lateral_speed# - .004 +boost#
	If KeyDown(31)=1 Or KeyDown(208) Then speed# = speed# -(.005 +boost#)
	If KeyDown(32)=1 Or KeyDown(205) Then lateral_speed# = lateral_speed# + .004 +boost#

	; FRICTION FOR SMOOTH MOVEMENT
	
	lateral_speed#=lateral_speed#*.97
	speed#=speed#*.95
		
	PositionEntity cam,EntityX(player),EntityY(player)+1,EntityZ(player)

	; CAMERA MOVEMENTS
	MY#=curvevalue#(MouseYSpeed(),MY#,4 )
	MX#=curvevalue#(MouseXSpeed(),MX#,4 )
	TurnEntity cam,MY#,0,0 ; turn camera up and down
	TurnEntity player,0,-mx+pyspin#,0 ;  turn pivot left --right
	RotateEntity cam,EntityPitch(cam),EntityYaw(player),0
	MoveMouse midw,midh; Bring mouse to middle of screen for mouselook to work
	
	
	
	If on_platform=0 Then pyvel#=pyvel#-gravity#
	;pyvel#=pyvel#*.99

	;move the player --- the camera in this case!
	MoveEntity player,lateral_speed#,pyvel#,speed#
	
	TranslateEntity player,pxvel#,0,pzvel#
	
	
	pxvel#=pxvel#*.9
	pzvel#=pzvel#*.9

		
	
FlushKeys
FlushMouse
	
End Function

Function do_collisions.platform()

For col=1 To CountCollisions(player)
		cnx#=CollisionNX(player,col)
		cny#=CollisionNY(player,col)
		cnz#=CollisionNZ(player,col)

		If cnx#<>0
			pxvel#=pxvel#+cnx/10
		EndIf
		If cnz#<>0
			pzvel=pzvel+cnz/10
		EndIf

		
		If cny#>0
			pyvel=Abs(pyvel)*.8 ; bounce 
			jumping=0
		EndIf
		If cny#<0
			pyvel=pyvel-.05
			For foo.platform=Each platform
				If CollisionEntity(player,col)=foo\entity
			      foo\ydirection=1-foo\ydirection
					foo\yvel#=0
					
					;wire=1-wire
				EndIf
			Next
		EndIf	
	Next
Return foo.platform
End Function
;====================================================================================

;curvevalue()
Function curvevalue#(newvalue#,oldvalue#,increments# )
	If increments>1 Then oldvalue#=oldvalue#-(oldvalue#-newvalue#)/increments
	If increments<=1 Then oldvalue=newvalue
	Return oldvalue#
End Function

;====================================================================================

Function pick_platform.platform()
px#=EntityX(player)
py#=EntityY(player)
pz#=EntityZ(player)
picklength#=.7
	
	LinePick(px#,py#-picklength,pz#,0,picklength#,0,1)
		up_pict=0
		up_pict=PickedEntity()
	LinePick(px#,py#+picklength#,pz#,0,-picklength#,0,1)
		down_pict=0
		down_pict=PickedEntity()
	
	on_platform=0
	pyspin#=0
	
	If up_pict<>0 Or down_pict<>0
		
		For foo.platform=Each platform
			
			EntityFX foo\entity,0
			If foo\entity=down_pict
				EntityFX foo\entity,1
				pyvel#=foo\yvel#  + foo\yvel/50
				pyspin#=foo\yspin#
				pxvel#=foo\xvel#
				pzvel#=foo\zvel#

				
				
				on_platform=1
				jumping=0
				;ResetEntity player
			EndIf
			
			If foo\entity=up_pict
				;pictentity=foo\entity
				pyvel#=pyvel#-.01
				foo\ydirection=1
				foo\yvel=0
				
				
				;ResetEntity player
			EndIf

			
		Next
	EndIf
	
	


Return foo.platform

End Function
;=============================================================================================


Function mess$(x,y,message$)

; styles


	leftoffset= StringWidth(message$)*.1
	width=StringWidth(message$)*1.2
	height=FontHeight()*1.2
	
	Color 0,0,0
	Rect x+3,y+3,width,height+3,1 ; shadow
	
	Color 0,0,60
	
	Rect x-leftoffset,y,width,height,1	; draw a rectangle

	Color 0,140,0
	Rect x-leftoffset-1,y-1,width+1,height+1,0
	Rect x-leftoffset-2,y-2,width+2,height+2,0

	
	Color 255,255,0
	
	Text x,y,message,0,0					; print message inside the rectangle

	
End Function

;=============================================================================================

Function draw_overlay()

If fps=1 Then draw_crosshairs()

; Draw the FPS rect
			
					
			; screen messages ************
									
			
					
					
							
							mess$(60,5,"WASD or arrows to move, SPACE to JUMP")
							;If pictentity<>0 Then mess$(60,180,"distance"+EntityDistance(player,pictentity))
							If fps=0 Then mess$(60,30,"press 2 for FPS MODE")
							If fps=1 Then mess$(60,30,"press 1 for CHASE-CAM MODE")
							;If on_platform=1 Then mess$(60,220,"ON A PLATFORM")
							;If on_platform=0 Then mess$(60,220,"OFF PLATFORMS")

							;mess$(60,260,"x= "+EntityX(player))
							;mess$(60,280,"y= "+EntityY(player))
							;mess$(60,300,"z= "+EntityZ(player))

							;mess$(60,260,"fill this in")
	
					
					
End Function


;=============================================================================================

Function smoothcam(pivot,target,camspeed)


	curx#=EntityX(cam)
	curz#=EntityZ(cam)
	destx#=EntityX(pivot,True)
	destz#=EntityZ(pivot,True)
	
	curx#=curx#+((destx#-curx#)/camspeed)
	curz#=curz#+((destz#-curz#)/camspeed)
	
	PositionEntity cam,curx,EntityY(target)+camheight#,curz
	
End Function


;=============================================================================================

Function draw_crosshairs()

Color 0,235,0
Rect midw-3,midh-3,6,6,0
;Line midw-6,midh,midw+6,midh
End Function


;=============================================================================================

Function alpha_platforms.platform()

	; get the distance from the camera to the player this could be static for speed increase
 camdist_to_player#=EntityDistance(cam,player)-3



 For foo.platform = Each platform  
	; now see if the platform is between the player and the camera
	camdist_to_platform#=EntityDistance(cam,FOO\ENTITY)
		If camdist_to_player>camdist_to_platform
			EntityAlpha FOO\ENTITY,.7
		Else
			EntityAlpha FOO\ENTITY,1
		EndIf
 Next

	



Return foo.platform

End Function


;=============================================================================================
Function save_bitmap()

Repeat
Until KeyDown(59)=0
	SaveBuffer(FrontBuffer(),"screenshot.bmp")


End Function


;=============================================================================================

Function create_tex()
	
	
	
disctex=CreateTexture(256,256,64+8)

SetBuffer TextureBuffer(disctex)
For x=1 To 230
    Color Rnd(100,255),Rnd(100,255),Rnd(100,255)
	Oval Rnd(0,255),Rnd(0,255),Rnd(8,32),Rnd(8,32),1
Next


boxtex=CreateTexture(32,32,8)
SetBuffer TextureBuffer(boxtex)
Color 200,200,200
Rect 0,0,32,32

Color 30,30,30
Rect 0,0,16,16,1
Rect 16,16,16,16,1
ScaleTexture boxtex,.5,.5


grasstex=CreateTexture(127,127,8)
SetBuffer TextureBuffer(grasstex)
Color 0,128,0
Rect 0,0,128,128

For x=0 To 128
For y=0 To 128
	count=count+1
	If count=5 
		count=1
		Color Rnd(0,20),Rnd(0,155),0
		Line x,y,x+3,y+3
	EndIf
Next
Next

SetBuffer BackBuffer()


End Function
