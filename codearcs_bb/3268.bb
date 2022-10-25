; ID: 3268
; Author: Pakz
; Date: 2016-04-30 01:01:51
; Title: 2d RPG player and map example
; Description: Collision with walls and items (Non scrolling map)

AppTitle "Topdown RPG player map collision - cursor keys to move."
Graphics 640,480,16,2
SetBuffer BackBuffer()

Global mw = 20
Global mh = 15
Global tw = 32
Global th = 32
Global px = 0
Global py = 0
Global pw = tw
Global ph = th

Dim map(mw,mh)

readlevel()

While KeyDown(1) = False
	Cls
	moveplayer
	playeritemcollision
	drawlevel
	drawplayer
	Flip
Wend
End

Function moveplayer()
	Local x=0
	Local y=0
	If KeyDown(200) ; up
		y=-1
	End If
	If KeyDown(205) ; right
		x=1
	End If
	If KeyDown(208) ; down
		y=1
	End If
	If KeyDown(203) ; left
		x=-1
	End If
	If px+x < 0 Then x=0
	If px+x > mw*tw Then x=0
	If py+y < 0 Then y=0
	If py+y > mh*th Then y=0
	If playermapcollision(px+x,py) = False
		px=px+x
	End If
	If playermapcollision(px,py+y) = False
		py=py+y
	End If

End Function

Function playermapcollision(x1,y1)
	Local cx=x1/tw
	Local cy=y1/th
	For y2=cy-1 To cy+1
	For x2=cx-1 To cx+1
		If x2>=0 And x2<=mw And y2>=0 And y2<=mh
		If map(x2,y2) = 1 ; is the map around the player a 1 value
		If RectsOverlap(x2*tw,y2*th,tw,th,x1,y1,pw,ph)
			;
			; Here the player is inside a wall
			; a value 1 on the map
			;
			Return True
			;
		End If
		End If
		End If
	Next
	Next
	; no collision occured
	Return False
End Function

Function playeritemcollision()
	Local cx=px/tw
	Local cy=py/th
	For y2=cy-1 To cy+1
	For x2=cx-1 To cx+1
		If x2>=0 And x2<=mw And y2>=0 And y2<=mh
		If map(x2,y2) = 3 ; is the map around the player a 3 value
		If RectsOverlap(x2*tw+8,y2*th+8,tw-16,th-16,px,py,pw,ph)
			;
			; Here the player touches a map item (3)
			; We remove it from the map
			;
			map(x2,y2) = 0
		End If
		End If
		End If
	Next
	Next
End Function



Function drawplayer()
	Color 0,0,255
	Oval px,py,pw,ph
End Function

Function drawlevel()
	For y=0 To mh-1
	For x=0 To mw-1
		If map(x,y) = 1
			Color 255,255,255
			Rect x*tw,y*th,tw,th,True
		End If
		If map(x,y) = 2
			Color 0,255,0
			x1=x*tw
			y1=y*th
			Line x1+tw/2,y1,x1+tw,y1+th
			Line x1+tw,y1+th,x1,y1+th
			Line x1,y1+th,x1+tw/2,y1
		End If
		If map(x,y) = 3
			Color 200,180,10
			Oval x*tw+4,y*th+4,tw-8,th-8
		End If
	Next
	Next
End Function

Function readlevel(level=1)
	Select level
		Case 1
			Restore level1
	End Select
	For y=0 To mh-1
	For x=0 To mw-1
		Read a
		map(x,y) = a
	Next
	Next
End Function

.level1
Data 0,0,0,3,3,3,0,1,1,1,1,0,1,1,1,1,1,1,1,1
Data 0,1,1,1,0,0,0,1,0,0,1,0,1,0,0,0,0,3,3,1
Data 0,1,1,3,0,2,0,1,0,0,1,0,1,0,0,0,0,3,3,1
Data 0,1,1,0,0,0,0,1,0,1,1,0,1,1,0,1,1,1,1,1
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0
Data 0,1,0,0,1,0,0,0,0,0,2,0,0,0,0,0,0,1,0,0
Data 0,1,0,0,1,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1
Data 0,1,0,3,1,0,0,2,0,0,0,0,0,0,1,0,0,0,0,0
Data 0,1,1,1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0
Data 0,0,1,1,1,1,1,1,1,1,0,0,2,0,1,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,1,1,1,1,0,1,0,0,0,0,0
Data 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
