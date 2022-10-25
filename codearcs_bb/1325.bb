; ID: 1325
; Author: Rob Farley
; Date: 2005-03-13 08:49:40
; Title: Seamless tile creator
; Description: Create seamless tiles from an image

; Seamless texture creator.
; 2005 MentalIllusion.
; http://www.mentalillusion.co.uk
;
;
; This is not designed to take a texture and make it seamless. It creates a texture from
; a complete image, ie, if you've got a picture of a lawn you load the lawn into this,
; pick the area you want to turn into a texture and it does the rest.

; Once you've picked the area it will save out a BMP file called tile_[yourfilename].bmp




; Please add to and improve this code!
;
;
; A few bits need doing to improve this:
; * contrast needs to increase on the blendy bits
; * Front end
; * Able to use images bigger than the screen



Graphics 1024,768,32,1

font = LoadFont("Arial.ttf",20,True)
SetFont font

filename$="[your image.jpg]"

i = LoadImage(filename)

size = 256
hsize = size/2

; set this to adjust the overhang
qsize = size/4

; set this to make it noiser!
; The noise variable makes the blends imperfect, however, it tends to make it look better
; have a play with this value, a value of 1 will just give a dither pattern which will look crap.
; .1 or .2 on most things looks pretty good, if you're doing grass or something that's almost a
; noise pattern anyway push it up to about .5... basically have a play.
noise# = .2

; RGB Functions
Global gotr#=0
Global gotg#=0
Global gotb#=0

SetBuffer BackBuffer()

Repeat
	x=MouseX()
	y=MouseY()
	
	If x-hsize<0 Then x=hsize
	If x+hsize+qsize>ImageWidth(i) Then x = ImageWidth(i)-hsize-qsize
	If y-hsize<0 Then y=hsize
	If y+hsize+qsize>ImageHeight(i) Then y = ImageHeight(i)-hsize-qsize
	
	Color c,0,0
	c=(c+10) Mod 256
	DrawBlock i,0,0
	Rect x-hsize,y-hsize,size+qsize,size+qsize,False
	Color 0,c,0
	Rect x-hsize,y-hsize,size,size,False
	oText MouseX(),MouseY(),"X",True,True
	oText GraphicsWidth()/2,10,"Select area for tile",True
	Flip


Until MouseDown(1)

x=x - hsize
y=y - hsize

j = CreateImage(size+qsize,size+qsize)
CopyRect x,y,size+qsize,size+qsize,0,0,ImageBuffer(i),ImageBuffer(j)
k = CreateImage(size,size)

LockBuffer ImageBuffer(i)
LockBuffer ImageBuffer(j)

; sort left hand edge from right hand stuff
For yy=0 To size+qsize
For xx=0 To qsize
	; create a bit of noise to make it imperfect
	disturb# = Rnd(-noise,noise)
	offset# = Float(xx) / Float(qsize) + disturb
	If offset>1 Then offset = 1
	If offset<0 Then offset = 0
	noffset# = 1 - offset
	
	GetRGB(i,x+size+xx,y+yy)
	r1# = gotr * noffset
	g1# = gotg * noffset
	b1# = gotb * noffset
	
	GetRGB(j,xx,yy)
	r2# = gotr * offset
	g2# = gotg * offset
	b2# = gotb * offset
	
	r# = r1 + r2
	g# = g1 + g2
	b# = b1 + b2
	
	WriteRGB(j,xx,yy,r,g,b)
Next
Next

UnlockBuffer ImageBuffer(i)
UnlockBuffer ImageBuffer(j)

CopyRect 0,0,size,size,0,0,ImageBuffer(j),ImageBuffer(k) 

LockBuffer ImageBuffer(j)
LockBuffer ImageBuffer(k)

; sort top edge from stuff below
For xx=0 To size
For yy=0 To qsize
	; create a bit of noise to make it imperfect
	disturb# = Rnd(-noise,noise)
	offset# = Float(yy) / Float(qsize) + disturb
	If offset>1 Then offset = 1
	If offset<0 Then offset = 0
	noffset# = 1 - offset
	
	GetRGB(j,xx,yy+size)
	r1# = gotr * noffset
	g1# = gotg * noffset
	b1# = gotb * noffset
	
	GetRGB(k,xx,yy)
	r2# = gotr * offset
	g2# = gotg * offset
	b2# = gotb * offset
	
	r# = r1 + r2
	g# = g1 + g2
	b# = b1 + b2
	
	WriteRGB(k,xx,yy,r,g,b)
Next
Next

UnlockBuffer ImageBuffer(j)
UnlockBuffer ImageBuffer(k)

SaveImage(k,"tile_"+Left(filename,Instr(filename,"."))+"bmp")


Cls
For x=0 To (GraphicsWidth()/size)+1
For y=0 To (GraphicsHeight()/size)+1
DrawBlock k,x*size,y*size
Next
Next
oText GraphicsWidth()/2+x,GraphicsHeight()/2+y,"Tile saved, Press any Key",True

Flip
WaitKey

Function Otext(xp,yp,t$,xcen=False,ycen=False)

Color 0,0,0
For x=-2 To 2
For y=-2 To 2
	Text xp+x,yp+y,t$,xcen,ycen
Next
Next
Color 255,255,255
Text xp,yp,t$,xcen,ycen

End Function


Function WriteRGB(image_name,x,y,red,green,blue)
; Writes a pixel to an image.
; The imagebuffer needs to be locked as it does a write pixel fast.
argb=(blue Or (green Shl 8) Or (red Shl 16) Or ($ff000000))
WritePixelFast x,y,argb,ImageBuffer(image_name)
End Function

Function GetRGB(image_name,x,y)
; Gets the RGB components from an image.
; The imagebuffer needs to be locked as it does a read pixel fast.
; The components are put into the global varibles gotr, gotg and gotb
	argb=ReadPixelFast(x,y,ImageBuffer(image_name))
	gotr=(ARGB Shr 16) And $ff 
	gotg=(ARGB Shr 8) And $ff 
	gotb=ARGB And $ff
End Function
