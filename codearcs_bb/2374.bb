; ID: 2374
; Author: Warner
; Date: 2008-12-16 02:59:12
; Title: BB side scroller/2d tile engine
; Description: 2d tile engine/side scroller framework

;------------------------------------------------------------------------------------------------
;									setup Graphics
;------------------------------------------------------------------------------------------------

	Graphics 800, 600, 0, 2
	SetBuffer BackBuffer()
	
	;every 5 milliseconds, game should be updated
	Const frame = 5
	
;------------------------------------------------------------------------------------------------
;									settings
;------------------------------------------------------------------------------------------------

	;define the size of the tile world
	Const WORLD_WIDTH	= 200
	Const WORLD_HEIGHT 	= 19
	;define the number of tiles that fit on the screen
	Const SCREEN_WIDTH 	= 25
	Const SCREEN_HEIGHT	= 18
	;define the width And height of Each tile
	Const TILE_WIDTH 	= 32
	Const TILE_HEIGHT 	= 32

	;define screen boundries 
	;the tile grid world will scroll If 
	;mainx reaches these boundries
	Const SCROLL_LEFT 	= 100
	Const SCROLL_RIGHT	= 600
	
;------------------------------------------------------------------------------------------------
;									globals
;------------------------------------------------------------------------------------------------

	;create level array
	Dim world(WORLD_WIDTH, WORLD_HEIGHT)
	
	;create tileset
	;Replace ;loadtileset; with LoadAnimImage To load a tileset
	Global tileset = LoadTileSet() ;example:LoadAnimImage("tiles.bmp", 32, 32, 0, 7)
	
	;Replace ;loadmainchar; with LoadImage To load main character
	Global main = LoadMainChar() ;example:LoadImage("main.bmp")
	
	;score
	Global score = 0
	
;------------------------------------------------------------------------------------------------
;									initialize
;------------------------------------------------------------------------------------------------

	;load Data into world array
	;Replace this routine with something of your own
	LoadWorld()
	
	now = MilliSecs()
	elaps = 0
		
;------------------------------------------------------------------------------------------------
;									main loop
;------------------------------------------------------------------------------------------------

	Repeat
	
		Cls

		;measure timer elaps
		old = now
		now = MilliSecs()
		elaps = elaps + (now - old)
		
		;sync gameplay To timer
		;every "frame" MilliSecs, game should be updated
		While (elaps > frame)
		
			;control main character with arrow keys
			;collision is tested Before the character is moved
			If KeyDown(203)  Then If Not(TestCollision(main, mainx - 1, mainy)) Then mainx = mainx - 1
			If KeyDown(205)  Then If Not(TestCollision(main, mainx + 1, mainy)) Then mainx = mainx + 1
			;If KeyDown(200) Then If Not(TestCollision(main, mainx, mainy - 1)) Then mainy = mainy - 1
			;If KeyDown(208) Then If Not(TestCollision(main, mainx, mainy + 1)) Then mainy = mainy + 1
			
			;If player is jumping
			If jump > 0 Then
				;If he can move up
				If Not(TestCollision(main, mainx, mainy - 1)) Then 
					;move him up, And decrease ;jump; timer
					mainy = mainy - 1
					jump = jump - 1
				;If he can;t move up
				Else
					;reset ;jump; timer
					jump = 0
				EndIf
			;If player is Not jumping
			Else			
				;If he can move down
				If Not(TestCollision(main, mainx, mainy + 1)) Then 
					;move him down
					mainy = mainy + 1
				;If he can;t move down (=standing on something)
				Else
					;If user presses space, set jump timer To the height of 5 tiles
					If KeyDown(57) Then jump = TILE_HEIGHT * 5
				EndIf
			End If
		
			elaps = elaps - frame
			
		Wend
		
		;auto-scrolling
		If (mainx - scrollx > SCROLL_RIGHT) Then scrollx = mainx - SCROLL_RIGHT
		If (mainx - scrollx < SCROLL_LEFT) Then scrollx = mainx - SCROLL_LEFT
		If scrollx < 0 Then scrollx = 0
	
		;break down variable ;scrollx; into two parts: low And high
		scroll_l = scrollx mod TILE_WIDTH   ;low:  used To pan the tile grid slightly
		scroll_h = scrollx / TILE_WIDTH     ;high: used To skip through tiles
		
		;draw tiles To screen
		For x = 0 To SCREEN_WIDTH
		For y = 0 To SCREEN_HEIGHT
			tile = readworld(x + scroll_h, y) ;apply ;high; part of scrolling here
			;zero means empty space
			If tile > 0 Then
				;apply ;low; part of scrolling here
				DrawImage tileset, x * TILE_WIDTH - scroll_l, y * TILE_HEIGHT, tile - 1
			End If
		Next
		Next
	
		;draw main character
		DrawImage main, mainx - scrollx, mainy
	
		;draw help/score	
		Text 0, 0, "move = left/right   jump = space"
		Text 0, 20, "score:" + score
		
		Flip 1
	
	;esc = Exit	
	Until KeyHit(1)
	
	End

;------------------------------------------------------------------------------------------------
;										loadmainchar
;------------------------------------------------------------------------------------------------
;create Oval as main character
;i used this To avoid including images To this source
;remove this routine And use LoadImage instead
;For an example, see above where this Function is called from
Function LoadMainChar()

	image = CreateImage(48, 48)
	Cls

	Color 255, 255, 255	
	Oval 0, 0, 48, 48
	
	GrabImage image, 0, 0
	
	Return image
	
End Function	

;------------------------------------------------------------------------------------------------
;										loadworld
;------------------------------------------------------------------------------------------------
;create a random tile world
;i used this To avoid typing the entire world as Data
;use your own system instead, For instance Read a textfile
;this Function is wrapped around ;writeworld;
;with ;writeworld; you can set an individual tile in the game world
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

;------------------------------------------------------------------------------------------------
;										loadtileset
;------------------------------------------------------------------------------------------------
;create a tile image set
;i used this Function To avoid including images To this source
;remove this routine And use LoadAnimImage instead
;For an example, see above where this Function is called from
Function LoadTileSet()

	image = CreateImage(32, 32, 4)

	Cls

	Color 0, 255, 0
	Rect 0, 0, 32, 32
	Color 255, 0, 0
	Oval 0, 0, 32, 32
	GrabImage image, 0, 0, 0
	
	Cls
	
	Color 128, 128, 0
	Rect 0, 0, 32, 32
	Color 255, 160, 160
	For i = -1 To 3
	For j = 0 To 3
		Rect i * 8 + (j Mod 2 * 4), j * 8, 6, 4
	Next
	Next
	GrabImage image, 0, 0, 1
	
	Cls
	
	For i = 0 To 16
	For j = 0 To 16
		Color 64, Rand(160, 255), 64
		Rect i * 2, j * 2, 2, 2
	Next
	Next
	GrabImage image, 0, 0, 2

	Cls
	
	For i = 0 To 16
	For j = 0 To 16
		c = Rand(40, 120)
		Color c, c, 32
		Rect i * 2, j * 2, 2, 2
	Next
	Next
	GrabImage image, 0, 0, 3
		
	Color 255, 255, 255
	
	Return image

End Function

;------------------------------------------------------------------------------------------------
;										readworld
;------------------------------------------------------------------------------------------------
;Read a value from tile grid
;this Function is protected: when reading
;outside the array boundries, a zero is returned
Function readworld(x, y)

	If x < 0 Then Return 0
	If y < 0 Then Return 0
	If x >= WORLD_WIDTH Then Return 0
	If y >= WORLD_HEIGHT Then Return 0
	Return world(x, y)
	
End Function

;------------------------------------------------------------------------------------------------
;										writeworld
;------------------------------------------------------------------------------------------------
;Write a value To tile grid
;this Function is protected: when writing
;outside the array boundries, the Function returns
Function writeworld(x, y, tile)

	If x < 0 Then Return
	If y < 0 Then Return
	If x >= WORLD_WIDTH Then Return
	If y >= WORLD_HEIGHT Then Return
	world(x, y) = tile

End Function	

;------------------------------------------------------------------------------------------------
;										testcollision
;------------------------------------------------------------------------------------------------
;test If a certain image would collide with tileworld on certain position
;char = who is tested
;newx, newy = location that you want To test
Function TestCollision(char, newx, newy)

	cx = newx / TILE_WIDTH
	cy = newy / TILE_HEIGHT
	cw = ((ImageWidth(char) - 1) / TILE_WIDTH) + 1
	ch = ((ImageHeight(char) - 1) / TILE_HEIGHT) + 1

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

;------------------------------------------------------------------------------------------------
;										tilecollisionevent
;------------------------------------------------------------------------------------------------
;this Function will be called If a collision is found in TestCollision
;char = who is colliding
;tilex,tiley = position of tile
;tile = index number of tile
;If you Return True, the tile will be noticed by TestCollision
;If you Return False, the tile will be ignored by TestCollision
Function TileCollisionEvent(char, tilex, tiley, tile)

	If (char = main) Then
		If tile = 1 Then 
			;you;ve collided with tile number 1. that is the First tile in the tileset
			writeworld tilex, tiley, 0 ;remove this tile
			score = score + 1 ;add score
			Return False ;act as If no tile was there
		End If
	End If
	
	Return True 
		
End Function
