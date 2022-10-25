; ID: 29
; Author: skidracer
; Date: 2001-08-30 05:13:55
; Title: Fade
; Description: Fades the screen to black

; fade.bb
; by simon@acid.co.nz

; as readpixel is slow this fade routine copies the graphics buffer to a bank
; and uses the bank as a pixel source for subsequent calls to fade

Graphics 640,480,32

While Not MouseHit(1)
	Color Rnd(255),Rnd(255),Rnd(255)
	Line Rnd(640),Rnd(480),Rnd(640),Rnd(480)
Wend
gfxbank=GrabBank()

; use double buffering for clean fade

SetBuffer BackBuffer()
For i=1 To 32
	Fade(gfxbank)
	Flip
Next
FreeBank gfxbank

MouseWait
End	

Function GrabBank()
	w=GraphicsWidth()
	h=GraphicsHeight()
	bank=CreateBank(w*h*4)	
	gbuffer=GraphicsBuffer()
	LockBuffer gbuffer
	For y=0 To h-1
		For x=0 To w-1
			PokeInt bank,o,ReadPixelFast(x,y)
			o=o+4
		Next
	Next
	UnlockBuffer gbuffer	
	Return bank
End Function

Function Fade(bank)
	w=GraphicsWidth()-1
	h=GraphicsHeight()-1
	gbuffer=GraphicsBuffer()
	LockBuffer gbuffer
	For y=0 To h
		For x=0 To w
			rgb=PeekInt(bank,o)
			d=(rgb Shr 3) And $1f1f1f
			If d=0 d=rgb
			rgb=rgb-d
			WritePixelFast x,y,rgb
			PokeInt(bank,o,rgb)
			o=o+4
		Next
	Next
	UnlockBuffer gbuffer	
End Function
