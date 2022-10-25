; ID: 2373
; Author: Warner
; Date: 2008-12-15 22:40:13
; Title: BMX side scroller/2d tile engine
; Description: 2d tile engine/side scroller framework

'------------------------------------------------------------------------------------------------
'									setup graphics
'------------------------------------------------------------------------------------------------

	Graphics 800, 600
	
	'every 5 milliseconds, game should be updated
	Const frame = 5
	
'------------------------------------------------------------------------------------------------
'									settings
'------------------------------------------------------------------------------------------------

	'define the size of the tile world
	Const WORLD_WIDTH	= 200
	Const WORLD_HEIGHT 	= 19
	'define the number of tiles that fit on the screen
	Const SCREEN_WIDTH 	= 25
	Const SCREEN_HEIGHT	= 18
	'define the width and height of each tile
	Const TILE_WIDTH 	= 32
	Const TILE_HEIGHT 	= 32

	'define screen boundries 
	'the tile grid world will scroll if 
	'mainx reaches these boundries
	Const SCROLL_LEFT 	= 100
	Const SCROLL_RIGHT	= 600
	
'------------------------------------------------------------------------------------------------
'									globals
'------------------------------------------------------------------------------------------------

	'create level array
	Global world:Int[,] = New Int[WORLD_WIDTH, WORLD_HEIGHT]
	
	'create tileset
	'replace 'loadtileset' with LoadAnimImage to load a tileset
	Global tileset:TImage = LoadTileSet() 'example:LoadAnimImage("tiles.bmp", 32, 32, 0, 7)
	
	'replace 'loadmainchar' with LoadImage to load main character
	Global main:TImage = LoadMainChar() 'example:LoadImage("main.bmp")
	
	'score
	Global score = 0
	
'------------------------------------------------------------------------------------------------
'									initialize
'------------------------------------------------------------------------------------------------

	'load data into world array
	'replace this routine with something of your own
	LoadWorld()
	
	now = MilliSecs()
	elaps = 0
		
'------------------------------------------------------------------------------------------------
'									main loop
'------------------------------------------------------------------------------------------------

	Repeat
	
		Cls

		'measure timer elaps
		old = now
		now = MilliSecs()
		elaps = elaps + (now - old)
		
		'sync gameplay to timer
		'every "frame" millisecs, game should be updated
		While (elaps > frame)
		
			'control main character with arrow keys
			'collision is tested before the character is moved
			If KeyDown(key_left)  Then If Not(TestCollision(main, mainx - 1, mainy)) Then mainx = mainx - 1
			If KeyDown(key_right) Then If Not(TestCollision(main, mainx + 1, mainy)) Then mainx = mainx + 1
			'If KeyDown(key_up)    Then If Not(TestCollision(main, mainx, mainy - 1)) Then mainy = mainy - 1
			'If KeyDown(key_down)  Then If Not(TestCollision(main, mainx, mainy + 1)) Then mainy = mainy + 1
			
			'if player is jumping
			If jump > 0 Then
				'if he can move up
				If Not(TestCollision(main, mainx, mainy - 1)) Then 
					'move him up, and decrease 'jump' timer
					mainy = mainy - 1
					jump = jump - 1
				'if he can't move up
				Else
					'reset 'jump' timer
					jump = 0
				EndIf
			'if player is not jumping
			Else			
				'if he can move down
				If Not(TestCollision(main, mainx, mainy + 1)) Then 
					'move him down
					mainy = mainy + 1
				'if he can't move down (=standing on something)
				Else
					'if user presses space, set jump timer to the height of 5 tiles
					If KeyDown(key_space) Then jump = TILE_HEIGHT * 5
				EndIf
			End If
		
			elaps = elaps - frame
			
		Wend
		
		'auto-scrolling
		If (mainx - scrollx > SCROLL_RIGHT) Then scrollx = mainx - SCROLL_RIGHT
		If (mainx - scrollx < SCROLL_LEFT) Then scrollx = mainx - SCROLL_LEFT
		If scrollx < 0 Then scrollx = 0
	
		'break down variable 'scrollx' into two parts: low and high
		scroll_l = scrollx Mod 32   'low:  used to pan the tile grid slightly
		scroll_h = scrollx / 32     'high: used to skip through tiles
		
		'draw tiles to screen
		For x = 0 To SCREEN_WIDTH
		For y = 0 To SCREEN_HEIGHT
			tile = readworld(x + scroll_h, y) 'apply 'high' part of scrolling here
			'zero means empty space
			If tile > 0 Then
				'apply 'low' part of scrolling here
				DrawImage tileset, x * TILE_WIDTH - scroll_l, y * TILE_HEIGHT, tile - 1
			End If
		Next
		Next
	
		'draw main character
		DrawImage main, mainx - scrollx, mainy
	
		'draw help/score	
		DrawText "move = left/right   jump = space", 0, 0
		DrawText "score:" + score, 0, 20
		
		Flip 1
	
	'esc = exit	
	Until KeyHit(key_escape)
	
	End

'------------------------------------------------------------------------------------------------
'										loadmainchar
'------------------------------------------------------------------------------------------------
'create oval as main character
'i used this to avoid including images to this source
'remove this routine and use LoadImage instead
'for an example, see above where this function is called from
Function LoadMainChar:TImage()

	image:TImage = CreateImage(48, 48)
	Cls

	SetColor 255, 255, 255	
	DrawOval 0, 0, 48, 48
	
	GrabImage image, 0, 0
	
	Return image
	
End Function	

'------------------------------------------------------------------------------------------------
'										loadworld
'------------------------------------------------------------------------------------------------
'create a random tile world
'i used this to avoid typing the entire world as data
'use your own system instead, for instance read a textfile
'this function is wrapped around 'writeworld'
'with 'writeworld' you can set an individual tile in the game world
Function LoadWorld()

	pp = 15
	For x = 0 To WORLD_WIDTH
		If Rand(0, 5) = 1 Then writeworld x, Rand(9, 12), 1
		pp = pp + Rand(-1, 1)
		If pp < 13 Then pp = 13
		If pp > 17 Then pp = 17
		For y = pp To 18
			writeworld x, y, 4 - (y = pp)
		Next
	Next

End Function

'------------------------------------------------------------------------------------------------
'										loadtileset
'------------------------------------------------------------------------------------------------
'create a tile image set
'i used this function to avoid including images to this source
'remove this routine and use LoadAnimImage instead
'for an example, see above where this function is called from
Function LoadTileSet:TImage()

	image:TImage = CreateImage(32, 32, 4)

	Cls

	SetColor 0, 255, 0
	DrawRect 0, 0, 32, 32
	SetColor 255, 0, 0
	DrawOval 0, 0, 32, 32
	GrabImage image, 0, 0, 0
	
	Cls
	
	SetColor 128, 128, 0
	DrawRect 0, 0, 32, 32
	SetColor 255, 160, 160
	For i = -1 To 3
	For j = 0 To 3
	DrawRect i * 8 + (j Mod 2 * 4), j * 8, 6, 4
	Next
	Next
	GrabImage image, 0, 0, 1
	
	Cls
	
	For i = 0 To 16
	For j = 0 To 16
		SetColor 64, Rand(160, 255), 64
		DrawRect i * 2, j * 2, 2, 2
	Next
	Next
	GrabImage image, 0, 0, 2

	Cls
	
	For i = 0 To 16
	For j = 0 To 16
		c = Rand(40, 120)
		SetColor c, c, 32
		DrawRect i * 2, j * 2, 2, 2
	Next
	Next
	GrabImage image, 0, 0, 3
		
	SetColor 255, 255, 255
	
	Return image

End Function

'------------------------------------------------------------------------------------------------
'										readworld
'------------------------------------------------------------------------------------------------
'read a value from tile grid
'this function is protected: when reading
'outside the array boundries, a zero is returned
Function readworld(x, y)

	If x < 0 Then Return 0
	If y < 0 Then Return 0
	If x >= WORLD_WIDTH Then Return 0
	If y >= WORLD_HEIGHT Then Return 0
	Return world[x, y]
	
End Function

'------------------------------------------------------------------------------------------------
'										writeworld
'------------------------------------------------------------------------------------------------
'write a value to tile grid
'this function is protected: when writing
'outside the array boundries, the function returns
Function writeworld(x, y, tile)

	If x < 0 Then Return
	If y < 0 Then Return
	If x >= WORLD_WIDTH Then Return
	If y >= WORLD_HEIGHT Then Return
	world[x, y] = tile

End Function	

'------------------------------------------------------------------------------------------------
'										testcollision
'------------------------------------------------------------------------------------------------
'test if a certain image would collide with tileworld on certain position
'char = who is tested
'newx, newy = location that you want to test
Function TestCollision(char:TImage, newx, newy)

	cx = newx / TILE_WIDTH
	cy = newy / TILE_HEIGHT
	cw = ((char.width - 1) / TILE_WIDTH) + 1
	ch = ((char.height - 1) / TILE_HEIGHT) + 1

	For i = cx To cx+cw
	For j = cy To cy+ch	
	
		tile = readworld(i, j)
		If tile > 0 Then
			If ImagesCollide(char, newx, newy, 0, tileset, i * TILE_WIDTH, j * TILE_HEIGHT, tile - 1) Then				
				Return TileCollisionEvent(char, i, j, tile)
			End If
		End If
				
	Next
	Next
	
	Return False

End Function

'------------------------------------------------------------------------------------------------
'										tilecollisionevent
'------------------------------------------------------------------------------------------------
'this function will be called if a collision is found in TestCollision
'char = who is colliding
'tilex,tiley = position of tile
'tile = index number of tile
'if you return TRUE, the tile will be noticed by TestCollision
'if you return FALSE, the tile will be ignored by TestCollision
Function TileCollisionEvent(char:TImage, tilex, tiley, tile)

	If (char = main) Then
		If tile = 1 Then 
			'you've collided with tile number 1. that is the first tile in the tileset
			writeworld tilex, tiley, 0 'remove this tile
			score = score + 1 'add score
			Return False 'act as if no tile was there
		End If
	End If
	
	Return True 
		
End Function
