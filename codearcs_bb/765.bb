; ID: 765
; Author: Nebula
; Date: 2003-08-17 18:44:22
; Title: Isometric collision
; Description: Detects collision in a isometric square

;
; Simple 2D Isometric collision routine - By Nebula
;

;
; Usage : 	The input coordinates for the isocollision funtion need to be under the size
; 			of the isometric square. x<64 and y<32. 
;
;			The isocollision function returns 1=true if the mouse is inside a iso square and
;			returns higher numbers if it is in one of the outside corners. lt=2,rt=3,lb=4,rb=5.
;			
;
;


Graphics 640,480,16,2

HidePointer()

Repeat
	Cls
	drawiso(320,150)
	;	
	Plot MouseX(),MouseY()
	;
	Text 0,0,Str(MouseX()-320) + " : " + Str(MouseY()-150)
	;
	a = isocollision((MouseX()-320),MouseY()-150)
	If a > 0 Then
		If a = 1 Then
			Text 0,32,"Collision !!!"
		End If
		If a > 1 Then Text 0,32,"Border :   " + a
	End If

	Flip
Until KeyDown(1)


End

;
; My Latest iso collision algorithm using Cos and Sin to rotate the mouse
; coordinates and check for simple rectangular collision.
;
; Tested on 64*32 iso tiles and 128*64. Still needs a little
; tweaking
Function RRectcollision(x,y,w,h,rot#)
	a = ((((Cos(rot)*((x/2)-(w/2)/2.5) )  + Sin(rot) * (y-h/2.5))+(w/2))*1.4)-h
	b = (((-Sin(rot) * ((x/2)-(w/2)/2.5) ) + Cos(rot)	*(y-h/2.5)+h) * 1.4)-3
	If a< 0 Or a> w/2 Or b<h Or b>h+h Then Return True
	Return False
End Function


;
; This function returns :
;                           1 - If the mouse is inside a isometric square
;							2 - Left top just outside of the square
;							3 - right top just outside of the square
;							4 - Left bottom just outside of the square
;							5 - right bottom just outside of the square
;
Function isocollision(x1,y1)
If (x1<0 Or x1>64) Or (y1<0 Or y>32) Then Return
For y=0 To 32
	For l=0 To c			
		If ((x1 = (-c+32+l)) Or (x1 = (c+32-l))) And (y1 = y) Then Return True
	Next	
	If y=>16 Then c=c-2 Else c=c+2
Next
If x1<64 And y1<32 Then	Return ((y1/16*2)+x1/32)+2
End Function

;
; Draw a simple isometric square
;
Function drawiso(x1,y1)
Rect x1,y1,64,32,0
Line x1+31,y1,x1+61,y1+16
Line x1+31,y1,x1,y1+16
Line x1+31,y1+31,x1+63,y1+16
Line x1+31,y1+31,x1,y1+16
End Function
