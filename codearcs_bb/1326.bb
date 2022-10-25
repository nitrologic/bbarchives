; ID: 1326
; Author: Massimo32
; Date: 2005-03-14 17:17:27
; Title: Edit palette in png images
; Description: How to change colors in a paletted png image without loading image in memory

Dim crc_table(255)
crc_init()

image$ = Input("Image (.png): ")
If Lower(Right(image,4)) <> ".png" Then image = image + ".png"

palette$ = Input("Palette (.pal): ")
If Lower(Right(palette,4)) <> ".pal" Then palette = palette + ".pal"

file_image = OpenFile(image)
file_palette = OpenFile(palette)

If file_image = 0 Then RuntimeError(image + " not found!")
If file_palette = 0 Then RuntimeError(palette + " not found!")


;--- READ PALETTE INSIDE THE IMAGE ---

;search for the palette 'chunk'

i0 = ReadByte(file_image)
i1 = ReadByte(file_image)
i2 = ReadByte(file_image)
i3 = ReadByte(file_image)

palette_found=False

While Not Eof 

	If Chr(i0)+Chr(i1)+Chr(i2)+Chr(i3) = "PLTE"
		palette_found = True
		Exit
	EndIf

	i0 = i1
	i1 = i2
	i2 = i3
	i3 = ReadByte(file_image)

Wend

If Not palette_found
	RuntimeError("Palette not found inside image!")
EndIf

palette_offset = FilePos(file_image) - 8

;read data size
SeekFile(file_image, palette_offset)
data_size = 0
For i = 0 To 3
	data_size = 256*data_size + ReadByte(file_image) 
Next

;create and fill buffer
buf = CreateBank(4 + data_size)
For i = 0 To BankSize(buf)-1
	PokeInt(buf, i, ReadByte(file_image))
Next


;--- READ NEW PALETTE AND CHANGE BUFFER ---


;check palette heading
row$ = ReadLine(file_palette)
If Upper(row) <> "JASC-PAL" Then RuntimeError("Palette type not recognized (should be JASC-PAL): " + row)

;check color depth
row$ = ReadLine(file_palette)
If row <> "0100" Then RuntimeError("New palette should be 8 bit/channel!")

;check size of palettes
row$ = ReadLine(file_palette)
palette_size = Int(row)
If 3*palette_size <> data_size Then RuntimeError("Size of new palette: " + 6*Int(row) + " Size of image palette: " + data_size)

For i = 0 To palette_size-1
	
	row$ = ReadLine(file_palette)

	;red
	green_offset = Instr(row, " ")
	red$ = (Mid(row,1,green_offset-1))
	
	;green
	blue_offset = Instr(row, " ", green_offset+1)
	green$ = (Mid(row,green_offset+1,blue_offset-green_offset-1))
	
	;blue
	blue$ = (Mid(row, blue_offset+1, Len(row)-blue_offset))

	PokeByte(buf, 4 +3*i, Int(red))
	PokeByte(buf, 4 +3*i +1, Int(green))
	PokeByte(buf, 4 +3*i +2, Int(blue))

Next


;calculate crc		
crc32% = crc_bank(buf)

Print (" ")
Print ("New crc: " + Hex(crc32))

;write new file
file_image_out = WriteFile("tmp_"+image)

SeekFile(file_image, 0)

While Not Eof(file_image)

	pos% = FilePos(file_image)
	If pos < palette_offset + 4 
		WriteByte(file_image_out, ReadByte(file_image))
	Else If pos < (palette_offset +4 +4 +data_size)
		WriteByte(file_image_out, PeekByte(buf, pos -(palette_offset +4)))
		SeekFile(file_image, FilePos(file_image)+1)
	Else If pos < palette_offset +4 +4 +data_size +4
		shift = palette_offset +4 +4 +data_size +4 -pos -1
;		Print Hex(crc32 Shr 8*shift)
		WriteByte(file_image_out, (crc32 Shr 8*shift) And $FF)
		SeekFile(file_image, FilePos(file_image)+1)
	Else
		WriteByte(file_image_out, ReadByte(file_image))
	EndIf

Wend

CloseFile(file_image_out)
CloseFile(file_image)	

Print (" ")
Print("New image saved as: tmp_"+image)	




;_____________________________________


Function crc_init()
  Local i
  Local j
  Local value

  For i=0 To 255
    value=i
    For j=0 To 7
      If (value And $1) Then 
        value=(value Shr 1) Xor $EDB88320
      Else
        value=(value Shr 1)
      EndIf
    Next
    crc_table(i)=value
  Next
End Function



Function crc_bank(bank)
  Local byte
  Local crc
  Local i
  Local size

  crc=$FFFFFFFF
  size=BankSize(bank)-1
  For i=0 To size
    byte=PeekByte(bank,i)
    crc=(crc Shr 8) Xor crc_table(byte Xor (crc And $FF))
  Next
  Return ~crc
End Function
