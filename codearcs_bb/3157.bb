; ID: 3157
; Author: Pakz
; Date: 2014-11-01 17:21:28
; Title: Mesh surface Packer Example
; Description: 2d Example how you can turn a mesh of blocks(map) into bigger blockw.

; Big Surfaces Maker

Graphics 800,600,32,2
SetBuffer BackBuffer()
SeedRnd 1

AppTitle "Press escape to end."

Global mapw = 50
Global maph = 50
Dim map(mapw,maph)

Type r
	Field x,y,w,h
End Type
Dim  cmap(mapw,maph)
initmap()
ms = MilliSecs()
initsurfaces()	
ms = MilliSecs() - ms

timer = CreateTimer(60)
While KeyDown(1) = False
	WaitTimer timer
	Cls
	drawmap()
	drawrects()
	;
	If cnt > 60*3
		initmap()
		ms = MilliSecs()		
		initsurfaces()	
		ms = MilliSecs() - ms
		cnt=0
	End If
	cnt=cnt+1
	Text GraphicsWidth()-196,10,"Took:"+ms+" ms"
	Flip
Wend
End

Function initsurfaces()
	Delete Each r
	For y=0 To maph
	For x=0 To mapw
		cmap(x,y) = 0
	Next
	Next
	;
	; Pass 1 - Fit increasingly smaller blocks into the space and add to list
	;
	cnt=0
	exitloop = False
	rad = mapw
	x1 = 0
	y1 = 0
	While exitloop = False
		fits = True
		x2 = -rad
		y2 = -rad
		If map(x1,y1) = 1
			For y2=-rad To rad
			For x2=-rad To rad
				x3 = x1+x2
				y3 = y1+y2
				If RectsOverlap(x3,y3,1,1,0,0,mapw+1,maph+1) = True
						If map(x3,y3) = 0 Then fits = False:Exit
						If cmap(x3,y3) = 1 Then fits = False:Exit
					Else
					fits = False :Exit
				EndIf
			Next
			Next
		
			If fits = True Then
				For y2 = -rad To rad
				For x2 = -rad To rad
					x3 = x1 + x2
					y3 = y1 + y2
					cmap(x3,y3) = 1
				Next
				Next
				r1.r = New r			
				r1\x = x1-rad
				r1\y = y1-rad			
				r1\w = rad*2
				r1\h = rad*2
			End If
		End If
		x1 = x1 + 1
		If x1 > mapw
			y1 = y1 + 1
			x1 = 0
		End If
		If x1 => mapw And y1=>maph Then 
			x1 = 0
			y1 = 0
			rad = rad - 1
		End If
		If rad < 0 Then exitloop = True
	Wend	
	;
	; Pass 2 - check the list for 4x4 rectangles to create one off
	;
	For y=0 To maph
	For x=0 To mapw
		If map(x,y) = 1
			For this.r = Each r
				If this\x = x And this\y = y And this\w = 0
					aset = False
					For a.r = Each r
						If a\x = this\x+1 And a\y = this\y And a\w = 0 Then aset = True 
					Next
					bset = False
					For b.r = Each r
						If b\x = this\x And b\y = this\y+1 And b\w = 0 Then bset = True 
					Next
					cset = False
					For c.r = Each r
						If c\x = this\x+1 And c\y = this\y+1 And c\w = 0 Then cset = True 
					Next
					If aset = True And bset = True And cset = True
						that.r = New r
						that\x = this\x
						that\y = this\y
						that\w = 1
						that\h = 1
						For a.r = Each r
							del = False
							If a\x = this\x+1 And a\y = this\y And a\w = 0 Then Del = True
							If a\x = this\x And a\y = this\y+1 And a\w = 0 Then del = True
							If a\x = this\x+1 And a\y = this\y+1 And a\w = 0 Then del = True
							If del = True Then Delete a
						Next
						Delete this
					End If
				End If			
			Next
		End If
	Next
	Next
End Function

Function drawrects()
	Color 255,255,255
	For this.r = Each r
		Rect this\x*10,this\y*10,(this\w+1)*10,(this\h+1)*10,False
	Next
End Function

Function drawmap()
	For y=0 To maph
	For x=0 To mapw
		Select map(x,y)
			Case 0:Color 0,0,0
			Case 1:Color 100,100,100
		End Select
		Rect x*10,y*10,10,10,True
	Next
	Next
End Function

Function initmap()
	For y=0 To maph
	For x=0 To mapw
		map(x,y) = 0
	Next
	Next	

	exitloop = False
	While exitloop = False
		x1 = Rand(mapw)
		y1 = Rand(maph)
		rad = Rand(3,6)
		For y2 = -rad To rad
		For x2 = -rad To rad
			x3 = x1+x2
			y3 = y1+y2
			If x3 => 0 And y3 >= 0 And x3 =< mapw And y3 <= maph
				map(x3,y3) = map(x3,y3) + 1
				If map(x3,y3) > 10 Then exitloop = True				
			End If			
		Next
		Next
	Wend	
	For y = 0 To maph
	For x = 0 To mapw
		If map(x,y) < 5 Then map(x,y) = 0
		If map(x,y) > 4 Then map(x,y) = 1
	Next
	Next
End Function
