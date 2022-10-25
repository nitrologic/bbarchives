; ID: 2871
; Author: pc_tek
; Date: 2011-07-11 15:43:52
; Title: Tile2Iso
; Description: Convert to Isometric

;	Author:	JP Hamilton 2011
;	Title:	Tile2Iso.bb creates Isometric images from a uniform tile.  Floor, Left & Right
;	Inputs:  1 tile of equal sides (32x32, 64x64...etc)
;	Outputs:	1 image containing the Isometric images
Graphics3D 640,480,32,2
filein$="image.png"
fileout$="slab1.bmp"

GFX_In=LoadImage(filein$)
GFX_Out=CreateImage(ImageWidth(GFX_In)*4,ImageHeight(GFX_In)+ImageHeight(GFX_In)/2)

For x=0 To ImageWidth(GFX_In)-1
	For y=0 To ImageHeight(GFX_In)-1
		ix=x+y:iy=ImageWidth(GFX_In)/2-Int(x/2)+y/2
		WritePixel ix+0,iy,ReadPixel(x,y,ImageBuffer(GFX_In))
		WritePixel ix+1,iy,ReadPixel(x,y,ImageBuffer(GFX_In))
	Next
Next

For x=0 To ImageWidth(GFX_In)-1
	For y=0 To ImageHeight(GFX_In)-1
		ix=x:iy=y+x/2
		WritePixel ix+ImageWidth(GFX_In)*2,iy+1,ReadPixel(x,y,ImageBuffer(GFX_In))
	Next
Next

For x=0 To ImageWidth(GFX_In)-1
	For y=0 To ImageHeight(GFX_In)-1
		ix=x:iy=ImageWidth(GFX_In)/2-x/2+y
		WritePixel ix+ImageWidth(GFX_In)*3,iy,ReadPixel(x,y,ImageBuffer(GFX_In))
	Next
Next

GrabImage GFX_Out,0,0
SaveImage(GFX_Out,fileout$)
EndGraphics
End
