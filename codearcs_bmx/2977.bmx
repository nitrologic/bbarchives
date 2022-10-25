; ID: 2977
; Author: BlitzSupport
; Date: 2012-09-14 10:40:20
; Title: JPEG reference parser and EXIF reader
; Description: Reads JPEG files and extracts EXIF data

' General JPEG/JFIF file information reader with EXIF data extraction...

' Tested on 15,000+ image files, 13,500 of which are named as JPEGs. Skips or recovers from bad
' data found in some of these files, and safely handles files erroneously labelled as JPEGs.

' Some references...

' http://www.exif.org/Exif2-2.PDF
' http://www.media.mit.edu/pia/Research/deepview/exif.html
' http://regex.info/exif.cgi

' http://www.takenet.or.jp/~ryuuji/minisoft/exifread/english/ 	-	Exif Reader, for validation -- also crashed on endless recursion file; we don't now!
' http://opanda.com/en/iexif/									-	Another reader for validation

' The first one is useful if you want to add more interpretations in transtag.bmx
' -- search the PDF and transtag.bmx for the tag name you want (eg. XResolution)
' then perform operations on the received 'value' string according to the docs. The
' meanings of EXIF data types are also in the PDF -- important, as Long means Int
' in Blitz, and we can't use unsigned values, though in practice it shouldn't
' matter for files under 2GB in size...

SuperStrict




' Ugly hack to keep track of which file offsets have already been visited. Necessary
' as some EXIF data contains circular references that otherwise cause infinite loops...
	
Global IFDsVisited:TList


' D E M O . . .


' SINGLE FILE DEMO:

' This is not much use if it doesn't contain EXIF data!

	Local img:String

	img = "mydog.jpg"
	
	PrintJPEGInfo img
	
	End

' MULTIPLE FILE DEMO:

' Comment out ^^^ End ^^^ for folder (and sub-folders) demo. Change folder$ below to your image folder...

	Global ImageCount:Long
	
	Local folder$
	
	folder = "C:\My Pictures\"
	
	ParseFolder folder
	
	Print ""
	Print "Tested " + ImageCount + " image files."
	Print ""

	End





' Demo function...

Function ParseFolder (dir:String)

	If Right (dir:String, 1) <> "\" And Right (dir:String, 1) <> "/"
		dir:String = dir:String + "/"
	EndIf
	
	Local folder:Int = ReadDir (dir:String)

	If folder
	
		Repeat

			Local entry:String = NextFile (folder)

			If entry = "" Then Exit
			
			If entry <> "." And entry <> ".."

				Local file:String
				Local full:String
				
				If FileType (dir + entry) = FILETYPE_FILE
	
					file = entry
		
					full = dir
		
					If Right (full, 1) <> "\" And Right (full, 1) <> "/"
						full = full + "\"
					EndIf
		
					full = full + file
		
					ImageCount = ImageCount + 1
					
					'Print ""
					'Print "Reading " + full + " (image number " + ImageCount + ")..."
					
					PrintJPEGInfo full
		
				Else
		
					If FileType (dir + entry) = FILETYPE_DIR
	
						file = entry
		
						If file <> "." And file <> ".."
		
							Local ffolder:String = dir
		
							If Right (ffolder, 1) <> "\" And Right (ffolder, 1) <> "/"
								ffolder = ffolder + "\"
							EndIf
	
							ffolder = ffolder + file
								
							ParseFolder (ffolder)
		
						EndIf
		
					EndIf
		
				EndIf
	
			EndIf

		Forever
	
	EndIf

End Function



' Core functions...

' -------------------------------------------------------------------------
' Main EXIF reader...
' -------------------------------------------------------------------------

Function PrintEXIFInfo (jpeg:TStream, tiffsize:Int)

	' --------------------------------------------------------------------
	' Local EXIF image file directory parser...
	' --------------------------------------------------------------------

	' This will loop through the Image File Directory entries, loop through the tag directories
	' within each, and recursively loop into offset IFDs...
	
	Local params:ParameterBundle = New ParameterBundle

	params.jpeg			= jpeg
	
	params.tiffstart	= StreamPos (params.jpeg)
	params.tiffend		= (params.tiffstart + tiffsize) - 1
	
	' -----------------------------------------------------------------
	' TIFF header...
	' -----------------------------------------------------------------

	Select ReadShort (params.jpeg)
	
		Case $4D4D ' Motorola byte format ("MM")
		
			ReadShort (params.jpeg)
'				If ReadShort (params.jpeg) = $002A Then TabPrint "[EXIF section's TIFF header in MOTOROLA byte format]"
			params.endian = ENDIAN_MOTOROLA
			
		Case $4949 ' Intel byte format ("II")
		
			ReadShort (params.jpeg)
'				If ReadShort (params.jpeg) = $2A00 Then TabPrint "[EXIF section's TIFF header in INTEL byte format]"
			params.endian = ENDIAN_INTEL

		Default

			' Prolly won't happen...
			
			Print "[EXIF section's TIFF header in UNDEFINED byte format...]"
			Notify "Undefined byte format... I'm outta here!", True
			End
			
	End Select
	
	params.ifdoffset = ReadInt (params.jpeg)
	
	If params.endian = ENDIAN_INTEL Then params.ifdoffset = SwapEndianInt (params.ifdoffset)
	
	SeekStream params.jpeg, params.tiffstart + params.ifdoffset ' Almost always 8, but I found at least one image that was different!

'		Print "IFDOFFSET Seeked to " + StreamPos (params.jpeg)
	
	' -----------------------------------------------------------------
	' Begin with Primary Image Data IFD...
	' -----------------------------------------------------------------

	' Create/clear global list to record IFD offsets visited (prevents infinite loops
	' with broken circular-referencing files)...
	
	IFDsVisited = CreateList ()

	ParseIFD params

	params = Null
	
End Function


' NB. ParseIFD and ProcessTag recursively call each other! Scary but works fine...

' Support stuff...


' -------------------------------------------------------------------------
' Some utility functions...
' -------------------------------------------------------------------------

' Just for prettifying this demo...

Function TabPrint (info:String, indent:Int = 0)
'		Return
	Local tabs:String
	For Local loop:Int = 1 To indent + 1
		tabs = tabs + "~t"
	Next
	Print tabs + info
End Function

' Ahem... yeah:
	
Function SwapEndianInt:Int (value:Int)
	Local temp:String = Hex (value)
	Return Int ("$" + Right (temp, 2) + Mid (temp, 5, 2) + Mid (temp, 3, 2) + Left (temp, 2))
End Function

Function SwapEndianShort:Short (value:Short)
	Local temp:String = Hex (value)
	Return Short ("$" + Right (temp, 2) + Mid (temp, 5, 2))
End Function

' Info function for basic JPEG data...
	
Function PrintImageData (jpeg:TStream)

	' Bits per pixel...
	
	Local bpp:Int = ReadByte (jpeg)

	' Height and width...
	
	Local height:Int = ReadShort (jpeg)
	Local width:Int = ReadShort (jpeg)
	
	' Components per pixel (1 for grayscale, 3 for RGB)...
	
	Local components:Int = ReadByte (jpeg)
	
	' Depth/total colours...
	
	Local depth:Int = bpp * components
	Local colors:Int = 2 ^ depth

	' Print information...

	TabPrint "Image details: " + width + " x " + height + " @ " + depth + "-bit (" + Int (2 ^ depth) + " colours)"

End Function


Function TranslateTag:String (tag:Int, value:String, bonusball:String = "")

	Function Unspec:String (value:String)
		Return "Unspecified or invalid (reserved by EXIF spec): " + value
	End Function

	Local numdom:String [] ' Numerator/denominator in rational number strings...
	
	Select tag
	
		' -----------------------------------------------------
		' IFD0/1 values...
		' -----------------------------------------------------
		
		Case $0100
		
			' "ImageWidth"
			
			Return value
			
		Case $0101
		
			' "ImageLength"
			
			Return value
			
		Case $0102
		
			' "BitsPerSample"
			
			Return value + " bits per sample"
			
		Case $0103
		
			' "Compression"
			
			Select Short (value)
				Case 1
					Return "Uncompressed image data"
				Case 6
					Return "JPEG-compressed thumbnail image data"
				Default
					Return Unspec (value)
			End Select
			
		Case $0106
		
			' "PhotometricInterpretation"
			
			Select Short (value)
				Case 2
					Return "Pixels are in RGB format"
				Case 6
					Return "Pixels are in YCbCr format"
				Default
					Return Unspec (value)
			End Select
			
		Case $010E
		
			' "ImageDescription"
			
			Return value
			
		Case $010F
		
			' "Make"
			
			Return value
			
		Case $0110
		
			' "Model"
			
			Return value
			
		Case $0111
			' "StripOffsets"
			Return value
			
		Case $0112
		
			' "Orientation"
			
			' NOTE: These may be incorrect! Struggling to work them out from spec descriptions!
			
			' The rotated values might be flipped too... need to test images with these values...
			
			Select value
				Case 1
					Return "Normal"
				Case 2
					Return "Horizontally flipped"
				Case 3
					Return "Horizontally and vertically flipped"
				Case 4
					Return "Vertically flipped"
				Case 5
					Return "Rotated... somehow..."
				Case 6
					Return "Rotated... somehow..."
				Case 7
					Return "Rotated... somehow..."
				Case 8
					Return "Rotated... somehow..."
				Default
					Return Unspec (value)
			End Select
			
		Case $0115
			' "SamplesPerPixel"
			Return value + " samples per pixel"
			
		Case $0116
			' "RowsPerStrip"
			Return value
			
		Case $0117
			' "StripByteCounts"
			Return value
		
		' BELOW: Re. XResolution and YResolution. Since we don't know if ResolutionUnit
		' has been read yet, if you need this data to be correctly returned, scan the
		' whole file for the EXIF data you're interested in, store it, THEN interpret it.
		
		' So, for this information, you would store XResolution, YResolution, and
		' ResolutionUnit. Once the file has been fully read, you can then call
		' TranslateTag with the string value of XResolution in the 'value' parameter
		' and the string value of ResolutionUnit in the 'bonusball' parameter to get
		' the correct results.
		
		' (Careful if you try to perform division on 'value' (eg. "72/1") as I've found
		' at least one photo with invalid data that results in a divide-by-zero error!)
		
		Case $011A

			' "XResolution"

			Select bonusball
				Case "2"
					Return value + " pixels per inch"
				Case "3"
					Return value + " pixels per cm"
				Default
					Return value + " pixels per inch (assumed, as per spec)"
			End Select
			
		Case $011B

			Select bonusball
				Case ""
					Return value + " pixels per inch (assumed, as per spec)"
				Case "2"
					Return value + " pixels per inch"
				Case "3"
					Return value + " pixels per cm"
				Default
					Return Unspec (value)
			End Select
			
		Case $011C
			
			' "PlanarConfiguration"
			
			Select value
				Case 1
					Return "Pixels are in 'chunky' format"
				Case 2
					Return "Pixels are in 'planar' format"
				Case 3
					Return Unspec (value)
			End Select
			
		Case $0128
		
			' "ResolutionUnit"
		
			Select value
				Case ""
					Return "inches (assumed, as per spec)"
				Case 2
					Return "inches"
				Case 3
					Return "cm"
				Default
					Return Unspec (value)
			End Select

		Case $012D
			' "TransferFunction"
			Return value

		Case $0131
		
			' "Software"
		
			Return value

		Case $0132
		
			' "DateTime"
		
			Return value

		Case $013B
		
			' "Artist"
		
			Return value

		Case $013E
			' "WhitePoint"
			Return value

		Case $013F
			' "PrimaryChromaticities"
			Return value

		Case $0201
		
			' "JPEGInterchangeFormat"
		
			Return "JPEG-compressed thumbnail data stored at byte offset #" + value

		Case $0202
		
			' "JPEGInterchangeFormatLength"

			If value = 0
				Return "Empty JPEG-compressed thumbnail data"
			Else
				Return "JPEG-compressed thumbnail data stored in " + value + " bytes"
			EndIf

		Case $0211
			' "YCbCrCoefficients"
			Return value

		Case $0212
			' "YCbCrSubSampling"
			Return value

		Case $0213
			
			' "YCbCrPositioning"
			
			' Probably means something to somebody...
			
			Select value
				Case 1
					Return "Chrominance components centered in relation to luminance"
				Case 1
					Return "Chrominance components co-sited with luminance"
			End Select

		Case $0214
			' "ReferenceBlackWhite"
			Return value

		Case $8298
			' "Copyright"
			Return value

		' -----------------------------------------------------
		' EXIF Sub-IFD values...
		' -----------------------------------------------------
		
		Case $0000829A
			' "ExposureTime"
			Return value

		Case $0000829D
			' "FNumber"
			Return value

		Case $00008822
		
			' "ExposureProgram"
		
			Select value
				Case 0
					Return "Not defined"
				Case 1
					Return "Manual exposure"
				Case 2
					Return "Normal"
				Case 3
					Return "Aperture priority"
				Case 4
					Return "Shutter priority"
				Case 5
					Return "Creative"
				Case 6
					Return "Action"
				Case 7
					Return "Portraits"
				Case 8
					Return "Landscapes"
				Default
					Return Unspec (value)
			End Select

		Case $00008824
			' "SpectralSensitivity"
			Return value

		Case $00008827
			' "ISOSpeedRatings"
			Return value

		Case $00008828
			' "OECF"
			Return value

		Case $00009000
		
			' "ExifVersion"
			
			Local version:String = Int (Left (value, 2)) + "." + Int (Right (value, 2))
			
			' Not widely tested!
			
			While Right (version, 1) = "0"
				version = Left (version, Len (version) - 1)
				If version = "" Then version = "Unknown"; Exit
			Wend
			
			' 0220 becomes 2.2, 0221 becomes 2.21, etc...
			
			Return version
				
		Case $00009003
			' "DateTimeOriginal"
			Return value

		Case $00009004
			' "DateTimeDigitized"
			Return value

		Case $00009101
			' "ComponentsConfiguration"
			Return value

		Case $00009102
			' "CompressedBitsPerPixel"
			Return value

		Case $00009201
		
			' "ShutterSpeedValue"
		
			Return value

		Case $00009202

			' "ApertureValue"
			
			Return value

		Case $00009203
			' "BrightnessValue"
			Return value

		Case $00009204
			' "ExposureBiasValue"
			Return value

		Case $00009205
			' "MaxApertureValue"
			Return value

		Case $00009206
			' "SubjectDistance"
			Return value

		Case $00009207
			' "MeteringMode"
			Return value

		Case $00009208
			' "LightSource"
			Return value

		Case $00009209
			' "Flash"
			Return value

		Case $0000920A
			' "FocalLength"
			Return value

		Case $00009214
			' "SubjectArea"
			Return value

		Case $0000927C
			' "MakerNote"
			Return value

		Case $00009286
			' "UserComment"
			Return value

		Case $00009290
			' "SubSecTime"
			Return value

		Case $00009291
			' "SubSecTimeOriginal"
			Return value

		Case $00009292
			' "SubSecTimeDigitized"
			Return value

		Case $0000A000
			' "FlashPixVersion"
			Return value

		Case $0000A001
		
			' "ColorSpace"
			
			Select value
				Case 1
					Return "sRGB color space"
				Case $FFFF
					Return "Uncalibrated color space"
				Default
					Return Unspec (value)
			End Select

		Case $0000A002
			' "ExifImageWidth"
			Return value

		Case $0000A003
			' "ExifImageHeight"
			Return value

		Case $0000A004
			' "RelatedSoundFile"
			Return value

		Case $0000A20B
			' "FlashEnergy"
			Return value

		Case $0000A20C
			' "SpatialFrequencyResponse"
			Return value

		Case $0000A20E
			' "FocalPlaneXResolution"
			Return value

		Case $0000A20F
			' "FocalPlaneYResolution"
			Return value

		Case $0000A210
			' "FocalPlaneResolutionUnit"
			Return value

		Case $0000A214
			' "SubjectLocation"
			Return value

		Case $0000A215
			' "ExposureIndex"
			Return value

		Case $0000A217
			' "SensingMethod"
			Return value

		Case $0000A300
			' "FileSource"
			Return value

		Case $0000A301
			' "SceneType"
			Return value

		Case $0000A302
			' "CFAPattern"
			Return value

		Case $0000A401
			' "CustomRendered"
			Return value

		Case $0000A402
			' "ExposureMode"
			Return value

		Case $0000A403
			' "WhiteBalance"
			Return value

		Case $0000A404
			' "DigitalZoomRatio"
			Return value

		Case $0000A405
			' "FocalLengthIn35mmFilm"
			Return value

		Case $0000A406
			' "SceneCaptureType"
			Return value

		Case $0000A407
			' "GainControl"
			Return value

		Case $0000A408
			' "Contrast"
			Return value

		Case $0000A409
			' "Saturation"
			Return value

		Case $0000A40A
			' "Sharpness"
			Return value

		Case $0000A40B
			' "DeviceSettingDescription"
			Return value

		Case $0000A40C
			' "SubjectDistanceRange"
			Return value

		Case $0000A420
			' "ImageUniqueID"
			Return value

		' -----------------------------------------------------
		' GPS sub-IFD values...
		' -----------------------------------------------------
	
		Case $0
			' "GPSVersionID"
			Return value

		Case $1
			' "GPSLatitudeRef"
			Return value

		Case $2
			' "GPSLatitude"
			Return value

		Case $3
			' "GPSLongitudeRef"
			Return value

		Case $4
			' "GPSLongitude"
			Return value

		Case $5
			' "GPSAltitudeRef"
			Return value

		Case $6
			' "GPSAltitude"
			Return value

		Case $7
			' "GPSTimeStamp"
			Return value

		Case $8
			' "GPSSatellites"
			Return value

		Case $9
			' "GPSStatus"
			Return value

		Case $A
			' "GPSMeasureMode"
			Return value

		Case $B
			' "GPSDOP"
			Return value

		Case $C
			' "GPSSpeedRef"
			Return value

		Case $D
			' "GPSSpeed"
			Return value

		Case $E
			' "GPSTrackRef"
			Return value

		Case $F
			' "GPSTrack"
			Return value

		Case $10
			' "GPSImgDirectionRef"
			Return value

		Case $11
			' "GPSImgDirection"
			Return value

		Case $12
			' "GPSMapDatum"
			Return value

		Case $13
			' "GPSDestLatitudeRef"
			Return value

		Case $14
			' "GPSDestLatitude"
			Return value

		Case $15
			' "GPSDestLongitudeRef"
			Return value

		Case $16
			' "GPSDestLongitude"
			Return value

		Case $17
			' "GPSDestBearingRef"
			Return value

		Case $18
			' "GPSDestBearing"
			Return value

		Case $19
			' "GPSDestDistanceRef"
			Return value

		Case $1A
			' "GPSDestDistance"
			Return value

		Case $1B
			' "GPSProcessingMethod"
			Return value

		Case $1C
			' "GPSAreaInformation"
			Return value

		Case $1D
			' "GPSDateStamp"
			Return value

		Case $1E
			' "GPSDifferential"
			Return value

	End Select

End Function

Function ProcessTag:String (params:ParameterBundle)

	' ---------------------------------------------------------
	' Reading the tag data...
	' ---------------------------------------------------------

	' "params.value" contains the value we need to interpret; it
	' has already been read and converted to the correct endian-
	' ness. Once interpreted, we stick it in a local string
	' variable, "finalvalue"...
	
	' Unimplemented cases need to be added as you need them.
	' For every data format, datalength is bytespercomponent
	' multiplied by params.components. If datalength is 4 or
	' less, params.value contains the value required and should
	' be interpreted according to the data type. If datalength
	' is greater than 4, store the current StreamPos position,
	' seek ahead to params.tiffstart + dataoffset, then read
	' and interpret the value. Seek back to the previously
	' stored position...

	Local bytespercomponent:Int
	Local datalength:Int
	
	Local formatdesc:String
	Local finalvalue:String = ""
	
	' ---------------------------------------------------------
	' IMPORTANT! Not all fully implemented!
	' ---------------------------------------------------------
	' May be some discrepancies relating to unsigned values
	' and due to interpreting values differently dependent on
	' datalength value (4 or less: use as-is; 5 or more: treat
	' as offset to value)...
	' ---------------------------------------------------------

	' Not all data formats are implemented here, but they appear very
	' uncommon. To implement any of these, see the PDF referenced in
	' demo.bmx for sizes of types, then follow one of the existing
	' examples below...
	
	Select params.dataformat
	
		Case 1
		
			' -----------------------------------------------
			' UNSIGNED BYTE
			' -----------------------------------------------
			
			formatdesc = formatdesc + "unsigned byte (unimplemented)"
			
			bytespercomponent = 1
			datalength = bytespercomponent * params.components

			' This CAN happen! See J:\My Pictures/Backdrops/Windows Backdrops/AU-wp1.jpg
			
			'If datalength > 4 Then Notify "unsigned byte not properly implemented!"; End

			' Print "datalength " + datalength
			Local valueptr:Byte Ptr = Varptr params.value
			
		Case 2

			' -----------------------------------------------
			' ASCII STRING (should be working)
			' -----------------------------------------------

			bytespercomponent = 1
			datalength = bytespercomponent * params.components

			If datalength < 5

				Local valueptr:Byte Ptr = Varptr params.value
				
				For Local loop:Int = 0 Until datalength
					Local char:Byte = valueptr [loop]
					If char
						finalvalue = finalvalue + Chr (char)
					Else
						Exit
					EndIf
				Next
				
			Else
			
				' It's an offset...
				
				Local dataoffset:Int = params.value
				
				' Go read from file offset, then return here...

				Local temp:Int = StreamPos (params.jpeg)
				
				SeekStream params.jpeg, params.tiffstart + dataoffset'; Print "VALUE2 Seeked to " + StreamPos (params.jpeg)

				' Can't just use ReadString -- found example with 0-char before end of datalength...
				
				For Local loop:Int = 0 Until datalength
					Local char:Byte = ReadByte (params.jpeg)
					If char
						finalvalue = finalvalue + Chr (char)
					Else
						Exit
					EndIf
				Next

				SeekStream params.jpeg, temp'; Print "VALUE2ELSE Seeked to " + StreamPos (params.jpeg)
				
			EndIf
															
		Case 3
	
			' -----------------------------------------------
			' UNSIGNED SHORT (should be working)
			' -----------------------------------------------

			bytespercomponent = 2
			datalength = bytespercomponent * params.components

			If datalength < 5

'							finalvalue = Long (Short (params.value))
				finalvalue = Short (params.value)
			
			Else

				Local dataoffset:Int = params.value
				
				Local temp:Int = StreamPos (params.jpeg)

				SeekStream params.jpeg, params.tiffstart + dataoffset'; Print "VALUE3 Seeked to " + StreamPos (params.jpeg)
				
				Local readoff:Short = ReadShort (params.jpeg)
				
				If params.endian = ENDIAN_INTEL
					readoff = SwapEndianShort (readoff)
				EndIf
				
				finalvalue = Int (readoff)

				SeekStream params.jpeg, temp'; Print "VALUE3ELSE Seeked to " + StreamPos (params.jpeg)

			EndIf
			
		Case 4
		
			' -----------------------------------------------
			' UNSIGNED LONG
			' -----------------------------------------------

			bytespercomponent = 4
			datalength = bytespercomponent * params.components
			
			finalvalue = Int (params.value)

		Case 5

			' -----------------------------------------------
			' UNSIGNED RATIONAL (should be working)
			' -----------------------------------------------

			bytespercomponent = 8
			datalength = bytespercomponent * params.components

			If datalength < 5
			
				' Can't happen, two longs = 8 bytes...
				
'							Notify "Oh! It CAN happen... I see. Check unsigned rational conversion with 4 bytes or less!"; End
				
			Else

				' It's an offset...

				Local dataoffset:Int = params.value
				
				Local temp:Int = StreamPos (params.jpeg)

				SeekStream params.jpeg, params.tiffstart + dataoffset'; Print "VALUE5 Seeked to " + StreamPos (params.jpeg)
				
				Local num:Int = ReadInt (params.jpeg)
				Local den:Int = ReadInt (params.jpeg)
				
				If params.endian = ENDIAN_INTEL
					num = SwapEndianInt (num)
					den = SwapEndianInt (den)
				EndIf

				finalvalue = num + "/" + den

				SeekStream params.jpeg, temp'; Print "VALUE5ELSE Seeked to " + StreamPos (params.jpeg)

			EndIf

		Case 6
		
			' -----------------------------------------------
			' SIGNED BYTE
			' -----------------------------------------------

			formatdesc = formatdesc + "signed byte (unimplemented)"
							
			bytespercomponent = 1
			datalength = bytespercomponent * params.components

		Case 7
		
			' -----------------------------------------------
			' UNDEFINED (case-specific)
			' -----------------------------------------------

			bytespercomponent = 1
			datalength = bytespercomponent * params.components

			' Usually implemented as ASCII characters, apparently. Let's wing it and see...
			
			If datalength < 5
			
				Local valueptr:Byte Ptr = Varptr params.value
			
				For Local loop:Int = 0 Until datalength
					finalvalue = finalvalue + Chr (valueptr [loop])
				Next
			
			Else

				' It's an offset...

				Local dataoffset:Int = params.value
				
				Local temp:Int = StreamPos (params.jpeg)
				
				SeekStream params.jpeg, params.tiffstart + dataoffset'; Print "VALUE7ELSE Seeked to " + StreamPos (params.jpeg)
				
					For Local loop:Int = 0 Until datalength
						finalvalue = finalvalue + Chr (ReadByte (params.jpeg))
					Next
				
				SeekStream params.jpeg, temp'; Print "VALUE7ELSE SEEKBACK Seeked to " + StreamPos (params.jpeg)


			EndIf
			
		Case 8
		
			' -----------------------------------------------
			' SIGNED SHORT
			' -----------------------------------------------
			
			formatdesc = formatdesc + "signed short (unimplemented)"
	
			bytespercomponent = 2
			datalength = bytespercomponent * params.components
			
		Case 9
	
			' -----------------------------------------------
			' SIGNED LONG
			' -----------------------------------------------

			bytespercomponent = 4
			datalength = bytespercomponent * params.components

			finalvalue = Int (params.value) ' Signed long = Blitz Int

		Case 10

			' -----------------------------------------------
			' SIGNED RATIONAL (should be working)
			' -----------------------------------------------
			
			bytespercomponent = 8
			datalength = bytespercomponent * params.components

			If datalength < 5
			
				' Can't happen, two longs = 8 bytes...
				
				' Notify "Oh! It CAN happen... I see. Check signed rational conversion with 4 bytes or less!"; End
				
			Else

				' It's an offset...

				Local dataoffset:Int = params.value
				
				Local temp:Int = StreamPos (params.jpeg)

				SeekStream params.jpeg, params.tiffstart + dataoffset'; Print "VALUE10ELSE Seeked to " + StreamPos (params.jpeg)
				
				Local num:Int = ReadInt (params.jpeg)
				Local den:Int = ReadInt (params.jpeg)
				
				If params.endian = ENDIAN_INTEL
					num = SwapEndianInt (num)
					den = SwapEndianInt (den)
				EndIf

				finalvalue = num + "/" + den

				SeekStream params.jpeg, temp'; Print "VALUE10ELSE SEEKBACK Seeked to " + StreamPos (params.jpeg)

			EndIf

		Case 11
		
			' -----------------------------------------------
			' SINGLE FLOAT
			' -----------------------------------------------
			
			formatdesc = formatdesc + "single float (unimplemented)"
	
			bytespercomponent = 4
			datalength = bytespercomponent * params.components

		Case 12
		
			' -----------------------------------------------
			' DOUBLE FLOAT
			' -----------------------------------------------
			
			formatdesc = formatdesc + "double float (unimplemented)"
	
			bytespercomponent = 8
			datalength = bytespercomponent * params.components

		Default
		
			' -----------------------------------------------
			' UNKNOWN DATA FORMAT!
			' -----------------------------------------------

			' Ha, this can happen too! J:\My Pictures/Cars/0_2_FullSize~2.jpg and J:\My Pictures/Cars/78vauxhall_equus_21.jpg
			
			formatdesc = formatdesc + "Unknown data format (invalid)"
			
	End Select

	Local subifd:Int = False ' Used to skip display of information when sub-IFD tag is found...

	Local tagstring:String ' Used in "If Not subifd" section below...

	' ----------------------------------------------------------
	' OK, what kind of tag is it?
	' ----------------------------------------------------------

	Select params.tag

		' -----------------------------------------------------
		' Sub-IFDs: recursively calls ParseIFD on each one...
		' -----------------------------------------------------

		Case $8769, $8825, $A005

			Select params.tag

				Case $8769
					TabPrint "Found Exif SubIFD:", params.level' at offset " + params.tagpos

				Case $8825
					TabPrint "Found GPS Info SubIFD:", params.level' at offset " + params.tagpos

				Case $A005
					TabPrint "Found Interoperability SubIFD:", params.level' at offset " + params.tagpos
					
			End Select

			' This is a subifd, not a standard tag...
			
			subifd = True
			
			' -------------------------------------------
			' Store current position so we can come back...
			' -------------------------------------------

			Local temp:Int = StreamPos (params.jpeg)
'						Print "At pos BEFORE: " + temp
			
			' Hmmm...
			
			Local offset:Int = params.tiffstart + params.value
			Local abort:Int = False
			
			' Check against known-visited "Interoperability SubIFD" file offsets to prevent infinite circular recursion. Ugly...
			
			For Local check:IFDOffset = EachIn IFDsVisited
				If check.offset = offset
					abort = True
					Exit
				EndIf
			Next
			
			If Not abort ' Only do this if not in visited list!
			
				SeekStream params.jpeg, offset'; Print "VALUE PREPARSE Seeked to " + StreamPos (params.jpeg)
				
				' Interoperability SubIFDs may cause circular references, so add to know-visited list...
				
				If params.tag = $A005
					Local off:IFDOffSet = New IFDOffset
					off.offset = offset
					ListAddLast IFDsVisited, off
				EndIf
				
				' ------------------------------------------------
				' Go parse sub-IFD...
				' ------------------------------------------------

				params.level = params.level + 1 ' For TabPrint indenting!
				
					ParseIFD params ' Retro-recursively parse sub-IFD... eek.
					
				params.level = params.level - 1 ' For TabPrint indenting!
				
				' ------------------------------------------------
				' Back to where we left off...
				' ------------------------------------------------
				
				SeekStream params.jpeg, temp'; Print "VALUE POSTPARSE Seeked to " + StreamPos (params.jpeg)
			
'						Else
'							Print "ERROR IN EXIF DATA: Been here before!"
			EndIf
			
'						Print "At pos AFTER: " + temp

		Default
		
			tagstring = "Unimplemented tag [$" + Hex (params.tag) + "] -- add to ProcessTag function!"
		
	End Select

	' ---------------------------------------------------------
	' Valid tag, not a sub-IFD, so print information...
	' ---------------------------------------------------------

	If Not subifd

		tagstring = "~q" + TagName (params.tag) + "~q"

		'TabPrint "Tag $" + Right (Hex (params.tag), 4) + " (data format " + params.dataformat + ")" + " found at offset " + params.tagpos + ":", params.level
		'TabPrint "Tag $" + Right (Hex (params.tag), 4) + ": " + tagstring, params.level
		TabPrint "Tag name:  " + tagstring, params.level

		If finalvalue = ""
			finalvalue = params.value + " (uninterpreted integer, meant to be " + formatdesc + ")"
		EndIf
		
		' Remove these If/EndIf lines to see Makernote junk (can be binary data hence skipped here)...
		
		If params.tag <> $0000927C

'			TabPrint "Value: ~q" + finalvalue + "~q", params.level
			TabPrint "Tag value: ~q" + TranslateTag (params.tag, finalvalue) + "~q", params.level

		EndIf
		
		TabPrint ""
	
	EndIf
	
End Function

Function TagName:String (tag:Int)

	Select tag
	
		' -----------------------------------------------------
		' IFD0/1 values...
		' -----------------------------------------------------
		
		Case $0100
			Return "ImageWidth"
		Case $0101
			Return "ImageLength"
		Case $0102
			Return "BitsPerSample"
		Case $0103
			Return "Compression"
		Case $0106
			Return "PhotometricInterpretation"
		Case $010E
			Return "ImageDescription"
		Case $010F
			Return "Make"
		Case $0110
			Return "Model"
		Case $0111
			Return "StripOffsets"
		Case $0112
			Return "Orientation"
		Case $0115
			Return "SamplesPerPixel"
		Case $0116
			Return "RowsPerStrip"
		Case $0117
			Return "StripByteCounts"
		Case $011A
			Return "XResolution"
		Case $011B
			Return "YResolution"
		Case $011C
			Return "PlanarConfiguration"
		Case $0128
			Return "ResolutionUnit"
		Case $012D
			Return "TransferFunction"
		Case $0131
			Return "Software"
		Case $0132
			Return "DateTime"
		Case $013B
			Return "Artist"
		Case $013E
			Return "WhitePoint"
		Case $013F
			Return "PrimaryChromaticities"
		Case $0201
			Return "JPEGInterchangeFormat"
		Case $0202
			Return "JPEGInterchangeFormatLength"
		Case $0211
			Return "YCbCrCoefficients"
		Case $0212
			Return "YCbCrSubSampling"
		Case $0213
			Return "YCbCrPositioning"
		Case $0214
			Return "ReferenceBlackWhite"
		Case $8298
			Return "Copyright"
			
		' -----------------------------------------------------
		' EXIF Sub-IFD values...
		' -----------------------------------------------------
		
		Case $0000829A
			Return "ExposureTime"
		Case $0000829D
			Return "FNumber"
		Case $00008822
			Return "ExposureProgram"
		Case $00008824
			Return "SpectralSensitivity"
		Case $00008827
			Return "ISOSpeedRatings"
		Case $00008828
			Return "OECF"
		Case $00009000
			Return "ExifVersion"
		Case $00009003
			Return "DateTimeOriginal"
		Case $00009004
			Return "DateTimeDigitized"
		Case $00009101
			Return "ComponentsConfiguration"
		Case $00009102
			Return "CompressedBitsPerPixel"
		Case $00009201
			Return "ShutterSpeedValue"
		Case $00009202
			Return "ApertureValue"
		Case $00009203
			Return "BrightnessValue"
		Case $00009204
			Return "ExposureBiasValue"
		Case $00009205
			Return "MaxApertureValue"
		Case $00009206
			Return "SubjectDistance"
		Case $00009207
			Return "MeteringMode"
		Case $00009208
			Return "LightSource"
		Case $00009209
			Return "Flash"
		Case $0000920A
			Return "FocalLength"
		Case $00009214
			Return "SubjectArea"
		Case $0000927C
			Return "MakerNote"
		Case $00009286
			Return "UserComment"
		Case $00009290
			Return "SubSecTime"
		Case $00009291
			Return "SubSecTimeOriginal"
		Case $00009292
			Return "SubSecTimeDigitized"
		Case $0000A000
			Return "FlashPixVersion"
		Case $0000A001
			Return "ColorSpace"
		Case $0000A002
			Return "ExifImageWidth"
		Case $0000A003
			Return "ExifImageHeight"
		Case $0000A004
			Return "RelatedSoundFile"
		Case $0000A20B
			Return "FlashEnergy"
		Case $0000A20C
			Return "SpatialFrequencyResponse"
		Case $0000A20E
			Return "FocalPlaneXResolution"
		Case $0000A20F
			Return "FocalPlaneYResolution"
		Case $0000A210
			Return "FocalPlaneResolutionUnit"
		Case $0000A214
			Return "SubjectLocation"
		Case $0000A215
			Return "ExposureIndex"
		Case $0000A217
			Return "SensingMethod"
		Case $0000A300
			Return "FileSource"
		Case $0000A301
			Return "SceneType"
		Case $0000A302
			Return "CFAPattern"
		Case $0000A401
			Return "CustomRendered"
		Case $0000A402
			Return "ExposureMode"
		Case $0000A403
			Return "WhiteBalance"
		Case $0000A404
			Return "DigitalZoomRatio"
		Case $0000A405
			Return "FocalLengthIn35mmFilm"
		Case $0000A406
			Return "SceneCaptureType"
		Case $0000A407
			Return "GainControl"
		Case $0000A408
			Return "Contrast"
		Case $0000A409
			Return "Saturation"
		Case $0000A40A
			Return "Sharpness"
		Case $0000A40B
			Return "DeviceSettingDescription"
		Case $0000A40C
			Return "SubjectDistanceRange"
		Case $0000A420
			Return "ImageUniqueID"
	
		' -----------------------------------------------------
		' GPS sub-IFD values...
		' -----------------------------------------------------
	
		Case $0
			Return "GPSVersionID"
		Case $1
			Return "GPSLatitudeRef"
		Case $2
			Return "GPSLatitude"
		Case $3
			Return "GPSLongitudeRef"
		Case $4
			Return "GPSLongitude"
		Case $5
			Return "GPSAltitudeRef"
		Case $6
			Return "GPSAltitude"
		Case $7
			Return "GPSTimeStamp"
		Case $8
			Return "GPSSatellites"
		Case $9
			Return "GPSStatus"
		Case $A
			Return "GPSMeasureMode"
		Case $B
			Return "GPSDOP"
		Case $C
			Return "GPSSpeedRef"
		Case $D
			Return "GPSSpeed"
		Case $E
			Return "GPSTrackRef"
		Case $F
			Return "GPSTrack"
		Case $10
			Return "GPSImgDirectionRef"
		Case $11
			Return "GPSImgDirection"
		Case $12
			Return "GPSMapDatum"
		Case $13
			Return "GPSDestLatitudeRef"
		Case $14
			Return "GPSDestLatitude"
		Case $15
			Return "GPSDestLongitudeRef"
		Case $16
			Return "GPSDestLongitude"
		Case $17
			Return "GPSDestBearingRef"
		Case $18
			Return "GPSDestBearing"
		Case $19
			Return "GPSDestDistanceRef"
		Case $1A
			Return "GPSDestDistance"
		Case $1B
			Return "GPSProcessingMethod"
		Case $1C
			Return "GPSAreaInformation"
		Case $1D
			Return "GPSDateStamp"
		Case $1E
			Return "GPSDifferential"
		
	End Select

End Function

Function ParseIFD (params:ParameterBundle)

	' ---------------------------------------------------------------
	' Local function for processing tags...
	' ---------------------------------------------------------------
	
	' ---------------------------------------------------------------
	' Image File Directory (IFD) parser (header is in TIFF format)...
	' ---------------------------------------------------------------

	Print ""

	Local dirs:Short
	Local loop:Int
	Local nextifd:Int
	
	Local count:Int = 0
	
	Repeat

		count = count + 1
		
'		Print "DIR COUNT: " + count
		
		' ----------------------------------------------------------
		' Loop through directories, read next IFD offset, abort if 0
		' ----------------------------------------------------------

		TabPrint "--------------------------------------------------------------------------------", params.level
		TabPrint "Reading new IFD", params.level
		TabPrint "--------------------------------------------------------------------------------", params.level
		
'Print ""
'Print "DIRS START at " + StreamPos (params.jpeg)

		dirs = ReadShort (params.jpeg)
		
		If params.endian = ENDIAN_INTEL Then dirs = SwapEndianShort (dirs)

'		Print "Reading " + dirs + " directories..."
		
		Local abort:Int = False
		
		' ----------------------------------------------------------
		' Directory loop...
		' ----------------------------------------------------------

		For loop = 0 Until dirs
		
			' Loop #7 recursing...

			' -----------------------------------------------------
			' 12 bytes per entry...
			' -----------------------------------------------------

			params.tagpos = StreamPos (params.jpeg)
			
			params.tag			= ReadShort (params.jpeg)
			params.dataformat	= ReadShort (params.jpeg)
			params.components	= ReadInt (params.jpeg)
			params.value		= ReadInt (params.jpeg)
			
			If params.endian = ENDIAN_INTEL
				params.tag			= SwapEndianShort (params.tag)
				params.dataformat	= SwapEndianShort (params.dataformat)
				params.components	= SwapEndianInt (params.components)
				params.value		= SwapEndianInt (params.value)
			EndIf

'			Print "TAG AT " + params.tagpos + ": $" + Hex (params.tag)

			' Invalid tag values in J:\My Pictures\Cars\78vauxhall_equus_21.jpg ! Offset 268...
			' Also get this in J:\My Pictures/Music/Pixies/buenos aires 05-02-03.jpg after Interoperability IFD tag...
			
			If params.dataformat = 0 Then Print "INVALID DATA FORMAT ON LOOP " + loop; abort = True; Exit
			
			ProcessTag params

		Next

'		Print "DIRS STOP at " + StreamPos (params.jpeg)

		' ----------------------------------------------------------
		' Assume more IFDs to read if valid data format was found...
		' ----------------------------------------------------------

		If Not abort

			nextifd = ReadInt (params.jpeg) ' THIS IS IT... probably.
			
			If params.endian = ENDIAN_INTEL Then nextifd = SwapEndianInt (nextifd)

			' Seek to next IFD...

			If nextifd
			
				Local seekto:Int = params.tiffstart + nextifd
				
				' Make sure where we're going is within the TIFF section of
				' the JPEG file... or we'll crash/loop forever on broken files...
				
				If seekto < params.tiffstart Or seekto > params.tiffend
					nextifd = 0 ' Invalid TIFF pointer!
				Else
					SeekStream params.jpeg, seekto'; Print "NEXTIFD Seeked to " + StreamPos (params.jpeg)
				EndIf
				
			EndIf
		
		Else
			nextifd = 0 ' Abort, bad data format, assume no more IFDs...
		EndIf
		
	Until nextifd = 0

	TabPrint "--------------------------------------------------------------------------------", params.level
	TabPrint "", params.level
	
End Function

Function PrintJPEGInfo (f:String, singleframe:Int = False)

	Print ""
	Print "Info for " + f
	
	Local jpeg:TStream = BigEndianStream (ReadFile (f))

	If jpeg

		Try

			' Start of Image (SOI) marker ($FFD8) -- MUST BE PRESENT!
			
			If ReadByte (jpeg) = $FF And ReadByte (jpeg) = $D8

			'	TabPrint ""
			'	TabPrint "--------------------------------------------------------------------------------"
			'	TabPrint "Start of Image marker $D8 found at byte offset 0"
			'	TabPrint "--------------------------------------------------------------------------------"
			'	TabPrint "Assuming JPEG file"
				
				Local loop:Int			' For byte seek loops...
				Local datalength:Int	' Block length store

				Local checkff:Byte		' Byte to be tested for $FF (start of block)...
				Local marker:Byte		' Block marker...

				Local startofblock:Int
				Local startofdata:Int
				Local blockbytesread:Int

				Local markerinfo:String
				Local scandata:Int
				
				Local printinfo:Int
				
				Local exifcount:Int
				Local alldone:Int
				
				While Not Eof (jpeg)
		
					' Searching for blocks beginning with $FF, then single byte marker, then data...
					
					' |FFxx|length_of_block|data_data_data...

					' |FFxx|length_of_block| is four bytes total, two each...
					
					' ---------------------------------------------------------
					' You are here --> |FFxx|length_of_block|data_data_data...
					' ---------------------------------------------------------
					
					startofblock = StreamPos (jpeg)
					
					' Looking for FF first...
					
					Repeat
						checkff = ReadByte (jpeg) ' Some Photoshop 7 files have a huge string of zeroes directly after block's stated data length
					Until checkff

					' Used later...
					
					startofdata = 0
					blockbytesread = 0
					datalength = 0
					markerinfo = ""
					
					If checkff = $FF
	
						' ... then xx, the byte AFTER the FF block marker, skipping if FF (padding)...
						
						Repeat
							marker = ReadByte (jpeg)
						Until marker <> $FF

						' -----------------------------------------------------
						' We are now here --> |length_of_block|data_data_data...
						' -----------------------------------------------------
						
						' Grab next two bytes (length of block) before proceeding, unless marker is standalone...

						Select marker
						
							Case $D0, $D1, $D2, $D3, $D4, $D5, $D6, $D7, $D8, $D9, $0, $FF
							
								' Standalone markers with no following data.
							
							Default

								datalength = ReadShort (jpeg) - 2 ' The 2 subtracted bytes store the length itself...
						
						End Select
						
						' -----------------------------------------------------
						' Now here --> |data_data_data...
						' -----------------------------------------------------
						
						' Record start of data so we can deduce how many bytes are read in each Case afterwards...

						startofdata = StreamPos (jpeg)
						
						scandata = False
						printinfo = False
						
						Select marker
							
							Case $0, $FF
							
								' Ignore these...

							Case $C0
							
								markerinfo = "Start of Frame (Huffman Baseline DCT)"

								printinfo = True

							Case $C1
							
								markerinfo = "Start of Frame (Huffman Extended Sequential DCT)"

								printinfo = True

							Case $C2
							
								markerinfo = "Start of Frame (Huffman Progressive DCT)"

								printinfo = True

							Case $C3
							
								markerinfo = "Start of Frame (Huffman Lossless Seqential)"

								printinfo = True

							Case $C4
							
								markerinfo = "Define Huffman Table"

							Case $C5
							
								markerinfo = "Start of Frame (Huffman Differential Sequential DCT)"

								printinfo = True

							Case $C6
							
								markerinfo = "Start of Frame (Huffman Differential Progressive DCT)"

								printinfo = True

							Case $C7
							
								markerinfo = "Start of Frame (Huffman Differential Lossless Sequential)"

								printinfo = True

							Case $C8
							
								markerinfo = "Start of Frame (Arithmetic, reserved for JPEG extensions)"

								printinfo = True

							Case $C9
							
								markerinfo = "Start of Frame (Arithmetic Extended Sequential DCT)"

								printinfo = True

							Case $CA
							
								markerinfo = "Start of Frame (Arithmetic Progressive DCT)"

								printinfo = True

							Case $CB
							
								markerinfo = "Start of Frame (Arithmetic Lossless Sequential)"

								printinfo = True

							Case $CC
							
								markerinfo = "Define Arithmetic Table"

							Case $CD
							
								markerinfo = "Start of Frame (Arithmetic Differential Sequential DCT)"

								printinfo = True

							Case $CE
							
								markerinfo = "Start of Frame (Arithmetic Differential Progressive DCT)"

								printinfo = True

							Case $CF
							
								markerinfo = "Start of Frame (Arithmetic Differential Lossless Sequential)"

								printinfo = True

							Case $D0, $D1, $D2, $D3, $D4, $D5, $D6, $D7
							
								markerinfo = "Restart"
								
								' Standalone marker, no following data...
								
							Case $D8
							
								markerinfo = "Start of Image"

								' Now going through scan (picture) data and ignoring it because it's bloody complicated...
							
								Local newff:Byte
								Local breakout:Int = False
								
								Local startdatascan:Int = StreamPos (jpeg)
								
								Repeat
								
									' Got a $FF value?
									
									If ReadByte (jpeg) = $FF

										' See if it's a new block marker...
										
										newff = ReadByte (jpeg)
										
										If (newff <> 0) And (newff <> $FF) ' Ignore 0/FF (possible valid/padding data)...
											breakout = True
										EndIf
									
									EndIf
									
								Until breakout
								
								' Special case... $D8 can appear multiple times per JPEG file, not just at the end. (Multiple images.)
								
								If newff = $D9
									If Eof (jpeg)
										alldone = True
									EndIf
								EndIf

								Local scanlength:Int = StreamPos (jpeg) - startdatascan
								
								' Add scan data to datalength...
								
								datalength = (datalength + scanlength) - 2 ' We need to go back two previously read bytes so they can be parsed...
								
							Case $D9
							
								'Print "D9"
							
								markerinfo = "End of Image"
								
								'TabPrint "EOI"

								' Now going through scan (picture) data and ignoring it because it's bloody complicated...
							
								Local newff:Byte
								Local breakout:Int = False
								
								Local startdatascan:Int = StreamPos (jpeg)
								
								Repeat
								

								'TabPrint "SP: " + StreamPos (jpeg)
								
									' Got a $FF value?
									
									If Eof (jpeg) Then breakout = True'; Print "EOF!"
									
									If ReadByte (jpeg) = $FF

										' See if it's a new block marker...
										
										newff = ReadByte (jpeg)
										
										'Print Hex (newff)
										
										' Value after $FF marker is 0 or $FF, both valid padding data, so keep reading,
										' otherwise, break out of loop:
										
										If (newff <> 0) And (newff <> $FF) ' Ignore 0/FF (possible valid/padding data)...
											breakout = True
										EndIf
																		
									EndIf
								
								Until breakout
								
								' Special case... $D9 can appear multiple times per JPEG file, not just at the end. (Multiple images.)
								
								If newff = $D9
									'Print StreamPos (jpeg)
									If Eof (jpeg)
										'Print "ALLDONE"'; End
										alldone = True
									EndIf
								EndIf
								
								Local scanlength:Int = StreamPos (jpeg) - startdatascan
								
								' Add scan data to datalength...
								
								datalength = (datalength + scanlength) - 2 ' We need to go back two previously read bytes so they can be parsed...
								
								' Standalone marker, no following data...
								
							Case $DA
							
								markerinfo = "Start of Scan"
								
								'TabPrint "START OF SCAN DATA"
								
								For loop = 0 Until ReadByte (jpeg) ' Components
									ReadByte jpeg ' Component ID
									ReadByte jpeg ' Huffman table (bits 0-3: AC table; bits 4-7: DC table)
								Next
								
								' Ignore these three bytes...
								
								ReadByte (jpeg)
								ReadByte (jpeg)
								ReadByte (jpeg)

								' Now going through scan (picture) data and ignoring it because it's bloody complicated...
							
								Local newff:Byte
								Local breakout:Int = False
								
								Local startdatascan:Int = StreamPos (jpeg)
								
								Repeat
								
									' Got a $FF value?
									
									If ReadByte (jpeg) = $FF

										' See if it's a new block marker...
										
										newff = ReadByte (jpeg)
										
										Select newff
									
											' Ignore 0 (means valid FF value in scan data), FF (possible padding data), D0-D7 (restart markers)...
											
											Case $0, $FF, $D0, $D1, $D2, $D3, $D4, $D5, $D6, $D7

												' Ignore these and move on...
												
											' Anything else? Must be a valid block marker, in theory...
											
											Case $D9
												'TabPrint "D9"
												breakout = True
												
											Default
											
												breakout = True
									
										End Select
									
									EndIf
									
								Until breakout
								
								' Special case... $D9 can appear multiple times per JPEG file, not just at end! (Multiple images.)
								
								If newff = $D9
									'TabPrint "D9 END"
									If Eof (jpeg)
										alldone = True
									EndIf
								EndIf
								
								'Print "EOS " + StreamPos (jpeg)
								
								Local scanlength:Int = StreamPos (jpeg) - startdatascan
								
								' Consider scan data to be part of datalength...
								
								datalength = (datalength + scanlength) - 2 ' We need to go back two previously read bytes so they can be parsed...
								
							Case $DB
							
								markerinfo = "Define Quantization Table"
							
							Case $DC
							
								markerinfo = "Define Number of Lines"

							Case $DD
							
								markerinfo = "Define Restart Interval"
								
							Case $DE
							
								markerinfo = "Define Hierarchical Progression"
							
							Case $DF
							
								markerinfo = "Expand reference components"
							
							Case $E0 ' JFIF marker
						
								markerinfo = "JFIF/APP0"
								
								' Check for JFIF identification string (generally treated as optional)...
								
								ReadString jpeg, 5
								
								' Or...
								
'								If ReadString (jpeg, 5) = "JFIF" + Chr (0)
									'TabPrint "Valid JFIF file"
'								Else
									'TabPrint "Not a valid JFIF file"
'								EndIf

							Case $E1 ' EXIF information
							
								markerinfo = "EXIF"

								' Only parse if Exif00 is here. Apparently other apps can insert data using
								' this tag, which will cause reader to fail if it's not EXIF data...
								
'								TabPrint "EXIF data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
								
								If ReadString (jpeg, 6) = "Exif" + Chr (0) + Chr (0)
									PrintEXIFInfo jpeg, datalength - 6 ' Length of TIFF data containing EXIF information!
									exifcount = exifcount + 1
								EndIf
								
								' Exit ' Early-out if we only need first lot of EXIF data...
					
							Case $E2, $E3, $E4, $E5, $E6, $E7, $E8, $E9, $EA, $EB, $EC, $EE, $EF
								
								markerinfo = "Unimplemented, application-specific"
							
							Case $ED
							
								markerinfo = "Photoshop/APP14"

							Case $FE
							
								markerinfo = "Comment"
								
							Default
								
								markerinfo = "UNIMPLEMENTED"
								
						End Select

'						TabPrint ""
'						TabPrint "--------------------------------------------------------------------------------"
'						TabPrint markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
'						TabPrint "--------------------------------------------------------------------------------"
						
				'		If datalength
				'			TabPrint "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
				'		Else
				'			TabPrint "No data for this type of marker"
				'		EndIf
						
				'		If printinfo
							'PrintImageData jpeg ' Got a $Cx marker so print info!
							' Exit ' Only wanted information for main image, don't need EXIF? Just uncomment this!
				'		EndIf

					Else
						' Invalid marker offset, so skip rest of file for safety...
						'TabPrint "BOOM! INVALID MARKER: " + Hex (checkff) + " -- skipping rest of file!"
						Exit
'						End
					EndIf

					If alldone
					
						'TabPrint ""
						'TabPrint "--------------------------------------------------------------------------------"
						'TabPrint "End of Image marker $D9 found at end of file -- all done!"
						'TabPrint "--------------------------------------------------------------------------------"
						'TabPrint ""

					Else
					
						' Number of bytes already read in this block...
							
						blockbytesread = StreamPos (jpeg) - startofdata
	
						' Go to next block...

						SeekStream jpeg, StreamPos (jpeg) + (datalength - blockbytesread)
						'Print "NEXTBLOCK Seeked to " + StreamPos (jpeg)
						
					EndIf
					
				Wend
		
				If exifcount = 0 Then Print "No EXIF data in file."

			EndIf

			Catch ReadFail:Object
			Print "Read error in " + f + "; " + StreamPos (jpeg)

		End Try
		
		CloseFile jpeg
	
	Else
		TabPrint "File not found!"
	EndIf

End Function

' Byte-endianness...

Const ENDIAN_MOTOROLA:Int = 0
Const ENDIAN_INTEL:Int = 1

' --------------------------------------------------------------------
' Bundle of frequently-passed parameters among these functions...
' --------------------------------------------------------------------

Type ParameterBundle

	' JPEG's EXIF data is actually stored in an embedded TIFF file...
	
	Field jpeg:TStream			' File
	
	Field tiffstart:Int			' TIFF 'file' start
	Field tiffend:Int			' Last byte of TIFF 'file'
	
	Field ifdoffset:Int			' Next IFD offset
	
	Field tag:Short				' Data tag
	Field value:Int				' Tag value
	Field endian:Int			' Byte order
	Field components:Int		' Data components
	Field dataformat:Short		' Data format
	Field tagpos:Int			' Tag position in file
	
	Field level:Int ' For TabPrint -- indents based on level of recursion
	
End Type

' Gah, where's Monkey's BoxInt when you need it?

Type IFDOffset
	Field offset:Int
End Type
