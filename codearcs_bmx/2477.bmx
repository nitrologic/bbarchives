; ID: 2477
; Author: BlitzSupport
; Date: 2009-05-10 16:09:43
; Title: Retrieve image information without loading entire image
; Description: Read width/height/depth information directly from image file (no LoadImage required)

Type ImageInfo
	Field width:Int
	Field height:Int
	Field colors:Int
	Field info:String
End Type

Function GetBMPInfo:ImageInfo (f:String)

	Local image:ImageInfo = New ImageInfo

	If Lower (Left (f, 7)) = "http://"
		f = "http::" + Right (f, Len (f) - 7)
	EndIf
	
	Local bmp:TStream = LittleEndianStream (ReadFile (f))

	If bmp
		
		Try
		
			If ReadByte (bmp) = $42 And ReadByte (bmp) = $4D
	
				For Local loop:Int = 1 To 12
					ReadByte bmp
				Next
				
				Local width:Int
				Local height:Int
				
				If ReadInt (bmp) = 40
					width = ReadInt (bmp)
					height = ReadInt (bmp)
				EndIf
	
				' Not needed...
				
				ReadByte bmp
				ReadByte bmp
				
				Local depth:Int = ReadShort (bmp)
				
				Local compression:Int = ReadInt (bmp)
				
				Local version:String
				
				Select compression
					Case 0
						version = "No compression"
					Case 1
						version = "RLE-8 compression"
					Case 2
						version = "RLE-4 compression"
					Default
						version = "Unknown compression"
				End Select

				image.width	= width
				image.height	= height
				image.colors	= 2 ^ depth
				image.info	= version
			
			Else
				image = Null
			EndIf
		
			Catch ReadFail:Object
			DebugLog "Read error in " + f
			image = Null

		End Try
		
		CloseFile bmp

	Else
		image = Null
	EndIf
		
	Return image

End Function

Function GetGIFInfo:ImageInfo (f:String)

	Local image:ImageInfo = New ImageInfo
	
	If Lower (Left (f, 7)) = "http://"
		f = "http::" + Right (f, Len (f) - 7)
	EndIf
	
	' Read the file...
	
	Local gif:TStream = LittleEndianStream (ReadFile (f))

	If gif
	
		Try
		
			' First 3 bytes must be "GIF"...
			
			Local g:String
			
			Local loop:Int ' For byte-seek loops...
			
			For loop = 0 To 2
				g = g + Chr (ReadByte (gif))
			Next
	
			If g = "GIF"
	
				Print "Got GIF???"
				
				' Next 3 bytes contain version (87a or 89a)...
				
				Local version:String = "GIF version "
				
				For loop = 3 To 5
					version = version + Chr (ReadByte (gif))
				Next
		
				' Dimensions...
				
				Local width:Int = ReadShort (gif)
				Local height:Int = ReadShort (gif)
		
				' Depth is encoded in first 3 bits of this byte!
				
				Local packed:Int = ReadByte (gif)
				Local depth:Int = (packed & 1) + (packed & 1 Shl 1) + (packed & 1 Shl 2) + 1
				Local colors:Int = 2 ^ depth
		
				image.width	= width
				image.height	= height
				image.colors	= colors
				image.info	= version:String

			Else
				image = Null
			EndIf

			Catch ReadFail:Object
			DebugLog "Read error in " + f
			image = Null
			
		End Try
		
		CloseFile gif
	
	Else
		image = Null
	EndIf
	
	Return image

End Function

Function GetJPEGInfo:ImageInfo (f:String)

	Global remote:Int = False ' Used for online images
	
	If Lower (Left (f, 7)) = "http://"
		f = "http::" + Right (f, Len (f) - 7)
		remote = True
	EndIf

	Local image:ImageInfo = New ImageInfo

	Local jpeg:TStream = BigEndianStream (ReadFile (f))

	If jpeg

		Try

			' Start of image (SOI) marker ($FFD8) -- MUST BE PRESENT!
			
			If ReadByte (jpeg) = $FF And ReadByte (jpeg) = $D8
		
				' ... followed by JFIF 'APP0' marker ($FFE0). In theory must be present, but reality says otherwise...
		
				ReadByte jpeg	' Should be $FF but not always true...
				ReadByte jpeg	' Should be $E0 but not always true...
		
				' Start of first block...
				
				Local block_length:Int = ReadShort (jpeg) - 2 ' Less these two bytes!
		
				' Check for JFIF identification string (generally treated as optional)...
		
				Local jfif:Int = 0
				
				' Have to check each byte separately as BlitzMax's 'early-out' feature may mean the
				' wrong number of bytes are read if one doesn't match, eg. If ReadByte (x) And ReadByte (y)...
				
				If ReadByte (jpeg)	= 74 Then jfif = jfif + 1	' ASCII code for "J"
				If ReadByte (jpeg)	= 70 Then jfif = jfif + 1	' ASCII code for "F"
				If ReadByte (jpeg)	= 73 Then jfif = jfif + 1	' ASCII code for "I"
				If ReadByte (jpeg)	= 70 Then jfif = jfif + 1	' ASCII code for "F"
				If ReadByte (jpeg)	= 0 Then jfif = jfif + 1		' 0
				
				If jfif = 5 Then jfif = True Else jfif = False
				
				' Read next two bytes. If the file has a JFIF marker, this is the version string. If
				' not, it's probably random bollocks...
				
				Local major:String = String (ReadByte (jpeg))			' Major revision number
				Local minor:String = RSet (String (ReadByte (jpeg)), 2)	' Minor revision (padded with leading space)
				
				Local version:String
				
				If jfif
		
					' JFIF-compliant! Yay!
					
					minor = Replace (minor, " ", "0")				' Replace space with 0!
					
					' The above changes version from (eg.) "1.2" to "1.02",
					' as in common rendering of "JFIF, version 1.02"...
					
					version = "JFIF version " + major + "." + minor
				
				Else
				
					' Missing either APP0 marker or "JFIF" string. Boo!
					
					version = "Not a 100% JFIF-compliant JPEG file"
					
				EndIf
		
				image.info = version
				
				Local loop:Int ' For byte seek loops...
					
				' Skip block length, minus the previous 7 reads since start of block...
				
				If remote
					
					' Online image, read byte-by-byte...
					
					For loop = 1 To block_length - 7
						ReadByte jpeg
					Next
				Else
					' Local image, just stream...
					SeekStream jpeg, StreamPos (jpeg) + (block_length - 7)
				EndIf
	
				Local back_byte:Int = 0 ' See below...
							
				While Not Eof (jpeg)
		
					' We should be at the start of a block; if not, bail out...
					
	'				DebugLog "---------------------------------------------------------------------------------------"
	'				DebugLog "New block at " + StreamPos (jpeg)
	'				DebugLog "---------------------------------------------------------------------------------------"
	
					Local checkff:Byte ' Byte to be tested for $FF (start of block)...
					
					' See further down -- needed as we can't seek backwards with online images...
					
					If back_byte
						' Byte from last time around...
						checkff = back_byte
					Else
						checkff = ReadByte (jpeg)
					EndIf
					
					If checkff = $FF
					
						back_byte = 0 ' Reset for next loop...
	
						' Read the byte AFTER a $FF marker...
						
						Local afterff:Byte = ReadByte (jpeg)
						
				' 		Some debug information, perhaps of interest...
				'		DebugLog "$FF" + Right (Hex (afterff), 2)
				'		$D8 = Start of Image (SOI) marker
				'		$D9 = End of Image (EOI) marker
				'		$ED = Photoshop data marker
				'		$E1 = Start of Exif data
						
						' Grab next two bytes (length of block) before proceeding...

						block_length = ReadShort (jpeg) - 2 ' The 2 subtracted bytes store the length itself...
						
						If afterff => $C0 And afterff <= $C3
				
							' Bits per pixel...
							
							Local bpp:Int = ReadByte (jpeg)
				
							' Height and width...
							
							Local height:Int = ReadShort (jpeg)' (ReadByte (jpeg) Shl 8) + ReadByte (jpeg)
							Local width:Int = ReadShort (jpeg)'(ReadByte (jpeg) Shl 8) + ReadByte (jpeg)
							
							' Components per pixel (1 for grayscale, 3 for RGB)...
							
							Local components:Int = ReadByte (jpeg)
							
							' Depth/total colours...
							
							Local depth:Int = bpp * components
							Local colors:Int = 2 ^ depth
				
							' Fill in ImageInfo data...
				
							image.width	= width
							image.height	= height
							image.colors	= colors

							' Done!
				
							Exit
	
						Else
						
							' Go to next block...
	
							If remote
							
								' Online image, read byte-by-byte...
								
								For loop = 1 To block_length
									ReadByte jpeg
								Next
								
							Else
								' Local image, just seek...
								SeekStream jpeg, StreamPos (jpeg) + block_length
							EndIf
													
							' Found huge string of zeroes after jumping block_length in PS 7 JPEG! Skip...
							
							Local next_byte:Byte = 0
							
							Repeat
								next_byte = ReadByte (jpeg)
							Until next_byte
							
							' OK, found non-zero byte, so go back one byte and return to start of loop...
							
							back_byte = next_byte ' Store last-read byte (can't seek back with online images)...
	
							' back_byte will be checked at start of While/Wend loop...
							
						EndIf
						
					Else
					
						' Not at a block marker. Oops! Bail...
						
						image = Null
						Exit
						
					EndIf
					
				Wend
		
			Else
				image = Null
			EndIf

			Catch ReadFail:Object
			DebugLog "Read error in " + f
			image = Null

		End Try
		
		CloseFile jpeg
		
	Else
		image = Null
	EndIf
	
	Return image

End Function

Function GetPNGInfo:ImageInfo (f:String)

	If Lower (Left (f, 7)) = "http://"
		f = "http::" + Right (f, Len (f) - 7)
	EndIf

	Local image:ImageInfo = New ImageInfo

	Local png:TStream = BigEndianStream (ReadFile (f))

	If png

		Try
		
			' PNG header...
			
			If ReadByte (png) = $89 And Chr (ReadByte (png)) = "P" And Chr (ReadByte (png)) = "N" And Chr (ReadByte (png)) = "G"
			
				' PNG header continued...
				
				If ReadByte (png) = 13 And ReadByte (png) = 10 And ReadByte (png) = 26 And ReadByte (png) = 10
	
					For Local loop:Int = 1 To 4
						ReadByte png
					Next
					
					' IHDR chunk (always first)...
					
					If Chr (ReadByte (png)) = "I" And Chr (ReadByte (png)) = "H" And Chr (ReadByte (png)) = "D" And Chr (ReadByte (png)) = "R"
				
						Local width:Int	= ReadInt (png)
						Local height:Int	= ReadInt (png)
						Local depth:Int	= ReadByte (png)
	
						Local colortype:Int	= ReadByte (png)
						
						Local info:String
						
						Select colortype
						
							Case 0
								info = "Pixels represented by grayscale values"
	
							Case 2
								info = "Pixels represented by RGB values"
							
							Case 3
								info = "Pixels represented by palette indices"
							
							Case 4
								info = "Pixels represented by grayscale values plus alpha"
							
							Case 6
								info = "Pixels represented by RGB values plus alpha"
							
							Default
								info = "Unknown pixel format"
								
						End Select
	
						image.width	= width
						image.height	= height
						image.colors	= 2 ^ depth
						image.info	= info
					
					Else
						image = Null
					EndIf
					
				Else
					image = Null
				EndIf
			
			Else
				image = Null
			EndIf
			
			Catch ReadFail:Object
			DebugLog "Read error in " + f
			image = Null
			
		End Try
		
		CloseFile png
		
	Else
		image = Null
	EndIf
		
	Return image

End Function

Function GetTGAInfo:ImageInfo (f$)

	If Lower (Left (f, 7)) = "http://"
		f = "http::" + Right (f, Len (f) - 7)
	EndIf

	Local image:ImageInfo = New ImageInfo
	
	Local tga:TStream = LittleEndianStream (ReadFile (f$))

	If tga
		
		Try
		
			Local idlength:Byte = ReadByte (tga)
	
			Local colormap:Byte = ReadByte (tga)
			
			Local imagetype:Byte = ReadByte (tga)
			
			Local info:String
			
			Select imagetype
			
				' First three bits:
				
				Case 0
					info = "No image data present"
				Case 1
					info = "Uncompressed color-mapped image"
				Case 2
					info = "Uncompressed RGB image"
				Case 3
					info = "Uncompressed grayscale image"
	
				' Fourth bit:
				
				Case 9
					info = "RLE-compressed color-mapped image"
				Case 10
					info = "RLE-compressed RGB image"
				Case 11
					info = "RLE-compressed grayscale image"
	
				' From http://www.gamers.org/dEngine/quake3/TGA.txt ...
				
				Case 32
					info = "Color-mapped image (Huffman/Delta/RLE-compressed)"
	
				Case 33
					info = "Color-mapped image (Huffman/Delta/RLE-compressed, 4-pass quadtree)"
	
				Default
					info = "Unknown image type"
					
			End Select
			
			Local colormapstart:Short	= ReadShort (tga)
			Local colormaplength:Short	= ReadShort (tga)
			Local colormapbpp:Byte		= ReadByte (tga)
	
			Local xorigin:Short = ReadShort (tga)
			Local yorigin:Short = ReadShort (tga)
			
			Local width:Short	= ReadShort (tga)
			Local height:Short	= ReadShort (tga)
			
			Local depth:Byte	= ReadByte (tga)
	
			If colormap
	
				depth = colormapbpp
	
	'				DebugLog "Color map start: " + colormapstart
	'				DebugLog "Color map length: " + colormaplength
	'				DebugLog "Color map bits per pixel: " + colormapbpp
	
	'				Select colormap
	'					Case 0
	'						DebugLog "Image has no indexed palette"
	'					Case 1
	'						DebugLog "Image has indexed palette (" + colormaplength + " entries)"
	'					Case colormap =>2 And colormap <= 127
	'						DebugLog "Truevision-specific color map"
	'					Case colormap => 128 And colormap <= 255
	'						DebugLog "Third-party color map"
	'				End Select
	
			EndIf
			
			Local desc:Byte = ReadByte (tga)
			
			Local pixelattr:Byte = desc & (Int (2 ^ 3) | Int (2 ^ 2) | Int (2 ^ 1) | Int (2 ^ 0))
	
			Select pixelattr
			
				Case 0
	
					info = info + ", no alpha mask"
	
				Case 1
	
					info = info + ", with background mask"
	
				Case 8
	
					info = info + ", with alpha mask"
					
			End Select
	
			' 32-bit depth may or may not include an alpha mask, but RGB values are max 24-bit...
			
			If depth = 32 Then depth = 24
			
			Local colors:Int = Int (2 ^ depth)
			
			' NOTE: colors value is the maximum number of colours available to each
			' pixel. This applies even in images with a limited number of palette
			' entries, eg. a palette of 8 indexed colours may still contain 24-bit values!
			
			image.width = width
			image.height = height
			image.colors = colors
			image.info = info
			
			Catch ReadFail:Object
			DebugLog "Read error in " + f
			image = Null

		End Try
	
		CloseFile tga
	
	Else
		image = Null
	EndIf
	
	Return image

End Function

' -----------------------------------------------------------------------------
' File format tests...
' -----------------------------------------------------------------------------

Function GotBMP:Int (f:String)

	If Lower (Left (f, 7)) = "http://"
		f = "http::" + Right (f, Len (f) - 7)
	EndIf
	
	Local result:Int = False
	
	Local bmp:TStream = LittleEndianStream (ReadFile (f))

	If bmp
		
		Try
		
			If ReadByte (bmp) = $42 And ReadByte (bmp) = $4D
	
				For Local loop:Int = 1 To 12
					ReadByte bmp
				Next
				
				If ReadInt (bmp) = 40
					result = True
				EndIf
		
			EndIf
			
			Catch ReadFail:Object
			DebugLog "Read error in " + f
		
		End Try
		
		CloseFile bmp
		
	EndIf

	Return result
	
End Function

Function GotGIF:Int (f:String)

	If Lower (Left (f, 7)) = "http://"
		f = "http::" + Right (f, Len (f) - 7)
	EndIf
	
	Local result:Int = False
	
	Local gif:TStream = LittleEndianStream (ReadFile (f))

	If gif
	
		Try
		
			' First 3 bytes must be "GIF"...
			
			Local g:String ' /beavis: Uh... huh huh!
			
			Local loop:Int ' For byte-seek loops...
			
			For loop = 0 To 2
				g = g + Chr (ReadByte (gif))
			Next
	
			If g = "GIF"
	
				' Next 3 bytes contain version (87a or 89a)...
				
				Local version:String
				
				For loop = 3 To 5
					version = version + Chr (ReadByte (gif))
				Next
				
				If version = "87a" Or version$ = "89a"
					result = True
				EndIf

			EndIf
			
			Catch ReadFail:Object
			DebugLog "Read error in " + f
		
		End Try
		
		CloseFile gif
	
	EndIf

	Return result
			
End Function

Function GotJPEG:Int (f:String)

	If Lower (Left (f, 7)) = "http://"
		f = "http::" + Right (f, Len (f) - 7)
	EndIf
	
	Local result:Int = False
	
	Local jpeg:TStream = BigEndianStream (ReadFile (f))

	If jpeg

		Try
		
			If ReadByte (jpeg) = $FF And ReadByte (jpeg) = $D8

				ReadByte jpeg
				ReadByte jpeg
				
				Local block_length:Int = ReadShort (jpeg) - 2
				
				ReadByte jpeg
				ReadByte jpeg
				ReadByte jpeg
				ReadByte jpeg
				ReadByte jpeg

				ReadByte jpeg
				ReadByte jpeg

				Local loop:Int
					
				For loop = 1 To block_length - 7
					ReadByte jpeg
				Next

				If ReadByte (jpeg) = $FF
					result = True
				EndIf
				
			EndIf
			
			Catch ReadFail:Object
			DebugLog "Read error in " + f
		
		End Try
		
		CloseFile jpeg
		
	EndIf

	Return result
	
End Function

Function GotPNG:Int (f:String)

	If Lower (Left (f, 7)) = "http://"
		f = "http::" + Right (f, Len (f) - 7)
	EndIf
	
	Local result:Int = False
	
	Local png:TStream = BigEndianStream (ReadFile (f))

	If png

		Try

			If ReadByte (png) = $89 And Chr (ReadByte (png)) = "P" And Chr (ReadByte (png)) = "N" And Chr (ReadByte (png)) = "G"
			
				' PNG header continued...
				
				If ReadByte (png) = 13 And ReadByte (png) = 10 And ReadByte (png) = 26 And ReadByte (png) = 10
	
					For Local loop:Int = 1 To 4
						ReadByte png
					Next
					
					' IHDR chunk (always first)...
					
					If Chr (ReadByte (png)) = "I" And Chr (ReadByte (png)) = "H" And Chr (ReadByte (png)) = "D" And Chr (ReadByte (png)) = "R"
						
						result = True
						
					EndIf
					
				EndIf
				
			EndIf
				
			Catch ReadFail:Object
			DebugLog "Read error in " + f
		
		End Try
		
		CloseFile png
		
	EndIf

	Return result
	
End Function

Function GotTGA:Int (f:String, ext:Int = True)

	' Best to take extension into account here, since there are no 100% identifying TGA markers...
	
	If ext
		If Lower (ExtractExt (f)) <> "tga"
			Return False
		EndIf
	EndIf
	
	If Lower (Left (f, 7)) = "http://"
		f = "http::" + Right (f, Len (f) - 7)
	EndIf
	
	Local result:Int = False
	
	Local tga:TStream = LittleEndianStream (ReadFile (f))

	If tga

		Try

			ReadByte tga
			ReadByte tga
			ReadByte tga

			ReadShort tga
			ReadShort tga
			Local mapbits:Byte = ReadByte (tga)
	
			ReadShort tga
			ReadShort tga
			
			' Width and height > 0...
			
			If ReadShort (tga) > 0 And ReadShort (tga) > 0
			
				' Depth > 0 or bits per palette entry > 0...

				Local depth:Byte = ReadByte (tga)

				If depth

					result = True
					
					Select depth
						Case 8
						Case 16
						Case 24
						Case 32
						Default
							result = False
					End Select
					
				Else
					If mapbits
	
						result = True
						
						Select depth
							Case 15
							Case 16
							Case 24
							Case 32
							Default
								result = False
						End Select
					EndIf
				EndIf
				
			EndIf

			Catch ReadFail:Object
			DebugLog "Read error in " + f
		
		End Try
		
		CloseFile tga
		
	EndIf

	Return result
	
End Function

Function GetImageInfo:ImageInfo (f:String)

	Local i:ImageInfo = New ImageInfo
	
	Local ext:String = Lower (ExtractExt (f))

	Select ext:String
	
		Case "jpg", "jpeg", "jpe", "jfif"
			i = GetJPEGInfo (f)
			
		Case "gif"
			i = GetGIFInfo (f)

		Case "bmp"
			i = GetBMPInfo (f)

		Case "png"
			i = GetPNGInfo (f)

		Case "tga"
			i = GetTGAInfo (f)
			
		Default
			i = Null

	End Select

	If i = Null
		If GotJPEG (f) Then i = GetJPEGInfo (f); If i Then i.info = "This is really a JPEG file!"; Return i
		If GotBMP (f) Then i = GetBMPInfo (f); If i Then i.info = "This is really a BMP file!"; Return i
		If GotPNG (f) Then i = GetPNGInfo (f); If i Then i.info = "This is really a PNG file!"; Return i
		If GotGIF (f) Then i = GetGIFInfo (f); If i Then i.info = "This is really a GIF file!"; Return i
		If GotTGA (f) Then i = GetTGAInfo (f); If i Then i.info = "This is really a TGA file!"; Return i
	EndIf
	
	Return i
	
End Function
