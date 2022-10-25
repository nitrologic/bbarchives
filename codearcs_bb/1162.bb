; ID: 1162
; Author: TomToad
; Date: 2004-09-21 14:58:10
; Title: minimum distance from point to line
; Description: find the distance from a point to a line in 2D

Function PointToPointDist#(x1#,y1#,x2#,y2#)

dx# = x1-x2
dy# = y1-y2

Return Sqr(dx*dx + dy*dy)
End Function

Function MinDistPointLine#(px#,py#,x1#,y1#,x2#,y2#)

If x1 = x2 And y1 = y2 Then Return PointToPointDist(px,py,x1,y1)

sx# = x2-x1
sy# = y2-y1

q# = ((px-x1) * (x2-x1) + (py - y1) * (y2-y1)) / (sx*sx + sy*sy)

If q < 0.0 Then q = 0.0
If q > 1.0 Then q = 1.0

Return PointToPointDist(px,py,(1-q)*x1+q*x2,(1-q)*y1 + q*y2)
End Function
