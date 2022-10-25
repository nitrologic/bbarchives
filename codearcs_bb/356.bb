; ID: 356
; Author: skn3[ac]
; Date: 2002-06-26 12:31:31
; Title: Create 2D terrains
; Description: The best of many methods i tried

Graphics 1024,768,32,1

Repeat
	SetBuffer BackBuffer()
	Cls
	CreateMap(GraphicsWidth(),GraphicsHeight())
	Flip
	WaitKey()
Until KeyDown(1)=True


Function CreateMap(width,height)
	y2=height
	y1=(height-100)/2+Rand(50,300)	
	For x=0 To width
		SeedRnd MilliSecs()
		y1=y1+Cos(x)*Rand(-(Sin(x)*Rand(5)),(Sin(x)*Rand(5)))
		If y1>y2 Then y1=y2
		If y1<(height-150)/3 Then y1=(height-150)/3
		Plot x,y1
	Next
End Function
