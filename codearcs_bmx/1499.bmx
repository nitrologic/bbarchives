; ID: 1499
; Author: Hotcakes
; Date: 2005-10-21 02:13:24
; Title: HOT.IFFILBMloader
; Description: A loader module for IFF ILBM images

Strict

Rem
bbdoc: IFF ILBM loader
End Rem
Module HOT.IFFILBMLoader

ModuleInfo "Version: 1.00"
ModuleInfo "Author: Toby Zuijdveld"
ModuleInfo "License: Blitz Shared Source Code"
ModuleInfo "Copyright: Jerry Morrison, Electronic Arts (public domain)"
ModuleInfo "Modserver: n/a"

ModuleInfo "History: 1.00 Release"

Import BRL.Pixmap
Import BRL.EndianStream

Private

Function ReadTag$( stream:TStream )
	Local tag:Byte[4]
	If stream.ReadBytes( tag,4 )<>4 Return
	Return Chr(tag[0])+Chr(tag[1])+Chr(tag[2])+Chr(tag[3])
End Function

Function UStoSI%(a:Short)
	If a>$7FFF Then Return -($8000-(a-$8000)) Else Return a
End Function	' unsigned short to signed integer

Rem
	"cmpByteRun1" is the byte run encoding
End Rem ' End Rem
Const	cmpNone		= 0
Const	cmpByteRun1	= 1

Global	ColourMapRed[]	= Null
Global	ColourMapGreen[]
Global	ColourMapBlue[]	' dodginess!

Public

Rem
bbdoc: Preload a Colour Map for use when loading future ILBM images
about:
#IFFCMAP loads CMAP information from the given @url file.<br>
<br>
Every subsequent ILBM image will be loaded using this palette.<br>
<br>
By default, the CMAP information stored with an ILBM picture would be used.  To restore that behaviour, use #IFFCMAP without a @url specified.<br>
If the Colour Map cannot be loaded, 0 is returned and this function's default behaviour will apply.<br>
End Rem
Function IFFCMAP( url:Object="" )
	If url=""
		ColourMapRed	= Null
		ColourMapGreen	= Null
		ColourMapBlue	= Null
		Return 1
	EndIf
	Local stream:TStream=ReadStream( url )
	If Not stream
		ColourMapRed	= Null
		ColourMapGreen	= Null
		ColourMapBlue	= Null
		Return 0
	EndIf
	stream	= BigEndianStream(stream)
	If ReadTag( stream )<>"FORM" Return
	Local	dud%	= stream.Readint()	' length of file after ILBM header
	If ReadTag( stream )<>"ILBM" Return
	Local	cmap%	= 0
	Local	i%		= 0
	Local	n%		= 0
	While Not stream.Eof()
		Local blahblah$=ReadTag$(stream)
		Select	blahblah$'Readtag$(stream)
			Case	"CMAP"	' ColorMap
				dud		= stream.ReadInt()	' length of ColourMap chunk
				cmap	= dud/3
				ColourMapRed	= ColourMapRed[..cmap+1]
				ColourMapGreen	= ColourMapGreen[..cmap+1]
				ColourMapBlue	= ColourMapBlue[..cmap+1]
				For i=1 To cmap
					ColourMapRed[i]		= stream.ReadByte()
					ColourMapGreen[i]	= stream.ReadByte()
					ColourMapBlue[i]	= stream.ReadByte()
				Next	' i=1 To cmap
				n=1
				Exit
			Default			' unsupported chunks
				stream.SkipBytes(stream.ReadInt())	' skip chunk
		End Select	' ReadTag$(stream)
		If n=0 Then Exit
	Wend	' Not stream.EOF()
	stream.Close
	Return n
End Function

Private

Type TPixmapLoaderILBM Extends TPixmapLoader
	Method LoadPixmap:TPixmap(stream:TStream)
		stream=BigEndianStream( stream )
		
		If ReadTag( stream )<>"FORM" Return

		Local	dud%					= stream.Readint()	' length of file after ILBM header

		If ReadTag( stream )<>"ILBM" Return

		Local	cmap%					= 0
		Local	w:Short					= 0
		Local	h:Short					= 0
		Local	x%						= 0
		Local	y%						= 0
		Local	nPlanes:Byte			= 0
		Local	masking:Byte			= 0
		Local	compression:Byte		= 0
		Local	transparentColour:Short	= 0
		Local	xAspect:Byte			= 0
		Local	yAspect:Byte			= 0
		Local	pageWidth%				= 0
		Local	pageHeight%				= 0
		Local	plane%					= 0
		Local	scanline%				= 1
		Local	i%						= 0
		Local	n%						= 0
		Local	nn%						= 0
		Local	pixmap:TPixmap			= Null
		Local	buffer2:Byte[]
		
		While Not stream.Eof()
			Local blahblah$=ReadTag$(stream)
			Select	blahblah$'Readtag$(stream)
				Case	"BMHD"	' BitMapHeader
					dud					= stream.ReadInt()				' length of BitMapHeader chunk
					w					= stream.ReadShort()
					h					= stream.ReadShort()			' raster width & height in pixels
					x					= UStoSI(stream.ReadShort())
					y					= UStoSI(stream.ReadShort())	' pixel position for this image
					nPlanes				= stream.ReadByte()				' # source bitplanes
					masking				= stream.ReadByte()				' Choice of masking technique.
					compression			= stream.ReadByte()				' Choice of compression algorithm applied to the rows of all source and mask planes. 
					Local	dud2%		= stream.ReadByte()				' unused; for consistency, put 0 here
					transparentColour	= stream.ReadShort()			' transparent "colour number" (sort of)
					xAspect				= stream.ReadByte()
					yAspect				= stream.ReadByte()				' pixel aspect, a ratio width : height
					pageWidth			= UStoSI(stream.ReadShort())
					pageHeight			= UStoSI(stream.ReadShort())	' source "page" size in pixels
					stream.SkipBytes(dud-20)							' extended chunk compatibility
				Case	"CMAP"	' ColorMap
							dud			= stream.ReadInt()				' length of ColourMap chunk
					If Not ColourMapRed
						cmap		= dud/3
						ColourMapRed	= ColourMapRed[..cmap+1]
						ColourMapGreen	= ColourMapGreen[..cmap+1]
						ColourMapBlue	= ColourMapBlue[..cmap+1]
						For i=1 To cmap
							ColourMapRed[i]		= stream.ReadByte()
							ColourMapGreen[i]	= stream.ReadByte()
							ColourMapBlue[i]	= stream.ReadByte()
						Next	' i=1 To cmap
						If dud & 1 Then stream.SkipBytes(1)				' padded chunk compatibility
					Else
						stream.SkipBytes(dud)							' skip chunk
						If dud & 1 Then stream.SkipBytes(1)				' padded chunk compatibility
					EndIf	' ColourMap=Null
				Case	"BODY"	' 
							dud				= stream.ReadInt()			' length of BODY chunk
					Local	row[]
					Local	buffer%[17]
							pixmap			= TPixmap.Create(w,h,PF_RGB888)
					If 2^nPlanes>cmap
						ColourMapRed=ColourMapRed[..(2^nPlanes)+1]
						ColourMapGreen=ColourMapGreen[..(2^nPlanes)+1]
						ColourMapBlue=ColourMapBlue[..(2^nPlanes)+1]
					EndIf	' 2^nPlanes>cmap
					For Local column%=1 To h
						row			= New Int[w+17]
						Select	compression
							Case	cmpNone
								For plane=1 To nPlanes
									For scanline=1 To w Step 16
										buffer[1]	= stream.ReadShort()
										For dud=2 To 16
											buffer[dud]				= (buffer[1] Shr (dud-1)) & 1
										Next	' dud=2 To 16
										buffer[1]	= buffer[1] & 1
										For dud=1 To 16
											row[scanline+(16-dud)]	= row[scanline+(16-dud)]+(buffer[dud] Shl (plane-1))
										Next	' dud=1 To 16
									Next	' scanline=1 To w Step 8
								Next	' plane=1 To nPlanes
							Case	cmpByteRun1
								Local	dud2	= 1
										nn		= 1
								Local	ii
								If Not buffer2
									buffer2	= New Byte[w*h*(nPlanes+1)]
									Repeat
										n=stream.ReadByte();dud2:+1
										If n>127 Then n:-256	' unsigned to signed conversion
										If n<>-128	' noop
											If n<0	' replicate the next byte -n+1 times
												n	= Abs(n)+1
												ii	= stream.ReadByte();dud2:+1
												For i=1 To n
													buffer2[nn]=ii;nn:+1
												Next	' i=1 To n
											Else	' copy the next n+1 bytes literally
												For i=0 To n
													buffer2[nn]=stream.ReadByte();dud2:+1;nn:+1
												Next	' i=0 To n
											EndIf	' n<0
										EndIf	' n<>-128
									Until dud2>dud
								EndIf	' Not buffer2
								For plane=1 To nPlanes
									For i=1 To w Step 16
										buffer[1]	= buffer2[scanline+1]+(buffer2[scanline] Shl 8);scanline:+2
										For dud=2 To 16
											buffer[dud]				= (buffer[1] Shr (dud-1)) & 1
										Next	' dud=2 To 16
										buffer[1]	= buffer[1] & 1
										For dud=1 To 16
											row[i+(16-dud)]	= row[i+(16-dud)]+(buffer[dud] Shl (plane-1))
										Next	' dud=1 To 16
									Next	' i=1 To w Step 16
								Next	' plane=1 To nPlanes
							Default	' unsupported compression algorithm
								dud	= 0
								Exit
						End Select	' compression
						If dud=0 Then pixmap			= Null;Exit
						For dud=1 To w
							pixmap.WritePixel dud-1,column-1,(ColourMapRed[row[dud]+1] Shl 16)+(ColourMapGreen[row[dud]+1] Shl 8)+ColourMapBlue[row[dud]+1]
						Next	' row=1 To w
					Next	' column=1 To h
					If cmap Then ColourMapRed	= Null
					Exit
				Default			' unsupported chunks
					stream.SkipBytes(stream.ReadInt())									' skip chunk
			End Select	' ReadTag$(stream)
			
			If pixmap Then Exit
		Wend	' Not stream.EOF()
		
		Return pixmap
	End Method

End Type

New TPixmapLoaderILBM
