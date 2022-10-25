; ID: 913
; Author: ford escort
; Date: 2004-02-04 17:34:23
; Title: transparent sprites in b+
; Description: this make transparent sprites in b+

;------this must be included before the functions call
Dim lookup#(255)
For a=0 To 255
	lookup#(a)=(a*1.0)/100
Next 
;------------------------------------------------------
Function bufferize(image,flag,rm,gm,bm)	; this function convert a picture to a rgb coded memorybank
								 	; flag 1 free the memory of the original image after the process							
									;rm,gm,bm rgb of the color mask (not drawed)
	w=ImageWidth(image)		 	;get the width
	h=ImageHeight(image)		 	;get the height
	bank=CreateBank((w*h*3)+7)		;the First 4 bytes to store the width and height of the picture+3 for the mask
	LockBuffer ImageBuffer(image)	;lock the buffer to speedup things
	PokeShort(bank,0,w)		;store the imaghe width
	PokeShort(bank,2,h)		;store the image height
	PokeByte(bank,4,rm)		;store the red value of the mask
	PokeByte(bank,5,gm)		;store the green value of the mask
	PokeByte(bank,6,bm)		;store the blue value of the mask
	offset=7
	For y=0 To h-1					;
		For x=0 To w-1				;
			c=(ReadPixelFast(x,y,ImageBuffer(image))Shl 8)Shr 8 ; read the curent pixel
			r=(c Shl 8)Shr 24
			g=(c Shl 16)Shr 24
			b=(c Shl 24)Shr 24
			PokeByte bank,offset,r;store the red value of the curent pixel
			offset=offset+1
			PokeByte bank,offset,g;store the green value of the curent pixel
			offset=offset+1
			PokeByte bank,offset,b;store the blue value of the curent pixel
			offset=offset+1
		Next
	Next
UnlockBuffer ImageBuffer(image)
If flag=1
	FreeImage image
EndIf
Return bank
End Function

;usage:
;
;
;alphasprite(bank,xx,yy,percent,flag)
;bank : the handle of the bank containing the sprite (made with bufferize function)
;xx,yy: onscreen coordinate where the sprite will be drawn
;percent: transparency percentage
;flag : if set to 1 the mask color will not be drawn same as maskimage
;
Function alphasprite(bank,xx,yy,percent,flag); this draw a previously memory saved image to the position x,yand percent alpha blending if flag=1 then masked
	buffer=GraphicsBuffer()
	gtw=GraphicsWidth()
	gth=GraphicsHeight()
	w=PeekShort(bank,0)
	h=PeekShort(bank,2)
	rm=PeekByte(bank,4)
	gm=PeekByte(bank,5)
	bm=PeekByte(bank,6)
	offset=7
	LockBuffer buffer
	For y=0 To h-1				;
		For x=0 To w-1
			r2=PeekByte(bank,offset)	;the
			offset=offset+1				;r
			g2=PeekByte(bank,offset)	;b
			offset=offset+1				;values
			b2=PeekByte(bank,offset)	;the
			offset=offset+1				;pictures 
			If xx+x<=Gtw And yy+y<=GtH	; the coordinates are IN the drawing buffer
				If flag=0 Or (flag=1 And r2<>rm Or g2<>gm Or b2<>bm); the flag =0 or the colors are not the masked colors
					c=ReadPixelFast(xx+x,yy+y,buffer) ; read the curent pixel
					r1=(c Shl 8)Shr 24
					g1=(c Shl 16)Shr 24
					b1=(c Shl 24)Shr 24
					r=(lookup(r2)*(percent)+lookup(r1)*(100-(percent)))Shl 16;prepare the values 
					g=(lookup(g2)*(percent)+lookup(g1)*(100-(percent)))Shl 8  ;to 
					b=(lookup(b2)*(percent)+lookup(b1)*(100-(percent)))       ;bewritepixeled
					WritePixelFast xx+x,yy+y,r+g+b,buffer;draw the new color pixel
				EndIf			
			EndIf
		Next
	Next
	UnlockBuffer buffer
End Function
