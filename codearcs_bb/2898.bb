; ID: 2898
; Author: Armitage 1982
; Date: 2011-10-30 12:17:16
; Title: Triangulate (Tesselate) polygon
; Description: Triangulate any polygon contour without holes by Substracting ears algorithm

' This Type Try To triangulate (Tessellation) any polygon contour without calculating holes.
' It's a Polygon triangulation by Substracting ears algorithm.
' I include a fake 2D Vector Class named b2Vec2, please replace it with you own vector type (recommended).
' Entry submitted by John W. Ratcliff mailto:jratcliff@verant.com
' Ported to BlitzMax by Armitage1982 (Michaël Lievens) http://www.arm42.com
' Thanks to the flipcode archives file : http://www.flipcode.com/archives/Efficient_Polygon_Triangulation.shtml
' Original source code of this algorithm is unknown.
' 
' Permission is hereby granted, free of charge, to any person obtaining a copy
' of this software and associated documentation files (the "Software"), to deal
' in the Software without restriction, including without limitation the rights
' to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
' copies of the Software, and to permit persons to whom the Software is
' furnished to do so, subject to the following conditions:
' 
' The above copyright notice and this permission notice shall be included in
' all copies or substantial portions of the Software.
' 
' THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
' IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
' FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
' AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
' LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
' OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
' THE SOFTWARE.
' 
SuperStrict

Rem
bbdoc: Fake 2D Vector Type
End Rem
Type b2Vec2

	Field _x:Float
	Field _y:Float
	
	Function Create:b2Vec2(X:Int, Y:Int)
		
		Local v:b2Vec2 = New b2Vec2
		
		v._x = X
		v._y = Y
		
		Return v
		
	End Function
	
	Method X:Float()
		Return _x
	End Method

	Method Y:Float()
		Return _y
	End Method

End Type

Rem
bbdoc: Convenience function for creating a b2Vec2 object.
End Rem
Function Vec2:b2Vec2(x:Float, y:Float)
	Return New b2Vec2.Create(x, y)
End Function


Rem
bbdoc:Tesselation Type
End Rem
Type TTriangulate

	Const EPSILON:Float = 0.0000000001

	Rem
	bbdoc: compute area of a contour/polygon
	endrem
	Function Area:Float(contour:TList)
		
		Local N:Int = contour.Count()
		Local A:Float = 0.0
		Local P:Int = N - 1
		Local Q:Int = 0
		
		
		While Q < N
			If Q > 0 Then P = Q
			Local B2P:b2Vec2 = b2Vec2(contour.ValueAtIndex(P))
			Local B2Q:b2Vec2 = b2Vec2(contour.ValueAtIndex(Q))
			A:+B2P.X() * B2Q.Y() - B2Q.X() * B2P.Y()
			Q:+1
		Wend
		
		Return A * 0.5
		
	End Function
	
	Rem
	bbdoc: InsideTriangle decides if a point P is Inside of the triangle defined by A, B, C.
	endrem
	Function InsideTriangle:Int(Ax:Float, Ay:Float, Bx:Float, By:Float, Cx:Float, Cy:Float, Px:Float, Py:Float)
		
		Local _ax:Float, _ay:Float, _bx:Float, _by:Float, _cx:Float, _cy:Float, apx:Float, apy:Float, bpx:Float, bpy:Float, cpx:Float, cpy:Float
		Local cCROSSap:Float, bCROSScp:Float, aCROSSbp:Float
		
		_ax = Cx - Bx
		_ay = Cy - By
	  	_bx = Ax - Cx
		_by = Ay - Cy
	  	_cx = Bx - Ax
		_cy = By - Ay
	  	
		apx = Px - Ax
		apy = Py - Ay
	  	bpx = Px - Bx
		bpy = Py - By
	  	cpx = Px - Cx
		cpy = Py - Cy
		
		aCROSSbp = _ax * bpy - _ay * bpx
	  	cCROSSap = _cx * apy - _cy * apx
	  	bCROSScp = _bx * cpy - _by * cpx
	  
		Return ((aCROSSbp >= 0.0) & (bCROSScp >= 0.0) & (cCROSSap >= 0.0))
		
	End Function
	
	Rem
	bbdoc:
	endrem
	Function Snip:Int(contour:TList, u:Int, o:Int, w:Int, n:Int, V:Int[] var)
		
		Local P:Int = 0
		Local Ax:Float, Ay:Float, Bx:Float, By:Float, Cx:Float, Cy:Float, Px:Float, Py:Float
		
		Local B2U:b2Vec2 = b2Vec2(contour.ValueAtIndex(V[u]))
		Local B2O:b2Vec2 = b2Vec2(contour.ValueAtIndex(V[o]))
		Local B2W:b2Vec2 = b2Vec2(contour.ValueAtIndex(V[w]))
		
		Ax = B2U.X()
		Ay = B2U.Y()
		
		Bx = B2O.X()
		By = B2O.Y()
		
		Cx = B2W.X()
		Cy = B2W.Y()
	
		If EPSILON > (((Bx - Ax) * (Cy - Ay)) - ((By - Ay) * (Cx - Ax))) Return False
	
		While P < n
			If ((P = u) | (P = o) | (P = w))
				P:+1
				Continue
			EndIf
			Local B2P:b2Vec2 = b2Vec2(contour.ValueAtIndex(V[P]))
			Px = B2P.X()
			Py = B2P.Y()
			If TTriangulate.InsideTriangle(Ax, Ay, Bx, By, Cx, Cy, Px, Py) Then Return False
			P:+1
		WEnd
	
		Return True
		
	End Function
	
	Rem
	bbdoc: Triangulate a polygon contour, results in a series of triangles.
	about: You can inverse the drawing series of triangles by setting the inverse parameter to 1 or 0. This allow you to choose the polygon construction direction.
	<p>If you set the inverse parameter to -1, the triangulation will try to automatically detect the polygon construction direction like in the original Ratcliff code (not reliable)
	</p>
	endrem
	Function Process:TList(contour:TList, inverse:Int = 0)
		
		' Allocate and initialize list of Vertices in polygon
		Local trisList:TList = CreateList()
		
		Local n:Int = contour.Count()
		If n < 3 Then Return trisList
	
		Local V:Int[n]
		Local o:Int = 0

		' Automatically determine the Polygon construction direction like in the original Ratcliff code (not reliable)
		If inverse = -1
		
			' We want a counter-clockwise polygon in V
			If 0.0 < TTriangulate.Area(contour)
				While o < n
					V[o] = o
					o:+1
				Wend
			Else
				While o < n
					V[o] = (n - 1) - o
					o:+1
				Wend
			EndIf

		Else		' Let you choose the polygon direction creation
			
			If inverse = 1
				While o < n
					V[o] = (n - 1) - o
					o:+1
				Wend
			Else
				While o < n
					V[o] = o
					o:+1
				Wend
			EndIf
					
		EndIf
	
		Local nv:Int = n
		
		' remove nv-2 Vertices, creating 1 triangle every time
		
		Local cnt:Int = 2 * nv	'error detection
		
		o = nv - 1
		While nv > 2
	
			cnt:-1
			' if we loop, it is probably a non-simple polygon
			If cnt <= 0 Then Return trisList	' Triangulate: ERROR - probable bad polygon!
			
			' three consecutive vertices in current polygon, <u,v,w>
			Local u:Int = o
			If (nv <= u) Then u = 0		' previous
			
			o = u + 1
			If (nv <= o) Then o = 0		' new o
			
			Local w:Int = o + 1
			If (nv <= w) Then w = 0		' next
	
			If Snip(contour, u, o, w, nv, V)
	
				Local S:Int, T:Int
				
				' true names of the vertices
				Local B2U:b2Vec2 = b2Vec2(contour.ValueAtIndex(V[u]))
				Local B2O:b2Vec2 = b2Vec2(contour.ValueAtIndex(V[o]))
				Local B2W:b2Vec2 = b2Vec2(contour.ValueAtIndex(V[w]))
				
				' output Triangle
				Local tris:tri = tri.Create(B2U, B2O, B2W)
				
				trisList.AddLast(tris)
				
				' Remove v from remaining polygon
				S = o
				T = o + 1
				While T < nv
					V[S] = V[T]
					S:+1
					T:+1
				Wend
	
				nv:-1
				
				' Reset error detection counter
				cnt = 2 * nv
	
			End If
			
		Wend
	
		V = Null
			
		Return trisList
	
	End Function

End Type
	
Type tri

	Field p1:b2Vec2, p2:b2Vec2, p3:b2Vec2
	
	Function Create:tri(p1:b2Vec2, p2:b2Vec2, p3:b2Vec2)
		Local t:tri=New tri
		t.p1=p1
		t.p2=p2
		t.p3=p3
		Return t
	End Function
	
	Method draw()
		Local poly:Float[] 
		poly = [p1.X(), p1.Y(), p2.X(), p2.Y(), p3.X(), p3.Y()]
		DrawPoly poly
	End Method
	
End Type


'Demo - Left click to place points, Right click to reset, press Space key to invert Polygon, Enter to Autodetect Polygon direction
'SetGraphicsDriver GLMax2DDriver()

Graphics(800, 600, 0)
SetBlend(ALPHABLEND)

Local points:TList = New TList
Local oldPoint:b2Vec2 = Null

Local TrisList:TList = CreateList()
Local inverse:Int = 0

While Not (KeyHit(KEY_ESCAPE) Or AppTerminate())
	
	SetColor(255, 255, 255)
	DrawText("LEFT click to add vertex, RIGHT click to reset", 0.0, 0.0)

	If inverse = 0
		DrawText("Press SPACE key to draw Polygon : Clockwise", 0.0, 16.0)
	ElseIf inverse = 1
		DrawText("Press SPACE key to draw Polygon : Counter-clockwise", 0.0, 16.0)
	Else
		DrawText("Press SPACE key to draw Polygon : Autodetection (add vertex to change result)", 0.0, 16.0)
	End If
	
	DrawText("Press ENTER to Autodetect Polygon direction (not reliable)", 0.0, 32.0)
	
	If MouseHit(1)
		points.AddLast(Vec2(MouseX(), MouseY()))
		' Tessalate polygon
		TrisList = TTriangulate.Process(points, inverse)
	End If
	
	If MouseHit(2)
		points = New TList
		TrisList = CreateList()
	End If

	If KeyHit(KEY_SPACE)

		If inverse = 1
			inverse = 0
		Else
			inverse = 1
		End If
		
		' Tessalate polygon
		TrisList = TTriangulate.Process(points, inverse)
		
	EndIf
	
	If KeyHit(KEY_ENTER)
	
		inverse = -1
		
		' Tessalate polygon with autodetection
		TrisList = TTriangulate.Process(points, inverse)
		
	End If
	
	For Local P:b2Vec2 = EachIn points

		SetColor(255, 255, 255)
		If oldPoint Then DrawLine(oldPoint.X(), oldPoint.Y(), P.X(), P.Y())
		SetColor(255, 0, 0)
			
		' Draw vertex
		SetColor(255, 0, 0)
		SetHandle(2.0, 2.0)
		DrawOval(P.X(), P.Y(), 4.0, 4.0)
		SetHandle(0.0, 0.0)
		
		oldPoint = P
		
	Next
	
	For Local T:tri = EachIn TrisList
	
		' Draw polygon
		SetColor(128, 128, 128)
		SetAlpha(0.7)
			T.draw()
		SetAlpha(1.0)
		
		SetColor(200, 200, 200)
		SetAlpha(0.85)
		DrawLine(T.p1.X(), T.p1.Y(), T.p2.X(), T.p2.Y())
		DrawLine(T.p2.X(), T.p2.Y(), T.p3.X(), T.p3.Y())
		DrawLine(T.p1.X(), T.p1.Y(), T.p3.X(), T.p3.Y())
		SetAlpha(1.0)
		
	Next
		
	DebugLog TrisList.Count()
			
	Flip()
	
	WaitEvent()
	
	Cls()
	
Wend
