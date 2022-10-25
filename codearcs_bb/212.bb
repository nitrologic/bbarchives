; ID: 212
; Author: Nigel Brown
; Date: 2002-02-01 02:54:52
; Title: 2D Rectangle Collide (FAST)
; Description: collision detection between two rectangles

Graphics	640,480,16,2
SetBuffer	BackBuffer()

xpos1% = 100:ypos1% = 100
xpos2% = 200:ypos2% = 200

width%=10	:height%=10


;---------------------------------------------------------------------
Repeat
;---------------------------------------------------------------------

	; Check for move up.
	If KeyDown(200)
		ypos1% = ypos1% - 1
	EndIf
		
	; Check for move down.
	If KeyDown(208)
		ypos1% = ypos1% + 1
	EndIf

	; Check for move left.
	If KeyDown(203)
		xpos1% = xpos1% - 1
	EndIf
		
	; Check for move right
	If KeyDown(205)
		xpos1% = xpos1% + 1
	EndIf

	If CheckCollide( xpos1%, ypos1%, xpos2%, ypos2%, width%, height% )
		ClsColor 255,0,0
	Else
		ClsColor	0,0,0
	EndIf
	
	Cls
	
	Rect	xpos1%,ypos1%,10,10,1
	Rect	xpos2%,ypos2%,10,10,1

	Flip
	
	Delay 50

Until KeyDown(1)
End


;---------------------------------------------------------------------
Function CheckCollide%( x1%, y1%, x2%, y2%, width%, height% )
;---------------------------------------------------------------------

	If Abs( x1% - x2% ) < width% And Abs( y1% - y2% ) < height%  Return True
		
	Return False

End Function
