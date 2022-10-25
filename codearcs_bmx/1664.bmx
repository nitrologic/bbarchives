; ID: 1664
; Author: Oddball
; Date: 2006-04-12 14:40:24
; Title: DrawPoly(), filled or unfilled
; Description: Draw filled or unfilled polygons at arbitrary locations, and with the correct scale and rotation.

Function DrawPoly( xy:Float[], fill:Int=True, x:Float=0, y:Float=0 )
	Local origin_x:Float
	Local origin_y:Float
	GetOrigin origin_x,origin_y
	Local handle_x:Float
	Local handle_y:Float
	GetHandle handle_x,handle_y
	
	If fill
		_max2dDriver.DrawPoly xy,..
		-handle_x,-handle_y,..
		x+origin_x,y+origin_y
	Else
		Local x1:Float=xy[xy.Length-2]
		Local y1:Float=xy[xy.Length-1]
		For Local i:Int=0 Until Len xy Step 2
			Local x2:Float=xy[i]
			Local y2:Float=xy[i+1]
			_max2dDriver.DrawLine..
			-handle_x+x1,-handle_y+y1,..
			-handle_x+x2,-handle_y+y2,..
			x+origin_x-0.5,y+origin_y-0.5
			x1=x2
			y1=y2
		Next
	EndIf
End Function
