; ID: 1322
; Author: n8r2k
; Date: 2005-03-11 12:21:34
; Title: Background or Texture creator
; Description: Creates a neat image onscreen

Graphics 800,600,16,2
SeedRnd MilliSecs()
cols = 800
rows = 600
Dim num(Cols)

Goto blue ;change this to what u want


.grass
For x = 0 To cols Step 1
	For y = 0 To rows Step 1
	Color 0,Rand(0,200),0
		Plot x,y
Next
Next
SaveBuffer(FrontBuffer(),"grass.png")
Goto close 

.foil
For x = 0 To cols Step 1
	For y = 0 To rows Step 1
	Color Rand(0,200),Rand(0,200),Rand(0,200)
		Plot x,y
Next
Next
SaveBuffer(FrontBuffer(),"foil.png")
Goto close

.blue
For x = 0 To cols Step 1
	For y = 0 To rows Step 1
	Color Rand(0,200),Rand(0,200),255
		Plot x,y
Next
Next
SaveBuffer(FrontBuffer(),"blue.png")
Goto close

.red
For x = 0 To cols Step 1
	For y = 0 To rows Step 1
	Color Rand(0,200),0,0
		Plot x,y
Next
Next
SaveBuffer(FrontBuffer(),"red.png")
Goto close

.close 
End
