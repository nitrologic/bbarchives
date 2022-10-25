; ID: 2951
; Author: Captain Wicker
; Date: 2012-06-19 21:19:42
; Title: Setting the Gfx Driver and Screen Resolution
; Description: a little something i use to allow users to set up graphics settings :)

Graphics 800, 600, 16, 2

chipset = CountGfxDrivers()
Print "Select a graphics driver:"

For g = 1 To chipset

	Print g + " = " + GfxDriverName$(g)
	
Next

Selected = Input("")


SetGfxDriver Selected

Text 0,0,"Drivers Settings Applied"

width# = GraphicsWidth()
height# = GraphicsHeight()

Print "Type Screen Width: "

INwidth$ = Input("")

width# = INwidth$

Print "Type Screen Height: "

INheight$ = Input("")

height# = INheight$


Graphics3D width#, height#, 32, 3
SetBuffer(BackBuffer() )

Global light,camera,cube

light=CreateLight()
RotateEntity light,90,0,0

camera=CreateCamera()

cube=CreateCube()
PositionEntity cube,0,0,5

gwidth# = GraphicsWidth()
gheight# = GraphicsHeight()

mywidth$ = "Screen Width: " + Str gwidth#
myheight$ = "Screen Height: " + Str gheight#

While KeyHit(1)<>True
	
	TurnEntity cube,1,2,3
	


	UpdateWorld
	RenderWorld
	
	Text 0,0, mywidth$
	Text 0,15, myheight$

	Flip(True)
Wend
End
