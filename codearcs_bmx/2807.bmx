; ID: 2807
; Author: Warpy
; Date: 2011-01-10 16:37:55
; Title: Draw evenly spaced dots along a line
; Description: Draw dots along a line, or line segments, spaced evenly. Two methods

Strict

'this method will draw each line segment individually
'it will change the gap slightly so that the dots are spread evenly along each segment
'it always draws a dot on each corner and endpoint
Function drawdots1(points#[], gap#)
	If Len(points)<4 Return	'need at least two pairs of co-ordinates
	
	Local sx#,sy#,ex#,ey#
	Local dx#,dy#,d#,steps,tgap#
	Local x#,y#
	
	DrawDot points[0],points[1]	'draw a dot at the start of the line
	
	For Local i=0 To Len(points)-3 Step 2		'draw each line segment
	
		sx=points[i]			'get start and end of this segment
		sy=points[i+1]
		ex=points[i+2]
		ey=points[i+3]
				
		dx# = ex-sx			'calculate a vector in the direction of the line, so we can get the distance
		dy# = ey-sy
		d# = Sqr(dx*dx+dy*dy)
		
		steps = round(d/gap)	'work out how many dots to draw (distance between the points, divide by the desired gap size). Round to nearest whole number
		tgap# = d/steps		'work out what the gap needs to be to space the calculated number of needed dots evenly
		
		dx:*tgap/d			'work out the vector between each dot
		dy:*tgap/d
		
		For Local j=1 To steps
			x# = sx+j*dx	'calculate the dot's position by adding the gap vector to the position of the start of the line
			y# = sy+j*dy
			DrawDot x,y	'draw a dot
		Next
	Next
End Function

'this method will draw dots evenly spaced along the whole set of lines
'it might not draw dots on the corners.
Function drawdots2(points#[],gap#)
	If Len(points)<4 Return	'need at least two pairs of co-ordinates

	Local sx#,sy#,ex#,ey#
	Local dx#,dy#,d#,tstep#
	Local x#,y#
	
	Local t#=0	't will keep track of how far along each line segment we are - 0 for at the start, 1 for at the end
	
	For Local i=0 To Len(points)-3 Step 2	'go through the line segments
	
		sx=points[i]			'get start and end of this segment
		sy=points[i+1]
		ex=points[i+2]
		ey=points[i+3]
	
		dx = ex-sx			'work out a vector in the direction of the line
		dy = ey-sy
		d = Sqr(dx*dx+dy*dy)	'work out the length of the line
		
		tstep# = gap/d		'work out what fraction of the line each gap represents
		
		While t<1		'draw dots until we reach the end of the line
		
			x = sx+dx*t	'work out the position of the dot by multiplying the vector by t
			y = sy+dy*t
			DrawDot x,y	'draw the dot
			t:+tstep		'increase t by the amount corresponding to a gap
		Wend
		
		t:-1				'when we reach the end of the line, t might be more than 1, meaning we didn't manage to get a whole gap in at the end
						'subtract 1 from t, and carry over the remainder to the next line segment
	Next
	
	DrawDot ex,ey			'draw the end point of the line. 
End Function

Function DrawDot(x#,y#)
	DrawOval x-2,y-2,4,4
End Function

Function Round(f#)	'round a floating point number to the nearest whole number
	Local i = Floor(f)
	If f-i>=.5
		Return i+1
	Else
		Return i
	EndIf
End Function


Graphics 800,600,0
Local points#[]
Local mode=0
Local gapsize#=20

While Not (KeyHit(KEY_ESCAPE) Or AppTerminate())
	DrawText "Click the mouse!",0,0
	DrawText "Right-click to change methods",0,15
	DrawText "Press up/down to change gap size",0,30

	If MouseHit(1)
		points :+ [Float(MouseX()),Float(MouseY())]
		If Len(points)>16
			points=points[2..]
		EndIf
	EndIf
	
	If MouseHit(2)
		mode=1-mode
	EndIf
	
	gapsize :+ (KeyDown(KEY_UP)-KeyDown(KEY_DOWN))*.25
	If gapsize<1 gapsize=1
	DrawText "Gap Size: "+gapsize,400,15
	
	If mode=0
		drawdots1(points,gapsize)
		DrawText "Using Method 1",400,0
	Else
		drawdots2(points,gapsize)
		DrawText "Using Method 2",400,0
	EndIf
	Flip
	Cls
Wend
