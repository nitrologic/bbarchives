; ID: 1488
; Author: Matty
; Date: 2005-10-12 05:23:26
; Title: Pathfinding (Pre calculated 2D)
; Description: Pathfinding routine I use in my games

Const PathUp=1,PathDown=2,PathLeft=3,PathRight=4
;Const MaxX=31,MaxZ=31
Global MaxX=7 ;horizontal number of grid squares (0-25) so 26 really
Global MaxZ=7 ;vertical number of grid squares (0-25)
Const MaxDepth=125 ;how deep to search, if a path to a point is longer than this then it won't find one....

;Be careful how high you set the MaxX,MaxZ as the pathfinding array is a 4 dimensional array
;so doubling MaxX,MaxZ will multiply the size of the array by 16.  I really only need to use
;3 bits per 'array slot' so a bank would be much more memory efficient (instead of the 32 bits used
;here!)
;
;This is not the most memory efficient path finding method but for my game it works.  It would be suitable
;for 2d games with small play areas and reasonably static environments...
;
;If you have only a small number of units that need to do pathfinding it can be used to calculate
;paths in real time with a few adjustments.
;
;
;This pathfinding method works better when the play area is full of obstacles.  The more open the 
;map the longer it will take to pre calculate the paths....Worst case scenario is a totally open 
;play area with no walls at all...
;

Graphics 800,600,32,2

Dim MapArray(MaxX,MaxZ,1)
Dim PathfindingArray(MaxX,MaxZ,MaxX,MaxZ)
Dim PathArray(MaxX,MaxZ)
Dim PathTempArray(MaxX,MaxZ)
Dim PathToTake(MaxDepth,2)

;goto skiptohere ;uncomment this out if you wish to try pre calculating a path...you need to supply 
;your own map file (format described below) and may need to alter the MaxX,MaxZ values and perhaps MaxDepth above...current
;value for MaxDepth should be suitable for Maps of size 25x25


;Obviously you will use larger maps than the one shown here.


Data 1,1,1,1,1,1,1,1
Data 1,0,0,1,0,1,0,1
Data 1,0,0,0,0,1,0,1
Data 1,0,1,1,1,1,0,1
Data 1,0,0,0,0,0,0,1
Data 1,0,1,1,0,1,1,1
Data 1,0,1,0,0,0,0,1
Data 1,1,1,1,1,1,1,1

For Z=0 To MaxZ
For X=0 To MaxX
Read MapArray(X,Z,0)
PathArray(x,z)=MapArray(x,z,0)

Next
Next
CalculatePathFindingArray()
DisplayTestPathFinding()

End 
.skiptohere

inmapfile=ReadFile("Your Map File Here")
If inmapfile=0 Then RuntimeError("You have to put a valid map file in for this to work")
;
;Map file should be generated in the following way:
;outfile=writefile("Your Map File Here")
;if outfile=0 then runtimeerror("Could not create map file for some reason...")
;For x=0 to maxx
;	for z=0 to maxz
;	writebyte outfile,MapArray(x,z,0) ;MapArray(x,z,0)=0 for open grid square, MapArray(x,z,0)=1 for blocked grid square
;
;	next
;next
;closefile outfile


For x=0 To maxx
	For z=0 To maxz
		MapArray(x,z,0)=ReadByte(inmapfile) 
		PathArray(x,z)=MapArray(x,z,0)
	Next
Next


CloseFile inmapfile

CalculatePathFindingArray()

outfile=WriteFile("your pre calculated path file name goes here")
If outfile=0 Then RuntimeError("something went wrong trying to write the path finding file to disk")
For Xi=0 To MaxX
For Zi=0 To MaxZ
For Xf=0 To MaxX
For Zf=0 To MaxZ
WriteByte outfile,PathFindingArray(xi,zi,xf,zf)
Next
Next
Next
Next


CloseFile outfile



Function CalculatePathFindingArray(MapValue=0)

starttime=MilliSecs()


For Xi=0 To MaxX
	For Zi=0 To MaxZ
		For Xf=0 To MaxX
			For Zf=0 To MaxZ
				If MapArray(Xi,Zi,0)=0 And MapArray(Xf,Zf,0)=0 Then 
					PathFindingArray(Xi,Zi,Xf,Zf)=NewPathFind(Xi,Zi,Xf,Zf,MaxDepth)

					If KeyDown(57) Then 
						Cls
						Text 0,0,"Progress:Xi="+Xi+", Zi="+Zi+", Xf="+Xf+", Zf="+Zf
						Flip
					EndIf 
					
					If MilliSecs()-calctime>5000 Then 
						calctime=MilliSecs()
						Cls
						Text 0,0,"Progress:"+100.0*Float(Xi*MaxX*MaxZ*MaxX+Zi*MaxX*MaxZ+Xf*MaxX+Zf)/Float(MaxX*MaxZ*MaxX*MaxZ)+"%"+", Map Number:"+Mapvalue
						secondssofar=(MilliSecs()-starttime)/1000
						mins=secondssofar/60
						secs=secondssofar Mod 60
						Text 0,15,"Time:"+mins+":"+secs
						Flip 
					EndIf 
 
					If KeyDown(1) Then End 
				EndIf 
			Next
		Next
	Next
Next


End Function

Function NewPathFind(Column,Row,TargetColumn,TargetRow,Depth)
;

PathString$="" ;no longer used anyway - it was originally used for storing a sequence of grid squares for another game

Dim PathTempArray(MaxX,MaxZ) ;
Dim PathToTake(MaxDepth,2)

If Depth>MaxDepth Then Depth=MaxDepth
If PathArray(Column,Row)<>0 Then Return 0 ;if the starting square is blocked then don't do any calculations

If Column>=0 And Row>=0 And TargetColumn>=0 And TargetRow>=0 And Column<=MaxX And Row<=MaxZ And TargetColumn<=MaxX And TargetRow<=MaxZ Then 
;make sure that the starting square and finishing square is within the grid

	MaxIndex=DepthSearch(Column,Row,TargetColumn,TargetRow,1,Depth) ;do a recursive search for the shortest route
	If MaxIndex>0 Then ;this little section no longer used, was used for another game...

		PathString$=LSet(Str(PathToTake(1,1)),4)+LSet(Str(PathToTake(1,2)),4)		
;		For i=MaxIndex-1 To 1 Step -1
;			PathString$=PathString$+LSet(Str(PathToTake(i,1)),4)+LSet(Str(PathToTake(i,2)),4)
;		Next	

	Else
		PathString$=""
	EndIf 

Else


EndIf 
PathDirection=0
;If PathToTake(1,1)<Column Then PathDirection=PathLeft
;If PathToTake(1,1)>Column Then PathDirection=PathRight
;If PathToTake(1,2)<Row Then PathDirection=PathUp
;If PathToTake(1,2)>Row Then PathDirection=PathDown
;return PathDirection

Return PathToTake(1,0) ;PathToTake basically tells us which square to go to (up one or left one or right one or down one )


End Function

Function DepthSearch(X,Y,TX,TY,N,Depth)
;
;I made this function quite some time ago that I no longer remember how it works...but it does
;
;
;
;

If N>Depth Then Return 0 ;if we have searched too deep then return 0 - no path found

PathTempArray(X,Y)=N ;assign the gridsquare we are searching a value indicating how many moves from our starting point we are (also equal to the depth of the search so far)

If X=TX And Y=TY Then Return N ;if we are at our target square then exit and return the number of moves it took to get there

AlreadyKnowTheWay=False ;this was a vain attempt at optimising the code, it doesn't work feel free to delete it...
;If PathFindingArray(X,Y,TX,TY)<>0 Then AlreadyKnowTheWay=True 


;the next 4 if statements perform recursive searches in each of the 4 directions that movement is allowed in
;if you had an 8 directional movement system for your AI units then you would need additional bits here...
;the final value of MaxIndex1..MaxIndex4 tells us how many squares it took us to get to the target going in a certain direction from our current point...
;


If X>0 And AlreadyKnowTheWay=False Then 

	If PathArray(X-1,Y)=0 And (PathTempArray(X-1,Y)=0 Or PathTempArray(X-1,Y)>N+1) Then MaxIndex1=DepthSearch(X-1,Y,TX,TY,N+1,Depth)
	;above line checks if the square to our left is open (ie we can move there)
	;it also checks (PathTempArray(X-1,Y)>N+1) if going to the square to our left would take less moves to get there in total then
	;what previous searches have already calculated for that square...
EndIf
If Y>0  And AlreadyKnowTheWay=False Then 
	If PathArray(X,Y-1)=0 And (PathTempArray(X,Y-1)=0 Or PathTempArray(X,Y-1)>N+1) Then MaxIndex2=DepthSearch(X,Y-1,TX,TY,N+1,Depth)
EndIf 

If X<MaxX And AlreadyKnowTheWay=False Then 
	If PathArray(X+1,Y)=0 And (PathTempArray(X+1,Y)=0 Or PathTempArray(X+1,Y)>N+1) Then MaxIndex3=DepthSearch(X+1,Y,TX,TY,N+1,Depth)
EndIf 

If Y<MaxZ And AlreadyKnowTheWay=False Then 
	If PathArray(X,Y+1)=0 And (PathTempArray(X,Y+1)=0 Or PathTempArray(X,Y+1)>N+1) Then MaxIndex4=DepthSearch(X,Y+1,TX,TY,N+1,Depth)
EndIf 


If AlreadyKnowTheWay=False Then ;this will always be false, not really needed
MinIndex=MaxDepth+1

If MaxIndex1<MinIndex And MaxIndex1<>0 Then MinIndex=MaxIndex1:PathToTake(N,0)=PathLeft:PathToTake(N,1)=X-1:PathToTake(N,2)=Y
If MaxIndex2<MinIndex And MaxIndex2<>0 Then MinIndex=MaxIndex2:PathToTake(N,0)=PathUp:PathToTake(N,1)=X:PathToTake(N,2)=Y-1
If MaxIndex3<MinIndex And MaxIndex3<>0 Then MinIndex=MaxIndex3:PathToTake(N,0)=PathRight:PathToTake(N,1)=X+1:PathToTake(N,2)=Y
If MaxIndex4<MinIndex And MaxIndex4<>0 Then MinIndex=MaxIndex4:PathToTake(N,0)=PathDown:PathToTake(N,1)=X:PathToTake(N,2)=Y+1
;basically this compares each of the 4 directions initially chosen to get to our target point and
;sets the "PathToTake" to be the one which was shortest.


If MinIndex=MaxDepth+1 Then 
	Return 0
Else
	Return MinIndex 
EndIf 
Else
PathToTake(N,0)=PathFindingArray(X,Y,TX,TY)
Return 0

EndIf 

End Function


Function DisplayTestPathFinding()



xi=0
zi=maxz-1
Repeat
Cls
For x=0 To maxx
For z=0 To maxz
If maparray(x,z,0)=1 Then 
Color 80,80,80
Rect x*10,z*10,10,10,1
EndIf 
Next
Next


If MilliSecs()-its>1000 Or KeyHit(57)>0 Or maparray(xi,zi,0)=1 Or maparray(tx,tz,0)=1 Then 
FlushKeys()
its=MilliSecs()
xi=xi-1
If xi<0 Then xi=maxx:zi=zi-1
If zi<0 Then zi=maxz
tx=Rand(0,maxx)
tz=Rand(0,maxz)
EndIf 
If maparray(xi,zi,0)=0 And maparray(tx,tz,0)=0 Then 
jits=0
nx=xi
nz=zi
Repeat
jits=jits+1
If jits=1 Then Color 255,0,255 Else Color 255,255,0
Rect 2+nx*10,2+nz*10,6,6,1

Select pathfindingarray(nx,nz,tx,tz)

Case pathup
nz=nz-1
Case pathdown
nz=nz+1
Case pathright
nx=nx+1
Case pathleft
nx=nx-1
Default
;do nothing
End Select
Until jits>MaxDepth Or (nx=tx And nz=tz)
Color 0,255,0
Rect 2+tx*10,2+tz*10,6,6,1

Else
its=its+1000
EndIf 
Flip
Until KeyDown(1)>0


End Function

Function readmapfile(filename$)
infile=ReadFile(filename)
For x=0 To MaxX
For z=0 To MaxZ
;MapArray(x,z,0);=Maze(x,sy-z)
maparray(x,z,0)=ReadByte(infile)
PathArray(x,z)=MapArray(x,z,0)
Next
Next
CloseFile infile

End Function

Function readpathfile(filename$)
infile=ReadFile(filename)
For Xi=0 To MaxX
For Zi=0 To MaxZ
For Xf=0 To MaxX
For Zf=0 To MaxZ
PathFindingArray(xi,zi,xf,zf)=ReadByte(infile)
Next
Next
Next
Next


CloseFile infile
End Function
