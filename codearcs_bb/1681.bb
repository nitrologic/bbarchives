; ID: 1681
; Author: Andres
; Date: 2006-04-20 14:15:47
; Title: TBM Images
; Description: Transparent bitmap functions (uses banks)

Function CreateTBMImage(img$, map$, file$, flag=1) ; flags: 1 - normal, 2 - scaled image, 3 - scaled map
	Local x, y
	Local temp_image = LoadImage(img$)
	Local temp_map = LoadImage(map$)
	
	If temp_image And temp_map
		Select flag
			Case 2
				ScaleImage temp_image, ImageWidth(temp_map), ImageHeight(temp_map)
			Case 2
				ScaleImage temp_map, ImageWidth(temp_image), ImageHeight(temp_image)
		End Select
		
		wf = WriteFile(file$)
		If wf
			WriteInt wf, ImageWidth(temp_image) ; Width
			WriteInt wf, ImageHeight(temp_image) ; Height
			
			LockBuffer ImageBuffer(temp_image)
			LockBuffer ImageBuffer(temp_map)
			For y = 0 To ImageHeight(temp_image) - 1
				For x = 0 To ImageWidth(temp_image) - 1
					argb = ReadPixel(x, y, ImageBuffer(temp_image))
						red = (argb Shr 16) And $FF
						green = (argb Shr 8) And $FF
						blue = argb And $FF
					argb = ReadPixel(x, y, ImageBuffer(temp_map))
						red2 = (argb Shr 16) And $FF
						green2 = (argb Shr 8) And $FF
						blue2 = argb And $FF
					
					WriteByte wf, red
					WriteByte wf, green
					WriteByte wf, blue
					WriteByte wf, (red2 + green2 + blue2) / 3
				Next
			Next
			UnlockBuffer ImageBuffer(temp_map)
			UnlockBuffer ImageBuffer(temp_image)
			
			FreeImage temp_map
			FreeImage temp_image
		Else
			Notify "Unable to write the file!"
		EndIf
	Else
		Notify "Unable to open one of the image files!"
	EndIf
End Function

Function LoadTBMImage(file$)
	Local rf = ReadFile(file$)
	Local Width, Height, Bank, argb
	If rf
		Width = ReadInt(rf)
		Height = ReadInt(rf)
		Bank = CreateBank(8 + Width * Height * 4)
		PokeInt Bank, 0, Width
		PokeInt Bank, 4, Height
		i = 8
		For i = 8 To 8 + (Width - 1) * Height * 4
			PokeByte Bank, 8 + i, ReadByte(rf)
		Next
		
		CloseFile rf
		Return Bank
	Else
		Notify "Unable to read the file!"
	EndIf
End Function

Function DrawTBMImage(Handler%, x%, y%, strength#=1.0, frame=0, frames= 1) ; Draw the TBM image on the screen
	If Strength > 0
		Local Width = PeekInt(Handler%, 0)
		Local Height = PeekInt(Handler%, 4)
		Local red, green, blue, r, g, b, modifier, xx, yy, argb
		
		Local FrameWidth = (Width / Frames)
		Local offset = (frame * FrameWidth)
		Local Size = BankSize(handler%)
		ReadPixelFast(-1, -1)
		LockBuffer GraphicsBuffer()
		For yy = 0 To Height - 1
			For xx = 0 To FrameWidth - 1
				If Size < 8 + ((yy * Width) + (xx + offset) + 2) * 4 + 3 Then Exit
				
				red = PeekByte(Handler%, 8 + ((yy * Width) + (xx + offset) + 2) * 4 + 0)
				green = PeekByte(Handler%, 8 + ((yy * Width) + (xx + offset) + 2) * 4 + 1)
				blue = PeekByte(Handler%, 8 + ((yy * Width) + (xx + offset) + 2) * 4 + 2)
				modifier = PeekByte(Handler%, 8 + ((yy * Width) + (xx + offset) + 2) * 4 + 3)
				
				If modifier > 0
					If strength < 1.0 Or modifier < 255
						argb = ReadPixelFast(x + xx, y + yy, GraphicsBuffer())
							r = (argb Shr 16) And $FF
							g = (argb Shr 8) And $FF
							b = argb And $FF
						red = r + (red - r) * (Float modifier / 255) * strength#
						green = g + (green - g) * (Float modifier / 255) * strength#
						blue = b + (blue - b) * (Float modifier / 255) * strength#
					EndIf
					
					WritePixel x + xx, y + yy, (blue Or (green Shl 8) Or (red Shl 16) Or ($FF000000)), GraphicsBuffer()
				EndIf
			Next
		Next
		UnlockBuffer GraphicsBuffer()
	EndIf
End Function

Function TBMImageWidth%(Handler%) ; Width of the TBM image
	Return PeekInt(Handler%, 0)
End Function

Function TBMImageHeight%(Handler%) ; Height of the TBM image
	Return PeekInt(Handler%, 4)
End Function
