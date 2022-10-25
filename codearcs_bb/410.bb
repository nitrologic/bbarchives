; ID: 410
; Author: sswift
; Date: 2002-08-27 17:26:20
; Title: Swift Terrain System
; Description: The Swift Terrain System - A fast, detailed, tiled mesh terrain with level of detail

Here is a sample of the start of the source code for the terrain system so you can see how much it's commented, and how many rendering options it has.


; -------------------------------------------------------------------------------------------------------------------
; Swift Terrain System:  Copyright 2002 - Shawn C. Swift
; -------------------------------------------------------------------------------------------------------------------

Const TERRAIN_TILES_X = 16										; These values adjust the number of tiles across and down on the terrain.   If you try to create a terrain with too many tiles, Blitz will crash.  The number of tiles you can create depends on the max subdivision per tile.
Const TERRAIN_TILES_Z = 16                      	            ; If you want a larger terrain without adding more tiles, you can increase TERRAIN_TILE_SCALE_XZ#.  That will increase the size of the individual tiles.

Global TERRAIN_TILE_SCALE_XZ# = 64.0 							; This is how wide/long each terrain tile is.  Increasing this value will stretch the textures out more, but give you a higher view distance.
																; The default settings for this is 128, and that combined with the other default settings gives you a view distance of 1 kilometer.

Global TERRAIN_TILE_SCALE_Y#  = 64.0							; This is the height scaling for the heightmaps.  The larger you make this value, the taller your mountains will be,
																; but because of the limitations of having only 256 shades of grey in an 8-bit image, you can only have 256 diffrent heights.
																; So the larger you make this value, the bigger the smallest change in height you can represent with a heightmap.

Const MAX_TERRAIN_SUBDIVISION	= 32							; This is the highest detail the terrain can be.  Must be a power of 2.
Const MIN_TERRAIN_SUBDIVISION	= 2		 						; This is the lowest detail the terrain can be.  Also must be a power of 2.
Const TERRAIN_DETAIL_LEVELS		= 5	 							; This is the number of detail levels the terrain has between MIN_TERRAIN_SUBDIVISION and MAX_TERRAIN_SUBDIVISION.  Between 2 and 32 for example, there are 5:  2, 4, 8, 16, and 32.  You MUST update this if you change MIN_TERRAIN_SUBDIVISION or MAX_TERRAIN_SUBDIVISION!

Const SKIP_SMOOTHING = True 									; Setting this to true will make heightmap calculation go faster, but the terrains will have more jagged edges.


Global TERRAIN_NEAR_DETAIL_CLAMP = 0							; This is the highest detail level shown up close.  This variable allows you to you clamp the near detail without changing the highest detail which can be used in your game.
																; For example, if you use a max subdivision size of 32, and you want your game to run on slower PC's with a lower detail level, you can set this to 1 in your game at
																; any time, and the detail level shown up close would drop down to a subdivision of 16, and the game would run significantly faster.  


Global TERRAIN_NEAR_DETAIL_DIST# = TERRAIN_TILE_SCALE_XZ#*4.0	; This is the distance at which the second level of detail first kicks in.  It's a bit tricky to find a good value for this, but for most tile sizes,
																; TERRAIN_TILE_SCALE_XZ#*3.0 and TERRAIN_TILE_SCALE_XZ#*4.0 seem to work well.  Values smaller than these tend to show too much popping and reveal seams between the tiles.
																; Values larger than these tend to increase the polygon count far to much.  You should probably leave this at TERRAIN_TILE_SCALE_XZ#*4.0 if you use the other default terrain settings.

Global MAX_TILES_IN_VIEW = 8									; This is roughly the maximum number of tiles which will be drawn into the distance from the camera.  This value is sort of like a far clipping plane, but more useful,
																; because more important than the distance which you can see, is the number of tiles which are in view.  You can change the scale of the terrain and not change the number
																; of tiles in view.  But if you change the number of levels of detail you can have, or increase the distance at which the first level of detail goes out to, you could
																; easily kill the framerate.  This gives you control over the number of tiles which are drawn, but you should probably never need to change this from it's default value, 
																; unless you want a smaller view distance and a slightly higher speed.


Global TERRAIN_SCALE# = Float(TERRAIN_TILES_X) * TERRAIN_TILE_SCALE_XZ#		; This is the total width of the entire terrain.


Type TerrainTile

	Field Current_Detail_Level

	Field TileHeightmap.Heightmap		; The heightmap which this terrain tile uses.
										; More than one tile may use the same heightmap.

	Field Mesh[TERRAIN_DETAIL_LEVELS]	; This is a list of pointers to the entities which represent each detail level of this
										; terrain tile.  The detail levels are 64, 32, 16, 8, 4, and 2.
										
End Type 


Type Heightmap

	; If you adjust the number of detail levels, you must adjust this value.
	; You have to add one to each detail level, then square the value, and add all the detail levels together.
	; Ie, If you have three detail levels, 16, 8, and 4, then you would add (16+1)^2, (8+1)^2, and (4+1)^2.
	Field Height#[33*33 + 17*17 + 9*9 + 5*5 + 3*3]
				
End Type	


; These arrays hold the data for each tile of the terrain.
Dim Terrain_Tile.TerrainTile(TERRAIN_TILES_X, TERRAIN_TILES_Z)
Dim Terrain_Tile_Brush(TERRAIN_TILES_X, TERRAIN_TILES_Z)			; This is a list of which brushes go with which tiles.  These are not brush handles, they're indexes into the Brush_List() array.
Dim Terrain_Tile_Rotate(TERRAIN_TILES_X, TERRAIN_TILES_Z)			; 0 = Normal  :  1 = 90 :  2 = 180  :  3 = 270


; This is the number of brushes which you are using on the terrain. 
; You should set this to the exact number of brushes you have set up.
Const TOTAL_TERRAIN_BRUSHES = 7


; This is a list of all the brushes used to paint the terrain.
; You should fill this array with handles to all the brushes you want to use after you create them.
Dim Terrain_Brush_List(TOTAL_TERRAIN_BRUSHES)
