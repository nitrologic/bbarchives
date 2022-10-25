; ID: 826
; Author: fredborg
; Date: 2003-11-12 17:04:46
; Title: PolyTry Blitz Version
; Description: Tesselation of mono connected non convex polygons

;
; Tesselate
;
; Converted from the original PolyTry source code by Peppino Sbargzeguti
; http://www.codeproject.com/useritems/polygon_tesselation.asp
;
; Blitz Version by Mikkel Fredborg
;

Const VERTEXLIMIT	= 999

Const DEGENERATE	= 0
Const CONCAVE		= 1
Const CONVEX		= 2

Type tessVert
	Field x#,y#,z#,u#,v#,n
End Type

Type tessSurf
	Field v0.tessVert[VERTEXLIMIT]
	Field v1.tessVert[VERTEXLIMIT]
	Field v2.tessVert[VERTEXLIMIT]
	Field n_tris
End Type

Dim tsv.tessVert(0)

Global mE0x#,mE0y#,mE0z#	
Global mE1x#,mE1y#,mE1z#				
Global mNx#,mNy#,mNz#,mA#
Global tessNx#,tessNy#,tessNz#

Function tess_Clean()
	
	Delete Each tessVert
	Delete Each tessSurf
	
End Function

Function tess_AddVert(x#,y#,z#=0.0,u#=0.0,v#=0.0)
	
	p.tessVert = Last tessVert
	If (p = Null)
		n = 0
	Else
		n = p\n+1
	End If
		
	p.tessVert = New tessVert
	p\x		= x
	p\y		= y
	p\z		= z
	p\u		= u
	p\v		= v
	p\n		= n
	
End Function

Function tess_ComputeNormal()

	tessNx# = 0.0
	tessNy# = 0.0
	tessNz# = 0.0

	For a.tessVert = Each tessVert
		b.tessVert = After a
		If b = Null Then Exit
		
        tessNx = tessNx + ((a\y - b\y ) * ( a\z + b\z))
        tessNy = tessNy + ((a\z - b\z ) * ( a\x + b\x))
        tessNz = tessNz + ((a\x - b\x ) * ( a\y + b\y))
	Next
	
	; Normalize it, not really nescessary
	; just nicer to look at :)
	Local d# = Sqr(tessNx*tessNx + tessNy*tessNy + tessNz*tessNz)
	If d>0.0
		tessNx = tessNx/d
		tessNy = tessNy/d
		tessNz = tessNz/d
	End If

End Function

Function tess_Triangulate()

	Local n_tris = 0
	Local n_verts = 0
	Local noErrors = True

	; Close the polygon, by adding the first vert after the last
	p.tessVert = First tessVert
	If Not (p=Null)
		tess_AddVert(p\x,p\y,p\z,p\u,p\v)
	End If

	; Get the normal of the entire polygon
	tess_ComputeNormal()

	; Count number of vertices
	n_verts = -1
	For p.tessVert = Each tessVert
		n_verts = n_verts + 1
	Next

	; Index the vertices
	Dim tsv(n_verts)
	n = 0
	For p.tessVert = Each tessVert
		tsv(n) = p
		n = n + 1
	Next

	; Prepare a TessSurf
	surf.TessSurf	= New TessSurf
	surf\n_tris		= 0

	; Now it gets funny
	While n_verts=>3 And noErrors = True
	
		noErrors = False
		
		i = 0
		j = 1
		k = 2
		While k<(n_verts+3)
			If n_verts=0 Then Exit

			ib = i Mod n_verts
			jb = j Mod n_verts
			kb = k Mod n_verts
		
			Select tess_TriangleArea(ib,jb,kb)
				Case CONVEX:
					If tess_IsAnyPointInside(ib,jb,kb,n_verts) 
						; Triangle is ok, but it cross another part of the polygon
						i = j
						j = k
						k = k + 1
					Else
						; Triangle is ok, so build it
						tess_AddTriangle(surf,ib,jb,kb)
						n_tris   = n_tris + 1
						n_verts  = tess_RemoveVertex(jb,n_verts)
						noErrors = True
					End If
					
				Case CONCAVE:
					; Triangle faces the wrong way
					i = j
					j = k
					k = k + 1
				
				Case DEGENERATE:
					; Bad triangle (zero area)
					n_verts  = tess_RemoveVertex(jb,n_verts)
					noErrors = True
		
			End Select
		Wend
		
	Wend
	
	Return n_tris
	
End Function

Function tess_TriangleArea(i,j,k)
	
	Local v0.tessVert = tsv(i)
	Local v1.tessVert = tsv(j)
	Local v2.tessVert = tsv(k)

	mE0x# = v0\x-v2\x
	mE0y# = v0\y-v2\y
	mE0z# = v0\z-v2\z
	
	mE1x# = v1\x-v2\x
	mE1y# = v1\y-v2\y
	mE1z# = v1\z-v2\z				

	mNx# = mE0y * mE1z - mE0z * mE1y
	mNy# = mE0z * mE1x - mE0x * mE1z
	mNz# = mE0x * mE1y - mE0y * mE1x
		
	mA# = (mNx*mNx + mNy*mNy + mNz*mNz)

	If Abs(mA) < 0.000001
		Return DEGENERATE
	End If
	
	If (mNx#*tessNx# + mNy#*tessNy# + mNz#*tessNz#) < 0.0
		Return CONCAVE
	Else
		Return CONVEX
	End If
	
End Function

Function tess_RemoveVertex(j,n_verts)

	For i = j+1 To n_verts
		tsv(i-1)=tsv(i)
	Next
	Return n_verts-1
		
End Function

Function tess_AddTriangle(surf.tessSurf,i,j,k)

	surf\v0[ surf\n_tris ] = tsv(i)
	surf\v1[ surf\n_tris ] = tsv(j)
	surf\v2[ surf\n_tris ] = tsv(k)
	surf\n_tris = surf\n_tris + 1
	Return surf\n_tris
	
End Function

Function tess_IsAnyPointInside(i,j,k,n_verts)

	For ip=0 To n_verts
    	If (ip<i) Or (ip>k)
			If tess_IsPointInside(tsv(ip),tsv(k))
				Return True
			End If
		End If
	Next

	Return False
	
End Function

Function tess_IsPointInside(point.tessVert,q2.tessVert)

	Local pmq2x# = point\x - q2\x
	Local pmq2y# = point\y - q2\y
	Local pmq2z# = point\z - q2\z		
	
	Local ntmpx# = 0.0
	Local ntmpy# = 0.0
	Local ntmpz# = 0.0
	
	Local b0# = 0.0
	Local b1# = 0.0

	ntmpx# = pmq2y * mE1z - pmq2z * mE1y
	ntmpy# = pmq2z * mE1x - pmq2x * mE1z
	ntmpz# = pmq2x * mE1y - pmq2y * mE1x
	b0# = mNx*ntmpx + mNy*ntmpy + mNz*ntmpz
	If b0 <= 0.0 Then Return False
	
	ntmpx# = mE0y * pmq2z - mE0z * pmq2y
	ntmpy# = mE0z * pmq2x - mE0x * pmq2z
	ntmpz# = mE0x * pmq2y - mE0y * pmq2x
	b1# = mNx*ntmpx + mNy*ntmpy + mNz*ntmpz
	If b1 <= 0.0 Then Return False
	
    If (mA-B0-B1)>0.0
		Return True
	Else
		Return False
	End If

End Function

Function tess_TriNormal(v0x#,v0y#,v0z#,v1x#,v1y#,v1z#,v2x#,v2y#,v2z#)
	
	ax#=v1x-v0x
	ay#=v1y-v0y
	az#=v1z-v0z
	
	bx#=v2x-v1x
	by#=v2y-v1y
	bz#=v2z-v1z
	
	tessNx#=(ay#*bz#)-(az#*by#)
	tessNy#=(az#*bx#)-(ax#*bz#)
	tessNz#=(ax#*by#)-(ay#*bx#)

End Function



;
; Example usage
;
; 
Graphics3D 640,480,0,2
SetBuffer BackBuffer()

wire=False
WireFrame wire

camera = CreateCamera()
PositionEntity camera,0,0,0

mesh = CreateMesh()
surf = CreateSurface(mesh)
EntityColor mesh,255,0,0

box = CreateCube()
PositionEntity box,1,0,3
ScaleEntity box,1,1,2
RotateEntity box,0,40,0
EntityPickMode box,2

Repeat

	TurnEntity box,KeyDown(200)-KeyDown(208),KeyDown(203)-KeyDown(205),0

	If MouseHit(1)
		
		If cleanme
			Cls 
			cleanme = False
			ClearSurface surf
		End If

		If CameraPick(camera,MouseX(),MouseY())
			x# = PickedX()+(PickedNX()*0.01)
			y# = PickedY()+(PickedNY()*0.01)
			z# = PickedZ()+(PickedNZ()*0.01)			
			tess_AddVert(x,y,z)
		End If
		
	End If
	
	If MouseHit(2)
		tris = tess_Triangulate()
	
		;
		; Build a real 3D surface from the tessSurf
		ClearSurface surf
		For tessVert.TessVert = Each TessVert
			AddVertex surf,tessVert\x,tessVert\y,tessVert\z
		Next
		tessSurf.TessSurf = First TessSurf
		For t = 0 To tessSurf\n_tris-1
			AddTriangle surf,tessSurf\v0[t]\n,tessSurf\v1[t]\n,tessSurf\v2[t]\n
		Next		
		UpdateNormals mesh
		
		;
		; Clean up :)
		tess_Clean()
		
		cleanme = True
	End If

	If KeyHit(17)
		wire = Not wire
		WireFrame wire
	End If

	If KeyHit(57)
		FlipMesh mesh
	End If

	RenderWorld
	
	;
	; Draw the building wire polygon thingy
	x1 = -1
	y1 = -1
	For tessvert.tessvert = Each tessvert
		Color 255,0,0
		CameraProject camera,tessvert\x,tessvert\y,tessvert\z
		x0 = ProjectedX()
		y0 = ProjectedY()
		Rect x0-1,y0-1,3,3
		If x1<>-1
			Color 255,255,255
			Line x0,y0,x1,y1
		End If
		x1 = x0
		y1 = y0
	Next
	
	;
	; Show Vertex normals
	n_verts = CountVertices(surf)-1
	For v = 0 To n_verts
		TFormPoint VertexX(surf,v),VertexY(surf,v),VertexZ(surf,v),mesh,0
		CameraProject camera,TFormedX(),TFormedY(),TFormedZ()
		x0 = ProjectedX()
		y0 = ProjectedY()
		
		TFormPoint VertexX(surf,v)+(VertexNX(surf,v)*0.1),VertexY(surf,v)+(VertexNY(surf,v)*0.1),VertexZ(surf,v)+(VertexNZ(surf,v)*0.1),mesh,0
		CameraProject camera,TFormedX(),TFormedY(),TFormedZ()
		x1 = ProjectedX()
		y1 = ProjectedY()
		
		Color 255,255,0
		Line x0,y0,x1,y1
	Next
	
	Color 255,255,255
	Text 320,  0,"Tris - "+tris+" | Time - "+ms+" ms",True
	Text 320,460,"Left Mouse - Add Vertex | Right Mouse - Triangulate | Space - Flip Mesh",True
	
	Flip

Until KeyHit(1)

End
