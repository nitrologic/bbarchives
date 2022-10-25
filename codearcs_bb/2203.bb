; ID: 2203
; Author: Nebula
; Date: 2008-01-20 16:27:19
; Title: Push Ai - move baddies away
; Description: Move screen parts with mouse

; Push ai reaction ; Pushes ai away using a distance value


Graphics 640,480,16,2
SetBuffer BackBuffer()

Type aies
	Field x,y

End Type

Global ox , oy
For i=0 To 10
	this.aies = New aies
	this\x = Rand(320)
	this\y = Rand(320)
Next

Global Lastx,lasty,diffx,diffy
While KeyDown(1) = False
	Cls
	Rect MouseX(),MouseY(),12,12
	diffx = MouseX()-lastx
	diffy = MouseY()-lasty
	drawaies
	moveaies
	lastx = MouseX()
	lasty = MouseY()
	Flip
Wend
End

Function moveaies()
	Local xv#,yv#
	For this.aies = Each aies
		If dist(MouseX(),MouseY(),this\x,this\y) < 40 Then
			this\x = this\x + diffx
			this\y = this\y + diffy			
			Else
		End If
	Next
	
End Function

Function drawaies()
For this.aies = Each aies
	Oval this\x,this\y,10,10
Next

End Function

Function dist(x1,y1,x2,y2)
	Return Sqr((x1-x2)^2+(y1-y2)^2)
End Function
