; ID: 2508
; Author: Warner
; Date: 2009-06-14 06:10:11
; Title: Delaunay triangulation
; Description: Delaunay triangulation

;-------------------------------------------------------------------------------------------
	;					types and constants
	;-------------------------------------------------------------------------------------------
	Type Point
		Field x#
		Field y#
	End Type
	
	Type Tri
		Field vv0
		Field vv1
		Field vv2
	End Type

	;Set these as applicable
	Const MaxVertices = 500
	Const MaxTriangles = 1000
	
	Dim Vertex.Point(MaxVertices)
	Dim Triangle.Tri(MaxTriangles)
	
	For i = 0 To MaxVertices
	Vertex.Point(i) = New Point
	Next
	
	For i = 0 To MaxTriangles
	Triangle.Tri(i) = New Tri
	Next

	Global tPoints = 1

	Vertex(1)\x = 0
	Vertex(1)\y = 0
	Vertex(2)\x = 800
	Vertex(2)\y = 0
	Vertex(3)\x = 800
	Vertex(3)\y = 600
	Vertex(4)\x = 0
	Vertex(4)\y = 600
	tPoints = tPoints + 4


	Dim Complete(0)
	Dim Edges#(2, 3)

	;-------------------------------------------------------------------------------------------
	;					graphics setup
	;-------------------------------------------------------------------------------------------

	Graphics 800, 600, 0, 2

	;-------------------------------------------------------------------------------------------
	;					main loop
	;-------------------------------------------------------------------------------------------

	Repeat
	
	If MouseHit(1) Then

		Vertex(tPoints)\x = MouseX()
		Vertex(tPoints)\y = MouseY()

		If tPoints > 2 Then
		    Cls
		    HowMany = Triangulate(tPoints)
		Else
	    	Oval Vertex(tPoints)\x - 10, Vertex(tPoints)\y, 20, 20
		End If

		tPoints = tPoints + 1

		Text 0, 0, "Points: " + tPoints
		Text 0, 20, "Triangles: " + HowMany

		For i = 1 To HowMany
		    Line Vertex(Triangle(i)\vv0)\x, Vertex(Triangle(i)\vv0)\y, Vertex(Triangle(i)\vv1)\x, Vertex(Triangle(i)\vv1)\y
		    Line Vertex(Triangle(i)\vv1)\x, Vertex(Triangle(i)\vv1)\y, Vertex(Triangle(i)\vv2)\x, Vertex(Triangle(i)\vv2)\y
		    Line Vertex(Triangle(i)\vv0)\x, Vertex(Triangle(i)\vv0)\y, Vertex(Triangle(i)\vv2)\x, Vertex(Triangle(i)\vv2)\y
		Next 

	End If
	
	Until KeyHit(1)
	
	End


;------------------------------------------------------------------------------------------------
;										InCircle()
;------------------------------------------------------------------------------------------------
;Return True if the point (xp,yp) lies inside the circumcircle
;made up by points (x1,y1) (x2,y2) (x3,y3)
;The circumcircle centre is returned in (xc,yc) And the radius r
;NOTE: A point on the edge is inside the circumcircle

Function InCircle(xp#, yp#, x1#, y1#, x2#, y2#, x3#, y3#, xc, yc, r) 
		     
		Local eps#
		Local m1#
		Local m2#
		Local mx1#
		Local mx2#
		Local my1#
		Local my2#
		Local dx#
		Local dy#
		Local rsqr#
		Local drsqr#
		
		eps = 0.000001
		
		Result = False
		      
		If Abs(y1 - y2) < eps And Abs(y2 - y3) < eps Then
		    Return
		End If
		
		If Abs(y2 - y1) < eps Then
		    m2 = -(x3 - x2) / (y3 - y2)
		    mx2 = (x2 + x3) / 2
		    my2 = (y2 + y3) / 2
		    xc = (x2 + x1) / 2
		    yc = m2 * (xc - mx2) + my2
		ElseIf Abs(y3 - y2) < eps Then
		    m1 = -(x2 - x1) / (y2 - y1)
		    mx1 = (x1 + x2) / 2
		    my1 = (y1 + y2) / 2
		    xc = (x3 + x2) / 2
		    yc = m1 * (xc - mx1) + my1
		Else
		    m1 = -(x2 - x1) / (y2 - y1)
		    m2 = -(x3 - x2) / (y3 - y2)
		    mx1 = (x1 + x2) / 2
		    mx2 = (x2 + x3) / 2
		    my1 = (y1 + y2) / 2
		    my2 = (y2 + y3) / 2
		    xc = (m1 * mx1 - m2 * mx2 + my2 - my1) / (m1 - m2)
		    yc = m1 * (xc - mx1) + my1
		End If
		      
		dx = x2 - xc
		dy = y2 - yc
		rsqr = dx * dx + dy * dy
		r = Sqr(rsqr)
		dx = xp - xc
		dy = yp - yc
		drsqr = dx * dx + dy * dy
		
		If drsqr <= rsqr Then Result = True
		
		Return Result
		
End Function

;------------------------------------------------------------------------------------------------
;								WhichSide()
;------------------------------------------------------------------------------------------------
;Determines which side of a Line the point (xp,yp) lies.
;The Line goes from (x1,y1) To (x2,y2)
;Returns -1 For a point To the Left
;         0 For a point on the Line
;        +1 For a point To the Right

Function WhichSide(xp#, yp#, x1#, y1#, x2#, y2#)
 
		Local equation#
		
		equation = ((yp - y1) * (x2 - x1)) - ((y2 - y1) * (xp - x1))
		
		If equation > 0 Then
		    Result = -1
		ElseIf equation = 0 Then
		    Result = 0
		Else
		    Result = 1
		End If

Return Result

End Function


;------------------------------------------------------------------------------------------------
;								Triangulate()
;------------------------------------------------------------------------------------------------
;Takes as Input NVERT vertices in arrays Vertex()
;Returned is a list of NTRI triangular faces in the array
;Triangle() These triangles are arranged in clockwise order.

Function Triangulate(nvert)

Dim Complete(MaxTriangles)
Dim Edges#(2, MaxTriangles * 3)

Local Nedge#

;For Super Triangle
Local xmin#
Local xmax#
Local ymin#
Local ymax#
Local xmid#
Local ymid#
Local dx#
Local dy#
Local dmax#

;General Variables
Local i
Local j
Local k
Local ntri
Local xc#
Local yc#
Local r#
Local inc

;Find the maximum And minimum vertex bounds.
;This is To allow calculation of the bounding triangle
xmin = Vertex(1)\x
ymin = Vertex(1)\y
xmax = xmin
ymax = ymin
For i = 2 To nvert
    If Vertex(i)\x < xmin Then xmin = Vertex(i)\x
    If Vertex(i)\x > xmax Then xmax = Vertex(i)\x
    If Vertex(i)\y < ymin Then ymin = Vertex(i)\y
    If Vertex(i)\y > ymax Then ymax = Vertex(i)\y
Next 
dx = xmax - xmin
dy = ymax - ymin
If dx > dy Then
    dmax = dx
Else
    dmax = dy
End If
xmid = (xmax + xmin) / 2
ymid = (ymax + ymin) / 2

;Set up the supertriangle
;This is a triangle which encompasses all the sample points.
;The supertriangle coordinates are added To the End of the
;vertex list. The supertriangle is the First triangle in
;the triangle list.

Vertex(nvert + 1)\x = xmid - 2 * dmax
Vertex(nvert + 1)\y = ymid - dmax
Vertex(nvert + 2)\x = xmid
Vertex(nvert + 2)\y = ymid + 2 * dmax
Vertex(nvert + 3)\x = xmid + 2 * dmax
Vertex(nvert + 3)\y = ymid - dmax
Triangle(1)\vv0 = nvert + 1
Triangle(1)\vv1 = nvert + 2
Triangle(1)\vv2 = nvert + 3
Complete(1) = False
ntri = 1

;Include Each point one at a time into the existing mesh
For i = 1 To nvert
    Nedge = 0
    ;Set up the edge buffer.
    ;If the point (Vertex(i)\x,Vertex(i)\y) lies inside the circumcircle Then the
    ;three edges of that triangle are added To the edge buffer.
    j = 0
    While j < ntri
        j = j + 1
        If Complete(j) <> True Then
            inc = InCircle(Vertex(i)\x, Vertex(i)\y, Vertex(Triangle(j)\vv0)\x, Vertex(Triangle(j)\vv0)\y, Vertex(Triangle(j)\vv1)\x, Vertex(Triangle(j)\vv1)\y, Vertex(Triangle(j)\vv2)\x, Vertex(Triangle(j)\vv2)\y, xc, yc, r)
            ;Include this If points are sorted by X
            ;If (xc + r) < Vertex(i)\x Then
                ;complete(j) = True
            ;Else
            If inc Then
                Edges(1, Nedge + 1) = Triangle(j)\vv0
                Edges(2, Nedge + 1) = Triangle(j)\vv1
                Edges(1, Nedge + 2) = Triangle(j)\vv1
                Edges(2, Nedge + 2) = Triangle(j)\vv2
                Edges(1, Nedge + 3) = Triangle(j)\vv2
                Edges(2, Nedge + 3) = Triangle(j)\vv0
                Nedge = Nedge + 3
                Triangle(j)\vv0 = Triangle(ntri)\vv0
                Triangle(j)\vv1 = Triangle(ntri)\vv1
                Triangle(j)\vv2 = Triangle(ntri)\vv2
                Complete(j) = Complete(ntri)
                j = j - 1
                ntri = ntri - 1
            End If
            ;End If
        End If
    Wend 

    ;Tag multiple edges
    ;Note: If all triangles are specified anticlockwise Then all
    ;interior edges are opposite pointing in direction.
    For j = 1 To Nedge - 1
        If (Not (Edges(1, j) = 0)) And (Not (Edges(2, j) = 0)) Then
            For k = j + 1 To Nedge
                If (Not (Edges(1, k) = 0)) And (Not (Edges(2, k) = 0)) Then
                    If Edges(1, j) = Edges(2, k) Then
                        If Edges(2, j) = Edges(1, k) Then
                            Edges(1, j) = 0
                            Edges(2, j) = 0
                            Edges(1, k) = 0
                            Edges(2, k) = 0
                         End If
                     End If
               End If
             Next 
        End If
    Next 
    
    ;Form New triangles For the current point
    ;Skipping over any tagged edges.
    ;All edges are arranged in clockwise order.
    For j = 1 To Nedge
            If (Not (Edges(1, j) = 0)) And (Not (Edges(2, j) = 0)) Then
                ntri = ntri + 1
                Triangle(ntri)\vv0 = Edges(1, j)
                Triangle(ntri)\vv1 = Edges(2, j)
                Triangle(ntri)\vv2 = i
                Complete(ntri) = False
            End If
    Next
Next

;Remove triangles with supertriangle vertices
;These are triangles which have a vertex number greater than NVERT
i = 0
While i < ntri
    i = i + 1
    If (Triangle(i)\vv0 > nvert) Or (Triangle(i)\vv1 > nvert) Or (Triangle(i)\vv2 > nvert) Then
        Triangle(i)\vv0 = Triangle(ntri)\vv0
        Triangle(i)\vv1 = Triangle(ntri)\vv1
        Triangle(i)\vv2 = Triangle(ntri)\vv2
        i = i - 1
        ntri = ntri - 1
    End If
Wend

Return ntri
End Function
