; ID: 3143
; Author: zoqfotpik
; Date: 2014-09-29 06:49:22
; Title: Dijkstra Map Pathfinding
; Description: Pathfinding using the Dijkstra Map algorithm

' Dijkstra map pathfinding, as discovered by Brian Walker, author of the excellent roguelike Brogue.
' Read the below article and hopefully my code makes sense.
' http://www.roguebasin.com/index.php?title=The_Incredible_Power_of_Dijkstra_Maps
' ~ZFP
Global map:Int[64,64]
Global dmap:Int[64,64]

Graphics 640,480

Function initdmap()
'This sets up the initial high value of squares in the dijkstra map
	For x = 0 To 63
	For y = 0 To 63
	dmap[x,y]=66666 ' arbitrary
	Next
	Next
	
End Function

Function initmap()
'Randomly initialize the wall map.  0 is not wall, 1 is wall.
	For i = 0 To 63
	For j = 0 To 63
	If Rand(10)>6 map[i,j]=1
	Next
	Next
End Function

Function calcdmap:Int()
' This performs one pass through the dijkstra map.
	changes = 0 ' how many changes have been made this pass through the dmap.
	For x = 1 To 62
	For y = 1 To 62
		If map[x,y]=0
			lowestvalue = 66666
			For i = x-1 To x+1
				For j=y-1 To y+1
					If i=x And j=y i=i+1
					If dmap[i,j]<lowestvalue lowestvalue = dmap[i,j]
				Next
			Next
			If dmap[x,y]>lowestvalue+1
				dmap[x,y]=lowestvalue+1
				changes=changes+1
			EndIf
		EndIf
	Next
	Next
	Print changes
	Return changes
End Function

Function procdmap()
' Perform multiple passes on the map.  Do it until changestomap = 0
	changestomap = 10000
	While changestomap  > 0 
		changestomap =calcdmap()
	Wend
	
End Function

Function drawdmap()
' Draw a basic heat map for dmap number values
	For i = 0 To 63 
		For j = 0 To 63
			SetColor dmap[i,j]*2,0,0
			If dmap[i,j]=66666 SetColor 255,255,255
			DrawRect i*10,j*10,9,9
		Next
	Next
End Function

initmap()
initdmap()
dmap[1,1]=0
procdmap()
	x = 20 
	y = 20

While Not KeyDown(KEY_ESCAPE)
	Cls
	
	If MouseDown(1)	' Set a new x and y of the navigator dot
		x = MouseX()/10
		y = MouseY()/10
	EndIf
	
	If MouseDown(2) ' set a new goal square and reprocess the dijkstra map
		initdmap()
		dmap[MouseX()/10,MouseY()/10] = 0
		procdmap()
	EndIf
		
	' The below code moves the navigator along the dmap in a very barebones way
	' the slowness of the display is because of this
	nx = x+Rand(3)-2
	ny = y+Rand(3)-2
	If dmap[nx, ny] < dmap[x,y] 
		x = nx
		y = ny
	EndIf
			
	'calcdmap()
	drawdmap()
	SetColor 0,255,0
	DrawRect(x*10,y*10,9,9)
	Flip
Wend
