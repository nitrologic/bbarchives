; ID: 2406
; Author: Nate the Great
; Date: 2009-02-04 01:20:58
; Title: Very cool rain/water efect on cam
; Description: kind of a work around for raytracing

;VERY cool magnifying glass demo.

Graphics 640,480,0,2
HidePointer()
img = CreateImage(640,480)
SetBuffer ImageBuffer(img)
SeedRnd(22432)
For x = 1 To 9000
Color Rnd(255),Rnd(255),Rnd(255)
Rect Rnd(640),Rnd(480),Rnd(20),Rnd(20),1
Next




Global dimg = CreateImage(150,150)


SetBuffer BackBuffer()


While Not KeyDown(1)
Cls

DrawImage img,(-(MouseX())+320)/3,(-(MouseY()/1)+240)/3
drop(MouseX(),MouseY(),50,1)



Flip False
Wend
End


Function drop(x,y,r,s# = 1)

LockBuffer ImageBuffer(dimg)
LockBuffer BackBuffer()

For x1 = -r To r
	For y1 = -r To r
		dist# = Sqr(x1*x1 + y1*y1)
		If dist# <= r Then
			
			WritePixelFast1 x1+r,y1+r,ReadPixelFast1(x1*(dist#/r*s#)+x,y1*(dist#/r*s#)+y),ImageBuffer(dimg)
		EndIf
	Next
Next


UnlockBuffer BackBuffer()
UnlockBuffer ImageBuffer(dimg)
Color 0,0,0
Oval x-r+1,y-r+1,2*r-1,2*r-1,1
DrawImage dimg,x-r,y-r

SetBuffer ImageBuffer(dimg)
Color 0,0,0
Rect 0,0,151,151,1
SetBuffer BackBuffer()

End Function


Function writepixelfast1(x,y,col,buffer)

If x > -1 And y > -1 Then
	If x < GraphicsWidth()-1 And y < GraphicsHeight()-1 Then
		WritePixelFast x,y,col,buffer
	EndIf
EndIf

End Function


Function readpixelfast1(x,y)

If x > 0 And y > 0 Then
	If x < GraphicsWidth() And y < GraphicsHeight() Then
		Return(ReadPixelFast(x,y))
	EndIf
EndIf

Return 0

End Function
