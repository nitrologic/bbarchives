; ID: 374
; Author: matt!
; Date: 2002-07-29 08:34:09
; Title: Tree of Pythagoras
; Description: Tree of Pythagoras Blitz rewrite

;Tree of Pythagoras
;
;Blitz rewrite by Matt Sephton (matt@ewtoo.org)
;
;based on Wouter van Oortmerssen's (http://wouter.fov120.com)
;SHEEP example (http://www.osnews.com/story.php?news_id=169)
;based on an old E example by Raymond Hoving

Function pythtree(ax, ay, bx, by, depth)
	cx = ax-ay+by
	cy = ax+ay-bx
	dx = bx+by-ay
	dy = ax-bx+by
	ex = 0.5*(cx-cy+dx+dy)
	ey = 0.5*(cx+cy-dx+dy)
	c = depth * 22	;c = -1-depth*$100020
	colour_line(cx, cy, ax, ay, c)
	colour_line(ax, ay, bx, by, c)
	colour_line(bx, by, dx, dy, c)
	colour_line(dx, dy, cx, cy, c)
	colour_line(cx, cy, ex, ey, c)
	colour_line(ex, ey, dx, dy, c)
	If depth < 12 Then
		pythtree(cx, cy, ex, ey, depth+1)
		pythtree(ex, ey, dx, dy, depth+1)
	EndIf
End Function

Function colour_line(x,y,dx,dy,c)
	Color c,255-c,96+c
	Line x, y, dx, dy
End Function

width = 640
height = 480

AppTitle "Pythagoras Tree"
Graphics width, height, 16, 2

pythtree(width/2-width/12, height-20, width/2+width/12, height-20, 0)

While Not KeyDown(1)
Wend
End
