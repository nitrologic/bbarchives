; ID: 3270
; Author: AdamStrange
; Date: 2016-05-11 02:28:32
; Title: 2d RPG scrolling final
; Description: 2d scrolling rpg

strict

'window setup
global windowWidth:int = 640
global windowHeight:int = 480

Graphics windowWidth, windowHeight


const MAP_NONE = 0
const MAP_WALL = 1
const MAP_WATER = 2
const MAP_TREE = 3
const MAP_DEEPWATER = 4
const MAP_ROCK = 5
const MAP_ROAD = 6
const MAP_DOOR = 7
const MAP_KEY = 8
const MAP_GOLD = 9
const MAP_GRASS = 10
const MAP_STONE = 11
const MAP_BRIDGE = 12
const MAP_BRIDGEV = 13
const MAP_ROOF = 14
const MAP_DARKGRASS = 15
const MAP_TREE2 = 16
const MAP_FENCE = 17
const MAP_LADDER = 18
const MAP_CLIFF = 19
const MAP_FLOWERS = 20
const MAP_HOLE = 21
const MAP_GRAVE = 22
const MAP_WATERFALL = 23
const MAP_SWORD = 24
const MAP_SHIELD = 25
const MAP_HEART = 26
const MAP_BOULDER = 27
const MAP_WATERBOULDER = 28



Global editmode:int = False
Global edittile:int 

Global mapEditmode:int = False

Global keys:int = 0
global totalKeys:int = 0
Global gold:int = 0
global totalgold:int = 0


' map variables
Global mapWidth:int = 40
Global mapHeight:int = 30
Global tileWidth:int = 32
Global tileHeight:int = 32
global displayWidth:int = windowWidth / tileWidth
global displayHeight:int = windowHeight / tileHeight

global xLevelMax:int = 5
global yLevelMax:int = 5
global totalLevels:int = xLevelMax * yLevelMax

print "totalLevels="+totalLevels

Global numlevels:int = 5

global map:int[totalLevels, mapWidth, mapHeight]

' player variables
Global pw = (tileWidth-5)
Global ph = (tileHeight-5)
Global px = GraphicsWidth()/2- tileWidth/2
Global py = GraphicsHeight()/2-tileHeight/2

' scrolling variables
Global mx
Global my
Global msx = 0
Global msy
'temporary vars for editing
Global tempmx
Global tempmy
Global tempmsx
Global tempmsy
Global tempmcx
Global tempmcy

global ignoreWalls:int = false

' for the multi map
'
' this is a mdimensional array
' the player location on the multi map
' is mcx (map cursor x)
' it starts at 1,1, the center of the 
' multidimensional array
'
Global mcx=1
Global mcy=1

global ml:int[xLevelMax, yLevelMax]




setblend alphablend

readlevels()
'makemaps()

'.mainloop
While KeyDown(1) = False and not(AppTerminate())
	Cls	
	If KeyHit(KEY_W) then
		ignoreWalls = not(ignoreWalls)
	end if
	
	if keyhit(KEY_M) then
		mapEditMode = not(mapEditMode)
		if not(mapEditMode) then saveLevels()
	end if
	
	If KeyHit(KEY_E) then
		If editmode = True Then
			editmode = False
			saveLevels()
			mx = tempmx
			my = tempmy
			msx = tempmsx
			msy = tempmsy
			mcx = tempmcx
			mcy = tempmcy
		Else
			editmode = True
			tempmx = mx
			tempmy = my
			tempmsx = msx
			tempmsy = msy
			tempmcx = mcx
			tempmcy = mcy
		end if
	EndIf
	
	edit()
	mapEdit()
	
	If editmode = False and mapEditMode = false
		local i:int
		For i=0 To 4 'speed of movement
			moveplayer
			centermap
			switchmap()
		Next
		
		drawmap(ml[mcx,mcy], msx,msy)
		
		setalpha 0.2
		setcolor 0,0,0
		drawrect 0,0,640,30
		setalpha 1
		
		drawplayer()
		
		setColor 0,0,0
		drawText "Use cursors to move around",1,1
		drawText "cursor : " + mcx +","+mcy+" on map : "+chr(ml[mcx,mcy]+65), 1,16
		drawText "gold:"+gold+" of "+totalgold+"   keys:"+keys+" of "+totalkeys+"    w: ignore walls", 241,1
		drawText "e: edit mode    m: map overview", 241,16
		setColor 240,240,150
		drawText "Use cursors to move around",0,0
		drawText "cursor : " + mcx +","+mcy+" on map : "+chr(ml[mcx,mcy]+65), 0,15
		drawText "gold:"+gold+" of "+totalgold+"   keys:"+keys+" of "+totalkeys+"    w: ignore walls", 240,0
		drawText "e: edit mode    m: map overview", 240,15
	End If
	
	delay 10
	Flip
Wend

saveLevels()
End


function saveLevels()
	local file:TStream = writefile("default.map")

	if not file then
		print "failed to create default.map file" 
	else
		local x:int
		local y:int
		local x1:int
		local y1:int
		
		for y = 0 to yLevelMax-1
			for x = 0 to xLevelMax-1
				writebyte file, ml[x,y] & 255
				
				For y1 = 0 To mapHeight-1
					For x1 = 0 To mapWidth-1
						writebyte file, map[ml[x,y], x1,y1] & 255
					Next
				Next
				
			next
		next
		closestream file
	end if
end function



Function readlevels()
	local file:TStream
	file = openfile("default.map")
	if file then
		print "opening file"

		local x:int
		local y:int
		local x1:int
		local y1:int
		
		for y = 0 to yLevelMax-1
			for x = 0 to xLevelMax-1
				ml[x, y] = readbyte(file)
				
				For y1 = 0 To mapHeight-1
					For x1 = 0 To mapWidth-1
						map[ml[x,y], x1,y1] = readbyte(file)
					Next
				Next
				
			next
		next
		closestream file

	else
		print "could not open file default.map"
		local i:int
		local x:int
		local y:int
		local tile:int
		
		'read inbuilt map data
		print numlevels
		For i=0 To numlevels-1
			For y=0 To mapHeight-1
				For x=0 To mapWidth-1
					Readdata tile
					map[i, x,y] = tile
					if tile = MAP_GOLD then totalgold :+ 1
					if tile = MAP_KEY then totalkeys :+ 1
				Next
			Next
		Next

		'null all level data
		For y=0 To yLevelMax-1
			For x=0 To xLevelMax-1
				ml[x, y] = -1
			next
		next

		'create new level data
		'corner maps -1 non existant
		ml[0,0]=-1
		ml[2,0]=-1
		ml[0,2]=-1
		ml[2,2]=-1
		' the maps assigned
		ml[1,0]=1
		ml[1,1]=0
		ml[1,2]=3
		ml[2,1]=2
		ml[0,1]=4
	end if

End Function


Function drawmap(level:int, xo:int, yo:int)
	local xs:int = -xo/mapWidth
	local ys:int = (-yo/mapHeight)-1
	if ys < 0 then ys = 0
	if xs < 0 then xs = 0

	local xe:int = xs+displayWidth + 4
	local ye:int = ys+displayHeight + 1
	
	if xe => mapWidth then xe = mapWidth-1
	if ye => mapHeight then ye = mapHeight-1
	
	local x:int
	local y:int	
	
	For y = ys To ye
		For x = xs To xe
			drawtile(x,y, map[level,x,y], xo,yo)
		Next
	Next
End Function



Function drawplayer()
	if ignoreWalls then 
		setColor 200,130,10
		drawOval px,py,pw,ph
		setcolor 250,250,250
		drawtext "ignore", px, py
	else
		setColor 230,230,100
		drawOval px,py,pw,ph
	end if
End Function


'triangle must be clockwise. or it won't draw
Function DrawTriangle(x1:Float,y1:Float, x2:Float,y2:Float, x3:Float,y3:Float, r1:Float, g1:Float, b1:Float, alpha:float)
'	glDisable(GL_TEXTURE_2D)
	
	glColor4f(r1, g1, b1, alpha)

	glBegin(GL_TRIANGLES)
	
		glVertex2f(x1,y1)
		glVertex2f(x2,y2)
		glVertex2f(x3,y3)

	glEnd()

'	glEnable(GL_TEXTURE_2D)
End Function


function drawFlower(x:int, y:int)
		setcolor 255,255,200
		drawline x,y+1,x+2, y+1
		drawline x+1,y,x+1, y+2
		
		setColor 150,100,0
		plot x+1, y+1
end function


function drawScreenTile(tile:int, x1:int, y1:int, Width:int, Height:int)
	local x4:int = x1 + Width-1
	local y4:int = y1 + Height-1
	
	local drawWater:int = false
	
	Select tile
		Case MAP_NONE 'base grass
			setColor 60,100,0
			drawRect x1,y1,Width,Height

			drawWater = true
			
		case MAP_SWORD	
			setColor 60,100,0
			drawRect x1,y1,Width,Height
			
			local x2:int = x1 + (width * 0.5)
			local h2:int = Height - 10
			local y2:int = y1+h2
			
			setcolor 160,160,160
			drawline x2,y1, x2,y1+h2
			setcolor 150,150,150
			drawline x2-1,y1+2, x2-1,y1+h2
			drawline x2-2,y1+6, x2-2,y1+h2
			setcolor 130,130,130
			drawline x2+1,y1+2, x2+1,y1+h2
			drawline x2+2,y1+6, x2+2,y1+h2

			setcolor 90,40,00
			drawrect x2-2,y2+4, 5,4

			setcolor 190,130,50
			drawrect x2-6,y2, 13,4

			setcolor 150,70,20
			drawoval x2-2,y2+6, 4,4
			
		case MAP_SHIELD
			setColor 60,100,0
			drawRect x1,y1,Width,Height

			setcolor 150,150,150

		case MAP_HEART
			setColor 60,100,0
			drawRect x1,y1,Width,Height

		Case MAP_GRAVE
			setColor 60,100,0
			drawRect x1,y1,Width,Height
			
			setcolor 150,150,150
			drawoval x1+5, y1+2, 15,10

			setcolor 130,130,130
			drawoval x1+5, y1+4, 15,10
			drawrect x1+5, y1+8, 15,6
			setcolor 120,120,120
			drawrect x1+5, y1+10, 15,6

			setColor 114,42,10
			drawrect x1+7, y1+16, 12,16
			setColor 104,32,0
			drawrect x1+13, y1+16, 6,16
						
		Case MAP_HOLE
			setColor 60,100,0
			drawRect x1,y1,Width,Height

			setColor 60,150,0
			drawoval x1, y1+4, Width, Height*0.6
			setColor 114,42,10
			drawoval x1, y1+5, Width, Height*0.6
			setColor 50,30,10
			drawoval x1, y1+8, Width, Height*0.6
			setColor 0,0,0
			drawoval x1, y1+12, Width, Height*0.4
			

		Case MAP_FLOWERS
			setColor 60,100,0
			drawRect x1,y1,Width,Height
			
			setcolor 200,150,120
			drawFlower(x1+3, y1+3)
			drawFlower(x1+8, y4-15)
			drawFlower(x1+12, y1+20)
			drawFlower(x4-12, y4-17)

		Case MAP_STONE
			setColor 101,93,94
			drawRect x1,y1,tileWidth,tileHeight

			setColor 90,83,84
			drawWater = true

		Case MAP_ROOF
			setColor 160,13,20
			drawRect x1,y1,tileWidth,tileHeight

			local xx:int
			local yy:int

			setColor 160,43,60
			drawrect x1,y1,tileWidth, 2

			setColor 100,0,0
			drawrect x1,y4-2,tileWidth,2

			setColor 140,0,0
			for yy = y1 to y4 step 4
				drawline x1, yy, x4, yy
			next
			
			setColor 80,50,20
			for xx = x1 to x4 step 6
				drawline xx,y1, xx, y4
			next
			

		Case MAP_BRIDGE
			setColor 120,51,20
			drawRect x1,y1,tileWidth,tileHeight

			local x2:int = x1 + (tileWidth*0.3)
			local x3:int = x1 + (tileWidth*0.7)
			local y3:int = y1 + (tileHeight-2)
			local xx:int

			setColor 80,50,20
			for xx = x1 to x4 step 6
				drawline xx,y1, xx, y4
			next
			
			setColor 30,100,245
			drawline x1,y1, x2,y1
			drawline x3,y1, x4,y1
			drawline x2,y4, x3,y4

			setColor 00,50,200
			drawline x1,y4, x2,y4
			drawline x3,y4, x4,y4
			drawline x2,y3, x3,y3

		Case MAP_BRIDGEV
			setColor 120,51,20
			drawRect x1,y1,tileWidth,tileHeight

			local yy:int

			setColor 80,50,20
			for yy = y1 to y4 step 6
				drawline x1,yy, x4,yy
			next
			
		Case MAP_DARKGRASS
			setColor 40,80,0
			drawRect x1,y1,tileWidth,tileHeight

			setColor 60,100,0
			drawWater = true
			
		Case MAP_FENCE
			setColor 60,100,0
			drawRect x1,y1,tileWidth,tileHeight
			
			local y2:int = y1+(tileHeight* 0.25)
			local y3:int = y1+(tileHeight* 0.5)
			local y5:int = y1+(tileHeight* 0.75)
			
			setcolor 180,180,180
			drawRect x1,y2,tileWidth,4
			drawRect x1,y3,tileWidth,4
			drawRect x1,y5,tileWidth,4
			setcolor 210,210,210
			drawLine x1,y2, x4,y2
			drawLine x1,y3, x4,y3
			drawLine x1,y5, x4,y5

			local x2:int = x1+(tileWidth* 0.23)
			local x3:int = x1+(tileWidth* 0.68)

			setcolor 150,150,150
			drawRect x2+4,y1+2,1,tileHeight-4
			drawRect x3+4,y1+2,1,tileHeight-4
			setcolor 170,170,170
			drawRect x2,y1+2,4,tileHeight-4
			drawRect x3,y1+2,4,tileHeight-4
			setcolor 210,210,210
			drawRect x2,y1+2,5,2
			drawRect x3,y1+2,5,2
			
		Case MAP_LADDER
			setColor 114,42,10
			drawRect x1,y1,tileWidth,tileHeight

			local x2:int = x1+(tileWidth* 0.23)
			local x3:int = x1+(tileWidth* 0.68)
			local y2:int = y1+1

			setColor 104,42,10
			drawRect x3,y1,10,tileHeight

			setColor 124,42,10
			drawRect x2,y1,5,tileHeight

			setColor 60,100,0
			drawLine x2,y1,x3,y1
			drawLine x1,y4,x2,y4
			drawLine x3,y4,x4,y4
			setColor 60,150,0
			drawLine x1,y1,x2,y1
			drawLine x3,y1,x4,y1
			drawLine x2,y2,x3,y2
			
			setcolor 200,200,150
			drawrect x2+2, y1, 2, tileHeight
			drawrect x3-2, y1, 2, tileHeight
			
			local yy:int
			for yy = y1+3 to y4 step 5
				drawrect x2, yy, 16, 2
			next
			
		Case MAP_CLIFF
			setColor 114,42,10
			drawRect x1,y1,tileWidth,tileHeight

			local x2:int = x1+(tileWidth* 0.23)
			local x3:int = x1+(tileWidth* 0.68)
			local y2:int = y1+1

			setColor 104,42,10
			drawRect x3,y1,10,tileHeight

			setColor 124,42,10
			drawRect x2,y1,5,tileHeight

			setColor 60,100,0
			drawLine x2,y1,x3,y1
			drawLine x1,y4,x2,y4
			drawLine x3,y4,x4,y4
			setColor 60,150,0
			drawLine x1,y1,x2,y1
			drawLine x3,y1,x4,y1
			drawLine x2,y2,x3,y2
			
		Case MAP_TREE2
			setColor 60,100,0
			drawRect x1,y1,tileWidth,tileHeight

			local x2:int = x1 + (tileWidth * 0.3)
			local x5:int = x1 + (tileWidth * 0.7)
			local x3:int = x1 + (tileWidth * 0.5)
			local y3:int = y1 - (tileHeight * 0.5)
			local y5:int = y4 - 6
			
			setColor 90,51,20
			drawRect x3-3,y5,6,6

			setColor 30,100,30
			drawOval x1, y1-20, tileWidth, tileHeight+13

			setColor 40,130,30
			drawOval x1, y1-20, tileWidth-7, tileHeight+13

			setColor 50,150,30
			drawOval x1+(tilewidth*0.1), y1-15, tileWidth*0.4, tileHeight
			
		Case MAP_TREE'tree
			setColor 60,100,0
			drawRect x1,y1,tileWidth,tileHeight

			local x2:int = x1 + (tileWidth * 0.3)
			local x5:int = x1 + (tileWidth * 0.7)
			local x3:int = x1 + (tileWidth * 0.5)
			local y3:int = y1 - (tileHeight * 0.5)
			local y5:int = y4 - 3

			drawTriangle(x1,y5, x3,y3, x4,y5, 0,0.5,0,1)
			drawTriangle(x1,y5, x3,y3, x2,y5, 0.1,0.6,0.1,1)
			drawTriangle(x5,y5, x3,y3, x4,y5, 0,0.4,0,1)

			setColor 90,51,20
			drawRect x3-3,y5,6,3
			
			setColor 0,50,0
			drawline x1,y5, x4, y5
			drawline x1,y5, x3, y3 
			drawline x4,y5, x3, y3 

		Case MAP_GRASS
			setColor 60,100,0
			drawRect x1,y1,tileWidth,tileHeight

			setColor 0,150,0
			local xx:int
			local yy:int
			for yy = y1 to y4 step 8
				for xx = x1 to x4 step 4
					drawline xx,yy, xx, yy-5
				next
			next 
			
		Case MAP_WALL
			setColor 140,140,140
			drawRect x1,y1,tileWidth,tileHeight

			setColor 100,100,100
			local x2:int = x1 + (tileWidth * 0.3)
			local x3:int = x1 + (tileWidth * 0.7)
			local y2:int = y1 + (tileHeight * 0.5)
			drawline x1,y1, x4,y1
			drawline x1,y2, x4,y2

			drawline x2,y1, x2,y2
			drawline x3,y2, x3,y4
			
		Case MAP_ROAD
			setColor 176,146,73
			drawRect x1,y1,tileWidth,tileHeight

			setColor 156,126,63
			drawWater = true
			
		Case MAP_WATER'water
			setColor 30,100,245
			drawRect x1,y1,tileWidth,tileHeight
			
			setColor 70,150,245
			drawWater = true

		Case MAP_WATERBOULDER
			setColor 30,100,245
			drawRect x1,y1,tileWidth,tileHeight
			
			setalpha 0.2
			setcolor 50,50,50
			drawoval x1,y1,width,height-2
			setalpha 1
			y1 :+ 5
			height :- 5

			setColor 140,80,60
			drawOval x1+2, y1, Width-6, Height-7

			setColor 170,100,70
			drawOval x1+2, y1, Width-10, Height-7
			
			setColor 70,150,245
			drawWater = true

		Case MAP_WATERFALL
			setColor 0,50,150
			drawRect x1,y1,tileWidth,tileHeight
			setColor 0,00,100
			drawRect x1,y4-5,tileWidth,5

			local x2:int = x1+(tileWidth* 0.23)
			local x3:int = x1+(tileWidth* 0.68)
			local y2:int = y1+1

			setColor 0,50,220
			drawRect x3,y1,10,tileHeight

			setColor 0,50,180
			drawRect x2,y1,5,tileHeight

			setColor 0,0,150
			drawLine x2,y1,x3,y1
			drawLine x1,y4,x2,y4
			drawLine x3,y4,x4,y4
			setColor 100,150,200
			drawLine x1,y1,x2,y1
			drawLine x3,y1,x4,y1
			drawLine x2,y2,x3,y2

			setColor 100,150,200
			drawLine x1+10,y1,x1+10,y1+10
			drawLine x1+6,y1,x1+6,y1+3
			drawLine x1+20,y1,x1+20,y1+5
			drawLine x1+26,y1,x1+26,y1+3
			drawLine x1+15,y1+10,x1+15,y1+20

			setColor 0,50,150
			drawLine x1+8,y4,x1+8,y4-10
			drawLine x1+17,y4,x1+17,y4-3
			drawLine x1+23,y4,x1+23,y4-5

'			setColor 50,130,215
'			drawWater = true

		Case MAP_DEEPWATER'deepwater
			setColor 00,50,200
			drawRect x1,y1,tileWidth,tileHeight

			setColor 50,130,215
			drawWater = true

		Case MAP_DOOR ' door
			setColor 120,50,20
			drawRect x1,y1,tileWidth,tileHeight
			
			setColor 100,60,30
			local xx:int
			local yy:int
			for xx = x1 to x4 step 4
				drawline xx,y1, xx, y4
			next

			setColor 210,120,70
			drawRect x1,y1,tileWidth,5

			setColor 100,60,30
			drawFrame(x1,y1,tileWidth-1,tileHeight-1)
			drawLine x1,y1+5,x4,y1+5

		Case MAP_KEY' key
			setColor 60,100,0
			drawRect x1,y1,tileWidth,tileHeight

			setColor 180,150,50
			drawrect x1,y1+15,tileWidth, 5
			drawrect x4-4,y1+15, 4,10
			setColor 140,110,30
			drawrect x1,y1+19,tileWidth, 1
			setColor 200,170,50
			drawrect x1,y1+16,tileWidth, 1

			setColor 140,110,30
			drawOval x1,y1+12,tileWidth*0.3, tileHeight*0.5
			setColor 200,170,50
			drawOval x1,y1+10,tileWidth*0.3, tileHeight*0.5

			setColor 60,100,0
			drawOval x1+3,y1+15,tileWidth*0.12, tileHeight*0.25
			
		Case MAP_ROCK
			setColor 60,100,0
			drawRect x1,y1,Width,Height
			
			setColor 50,50,50
			drawOval x1, y4-4, Width-4, 4

			drawOval x1, y1, Width-4, Height-4

			setColor 70,70,70
			drawOval x1, y1, Width-8, Height-4

			setColor 80,80,80
			drawOval x1, y1, Width*0.5, Height-10

		Case MAP_BOULDER
			setColor 60,100,0
			drawRect x1,y1,Width,Height
			
			setColor 50,80,50
			drawOval x1, y4-4, Width-4, 4
			
			y1 :+ 5
			height :- 5

			setColor 140,80,60
			drawOval x1, y1, Width-4, Height-4

			setColor 170,100,70
			drawOval x1, y1, Width-8, Height-4

			setColor 190,120,90
			drawOval x1+3, y1+2, Width*0.5, Height-12

		Case MAP_GOLD ' gold
			setColor 60,100,0
			drawRect x1,y1,Width,Height

			setColor 140,100,30
			drawOval x1, y4-10, Width*0.5, Height*0.3
			drawOval x1+15, y4-10, Width*0.5, Height*0.3
			setColor 230,200,0
			drawOval x1, y4-13, Width*0.5, Height*0.3
			drawOval x1+15, y4-13, Width*0.5, Height*0.3
			
			setColor 140,100,30
			drawOval x1+7, y4-17, Width*0.5, Height*0.3
			setColor 230,200,0
			drawOval x1+7, y4-20, Width*0.5, Height*0.3
	End Select
	
	if drawWater then
			local y2:int = y1 + (Height * 0.1)
			local x2:int = x1 + (Width * 0.3)
			drawline x1,y2,x2,y2
			y2 = y1 + (Height * 0.3)
			x2 = x1 + (Width * 0.4)
			local x3:int = x1 + (Width * 0.6)
			drawline x2,y2,x3,y2
			y2 = y1 + (Height * 0.6)
			x2 = x1 + (Width * 0.7)
			x3 = x1 + (Width * 0.9)
			drawline x2,y2,x3,y2
			y2 = y1 + (Height * 0.8)
			x2 = x1 + (Width * 0.2)
			x3 = x1 + (Width * 0.6)
			drawline x2,y2,x3,y2
	end if
end function

Function drawtile(x,y,t, xo:int = 0, yo:int = 0)
	local x1:int = (x*tileWidth) + xo
	local y1:int = (y*tileHeight) + yo

	drawScreenTile(t, x1, y1, tileWidth, tileHeight)
End Function



function rectsOverlap:int(x1,y1,w1,h1, x2,y2,w2,h2)
	w1 :+ x1
	h1 :+ y1
	w2 :+ x2
	h2 :+ y2
	
	if w1 < x2 then return false
	if x1 > w2 then return false

	if h1 < y2 then return false
	if y1 > h2 then return false
	
	return true
end function



function drawFrame(x:int, y:int, width:int, height:int)
	drawline x,y, x+width,y
	drawline x+width,y, x+width,y+height
	drawline x+width,y+height, x, y+height
	drawline x,y+height, x,y
end function



function mapEdit()
	If mapEditMode = false then return
	
	local x:int
	local y:int
	local x1:int
	local y1:int
	local width:int = windowWidth / 27

	setColor 240,240,150
	drawText "Select level from below",5,windowHeight-56
	drawText "Select map position from below, then select level from the bottom",5,70

	For x = 0 To totalLevels-1
		x1 = (x*Width)
		if x = ml[mcx,mcy] then
			setcolor 255,0,0
			drawrect x1,windowHeight-tileHeight-8, Width-1,tileHeight+8
		end if
		setColor 240,240,150
		if mousex()>x1 and mousex()<x1+width and mousey() > windowHeight-32 then
			setcolor ((sin(millisecs()*0.4)*0.5)+0.75)*255,0,0
			if mousedown(1) then ml[mcx,mcy] = x
		end if
		drawFrame(x1,windowHeight-tileHeight, Width-1,tileHeight-1)
		setColor 240,240,240
		drawtext chr(65+x), x1+6,windowHeight-tileHeight+10
	next		
	
	for y = 0 to yLevelMax-1
		y1 = (y*60)+90
		for x = 0 to xLevelMax-1
			x1 = (x*60)+10
			
			if mcx = x and mcy = y then
				setColor 250,0,0
				drawrect x1,y1, 59,59
			end if

			setColor 240,240,150
			if mouseX() > x1 and mousey() > y1 and mousex() < x1+60 and mousey() < y1+60 then
				setcolor ((sin(millisecs()*0.4)*0.5)+0.75)*255,0,0
				if mouseDown(1) then
					mcx = x
					mcy = y
				end if
			end if
			
			drawFrame(x1,y1, 59,59)
			if ml[x, y] = -1 then
			else
				drawtext chr(65+ml[x,y]), x1+20,y1+20
			end if
		next
	next
end function


Function edit()
	If editmode = false then return

	local x:int
	local x1:int
	local width:int = windowWidth / 27

	drawmap(ml[mcx,mcy], msx,msy)

	'draw top black bg
	setColor 20,20,20		
	drawRect 0,0,GraphicsWidth(),36
	'draw bottom black bg
	drawRect 0,windowHeight-36,GraphicsWidth(),36
	
	setColor 240,240,150
	drawText "Select tile from above",5,tileHeight+10
	drawText "Select level from below",5,windowHeight-56

	For x = 0 To totalLevels-1
		x1 = (x*Width)
		if x = ml[mcx,mcy] then
			setcolor 255,0,0
			drawrect x1,windowHeight-tileHeight-8, Width-1,tileHeight+8
		end if
		setColor 240,240,150
		if mousex()>x1 and mousex()<x1+width and mousey() > windowHeight-32 then
			setcolor ((sin(millisecs()*0.4)*0.5)+0.75)*255,0,0
			if mousedown(1) then ml[mcx,mcy] = x
		end if
		drawFrame(x1,windowHeight-tileHeight, Width-1,tileHeight-1)
		setColor 240,240,240
		drawtext chr(65+x), x1+6,windowHeight-tileHeight+10
	next		
	
	For x = 0 To 29
		x1 = (x*Width)

		drawScreenTile(x, x1, 0, Width, tileHeight)

		If RectsOverlap(MouseX(),MouseY(),1,1,x* Width,0,32,32)
			setColor 128+sin(millisecs())*128,0,0
			drawFrame x* Width,0,width-1,31

			If MouseDown(1) = True
				edittile = x
			End If
		End If
		
		If edittile = x
			setColor 255,0,0
			drawFrame(x* Width,0,width-1,31)
			drawRect x* Width,32,width,8
		End If
	Next
	
	'draw the map cursor
	If RectsOverlap(MouseX(),MouseY(),1,1,  0,32,GraphicsWidth(),GraphicsHeight()-64)
		local x1:int = (MouseX()-msx)/tileWidth
		local y1:int = (MouseY()-msy)/tileHeight
		
		If MouseDown(1) = True then
			if x1 > -1 and y1 > -1 then
				map[ml[mcx,mcy], x1,y1] = edittile
			end if
		End If
		
		drawtile(x1,y1,edittile, msx, msy)
		
		setColor 255,0,0
		drawFrame x1* tileWidth +msx,y1* tileHeight +msy,31,31
		drawFrame x1* tileWidth +msx-1,y1* tileHeight +msy-1,33,33
	End If

	setalpha (sin(millisecs()*0.4)*0.5)+0.75
	drawplayer()
	setalpha 1

	If KeyDown(KEY_RIGHT) ' right
		msx = msx-3
	EndIf
	If KeyDown(KEY_LEFT)'left		
		msx = msx+3
	End If
	If KeyDown(KEY_UP)'up
		msy = msy+3
	End If
	If KeyDown(KEY_DOWN)'down
		msy = msy-3
	End If

End Function


' here we check if the player is
' at a border and if he can go to another map
' and set the start positions
Function switchmap()

	' touches the top part of the screen
	If py < 5 And mcy>0 
		If ml[mcx,mcy-1] <> -1
			mcy = mcy-1
			py = windowHeight-tileHeight-5
			msy=-(mapHeight*tileHeight-GraphicsHeight())
		End If
	End If

	' touches the left side of the screen
	If px<5 And mcx>0 
		If ml[mcx-1,mcy] <>-1
			mcx = mcx - 1
			px = windowWidth-5-pw			
			msx = -(mapWidth*tileWidth - GraphicsWidth())
		End If		
	End If

	' touches the right side of the screen
	If px+pw>GraphicsWidth()-4 And mcx<2
		If ml[mcx+1,mcy] <> -1
			mcx=mcx+1
			px = 5
			msx=0
		End If
	End If

	' touches the bottom of the screen
	If py+ph>GraphicsHeight()-5 And mcy<2
		If ml[mcx,mcy+1] <>-1
			mcy=mcy+1
			py = 5
			msy=0
		End If
	End If
End Function




Function centermap()
	If px<GraphicsWidth()/3 And Abs(msx)>0 Then
		msx=msx+1
		px=px+1
	end if
	If py<GraphicsHeight()/3 And Abs(msy)>0 Then
		msy=msy+1
		py=py+1
	end if
	If px>GraphicsWidth()/100*66
		If (Abs(msx)+GraphicsWidth()) < mapWidth*tileWidth then
			msx=msx-1
			px=px-1
		End If
	End If
	If py>GraphicsHeight()/100*66
		If (Abs(msy)+GraphicsHeight()) < mapHeight*tileHeight then
			msy=msy-1
			py=py-1
		End If
	End If	
End Function



Function moveplayer()
	Local x:int = 0
	Local y:int = 0
	If KeyDown(KEY_UP) ' up
		y = -1
	End If
	If KeyDown(KEY_RIGHT) ' right
		x = 1
	End If
	If KeyDown(KEY_DOWN) ' down
		y = 1
	End If
	If KeyDown(KEY_LEFT) ' left
		x = -1
	End If
	
	If px+x < 0 Then x = 0
	If px+pw+x > GraphicsWidth() Then x = 0
	If py+y < 0 Then y = 0
	If py+ph+y > GraphicsHeight() Then y = 0
	
	local nx:int
	local ny:int
	local collide:int
	
	if x <> 0 then
		nx = (px + Abs(msx)) + x
		ny = (py + Abs(msy))
		collide = playermapcollision(nx, ny, x,0)
		if collide = MAP_NONE then
			px = px+x
		else if collide = MAP_WATER and rand(0,100) < 40 then
			px = px+x
		End If
	end if
	
	if y <> 0 then
		nx = (px + Abs(msx))
		ny = (py + Abs(msy)) + y
		collide = playermapcollision(nx, ny, 0,y)
		if collide = MAP_NONE then
			py = py+y
		else if collide = MAP_WATER and rand(0,100) < 40 then
			py = py+y
		End If
	end if

End Function



function getTileHit:int(mapTile:int)
	if mapTile = MAP_WALL or mapTile = MAP_DEEPWATER or mapTile = MAP_TREE or mapTile = MAP_ROCK or mapTile = MAP_WATERFALL or..
		 mapTile = MAP_ROOF or mapTile = MAP_TREE2 or mapTile = MAP_FENCE or mapTile = MAP_CLIFF then
			if ignoreWalls then
				return MAP_NONE
			else
				return MAP_WALL
			end if
	end if
	if mapTile = MAP_WATER or mapTile = MAP_GRASS or mapTile = MAP_LADDER then return MAP_WATER

	if mapTile = MAP_BOULDER then return MAP_BOULDER
	if mapTile = MAP_WATERBOULDER then return MAP_WATERBOULDER
	
	return MAP_NONE
end function

function checkBoulder:int(cx:int, cy:int, x:int, y:int)
	local static:int = false
	
	local mapHit:int = getTileHit(map[ml[mcx,mcy], cx,cy])
	local nextHit:int = getTileHit(map[ml[mcx,mcy], cx+x,cy+y])

	if mapHit = MAP_BOULDER then
		
		if map[ml[mcx,mcy], cx+x,cy+y] = MAP_HOLE then
			map[ml[mcx,mcy], cx+x,cy+y] = MAP_FLOWERS
		else if map[ml[mcx,mcy], cx+x,cy+y] = MAP_WATER then
			map[ml[mcx,mcy], cx+x,cy+y] = MAP_WATERBOULDER
		else if map[ml[mcx,mcy], cx+x,cy+y] = MAP_WATERBOULDER then
			return true
		else
			map[ml[mcx,mcy], cx+x,cy+y] = MAP_BOULDER
		end if
		map[ml[mcx,mcy], cx,cy] = MAP_NONE

		if nextHit <> MAP_NONE then
			return true
		end if
	end if
	
	return static
end function
 
function positionCollide:int(x:int, y:int, xdir:int,ydir:int)
	Local cx:int = x/tileWidth
	Local cy:int = y/tileHeight
	if cx >= mapWidth or cy >= mapHeight then return 

	local mapTile:int = map[ml[mcx,mcy], cx,cy]
	local mapHit:int = getTileHit(mapTile)

	'deal with pushing a boulder - we also check if it has hit something or changed here too
	if mapHit = MAP_BOULDER then
		local static:int = true
		if xdir < 0 then
			if cx > 0 then
				static = checkBoulder(cx, cy, -1, 0)
			end if
		else if xdir > 0 then
			if cx < mapWidth-2 then
				static = checkBoulder(cx, cy, 1, 0)
			end if
		end if

		if ydir < 0 then
			if cy > 0 then
				static = checkBoulder(cx, cy, 0, -1)
			end if
		else if ydir > 0 then
			if cy < mapHeight-2 then
				static = checkBoulder(cx, cy, 0, 1)
			end if
		end if

		if static then
			return MAP_WALL
		else		 
			return MAP_NONE
		end if
		
	else if mapHit <> MAP_NONE then
		return mapHit
	end if

	If mapTile = MAP_GOLD then
		gold :+ 1
		map[ml[mcx,mcy], cx,cy] = MAP_NONE
	else if mapTile = MAP_DOOR then
		If keys > 0 then
			keys :- 1
			map[ml[mcx,mcy], cx,cy] = MAP_NONE
		else
			return MAP_WALL	
		End If
	else if mapTile = MAP_KEY then
		keys :+ 1
		totalkeys :- 1
		map[ml[mcx,mcy], cx,cy] = MAP_NONE
	end if
	
	' no collision occured
	Return MAP_NONE
end function
	
	
Function playermapcollision(x:int, y:int, xd:int, yd:int)
	local c1:int = positionCollide(x, y, xd,yd)
	local c2:int = positionCollide(x+26, y, xd,yd)
	local c3:int = positionCollide(x+26, y+26, xd,yd)
	local c4:int = positionCollide(x, y+26, xd,yd)

	if c1 = MAP_WALL or c2 = MAP_WALL or c3 = MAP_WALL or c4 = MAP_WALL then return MAP_WALL
	if c1 = MAP_WATER or c2 = MAP_WATER or c3 = MAP_WATER or c4 = MAP_WATER then return MAP_WATER
	
	return MAP_NONE
End Function



'map 0
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,0,0,2,2,4,2,2,2,0,0,0,0,3,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,2,2,2,2,0,0,0,0,0,0,3,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,0,0,0,0,2,2,0,0,0,0,0,0,3,3,3,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3
defdata 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3
defdata 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,2,2,4,2,2,2,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1
defdata 0,0,0,0,0,0,0,2,2,2,2,2,4,4,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1
defdata 0,0,0,0,0,0,2,4,4,4,4,4,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1
defdata 0,0,0,0,0,2,4,4,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1
defdata 0,0,0,2,2,4,4,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,8,0,1,1,1,1,1,1,1
defdata 2,2,2,4,4,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,0,0,0,1,1,1,1,1,1,1
defdata 4,4,4,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,1,0,1,1,1,1,1
defdata 2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,0,0,1,1,1,1,1
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,0,0,0,0,1,0,1,1,1
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,0,0,0,0,0,1,1,1,0
defdata 0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,3,3,0,0,0,1,1,0,0
defdata 0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0
defdata 0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,0,0,0
defdata 0,0,0,0,0,1,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,0
defdata 0,0,0,1,1,1,1,1,1,1,0,0,0,0,1,9,9,9,1,0,0,0,0,3,3,0,0,0,0,0,3,3,3,3,3,0,0,0,0,0
defdata 0,0,0,1,0,0,0,9,9,1,0,0,0,0,1,9,9,9,1,0,0,0,0,3,3,3,0,0,0,0,3,3,3,3,3,0,0,0,0,0
defdata 0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,0,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 0,0,0,1,0,0,0,0,0,1,0,0,0,0,1,0,0,0,1,0,0,0,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 0,0,0,1,0,0,0,0,0,1,0,0,1,1,1,1,1,7,1,0,0,0,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0
defdata 0,0,0,1,0,0,0,0,0,7,0,0,7,0,0,0,0,0,1,0,0,0,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 0,0,0,1,0,0,0,0,0,1,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,0,0,0,0,0
defdata 0,0,0,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,8,2,2,4,4,2,2,0,0,0,0
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,4,4,4,2,0,2,0,0,0,0
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,4,4,2,2,2,2,2,0,0,0,0
'map 1
defdata 3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1
defdata 3,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1
defdata 3,3,1,0,9,9,1,0,0,0,3,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1
defdata 3,3,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1
defdata 3,0,1,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1
defdata 3,0,1,1,7,1,0,0,0,0,0,0,2,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1
defdata 0,0,0,0,0,0,0,0,0,0,2,2,2,4,4,2,2,0,0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,1,0,1,1,1,1,1
defdata 0,1,0,0,0,0,0,0,0,2,2,4,2,2,2,2,2,0,0,0,2,2,2,2,0,0,0,0,3,0,0,0,0,0,0,1,1,1,1,1
defdata 1,1,1,0,0,0,0,0,2,2,4,2,2,0,0,0,0,0,0,0,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0,1,0,1,0,0
defdata 0,1,0,0,0,2,2,2,2,2,2,2,0,0,0,0,0,0,0,0,2,2,0,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 0,0,0,0,0,2,4,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 0,1,0,0,0,2,4,4,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 1,1,1,0,0,2,2,4,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,2,2,2,2,2,0,0,0,0,0,3,3,3,0,0,0
defdata 0,1,0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,4,2,2,2,2,0,0,0,0,0,0,3,3,0,0
defdata 0,0,0,2,4,4,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,4,4,4,4,2,0,2,0,0,0,0,0,0,3,0,0
defdata 0,0,0,2,4,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,4,4,4,4,2,2,2,0,0,0,0,0,0,0,0
defdata 0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,2,2,4,2,2,2,0,2,0,0,0,0,0,0,0,0
defdata 0,2,2,2,2,8,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,2,2,2,2,0,2,2,2,0,0,0,0,0,0,0,0,0
defdata 2,2,2,0,0,0,0,0,0,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0
defdata 2,2,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0
defdata 2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1
defdata 0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,0,0,0,0,0,0,0,1,1
defdata 0,0,0,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,8,0,1,1,1,1,1
defdata 0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1
'map 2
defdata 3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3
defdata 3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3
defdata 3,3,3,3,3,3,0,0,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,3,3,3
defdata 3,3,3,0,0,0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,3,3,3,0,3,3,3,0,3,3,0,0,0,0,0,0,0,3,3,3
defdata 3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,3,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,3,3,3
defdata 3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,0,3,3,3
defdata 3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,4,4,4,2,0,3,3,3
defdata 3,8,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,0,3,3,3
defdata 3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,3,3
defdata 3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 1,0,0,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,3,3
defdata 1,1,0,0,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,3,3
defdata 1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,3,3
defdata 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 0,0,1,0,0,0,0,0,0,0,0,0,0,1,7,1,1,1,0,0,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 0,0,1,1,0,0,0,0,0,0,0,0,0,1,0,0,0,1,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,9,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,1,9,9,9,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,0,0,0,0,0,0,0,0,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,3,3,0,0,0,0,0,3
defdata 0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,0,0,0,0,0,0,0,3
defdata 0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3
defdata 0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,3,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 3,3,3,0,0,0,0,0,0,0,0,0,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3,3,3
defdata 3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3
'.level4
defdata 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3
defdata 1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3
defdata 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3,3
defdata 3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3
defdata 3,0,0,1,1,1,7,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3
defdata 3,0,0,1,9,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 3,0,0,1,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,7,1,1,1,1,0,0,3
defdata 3,0,0,1,0,0,0,0,0,0,0,9,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,9,1,0,0,3
defdata 3,0,0,1,0,0,0,1,1,1,1,1,1,7,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,9,1,0,0,3
defdata 3,0,0,1,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,3
defdata 3,0,0,1,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1,0,0,3
defdata 3,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,7,1,1,1,1,0,0,3
defdata 3,0,0,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 3,0,0,1,0,0,1,0,0,1,0,0,0,0,0,0,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3
defdata 3,0,0,7,0,0,1,1,1,1,0,0,0,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3
defdata 3,0,0,1,0,0,1,0,0,0,0,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,3
defdata 3,0,0,1,0,9,1,0,0,0,3,3,3,3,3,3,3,8,0,0,0,3,3,3,0,3,0,0,0,0,0,0,3,3,3,3,3,3,3,3
defdata 3,0,0,1,1,1,1,0,0,3,3,3,3,3,3,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,3,3,8,3,3,3,3,3
defdata 3,0,0,0,0,0,0,0,0,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,0,0,0,0,0,3,3,3,3,3
defdata 3,0,0,0,0,0,0,0,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,0,0,3,3,3,3,3
defdata 3,0,0,0,0,0,0,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,3,0,0,0,0,0,0,0,0,3,0,3,3
defdata 3,0,0,0,0,0,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,3,3
defdata 3,3,0,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3
defdata 3,3,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3
defdata 3,3,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3
defdata 3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,2,2,2,2,2,2,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3
defdata 3,3,3,0,0,0,0,0,0,0,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,0,2,2,2,2
defdata 3,3,3,0,0,0,0,0,2,2,2,2,2,2,0,2,2,2,2,2,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
defdata 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2
'.level5
defdata 3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0
defdata 3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0
defdata 3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0
defdata 3,3,3,3,0,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,3,3,3,3,3,3,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,0,0,0,0,0
defdata 3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,0,0,0,0,0
defdata 3,3,3,3,3,3,0,3,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0
defdata 3,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,3,0,3,3,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,0,3,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,3,0,0,0,0,1,1,1,0,0,0,0,0,3,0,0,0,0,7,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,3,0,0,0,0,7,0,1,0,0,0,0,3,0,0,0,0,0,1,0,0,0,0,8,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,3,0,0,0,0,1,9,1,0,0,0,0,3,0,0,0,0,0,1,0,0,0,0,8,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,3,0,0,0,0,1,1,1,0,0,0,0,0,0,0,0,0,0,1,9,9,8,8,8,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,1,1,1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,3,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 3,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0
defdata 2,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 2,3,3,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 2,2,3,3,0,0,0,0,0,0,0,0,0,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0
defdata 2,2,2,3,0,3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,0,0,0,0,0,0,0,0,0,0,0
defdata 2,2,2,3,3,3,0,2,2,2,2,0,2,0,0,0,0,0,0,0,0,0,0,0,0,3,3,3,3,3,0,0,0,0,0,0,0,0,0,0
defdata 2,2,2,3,3,2,0,2,2,2,2,2,2,2,2,2,2,2,2,2,0,0,0,0,0,0,0,3,3,3,0,0,0,0,0,0,0,0,0,0
defdata 2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,3,0,0,3,3,0,0,3,3,3,3,0,0,0,0,0,0,0,0
defdata 2,2,4,4,4,4,4,4,2,2,2,2,2,2,4,4,4,2,2,2,2,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0,0,0,0,0
defdata 2,0,2,4,4,4,4,2,4,4,4,4,4,4,4,4,4,4,4,4,2,2,3,3,3,3,3,3,3,3,3,3,3,3,3,0,0,0,0,0
defdata 2,2,4,4,4,4,4,4,4,4,2,2,4,4,4,4,4,4,4,4,4,2,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3
defdata 2,2,4,4,4,4,4,4,4,2,2,2,2,2,4,4,4,4,4,4,4,4,4,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3
