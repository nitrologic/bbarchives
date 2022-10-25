; ID: 1828
; Author: kevin8084
; Date: 2006-09-30 12:39:25
; Title: 3d Maze Generator
; Description: Creating a 3d Maze

; **************************************************************************************************
; Simple 3d Maze Generator by Kevin Lee Legge (kevin8084@gmail.com)
; **************************************************************************************************

; Set up the graphics
Graphics3D 800,600
SetBuffer BackBuffer()

Dim maze(9,9) ; this is to hold a 10x10 array

; some constants for collision purposes
Const player_type=1
Const scene_type=2

; create the ground
plane=CreatePlane()
EntityColor plane,70,70,70
EntityType plane,scene_type

; we need to see things and be able to move around in the world
Global camera=CreateCamera()
PositionEntity camera,0,10,0
CameraRange camera,.1,500
EntityType camera,player_type
EntityRadius camera,1

; to make things a wee bit brighter, though we can use AmbientLight, as well
light=CreateLight()
PositionEntity light,50,100,50
RotateEntity light,90,0,0

readData() ; read our maze data

; set up our collisions so that we don't move through the walls
Collisions player_type,scene_type,2,2

While Not KeyHit(1)
If KeyDown(200) Then MoveEntity camera,0,0,.1
If KeyDown(208) Then MoveEntity camera,0,0,-.1
If KeyDown(203) Then TurnEntity camera,0,1,0
If KeyDown(205) Then TurnEntity camera,0,-1,0

TranslateEntity camera,0,-1,0 ; a little bit of gravity for us

UpdateWorld
RenderWorld
Flip
Delay 10
Wend
End





Function readData()
; this function reads the data and generates the maze
Restore mazeData ; this makes sure that we read from the beginning of the data
Read xValue,zValue ; the first 2 values are how many numbers across and how many down
For z=zValue-1 To 0 Step-1 ; because of the way the data is set up, we need to invert z
	For x=0 To xValue-1 ; no inversion needed for the x value
		Read maze(x,z)  ; store data in the array
		Select maze(x,z); what value does maze(x,z) contain?
			Case 0 ; we have a wall here
				cube=CreateCube()
				; because we are making a 100x100 world, each cube occupies a 10x10 space
				; so we need to only scale the cube's x and z by 5. 
				ScaleEntity cube,5,2,5
				; again, because we are making a 100x100 world and the mazeData is a matrix
				; of 10x10 numbers, we need to multiply each x/z position by 10
				PositionEntity cube,x*10,2,z*10
				EntityColor cube,200,100,75
				EntityType cube,scene_type
			Case 4 ; we have the player's position here
				PositionEntity camera,x*10,2,z*10
		End Select
	Next
Next
End Function




.mazeData
Data 10,10 ; 10x10 matrix
Data 0,0,0,0,0,0,0,0,0,0
Data 0,1,0,1,1,1,1,1,1,0
Data 0,1,0,0,0,0,1,1,1,0
Data 0,1,1,1,1,0,1,0,1,0
Data 0,1,1,4,1,0,1,0,1,0
Data 0,1,1,1,1,0,1,0,1,0
Data 0,1,1,1,1,0,1,0,1,0
Data 0,1,0,0,0,0,0,0,1,0
Data 0,1,1,1,1,1,1,1,1,0
Data 0,0,0,0,0,0,0,0,0,0
