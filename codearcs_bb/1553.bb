; ID: 1553
; Author: bingman
; Date: 2005-12-03 15:30:31
; Title: Move Stuff
; Description: Move /rotate objects and record their position

; FURNITURE MOVER

; Use as you please  'bingster'   www.stonefoundationentertainment.com


; This program lets you select an item, move/rotate where you want and then save the position.
; Handy for fine tuning object positions and /or using the coordinates in other c.g. programs.
;----------------------------------------------------------------------------------------------

;-----------------------
; CONTROLS

;-----------------------
;CAMERA
;Arrow keys move the camera forward/backward/left/right.
;Move mouse to the far left or right turns the camera. (you'll see the crosshairs move.)
;-----------------------
;SELECT OBJECT
;Put the cross hairs on the object and then click the left mouse button to select it. 
; It will flash when selected.
;-----------------------
;CHANGE OBJECT POSITION
; KEYBOARD COMMANDS
; "P" selects move position mode
;1 = RESET 'X' to "0"
;2 = RESET 'Y' to "0"
;3 = RESET 'Z' to "0"

; NUMBER PAD COMMANDS 
;4/6 move X position
;9/3 move Y position
;8/2 move Z position

;MOUSE COMMANDS
;Holding down RIGHT mouse button while moving object will change the move scale from "1" To "0.1"
;Holding down MIDDLE mouse button while moving object will change the move scale from "1" To "0.01"

;-----------------------
;CHANGE OBJECT ROTATION
; KEYBOARD COMMANDS
; "R" selects rotation mode
;4 = RESET 'Pitch' to "0"
;5 = RESET 'Yaw' to "0"
;6 = RESET 'Roll' to "0"

; NUMBER PAD  COMMANDS
;8/2 Pitch
;9/3 Yaw
;4/6 Roll

;MOUSE COMMANDS
;Holding down RIGHT mouse button while rotating object will change rotation scale from "1" To "0.1"
;Holding down MIDDLE mouse button while rotating object will change rotation scale from "1" To "0.01"

;-----------------------
; SAVE DATA COMMANDS
;"W" writes the selected object postion to the file "locations.bb"
;The file is written in Blitz.bb format. 

;EXAMPLE of "locations.bb" file content
;PositionEntity Cube01,10,20,30
;RotateEntity Cube01,0,90,45

;-----------------------
; NOTES
; 1. File save name is "locations.bb" ( you can name it anything you want)
; 2. Object must be selected to save the location data. ("W" to write to "locations.bb")
; 3. Object must have collision detection enabled.
; 4. Object should have name so that you can identify it in "locations.bb"
; 5. Once an object is rotated off axis, it will move/rotate relative to its new coordinates.

;-----------------------
;EXAMPLE
;This example has a simple cube.(see below) 
;You can create an Include file with the stuff you want to move. (Include "mystuff.bb")


;++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
;--------- GRAPHICS -------
Graphics3D 800,600,16,1
SetBuffer BackBuffer()

;--------- CAMERA ---------
camera=CreateCamera()
CameraRange camera,.1,5000
PositionEntity camera, 100,30,100
RotateEntity camera,0,0,0

;--------- FILE / POSITION /ROTATION VARIABLES -----------------
Global DataFile = WriteFile ("locations.bb") ; Name of File with position/rotation data
Global MyData$ = ""
entity=0
Global DataScale# = 1
Global movex# = 0
Global movey# = 0
Global movez# = 0
Global myX# = 0
Global myY# = 0
Global myZ# = 0
Global myYaw# = 0
Global myPit# = 0
Global myRol# = 0
Global myScX# = 1
Global myScY# = 1
Global myScZ# = 1

Global MyName$ = "my name"
Global DataFlag = 0

;---------- FRAME RATE -----
Global FRate  
Global ftime1,ftime2,ftime3,ftime4
SetFPSLimit(50)

;----------- COLLISION SETUP --------
Const C_PLAYER 	= 1
Const C_LEVEL	= 2
Collisions C_PLAYER,C_LEVEL,2,2
Global particlecount = 0
Global maxparticles  = 400

;-------- GROUND PLANE -----
p2 = CreatePlane(10,p)
EntityAlpha p2,1
EntityColor p2,0,0,0
PositionEntity p2,0,-2,0
EntityPickMode p2,2
EntityType P2,C_LEVEL,True

; ---------- EXAMPLE OBJECT TO MOVE --------
Cube01 = CreateCube()
NameEntity Cube01,"Cube01"
ScaleEntity Cube01,10,10,10
PositionEntity Cube01, 0,10,0
EntityPickMode Cube01,2
EntityType Cube01,C_LEVEL,True

Cube02 = CreateCube()
NameEntity Cube02,"Cube02"
ScaleEntity Cube02,10,10,10
PositionEntity Cube02, -50,20,-50
EntityPickMode Cube02,2
EntityType Cube02,C_LEVEL,True


;-------------------------------------------
;*************** MAIN LOOP *******************8
 While Not KeyHit(1)

If KeyDown(200) Then MoveEntity camera,0,0,1 ;up
If KeyDown(208) Then MoveEntity camera,0,0,-1;down
If KeyDown(203) Then MoveEntity camera,-1,0,0;left
If KeyDown(205) Then MoveEntity camera,1,0,0;right

	x=MouseX()
	y=MouseY()
	
	If x<32 TurnEntity camera,0,1,0
	If x>640-32 TurnEntity camera,0,-1,0
	If e<>entity
	If entity Then EntityAlpha entity,1
	EndIf
;---------- DATA SCALE 1 or 0.1 ----------
	If MouseDown(2) 
	DataScale = 0.1
	ElseIf MouseDown(3) 
	DataScale = 0.01
	Else
	DataScale = 1
	EndIf
;----- SET POSITION or ROTATION MODE -----
If KeyDown(25) Then DataFlag = 0  ;P
If KeyDown(19) Then DataFlag = 1  ;R

;------------- SELECT ENTITY -------------
	If MouseDown(1) 
	If entity Then EntityAlpha entity,1
	e=CameraPick( camera,x,y )
	entity=e
	EndIf
	If entity
	EntityAlpha entity,Sin( MilliSecs() )*.5+.5
	If KeyDown(75) Then movex = 1 * DataScale
	If KeyDown(77) Then movex = -1 * DataScale
	If KeyDown(73) Then movey = 1 * DataScale
	If KeyDown(81) Then movey = -1 * DataScale
	If KeyDown(80) Then movez = 1 * DataScale
	If KeyDown(72) Then movez = -1 * DataScale
	
;---------------- RESET POSITION -------------
    If KeyDown(2)  Then PositionEntity entity,0,MyY,MyZ
    If KeyDown(3)  Then PositionEntity entity,MyX,0,MyZ
    If KeyDown(4)  Then PositionEntity entity,MyX,MyY,0

;---------------- RESET ROTATION -------------
    If KeyDown(5)  Then RotateEntity entity,0,MyYaw,MyRol
    If KeyDown(6)  Then RotateEntity entity,MyPit,0,MyRol
    If KeyDown(7)  Then RotateEntity entity,MyPit,MyYaw,0

;--------------- MOVE / ROTATE OBJECT --------
If DataFlag = 0 Then MoveEntity entity, movex,movey,movez ;x/y/z
If DataFlag = 1 Then TurnEntity entity, movez,movex,movey ;pit/yaw/roll

;------- GET OBJECT POSTION / ROTATION -------
    MyName$ =EntityName$(entity)
    MyX = EntityX#(entity)
	MyY = EntityY#(entity)
	MyZ = EntityZ#(entity)

	MyPit =EntityPitch#(entity)
	MyYaw =EntityYaw#(entity)
	MyRol =EntityRoll#(entity)

;-------------- WRITE DATA -------------------
	If KeyDown(17)
	MyData$ = "PositionEntity " +MyName$ +"," + MyX +"," + MyY + "," +MyZ
	WriteLine(DataFile,MyData$)
	MyData$ = "RotateEntity " +MyName$ +"," + MyPit +"," + MyYaw + "," +MyRol
	WriteLine(DataFile,MyData$)
	While KeyDown(17)
	Wend
	EndIf
;------------
	movex = 0
	movey = 0
	movez = 0
	Else
	EndIf
     
	UpdateWorld
	RenderWorld 
	
	Rect x,y-3,1,7	
	Rect x-3,y,7,1

Color 255,0,0
If DataFlag = 0
Text 20,0,  "Position"
Text 20,20, "Name = " + MyName$
Text 20,40, "X= " + MyX#
Text 20,60, "Y= " + MyY# 
Text 20,80, "Z= " + MyZ# 	
EndIf

If DataFlag = 1
Text 20,0,  "Rotation"
Text 20,20, "Name =" + MyName$
Text 20,40, "Pitch= " + MyPit#
Text 20,60, "Yaw= " + MyYaw# 
Text 20,80, "Roll= " + MyRol# 	
EndIf


Flip
Wend
CloseFile(DataFile)
End
;*************** END MAIN LOOP *******************8


Function SetFPSLimit(f)
	frate=(1000/f)
End Function

Function LimitFPS()
	ftime2 = MilliSecs() 
	ftime3 = ftime2 - ftime1 
	ftime4 = fRate - ftime3 
	Delay ftime4 
	ftime1 = MilliSecs() 
End Function

;
; Recursive EntityPickmode
Function EntityPickmodeRec(entity,pick_geometry,obscurer=False)
	If entity = 0 Then Return
	EntityPickMode entity,pick_geometry,obscurer
	children = CountChildren(entity)
	For child = 1 To children
	EntityPickmodeRec(GetChild(entity,child),pick_geometry,obscurer)
	Next
