; ID: 1902
; Author: Malice
; Date: 2007-01-21 01:20:54
; Title: ColourCycling
; Description: Gradually changes 1 colour to another (steps with each function call)

Graphics3D 800,600,32,0
SetBuffer BackBuffer()

SeedRnd MilliSecs()

Global Redcol
Global Greencol
Global Bluecol
	
	redend=Rand(256)
	greenend=Rand(256)
	blueend=Rand(256)
	

While Not KeyDown(1)
		
		cycle_colours(redcol,greencol,bluecol,redend,greenend,blueend)

		Color Redcol,Greencol,bluecol
			Rect 200,150,400,300,1
		Color 256-Redcol,256-greencol,256-bluecol
			Rect 350,250,100,100,1			
			
	
	If redcol=redend And greencol=greenend And bluecol=blueend
			redend=Rand(256)
			greenend=Rand(256)
			blueend=Rand(256)
	EndIf

Flip

Wend
		
Function cycle_colours(red1,green1,blue1,red2,green2,blue2)

	Redcol=Red1+(Sgn(red2-red1))
	Greencol=Green1+(Sgn(green2-green1))
	Bluecol=blue1+(Sgn(blue2-blue1))
		
End Function
