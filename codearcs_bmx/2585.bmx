; ID: 2585
; Author: BlitzSupport
; Date: 2009-09-21 14:13:03
; Title: GIMP file format exploration
; Description: Reads the GIMP's XCF file format

' -----------------------------------------------------------------------------
' GIMP file format exploration...
' -----------------------------------------------------------------------------

SuperStrict

' -----------------------------------------------------------------------------
' GIMP XCF file format specs taken from...
' -----------------------------------------------------------------------------
' http://svn.gnome.org/viewvc/gimp/trunk/devel-docs/xcf.txt?view=markup
' -----------------------------------------------------------------------------

' -----------------------------------------------------------------------------
' Test calls...
' -----------------------------------------------------------------------------

GIMPInfo "lagunafixed.xcf"
GIMPInfo "lagunaclaycanvas.xcf"
GIMPInfo "test.xcf"
GIMPInfo "boing.xcf"

' -----------------------------------------------------------------------------
' Constants...
' -----------------------------------------------------------------------------

Const PROP_COLORMAP:Int				= 1
Const PROP_ACTIVE_LAYER:Int 			= 2
Const PROP_OPACITY:Int				= 6
Const PROP_MODE:Int					= 7
Const PROP_VISIBLE:Int				= 8
Const PROP_LINKED:Int				= 9
Const PROP_PRESERVE_TRANSPARENCY:Int	= 10
Const PROP_APPLY_MASK:Int			= 11
Const PROP_EDIT_MASK:Int				= 12
Const PROP_SHOW_MASK:Int				= 13
Const PROP_OFFSETS:Int				= 15
Const PROP_COMPRESSION:Int			= 17
Const PROP_RESOLUTION:Int			= 19
Const PROP_TATTOO:Int				= 20
Const PROP_PARASITES:Int				= 21
Const PROP_UNIT:Int					= 22 ' Incorrectly defined as 21 in above document!

' -----------------------------------------------------------------------------
' GIMP file reader...
' -----------------------------------------------------------------------------

Function GIMPInfo (filename:String)

	' ------------------------------------------------------------------------
	' Local function for reading zero-terminated strings...
	' ------------------------------------------------------------------------

	Function ReadZeroString:String (file:TStream)
		
		Local sbyte:Byte
		Local zstring:String
		
		Repeat
			sbyte = ReadByte (file)
			If sbyte Then zstring = zstring + Chr (sbyte)
		Until sbyte = 0

		Return zstring
				
	End Function

	' ------------------------------------------------------------------------
	' Local function for reading GIMP 'property' values...
	' ------------------------------------------------------------------------
	
	Function ReadProperties:Int (file:TStream)
	
		Local property_type:Int
		Local payload_length:Int
	
		' To support more properties, see line 546, entitled "Layer properties",
		' in the document linked at the top of this file.
		
		' General method: Copy the PROP_TEMPLATE code below, define the Const value of
		' the property as per the document, change xxx to the number of bytes in the
		' payload (as per the document), then just read and interpret the data that
		' follows, eg.
		
		' PROP_SHOW_MASK (editing state)
		'	 uint32  13  The type number for PROP_APPLY_MASK is 13			' This is the Const value to declare
		'	 uint32  4   Four bytes of payload							' This is how many bytes to read
		'	 uint32  b   1 if the layer mask is visible, 0 if not			' The is the data (one integer in this case)

		' ... becomes:
		
		Rem
				Const PROP_SHOW_MASK:Int = 13 ' Add to top of this file!
				
				...
				
				Case PROP_SHOW_MASK

					payload_length = ReadInt (file)
					
					If payload_length = 4
					
						Local mask_visible:Int = ReadInt (file)
						If mask_visible Then Print "Mask visible" Else Print "Mask not visible"
						
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf
		End Rem
		
		Repeat
	
			property_type = ReadInt (file)
	
			Select property_type

				Rem
				
				Case PROP_TEMPLATE

					payload_length = ReadInt (file)
					
					If payload_length = xxx
					
						
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf
				
				End Rem
				
				Case 0
				
					' Do nothing!
				
				Case PROP_COLORMAP
				
					payload_length = ReadInt (file)
					
					Local num_colors:Int = ReadInt (file)
					
					Local red:Byte [num_colors], green:Byte [num_colors], blue:Byte [num_colors]

					Print "~tReading color map into RGB arrays..."
										
					For Local loop:Int = 0 Until num_colors
						red [loop]	= ReadByte (file)
						green [loop]	= ReadByte (file)
						blue [loop]	= ReadByte (file)
					Next
					
				Case PROP_ACTIVE_LAYER

					' No payload to read...
					
					Print "~tThis is the active layer"
				
				Case PROP_OPACITY
				
					payload_length = ReadInt (file)
					
					If payload_length = 4
						Local opacity:Int = ReadInt (file)
						Print "~tOpacity: " + opacity + " out of 255"
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf

				Case PROP_MODE
				
					payload_length = ReadInt (file)
					
					If payload_length = 4
					
						Local layer_mode:String
						
						Select ReadInt (file)
						
							Case 0
								layer_mode = "Normal"
							Case 1
								layer_mode = "Dissolve"
							Case 2
								layer_mode = "Behind"
							Case 3
								layer_mode = "Multiply"
							Case 4
								layer_mode = "Screen"
							Case 5
								layer_mode = "Overlay"
							Case 6
								layer_mode = "Difference"
							Case 7
								layer_mode = "Addition"
							Case 8
								layer_mode = "Subtract"
							Case 9
								layer_mode = "Darken Only"
							Case 10
								layer_mode = "Lighten Only"
							Case 11
								layer_mode = "Hue"
							Case 12
								layer_mode = "Saturation"
							Case 13
								layer_mode = "Color"
							Case 14
								layer_mode = "Value"
							Case 15
								layer_mode = "Divide"
							Case 16
								layer_mode = "Dodge"
							Case 17
								layer_mode = "Burn"
							Case 18
								layer_mode = "Hard Light"
							Case 19
								layer_mode = "Soft Light"
							Case 20
								layer_mode = "Grain Extract"
							Case 21
								layer_mode = "Grain Merge"
							Default
								layer_mode = "Unknown"
								
						End Select
						
						Print "~tLayer mode: " + layer_mode
						
					Else
						Print "Payload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf

				Case PROP_VISIBLE
				
					payload_length = ReadInt (file)
					
					If payload_length = 4
						If ReadInt (file) = 1
							Print "~tLayer visible"
						Else
							Print "~tLayer not visible"
						EndIf
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf
					
				Case PROP_LINKED
				
					payload_length = ReadInt (file)
					
					If payload_length = 4
						If ReadInt (file) = 1
							Print "~tLayer linked"
						Else
							Print "~tLayer not linked"
						EndIf
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf
					
				Case PROP_PRESERVE_TRANSPARENCY
				
					payload_length = ReadInt (file)
					
					If payload_length = 4
						If ReadInt (file) = 1
							Print "~tLayer transparency preserved"
						Else
							Print "~tLayer transparency not preserved"
						EndIf
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf
				
				Case PROP_APPLY_MASK

					payload_length = ReadInt (file)
					
					If payload_length = 4
						If ReadInt (file) = 1
							Print "~tLayer mask applied"
						Else
							Print "~tLayer mask not applied"
						EndIf
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf

				Case PROP_EDIT_MASK

					payload_length = ReadInt (file)
					
					If payload_length = 4
						If ReadInt (file) = 1
							Print "~tLayer mask is being edited"
						Else
							Print "~tLayer mask is not being edited"
						EndIf
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf
					
				Case PROP_SHOW_MASK

					payload_length = ReadInt (file)
					
					If payload_length = 4
						If ReadInt (file) = 1
							Print "~tLayer mask is visible"
						Else
							Print "~tLayer mask is not visible"
						EndIf
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf
				
				Case PROP_OFFSETS
				
					payload_length = ReadInt (file)
					
					If payload_length = 8
					
						Local dx:Int = ReadInt (file)
						Local dy:Int = ReadInt (file)
						
						Print "~tLayer offsets from top-left of canvas: x = " + dx + ", y = " + dy
						
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf
				
				Case PROP_COMPRESSION
				
					payload_length = ReadInt (file)
					
					If payload_length = 1 ' Expected length for property
					
						Local compression:Byte = ReadByte (file)
						
						Select compression
							Case 0
								Print "~tNo compression"
							Case 1
								Print "~tRLE encoded"
							Case 2
								Print "~tzlib compression"
							Case 3
								Print "~tFractal compression"
						End Select
					
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf
				
				Case PROP_RESOLUTION
				
					payload_length = ReadInt (file)
					
					If payload_length = 8
						Local x_res:Float = ReadFloat (file)
						Local y_res:Float = ReadFloat (file)
						Print "~tResolution: " + x_res + " x " + y_res + " DPI"
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf

				Case PROP_TATTOO

					payload_length = ReadInt (file)
					
					If payload_length = 4
						Print "~tTattoo (unique ID for drawable element): " + ReadInt (file)
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf

				Case PROP_PARASITES
				
					payload_length = ReadInt (file)
					
					Local string_len:Int = ReadInt (file)
					Print "~tParasite name: " + ReadZeroString (file)
					
					ReadString file, payload_length - (string_len + 4) ' Subtract length of string and 4 bytes of string_len from payload_length
				
				Case PROP_UNIT

					payload_length = ReadInt (file)
					
					If payload_length = 4
					
						Local unit:String
						
						Select ReadInt (file)
							Case 0
								unit = "Inches"
							Case 1
								unit = "Millimetres"
							Case 2
								unit = "Points"
							Case 3
								unit = "Picas"
							Default
								unit = "Unknown unit size"
						End Select
						
						Print "~tUnits: " + unit
						
					Else
						Print "~tPayload length unexpected! Contains: " + ReadString (file, payload_length)
					EndIf
					
				Default
	
					Print "~tUnhandled property type: " + property_type
	
					payload_length = ReadInt (file)
					ReadString file, payload_length
	
			End Select
	
		Until property_type = 0
	
	End Function

	' ------------------------------------------------------------------------
	' Main function code...
	' ------------------------------------------------------------------------

	Local file:TStream = BigEndianStream (ReadFile (filename))
	
	If file
	
		Print ""
		Print "--"
		Print ""
		Print "Filename: " + filename
		
		' Read expected XCF string...
		
		If ReadString (file, 9) = "gimp xcf "
		
			Local version:String = ReadString (file, 4)
			Local v:Int
		
			' Version string may be "file" or "v001", "v002", etc...
				
			If version = "file"
				v = 0
			Else
				If Left (version, 1) = "v"
					v = Int (Right (version, 3)) ' Read xxx from "vxxx", eg. "v002"
				Else
					v = -1
				EndIf
			EndIf
			
			' WTF? Should probably abort here...
			
			If v = -1
				version = "Unknown XCF format!"
			Else
				version = v
			EndIf
			
			Print "File format version: " + version
			
			ReadByte file ' String's 0-byte
		
			' Width, height and pixel format...
			
			Local width:Int = ReadInt (file)
			Local height:Int = ReadInt (file)
			Local pixels:Int = ReadInt (file)
			
			Local pixel_format:String
			
			Select pixels
				Case 0
					pixel_format = "RGB color"
				Case 1
					pixel_format = "Grayscale"
				Case 2
					pixel_format = "Indexed color"
				Default
					pixel_format = "Unknown depth format"
			End Select
			
			Print "Image size: " + width + " x " + height + " (" + pixel_format + ")"
			Print ""
			Print "Image properties:"
			Print ""
			
			ReadProperties file ' Call local ReadProperties function to get master image properties...
			
			' Read layers...
			
			Local layer:Int
			
			Repeat
			
				layer = ReadInt (file)
				
				If layer
					Print "Layer pointer: " + layer ' Doh... I was meant to deal with this! Should be handled much like channels, below...
				EndIf
				
			Until layer = 0
	
			' Read channels...
			
			Local channel:Int
			
			Repeat
			
				channel = ReadInt (file)
				
				If channel
				
					Local channel_pos:Int = StreamPos (file) ' Store current position in file...
					
						' Read channel information...
					
						SeekStream file, channel
						
						Local channel_width:Int = ReadInt (file)
						Local channel_height:Int = ReadInt (file)
					
						ReadInt (file) ' Undocumented integer!
						
						Local channel_string:String
						Local channel_string_length:Int = ReadInt (file)
						
						If channel_string_length
							channel_string = ReadString (file, channel_string_length - 1)
							ReadByte (file) ' String's 0-byte
						EndIf
						
						Print ""
						Print "Properties for channel ~q" + channel_string + "~q (" + channel_width + " x " + channel_height + ")"
						Print ""
						
						' Read channel properties via local ReadProperties function...
						
						ReadProperties file

						Local pixel_data:Int = ReadInt (file) ' Pointer to pixel data
						
					SeekStream file, channel_pos ' Returned to stored position in file...
	
				EndIf
				
			Until channel = 0
			
		Else
			Print "Not a GIMP file"		
		EndIf
		
		CloseFile file
		
	EndIf

End Function
