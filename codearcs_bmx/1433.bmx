; ID: 1433
; Author: gman
; Date: 2005-08-03 22:54:08
; Title: Pub.PCXLoader
; Description: Pixmap loader for PCX files

Strict

Rem
bbdoc: PCX loader.  Only supports 8bit 256 color palette PCX images.
End Rem
Module Pub.PCXLoader

ModuleInfo "Version: 1.00"
ModuleInfo "Author: gman"
ModuleInfo "License: freebie"

Import BRL.Pixmap
Import BRL.Bank

Type TPixmapLoaderPCX Extends TPixmapLoader

	Method LoadPixmap:TPixmap( file:TStream )
		Local retval:TPixmap=Null
		
		Local PCXData:Byte[]
		Local PaletteData:Int[]

		Local header:SPCXHeader=New SPCXHeader
		header.fillFromReader(file,SPCXHeader.size,0,0)

		' Return If the header is wrong
		If (header.Manufacturer <> $0a And header.Encoding <> $01) Then Return Null

		' Return If this isn't a supported type
		If ((header.BitsPerPixel < 8) Or (header.BitsPerPixel > 24))
			DebugLog("Unsupported bits per pixel in PCX file ("+header.BitsPerPixel+").")
			Return Null
		EndIf

		Local pos:Int = StreamPos(file)
		Local palIndicator:Byte
				
		' check the PAL indicator
		SeekStream(file,StreamSize(file)-769)

		palIndicator=ReadByte(file)

		If ( palIndicator <> 12 )
			DebugLog("Unsupported pal indicator in PCX file ("+palIndicator+").")
			Return Null
		EndIf
					
		' read palette
		PaletteData = PaletteData[..256]
		Local tempPalette:Byte Ptr=ReadString(file,768).ToCString()
		
		' convert the red,green,blue of the palette into colors				
		For Local i:Int=0 To 255
			PaletteData[i]=($ff000000 | ..
				tempPalette[i*3+0] Shl 16 | ..
				tempPalette[i*3+1] Shl 8 | ..
				tempPalette[i*3+2])								
		Next
		tempPalette=Null

		SeekStream(file,pos)

		Local width:Int, height:Int
		width = header.XMax - header.XMin + 1
		height = header.YMax - header.YMin + 1
				
		' read in the image data
		Local offset:Long,imagebytes:Long
		Local cnt:Int,char:Byte
				
		imagebytes = header.BytesPerLine * header.Planes * (1 + header.Ymax - header.Ymin)
		PCXData=PCXData[..imagebytes]
		Local bufr:Byte Ptr=Varptr(PCXData[0])
	
		Local written:Int		
		For offset = 0 To imagebytes-1    ' /* increment by cnt below */

			' read in the next char and see if we have read past the end of file
          	If Not encget(char,cnt,file) Then Exit

			' store the data, repeat the char cnt times if needed
               For Local i:Int = 0 To cnt-1
				bufr[0]=char
               	bufr:+1
			Next

			written:+ cnt

			' check to see if we have written enough
			If written>=imagebytes Then Exit
		Next

		' create the pixmap									
		Local tmpptr:Byte Ptr=Varptr(PCXData[0])
		retval=CreatePixmap(width,height,PF_RGB888)				
		
		For Local y:Int=0 To height-1				
			For Local x:Int=0 To width-1
				WritePixel(retval,x,y,PaletteData[tmpptr[0]])
				tmpptr:+1
			Next
		Next				
		
		header=Null
		bufr=Null
		tmpptr=Null
		PaletteData=Null
		PCXData=Null

		Return retval
	End Method
End Type

New TPixmapLoaderPCX

Private 

Rem
/* This procedure reads one encoded block from the image file And stores a
count And data byte.

Return result:  1 = valid data stored, Null = out of data in file */
EndRem
Function encget:Int(pbyt:Byte Var,pcnt:Int Var,fid:TStream)
	Local i:Byte
     pcnt = 1        '/* assume a "run" length of one */

	If Eof(fid) Then Return Null Else i=ReadByte(fid)

	If ($C0 = ($C0 & i))
		pcnt = $3F & i
		If Eof(fid) Then Return Null Else i=ReadByte(fid)
	EndIf

	pbyt = i
     Return 1
EndFunction

Type SPCXHeader Extends PACK_STRUCT
	Global size:Int=128

	Field Manufacturer:Byte
	Field Version:Byte
	Field Encoding:Byte
	Field BitsPerPixel:Byte
	Field XMin:Short
	Field YMin:Short
	Field XMax:Short
	Field YMax:Short
	Field HorizDPI:Short
	Field VertDPI:Short
	Field Palette:Byte[48]
	Field Reserved:Byte
	Field Planes:Byte
	Field BytesPerLine:Short
	Field PaletteType:Short
	Field HScrSize:Short
	Field VScrSize:Short
	Field Filler:Byte[54]

	Method fillFromBank(bank:TBank,start:Int=0)
		Local i:Int
		
		Manufacturer=PeekByte(bank,start)
		Version=PeekByte(bank,start+1)
		Encoding=PeekByte(bank,start+2)
		BitsPerPixel=PeekByte(bank,start+3)
		XMin=PeekShort(bank,start+4)
		YMin=PeekShort(bank,start+6)
		XMax=PeekShort(bank,start+8)
		YMax=PeekShort(bank,start+10)
		HorizDPI=PeekShort(bank,start+12)
		VertDPI=PeekShort(bank,start+14)
		
		For i=0 To 47
			Palette[i]=PeekByte(bank,start+16+i)
		Next
		
		Reserved=PeekByte(bank,start+64)
		Planes=PeekByte(bank,start+65)
		BytesPerLine=PeekShort(bank,start+66)
		PaletteType=PeekShort(bank,start+68)
		HScrSize=PeekShort(bank,start+70)
		VScrSize=PeekShort(bank,start+72)
		For i=0 To 53
			Filler[i]=PeekByte(bank,start+74+i)
		Next		
	EndMethod
	
EndType

' generic type for reading in PACK_STRUCT structures from files
Type PACK_STRUCT
	Global size:Int=0
			
	Method fillFromBank(bank:TBank,start:Int)
	EndMethod

	' returns true if successful
	Method fillFromReader:Int(fileToRead:TStream,tbsize:Int,readeroffset:Int=0,bankoffset:Int=0)
		If Not fileToRead Then Return False
		
		' create the bank
		Local structbank:TBank=CreateBank(tbsize)
		
		' read from the file
		structbank.Read(fileToRead,readeroffset,tbsize)
		
		' populate the STRUCT with what was read
		fillFromBank(structbank,bankoffset)
		
		' clear out the bank
		structbank=Null
		
		Return True
	EndMethod
	
	Method getBank:TBank()
		Return Null
	EndMethod	
EndType
