; ID: 1877
; Author: Curtastic
; Date: 2006-12-11 03:02:57
; Title: Water 2D
; Description: Realtime water particles to play with.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;WATER 2D. Written by Curtastic.
;CONTROLS:
;Leftclick to make walls.
;Rightclick to erase walls.
;Spacebar or middleclick to make water.
;Enter to make it rain.
;Best run with debug off.
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;if pressure<>0 then water levels are accurate. it runs slower
;the higher the number, the faster and less accurate pressure is
;if pressure is positive then a gap must be at least 2 pixels wide for pressure to work
Const PRESSURE=1

;makes water that falls be sprayish, instead of smooth-slimy. runs a little slower
Const FRAY=1

;shows error messages and stuff
Const DEBUG=0




Const scx=320,scy=240
Graphics scx,scy,0,1


;those rude people who break your stuff.
;actually its to debug, its the amount of particles that are moving.
Global movers


;map=0 =empty
;map=1 =wall
;map=2 =particle
Dim map(scx,scy)

Dim dotmap.dot(scx,scy)

Global image=CreateImage(scx,scy)

;get the transparent color
Global backcolor

SetBuffer ImageBuffer(image)
backcolor=ReadPixel(1,1)

;make border walls
Rect 0,0,scx,scy,0


;make map know border walls
For fx=0 To scx-1
	map(fx,0)=1
	map(fx,scy-1)=1
Next

For fy=0 To scy-1
	map(0,fy)=1
	map(scx-1,fy)=1
Next


Type dot
	Field x,y,moved,presx,blue
End Type


Dim movex(4),movey(4)
movex(0)=1
movex(2)=-1
movey(1)=1
movey(3)=-1


timer=CreateTimer(250)

Local d2.dot


Repeat
	SetBuffer ImageBuffer(image)
	
	controls()
	dotsupdate()

	SetBuffer FrontBuffer()

	drawfront()
	
	;Flip False ;Uncomment if using BlitzPlus
	
	
	WaitTimer timer
	
	If KeyHit(1) Then End
Forever



Function DrawFront()
	DrawBlock image,0,0
	Color 255,0,0
	If DEBUG Then Text 1,1,movers
	Rect MouseX(),MouseY(),3,3

End Function



Function DotsUpdate()
	Local d2.dot

	If DEBUG=0 Then LockBuffer
	If DEBUG Then movers=0
	For d.dot=Each dot
		If d\moved=1 Then
			If DEBUG Then movers=movers+1

				
			If d\y>=scy-3 Then
				;fell to the bottom of the screen
				map(d\x,d\y)=0
				dotmap(d\x,d\y)=Null
				For fy=-1 To 0
					For fx=-1 To +1
						d2=dotmap(d\x+fx,d\y+fy)
						If d2<>Null Then
							d2\moved=1
						EndIf
					Next
				Next
				WritePixelFast d\x,d\y,0
				Delete d
			Else
				If map(d\x,d\y+1)=0 Then
					;nothing under it
		
					;move down
					
					If FRAY Then
						;push other fallers aside to widen the downward stream
						For dir=-1 To +1 Step 2
							d2=dotmap(d\x+dir,d\y+1)
							If d2<>Null Then d2\presx=d2\presx+dir
						Next
	
						If d\presx=0 Then
							dotmove(d,0,1)
						ElseIf map(d\x+Sgn(d\presx),d\y+1)=0 Then
							;move to the side also
							dotmove(d,Sgn(d\presx),1)
							d\presx=0
						Else					
							dotmove(d,0,1)
							d\presx=0
						EndIf
					Else
						dotmove(d,0,1)
					EndIf
				Else
					;something under it
	
					nodir=0
					For go=1 To scx+1
						For dir=-1 To +1 Step 2
							If nodir<>dir Then
								If map(d\x+go*dir,d\y)=1 Then
									If nodir<>0 Then
										;both dirs were nonos. nowhere to teleport to
										d\moved=0
										
										If PRESSURE Then
											If map(d\x,d\y+1)=2 Then
												If Rand(1,PRESSURE)=1 Then
													dotcheckpressure(d)
												EndIf
											EndIf
										EndIf
										
										Goto dotdone
									EndIf
									nodir=dir
								EndIf
								If map(d\x+go*dir,d\y+1)=0 Then
									dotmove(d,go*dir,1)
									Goto dotdone
								ElseIf map(d\x+go*dir,d\y)=0 Then
									d2=dotmap(d\x+go*dir,d\y+1)
									If d2<>Null Then
										If d2\moved=1 Then
											dotmove(d,go*dir,0)
											Goto dotdone
										EndIf
									EndIf
								EndIf
							EndIf
						Next
					Next
					If DEBUG Then error "unreachable code reached"
				EndIf
			EndIf
			
		EndIf
		.dotdone
	Next

	If DEBUG=0 Then UnlockBuffer

End Function




Function DotMove(d.dot,x,y)
	Local d2.dot

	dotmap(d\x,d\y)=Null
	map(d\x,d\y)=0

	If x=0 And y=1 Then
		;common speedup
		For fx=-1 To +1
			d2=dotmap(d\x+fx,d\y-1)
			If d2<>Null Then d2\moved=1
		Next
	Else
		For fy=-1 To +1
			For fx=-1 To +1
				d2=dotmap(d\x+fx,d\y+fy)
				If d2<>Null Then d2\moved=1
			Next
		Next
	EndIf

	WritePixelFast d\x,d\y,backcolor
	
	d\x=d\x+x
	d\y=d\y+y
	dotmap(d\x,d\y)=d
	WritePixelFast d\x,d\y,d\blue
	
	map(d\x,d\y)=2

	d2=dotmap(d\x-1,d\y-1)
	If d2<>Null Then d2\moved=1
	d2=dotmap(d\x,d\y-1)
	If d2<>Null Then d2\moved=1
	d2=dotmap(d\x+1,d\y-1)
	If d2<>Null Then d2\moved=1
	d2=dotmap(d\x-1,d\y)
	If d2<>Null Then d2\moved=1
	d2=dotmap(d\x+1,d\y)
	If d2<>Null Then d2\moved=1
End Function


;this function is only called if PRESSURE<>0
;travels the border of the pool to find a spot that can move up, and is farther down than d.dot
Function DotCheckPressure(d.dot)
	they=d\y+1

	startx=d\x
	starty=d\y

	ang=0
	;0=right, 1=down, 2=left, 3=up
	;"And %11" wraps number to be in range of 0 to 3
	
	x=startx
	y=starty
	
	;scale the outer wall by always trying to move left. so the outer wall will always be on your left
	;for traveling the edge of the the pool, anything that is not a particle is considered a wall
	Repeat

		;try to turn left
		ang2=(ang-1) And %11
		newx=x+movex(ang2)
		newy=y+movey(ang2)
		If map(newx,newy)=2 Then
			x=newx
			y=newy
			ang=ang2
		Else
			;try to go straight
			newx=x+movex(ang)
			newy=y+movey(ang)
			If map(newx,newy)=2 Then
				x=newx
				y=newy
			Else
				;try to turn right
				ang2=(ang+1) And %11
				newx=x+movex(ang2)
				newy=y+movey(ang2)
				If map(newx,newy)=2 Then
					x=newx
					y=newy
					ang=ang2
				Else
					;turn around
					ang=(ang+2) And %11
				EndIf
			EndIf
		EndIf
		
		
		;find a spot where the water is lower and can move up
		If y>they Then
			If ang<>1 Then ;speedup, cant happen when going down
				
				;found what we are looking for
				If map(x,y-1)=0 Then
					dotmove(d, x-d\x, y-1-d\y)
					Return 1
				EndIf
				;check sides
				If PRESSURE<0 Then
					For fx=-1 To +1 Step 2
						If map(x+fx,y)=0 Then
							dotmove(d, x+fx-d\x, y-d\y)
							Return 1
						EndIf
					Next
				EndIf
			EndIf
		EndIf
		If DEBUG Then If KeyDown(2) Then WritePixel x,y,$FF0000+Rnd(255)
		
		;just went in a big circle around the pool
		If x=startx And y=starty Then Return 0
	Forever
End Function



Function Controls()
	Local d2.dot
	
	;Enter to make rain.
	If KeyDown(28) Then
		For f=1 To 10
			d.dot=New dot
			d\x=Rnd(scx-3)+1
			d\y=1
			If map(d\x,d\y)>0 Then
				Delete d
			Else
				d\blue=Rnd(100,255)
				d\moved=1
				dotmap(d\x,d\y)=d
				map(d\x,d\y)=2
				WritePixel d\x,d\y,d\blue
				dotmove(d,0,0)
			EndIf
		Next
	EndIf
	
	;Make water from the mouse.
	If KeyDown(57) Or MouseDown(3) Then
		d.dot=New dot
		d\x=MouseX()
		d\y=MouseY()
		If d\x<1 Then d\x=1
		If d\y<1 Then d\y=1
		If d\x>scx-2 Then d\x=scx-2
		If d\y>scy-2 Then d\y=scy-2
		If map(d\x,d\y)>0 Then
			Delete d
		Else
			d\blue=Rnd(100,255)
			d\moved=1
			dotmap(d\x,d\y)=d
			map(d\x,d\y)=2
			WritePixel d\x,d\y,d\blue
			dotmove(d,0,0)
		EndIf
	EndIf
	
	;Draw terrain.
	If MouseDown(1) Then
		mx=MouseX()
		my=MouseY()
		For fy=0 To 5
			For fx=0 To 5
				If inscreen(mx+fx,my+fy,1) Then
					If map(mx+fx,my+fy)=0 Then
						map(mx+fx,my+fy)=1
						WritePixel mx+fx,my+fy,Rnd(55+fy*20,155+fy*20) Shl 8
					EndIf
				EndIf
			Next
		Next
	EndIf
	
	;Erase terrain.
	If MouseDown(2) Then
		mx=MouseX()
		my=MouseY()
		For fy=0 To 5
			For fx=0 To 5
				If inscreen(mx+fx,my+fy,1) Then
					If map(mx+fx,my+fy)=1 Then
						WritePixel mx+fx,my+fy,backcolor
						map(mx+fx,my+fy)=0
						For fy2=-1 To +1
							For fx2=-1 To +1
								d2=dotmap(mx+fx+fx2,my+fy+fy2)
								If d2<>Null Then
									d2\moved=1
								EndIf
							Next
						Next
					EndIf
				EndIf
			Next
		Next
	EndIf
End Function



;used for controls only
Function InScreen(x,y,closer=0)
	Return x>=closer And y>=closer And x<scx-closer And y<scy-closer
End Function




;this function is changed to be WritePixelFast when DEBUG is on
Function WritePixelFast2(x,y,c)
	If inscreen(x,y,1)=0 Then
		If DEBUG=0 Then UnlockBuffer
		error x+" "+y
	EndIf
	WritePixel x,y,c
End Function


Function Error(s$)
	If DEBUG=0 Then UnlockBuffer
	RuntimeError "Error: "+s
End Function
