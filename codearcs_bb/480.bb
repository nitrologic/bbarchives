; ID: 480
; Author: Giano
; Date: 2002-11-06 04:38:10
; Title: defaultTextureEntity
; Description: Add a squared texture to a mesh

Function defaultTextureEntity(mesh, colr1=32,colg1=128,colb1=192, colr2=255,colg2=255,colb2=160)
	tex=CreateTexture( 64,64 )
	ScaleTexture tex,.125,.125
	SetBuffer TextureBuffer( tex )
	Color colr1,colg1,colb1:Rect 32,0,32,32:Rect 0,32,32,32
	Color colr2,colg2,colb2:Rect 0,0,32,32:Rect 32,32,32,32
	SetBuffer BackBuffer()
	Color 255,255,255
	EntityTexture mesh,tex
	FreeTexture tex
End Function
