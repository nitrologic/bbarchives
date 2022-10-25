; ID: 1221
; Author: Nilium
; Date: 2004-12-05 13:55:02
; Title: 3D math functions
; Description: Various 3D math functions I wrote for Vein R3

;#Region DESCRIPTION
	;; Math functions, Vector class taken from the Anima engine (written by me)
;#End Region

;#Region CLASSES
	Type Vector
		Field X#
		Field Y#
		Field Z#
	End Type
	
	Type Size
		Field Width#
		Field Height#
		Field Depth#
	End Type
	
	Type Cube
		Field Position.Vector
		Field Size.Size
	End Type
	
	Type Rectangle
		Field X,Y,Width,Height
		Field Onscreen
	End Type
;#End Region

;#Region PROCEDURES
	Function Size.Size(Width#=0,Height#=0,Depth#=0)
		Local s.Size = New Size
		s\Width = Width
		s\Height = Height
		s\Depth = Depth
		Return s
	End Function
	
	Function Vector.Vector(X#=0,Y#=0,Z#=0)
		Local v.Vector = New Vector
		v\X = X
		v\Y = Y
		v\Z = Z
		Return v
	End Function
	
	Function VectorAdd(a.Vector,b.Vector)
		a\X =a\X + b\X
		a\Y =a\Y + b\Y
		a\Z =a\Z + b\Z
	End Function
	
	Function VectorSubtract(a.Vector,b.Vector)
		a\X =a\X - b\X
		a\Y =a\Y - b\Y
		a\Z =a\Z - b\Z
	End Function
	
	Function VectorSum.Vector(a.Vector,b.Vector)
		Return Vector(a\X+b\X,a\Y+b\Y,a\Z+b\Z)
	End Function
	
	Function VectorDifference.Vector(a.Vector,b.Vector)
		Return Vector(a\X-b\X,a\Y-b\Y,a\Z-b\Z)
	End Function
	
	Function VectorMultiply(a.Vector,b.Vector)
		a\X =a\X * b\X
		a\Y =a\Y * b\Y
		a\Z =a\Z * b\Z
	End Function
	
	Function VectorDivide(a.Vector,b.Vector)
		a\X =a\X / b\X
		a\Y =a\Y / b\Y
		a\Z =a\Z / b\Z
	End Function
	
	Function VectorProduct.Vector(a.Vector,b.Vector)
		Return Vector(a\X*b\X,a\Y*b\Y,a\Z*b\Z)
	End Function
	
	Function VectorQuotient.Vector(a.Vector,b.Vector)
		Return Vector(a\X/b\X,a\Y/b\Y,a\Z/b\Z)
	End Function
	
	Function VectorCross.Vector(a.Vector,b.Vector)
		Return Vector( (a\Y*b\Z)-(a\Z*b\Y), (a\Z*b\X)-(a\X*b\Z), (a\X*b\Y)-(a\Y*b\X) )
	End Function
	
	Function VectorDot#(a.Vector,b.Vector)
		Return (a\X*b\X) + (a\Y*b\Y) + (a\Z*b\Z)
	End Function
	
	Function VectorAngle#(a.Vector,b.Vector)
		Local d# = VectorDot(a,b)
		Local m# = VectorMagnitude(a)*VectorMagnitude(b)
		Return ACos(d#/m#)
	End Function
	
	Function VectorNormalize(a.Vector)
		Local m# = VectorMagnitude(a)
		a\X = a\X / m#
		a\Y = a\Y / m#
		a\Z = a\Z / m#
	End Function
	
	Function VectorMagnitude#(a.Vector)
		Return Sqr(a\X*a\X + a\Y*a\Y + a\Z*a\Z)
	End Function
	
	Function VectorScale(a.Vector,b#)
		a\X = a\X * b#
		a\Y = a\Y * b#
		a\Z = a\Z * b#
	End Function
	
	Function VectorSDivide(a.Vector,b#)
		a\X = a\X / b#
		a\Y = a\Y / b#
		a\Z = a\Z / b#
	End Function
	
	Function MinF#(A#,B#)
		If A < B Then Return B
		Return A
	End Function
	
	Function MinI%(A%,B%)
		If A < B Then Return B
		Return A
	End Function
	
	Function MaxF#(A#,B#)
		If A > B Then Return B
		Return A
	End Function
	
	Function MaxI%(A%,B%)
		If A > B Then Return B
		Return A
	End Function
	
	Function ConstrictF#(A#,B#,C#)
		If A > C Then
			While A > C
				A = A - (C-B)
			Wend
		ElseIf A < B Then
			While A < B
				A = A + (C-B)
			Wend
		EndIf
		Return A
	End Function
	
	Function ConstrictI#(A%,B%,C%)
		If A > C Then
			While A > C
				A = A - (C-B)
			Wend
		ElseIf A < B Then
			While A < B
				A = A + (C-B)
			Wend
		EndIf
		Return A
	End Function
	
	Function PointInRect(PX,PY,RX,RY,W,H)
		Return (PX >= RX) And (PY >= RY) And (PX <= RX+W) And (PY <= RY+H)
	End Function
	
	Function NearestPower(N#)
		v# = 1
		While N# > v#
			v# = v# * 2
		Wend
		k# = v# - N#
		Return v/(1 Or (k# > v/4))
	End Function
	
	Function GetCube.Cube(Camera,x#,y#,z#,sx#,sy#,sz#)
		Local MinX=9999,MinY=9999,MaxX=-9999,MaxY=-9999,MaxZ=-9999
		
		Local c = Camera
		
		CameraProject c,x,y,z
		px = ProjectedX()
		py = ProjectedY()
		pz = ProjectedZ()
		
		If px < MinX Then MinX = px
		If py < MinY Then MinY = py
		If px > MaxX Then MaxX = px
		If py > MaxY Then MaxY = py
		If pz > MaxZ Then MaxZ = pz
		
		CameraProject c,sx,y,z
		px = ProjectedX()
		py = ProjectedY()
		pz = ProjectedZ()
		
		If px < MinX Then MinX = px
		If py < MinY Then MinY = py
		If px > MaxX Then MaxX = px
		If py > MaxY Then MaxY = py
		If pz > MaxZ Then MaxZ = pz
		
		CameraProject c,sx,y,sz
		px = ProjectedX()
		py = ProjectedY()
		pz = ProjectedZ()
		
		If px < MinX Then MinX = px
		If py < MinY Then MinY = py
		If px > MaxX Then MaxX = px
		If py > MaxY Then MaxY = py
		If pz > MaxZ Then MaxZ = pz
		
		CameraProject c,x,y,sz
		px = ProjectedX()
		py = ProjectedY()
		pz = ProjectedZ()
		
		If px < MinX Then MinX = px
		If py < MinY Then MinY = py
		If px > MaxX Then MaxX = px
		If py > MaxY Then MaxY = py
		If pz > MaxZ Then MaxZ = pz
		
		CameraProject c,x,sy,z
		px = ProjectedX()
		py = ProjectedY()
		pz = ProjectedZ()
		
		If px < MinX Then MinX = px
		If py < MinY Then MinY = py
		If px > MaxX Then MaxX = px
		If py > MaxY Then MaxY = py
		If pz > MaxZ Then MaxZ = pz
		
		CameraProject c,sx,sy,z
		px = ProjectedX()
		py = ProjectedY()
		pz = ProjectedZ()
		
		If px < MinX Then MinX = px
		If py < MinY Then MinY = py
		If px > MaxX Then MaxX = px
		If py > MaxY Then MaxY = py
		If pz > MaxZ Then MaxZ = pz
		
		CameraProject c,sx,sy,sz
		px = ProjectedX()
		py = ProjectedY()
		pz = ProjectedZ()
		
		If px < MinX Then MinX = px
		If py < MinY Then MinY = py
		If px > MaxX Then MaxX = px
		If py > MaxY Then MaxY = py
		If pz > MaxZ Then MaxZ = pz
		
		CameraProject c,x,sy,sz
		px = ProjectedX()
		py = ProjectedY()
		pz = ProjectedZ()
		
		If px < MinX Then MinX = px
		If py < MinY Then MinY = py
		If px > MaxX Then MaxX = px
		If py > MaxY Then MaxY = py
		If pz > MaxZ Then MaxZ = pz
		
		i.Cube = New Cube
		i\Position = Vector(MinX,MinY,-1)
		i\Size = Size(MaxX-MinX,MaxY-MinY,MaxZ)
		Return i
	End Function
	
	Function CubeInCamera(Camera,x#,y#,z#,sx#,sy#,sz#)
		Local c = Camera
		Local MinX=9999,MinY=9999,MaxX=-9999,MaxY=-9999
		
		CameraProject c,x,y,z
		px = ProjectedX()
		py = ProjectedY()
		If ProjectedZ() >= 1 Then
			If px < MinX Then MinX = px
			If px > MaxX Then MaxX = px
			If py < MinY Then MinY = py
			If py > MaxY Then MaxY = py
		EndIf
		z = z + ProjectedZ()
		
		CameraProject c,sx,y,z
		px = ProjectedX()
		py = ProjectedY()
		If ProjectedZ() >= 1 Then
			If px < MinX Then MinX = px
			If px > MaxX Then MaxX = px
			If py < MinY Then MinY = py
			If py > MaxY Then MaxY = py
		EndIf
		z = z + ProjectedZ()
		
		CameraProject c,sx,y,sz
		px = ProjectedX()
		py = ProjectedY()
		If ProjectedZ() >= 1 Then
			If px < MinX Then MinX = px
			If px > MaxX Then MaxX = px
			If py < MinY Then MinY = py
			If py > MaxY Then MaxY = py
		EndIf
		z = z + ProjectedZ()
		
		CameraProject c,x,y,sz
		px = ProjectedX()
		py = ProjectedY()
		If ProjectedZ() >= 1 Then
			If px < MinX Then MinX = px
			If px > MaxX Then MaxX = px
			If py < MinY Then MinY = py
			If py > MaxY Then MaxY = py
		EndIf
		z = z + ProjectedZ()
		
		CameraProject c,x,sy,z
		px = ProjectedX()
		py = ProjectedY()
		If ProjectedZ() >= 1 Then
			If px < MinX Then MinX = px
			If px > MaxX Then MaxX = px
			If py < MinY Then MinY = py
			If py > MaxY Then MaxY = py
		EndIf
		z = z + ProjectedZ()
		
		CameraProject c,sx,sy,z
		px = ProjectedX()
		py = ProjectedY()
		If ProjectedZ() >= 1 Then
			If px < MinX Then MinX = px
			If px > MaxX Then MaxX = px
			If py < MinY Then MinY = py
			If py > MaxY Then MaxY = py
		EndIf
		z = z + ProjectedZ()
		
		CameraProject c,sx,sy,sz
		px = ProjectedX()
		py = ProjectedY()
		If ProjectedZ() >= 1 Then
			If px < MinX Then MinX = px
			If px > MaxX Then MaxX = px
			If py < MinY Then MinY = py
			If py > MaxY Then MaxY = py
		EndIf
		z = z + ProjectedZ()
		
		CameraProject c,x,sy,sz
		px = ProjectedX()
		py = ProjectedY()
		If ProjectedZ() >= 1 Then
			If px < MinX Then MinX = px
			If px > MaxX Then MaxX = px
			If py < MinY Then MinY = py
			If py > MaxY Then MaxY = py
		EndIf
		z = z + ProjectedZ()
		Stop
		If z <= 0 Then Return
		
		Return RectsOverlap(0,0,GraphicsWidth(),GraphicsHeight(),MinX,MinY,maxx-minx,maxy-miny)
	End Function
	
	Function GetMeshAABB.Cube(Mesh,glbl=1)
		c.Cube = New Cube
		c\Position = Vector()
		c\Size = Size()
		
		Local min#[3]
		Local max#[3]
		
		For x# = 0 To 3
			min[x] = 65536
			max[x] = -65536
		Next
		
		Local MX = 1
		Local MY = 2
		Local MZ = 3
		
		For surfaces = 1 To CountSurfaces(Mesh)
			s = GetSurface(Mesh,surfaces)
			For i = 0 To CountVertices(s)-1
				x# = VertexX(s,i)
				y# = VertexY(s,i)
				z# = VertexZ(s,i)
				
				If glbl Then
					TFormPoint x,y,z,Mesh,0
					x = TFormedX()
					y = TFormedY()
					z = TFormedZ()
				EndIf
				
				If x < min[MX] Then min[MX] = x
				If x > max[MX] Then max[MX] = x
				
				If y < min[MY] Then min[MY] = y
				If y > max[MY] Then max[MY] = y
				
				If z < min[MZ] Then min[MZ] = z
				If z > max[MZ] Then max[MZ] = z
			Next
		Next
		
		c\Position\x = min[MX]-.1
		c\Position\y = min[MY]-.1
		c\Position\z = min[MZ]-.1
		c\Size\Width = max[MX]-min[MX]+.2
		c\Size\height = max[MY]-min[MY]+.2
		c\Size\depth = max[MZ]-min[MZ]+.2
		Return c
	End Function
	
	Function AABBToScreen.Rectangle(Camera,x#,y#,z#,width#,height#,depth#)
		r.Rectangle = New Rectangle
		
		Local minx=16777215,miny=16777215,maxx=-16777215,maxy=-16777215
		
		CameraProject Camera,x,y,z
		zs = zs + (ProjectedZ() > 0)
		px = ProjectedX()
		py = ProjectedY()
		
		If px > maxx Then maxx = px
		If px < minx Then minx = px
		
		If py > maxy Then maxy = py
		If py < miny Then miny = py
		
		CameraProject Camera,x+width,y,z
		zs = zs + (ProjectedZ() > 0)
		px = ProjectedX()
		py = ProjectedY()
		
		If px > maxx Then maxx = px
		If px < minx Then minx = px
		
		If py > maxy Then maxy = py
		If py < miny Then miny = py
		
		CameraProject Camera,x+width,y,z+depth
		zs = zs + (ProjectedZ() > 0)
		px = ProjectedX()
		py = ProjectedY()
		
		If px > maxx Then maxx = px
		If px < minx Then minx = px
		
		If py > maxy Then maxy = py
		If py < miny Then miny = py
		
		CameraProject Camera,x,y,z+depth
		zs = zs + (ProjectedZ() > 0)
		px = ProjectedX()
		py = ProjectedY()
		
		If px > maxx Then maxx = px
		If px < minx Then minx = px
		
		If py > maxy Then maxy = py
		If py < miny Then miny = py
		
		CameraProject Camera,x,y+height,z
		zs = zs + (ProjectedZ() > 0)
		px = ProjectedX()
		py = ProjectedY()
		
		If px > maxx Then maxx = px
		If px < minx Then minx = px
		
		If py > maxy Then maxy = py
		If py < miny Then miny = py
		
		CameraProject Camera,x+width,y+height,z
		zs = zs + (ProjectedZ() > 0)
		px = ProjectedX()
		py = ProjectedY()
		
		If px > maxx Then maxx = px
		If px < minx Then minx = px
		
		If py > maxy Then maxy = py
		If py < miny Then miny = py
		
		CameraProject Camera,x+width,y+height,z+depth
		zs = zs + (ProjectedZ() > 0)
		px = ProjectedX()
		py = ProjectedY()
		
		If px > maxx Then maxx = px
		If px < minx Then minx = px
		
		If py > maxy Then maxy = py
		If py < miny Then miny = py
		
		CameraProject Camera,x,y+height,z+depth
		zs = zs + (ProjectedZ() > 0)
		px = ProjectedX()
		py = ProjectedY()
		
		If px > maxx Then maxx = px
		If px < minx Then minx = px
		
		If py > maxy Then maxy = py
		If py < miny Then miny = py
		
		r\Onscreen = 1
		r\x = minx
		r\y = miny
		r\width = maxx-minx
		r\height = maxy-miny
		
		Return r
	End Function
	
	Function FreeCube(c.Cube)
		If c = Null Then Return
		Delete c\Size
		Delete c\Position
		Delete c
	End Function
	
	Function PointInCube(x#,y#,z#,c.Cube)
		Return (x >= c\Position\X And x <= c\Position\X+c\Size\Width And y => c\Position\Y And y <= c\Position\Y+c\Size\Height And z => c\Position\Z And z <= c\Position\Z+c\Size\Depth)
	End Function
	
	Function CreateMeshBox(Entity)
		Local MinX#,MinY#,MinZ#
		Local MaxX#,MaxY#,MaxZ#
		For n = 1 To CountSurfaces(Entity)
			s = GetSurface(Entity,n)
			For i = 0 To CountVertices(s)-1
				x# = VertexX(s,i)
				y# = VertexY(s,i)
				z# = VertexZ(s,i)
				
				If x < MinX Then MinX = x
				If x > MaxX Then MaxX = x
				
				If y < MinY Then MinY = y
				If y > MaxY Then MaxY = y
				
				If z < MinZ Then MinZ = z
				If z > MaxZ Then MaxZ = z
			Next
		Next
		EntityBox Entity,MaxX,MaxY,MaxZ,MinX-MaxX,MinY-MaxY,MinZ-MaxZ
	End Function
;#End Region
