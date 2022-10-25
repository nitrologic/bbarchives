; ID: 2717
; Author: ImaginaryHuman
; Date: 2010-05-17 22:45:17
; Title: Draw Antialiased Circles
; Description: Draw antialiased circles with varying smoothness

'Antialiased circle by ImaginaryHuman

'There are 3 versions depending on the thickness/focus of the antialiasing around the circle
'SmoothCircle1 can accept thickness from 0 to 1.0, corresponding to the circle edge blurring across one whole pixel
'SmoothCircle2 can accept thickness from 0 to 3.0, corresponding to the circle edge blurring across up to 3 whole pixels
'SmoothCircle5 can accept thickness from 0 to 5.0, corresponding to the circle edge blurring across up to 5 whole pixels

'The circle is generated first by filling all rows within the circle using an integer-based bresenham circle algorithm
'The perimeter of a second circle is then generated to create points along the edge, and then among its neighbors on the same row or column
'several samples are taken of the distance from the pixel to the edge of the circle, producing a tonal gradient scaled by the Smoothness (thickness).
'The antialiasing circle is split into four quadrants. The left and right quadrants are generated and rendered a row at a time while
'the same data swaps the x and y coordinates to fill vertical columns in the top and bottom quadrants.
'The antialiasing circle algorithm is slightly modified in the loop counter to ensure that pixels on the exact diagonals are included in the antialiasing,
'but such a modification would normally cause those pixels to be drawn more than once in the fill pass.

Strict
Local Coloration:Int=$4488FF00			'Circle color RGBA
Local Smoothness:Float=2.0				'0..5 antialiasing border thickness, 0..1 use SmoothCircle1, 0..3 use SmoothCircle3, 0..5 use SmoothCircle5

Global ScreenW:Int=1440
Global ScreenH:Int=900
Graphics ScreenW,ScreenH,32
 
Local xMid:Int=ScreenW/2
Local yMid:Int=ScreenH/2
Local dx:Int
Local dy:Int
Local Radius:Int							'Must be an integer
Repeat
	Cls
	If MouseDown(1)
		xMid=MouseX()
		yMid=MouseY()
	EndIf
	dX=xMid-MouseX()					'Distance between the two points horizontally
	dY=yMid-MouseY()					'Distance between the two points vertically
	Radius=Sqr((dX*dX)+(dY*dY))		'Find the distance, always an absolute value
	SmoothCircle3(xMid,yMid,Radius,Coloration,Smoothness)
	Flip 1
Until KeyHit(KEY_ESCAPE)

Function SmoothCircle1(xCenter:Int,yCenter:Int,Radius:Int,Color:Int=$FFFFFFFF,Smoothing:Float=1.0)
	'Draw an antialiased circle, centered at xCenter,yCenter, with Radius in Color, and an antialiased border of Smoothing size
	'Smoothing can be from 0 to 1.0

	'Fill the circle interior using bresenham circle algorithm, no antialiasing, one row at a time, all integer math
	SetColor Color Shr 24,(Color & $FF0000) Shr 16,(Color & $FF00) Shr 8
	Local p:Int
	Local x:Int
	Local y:Int
	Local prevy:Int
	x=0
	y=radius
	DrawRect xCenter-y,yCenter+x,y Shl 1,1
	p=1-radius
	While x<y-1
		prevy=y
		If p<0
			x:+1
		Else
			x:+1
			y:-1
		EndIf
		If p<0
			p=p+(x Shl 1)+1
		Else
			p=p+((x-y) Shl 1)+1
		EndIf
		If y<prevy And x<y
			DrawRect xCenter-x,yCenter+y,x Shl 1,1
			DrawRect xCenter-x,yCenter-y,x Shl 1,1
		EndIf
		DrawRect xCenter-y,yCenter+x,y Shl 1,1
		DrawRect xCenter-y,yCenter-x,y Shl 1,1
	Wend

	'Do a second bresenham circle using the points around the circle as reference locations upon which to center a row or column of
	'antialiasing tests. The circle is divided into four quadrants. The left and right quadrants are calculated first and at each circle pixel
	'a number of neighboring pixels in the row are sampled and drawn based on the antialiasing boundary thickness. Then a similar thing
	'is done vertically by adding columns of neighboring pixels to antialiase the top and bottom quadrants.

	'Antialiase Left And Right quadrant edges
	x=0
	y=radius
	APlot(xCenter+y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'2nd pixel (inside)
	APlot(xCenter+y-1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y-1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'Top and bottom quadrants (x and y swapped)
	APlot(xCenter+x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
	'2nd pixel
	APlot(xCenter+x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
	p=1-radius
	While x<=y   'x<y-1 normally, but need to plot one or two extra pixels at the diagonal intersections between quadrants otherwise we get a `nip` out of the edge where antiliasing did not occur - this actually causes a kind of overdraw of some pixels but leaves a gap otherwise
		prevy=y
		If p<0
			x:+1
		Else
			x:+1
			y:-1
		EndIf
		If p<0
			p=p+(x Shl 1)+1
		Else
			p=p+((x-y) Shl 1)+1
		EndIf
	
		'Left and right quadrants
		APlot(xCenter+y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		'2nd pixel
		APlot(xCenter+y-1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y-1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		'Top and bottom quadrants (x and y swapped)
		APlot(xCenter+x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
		'2nd pixel
		APlot(xCenter+x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
	Wend

End Function

Function SmoothCircle3(xCenter:Int,yCenter:Int,Radius:Int,Color:Int=$FFFFFFFF,Smoothing:Float=1.0)
	'Draw an antialiased circle, centered at xCenter,yCenter, with Radius in Color, and an antialiased border of Smoothing size
	'Smoothing can be from 0 to 3.0

	'Fill the circle interior using bresenham circle algorithm, no antialiasing, one row at a time, all integer math
	SetColor Color Shr 24,(Color & $FF0000) Shr 16,(Color & $FF00) Shr 8
	Local p:Int
	Local x:Int
	Local y:Int
	Local prevy:Int
	x=0
	y=radius
	DrawRect xCenter-y,yCenter+x,y Shl 1,1
	p=1-radius
	While x<y-1
		prevy=y
		If p<0
			x:+1
		Else
			x:+1
			y:-1
		EndIf
		If p<0
			p=p+(x Shl 1)+1
		Else
			p=p+((x-y) Shl 1)+1
		EndIf
		If y<prevy And x<y
			DrawRect xCenter-x,yCenter+y,x Shl 1,1
			DrawRect xCenter-x,yCenter-y,x Shl 1,1
		EndIf
		DrawRect xCenter-y,yCenter+x,y Shl 1,1
		DrawRect xCenter-y,yCenter-x,y Shl 1,1
	Wend

	'Do a second bresenham circle using the points around the circle as reference locations upon which to center a row or column of
	'antialiasing tests. The circle is divided into four quadrants. The left and right quadrants are calculated first and at each circle pixel
	'a number of neighboring pixels in the row are sampled and drawn based on the antialiasing boundary thickness. Then a similar thing
	'is done vertically by adding columns of neighboring pixels to antialiase the top and bottom quadrants.

	'Antialiase Left And Right quadrant edges
	x=0
	y=radius
	APlot(xCenter+y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'2nd pixel (inside)
	APlot(xCenter+y-1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y-1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'3rd pixel (inside further)
	APlot(xCenter+y-2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y-2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'4th pixel (outside)
	APlot(xCenter+y+1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y-1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y+1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y-1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'Top and bottom quadrants (x and y swapped)
	APlot(xCenter+x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
	'2nd pixel
	APlot(xCenter+x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
	'3rd pixel
	APlot(xCenter+x,yCenter+y-2,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y-2,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y+2,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y+2,xCenter,yCenter,Radius,Color,Smoothing)
	'4th pixel
	APlot(xCenter+x,yCenter+y+1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y+1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y-1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y-1,xCenter,yCenter,Radius,Color,Smoothing)

	p=1-radius
	While x<=y   'x<y-1 normally, but need to plot one or two extra pixels at the diagonal intersections between quadrants otherwise we get a `nip` out of the edge where antiliasing did not occur - this actually causes a kind of overdraw of some pixels but leaves a gap otherwise
		prevy=y
		If p<0
			x:+1
		Else
			x:+1
			y:-1
		EndIf
		If p<0
			p=p+(x Shl 1)+1
		Else
			p=p+((x-y) Shl 1)+1
		EndIf
	
		'Left and right quadrants
		APlot(xCenter+y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		'2nd pixel
		APlot(xCenter+y-1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y-1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		'3rd pixel
		APlot(xCenter+y-2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y-2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		'4th pixel
		APlot(xCenter+y+1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y-1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y+1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y-1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)

		'Top and bottom quadrants (x and y swapped)
		APlot(xCenter+x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
		'2nd pixel
		APlot(xCenter+x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
		'3rd pixel
		APlot(xCenter+x,yCenter+y-2,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y-2,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y+2,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y+2,xCenter,yCenter,Radius,Color,Smoothing)
		'4th pixel
		APlot(xCenter+x,yCenter+y+1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y+1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y-1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y-1,xCenter,yCenter,Radius,Color,Smoothing)
	Wend

End Function

Function SmoothCircle5(xCenter:Int,yCenter:Int,Radius:Int,Color:Int=$FFFFFFFF,Smoothing:Float=1.0)
	'Draw an antialiased circle, centered at xCenter,yCenter, with Radius in Color, and an antialiased border of Smoothing size
	'Smoothing can be from 0 to 5.0

	'Fill the circle interior using bresenham circle algorithm, no antialiasing, one row at a time, all integer math
	SetColor Color Shr 24,(Color & $FF0000) Shr 16,(Color & $FF00) Shr 8
	Local p:Int
	Local x:Int
	Local y:Int
	Local prevy:Int
	x=0
	y=radius
	DrawRect xCenter-y,yCenter+x,y Shl 1,1
	p=1-radius
	While x<y-1
		prevy=y
		If p<0
			x:+1
		Else
			x:+1
			y:-1
		EndIf
		If p<0
			p=p+(x Shl 1)+1
		Else
			p=p+((x-y) Shl 1)+1
		EndIf
		If y<prevy And x<y
			DrawRect xCenter-x,yCenter+y,x Shl 1,1
			DrawRect xCenter-x,yCenter-y,x Shl 1,1
		EndIf
		DrawRect xCenter-y,yCenter+x,y Shl 1,1
		DrawRect xCenter-y,yCenter-x,y Shl 1,1
	Wend

	'Do a second bresenham circle using the points around the circle as reference locations upon which to center a row or column of
	'antialiasing tests. The circle is divided into four quadrants. The left and right quadrants are calculated first and at each circle pixel
	'a number of neighboring pixels in the row are sampled and drawn based on the antialiasing boundary thickness. Then a similar thing
	'is done vertically by adding columns of neighboring pixels to antialiase the top and bottom quadrants.

	'Antialiase Left And Right quadrant edges
	x=0
	y=radius
	APlot(xCenter+y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'2nd pixel (inside)
	APlot(xCenter+y-1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y-1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'3rd pixel (inside further)
	APlot(xCenter+y-2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y-2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'4th pixel (outside)
	APlot(xCenter+y+1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y-1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y+1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y-1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'5th pixel (inside further still)
	APlot(xCenter+y-3,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+3,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y-3,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y+3,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'6th pixel (outside further)
	APlot(xCenter+y+2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y-2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+y+2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-y-2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
	'Top and bottom quadrants (x and y swapped)
	APlot(xCenter+x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
	'2nd pixel
	APlot(xCenter+x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
	'3rd pixel
	APlot(xCenter+x,yCenter+y-2,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y-2,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y+2,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y+2,xCenter,yCenter,Radius,Color,Smoothing)
	'4th pixel
	APlot(xCenter+x,yCenter+y+1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y+1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y-1,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y-1,xCenter,yCenter,Radius,Color,Smoothing)
	'5th pixel
	APlot(xCenter+x,yCenter+y-3,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y-3,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y+3,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y+3,xCenter,yCenter,Radius,Color,Smoothing)
	'6th pixel
	APlot(xCenter+x,yCenter+y+2,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter+y+2,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter+x,yCenter-y-2,xCenter,yCenter,Radius,Color,Smoothing)
	APlot(xCenter-x,yCenter-y-2,xCenter,yCenter,Radius,Color,Smoothing)
	p=1-radius
	While x<=y   'x<y-1 normally, but need to plot one or two extra pixels at the diagonal intersections between quadrants otherwise we get a `nip` out of the edge where antiliasing did not occur - this actually causes a kind of overdraw of some pixels but leaves a gap otherwise
		prevy=y
		If p<0
			x:+1
		Else
			x:+1
			y:-1
		EndIf
		If p<0
			p=p+(x Shl 1)+1
		Else
			p=p+((x-y) Shl 1)+1
		EndIf
	
		'Left and right quadrants
		APlot(xCenter+y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		'2nd pixel
		APlot(xCenter+y-1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y-1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		'3rd pixel
		APlot(xCenter+y-2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y-2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		'4th pixel
		APlot(xCenter+y+1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y-1,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y+1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y-1,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		'5th pixel
		APlot(xCenter+y-3,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+3,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y-3,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y+3,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		'6th pixel
		APlot(xCenter+y+2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y-2,yCenter+x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+y+2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-y-2,yCenter-x,xCenter,yCenter,Radius,Color,Smoothing)
		'Top and bottom quadrants (x and y swapped)
		APlot(xCenter+x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y,xCenter,yCenter,Radius,Color,Smoothing)
		'2nd pixel
		APlot(xCenter+x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y-1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y+1,xCenter,yCenter,Radius,Color,Smoothing)
		'3rd pixel
		APlot(xCenter+x,yCenter+y-2,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y-2,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y+2,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y+2,xCenter,yCenter,Radius,Color,Smoothing)
		'4th pixel
		APlot(xCenter+x,yCenter+y+1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y+1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y-1,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y-1,xCenter,yCenter,Radius,Color,Smoothing)
		'5th pixel
		APlot(xCenter+x,yCenter+y-3,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y-3,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y+3,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y+3,xCenter,yCenter,Radius,Color,Smoothing)
		'6th pixel
		APlot(xCenter+x,yCenter+y+2,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter+y+2,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter+x,yCenter-y-2,xCenter,yCenter,Radius,Color,Smoothing)
		APlot(xCenter-x,yCenter-y-2,xCenter,yCenter,Radius,Color,Smoothing)
	Wend

End Function

Function APlot(x:Int,y:Int,xMid:Int,yMid:Int,Radius:Int,Color:Int=$FFFFFFFF,Smoothing:Float=1.0)
	'Antialiase a point relative to the edge of a circle
	'x,y is location of point to test and draw based on distance from the circle boundary
	'xMid,yMid is the center of the circle
	'Radius is the radius of the circle
	'Color is the color to render in, RGBA, A is ignored
	'Smoothing is the thickness of the antialiasing band around the circle (1.0 is normal)
	Local Val:Float
	Local dX:Float=xMid-x				'Horizontal distance
	Local dY:Float=yMid-y				'Vertical distance
	Local Distance:Float=Sqr((dX*dX)+(dY*dY))	'Find diagonal distance, always absolute
	Distance:-(Smoothing/2.0)
	Val=((Radius-Distance)/Smoothing)
	Val=Max(-1.0,Min(1.0,Val))
	SetColor (Val*(Color Shr 24)),(Val*((Color & $FF0000) Shr 16)),(Val*((Color & $FF00) Shr 8))
	Plot x,y
End Function
