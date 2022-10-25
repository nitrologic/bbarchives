; ID: 1082
; Author: skn3[ac]
; Date: 2004-06-11 14:35:37
; Title: B+ fast pixel functions made easy!
; Description: Use the b+ fast pixel drawing operations easily. Uses sensibly structured commands to wrap the b+ LockedPixels(), LockedFormat() and LockedPitch() functions

; ID: 1082
; Author: skn3[ac]
; Date: 2004-06-11 14:35:37
; Title: B+ fast pixel functions made easy!
; Description: Use the b+ fast pixel drawing operations easily. Uses sensibly structured commands to wrap the b+ LockedPixels(), LockedFormat() and LockedPitch() functions

; ------------------------------------------------------------------------------------------------------------
;functions and runtime values
Global fastbank=0,fastformat=0,fastpitch=0,fastbuffer=0

Function FastLockBuffer(buffer)
	LockBuffer(buffer)
	fastbank   = LockedPixels()
	fastformat = LockedFormat()
	fastpitch  = LockedPitch()
	fastbuffer = buffer
End Function

Function FastUnlockBuffer()
	UnlockBuffer(fastbuffer)
	fastbank   = 0
	fastformat = 0
	fastpitch  = 0
	fastbuffer = 0
End Function

Function FastWritePixel(x,y,rgb)
	Select fastformat
		Case 1 : PokeInt(fastbank,(y * fastpitch) + (x Shl 1),(rgb Shl 24) + rgb)
		Case 2 : PokeInt(fastbank,(y * fastpitch) + (x Shl 1),(rgb Shl 24) + rgb)
		Case 3 : PokeInt(fastbank,(y * fastpitch) + (x * 3),rgb)
		Case 4 : PokeInt(fastbank,(y * fastpitch) + (x Shl 2),rgb)
	End Select
End Function

Function FastWritePixelFormat1(x,y,rgb)
	PokeInt(fastbank,(y * fastpitch) + (x Shl 1),(rgb Shl 24) + rgb)
End Function

Function FastWritePixelFormat2(x,y,rgb)
	PokeInt(fastbank,(y * fastpitch) + (x Shl 1),(rgb Shl 24) + rgb)
End Function

Function FastWritePixelFormat3(x,y,rgb)
	PokeInt(fastbank,(y * fastpitch) + (x * 3),rgb)
End Function

Function FastWritePixelFormat4(x,y,rgb)
	PokeInt(fastbank,(y * fastpitch) + (x Shl 2),rgb)
End Function

Function FastReadPixel(x,y)
	Select fastformat
		Case 1 : Return PeekShort(fastbank,(y * fastpitch) + (x Shl 1))
		Case 2 : Return PeekShort(fastbank,(y * fastpitch) + (x Shl 1))
		Case 3 : Return PeekInt(fastbank,(y * fastpitch) + (x * 3))
		Case 4 : Return PeekInt(fastbank,(y * fastpitch) + (x Shl 2))
	End Select
End Function

Function FastReadPixelFormat1(x,y)
	Return PeekShort(fastbank,(y * fastpitch) + (x Shl 1))
End Function

Function FastReadPixelFormat2(x,y)
	Return PeekShort(fastbank,(y * fastpitch) + (x Shl 1))
End Function

Function FastReadPixelFormat3(x,y)
	Return PeekInt(fastbank,(y * fastpitch) + (x * 3))
End Function

Function FastReadPixelFormat4(x,y)
	Return PeekInt(fastbank,(y * fastpitch) + (x Shl 2))
End Function

Function FastGetRgb(r,g,b)
	Select fastformat
		Case 1 : Return (r/8 Shl 11) Or (g/4 Shl 5) Or (b Shr 3)
		Case 2 : Return (r/8 Shl 10) Or (g/8 Shl 5) Or (b Shr 3)
		Case 3 : Return b Or (g Shl 8) Or (r Shl 16)
		Case 4 : Return b Or (g Shl 8) Or (r Shl 16)
	End Select
End Function

Function FastGetR(rgb)
	Select fastformat
		Case 1 : Return ((rgb And $F800) Shr 11) Shl 3 
		Case 2 : Return ((rgb And $7C00) Shr 10) Shl 3
		Case 3 : Return (rgb And $FF0000) Shr 16
		Case 4 : Return (rgb And $FF0000) Shr 16
	End Select
End Function

Function FastGetG(rgb)
	Select fastformat
		Case 1 : Return ((rgb And $7E0) Shr 5) Shl 2 
		Case 2 : Return ((rgb And $3E0) Shr 5) Shl 3
		Case 3 : Return (rgb And $FF00) Shr 8
		Case 4 : Return (rgb And $FF00) Shr 8
	End Select
End Function

Function FastGetB(rgb)
	Select fastformat
		Case 1 : Return (rgb And $1F) Shl 3
		Case 2 : Return (rgb And $1F) Shl 3
		Case 3 : Return rgb And $FF
		Case 4 : Return rgb And $FF
	End Select
End Function

; ------------------------------------------------------------------------------------------------------------
;examples
; - how to read a correct r,g,b from a pixel
;
;	FastLockBuffer(Backbuffer())
;		rgb = FastReadPixel(x,y)
;		r   = FastGetR(rgb)
;		g   = FastGetG(rgb)
;		b   = FastGetB(rgb)
;	FastUnlockBuffer()


; - how to write a correct r,g,b to a pixel
;
;	FastLockBuffer(backbuffer())
;		rgb = FastGetRgb(r,g,b)
;		FastWritePixel(x,y,rgb)
;	FastUnlockBuffer()


; - how to write multiple pixels at once
;	The system checks what format the graphics buffer is in each time you read/write a pixel.
;	if you use the following functions you can bypass the checks.
;
;	FastWritePixelFormat1(x,y,rgb)
;	FastWritePixelFormat2(x,y,rgb)
;	FastWritePixelFormat3(x,y,rgb)
;	FastWritePixelFormat4(x,y,rgb)
;
;	you would have to test the value fastformat, which ranges from 1-4. The benifit of doing this 
;	only becomes apparent when you want to perform lots of graphics operations at once.
;
;	FastLockBuffer(backbuffer())
;		rgb = FastGetRgb(r,g,b)
;		select fastformat
;			case 1
;				for x=0 to 640
;					FastWritePixelFormat1(x,y,rgb)
;				next
;			case 2
;				for x=0 to 640
;					FastWritePixelFormat2(x,y,rgb)
;				next
;			case 3
;				for x=0 to 640
;					FastWritePixelFormat3(x,y,rgb)
;				next
;			case 4
;				for x=0 to 640
;					FastWritePixelFormat4(x,y,rgb)
;				next
;		end select
;	FastUnlockBuffer()

; ------------------------------------------------------------------------------------------------------------
;demo
Graphics 640,480,32,2 ;<-- try changing to fullscreen if you are using a slow pc
SetBuffer(BackBuffer())

Global fpsdisplay = 0
Global fpslast    = MilliSecs()
Global fpscount   = 0

Global colorcycle = 0
Global colormode  = True

Global drawmode   = 1

Repeat
	Select drawmode
		Case 1
			rgb = (colorcycle Shl 16)+(g Shl 8)+b
			For y = 0 To 479
				For x = 0 To 639
					WritePixel(x,y,rgb)
				Next
			Next
			Color 255,255,255
			Text 5,5,"drawmode: Blitz WritePixel" + " (press space to toggle)"
			Text 5,20,"fps: " + fpsdisplay
			If KeyHit(57) drawmode = 2
		Case 2
			LockBuffer(BackBuffer())
				rgb = (colorcycle Shl 16)+(g Shl 8)+b
				For y = 0 To 479
					For x = 0 To 639
						WritePixelFast(x,y,rgb)
					Next
				Next
			UnlockBuffer(BackBuffer())
			Color 255,255,255
			Text 5,5,"drawmode: Blitz WritePixelFast" + " (press space to toggle)"
			Text 5,20,"fps: " + fpsdisplay
			If KeyHit(57) drawmode = 3
		Case 3
			FastLockBuffer(BackBuffer())
				rgb = FastGetRgb(colorcycle,0,0)
				For y = 0 To 479
					For x = 0 To 639
						FastWritePixel(x,y,rgb)
					Next
				Next
			FastUnlockBuffer()
			Color 255,255,255
			Text 5,5,"drawmode: FastWrite (no format speed up)" + " (press space to toggle)"
			Text 5,20,"fps: " + fpsdisplay
			If KeyHit(57) drawmode = 4
		Case 4
			FastLockBuffer(BackBuffer())
				rgb = FastGetRgb(colorcycle,0,0)
				Select fastformat
					Case 1
						For y = 0 To 479
							For x = 0 To 639
								FastWritePixelFormat1(x,y,rgb)
							Next
						Next
					Case 2
						For y = 0 To 479
							For x = 0 To 639
								FastWritePixelFormat2(x,y,rgb)
							Next
						Next
					Case 3
						For y = 0 To 479
							For x = 0 To 639
								FastWritePixelFormat3(x,y,rgb)
							Next
						Next
					Case 4
						For y = 0 To 479
							For x = 0 To 639
								FastWritePixelFormat4(x,y,rgb)
							Next
						Next
				End Select
			FastUnlockBuffer()
			Color 255,255,255
			Text 5,5,"drawmode: FastWrite (format speed up)" + " (press space to toggle)"
			Text 5,20,"fps: " + fpsdisplay
			If KeyHit(57) drawmode = 1
	End Select
	Flip False
	
	;update fps
	fpscount = fpscount + 1
	If MilliSecs() > fpslast+1000
		fpsdisplay = fpscount
		fpscount   = 0
		fpslast    = MilliSecs()
	End If
	
	;update color cycle
	If colormode = False
		colorcycle = colorcycle - 1
		If colorcycle = 0 colormode = True
	Else
		colorcycle = colorcycle + 1
		If colorcycle = 255 colormode = False
	End If
Until KeyDown(1)
