; ID: 3102
; Author: Pakz
; Date: 2014-01-30 04:22:34
; Title: A Star Pathfinding
; Description: Basic A Star pathfinding code

Graphics 640,480,32,2
SetBuffer BackBuffer()
Global sx,sy,ex,ey
Global mapwidth = 39
Global mapheight = 29
Global cellwidth = 16
Global cellheight = 16
Dim map(mapwidth,mapheight)
Type ol
	Field x,y,f,g,h,px,py
End Type
Type cl
	Field x,y,f,g,h,px,py
End Type
Type path
	Field x,y
End Type
SeedRnd MilliSecs()
readmap()

While KeyDown(1) = False
	Cls
	setcoordinates()
	drawmap()
	tim = MilliSecs()
	findpath()
	tim = MilliSecs()-tim
	drawpath()
	Color 0,0,0
	Rect 0,0,GraphicsWidth(),12,True
	Color 255,0,0
	Text 0,0,"time taken "+tim
	Flip
	Delay 1000
Wend
End

Function findpath()
	; Remove old pathfinding data
	Delete Each ol
	Delete Each cl
	Delete Each path
	; Move the start position onto the open list
	d.ol = New ol
	d\x = sx
	d\y = sy
	Local exitloop = False
	Local tx,ty,tf,tg,th,tpx,tpy,newx,newy,lowestf
	While exitloop = False
		; If the open list is empty then exit loop (path not found)
		If openlistisempty() = True Then exitloop = True
		; Get the position from the open list with the lowest f value
		lowestf = 100000
		For e.ol = Each ol
			If e\f < lowestf Then
				lowestf = e\f
				tx = e\x
				ty = e\y
				tf = e\f
				tg = e\g
				th = e\h
				tpx = e\px
				tpy = e\py
			End If
		Next
		; If the current position is the end position then path is found
		If tx = ex And ty = ey Then
			exitloop = True
			f.cl = New cl
			f\x = tx
			f\y = ty
			f\f = tf
			f\g = tf
			f\h = th
			f\px = tpx
			f\py = tpy
			findpathback()
			Else
			; Move the current position to the closed list
			g.cl = New cl
			g\x = tx
			g\y = ty
			g\f = tf
			g\g = tg
			g\h = th
			g\px = tpx
			g\py = tpy
			; Remove the current position from the open list
			removefromopenlist(tx,ty)
			; Get the eight positions from around the current position
			; and move them to the open list
			;
			For y=-1 To 1
			For x=-1 To 1
			newx = tx + x
			newy = ty + y
			If newx > -1 And newy > -1 And newx < mapwidth+1 And newy < mapheight+1
			If isonopenlist(newx,newy) = False Then
			If isonclosedlist(newx,newy) = False Then
			If map(newx,newy) = 0 Then
				h.ol = New ol
				h\x = newx
				h\y = newy
				h\g = tg + 1
				h\h = distance(newx,newy,ex,ey)
				h\f = h\g+h\h
				h\px = tx
				h\py = ty
			End If
			End If
			End If
			End If
			Next
			Next
			
		End If
	Wend
End Function

Function findpathback()
	Local exitloop = False
	x = ex
	y = ey
	While exitloop = False
		For this.cl = Each cl
			If x = this\x And y = this\y Then
				x = this\px
				y = this\py
				that.path = New path
				that\x = x
				that\y = y
			End If
		Next
		If x = sx And y = sy Then exitloop = True
	Wend
End Function

Function drawpath()
	Color 255,255,0
	For this.path = Each path
		Oval this\x*cellwidth+4,this\y*cellheight+4,8,8,True
	Next
End Function

Function openlistisempty()
	Local count = 0
	For this.ol = Each ol
		count = count + 1
		If count > 0 Then Return False
	Next
	If count = 0 Then Return True
End Function

Function isonclosedlist(x,y)
	For this.cl = Each cl
		If this\x = x And this\y = y Then Return True
	Next
	Return False
End Function

Function isonopenlist(x,y)
	For this.ol = Each ol
		If this\x = x And this\y = y Then Return True
	Next
	Return False
End Function

Function removefromopenlist(x,y)
	For this.ol = Each ol
		If this\x = x And this\y = y Then
			Delete this
			Return
		End If
	Next
End Function

Function setcoordinates()
	Local exitloop = False
	While exitloop = False
		sx = Rand(mapwidth)
		sy = Rand(mapheight)
		ex = Rand(mapwidth)
		ey = Rand(mapheight)
		If map(sx,sy) = 0 And map(ex,ey) = 0 Then
			If sx<>ex And sy<>ey Then
				exitloop = True
			End If
		End If
	Wend
End Function

Function drawmap()
	Color 0,0,255
	For y = 0 To mapheight
	For x = 0 To mapwidth
		If map(x,y) = 1 Then
			Rect x*cellwidth,y*cellheight,cellwidth,cellheight,True
		End If
	Next
	Next
	Color 0,255,0
	Oval sx*cellwidth+8,sy*cellheight+8,8,8,True
	Color 255,0,0
	Oval ex*cellwidth+8,ey*cellheight+8,8,8,True
End Function

Function readmap()
	Restore level
	For y = 0 To mapheight
	For x = 0 To mapwidth
		Read a
		map(x,y) = a
	Next
	Next
End Function

Function distance(x1,y1,x2,y2)
	Return Sqr( ( x1 - x2 ) * ( x1 - x2 ) + ( y1 - y2 ) * ( y1 - y2 ) )
End Function

.level
Data 0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0
Data 0,1,1,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,1,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,1,1,1,0
Data 0,0,0,0,0,0,0,1,0,1,1,1,1,1,1,1,1,0,1,0,1,0,0,1,0,0,0,0,0,0,0,0,1,0,1,0,1,0,1,0
Data 0,1,1,1,1,1,0,1,0,1,0,0,0,0,0,0,1,0,1,0,1,0,0,0,0,0,1,1,1,1,1,1,1,0,1,0,1,0,1,0
Data 0,1,0,0,0,1,0,1,0,1,0,1,1,1,1,0,1,0,1,0,1,0,0,1,0,0,1,0,0,0,0,0,1,0,1,0,1,0,1,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,0,1,0,1,0,1,0,1,0,0,1,0,0,1,0,1,1,1,0,1,0,1,0,1,0,1,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,0,1,0,1,0,1,0,1,0,0,1,0,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,0,1,0,1,0,1,0,1,1,0,1,0,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,0,1,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0
Data 0,1,0,1,0,1,0,1,0,0,0,1,0,0,1,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,1,0,1,0,0,0,1,0,1,0
Data 0,1,0,1,0,1,0,1,0,1,0,1,0,0,1,0,1,0,0,0,0,1,0,1,0,0,1,0,0,0,1,0,1,0,1,0,1,0,1,0
Data 0,1,0,1,1,1,0,1,0,1,0,1,0,1,1,0,1,0,1,0,0,1,0,1,0,0,1,1,1,1,1,0,1,0,1,0,1,0,1,0
Data 0,1,0,0,0,0,0,1,0,1,0,1,0,0,0,0,1,0,1,0,0,1,0,1,0,0,0,0,0,0,0,0,1,0,1,0,1,0,1,0
Data 0,1,1,1,1,1,1,1,0,1,0,1,1,1,1,1,1,0,1,0,0,1,0,1,0,1,1,1,1,1,1,1,1,1,1,0,1,0,1,0
Data 0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0
Data 1,1,1,1,1,0,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,1,1,1,1,1,1,1,1,1,1
Data 0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,1,0,0
Data 0,0,0,1,1,1,1,1,1,1,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,0,1,0,0
Data 0,0,0,1,0,0,0,0,0,1,0,1,1,1,1,1,1,0,1,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,1,0,1,0,0
Data 0,0,0,1,0,1,1,1,0,1,0,1,0,0,0,0,1,0,1,0,0,0,0,1,0,1,1,1,1,1,1,1,1,1,0,1,0,1,0,0
Data 0,0,0,1,0,1,0,1,0,1,0,0,0,1,1,0,1,0,1,0,1,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0
Data 0,0,0,1,0,1,0,1,0,1,0,1,0,1,0,0,1,0,0,0,1,0,0,1,0,1,0,0,0,0,0,0,0,0,0,1,0,1,0,0
Data 0,0,0,1,0,1,0,1,0,1,0,1,0,1,0,0,1,0,1,0,1,0,0,1,0,1,1,1,1,1,1,1,1,1,0,1,0,1,0,0
Data 0,0,0,0,0,1,0,1,0,1,0,1,0,1,0,0,1,0,1,0,1,0,0,1,0,1,0,0,0,0,0,0,0,0,0,1,0,1,0,0
Data 0,0,0,1,0,1,0,1,0,1,0,1,0,1,0,0,1,0,1,0,1,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0
Data 0,0,0,1,0,1,0,1,0,1,0,1,0,1,0,0,1,0,1,0,1,0,0,1,0,1,1,1,1,1,1,1,1,1,0,1,0,1,0,0
Data 0,0,0,1,0,1,0,1,0,1,0,1,0,1,0,0,1,0,1,0,1,0,0,1,0,1,0,0,0,0,1,0,1,0,0,1,0,1,0,0
Data 0,0,0,1,0,1,0,0,0,1,0,1,0,1,0,0,1,0,1,0,1,0,0,1,0,1,0,0,0,0,1,0,1,0,0,1,0,1,0,0
Data 0,0,0,1,0,1,1,1,1,1,0,1,0,1,1,1,1,0,1,0,1,1,1,1,0,1,0,1,0,0,1,0,1,0,0,1,0,1,0,0
Data 0,0,0,1,0,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,0,0,0,0,0,1,0,1,0,0,0,0,0,0,0,1,0,0,0,0
