; ID: 3273
; Author: Pakz
; Date: 2016-05-30 18:29:12
; Title: Rooms and Doors generator
; Description: Make maps/dungeons

Graphics 800,600,32,2
SetBuffer BackBuffer()

AppTitle "Map generator"

; mapwidthheight
Global mw=100
Global mh=100
;tilewidthheight
Global tw=GraphicsWidth()/mw
Global th=GraphicsHeight()/mh
;min/maxroomsizewh
Global minroomsize = 5
Global maxroomsize = 15

Dim map(mw,mh)

SeedRnd MilliSecs()

makemap

Global timer=CreateTimer(10)

While KeyDown(1) = False
	WaitTimer timer
	Cls
	drawmap
	If KeyDown(57) Or cnt>20 Then newmap:cnt=0
	cnt=cnt+1
	Color 255,255,255
	Text 0,0,"Press space to generate new - esc = exit"
	Flip
Wend
End

Function newmap()
	For y=0 To mh
	For x=0 To mw
		map(x,y)=0
	Next
	Next
	makemap
End Function

Function makemap()
	map(mw/2,mh/2) = 3
	Local total=Rand(20000,150000)
	For i=0 To total
		x = Rand(maxroomsize+2,mw-(maxroomsize+2))
		y = Rand(maxroomsize+2,mh-(maxroomsize+2))
		If map(x,y) = 3
			a = Rand(0,4)
			w=Rand(minroomsize,maxroomsize)
			h=Rand(minroomsize,maxroomsize)
			Select a
				Case 0;nroom
				If fits(x-w/2,y-h,w,h-1) = True
					mr(x,y-h,x+w/2,y-h/2,x,y,x-w/2,y-h/2)
				EndIf
				Case 1;eroom
				If fits(x+1,y-h/2,w,h) = True
					mr(x+w/2,y-h/2,x+w,y,x+w/2,y+h/2,x,y)
				EndIf
				Case 2;sroom
				If fits(x-w/2,y+1,w,h) = True
					mr(x,y,x+w/2,y+h/2,x,y+h,x-w/2,y+h/2)
				EndIf
				Case 3;wroom
				If fits(x-w-1,y-h/2,w,h) = True
					mr(x-w/2,y-h/2,x,y,x-w/2,y+h/2,x-w,y)
				EndIf
			End Select
		End If
	Next
	; here we remove left over doors
	For y=2 To mh-2
	For x=2 To mw-2
		If map(x,y) = 3
			; if into darkness then remove
			If map(x-1,y) = 0 Or map(x+1,y) = 0
				map(x,y) = 2
			End If
			If map(x,y-1) = 0 Or map(x,y+1) = 0
				map(x,y) = 2
			End If
			cnt=0
			; every door if blocked remove
			For y1=y-1 To y+1
			For x1=x-1 To x+1
			If map(x1,y1) = 2 Then cnt=cnt+1
			Next
			Next
			If cnt>2 Then map(x,y)=2
		End If
	Next
	Next
End Function

; makeroom
Function mr(x1,y1,x2,y2,x3,y3,x4,y4)
	For y5=y1 To y3
	For x5=x4 To x2
		map(x5,y5) = 1
	Next
	Next
	For y5=y1 To y3
		map(x4,y5) = 2
		map(x2,y5) = 2		
	Next
	For x5=x4 To x2
		map(x5,y1) = 2
		map(x5,y3) = 2
	Next
	map(x1,y1) = 3
	map(x2,y2) = 3
	map(x3,y3) = 3
	map(x4,y4) = 3

End Function

; Is there anything in the map
Function fits(x,y,w,h)
	; if outside
	If x<0 Or y<0 Or x+w>mw Or y+h>mh Then Return False	
	; if inside
	For y1=y To y+h
	For x1=x To x+w
		If map(x1,y1)>0 Then Return False
	Next
	Next
	Return True
End Function

Function drawmap()
	For y=0 To mh
	For x=0 To mh
		Select map(x,y)
			Case 0;nothing
			Color 0,0,0
			Case 1;floor
			Color 255,255,255
			Case 2;wall
			Color 100,100,100
			Case 3;door
			Color 255,0,0
		End Select
		Rect x*tw,y*th,tw,th
	Next
	Next
End Function
