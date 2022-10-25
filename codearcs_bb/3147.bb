; ID: 3147
; Author: Pakz
; Date: 2014-10-14 06:13:32
; Title: Midpoint Displacement Algorithm
; Description: For making heightmaps and such

; Midpoint Displacement
Graphics 640,480,32,2
SetBuffer BackBuffer()

Global mapwidth = 100
Global mapheight = 100
Dim map(mapwidth,mapheight)

domidpoint
tw = GraphicsWidth()/mapwidth
th = GraphicsHeight()/mapheight
For y=0 To mapheight
For x=0 To mapwidth
	Color map(x,y),map(x,y),map(x,y)
	Rect x*tw,y*th,tw,th,True
Next
Next

Flip
WaitKey
End

; Here the midpoint code begins.
Function domidpoint()
  ; Erase the old map array..
  For y = 0 To mapheight
  For x = 0 To mapwidth
    map(x,y)=0
  Next
  Next
  ; Setup points in the 4 corners of the map.
  map(0,0) = 128
  map(mapwidth,0) = 128
  map(mapwidth,mapheight) = 128
  map(0,mapheight) = 128
  ; Do the midpoint
  midpoint(0,0,mapwidth,mapheight)
End Function 

 ; This is the actual Mid point displacement code.
 Function midpoint(x1,y1,x2,y2)
   	; If this is pointing at just on pixel, Exit because
 	; it doesn't need doing}
   	If x2-x1<2 And y2-y1<2 Then Return False

 	; Find distance between points And
  	;use when generating a random number.
    dist=(x2-x1+y2-y1)
    hdist=dist / 2
  	;Find Middle Point
    midx=(x1+x2) / 2
    midy=(y1+y2) / 2
  	;Get pixel colors of corners
    c1=map(x1,y1)
    c2=map(x2,y1)
    c3=map(x2,y2)
    c4=map(x1,y2)

  	; If Not already defined, work out the midpoints of the corners of
  	; the rectangle by means of an average plus a random number.
    If(map(midx,y1)=0) Then map(midx,y1)=((c1+c2+Rand(dist)-hdist) / 2)
    If(map(midx,y2)=0) Then map(midx,y2)=((c4+c3+Rand(dist)-hdist) / 2)
    If(map(x1,midy)=0) Then map(x1,midy)=((c1+c4+Rand(dist)-hdist) / 2)
    If(map(x2,midy)=0) Then map(x2,midy)=((c2+c3+Rand(dist)-hdist) / 2)

  	; Work out the middle point...
    map(midx,midy) = ((c1+c2+c3+c4+Rand(dist)-hdist)/4)

  	; Now divide this rectangle into 4, And call again For Each smaller
  	; rectangle
    midpoint(x1,y1,midx,midy);
    midpoint(midx,y1,x2,midy);
    midpoint(x1,midy,midx,y2);
    midpoint(midx,midy,x2,y2);

End Function
