; ID: 1114
; Author: aCiD2
; Date: 2004-07-22 03:52:03
; Title: Segmented quad
; Description: Create a segmented plane, to allow blitz to cull

Dim NuClearTerrain_TerrainVerts(0, 0, 0)
Dim NuClearTerrain_Meshs(0, 1)
Dim NuClearTerrain_UV#(0, 0, 0)

Function CreateSegmentedQuad(Width%, Height%, SegmentSize = 32 )
	
	; Enusre the terrain is createable.
	Width = pm_Max(Width, pm_Min(Width, 1))
	Height = pm_Max(Height, pm_Min(Height, 1))
	SegmentSize = pm_Max(SegmentSize, pm_Min(SegmentSize, 32))
	
	If Width <> Height RuntimeError "Terrains, must be square in size! Eg. 64x64, 128x128"
	
	Local PwrOf2 = False
	For I = 0 To 10
		If Width = 2^I Then PwrOf2 = True: Exit
	Next
	If PwrOf2 = False RuntimeError "Sizes must be power of twos, eg - 64, 128, 256"
	
	PwrOf2 = False
	For I = 0 To 10
		If Width = 2^I Then PwrOf2 = True: Exit
	Next
	If PwrOf2 = False RuntimeError "Sizes must be power of twos, eg - 64, 128, 256"
	
	; Checks how many 'segments' it needs
	Local Segments = (Width / SegmentSize) * (Width / SegmentSize)
	
	; Setup the mesh
	Dim NuClearTerrain_TerrainVerts(SegmentSize, SegmentSize, Segments)
	Dim NuClearTerrain_UV(Width, Height, 1)
	Dim NuClearTerrain_Meshs(Segments, 1)
	For I = 1 To Segments
		NuClearTerrain_Meshs(I, 0) = CreateMesh()
		NuClearTerrain_Meshs(I, 1) = CreateSurface(NuClearTerrain_Meshs(I, 0))
		
		If I > 1 Then EntityParent NuClearTerrain_Meshs(I, 0), NuClearTerrain_Meshs(1, 0)
	Next

	; Create the uv look up table
	Local X, Y
	For X = 0 To Width
		For Y = 0 To Height
			NuClearTerrain_UV(X, Y, 0) = Float(X) / Width
			NuClearTerrain_UV(X, Y, 1) = 1 - Float(Y) / Height
		Next
	Next

	; Create the vertices for the grid.
	For S = 1 To Segments
		For X = 0 To SegmentSize - 1
			For Y = 0 To SegmentSize - 1
				NuClearTerrain_TerrainVerts(X, Y, S) = AddVertex(NuClearTerrain_Meshs(s, 1), X, 0, Y)
			Next
		Next
	Next
	
	ZPos = 0
	
	; Add all the triangles.
	For S = 1 To Segments
		If ((S-1) Mod (Width / SegmentSize)) = 0 And s > 2 ZPos = Zpos + SegmentSize - 1: XPos = 0
		For X = 0 To SegmentSize - 2
			For Y = 0 To SegmentSize - 2
				AddTriangle NuClearTerrain_Meshs(s, 1), NuClearTerrain_TerrainVerts(X, Y, S), NuClearTerrain_TerrainVerts(X, Y + 1, S), NuClearTerrain_TerrainVerts(X + 1, Y, S)
				AddTriangle NuClearTerrain_Meshs(s, 1), NuClearTerrain_TerrainVerts(X + 1, Y, S), NuClearTerrain_TerrainVerts(X, Y + 1, S), NuClearTerrain_TerrainVerts(X + 1, Y + 1, S)
			Next
		Next
		UpdateNormals NuClearTerrain_Meshs(S, 0)
		PositionEntity NuClearTerrain_Meshs(S, 0), Xpos, 0, zpos, True
		EntityPickMode NuClearTerrain_Meshs(s, 0), 2, True
		xpos = xpos + SegmentSize - 1
	Next
	
	; Set the texture coords
	For s = 1 To Segments
		For X = 0 To SegmentSize - 1
			For Y = 0 To SegmentSize - 1
				TFormPoint X, 0, Y, NuClearTerrain_Meshs(s, 0), 0
				VertexTexCoords NuClearTerrain_Meshs(s, 1), NuClearTerrain_TerrainVerts(X, Y, S), NuClearTerrain_UV(TFormedX(), TFormedZ(), 0), NuClearTerrain_UV(TFormedX(), TFormedZ(), 1)
			Next
		Next
	Next
	
	Return NuClearTerrain_Meshs(1, 0)
	
End Function
