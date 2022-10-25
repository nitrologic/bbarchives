; ID: 1882
; Author: ninjarat
; Date: 2006-12-17 08:21:40
; Title: Dissolve Effect
; Description: Makes dissolve effect by converting the screen to tiles and then shrinking.

Global imgs:TImage[20,15]
initfadeout()

'your code goes here

Function initfadeout()
	For j=0 To 19
		For k=0 To 14
			imgs[j,k]=CreateImage(GraphicsWidth()/20,GraphicsHeight()/15,1,DYNAMICIMAGE)
			MidHandleImage imgs[j,k]
		Next
	Next
End Function
	
Function fadeout()
	For j=0 To 19
		For k=0 To 14
			GrabImage imgs[j,k],j*(GraphicsWidth()/20),k*(GraphicsHeight()/15)
		Next
	Next
	
	For i#=1 To 0 Step -.015
		Cls
		SetTransform (1-i)*180,i,i
		For j=0 To 19
			For k=0 To 14
				x=j*(GraphicsWidth()/20); y=k*(GraphicsHeight()/15)
				xh=GraphicsWidth()/40; yh=GraphicsHeight()/30
				DrawImage imgs[j,k],x+xh,y+yh
			Next
		Next
		SetHandle 0,0
		SetTransform 0,1,1
		Flip
	Next
End Function
