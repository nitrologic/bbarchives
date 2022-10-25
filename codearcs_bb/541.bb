; ID: 541
; Author: Marcelo
; Date: 2003-01-07 14:35:40
; Title: ReadImageInfo()
; Description: Retrieve width, height, depth and alpha from a image file without loading it.

; Image info
Type TImageInfo
	Field Width
	Field Height
	Field Depth
	
	Field HasAlpha
End Type

Function ReadImageInfo.TImageInfo(imgfile$)
	imgfile$ = Trim(Lower(imgfile$))
	
	file = ReadFile(imgfile)
	If Not file Then Return Null
	
	Width = 0 : Height = 0 : Depth = 0 : HasAlpha = False
	
	If Instr(imgfile, ".png")

		; Check signature
		Sig$ = Chr(137) + "PNG" + Chr(13) + Chr(10) + Chr(26) + Chr(10)
		
		If ReadByteString(file, Len(Sig)) <> Sig
			CloseFile(file)
			Return Null
		EndIf
		
		; Check for valid IHDR chunk
		Size = Int_SwapEndian(ReadInt(file))
		Chunk$ = ReadByteString(file, 4)
		If Chunk <> "IHDR" Or Size <> 13
			CloseFile(file)
			Return Null
		EndIf
		
		; Read IHDR chunk
		Width = Int_SwapEndian(ReadInt(file))
		Height = Int_SwapEndian(ReadInt(file))
		Depth = ReadByte(file)
		
		Col = ReadByte(file)
		
		Select Col
			Case 2
				Depth = Depth * 3 ; RGB  (red + green + blue)
				
			Case 4
				Depth = Depth * 2 ; GA   (gray + alpha)
				HasAlpha = True
				
			Case 5
				Depth = Depth * 4 ; ARGB (Alpha + RGB)
				HasAlpha = True
		End Select
		
	Else If Instr(imgfile, ".bmp")
	
		; Check for bmp file
		If ReadByteString(file, 2) <> "BM"
			CloseFile(file)
			Return Null
		EndIf
		
		; Some non-used stuff
		ReadInt(file) ; File size
		ReadShort(file) ; reserved1
		ReadShort(file) ; reserved2
		ReadInt(file)	; data offset from this point

		Size = ReadInt(file) ; BMPINFO chunk size
		If Size <> 40
			CloseFile(file)
			Return Null
		EndIf
		
		Width = ReadInt(file)
		Height = ReadInt(file)
		
		ReadShort(file) ; planes
		Depth = ReadInt(file)

	Else If Instr(imgfile, ".jpg")
	
		; SOI + APP0
		Sig$ = Chr($FF) + Chr($D8) + Chr($FF) + Chr($E0)
		
		If ReadByteString(file, Len(Sig)) <> Sig
			CloseFile(file)
			Return Null
		EndIf
		
		Size = Short_SwapEndian(ReadShort(file)) - 2
		Pos = FilePos(file)
		
		Ident$ = ReadByteString(file)
		Version = Short_SwapEndian(ReadShort(file))
		
		; Check for JFIF version 1.2
		If (Ident <> "JFIF") Or ( (((Version And $FF00) Shr 8) <> 1) And ((Version And $FF) <> 2) )
			CloseFile(file)
			Return Null
		EndIf
		
		; Search for markers
		SeekFile(file, Pos + Size)

		While Not Eof(file)
			If ReadByte(file) = $FF
				BType = ReadByte(file)
				Size = Short_SwapEndian(ReadShort(file)) - 2
				Pos = FilePos(file)
				
				; if the type is from SOF0 to SOF3
				If (BType >= $C0) And (BType <= $C3)
					Prec = ReadByte(file)
					
					Height = Short_SwapEndian(ReadShort(file))
					Width = Short_SwapEndian(ReadShort(file))
					
					Exit
				EndIf
				
				; Goto next marker
				SeekFile(file, Pos + Size)
			EndIf		
		Wend
		
		Depth = 24	
	EndIf

	CloseFile(file)
	
	Info.TImageInfo = New TImageInfo
	Info\Width = Width
	Info\Height = Height
	Info\Depth = Depth
	Info\HasAlpha = HasAlpha
	
	Return Info
End Function



; Utils
Function Int_SwapEndian%(n%)
	Return ((n And $FF) Shl 24) Or ((n And $FF00) Shl 8) Or ((n And $FF0000) Shr 8) Or ((n And $FF000000) Shr 24)
End Function

Function Short_SwapEndian%(n%)
	Return ((n And $FF) Shl 8) Or ((n And $FF00) Shr 8)
End Function

Function ReadByteString$(file, count = 0)
	Ret$ = ""
	
	If count = 0
		Char = ReadByte(file)
		While Char <> 0
			Ret = Ret + Chr(Char)
			Char = ReadByte(file)
		Wend
	Else
		For i = 1 To count
			Ret = Ret + Chr(ReadByte(file))
		Next
	EndIf
	
	Return Ret
End Function
