; ID: 2839
; Author: Matthew Smith
; Date: 2011-04-26 07:12:52
; Title: 2d Map Builder
; Description: Build 2d map and tiles from an image

;------------------------------------------------------------------------------------------------------------------------------------------
; MapBuilder
; v2.0 
; Matthew Smith 2001, 2011, 2013
;------------------------------------------------------------------------------------------------------------------------------------------

AppTitle "Map Builder v2.0 - Matthew Smith"
Graphics 640, 480, 16, 2

Global MapName$
MapName$=Input$("Enter filename of level gfx: ")
If (MapName = "") Then End

Dim TileMap(1, 1)					;Map Information
Global MapSizeX
Global MapSizeY

;Adjust these items depending on game
Global ScaleTiles = True
Global TileWidth = 32				;Tile Sizes
Global TileHeight = 32
Global TilePadding = 1				;Padding added to tile
Global MapPadding = False			;Padding added to map (1 cell border)

Global gfxMap
Global gfxTileStore
Global gfxTileCompareStore

Dim ColorTiles(1, 1)
Const CompareAtOnce = 250
Global TilesMax = 0					;Number of Tiles Created 

;Read Ini File
ReadIniFile()

;Build
SetGfxMapInfo()
CreateBlankTile()
BuildMap()

;Finalise
FreeImage gfxTileStore
FreeImage gfxTileCompareStore
FreeImage gfxMap
EndGraphics
End
				
Function SetGfxMapInfo()
	;Load image
	gfxMap = LoadImage(MapName + ".png")
		
	;Get size of map (tiles)
	MapSizeX = Int(ImageWidth(gfxMap) / TileWidth)
	MapSizeY = Int(ImageHeight(gfxMap) / TileHeight)
	
	;Set map size
	ClearMap(MapSizeX, MapSizeY)	
	
	;Set tile image store
	FreeImage gfxTileStore
	gfxTileStore = CreateImage(TileWidth, TileHeight, 512)
		
	;Set comparison tile image store
	FreeImage gfxTileCompareStore
	gfxTileCompareStore = CreateImage(TileWidth * (CompareAtOnce + 1), TileHeight)
	
	;Set counter	
	TilesMax = 0

End Function

Function ClearMap(sizeX, sizeY)
	Local x, y
	
	;Resize
	Dim TileMap(sizeX, sizeY)
	
	;Process
	For x = 0 To sizeX
		For y = 0 To sizeY
			TileMap(x, y) = -1
		Next
	Next
	
End Function

Function CreateBlankTile()
	;Prepare
	SetBuffer(BackBuffer())
	ClsColor(0, 0, 0)
	Cls
		
	;Copy blank tile and set value in map
	DrawBlockRect(gfxMap, 0, 0, TileWidth, TileHeight, TileWidth, TileHeight)
	TileMap(0, 0) = 0
	
	;Inc counter
	TilesMax = TilesMax + 1

End Function

Function CreateTile(BX, BY)
	;Prepare 
	SetBuffer(ImageBuffer(gfxTileStore, TilesMax))
	
	;Copy tile and set value in map
	DrawBlockRect(gfxMap,  0, 0, (BX * TileWidth), (BY * TileHeight), TileWidth, TileHeight)
	TileMap(BX, BY) = TilesMax
	
	;Inc counter
	TilesMax = TilesMax + 1
		
	;Finalise
	SetBuffer(BackBuffer())
	
End Function

Function PrepareMapComparison(BX, BY, TC)
	Local counter
	Local compare
	Local index
	
	;Set buffer
	SetBuffer(ImageBuffer(gfxTileCompareStore))
	
	;Clear comparison image
	Color 0, 0, 0
	Cls

	;Get map tile
	DrawBlockRect(gfxMap, 0, 0, (BX * TileWidth), (BY * TileHeight), TileWidth, TileHeight)
		
	;Get total tiles to compare
	compare = TC + CompareAtOnce
	If (TC + CompareAtOnce > TilesMax) Then compare = TilesMax
	
	;Get stored tile(s) to compare
	counter = 1
	For index = TC To compare - 1
		DrawBlock(gfxTileStore, TileWidth * counter, 0, index)
		counter = counter + 1
		
	Next
	
	;Copy into Backbuffer
	SetBuffer(BackBuffer())
	Cls
		
	;Draw	
	DrawBlock(gfxTileCompareStore, 0, 16)
	
End Function

Function DisplayMap()
	Local x, y
	
	;Process
	For y = 0 To MapSizeY - 1
		For x = 0 To MapSizeX - 1
			If (TileMap(x, y) <> -1) Then
				DrawBlock(gfxTileStore, x * TileWidth, 100 + (y * TileHeight), TileMap(x, y))
				
			End If
			
		Next
		
	Next
	
	;Draw
	DrawBlock gfxMap, 260, 100
	
End Function

Function BuildMap()
	Local BX, BY, TC, TB, TileCount
	Local counter# = 0
	Local matchIndex = -1
	Local totalTiles# = (MapSizeX * MapSizeY)
	
	; Work Thru Loaded Map
	For BY = 0 To MapSizeY - 1
		For BX = 0 To MapSizeX - 1
			;Trap ESC key
			If KeyDown(1) Then Return
			
			;Prepare
			TC = 0
			
			;Process
			For TC = 0 To TilesMax - 1 Step CompareAtOnce
				;Prepare comparision
				PrepareMapComparison(BX, BY, TC)
				
				;Update display summary
				Color 255, 255, 255
				Text 0, 0, "X" + Right$("000" + (BX + 1), 3) + " Y" + Right$("000" + (BY + 1), 3) + " (" + MapSizeX + "x" + MapSizeY + ") "  + Right$("   " + Int((counter / totalTiles) * 100), 3) + "%"
				Text 320, 0, "Found:" + Right$("0000" + TilesMax, 4)
				;Text 296, 0, "TF" + Right$("00000" + TilesMax, 5) + "/" + Right$("00000" + Int(counter), 5)
				Flip
				
				;Validate if exists				
				matchIndex = CompareTiles(TC)
				If (matchIndex <> -1) Then
					;Store result
					TileMap(BX, BY) = matchIndex
					Exit
								
				End If

			Next
			
			;No match found?, if so create new tile
			If (matchIndex = -1) Then CreateTile(BX, BY)
			
			;Increment counter
			counter = counter + 1
			
		Next
	Next
	
	;Save Map and Tiles
	SaveTiles()
	SaveTileMapData()
	SaveTileMapMonkey()
	
	;Set finalised message
	Color 255, 255, 255
	Text(0, 120, "Process complete! Press any key to exit...")
	Flip
	
	;Wait
	WaitKey
	
End Function

;Returns the index of the matching tile if found
Function CompareTiles(TC)
	Local x, y, index
	Local compare
	Local counter
	Local ct
	Dim ColorTiles(3, CompareAtOnce)
	
	;Prepare
	SetBuffer(ImageBuffer(gfxTileCompareStore))
	
	;Set number of tiles to compare
	compare = TC + CompareAtOnce
	If (TC + CompareAtOnce > TilesMax) Then compare = TilesMax
	
	;Search for existing tile match
	For y = 0 To TileHeight - 1
		For x = 0 To TileWidth - 1
			;Get image
			GetColor x, y
			ColorTiles(1, 0) = ColorRed() 
			ColorTiles(2, 0) = ColorGreen()
			ColorTiles(3, 0) = ColorBlue()
			
			;Process
			counter = 1
			For ct = TC To compare - 1
				If (ColorTiles(0, counter) <> -1) Then
					;Get tile store image
					GetColor x + (TileWidth * counter), y
					ColorTiles(1, counter) = ColorRed()
					ColorTiles(2, counter) = ColorGreen()
					ColorTiles(3, counter) = ColorBlue()
					
					;Validate
					If (ColorTiles(1, 0) <> ColorTiles(1, counter) Or ColorTiles(2, 0) <> ColorTiles(2, counter) Or ColorTiles(3, 0) <> ColorTiles(3, counter)) Then
						ColorTiles(0, counter) = -1
						
					End If
					
				End If
				
				;Increment
				counter = counter + 1
							
			Next
		Next
	Next
	
	;Validate
	counter = 1
	For index = TC To compare - 1
		;Match?, if so return tile index
		If (ColorTiles(0, counter) <> -1) Then Return index
		
		;Increment
		counter = counter + 1 
		
	Next
	
	;Not found
	Return -1
			
End Function

Function SaveTiles()
	Local BT
	Local TX, TY
	Local BX, BY 
	Local TC
	Local gfxTileFinalStore
	
	Local finalImageWidth = 320
	Local finalTileWidth = TileWidth
	Local finalTileHeight = TileHeight
	
	;Scale
	If (ScaleTiles) Then
		;finalImageWidth = finalImageWidth * 2
		finalTileWidth = finalTileWidth * 2
		finalTileHeight = finalTileHeight * 2
		
	End If
	
	;Create store
	FreeImage gfxTileFinalStore
	TX = finalImageWidth / TileWidth
	TY = Int((TilesMax - 1) / TX) + 1
	gfxTileFinalStore = CreateImage(TX * (finalTileWidth + (TilePadding * 2)), TY * (finalTileHeight + (TilePadding * 2)))
	
	;Set buffer	
	SetBuffer(ImageBuffer(gfxTileFinalStore))
	Color 0, 0, 0
	Cls
	
	;Output tiles to store
	TC = 0
	For BY = 0 To TY - 1
		For BX = 0 To TX - 1
			If TC <= TilesMax - 1
				;Grab image
				SetBuffer(ImageBuffer(gfxTileStore,TC))
				Local gfxTile = CreateImage(TileWidth, TileHeight)
				GrabImage(gfxTile,0,0)
				
				;Scale?
				If (ScaleTiles) Then gfxTile = ScaleImageFast(gfxTile, 2.0, 2.0)
				
				;Store
				SetBuffer(ImageBuffer(gfxTileFinalStore))
				DrawBlock(gfxTile, BX * (finalTileWidth + (TilePadding * 2)) + TilePadding, (BY * (finalTileHeight + (TilePadding * 2)) + TilePadding))
				
			End If
			TC = TC + 1
		Next
	Next
	
	;Save tiles to file
	CreateFolder("map")
	SaveImage(gfxTileFinalStore, "map\" + MapName + ".bmp")
	
	;Finalise
	SetBuffer(BackBuffer())
	FreeImage gfxTileFinalStore
	
End Function

Function SaveTileMapData()
	Local fileName$ = "map\" + MapName + ".tiles.map"
	Local x, y
	
	;Create file
	CreateFolder("map")
	Local file = WriteFile(fileName)

	;Pad map?
	Local msX = MapSizeX
	Local msY = MapSizeY
	If (MapPadding) Then msX = msX + 2
	If (MapPadding) Then msY = msY + 2
	
	;Scale tiles sizes?
	Local tsX = TileWidth
	Local tsY = TileHeight
	If (ScaleTiles) Then tsX = (tsX * 2)
	If (ScaleTiles) Then tsY = (tsY * 2)
		
	;Output map summary
	WriteString(file, MapName + ".png")			;Tile filename
	WriteInt(file, TilesMax)					;Total tiles
	WriteInt(file, msX) 						;Map Size
	WriteInt(file, msY) 
	WriteInt(file, tsX) 						;Tile Size
	WriteInt(file, tsY)

	;Output padding - top row?
	If (MapPadding) Then WritePaddingRowData(file)
	
	;Output map
	For y = 0 To MapSizeY - 1
		;Padding
		If (MapPadding) Then WriteInt(file, 0)
		
		For x = 0 To MapSizeX - 1
			WriteInt(file, TileMap(x, y))
		Next
		
		;Padding
		If (MapPadding) Then WriteInt(file, 0)
	Next

	;Output padding - bottom row?
	If (MapPadding) Then WritePaddingRowData(file)
	
	;Close
	CloseFile(file)
	
End Function

Function SaveTileMapMonkey()
	Local fileName$ = "map\" + MapName + ".tiles.monkey.txt"
	Local x, y
	
	;Create file
	CreateFolder("map")
	Local file = WriteFile(fileName)
		
	;Pad map?
	Local msX = MapSizeX
	Local msY = MapSizeY
	If (MapPadding) Then msX = msX + 2
	If (MapPadding) Then msY = msY + 2
	
	;Scale tiles sizes?
	Local tsX = TileWidth
	Local tsY = TileHeight
	If (ScaleTiles) Then tsX = (tsX * 2)
	If (ScaleTiles) Then tsY = (tsY * 2)
	
	;Output map summary
	WriteLine(file, "'#Region " + Chr(34) + " MapData " + Chr(34))
	WriteLine(file, "	'Map summary")
	WriteLine(file, "	'Map dump file: " + MapName + ".png")
	WriteLine(file, "	Field TilesMax:Int=" + TilesMax)
	WriteLine(file, "	Field MapSizeX:Int=" + msX)
	WriteLine(file, "	Field MapSizeY:Int=" + msY)
	WriteLine(file, "	Field TileWidth:Int=" + tsX)
	WriteLine(file, "	Field TileHeight:Int=" + tsY) 
	WriteLine(file, "")
	WriteLine(file, "	'Map Data")
	WriteLine(file, "	Field TileMap:=[")
	
	;Output padding - top row?
	If (MapPadding) Then WritePaddingRowMonkey(file)
		
	;Output Map
	For y = 0 To MapSizeY - 1
		Local mapRow$ = ""
		
		;Padding?
		If (MapPadding) Then mapRow = "0"
		
		;Build row data
		For x = 0 To MapSizeX - 1
			If (Len(mapRow) > 0) Then mapRow = mapRow + ","
			mapRow = mapRow + Str(TileMap(x, y))
		Next 
		
		;Padding?
		If (MapPadding) Then mapRow = mapRow + ",0"
		
		;Append to end of row?
		Select MapPadding
			Case True
				mapRow = mapRow + ","
			Case False
				If (y < MapSizeY - 1) Then mapRow = mapRow + ","
				If (y = MapSizeY - 1) Then mapRow = mapRow + "]"
		End Select
		
		;Finalise
		mapRow = "	" + mapRow
		
		;Write row
		WriteLine(file, mapRow)
		
	Next	
	
	;Output padding - botton row?
	If (MapPadding > 0) Then WritePaddingRowMonkey(file, True)

	;Finalise
	WriteLine(file, "")
	WriteLine(file, "'#End Region")
	
	;Close	
	CloseFile(file) 

End Function

Function WritePaddingRowData(file)
	Local x
	
	;Get width
	Local width = MapSizeX
	If (MapPadding) Then width = width + 2 
	
	;Write
	For x = 0 To width - 1
		WriteInt(file, 0)
	Next
	
End Function

Function WritePaddingRowMonkey(file, isLast=False)
	Local paddingRow$ = ""
	Local x
	
	;Get width
	Local width = MapSizeX
	If (MapPadding) Then width = width + 2 
	
	;Build row data
	For x = 0 To width - 1
		If (Len(paddingRow) > 0) Then paddingRow = paddingRow + ","
		paddingRow = paddingRow + "0" 
	Next
	
	;Append to end of row?
	If (Not isLast) Then paddingRow = paddingRow + ","
	If (isLast) Then paddingRow = paddingRow + "]"

	;Finalise
	paddingRow = "	" + paddingRow

	;Write row
	WriteLine(file, paddingRow)
	
End Function

Function CreateFolder(path$)
	Local folder$ = SystemProperty("appdir") + path
	CreateDir(folder)
	
End Function


;sswift - Scales the image without blurring
Function ScaleImageFast(SrcImage, ScaleX#, ScaleY#)
	Local SrcWidth,  SrcHeight
	Local DestWidth, DestHeight
	Local ScratchImage, DestImage
	Local SrcBuffer, ScratchBuffer, DestBuffer
	Local X1, Y1, X2, Y2

	;Get the width and height of the source image. 	
	SrcWidth  = ImageWidth(SrcImage)
	SrcHeight = ImageHeight(SrcImage)

	;Calculate the width and height of the dest image.
	DestWidth  = Floor(SrcWidth  * ScaleX#)
	DestHeight = Floor(SrcHeight * ScaleY#)

	;If the image does not need to be scaled, just copy the image and exit the function.
	If (SrcWidth = DestWidth) And (SrcHeight = DestHeight) Then Return CopyImage(SrcImage)

	;Create a scratch image that is as tall as the source image, and as wide as the destination image.
	ScratchImage = CreateImage(DestWidth, SrcHeight)
				
	;Create the destination image.
	DestImage = CreateImage(DestWidth, DestHeight) 

	;Get pointers to the image buffers.
	SrcBuffer     = ImageBuffer(SrcImage)
	ScratchBuffer = ImageBuffer(ScratchImage)
	DestBuffer    = ImageBuffer(DestImage)

	;Duplicate columns from source image to scratch image.
	For X2 = 0 To DestWidth-1
		X1 = Floor(X2 / ScaleX#)
		CopyRect X1, 0, 1, SrcHeight, X2, 0, SrcBuffer, ScratchBuffer
	Next
			
	;Duplicate rows from scratch image to destination image.
	For Y2 = 0 To DestHeight-1
		Y1 = Floor(Y2 / ScaleY#)
		CopyRect 0, Y1, DestWidth, 1, 0, Y2, ScratchBuffer, DestBuffer
	Next
						
	;Free the scratch image.
	FreeImage ScratchImage					
						
	;Return the new image.
	Return DestImage
					
End Function

Function ReadIniFile()
	Local fileName$="Config.ini"
	Local stream = ReadFile(fileName)
	Local l$,flag$,value$
	
	;Validate
	If (Not stream) Then Return
	
	;Scan
	While Not Eof(stream)
		l = ReadLine(stream)
		flag = Upper(GetIniFlag(l))
		value = GetIniValue(l)
		
		;Validate
		Select Upper(value)
			Case "TRUE"
				value = 1
			Case "FALSE"
				value = 0
			Default
				;DO nothing
		End Select
				
		;Assign
		Select flag
			Case "SCALETILES"
				ScaleTiles = value
			Case "TILEWIDTH"
				TileWidth = value
			Case "TILEHEIGHT"
				TileHeight = value
			Case "TILEPADDING"
				TilePadding = value
			Case "MAPPADDING"
				MapPadding = value
			Default
				;DO nothing
		End Select
		
	Wend
	
	;Finalise
	CloseFile(stream)
	
End Function

Function GetIniFlag$(l$)
	Local pos = Instr(l,"=")
	If (pos = 0) Then Return l
	Return Mid(l,1,pos-1)
End Function

Function GetIniValue$(l$)
	Local pos = Instr(l,"=")
	If (pos = 0) Then Return ""
	Return Mid(l,pos + 1,Len(l) - pos)
End Function
