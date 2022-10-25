; ID: 2235
; Author: Jesse
; Date: 2008-03-26 21:05:23
; Title: 2D Tile map engine
; Description: Intermediate tile map engine

' map constants

Const MINTILESA				:Int = 10
Const MINTILESD				:Int = 10
	
Const TILEWIDTH				:Float = 32.0
Const TILEHEIGHT			:Float = 32.0

Const MIDTILEWIDTH			:Float = TILEWIDTH/2
Const MIDTILEHEIGHT			:Float = TILEHEIGHT/2
'type of tiles

Const WALKABLE				:Int = 0
Const UNWALKABLE			:Int = 1

' type of grass

Const GRASSTYPE				:Int = 0

' type of wall

Const WALLTYPE				:Int = 1


'	map controls

Const MAP_LEFT				:Int = KEY_LEFT
Const MAP_RIGHT				:Int = KEY_RIGHT
Const MAP_UP				:Int = KEY_UP
Const MAP_DOWN				:Int = KEY_DOWN

Const SPEED_X				:Float = 6.0
Const SPEED_Y				:Float = 6.0


'Type of tiles
Type set
	Field a:Int 'Walkable unwalkable
	Field b:Int 'Type of tile (ex: wall,grass)
	Field c:Int 'type of graphic tile to display (ea: if wall which wall) 
End Type

Type TMap
	Field x					:Float,..
		  y					:Float
	Field speedx			:Float,..
		  speedy			:Float
	Field maxx				:Float,..
		  maxy				:Float
	Field bitx				:Float,..
		  bity				:Float
	Field tilesAcross		:Float,..
		  tilesDown			:Float
	Field width				:Float,..
		  height			:Float
	Field viewwidth			:Float,..
		  ViewHeight		:Float
	Field Midviewwidth		:Float,..
		  Midviewheight		:Float
	Field Offsetx			:Float,..
		  Offsety			:Float
	Field windowwidth		:Float,..
		  windowheight		:Float
	Field tilewidth			:Float,..
		  tileheight		:Float
	Field maze				:set[][]
	Field tilesinviewacross	:Float,..
		  tilesinviewdown	:Float
	Field screen_width		:Float,.. 
		  screen_height		:Float
	Field wall				:TImage
	Field tile				:TImage[8]
	Field windowshiftx		:Float
	Field windowshifty		:Float
	Field Offbitx			:Float,..
		  Offbity			:Float
	Field clearscreen		:Int
'sets up the maze including tiles and format

'************************************************************************************
'																					*
'			creates a map any size 													*
'																					*
'		mw = mapwidth in tiles														*
'		mh = mapheight in tiles														*
'																					*
'		MINTILESA = controlls the minimum number of tiles across					*
'		MINTILESD = controlls the minimum number of tiles down						*
'																					*
'************************************************************************************ 

	Function Create:TMap(mw:Float=MINTILESA,mh:Float=MINTILESD) 'mapwidth,mapheight,tilewidth,tileheight 
		If mw< MINTILESA Then mw=MINTILESA
		If mh< MINTILESD Then mh=MINTILESD
		Local map:Tmap = New TMap
		map.screen_width  = GraphicsWidth()
		map.screen_height = GraphicsHeight()
		map.wall=LoadAnimImage("walltiles.png",TILEWIDTH,TILEHEIGHT,0,16,FILTEREDIMAGE|DYNAMICIMAGE)
		map.tile[0]	=LoadImage("grass.png", FILTEREDIMAGE|DYNAMICIMAGE)
		map.tilesAcross = mw
		map.tilesdown = mh
		map.windowwidth=GraphicsWidth() 
		map.windowheight=GraphicsHeight()
		map.tilewidth = TILEWIDTH
		map.tileheight = TILEHEIGHT
		map.width = mw * TILEWIDTH 'multiply map width by bitmap width for map width in pixels
		map.height= mh * TILEHEIGHT 'multiply map height by bitmap height for map height in pixels
		centermap(map)
		map.maxx = map.width-map.windowwidth
		map.maxy = map.height-map.windowheight
		map.midviewwidth  = (map.Tilesinviewacross / 2)*TILEWIDTH
		map.midviewheight = (map.Tilesinviewdown/ 2)*TILEHEIGHT
		If map.maxx<0 map.maxx = 0
		If map.maxy<0 map.maxy = 0
		map.speedx = SPEED_X
		map.speedy = SPEED_Y
		
	'create map array
		map.maze = map.maze[..mw]
		For Local h:Int = 0 To mw-1
			
			map.maze[h]=map.maze[h][..mh]
		
		Next
		For Local down:Int = 0 To mh-1
			For Local across:Int = 0 To mw-1 'places a wall edge around maze
				map.maze[across][down]= New set
				If across = 0 Or across = (mw-1) Or down=0 Or down = (mh-1)
				
					map.maze[across][down].a=UNWAlKABLE
					map.maze[across][down].b=WALLTYPE
				
				Else
				
					map.maze[across][down].a=WALKABLE
					map.maze[across][down].b=GRASSTYPE
				
				EndIf
			Next
		Next

	'places wall tiles randomly on the maze excluding maze edges		
		Local rx:Int
		Local ry:Int 
		For Local n:Int = 1 To mw*mh Step 5 ' step determine s more or less the density 
	 	
			rx = Rnd(1,mw-2)
			ry = Rnd(1,mh-2)
			map.maze[rx][ry].a = UNWALKABLE
			map.maze[rx][ry].b = WALLTYPE
		
		Next	
		CompileMaze(map.maze)
		Return map
		
	End Function
	
'************************************************************************************
'																					*
'		moves Map Window using the mouse when the mouse is less then				*
'		five pixels from edges of screen											*
'																					*
'************************************************************************************	

	'moves the maze when mouse is at edges of screen	
	Method MouseMapControl(controlled:Int=False) 
		
		If Not controlled Then
			
			If width > Windowwidth Then
				If MouseX() >(screen_width -5)
					MoveMapX(speedx)  
				ElseIf MouseX() < 5
					MoveMapX(-speedx)
				EndIf
            EndIf

			If height > windowheight Then
				If MouseY() > (screen_height - 5)
					MoveMapY(speedy) 
				ElseIf MouseY() < 5
					MoveMapY(-speedy) 
				EndIf
			EndIf
		
		EndIf
		
		bitx = x Mod tilewidth	' division remainder determins the number of pixels
		bity = y Mod tileheight	' to move per tile before moving to the next tile. 
		offbitx = offsetx-bitx
		offbity = offsety-bity
	EndMethod

'************************************************************************************
'																					*
'			use keyboard to move window in preset steps								*
'																					*
'************************************************************************************
	
	Method KeyControl(controlled:Int=False) 
		
		If Not controlled Then
	
			If width > Windowwidth Then
				If KeyDown(MAP_RIGHT)
					MoveMapX(speedx)  
				ElseIf KeyDown(MAP_LEFT)
					MoveMapX(-speedx)
				EndIf
            EndIf

			If height > windowheight Then
	
				If KeyDown(MAP_DOWN)
					MoveMapY(speedy) 
				ElseIf KeyDown(MAP_UP)
					MoveMapY(-speedy) 
				EndIf
	
			EndIf
		
		EndIf
		
		bitx = x Mod tilewidth	' division remainder determins the number of pixels
		bity = y Mod tileheight	' to move per tile before moving to the next tile. 
		offbitx = offsetx-bitx
		offbity = offsety-bity
	
	EndMethod
	
'************************************************************************************
'																					*
'		returns a valid random walkable x,y position								*
'																					*
'************************************************************************************
	
	Method Random(rx:Float Var,ry:Float Var)
		Local x:Int
		Local y:Int
		Repeat 
	
			x:Int = Rand(1,TilesAcross-1)
			y:Int = Rand(1,TilesDown-1 )
	
		Until maze[x][y].a = WALKABLE
		 
		rx = x*TILEWIDTH + MIDTILEWIDTH
		ry = y*TILEHEIGHT + MIDTILEHEIGHT
	
	End Method
'************************************************************************************
'																					*
'		moves map window to a valid x,y positon										*
'																					*
'************************************************************************************
	Method XYControl(x:Float Var,y:Float Var)
		If y > Maxy Then 
			Self.y = maxy 
		ElseIf y < 0 Then
			Self.y = 0
		Else
			Self.y = y
		EndIf
		If x > maxx  Then
			Self.x = maxx
		ElseIf x < 0 Then
			Self.x = 0
		Else
			Self.x = x
		EndIf
		
		x = Self.x
		y = Self.y
		bitx = Self.x Mod tilewidth	' division remainder determins the number of pixels
		bity = Self.y Mod tileheight	' to move per tile before moving to the next tile. 
		offbitx = offsetx-bitx
		offbity = offsety-bity

	End Method

'************************************************************************************
'																					*
'		moves map position x,y to center of screen if possible						*
'																					*
'************************************************************************************
	
	Method Centerto(x:Float,y:Float,dx:Float,dy:Float,speed:Float)
		Local 	sx:Float,.. 'viewwidth center relative to map.
				sy:Float
		Local 	fx:Float,..
				fy:Float
		
		If Not speed
		
			If dx = 0 Then dx = SPEED_X
			If dy = 0 Then dy = SPEED_Y
		
		EndIf
		
		dx = Abs(dx)
		dy = Abs(dy)
		fy:Float = y+dy
		
		sx:Float = Self.x+midviewwidth
		
		If x < sx  Then
		 
			Self.x :- dx
			sx :- dx
			If x > sx Then Self.x = x-midviewwidth
		
		ElseIf x > sx  Then 
		
			Self.x :+ dx
			sx :+ dx
			If x < sx Then Self.x = x-midviewwidth
		
		EndIf

		If Self.x > maxx  Then Self.x = Maxx
		If Self.x < 0 Then Self.x = 0

		bitx = Self.x Mod tilewidth
		offbitx = offsetx-bitx
		sy:Float = offsety+Self.y+midviewheight
		
		If y < sy Then 
		
			Self.y:-dy
			sy:-dy
			If y > sy Then Self.y = y-midviewheight
		
		ElseIf y > sy Then 
		
			Self.y :+ dy
			sy:+dy
			If y < sy Then Self.y = y-midviewheight
		
		EndIf

		If Self.y > maxy  Then Self.y = Maxy
		If Self.y < 0 Then Self.y = 0

		bity = Self.y Mod tileheight
		offbity = offsety-bity
	
	End Method
		
'************************************************************************************
'																					*
'		returns x,y position relative to window and screen position 				*
'																					*
'************************************************************************************

	Method GetXY(x:Float Var,y:Float Var)
		
		x = offsetx-Self.x
		y = offsety-Self.y
	
	End Method
	
'************************************************************************************
'																					*
'			sets map moving speed													*
'																					*
'************************************************************************************	

	Method setspeed(sx:Float=SPEED_X,sy:Float=SPEED_Y)
	
		speedx = sx
		speedy = sy
	
	End Method

'************************************************************************************
'																					*
'		move map X direction a specified amount	+ or -								*		
'																					*
'************************************************************************************

	Method MoveMapX(x:Float = SPEED_X)
	
		Self.x :+x
		If Self.x < 0 
			Self.x = 0 
		ElseIf Self.x > maxx
			Self.x = maxx
		EndIf
	
	End Method

'************************************************************************************
'																					*
'		clears screen only when map either is smaller then screen size				*
'																					*
'************************************************************************************
	
	Method windowclear()
		If clearscreen Then Cls()
	End Method

'************************************************************************************
'																					*
'		moves map Y direction a specified amount + or -								*
'																					*
'************************************************************************************
	
	Method MoveMapY(y:Float = SPEED_Y)
	
		Self.y :+ y 
		If Self.y < 0 
			Self.y = 0
		ElseIf Self.y > maxy
			Self.y = maxy
		EndIf
	
	End Method

'************************************************************************************
'																	*
'		This Function checks for collition of objects with map tiles.			*
'																	*
'		x,y is the position to check collition from							*
'																	*
'		sw1,sh1 --> is x,y of point 1	from the center of the object				*
'		sw2,sh2 --> is x,y of point 2	from the center of the object				*
'																	*
'		as in a car collition with an object, if the car is traveling in the		*
'		forward direction, the car front or two front points are the first		*
'		two points affected by the collition.								*
'																	*
'																	*
'************************************************************************************


	Method RectCollided:Int(x:Float,y:Float,sw1:Float,sh1:Float,sw2:Float,sh2:Float)
			
			Local collided:Int = True
			If  maze[(x+sw1)/TILEWIDTH][(y+sh1)/TILEHEIGHT].b=0 And ..  
				maze[(x+sw2)/TILEWIDTH][(y+sh2)/TILEHEIGHT].b=0 Then

				'moving along the x axis
				If x < (width-HALFBITMAPWIDTH)   And  x > 0 Then collided = False  
				'move along the y axis
				If y < (height-HALFBITMAPHEIGHT) And  y > 0 Then collided = False
		  	EndIf
			Return collided
	
	End Method

'************************************************************************************
'																	*
'		moves the display window left, right, up or down						*
'		usefull for when  window(or map) is smaller then display are			*
'																	*
'************************************************************************************ 

	Method SetMapOffset(x:Float,y:Float)
	
		If width < screen_width
			If x > viewwidth 
				offsetx = viewwidth
			ElseIf x <-viewwidth 
				offsetx = -viewwidth
			Else
				offsetx = x
			EndIf
		EndIf
		If height < screen_height
			If y > viewheight
				offsety = viewheight
			ElseIf y < -viewheight
				 offsety = -viewheight
			Else
				offsety = y
			EndIf
		EndIf
	
	End Method
'************************************************************************************
'																	*
'			Displays a rotated image to its corresponding map position			*
'																	*
'************************************************************************************

	Method DrawImageToMap(image:timage,x:Float,y:Float,angle:Float,index:Int = 0)
	
			Local thisx:Float, thisY:Float
			Local posx:Float, posy:Float
			GetXY(thisx,thisy)
			thisx:+x
			thisy:+y
			posx= thisx+image.width
			posy= thisy+image.height
			SetRotation angle
			If (posx >= 0) 
				If (thisx < SCREEN_WIDTH)
					If (posy >= 0)
					 	If (thisy < SCREEN_HEIGHT)
							DrawImage image,thisx,thisy,index
						EndIf
					EndIf
				EndIf
			EndIf
					
	End Method
'************************************************************************************
'																					*
'			Displays visible map area												*
'																					*
'************************************************************************************

	Method display()
	
		Local 		nx		:Int,..
					ny		:Int
		Local 		tilex	:Float,..
					tiley	:Float
		Local 		tx		:Int = x/TileWidth,..	
			 		ty		:Int = y/TileHeight
		Local 		tpx		:Float,..
					tpy		:Float
		
		SetColor 255,255,255
		SetRotation(0)
		
		For ny = 0 To tilesinviewdown 		
			tiley = ny+ty   
			tpy = offbity+ny*TileHeight
			For nx = 0 To tilesinviewacross 
				tilex = nx+tx   
				If tilex < tilesacross And tiley < tilesdown
					Local shape:Int = maze[tilex][tiley].b
					Select shape
						Case WALLTYPE
							DrawImage(wall,offbitx+nx*TileWidth,tpy,maze[tilex][tiley].c)
						Case GRASSTYPE
							DrawImage(tile[maze[tilex][tiley].c],offbitx+nx*TileWidth,tpy)
					End Select
				EndIf		
			Next
		Next
	End Method

End Type

'*************************************************************************************
'																	*
'			Compiles tiles adjacent To each other To uniform patterns			*
'			for use with "mazetiles.png"									*
'			may be used with different tile set but must fallow pattern:		*
' 			codes generated is binary									* 
' 			(+) -> connects To (*) tile									*
' 			(*) -> represents the actual tile								*
'																	*
' 			tile  0: #0000	 *	 	tile  1: #0001	 +						*
'          				 					 *						*
'																	*
'			tile  2: #0010  *+		tile  3: #0011	 +						*
'											 *+						*
'																	*
'			tile  4: #0100	 *		tile  5: #0101	 +						*
'						 +					 *						*
'																	*
' 			tile  6: #0110  *+		tile  7: #0111	 +						*
'							 +				 *+						*
'					 												*
'			tile  8: #1000	+*		tile  9: #1001	 +						*
'											+*						*
'																	*
' 			tile 10: #1010	+*+	 	tile 11: #1011	 +						*
'											+*+						*
'																	*
' 			tile 12: #1100	+*		tile 13: #1101	 +						*
'					 	+					+*						*
'											 +						*
'																	*
' 			tile 14: #1110	+*+		tile 15: #1111	 +						*
'											+*+						*
'											 +						*
'																	*
'*************************************************************************************

Function  CompileMaze(maze:set[][] Var)
	Local a:Int,c:Int

	For a:Int = 0 To maze.length-1 'tiles across
		Local d:Int
		For d:Int = 0 To maze[a].length-1 'tiles down
			If maze[a][d].a = WALLTYPE
				If a >0 
					'tile away from left edge
					If a < maze.length-1 
						' tile away from right edge
						If d >0  
							'away from top edge
							If d< maze[a].length-1	' tile out of range of right edge	
								'tile is away from bottom edge
								maze[a][d].c = (maze[a-1][d].a Shl 3)..		' *		 
											  |(maze[a][d+1].a Shl 2)..	'*+*  	  
											  |(maze[a+1][d].a Shl 1)..	' *
											  | maze[a][d-1].a
							Else	 
								'tile on bottom edge(not corner) 
								maze[a][d].c = (maze[a-1][d].a Shl 3)..		' *
											  |(maze[a+1][d].a Shl 1)..	'*+*
											  |maze[a][d-1].a			'
							EndIf
						Else
							' is on top edge(not corner)
							maze[a][d].c = (maze[a-1][d].a Shl 3)..			'
										|(maze[a][d+1].a Shl 2)..		'*+*
										|(maze[a+1][d].a Shl 1)			' *
						EndIf			
					Else
						If d > 0
							'tile is on right edge
							If d< maze[a].length-1
								'tile is on  right edge(not corner)
								maze[a][d].c = (maze[a-1][d].a Shl 3)..		' *
											|(maze[a][d+1].a Shl 2)..	'*+
											|maze[a][d-1].a			' *
							Else
								'tile is on top left corner 
								maze[a][d].c = (maze[a-1][d].a Shl 3)..		'+*
											|maze[a][d-1].a			'*
							EndIf
						Else
								'tile is on bottom left corner
								maze[a][d].c = (maze[a-1][d].a Shl 3)..		'* 
											|(maze[a][d+1].a Shl 2).. 	'+*
											
						EndIf
					EndIf
				Else
					If d>0
						'tile not on top left cornertile 
						If d< maze[a].length-1
							'tile on edge(not corner)
							maze[a][d].c = (maze[a][d+1].a Shl 2)..			' *
										|(maze[a+1][d].a Shl 1).. 		' +*
										|maze[a][d-1].a				' *
						Else
							'tile on bottom corner
							maze[a][d].c = (maze[a+1][d].a Shl 1)..			' *
										|maze[a][d-1].a				' +*
						EndIf
					Else
						'tile on top corner					
						maze[a][d].c = (maze[a][d+1].a Shl 2)..				' +*
									|(maze[a+1][d].a Shl 1)				' *
					EndIf
				EndIf
			EndIf
			If maze[a][d].c = 0
				c=(c+1) Mod 7
				If c 
					maze[a][d].b= 0
					maze[a][d].a= 0
				EndIf
				
			EndIf
		Next
	Next
	
	For a:Int = 0 To maze.length-1
		For Local d:Int = 0 To maze[a].length-1
			'If maze[a][d].a = 0 maze[a][d].c = 0
		Next
	Next
	
End Function 

'************************************************************************************
'																	*
'		centers window to center of map if map is bigger than window 			*
'		centers map to center of screen if map with and/or height is			*
'		smaller than screen 											*
'																	*
'************************************************************************************

Function Centermap(map:tmap)
	map.clearscreen = False
	'centers maze to screen horizontally if smalller than screen width
		If map.width < map.windowwidth  Then  
			map.viewwidth = map.width
			map.offsetx = (map.windowwidth-map.width) / 2
			map.clearscreen = True
		Else ' center map horizontally to view area (absolute)
			map.viewwidth = map.windowwidth 
			map.offsetx = 0
			map.x = (map.width - map.windowwidth)/2
		EndIf
		
	'centers maze to screen vertically if smaller than screen height		
		If map.height < map.windowheight Then  
			map.ViewHeight = map.height
			map.Offsety = (map.windowheight-map.height) / 2
			map.clearscreen = True 
		Else 'center map vertically to view area (absolute)
			map.ViewHeight = map.windowheight
			map.Offsety = 0
			map.y = (map.height - map.windowheight)/2
		EndIf
   		map.tilesinviewacross = Ceil(Float(map.viewwidth)/map.tilewidth)
		map.tilesinviewdown = Ceil(Float(map.viewheight) /map.tileheight)

End Function
