; ID: 2463
; Author: _JIM
; Date: 2009-04-16 12:32:59
; Title: Point inside poly
; Description: Find out if a point is inside a polygon in a really fast way

'Finds out if a point is on the left or right side of a segment
Function LineIntersectOX(lx1:Float, ly1:Float, lx2:Float, ly2:Float, px:Float, py:Float)
	Local newpx:Float
	
	If ((ly1 < py) And (ly2 > py) ..
		Or (ly1 > py) And (ly2 < py)) And (lx1 > px) And (lx2 > px)
		Return True
	End If
	
	Local b = ((ly1 <= py) And (ly2 >= py)) Or ((ly1 >= py) And (ly2 <= py))
	If (b = False)
		Return False
	EndIf
	
	'Thanks to Nate the Great
	If ly2 = ly1 Then
		Return False
	Else	
		newpx = ((py - ly1) / (ly2 - ly1)) * (lx2 - lx1) + lx1
		If (px < newpx) Return True
	EndIf
	Return False
End Function

'finds out if a point is inside a polygon
Function PointInPoly(px:Float, py:Float, poly:Float[])
	Local i:Int
	Local sum:Int

	For i = 0 To Int(poly.length / 2) - 2
		Local p1x:Float = poly[i * 2]
		Local p1y:Float = poly[i * 2 + 1]
		Local p2x:Float = poly[(i + 1) * 2]
		Local p2y:Float = poly[(i + 1) * 2 + 1]
		
		sum:+LineIntersectOX(p1x, p1y, p2x, p2y, px, py)
	Next
	
	If (poly.length > 4)
		Local p1xd:Float = poly[0]
		Local p1yd:Float = poly[1]
		Local p2xd:Float = poly[poly.length - 2]
		Local p2yd:Float = poly[poly.length - 1]
		
		sum:+LineIntersectOX(p1xd, p1yd, p2xd, p2yd, px, py)
	End If
	
	Return sum Mod 2
End Function
