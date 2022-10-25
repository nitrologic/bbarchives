; ID: 1013
; Author: Ross C
; Date: 2004-05-04 09:30:21
; Title: Texture Mask Color
; Description: Mask off any color in a texture, instead of using black all the time

Graphics3D 800,600
SetBuffer BackBuffer()

cam=CreateCamera()
PositionEntity cam,0,0,-10

Global sprite=CreateSprite()
Global tex=LoadTexture("image1.png",4)
EntityTexture sprite,tex


cube=CreateCube()
PositionEntity cube,0,0,2
ScaleEntity cube,6,2,1


prepare_texture(tex)

While Not KeyHit(1)


	If KeyHit(2) Then texture_mask_colour(tex,  0,255,0,55) ; will clear green colours within 55 either way, fading.
	
	If KeyHit(4) Then texture_mask_colour(tex,0,0,0,50) ; will clear black and values 55 close to it.
	
	If KeyHit(3) EntityAlpha sprite,0.5
	UpdateWorld
	RenderWorld
	Text 0,0," Press 1 to mask green from the texture"
	Text 0,10," Press 3 to mask black from the texture"
	Text 0,20," Press 2 to turn on entityalpha "
	Flip
Wend
End


;--------------------------------------------------------------------
;  This function will clear a texture of all it's alpha information |
;--------------------------------------------------------------------
;parameters  =  texture : the texture you wish to clear alpha information from
Function prepare_texture(texture)

	LockBuffer TextureBuffer(texture)

	For loop=0 To TextureWidth(texture)-1
		For loop1=0 To TextureHeight(texture)-1

			RGB1=ReadPixelFast(loop,loop1,TextureBuffer(texture))
			r=(RGB1 And $FF0000)shr 16;separate out the red
			g=(RGB1 And $FF00) shr 8;green
			b=RGB1 And $FF;and blue parts of the color
			a=(RGB1 And $FF000000)Shr 24
			
			a=255; remove any alpha information currently in the texture.

			newrgb= (a shl 24) or (r shl 16) or (g shl 8) or b; combine the ARGB back into a number

			WritePixelFast(loop,loop1,newrgb,TextureBuffer(texture)); write the info back to the texture
		Next
	Next

	UnlockBuffer TextureBuffer(texture)

End Function


;---------------------------------------------------------------------
;  This function will mask the passed across RGB of the texture also |
;  passed across. Do NOT pass across a value for flag if you only    |
;  want to mask an exact colour.                                     |
;---------------------------------------------------------------------
;parameters = texture   : the texture you wish to clear alpha information from
;           = r1        : the red value to mask
;           = g1        : the green value to mask
;           = b1        : the blue value to mask
;           = tolerance : the tolerance value. If set to 0, then the function will only mask the
;                         EXACT colours passed across. Higher value will mask values close to the colour.

Function texture_mask_colour(texture,r1,g1,b1,tolerance=0)

	
	LockBuffer TextureBuffer(texture)

	For loop=0 To TextureWidth(texture)-1
		For loop1=0 To TextureHeight(texture)-1
			RGB1=ReadPixelFast(loop,loop1,TextureBuffer(texture)) ; read the RGB value from the texture
			r=(RGB1 And $FF0000)shr 16;separate out the red
			g=(RGB1 And $FF00) shr 8;green
			b=RGB1 And $FF;and blue parts of the color
			a=(RGB1 And $FF000000)Shr 24 ; extract the alpha

			If r>=r1-tolerance And r=<r1+tolerance And g=>g1-tolerance And g=<g1+tolerance And b=>b1-tolerance And b=<b1+tolerance Then; check RGB lies with the tolerance
				;temp=((Abs(r-r1)+Abs(g-g1)+Abs(b-b1))/3.0)*4.0 ; alpha the values based on the tolerance value
				a=0;temp

			End If

			newrgb= (a shl 24) or (r shl 16) or (g shl 8) or b ; combine the ARGB back into one value

			WritePixelFast(loop,loop1,newrgb,TextureBuffer(texture)); write back to the texture
		Next
	Next
	UnlockBuffer TextureBuffer(texture)
End Function
