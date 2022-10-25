; ID: 2423
; Author: CloseToPerfect
; Date: 2009-02-24 19:04:50
; Title: Random Maze Generator
; Description: Mazes

;Random maze generator
;All mazes are solvable, all points in the maze have only 1 route to get there

;2/20/2009 by John Chase
;use and modify for your needs


;usage
;generate_map(sizeX,sizeY,Strightness)
;sizeX = how big the maze is width
;sizeY = how big the maze is height
;straightness is a precent change 1-100 of how often the maze will try to turn

;dilutemap(NumberOfTime)
;will cycle though maze removing dead ends


Graphics 800,600,16 
SeedRnd (MilliSecs()) 
Dim map(1,1) 
Global SizeX,SizeY 
Global Wall	   = 4 
Global Flr	   = 5 

StartTime = MilliSecs()  
generate_map(25,25,100)
StopTime = MilliSecs()
Time = (StopTime - StartTime)

Cls
Text 1,588,"Time to make map in millisecs :"+Time


For x=0 To SizeX-1
For y=0 To SizeY-1
	If map(x,y) = Wall Then Rect x*10,y*10,10,10,1
Next
Next 
Text 1,250,"Straightness factor of 100"


generate_map(25,25,66)
For x=0 To SizeX-1
For y=0 To SizeY-1
	If map(x,y) = Wall Then Rect x*10+260,y*10,10,10,1
Next
Next 
Text 260,250,"Straightness factor of 66"


generate_map(25,25,33)
For x=0 To SizeX-1
For y=0 To SizeY-1
	If map(x,y) = Wall Then Rect x*10+520,y*10,10,10,1
Next
Next 
Text 520,250,"Straightness factor of 33"


generate_map(25,25,0)
For x=0 To SizeX-1
For y=0 To SizeY-1
	If map(x,y) = Wall Then Rect x*10,y*10+270,10,10,1
Next
Next 
Text 1,520,"Straightness factor of 0"


DiluteMap(5)
For x=0 To SizeX-1
For y=0 To SizeY-1
	If map(x,y) = Wall Then Rect x*10+260,y*10+270,10,10,1
Next
Next 
Text 260,520,"Last maze diluted 5 times"

DiluteMap(5)
For x=0 To SizeX-1
For y=0 To SizeY-1
	If map(x,y) = Wall Then Rect x*10+520,y*10+270,10,10,1
Next
Next 
Text 520,520,"Last maze diluted 5 more times"
Text 1,550,"Press any key to see more examples"
Flip
WaitKey 


StartTime = MilliSecs()  
generate_map(79,55,50)
StopTime = MilliSecs()
Time = (StopTime - StartTime)
Cls
Text 1,588,"Time to make map in millisecs :"+Time
Text 1,578,"A bigger square maze, press any key"

For x=0 To SizeX-1
For y=0 To SizeY-1
	If map(x,y) = Wall Then Rect x*10,y*10,10,10,1
Next
Next 
Flip
WaitKey 

StartTime = MilliSecs()  
generate_map(79,25,50)
StopTime = MilliSecs()
Time = (StopTime - StartTime)
Cls
Text 1,588,"Time to make map in millisecs :"+Time
Text 1,578,"A bigger rectangulr maze, press any key"
For x=0 To SizeX-1
For y=0 To SizeY-1
	If map(x,y) = Wall Then Rect x*10,y*10,10,10,1
Next
Next 
Flip
WaitKey 

StartTime = MilliSecs()  
generate_map(158,110,50)
StopTime = MilliSecs()
Time = (StopTime - StartTime)
Cls
Text 1,588,"Time to make map in millisecs :"+Time
Text 1,578,"A much bigger maze,on my computer it only takes about a half second, press any key"
For x=0 To SizeX-1
For y=0 To SizeY-1
	If map(x,y) = Wall Then Rect x*5,y*5,5,5,1
Next
Next 
Flip
WaitKey 

StartTime = MilliSecs()  
generate_map(399,250,50)
StopTime = MilliSecs()
Time = (StopTime - StartTime)
Cls
Text 1,588,"Time to make map in millisecs :"+Time
Text 1,578,"A hugh maze,on my computer it only takes about 2 seconds, press any key"
For x=0 To SizeX-1
For y=0 To SizeY-1
	If map(x,y) = Wall Then Rect x*2,y*2,2,2,1
Next
Next 
Flip
WaitKey 

Cls
Text 0,0,"Thank you for looking at my maze generator."
Text 0,10,"I hope it can be useful for you."
Text 0,30,"John Chase"
Text 0,580,"Press any key to exit"
Flip
WaitKey 




;function to remove dead ends.
Function DiluteMap(Number)
For i = 1 To number
	For x=0 To sizex-1
		For y=0 To sizey-1
			count =0
			;first check to see if inbounds then check to see if we can dilute
			If x+1 < sizex-1 Then If map(x+1,y)=Flr Then count=count+1
			If x-1 > 0 Then	If map(X-1,y)=Flr Then count=count+1
			If y+1 < sizey-1 Then If map(x,y+1)=Flr Then count=count+1
			If y-1 > 0 Then If map(x,y-1)=Flr Then count=count+1
			;if only 1 way out, change it to a wall
			If count=1 Then map(x,y)=Wall
		Next
	Next
Next 
End Function


Function generate_map(xsize,ysize,straightness)
SizeX      = xsize
SizeY      = ysize
NotChecked = 0
Checked    = 1
Open       = 2
NotOpen    = 3
AllChecked = SizeX*SizeY
LastDirection = Rand(0,3)
North      = Open
South      = Open
East       = Open
West       = Open 
TimeUp     = False

;make map array fill with walls
Dim map(SizeX-1,SizeY-1)
For x=0 To SizeX-1
For y=0 To SizeY-1
Map(x,y) = Wall
Next
Next

;pick a random cell and mark it as Flr hold in 1 cell from the edge
CurrentX = Rand(2,SizeX-3)
CurrentY = Rand(2,SizeY-3)

Repeat
;pick a direction
Moved = False
NumFailedMoves = 0
ChangeDirection = True

;check strightness factor
;otherwise random percent chance
	If Rand(1,100) < straightness Then 
		ChangeDirection = False
		Dir = LastDirection
	EndIf

;keep trying till you find a direction open
Repeat
;pick a direction to move at random
	If ChangeDirection = True Then 
		Dir = Rand(0,3)
		LastDirection = Dir
	EndIf

	ChangeDirection = True

	Select Dir
;north
	Case 0
		If North = Open Then 
			Moved = True
			CurrentY = CurrentY - 1
		EndIf
;south
	Case 1
		If South = Open Then 
			Moved = True
			CurrentY = CurrentY + 1
		EndIf
;east
	Case 2
		If East = Open Then 
			Moved = True
			CurrentX = CurrentX + 1
		EndIf
;West
	Case 3
		If West = Open Then 
			Moved = True
			CurrentX = CurrentX - 1
		EndIf
	End Select

Until Moved = True 
;mark the map
Map(currentX,CurrentY) = Flr
LastDrawTimer = MilliSecs()

;Text currentX*10,currenty*10,"O"
;Flip

.checkdirection
;step 3 from current cell check N,S,E,W in a random style
;first set all direction Direction checked
North = NotOpen
South = NotOpen
East  = NotOpen
West  = NotOpen

;check all 4 directions 
;north
	;out of bounds?
	If CurrentY-2 < 0 Then 
		North = NotOpen
	ElseIf Map(CurrentX,CurrentY-1) = wall And Map(CurrentX,CurrentY-2) = wall Then 
		If map(CurrentX-1,CurrentY-1) = wall And map(CurrentX+1,CurrentY-1) =wall Then 
			North = Open
		Else
			North = NotOpen
		EndIf
	EndIf
;south
	;out of bounds?
	If CurrentY+2 > SizeY-1 Then 
		South = NotOpen
	ElseIf Map(CurrentX,CurrentY+1) = wall And Map(CurrentX,CurrentY+2) = wall Then
		If map(CurrentX-1,CurrentY+1) = wall And map(CurrentX+1,CurrentY+1) = wall Then 
			South = Open
		Else
			South = NotOpen
		EndIf
	EndIf
;east
	If CurrentX+2 > SizeX-1 Then
		East = NotOpen
	ElseIf Map(CurrentX+1,CurrentY) = wall And Map(CurrentX+2,CurrentY) = wall Then 
		If map(CurrentX+1,CurrentY-1) = wall And map(CurrentX+1,CurrentY+1) = wall Then 
			East = Open
		Else
			East = NotOpen
		EndIf
	EndIf
;west
	;out of bounds?
	If CurrentX-2 < 0 Then
		West = NotOpen
	ElseIf Map(CurrentX-1,CurrentY) = wall And Map(CurrentX-2,CurrentY) = wall Then 
		If map(CurrentX-1,CurrentY-1) = wall And map(CurrentX-1,CurrentY+1) = wall Then 
			West = Open
		Else
			West = NotOpen
		EndIf
	EndIf

;if time passes without finding anything we are done
If MilliSecs() - LastDrawTimer > 100 Then TimeUp = True 

;now what happens if all directions are not open
If North = NotOpen And South = NotOpen And East = NotOpen And West = NotOpen And TimeUp = False Then 
Done = False
;pick a random already floored location and try again
	Repeat
	CurrentX = Rand(1,SizeX-2)
	CurrentY = Rand(1,SizeY-2)
	If Map(CurrentX,CurrentY) = Flr Then Done = True
	Until Done = True
	Goto checkdirection
EndIf

Until TimeUp = True 
End Function
