; ID: 157
; Author: Popstar
; Date: 2002-01-08 09:09:44
; Title: TileWiper V1.2
; Description: Optimizes any tileset...

;----------------------------;
;      TileWiper V1.2        ;
;----------------------------;
;My first Blitz Basic Program;
;      Latest version:       ;
;    January 8th - 2002      ;
;----------------------------;
;          Made by           ;
;    Laust Palbo Nielsen     ;
;----------------------------;
;  This program was made to  ;
;optimize the amount of tiles;
;  used in a tilemap on all  ;
; tilebased sytems that can  ;
;        flip tiles          ;
;                            ;
; If you use this program as ;
;part of the development of a;
; commercial product, please ;
;send a copy of the finished ;
;       product to me.       ;
;                            ;
;   Current address can be   ;
;   obtained by mailing me   ;
;      @ laust@palbo.dk      ;
;----------------------------;

AppTitle "TileWiper V1.2"

Global TileOne, TileTwo, Changeflag, TileSizeX, TileSizeY, TileSize, TilesX, TilesY, MapSize, PicWidth, PicHeight, MaxLoss#, Tiles
Global Width, Height, OffSetX, OffSetY, Threshold#, Image$

Global Counter

Global SumRed, SumGreen, SumBlue, Sum#, Percent#
Global SumRedFlipX, SumGreenFlipX, SumBlueFlipX, SumFlipX#, PercentFlipX#
Global SumRedFlipY, SumGreenFlipY, SumBlueFlipY, SumFlipY#, PercentFlipY#
Global SumRedFlipXY, SumGreenFlipXY, SumBlueFlipXY, SumFlipXY#, PercentFlipXY#

Global ColorDifRed, ColorDifGreen, ColorDifBlue
Global ColorDifRedFlipX, ColorDifGreenFlipX, ColorDifBlueFlipX
Global ColorDifRedFlipY, ColorDifGreenFlipY, ColorDifBlueFlipY
Global ColorDifRedFlipXY, ColorDifGreenFlipXY, ColorDifBlueFlipXY

Global ColorTwoGreen, ColorOneBlue, ColorTwoBlue
Global ColorTwoRedFlipX, ColorTwoGreenFlipX, ColorTwoBlueFlipX
Global ColorTwoRedFlipY, ColorTwoGreenFlipY, ColorTwoBlueFlipY
Global ColorTwoRedFlipXY, ColorTwoGreenFlipXY, ColorTwoBlueFlipXY

Global Dist#, DistFlipX#, DistFlipY#, DistFlipXY#

Width=352 : Height=420

Graphics3D Width, Height, 32, 2
ClsColor 128,128,196
Cls

Color 255, 255, 255

;-----------------------------------;
;         Start the Program         ;
;-----------------------------------;

;Get the user information via inputs

Image$=Input$ ("Source image: ") If Image$="" Then Image$="background.bmp"
Loss#=Input ("Loss (1-100) - Press Enter for 100 : ") If Loss#=0 Then Loss#=100
Thres#=Loss# ;Input ("Threshold (1-100): ") If Thres#=0 Then Thres#=5
TileSizeX=Input ("Tile size X - Press Enter for 8 : ") If TileSizeX=0 Then TileSizeX=8
TileSizeY=Input ("Tile size Y - Press Enter for 8 : ") If TileSizeY=0 Then TileSizeY=8

ImgSize=LoadImage (Image$)
PicWidth=ImageWidth (ImgSize)
PicHeight=ImageHeight (ImgSize)
TileSize=TileSizeX*TileSizeY

FreeImage (ImgSize)

;Now that the user has typed in this information, get the last variables

MaxLoss#=Loss#*195.075*TileSize
Threshold#=Thres#*195.075*(TileSize/20)

;Get the number of tiles on the X and Y Axis

TilesX=PicWidth/TileSizeX
TilesY=PicHeight/TileSizeY

;Get the number of tiles in all

MapSize=(TilesX*TilesY)

;Get the starttime so we can see how long it takes

StartTime=MilliSecs()

;Change the screenmode if the picture is too large

Width=Picwidth+32
Height=PicHeight+260

If Width<352 Then Width=352
If Height<420 Then Height=420

Graphics3D Width, Height, 32 ,2
ClsColor 128,128,196

;Clear the screen and start working

Cls

OptimizedImage=CreateImage (PicWidth, PicHeight)

;Set the offsets so the pictures will be centered on the screen

OffsetX=(Width-PicWidth)/2
OffsetY=(Height-PicHeight)/4

Dim NewMap						(MapSize)			;This is where the final map will be stored
Dim FlipMap						(MapSize)			;This map stores the data of which way a tile is flipped
Dim PercentMap#					(Mapsize)			;This map stores the current % information of each tile

Dim TileSetRed					(TileSize, MapSize)	;These three arrays store the standard tile colors
Dim TileSetGreen				(TileSize, MapSize)
Dim TileSetBlue					(TileSize, MapSize)

Dim TileSetRedFlipX				(TileSize, MapSize)	;These three arrays store the tile colors of tiles flipped
Dim TileSetGreenFlipX			(TileSize, MapSize)	;on the X-axis
Dim TileSetBlueFlipX			(TileSize, MapSize) 

Dim TileSetRedFlipY				(TileSize, MapSize)	;These three arrays store the tile colors of tiles flipped
Dim TileSetGreenFlipY			(TileSize, MapSize)	;on the Y-axis
Dim TileSetBlueFlipY			(TileSize, MapSize)

Dim TileSetRedFlipXY			(TileSize, MapSize)	;These three arrays store the tile colors of tiles flipped
Dim TileSetGreenFlipXY			(TileSize, MapSize)	;on the X and Y-axis
Dim TileSetBlueFlipXY			(TileSize, MapSize)

Dim CountArray					(MapSize)			;This array is used at the end to count the amount of unique tiles

;Setup the NewMap and PercentMap arrays so all the tiles that aren't changed will still be drawn

Setup ()

;Load the Tilemap

Tiles=LoadAnimImage (Image$, TileSizeX, TileSizeY, 0, MapSize)

PreCalculate ()

SetBuffer FrontBuffer()								;Now that we're done, go to the frontbuffer
Cls													;and clear the screen

PreCalcTimeEnd=MilliSecs()

;DrawSource ()
DrawDestination ()

Color 255, 255, 255

Text Width/2, OffsetY-20, "Precalculation done in "+((PreCalcTimeEnd-StartTime)/1000)+" seconds", 1, 0

;Main Program
;----------------------------

Main ()

;Compare
;--------------------------------

For Count=MapSize-1 To 0 Step -1		;Go through the entire map backwards
	Compare=NewMap(Count)				;Compare gets the tilenumber that has been used in NewMap(Count)
	If Compare<>Newmap(Compare)			;If the tile on the position that equals Compare does npt itself equal Compare,
		NewMap(Compare)=Compare			;then put it back in its original position
		FlipMap(Compare)=0				;and make sure that it is not flipped.
	End If
Next


;Draw the last changes
;--------------------------------

SetBuffer ImageBuffer(OptimizedImage)

FlipTiles ()

SetBuffer FrontBuffer()

DrawImage (OptimizedImage, OffsetX, OffsetY)


;Check to see how many different tiles are left.
;--------------------------------

For Count=0 To MapSize-1
	Intermediate=NewMap(Count)
	For Reading=Count+1 To MapSize-1
		If Intermediate=Newmap(Reading)
			CountArray(Reading)=0
		End If
	Next
Next


;Check to see how many of these tiles are flipped.
;--------------------------------

FinalCount=0
FlipXsum=0
FlipYsum=0
FlipXYsum=0

For Count=0 To MapSize-1
	FinalCount=FinalCount+CountArray(Count)
	If FlipMap(Count)>0
		If FlipMap(Count)=1 Then FlipXsum=FlipXsum+1
		If FlipMap(Count)=2 Then FlipYsum=FlipYsum+1
		If FlipMap(Count)=3 Then FlipXYsum=FlipXYsum+1
	End If
Next


;Prepare a TileSet from NewMap
;--------------------------------
;!!!Note: This is where I should create a binary file to store the map information!!!

TileSet ()


;Print the final results
;--------------------------------

Color 128,128,196										;Set the color to the background color
Rect (Width/2)-60, 20, 120, 20, 1					;Remove what we displayed last time
Color 255, 255, 255									;Set the color to white

StopTime=MilliSecs()

Text Width/2, 20, "Done in "+((StopTime-StartTime)/1000)+" seconds", 1, 0
Text 8, Height-180, " # of Tiles: "+MapSize
Text 8, Height-160, " Unique Tiles: "+FinalCount
Text 8, Height-120, " Tiles Flipped X: "+FlipXsum
Text 8, Height-100, " Tiles Flipped Y: "+FlipYsum
Text 8, Height-80, " Tiles Flipped XY: "+FlipXYsum

Text 8, Height-40, " Press 'S' to save optimized BMP image."
Text 8, Height-20, " Press ESC to quit."


;Wait for the final user interaction
;--------------------------------

Repeat

	If KeyDown(1)=1 Then End						;If user presses ESC then end the program
	If KeyDown(31)=1								;If user presses 'S' then save the images and end the program
		SaveImage (OptimizedImage, "TW_"+Image$)
		End
	End If

Forever

End








;!!!!----------------------------------------------------Functions------------------------------------------------------!!!!

Function Setup ()

	For Count=0 To MapSize-1
		NewMap(Count)=Count
		PercentMap#(Count)=MaxLoss#
		CountArray(Count)=1
	Next

End Function

;------------------------------

Function PreCalculate()

;Store all colors of all tiles in four sets of arrays: TileSet

	Text width/2, 0, "Precalculating!", 1, 0 			;Tell the user why 'nothing' is happening
													
	For PreCalc=0 To MapSize-1

		SetBuffer BackBuffer()							;Do all the drawing in the backbuffer so it doesn't look weird onscreen
	
		DrawBlock (tiles, 0, 0, PreCalc)
		PreCalcCount=0
		CounterX=0
		CounterY=0
	
		For Y=0 To TileSizeY-1
			For X=0 To TileSizeX-1
				GetColor X, Y

				TileSetRed			(PreCalcCount, PreCalc)=ColorRed()	;Store R, G and B values of the source tiles
				TileSetGreen		(PreCalcCount, PreCalc)=ColorGreen()
				TileSetBlue			(PreCalcCount, PreCalc)=ColorBlue()

				FlipXOne=(TileSizeX-1)-X								;Flip X-values
				FlipYOne=(Y)											;Y is normal
				FlipX=FlipXOne+(TileSizeX*FlipYOne)						;Calculate the FlipX value

				TileSetRedFlipX		(FlipX, PreCalc)=ColorRed()			;Store R, G and B values of tiles flipped
				TileSetGreenFlipX	(FlipX, PreCalc)=ColorGreen()		;on the X-axis
				TileSetBlueFlipX	(FlipX, PreCalc)=ColorBlue()

				FlipXOne=(X)											;X is normal
				FlipYOne=(TileSizeY-1)-Y								;Flip Y-values
				FlipY=FlipXOne+(TileSizeX*FlipYOne)						;Calculate the FlipX value
			
				TileSetRedFlipY		(FlipY, PreCalc)=ColorRed()			;Store R, G and B values of tiles flipped
				TileSetGreenFlipY	(FlipY, PreCalc)=ColorGreen()		;on the Y-axis
				TileSetBlueFlipY	(FlipY, PreCalc)=ColorBlue()

				FlipXOne=(TileSizeX-1)-X								;Flip X-values
				FlipYOne=(TileSizeY-1)-Y								;Flip Y-valus
				FlipXY=FlipXOne+(TileSizeX*FlipYOne)					;Calculate the FlipXY value
			
				TileSetRedFlipXY	(FlipXY, PreCalc)=ColorRed()		;Store R, G and B values of tiles flipped
				TileSetGreenFlipXY	(FlipXY, PreCalc)=ColorGreen()		;on the X and Y-axis
				TileSetBlueFlipXY	(FlipXY, PreCalc)=ColorBlue()

				PreCalcCount=PreCalcCount+1

			Next
		Next

		If KeyDown(1)=1 Then End
	
		SetBuffer FrontBuffer()							;Go back to the frontbuffer and show the user that something is actually happening
		Color 128,128,196									;Set the color to the background color
		Rect (Width/2)-60, 20, 120, 20, 1				;Remove what we displayed last time
		Color 255, 255, 255								;Set the color to white
		Text Width/2, 20, (PreCalc+1)+"/"+MapSize, 1, 0	;Show how far we've come
							
	Next

End Function

;------------------------------

Function Main ()

	Color 0, 255, 128
	RectX=0 : RectY=0

	For TileOne=0 To MapSize-1

		If RectX=PicWidth 
			RectX=0
			RectY=RectY+TileSizeY
		End If

		If PercentMap#(TileOne)<>-1							;Only check if the tile hasn't been locked.

			Color 128,128,196									;Set the color to the background color
			Rect (Width/2)-60, 20, 120, 20, 1				;Remove what we displayed last time
			Color 255, 255, 255								;Set the color to white
			Text Width/2, 20, TileOne+"/"+MapSize, 1, 0		;Show how far we've come

;			For TileTwo=TileOne+1 To MapSize-1
			For TileTwo=0 To MapSize-1
						
				If PercentMap#(TileTwo)<>-1 And TileTwo<>TileOne

					Color 128, 0, 128
					Rect OffSetX+RectX, OffSetY+RectY, TileSizeX, TileSizeY, 1
			
					CompareTiles ()

				End If

			Next

			;If there has been any changes to NewMap (ChangeFlag is set to 1) then redraw NewMap

			If ChangeFlag=1
				DrawDestination ()
				ChangeFlag=0
			End If

		End If

		RectX=RectX+TileSizeX

	Next

End Function

;------------------------------

Function DrawSource ()

	Frame=0	

	For y=0 To TilesY-1
		For x=0 To TilesX-1
			DrawBlock (Tiles, (X*TileSizeX)+OffsetX-1, (Y*TileSizeY+OffsetY), Frame)
			Frame=Frame+1
		Next
	Next

End Function

;------------------------------

Function DrawDestination ()

	Frame=0	

	For y=0 To TilesY-1
		For x=0 To TilesX-1
			DrawBlock (Tiles, (X*TileSizeX)+OffsetX, (Y*TileSizeY+OffsetY), NewMap(Frame))
			Frame=Frame+1
		Next
	Next
	
End Function

;------------------------------

Function CompareTiles ()

	Counter=0

	SumRed=0
	SumGreen=0
	SumBlue=0

	Sum#=0

	SumRedFlipX=0
	SumGreenFlipX=0
	SumBlueFlipX=0

	SumFlipX#=0

	SumRedFlipY=0
	SumGreenFlipY=0
	SumBlueFlipY=0

	SumFlipY#=0

	SumRedFlipXY=0
	SumGreenFlipXY=0
	SumBlueFlipXY=0

	SumFlipXY#=0
	
	Dist#=0
	DistFlipX#=0
	DistFlipY#=0
	DistFlipXY#=0

	For Counter=0 To TileSize-1 

		GetRGBValues ()
		
		GetSums ()

	Next

	;Since most of the time is spent in this loop, the user can exit it here by pressing 'Esc.'
				
	If KeyDown(1)=1 Then End
	
	If Sum#=0 Or Sum#<Threshold# And PercentMap(TileTwo)<>-1
		NewMap(TileTwo)=TileOne
		PercentMap#(TileTwo)=-1
		FlipMap(TileTwo)=0
		ChangeFlag=1
	End If

	If SumFlipX#=0 Or SumFlipX#<Threshold# And PercentMap(TileTwo)<>-1
		NewMap(TileTwo)=TileOne
		PercentMap#(TileTwo)=-1
		FlipMap(TileTwo)=1
		ChangeFlag=1
	End If

	If SumFlipY#=0 Or SumFlipY#<Threshold# And PercentMap(TileTwo)<>-1
		NewMap(TileTwo)=TileOne
		PercentMap#(TileTwo)=-1
		FlipMap(TileTwo)=2
		ChangeFlag=1
	End If

	If SumFlipXY#=0 Or SumFlipXY#<Threshold# And PercentMap(TileTwo)<>-1
		NewMap(TileTwo)=TileOne
		PercentMap#(TileTwo)=-1
		FlipMap(TileTwo)=3
		ChangeFlag=1
	End If

	DoSums ()

End Function

;--------------------------------

Function GetRGBValues ()

	;Get the RGB values for tiles that aren't flipped.
				
	ColorOneRed=TileSetRed(Counter, TileOne)
	ColorTwoRed=TileSetRed(Counter, TileTwo)
	ColorOneGreen=TileSetGreen(Counter, TileOne)

	;Get the RGB values for the tiles that we are comparing to.
				
	ColorTwoGreen=TileSetGreen(Counter, TileTwo)
	ColorOneBlue=TileSetBlue(Counter, TileOne)
	ColorTwoBlue=TileSetBlue(Counter, TileTwo)

	;Get the RGB values for tiles that are flipped X.
				
	ColorTwoRedFlipX=TileSetRedFlipX(Counter, TileTwo)
	ColorTwoGreenFlipX=TileSetGreenFlipX(Counter, TileTwo)
	ColorTwoBlueFlipX=TileSetBlueFlipX(Counter, TileTwo)

	;Get the RGB values for tiles that are flipped Y.
				
	ColorTwoRedFlipY=TileSetRedFlipY(Counter, TileTwo)
	ColorTwoGreenFlipY=TileSetGreenFlipY(Counter, TileTwo)
	ColorTwoBlueFlipY=TileSetBlueFlipY(Counter, TileTwo)

	;Get the RGB values for tiles that are flipped X and Y.
				
	ColorTwoRedFlipXY=TileSetRedFlipXY(Counter, TileTwo)
	ColorTwoGreenFlipXY=TileSetGreenFlipXY(Counter, TileTwo)
	ColorTwoBlueFlipXY=TileSetBlueFlipXY(Counter, TileTwo)

	Dist#=Dist#+((ColorOneRed-ColorTwoRed)*(ColorOneRed-ColorTwoRed))+((ColorOneGreen-ColorTwoGreen)*(ColorOneGreen-ColorTwoGreen))+((ColorOneBlue-ColorTwoBlue)*(ColorOneBlue-ColorTwoBlue))
	DistFlipX#=DistFlipX#+((ColorOneRed-ColorTwoRedFlipX)*(ColorOneRed-ColorTwoRedFlipX))+((ColorOneGreen-ColorTwoGreenFlipX)*(ColorOneGreen-ColorTwoGreenFlipX))+((ColorOneBlue-ColorTwoBlueFlipX)*(ColorOneBlue-ColorTwoBlueFlipX))
	DistFlipY#=DistFlipY#+((ColorOneRed-ColorTwoRedFlipY)*(ColorOneRed-ColorTwoRedFlipY))+((ColorOneGreen-ColorTwoGreenFlipY)*(ColorOneGreen-ColorTwoGreenFlipY))+((ColorOneBlue-ColorTwoBlueFlipY)*(ColorOneBlue-ColorTwoBlueFlipY))
	DistFlipXY#=DistFlipXY#+((ColorOneRed-ColorTwoRedFlipXY)*(ColorOneRed-ColorTwoRedFlipXY))+((ColorOneGreen-ColorTwoGreenFlipXY)*(ColorOneGreen-ColorTwoGreenFlipXY))+((ColorOneBlue-ColorTwoBlueFlipXY)*(ColorOneBlue-ColorTwoBlueFlipXY))

End Function

;--------------------------------

Function GetSums ()

	;Add it all up and get a final sum for tiles that aren't flipped.
				
	Sum#=Dist#

	SumFlipX#=DistFlipX#

	SumFlipY#=DistFlipY#
				
	SumFlipXY#=DistFlipXY#

;WaitMouse

End Function

;--------------------------------

Function DoSums()

	Percent#=Sum#;/TileSize
	PercentFlipX#=SumFlipX#;/TileSize
	PercentFlipY#=SumFlipY#;/TileSize
	PercentFlipXY#=SumFlipXY#;/TileSize

	;--------------------------------
		
	;If this tile looks more like the original than any other, then use this tile instead.
		
	If Percent#<MaxLoss# And Percent#<=PercentMap#(TileTwo)
		PercentMap#(TileTwo)=Percent#
		NewMap(TileTwo)=TileOne
		FlipMap(TileTwo)=0
		ChangeFlag=1			;By setting ChangeFlag to 1, the NewMap will be redrawn
	End If
		
	;If this tile, flipped X, looks more like the original than any other, then use this tile instead.
		
		
	If PercentFlipX#<MaxLoss# And PercentFlipX#<=PercentMap#(TileTwo)
		PercentMap#(TileTwo)=PercentFlipX#
		NewMap(TileTwo)=TileOne
		FlipMap(TileTwo)=1
		ChangeFlag=1			;By setting ChangeFlag to 1, the NewMap will be redrawn
	End If
		
	;If this tile, flipped Y, looks more like the original than any other, then use this tile instead.
		
		
	If PercentFlipY#<MaxLoss# And PercentFlipY#<=PercentMap#(TileTwo)
		PercentMap#(TileTwo)=PercentFlipY#
		NewMap(TileTwo)=TileOne
		FlipMap(TileTwo)=2
		ChangeFlag=1			;By setting ChangeFlag to 1, the NewMap will be redrawn
	End If
		
	;If this tile, flipped X and Y, looks more like the original than any other, then use this tile instead.
		
	
	If PercentFlipXY#<MaxLoss# And PercentFlipXY#<=PercentMap#(TileTwo)
		PercentMap#(TileTwo)=PercentFlipXY#
		NewMap(TileTwo)=TileOne
		FlipMap(TileTwo)=3
		ChangeFlag=1			;By setting ChangeFlag to 1, the NewMap will be redrawn
	End If

End Function

;--------------------------------

Function FlipTiles ()

	Frame=0

	For Y=0 To TilesY-1
		For X=0 To TilesX-1

			;Draw the NewMap one last time to make sure that the new changes are drawn


			If FlipMap(Frame)=0				;Check the FlipMap to make sure that this tile shouldn't be flipped
				DrawBlock (Tiles, (X*TileSizeX), Y*TileSizeY, NewMap(Frame))
			End If


			;Flip the tiles that need to be flipped

			If FlipMap(Frame)>0				;Check the FlipMap to see if (and how) this tile should be flipped
				
				CountPixel=0				
				
				For PY=0 To TilesizeY-1
					For PX=0 To TilesizeX-1
				
						If FlipMap(Frame)=1 Then Color TileSetRedFlipX(CountPixel, NewMap(Frame)), TileSetGreenFlipX(CountPixel, NewMap(Frame)), TileSetBlueFlipX(CountPixel, NewMap(Frame))
						If FlipMap(Frame)=2 Then Color TileSetRedFlipY(CountPixel, NewMap(Frame)), TileSetGreenFlipY(CountPixel, NewMap(Frame)), TileSetBlueFlipY(CountPixel, NewMap(Frame))
						If FlipMap(Frame)=3 Then Color TileSetRedFlipXY(CountPixel, NewMap(Frame)), TileSetGreenFlipXY(CountPixel, NewMap(Frame)), TileSetBlueFlipXY(CountPixel, NewMap(Frame))
				
					Plot (X*TileSizeX)+PX, (Y*TileSizeY)+PY
				
					CountPixel=CountPixel+1
				
					Next
				Next
		
			End If

			Frame=Frame+1

		Next
	Next

End Function

;--------------------------------

Function TileSet ()

	NewSet=CreateImage (PicWidth, PicHeight)
	SetBuffer ImageBuffer(NewSet)

	DrawBlock (Tiles, 0, 0, 0)
	GetColor 0, 0
	ClsColor ColorRed(), ColorGreen(), ColorBlue()
	Cls

	CounterTwo=0 : X=0 : Y=0
	For Count=0 To FinalCount-1
		Repeat
			If CountArray(CounterTwo)=1
				DrawBlock (Tiles, X*TileSizeX, Y*TileSizeY, NewMap(CounterTwo))
				Flag=1 : X=X+1
				If X=TilesX
					Y=Y+1
					X=0
				End If
			End If
			CounterTwo=CounterTwo+1
		Until Flag=1
		Flag=0
	Next

	SetBuffer FrontBuffer()

End Function
