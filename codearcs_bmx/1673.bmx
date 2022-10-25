; ID: 1673
; Author: drnmr
; Date: 2006-04-14 13:45:08
; Title: DrawTriangle()
; Description: Draws an unfilled triangle.

Function DrawTriangle(x:Int,y:Int,length:Int,height:Int)
	toppointx = length/2+x'top point y is just y
	leftpointy = y+height'left point x is just x
	rightpointx = x+length
	rightpointy = y+height
	DrawLine toppointx,y,x,leftpointy
	DrawLine x,leftpointy,rightpointx,rightpointy
	DrawLine rightpointx,rightpointy,toppointx,y
EndFunction
