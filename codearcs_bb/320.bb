; ID: 320
; Author: Per Jonsson
; Date: 2002-05-11 16:36:11
; Title: Save terrain as heightmap bmp
; Description: Save terrain as heightmap bmp

Function SaveTerrainHeightmap(terrainhandle,filename$)
	gridsize=TerrainSize(terrainhandle)
	img=CreateImage(gridsize,gridsize)
	ib=ImageBuffer(img)
	LockBuffer ib
	For x=1 To gridsize
		For y=1 To gridsize
			WritePixel x,gridsize-y-1,Floor(TerrainHeight#(terrainhandle,x,y)*16777215.0),ib
		Next
	Next
	UnlockBuffer ib
	SaveImage(img,filename$)	
End Function
