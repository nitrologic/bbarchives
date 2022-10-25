; ID: 1081
; Author: Mr Snidesmin
; Date: 2004-06-08 02:08:25
; Title: Isometric Terrain Loader
; Description: loads a terrain from a bitmap but uses isometric tiles

Type Bounds
	Field x#
	Field y#
	Field z#

	Field dx#
	Field dy#
	Field dz#
End Type

Function MakeIsoGrid%(size%, scale=1.0)
	m% = CreateMesh()
	s% = CreateSurface(m)
		
	size = size/2
	
	oristart% = 1
	For x% = -size To +size
		ori = oristart
		oristart = -oristart
		For y% = -size*0.6 To +size*0.6
			ori = -ori
			If ori < 0 Then 
				offy# = 0
			Else
				offy# = -Scale*Sin(30)
			End If
			
			
			x0# = 0.5*Tan(60)*x*scale+px + scale * Cos(0*120+90*ori)
			y0# = offy + y*scale*(1+Sin(30))+py + scale * Sin(0*120+90*ori)
				
			x1# = 0.5*Tan(60)*x*scale+px + scale * Cos(2*120+90*ori)
			y1# = offy + y*scale*(1+Sin(30))+py + scale * Sin(1*120+90*ori)
		
			x2# = 0.5*Tan(60)*x*scale+px + scale * Cos(1*120+90*ori)
			y2# = offy + y*scale*(1+Sin(30))+py + scale * Sin(2*120+90*ori)

			InsertTriangleXZ s, x0, y0, x1, y1, x2, y2, scale/12
		Next
	Next
	Return m
End Function


Function DrawIsoGrid(size%, px%, py%, scale#=10.0)
	size = size/2
	oristart% = 1
	For x% = -size To +size
		ori = oristart
		oristart = -oristart
		For y% = -size*0.6 To +size*0.6
			ori = -ori
			If ori < 0 Then 
				offy# = 0
			Else
				offy# = -Scale*Sin(30)
			End If
			For i% = 0 To 2
				x0# = 0.5*Tan(60)*x*scale+px + scale * Cos(i*120+90*ori)
				y0# = offy + y*scale*(1+Sin(30))+py + scale * Sin(i*120+90*ori)
				
				x1# = 0.5*Tan(60)*x*scale+px + scale * Cos((i+1)*120+90*ori)
				y1# = offy + y*scale*(1+Sin(30))+py + scale * Sin((i+1)*120+90*ori)
		
				Line x0, y0, x1, y1
			Next
		Next
	Next
End Function

Function LoadIsoTerrain%(sBMP$, size%=100)
	img%=LoadImage(sBMP)
	m = MakeIsoGrid(size)
	MapTexture m
	FitMesh m, 0, 0, 0, ImageWidth(img), 0.001, ImageHeight(img), True
	SetBuffer ImageBuffer(img)
	
	s% = GetSurface(m, 1)
	For i% = 0 To CountVertices(s)-1
		x# = VertexX(s, i)
		z# = VertexZ(s, i)
		GetColor(x, z)
		y# = (ColorRed()+ColorGreen()+ColorBlue())/20
		
		If y < 2 Then y = 0
		VertexCoords s, i, x, y, z
	Next
	SetBuffer BackBuffer()
	FreeImage img
	UpdateNormals m
	Return m
End Function

Function GetSurfaceBounds.Bounds(surface)
	b.Bounds = New Bounds
	
	b\x = VertexX(surface, 0)
	b\y = VertexY(surface, 0)
	b\z = VertexZ(surface, 0)
	b\dx = VertexX(surface, 0)
	b\dy = VertexY(surface, 0)
	b\dz = VertexZ(surface, 0)
	
	For i = 1 To CountVertices(surface) - 1
		If VertexX(surface, i) < b\x Then b\x = VertexX(surface, i)
		If VertexY(surface, i) < b\y Then b\y = VertexY(surface, i)
		If VertexZ(surface, i) < b\z Then b\z = VertexZ(surface, i)

		If VertexX(surface, i) > b\dx Then b\dx = VertexX(surface, i)
		If VertexY(surface, i) > b\dy Then b\dy = VertexY(surface, i)
		If VertexZ(surface, i) > b\dz Then b\dz = VertexZ(surface, i)
	Next
	
	b\dx = b\dx - b\x
	b\dy = b\dy - b\y
	b\dz = b\dz - b\z

	b\dx = b\dx/2
	b\dy = b\dy/2
	b\dz = b\dz/2
		
	b\x = b\x + b\dx
	b\y = b\y + b\dy
	b\z = b\z + b\dz
	
	Return b
End Function


Function InsertTriangleXZ(surface, x1#,z1#, x2#,z2#, x3#,z3#, approx#=0.0)
	InsertTriangle(surface, x1,0,z1, x2,0,z2, x3,0,z3, approx)
End Function


Function InsertTriangle(surface, x1#,y1#,z1#, x2#,y2#,z2#, x3#,y3#,z3#, approx#=0.0)
	v1% = -1
	v2% = -1
	v3% = -1
		
	For i% = 0 To CountVertices(surface) - 1
		x# = VertexX(surface, i)
		y# = VertexY(surface, i)
		z# = VertexZ(surface, i)
		
		If approx = 0 Then
			If x1=x And y1=y And z1=z Then v1 = i
			If x2=x And y2=y And z2=z Then v2 = i
			If x3=x And y3=y And z3=z Then v3 = i
		Else
			If Sqr((x1-x)*(x1-x)+(y1-y)*(y1-y)+(z1-z)*(z1-z)) < approx Then v1 = i
			If Sqr((x2-x)*(x2-x)+(y2-y)*(y2-y)+(z2-z)*(z2-z)) < approx Then v2 = i
			If Sqr((x3-x)*(x3-x)+(y3-y)*(y3-y)+(z3-z)*(z3-z)) < approx Then v3 = i
		End If
	Next
	
	If v1 < 0 Then v1 = AddVertex(surface, x1, y1, z1)
	If v2 < 0 Then v2 = AddVertex(surface, x2, y2, z2)
	If v3 < 0 Then v3 = AddVertex(surface, x3, y3, z3)
	
	AddTriangle surface, v1, v2, v3
End Function

Function MapTexture(mesh, mapType% = 0)
	For i = 1 To CountSurfaces(mesh)
		surface = GetSurface(mesh, i)
		
		b.bounds = GetSurfaceBounds(surface)
		
		Select mapType
			Case 1: ;XZ/Y Cylinderical Mapping
				
				For i = 0 To CountVertices(surface) - 1
					x# = VertexX(surface, i)-b\x
					z# = VertexZ(surface, i)-b\z
					
					a# = ATan2(z,x)
					
					v# = (VertexY(surface, i) - b\y) / b\dy
					v = v/2 + 0.5
					u# = a/720 + 0.5
										
					VertexTexCoords surface, i, u, v
				Next
				
			Default: ;Map UV to XZ
				For i = 0 To CountVertices(surface) - 1
					u# = (VertexX(surface, i) - b\x) / b\dx
					v# = (VertexZ(surface, i) - b\z) / b\dz
		
					VertexTexCoords surface, i, (u+1)/2, (v+1)/2
				Next
		End Select
		Delete b
	Next
End Function


;Quick Example
Graphics3D 800, 600, 0, 2

terrain = LoadIsoTerrain("height.bmp", 50)
cam = CreateCamera()
light = CreateLight()
RotateEntity light, 10, 50, 0
HidePointer()
PositionEntity cam, 200, 50, 200
PointEntity cam, terrain

SetBuffer BackBuffer()
While Not KeyHit(1)
		dY# = EntityPitch(cam)+MouseYSpeed()/2
		If dY > 89 Then dY = 89
		If dY < -89 Then dY = -89
	
		RotateEntity cam, dY, EntityYaw(cam)-(MouseXSpeed()/2), 0

	
		MoveEntity cam, 0, 0, (MouseDown(1)-MouseDown(2))*5
	MoveMouse GraphicsWidth()/2, GraphicsHeight()/2
	
	UpdateWorld()
	RenderWorld()
	VWait()
	Flip()
Wend
End
