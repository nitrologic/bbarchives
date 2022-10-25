; ID: 1361
; Author: KamaShin
; Date: 2005-04-27 13:42:28
; Title: How to use tiles
; Description: Map making with tiles, for beginner

rem
The method I'm going to describe is ONE method to do things, not THE method... it works and is pretty simple:

First of all, you must have your tiles stored in one picture (a .bmp or .png) that we will load using the LoadAnimeImage.
Thus, each tile must have the same dimensions (I'll assume for the tutorial that our tiles are 32*32).
end rem

Global Width:Int = 800, Height:Int = 600 'This is the resolution of our "game"
Global MapWidth:Int, MapHeight:Int 'These are the width and height of our map (unknown for now since the map hasn't been created yet)
Global TileSize:Int = 32 'I'm assuming that the tiles are squares
Global MyMap:Int[60,30] 'A map is a matrix of integer: here, 60 tiles width and 30 tiles height
MapWidth = 60*TileSize
MapHeight = 30*TileSize
Global OffsetX:Int = 0, OffsetY:Int = 0 'Since there is no camera we can move around, we'll simulate one by "moving" the tiles themselves
										'(we won't really move them, just calculate very simply where they must be drawn)
Global TheTiles:TImage 'the variable that will store all our tiles in memory, ready to be drawn

Graphics Width, Height, 32, 60 'see the doc if you don't know what these commands do
SetBlend ALPHABLEND 'ALPHABLEND is usefull only if your tiles/objects have transparency effects
HideMouse()
SetColor 255,255,255

TheTiles = LoadAnimImage("AnyPathYouWant/YourTiles.png", TileSize, TileSize, 0, NumberOfTilesInYourPicture, FILTEREDIMAGE|MASKEDIMAGE) 'see the doc to know how this works
rem
TheTiles will store each tile as a frame... However, we won't be using TheTiles as an animation but as an array (vector) storing all the tiles.
Thus, the matrix MyMap will only store integers, each integer refering to a tile in TheTiles
end rem

'If you want to do things right, you should write a map editor that would let you fill your map with tiles... since this is not the purpose
'here, I'll use a small loop that will randomly fill MyMap

Local i:Int, j:Int
For i=0 To 29 'using mathematics convention, i represent the rows and j the columns which is why i varies from 0 to 29 (30 rows)
	For j=0 To 59 'and j from 0 to 59 (which makes 60 columns)
		MyMap[j,i] = Rand(0, NumberOfTilesInYourPicture-1) 'note that in MyMap[j,i] j is in first position and i comes second
														   'see the doc to know how Rand works and notice that the max value is
														   'the number of tiles in MyTile minus 1 (because MyTile works like a 0 based array)
	Next
Next
'there, our map has been filled with tiles (randomly so it will probably be ugly but that's not our concern here)

'next,we will be writing the loop that draws the map and allows the user to move around the map
Local x:Int, y:Int, TileToDraw:Int
rem
x and y will represent the x and y position of the tile that is about to be drawn
TileToDraw is the index of the tile in TheTile to be drawn
end rem
Repeat

	Cls
	
	For i=0 To 29
		For j=0 To 59
			x = j*TileSize-OffsetX 'simple mathematics here: j*TileSize is the absolute x coordinate of our tile... We then substract
								   'OffsetX to simulate the fact that the "camera" may have moved along the x axis
								   'OffsetX represent the distance between the left border of the map and the left border of our fake camera
			y = i*TileSize-OffsetY 'same as above
			TileToDraw = MyMap[j,i] 'remember? MyMap stores the index of the tiles in TheTiles... this variable TileToDraw could be bypassed
									'by calling directly MyMap[j,i] in the next line, but I used TileToDraw for clarity
			DrawImage TheTiles, x, y, TileToDraw 'see the doc to know how this function works
		Next
	Next
	
	Flip
	'OK... now the tiles are drawn... let's allow the user to move around the map... we'll be doing this using the mouse
	'moving the mouse to the extrem left of the screen should move our fake camera to the left... and so on with the 3 other borders
	TestMouseMove()
	FlushMem()

Until KeyHit (KEY_ESCAPE)

Function TestMouseMove()
	If MouseX() <= 0 Then 'means that the mouse is "on" the left border of the screen
		
		OffsetX :- 1 'remember that OffsetX has been defined as the distance between the left border of the map and the left border of the "camer"
					 'thus, moving the "camera" to the left means reducing the distance of thos 2 borders
		If OffsetX < 0 Then 'we've moved the camera out of the map, too far on the left
			OffsetX = 0
		End If
		
	ElseIf MouseX() >= Width Then 'means that the mouse is on the right border
		
		OffsetX :+ 1 'there, we're increasing the distance between our borders because the "camera" is moving right
		If OffsetX > MaxWidth-Width 'we've moved the screen to far on the right... notice that it's not Offset > MaxWidth
									'but OffsetX > MaxWidth-Width, that's because we're testing OffsetX with the RIGHT border of the screen,
									' not the left border, and OffsetX represent the left border
			OffsetX = MaxWidth-Width
		End If
	
	End If
	
	If MouseY() <= 0 Then 'the mouse is on the upper border of the screen, we must move the "camera" up
		
		OffsetY :- 1
		If OffsetY < 0 Then
			OffsetY = 0
		End If
		
	ElseIf MouseY() >= Height Then 'the opposite now
	
		OffsetY :+ 1
		If OffsetY > MaxHeight-Height Then
			OffsetY = MaxHeight-Height
		End If
	
	End If
End Function

rem
Well, that's it for now... you do have a Map, filled with tiles, drawing correctly, and you can "move" around the map...
There is still a lot of improvements you can do here though:
first is the fact that EVERY tiles are drawn... for a 60*30 maps this is beginning to be a bit hard on the computer... with larger map
it will become veeeeeery slow, so the first improvement would be to draw only the tiles that are visible on screen (or partially visible)
another improvement would be to create a type TTile that would store the frame of the tile and the x/y coordinates of the tile, and thus,
you would only calculate the x and y coordinate of the tile once, when it is created, and then draw it using OffsetX/OffsetY to still simulate
the fake "camera"... MyMap would then become an array of TTile rather than of Integers...
You could even then create a type TMap that would store several matrix of tiles allowing what is called layers, and another matrix called
for example Mask, that would work for collisions (if MyMap.Mask[j,i]=0 then player can move on tile j,i else not)...
but first of all, you'll want to create a tile editor :)
end rem
