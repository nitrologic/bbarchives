; ID: 2069
; Author: Kalisme
; Date: 2007-07-17 01:27:05
; Title: simple a* pathfinding
; Description: an early attempt at a* pathfinding.

;A* pathfinding attempt 1
;Put together by Kevin Laherty. ( kalisme@hotmail.com )
;based on an article by Patrick Lester,(hosted on GameDev.net)

Graphics 640,480
SetBuffer BackBuffer()

Dim r_map(21,21)	;<- for the in game map

Dim a_map(21,21,5)	;<- for the A* pathfinder

path$="s"			;<- the current path our li'l fella has to
					;	go to reach his goal. "s" means it has no goal or it is unreachable.

Global s_x=2,s_y=2	;starting point X & Y (current location of our li'l fella)
Global e_x,e_y		;exit point X & Y (where we aim to get to)

.map_data
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1	;<- just our map.
Data 1,0,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1,0,0,1	;	it's 20 X 20...
Data 1,0,0,0,1,0,0,0,0,0,1,1,0,1,0,0,1,0,0,1	;	nothing amazing here,
Data 1,0,0,0,1,0,0,1,0,0,0,1,0,1,0,1,1,1,0,1	;	play around with it. :)
Data 1,0,0,0,1,0,0,1,0,0,0,1,0,1,0,0,0,1,0,1	;	(it's the best way
Data 1,0,0,1,1,0,0,1,1,1,1,1,0,1,0,1,1,1,0,1	;	to know the code works)
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1
Data 1,1,1,1,1,1,1,1,0,0,0,0,1,1,1,1,0,0,0,1
Data 1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1	;	0 = grass
Data 1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1	;	1 = brick
Data 1,0,1,1,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1
Data 1,0,1,0,1,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1
Data 1,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
Data 1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1
Restore map_data
For b = 1 To 20
For a = 1 To 20
Read c
r_map(a,b)=c									;<- storing our map data into its array. (r_map(x,y))
Next
Next


While KeyHit(1) =0
	Cls

	;render map
	;{
		For b = 1 To 20
			For a = 1 To 20
				If r_map(a,b)=0 Then Color 0,100,0:Rect (a-1)*20,(b-1)*20,20,20
				If r_map(a,b)=1 Then Color 200,150,0:Rect (a-1)*20,(b-1)*20,20,20:Color 255,255,255:Line (a-1)*20,(b-1)*20+19,(a-1)*20+20,(b-1)*20+19:Line (a-1)*20,(b-1)*20+10,(a-1)*20+20,(b-1)*20+10:Line (a-1)*20+15,(b-1)*20+10,(a-1)*20+15,(b-1)*20+20:Line (a-1)*20,(b-1)*20,(a-1)*20,(b-1)*20+10
	
				If e_x=a Then If e_y=b Then Color 250,150,150:Text (a-1)*20+8,(b-1)*20+8,"X"
				If s_x=a Then If s_y=b Then Color 150,150,200:Oval (a-1)*20+7,(b-1)*20+5,5,5:Line (a-1)*20+10,(b-1)*20+5,(a-1)*20+10,(b-1)*20+15:Line (a-1)*20,(b-1)*20+10,(a-1)*20+20,(b-1)*20+10:Line (a-1)*20,(b-1)*20+20,(a-1)*20+10,(b-1)*20+15:Line (a-1)*20+20,(b-1)*20+20,(a-1)*20+10,(b-1)*20+15

				If Int(MouseX()/20)=a-1 Then If Int(MouseY()/20)=b-1 
					Color 200,0,0:Rect (a-1)*20,(b-1)*20,20,20,0
					If MouseHit(1) Then s_x=a:s_y=b					;<- sets a starting point
					run_a=0
					If MouseHit(2) Then e_x=a:e_y=b:run_a=1			;<- sets a target point
				EndIf

			Next
		Next
	;}

	;render A* workings out
	;{
		If KeyDown(2)
			For b = 1 To 20
				For a = 1 To 20
					If a_map(a,b,1)=1 Then Color 60,60,20: Rect (a-1)*20,(b-1)*20,20,20,0	;1 = wall
					If a_map(a,b,1)=2 Then Color 20,200,20: Rect (a-1)*20,(b-1)*20,20,20,0	;2 = open
					If a_map(a,b,1)=3 Then Color 0,0,200: Rect (a-1)*20,(b-1)*20,20,20,0	;3 = closed


					Color 255,0,0
					If a_map(a,b,5)=4 Then Text (a-1)*20,(b-1)*20,"->"		;<- these are some REALLY
					If a_map(a,b,5)=6 Then Text (a-1)*20,(b-1)*20,"<-"		;	badly put together 					
					If a_map(a,b,5)=2 Then Text (a-1)*20,(b-1)*20,"V"		;	ASCII arrows....
					If a_map(a,b,5)=8 Then Text (a-1)*20,(b-1)*20,"^"		;	hope you can understand them :(
					
					If a_map(a,b,5)=1 Then Text (a-1)*20,(b-1)*20,"\|"
					If a_map(a,b,5)=3 Then Text (a-1)*20,(b-1)*20,"|/"
					If a_map(a,b,5)=7 Then Text (a-1)*20,(b-1)*20,"/|"
					If a_map(a,b,5)=9 Then Text (a-1)*20,(b-1)*20,"|\"
				Next
			Next
		EndIf
	;}

Color 255,255,255
	Text 410,0,"current path:" + path$
	Text 410,10,"LMB = select start point"
	Text 410,20,"RMB = select end point"
	Text 410,30,"1   = display A* workings out"
	Text 410,50,"ESC = quit."

	Color 255,255,255
	Line MouseX(),MouseY(),MouseX()+10,MouseY()+15		;<- just drawing a mouse cursor.
	Line MouseX(),MouseY(),MouseX()+10,MouseY()+5
	Line MouseX(),MouseY(),MouseX(),MouseY()+10
	Line MouseX()+10,MouseY()+5,MouseX(),MouseY()+10

	Flip



If run_a=1 Then					;<- if a path has just been set,
								;	then we run the A* pathfinder

;--------------------------------------------------------------------------------
;Here is where the A* code starts
;--------------------------------------------------------------------------------
c_x=s_x				;<- c_x refurs to current X location for
c_y=s_y				;	A* pathfinder loop. when we start we
					;	set the current X & Y to the starting
					;	point (where our li'l fella is currently)
;fill in unwalkable paths
;{
	For b = 1 To 20
		For a = 1 To 20
			a_map(a,b,1)=0			;<- clears past tile data
			a_map(a,b,2)=0			;<- clears past G cost data
			a_map(a,b,3)=0			;<- clears past H cost data
			a_map(a,b,4)=0			;<- clears past F cost data
			a_map(a,b,5)=0			;<- clears past directional data

			If r_map(a,b)=1 Then a_map(a,b,1)=1			;<- if tile at (a,b) is a wall, 
		Next											;	mark it as a "1" on the A*
	Next												;	pathfinders array.
;}

count=0
work=0

;main loop
	While count < 20*20
	
		a_map(c_x,c_y,1)=3
	

		;check
		cur_g=0
		c_dir=0
		lst_F=10000
		n_c_x=c_x
		n_c_y=c_y

			For c_b=1 To 3							;<- we only check tiles 1 unit
				For c_a=1 To 3						;	away from our current position
					c_dir=c_dir+1			;<- current direction (1=upper left, 2=up ect.. ect..)
					chk_x=c_x+(c_a-2)
					chk_y=c_y+(c_b-2)

						If a_map(chk_x,chk_y,1)<> 1 Then If a_map(chk_x,chk_y,1)<> 3
							a_map(chk_x,chk_y,1)=2
							If Abs((c_a-2)+(c_b-2))>0 Then cur_g=10
							If Abs((c_a-2)+(c_b-2))=2 Then cur_g=14
							cur_g=cur_g+a_map(c_x,c_y,2)



								If a_map(chk_x,chk_y,2) > cur_g Or a_map(chk_x,chk_y,2) = 0
									a_map(chk_x,chk_y,2) = cur_g			;G cost
									a_map(chk_x,chk_y,5) = c_dir			;Direction
									c_H=(Abs(chk_x-e_x)+Abs(chk_y-e_y))*10
									a_map(chk_x,chk_y,3) = c_H				;H cost
									a_map(chk_x,chk_y,4) = a_map(chk_x,chk_y,2)+a_map(chk_x,chk_y,3) ;F cost
								EndIf

							chk_F=a_map(chk_x,chk_y,4)
							If chk_F < lst_F Then n_c_x=chk_x:n_c_y=chk_y:lst_F=chk_F

						EndIf
	
				Next
			Next
		;finish check

		count=count+1
		
		;Ok, the next "if" statement checks if the new "current location"
		;is the same as the past "current location". The only reason this
		;should happen is if the current path check has got itself cornered
		;and cant move anymore... this MAY mean there isn't any path,
		;but more likley it just chose a silly path. To get around this
		;we quickly read through the A* map data and look for a remaining "open"
		;tile, then if we find one, we continue searching from that tile.
		;If we run out of open tiles, there probably isn't a possible path.
			If n_c_x=c_x Then If n_c_y=c_y Then ;quick scan
					For qs_y= 1 To 20
						For qs_x= 1 To 20
							If a_map(qs_x,qs_y,1)=2 Then n_c_x=qs_x: n_c_y=qs_y		;<-looks for remaining "open" tile

					Next
				Next
			EndIf


		c_x=n_c_x		;<- declares the new "current location"
		c_y=n_c_y

	;Yup, if the "current location" ever becomes the "end location"
	;then we can find a logical path... awsome! =D
		If c_x = e_x Then If c_y = e_y Then count = 20*1000:work = 1
	Wend
;main loop over

;if it works: =)
If work=1					;<- if it works, we should record the path!
	find=0					;	we mearly walk back using the directional
	c_x=e_x					;	data we created. The shortest path should
	c_y=e_y					;	get returned.
	path$=""
	While find=0																;yeah... this is some
		If c_x=s_x Then If c_y = s_y Then find = 1								;weak coding... but
																				;I figured a String$
			If a_map(c_x,c_y,5) = 1 Then path$=path$ + "c":c_x=c_x+1:c_y=c_y+1	;was a quick and simple
			If a_map(c_x,c_y,5) = 2 Then path$=path$ + "x":c_x=c_x:c_y=c_y+1	;way of recording a
			If a_map(c_x,c_y,5) = 3 Then path$=path$ + "z":c_x=c_x-1:c_y=c_y+1	;path.
			If a_map(c_x,c_y,5) = 4 Then path$=path$ + "d":c_x=c_x+1:c_y=c_y
			If a_map(c_x,c_y,5) = 6 Then path$=path$ + "a":c_x=c_x-1:c_y=c_y
			If a_map(c_x,c_y,5) = 7 Then path$=path$ + "e":c_x=c_x+1:c_y=c_y-1
			If a_map(c_x,c_y,5) = 8 Then path$=path$ + "w":c_x=c_x:c_y=c_y-1
			If a_map(c_x,c_y,5) = 9 Then path$=path$ + "q":c_x=c_x-1:c_y=c_y-1
	Wend
EndIf

;if the check doesn't work: =(
If work=0
	path$="s"	;<- I just chose "s" because it sat between "a" & "d"... nothing relivent. =P
EndIf

;end main loop
EndIf
;--------------------------------------------------------------------------------


;Brilliant! more weak coding from Kev....
;In this final chunk, we move our li'l fella
;to the end point by reading the "path$" string
;backwards... Removing a direction once we've used it.
ms=MilliSecs()-oms
If ms> 200 Then											;<- this just slows the li'l fella down...
	If path$<>"s"										;	so we can watch him walk to his goal.
		If Len(path$)> 0								;	no real animation... but that's just fine
			get$=Right$(path$,1)						;	if your a fan of Nethack or Rouge like
			If get$ = "c" Then s_x=s_x-1: 	s_y=s_y-1	;	myself ;)
			If get$ = "x" Then s_x=s_x: 	s_y=s_y-1
			If get$ = "z" Then s_x=s_x+1: 	s_y=s_y-1
			If get$ = "d" Then s_x=s_x-1:   s_y=s_y
			If get$ = "a" Then s_x=s_x+1:	 s_y=s_y
			If get$ = "e" Then s_x=s_x-1:	 s_y=s_y+1
			If get$ = "w" Then s_x=s_x:		 s_y=s_y+1
			If get$ = "q" Then s_x=s_x+1:	 s_y=s_y+1

			path$=Left$(path$,Len(path$)-1)				;<-removes first letter from string$

		EndIf
	EndIf
	oms=MilliSecs()
EndIf

Wend
