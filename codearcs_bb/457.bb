; ID: 457
; Author: djr2cool
; Date: 2002-10-11 09:57:17
; Title: Liquid Fun
; Description: Drag you mouse through some liquid

SetBuffer BackBuffer()

Global CurrentTexture=1,NextTexture=0,TempValue=0

Const MAXWIDTH = 175
Const MAXHEIGHT = 125


;Change these to get different results
Global DEPTH=-800
Global VISCOSITY=128  ;low the number the more water like, hight the number is more like oil

Dim WaveMap(4,MAXWIDTH,MAXHEIGHT)




While Not KeyDown(1)

	Cls	
	UpdateWaveMap()
	Color 255,0,0
	Plot MouseX(),MouseY()
	If MouseDown(1) Then WaveMap(CurrentTexture,MouseX(),MouseY())=DEPTH
	
	Flip 

Wend

Function UpdateWaveMap()
	For y = 1 To MAXHEIGHT-1
		For x = 1 To MAXWIDTH-1
			n=(WaveMap(CurrentTexture,x-1,y)+WaveMap(CurrentTexture,x+1,y)+WaveMap(CurrentTexture,x,y-1)+WaveMap(CurrentTexture,x,y+1))/2 - WaveMap(NextTexture,x,y)
		   	n=n-n/VISCOSITY
			WaveMap(NextTexture,x,y)=n
			c = 100-WaveMap(CurrentTexture,x,y) And 255
			Color 0,0,c
			Plot x,y
		Next
	Next
			
	TempValue=CurrentTexture
	CurrentTexture=NextTexture
	NextTexture=TempValue
End Function
