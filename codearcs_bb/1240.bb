; ID: 1240
; Author: Adren Software
; Date: 2004-12-18 15:50:51
; Title: MouseHover()
; Description: Commands for checking to see if the mouse is over an image

Function MouseHover(image,x,y)

If RectsOverlap(x,y,ImageWidth(image),ImageHeight(image),MouseX(),MouseY(),12,21) Then
	Return True
Else
	Return False
EndIf

End Function

Function MouseClick(image,x,y,button=1)

If RectsOverlap(x,y,ImageWidth(image),ImageHeight(image),MouseX(),MouseY(),12,21) And MouseHit(button) Then
	Return True
Else
	Return False
EndIf

End Function
