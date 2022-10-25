; ID: 3240
; Author: Endive
; Date: 2016-01-09 12:00:42
; Title: 3D Terrain with Hidden Line Removal
; Description: Simple hidden line removal for terrain

Graphics 1000,600
SetBlend solidblend
Type point
Field x#,y#
End Type
Global SCREENWIDTH#=GraphicsWidth()
Global SCREENHEIGHT#=GraphicsHeight()

While Not KeyDown(KEY_ESCAPE)
Cls
ticks = ticks + 1
For zz = 700 To 100 Step -5
	SetColor 255-zz/2,255-zz/2,255-zz/2
	For xx = -500 To 500 Step 5
		drawfilledline3d (xx,(25+Sin(xx*4+ticks*2)*25+(Cos(zz*2+ticks*2))*100) + 200,zz, xx+5,(25+Sin((xx+3)*4+ticks*2)*25+(Cos(zz*2+ticks*2))*100) + 200,zz)
	Next
Next

Flip
Wend

Function drawquad3d(x1#,y1#,z1#, x2#,y2#,z2#, x3#,y3#,z3#, x4#,y4#,z4#)
	drawline3d(x1,y1,z1, x2,y2,z2)
	drawline3d(x2,y2,z2, x3,y3,z3)
	drawline3d(x3,y3,z3, x4,y4,z4)
	drawline3d(x4,y4,z4, x1,y1,z1)
End Function
	

Function drawline3d(x1#,y1#,z1#, x2#,y2#,z2#)
	Local point1:point = map3d(x1,y1,z1)
	Local point2:point = map3d(x2,y2,z2)
	If point1.x>0 Or point2.x > 0
		If point1.x <GraphicsWidth() Or point2.x<GraphicsWidth()
			DrawLine point1.x, point1.y, point2.x, point2.y
		EndIf
	EndIf
End Function

Function drawfilledline3d(x1#,y1#,z1#, x2#,y2#,z2#)
' Draws a polygon to the bottom of the screen first
	Local point1:point = map3d(x1,y1,z1)
	Local point2:point = map3d(x2,y2,z2)
	
	Local xx1#=point1.x
	Local yy1#=point1.y
	Local xx2#=point2.x
	Local yy2#=point2.y
	' First, check to make sure x coords are within screen width extents
	If xx1>0 Or xx2>0
		If xx1<GraphicsWidth() Or xx2 < GraphicsWidth()
			Local quad#[]=[xx1,yy1, xx1,SCREENHEIGHT, xx2,SCREENHEIGHT, xx2,yy2]
			SetColor 0,0,0
			DrawPoly quad
			SetColor 0,255-(z1/2),0
			DrawLine point1.x, point1.y, point2.x, point2.y
		EndIf
	EndIf
End Function


Function plot3d(x#,y#,z#)
	Local plotpoint:point = New point ' holds the 2d point we will plot
	plotpoint = map3d(x,y,z)
	Plot plotpoint.x, plotpoint.y
End Function

Function map3d:point(x#,y#,z#)
Local myx = ((x / z) * 300) + (GraphicsWidth() *.5)
Local myy = ((y / z) * 100) + (GraphicsHeight() *.5)
Local outpoint:point = New point
outpoint.x = myx; outpoint.y = myy
Return outpoint
End Function
