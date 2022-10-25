; ID: 859
; Author: indiepath
; Date: 2003-12-16 12:06:09
; Title: Shadow Mapper
; Description: Ultra Fast shadow mapper for blitz terrains

; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
; 			ULTRA FAST TERRAIN SHADOW MAPPER
;	       bY Tim Fisher a.k.a Flynn (C) 2003 v0.2
; ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

; I am sure this can be optimised!!!! :)


Graphics 640,480,16,2


; Load Your Terrain bitmap file
Global termap = LoadImage("terrain01.bmp")
Global LMSize = ImageWidth(termap)

; Initialise the mapping arrays
Dim ter#(1,1)
Dim Lmap#(1,1)


time1 = MilliSecs()

; Initialise the arrays
Initialise(termap,LMSize)


; Render the LightMap/Shadowmap. RenderLMAP(SunXPos,SunYPos,SunZPos,Lightmapsize)
shadow = RenderLMAP(4000,-1000,7000,LMSize)


SetBuffer BackBuffer()

; Do some blurring of the image (makes it look better!!)
LMBLurIMage(shadow,1)




DrawImage termap,0,0
DrawImage shadow,260,0
time2 = MilliSecs() - time1
Text 0,0,"FINISHED in "+time2 + "ms"
Text 0,10,"Hit Any Key to Save Image"
Flip

WaitKey
SaveImage (shadow,"shadow.bmp")

End

; ---------------------------------------------------------------
; Renders the Light Map according to the position of the sun
; sunX,SunY,sunZ	=	Suns Position relative to image origin
; LMAPSize			=   Size in Pixels of the Terrain Image
; ---------------------------------------------------------------

Function RenderLMAP(sunX,sunY,sunZ,LMAPSize)

	shadow = CreateImage(LMAPSize,LMAPSize)
	SetBuffer ImageBuffer(shadow)
	ClsColor 255,255,255
	Cls
	
	For x = 0 To LMAPSize
		For y = 0 To LMAPSize
			If LockedLine(x,y,ter(x,y),sunX,sunY,sunZ) Then LMAP(x,y) = 100
			
		Next
	Next

	SetBuffer ImageBuffer(shadow)
	LockBuffer ImageBuffer(shadow)
	For x = 0 To LMAPSize
		For y = 0 To LMAPSize
			col = lmap(x,y)
			If col <> 255 Then WritePixelFast (x,y,col Shl 16 Or col Shl 8 Or col)
		Next
		Next
	UnlockBuffer ImageBuffer(shadow)
	Return shadow
End Function

; ------------------------------------------------------------------------
; Initialises the arrays and popultates according to the terrain map data
; termap			=	the terrain map to use
; LMAPSize			=   Size in Pixels of the Terrain Image
; I have used arrays since I need to do many look ups and if I used 
; ReadPixelFast everytime I needed some info then the whole thing would 
; take minutes and not seconds!!!!
; ------------------------------------------------------------------------

Function Initialise(termap,LMapSize)
	Dim ter#(LMapSize,LMapSize)
	Dim Lmap#(LMapSize,LMapSize)
	
	SetBuffer ImageBuffer(termap)
	LockBuffer ImageBuffer(termap)

	For x = 0 To LMapSize
		For y = 0 To LMapSize
			col = ReadPixelFast( x,y)
			ter(x,y) = col Shr 16 And $FF
			Lmap(x,y) = 255
		Next
	Next
	UnlockBuffer ImageBuffer(termap)
End Function

; ---------------------------------------------------------------
; This creates a ray from the specific pixel to the sun,
; if the ray hits a point on the Terrainmap that is higher than
; the projected pixels vector then that pixel is shadowed. 
; x1,y1,z1 			=   The Pixel to check for Shadows
; x2,y2,z2			=   The position of the sun
; ---------------------------------------------------------------

Function LockedLine(x1#,y1#,z1#,x2#,y2#,z2#)
	
	Local steps,xI#
	
	x2 = x2-x1
	y2 = y2-y1
	z2 = z2-z1
	If Abs(x2)>Abs(y2) steps = Abs(x2) Else steps = Abs(y2)
	xI = x2 / steps
	y2 = Y2 / steps
	z2 = z2 / steps
	
	While (x1 <= LMSize) And (y1 <= LMSize) And (z1 <= LMSize) And (x1 => 0) And (y1 => 0) And (z1 => 0)
	
		; does the ray collide with the terrain??
		If (ter#(x1,y1) > z1) Then Return True
	
		x1=x1+xI:y1=y1+y2:z1=z1+z2
		
	Wend
	Return False
End Function

; --------------------------------------------------------------------
; An image blur routine I borrowed off of BlitzBasic
; -------------------------------------------------------------------

Function LMBlurImage(Image, radius = 1)

	ImgBuf = ImageBuffer(Image)
	
	LockBuffer(ImgBuf)
	
	W% = ImageWidth(Image)
	H% = ImageHeight(Image)

	; Go thru all the pixels
	For y% = 0 To H-1
		For x% = 0 To W-1
		
			; Measure the box to get the pixel samples from
			ix1 = x - radius
			iy1 = y - radius
			ix2 = x + radius
			iy2 = y + radius
			
			; Prevent it going out of bound
			If ix1 < 0 Then ix1 = 0
			If iy1 < 0 Then iy1 = 0
			If ix2 > W-1 Then ix2 = W-1
			If iy2 > H-1 Then iy2 = H-1
			
			r = 0 : g = 0 : b = 0
			num = 0
			
			; Run thru all the sampled box
			For y2% = iy1 To iy2
				For x2% = ix1 To ix2
					
					; Sum the sampled pixel 
					argb = ReadPixelFast(x2, y2, ImgBuf) And $FFFFFF
					ar = (argb Shr 16 And %11111111)
					ag = (argb Shr 8 And %11111111)
					ab = (argb And %11111111)
					
					r = r + ar
					g = g + ag
					b = b + ab
					
					num = num + 1
				Next	
			Next
			
			; Get the average value
			r = r / num
			g = g / num
			b = b / num

			rgb = b Or (g Shl 8) Or (r Shl 16)
			WritePixelFast(x, y, rgb, ImgBuf)

		Next
	Next
	
	UnlockBuffer(ImgBuf)
	
End Function
