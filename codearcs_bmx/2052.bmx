; ID: 2052
; Author: Otus
; Date: 2007-06-28 08:14:30
; Title: Distance to line
; Description: Calculates the distance to line or line segment fast

Function DistanceToLineSegment:Double(ax:Double,ay:Double,..
		bx:Double,by:Double, px:Double,py:Double)
	'Returns the distance from p to 
	'	the closest point on line segment a-b.
	
	Local dx:Double=bx-ax
	Local dy:Double=by-ay
	
	Local t:Double =  ( (py-ay)*dy + (px-ax)*dx ) / (dy*dy + dx*dx)
	
	If t<0
		dx=ax
		dy=ay
	ElseIf t>1
		dx=bx
		dy=by
	Else
		dx = ax+t*dx
		dy = ay+t*dy
	End If
	
	dx:-px
	dy:-py
	
	Return Sqr(dx*dx + dy*dy)
	
End Function

Function DistanceToLine:Double(ax:Double,ay:Double, bx:Double,by:Double, px:Double,py:Double)
	'Returns the distance from p to the closest point
	'	ont the line passing through a and b.
	
	Local dx:Double=bx-ax
	Local dy:Double=by-ay
	
	Return ( (ay-py)*dx + (px-ax)*dy ) / Sqr(dy*dy + dx*dx)
		
End Function

'in.c:

double dx;
double dy;
double t;

double DistanceToLineSegment(double ax, double ay, double bx, double by, double px, double py)
{
	dx = bx-ax;
	dy = by-ay;
	
	t = ( (py-ay)*dy + (px-ax)*dx ) / (dy*dy + dx*dx);
	
	if (t<0) {
		ax-=px;
		ay-=py;
		return sqrt(ax*ax + ay*ay);
	}
	if (t>1) {
		bx-=px;
		by-=py;
		return sqrt(bx*bx + by*by);
	}
	
	ax+=t*dx-px;
	ay+=t*dy-py;
	return sqrt(ax*ax + ay*ay);
}

double DistanceToLine(double ax, double ay, double bx, double by, double px, double py)
{
	dx = bx-ax;
	dy = by-ay;
	
	return ( (ay-py)*dx + (px-ax)*dy ) / sqrt(dy*dy + dx*dx);
}
