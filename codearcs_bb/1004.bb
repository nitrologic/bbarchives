; ID: 1004
; Author: indiepath
; Date: 2004-04-23 03:48:56
; Title: New Terrain LightMapper
; Description: Very fast Terrain Light/Shadow mapping application. Uses Heightmaps only.

; ******************************* Terrain Shadow Lab ******************************

Include "Level_Shadow.bb"

Global gWidth 		= 	640
Global gHeight 		= 	480
Global InMap$		=	"levelterrain123.bmp"			; Your Height Map
Global OutMap$		=	"shadowmap.bmp"					; The Output File
Global Sun.Vector	=	Vector(1,2,-1)					; Position of the Sun IN VECTORS!
Global InImage
Global OutImage


Graphics gWidth,gHeight,32,2

InImage		=	LoadImage(InMap$)
MapSize		=	ImageWidth(InImage)


LEV_SHAD_Initialise(InImage,MapSize)					; Put all map info into an array
CalcNormalMap(MapSize)									; calculate Map Normals for precise lighting


Start		= 	MilliSecs()
OutImage	=	LEV_SHAD_RenderLMAP(Sun,MapSize)		; Render LightMap According to Sun Position
Timenow 	= 	MilliSecs() - Start

SaveImage (OutImage,Outmap$)

SetBuffer BackBuffer()

DrawImage 	InImage,0,0
DrawImage	OutImage,MapSize,0

Text		0,Mapsize+10,"Time : " + Timenow

Flip

WaitKey()
