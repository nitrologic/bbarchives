; ID: 2128
; Author: Leon Drake
; Date: 2007-10-27 00:54:22
; Title: Rectsoverlap
; Description: rects overlap for Bmax

Function RectsOverlap(x1#,y1#,w1#,h1#,x2#,y2#,w2#,h2#)

If x1#+w1# >= x2# And x1# <= x2#+w2# And y1#+h1# >= y2# And y1# <= y2#+h2# Then
Return True
Else
Return False
EndIf

End Function
