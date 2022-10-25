; ID: 836
; Author: xlsior
; Date: 2003-11-23 07:21:09
; Title: Monochrome
; Description: Converts a color image to greyscale

;
; Monochrome -- A function that converts the contents backbuffer() to greyscale.
; 11/23/2003, by Marc van den Dikkenberg / xlsior
; 
; Usage: monochrome(perct#)
;		 perct# is the level of the effect, in percentages.
;		 0 means no change, while 100 is pure greyscales.
;
; Note: The fade itself is probably to slow to be useful, 
; and has been included for educational purposes only.
;

Graphics 640,480,16,2
SetBuffer BackBuffer()

gfx$=LoadImage("x:\monkey6.jpg")
DrawImage gfx$,0,0
Flip
WaitKey()

For perct=10 To 100 Step 10
	DrawImage gfx$,0,0
	Monochrome(perct)
	Flip
Next

fntArial=LoadFont("Arial",24,True,False,False)
SetFont fntarial
Color 255,128,0
Rect 200,220,240,40
Color 255,255,255
Text 260,230,"Intermission"
FreeFont fntarial
Flip
WaitKey()

For perct=100 To 10 Step -10
	DrawImage gfx$,0,0
	Monochrome(perct)
	Flip
Next

WaitKey()
End


Function Monochrome(perct#)
	SetBuffer BackBuffer()
	LockBuffer
	For y=0 To 479 
		For x=0 To 639
			temp1=ReadPixel(x,y)
			
			orgb=(temp1 And $FF)
			orgg=(temp1 And $FF00) Shr 8
			orgr=(temp1 And $FF0000) Shr 16
			desb=((orgr*0.299)+(orgg*0.587)+(orgb*0.114)) 
		desr=orgr*(1-(perct#/100))+desb*(perct#/100) 
		desg=orgg*(1-(perct#/100))+desb*(perct#/100) 
			desb=orgb*(1-(perct#/100))+desb*(perct#/100) 

			WritePixel x,y,desb+(desg Shl 8)+(desr Shl 16)
		Next 
	Next 
	UnlockBuffer
End Function
