; ID: 3248
; Author: TomToad
; Date: 2016-01-21 20:00:10
; Title: Palette Rotation
; Description: Simulates 8 bit palette rotation

SuperStrict

Graphics 800,600

'We need to create the color planes.  We will use 16 fullscreen TImages.  Note that you only need the images to
'  Be big enough to cover the area you wish to have a Palette
Global Planes:TImage[16]

'Next, we need to create a Palette.  Each color corrisponds to one of the color planes.  You can use any color you want\
'  Here, I fill it with some default values
Global Palette:Int[] = [$000000,$FFFFFF,$FF0000,$00FF00,$0000FF,$FF00FF,$FFFF00,$00FFFF, ..
						$7F7F7F,$7f0000,$007F00,$00007F,$7F007F,$7F7F00,$007F7F,$FF7F00]



'This is a more optimized PlotPixel than before.  It will queue all the changes, but only commit the changes when rendering to the screen
'   for the first time
Type TPixel 'a pixel type
	Field x:Int, y:Int
	
	Function Create:TPixel(x:Int,y:Int)
		Local Pixel:TPixel = New TPixel
		Pixel.x = x
		Pixel.y = y
		Return Pixel
	End Function
End Type

Type TQueue 'Creating a type for the queued pixels
	Field Dirty:Int = False 'A flag that is set when pixels are queued
	
	Field Pixels:Byte[800,600] 'An array to hold the indexes for the Planes pixel queue.  To make searching the queue faster.
	Field Pixmaps:TPixmap[16] 'All rendering will be done to pixmaps then transfered to the Planes
	Field Queue:TList[16] 'An array of lists to hold the queued pixels
	Field ClearQueue:TList = CreateList() 'Pixels to be cleared in each plane
	
	Method New() 'Initialize the values
		For Local i:Int = 0 To 15
			Queue[i] = CreateList() 'Initialize the queue
			Pixmaps[i] = CreatePixmap(800,600,PF_RGBA8888) 'Create the pixmap
			If i = 0 'Pixmap 0 will be all set, the rest clear
				ClearPixels(Pixmaps[0],$FFFFFFFF)
			Else
				ClearPixels(Pixmaps[i],$00FFFFFF)
			End If
			Planes[i] = LoadImage(Pixmaps[i]) 'Transfer the pixmap to the Plane
		Next
		For Local y:Int = 0 To 599
			For Local x:Int = 0 To 799
				Pixels[x,y] = 16 'Using 16 to signify that this pixel hasn't been queued anywhere
			Next
		Next
	End Method
	
	Method QueuePixel(x:Int, y:Int, Plane:Int) 'queues the pixel for writing
		Local Queued:Int = Pixels[x,y] 'Which plane is this pixel queued in?
		If Plane = Queued Then Return 'This pixel is already queued.  No need to do it twice
		
		'We must first remove the queued pixel before changing planes
		If Queued < 16 'If 16, then pixel is not queued and does not need to be removed
			For Local Pixel:TPixel = EachIn Queue[Queued]
				If Pixel.x = x And Pixel.y = y 'We have found our queued pixel
					Queue[Queued].Remove(Pixel) 'Remove this pixel from the queue
					Exit 'exit the loop
				End If
			Next
		End If
		
		'Now we must queue the pixel into the new plane
		Pixels[x,y] = Plane
		Queue[Plane].AddLast(TPixel.Create(x,y))
		
		'last, we need to mark the queue dirty so the renderer knows it needs to commit
		Dirty = True
	End Method
	
	'Here we commit the changes made and clear the queue
	Method Commit()
		If Not Dirty Then Return 'no need to commit if no changes are made
		
		For Local i:Int = 0 To 15 'Go through each plane's queue
			For Local Pixel:TPixel = EachIn ClearQueue 'First we will set alpha to 0 on pixels that should be clear
				Pixmaps[i].pixels[Pixel.y*Pixmaps[i].pitch+Pixel.x*4+3] = 0 'A little bit of math for faster access to the pixmap's pixels
			Next
			For Local Pixel:TPixel = EachIn Queue[i] 'now we will write the pixels in the queue
				Pixmaps[i].pixels[Pixel.y*pixmaps[i].pitch+Pixel.x*4+3] = $ff 'same math as before :)
				ClearQueue.AddLast(Pixel) 'Add this pixel to the Clear queue so all planes above will clear this pixel
				Pixels[Pixel.x,Pixel.y] = 16 'this pixel no longer needs to be indexed
			Next
			Queue[i].Clear() 'Clear this plane's queue
			Planes[i] = LoadImage(Pixmaps[i]) 'transfer the pixmap to the image
		Next
		
		ClearQueue.Clear() 'Clear the clear queue.
		Dirty = False 'now everything is reset to be filled again :)
	End Method
End Type
Global Queue:TQueue = New TQueue 'now to create the queue
		
		
' Place a pixel into the queue
Function PlotPixel(x:Int,y:Int,Plane:Int)
	If x > 799 Or x < 0 Or y > 599 Or y < 0 Or plane > 15 Or plane < 0 Then Return 'Pixel out of range
	Queue.QueuePixel(x,y,Plane)
End Function

'Here we render the planes.  We grab the color from the Palette and seperate the components for SetColor.  Then draw the plane to the screen
Function RenderScreen()
	Queue.Commit() 'commit any changes to the planes
	For Local i:Int = 0 To 15
		Local Red:Int = (Palette[i] & $FF0000) Shr 16
		Local Green:Int = (Palette[i] & $FF00) Shr 8
		Local Blue:Int = Palette[i] & $FF
		
		SetColor Red,Green,Blue
		DrawImage Planes[i],0,0
	Next
End Function

'A little message to display to let people know something is going on.
Cls
DrawText "Please wait, setting up graphics",10,10
Flip
'We shall plot some concentric rings. Distance is modded to 16 to corrispond to one of the 16 planes
For Local y:Int = 0 To 599
	For Local x:Int = 0 To 799
		Local Distance:Int = Sqr((x-400)^2+(y-300)^2)/5
		PlotPixel(x,y,Distance Mod 16)
	Next
Next


While Not KeyHit(KEY_ESCAPE) And Not AppTerminate()
	RenderScreen 'Here we render the color planes, no need for Cls as the planes will cover the entire screen
	Flip 'Then flip, If you get a lot of tearing on the screen, change this to Flip 1
'Rem	
	'Here we rotate the Palette entries by one. :-)
	Local Temp:Int = Palette[0]
	For Local i:Int = 0 To 14
		Palette[i] = Palette[i+1]
	Next
	Palette[15] = temp	
'End Rem

Rem 'rem out the previous lines and remove the rem/endrem here to see Palette being manipulated in a different way
	For Local i:Int = 0 To 15
		Palette[i] = (Palette[i] + 1) &$FFFFFF
	Next
End Rem	
Wend
