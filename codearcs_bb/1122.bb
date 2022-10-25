; ID: 1122
; Author: Nilium
; Date: 2004-07-31 23:04:48
; Title: Ulysses Foliage System
; Description: Places foliage models and grass across objects

;; ************************ READ ME **************************
;; If you do not have the BZ2 user-lib made by elias_t then this function is necessary, otherwise **REMOVE IT**.
;; ************************ READ ME **************************
;Function bz2(compress,level,cfrom$,cto$)
;	CopyFile cfrom,cto
;End Function
;; Commented out by default because I have the bz2 userlib and because it's included in Ulysses.zip.


	
Type GrassTile
	Field Surface
	Field US#,VS#
	Field Columns,Rows
End Type

Type GrassSet
	Field Mesh
	Field Radius#
	Field Range#
	Field W,H
	Field GSin#[359]
	Field GCos#[359]
	Field TPivot
	Field Camera
	Field OX#,OY#,OZ#,OYaw#,OPitch#
	Field TPivotB
End Type

Type GTex
	Field Index
	Field FOV
	Field ID
	Field Count
	Field Size#
	Field Rate%
	Field Tex.GrassTile
	Field MaxCount%
	Field Frame
	Field On
	Field WindDirection%
	Field WindSpeed#
	Field WindPower#
	Field Mesh
	Field WindAffects
	Field BunchRange#
	Field BunchCount% ;; you can go from 1 to GrassBunchCount-1
End Type

Type GrassObject
	Field Entity
	Field Surface
	Field Nodes.GNode[NODE_SEG^2 - 1]
	Field Rows.NodeRow[NODE_SEG-1]
End Type

Type GNode
	Field X#,Y#,Z#
	Field Width#,Depth#
	Field NX#,NY#,NZ#
	Field U%				;; Useable
	Field Occupied[OccupiedIndices]
	Field R#,G#,B#,A#
End Type

Type NodeRow
	Field X#,Width#
	Field Z#,Depth#
	Field Nodes.GNode[NODE_SEG-1]
End Type

Type GSprite
	Field TimeOut
	Field X#,Y#,Z#
	Field NX#,NY#,NZ#
	Field N.GNode
	Field Parent.GTex
	Field IDelete
	Field Quads.GQuad[cGrassBunchCount-1]
	Field Range#
End Type

Type GQuad
	Field X#,Y#,Z#
	Field VX#[3]
	Field VY#[3]
	Field VZ#[3]
	Field CAlpha#
	Field DAlpha#
	Field Angle#
	Field gx#,gy#,gz#,rx#,ry#,rz#
	Field Size#
	Field Reverse%
	Field R%,G%,B%
End Type

Type GBlock
	Field X#,Y#,Z#,W#,L#,H#
End Type

;; Internal array
Dim SortArray#(0,0)

;; The amount of grass per node
Const cGrassBunchCount = 10

;; Function Arguments
Const cGrassCount = 500
Const cGrassRate = 1

;; The amount of segments per precalculated node array
;; Keep in mind, this value should be low, even if you export the node list and import it later to save time, Blitz will still slow down when you go through NODE_SEG^2 amount of nodes
Const NODE_SEG = 48

;; The amount of larger blocks of nodes to check against when finding the node to use
Const CONT_SEG = 8

;; Various Function Modified Variables
Global IntersectedX#,IntersectedY#,IntersectedZ#,IntersectedTriangle%,IntersectedSurface%

;; The time it takes to call the UpdateGrass function
Global GrassUpdateTime% = 0

;; The width/length of the grass bunch range
Global cGrassBunchRange# = 3

;; Grass system data and information about the graphics mode
Global gGrassSet.GrassSet = Null

;; Grass sprites in existence (not quads, rather the nodes that contain n amount of quads)
Global cCurrentGrassCount = 0

;; Amount of different kinds of grass spawners a node can 'carry'
Const OccupiedIndices = 16

;; Amount of spawners in existence, never modify this variable
Global ExistingSpawners = 0

;; Amount of nodes in existence
Global ExistingNodes

;; The amount of grass quads drawn
Global GrassQuadsDrawn = 0

;; Minimum Y normal of grass angles
Global YNormal# = .8

;; The alpha-level of grass pieces.
Global GrassAlpha# = 1

;; The speed at which the grass fades in.  0 is not at all, 100 is instantly (as in no interpolation).
Global AlphaFadeSpeed# = 2.5

;; Wind speed defaults
Const WindAffects = 1
Const WindDirection = 45	;; This is intentionally an integer so I can use it with the sin/cos lookup tables (refer to type GrassSet)
Const WindPower# = .6	;; Set this to something like .4 or something, otherwise it won't work out too well...
Const WindSpeed# = .06	;; Decrease to speed up, increase to slow down

;; Kill off-screen grass quads
Global KillOffScreen = 0

;; The amount of time when a grass bunch isn't visible before it gets removed
Global GrassTimeout = 400

;; Call this before the main loop- this is mandatory
Function InitGrass.GrassSet(Camera,CheckRange#=64,GrassRadius#=80)
	g.GrassSet = New GrassSet
	g\Camera = Camera
	g\Range = CheckRange	;; store checking settings
	g\Radius = GrassRadius
	g\Mesh = CreateMesh()	;; create the mesh that all grass sprites are placed in
	EntityFX g\Mesh,1+2+16+32
	g\W = GraphicsWidth()	;; store graphics width and height
	g\H = GraphicsHeight()
	For A = 0 To 359		;; build sin\cos lookup tables
		g\GSin[A] = Sin(A)
		g\GCos[A] = Cos(A)
	Next
	g\TPivot = CreatePivot()
	g\TPivotB = CreatePivot()
	gGrassSet = g
	Return g
End Function

;; frees the grass system from memory
Function KillGrass()
	FreeEntity gGrassSet\TPivot
	FreeEntity gGrassSet\TPivotB
	FreeEntity gGrassSet\Mesh
	Delete Each GTex
	Delete Each GSprite
	Delete Each GrassTile
	Delete Each GrassSet
	Delete Each GQuad
	Delete Each GrassObject
	Delete Each GBlock
	Delete Each GNode
End Function

;; Loads a texture useable by Ulysses
Function LoadGrassTexture(Path$,Columns=1,Rows=1,NoMips=False)
	If NoMips Then ClearTextureFilters
	T = LoadTexture(Path,1+2+16+32)
	If NoMips Then TextureFilter "",1+8
	If Not T Then Return False
	g.GrassTile = New GrassTile
	g\Surface = CreateSurface(gGrassSet\Mesh)
	B = CreateBrush()
	BrushTexture B,T,0,0
	PaintSurface g\Surface,B
	FreeBrush B
	g\US = (Float(TextureWidth(T))/Columns)/TextureWidth(T)
	g\VS = (Float(TextureHeight(T))/Rows)/TextureHeight(T)
	g\Rows = Rows
	g\Columns = Columns
	FreeTexture T
	Return Handle(g)
End Function

;; Pass your grass sprite texture to Texture
Function CreateGrassSpawner(Texture,Frame=0,Size#=2,Mesh=0,GrowthRate%=cGrassRate,MaxAmount%=cGrassCount,FOV% = 90,On=1)
	If Texture = 0 Then Return False
	
	g.GTex = New GTex
	g\ID = Handle(g)
	g\FOV = FOV
	g\Size = Size
	g\MaxCount = MaxAmount
	g\Frame = Frame
	g\Rate = GrowthRate
	g\Tex = Object.GrassTile(Texture)
	g\On = On
	g\Index = ExistingSpawners
	g\WindDirection = WindDirection
	g\WindPower = WindPower
	g\WindSpeed = WindSpeed
	g\WindAffects = WindAffects
	g\BunchCount = cGrassBunchCount
	g\BunchRange = cGrassBunchRange
	Local MinY# = 999999
	If Mesh <> 0 Then
		HideEntity Mesh
		For n = 1 To CountSurfaces(Mesh)
			S = GetSurface(Mesh,n)
			For V = 0 To CountVertices(S)-1
				y# = VertexY(S,V)
				If y < MinY Then MinY = y
			Next
		Next
		PositionMesh Mesh,0,-MinY,0
	EndIf
	g\Mesh = Mesh
	ExistingSpawners = ExistingSpawners + 1
	Return g\ID
End Function

;; Tells a spawner to create grass sprites
Function StartGrassSpawner(Spawner)
	g.GTex = Object.GTex(Spawner)
	If g = Null Then Return False
	g\On = 1
	Return True
End Function

;; Toggles whether or not a spawner creates grass sprites
Function ToggleGrassSpawner(Spawner)
	g.GTex = Object.GTex(Spawner)
	If g = Null Then Return False
	g\On = Not g\On
	Return True
End Function

;; Sets the wind speeds for a specific spawner.  Normally you shouldn't have to use this, but in the case you want to disable the effects of wind on a mesh, for example, you can.
Function SetWind(Spawner,Affects=WindAffects,WindSpeed#=WindSpeed,WindDirection=WindDirection,WindPower#=WindPower#)
	g.GTex = Object.GTex(Spawner)
	If g = Null Then Return False
	g\WindSpeed = WindSpeed
	g\WindDirection = WindDirection
	g\WindPower = WindPower
	g\WindAffects = Affects
	Return True
End Function

;; Sets whether or not a spawner is affected by wind speeds
Function WindAffects(Spawner,Affects=WindAffects)
	g.GTex = Object.GTex(Spawner)
	If g = Null Then Return False
	g\WindAffects = Affects
	Return True
End Function

;; Tells a spawner to not create grass sprites
Function StopGrassSpawner(Spawner)
	g.GTex = Object.GTex(Spawner)
	If g = Null Then Return False
	g\On = 0
	Return True
End Function

Function SetSpawner(Spawner,Range=2.3,Amount=cGrassBunchCount)
	g.GTex = Object.GTex(Spawner)
	If g = Null Then Return False
	g\BunchRange = Range
	g\BunchCount = Amount
	Return True
End Function

;; Frees a spawner from memory
Function FreeGrassSpawner(Spawner)
	FreeEntity gGrassSet\Mesh
	gGrassSet\Mesh = CreateMesh()
	S.GTex = Object.GTex(Spawner)
	If S = Null Then Return False
	SIndex = S\Index
	
	For n.GNode = Each GNode
		For f = SIndex To OccupiedIndices
			If f < OccupiedIndices Then
				n\Occupied[f] = n\Occupied[f+1]
			EndIf
			
			If f >= ExistingSpawners Then
				n\Occupied[f] = 0
			EndIf
		Next
	Next
	
	For g.GTex = Each GTex
		If g\Index > SIndex Then
			g\Index = g\Index - 1
		EndIf
		If g\ID = Spawner Then
			For i.GSprite = Each GSprite
				If i\Parent = g Then
					i\N\Occupied[i\Parent\Index] = 0
					For k = 0 To cGrassBunchCount-1
						Delete i\Quads[k]
					Next
					Delete i
				EndIf
			Next
			Delete g
			Return True
		EndIf
	Next
	Return False
End Function

;; Creates a virtual block (top-down) that prevents grass from being grown within a specified area.  This allows somewhat finer-precision control of the grass system.
Function CreateGrassBlock(X#,Y#,Z#,Width#,Height#,Depth#)
	B.GBlock = New GBlock
	If Width < 0 Then
		X = X + Width
		Width = -Width
	EndIf
	
	If Height < 0 Then
		Y = Y + Height
		Height = -Height
	EndIf
	
	If Depth < 0 Then
		Z = Z + Depth
		Depth = -Depth
	EndIf
	
	B\X = X
	B\Y = Y
	B\Z = Z
	
	B\W = Width
	B\H = Height
	B\L = Depth
	Return Handle(B)
End Function

Function RemoveGrassBlock(Block)
	B.GBlock = Object.GBlock(Block)
	If B <> Null Then Delete B Return 1
	Return 0
End Function

;; Internal function
Function gQuickSort( L, R, RandomPivot = True )
	Local A, B, SwapA#, SwapB#, Middle#
	A = L
	B = R
	
	If RandomPivot Then
		Middle = SortArray( Rand(L, R), 0 )
	Else
		Middle = SortArray( (L+R)/2, 0 )
	EndIf
	
	While True
		
		While SortArray( A, 0 ) < Middle
			A = A + 1
			If A > R Then Exit
		Wend
		
		While  Middle < SortArray( B, 0 )
			B = B - 1
			If B < 0 Then Exit
		Wend
		
		If A > B Then Exit
		
		SwapA = SortArray( A, 0 )
		SwapB = SortArray( A, 1 )
		SortArray( A, 0 ) = SortArray( B, 0 )
		SortArray( A, 1 ) = SortArray( B, 1 )
		SortArray( B, 0 ) = SwapA
		SortArray( B, 1 ) = SwapB
		
		A = A + 1
		B = B - 1
		
		If B < 0 Then Exit
		
	Wend
	
	If L < B Then gQuickSort( L, B )
	If A < R Then gQuickSort( A, R )
End Function

;; Updates the grass system
Function UpdateGrass()
	If gGrassSet = Null Then Return False
	For t.GrassTile = Each GrassTile
		ClearSurface t\Surface
	Next
	GrassQuadsDrawn = 0
	
	For i.GSprite = Each GSprite
		
		CameraProject gGrassSet\Camera,i\X,i\Y,i\Z
		PX = ProjectedX()
		PY = ProjectedY()
		PositionEntity gGrassSet\TPivot,i\X,i\Y,i\Z
		D# = EntityDistance(gGrassSet\Camera,gGrassSet\TPivot)
		
		If (ProjectedZ() <= 0) Or ((PX < -100 Or PX > gGrassSet\W+100) And KillOffScreen)  Or D > i\Range*1.5 Then
			i\TimeOut = i\TimeOut + 1
			If i\TimeOut > GrassTimeout
				i\IDelete = 1
				For n = 0 To i\Parent\BunchCount-1
					i\Quads[n]\DAlpha = 0
				Next
			EndIf
		Else
			i\TimeOut = 0
			i\IDelete = 0
			For n = 0 To i\Parent\BunchCount-1
				i\Quads[n]\DAlpha = GrassAlpha
			Next
		EndIf
		
		If i\IDelete = 1 Then
			If i\Quads[0]\CAlpha <= .1 Then i\IDelete = 2
		EndIf
		
		If i\IDelete = 2 Then
			i\Parent\Count = i\Parent\Count - 1
			cCurrentGrassCount = cCurrentGrassCount - 1
			For n = 0 To i\Parent\BunchCount-1
				Delete i\Quads[n]
			Next
			i\N\Occupied[i\Parent\Index] = 0
			Delete i
		EndIf
	Next
	
	CX# = EntityX(gGrassSet\Camera,1)
	CY# = EntityY(gGrassSet\Camera,1)
	CZ# = EntityZ(gGrassSet\Camera,1)
	
	TFormPoint gGrassSet\OX,gGrassSet\OY,gGrassSet\OZ,0,gGrassSet\Camera
	
	Backward# = 1
	If TFormedZ() > 0 Then Backward# = TFormedZ()*.4
	
	CYaw# = EntityYaw(gGrassSet\Camera,1)
	TurnYaw = (gGrassSet\OYaw-CYaw)
	CPitch# = EntityPitch(gGrassSet\Camera,1)
	TPivot = gGrassSet\TPivot
	TPivotB = gGrassSet\TPivotB
	For g.GTex = Each GTex
		If g\On = 1 Then
			If CX <> gGrassSet\OX Or CY <> gGrassSet\OY Or CZ <> gGrassSet\OZ Or CYaw <> gGrassSet\OYaw Or CPitch <> gGrassSet\OPitch Then
				Count = g\MaxCount-g\Count
				If Count > g\Rate Then Count = g\Rate
				For j = 1 To Count
					A = (CYaw+Rnd(-g\FOV/2,g\FOV/2)+90-TurnYaw*1.5) Mod 359
					If A > 359 Then A = A - 359
					If A < 0 Then A = A + 359
					R# = Rnd(.5,gGrassSet\Radius)*Backward;*Cos(CPitch)
					X# = CX + gGrassSet\GCos[Int A] * R
					Z# = CZ + gGrassSet\GSin[Int A] * R
					PositionEntity gGrassSet\TPivot,X,0,Z
					Closest.GNode = Null
					ClD# = 5000000
					For nr.NodeRow = Each NodeRow
						If X > nr\X And X < nr\X+nr\Width Then
							If Z > nr\Z And Z < nr\Z+nr\Depth Then
								For n = 0 To NODE_SEG-1
									If X > nr\Nodes[n]\X And X < nr\Nodes[n]\X + nr\Nodes[n]\Width Then
										If Z > nr\Nodes[n]\Z And Z < nr\Nodes[n]\Z+nr\Depth Then
											Closest = nr\Nodes[n]
										EndIf
									EndIf
								Next
							EndIf
						EndIf
					Next
					
					Con = 1
					
					If Closest <> Null Then
						X = Closest\X : Y = Closest\Y : Z = Closest\Z
						For b.GBlock = Each GBlock
							If X > b\X And X < b\X+b\W And Z > b\Z And Z <b\Z+b\L And Y > b\Y And Y < b\Y + b\H  Then Con = 0 Exit
						Next
					Else
						Con = 0
					EndIf
					
					If Con Then
						If Closest\Occupied[g\Index] = 0 And Closest\U Then
							i.GSprite = New GSprite
							i\Parent = g
							i\X = Closest\X
							i\Z = Closest\Z
							i\Y = Closest\Y
							i\NX# =Closest\NX
							i\NY# = Closest\NY
							i\NZ# = Closest\NZ
							i\N = Closest
							Closest\Occupied[i\Parent\Index] = 1
							i\Range = R*Rnd(1,2)
							
							PositionEntity TPivotB,i\X,i\Y,i\Z
							RotateEntity TPivotB,0,0,0
							
							For n = 0 To g\BunchCount-1
								PositionEntity TPivot,i\X,i\Y,i\Z
								i\Quads[n] = New GQuad
								rX# = Rnd(-i\Parent\BunchRange,i\Parent\BunchRange)
								rZ# = Rnd(-i\Parent\BunchRange,i\Parent\BunchRange)
								i\Quads[n]\X = RX
								i\Quads[n]\Z = rZ
								i\Quads[n]\Size = Rnd(g\Size*.7,g\Size*2)
								i\Quads[n]\Angle = Rnd(359)
								i\Quads[n]\DAlpha = GrassAlpha
								i\Quads[n]\Reverse = Rand(0,1)
								i\Quads[n]\R = Closest\R*255
								i\Quads[n]\G = Closest\G*255
								i\Quads[n]\B = Closest\B*255
								
								RotateEntity TPivot,0,i\Quads[n]\Angle,0
								AlignToVector TPivot,i\NX,i\NY,i\NZ,2,1
								MoveEntity TPivot,rX,0,rZ
								AlignToVector TPivot,i\NX,3.6,i\NZ,2,1
								
								If i\Parent\Mesh = 0 Then								
									TFormPoint -i\Quads[n]\Size,i\Quads[n]\Size*2,0,TPivot,TPivotB
									i\Quads[n]\VX[0] = TFormedX()
									i\Quads[n]\VY[0] = TFormedY()
									i\Quads[n]\VZ[0] = TFormedZ()
									
									TFormPoint i\Quads[n]\Size,i\Quads[n]\Size*2,0,TPivot,TPivotB
									i\Quads[n]\VX[1] = TFormedX()
									i\Quads[n]\VY[1] = TFormedY()
									i\Quads[n]\VZ[1] = TFormedZ()
									
									TFormPoint i\Quads[n]\Size,0,0,TPivot,TPivotB
									i\Quads[n]\VX[2] = TFormedX()
									i\Quads[n]\VY[2] = TFormedY()
									i\Quads[n]\VZ[2] = TFormedZ()
									
									TFormPoint -i\Quads[n]\Size,0,0,TPivot,TPivotB
									i\Quads[n]\VX[3] = TFormedX()
									i\Quads[n]\VY[3] = TFormedY()
									i\Quads[n]\VZ[3] = TFormedZ()
								EndIf
								
								i\Quads[n]\rx = EntityPitch(TPivot,1)
								i\Quads[n]\ry = EntityYaw(TPivot,1)
								i\Quads[n]\rz = EntityRoll(TPivot,1)
								i\Quads[n]\gx = EntityX(TPivot,1)
								i\Quads[n]\gy = EntityY(TPivot,1)
								i\Quads[n]\gz = EntityZ(TPivot,1)
							Next
							g\Count = g\Count + 1
							cCurrentGrassCount = cCurrentGrassCount + 1
						EndIf
					EndIf
				Next
			EndIf
		EndIf
	Next
	
	gGrassSet\OX = CX
	gGrassSet\OY = CY
	gGrassSet\OZ = CZ
	gGrassSet\OYaw = CYaw
	gGrassSet\OPitch = CPitch
	
	TPivot = gGrassSet\TPivot
	
	Dim SortArray(cCurrentGrassCount-1,2)
	n = 0
	For i.GSprite = Each GSprite
		PositionEntity TPivot,i\X,i\Y,i\Z
		SortArray(n,0) = -EntityDistance(TPivot,gGrassSet\Camera)+i\Parent\Index
		SortArray(n,1) = Handle(i)
		n = n + 1
	Next
	
	If cCurrentGrassCount > 12 Then gQuickSort(0,cCurrentGrassCount-2,1)
	
	ASpeed# = AlphaFadeSpeed#/100.0
	
	TimeTaken = MilliSecs()
	WTime = MilliSecs()
	For p = 0 To cCurrentGrassCount-1
		i.GSprite = Object.GSprite(SortArray(p,1))
		If i <> Null Then
			For n = 0 To i\Parent\BunchCount-1
				i\Quads[n]\CAlpha = i\Quads[n]\CAlpha+(i\Quads[n]\DAlpha-i\Quads[n]\CAlpha)*ASpeed#
				
				If i\Quads[n]\CAlpha*i\N\A > .01 Then
					Col = (i\Parent\Frame) Mod i\Parent\Tex\Columns
					Row = (i\Parent\Frame-Col)/i\Parent\Tex\Columns
					US# = i\Parent\Tex\US
					VS# = i\Parent\Tex\VS
					U# = US * Col
					V# = VS * Row
					
					If i\Parent\WindAffects Then
						WA = ((WindDirection)+i\X*2) Mod 359
						WB = Int(WTime*WindSpeed + i\X) Mod 359
						If WA > 359 Then WA = WA - 359
						If WA < 0 Then WA = WA + 359
						If WB > 359 Then WB = WB - 359
						If WB < 0 Then WB = WB + 359
						windX# = gGrassSet\GSin[WA] * WindPower*gGrassSet\GSin[WB]
						windZ# = gGrassSet\GCos[WA] * WindPower*gGrassSet\GCos[WB]
					Else
						windX = 0
						windZ = 0
					EndIf
					
					If i\Quads[n]\Y = 0 Then i\Quads[n]\Y = EntityY(TPivot)-i\Y
					
					If i\IDelete = 0 Then
						RX# = 0 : RY# = 0 : RZ# = 0
						
						For beef = 0 To 3
							RX = RX + i\Quads[n]\VX[beef]
							RY = RY + i\Quads[n]\VY[beef]
							RZ = RZ + i\Quads[n]\VZ[beef]
						Next
						
						RX = RX / 3
						RY = RY / 3
						RZ = RZ / 3
						
						CameraProject gGrassSet\Camera,i\Quads[n]\gx,i\Quads[n]\gy,i\Quads[n]\gz
						
						i\Quads[n]\DAlpha = GrassAlpha
						
						If ProjectedZ() <= 0 Then
							i\Quads[n]\DAlpha = 0
						ElseIf i\Parent\Mesh = 0
							PX = ProjectedX()
							If PX <= -16 Or PX > gGrassSet\W+16 Then
								i\Quads[n]\DAlpha = 0
							Else
								PY = ProjectedY()
								If PY <= -16 Or PY > gGrassSet\H + 16 Then i\Quads[n]\DAlpha = 0
							EndIf
						EndIf
					Else
						i\Quads[n]\DAlpha = 0
					EndIf
					
					GrassQuadsDrawn = GrassQuadsDrawn + 1
					S = i\Parent\Tex\Surface
					
					If i\Parent\Mesh = 0 Then
						eV = AddVertex(S,i\X+i\Quads[n]\VX[0]+windX,i\Y+i\Quads[n]\VY[0],i\Z+i\Quads[n]\VZ[0]+windZ,U+i\Quads[n]\Reverse*US,V)
						
						AddVertex(S,i\X+i\Quads[n]\VX[1]+windX,i\Y+i\Quads[n]\VY[1],i\Z+i\Quads[n]\VZ[1]+windZ,U+US-i\Quads[n]\Reverse*US,V)
						
						AddVertex(S,i\X+i\Quads[n]\VX[2],i\Y+i\Quads[n]\VY[2],i\Z+i\Quads[n]\VZ[2],U+US-i\Quads[n]\Reverse*US,V+VS)
						
						AddVertex(S,i\X+i\Quads[n]\VX[3],i\Y+i\Quads[n]\VY[3],i\Z+i\Quads[n]\VZ[3],U+i\Quads[n]\Reverse*US,V+VS)
						
						AddTriangle S,eV,eV+1,eV+2
						AddTriangle S,eV+2,eV+3,eV
						
						For o = 0 To 3
							VertexColor S,eV+o,i\Quads[n]\R,i\Quads[n]\G,i\Quads[n]\B,i\Quads[n]\CAlpha
						Next
					Else
						Mesh = i\Parent\Mesh
						For in = 1 To CountSurfaces(Mesh)
							Surface = GetSurface(Mesh,1)
							lv = CountVertices(S)
							PositionEntity Mesh,i\Quads[n]\gx,i\Quads[n]\gy,i\Quads[n]\gz,1
							RotateEntity Mesh,i\Quads[n]\rx+windX*3,i\Quads[n]\ry,i\Quads[n]\rz+windZ*3,1
							If i\Parent\WindAffects Then AlignToVector Mesh,windX/2,3.6,windZ/2,2,.3
							For iv = 0 To CountVertices(Surface)-1
								TFormPoint VertexX(Surface,iv)*i\Quads[n]\Size,VertexY(Surface,iv)*i\Quads[n]\Size,VertexZ(Surface,iv)*i\Quads[n]\Size,Mesh,0
								nv = AddVertex(S,TFormedX(),TFormedY(),TFormedZ(),U+VertexU(Surface,iv)*US,V+VertexV(Surface,iv)*VS,VertexW(Surface,iv))
								VertexColor S,nv,i\Quads[n]\R,i\Quads[n]\G,i\Quads[n]\B,i\Quads[n]\CAlpha
								TFormNormal VertexNX(Surface,iv),VertexNY(Surface,iv),VertexNZ(Surface,iv),Mesh,0
								VertexNormal S,nv,TFormedX(),TFormedY(),TFormedZ()
							Next
							For iv = 0 To CountTriangles(Surface)-1
								AddTriangle S,lv+TriangleVertex(Surface,iv,0),lv+TriangleVertex(Surface,iv,1),lv+TriangleVertex(Surface,iv,2)
							Next
						Next
						Surface = 0
					EndIf
				EndIf
			Next
		EndIf
	Next
	GrassUpdateTime = MilliSecs()-TimeTaken
End Function

;; Specify a mesh, if you want to specify a specific surface in the mesh then you can do that as well via the Surface argument
Function AddGrassObject(GObject,Surface=0,Lightmap$="")
	g.GrassObject = New GrassObject
	g\Entity = GObject
	g\Surface = Surface
	
	Local Mi#[2]
	Local Ma#[2]
	
	If Lightmap <> "" Then
		Tex = LoadTexture(Lightmap,1+2)
		If Tex Then
			T = TextureBuffer(Tex)
			W = TextureWidth(Tex)
			H = TextureHeight(TeX)
		EndIf
	EndIf
	
	If Surface <> 0 Then
		For n = 0 To CountVertices(Surface)-1
			TFormPoint VertexX(Surface,n),VertexY(Surface,n),VertexZ(Surface,n),GObject,0
			TX# = TFormedX()
			TY# = TFormedY()
			TZ# = TFormedZ()
			If TX < Mi[0] Then Mi[0] = TX
			If TY < Mi[1] Then Mi[1] = TY
			If TZ < Mi[2] Then Mi[2] = TZ
			
			If TX > Ma[0] Then Ma[0] = TX
			If TY > Ma[1] Then Ma[1] = TY
			If TZ > Ma[2] Then Ma[2] = TZ
		Next
	Else
		For k = 1 To CountSurfaces(GObject)
			Surface = GetSurface(GObject,k)
			For n = 0 To CountVertices(Surface)-1
				TFormPoint VertexX(Surface,n),VertexY(Surface,n),VertexZ(Surface,n),GObject,0
				TX# = TFormedX()
				TY# = TFormedY()
				TZ# = TFormedZ()
				If TX < Mi[0] Then Mi[0] = TX
				If TY < Mi[1] Then Mi[1] = TY
				If TZ < Mi[2] Then Mi[2] = TZ
				
				If TX > Ma[0] Then Ma[0] = TX
				If TY > Ma[1] Then Ma[1] = TY
				If TZ > Ma[2] Then Ma[2] = TZ
			Next
		Next
		Surface = 0
	EndIf
	If Tex Then LockBuffer(T)
	
	NodeWidth# = (Ma[0]-Mi[0])/NODE_SEG
	NodeDepth# = (Ma[2]-Mi[2])/NODE_SEG
	RowWidth# = (Ma[0]-Mi[0])
	
	For v# = 0 To NODE_SEG-1
		r.NodeRow = New NodeRow
		r\X = Mi[0]
		r\Z = Mi[2]+(Ma[2]-Mi[2])*(v/NODE_SEG)
		r\Width = RowWidth
		r\Depth = NodeDepth
		For u# = 0 To NODE_SEG-1
			If Surface <> 0 Then
				RI = Ray_Intersect_Surface(GObject,Surface,Mi[0]+(Ma[0]-Mi[0])*(u/NODE_SEG),Ma[1],Mi[2]+(Ma[2]-Mi[2])*(v/NODE_SEG),.01,Mi[1]-Ma[1],.01,0,1)
			Else
				RI = Ray_Intersect_Mesh(GObject,Mi[0]+(Ma[0]-Mi[0])*(u/NODE_SEG),Ma[1]+1,Mi[2]+(Ma[2]-Mi[2])*(v/NODE_SEG),.01,Mi[1]-Ma[1]-2,.01,0,1)
			EndIf
			
			Node = v*NODE_SEG+u
			
			g\Nodes[Node] = New GNode
			r\Nodes[u] = g\Nodes[Node]
			g\Nodes[Node]\U = RI
			
			If Tex Then
				X = (u/NODE_SEG)*W
				Y = (v/NODE_SEG)*H
				Pixel = ReadPixelFast(X,Y,T)
				cA# = Pixel Shr 24 And 255
				cR# = Pixel Shr 16 And 255
				cG# = Pixel Shr 8 And 255
				cB# = Pixel And 255
				g\Nodes[Node]\R = cR
				g\Nodes[Node]\G = cG
				g\Nodes[Node]\B = cB
				g\Nodes[Node]\A = cA/255
			Else
				g\Nodes[Node]\R = 255
				g\Nodes[Node]\G = 255
				g\Nodes[Node]\B = 255
				g\Nodes[Node]\A = 1
			EndIf
			
			If RI Then
				g\Nodes[Node]\X = IntersectedX
				g\Nodes[Node]\Y = IntersectedY
				g\Nodes[Node]\Z = IntersectedZ
				g\Nodes[Node]\NX = TriangleNX(IntersectedSurface,IntersectedTriangle)
				g\Nodes[Node]\NY = TriangleNY(IntersectedSurface,IntersectedTriangle)
				g\Nodes[Node]\NZ = TriangleNZ(IntersectedSurface,IntersectedTriangle)
				g\Nodes[Node]\Width = NodeWidth
				g\Nodes[Node]\Depth = NodeDepth
				
				If g\Nodes[Node]\NY < YNormal Then
					g\Nodes[Node]\U = 0
				EndIf
			EndIf
		Next
	Next
	If Tex Then
		UnlockBuffer(T)
		FreeTexture(Tex)
	EndIf
	
	Return Handle(g)
End Function

;; Export a nodeset
Function ExportNodes(Path$)
	FOut = WriteFile(Path$+".unltemp")
	If Not FOut Then Return False
	
	WriteString FOut,"NODESET_"
	
	For r.NodeRow = Each NodeRow
		WriteFloat FOut,r\X
		WriteFloat FOut,r\Z
		WriteFloat FOut,r\Width
		WriteFloat FOut,r\Depth
		For n = 0 To NODE_SEG-1
			i.GNode = r\Nodes[n]
			WriteFloat FOut,i\X
			WriteFloat FOut,i\Y
			WriteFloat FOut,i\Z
			WriteFloat FOut,i\Width
			WriteFloat FOut,i\Depth
			WriteFloat FOut,i\NX
			WriteFloat FOut,i\NY
			WriteFloat FOut,i\NZ
			WriteByte FOut,i\R
			WriteByte FOut,i\G
			WriteByte FOut,i\B
			WriteByte FOut,i\A*255
			WriteByte FOut,i\U
		Next
	Next
	
	CloseFile FOut
	
	bz2( 0, 9, Path$+".unltemp", Path$+".unl" )
	DeleteFile Path$+".unltemp"
	
	Return True
End Function

;; Imports a nodeset.  Nodesets should be rebuilt after -any- changes to anything grass is placed upon
Function ImportNodes(Path$)
	If FileType(Path+".unl") <> 1 Then Return False
	
	bz2( 1, 0, Path+".unl", Path+".unltemp" )
	
	FIn = ReadFile(Path$+".unltemp")
	If Not FIn Then Return False
	
	ID$ = ReadString(FIn)
	If ID$ <> "NODESET_" Then
		CloseFile FIn
		Return False
	EndIf
	
	While Not Eof(FIn)
		r.NodeRow = New NodeRow
		r\X = ReadFloat(FIn)
		r\Z = ReadFloat(FIn)
		r\Width = ReadFloat(FIn)
		r\Depth = ReadFloat(FIn)
		For n = 0 To NODE_SEG-1
			i.GNode = New GNode
			i\X = ReadFloat(FIn)
			i\Y = ReadFloat(FIn)
			i\Z = ReadFloat(FIn)
			i\Width = ReadFloat(FIn)
			i\Depth = ReadFloat(FIn)
			i\NX = ReadFloat(FIn)
			i\NY = ReadFloat(FIn)
			i\NZ = ReadFloat(FIn)
			i\R = ReadByte(FIn)
			i\G = ReadByte(FIn)
			i\B = ReadByte(FIn)
			i\A = Float(ReadByte(FIn))/255
			i\U = ReadByte(FIn)
			r\Nodes[n] = i
		Next
	Wend
	
	DeleteFile Path+".unltemp"
	
	CloseFile FIn
	Return True
End Function

;; Removes an object useable by the grass system
Function RemoveGrassObject(GObject)
	g.GrassObject = Object.GrassObject(GObject)
	If g = Null Then Return False
	For n = 0 To NODE_SEG-1
		For i = 0 To NODE_SEG-1
			Delete g\Rows[n]\Nodes[i]
		Next
		Delete g\Rows[n]
	Next
	Delete g
	Return True
End Function

;; sswift's ray-triangle intersection code, butchered by me
Function Ray_Intersect_Triangle(Px#, Py#, Pz#, Dx#, Dy#, Dz#, V0x#, V0y#, V0z#, V1x#, V1y#, V1z#, V2x#, V2y#, V2z#, Extend_To_Infinity=True, Cull_Backfaces=False)
	E1x# = V2x# - V0x#
	E1y# = V2y# - V0y#
	E1z# = V2z# - V0z#

	E2x# = V1x# - V0x#
	E2y# = V1y# - V0y#
	E2z# = V1z# - V0z#

	Hx# = (Dy# * E2z#) - (E2y# * Dz#)
	Hy# = (Dz# * E2x#) - (E2z# * Dx#)
	Hz# = (Dx# * E2y#) - (E2x# * Dy#)

	A# = (E1x# * Hx#) + (E1y# * Hy#) + (E1z# * Hz#)

	If (Cull_Backfaces = True) And (A# >= 0) Then Return False
		
	If (A# > -0.00001) And (A# < 0.00001) Then Return False
	
	F# = 1.0 / A#

	Sx# = Px# - V0x#
	Sy# = Py# - V0y#
	Sz# = Pz# - V0z#
	
	U# = F# * ((Sx# * Hx#) + (Sy# * Hy#) + (Sz# * Hz#))
	
	If (U# < 0.0) Or (U# > 1.0) Return False

	Qx# = (Sy# * E1z#) - (E1y# * Sz#)
	Qy# = (Sz# * E1x#) - (E1z# * Sx#)
	Qz# = (Sx# * E1y#) - (E1x# * Sy#)
	
	V# = F# * ((Dx# * Qx#) + (Dy# * Qy#) + (Dz# * Qz#))
	
	If (V# < 0.0) Or ((U# + V#) > 1.0) Return False

	T# = F# * ((E2x# * Qx#) + (E2y# * Qy#) + (E2z# * Qz#))
	IntersectedX# = Px+Dx*T
	IntersectedY# = Py+Dy*T
	IntersectedZ# = Pz+Dz*T

	If (T# < 0) Then Return False

	If (Extend_To_Infinity = False) And (T# > 1) Return False

	Return True
End Function

Function Ray_Intersect_Mesh(Mesh, Px#, Py#, Pz#, Dx#, Dy#, Dz#, Extend_To_Infinity=True, Cull_Backfaces=False)
	Surfaces = CountSurfaces(Mesh)
	If Surfaces > 0
		For SurfaceLoop = 1 To Surfaces
			Surface = GetSurface(Mesh, SurfaceLoop)
	
			Tris  = CountTriangles(Surface)
			For TriLoop = 0 To Tris-1
	
				V0 = TriangleVertex(Surface, TriLoop, 0)
				V1 = TriangleVertex(Surface, TriLoop, 1)
				V2 = TriangleVertex(Surface, TriLoop, 2)
		
				V0x# = VertexX#(Surface, V0)
				V0y# = VertexY#(Surface, V0)
				V0z# = VertexZ#(Surface, V0)

				V1x# = VertexX#(Surface, V1)
				V1y# = VertexY#(Surface, V1)
				V1z# = VertexZ#(Surface, V1)

				V2x# = VertexX#(Surface, V2)
				V2y# = VertexY#(Surface, V2)
				V2z# = VertexZ#(Surface, V2)

				TFormPoint V0x#, V0y#, V0z#, Mesh, 0
				V0x# = TFormedX#()
				V0y# = TFormedY#()
				V0z# = TFormedZ#()

				TFormPoint V1x#, V1y#, V1z#, Mesh, 0
				V1x# = TFormedX#()
				V1y# = TFormedY#()
				V1z# = TFormedZ#()
			
				TFormPoint V2x#, V2y#, V2z#, Mesh, 0
				V2x# = TFormedX#()
				V2y# = TFormedY#()
				V2z# = TFormedZ#()
				
				Intersected = Ray_Intersect_Triangle(Px#, Py#, Pz#, Dx#, Dy#, Dz#, V0x#, V0y#, V0z#, V1x#, V1y#, V1z#, V2x#, V2y#, V2z#, Extend_To_Infinity, Cull_Backfaces)
				If Intersected Then
					IntersectedTriangle = TriLoop
					IntersectedSurface = Surface
					Return True
				EndIf
			Next
		Next
	EndIf
	
	IntersectedTriangle = -1
	IntersectedSurface = -1
	
	Return False
End Function

;; hacked the Ray_Intersect_Mesh function so it can be used on surfaces as well
Function Ray_Intersect_Surface(Mesh, Surface, Px#, Py#, Pz#, Dx#, Dy#, Dz#, Extend_To_Infinity=True, Cull_Backfaces=False)
	Tris  = CountTriangles(Surface)
	For TriLoop = 0 To Tris-1
		V0 = TriangleVertex(Surface, TriLoop, 0)
		V1 = TriangleVertex(Surface, TriLoop, 1)
		V2 = TriangleVertex(Surface, TriLoop, 2)

		V0x# = VertexX#(Surface, V0)
		V0y# = VertexY#(Surface, V0)
		V0z# = VertexZ#(Surface, V0)

		V1x# = VertexX#(Surface, V1)
		V1y# = VertexY#(Surface, V1)
		V1z# = VertexZ#(Surface, V1)

		V2x# = VertexX#(Surface, V2)
		V2y# = VertexY#(Surface, V2)
		V2z# = VertexZ#(Surface, V2)

		TFormPoint V0x#, V0y#, V0z#, Mesh, 0
		V0x# = TFormedX#()
		V0y# = TFormedY#()
		V0z# = TFormedZ#()

		TFormPoint V1x#, V1y#, V1z#, Mesh, 0
		V1x# = TFormedX#()
		V1y# = TFormedY#()
		V1z# = TFormedZ#()
	
		TFormPoint V2x#, V2y#, V2z#, Mesh, 0
		V2x# = TFormedX#()
		V2y# = TFormedY#()
		V2z# = TFormedZ#()
		
		Intersected = Ray_Intersect_Triangle(Px#, Py#, Pz#, Dx#, Dy#, Dz#, V0x#, V0y#, V0z#, V1x#, V1y#, V1z#, V2x#, V2y#, V2z#, Extend_To_Infinity, Cull_Backfaces)
		If Intersected Then
			IntersectedTriangle = TriLoop
			IntersectedSurface = Surface
			Return True
		EndIf
	Next

	IntersectedTriangle = -1
	IntersectedSurface = -1
	
	Return False
End Function

;; some functions of my own...
;; just to note, these don't return the actual normals of a triangle, they only return the averaged normals of the vertices.  in most cases- when it comes to terrain- this is enough
Function TriangleNX#(Surface,Triangle)
	Local NX#
	For N = 0 To 2
		NX# = NX# + VertexNX(Surface,TriangleVertex(Surface,Triangle,N))
	Next
	Return NX / 3
End Function

Function TriangleNY#(Surface,Triangle)
	Local NY#
	For N = 0 To 2
		NY# = NY# + VertexNY(Surface,TriangleVertex(Surface,Triangle,N))
	Next
	Return NY / 3
End Function

Function TriangleNZ#(Surface,Triangle)
	Local NZ#
	For N = 0 To 2
		NZ# = NZ# + VertexNZ(Surface,TriangleVertex(Surface,Triangle,N))
	Next
	Return NZ / 3
End Function



;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;; EXAMPLE CODE	;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; FPS
Global FPS,LastCheck,Frames
Function GetFPS()
	Frames = Frames + 1
	
	If MilliSecs() > LastCheck+1000 Then
		LastCheck = MilliSecs()
		FPS = Frames
		Frames = 0
	EndIf
	Return FPS
End Function

;; Graphics Mode
Graphics 320,240,32,2
If FileType("grass3.tga") <> 1 Then
	bz2 1,0,"grass3.tga.bz2","grass3.tga"
	Delay 20
EndIf
ClsColor 255,255,255
Cls
Color 0,0,0
Graphics3D 800,600,32,Int(Replace(Lower(Input$("Windowed? Y/N        ")),"y","1"))+1
;; Camera
C = CreateCamera()

;; Terrain - objects no longer need pick-modes
T = LoadMesh("terrain.3ds")
ScaleEntity T,40*.4,35*.4,40*.4
TranslateEntity T,0,-70,0

;; for some reason whenever i add or remove something from this the surface indices change, so now i have to check for vertex count to know which one i need
If CountVertices(GetSurface(t,1)) > CountVertices(GetSurface(t,2)) Then
	s1 = 1
	s2 = 2
Else
	s1 = 2
	s2 = 1
EndIf

B = LoadBrush("detail.png")
GetBrushTexture(B)
PaintSurface GetSurface(T,s1),B
FreeBrush B
B = LoadBrush("path.png")
PaintSurface GetSurface(T,s2),B
FreeBrush B

;; Lighting
L = CreateLight(2)
PositionEntity L,0,500,0
LightRange L,200
LightColor L,255,255,255
AmbientLight 96,96,96

;; Grass
InitGrass(C,50,180)
Tex = LoadGrassTexture("grass3.tga",2,4)

SetSpawner(CreateGrassSpawner(Tex,0,.9/1.5,0,1,200),3,2)
SetSpawner(CreateGrassSpawner(Tex,1,1.65/1.5,0,3,200),3,7)
SetSpawner(CreateGrassSpawner(Tex,2,.8/1.5,0,1,200),2.3,3)
;SetSpawner(CreateGrassSpawner(Tex,3,1/1.5,0,1,200),2.3,1)
;SetSpawner(CreateGrassSpawner(Tex,4,1/1.5,0,1,200),2.3,1)
SetSpawner(CreateGrassSpawner(Tex,5,1.65/1.5,0,4,200),2,1)

If FileType("terrain.unl") Then
	Print "Rebuild object nodeset?  Y/N"
	Repeat
		k = Asc(Lower(Chr(WaitKey())))
	Until k = Asc("y") Or k = Asc("n")
	If k = Asc("y") Then
		Print "Rebuilding..."
		DeleteFile "terrain.unl"
		TerrainObject = AddGrassObject(T,GetSurface(T,s1))
		ExportNodes("terrain")
	Else
		Print "Loading nodeset..."
		ImportNodes("terrain")
	EndIf
	Print "Done."
Else
	TerrainObject = AddGrassObject(T,GetSurface(T,s1))
	ExportNodes("terrain")
EndIf

;; Tweening
MFPS = 60
Period = 1000/MFPS
Time = MilliSecs()-Period

S = CreateSprite()
ScaleSprite S,10,10
EntityFX S,1+16
EntityBlend S,3
EntityOrder S,-5
PositionEntity S,0,0,2
EntityColor S,255,255,255
EntityParent S,C,1

Alpha# = 1.4

CameraClsColor C,191,223,245

Repeat
	Repeat
		Elapsed = MilliSecs() - Time
	Until Elapsed
	
	Ticks = Elapsed / Period
	Tween# = Float(Elapsed Mod Period) / Float(Period)
	
	For U = 0 To Ticks
		Time = Time + Period
		
		If U = Ticks Then
			CaptureWorld
		EndIf
		
		If Alpha < .01 Then
			Alpha = 0
		Else
			Alpha# = Alpha# * .98
		EndIf
		EntityAlpha S,Alpha
		
		;; Camera
		PitchAccel# = MouseYSpeed()*.05
		PanAccel# = -MouseXSpeed()*.05
		
		Pitch# = Pitch + PitchAccel
		Pan# = Pan + PanAccel
		If Pitch > 80 Then Pitch = 80
		If Pitch < -80 Then Pitch = -80
		RotateEntity C,Pitch,Pan,0
		MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
		MoveV# =(KeyDown(200)-KeyDown(208))*.7
		MoveEntity C,0,0,MoveV
		
		;; Grass
		UpdateGrass()
		
		;; Graphics
		UpdateWorld
	Next
	
	;; Graphics
	RenderWorld Tween
	
	;; FPS
	GetFPS()
	Text 2,2,"FPS : "+FPS
	
	Flip 0
Until KeyHit(1)

;; Grass
KillGrass()

;; Graphics
ClearWorld
End
