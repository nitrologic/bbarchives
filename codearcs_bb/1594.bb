; ID: 1594
; Author: Markus Rosse
; Date: 2006-01-13 13:39:22
; Title: LineRectIntersection
; Description: This Function determine if a Line and a Rectangle intersect

Global LineRectIntersectionX#,LineRectIntersectionY# ; Important!
;----------------------------------------------------------------------------------------------------
; This Function determine if a Line and a Rectangle intersect. If there is a intersection, the
; Function returns TRUE, otherwise FALSE. The Coordinates of the first Intersection of the Line
; are returned in the global Variables LineRectIntersectionX# and LineRectIntersectionY#.
;----------------------------------------------------------------------------------------------------
Function LineRectIntersection(LineX1#,LineY1#,LineX2#,LineY2#,RectX#,RectY#,RectWidth#,RectHeight#)
Local b#,m#,x#,Intersection
If LineY1#=LineY2#
	If LineX1#<LineX2#
		For x#=LineX1# To LineX2#
			If x#>=RectX# And x#<=RectX#+RectWidth# And LineY1#>=RectY# And LineY1#<=RectY#+RectHeight#
				LineRectIntersectionX#=x#
				LineRectIntersectionY#=y#
				Intersection=True
			End If
		Next
	Else
		For x#=LineX1# To LineX2# Step -1
			If x#>=RectX# And x#<=RectX#+RectWidth# And LineY1#>=RectY# And LineY1#<=RectY#+RectHeight#
				LineRectIntersectionX#=x#
				LineRectIntersectionY#=y#
				Intersection=True
			End If
		Next
	End If
ElseIf LineX1#=LineX2#
	If LineY1#<LineY2#
		For y#=LineY1# To LineY2#
			If LineX1#>=RectX# And LineX1#<=RectX#+RectWidth# And y#>=RectY# And y#<=RectY#+RectHeight#
				LineRectIntersectionX#=x#
				LineRectIntersectionY#=y#
				Intersection=True
			End If
		Next
	Else
		For y#=LineY1# To LineY2# Step -1
			If LineX1#>=RectX# And LineX1#<=RectX#+RectWidth# And y#>=RectY# And y#<=RectY#+RectHeight#
				LineRectIntersectionX#=x#
				LineRectIntersectionY#=y#
				Intersection=True
			End If
		Next
	End If
Else
	m#=(LineY1#-LineY2#)/(LineX1#-LineX2#)
	b#=LineY1#-(m#*LineX1)
	If LineX1#<LineX2#
		For x#=LineX1# To LineX2#
			y#=(m#*x#)+b#
			If x#>=RectX# And x#<=RectX#+RectWidth# And y#>=RectY# And y#<=RectY#+RectHeight#
				LineRectIntersectionX#=x#
				LineRectIntersectionY#=y#
				Intersection=True
			End If
		Next
	Else
		For x#=LineX1# To LineX2# Step -1
			y#=(m#*x#)+b#
			If x#>=RectX# And x#<=RectX#+RectWidth# And y#>=RectY# And y#<=RectY#+RectHeight#
				LineRectIntersectionX#=x#
				LineRectIntersectionY#=y#
				Intersection=True
			End If
		Next
	End If
	If Not Intersection
		If LineY1#<LineY2#
			For y#=LineY1# To LineY2#
				x#=(y#-b#)/m#
				If x#>=RectX# And x#<=RectX#+RectWidth# And y#>=RectY# And y#<=RectY#+RectHeight#
					LineRectIntersectionX#=x#
					LineRectIntersectionY#=y#
					Intersection=True
				End If
			Next
		Else
			For y#=LineY1# To LineY2# Step -1
				x#=(y#-b#)/m#
				If x#>=RectX# And x#<=RectX#+RectWidth# And y#>=RectY# And y#<=RectY#+RectHeight#
					LineRectIntersectionX#=x#
					LineRectIntersectionY#=y#
					Intersection=True
				End If
			Next
		End If
	End If
End If
Return Intersection
End Function
