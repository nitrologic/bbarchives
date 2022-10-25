; ID: 2949
; Author: Nexinarus
; Date: 2012-06-18 03:20:28
; Title: Basic &amp; Charged Jumping in 3D
; Description: Single Jumps

Global mup=200,MDN=208,mlt=203,mrt=205,mjp=57,mst=28
;input user keys here before the program starts
Color 255,255,255
For x=0 To 6
	Cls
	Text 10, 0,"   Up:"
	Text 10,12," Down:"
	Text 10,24," Left:"
	Text 10,36,"Right:"
	Text 10,48," Jump:"
	Text 10,60,"Shoot:"
	If X<6 Text 10,84,"[ESC] Use Defaults"
	
	If x<6 Rect 58,x*12,8,12
	If x>0 Text 58,0,mup
	If x>1 Text 58,12,mdn
	If x>2 Text 58,24,mlt
	If x>3 Text 58,36,mrt
	If x>4 Text 58,48,mjp
	If x>5 Text 58,60,mst
	If x=6 Text 10,84,"Press any key to continue..."
.redo
	confirm=False
	FlushKeys
	Repeat
		For keyz=0 To 255
			If KeyHit(keyz)
				If x=0 mup=keyz
				If x=1 mdn=keyz
				If x=2 mlt=keyz
				If x=3 mrt=keyz
				If x=4 mjp=keyz
				If x=5 mst=keyz
				confirm=True
			End If
		Next
	Until confirm=True
	If X<6
		If MUP=1 Or MDN=1 Or MLT=1 Or MRT=1 Or MJP=1 Or MST=1
			mup=200
			MDN=208
			mlt=203
			mrt=205
			mjp=57
			mst=28
			x=5
		End If
	End If	
	If x=0
		If mup=15 Goto redo
	ElseIf x=1
		If mdn= 15 Goto redo
		If mdn=mup Goto redo
	ElseIf x=2
		If mlt= 15 Goto redo
		If mlt=mup Goto redo
		If mlt=mdn Goto redo
	ElseIf x=3
		If mRt= 15 Goto redo
		If mRT=mup Goto redo
		If mRT=mdn Goto redo
		If mRT=mLT Goto redo
	ElseIf x=4
		If mJP= 15 Goto redo
		If mJP=mup Goto redo
		If mJP=mdn Goto redo
		If mJP=mLT Goto redo
		If mJP=mRT Goto redo
	ElseIf x=5
		If mST= 15 Goto redo
		If mST=mup Goto redo
		If mST=mdn Goto redo
		If mST=mLT Goto redo
		If mST=mRT Goto redo
		If mST=mJP Goto redo
	End If
Next
;ADDED DATA---------------------------------------
RESTOREBMP ;CREATE CHARACTGER AND BULLET AND BEAM
;-------------------------------------------------

Const PERIOD=32 ;(1024/PERIOD) = FPS (FRAMES PER SECOND)

;SET GRAPHICS MODE AND LOAD STUFF HERE.
Graphics3D 640,480,16,3

Global TIME=MilliSecs()-PERIOD,ELAPSED,FRAMETICKS,TWEEN#
;TIME = CALCULATE TIME PERIOD
;ELLAPSE = TIME ELLAPSED (TIME PASSED) This changes throughout the program
;FRAMETICKS = How many frames have ellapsed (passe)
;TWEEN# = used to get inbetween frames


;ADDED DATA--------------------------------------------------------------------
Const CLSN_PLYR=1,CLSN_OBJS=2,CLSN_GRND=3
Collisions CLSN_PLYR,CLSN_OBJS,2,2
Collisions CLSN_PLYR,CLSN_GRND,2,2

AmbientLight 84,168,252

PLANE1TEX=CreateTexture(32,32)
SetBuffer TextureBuffer(PLANE1TEX)
Color 84,84,252
Rect 0,0,32,32
For ALLLINES=0 To 2
	Color 42-(ALLLINES Mod 2)*42,42-(ALLLINES Mod 2)*42,210-(ALLLINES Mod 2)*42
	Line ALLLINES,1,ALLLINES+29,30
	Line ALLLINES,30,ALLLINES+29,1
Next
Color 0,0,0
For ALLSQUARES=0 To 3
	Rect ALLSQUARES/2*16,16*(ALLSQUARES Mod 2),16,16,0
Next
Color 0,0,168
Line 0,0,31,31
Line 0,31,31,0

PLANE2TEX=CreateTexture(32,32)
SetBuffer TextureBuffer(PLANE2TEX)
Color 252,84,84
Rect 0,0,32,32
For ALLLINES=0 To 2
	Color 210-(ALLLINES Mod 2)*42,42-(ALLLINES Mod 2)*42,42-(ALLLINES Mod 2)*42
	Line ALLLINES,1,ALLLINES+29,30
	Line ALLLINES,30,ALLLINES+29,1
Next
Color 0,0,0
For ALLSQUARES=0 To 3
	Rect ALLSQUARES/2*16,16*(ALLSQUARES Mod 2),16,16,0
Next
Color 168,0,0
Line 0,0,31,31
Line 0,31,31,0
;------------------------------------------------------------------------------

SetBuffer BackBuffer() ;Double buffering


;ADDED DATA--------------------------------------------------------------------

;cubes
cube=CreateCube()
EntityColor cube,1,1,255
ScaleEntity cube,.5,.5,.5
PositionEntity cube,-5,1.5,0
EntityType cube,clsn_objs

cube=CreateCube()
EntityColor cube,255,1,1
ScaleEntity cube,.25*2,.25*2,.25*2
PositionEntity cube,0,.25*2,0
EntityType cube,clsn_objs

cube=CreateCube()
EntityColor cube,1,255,1
ScaleEntity cube,.25*6,.25*12,.25*6
PositionEntity cube,5,.25*12,0
EntityType cube,clsn_objs

;light
btllight=CreateLight(2)
PositionEntity btllight,0,10,0

;charsize x,y
sizex=16*1
sizey=16*1
plyrdir=0 ;player direction
WALKSPEED=2 ;PLAYER'S WALKING SPEED

player1=CreateSprite()
plyr1tex=LoadAnimTexture("char1.bmp",7,32,32,0,2)
EntityTexture player1,plyr1tex,0
SpriteViewMode player1,2
PositionEntity player1,0,5,0
ScaleSprite player1,0.03125*sizex,0.03125*sizey
EntityRadius player1,0.0078125*sizex,0.015625*sizey
EntityType player1,clsn_plyr
;delete when done----------
sphere=CreateSphere(32,player1)
EntityAlpha sphere,.5
ScaleEntity sphere,0.0078125*sizex,0.015625*sizey,0.0078125*sizex
PositionEntity sphere,0,0,0
;--------------------------

;player camera
camera=CreateCamera(player1)
CameraViewport camera,0,0,GraphicsWidth(),GraphicsHeight()
CameraProjMode camera,2
PositionEntity camera,0,10,-10
RotateEntity camera,45,0,0
CameraZoom camera,.5
;fixed camera
smlcam=CreateCamera()
CameraViewport smlcam,GraphicsWidth()*7/8,GraphicsHeight()*7/8,GraphicsWidth()/8,GraphicsHeight()/8
CameraProjMode smlcam,2
PositionEntity smlcam,3,10,-10
RotateEntity smlcam,45,0,0
CameraZoom smlcam,.5

;battlefield
btlcube=CreateCube()
ScaleEntity btlcube,12,Float(1)/256,4
ScaleTexture plane1tex,Float(1)/24,Float(1)/8
EntityTexture btlcube,plane1tex
PositionEntity btlcube,0,0,0
EntityType btlcube,clsn_grnd

;exitfield
exitcube=CreateCube()
ScaleEntity exitcube,12+8,Float(1)/256,4+8
ScaleTexture plane2tex,Float(1)/40,Float(1)/24
EntityTexture exitcube,plane2tex
PositionEntity EXITcube,0,-Float(1)/256,0
EntityType EXITcube,clsn_grnd

FLUSH ; FLUSH INPUTS
;------------------------------------------------------------------------------


;BEGINNING OF PROGRAM

Repeat
	;WAIT FOR ELLAPSED TIME
	Repeat
		ELAPSED=MilliSecs()-TIME
	Until ELAPSED
	
	FRAMETICKS=ELAPSED/PERIOD ;how many 'frames' have elapsed
	
	Tween#=Float(elapsed Mod period)/Float(period) ;fractional remainder
	
	;check each frame
	For checkticks=1 To frameticks
		time=time+period ;goto next time period
		If checkticks=frameticks Then CaptureWorld ; capture each frame in 3d world within time period
		;place images/entties and inputs here--------------------------------------------
	
		
		;ADDED DATA----------------------------------
		If jroutine=1;new jump routine stumped

;----------------------------original charge jump code
;			velud=veluD-1
;			If CountCollisions(player1)>0 velud=-1
;			If KeyHit(mjp)
;				If CountCollisions(player1)>0 velud=8;8,11,14,16,18 (1-5 blocks respectfully)
;			End If
;			If KeyDown(mjp)=True
;				tmpvud=tmpvud+1
;				If tmpvud>18 tmpvud=18
;			ElseIf KeyDown(mjp)=False
;				If CountCollisions(player1)>0 velud=tmpvud
;				tmpvud=-1
;			End If
;------------------------------------------------------

			velud=veluD-1
			For checkallCollisions=1 To CountCollisions(player1)
				If CollisionNY(player1,checkallcollisions)>0;0.03125*sizey
					velud=-1
					Exit
				ElseIf CollisionNY(player1,checkallcollisions)<0;-0.03125*sizey
					velud=-1
					Exit
				End If
			Next

			If KeyHit(mjp)
				For checkallCollisions=1 To CountCollisions(player1)
					If CollisionNY(player1,checkallcollisions)>0;0.03125*sizey
						velud=8
						Exit
					End If
				Next
			End If
			If KeyDown(mjp)=True
				tmpvud=tmpvud+1
				If tmpvud>18 tmpvud=18
			ElseIf KeyDown(mjp)=False
				For checkallCollisions=1 To CountCollisions(player1)
					If CollisionNY(player1,checkallcollisions)>0;0.03125*sizey
						velud=tmpvud
						Exit
					End If
				Next
				tmpvud=-1
			End If

		ElseIf jroutine=0;jump routine with help from blitz forum community
			If jump=False
				If CountCollisions(player1)=0 jump=True
				velud=0
			End If
			velud=velud-1
			For checkallCollisions=1 To CountCollisions(player1)
				If CollisionNY(player1,checkallcollisions)>0;0.03125*sizey
					jump=False
					velud=-1
					Exit
				ElseIf CollisionNY(player1,checkallcollisions)<0;-0.03125*sizey
					jump=True
					velud=-1
					Exit
				End If
			Next
			If KeyHit(mjp)
				For checkallCollisions=1 To CountCollisions(player1)
					If CollisionNY(player1,checkallcollisions)>0;0.03125*sizey
						jump=False
						velud=-1
						Exit
					End If
				Next
				If jump=False
					velud=8;8,11,14,16,18 (1-5 blocks respectfully)
					jump=True
				End If
			End If
		End If
		If KeyHit(15) jroutine=Not jroutine ;tab ;toggles jumping routines
		
		;move up
		If KeyDown(mup)=True
			If CountCollisions(PLAYER1)=0
				If VELNS=0 VELN=1
			ElseIf CountCollisions(PLAYER1)>0
				veln=veln+walkspeed
				If veln>5*walkspeed veln=5*walkspeed
			End If
		ElseIf KeyDown(mup)=False
			If CountCollisions(PLAYER1)>0
				veln=veln-walkspeed
				If veln<0 veln=0
			End If
		End If
		;move down
		If KeyDown(mdn)=True
			If CountCollisions(PLAYER1)=0
				If VELNS=0 VELS=1
			ElseIf CountCollisions(PLAYER1)>0
				vels=vels+walkspeed
				If vels>5*walkspeed vels=5*walkspeed
			End If
		ElseIf KeyDown(mdn)=False
			If CountCollisions(PLAYER1)>0
				vels=vels-walkspeed
				If vels<0 vels=0
			End If
		End If
		velns=veln-vels ;balances north(up) and south(down) movement
		;move left
		If KeyDown(mlt)=True
			If CountCollisions(PLAYER1)=0
				If VELWE=0 VELW=1
			ElseIf CountCollisions(PLAYER1)>0
				velw=velw+walkspeed
				If velw>5*walkspeed velw=5*walkspeed
			End If
		ElseIf KeyDown(mlt)=False
			If CountCollisions(PLAYER1)>0
				velw=velw-walkspeed
				If velw<0 velw=0
			End If
		End If
		;move RIGHT
		If KeyDown(mrt)=True
			If CountCollisions(PLAYER1)=0
				If VELWE=0 VELE=1
			ElseIf CountCollisions(PLAYER1)>0
				velE=velE+walkspeed
				If velE>5*walkspeed velE=5*walkspeed
			End If
		ElseIf KeyDown(mRT)=False
			If CountCollisions(PLAYER1)>0
				velE=velE-walkspeed
				If velE<0 velE=0
			End If
		End If
		velWE=velE-velW ;balances west(left) and east(right) movement
		If velwe<>0 tmpdir=-(Sgn(velwe)-1)/2 ;calculate char dir using valwe
		If tmpdir<>plyrdir
			plyrdir=tmpdir
			direction(player1,plyr1tex,tmpdir)
		End If
		;CHARACTER MOVEMENT
		MoveEntity player1,Float(velwe)/160,Float(velud)/32,Float(velns)/160
		;--------------------------------------------
		;--------------------------------------------------------------------------------
		UpdateWorld ;UPDATE YOUR 3D WORLD
	Next
	RenderWorld TWEEN# ; RENDERS CURRENT SCREEN FROM BACKGROUND BUFFER (INCLUDING TWEENING)
	
	;ADDED TEXT---------------------------------------------------------
	Color 255,255,255
	Text 0, 0,EntityX(PLAYER1)+","+EntityY(PLAYER1)+","+EntityZ(PLAYER1)
	Text 0,10,VELNS+", "+VELWE+", "+VELUD
	Text 0,20,"[TAB] Toggle jumping: "+jroutine
	Text 20,30,"press [JUMP] to Jump."
	If jroutine=1 Text 20,40,"hold & release [JUMP] to ChargeJump"
	;-------------------------------------------------------------------
	Flip ; show new screen update
Until KeyHit(1) Or (EntityCollided(Player1,CLSN_GRND)=exitcube) Or (EntityY(player1)<-2 Or EntityY(player1)>11);END MAIN LOOP

;CLEAN UP STUFF
time=0
elapsed=0
frameticks=0
tween#=0
checkticks=0

ClearWorld ;remove entities, brushes, textures from memory (anything missed)
ClearCollisions ;clear all collisions from memory
EndGraphics
flush


Function DIRECTION(SPR,TEX,FRM);CHANGE IMAGE ON SPRITE
	EntityTexture SPR,TEX,FRM
End Function

Function flush() ;flush all inputs from memory
	FlushKeys ;clear keyboard presses
	FlushMouse ;clear mouse clicks
	FlushJoy ;clear joystick presses
End Function

Function restorebmp()
	;set gfxmode
	Graphics 320,240,16,3
	;Create Image
	tmp=CreateImage(64,32)
	;set buffer to image
	SetBuffer ImageBuffer(tmp)
	;if char1.bmp doesn't exist... make it
	If FileType("char1.bmp")=2 DeleteDir "char1.bmp"
	If FileType("char1.bmp")=0
		Cls
		Restore char
		For v=0 To 31
			For h=0 To 31
				Read c
				If c>0
					Color (c-1)*84+7*(c=1),(c-1)*84+7*(c=1),(c-1)*84+7*(c=1)
					Plot h,v
					Plot 63-h,v
				End If
			Next
		Next
		SaveImage tmp,"char1.bmp"
	End If
	;if laser1.bmp doesn't exist... make it
	If FileType("laser1.bmp")=2 DeleteDir "laser1.bmp"
	If FileType("laser1.bmp")=0
		Cls
		For c=0 To 3
			Color c*63+63,c*63+63,c*63+63
			Rect 8,c+12,16,7-2*c
			Rect c+12+32,8,7-2*c,16
		Next
		SaveImage tmp,"laser1.bmp"
	End If
	;if bullet1.bmp doesn't exist... make it
	If FileType("bullet1.bmp")=2 DeleteDir "bullet1.bmp"
	If FileType("bullet1.bmp")=0
		Cls
		For c=0 To 3
			Color c*63+63,c*63+63,c*63+63
			Oval c*3/2+8,c*3/2+8,16-2*(c*3/2),16-2*(c*3/2)
			Oval c+2+32+8,c+2+8,16-2*(c+2),16-2*(c+2)
		Next
		SaveImage tmp,"bullet1.bmp"
	End If
End Function


.char
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,1,2,2,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,1,2,2,2,2,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,1,2,2,4,4,4,1,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,1,2,4,4,4,4,1,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,1,4,4,4,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,4,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,1,4,4,4,4,1,0,1,1,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,1,4,4,1,4,4,4,1,2,2,1,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,1,3,3,1,1,4,1,4,3,2,2,1,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,1,2,2,2,1,4,1,1,3,2,2,1,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,1,2,2,2,1,2,2,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,1,1,1,2,1,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,1,2,2,1,2,2,1,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,1,3,3,3,1,3,3,3,1,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
