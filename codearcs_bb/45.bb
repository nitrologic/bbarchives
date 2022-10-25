; ID: 45
; Author: skidracer
; Date: 2001-09-18 06:56:19
; Title: InsidePoly
; Description: check if a point is inside a polygon

Function dot(x0,y0,x1,y1,x2,y2)
	Return (x1-x0)*(y2-y1)-(x2-x1)*(y1-y0)
End Function

Function InsideQuad(px,py,x0,y0,x1,y1,x2,y2,x3,y3)
	If dot(x0,y0,x1,y1,px,py)>0
		If dot(x1,y1,x2,y2,px,py)>0
			If dot(x2,y2,x3,y3,px,py)>0
				If dot(x3,y3,x0,y0,px,py)>0
					Return True
				EndIf
			EndIf
		EndIf
	EndIf
End Function

