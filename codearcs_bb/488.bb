; ID: 488
; Author: semar
; Date: 2002-11-15 02:37:44
; Title: Line With Width
; Description: Draws a line with the desired  width

;draw line with width - by semar
Graphics 640,480,0,2
SetBuffer BackBuffer()

;initial point and angle settings
x1 = GraphicsWidth()/2
y1 = GraphicsHeight()/2
x2 = x1 + 100
y2 = y1 + 100

LINE_WIDTH = 5 ;width of the line

;until esc is pressed
While Not KeyDown(1)

;clear the screen
Cls

If MouseHit(1) Then
	;gets the second point with a left mouse click
	x2 = MouseX()
	y2 = MouseY()
EndIf	

draw_line(x1,y1,x2,y2,LINE_WIDTH)
Text 0,0,"Left Mouse Click to draw a new line - ESC to quit"

Flip

Wend
End

;=========================================
Function draw_line(x1,y1,x2,y2,LINE_WIDTH)
;=========================================

Local n# = 0
Local angle# = 0
Local normal# = 0

;found the angle of the line
angle = ATan2((y2-y1),(x2-x1))

;found the normal to the line
normal = 90 - angle

;show info about the angles found
Text 0,20, "Line Angle: " + angle
Text 0,40,"Line Normal: " + normal

;draw the line with the desired width
For n = -LINE_WIDTH/2 To LINE_WIDTH/2 Step 0.05
	
	x3# = x1 + Cos(normal)*n
	y3# = y1 - Sin(normal)*n

	x4# = x2 + Cos(normal)*n
	y4# = y2 - Sin(normal)*n
	
	Line x3,y3,x4,y4
Next
End Function
