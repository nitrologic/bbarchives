; ID: 2237
; Author: Warner
; Date: 2008-04-03 20:40:27
; Title: minib3d - converted terrain functions
; Description: minib3d conversion of a terrain optimizing code

Type TTerrain Extends TMesh

	Field HeightMap#[512, 512]

'---------------------------------------------------------------------------------------------------------------
'													Create()
'---------------------------------------------------------------------------------------------------------------
	Method Create:TTerrain()
	
		Local m:TTerrain = New TTerrain
		class$ = "Terrain"
		
		AddParent(Null)
		EntityListAdd(TTerrain.entity_list)
			
		Return Self
	
	End Method
	
'---------------------------------------------------------------------------------------------------------------
'													MakeTerrainMesh()
'---------------------------------------------------------------------------------------------------------------
	Method MakeTerrainMesh(HmapImage:TPixmap, RoamMaxHeightError#=-1, MaxTileDepth%)
		
		If HmapImage = Null Then Return
			
		'clear array
		For Local i:Int = 0 To 511
		For Local j:Int = 0 To 511
			HeightMap[i,j]=0
		Next
		Next
		
		Local RoamTerrainWidth%=Max(PixmapWidth(HmapImage),PixmapHeight(HmapImage))-1
		Local RoamTerrainHeight#[RoamTerrainWidth+1+1,RoamTerrainWidth+1+1]
	
		Local maxv# =  0
		Local minv# =  256
		For Local PixX%=0 To RoamTerrainWidth
		For Local PixY%=0 To RoamTerrainWidth
			Local Pixel% = 0
			If PixX < PixmapWidth(HmapImage) Then
			If PixY < PixmapHeight(HmapImage) Then
				Pixel=ReadPixel(HmapImage,PixX,PixY)
			End If
			End If
	        RoamTerrainHeight#[PixX,PixY]=(Pixel & $FF)
			If RoamTerrainHeight#[PixX,PixY] < minv Then minv = RoamTerrainHeight#[PixX,PixY]
			If RoamTerrainHeight#[PixX,PixY] > maxv Then maxv = RoamTerrainHeight#[PixX,PixY]
		Next
		Next
		
		Local vwidth# = (maxv - minv)
			
		If vwidth = 0 Then vwidth = 1.0
		For Local PixX%=0 To RoamTerrainWidth
		For Local PixY%=0 To RoamTerrainWidth
			RoamTerrainHeight#[PixX,PixY] = (RoamTerrainHeight#[PixX,PixY] - minv) * 30.0 / vwidth
		Next
		Next
	
		If RoamMaxHeightError = -1 Then
			For Local PixY%=0 To RoamTerrainWidth
			For Local PixX%=1 To RoamTerrainWidth
				Local dd# = (RoamTerrainHeight#[PixX,PixY] - RoamTerrainHeight#[PixX-1,PixY])
				If Abs(dd) > 10 Then RoamTerrainHeight#[PixX,PixY] = RoamTerrainHeight#[PixX-1,PixY] + Sgn(dd)*10
			Next
			Next
			For Local PixX%=0 To RoamTerrainWidth
			For Local PixY%=1 To RoamTerrainWidth
				Local dd# = (RoamTerrainHeight#[PixX,PixY] - RoamTerrainHeight#[PixX,PixY-1])
				If Abs(dd) > 10 Then RoamTerrainHeight#[PixX,PixY] = RoamTerrainHeight#[PixX,PixY-1] + Sgn(dd)*10
			Next
			Next
	
			Local ee# = 0
			For Local PixY%=0 To RoamTerrainWidth
			For Local PixX%=1 To RoamTerrainWidth
				ee :+ Abs(RoamTerrainHeight#[PixX,PixY] - RoamTerrainHeight#[PixX-1,PixY])
			Next
			Next
			For Local PixX%=0 To RoamTerrainWidth
			For Local PixY%=1 To RoamTerrainWidth
				ee :+ Abs(RoamTerrainHeight#[PixX,PixY] - RoamTerrainHeight#[PixX,PixY-1])
			Next
			Next
			RoamMaxHeightError = Sqr(ee) / 264.0
			If RoamMaxHeightError < 0.1 Then RoamMaxHeightError = 0.1
		End If
		
		Local RoamMaxTriangles%=RoamTerrainWidth*RoamTerrainWidth*2-1
		
		Local RoamBaseNeighbor%[RoamMaxTriangles*10+1]
		Local RoamBaseNeighborFlag%[RoamMaxTriangles*10+1]
	
		For Local CurrentNumber%=1 To RoamMaxTriangles
		  FindTriBaseNeighbor(RoamBaseNeighbor, RoamBaseNeighborFlag, CurrentNumber)
		Next
	
		CreateLand(RoamTerrainHeight, RoamBaseNeighbor, RoamBaseNeighborFlag, ..
					RoamTerrainWidth, RoamMaxHeightError, MaxTileDepth)
		
	End Method

'---------------------------------------------------------------------------------------------------------------
'													CreateLand()
'---------------------------------------------------------------------------------------------------------------
Method CreateLand(RoamTerrainHeight:Float[,], RoamBaseNeighbor:Int[], RoamBaseNeighborFlag:Int[], ..
					RoamTerrainWidth:Int, RoamMaxHeightError#, MaxTileDepth%)

	Local RoamCriticalTriLevel%=RoamTerrainWidth*RoamTerrainWidth-1
	Local MinTileSizeLevel%=2^MaxTileDepth
		
	Local RoamTriangle:TBank[2]
	Local RoamSurface:TSurface = CreateSurface()
	
	RoamTriangle[0]=CreateBank(RoamTerrainWidth*RoamTerrainWidth*2)
	RoamTriangle[1]=CreateBank(RoamTerrainWidth*RoamTerrainWidth*2)
	Local RoamVertex:Int[,] = New Int[RoamTerrainWidth+1,RoamTerrainWidth+1]
	AddVertex RoamSurface,0,0,0
	RoamBreakTriangle(RoamTerrainHeight, RoamBaseNeighbor, RoamBaseNeighborFlag, RoamTriangle, 0, ..
						RoamTerrainWidth,RoamTerrainWidth,RoamTerrainWidth,RoamTerrainWidth,0,1,0, ..
						RoamCriticalTriLevel, MinTileSizeLevel, RoamMaxHeightError)
	RoamBreakTriangle(RoamTerrainHeight, RoamBaseNeighbor, RoamBaseNeighborFlag, RoamTriangle, ..
						RoamTerrainWidth,0,0,0,0,RoamTerrainWidth,1,1, RoamCriticalTriLevel, MinTileSizeLevel, ..
						RoamMaxHeightError)
	RoamCreateTriangle(RoamTerrainHeight, RoamTriangle, RoamVertex, RoamSurface, 0,RoamTerrainWidth, ..
						RoamTerrainWidth,RoamTerrainWidth,RoamTerrainWidth,0,1,0,RoamTerrainWidth)
	RoamCreateTriangle(RoamTerrainHeight, RoamTriangle, RoamVertex, RoamSurface, RoamTerrainWidth,0,0,0,0, ..
						RoamTerrainWidth,1,1,RoamTerrainWidth)
	UpdateNormals
	
End Method

'---------------------------------------------------------------------------------------------------------------
'													FindTriBaseNeighbor()
'---------------------------------------------------------------------------------------------------------------
Method FindTriBaseNeighbor(RoamBaseNeighbor:Int[], RoamBaseNeighborFlag:Int[], Number%)
	If RoamBaseNeighbor[number]>0 
		Return 
	EndIf
	If Number<4
        Select Number
                Case 1
                        RoamBaseNeighbor[Number]=1
                        RoamBaseNeighborFlag[Number]=1
                Case 2
                        RoamBaseNeighbor[Number]=0
                        RoamBaseNeighborFlag[Number]=0
                Case 3
                        RoamBaseNeighbor[Number]=0
                        RoamBaseNeighborFlag[Number]=0
        End Select
		Return
	EndIf

	Local Parent%=Number Shr 1
	Local ParentLeftChild%=(Parent Shl 1)
	Local ParentRightChild%=(Parent Shl 1)+1
	
	Local PraParent%=Parent Shr 1
	Local PraParentLeftChild%=(PraParent Shl 1)
	Local PraParentRightChild%=(PraParent Shl 1)+1
	
	Local PraParentRightChildRightChild%
	Local PraParentLeftChildLeftChild%
	
	If Number=ParentLeftChild And Parent=PraParentLeftChild
       PraParentRightChildRightChild=(PraParentRightChild Shl 1)+1
       RoamBaseNeighbor[Number]=PraParentRightChildRightChild
       RoamBaseNeighborFlag[Number]=0
       RoamBaseNeighbor[PraParentRightChildRightChild]=Number
       RoamBaseNeighborFlag[PraParentRightChildRightChild]=0
		Return
	EndIf

	If Number=ParentRightChild And Parent=PraParentRightChild
       PraParentLeftChildLeftChild=(PraParentLeftChild Shl 1)
       RoamBaseNeighbor[Number]=PraParentLeftChildLeftChild
       RoamBaseNeighborFlag[Number]=0
       RoamBaseNeighbor[PraParentLeftChildLeftChild]=Number
       RoamBaseNeighborFlag[PraParentLeftChildLeftChild]=0
		Return
	EndIf

	Local PraParentBaseNeighbor%=RoamBaseNeighbor[PraParent]
	Local PraParentBaseNeighborFlag%=RoamBaseNeighborFlag[PraParent]

	If PraParentBaseNeighbor=0
        RoamBaseNeighbor[Number]=0
        RoamBaseNeighborFlag[Number]=0
        Return
	EndIf

	Local PraParentBaseNeighborRightChild%
	Local PraParentBaseNeighborRightChildLeftChild%
	
	If Number=ParentRightChild And Parent=PraParentLeftChild
	   PraParentBaseNeighborRightChild=(PraParentBaseNeighbor Shl 1)+1
	   PraParentBaseNeighborRightChildLeftChild=(PraParentBaseNeighborRightChild Shl 1)
	   RoamBaseNeighbor[Number]=PraParentBaseNeighborRightChildLeftChild
	   RoamBaseNeighborFlag[Number]=PraParentBaseNeighborFlag
	   RoamBaseNeighbor[PraParentBaseNeighborRightChildLeftChild]=Number
	   RoamBaseNeighborFlag[PraParentBaseNeighborRightChildLeftChild]=PraParentBaseNeighborFlag
	   Return
	EndIf

	Local PraParentBaseNeighborLeftChild%
	Local PraParentBaseNeighborLeftChildRightChild%
	
	If Number=ParentLeftChild And Parent=PraParentRightChild
		PraParentBaseNeighborLeftChild=PraParentBaseNeighbor Shl 1
		PraParentBaseNeighborLeftChildRightChild=(PraParentBaseNeighborLeftChild Shl 1)+1
		RoamBaseNeighbor[Number]=PraParentBaseNeighborLeftChildRightChild
		RoamBaseNeighborFlag[Number]=PraParentBaseNeighborFlag
		RoamBaseNeighbor[PraParentBaseNeighborLeftChildRightChild]=Number
		RoamBaseNeighborFlag[PraParentBaseNeighborLeftChildRightChild]=PraParentBaseNeighborFlag
	EndIf

End Method

'---------------------------------------------------------------------------------------------------------------
'													RoamBreakTriangle()
'---------------------------------------------------------------------------------------------------------------
Method RoamBreakTriangle(RoamTerrainHeight:Float[,], RoamBaseNeighbor:Int[], RoamBaseNeighborFlag:Int[], ..
							 RoamTriangle:TBank[], x0%,z0%,x1%,z1%,x2%,z2%,Number%,Branch%, ..
							 RoamCriticalTriLevel%, MinTileSizeLevel%, RoamMaxHeightError#)

	If Number>RoamCriticalTriLevel
        PokeByte(RoamTriangle[Branch],Number,0)
        Return
	EndIf
	
	Local xC%,zC%
	Local LeftChild%, RightChild%
	
	If number >= BankSize(RoamTriangle[0]) Then 
		Print "out of memory"
		Return
	End If
	
	If Number<MinTileSizeLevel
        xC=(x0+x2)/2
        zC=(z0+z2)/2
        LeftChild=(Number Shl 1)
        RightChild=(Number Shl 1)+1
        PokeByte(RoamTriangle[Branch],Number,1)
        RoamBreakTriangle(RoamTerrainHeight, RoamBaseNeighbor, RoamBaseNeighborFlag, RoamTriangle, ..
							x1,z1,xC,zC,x0,z0,LeftChild,Branch, RoamCriticalTriLevel, MinTileSizeLevel, ..
							RoamMaxHeightError)
        RoamBreakTriangle(RoamTerrainHeight, RoamBaseNeighbor, RoamBaseNeighborFlag, RoamTriangle, ..
							x2,z2,xC,zC,x1,z1,RightChild,Branch, RoamCriticalTriLevel, MinTileSizeLevel, ..
							RoamMaxHeightError)
        Return
	EndIf

	If PeekByte(RoamTriangle[Branch],Number)=1
        xC=(x0+x2)/2
        zC=(z0+z2)/2
        LeftChild=(Number Shl 1)
        RightChild=(Number Shl 1)+1
        RoamBreakTriangle(RoamTerrainHeight, RoamBaseNeighbor, RoamBaseNeighborFlag, RoamTriangle, ..
							x1,z1,xC,zC,x0,z0,LeftChild,Branch, RoamCriticalTriLevel, MinTileSizeLevel, ..
							RoamMaxHeightError)
        RoamBreakTriangle(RoamTerrainHeight, RoamBaseNeighbor, RoamBaseNeighborFlag, RoamTriangle, ..
							x2,z2,xC,zC,x1,z1,RightChild,Branch, RoamCriticalTriLevel, MinTileSizeLevel, ..
							RoamMaxHeightError)
        Return
	EndIf

	xC=(x0+x2)/2
	zC=(z0+z2)/2
	
	Local DeltaHeight#=Abs(RoamTerrainHeight[xC,zC]-(RoamTerrainHeight[x0,z0]+RoamTerrainHeight[x2,z2])*0.5)

	If (DeltaHeight < RoamMaxHeightError) Then Return

	xC=(x0+x2)/2
	zC=(z0+z2)/2
	LeftChild=(Number Shl 1)
	RightChild=(Number Shl 1)+1
	PokeByte(RoamTriangle[Branch],Number,1)
	RoamBreakTriangle(RoamTerrainHeight, RoamBaseNeighbor, RoamBaseNeighborFlag, RoamTriangle, ..
	x1,z1,xC,zC,x0,z0,LeftChild,Branch, RoamCriticalTriLevel, MinTileSizeLevel, ..
	RoamMaxHeightError)
	RoamBreakTriangle(RoamTerrainHeight, RoamBaseNeighbor, RoamBaseNeighborFlag, RoamTriangle, ..
	x2,z2,xC,zC,x1,z1,RightChild,Branch, RoamCriticalTriLevel, MinTileSizeLevel, ..
	RoamMaxHeightError)
	If RoamBaseNeighbor[Number]>0
	    RoamForceSplitTri(RoamBaseNeighbor, RoamBaseNeighborFlag, RoamTriangle, ..
		RoamBaseNeighbor[Number],Branch ~ RoamBaseNeighborFlag[Number])
	EndIf

End Method

'---------------------------------------------------------------------------------------------------------------
'													RoamForceSplitTri()
'---------------------------------------------------------------------------------------------------------------
Method RoamForceSplitTri(RoamBaseNeighbor:Int[], RoamBaseNeighborFlag:Int[], RoamTriangle:TBank[], Number%, ..
							Branch%)
	If PeekByte(RoamTriangle[Branch],Number)=1
        Return
	EndIf
	PokeByte(RoamTriangle[Branch],Number,1)
	If RoamBaseNeighbor[Number]>0
        RoamForceSplitTri(RoamBaseNeighbor, RoamBaseNeighborFlag, RoamTriangle, RoamBaseNeighbor[Number], ..
							Branch ~ RoamBaseNeighborFlag[Number])
	EndIf
	Local Parent%=Number Shr 1
	RoamForceSplitTri(RoamBaseNeighbor, RoamBaseNeighborFlag, RoamTriangle, Parent,Branch)
End Method

'---------------------------------------------------------------------------------------------------------------
'													RoamCreateTriangle()
'---------------------------------------------------------------------------------------------------------------
	Method RoamCreateTriangle(RoamTerrainHeight:Float[,], RoamTriangle:TBank[], RoamVertex:Int[,], ..
								RoamSurface:TSurface, x0%,z0%,x1%,z1%,x2%,z2%,..
	Number%,Branch%,RoamTerrainWidth%)
	
		 Local xC%, zC%
		 Local LeftChild%
	  	 Local RightChild%
	
	  	 If Number >= BankSize(RoamTriangle[0]) Then 
			Print "out of memory"
			Return
		 End If
		
		 If PeekByte(RoamTriangle[Branch],Number)=1
	        xC=(x0+x2)/2
	        zC=(z0+z2)/2
	        LeftChild=(Number Shl 1)
	        RightChild=(Number Shl 1)+1
			If RoamSurface.CountVertices() > 16000 Then
				Print "new surface"
				RoamSurface = CreateSurface()
				RoamVertex = New Int[RoamTerrainWidth+1,RoamTerrainWidth+1]
			End If
	        RoamCreateTriangle(RoamTerrainHeight, RoamTriangle, RoamVertex, RoamSurface, x1,z1,xC,zC,x0,z0,..
									LeftChild,Branch,RoamTerrainWidth)
	        RoamCreateTriangle(RoamTerrainHeight, RoamTriangle, RoamVertex, RoamSurface, x2,z2,xC,zC,x1,z1,..
									RightChild,Branch,RoamTerrainWidth)
	        Return
		Else
	
	        If RoamVertex[x0,z0]=0
	                RoamVertex[x0,z0]=AddVertex(RoamSurface,x0,RoamTerrainHeight[x0,z0],z0,x0,z0)
	        EndIf
	
	        If RoamVertex[x1,z1]=0
	                RoamVertex[x1,z1]=AddVertex(RoamSurface,x1,RoamTerrainHeight[x1,z1],z1,x1,z1)
	        EndIf
	
	        If RoamVertex[x2,z2]=0
	                RoamVertex[x2,z2]=AddVertex(RoamSurface,x2,RoamTerrainHeight[x2,z2],z2,x2,z2)
	        EndIf
	
	        AddTriangle (RoamSurface,RoamVertex[x0,z0],RoamVertex[x1,z1],RoamVertex[x2,z2])
			AddHeightTriangle(x0,RoamTerrainHeight[x0,z0],z0, ..
	       					  x1,RoamTerrainHeight[x1,z1],z1, ..
							  x2,RoamTerrainHeight[x2,z2],z2)
		EndIf
	
	End Method

'---------------------------------------------------------------------------------------------------------------
'												AddHeightTriangle()
'---------------------------------------------------------------------------------------------------------------
	Method AddHeightTriangle(x1#,y1#,z1#,x2#,y2#,z2#,x3#,y3#,z3#)
			
		If z3 < z1 Then
			Swap_Ter(x1,x3)
			Swap_Ter(y1,y3)
			Swap_Ter(z1,z3)
		End If
		If z2 < z1 Then
			Swap_Ter(x1,x2)
			Swap_Ter(y1,y2)
			Swap_Ter(z1,z2)
		End If
		If z3 < z2 Then
			Swap_Ter(x2,x3)
			Swap_Ter(y2,y3)
			Swap_Ter(z2,z3)
		End If
		
		Local dx12# = (x2 - x1) / (z2 - z1)
		Local dx13# = (x3 - x1) / (z3 - z1)
		Local dx23# = (x3 - x2) / (z3 - z2)
		
		Local ax# = x1
		Local bx# = x1
		
		Local x#, z#
	
		Local vv1# = PointDistToLine_Ter(x1, z1, x2, z2, x3, z3)
		Local vv2# = PointDistToLine_Ter(x2, z2, x1, z1, x3, z3)
		Local vv3# = PointDistToLine_Ter(x3, z3, x1, z1, x2, z2)
		
		For z = z1 To z3
			If z = z2 Then ax = x2
			
			Local s1#,s2#
			If ax < bx Then
				s1 = ax s2 = bx
			Else
				s1 = bx s2 = ax
			End If
			
			For x = s1 To s2
				Local v1# = PointDistToLine_Ter(x, z, x2,z2,x3,z3) / vv1
				Local v2# = PointDistToLine_Ter(x, z, x1,z1,x3,z3) / vv2
				Local v3# = PointDistToLine_Ter(x, z, x1,z1,x2,z2) / vv3
				Local h# = (v1 * y1) + (v2 * y2) + (v3 * y3)			
				SetHeightmapPixel(x, h, z)
			Next
			
			If z < z2 Then 
				ax = ax + dx12
			Else
				ax = ax + dx23
			End If
			bx = bx + dx13
		Next
			
	End Method

'---------------------------------------------------------------------------------------------------------------
'												Swap()
'---------------------------------------------------------------------------------------------------------------
	Method Swap_Ter(a# Var, b# Var)
		Local c# = a
		a = b
		b = c
	End Method

'---------------------------------------------------------------------------------------------------------------
'												SetHeightmapPixel()
'---------------------------------------------------------------------------------------------------------------
	Method SetHeightMapPixel(x#,y#,z#)
			
		x# =Floor(x)
		z# =Floor(z)
		If x < 0 Then Return
		If z < 0 Then Return
		If x > 511 Then Return
		If z > 511 Then Return
		Heightmap[x, z] = y
		
	End Method
	
	Method ReadHeightMapHeight#(x#,z#)
	
		Local fx% = Floor(x)
		Local fz% = Floor(z)
		If (fx = x) And (fz = z) Then Return ReadHeightMapPixel(x, z)
		If (fx <> x) And (fz <> z) Then
			'get integer position
			Local a:Int = Floor(x)
			Local b:Int = Floor(z)
		
			'get fractional part (range 0..1)
			Local c1# = x - Floor(x)
		  	Local d1# = z - Floor(z)
			'and invert it
			Local c2# = 1 - c1
			Local d2# = 1 - d1
			
			'get four points around position
			Local v00# = ReadHeightMapPixel(a + 0, b + 0)
			Local v10# = ReadHeightMapPixel(a + 1, b + 0)
			Local v01# = ReadHeightMapPixel(a + 0, b + 1)
			Local v11# = ReadHeightMapPixel(a + 1, b + 1)
			'interpolate
			Local v0# = (c2 * v00) + (c1 * v10)
			Local v1# = (c2 * v01) + (c1 * v11)	
			Local v# = (d2 * v0) + (d1 * v1)
			Return v
		End If
		
		If (fx <> x) Then
			'get integer position
			Local a:Int = Floor(x)
		
			'get fractional part (range 0..1)
			Local c1# = x - Floor(x)
			'and invert it
			Local c2# = 1 - c1
			
			'get two points around position
			Local v00# = ReadHeightMapPixel(a + 0, z)
			Local v10# = ReadHeightMapPixel(a + 1, z)
			'interpolate
			Local v0# = (c2 * v00) + (c1 * v10)
			Return v0
		End If
		
		If (fz <> z) Then
			'get integer position
			Local b:Int = Floor(z)
		
			'get fractional part (range 0..1)
		  	Local d1# = z - Floor(z)
			'and invert it
			Local d2# = 1 - d1
			
			'get two points around position
			Local v01# = ReadHeightMapPixel(x, b + 0)
			Local v11# = ReadHeightMapPixel(x, b + 1)
			'interpolate
			Local v1# = (d2 * v01) + (d1 * v11)	
			Return v1
		End If
		
	End Method

'---------------------------------------------------------------------------------------------------------------
'												ReadHeightmapPixel()
'---------------------------------------------------------------------------------------------------------------
	Method ReadHeightMapPixel#(x#,z#)
	
		x# =Floor(x)
		z# =Floor(z)
		If x < 0 Then Return 0
		If z < 0 Then Return 0
		If x > 511 Then Return 0
		If z > 511 Then Return 0
		Return Heightmap[x, z]
		
	End Method

'---------------------------------------------------------------------------------------------------------------
'												PointToPointDist()
'---------------------------------------------------------------------------------------------------------------
'get distance between two points
	Function PointToPointDist_Ter#(x1#,y1#,x2#,y2#)
		
		Local dx# = x1-x2
		Local dy# = y1-y2
		
		Return Sqr(dx*dx + dy*dy)
		
	End Function

'---------------------------------------------------------------------------------------------------------------
'												MinDistPointLine()
'---------------------------------------------------------------------------------------------------------------
	'find distance point->line (thanks tomtoad)
	Function PointDistToLine_Ter#(px#,py#,x1#,y1#,x2#,y2#)
	
		If x1 = x2 And y1 = y2 Then Return PointToPointDist_Ter(px,py,x1,y1)
		
		Local sx# = x2-x1
		Local sy# = y2-y1
		
		Local q# = ((px-x1) * (x2-x1) + (py - y1) * (y2-y1)) / (sx*sx + sy*sy)
		
		If q < 0.0 Then q = 0.0
		If q > 1.0 Then q = 1.0
		
		Return PointToPointDist_Ter(px,py,(1-q)*x1+q*x2,(1-q)*y1 + q*y2)
	
	End Function
 
'---------------------------------------------------------------------------------------------------------------
'												GetHeight()
'---------------------------------------------------------------------------------------------------------------
	Method GetHeight#(x#, z#)
	
		TFormPoint x, 0, z, Null, Self
		Local y# = ReadHeightmapHeight(TFormedX(), TFormedZ())
		TFormPoint TFormedX(), y, TFormedZ(), Self, Null
		Return TFormedY()
		
	End Method

End Type

'---------------------------------------------------------------------------------------------------------------
'													LoadTerrain()
'---------------------------------------------------------------------------------------------------------------
'usage: 
'		Local terr:TTerrain = LoadTerrain("heightmap.bmp")
'
'		RoamMaxHeightError  = cutoff value terrain steepness
'		MaxTileDepth		= cutoff depth for splitting triangles
'
Function LoadTerrain:TTerrain(filename$, RoamMaxHeightError#=-1, MaxTileDepth%=8)

	Local HmapImage:TPixmap

	If FileType(filename$) = 1 Then
		HmapImage = LoadPixmap(filename$)
		If HmapImage <> Null Then HmapImage = ResizePixmap(HmapImage, 512, 512)
	End If
	
	Local m:TTerrain = New TTerrain.Create()
	If HmapImage <> Null Then m.MakeTerrainMesh(HmapImage,RoamMaxHeightError,MaxTileDepth)

	Return m

End Function

'---------------------------------------------------------------------------------------------------------------
'													TerrainY()
'---------------------------------------------------------------------------------------------------------------
'usage:
'
'		Print TerrainY(terr, 256, 0, 256)
'
'Terrain extends TMesh, but don't use Mesh deform commands (PositionMesh)
'Else it screws up the heightmap reading
'
Function TerrainY#(t:TTerrain, x#, z#)

	If t = Null Then Return 0
	Return t.GetHeight(x, z)

End Function

'Heightmap is scaled to 512x512. I needed that for myself. Change it in LoadTerrain
'but also, change the HeightMap array size, and the array clearing routine in MakeTerrainMesh
'Not that input heightmap should be square. Not sure if it has to be a power of two
'
'The height is rescaled to a range 30. If you want to change that in MakeTerrainMesh.
'The automatic adjustment is based on that.
'So along, also change
'			RoamMaxHeightError = Sqr(ee) / 264.0
'
