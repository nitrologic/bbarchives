; ID: 2163
; Author: Nebula
; Date: 2007-11-28 17:55:31
; Title: Texture or graphics making parts
; Description: code parts that make graphics or textures

;
;
;
; Texture filter 7
;
;
;
;
;
Include "filter_include.bb"
;
Graphics 640,480,16,2
SetBuffer BackBuffer()
;
Type font
	Field bmp
	Field normal
End Type

Global font.font = New font
font\bmp = LoadFont("verdana.ttf",32)
font\normal = LoadFont("verdana.ttf",11)
;
Type gfx
	Field buffer[10]
End Type
Global gfx.gfx = New gfx
inigfx()
;
ms = MilliSecs()
;

; These are some filter combinations. Uncomment 1 per time to 
; generate them.

;bats : Delay 4000
;land(3) : Delay 4000
;image1() : Delay 4000
;image2 : Delay 4000
;image3 : Delay 4000
;image4  : Delay 4000; HQuality - needs 3d'n
image5 : Delay 4000



ms = MilliSecs() - ms
While KeyDown(1) = False
	Cls
	Color 255,255,255	
	If KeyHit(2) = True Then dripfilter2(150)
	If KeyHit(3) = True Then addedge(50)
	;DrawBlock gfx\buffer[0],0,0
	;DrawImageRect gfx\buffer[0],0,0,0,0,256,256
	DrawImageRect gfx\buffer[0],0,0,0,0,640,480
	Text GraphicsWidth()-256,0,ms
	Flip
Wend
End



Function bats()
	dripnoise()
	dripfilter2(150)
	edgemess
	addedge(100)
	For i=0 To 150
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	addedge(50)
	For i=0 To 14
	dripfilter2(150)
	Next
End Function


Function image1()
	Text 0,0,"please wait":Flip
	For i=0 To 15
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	;edgemess
	For i=0 To 20
	addedge(101)
	Next
End Function

Function image2()
	dripnoiseint(100)
	greyfilter(100)
	dripfilter(50)
	dripfilter2(150)
	dripfilter2(150)
End Function

Function land(num)
	dripnoiseint(num)
	edgemess()
	;greyfilter(100)

	For i=0 To 10
		dripfilter2(150)
	Next
End Function

Function image3()
	dripnoiseint(2)
	edgemess()

	For i=0 To 10
		dripfilter2(150)
	Next
	addedge(50)
	dripnoise

	addedge(80)
	blackout
End Function

Function image4()
	dripnoiseint(20)
	;blackout

	For i=0 To 5
	dripfilter(25)
	Next
	greyfilter(100)
	For i=0 To 5
	edgemess()
	Next
	dripfilter2(150)
	dripfilter2(150)
End Function

Function image5()
	For i=0 To 250
	spotnoise(110,250)
	Next
	edgemess
	blackout
	dripnoise
	dripnoiseint(20)
	greyfilter(50)
End Function 



; Cut here and delete the other things. save the include part









;
;
; Texture filter 6
;
;
;
;
;
;
;
;
Include "filter_include.bb"
;
Graphics 640,480,16,2
SetBuffer BackBuffer()
;
Type font
	Field bmp
	Field normal
End Type

Global font.font = New font
font\bmp = LoadFont("verdana.ttf",32)
font\normal = LoadFont("verdana.ttf",11)
;
Type gfx
	Field buffer[10]
End Type
Global gfx.gfx = New gfx
inigfx()
;
ms = MilliSecs()
;

; These are some filter combinations. Uncomment 1 per time to 
; generate them.

;bats : Delay 4000
;land(3) : Delay 4000
;image1() : Delay 4000
;image2 : Delay 4000
;image3 : Delay 4000
image4  : Delay 4000; HQuality - needs 3d'n
;image5 : Delay 4000



ms = MilliSecs() - ms
While KeyDown(1) = False
	Cls
	Color 255,255,255	
	If KeyHit(2) = True Then dripfilter2(150)
	If KeyHit(3) = True Then addedge(50)
	;DrawBlock gfx\buffer[0],0,0
	;DrawImageRect gfx\buffer[0],0,0,0,0,256,256
	DrawImageRect gfx\buffer[0],0,0,0,0,640,480
	Text GraphicsWidth()-256,0,ms
	Flip
Wend
End



Function bats()
	dripnoise()
	dripfilter2(150)
	edgemess
	addedge(100)
	For i=0 To 150
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	addedge(50)
	For i=0 To 14
	dripfilter2(150)
	Next
End Function


Function image1()
	Text 0,0,"please wait":Flip
	For i=0 To 15
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	;edgemess
	For i=0 To 20
	addedge(101)
	Next
End Function

Function image2()
	dripnoiseint(100)
	greyfilter(100)
	dripfilter(50)
	dripfilter2(150)
	dripfilter2(150)
End Function

Function land(num)
	dripnoiseint(num)
	edgemess()
	;greyfilter(100)

	For i=0 To 10
		dripfilter2(150)
	Next
End Function

Function image3()
	dripnoiseint(2)
	edgemess()

	For i=0 To 10
		dripfilter2(150)
	Next
	addedge(50)
	dripnoise

	addedge(80)
	blackout
End Function

Function image4()
	dripnoiseint(20)
	;blackout

	For i=0 To 5
	dripfilter(25)
	Next
	greyfilter(100)
	For i=0 To 5
	edgemess()
	Next
	dripfilter2(150)
	dripfilter2(150)
End Function

Function image5()
	For i=0 To 250
	spotnoise(110,250)
	Next
	edgemess
	blackout
	dripnoise
	dripnoiseint(20)
	greyfilter(50)
End Function 




; Cut here and delete the other things. save the include part








;
;
;
; Texture filter 5
;
;
;
;
;
;
Include "filter_include.bb"
;
Graphics 640,480,16,2
SetBuffer BackBuffer()
;
Type font
	Field bmp
	Field normal
End Type

Global font.font = New font
font\bmp = LoadFont("verdana.ttf",32)
font\normal = LoadFont("verdana.ttf",11)
;
Type gfx
	Field buffer[10]
End Type
Global gfx.gfx = New gfx
inigfx()
;
ms = MilliSecs()
;

; These are some filter combinations. Uncomment 1 per time to 
; generate them.

;bats : Delay 4000
;land(3) : Delay 4000
;image1() : Delay 4000
;image2 : Delay 4000
image3 : Delay 4000
;image4  : Delay 4000; HQuality - needs 3d'n
;image5 : Delay 4000



ms = MilliSecs() - ms
While KeyDown(1) = False
	Cls
	Color 255,255,255	
	If KeyHit(2) = True Then dripfilter2(150)
	If KeyHit(3) = True Then addedge(50)
	;DrawBlock gfx\buffer[0],0,0
	;DrawImageRect gfx\buffer[0],0,0,0,0,256,256
	DrawImageRect gfx\buffer[0],0,0,0,0,640,480
	Text GraphicsWidth()-256,0,ms
	Flip
Wend
End



Function bats()
	dripnoise()
	dripfilter2(150)
	edgemess
	addedge(100)
	For i=0 To 150
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	addedge(50)
	For i=0 To 14
	dripfilter2(150)
	Next
End Function


Function image1()
	Text 0,0,"please wait":Flip
	For i=0 To 15
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	;edgemess
	For i=0 To 20
	addedge(101)
	Next
End Function

Function image2()
	dripnoiseint(100)
	greyfilter(100)
	dripfilter(50)
	dripfilter2(150)
	dripfilter2(150)
End Function

Function land(num)
	dripnoiseint(num)
	edgemess()
	;greyfilter(100)

	For i=0 To 10
		dripfilter2(150)
	Next
End Function

Function image3()
	dripnoiseint(2)
	edgemess()

	For i=0 To 10
		dripfilter2(150)
	Next
	addedge(50)
	dripnoise

	addedge(80)
	blackout
End Function

Function image4()
	dripnoiseint(20)
	;blackout

	For i=0 To 5
	dripfilter(25)
	Next
	greyfilter(100)
	For i=0 To 5
	edgemess()
	Next
	dripfilter2(150)
	dripfilter2(150)
End Function

Function image5()
	For i=0 To 250
	spotnoise(110,250)
	Next
	edgemess
	blackout
	dripnoise
	dripnoiseint(20)
	greyfilter(50)
End Function 





; Cut here and delete the other things. save the include part








;
;
;
; Texture filter 4
;
;
;
;
;
;
;
;
Include "filter_include.bb"
;
Graphics 640,480,16,2
SetBuffer BackBuffer()
;
Type font
	Field bmp
	Field normal
End Type

Global font.font = New font
font\bmp = LoadFont("verdana.ttf",32)
font\normal = LoadFont("verdana.ttf",11)
;
Type gfx
	Field buffer[10]
End Type
Global gfx.gfx = New gfx
inigfx()
;
ms = MilliSecs()
;

; These are some filter combinations. Uncomment 1 per time to 
; generate them.

;bats : Delay 4000
;land(3) : Delay 4000
;image1() : Delay 4000
image2 : Delay 4000
;image3 : Delay 4000
;image4  : Delay 4000; HQuality - needs 3d'n
;image5 : Delay 4000



ms = MilliSecs() - ms
While KeyDown(1) = False
	Cls
	Color 255,255,255	
	If KeyHit(2) = True Then dripfilter2(150)
	If KeyHit(3) = True Then addedge(50)
	;DrawBlock gfx\buffer[0],0,0
	;DrawImageRect gfx\buffer[0],0,0,0,0,256,256
	DrawImageRect gfx\buffer[0],0,0,0,0,640,480
	Text GraphicsWidth()-256,0,ms
	Flip
Wend
End



Function bats()
	dripnoise()
	dripfilter2(150)
	edgemess
	addedge(100)
	For i=0 To 150
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	addedge(50)
	For i=0 To 14
	dripfilter2(150)
	Next
End Function


Function image1()
	Text 0,0,"please wait":Flip
	For i=0 To 15
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	;edgemess
	For i=0 To 20
	addedge(101)
	Next
End Function

Function image2()
	dripnoiseint(100)
	greyfilter(100)
	dripfilter(50)
	dripfilter2(150)
	dripfilter2(150)
End Function

Function land(num)
	dripnoiseint(num)
	edgemess()
	;greyfilter(100)

	For i=0 To 10
		dripfilter2(150)
	Next
End Function

Function image3()
	dripnoiseint(2)
	edgemess()

	For i=0 To 10
		dripfilter2(150)
	Next
	addedge(50)
	dripnoise

	addedge(80)
	blackout
End Function

Function image4()
	dripnoiseint(20)
	;blackout

	For i=0 To 5
	dripfilter(25)
	Next
	greyfilter(100)
	For i=0 To 5
	edgemess()
	Next
	dripfilter2(150)
	dripfilter2(150)
End Function

Function image5()
	For i=0 To 250
	spotnoise(110,250)
	Next
	edgemess
	blackout
	dripnoise
	dripnoiseint(20)
	greyfilter(50)
End Function 






; Cut here and delete the other things. save the include part










;
;
; Texture filter 3
;
;
;
;
;
;
Include "filter_include.bb"
;
Graphics 640,480,16,2
SetBuffer BackBuffer()
;
Type font
	Field bmp
	Field normal
End Type

Global font.font = New font
font\bmp = LoadFont("verdana.ttf",32)
font\normal = LoadFont("verdana.ttf",11)
;
Type gfx
	Field buffer[10]
End Type
Global gfx.gfx = New gfx
inigfx()
;
ms = MilliSecs()
;

; These are some filter combinations. Uncomment 1 per time to 
; generate them.

;bats : Delay 4000
;land(3) : Delay 4000
image1() : Delay 4000
;image2 : Delay 4000
;image3 : Delay 4000
;image4  : Delay 4000; HQuality - needs 3d'n
;image5 : Delay 4000



ms = MilliSecs() - ms
While KeyDown(1) = False
	Cls
	Color 255,255,255	
	If KeyHit(2) = True Then dripfilter2(150)
	If KeyHit(3) = True Then addedge(50)
	;DrawBlock gfx\buffer[0],0,0
	;DrawImageRect gfx\buffer[0],0,0,0,0,256,256
	DrawImageRect gfx\buffer[0],0,0,0,0,640,480
	Text GraphicsWidth()-256,0,ms
	Flip
Wend
End



Function bats()
	dripnoise()
	dripfilter2(150)
	edgemess
	addedge(100)
	For i=0 To 150
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	addedge(50)
	For i=0 To 14
	dripfilter2(150)
	Next
End Function


Function image1()
	Text 0,0,"please wait":Flip
	For i=0 To 15
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	;edgemess
	For i=0 To 20
	addedge(101)
	Next
End Function

Function image2()
	dripnoiseint(100)
	greyfilter(100)
	dripfilter(50)
	dripfilter2(150)
	dripfilter2(150)
End Function

Function land(num)
	dripnoiseint(num)
	edgemess()
	;greyfilter(100)

	For i=0 To 10
		dripfilter2(150)
	Next
End Function

Function image3()
	dripnoiseint(2)
	edgemess()

	For i=0 To 10
		dripfilter2(150)
	Next
	addedge(50)
	dripnoise

	addedge(80)
	blackout
End Function

Function image4()
	dripnoiseint(20)
	;blackout

	For i=0 To 5
	dripfilter(25)
	Next
	greyfilter(100)
	For i=0 To 5
	edgemess()
	Next
	dripfilter2(150)
	dripfilter2(150)
End Function

Function image5()
	For i=0 To 250
	spotnoise(110,250)
	Next
	edgemess
	blackout
	dripnoise
	dripnoiseint(20)
	greyfilter(50)
End Function 








; Cut here and delete the other things. save the include part








;
;
; Texture filter 2
;
;
;
;
;
Include "filter_include.bb"
;
Graphics 640,480,16,2
SetBuffer BackBuffer()
;
Type font
	Field bmp
	Field normal
End Type

Global font.font = New font
font\bmp = LoadFont("verdana.ttf",32)
font\normal = LoadFont("verdana.ttf",11)
;
Type gfx
	Field buffer[10]
End Type
Global gfx.gfx = New gfx
inigfx()
;
ms = MilliSecs()
;

; These are some filter combinations. Uncomment 1 per time to 
; generate them.

;bats : Delay 4000
land(3) : Delay 4000
;image1() : Delay 4000
;image2 : Delay 4000
;image3 : Delay 4000
;image4  : Delay 4000; HQuality - needs 3d'n
;image5 : Delay 4000



ms = MilliSecs() - ms
While KeyDown(1) = False
	Cls
	Color 255,255,255	
	If KeyHit(2) = True Then dripfilter2(150)
	If KeyHit(3) = True Then addedge(50)
	;DrawBlock gfx\buffer[0],0,0
	;DrawImageRect gfx\buffer[0],0,0,0,0,256,256
	DrawImageRect gfx\buffer[0],0,0,0,0,640,480
	Text GraphicsWidth()-256,0,ms
	Flip
Wend
End



Function bats()
	dripnoise()
	dripfilter2(150)
	edgemess
	addedge(100)
	For i=0 To 150
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	addedge(50)
	For i=0 To 14
	dripfilter2(150)
	Next
End Function


Function image1()
	Text 0,0,"please wait":Flip
	For i=0 To 15
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	;edgemess
	For i=0 To 20
	addedge(101)
	Next
End Function

Function image2()
	dripnoiseint(100)
	greyfilter(100)
	dripfilter(50)
	dripfilter2(150)
	dripfilter2(150)
End Function

Function land(num)
	dripnoiseint(num)
	edgemess()
	;greyfilter(100)

	For i=0 To 10
		dripfilter2(150)
	Next
End Function

Function image3()
	dripnoiseint(2)
	edgemess()

	For i=0 To 10
		dripfilter2(150)
	Next
	addedge(50)
	dripnoise

	addedge(80)
	blackout
End Function

Function image4()
	dripnoiseint(20)
	;blackout

	For i=0 To 5
	dripfilter(25)
	Next
	greyfilter(100)
	For i=0 To 5
	edgemess()
	Next
	dripfilter2(150)
	dripfilter2(150)
End Function

Function image5()
	For i=0 To 250
	spotnoise(110,250)
	Next
	edgemess
	blackout
	dripnoise
	dripnoiseint(20)
	greyfilter(50)
End Function 










; Cut here and delete the other things. save the include part








;
;
; Texture Filter 1
;
;
;
;
;
Include "filter_include.bb"
;
Graphics 640,480,16,2
SetBuffer BackBuffer()
;
Type font
	Field bmp
	Field normal
End Type

Global font.font = New font
font\bmp = LoadFont("verdana.ttf",32)
font\normal = LoadFont("verdana.ttf",11)
;
Type gfx
	Field buffer[10]
End Type
Global gfx.gfx = New gfx
inigfx()
;
ms = MilliSecs()
;

; These are some filter combinations. Uncomment 1 per time to 
; generate them.

bats : Delay 4000
;land(3) : Delay 4000
;image1() : Delay 4000
;image2 : Delay 4000
;image3 : Delay 4000
;image4  : Delay 4000; HQuality - needs 3d'n
;image5 : Delay 4000



ms = MilliSecs() - ms
While KeyDown(1) = False
	Cls
	Color 255,255,255	
	If KeyHit(2) = True Then dripfilter2(150)
	If KeyHit(3) = True Then addedge(50)
	;DrawBlock gfx\buffer[0],0,0
	;DrawImageRect gfx\buffer[0],0,0,0,0,256,256
	DrawImageRect gfx\buffer[0],0,0,0,0,640,480
	Text GraphicsWidth()-256,0,ms
	Flip
Wend
End



Function bats()
	dripnoise()
	dripfilter2(150)
	edgemess
	addedge(100)
	For i=0 To 150
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	addedge(50)
	For i=0 To 14
	dripfilter2(150)
	Next
End Function


Function image1()
	Text 0,0,"please wait":Flip
	For i=0 To 15
	spotnoise(100,150)
	spotnoise(50,100)
	Next
	;edgemess
	For i=0 To 20
	addedge(101)
	Next
End Function

Function image2()
	dripnoiseint(100)
	greyfilter(100)
	dripfilter(50)
	dripfilter2(150)
	dripfilter2(150)
End Function

Function land(num)
	dripnoiseint(num)
	edgemess()
	;greyfilter(100)

	For i=0 To 10
		dripfilter2(150)
	Next
End Function

Function image3()
	dripnoiseint(2)
	edgemess()

	For i=0 To 10
		dripfilter2(150)
	Next
	addedge(50)
	dripnoise

	addedge(80)
	blackout
End Function

Function image4()
	dripnoiseint(20)
	;blackout

	For i=0 To 5
	dripfilter(25)
	Next
	greyfilter(100)
	For i=0 To 5
	edgemess()
	Next
	dripfilter2(150)
	dripfilter2(150)
End Function

Function image5()
	For i=0 To 250
	spotnoise(110,250)
	Next
	edgemess
	blackout
	dripnoise
	dripnoiseint(20)
	greyfilter(50)
End Function 





; Cut here and delete the other things. save the include part



;
;
; This is the include part.
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
;
; Filters Include file
;

Function makefont()
	SetBuffer ImageBuffer(gfx\buffer[1])
	SetFont font\bmp
	Color 255,255,255
	cnt=32
	For y=0 To 480 Step 32
		For x=0 To 640-64 Step 32
		If cnt<256
		Text x+16,y+16,Chr(cnt)
		cnt=cnt+1
	End If
	Next:Next


	SetFont font\normal
	SetBuffer BackBuffer()
End Function



Function edgemessfont()
	SetBuffer ImageBuffer(gfx\buffer[1])
	LockBuffer ImageBuffer(gfx\buffer[1])
	Local iw = ImageWidth(gfx\buffer[1])
	Local ih = ImageHeight(gfx\buffer[1])
	For i=0 To 3
	
	For x1 = 1 To iw-1 
	For y1 = 1 To ih-1
	If Rand(1,2) = 1
	a = ReadPixelFast(x1+1,y1)
	b = ReadPixelFast(x1,y1)
	If rgbisblack(b) = True And rgbisblack(a) = False Then
		WritePixelFast(x1,y1,lightrgb(a,49))
	End If
	End If
	Next:Next
	
	;
	For x1 = iw-1 To 1 Step -1
	For y1 = 1 To ih-1
	If Rand(1,2) = 1
	a = ReadPixelFast(x1-1,y1)
	b = ReadPixelFast(x1,y1)
	If rgbisblack(b) = True And rgbisblack(a) = False Then
		WritePixelFast(x1,y1,lightrgb(a,49))
	End If
	End If
	Next:Next
	
	For x1 = 1 To iw-1
	For y1 = ih-1 To 1 Step -1
	If Rand(1,2) = 1
	a = ReadPixelFast(x1,y1-1)
	b = ReadPixelFast(x1,y1)
	If rgbisblack(b) = True And rgbisblack(a) = False Then
		WritePixelFast(x1,y1,lightrgb(a,49))
	End If
	End If
	Next:Next

	For x1 = 1 To iw-1 	 
	For y1 = 1 To ih-2
	If Rand(1,2) = 1
		a = ReadPixelFast(x1,y1+1)
		b = ReadPixelFast(x1,y1)
		If rgbisblack(b) = True And rgbisblack(a) = False Then
			WritePixelFast(x1,y1,lightrgb(a,49))
		End If
	End If
	Next:Next

Next

	UnlockBuffer ImageBuffer(gfx\buffer[1])
	SetBuffer BackBuffer()
End Function



Function lightuprect(x,y,w,h)
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])

	Local Stp# = 200/h
	Local cnt#

	For y1=y To y+h
	For x1=x To x+w
		a = ReadPixelFast(x1,y1)
		b = lightrgb(a,100+cnt)
		WritePixelFast(x1,y1,b)
	Next:
	cnt=cnt+stp
	Next


	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()

End Function


Function lightdownrect(x,y,w,h)
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])

	Local Stp# = 200/h
	Local cnt#

	For y1=y To y+h
	For x1=x To x+w
		a = ReadPixelFast(x1,y1)
		b = lightrgb(a,300-cnt)
		WritePixelFast(x1,y1,b)
	Next:
	cnt=cnt+stp
	Next


	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()

End Function

Function lightup()
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])

	For y=0 To 256 
	For x=0 To 256
		a = ReadPixelFast(x,y)
		b = lightrgb(a,100+y/2.56)
		WritePixelFast(x,y,b)
	Next:Next


	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
End Function


Function lightdown()
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])

	For y=0 To 256
	For x=0 To 256
		a = ReadPixelFast(x,y)
		b = lightrgb(a,256-y/2.56)
		WritePixelFast(x,y,b)
	Next:Next


	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
End Function

Function shadeup()
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])

	For y=0 To 256
	For x=0 To 256
		a = ReadPixelFast(x,y)
		b = lightrgb(a,100-y/2.56)
		WritePixelFast(x,y,b)
	Next:Next


	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
End Function


Function shadedown()
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])

	For y=0 To 256
	For x=0 To 256
		a = ReadPixelFast(x,y)
		b = lightrgb(a,y/2.56)
		WritePixelFast(x,y,b)
	Next:Next


	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
End Function

Function edgemess()
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])
	Local iw = ImageWidth(gfx\buffer[0])
	Local ih = ImageHeight(gfx\buffer[0])
	For i=0 To 3
	
	For x1 = 1 To 256-1 
	For y1 = 1 To 256-1
	If Rand(1,2) = 1
	a = ReadPixelFast(x1+1,y1)
	b = ReadPixelFast(x1,y1)
	If rgbisblack(b) = True And rgbisblack(a) = False Then
		WritePixelFast(x1,y1,lightrgb(a,99))
	End If
	End If
	Next:Next
	
	;
	For x1 = 256-1 To 1 Step -1
	For y1 = 1 To 256-1
	If Rand(1,2) = 1
	a = ReadPixelFast(x1-1,y1)
	b = ReadPixelFast(x1,y1)
	If rgbisblack(b) = True And rgbisblack(a) = False Then
		WritePixelFast(x1,y1,lightrgb(a,99))
	End If
	End If
	Next:Next
	
	For x1 = 1 To 256-1
	For y1 = 256-1 To 1 Step -1
	If Rand(1,2) = 1
	a = ReadPixelFast(x1,y1-1)
	b = ReadPixelFast(x1,y1)
	If rgbisblack(b) = True And rgbisblack(a) = False Then
		WritePixelFast(x1,y1,lightrgb(a,99))
	End If
	End If
	Next:Next

	For x1 = 1 To 256-1 	 
	For y1 = 1 To 256-1
	If Rand(1,2) = 1
		a = ReadPixelFast(x1,y1+1)
		b = ReadPixelFast(x1,y1)
		If rgbisblack(b) = True And rgbisblack(a) = False Then
			WritePixelFast(x1,y1,lightrgb(a,99))
		End If
	End If
	Next:Next

Next

	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
End Function

Function addedge(in)
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])
	Local iw = ImageWidth(gfx\buffer[0])
	Local ih = ImageHeight(gfx\buffer[0])

	;
	For i=0 To 3
	
	For x1 = 0 To 256-1 
	For y1 = 0 To 256-1
	a = ReadPixelFast(x1+1,y1)
	b = ReadPixelFast(x1,y1)
	If rgbisblack(b) = True And rgbisblack(a) = False Then
		WritePixelFast(x1,y1,lightrgb(a,In))
	End If
	Next:Next
	
	;
	For x1 = 256-1 To 1 Step -1
	For y1 = 1 To 256-1
	a = ReadPixelFast(x1-1,y1)
	b = ReadPixelFast(x1,y1)
	If rgbisblack(b) = True And rgbisblack(a) = False Then
		WritePixelFast(x1,y1,lightrgb(a,In))
	End If
	Next:Next
	
	For x1 = 1 To 256-1
	For y1 = 256-1 To 1 Step -1
	a = ReadPixelFast(x1,y1-1)
	b = ReadPixelFast(x1,y1)
	If rgbisblack(b) = True And rgbisblack(a) = False Then
		WritePixelFast(x1,y1,lightrgb(a,In))
	End If
	Next:Next

	For x1 = 1 To 256-1 	 
	For y1 = 0 To 256-1
	a = ReadPixelFast(x1,y1+1)
	b = ReadPixelFast(x1,y1)
	If rgbisblack(b) = True And rgbisblack(a) = False Then
		WritePixelFast(x1,y1,lightrgb(a,In))
	End If
	Next:Next
		
	
	Next
	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
End Function

Function spotnoise(rvl,rvh)	
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])
	Local iw = ImageWidth(gfx\buffer[0])
	Local ih = ImageHeight(gfx\buffer[0])
	;
	;
	For iii=0 To 1
	xz1 = Rand(0,256-82)
	xy1 = Rand(0,256-82)
	For ii=0 To 2
	xz = xz1 + Rand(0,20)
	yz = xy1 + Rand(0,20)
	For i=0 To 34
	x2 = xz+Rand(0,40)
	y2 = yz+Rand(0,40)
	Select Rand(1,12)
	Case 1
	z = Rand(rvl/1.3,rvh/1.3)
	r = z
	g = z
	b = z
	Default
	z = Rand(rvl,rvh)
	r = 0
	g = 0
	b = 0

	End Select

	If Rand(1,4) = 1
	For x1=0 To 4
	For y1=0 To 4
		If Rand(1,2) = 1 Then 
			If RectsOverlap(x1+x2,y1+y2,1,1,0,0,ImageWidth(gfx\buffer[0]),ImageHeight(gfx\buffer[0])) = True Then
				WritePixelFast x1+x2,y1+y2,getrgb(r,g,b)
			End If
		End If
	Next:Next
	End If	
	Next
	Next
	Next
	;
	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
	
End Function

Function dripfilter2(in)	
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])
	Local iw = ImageWidth(gfx\buffer[0])
	Local ih = ImageHeight(gfx\buffer[0])
	;
	;
	For i=0 To 10024
	x2 = Rand(0,256)
	y2 = Rand(0,256)
	If Rand(1,4) = 1
	For x1=0 To 4
	For y1=0 To 4		
		If RectsOverlap(x1+x2,y1+y2,1,1,0,0,256,256) = True Then
			a = ReadPixelFast(x1+x2,y1+y2)
			WritePixelFast x1+x2,y1+y2,greybrightpixel(a)
		End If
		Next:Next
	End If	
	Next
	;
	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
	
End Function



Function dripfilter(in)	
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])
	Local iw = ImageWidth(gfx\buffer[0])
	Local ih = ImageHeight(gfx\buffer[0])
	;
	;
	For i=0 To 10024
	x2 = Rand(0,256)
	y2 = Rand(0,256)
	Select Rand(1,12)
	Case 1
	r = Rand(150,200)
	g = Rand(40,80)
	b = Rand(40,80)
	Default
	r = Rand(150,200)
	g = 0
	b = 0

	End Select

	If Rand(1,4) = 1
	For x1=0 To 4
	For y1=0 To 4
		If Rand(1,2) = 1 Then 
			If RectsOverlap(x1+x2,y1+y2,1,1,0,0,256,256) = True Then
				a = ReadPixelFast(x1+x2,y1+y2)
				WritePixelFast x1+x2,y1+y2,lightrgb(a,in)
			End If
		End If
	Next:Next
	End If	
	Next
	;
	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
	
End Function



Function dripnoiseint(val)	
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])
	Local iw = ImageWidth(gfx\buffer[0])
	Local ih = ImageHeight(gfx\buffer[0])
	;
	;
	For i=0 To (100000/100*val)
	x2 = Rand(0,256)
	y2 = Rand(0,256)
	Select Rand(1,12)
	Case 1
	z = Rand(150,200)
	r = z+Rand(1,50)
	g = z+Rand(1,50)
	b = z+Rand(1,50)
	Default
	z = Rand(150,200)
	r = z+Rand(1,50)
	g = z+Rand(1,50)
	b = z+Rand(1,50)

	End Select

	If Rand(1,4) = 1
	For x1=0 To 4
	For y1=0 To 4
		If Rand(1,2) = 1 Then 
			If RectsOverlap(x1+x2,y1+y2,1,1,0,0,ImageWidth(gfx\buffer[0]),ImageHeight(gfx\buffer[0])) = True Then
				WritePixelFast x1+x2,y1+y2,getrgb(r,g,b)
			End If
		End If
	Next:Next
	End If	
	Next
	;
	If val = 100 Then
		For x=0 To 256
		For y=0 To 256
		a = ReadPixelFast(x,y)
		If getr(a) < 15 And getg(a) < 15 And getb(a) < 15 Then
		WritePixelFast(x,y,getrgb(240,240,240))
		End If
		Next:Next
		
	End If
	;
	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
	
End Function

Function greyfilter(In)
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])
	For x=0 To 256
	For y=0 To 256
		a = ReadPixelFast(x,y)
		b = greypixel(a)
		WritePixelFast x,y,lightrgb(b,in)
	Next:Next
	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
End Function

Function blackout()
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])
	Local iw = ImageWidth(gfx\buffer[0])
	Local ih = ImageHeight(gfx\buffer[0])

	For x=0 To 256
	For y=0 To 256
	a = ReadPixelFast(x,y)
	If getr(a) < 10 And getg(a)<10 And getb(a)<10 Then
	WritePixelFast(x,y,getrgb(255,255,255))
	Else
;	WritePixelFast(x,y,getrgb(10,10,10))
	End If
	
	Next:Next

	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()

End Function

Function dripnoise()	
	SetBuffer ImageBuffer(gfx\buffer[0])
	LockBuffer ImageBuffer(gfx\buffer[0])
	Local iw = ImageWidth(gfx\buffer[0])
	Local ih = ImageHeight(gfx\buffer[0])
	;
	;
	For i=0 To 10024
	x2 = Rand(0,256)
	y2 = Rand(0,256)
	Select Rand(1,12)
	Case 1
	z = Rand(150,200)
	r = z+Rand(1,50)
	g = z+Rand(1,50)
	b = z+Rand(1,50)
	Default
	z = Rand(150,200)
	r = z+Rand(1,50)
	g = z+Rand(1,50)
	b = z+Rand(1,50)

	End Select

	If Rand(1,4) = 1
	For x1=0 To 4
	For y1=0 To 4
		If Rand(1,2) = 1 Then 
			If RectsOverlap(x1+x2,y1+y2,1,1,0,0,ImageWidth(gfx\buffer[0]),ImageHeight(gfx\buffer[0])) = True Then
				WritePixelFast x1+x2,y1+y2,getrgb(r,g,b)
			End If
		End If
	Next:Next
	End If	
	Next
	;
	UnlockBuffer ImageBuffer(gfx\buffer[0])
	SetBuffer BackBuffer()
	
End Function



Function inigfx()
	gfx\buffer[0] = CreateImage(640,480)
	gfx\buffer[1] = CreateImage(640,480)
	gfx\buffer[2] = CreateImage(640,480)
	gfx\buffer[3] = CreateImage(640,480)
End Function

Function GetRGB(r,g,b)
	Return b Or (g Shl 8) Or (r Shl 16)
End Function

Function GetR(rgb)
    Return rgb Shr 16 And %11111111
End Function

Function GetG(rgb)
	Return rgb Shr 8 And %11111111
End Function

Function GetB(rgb)
	Return rgb And %11111111
End Function

Function rgbisblack(rgb)
	If getr(rgb) = 0 And getg(rgb) = 0 And getb(rgb) = 0 Then Return True
End Function

Function lightrgb(rgb,perc#)
	
	Local r# = getr(rgb)
	Local g# = getg(rgb)
	Local b# = getb(rgb)
	
	Local r1# = r / 100 * perc
	Local g1# = g / 100 * perc
	Local b1# = b / 100 * perc
	
	If r1 > 255 Then r1 = 255
	If g1 > 255 Then g1 = 255
	If b1 > 255 Then b1 = 255
	
	If r1 < 0 Then r1 = 0
	If g1 < 0 Then g1 = 0
	If b1 < 0 Then b1 = 0
	
	Return getrgb(r1,g1,b1)
			
End Function



Function greybrightpixel(rgb)
	;Grayscale
	;By pexe
	GS_pix = rgb

	GS_r% = (GS_pix Shr 16) And $ff ;\
	GS_g% = (GS_pix Shr 8) And $ff  ;  Transform values
	GS_b% = GS_pix And $ff          ;/

	GS_v% = GS_r+GS_g+GS_b
	GS_v% = GS_v/3

	GS_pix=(GS_v Or (GS_v Shl 8) Or (GS_v Shl 16) Or ($ff000000)) ;Put values back
	a# = getr(gs_pix)
	b# = getg(gs_pix)
	c# = getb(gs_pix)	
	a=a*1.5
	b=b*1.2
	If a>255 Then a=255
	If b>255 Then b=255
	gs_pix = getrgb(a,b,c)
	Return GS_pix
End Function
Function greypixel(rgb)
	;Grayscale
	;By pexe
	GS_pix = rgb

	GS_r% = (GS_pix Shr 16) And $ff ;\
	GS_g% = (GS_pix Shr 8) And $ff  ;  Transform values
	GS_b% = GS_pix And $ff          ;/

	GS_v% = GS_r+GS_g+GS_b
	GS_v% = GS_v/3

	GS_pix=(GS_v Or (GS_v Shl 8) Or (GS_v Shl 16) Or ($ff000000)) ;Put values back
	Return GS_pix
End Function
