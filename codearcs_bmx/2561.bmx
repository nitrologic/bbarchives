; ID: 2561
; Author: BlitzSupport
; Date: 2009-08-15 10:54:27
; Title: Cellular Textures
; Description: Procedural texture generation

' Array of pixels in 'texture' to be generated...

Local points:Float [256, 256]

' Number of random points to use for generating cells...

rpoints = 20 ' Play with this! Higher values get slower and slower...

' 2D point type...

Type Point
	Field x:Float
	Field y:Float
End Type

' Array of Point objects...

Local random:Point [rpoints]

' A list for the random points...

pointlist:TList = CreateList ()

' Display stuff...

Graphics 640, 480

gw2 = GraphicsWidth () / 2.0
gh2 = GraphicsHeight () / 2.0

SetBlend LIGHTBLEND
AutoMidHandle True

' Create a pixmap to draw to...

pix:TPixmap = CreatePixmap (256, 256, PF_RGB888)

' Main loop...

Repeat

	Cls

	' Delete last set of random points...
		
	ClearList pointlist
	
	' Generate new ones and add to list...
	
	For r = 0 To rpoints - 1
		pt:Point = New Point
		pt.x = Rand (0, 255)
		pt.y = Rand (0, 255)
		ListAddLast pointlist, pt
	Next
		
	' Iterate through all pixels in texture...
	
	For x = 0 To 255
	
		For y = 0 To 255
		
			' Set start values...
			
			closest# = 255
			furthest# = 0
			
			' For every pixel, find the nearest of the random points...
			
			For pt = EachIn pointlist
				d# = Dist (x, y, pt.x, pt.y)
				If d < closest Then closest = d
			Next
			
			' This pixel's colour value is based on distance to nearest random point...
			
			points [x, y] = closest
	
		Next
		
	Next
	
	' Have to scale the distance to 0-255 so it can be used to set the colour...

	scale# = 255.0 / closest
	
	' Scale each pixel's colour value to fit 0-255 (points is the pixel array)...
	
	For p:Float = EachIn points
		p = p * scale
	Next

	' Write colour value for each pixel into the pixmap...
	
	For x = 0 To 255
		For y = 0 To 255
			color = points [x, y]
			color = ((color Shl 16) + (color Shl 8) + color) ' Conversion to RGB...
			WritePixel pix, x, y, color
		Next
	Next
	
	' Grab image from bitmap so it can be scaled...
	
	image:TImage = LoadImage (pix)

	' Draw result! Try different combinations of the below...
	
	SetScale GraphicsWidth () / 256.0, GraphicsHeight () / 256.0
	SetColor 255, 255, 255
	DrawImage image, gw2, gh2

'	SetScale GraphicsWidth () / 256.0, -GraphicsHeight () / 256.0
'	SetColor 255, 0, 0
'	DrawImage image, gw2, gh2

'	SetScale -GraphicsWidth () / 256.0, GraphicsHeight () / 256.0
'	SetColor 0, 255, 0
'	DrawImage image, gw2, gh2

'	SetScale -GraphicsWidth () / 256.0, -GraphicsHeight () / 256.0
'	SetColor 0, 0, 255
'	DrawImage image, gw2, gh2

	' Draw the random points used for calculation...
	
	'SetColor 255, 0, 0
	
	'For pt = EachIn pointlist
	'	Plot pt.x, pt.y
	'Next
	
	Flip

Until KeyHit (KEY_ESCAPE)

End

' Distance between x/y points...

Function Dist:Float (x1:Float, y1:Float, x2:Float, y2:Float)
	Return Sqr (((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)))
End Function
