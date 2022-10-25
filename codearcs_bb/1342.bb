; ID: 1342
; Author: Snarkbait
; Date: 2005-04-04 01:18:58
; Title: PCX loader for B+
; Description: Load 8-bit palletted PCX images into Blitz Plus

;loadpcxfile.bb include
;blitz plus only, not needed for b3d
; only for 8-bit color pcx images

Dim pallette256(255,2)

Function loadPCXimage(image$)
	pcxfile = ReadFile(image$)
	If Not pcxfile RuntimeError "file not found"
	size = FileSize(image$)
	ident = ReadByte(pcxfile) ;should be 10
	version = ReadByte(pcxfile);should be 5
	encoding = ReadByte(pcxfile) ; should be 1
	bits_per_pixel = ReadByte(pcxfile)
	xmin = ReadShort(pcxfile)
	ymin = ReadShort(pcxfile)
	xmax = ReadShort(pcxfile)
	ymax = ReadShort(pcxfile)
	xsize = xmax - xmin + 1
	ysize = ymax - ymin + 1
	SeekFile(pcxfile,65)
	Nplanes = ReadByte(pcxfile)
	bytes_per_line = ReadShort(pcxfile)
	totalbytes = Nplanes * bytes_per_line
	; go to pallette header
	SeekFile(pcxfile,size - 769)
	a = ReadByte(pcxfile)
	If a = 12
		;read pallette
		For color_value = 0 To 255
			For RGB = 0 To 2
				readval = ReadByte(pcxfile)
				pallette256(color_value,RGB) = readval ;Shr 2
				;DebugLog pallette256(color_value,RGB)
			Next
		Next 
	EndIf
	SeekFile(pcxfile,128)
	newimage = CreateImage(xsize,ysize)
	SetBuffer ImageBuffer(newimage)
	LockBuffer
	xcount = 1:ycount = 1
	While Not Eof(pcxfile)
		readval = ReadByte(pcxfile)
		If (readval And $C0) = $C0
			pcnt = readval And $3F
			readval = ReadByte(pcxfile)
			For a = 1 To pcnt
				WritePixelFast xcount-1,ycount-1,argb(pallette256(readval,0),pallette256(readval,1),pallette256(readval,2))
				xcount = xcount + 1
				If xcount > totalbytes xcount = 1:ycount = ycount + 1
				If ycount > ysize Exit 
			Next
		Else
			WritePixelFast xcount-1,ycount-1,argb(pallette256(readval,0),pallette256(readval,1),pallette256(readval,2))
		 	xcount = xcount + 1
			If xcount > totalbytes xcount = 1:ycount = ycount + 1
			If ycount > ysize Exit
		EndIf
	Wend 
	UnlockBuffer 
	CloseFile pcxfile
	Return newimage
End Function

Function argb(red,green,blue)
	Return (blue Or (green Shl 8) Or (red Shl 16) Or (255 Shl 24))
End Function
