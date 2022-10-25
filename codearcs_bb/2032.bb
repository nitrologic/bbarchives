; ID: 2032
; Author: Matt Merkulov
; Date: 2007-06-10 00:30:32
; Title: Grass on a landscape
; Description: 2 methods: creating and pre-generating grass squares

;2 methods of grass displaying by Matt Merkulov

SeedRnd MilliSecs()

Const MethodCreate = 0
Const MethodHide = 1

Const LandscapeSize = 256
Const DitheringTextureSize = 512
Const DitheringTextureScale = 4
Const LandscapeSizeMask = LandscapeSize - 1
Const LandscapeHeight# = 32

Const GrassColor = 100
Const GrassDColor = 20
Const GrassBackground = 100
Const MaxGrassSpreadingRadius# = 0.72

Const BushesThreshold# = 0.28
Const MaxBushesThreshold# = 0.34
Const SandThreshold# = 0.3
Const HillsMinRadius = 16
Const HillsMaxRadius = 64
Const HillsMinHeight# = 0.2
Const HillsMaxHeight# = 1.0
Const HillsQuantity# = 100

Const BushTypesQuantity = 2
Const BushModelsQuantity = 2
Const BushTextureSize = 256

Const BushModelScaleMin# = 0.3
Const BushModelScaleMax# = 0.6

Const GrassSquareModelsQuantity = 40
Const BushesDensityLevelsQuantity = 4

Const PlayerHeight# = 1.5
Const PlayerSpeedPerSecond# = 10.0
Const PlayerTurnPerSecond# = 180.0

Const FadingTimeInSeconds# = 1.0
Const MaxAlpha# = 1.0


;Const GrassMethod = MethodCreate ; Generation of some grass
Const GrassMethod = MethodHide ; Pre - generating of grass squares


;Small grass quadrants - smooth, but slow grass rendering
;Const GrassGridCellSize# = 2.0
;Const BushesQuantityMultiplier = 3
;Const GrassRadius# = 8


;Largel grass quadrants - jerky, but fast grass rendering
Const GrassGridCellSize# = 8.0
Const BushesQuantityMultiplier = 48
Const GrassRadius# = 2


Const Paint = False
;Const Paint = True


Const GrassGridSize = LandscapeSize / GrassGridCellSize#
Const BushTextureXTexSize# = 1.0 / BushTypesQuantity
Const BushTextureYTexSize# = 1.0 / BushModelsQuantity

Graphics3D 800, 600
Camera = CreateCamera()
ScaleEntity camera, 0.3, 0.3, 0.3
PositionEntity Camera, LandscapeSize / 2, 100, LandscapeSize / 2
;RotateEntity Camera, 45, 0, 0
RotateEntity CreateLight(), 45, 45, 0


Type ActiveSquare
	Field X, Z, JustAdded
End Type

Type FadingBush
	Field Alpha#, FadingMode, X, Z
End Type

Dim ServiceMap(LandscapeSize, LandscapeSize)
Const VisibleActiveSquare = %11
Const InvisibleActiveSquare = %10
Const VisibleSquare = %01
Const InvisibleSquare = %00
Const Visibility = %01
Const Activity = %10

Dim BushModel(BushTypesQuantity - 1, BushModelsQuantity - 1)
Dim GrassSquareModel(GrassSquareModelsQuantity - 1, BushesDensityLevelsQuantity - 1)
Dim BushQuantityMap#(GrassGridSize - 1, GrassGridSize - 1)
Dim BushMap(GrassGridSize - 1, GrassGridSize - 1)
Dim BushFader.FadingBush(GrassGridSize - 1, GrassGridSize - 1)

Global Landscape = CreateTerrain(LandscapeSize)
Global LandscapeTexture = CreateTexture(LandscapeSize, LandscapeSize, 15)
Global LandscapeDitheringTexture = CreateTexture(DitheringTextureSize, DitheringTextureSize, 15)
Global Grass = CreateTexture(BushTextureSize * BushTypesQuantity, BushTextureSize * BushModelsQuantity, 13)
ScaleEntity Landscape, 1, LandscapeHeight#, 1

CreateLandscape

CreateBushes

PaintTextures

TryToAddSquare EntityX(Camera) / GrassGridCellSize#, EntityZ(Camera) / GrassGridCellSize#

Const FadingIn = 1, FadingOut = 2

Repeat
	MoveEntity Camera, 0, 0, PrevFrameRenderingTimeInSeconds# * PlayerSpeedPerSecond# * (KeyDown(200) - KeyDown(208))
	TurnEntity Camera, 0, PrevFrameRenderingTimeInSeconds# * PlayerTurnPerSecond# * (KeyDown(203) - KeyDown(205)), 0

	FrameBeginningTime = MilliSecs()

	PlayerX# = EntityX(Camera)
	PlayerZ# = EntityZ(Camera)
	PositionEntity Camera, PlayerX#, TerrainY#(Landscape, PlayerX#, 0, PlayerZ#) + PlayerHeight#, PlayerZ#

	SetBuffer TextureBuffer(LandscapeTexture)

	qua = 0
	For S.ActiveSquare=Each ActiveSquare
		tmp = ServiceMap(S\X, S\Z)
		If (ServiceMap(S\X, S\Z) And Activity) = Activity And S\JustAdded Then
		 	Delete S
		Else
			DX# = S\X - PlayerX# / GrassGridCellSize#
			DZ# = S\Z - PlayerZ# / GrassGridCellSize#
			Distance# = Sqr(DX# * DX# + DZ# * DZ#)
			S\JustAdded = False
			If Distance# > GrassRadius# Then
				If (ServiceMap(S\X, S\Z) And Visibility) = VisibleSquare Then
					ExpandSquare S
					If Paint Then WritePixel S\X * GrassGridCellSize#, S\Z * GrassGridCellSize#, $FF00FFFF
					If BushMap(S\X, S\Z) Or GrassMethod = MethodHide Then
						If BushFader(S\X, S\Z) <> Null Then
							FB.FadingBush = BushFader(S\X, S\Z)
						Else
							FB.FadingBush = New FadingBush
							FB\X = S\X
							FB\Z = S\Z
							FB\Alpha# = MaxAlpha#
							BushFader(S\X, S\Z) = FB
						End If
						FB\FadingMode = FadingOut
					End If
					ServiceMap(S\X, S\Z) = InvisibleActiveSquare
				Else
					If CountNeighbors(S, VisibleSquare) = 0 Then
						ServiceMap(S\X, S\Z) = InvisibleSquare
						If Paint Then WritePixel S\X * GrassGridCellSize#, S\Z * GrassGridCellSize#, $FF7F7F7F
						Delete S
					Else
						ServiceMap(S\X, S\Z) = InvisibleActiveSquare
					End If
				End If
			Else
				If (ServiceMap(S\X, S\Z) And Visibility) = InvisibleSquare Then
					ExpandSquare S
					If Paint Then WritePixel S\X * GrassGridCellSize#, S\Z * GrassGridCellSize#, $FFFF0000
					ServiceMap(S\X, S\Z) = VisibleActiveSquare
					S\JustAdded = False
					If GrassMethod = MethodHide Then
						ShowEntity BushMap(S\X, S\Z)
					Else
						BushQuantity = Floor(BushQuantityMap#(S\X, S\Z) - Rnd(1.0, 0.0001))
						If BushQuantity >= 0 And BushFader(S\X, S\Z) = Null Then
							E = CopyEntity(GrassSquareModel(Rand(0,GrassSquareModelsQuantity - 1), BushQuantity))
							LX# = S\X * GrassGridCellSize#
							LZ# = S\Z * GrassGridCellSize#
							H# = TerrainY(Landscape, LX#, 0, LZ#)
							DY1# = TerrainY(Landscape, LX# + 1.0, 0, LZ#) - H#
							DY2# = TerrainY(Landscape, LX#, 0, LZ# + 1.0) - H#
							AlignToVector E, -DY1#, 1.0, -DY2#, 2
							PositionEntity E, LX#, H#, LZ#
							BushMap(S\X, S\Z) = E
						Else
							BushMap(S\X, S\Z) = CreateMesh()
						End If
					End If
					If BushFader(S\X, S\Z) <> Null Then
						FB.FadingBush = BushFader(S\X, S\Z)
					Else
						FB.FadingBush = New FadingBush
						FB\X = S\X
						FB\Z = S\Z
						FB\Alpha# = 0.0
						BushFader(S\X, S\Z) = FB
					End If
					FB\FadingMode = FadingIn
				Else
					If CountNeighbors(S, InvisibleSquare) = 0 Then
						ServiceMap(S\X, S\Z) = VisibleSquare
						If Paint Then WritePixel S\X * GrassGridCellSize#, S\Z * GrassGridCellSize#, $FF7F7F7F
						Delete S
					Else
						ServiceMap(S\X, S\Z) = VisibleActiveSquare
					End If
				End If
			End If
		End If
	Next

	DFading# = PrevFrameRenderingTimeInSeconds# / FadingTimeInSeconds#
	For FB.FadingBush = Each FadingBush
		A# = FB\Alpha#
		If FB\FadingMode = FadingIn Then
			A# = A# + DFading#
			If A# > MaxAlpha# Then A# = MaxAlpha#
			FB\Alpha# = A#
			EntityAlpha BushMap(FB\X, FB\Z), A#
			If A# = MaxAlpha# Then Delete FB
		Else
			A# = A# - DFading#
			If A# < 0.0 Then A# = 0.0
			FB\Alpha# = A#
			EntityAlpha BushMap(FB\X, FB\Z), A#
			If A# = 0.0 Then
				If GrassMethod = MethodHide Then
					HideEntity BushMap(FB\X, FB\Z)
				Else
					FreeEntity BushMap(FB\X, FB\Z)
					BushMap(FB\X, FB\Z) = 0
				End If
				BushFader(FB\X, FB\Z) = Null
				Delete FB
			End If
		End If
	Next

	For S.ActiveSquare=Each ActiveSquare
		qua = qua + 1
	Next

	SetBuffer BackBuffer()
	
	RenderWorld

	;Stop

	If FPSCounterResetTime <= MilliSecs() Then
		FPSCounterResetTime = MilliSecs() + 1000
		FPS = FPSCounter
		FPSCounter = 0
	Else
		FPSCounter = FPSCounter + 1
	End If
	Text 0, 0, "Frames / sec:" + FPS + ", activesquares: " + qua

	Flip

	PrevFrameRenderingTimeInSeconds# = 0.001 * (MilliSecs() - FrameBeginningTime)
Until KeyHit(1)

For X = 0 To GrassGridSize - 1
	For Y = 0 To GrassGridSize - 1
		If BushMap(X, Y) Then FreeEntity BushMap(X, Y)
	Next
Next

FreeEntity Landscape
FreeTexture LandscapeTexture
FreeTexture LandscapeDitheringTexture

If GrassMethod = MethodCreate Then
	For n1 = 0 To GrassSquareModelsQuantity - 1
		For n2 = 0 To BushesDensityLevelsQuantity - 1
			FreeEntity GrassSquareModel(n1, n2)
		Next
	Next
End If

Function CreateLandscape()
	SetBuffer FrontBuffer()
	Text 0, 0, "Generating landscape..."
	For n = 1 To HillsQuantity
		HillRadius = Rnd(HillsMinRadius, HillsMaxRadius)
		HillX = Rand(0, LandscapeSize)
		HillY = Rand(0, LandscapeSize)
		HillHeight# = Rnd(HillsMinHeight#, HillsMaxHeight#)

		If -HillRadius < -HillX Then DXFrom = -HillX Else DXFrom = -HillRadius
		If HillRadius > LandscapeSize - HillX Then DXTo = LandscapeSize - HillX - 1 Else DXTo = HillRadius
		If -HillRadius < -HillY Then DYFrom = -HillY Else DYFrom = -HillRadius
		If HillRadius > LandscapeSize - HillY Then DYTo = LandscapeSize - HillY - 1 Else DYTo = HillRadius

		For DY = DYFrom To DYTo
			For DX = DXFrom To DXTo
				X = HillX + DX
				Y = HillY + DY
				K# = Sqr(DX * DX + DY * DY) / HillRadius
				If K# > 1.0 Then K# = 1.0
				Height# = 0.5 * (1.0 + Cos(180.0 * K#)) * HillHeight# * HillRadius / 64
				If Height# > TerrainHeight#(Landscape, X, Y) Then ModifyTerrain Landscape, X, Y, Height#
			Next
		Next
	Next

	SetBuffer FrontBuffer()
	Text 0, 10, "Painting landscape..."
	SetBuffer TextureBuffer(LandscapeTexture)
	For Y = 0 To LandscapeSize - 1
		For X = 0 To LandscapeSize - 1
			Height# = TerrainHeight#(Landscape, X, Y)
			If Height# > SandThreshold# Then
				WritePixel X, Y, $FF00FF00
			Else
				WritePixel X, Y, $FFFFFF00
			End If
			If Height# > BushesThreshold# Then
				Quantity# = 1.0 * (Height# - BushesThreshold#) / (MaxBushesThreshold# - BushesThreshold#)
				If Quantity# > 1.0 Then Quantity# = 1.0
				BushQuantityMap#(Floor(X / GrassGridCellSize#), Floor(Y / GrassGridCellSize#)) = Quantity# * BushesDensityLevelsQuantity
			End If
		Next
	Next
End Function

Function PaintTextures()
	ScaleTexture LandscapeTexture, LandscapeSize, -LandscapeSize
	TerrainShading Landscape, True

	ScaleTexture LandscapeDitheringTexture, DitheringTextureScale, DitheringTextureScale
	SetBuffer TextureBuffer(LandscapeDitheringTexture)
	For Y = 0 To DitheringTextureSize
		For X = 0 To DitheringTextureSize
			WritePixel X, Y, (Rand(96,160) * $010101) Or $FF000000
		Next
	Next

	EntityTexture Landscape, LandscapeDitheringTexture
	EntityTexture Landscape, LandscapeTexture, 0, 1
	TextureBlend LandscapeTexture, 2
End Function

Function CreateBushes()
	;SetBuffer BackBuffer()
	SetBuffer FrontBuffer()
	Text 0, 20, "Painting textures..."

	SetBuffer TextureBuffer(Grass)
	For Y = 0 To BushTextureSize * BushTypesQuantity - 1
		For X = 0 To BushTextureSize * BushModelsQuantity - 1
			WritePixel X, Y, 256 * GrassBackground
		Next
	Next

	For n1 = 0 To BushTypesQuantity - 1
		For n2 = 0 To BushModelsQuantity - 1
			
			m = CreateMesh()
			s = CreateSurface(m)
			If n1 = 0 Then
				For nn = 1 To 100 + n2 * 100
					Radius# = Rnd(BushTextureSize / 2, BushTextureSize)
					X = BushTextureSize * (Rnd(0,1) ^ 2 * (Rand(0, 1) * 2 - 1) + 1) * 0.5
					Repeat
						Angle# = Rnd(70, 110)
						X2 = Cos(Angle#) * Radius# + X
						Y2 = BushTextureSize - 1 - Sin(Angle#) * Radius# + n2 * BushTextureSize
					Until X2 >=0 And X2 < BushTextureSize
					For DeltaX = -3 To 3
						Color 0, GrassColor + DeltaX * GrassDColor, 0
						Line X + DeltaX, (n2 + 1) * BushTextureSize - 1, X2, Y2
					Next
				Next
				For nn= 0 To 7
					Angle# = nn * 45
					;CreateQuad s, Cos(Angle# - 45), Sin(Angle# - 45), Cos(Angle# + 45), Sin(Angle# + 45), Cos(Angle#) * 0.75, Sin(Angle#) * 0.75
					CreateQuad s, Cos(Angle#) * 0.5, Sin(Angle#) * 0.5, -Cos(Angle#) * 0.5, -Sin(Angle#) * 0.5, n1, n2, Cos(Angle# + 90), Sin(Angle# + 90)
				Next
			Else
				For nn = 1 To 150 + n2 * 150
					X = Rnd(BushTextureSize * 2 / 5, BushTextureSize * 3 / 5) + BushTextureSize
					Angle# = Rnd(10, 170)
					Radius# = Rnd(0, 1) * (1 - Abs(angle - 90) / 133) *  BushTextureSize
					X2 = Cos(Angle#) * Radius# + BushTextureSize / 2 + BushTextureSize
					Y2 = (n2 + 1) * BushTextureSize - 1 - Sin(Angle#) * Radius# 
					For DeltaX = -3 To 3
						Color 0, GrassColor + DeltaX * GrassDColor, 0
						Line X + DeltaX, (n2 + 1) * BushTextureSize - 1, X2, Y2
					Next
				Next
				For nn = 0 To 3
					Angle# = nn * 45
					xx# = Cos(Angle#)
					yy# = Sin(Angle#)
					CreateQuad s, xx#, yy#, -xx#, -yy#, n1, n2
				Next
			End If
			BushModel(n1, n2) = m
			HideEntity m
		Next
	Next

	SetBuffer FrontBuffer()
	Color 255, 255, 255
	Text 0, 30, "Generating grass squares (it might take some more time)..."

	If GrassMethod = MethodHide Then
		For Z = 0 To GrassGridSize - 1
			For X = 0 To GrassGridSize - 1
				m = CreateMesh()
				For n3 = 1 To BushesDensityLevelsQuantity * BushesQuantityMultiplier
					Repeat
						DX# = Rnd(-MaxGrassSpreadingRadius#, MaxGrassSpreadingRadius#)
						DZ# = Rnd(-MaxGrassSpreadingRadius#, MaxGrassSpreadingRadius#)
						If  Sqr(DX# * DX# + DZ# * DZ#) <= MaxGrassSpreadingRadius# Then

							LX# = (DX# + X) * GrassGridCellSize#
							LZ# = (DZ# + Z) * GrassGridCellSize# 

							H# = TerrainY(Landscape, LX#, 0, LZ#)
							Height# = H# / LandscapeHeight#
							If Height# > BushesThreshold# Then
								Quantity# = 1.0 * (Height# - BushesThreshold#) / (MaxBushesThreshold# - BushesThreshold#)
								If Rnd(1) < Quantity# Then
									m2 = CopyMesh(BushModel(Rand(0, BushTypesQuantity - 1), Rand(0, BushModelsQuantity - 1)))
									SXZ# = Rnd(BushModelScaleMin#, BushModelScaleMax#)
									SY# = Rnd(BushModelScaleMin#, BushModelScaleMax#)
									DY1# = TerrainY(Landscape, LX# + 1.0, 0, LZ#) - H#
									DY2# = TerrainY(Landscape, LX#, 0, LZ# + 1.0) - H#
									AlignToVector m2, -DY1#, 1.0, -DY2#, 2
									RotateMesh m2, 0, Rnd(0,359), 0
									ScaleMesh m2, SXZ#, SY#, SXZ#
									s = GetSurface(m2, 1)
									For n = 0 To CountVertices(s) - 1
										VertexNormal s, n, 0, 1, 0
									Next
									RotateMesh m2, EntityPitch(m2), EntityYaw(m2) , EntityRoll(m2)
									PositionMesh m2, LX#, H#, LZ#
									AddMesh m2, m
									FreeEntity m2
								End If
							End If
							Exit
						End If
					Forever
				Next
				BushMap(X, Z) = m
				EntityFX m, 16
				EntityTexture m, Grass
				HideEntity m
				EntityAlpha m, 0.0
			Next
		Next
	Else
		For n1 = 0 To GrassSquareModelsQuantity - 1
			For n2 = 0 To BushesDensityLevelsQuantity - 1
				m = CreateMesh()
				For n3 = 1 To (n2 + 1) * BushesQuantityMultiplier
					m2 = CopyMesh(BushModel(Rand(0, BushTypesQuantity - 1), Rand(0, BushModelsQuantity - 1)))
					DX# = Rnd(-0.5, 0.5): DZ# =Rnd(-0.5, 0.5)
					SXZ# = Rnd(BushModelScaleMin#, BushModelScaleMax#) / GrassGridCellSize#
					SY# = Rnd(BushModelScaleMin#, BushModelScaleMax#) / GrassGridCellSize#
					RotateMesh m2, 0, Rnd(0,359), 0
					ScaleMesh m2, SXZ#, SY#, SXZ#
					PositionMesh m2, DX#, 0, DZ#
					s = GetSurface(m2, 1)
					For n = 0 To CountVertices(s) - 1
						VertexNormal s, n, 0, 1, 0
					Next
					AddMesh m2, m
					FreeEntity m2
				Next
				ScaleMesh m, GrassGridCellSize#, GrassGridCellSize#, GrassGridCellSize#
				EntityFX m, 16
				EntityTexture m, Grass
				HideEntity m
				GrassSquareModel(n1, n2) = m
			Next
		Next
	End If

	For n1 = 0 To BushTypesQuantity - 1
		For n2 = 0 To BushModelsQuantity - 1
			FreeEntity BushModel(n1, n2)
		Next
	Next
End Function

Function CreateQuad(Surface, X1#, Y1#, X2#, Y2#, XTex#, YTex#, DX# = 0, DY# = 0)
	XTex# = XTex# * BushTextureXTexSize#
	YTex# = YTex# * BushTextureYTexSize#
	v1 = AddVertex(Surface, X1#, 0, Y1#, XTex#, YTex# + BushTextureYTexSize#)
	v2 = AddVertex(Surface, X1# + DX#, 1, Y1# + DY#, XTex#, YTex#)
	v3 = AddVertex(Surface, X2# + DX#, 1, Y2# + DY#, XTex# + BushTextureYTexSize#, YTex#)
	v4 = AddVertex(Surface, X2#, 0, Y2#, XTex# + BushTextureYTexSize#, YTex# + BushTextureYTexSize#)
	AddTriangle Surface, v1, v2, v3
	AddTriangle Surface, v3, v4, v1
End Function

Function TryToAddSquare(X, Z)
	S.ActiveSquare = New ActiveSquare
	S\X = X
	S\Z = Z
	S\JustAdded = True
End Function

Function ExpandSquare(S.ActiveSquare)
	If S\X > 0 Then TryToAddSquare S\X - 1, S\Z
	If S\X < GrassGridSize - 1 Then TryToAddSquare S\X + 1, S\Z
	If S\Z > 0 Then TryToAddSquare S\X, S\Z - 1
	If S\Z < GrassGridSize - 1 Then TryToAddSquare S\X, S\Z + 1
End Function

Function CountNeighbors(S.ActiveSquare, NeighborType)
	If S\X > 0 Then If (ServiceMap(S\X - 1, S\Z) And Visibility) = NeighborType Then SquaresQuantity = SquaresQuantity + 1
	If S\X < GrassGridSize - 1 Then If (ServiceMap(S\X + 1, S\Z) And Visibility) = NeighborType Then SquaresQuantity = SquaresQuantity + 1
	If S\Z > 0 Then If (ServiceMap(S\X, S\Z - 1) And Visibility) = NeighborType Then SquaresQuantity = SquaresQuantity + 1
	If S\Z < GrassGridSize - 1 Then If (ServiceMap(S\X, S\Z + 1) And Visibility) = NeighborType Then SquaresQuantity = SquaresQuantity + 1
	Return SquaresQuantity
End Function
