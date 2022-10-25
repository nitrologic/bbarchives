; ID: 180
; Author: Popstar
; Date: 2002-01-10 07:43:23
; Title: Bayer-Palbo Ordered Dithering
; Description: A modified 8x8 Bayer ordered dither algorithm

;-------------------------------;
;Bayer-Palbo Ordered Dithering  ;
;-------------------------------;
;A modified Bayer-method        ;
;ordered dithering algorithm.   ;
;-------------------------------;
;Apart from the normal ordered  ;
;dithering, this version also   ;
;adds contrast and minor error  ;
;diffusion.                     ;
;Try changing pct and ErrDif to ;
;find the best result.          ;
;-------------------------------;

Global B_Method=4

AppTitle "Bayer-Palbo Dithering Algorithm"
SeedRnd MilliSecs()
Graphics3D 1,1,32,2

pct=90      ;Try changing this value (0...100)
ErrDif=10   ;Try changing this value (0...)

If pct>80 Then B_Method=8

resolution=446/100.0*pct
Dim pattern(B_Method-1,B_Method-1)
Dim c_table(resolution)

infile$="TEST.JPG"

in_image=LoadImage(infile$)

width=ImageWidth(in_image)
height=ImageHeight(in_image)

Graphics3D width,height,32,2

in_image=LoadImage(infile$)
out_image=CreateImage (ImageWidth(in_image),ImageHeight(in_image))
show=True

Setup(resolution)
BPDither(in_image,out_image,resolution,ErrDif,show)
End

;--Data-----------------------------------

.bayer4
Data  0,  8,  2, 10
Data 12,  4, 14,  6
Data  3, 11,  1,  9
Data 15,  7, 13,  5

.bayer8
Data  0, 32,  8, 40,  2, 34, 10, 42   ; 8x8 Bayer ordered dithering
Data 48, 16, 56, 24, 50, 18, 58, 26   ; pattern. Each Input pixel
Data 12, 44,  4, 36, 14, 46,  6, 38   ; is scaled To the 0..63 range
Data 60, 28, 52, 20, 62, 30, 54, 22   ; Before looking in this table
Data  3, 35, 11, 43,  1, 33,  9, 41   ; To determine the action.
Data 51, 19, 59, 27, 49, 17, 57, 25
Data 15, 47,  7, 39, 13, 45,  5, 37
Data 63, 31, 55, 23, 61, 29, 53, 21

;--Functions------------------------------

Function Setup(resolution)

If B_Method=4
	
	Restore bayer4
	
	For y=0 To 3
	For x=0 To 3
		Read num
		pattern(x,y)=num
	Next
	Next
	
Else
	
	Restore bayer8
	
	For y=0 To 7
	For x=0 To 7
		Read num
		pattern(x,y)=num
	Next
	Next

EndIf

v#=180.0
k#=resolution
deg#=v#/k#

;Store the Cos() calculation in an array to reduce time.

If B_Method=4

	For value=0 To resolution 
		val#=(16+(Cos(value*deg#)*16))/2
		c_table(value)=Abs (val#-16)
	Next
	
Else
	
	For value=0 To resolution 
		val#=(64+(Cos(value*deg#)*64))/2
		c_table(value)=Abs (val#-64)
	Next

End If

End Function

;-----------------------------------------

Function BPDither(in_image,out_image,resolution,ErrDif,show)

a#=resolution/446.0
newres#=(446.0-resolution)/2

SetBuffer BackBuffer()
If show=True Then DrawImage in_image,0,0
Flip

mil1=MilliSecs()

For y=0 To ImageHeight(in_image)-1
For x=0 To ImageWidth(in_image)-1
	
	If KeyDown(1)=1 Then End
	
	SetBuffer ImageBuffer (in_image)
	LockBuffer ImageBuffer(in_image)
	cval=ReadPixelFast (x,y, ImageBuffer(in_image))
	cval=cval And $FFFFFF
	UnlockBuffer ImageBuffer(in_image)
	
	red=cval/256/256
	green=(cval-(red*256*256))/256
	blue=cval-(red*256*256)-(green*256)
	
	val#=((0.5*Red)+Green+(0.25*Blue))
	
	If val#<newres# : val#=0 : Goto jump : End If
	If val#>446-newres# Then val#=resolution+newres#
	
	nv=val#-newres#
	nv=nv+Rnd(-(ErrDif/2),(ErrDif/2))

	If nv<0 Then nv=0
	If nv>resolution Then nv=resolution
	
	If B_Method=4
		
		Xx=x And 3
		Yy=y And 3
		
	Else
		
		Xx=x And 7
		Yy=y And 7
		
	End If	
	
	If c_table(nv)>pattern (Xx,Yy)
		SetBuffer ImageBuffer(out_image)
		LockBuffer ImageBuffer(out_image)
		WritePixelFast x,y,$000000FFFFFF,ImageBuffer(out_image)
		UnlockBuffer ImageBuffer(out_image)
	End If
	.jump
Next
Next

mil2=MilliSecs()

DebugLog "Seconds: "+(mil2-mil1)/1000

If show=True Then FlipScreens(in_image,out_image)

End Function

;-----------------------------------------

Function FlipScreens(in_image,out_image)

While KeyHit(1)=0

SetBuffer BackBuffer()
DrawBlock out_image,0,0
Flip
WaitKey()

If KeyDown(1)=1 Then End

SetBuffer BackBuffer()
DrawBlock in_image,0,0
Flip
WaitKey()
Wend

End Function

;-----------------------------------------
