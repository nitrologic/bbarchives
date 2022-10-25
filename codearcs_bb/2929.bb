; ID: 2929
; Author: sting
; Date: 2012-03-05 01:12:02
; Title: XOR Pattern
; Description: Generates an xor pattern

s=4
p=10
Graphics 128*s+p*2,128*s+p*2,16,2

pic = CreateImage(128*s,128*s)
SetBuffer(ImageBuffer(pic))
For y=0 To (128*s)-1
	For x=0 To (128*s)-1
		c=x Xor y
		Color ((1.0*c) Mod 64),((1.3*c) Mod 512),((2.0*c) Mod 1024)
		Rect x*s,y*s,s,s,1
	Next
Next
SetBuffer(BackBuffer())

DrawBlock Pic,p,p
Flip
WaitKey
