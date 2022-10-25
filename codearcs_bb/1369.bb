; ID: 1369
; Author: WarpZone
; Date: 2005-05-09 17:06:04
; Title: DrawCircularGradient function
; Description: Draws a greyscale gradient oval

Graphics3D 1024,768,32,2
SetBuffer BackBuffer()

Repeat
h#=Rand (256)
w#=Rand (256)
x#=Rand (256)
y#=Rand (256)
t1=MilliSecs()
DrawCircularGradient(x#,y#,h#,w#)
t2=MilliSecs()
Color 255,255,255
Text 0,580,"New gradient created.  Size is "+x#+" by "+y#+". Time To render:"+(t2-t1)+" ms"
Flip()
WaitKey()
Color 0,0,0
Text 0,580,"New gradient created.  Size is "+x#+" by "+y#+". Time To render:"+(t2-t1)+" ms"
FreeImage img

Until KeyHit(1)

Function DrawCircularGradient(x#,y#,width#,height#)
For rings=1 To width#
If Width < Height Then greyness=((width-(rings*2))/(width# / 256)) Else greyness=((height-(rings*2))/(height# / 256))
Color greyness,greyness,greyness
Oval (x#+rings,y#+rings,width#-(rings*2),height#-(rings*2),1)
Next
Text 0,0, (width# / 256)
End Function
