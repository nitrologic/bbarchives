; ID: 1674
; Author: drnmr
; Date: 2006-04-14 14:11:57
; Title: DrawLineRect()
; Description: Draws the outline of a rectangle.

Function DrawLineRect(x:Int,y:Int,length:Int,height:Int)
	lowerlefty = y+height
	lowerrightx = x+length
	lowerrighty = y+height
	upperrightx = x+length
	DrawLine x,y,x,lowerlefty
	DrawLine x,lowerlefty,lowerrightx,lowerrighty
	DrawLine lowerrightx,lowerrighty,upperrightx,y
	DrawLine upperrightx,y,x,y
EndFunction
