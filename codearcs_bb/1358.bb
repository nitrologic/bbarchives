; ID: 1358
; Author: Snarkbait
; Date: 2005-04-23 20:54:08
; Title: .ico file loader
; Description: Load icons as image without DLL

;loadICOfile.bb include
;
; for 4-bit,8-bit and 24-bit .ico files
; will load 16x16, 32x32 and 48x48 icon files
;
;
; by snarkbait snarkbait66@gmail.com
;
; usage:
; icon = loadICOimage(iconfile$, indexnumber%, returnMask_boolean)


Dim pallette256(255,2) ; make array for 16 and 256-color icons

Type icoinfo
Field bCount,bWidth,bHeight,bColorCount,bReserved,wPlanes,wBitCount,dwBytesInRes,dwImageOffset
End Type 

Global black = argb(0,0,0)
Global white = argb(255,255,255)

Function getICOinfo$(icon$)
	icofile = ReadFile(icon$)
	If Not icofile RuntimeError "file not found"
	; icon header
	idReserved = ReadShort(icofile) ;should be 0
	idType = ReadShort(icofile);should be 1
	If idType <> 1 RuntimeError "Not a valid .ico file"
	idCount = ReadShort(icofile) ; number of icons in file
	info$ = "Icon file has " + idCount + " icons."
	For iconcount = 1 To idCount
		bWidth = ReadByte(icofile)
		bHeight = ReadByte(icofile) ; sometimes double the width, still use width
		bColorCount = ReadByte(icofile) ;# entries in pallette table
		bReserved = ReadByte(icofile) ; should be 0
		wPlanes = ReadShort(icofile) ;?
		wBitCount = ReadShort(icofile) ;bpp - if 0 use bpp info from bitmap header
		dwBytesInRes = ReadInt(icofile) ;total bytes of image including AND & XOR info
		dwImageOffset = ReadInt(icofile) ; offset to beginning of img data
		index$ = index$ + " Icon#" + iconcount + ":" + bWidth + "x" + bWidth + " - " + wBitCount + " bits per pixel   :"
	Next
	CloseFile icofile
	info$ = info$ + index$
	Return info$
End Function




Function loadICOimage(icon$,icoImageNumber = 1,returnMask = False) ; if returnMask = true, image returned will be black-and-white mask
	icofile = ReadFile(icon$)
	If Not icofile RuntimeError "file not found"
	; icon header
	idReserved = ReadShort(icofile) ;should be 0
	idType = ReadShort(icofile);should be 1
	If idType <> 1 RuntimeError "Not a valid .ico file"
	idCount = ReadShort(icofile) ; number of icons in file
	For iconcount = 1 To idCount
		;Icon Dir Entry
		ico.icoinfo = New icoinfo
		ico\bCount = iconcount
		ico\bWidth = ReadByte(icofile)
		ico\bHeight = ReadByte(icofile)
		ico\bColorCount = ReadByte(icofile) ;# entries in pallette table
		ico\bReserved = ReadByte(icofile) ; should be 0
		ico\wPlanes = ReadShort(icofile) ;?
		ico\wBitCount = ReadShort(icofile) ;bpp - if 0 use bpp info from bitmap header
		ico\dwBytesInRes = ReadInt(icofile) ;total bytes of image including AND & XOR info
		ico\dwImageOffset = ReadInt(icofile) ; offset to beginning of img data
	Next 
	
	; read image entries 
	For ico.icoinfo = Each icoinfo
		If ico\bCount = icoImageNumber	
			SeekFile(icofile,ico\dwImageOffset)
			biSize = ReadInt(icofile)
			biWidth = ReadInt(icofile)
			biHeight = ReadInt(icofile) ; x 2
			biPlanes = ReadShort(icofile)
			biBitCount = ReadShort(icofile)
			biCompression = ReadInt(icofile)
			biSizeimage = ReadInt(icofile)
			; go to bitmap info
			SeekFile(icofile,ico\dwImageOffset + biSize)
			
			Select biBitCount
			
			Case 4 ; 16-colour pallette
				; read pallette
				For color_value = 0 To 15
					For RGB = 0 To 2
						readval = ReadByte(icofile)
						pallette256(color_value,RGB) = readval ;use the same array for 16-color pallette
					Next
					useless = ReadByte(icofile) ;reserved byte - not used
				Next 
				If Not returnMask ; return actual icon image
					newimage = CreateImage(biWidth,biHeight/2)
					SetBuffer ImageBuffer(newimage)
					LockBuffer
					For ycount = biHeight/2 To 1 Step -1 ;icons are stored bottom row-up, right-to-left
						For xcount = 1 To biWidth Step 2
							readval = ReadByte(icofile)
							leftbits = readval Shr 4 ;16-color pixels are stored 2 pixels per byte, high-order 4-bits first
							rtbits = readval And $f ; get low order 4-bits for next pixel
							WritePixelFast xcount - 1,ycount - 1,argb(pallette256(leftbits,2),pallette256(leftbits,1),pallette256(leftbits,0))
							WritePixelFast xcount,ycount - 1,argb(pallette256(rtbits,2),pallette256(rtbits,1),pallette256(rtbits,0))
						Next
					Next				
					UnlockBuffer 
				Else ; return mask image
					SeekFile(icofile,ico\dwImageOffset + biSize + (2^biBitCount * 4) + (biWidth ^ 2/2)) ; go to start of mask info
					newimage = CreateImage(biWidth,biHeight/2)
					SetBuffer ImageBuffer(newimage)
					LockBuffer
					If biWidth = 32 ;32x32 icon
						For ycount = biHeight/2 To 1 Step -1
							For xcount = 0 To 3
								readval = ReadByte(icofile)
								xpos = 0
								For bits = 8 To 1 Step -1
									readbit = (readval And (2^bits - 1)) Shr (bits - 1) ; get individual bits from byte
									If readbit
										WritePixelFast (xcount * 8) + xpos,ycount - 1,white ; if bit = 1, paint pixel white
									Else
										WritePixelFast (xcount * 8) + xpos,ycount - 1,black ; if bit = 0, paint pixel black 
									EndIf
									xpos = xpos + 1
								Next
							Next
						Next
					Else
						If biWidth = 16 ; 16x16 icon
							For ycount = biHeight/2 To 1 Step -1
								For xcount = 0 To 1
									readval = ReadByte(icofile)
									xpos = 0
									For bits = 8 To 1 Step -1
										readbit = (readval And (2^bits - 1)) Shr (bits - 1)
										If readbit
											WritePixelFast (xcount * 8) + xpos,ycount - 1,white
										Else
											WritePixelFast (xcount * 8) + xpos,ycount - 1,black
										EndIf
										xpos = xpos + 1
									Next
								Next
								skip = ReadShort(icofile) ; skip past pad bytes
							Next
						Else
							If biWidth = 48 ; 48x48 icon
								For ycount = biHeight/2 To 1 Step -1
									For xcount = 0 To 5
										readval = ReadByte(icofile)
										xpos = 0
										For bits = 8 To 1 Step -1
											readbit = (readval And (2^bits - 1)) Shr (bits - 1)
											If readbit
												WritePixelFast (xcount * 8) + xpos,ycount - 1,white
											Else
												WritePixelFast (xcount * 8) + xpos,ycount - 1,black
											EndIf
											xpos = xpos + 1
										Next
									Next
									skip = ReadShort(icofile) ; skip pad bytes
								Next
							EndIf
						EndIf 
					EndIf
					UnlockBuffer
				EndIf 
				CloseFile icofile
				Return newimage
				
			Case 8
				;read pallette
				For color_value = 0 To 255
					For RGB = 0 To 2
						readval = ReadByte(icofile)
						pallette256(color_value,RGB) = readval 
					Next
					useless = ReadByte(icofile) ;reserved byte
				Next 
				
				;draw image
				If Not returnMask 
					newimage = CreateImage(biWidth,biHeight/2)
					SetBuffer ImageBuffer(newimage)
					LockBuffer
					For ycount = biHeight/2 To 1 Step -1
						For xcount = 1 To biWidth
							readval = ReadByte(icofile)
							WritePixelFast xcount - 1,ycount - 1,argb(pallette256(readval,2),pallette256(readval,1),pallette256(readval,0))
						Next
					Next				
					UnlockBuffer 
				;read AND mask
				Else
					SeekFile(icofile,ico\dwImageOffset + biSize + (2^biBitCount * 4) + (biWidth ^ 2))
					newimage = CreateImage(biWidth,biHeight/2)
					SetBuffer ImageBuffer(newimage)
					LockBuffer
					If biWidth = 32
						For ycount = biHeight/2 To 1 Step -1
							For xcount = 0 To 3
								readval = ReadByte(icofile)
								xpos = 0
								For bits = 8 To 1 Step -1
									readbit = (readval And (2^bits - 1)) Shr (bits - 1)
									If readbit
										WritePixelFast (xcount * 8) + xpos,ycount - 1,white
									Else
										WritePixelFast (xcount * 8) + xpos,ycount - 1,black
									EndIf
									xpos = xpos + 1
								Next
							Next
						Next
					Else
						If biWidth = 16
							For ycount = biHeight/2 To 1 Step -1
								For xcount = 0 To 1
									readval = ReadByte(icofile)
									xpos = 0
									For bits = 8 To 1 Step -1
										readbit = (readval And (2^bits - 1)) Shr (bits - 1)
										If readbit
											WritePixelFast (xcount * 8) + xpos,ycount - 1,white
										Else
											WritePixelFast (xcount * 8) + xpos,ycount - 1,black
										EndIf
										xpos = xpos + 1
									Next
								Next
								skip = ReadShort(icofile)
							Next
						Else
							If biWidth = 48
								For ycount = biHeight/2 To 1 Step -1
									For xcount = 0 To 5
										readval = ReadByte(icofile)
										xpos = 0
										For bits = 8 To 1 Step -1
											readbit = (readval And (2^bits - 1)) Shr (bits - 1)
											If readbit
												WritePixelFast (xcount * 8) + xpos,ycount - 1,white
											Else
												WritePixelFast (xcount * 8) + xpos,ycount - 1,black
											EndIf
											xpos = xpos + 1
										Next
									Next
									skip = ReadShort(icofile)
								Next
							EndIf
						EndIf 
					EndIf
					UnlockBuffer
				EndIf 
				CloseFile icofile
				Return newimage
	
			Case 24 ; no pallette info for 24-bit icon
				If Not returnmask
					newimage = CreateImage(biWidth,biHeight/2)
					SetBuffer ImageBuffer(newimage)
					LockBuffer
					For ycount = biHeight/2 To 1 Step -1
						For xcount = 1 To biWidth
							readblue = ReadByte(icofile) ; colors stored backwards
							readgreen = ReadByte(icofile)
							readred = ReadByte(icofile)
							WritePixelFast xcount - 1,ycount - 1,argb(readred,readgreen,readblue)
						Next
					Next				
					UnlockBuffer 
				Else
					SeekFile(icofile,ico\dwImageOffset + biSize + (biWidth ^ 2 * 3))
					newimage = CreateImage(biWidth,biHeight/2)
					SetBuffer ImageBuffer(newimage)
					LockBuffer
					If biWidth = 32
						For ycount = biHeight/2 To 1 Step -1
							For xcount = 0 To 3
								readval = ReadByte(icofile)
								xpos = 0
								For bits = 8 To 1 Step -1
									readbit = (readval And (2^bits - 1)) Shr (bits - 1)
									If readbit
										WritePixelFast (xcount * 8) + xpos,ycount - 1,white
									Else
										WritePixelFast (xcount * 8) + xpos,ycount - 1,black
									EndIf
									xpos = xpos + 1
								Next
							Next
						Next
					Else
						If biWidth = 16
							For ycount = biHeight/2 To 1 Step -1
								For xcount = 0 To 1
									readval = ReadByte(icofile)
									xpos = 0
									For bits = 8 To 1 Step -1
										readbit = (readval And (2^bits - 1)) Shr (bits - 1)
										If readbit
											WritePixelFast (xcount * 8) + xpos,ycount - 1,white
										Else
											WritePixelFast (xcount * 8) + xpos,ycount - 1,black
										EndIf
										xpos = xpos + 1
									Next
								Next
								skip = ReadShort(icofile)
							Next
						Else
							If biWidth = 48
								For ycount = biHeight/2 To 1 Step -1
									For xcount = 0 To 5
										readval = ReadByte(icofile)
										xpos = 0
										For bits = 8 To 1 Step -1
											readbit = (readval And (2^bits - 1)) Shr (bits - 1)
											If readbit
												WritePixelFast (xcount * 8) + xpos,ycount - 1,white
											Else
												WritePixelFast (xcount * 8) + xpos,ycount - 1,black
											EndIf
											xpos = xpos + 1
										Next
									Next
									skip = ReadShort(icofile)
								Next
							EndIf
						EndIf 
							 
					EndIf
						 
					UnlockBuffer
				EndIf
				CloseFile icofile
				Return newimage
				
			End Select
		EndIf 
	Next 
End Function

Function argb(red,green,blue)
	Return (blue Or (green Shl 8) Or (red Shl 16) Or ($ff Shl 24))
End Function
