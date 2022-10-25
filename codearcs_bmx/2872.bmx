; ID: 2872
; Author: BlitzSupport
; Date: 2011-07-14 15:12:39
; Title: JPEG reference parser
; Description: Parses JPEG file structure and shows sections found

' -----------------------------------------------------------------------------
' JPEG reference parser...
' -----------------------------------------------------------------------------

SuperStrict

' -----------------------------------------------------------------------------
' READ THIS! Change to your own image folder for demo...
' -----------------------------------------------------------------------------

' Demo code at bottom includes single-file test...

Local folder$
folder = "H:\Docs\My Pictures\"

' -----------------------------------------------------------------------------
' Utility functions...
' -----------------------------------------------------------------------------

Function StreamRemainder:Int (jpeg:TStream)
	Return StreamSize (jpeg) - StreamPos (jpeg)
End Function

Function SkipData (jpeg:TStream, datalength:Int)
	ReadString jpeg, Min (datalength, StreamRemainder (jpeg))
End Function

Function PrintImageData (jpeg:TStream, datalength:Short)

	' Check we have enough bytes left in file, abort if not...
	
	If StreamRemainder (jpeg) < datalength
		SkipData jpeg, datalength
		Return
	EndIf
	
	Local bpp:Int = ReadByte (jpeg)		' Bits per pixel
	Local height:Int = ReadShort (jpeg)	' Height
	Local width:Int = ReadShort (jpeg)		' Width
	Local components:Int = ReadByte (jpeg)	' Components per pixel (1 for grayscale, 3 for RGB)
	
	Local depth:Int = bpp * components
	Local colors:Int = 2 ^ depth

	SkipData jpeg, datalength - 6 ' ' Skip rest of frame header after reading above six bytes...
	
	Print "Image details: " + width + " x " + height + " @ " + depth + "-bit (" + Int (2 ^ depth) + " colours)"

End Function

' -----------------------------------------------------------------------------
' JPEG parser...
' -----------------------------------------------------------------------------

Function ParseJPEG (f:String)

	Print "Info for " + f + " (file size: " + FileSize (f) + " bytes)"
	Print ""

	Local jpeg:TStream = BigEndianStream (ReadStream (f))
				
	If jpeg And StreamSize (jpeg) > 1 ' Next two bytes are safe!

		Try

			' Start of Image (SOI) marker ($FFD8) -- MUST BE PRESENT!
			
			If ReadByte (jpeg) = $FF And ReadByte (jpeg) = $D8

				Print ""
				Print "--------------------------------------------------------------------------------"
				Print "Start of Image marker $D8 found at byte offset 0"
				Print "--------------------------------------------------------------------------------"
				Print "Assuming JPEG file"
				
				Local loop:Int			' For byte seek loops
				Local datalength:Int	' Block length store

				Local checkff:Byte		' Byte to be tested for $FF (start of block)...
				Local marker:Byte		' Block marker code

				Local startofblock:Int	' Record marker location
				Local startofdata:Int	' Record data location after marker

				Local markerinfo:String
				
				' OK, start reading the file...
				
				While Not Eof (jpeg)
		
					' Searching for blocks beginning with $FF, then single byte marker, then data...
					
					' |FFxx|length_of_block|data_data_data...

					' |FFxx|length_of_block| is four bytes total, two each...
					
					' ---------------------------------------------------------
					' You are here --> |FFxx|length_of_block|data_data_data...
					' ---------------------------------------------------------
					
					startofblock = StreamPos (jpeg) ' Tracker for bytes read...
					
					' Looking for FF first...
					
					Repeat
						checkff = ReadByte (jpeg) ' Some Photoshop 7 files have a huge string of zeroes directly after block's stated data length
					Until (checkff = $FF) Or (Eof (jpeg))

					' Used later...
					
					startofdata = 0
					datalength = 0
					markerinfo = ""
					
					If Not Eof (jpeg) And checkff = $FF
	
						' ... then xx, the byte AFTER the FF block marker, skipping if FF (padding)...
						
						Repeat
							marker = ReadByte (jpeg)
						Until (marker <> $FF) Or (Eof (jpeg))

						' -----------------------------------------------------
						' We are now here --> |length_of_block|data_data_data...
						' -----------------------------------------------------
						
						' Grab next two bytes (length of block) before proceeding, unless marker is standalone...

						Select marker
						
							Case $D0, $D1, $D2, $D3, $D4, $D5, $D6, $D7, $D8, $D9, $0, $FF
							
								' Standalone markers with no following data.
							
							Default

								datalength = 0
								
								If StreamRemainder (jpeg) > 1
									datalength = ReadShort (jpeg) - 2 ' The 2 subtracted bytes store the length itself...
								EndIf
						
						End Select
						
						' -----------------------------------------------------
						' Now here --> |data_data_data...
						' -----------------------------------------------------
						
						' Record start of data so we can deduce how many bytes are read in each Case afterwards...

						startofdata = StreamPos (jpeg)
						
						Select marker

							' ------------------------------------------------
							' Padding
							' ------------------------------------------------
							
							Case $0, $FF
							
								' Ignore these...

							' ------------------------------------------------
							' Frame decoding table markers
							' ------------------------------------------------

							Case $C4
							
								markerinfo = "Define Huffman Table"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf
								
							Case $CC
							
								markerinfo = "Define Arithmetic Table"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf
								
							' ------------------------------------------------
							' Frame markers (image data)
							' ------------------------------------------------

							' NB. Printing data for ALL image frames found, but the first one listed is always the main image...
							
							' You can add an "Exit" at the end of each of these cases to only read the main image
							' information...
							
							Case $C0, $C1, $C2, $C3
							
								markerinfo = "Start of Frame (non-differential Huffman coding)"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									PrintImageData jpeg, datalength
								Else
									Print "No data for this type of marker"
								EndIf

							Case $C5, $C6, $C7
							
								markerinfo = "Start of Frame (differential Huffman coding)"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									PrintImageData jpeg, datalength
								Else
									Print "No data for this type of marker"
								EndIf
						
							Case $C8, $C9, $CA, $CB
							
								markerinfo = "Start of Frame (non-differential arithmetic coding)"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									PrintImageData jpeg, datalength
								Else
									Print "No data for this type of marker"
								EndIf
						
							Case $CD, $CE, $CF
							
								markerinfo = "Start of Frame (differential arithmetic coding)"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									PrintImageData jpeg, datalength
								Else
									Print "No data for this type of marker"
								EndIf

							' ------------------------------------------------
							' Restart markers (only used when decoding images)
							' ------------------------------------------------

							Case $D0, $D1, $D2, $D3, $D4, $D5, $D6, $D7
							
								markerinfo = "Restart"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								Print "No data for this type of marker"
								
								' Standalone marker, no following data...
								
							' ------------------------------------------------
							' Start of JPEG data
							' ------------------------------------------------

							Case $D8
							
								markerinfo = "Start of Image"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf
								
								' Now going through scan (picture) data and ignoring it because it's bloody complicated...
							
								Local newff:Byte
								Local foundmarker:Int
								
								Local startdatascan:Int = StreamPos (jpeg)
								Local bytesread:Int
								
								While Not Eof (jpeg) And (Not foundmarker)
										
									If ReadByte (jpeg) = $FF

										If Not Eof (jpeg)

											' See if it's a new block marker...
											
											newff = ReadByte (jpeg)
											
											If (newff <> 0)
												foundmarker = newff
												Exit
											EndIf

										EndIf

									EndIf
									
								Wend
								
								If Eof (jpeg)
									
									If foundmarker
										Print "~nMarker $" + Right (Hex (foundmarker), 2) + " found at end of file"
									Else
										Print "~nFile ends with extraneous data"
									EndIf
									
								Else
									
									' Go back two bytes if we ran into a marker so that it can be processed in main loop...
									
									If foundmarker Then SeekStream jpeg, StreamPos (jpeg) - 2
									
								EndIf
									
							' ------------------------------------------------
							' End of JPEG data
							' ------------------------------------------------

							Case $D9

								markerinfo = "End of Image"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf

								' Now seeking through scan (picture) data...
							
								Local newff:Byte
								Local foundmarker:Int
								
								Local startdatascan:Int = StreamPos (jpeg)
								Local bytesread:Int
								
								While Not Eof (jpeg) And (Not foundmarker)
										
									If ReadByte (jpeg) = $FF

										If Not Eof (jpeg)
										
											' See if it's a new block marker...
											
											newff = ReadByte (jpeg)
											
											If (newff <> 0) And (newff <> $FF)
												foundmarker = newff
												Exit
											EndIf
											
										EndIf

									EndIf
									
								Wend
								
								If Eof (jpeg)
									
									If foundmarker
										Print "~nMarker $" + Right (Hex (foundmarker), 2) + " found at end of file"
									Else
										Print "~nFile ends with extraneous data"
									EndIf
									
								Else
									
									' Go back two bytes if we ran into a marker so that it can be processed in main loop...
									
									If foundmarker Then SeekStream jpeg, StreamPos (jpeg) - 2
									
								EndIf

							' ------------------------------------------------
							' Image data
							' ------------------------------------------------

							Case $DA
							
								markerinfo = "Start of Scan"
								
								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf
								
								' Now seeking through scan (picture) data...
							
								Local newff:Byte
								Local foundmarker:Int
								
								Local startdatascan:Int = StreamPos (jpeg)
								Local bytesread:Int
								
								While Not Eof (jpeg) And (Not foundmarker)
										
									If ReadByte (jpeg) = $FF
									
										If Not Eof (jpeg)
	 
											' See if it's a new block marker...
											
											newff = ReadByte (jpeg)
											
											Select newff
										
												' Ignore 0 (means valid FF value in scan data), FF (possible padding data), D0-D7 (restart markers)...
												
												Case $0, $FF, $D0, $D1, $D2, $D3, $D4, $D5, $D6, $D7
												
													' Ignore these and move on...
	
												Default
													
													' Valid marker; break out of bank stream reader and then the file stream reader...
									
													foundmarker = newff
													Exit
													
											End Select
										
										EndIf
									
									EndIf
									
								Wend
								
								If Eof (jpeg)
									
									If foundmarker
										Print "~nMarker $" + Right (Hex (foundmarker), 2) + " found at end of file"
									Else
										Print "~nFile ends with extraneous data"
									EndIf
									
								Else
									
									' Go back two bytes if we ran into a marker so that it can be processed in main loop...
									
									If foundmarker Then SeekStream jpeg, StreamPos (jpeg) - 2
									
								EndIf
								
							' ------------------------------------------------
							' Quantization table, ignored
							' ------------------------------------------------

							Case $DB
							
								markerinfo = "Define Quantization Table"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf

							' ------------------------------------------------
							' Number of lines in scan, ignored
							' ------------------------------------------------
								
							Case $DC
							
								markerinfo = "Define Number of Lines"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf
								
							' ------------------------------------------------
							' Restart interval, ignored
							' ------------------------------------------------
								
							Case $DD
							
								markerinfo = "Define Restart Interval"
								
								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf
								
							' ------------------------------------------------
							' Hierarchical progression, ignored
							' ------------------------------------------------
								
							Case $DE
							
								markerinfo = "Define Hierarchical Progression"
								
								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf
								
							' ------------------------------------------------
							' Expand reference components, ignored
							' ------------------------------------------------
								
							Case $DF
							
								markerinfo = "Expand reference components"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf
								
							' ------------------------------------------------
							' APP0 marker (mainly to state JFIF-compatibility)
							' ------------------------------------------------
								
							Case $E0 ' JFIF marker
						
								markerinfo = "JFIF/APP0"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf

							' ------------------------------------------------
							' APP1 marker (mainly used for EXIF data)
							' ------------------------------------------------

							Case $E1 ' EXIF information
							
								markerinfo = "EXIF/APP1"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf

							' ------------------------------------------------
							' Application-specific markers
							' ------------------------------------------------

							Case $E2, $E3, $E4, $E5, $E6, $E7, $E8, $E9, $EA, $EB, $EC, $EE, $EF
								
								markerinfo = "Application-specific"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf
								
							' ------------------------------------------------
							' Application-specific, but usually Photoshop
							' ------------------------------------------------

							Case $ED
							
								markerinfo = "Photoshop/APP14"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf
								
							' ------------------------------------------------
							' Comment marker
							' ------------------------------------------------

							Case $FE
							
								markerinfo = "Comment"
								
								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""

								If datalength
									SkipData jpeg, datalength							
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
								Else
									Print "No data for this type of marker"
								EndIf

							' ------------------------------------------------
							' Unknown marker. This shouldn't appear!
							' ------------------------------------------------

							Default
								
								markerinfo = "UNIMPLEMENTED"

								Print ""
								Print "--------------------------------------------------------------------------------"
								Print markerinfo + " marker $" + Right (Hex (marker), 2) + " found at byte offset " + startofblock
								Print "--------------------------------------------------------------------------------"
								Print ""
								
								If datalength
									Print "Data starts at byte offset " + startofdata + " and is " + datalength + " bytes long"
									SkipData jpeg, datalength							
								Else
									Print "No data for this type of marker"
								EndIf
								
						End Select

					Else
					
						' We reached end of file or read an invalid byte (should be an $FF marker)
						' so ignore the rest of the file...

						Exit

					EndIf
					
				Wend
		
			Else
				Print "Not a JPEG file!"
			EndIf

			Catch ReadFail:Object
			Notify "Read error in " + f + "; " + StreamPos (jpeg)

		End Try
		
		CloseStream jpeg

	Else
		Print "File not found, or shorter than two required bytes!"
	EndIf

End Function

' -----------------------------------------------------------------------------
' D E M O . . .
' -----------------------------------------------------------------------------

' Uncomment these 4 lines to test a single picture...

'Local img$
'img = "CHANGE ME"
'ParseJPEG img
'End

' -----------------------------------------------------------------------------
' Or name a local folder (sub-folders will be read too)...
' -----------------------------------------------------------------------------

ParseFolder folder

' -----------------------------------------------------------------------------
' Test function to iterate through all sub-folders...
' -----------------------------------------------------------------------------

Global ImageCount:Long

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
					
					Print ""
					Print "--------------------------------------------------------------------------------"
					Print "Reading image number " + ImageCount + "..."
					Print "--------------------------------------------------------------------------------"
					
					ParseJPEG full
		
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
