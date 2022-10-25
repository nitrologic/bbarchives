; ID: 862
; Author: skn3
; Date: 2003-12-22 03:32:15
; Title: Fast color replace function (b+ only)
; Description: Replaces all instace of a color, with an alternative color

;
; ************************************************************
; * Project Name : Fast Replace color
; * Author(s)    : Jonathan Pittock
; * Website      : www.acsv.net
; ************************************************************

;#Region Help
	;ReplaceColor(buffer,width,height,old_r,old_g,old_b,new_r,new_g,new_b)
	
	;Replaces all instances of old color, with new color, in a valid buffer.
	
	;Parameters
	;buffer - A valid blitz graphic buffer
	;width  - width of graphics buffer
	;height - height of graphics buffer
	;old_r  - red color component to be replaced
	;old_g  - green color component to be replaced
	;old_b  - blue color component to be replaced
	;new_r  - red color component that will replace old red component
	;new_r  - green color component that will replace old green component
	;new_r  - blue color component that will replace old blue component
;#End Region

;#Region Example code
	Graphics 640,400,32,2
	image = CreateImage(50,50,1,2)
	SetBuffer ImageBuffer(image)
	Color 255,0,0
	Rect 0,0,50,50,1
	SetBuffer BackBuffer()
	Cls
	DrawImage image,50,50
	Color 255,255,255
	Text 5,5,"press any key to replace colors in image"
	Flip
	WaitKey()
	starttime = MilliSecs()
	ReplaceColor(ImageBuffer(image),ImageWidth(image),ImageHeight(image), 255,0,0, 0,255,0)
	endtime = MilliSecs()
	Cls
	DrawImage image,50,50
	Color 255,255,255
	Text 5,5,"Done!, press any key to perform a stress test"
	Text 5,25,"Replace color took "+(endtime-starttime)+" ms"
	Flip
	WaitKey()
	starttime = MilliSecs()
	lastr = 0
	lastg = 255
	lastb = 0
	
	buffer       = ImageBuffer(image)
	bufferwidth  = ImageWidth(image)
	bufferheight = ImageHeight(image)
	
	For i=0 To 100
		r = Rand(0,255)
		g = Rand(0,255)
		b = Rand(0,255)
		ReplaceColor(buffer,bufferwidth,bufferheight, lastr,lastg,lastb, r,g,b)
		lastr = r
		lastg = g
		lastb = b
	Next
	endtime = MilliSecs()
	Cls
	DrawImage image,50,50
	Color 255,255,255
	Text 5,5,"Done!, press any key to end"
	Text 5,25,"Stress test of 100 ReplaceColor function calls took "+(endtime-starttime)+" ms"
	Flip
	WaitKey()
	FreeImage image
	
	End
;#End Region

;#Region ReplaceColor code
Function ReplaceColor(buffer,bufferwidth,bufferheight,oldr,oldg,oldb,newr,newg,newb)
	oldbuffer = GraphicsBuffer()
	SetBuffer buffer
	LockBuffer buffer
	bufferbank   = LockedPixels()
	bufferpitch  = LockedPitch()
	bufferformat = LockedFormat()
	
	Select bufferformat
		Case 1
			newrgb = (newr/8 Shl 11) Or (newg/4 Shl 5) Or (newb Shr 3)
			For y = 0 To bufferheight-1
				rowoffset  = y * bufferpitch
				For x = 0 To bufferwidth-1
					offset = rowoffset + (x Shl 1)
					rgb    = PeekShort(bufferbank,offset)
					r      = ((rgb And $F800) Shr 11) Shl 3 
					g      = ((rgb And $7E0) Shr 5) Shl 2 
					b      = (rgb And $1F) Shl 3
					
					If r = oldr And g = oldg And b = oldb
						;replace color
						rgb = newrgb
						;insert back into bank
						PokeShort bufferbank,offset,rgb
						PokeShort bufferbank,offset+2,rgb
					End If
				Next
			Next
		Case 2
			newrgb = (newr/8 Shl 10) Or (newg/8 Shl 5) Or (newb Shr 3)
			For y = 0 To bufferheight-1
				rowoffset  = y * bufferpitch
				For x = 0 To bufferwidth-1
					offset = rowoffset + (x Shl 1)
					rgb    = PeekShort(bufferbank,offset)
					r      = ((rgb And $7C00) Shr 10) Shl 3
					g      = ((rgb And $3E0) Shr 5) Shl 3
					b      = (rgb And $1F) Shl 3
					
					If r = oldr And g = oldg And b = oldb
						;replace color
						rgb = newrgb
						;insert back into bank
						PokeShort bufferbank,offset,rgb
						PokeShort bufferbank,offset+2,rgb
					End If
				Next
			Next
		Case 3
			newrgb = newb Or (newg Shl 8) Or (newr Shl 16)
			For y = 0 To bufferheight-1
				rowoffset  = y * bufferpitch
				For x = 0 To bufferwidth-1
					offset = rowoffset + (x * 3)
					rgb    = PeekInt(bufferbank,offset)
					r      = (rgb And $FF0000) Shr 16
					g      = (rgb And $FF00) Shr 8
					b      = rgb And $FF
					
					If r = oldr And g = oldg And b = oldb
						;replace color
						rgb = newrgb
						;insert back into bank
						PokeInt bufferbank,offset,rgb
					End If
				Next
			Next
		Case 4
			newrgb = newb Or (newg Shl 8) Or (newr Shl 16)
			For y = 0 To bufferheight-1
				rowoffset  = y * bufferpitch
				For x = 0 To bufferwidth-1
					offset = rowoffset + (x Shl 2)
					rgb    = PeekInt(bufferbank,offset)
					r      = (rgb And $FF0000) Shr 16
					g      = (rgb And $FF00) Shr 8
					b      = rgb And $FF
					
					If r = oldr And g = oldg And b = oldb 
						;replace color
						rgb = newrgb
						;insert back into bank
						PokeInt bufferbank,offset,rgb
					End If
				Next
			Next
	End Select
	UnlockBuffer buffer
	SetBuffer oldbuffer
End Function
;#End Region
