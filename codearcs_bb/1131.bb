; ID: 1131
; Author: skidracer
; Date: 2004-08-11 20:09:34
; Title: KeyImage
; Description: Produces clean edges for masked textures

Function KeyImage(texture,keycolor)
	oldbuffer=GraphicsBuffer() 
	buffer=TextureBuffer(texture)
	SetBuffer buffer
	LockBuffer buffer	
	w=GraphicsWidth()
	h=GraphicsHeight()
	keycolor=keycolor And $fcfcfc
; first pass set key
	For y=0 To h-1
		For x=0 To w-1
			c=ReadPixelFast(x,y)
			If (c And $fcfcfc)<>keycolor
				c=c Or $ff000000
			Else
				c=0
			EndIf
			WritePixelFast x,y,c
		Next
	Next
; second pass fix edges
	For y=1 To h-2
		For x=1 To w-2
			c=ReadPixelFast(x,y)
			If c=0
				t=0
				For yy=-1 To 1
					For xx=-1 To 1
						c=ReadPixelFast(x+xx,y+yy)
						If c and $ffffff
							t=((t And $fefefe) Shr 1)+((c And $fefefe) Shr 1)
						EndIf
					Next
				Next			
				WritePixelFast x+xx,y+yy,t
			EndIf
		Next
	Next
	UnlockBuffer buffer
	SetBuffer oldbuffer
End Function
