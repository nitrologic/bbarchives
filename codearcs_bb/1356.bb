; ID: 1356
; Author: Mikle
; Date: 2005-04-17 09:39:05
; Title: AppMesh
; Description: AppMesh() copies one mesh to another, as AddMesh(), but in more sophisticated way.

; AppMesh() copies one mesh to another, as AddMesh(),
; but in more sophisticated way.

; bTransform - says if we need to use entities' current
; position, rotation and scale, or to ignore it

; bUseOldSurfs - says if we add mesh to the existing surfaces
; or we create new ones

; AppSrf - first surface of the DestMesh to which the SrcMesh
; will begin to add. ATTENTION! If bUseOldSurfs = True,
; AppSrf will be ignored

; P.S. I'm not sure that there are no bugs.

Function AppMesh(SrcMesh, DestMesh, bTransform = False, AppSrf = 0, bUseOldSurfs = False)
	Local sc, nSurfs, vc, nVerts, tc, nTris
	Local Srf, DestSrf, Brush
	Local nAppSurfs, nAppVerts, nAppTris, dsc, nvi
	Local x#, y#, z#, nx#, ny#, nz#
	Local u#, v#, w#, red#, green#, blue#, alpha#
	Local v0, v1, v2
	nAppSurfs = CountSurfaces(DestMesh)
	If AppSrf <> 0
		dsc = GetSurfaceIndex(DestMesh, AppSrf) - 1
	EndIf
	nSurfs = CountSurfaces(SrcMesh)
	For sc = 1 To nSurfs
		Srf = GetSurface(SrcMesh, sc)
		dsc = dsc + 1

		If AppSrf = 0
			If bUseOldSurfs And (dsc <= nAppSurfs)
				DestSrf = GetSurface(DestMesh, dsc)
			Else
				DestSrf = CreateSurface(DestMesh)
			EndIf
		Else
			If bUseOldSurfs
				If dsc <= nAppSurfs
					DestSrf = GetSurface(DestMesh, dsc)
				Else
					DestSrf = CreateSurface(DestMesh)
				EndIf
			Else
				DestSrf = AppSrf
			EndIf
		EndIf

		nAppVerts = CountVertices(DestSrf)
		nAppTris = CountTriangles(DestSrf)
		nVerts = CountVertices(Srf)-1
		nTris = CountTriangles(Srf)-1

		For vc = 0 To nVerts
			nvi = vc+nAppVerts
			x = VertexX(Srf, vc)
			y = VertexY(Srf, vc)
			z = VertexZ(Srf, vc)
			If bTransform
				TFormPoint x, y, z, SrcMesh, DestMesh
				x = TFormedX()
				y = TFormedY()
				z = TFormedZ()
			EndIf
			AddVertex DestSrf, x, y, z
			For vtcc = 0 To 1
				u = VertexU(Srf, vc, vtcc)
				v = VertexV(Srf, vc, vtcc)
				w = VertexW(Srf, vc, vtcc)
				VertexTexCoords DestSrf, nvi, u, v, w, vtcc
			Next
			nx = VertexNX(Srf, vc)
			ny = VertexNY(Srf, vc)
			nz = VertexNZ(Srf, vc)
			If bTransform
				TFormVector nx, ny, nz, SrcMesh, DestMesh
				nx = TFormedX()
				ny = TFormedY()
				nz = TFormedZ()
			EndIf
			VertexNormal DestSrf, nvi, nx, ny, nz
			red = VertexRed(Srf, vc)
			green = VertexGreen(Srf, vc)
			blue = VertexBlue(Srf, vc)
			alpha = VertexAlpha(Srf, vc)
			VertexColor DestSrf, nvi, red, green, blue, alpha
		Next

		For tc = 0 To nTris
			v0 = TriangleVertex(Srf, tc, 0)+nAppVerts
			v1 = TriangleVertex(Srf, tc, 1)+nAppVerts
			v2 = TriangleVertex(Srf, tc, 2)+nAppVerts
			AddTriangle DestSrf, v0, v1, v2
		Next

		; Currently I do not replace old brushes
		If (AppSrf = 0) And (dsc > nAppSurfs)
			Brush = GetSurfaceBrush(Srf)
			PaintSurface DestSrf, Brush
			FreeBrush Brush
		EndIf
	Next
End Function

Function GetSurfaceIndex(Mesh, Srf)
	Local wSrf, nSurfs, sc
	nSurfs = CountSurfaces(Mesh)
	For sc = 1 To nSurfs
		wSrf = GetSurface(Mesh, sc)
		If (wSrf = Srf) Then Return sc
	Next
End Function
