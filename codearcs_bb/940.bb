; ID: 940
; Author: Ross C
; Date: 2004-02-22 10:07:08
; Title: 2D follow image/smooth waypoints
; Description: Makes an image smoothly(ish) follow waypoints

; **** 2D waypoint editor, modified. Original Code by Ross C *****
; ****                 Modified code by Ross C               *****
; ****     create smooth transitions between way points      *****


Graphics 800,600
SetBuffer BackBuffer()

Global max_path_points=100

Dim pathx#(max_path_points-1); set up 101 path points
Dim pathy#(max_path_points-1); -1 because arrays start at zero

Global path_counter=-1
Global current_path=0
Global cx#=0
Global cy#=0
Global ex#=400
Global ey#=100
Global movement_started=0


While Not KeyHit(1)
	Cls
	
	If MouseHit(1) Then
			setpath(MouseX(),MouseY()); set path point with the mouses current coords
	End If
	
	If KeyHit(57) And movement_started=0 And path_counter>0 Then; if spacebar pressed then move rectangle
		current_path=0; reset the current path to 0
		cx=pathx(0); set the rectangles coords to the first path point
		cy=pathy(0)
		movement_started=1; set flag for movement started
		ex=400
		ey=100
	End If
	If KeyHit(28) Then; if the enter key is pressed then stop movement
		movement_started=0; clear flag for movement
		cx=pathx(0); set rectangle coords to the first path point
		cy=pathy(0)
		current_path=-1; set current path to none exsistant
	End If

	
	
	
	draw_path_points(); function for drawing the pathpoints
	If movement_started=1 Then move(); draw the moving rectangle and move it
	Color 200,200,200; set drawing color
	Rect MouseX()-1,MouseY()-1,2,2; draw mouse location with a rectangle
	Text 0,0,"Click mouse to set path point. Press spacebar to start animation. Press enter to stop."
	Text 0,10,"movement started="+movement_started
	Flip
Wend
End

Function move()
	ang#=0
	If current_path<path_counter Then; make sure the current path point is less than the highest
		ang=ATan2(pathy(current_path+1)-pathy(current_path),pathx(current_path+1)-pathx(current_path))
		;/\ basically means find the angle between the current path point and the next one /\
		cx=cx+Cos(ang)*3; increase the x coord of the rect following the path points based on the angle
		cy=cy+Sin(ang)*3; increase the y coord of the rect following the path points based on the angle
		Color 100,200,100; change drawing color
		Rect cx-3,cy-3,6,6; draw the rectangle central to it's coords
		If Abs(cx-pathx(current_path+1))<2 And Abs(cy-pathy(current_path+1))<2 Then
			; /\ if the rects coords are with a 1 pixel distance of the path point then /\
			current_path=current_path+1; increase the current path point number
			cx=pathx(current_path); set the rectangles coords to that of the next path point
			cy=pathy(current_path)
		End If
	End If
	angle=ATan2( cy - ey, cx - ex ); find the angl between the enemies and the waypoint follower
	dist=Sqr( (ex-cx)*(ex-cx)+(ey-cy)*(ey-cy) ); find the distance between them
	DebugLog dist
	ex=ex+(Cos(angle)/1)*(dist/30); move the enemy at speed relative to the distance
	ey=ey+(Sin(angle)/1)*(dist/30); move the enemy at speed relative to the distance
	Rect ex-15,ey-15,30,30; draw the enemy
End Function

Function setpath(x,y)
	path_counter=path_counter+1; increase counter
        If path_counter>max_path_points-1 Then; if counter is higher than the available number of path points then set it back
               path_counter=max_path_points-1
        Else; else 
	       pathx(path_counter)=x; record the x and y positions
	       pathy(path_counter)=y
        End If
End Function

Function draw_path_points()
	For loop=0 To path_counter; loop through all the path points
		Color 100,100,200
		Rect pathx(loop),pathy(loop),2,2
		If loop<path_counter Then; do this loop while the loop value is less than the highest path value
			Rect pathx(loop+1)-1,pathy(loop+1)-1,2,2; draw a rectangle at the path point so its central
			Color 50,50,150; change drawing color
			Line pathx(loop),pathy(loop),pathx(loop+1),pathy(loop+1); draw a line between the current point and the next one
		End If
	Next
End Function
