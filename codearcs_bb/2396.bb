; ID: 2396
; Author: Yasha
; Date: 2009-01-22 17:30:00
; Title: Perturbation filter
; Description: Shmoosh an image!

Function Perturb(img,pbase,magn#)		;Map to perturb, noise map, magnitude

xsize=ImageWidth(img)
ysize=ImageHeight(img)
pmap=CreateImage(xsize,ysize)
LockBuffer ImageBuffer(img)
LockBuffer ImageBuffer(pbase)
LockBuffer ImageBuffer(pmap)

For x=0 To xsize-1
For y=0 To ysize-1
	p#=magn*(((ReadPixelFast(x,y,ImageBuffer(pbase)) And $000000ff)-128)/255.0)
	
	xcoord#=x+(xsize*p)
	xclo=Floor(xcoord):xcl2=refinc(xclo,0,xsize-1)
	xchi=Ceil(xcoord):xch2=refinc(xchi,0,xsize-1)
	xfrac#=xcoord-xclo
	
	ycoord#=y+(ysize*p)
	yclo=Floor(ycoord):ycl2=refinc(yclo,0,ysize-1)
	ychi=Ceil(ycoord):ych2=refinc(ychi,0,ysize-1)
	yfrac#=ycoord-yclo
		
	val1#=linpol(ReadPixelFast(xcl2,ycl2,ImageBuffer(img)) And $000000ff,ReadPixelFast(xch2,ycl2,ImageBuffer(img)) And $000000ff,xfrac)
	val2#=linpol(ReadPixelFast(xcl2,ych2,ImageBuffer(img)) And $000000ff,ReadPixelFast(xch2,ych2,ImageBuffer(img)) And $000000ff,xfrac)
	
	val3=linpol(val1,val2,yfrac)
	
	WritePixelFast(x,y,val3 Or (val3 Shl 8) Or (val3 Shl 16),ImageBuffer(pmap))
		
Next
Next

UnlockBuffer ImageBuffer(img)
UnlockBuffer ImageBuffer(pbase)
UnlockBuffer ImageBuffer(pmap)

Return pmap

End Function

Function refinc(x,min,max)	;If x is outside bounds, "reflects" it back in eg. refinc(130,0,100)=70

If x<min Then Return min+(min-x)
If x>max Then Return max-(x-max)
Return x

End Function

Function linpol#(a#,b#,x#)				;Linear interpolation
	Return (a*(1-x))+(b*x)
End Function
