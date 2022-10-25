; ID: 1813
; Author: markcw
; Date: 2006-09-13 15:36:15
; Title: SaveImageDDS function
; Description: Saves an image to DDS file format.

;SaveImageDDS example code

Graphics3D 640,480,0,2 ;Setup the scene
AppTitle "Save Image DDS"
SetBuffer BackBuffer()

camera=CreateCamera()
light=CreateLight()
RotateEntity light,45,45,0

image=MakeTestImage(128,128) ;Create a test image
;image=LoadImage("test.bmp") ;Alternatively load a bmp
;image=LoadImage("test.png") ;Alternatively load a png
;image=LoadImage("test.jpg") ;Alternatively load a jpg

alpha=MakeTestAlpha(ImageWidth(image),ImageHeight(image)) ;A test alpha

filename$="test.dds" ;DDS Filename
format=5 ;Set DDS format
;alpha=0 ;If we don't want an alpha channel set "alpha" to 0
ok=SaveImageDDS(image,filename$,format,alpha)

cube=CreateCube() ;Create a cube to texture
PositionEntity cube,0,0,4
If format=1 Then flags=1+4 Else flags=1+2 ;Set either masked/alpha flags
tex=LoadTexture(filename$,flags) ;Now load the new DDS file
If format=1 Or format=3 Or format=5
 EntityTexture cube,tex,0,0 ;Only texture cube if valid format
EndIf

cube2=CreateCube() ;Create a cube behind the first cube
PositionEntity cube2,0,2,8

While Not KeyHit(1) ;Main loop
 RenderWorld()

 TurnEntity cube,0.4,0.3,0.2 ;Rotate the cubes
 TurnEntity cube2,-0.4,-0.3,-0.2

 If space ;If space key was hit
  DrawImage image,50,50 ;Draw image
  If alpha DrawImage alpha,ImageWidth(image)+100,50 ;Draw alpha image
 EndIf
 If KeyHit(57) Then space=Not space ;Space key

 Text 0,0,"Hit space to show image and alpha maps"
 Text 0,20,"ok="+ok+" file="+filename+" format="+format+" alpha="+alpha

 Flip
Wend

Function MakeTestImage(width,height)

 Local image,x,y,rgb
 image=CreateImage(width,height)
 LockBuffer(ImageBuffer(image))
 For y=0 To ImageHeight(image)-1
  For x=0 To ImageWidth(image)-1
   rgb=y+(y*256)+(x*256^2) ;Gradient color
   WritePixelFast x,y,rgb,ImageBuffer(image)
  Next
 Next
 UnlockBuffer(ImageBuffer(image))
 SetBuffer ImageBuffer(image)
 Color 255,255,255 : Oval 40,40,30,30
 Color 0,0,0 : Text 50,50,"DXTC" : Color 255,255,255
 SetBuffer BackBuffer()
 Return image

End Function

Function MakeTestAlpha(width,height)

 Local alpha,x,y,rgb
 alpha=CreateImage(width,height)
 LockBuffer(ImageBuffer(alpha))
 For y=0 To ImageHeight(alpha)-1
  For x=0 To ImageWidth(alpha)-1
   rgb=(y*2)+((y*2)*256)+((y*2)*256^2) ;Grayscale
   If Not x Mod 8 Then rgb=$7F7F7F ;Grid lines
   If Not y Mod 8 Then rgb=$7F7F7F
   If Not x Mod 16 Then rgb=$FFFFFF
   If Not y Mod 16 Then rgb=$FFFFFF
   WritePixel x,y,rgb,ImageBuffer(alpha)
  Next
 Next
 UnlockBuffer(ImageBuffer(alpha))
 Return alpha

End Function

;SaveImageDDS, by markcw on 13 Sep 06
;MipMap support, by Tom C. on 12 Jul 07

;Description:
;This function works like SaveImage except it saves DDS files.
;Set the dxt mode with the format parameter 1-5 (or 0 for uncompressed RGB only) 
;the alphaimg parameter is for an alpha image.
;If you omit it (or set it to 0) you get no alpha.
;Of course you can use the same image handle for alpha image
;the last optional parameter indicates a mipmap generation,
;0 for all possible mipmaps, 1 for only the main image (default)
;Return value is False if fails, True if succeeds.

Function SaveImageDDS(image,filename$,format,alphaimg=0,mipmaps=1)
 ;image=image handle, filename$, format=optional compression format 0..5
 ;alphaimg=optional alpha image handle, zero if no alpha required
 ;mipmaps=count of mipmaps, if given the main picture will scaled down and saved to the file
 ;Uses MakeEmptyAlpha, MAX, MakeFourCC, ColorHighest, ColorTotal, Color565

 Local width=ImageWidth(image)
 Local height=ImageHeight(image)
 Local imagebuf,alphabuf

 ;DXT3/5, if no alpha image then create empty white 
 If alphaimg=0 And format<>1 Then
	alphaimg=MakeEmptyAlpha(width,height)
	alphaimg_created=True
 EndIf

 ;Determine mipmap count
 If mipmaps=0 Then
	c=MAX(width,height)
	mipmaps=Log(c)/Log(2)+1
 EndIf

 Local dwwidth,dwheight,flags1,flags2,caps1,caps2,bpp,pitch,sizebytes,w,h,mipsize
 Local bsize,bindex,fourcc,hdds,i,x,y,offset,mipoffset,ix,iy,argb
 Local color0,color1,color2,color3,color4,color5,color6,color7
 Local d0,d1,d2,d3,d4,d5,d6,d7,texel,file
 Local sclX#,sclY#

 ;dwFlags constants
 Local DDSD_CAPS=$00000001,DDSD_HEIGHT=$00000002,DDSD_WIDTH=$00000004
 Local DDSD_PITCH=$00000008,DDSD_PIXELFORMAT=$00001000
 Local DDSD_MIPMAPCOUNT=$00020000,DDSD_LINEARSIZE=$00080000
 Local DDSD_DEPTH=$00800000,DDPF_ALPHAPIXELS=$00000001
 Local DDPF_FOURCC=$00000004,DDPF_RGB=$00000040
 ;dwCaps1 constants
 Local DDSCAPS_COMPLEX=$00000008,DDSCAPS_TEXTURE=$00001000
 Local DDSCAPS_MIPMAP=$00400000
 ;dwCaps2 constants
 Local DDSCAPS2_CUBEMAP=$00000200,DDSCAPS2_CUBEMAP_POSITIVEX=$00000400
 Local DDSCAPS2_CUBEMAP_NEGATIVEX=$00000800
 Local DDSCAPS2_CUBEMAP_POSITIVEY=$00001000
 Local DDSCAPS2_CUBEMAP_NEGATIVEY=$00002000
 Local DDSCAPS2_CUBEMAP_POSITIVEZ=$00004000
 Local DDSCAPS2_CUBEMAP_NEGATIVEZ=$00008000,DDSCAPS2_VOLUME=$00200000

 ;Calculate DWORD-aligned width and height, multiple of 4
 dwwidth=(width+3)/4*4
 dwheight=(height+3)/4*4

 ;Default flags for all formats
 flags1=DDSD_CAPS Or DDSD_HEIGHT Or DDSD_WIDTH Or DDSD_PIXELFORMAT
 caps1=DDSCAPS_TEXTURE

 If format<=0 Or format>5 ;Uncompressed
	  flags1=flags1 Or DDSD_PITCH
	  flags2=DDPF_RGB
	  bpp=24
	  ;Determine Size of Bytes for each mipmap and add
	  w=dwwidth:h=dwheight
	  For i=1 To mipmaps
			mipsize=MAX(1,w)*(bpp/8) ;DWORD-aligned scanline
			sizebytes=sizebytes+mipsize*h
			w=MAX(1,w/2):h=MAX(1,h/2)
			;pitch
			If i=1 Then pitch=mipsize
	  Next
 Else ;Compressed
	  flags1=flags1 Or DDSD_LINEARSIZE
	  flags2=DDPF_FOURCC 
	  ;Determine Size of Bytes for each mipmap and add
	  If format>1 Then bytesC=16 Else bytesC=8
	  w=dwwidth:h=dwheight
	  For i=1 To mipmaps
			mipsize=(MAX(1,w/4)*MAX(1,h/4))*bytesC
			sizebytes=sizebytes+mipsize
			w=MAX(1,w/2):h=MAX(1,h/2)
			;Linear size
			If i=1 Then pitch=mipsize
	  Next
	  ;pitch=sizebytes
	  bsize=2 : bindex=0 : If format>1 Then bsize=4 : bindex=8 ;block values
	  If format=1 Then fourcc=MakeFourCC("D","X","T","1")
	  If format=2 Then fourcc=MakeFourCC("D","X","T","2")
	  If format=3 Then fourcc=MakeFourCC("D","X","T","3")
	  If format=4 Then fourcc=MakeFourCC("D","X","T","4")
	  If format=5 Then fourcc=MakeFourCC("D","X","T","5")
 EndIf

 hdds=CreateBank(128+sizebytes) ;Bank to store DDS

 ;Magic Value, DWORD
 PokeInt hdds,0,MakeFourCC("D","D","S"," ") ;dwMagic, "DDS "
 ;Surface Format Header, DDSURFACEDESC2 structure
 PokeInt hdds,4,124 ;dwSize, sizeof(DDSURFACEDESC2)
 PokeInt hdds,8,flags1 ;dwFlags, flags to indicate valid fields
 PokeInt hdds,12,dwheight ;dwHeight, image height in pixels
 PokeInt hdds,16,dwwidth ;dwWidth, image width in pixels
 PokeInt hdds,20,pitch ;dwPitchOrLinearSize, pitch or linear size
 PokeInt hdds,24,0 ;dwDepth, volume textures not supported until DX 8.0
 PokeInt hdds,28,mipmaps ;dwMipMapCount, for items with mipmap levels
 For i=1 To 11
  	PokeInt hdds,(i*4)+28,0 ;dwReserved[11]
 Next
 ;DDPIXELFORMAT structure
 PokeInt hdds,76,32 ;dwSize, sizeof(DDPIXELFORMAT)
 PokeInt hdds,80,flags2 ;dwFlags, flags to indicate valid fields
 PokeInt hdds,84,fourcc ;dwFourCC
 PokeInt hdds,88,bpp ;dwRGBBitCount
 PokeInt hdds,92,$00FF0000 ;dwRBitMask
 PokeInt hdds,96,$0000FF00 ;dwGBitMask
 PokeInt hdds,100,$000000FF ;dwBBitMask
 PokeInt hdds,104,$FF000000 ;dwRGBAlphaBitMask
 ;DDCAPS2 structure
 PokeInt hdds,108,caps1 ;dwCaps1, flags to indicate valid fields
 PokeInt hdds,112,caps2 ;dwCaps2, flags to indicate valid fields
 For i=1 To 2
  PokeInt hdds,(i*4)+112,0 ;dwReserved[2]
 Next
 PokeInt hdds,124,0 ;dwReserved2 
 mipoffset=128 ;default Offset after Header
 ;Main Surface Data, BYTE bData1[] and Attached Surfaces Data for MIPMAPS
 If format<=0 Or format>5 ;uncompressed, 24-bit
	For mips=1 To mipmaps ;Loop of optional mipmap count	
		If mips>1 Then
			;Store offset
			mipoffset=offset+pitch
			;Half of dimension
			w=MAX(1,width/2):h=MAX(1,height/2)
			sclX#=Float#(w)/Float#(width):sclY#=Float#(h)/Float#(height)
			ScaleImage image,sclX#,sclY#
			width=w:height=h
			pitch=width*(bpp/8)
		EndIf
		;Create Buffer
		imagebuf=ImageBuffer(image)
	  	LockBuffer(imagebuf)	
	 	For y=0 To height-1
		   	offset=mipoffset+pitch*y ;next DWORD-aligned scanline

		   	For x=0 To width-1
		    	argb=ReadPixelFast(x,y,imagebuf)
		    	PokeByte hdds,offset+(x*3),argb And $000000FF ;b
		    	PokeByte hdds,offset+(x*3)+1,(argb And $0000FF00) Shr 8 ;g
		    	PokeByte hdds,offset+(x*3)+2,(argb And $00FF0000) Shr 16 ;r
	   		Next
	  	Next
	   	UnlockBuffer(imagebuf)
	Next
 Else ;Compressed
  For mips=1 To mipmaps ;Loop of optional mipmap count	
	If mips>1 Then
		;Store offset
		mipoffset=offset+dwwidth*bsize
		;Half of dimension
		w=MAX(1,width/2):h=MAX(1,height/2)
		sclX#=Float#(w)/Float#(width):sclY#=Float#(h)/Float#(height)
		ScaleImage image,sclX#,sclY#
		;If not the same image-handle scale the alpha image too
		If image<>alphaimg And alphaimg>0 Then ScaleImage alphaimg,sclX#,sclY#
		width=w:height=h
		;Calculate DWORD-aligned width and height, multiple of 4
 		dwwidth=(width+3)/4*4:dwheight=(height+3)/4*4
	EndIf

	;Create Buffer
	imagebuf=ImageBuffer(image)
	If alphaimg Then alphabuf=ImageBuffer(alphaimg)
	LockBuffer(imagebuf)
	LockBuffer(alphabuf)
	For y=0 To dwheight-1 Step 4
	   offset=mipoffset+MAX(1,dwwidth/4)*(y*bsize) ;Next block-aligned scanline
	   For x=0 To dwwidth-1 Step 4
	   If format=2 Or format=3 ;DXT2,DXT3
		    ;Find color in alpha block and set each alpha texel
		     For iy=0 To 3
		      For ix=0 To 3
		       If x+ix<width And y+iy<height ;Not out of bounds
		        	argb=ReadPixelFast(ix+x,iy+y,alphabuf) ;Use alpha map
		       Else
			   		argb=0 ;Black
			   EndIf
		       i=ColorHighest(argb)/17 : If i>15 Then i=15 ;Alpha color 0..15
		       texel=PeekShort(hdds,offset+(x*bsize)+(iy*2)) Or (i Shl ix*4)
		       PokeShort hdds,offset+(x*bsize)+(iy*2),texel ;wAlphaTexels[4]
		      Next
		     Next
	    EndIf
	    If format=4 Or format=5 ;DXT4,DXT5
		     ;Find highest and lowest colors in alpha block
		     color0=0 : color1=$FFFFFFFF ;color0 highest
		     For iy=0 To 3
			 	For ix=0 To 3
			       	If x+ix<width And y+iy<height ;Not out of bounds
			        	argb=ReadPixelFast(ix+x,iy+y,alphabuf) ;Use alpha map
						If ColorHighest(argb)>ColorHighest(color0) Then color0=argb
			     		If ColorHighest(argb)<ColorHighest(color1) Then color1=argb
			       	EndIf
			     Next
		     Next
			 ;Make sure color0 is the highest
			 If color1>color0 Then
				i=color0 : color0=color1 : color1=i ;Switch order
			 EndIf
		     PokeByte hdds,offset+(x*bsize),ColorHighest(color0) ;bAlpha0
		     PokeByte hdds,offset+(x*bsize)+1,ColorHighest(color1) ;bAlpha1
		     ;Set each alpha texel in block to closest alpha
		     color0=ColorHighest(color0) : color1=ColorHighest(color1)
		     For iy=0 To 3
			      For ix=0 To 3
				       If x+ix<width And y+iy<height ;Not out of bounds
				       	argb=ReadPixelFast(ix+x,iy+y,alphabuf) ;Use alpha map
				       Else
				        	argb=0 ;black
				       EndIf
				       If color0>color1 ;8-alpha block
					        color2=((6*color0)+color1)/7
					        color3=((5*color0)+(2*color1))/7
					        color4=((4*color0)+(3*color1))/7
					        color5=((3*color0)+(4*color1))/7
					        color6=((2*color0)+(5*color1))/7
					        color7=(color0+(6*color1))/7
				       Else ;6-alpha block
					        color2=((4*color0)+color1)/5
					        color3=((3*color0)+(2*color1))/5
					        color4=((2*color0)+(3*color1))/5
					        color5=(color0+(4*color1))/5
					        color6=0
					        color7=255
				       EndIf
				       d0=Abs(color0-ColorHighest(argb)) ;Get differences
				       d1=Abs(color1-ColorHighest(argb))
				       d2=Abs(color2-ColorHighest(argb))
				       d3=Abs(color3-ColorHighest(argb))
				       d4=Abs(color4-ColorHighest(argb))
				       d5=Abs(color5-ColorHighest(argb))
				       d6=Abs(color6-ColorHighest(argb))
				       d7=Abs(color7-ColorHighest(argb))
				       i=0 : If d1<d0 Then d0=d1 : i=1 ;Find closest color
				       If d2<d0 Then d0=d2 : i=2
				       If d3<d0 Then d0=d3 : i=3
				       If d4<d0 Then d0=d4 : i=4
				       If d5<d0 Then d0=d5 : i=5
				       If d6<d0 Then d0=d6 : i=6
				       If d7<d0 Then d0=d7 : i=7
				       If iy<2 ;Upper 24bit-block
					        texel=PeekInt(hdds,offset+(x*bsize)+2) And $00FFFFFF
					        If iy=0 Then texel=texel Or (i Shl (ix*3))
					        If iy=1 Then texel=texel Or (i Shl ((ix*3)+12))
					        PokeInt hdds,offset+(x*bsize)+2,texel And $00FFFFFF
				       Else ;Lower 24bit-block
					        texel=PeekInt(hdds,offset+(x*bsize)+5) And $00FFFFFF
					        If iy=2 Then texel=texel Or (i Shl (ix*3))
					        If iy=3 Then texel=texel Or (i Shl ((ix*3)+12))
					        PokeInt hdds,offset+(x*bsize)+5,texel And $00FFFFFF
				       EndIf
			      Next
		 	 Next
	    EndIf
	
		;Find highest and lowest colors in texel block
	    ;Better algorithm might be to find the most common highest/lowest colors
	    color0=0 : color1=$FFFFFFFF ;color0 highest
	    For iy=0 To 3
	     	For ix=0 To 3
	      		If x+ix<width And y+iy<height ;Not out of bounds
	       			argb=ReadPixelFast(ix+x,iy+y,imagebuf)
					If ColorTotal(argb)>ColorTotal(color0) Then color0=argb
	      			If ColorTotal(argb)<ColorTotal(color1) Then color1=argb
	      		EndIf
	     	Next
	    Next
		;Make sure color0 is the highest
		If color1>color0 Then
			i=color0 : color0=color1 : color1=i ;Switch order
		EndIf
	    ;Switch order, color1 highest to indicate DXT1a
	    If format=1 And alphaimg<>0 ;DXT1a, using alpha bit
			i=color0 : color0=color1 : color1=i 
		EndIf
	    PokeShort hdds,offset+(x*bsize)+bindex,Color565(color0) ;wColor0
	    PokeShort hdds,offset+(x*bsize)+bindex+2,Color565(color1) ;wColor1
	    ;Set each texel in block to closest color
	    color0=ColorTotal(color0) : color1=ColorTotal(color1)
	    For iy=0 To 3
		     For ix=0 To 3
			      If x+ix<width And y+iy<height ;Not out of bounds
			      	argb=ReadPixelFast(ix+x,iy+y,imagebuf)	
			      Else
			    		argb=0 ;Black
			      EndIf
			      If color0>color1 ;Four-color block
				      	color2=((2*color0)+color1)/3
				       	color3=(color0+(2*color1))/3
			      Else ;Three-color block
				       	color2=(color0+color1)/2
			       		color3=3*16 ;Max transparent color
			      EndIf
			      d0=Abs(color0-ColorTotal(argb)) ;Get differences
			      d1=Abs(color1-ColorTotal(argb))
			      d2=Abs(color2-ColorTotal(argb))
			      d3=Abs(color3-ColorTotal(argb))
			      i=0 : If d1<d0 Then d0=d1 : i=1 ;Find closest color
			      If d2<d0 Then d0=d2 : i=2
			      If d3<d0 Then d0=d3 : i=3
			      If color0>color1 And Abs(color2-color3)<8
			       		If i=3 Then i=2 ;Close and wrong order so use color2
			      EndIf
			      If format=1 And alphaimg<>0 ;DXT1a, using alpha bit
			       		If i=3 Then i=2 ;No color3 so use color2
			      EndIf
			      If x+ix>width-1 Or y+iy>height-1 ;Out of bounds
			       		If color0<color1 Then i=0 Else i=1 ;Find lowest color
			      EndIf
			      texel=PeekByte(hdds,offset+(x*bsize)+bindex+4+iy) Or (i Shl ix*2)  
			      PokeByte hdds,offset+(x*bsize)+bindex+4+iy,texel ;bTexels[4]
		     Next
	    Next
	    ;Find color in texel block and set each alpha texel
	    If format=1 And alphaimg<>0 ;DXT1a, using alpha bit
		     For iy=0 To 3
			     For ix=0 To 3
					If x+ix<width And y+iy<height ;Not out of bounds
						argb=ReadPixelFast(ix+x,iy+y,alphabuf) ;Use alpha map
						color3=3*16 ;Max transparent color
						If ColorTotal(argb)<color3 ;Set alpha texel
							texel=PeekByte(hdds,offset+(x*bsize)+bindex+4+iy) Or (3 Shl ix*2)
						    PokeByte hdds,offset+(x*bsize)+bindex+4+iy,texel ;bTexels[4]
						EndIf
					EndIf    	
	      		Next
	     	Next
    		EndIf
	  Next
	Next
	UnlockBuffer(imagebuf)
	UnlockBuffer(alphabuf)
  Next
  ;Attached Surfaces Data, BYTE bData2[]
  ;Complex
 EndIf

 ;Write DDS bank to file
 file=WriteFile(filename$)
 If Not file FreeBank hdds : Return False ;Fail code
 WriteBytes hdds,file,0,128+sizebytes
 CloseFile file
 FreeBank hdds
 ;If an alpha image was created
 If alphaimg_created=True Then FreeImage alphaimg
 Return True ;Success code

End Function

Function MakeEmptyAlpha(width,height)

 Local alpha,x,y,rgb
 alpha=CreateImage(width,height)
 LockBuffer(ImageBuffer(alpha))
 For y=0 To ImageHeight(alpha)-1
	  For x=0 To ImageWidth(alpha)-1
		rgb=$FFFFFF
	   WritePixel x,y,rgb,ImageBuffer(alpha)
	Next
 Next
 UnlockBuffer(ImageBuffer(alpha))
 Return alpha

End Function

Function MAX(a,b)

 If a=>b Then Return a Else Return b

End Function

Function MakeFourCC(c0$,c1$,c2$,c3$)

 Return (Asc(c0$)+(Asc(c1$) Shl 8)+(Asc(c2$) Shl 16)+(Asc(c3$) Shl 24))

End Function

Function ColorHighest(argb)

 Local r,g,b,a
 r=(argb And $00FF0000) Shr 16
 g=(argb And $0000FF00) Shr 8
 b=(argb And $000000FF)
 If r>g Then a=r Else a=g
 If b>a Then a=b
 Return a

End Function

Function ColorTotal(argb)

 Local r,g,b
 r=(argb And $00FF0000) Shr 16
 g=(argb And $0000FF00) Shr 8
 b=(argb And $000000FF)
 Return (r+g+b) ;0..255*3

End Function

Function Color565(argb)

 Local r,g,b
 r=(argb And $00FF0000) Shr 16 : r=(r*31/255) Shl 11 ;Bits 11..15
 g=(argb And $0000FF00) Shr 8 : g=(g*63/255) Shl 5 ;Bits 5..10
 b=(argb And $000000FF) : b=b*31/255 ;Bits 0..4
 Return (r+g+b)

End Function
