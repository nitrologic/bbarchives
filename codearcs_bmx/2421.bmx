; ID: 2421
; Author: JoshK
; Date: 2009-02-23 04:42:55
; Title: SavePixmapDDS
; Description: Saves a pixmap as a .dds texture file

SuperStrict

Import brl.pixmap
Import brl.bank

Const DDS_MIPMAPS:Int=1
Const DDS_MASKCORRECTION:Int=2
Const DDS_CLAMPMIPMAPSX:Int=4
Const DDS_CLAMPMIPMAPSY:Int=8

Rem
bbdoc:
Description:
This function saves a pixmap as a DDS file.
Set the dxt mode with the format parameter 1. 3, Or 5 (Or 0 For uncompressed RGB only).
The optional mipmaps flag can be set To 0 To save the first mipmap only.  The Default value is True.
<p>Format:
<br>0 - Uncompressed
<br>1 - DXTC1 (RGB)
<br>3 - DXTC3 (RGBA)
<br>5 - DXTC5 (RGBA)
<p>Flags:
<br>DDS_MIPMAPS - generates mipmaps
<br>DDS_MASKCORRECTION - eliminates dark edges on masked images
<br>DDS_CLAMPMIPMAPSX - horizontally clamps mipmap generation
<br>DDS_CLAMPMIPMAPSY - vertically clamps mipmap generation
EndRem
Function SavePixmapDDS:Int(pixmap:TPixmap,url:Object,format:Int=5,flags:Int=DDS_MIPMAPS)
	
	'Constants
	Const pf_alphapix%=$00000001
	
	'dwFlags constants
	Const DDSD_CAPS:Int=$00000001,DDSD_HEIGHT:Int=$00000002,DDSD_WIDTH:Int=$00000004
	Const DDSD_PITCH:Int=$00000008,DDSD_PIXELFORMAT:Int=$00001000
	Const DDSD_MIPMAPCOUNT:Int=$00020000,DDSD_LINEARSIZE:Int=$00080000
	Const DDSD_DEPTH:Int=$00800000,DDPF_ALPHAPIXELS:Int=$00000001
	Const DDPF_FOURCC:Int=$00000004,DDPF_RGB:Int=$00000040
	
	'dwCaps1 constants
	Const DDSCAPS_COMPLEX:Int=$00000008,DDSCAPS_TEXTURE:Int=$00001000
	Const DDSCAPS_MIPMAP:Int=$00400000
	
	'dwCaps2 constants
	Const DDSCAPS2_CUBEMAP:Int=$00000200,DDSCAPS2_CUBEMAP_POSITIVEX:Int=$00000400
	Const DDSCAPS2_CUBEMAP_NEGATIVEX:Int=$00000800
	Const DDSCAPS2_CUBEMAP_POSITIVEY:Int=$00001000
	Const DDSCAPS2_CUBEMAP_NEGATIVEY:Int=$00002000
	Const DDSCAPS2_CUBEMAP_POSITIVEZ:Int=$00004000
	Const DDSCAPS2_CUBEMAP_NEGATIVEZ:Int=$00008000,DDSCAPS2_VOLUME:Int=$00200000
	
	'Locals
	Local stream:TStream
	Local dwwidth:Int,dwheight:Int,flags1:Int,flags2:Int,caps1:Int,caps2:Int,bpp:Int,pitch:Int,sizebytes:Int,w:Int,h:Int,mipsize:Int
	Local bsize:Int,bindex:Int,fourcc:Int,hdds:TBank
	Local i:Int,x:Int,y:Int,offset:Int,mipoffset:Int,ix:Int,iy:Int,argb:Int
	Local color:Int,color0:Int,color1:Int,color2:Int,color3:Int,color4:Int,color5:Int,color6:Int,color7:Int
	Local d0:Int,d1:Int,d2:Int,d3:Int,d4:Int,d5:Int,d6:Int,d7:Int,texel:Int,a:Int
	Local sclX:Float, sclY:Float	
	Local width:Int=PixmapWidth(pixmap)
	Local height:Int=PixmapHeight(pixmap)
	Local pixmapbuf:TPixmap,c:Int
	Local mips:Int
	Local bytesC:Int
	Local alphaispresent:Int=False,n:Int
	Local mipmaps:Int,maskcorrection:Int
	
	'Options
	If (DDS_MIPMAPS & flags) mipmaps=True
	If (DDS_MASKCORRECTION & flags) maskcorrection=True
	
	'Mask correction for first mipmap
	If maskcorrection
		pixmap=PixmapFilterMaskCorrection(pixmap)
	EndIf
	
	'Open stream
	stream=WriteStream(url)
	If Not stream Return False
	
	'No format specified, so take a guess
	If format=-1
		Select pixmap.format
			Case PF_A8,PF_RGBA8888,PF_BGRA8888
				format=5
			Default
				format=1
		EndSelect
	EndIf
	
	Select format
		Case 0
			Select pixmap.format
				Case PF_A8,PF_RGBA8888,PF_BGRA8888
					alphaispresent=True
				Default
					alphaispresent=False
			EndSelect
		Case 2,3,4,5
			alphaispresent=True
		Default
			alphaispresent=False
	EndSelect
	
	'Convert to RGBA
	If pixmap.format<>PF_RGBA8888
		pixmap=ConvertPixmap(pixmap,PF_RGBA8888)
	EndIf
	
	'Calculate mipmap count
	If mipmaps
		mipmaps=Int(Log(Max(width,height))/Log(2)+1.0+0.5)
	Else
		mipmaps=1
	EndIf
	
	'Calculate DWORD-aligned width And height, multiple of 4
	dwwidth=(width+3)/4*4
	dwheight=(height+3)/4*4
	
	'Default flags For all formats
	flags1=DDSD_CAPS|DDSD_HEIGHT|DDSD_WIDTH|DDSD_PIXELFORMAT
	caps1=DDSCAPS_TEXTURE
	
	'Weird hack 
	Local alphaimg:TPixmap

	If alphaispresent=True And format>1
		alphaimg=CreatePixmap(width,height,PF_RGB888)
		For x=0 To pixmap.width-1
			For y=0 To pixmap.height-1
				color=ReadPixel(pixmap,x,y)
				a=(color & $FF000000) Shr 24
				WritePixel alphaimg,x,y,a+(a Shl 8)+(a Shl 16)'+(a Shl 24)
			Next
		Next   
	EndIf
	
	
	'================================================
	'Calculate data size
	'================================================
	
	'Uncompressed
	If format=0
		flags1=flags1|DDSD_PITCH
		If alphaispresent
			flags2=DDPF_RGB|$00000001
			bpp=32
		Else
			flags2=DDPF_RGB
			bpp=24
		EndIf
		
		'Determine Size of Bytes For each mipmap And add
		w=dwwidth
		h=dwheight
		For i=1 To mipmaps
			mipsize=Max(1,w)*(bpp/8) 'DWORD-aligned scanline
			sizebytes=sizebytes+mipsize*h
			w=Max(1,w/2)
			h=Max(1,h/2)
			If i=1 pitch=mipsize
		Next
	
	'Compressed
	Else
		
		flags1:|DDSD_LINEARSIZE
		flags2=DDPF_FOURCC 
		
		'Determine Size of Bytes For each mipmap And add
		If format>1
			bytesC=16
		Else
			bytesC=8
		EndIf
		w=dwwidth
		h=dwheight
		For i=1 To mipmaps
			mipsize=(Max(1,w/4)*Max(1,h/4))*bytesC
			sizebytes=sizebytes+mipsize
			w=Max(1,w/2)
			h=Max(1,h/2)
			'Linear size
			If i=1 pitch=mipsize
		Next
		'pitch=sizebytes
		bsize=2
		bindex=0
		If format>1
			bsize=4
			bindex=8 'block values
		EndIf
		If format=1 fourcc=MakeFourCC("D","X","T","1")
		If format=2 fourcc=MakeFourCC("D","X","T","2")
		If format=3 fourcc=MakeFourCC("D","X","T","3")
		If format=4 fourcc=MakeFourCC("D","X","T","4")
		If format=5 fourcc=MakeFourCC("D","X","T","5")
		
	EndIf
	
	'================================================
	'
	'================================================
	
	'Bank To store DDS
	hdds=CreateBank(128+sizebytes)
	For n=0 To hdds.size()-1
		hdds.PokeByte n,0
	Next
	
	'============================================
	'DDS File Header
	'============================================
	
	'Magic Value, DWORD
	PokeInt hdds,0,MakeFourCC("D","D","S"," ") 'dwMagic, "DDS "
	
	'Surface Format Header, DDSURFACEDESC2 structure
	PokeInt hdds,4,124 'dwSize, SizeOf(DDSURFACEDESC2)
	PokeInt hdds,8,flags1 'dwFlags, flags To indicate valid fields
	PokeInt hdds,12,dwheight 'dwHeight, pixmap height in pixels
	PokeInt hdds,16,dwwidth 'dwWidth, pixmap width in pixels
	PokeInt hdds,20,pitch 'dwPitchOrLinearSize, pitch Or linear size
	PokeInt hdds,24,0 'dwDepth, volume textures Not supported Until DX 8.0
	PokeInt hdds,28,mipmaps 'dwMipMapCount, For items with mipmap levels
	For i=1 To 11
		PokeInt hdds,(i*4)+28,0 'dwReserved[11]
	Next
	
	'DDPIXELFORMAT structure
	PokeInt hdds,76,32 'dwSize, SizeOf(DDPIXELFORMAT)
	PokeInt hdds,80,flags2 'dwFlags, flags To indicate valid fields
	PokeInt hdds,84,fourcc 'dwFourCC
	PokeInt hdds,88,bpp 'dwRGBBitCount
	PokeInt hdds,92,$00FF0000 'dwRBitMask
	PokeInt hdds,96,$0000FF00 'dwGBitMask
	PokeInt hdds,100,$000000FF 'dwBBitMask
	PokeInt hdds,104,$FF000000 'dwRGBAlphaBitMask
	
	'DDCAPS2 structure
	PokeInt hdds,108,caps1 'dwCaps1, flags To indicate valid fields
	PokeInt hdds,112,caps2 'dwCaps2, flags To indicate valid fields
	PokeInt hdds,116,0 'dwReserved
	PokeInt hdds,120,0 'dwReserved
	PokeInt hdds,124,0 'dwReserved
	mipoffset=128 'Default Offset after Header
	
	'==================================================
	'Save texture data
	'==================================================
	
	'==================================================
	'Uncompressed
	'==================================================
	
	If format=0
		For mips=1 To mipmaps 'Loop of optional mipmap count	
			If mips>1
				'Store offset
				mipoffset=offset+pitch
				'Half of dimension
				w=Max(1,width/2)
				h=Max(1,height/2)
				sclX:Float=Float(w)/Float(width)
				sclY:Float=Float(h)/Float(height)
				pixmap=HalfSizePixmap(pixmap,maskcorrection)
				width=w
				height=h
				pitch=width*(bpp/8)
			EndIf
			
			'Create Buffer
		 	For y=0 To height-1
			   	offset=mipoffset+pitch*y 'Next DWORD-aligned scanline
			   	For x=0 To width-1
				    	argb=pixmap.ReadPixel(x,y)
				    	PokeByte hdds,offset+(x*(bpp/8))+0,(argb & $000000FF)'b
				    	PokeByte hdds,offset+(x*(bpp/8))+1,(argb & $0000FF00) Shr 8'g
				    	PokeByte hdds,offset+(x*(bpp/8))+2,(argb & $00FF0000) Shr 16'r
					If alphaispresent
						PokeByte hdds,offset+(x*(bpp/8))+3,(argb & $FF000000) Shr 24'a
					EndIf
				Next
		  	Next
		Next
	
	'==================================================
	'Compressed
	'==================================================
	
	Else 'Compressed

		For mips=1 To mipmaps 'Loop of optional mipmap count   
			If mips>1
				'Store offset
				mipoffset=offset+dwwidth*bsize
				'Half of dimension
				w=Max(1,width/2) ; h=Max(1,height/2)
				sclX#=Float(w)/Float(width) ; sclY#=Float(h)/Float(height)
				pixmap=HalfSizePixmap(pixmap,maskcorrection)
				'SavePixmapPNG pixmap,mips+".png"
				'Scalepixmap pixmap,sclX#,sclY#
				'If Not the same pixmap-handle scale the Alpha pixmap too
				If alphaimg<>Null
					alphaimg=HalfSizePixmap(alphaimg)'Scalepixmap alphaimg,sclX#,sclY#
				EndIf
				width=w
				height=h
				'Calculate DWORD-aligned width And height, multiple of 4
				dwwidth=(width+3)/4*4
				dwheight=(height+3)/4*4
			EndIf

			'Create Buffer
			For y=0 To dwheight-1 Step 4
				offset=mipoffset+Max(1,dwwidth/4)*(y*bsize) 'Next block-aligned scanline
				For x=0 To dwwidth-1 Step 4
					
					Select format
						
						Case 2,3'DXT2,DXT3
							'Find color in Alpha block And set each Alpha texel
							For iy=0 To 3
								For ix=0 To 3
									If x+ix<width And y+iy<height 'Not out of bounds
										argb=ReadPixel(alphaimg,ix+x,iy+y) 'Use Alpha map
									Else
										argb=0 'Black
									EndIf
									i=ColorHighest(argb)/17.0+0.5
									If i>15 i=15 'Alpha color 0..15
									texel=PeekShort(hdds,offset+(x*bsize)+(iy*2))|(i Shl (ix*4))
									PokeShort hdds,offset+(x*bsize)+(iy*2),texel 'wAlphaTexels[4]
								Next
							Next
						
						Case 4,5'DXT4,DXT5
							'Find highest And lowest colors in Alpha block
							color0=0  ; color1=$FFFFFFFF 'color0 highest
							For iy=0 To 3
								For ix=0 To 3
									If x+ix<width And y+iy<height 'Not out of bounds
									argb=ReadPixel(alphaimg,ix+x,iy+y) 'Use Alpha map
									If ColorHighest(argb)>ColorHighest(color0) color0=argb
									If ColorHighest(argb)<ColorHighest(color1) color1=argb
									EndIf
								Next
							Next
							'Make sure color0 is the highest
							If color1>color0
								i=color0
								color0=color1
								color1=i 'Switch order
							EndIf
							PokeByte hdds,offset+(x*bsize),ColorHighest(color0) 'bAlpha0
							PokeByte hdds,offset+(x*bsize)+1,ColorHighest(color1) 'bAlpha1
							'Set each Alpha texel in block To closest Alpha
							color0=ColorHighest(color0) ; color1=ColorHighest(color1)
							For iy=0 To 3
								For ix=0 To 3
									If x+ix<width And y+iy<height 'Not out of bounds
										argb=ReadPixel(alphaimg,ix+x,iy+y) 'Use Alpha map
									Else
										argb=0 'black
									EndIf
									If color0>color1 '8-Alpha block
										color2=((6*color0)+color1)/7
										color3=((5*color0)+(2*color1))/7
										color4=((4*color0)+(3*color1))/7
										color5=((3*color0)+(4*color1))/7
										color6=((2*color0)+(5*color1))/7
										color7=(color0+(6*color1))/7
									Else '6-Alpha block
										color2=((4*color0)+color1)/5
										color3=((3*color0)+(2*color1))/5
										color4=((2*color0)+(3*color1))/5
										color5=(color0+(4*color1))/5
										color6=0
										color7=255
									EndIf
									d0=Abs(color0-ColorHighest(argb)) 'Get differences
									d1=Abs(color1-ColorHighest(argb))
									d2=Abs(color2-ColorHighest(argb))
									d3=Abs(color3-ColorHighest(argb))
									d4=Abs(color4-ColorHighest(argb))
									d5=Abs(color5-ColorHighest(argb))
									d6=Abs(color6-ColorHighest(argb))
									d7=Abs(color7-ColorHighest(argb))
									i=0 ; If d1<d0 Then d0=d1 ; i=1 'Find closest color
									If d2<d0 Then d0=d2 ; i=2
									If d3<d0 Then d0=d3 ; i=3
									If d4<d0 Then d0=d4 ; i=4
									If d5<d0 Then d0=d5 ; i=5
									If d6<d0 Then d0=d6 ; i=6
									If d7<d0 Then d0=d7 ; i=7
									If iy<2 'Upper 24bit-block
										texel=PeekInt(hdds,offset+(x*bsize)+2) & $00FFFFFF
										If iy=0 texel=texel|(i Shl (ix*3))
										If iy=1 texel=texel|(i Shl ((ix*3)+12))
										PokeInt hdds,offset+(x*bsize)+2,texel & $00FFFFFF
										Else 'Lower 24bit-block
										texel=PeekInt(hdds,offset+(x*bsize)+5) & $00FFFFFF
										If iy=2 texel=texel|(i Shl (ix*3))
										If iy=3 texel=texel|(i Shl ((ix*3)+12))
										PokeInt hdds,offset+(x*bsize)+5,texel & $00FFFFFF
									EndIf
								Next
							Next
					EndSelect
					
					'Find highest And lowest colors in texel block
					'Better algorithm might be To find the most common highest/lowest colors
					color0=0 ; color1=$FFFFFFFF 'color0 highest
					For iy=0 To 3
						For ix=0 To 3
							If x+ix<width And y+iy<height 'Not out of bounds
								argb=ReadPixel(pixmap,ix+x,iy+y)
								If ColorTotal(argb)>ColorTotal(color0) color0=argb
								If ColorTotal(argb)<ColorTotal(color1) color1=argb
							EndIf
						Next
					Next
					'DebugLog argb
					'Make sure color0 is the highest
					If color1>color0 Then
						i=color0
						color0=color1
						color1=i 'Switch order
					EndIf
					'Switch order, color1 highest To indicate DXT1a
					If format=1 And alphaimg<>Null 'DXT1a, using Alpha bit
						i=color0
						color0=color1
						color1=i 
					EndIf
					'DebugLog (color0)+", "+(color1)
					'DebugLog Color565(color0)+", "+Color565(color1)
					PokeShort hdds,offset+(x*bsize)+bindex,Color565(color0) 'wColor0
					PokeShort hdds,offset+(x*bsize)+bindex+2,Color565(color1) 'wColor1
					'Set each texel in block To closest color
					color0=ColorTotal(color0) ; color1=ColorTotal(color1)
					For iy=0 To 3
						For ix=0 To 3
							If x+ix<width And y+iy<height 'Not out of bounds
								argb=ReadPixel(pixmap,ix+x,iy+y)   
							Else
								argb=0 'Black
							EndIf
		
							If color0>color1 'Four-color block
								color2=((2*color0)+color1)/3
								color3=(color0+(2*color1))/3
							Else 'Three-color block
								color2=(color0+color1)/2
								color3=3*16 'Max transparent color
							EndIf
							d0=Abs(color0-ColorTotal(argb)) 'Get differences
							d1=Abs(color1-ColorTotal(argb))
							d2=Abs(color2-ColorTotal(argb))
							d3=Abs(color3-ColorTotal(argb))
							i=0 ; If d1<d0 Then d0=d1 ; i=1 'Find closest color
							If d2<d0 Then d0=d2 ; i=2
							If d3<d0 Then d0=d3 ; i=3
							If color0>color1 And Abs(color2-color3)<8
								If i=3 i=2 'Close And wrong order so use color2
							EndIf
							If format=1 And alphaimg<>Null 'DXT1a, using Alpha bit
								If i=3 i=2 'No color3 so use color2
							EndIf
							If x+ix>width-1 Or y+iy>height-1 'Out of bounds
								'Find lowest color
								If color0<color1
									i=0
								Else
									i=1 
								EndIf
							EndIf
							texel=PeekByte(hdds,offset+(x*bsize)+bindex+4+iy)|(i Shl (ix*2)) 
							PokeByte hdds,offset+(x*bsize)+bindex+4+iy,texel 'bTexels[4]
						Next
					Next
					
					'Find color in texel block And set each Alpha texel
					If format=1 And alphaimg<>Null 'DXT1a, using Alpha bit
						For iy=0 To 3
							For ix=0 To 3
								If x+ix<width And y+iy<height 'Not out of bounds
									argb=ReadPixel(alphaimg,ix+x,iy+y) 'Use Alpha map
									color3=3*16 'Max transparent color
									If ColorTotal(argb)<color3 'Set Alpha texel
										texel=PeekByte(hdds,offset+(x*bsize)+bindex+4+iy) | (3 Shl (ix*2))
										PokeByte hdds,offset+(x*bsize)+bindex+4+iy,texel 'bTexels[4]
									EndIf
								EndIf       
							Next
						Next
					EndIf
				Next
			Next
		Next
	EndIf

	stream.WriteBytes hdds.buf(),hdds.size()
	stream.close()
	
	Return True
EndFunction

Private

Function MakeFourCC:Int(c0$,c1$,c2$,c3$)
	Return (Asc(c0$)+(Asc(c1$) Shl 8)+(Asc(c2$) Shl 16)+(Asc(c3$) Shl 24))
EndFunction

Function ColorHighest:Int(argb:Int)
	Local r:Int,g:Int,b:Int,a:Int
	r=(argb & $00FF0000) Shr 16
	g=(argb & $0000FF00) Shr 8
	b=(argb & $000000FF)
	If r>g a=r Else a=g
	If b>a a=b
	Return a
EndFunction

Function ColorTotal:Int(argb:Int)
	Local r:Int,g:Int,b:Int
	r=(argb & $00FF0000) Shr 16
	g=(argb & $0000FF00) Shr 8
	b=(argb & $000000FF)
	Return (r+g+b) '0..255*3
EndFunction

Function Color565:Int(argb:Int)
	Local r:Int,g:Int,b:Int
	r=(argb & $00FF0000) Shr 16
	r=Int((r*31.0/255.0+0.5)) Shl 11 'Bits 11..15
	g=(argb & $0000FF00) Shr 8
	g=Int((g*63.0/255.0+0.5)) Shl 5 'Bits 5..10
	b=(argb & $000000FF)
	b=Int(b*31/255.0+0.5) 'Bits 0..4
	Return (r+g+b)
EndFunction

Function ReadPixelA:Int(pixmap:TPixmap,x:Int,y:Int)
	Local color:Int=pixmap.ReadPixel(x,y)
	color=(color & $FF000000) Shr 24
	Return RGBA(color,color,color,color)
EndFunction

Function HalfSizePixmap:TPixmap(pixmap:TPixmap,flags:Int=0)
	Local w:Int,h:Int,x:Int,y:Int,result:TPixmap
	w=pixmap.width/2
	h=pixmap.height/2
	If w<1 w=1
	If h<1 h=1
	result=CreatePixmap(w,h,pixmap.format)
	For x=0 To w-1
		For y=0 To h-1
			result.WritePixel(x,y,ReadPixel4(pixmap,x*2,y*2,flags))
		Next
	Next
	If (DDS_MASKCORRECTION & flags)
		result=PixmapFilterMaskCorrection(result)
	EndIf
	Return result
EndFunction

Function RGBA:Int(r:Int,g:Int,b:Int,a:Int)
	Return b+(g Shl 8)+(r Shl 16)+(a Shl 24)
EndFunction

Function WrapReadPixel:Int(pixmap:TPixmap,x:Int,y:Int,flags:Int=0)
	If (DDS_CLAMPMIPMAPSX & flags)
		If x<0 x=0
		If x>pixmap.width-1 x=pixmap.width-1
	Else
		While x<0
			x=pixmap.width-x
		Wend
		While x>pixmap.width-1
			x=pixmap.width-x
		Wend
	EndIf
	If (DDS_CLAMPMIPMAPSY & flags)
		If y<0 y=0
		If y>pixmap.height-1 y=pixmap.height-1
	Else
		While y<0
			y=pixmap.height-y
		Wend
		While y>pixmap.height-1
			y=pixmap.height-y
		Wend
	EndIf
	Return pixmap.ReadPixel(x,y)
EndFunction

Function Alpha:Int(a:Int)
	Return RGBA(a,a,a,a)
EndFunction

Function PixmapFilterMaskCorrection:TPixmap(pixmap:TPixmap)
	Local x:Int,y:Int,color:Int
	Local result:TPixmap
	Local a:Byte
	Local channels:Byte[4]
	
	result=CreatePixmap(pixmap.width,pixmap.height,pixmap.format)
	For x=0 To pixmap.width-1
		For y=0 To pixmap.height-1
			color=pixmap.ReadPixel(x,y)
			MemCopy channels,Varptr color,4
			If channels[3]<128
				color=FindValidPixel(pixmap,x,y)
				MemCopy channels,Varptr color,4
				channels[3]=0
				MemCopy Varptr color,channels,4
			EndIf
			result.WritePixel(x,y,color)
		Next
	Next
	Return result
	
	Function FindValidPixel:Int(pixmap:TPixmap,x:Int,y:Int)
		Local px:Int,py:Int
		Local color:Int
		Local channel:Byte[4]
		Local i:Int=1
		Local anythingfound:Int
	
		Repeat
			
			anythingfound=False
		
			px=x+i
			py=y
			If px<=pixmap.width-1
				color=pixmap.ReadPixel(px,py)
				MemCopy channel,Varptr color,4
				If channel[3]>128 Return color
				anythingfound=True
			EndIf
			
			px=x-i
			py=y
			If px=>0
				color=pixmap.ReadPixel(px,py)
				MemCopy channel,Varptr color,4
				If channel[3]>128 Return color
				anythingfound=True
			EndIf
			
			px=x
			py=y+i
			If py<=pixmap.height-1
				color=pixmap.ReadPixel(px,py)
				MemCopy channel,Varptr color,4
				If channel[3]>128 Return color
				anythingfound=True
			EndIf
			
			px=x
			py=y-i
			If py=>0
				color=pixmap.ReadPixel(px,py)
				MemCopy channel,Varptr color,4
				If channel[3]>128 Return color
				anythingfound=True
			EndIf
			
			If Not anythingfound Exit
			i:+1
		Forever
		Return RGBA(1,0,1,0)
	EndFunction
	
EndFunction

Function ReadPixel4:Int(pixmap:TPixmap,x:Int,y:Int,flags:Int)
	Local color:Int
	Local channel:Byte[4,4]
	Local r:Int,g:Int,b:Int,a:Int
	Local Alpha:Float[4],alphasum:Float
	
	color=WrapReadPixel(pixmap,x+0,y+0,flags)
	MemCopy Varptr channel[0,0],Varptr color,4
	If channel[0,3]<128 Alpha[0]=0 Else Alpha[0]=1
	
	color=WrapReadPixel(pixmap,x+1,y+0,flags)
	MemCopy Varptr channel[1,0],Varptr color,4
	If channel[1,3]<128 Alpha[1]=0 Else Alpha[1]=1

	color=WrapReadPixel(pixmap,x+0,y+1,flags)
	MemCopy Varptr channel[2,0],Varptr color,4
	If channel[2,3]<128 Alpha[2]=0 Else Alpha[2]=1
	
	color=WrapReadPixel(pixmap,x+1,y+1,flags)
	MemCopy Varptr channel[3,0],Varptr color,4
	If channel[3,3]<128 Alpha[3]=0 Else Alpha[3]=1
	
	alphasum=Alpha[0]+Alpha[1]+Alpha[2]+Alpha[3]
	
	If (DDS_MASKCORRECTION & flags)
		If alphasum>0.0
			b=Int((Float(channel[0,0])*Alpha[0]+Float(channel[1,0])*Alpha[1]+Float(channel[2,0])*Alpha[2]+Float(channel[3,0])*Alpha[3])/alphasum)
			g=Int((Float(channel[0,1])*Alpha[0]+Float(channel[1,1])*Alpha[1]+Float(channel[2,1])*Alpha[2]+Float(channel[3,1])*Alpha[3])/alphasum)
			r=Int((Float(channel[0,2])*Alpha[0]+Float(channel[1,2])*Alpha[1]+Float(channel[2,2])*Alpha[2]+Float(channel[3,2])*Alpha[3])/alphasum)
			a=Int((Float(channel[0,3])*Alpha[0]+Float(channel[1,3])*Alpha[1]+Float(channel[2,3])*Alpha[2]+Float(channel[3,3])*Alpha[3])/alphasum)
		Else
			b=0
			g=0
			r=0
			a=0
		EndIf
	Else
		b=Int((Float(channel[0,0])+Float(channel[1,0])+Float(channel[2,0])+Float(channel[3,0]))/4.0)
		g=Int((Float(channel[0,1])+Float(channel[1,1])+Float(channel[2,1])+Float(channel[3,1]))/4.0)
		r=Int((Float(channel[0,2])+Float(channel[1,2])+Float(channel[2,2])+Float(channel[3,2]))/4.0)
		a=Int((Float(channel[0,3])+Float(channel[1,3])+Float(channel[2,3])+Float(channel[3,3]))/4.0)
	EndIf
	
	Return RGBA(r,g,b,a)
EndFunction

Public
