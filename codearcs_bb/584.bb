; ID: 584
; Author: Rob Pearmain
; Date: 2003-02-11 04:44:39
; Title: Mario64 Style Camera Control
; Description: Code to show how to implement Mario64 Style Camera and Player Control

;      Mario64 style camera and player control

;      by Rob (Speccyman) Pearmain 
;      rob.pearmain@ioko365.com
;      http://www.peargames.com
;      11th February 2003


;      After asking for lots of help and searching the NET I could not find
;      code that acurately emultated Mario64 control.

;      This is my attempt, and can be vastly improved no doubt, please feel
;      free to modify.

;      I studied Mario64 carefully on my N64 emulator, and decided that the
;      best control was on the level where he jumps into the picture and has
;      to meet the bombs etc.

;      The theory is, when Mario runs forwards, the camera follows.  When he runs
;      back the camera is pushed back viewing Mario running towards you.
;      When you turn left or right, you slowly turn around the camera pivot.

;      IMPORTANT.  When you take your finger off a key, the camera auto corrects
;      itself around the back of Mario, in a smooth rotating way, NOT, flying through
;      the player mesh or messing up the camera angles.

;      When you run forward, you run in the direction THE CAMERA is facing at the time

; ------------------------------------------------------------------
; Game's frames-per-second setting
; ------------------------------------------------------------------

Global gameFPS = 60

; ------------------------------------------------------------------
; Open 3D display mode
; ------------------------------------------------------------------

Graphics3D 640, 480

; The player consists of a pivot to control which direction he is
; moving, and a mesh which is the model.  The model rotates independently
; of the pivot to achieve the smooth turn to new direction functionality
; that exists in Mario64

global oPlayerPivot = createpivot()
entityradius oPlayerPivot,1
entitytype oPlayerPivot,1

global oPlayerMesh = LOADANIMMESH("mariorun.x")

; Change these settings depending on your mesh
scaleentity oPlayerMesh,.2,.2,.2
animate oPlayermesh,1,1

; The targetpivot will always sit in the same position as the player pivot
; but will rotate independently, that is why it is not a child.
; The camera will aim for the TargetOrbitPivot.
global oTargetPivot = createPivot()
global otargetorbitpivot = createpivot(oTargetPivot)

; Depending on how far you move the orbit pivot from the center it will
; effect settings on smoothing the camera and speed, experiment carefully.
; Here, I move it 15 units in fromt of the player.  It will always remain
; 15 units away from the player.
moveentity otargetorbitpivot,0,5,15

; Finally, we create the camera pivot, and attached a real camera to it.
global oCameraPivot = createpivot()
global cam = CreateCamera ()


; IMPORTANT, set the height of the camera
;positionentity cam,0,10,0

;      Milliseconds to wait after keypress for camera to adjust
;      Set this to 0 for no pause
Const CAMERAPAUSE=500

Global waitbeforecameraadjust=millisecs()

;      Counter for jump
Global nJump#
; ------------------------------------------------------------------
; Use double buffered animation (automatic, but I prefer this!)
; ------------------------------------------------------------------

SetBuffer BackBuffer ()


; ------------------------------------------------------------------
; General setup
; ------------------------------------------------------------------

; Set up a plane surface so we can see that we are running

plane=createplane()
tex=loadtexture("grass.jpg")
scaletexture tex,12,12
entitytexture plane,tex
positionentity plane,0,-1,0
entitytype plane,2

collisions 1,2,2,2

; ------------------------------------------------------------------
; Frame limiting code setup
; ------------------------------------------------------------------

framePeriod = 1000 / gameFPS
frameTime = MilliSecs () - framePeriod

fps=0
framecount=0
oldfps=0
Repeat

	; --------------------------------------------------------------
	; Frame limiting
	; --------------------------------------------------------------

	Repeat
		frameElapsed = MilliSecs () - frameTime
	Until frameElapsed

	frameTicks = frameElapsed / framePeriod

	frameTween# = Float (frameElapsed Mod framePeriod) / Float (framePeriod)

	; --------------------------------------------------------------
	; Update game and world state
	; --------------------------------------------------------------

	For frameLimit = 1 To frameTicks

		If frameLimit = frameTicks Then CaptureWorld
			frameTime = frameTime + framePeriod

			UpdateGame ()
			fps = fps + 1
		UpdateWorld

	Next

	; --------------------------------------------------------------
	; **** Wireframe for DEBUG only -- remove before release! ****
	; --------------------------------------------------------------

	If KeyHit (17): w = 1 - w: WireFrame w: EndIf ; Press 'W'

	; --------------------------------------------------------------
	; Draw 3D world
	; --------------------------------------------------------------

	RenderWorld frameTween

	; --------------------------------------------------------------
	; Show result
	; --------------------------------------------------------------


	text 0,32,"Frames per second = " + oldfps

	if millisecs()-framecount > 1000 then framecount=millisecs():oldfps=fps:fps=0
Flip

Until KeyHit (1)

End

; ------------------------------------------------------------------
; Game update routine, called from frame limiting code
; ------------------------------------------------------------------

Function UpdateGame ()


	; Put the TargetPivot and Player Model at the same spot as the Player's Pivot
	positionentity oTargetPivot,entityx(oPlayerPivot),entityy(oPlayerPivot),entityz(oPlayerPivot)
	positionentity oPlayermesh,entityx(oPlayerPivot),entityy(oPlayerPivot),entityz(oPlayerPivot)
	positionentity cam,entityx(oCameraPivot),entityy(oCameraPivot),entityz(oCameraPivot)

	; Point the camera to the orbitng target, and the camera always at the player
	pointentity oCameraPivot,otargetorbitpivot
	pointentity cam,oPlayerPivot


	;	    --------------------------
	;	    CONTROL
	;	    --------------------------

	; The following moveentity settings have been tweaked for all the other settings for pivots, experiment with care
	key=0

	;    Left
	if keydown(203) then rotateentity oPlayerPivot,0,wrapANGLE(Entityyaw#(cam,true)+90),0 : key=1

	;  Right
	if keydown(205) then rotateentity oPlayerPivot,0,wrapANGLE(Entityyaw#(cam,true)-90),0 : key=2

	;  Forward  (Move camera forward also)
	if keydown(200) then
	   ;		Check to see if Left or Right has been pressed, if so move diagonally
	   if key > 0
	      if key = 1
	                    rotateentity oPlayerPivot,0,wrapANGLE(Entityyaw#(cam,true)+45),0
	      else
	                    rotateentity oPlayerPivot,0,wrapANGLE(Entityyaw#(cam,true)-45),0
	      end if
	   else
	      rotateentity oPlayerPivot,0,wrapANGLE(Entityyaw#(cam,true)),0
	   end if

	   key=3


	end if

	;  Back (Move camera back slightly faster)
	if keydown(208) then
	   ;		Check to see if Left or Right has been pressed, if so move diagonally
	   if key > 0
	      if key = 1
	                    rotateentity oPlayerPivot,0,wrapANGLE(Entityyaw#(cam,true)+135),0

	      else
	                    rotateentity oPlayerPivot,0,wrapANGLE(Entityyaw#(cam,true)-135),0

	      end if
	   else
	      rotateentity oPlayerPivot,0,wrapANGLE(Entityyaw#(cam,true)+180),0
	   end if

	   key=4

	   moveentity oCameraPivot,0,0,-.35
	end if

	if keyhit(57)
	   if entitycollided(oPlayerPivot,2)
	      	nJump#=njump#+3
	      	key=5
	   end if

	end if

	translateentity oPlayerPivot,0,nJump#,0
	if nJump# > -.5 then nJump# = nJump#- .5
	if not entitycollided(oPlayerPivot,2) then key=5

	;   Reset adjust timer and move player
	if key>0 then
		waitbeforecameraadjust = millisecs()
		moveentity oPlayerPivot,0,0,.5
	end if

	;   ---------------------------
	;   ADJUST
	;   ---------------------------



	;  Here, we compare the YAW of the player's model compared to the direction the pivot is heading, and slowly turn it to
	;  match.  This gives the effect of smooth turning of the model
	 LWhichWayShouldITurn% = Check_DoesEntityNeedToTurnToTarget(oPlayerMesh,oPlayerPivot)

	 If lWhichWayShouldITurn% <> 0 Then
	    If lWhichWayShouldITurn% = 1 Then
	         TurnEntity oPlayerMesh,0,8,0

	    End If
	    If lWhichWayShouldITurn% = -1 Then
	         TurnEntity oPlayerMesh,0,-8,0

	    End If
	End If

	if key=0 and ((millisecs()-waitbeforecameraadjust) > CAMERAPAUSE) then
		;   If camera is far away from the target, then help the camera by pointing the target at the camera
		if entitydistance(oCameraPivot,otargetorbitpivot) > 5 then
	           pointentity oTargetPivot,oCameraPivot
		else
		;   If near, ignore the camera and move the target in line with the player
		    diff# = wrapangle((entityyaw#(oTargetPivot,true) - entityyaw#(oPlayerPivot,true)) )
		    if diff# < 180 then turnentity oTargetPivot,0,2,0
		    if diff# > 180 then turnentity oTargetPivot,0,-2,0
		end if

	end if


	;   Move the camera smoothly towards its target
        smoothcam(ocamerapivot,otargetorbitpivot,10)
        pointentity cam,oPlayerPivot

End Function

;   Cool little funtion to return a positive angle
Function WrapAngle(ang#)
	If ang > 359 Then ang = ang - 360
	If ang < 0 Then ang = 360 + ang
	Return ang
End Function

;   Smooth out the camera to its target
Function smoothcam(pivot,target,camspeed)

	curx#=EntityX(pivot)
	curz#=EntityZ(pivot)
	cury#=entityy(pivot)
	destx#=EntityX(target,True)
	destz#=EntityZ(target,True)
	desty#=EntityY(target,True)
	curx#=curx#+((destx#-curx#)/camspeed)
	curz#=curz#+((destz#-curz#)/camspeed)
	cury#=cury#+((desty#-cury#)/camspeed)

	PositionEntity pivot,curx#,cury#,curz#

End Function


Function Check_DoesEntityNeedToTurnToTarget(source_pivot,target_pivot)
;=================================================
;parameters:
; source_pivot: is the source pivot that we want to turn toward a target pivot
; target_pivot: is a target pivot where we want the source pivot to rotate to

;Returned values:
; 0 : no turn needed
; 1 : turn left
; -1 : turn right

;memo start angle and end angle, I just consider the integer parts of it, using Floor
s = Floor(EntityYaw(source_pivot)) ;this is the start yaw angle, that is, the current yaw orientation of the source
t = Floor(EntityYaw(target_pivot)) ;this is the end angle, that is, the angle we should reach

If s = t Or Abs(s-t) < 4 Then ;if the two angles are the same we do not need any rotation !
Return 0
EndIf

;the angle goes from 0,180 and 0,-180; now I normalize to 0-360
If s < 0 Then s = 360 + s
If t < 0 Then t = 360 + t

;now we found the right direction where to turn, in order to choose the shortest path:

;check if the difference is greather than 180
If Abs(s-t) > 180 Then

;check if the start angle is greater than the target angle
If s > t Then
Return 1 ;turn left
Else
Return -1 ;turn right
EndIf
Else
;check if the start angle is greater than the target angle
If s > t Then
Return -1 ;turn right
Else
Return 1 ;turn left
EndIf

EndIf

End Function
