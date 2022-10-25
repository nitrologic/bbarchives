; ID: 1739
; Author: fredborg
; Date: 2006-06-24 21:11:01
; Title: LoadMaskedTexture
; Description: Load a masked texture without black borders :)

Function LoadMaskedTexture(file$,flags=0,threshold=200)

	If flags And 2 Then flags = flags Xor 2
	If flags And 4 Then flags = flags Xor 4

	tex = LoadTexture(file$,flags Or 2)
	
	If tex
		buff	= TextureBuffer(tex)
		width	= TextureWidth(tex) - 1
		height	= TextureHeight(tex) - 1

		masktex = CreateTexture(width+1,height+1,flags Or 4)

		tbuff	= TextureBuffer(masktex)

		amul#	= 255.0/(255.0-threshold)

		LockBuffer buff
		LockBuffer tbuff
		For x = 0 To width
			For y = 0 To height
				argb = ReadPixelFast(x,y,buff)
				
				a = (argb Shr 24) And %11111111
				If a<threshold
					a = 0
				Else
					a = (a-threshold)*amul
				EndIf
				
				r = (argb Shr 16) And %11111111 
				g = (argb Shr 8) And %11111111 
				b = argb And %11111111 
				
				argb = b Or (g Shl 8) Or (r Shl 16) Or (a Shl 24)
				WritePixelFast x,y,argb,tbuff
			Next
		Next
		UnlockBuffer buff
		UnlockBuffer tbuff
		
		FreeTexture tex
	Else
		masktex = CreateTexture(64,64,flags)
		buff	= TextureBuffer(masktex)
		width	= TextureWidth(masktex) - 1
		height	= TextureHeight(masktex) - 1
		For x = 0 To width
			xb = (x/8) Mod 2
			For y = 0 To height
				yb = (y/8) Mod 2
				c = -Sgn(xb+yb)
				WritePixel x,y,c,buff
			Next
		Next
	End If
	
	Return masktex

End Function
