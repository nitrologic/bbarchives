; ID: 2884
; Author: JBR
; Date: 2011-08-23 14:27:32
; Title: Filled Rotated Ellipses
; Description: Plots filled rotated ellipses

; SPEEDY ROTATED ELLIPSE PLOTTING

Graphics3D 800,600,32,2
SetBuffer BackBuffer()

Global G_Screen_Width = GraphicsWidth()
Global G_Screen_Hight = GraphicsHeight()

Dim a_x%(90), a_y%(90), left_side%(G_Screen_Hight-1), rite_side%(G_Screen_Hight-1)


Repeat

	Cls
	
	G_Angle# = Rnd(0,360)
		
	time = MilliSecs()

	For ellipse = 1 To 1000
		create_ellipse( 0,0, 100.0, 50.0, G_Angle#, 255,0,255 )
	Next
	
	time = MilliSecs()-time

	Flip
	
	Color 255,255,255
	Text 0,0,"Time to draw 1000 :"+time+" millisecs"
	Text 0,12,"Press Space"
	Flip
	WaitKey()
		
Until KeyDown(1)

Flip
FlushKeys()
WaitKey()
End






;-----------------------------------------------------------------------------------------
Function create_ellipse( centre_x#, centre_y#, radius_x#, radius_y#, angle#, red,gre,blu )

	Local cos_a# = Cos( angle# )
	Local sin_a# = Sin( angle# )
	
	For angle# = 0 To 90
		Local x# = radius_x# * Cos(angle#*4.0)			; simple ellipse
		Local y# = radius_y# * Sin(angle#*4.0)
		
		Local rot_x# = (x#*cos_a#) - (y#*sin_a#)		; rotated ellipse
		Local rot_y# = (x#*sin_a#) + (y#*cos_a#)
		
		a_x%( angle# ) = G_Screen_Width/2 + centre_x# + rot_x#		; adjusted for origin in middle of screen
		a_y%( angle# ) = G_Screen_Hight/2 + centre_y# + rot_y#
	Next

	For i = 0 To G_Screen_Hight-1					; init the left/rite sides to impossible values
		left_side(i) = 1000000
		rite_side(i) = -1000000
	Next
	
	For lline = 0 To 90-1							; draw the lines
		Local x0 = a_x%(lline)
		Local y0 = a_y%(lline)
		Local x1 = a_x%(lline+1)
		Local y1 = a_y%(lline+1)
		
		If x0<0 Then x0=0							; clipping left/top
		If y0<0 Then y0=0
		If x1<0 Then x1=0
		If y1<0 Then y1=0
		
		If x0>=G_Screen_Width Then x0=G_Screen_Width-1		;clipping rite/bottom
		If y0>=G_Screen_Hight Then y0=G_Screen_Hight-1
		If x1>=G_Screen_Width Then x1=G_Screen_Width-1
		If y1>=G_Screen_Hight Then y1=G_Screen_Hight-1

;		Function DrawLine(x0, y0, x1, y1)
		Local dx,dy,sx,sy,err,e2
		dx = Abs(x1-x0)
		dy = Abs(y1-y0) 
		If x0 < x1 Then sx = 1 Else sx = -1
		If y0 < y1 Then sy = 1 Else sy = -1
		err = dx-dy
 
		Repeat
			If x0 < left_side(y0) Then left_side(y0) = x0
			If x0 > rite_side(y0) Then rite_side(y0) = x0
			
			If x0 = x1 And y0 = y1 Exit
			e2 = 2*err
			If e2 > -dy Then 
				err = err - dy
				x0 = x0 + sx
			End If
			If e2 <  dx Then 
				err = err + dx
				y0 = y0 + sy 
			End If
		Forever

	Next
	
	Color red,gre,blu

	For scan_line = 0 To G_Screen_Hight-1			; now scan the lines
		Local x_left = left_side(scan_line)
		Local x_rite = rite_side(scan_line)
		Local width  = x_rite-x_left
		
		If width > 0 Then
			Rect x_left, scan_line, width, 1
		EndIf

	Next
	
End Function
