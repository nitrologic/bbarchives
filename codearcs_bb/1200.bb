; ID: 1200
; Author: TomToad
; Date: 2004-11-16 20:10:23
; Title: FillTriangle
; Description: Draws a filled triangle to the 2D screen

Function FillTriangle(x1#,y1#,x2#,y2#,x3#,y3#)

Local slope1#,slope2#,slope3#,x#,y#,length#

If x2 < x1 ;make sure the triangle coordinates are ordered so that x1 < x2 < x3
 x = x2
 y = y2
 x2 = x1
 y2 = y1
 x1 = x
 y1 = y
End If
If x3 < x1
 x = x3
 y = y3
 x3 = x1
 y3 = y1
 x1 = x
 y1 = y

End If
If x3 < x2
 x = x3
 y = y3
 x3 = x2
 y3 = y2
 x2 = x
 y2 = y
End If

If x1 <> x3 Then slope1 = (y3-y1)/(x3-x1)
length = x2 - x1

If length <> 0 ;draw the first half of the triangle
slope2 = (y2-y1)/(x2-x1)
For x = 0 To length
 Line x+x1,x*slope1+y1,x+x1,x*slope2+y1
Next
End If

y = length*slope1+y1
length = x3-x2

If length <> 0 ;draw the second half
slope3 = (y3-y2)/(x3-x2)
For x = 0 To length
 Line x+x2,x*slope1+y,x+x2,x*slope3+y2
Next
End If

Return
End Function
