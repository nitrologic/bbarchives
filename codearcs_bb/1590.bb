; ID: 1590
; Author: Phoenix
; Date: 2006-01-05 06:39:18
; Title: Creating circles with a certain amount of corners
; Description: This function will create a circle with a variable amount of corners

;Circles with variable corners
;Written 5th January 2006 by Phoenix
;************************************************
Circle(100,100,100,8)
WaitKey()

Function Circle(x,y,radius,corners)
	If corners = 0 Then Return 0 ;Check so that we don't divide by 0

	For i = 0 To corners
		Line radius*Sin(360/corners*i)+x,radius*Cos(360/corners*i)+y,radius*Sin(360/corners*(i+1))+x,radius*Cos(360/corners*(i+1))+y
	Next
End Function
