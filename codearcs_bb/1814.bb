; ID: 1814
; Author: Andres
; Date: 2006-09-14 08:47:48
; Title: TGA with alpha
; Description: Load and draw TGA images with alpha

Const TGAHeaderSize% = 6, TGAAlphaChannel% = True

Function LoadTGA(path$)
	Local bank%, offset% = 0
	Local file% = ReadFile(path$)
	
	If file%
		IDLenght% = ReadByte(file%)
		ColorMapType% = ReadByte(file%)
		ImageType% = ReadByte(file%)
		ColorMapIndex% = ReadShort(file%)
		ColorMapEntries% = ReadShort(file%)
		ColorMapSize% = ReadByte(file%)
		Xhandle% = ReadShort(file%)
		YHandle% = ReadShort(file%)
		Width% = ReadShort(file%)
		Height% = ReadShort(file%)
		BPP% = ReadByte(file%)
		Attributes% = ReadByte(file%)
		
		ImageID$ = ""
		For i = 1 To IDLenght
			ImageID$ = ImageID$ + Chr(ReadByte(RF))
		Next
		
		bank% = CreateBank(TGAHeaderSize% + Width% * Height% * 8)
		image% = CreateImage(Width%, Height%)
		MaskImage image%, 255, 0, 255
		
		; HEADER
		PokeShort bank%, 0, Width% * 4
		PokeInt bank%, 2, image%
		
		LockBuffer ImageBuffer(image%)

		For y = 0 To height% - 1
			For x = 0 To width% - 1
				Select BPP%
					Case 24
						b% = ReadByte(file%)
						g% = ReadByte(file%)
						r% = ReadByte(file%)
						a% = 255
					Case 32
						b% = ReadByte(file%)
						g% = ReadByte(file%)
						r% = ReadByte(file%)
						a% = ReadByte(file%)
				End Select
				If TGAAlphaChannel%
					Select a%
						Case 0
							WritePixelFast x, height% - y - 1, (255 Or (0 Shl 8) Or (255 Shl 16) Or ($FF000000)), ImageBuffer(image%)
						Case 255
							WritePixelFast x, height% - y - 1, (b% Or (g% Shl 8) Or (r% Shl 16) Or ($FF000000)), ImageBuffer(image%)
						Default
							WritePixelFast x, height% - y - 1, (255 Or (0 Shl 8) Or (255 Shl 16) Or ($FF000000)), ImageBuffer(image%)
							PokeShort bank%, TGAHeaderSize% + offset + 0, x%
							PokeShort bank%, TGAHeaderSize% + offset + 2, height% - y% - 1
							
							PokeByte bank%, TGAHeaderSize% + offset + 4, r%
							PokeByte bank%, TGAHeaderSize% + offset + 5, g%
							PokeByte bank%, TGAHeaderSize% + offset + 6, b%
							PokeByte bank%, TGAHeaderSize% + offset + 7, a%
							
							offset = offset + 8
					End Select
				Else
					Select a%
						Case 0
							WritePixelFast x, height% - y, (255 Or (0 Shl 8) Or (255 Shl 16) Or ($FF000000)), ImageBuffer(image%)
						Default
							WritePixelFast x, height% - y, (b% Or (g% Shl 8) Or (r% Shl 16) Or ($FF000000)), ImageBuffer(image%)
					End Select
				EndIf
			Next
		Next
		
		ResizeBank bank%, TGAHeaderSize% + offset
		UnlockBuffer ImageBuffer(image%)
		CloseFile file%
		
		Return bank%
	EndIf
End Function

Function DrawTGA(screen%, bx%, by%, frames% = 1, frame% = 0)
	Local width% = TGAWidth(screen%), height% = TGAHeight(screen%)
	Local GWidth% = GraphicsWidth(), GHeight% = GraphicsHeight()
	
	Local FrameWidth% = width% / frames%
	Local StartX% = frame% * FrameWidth%
	
	DrawImageRect PeekInt(screen%, 2), bx%, by%, StartX%, 0, FrameWidth%, height%
	
	If Not TGAAlphaChannel% Then Return
	
	Local bckgrnd% = LockedPixels(), bckgrndw% = LockedPitch() / 4
	
	For i = 0 To BankSize(screen%) - (1 + TGAHeaderSize%) Step 8
		x% = PeekShort(screen%, TGAHeaderSize% + i + 0)
		y% = PeekShort(screen%, TGAHeaderSize% + i + 2)
		
		If x% + bx% - StartX% => 0 And x% + bx% - StartX% < GWidth% And y% + by% => 0 And y% + by% < GHeight%
			If x% => StartX And x% < StartX% + FrameWidth%
				r% = PeekByte(screen%, TGAHeaderSize% + i + 4)
				g% = PeekByte(screen%, TGAHeaderSize% + i + 5)
				b% = PeekByte(screen%, TGAHeaderSize% + i + 6)
				a# = Float PeekByte(screen%, TGAHeaderSize% + i + 7) / 255.0
				
				br% = PeekByte(bckgrnd%, ((by% + y%) * bckgrndw% + (bx% + x% - StartX%)) * 4 + 2)
				bg% = PeekByte(bckgrnd%, ((by% + y%) * bckgrndw% + (bx% + x% - StartX%)) * 4 + 1)
				bb% = PeekByte(bckgrnd%, ((by% + y%) * bckgrndw% + (bx% + x% - StartX%)) * 4 + 0)
				
				r% = Float br% + (r% - br%) * a#
				g% = Float bg% + (g% - bg%) * a#
				b% = Float bb% + (b% - bb%) * a#
				
				PokeInt bckgrnd%, ((by% + y%) * bckgrndw% + (bx% + x% - StartX%)) * 4 + 0, (b% Or (g% Shl 8) Or (r% Shl 16) Or ($FF000000))
			EndIf
		EndIf
	Next
End Function

Function TGAWidth%(screen%)
	Return PeekShort(screen%, 0) / 4
End Function

Function TGAHeight%(screen%)
	Return ImageHeight(PeekInt(screen%, 2))
End Function

Function FreeTGA(screen%)
	If PeekInt(screen%, 2) Then FreeImage PeekInt(screen%, 2)
	FreeBank screen%
End Function
