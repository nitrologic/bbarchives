; ID: 2443
; Author: Yasha
; Date: 2009-03-25 11:05:53
; Title: Terrain Erosion
; Description: Fluvial (rain) and thermal (cracking) erosion for procedural terrains

Graphics3D 1024,768,32,6
SetBuffer BackBuffer()

;SeedRnd(MilliSecs())

Local heightMap=LoadImage("heightmap.bmp"),terrainBank=CreateBank(),terrainScale#=20
Local heightMap2=CopyImage(heightMap),wireMode

HeightmapToBank heightMap,terrainBank,terrainScale

Local blitzTerrain=BlitzTerrainFromBank(terrainBank,ImageWidth(heightMap),ImageHeight(heightMap),terrainScale)

Local light=CreateLight()						;Setup light&camera
PositionEntity light,-100,100,-100
Local centrecam=CreatePivot(),camera=CreateCamera(centrecam)
PositionEntity camera,0,26,(ImageWidth(heightMap)+ImageHeight(heightMap))/2.6
PointEntity camera,centrecam
PointEntity light,centrecam


Local SC_FPS=60,ctime		;"CPU breathing code" - nothing to do with erosion
Local rtime=Floor(1000.0/SC_FPS),limited=True


While Not KeyDown(1)
	ctime=MilliSecs()
	
	TurnEntity centrecam,0,KeyDown(205)-KeyDown(203),0
	MoveEntity camera,0,0,KeyDown(200)-KeyDown(208)
	
	If KeyHit(28)		;Erode!
		;Play with these parameters
		ErodeThermal(terrainBank,50,0.0625*terrainScale,ImageWidth(heightMap),ImageHeight(heightMap))
		ErodeFluvial(terrainBank,50,0.01,0.5,ImageWidth(heightMap),ImageHeight(heightMap))
		
		;Rebuild the demo terrain
		FreeEntity blitzTerrain
		blitzTerrain=BlitzTerrainFromBank(terrainBank,ImageWidth(heightMap),ImageHeight(heightMap),terrainScale)
		
		;Update the "eroded" heightmap image
		BankToHeightmap terrainBank,heightMap2
	EndIf
	
	If KeyHit(17)
		wireMode=Not wireMode
		WireFrame wireMode
	EndIf
	
	RenderWorld
	Text 50,25,"Press Enter to apply erosion, W to toggle wireframe, Esc to exit"
	DrawImage heightMap,50,75
	Text 50,50,"Original"
	DrawImage heightMap2,100+ImageWidth(heightMap),75
	Text 100+ImageWidth(heightMap),50,"Eroded"
	
	Delay rtime-(MilliSecs()-ctime)-(limited+1)		;Free any spare CPU time
	Flip limited
Wend
End




Function ErodeFluvial(bank,iterations,solubility#,deposition#,xSize,zSize)		;Fluvial erosion.
	Local i,x,z,rainTable,dMax#,hiPointIndex,index,indexHeight#,rainHeight#
	Local zs4=zSize*4	;Just to speed things up below
	
	rainTable=CreateBank(BankSize(bank))
	
	For i=1 To iterations
		For index=0 To (xSize*zSize*4)-4 Step 4	;Add water & dissolve terrain
			PokeFloat rainTable,index,PeekFloat(rainTable,index)+solubility
			PokeFloat bank,index,PeekFloat(bank,index)-solubility
		Next
		
		For x=0 To xSize-1
			For z=0 To zSize-1
				
				;Identify dmax and high point
				dMax=0
				index=4*(x*zSize+z)
				indexHeight=PeekFloat(bank,index)+PeekFloat(rainTable,index)
				
				If z>0
					If x>0
						If indexHeight-(PeekFloat(bank,(index-zs4)-4)+PeekFloat(rainTable,(index-zs4)-4))>dMax
							hiPointIndex=(index-zs4)-4
							dMax=indexHeight-(PeekFloat(bank,hiPointIndex)+PeekFloat(rainTable,hiPointIndex))
						EndIf
					EndIf
					If indexHeight-(PeekFloat(bank,index-4)+PeekFloat(rainTable,index-4))>dMax
						hiPointIndex=index-4
						dMax=indexHeight-(PeekFloat(bank,hiPointIndex)+PeekFloat(rainTable,hiPointIndex))
					EndIf
					If x<xSize-1
						If indexHeight-(PeekFloat(bank,(index+zs4)-4)+PeekFloat(rainTable,(index+zs4)-4))>dMax
							hiPointIndex=(index+zs4)-4
							dMax=indexHeight-(PeekFloat(bank,hiPointIndex)+PeekFloat(rainTable,hiPointIndex))
						EndIf
					EndIf
				EndIf
				
				If x<xSize-1
					If indexHeight-(PeekFloat(bank,index+zs4)+PeekFloat(rainTable,index+zs4))>dMax
						hiPointIndex=index+zs4
						dMax=indexHeight-(PeekFloat(bank,hiPointIndex)+PeekFloat(rainTable,hiPointIndex))
					EndIf
				EndIf
				
				If z<zSize-1
					If x>0
						If indexHeight-(PeekFloat(bank,(index-zs4)+4)+PeekFloat(rainTable,(index-zs4)+4))>dMax
							hiPointIndex=(index-zs4)+4
							dMax=indexHeight-(PeekFloat(bank,hiPointIndex)+PeekFloat(rainTable,hiPointIndex))
						EndIf
					EndIf
					If indexHeight-(PeekFloat(bank,index+4)+PeekFloat(rainTable,index+4))>dMax
						hiPointIndex=index+4
						dMax=indexHeight-(PeekFloat(bank,hiPointIndex)+PeekFloat(rainTable,hiPointIndex))
					EndIf
					If x<xSize-1
						If indexHeight-(PeekFloat(bank,(index+zs4)+4)+PeekFloat(rainTable,(index+zs4)+4))>dMax
							hiPointIndex=(index+zs4)+4
							dMax=indexHeight-(PeekFloat(bank,hiPointIndex)+PeekFloat(rainTable,hiPointIndex))
						EndIf
					EndIf
				EndIf
				
				If x>0
					If indexHeight-(PeekFloat(bank,index-zs4)+PeekFloat(rainTable,index-zs4))>dMax
						hiPointIndex=index-zs4
						dMax=indexHeight-(PeekFloat(bank,hiPointIndex)+PeekFloat(rainTable,hiPointIndex))
					EndIf
				EndIf
				
				;Movement of water
				If dMax>0
					rainHeight=PeekFloat(rainTable,index)
					If rainHeight+PeekFloat(rainTable,hiPointIndex)+PeekFloat(bank,hiPointIndex)<=indexHeight-rainHeight
						PokeFloat rainTable,index,0
						PokeFloat rainTable,hiPointIndex,PeekFloat(rainTable,hiPointIndex)+rainHeight
					EndIf
					If rainHeight+PeekFloat(rainTable,hiPointIndex)+PeekFloat(bank,hiPointIndex)>=indexHeight
						PokeFloat rainTable,index,rainHeight-(dMax/2.0)
						PokeFloat rainTable,hiPointIndex,PeekFloat(rainTable,hiPointIndex)+(dMax/2.0)
					EndIf
					If rainHeight+PeekFloat(rainTable,hiPointIndex)+PeekFloat(bank,hiPointIndex)>indexHeight-rainHeight
						If rainHeight+PeekFloat(rainTable,hiPointIndex)+PeekFloat(bank,hiPointIndex)<indexHeight
							PokeFloat rainTable,index,(rainHeight-(dMax-rainHeight))/2.0
							PokeFloat rainTable,hiPointIndex,PeekFloat(rainTable,hiPointIndex)+(rainHeight-(rainHeight-(dMax-rainHeight))/2.0)
						EndIf
					EndIf
				EndIf
			Next
		Next
		
		For index=0 To (xSize*zSize*4)-4 Step 4	;Evaporate water & deposit sediment
			rainHeight=PeekFloat(rainTable,index)
			PokeFloat rainTable,index,rainHeight-rainHeight*deposition
			PokeFloat bank,index,PeekFloat(bank,index)+rainHeight*deposition
		Next
	Next
	
	For index=0 To (xSize*zSize*4)-4 Step 4
		PokeFloat bank,index,PeekFloat(bank,index)+PeekFloat(rainTable,index)
	Next
	
	FreeBank rainTable
End Function

Function ErodeThermal(bank,iterations,talus#,xSize,zSize)	;Thermal erosion. Target mesh, no. of iterations, talus angle, terrain dimensions
	Local i,x,z,dMax#,hiPointIndex,index,indexHeight#
	
	Local zs4=zSize*4	;Just to speed things up below
	
	For i=1 To iterations
		For x=0 To xSize-1
			For z=0 To zSize-1
				
				;Identify dmax and high point
				dMax#=0
				index=4*(x*zSize+z)
				indexHeight=PeekFloat(bank,index)
				
				If z>0
					If x>0
						If indexHeight-PeekFloat(bank,(index-zs4)-4)>dMax Then hiPointIndex=(index-zs4)-4:dMax=indexHeight-PeekFloat(bank,hiPointIndex)
					EndIf
					If indexHeight-PeekFloat(bank,index-4)>dMax Then hiPointIndex=index-4:dMax=indexHeight-PeekFloat(bank,hiPointIndex)
					If x<(xSize-1)
						If indexHeight-PeekFloat(bank,(index+zs4)-4)>dMax Then hiPointIndex=(index+zs4)-4:dMax=indexHeight-PeekFloat(bank,hiPointIndex)
					EndIf
				EndIf
				
				If x<(xSize-1)
					If indexHeight-PeekFloat(bank,index+zs4)>dMax Then hiPointIndex=index+zs4:dMax=indexHeight-PeekFloat(bank,hiPointIndex)
				EndIf
				
				If z<(zSize-1)
					If x>0
						If indexHeight-PeekFloat(bank,(index-zs4)+4)>dMax Then hiPointIndex=(index-zs4)+4:dMax=indexHeight-PeekFloat(bank,hiPointIndex)
					EndIf
					If indexHeight-PeekFloat(bank,index+4)>dMax Then hiPointIndex=index+4:dMax=indexHeight-PeekFloat(bank,hiPointIndex)
					If x<(xSize-1)
						If indexHeight-PeekFloat(bank,(index+zs4)+4)>dMax Then hiPointIndex=(index+zs4)+4:dMax=indexHeight-PeekFloat(bank,hiPointIndex)
					EndIf
				EndIf
				
				If x>0
					If indexHeight-PeekFloat(bank,index-zs4)>dMax Then hiPointIndex=index-zs4:dMax=indexHeight-PeekFloat(bank,hiPointIndex)
				EndIf
				
				If dMax>talus
					PokeFloat bank,index,indexHeight-(dMax/2)
					PokeFloat bank,hiPointIndex,PeekFloat(bank,hiPointIndex)+(dMax/2)
				EndIf
				
			Next
		Next
	Next
End Function


;The following functions are for demonstration purposes only and are in no way guaranteed to be safe
;===================================================================================================

Function HeightmapToBank(image,bank,scale#)
	Local x,y
	ResizeBank bank,ImageHeight(image)*ImageWidth(image)*4
	LockBuffer ImageBuffer(image)
	For x=0 To ImageWidth(image)-1
		For y=0 To ImageHeight(image)-1
			PokeFloat bank,4*(x*ImageHeight(image)+y),((ReadPixelFast(x,y,ImageBuffer(image)) And $00FF0000) Shr 16)/255.0*scale
		Next
	Next
	UnlockBuffer ImageBuffer(image)
End Function

Function BankToHeightmap(bank,image)	;This function doesn't check the size of the image or bank!
	Local x,y,hiPoint#,loPoint#,scale#,grey
	
	hiPoint=PeekFloat(bank,0):loPoint=hiPoint
	
	For x=0 To ImageWidth(image)-1
		For y=0 To ImageHeight(image)-1
			scale=PeekFloat(bank,4*(x*ImageHeight(image)+y))
			If scale>hiPoint Then hiPoint=scale
			If scale<loPoint Then loPoint=scale
		Next
	Next
	
	scale=255.0/(hiPoint-loPoint)
	
	LockBuffer ImageBuffer(image)
	For x=0 To ImageWidth(image)-1
		For y=0 To ImageHeight(image)-1
			grey=(PeekFloat(bank,4*(x*ImageHeight(image)+y))-loPoint)*scale		;Normalise the values to 0-255
			WritePixelFast x,y,(grey Shl 16) Or (grey Shl 8) Or grey,ImageBuffer(image)
		Next
	Next
	UnlockBuffer ImageBuffer(image)
End Function

Function BlitzTerrainFromBank(bank,xSize,ySize,scale#)	;Create a Blitz terrain with the bank's data. DEMO PURPOSES ONLY
	Local terrain,x,y
	
	;Create a terrain of large enough sides for this bank
	If Ceil(Log(xSize)/Log(2))>Ceil(Log(ySize)/Log(2))
		terrain=CreateTerrain(2^Ceil(Log(xSize)/Log(2)))
	Else
		terrain=CreateTerrain(2^Ceil(Log(ySize)/Log(2)))
	EndIf
	
	TerrainDetail terrain,12000,True
	TerrainShading terrain,True
	MoveEntity terrain,-(xSize/2.0),0,-(ySize/2.0)	;Centre the relevant bit
	ScaleEntity terrain,1,scale,1
	
	For x=0 To xSize-1
		For y=0 To ySize-1
			ModifyTerrain terrain,x,y,PeekFloat(bank,(x*ySize+y)*4)/scale
		Next
	Next
	
	Return terrain
End Function
