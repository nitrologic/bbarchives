; ID: 2445
; Author: USNavyFish
; Date: 2009-03-26 19:58:00
; Title: Multi-Colored Line (DOUBLE POST, PLEASE DELETE)
; Description: This function uses OpenGL to draw a line that fades from one color to another.

Function DrawMultiColorLines(x0:Float , y0:Float , x1:Float , y1:Float , rgb0:Byte[] , rgb1:Byte[]) 
		glDisable GL_TEXTURE_2D
		glBegin GL_LINES
		glColor3ub(rgb0[0] , rgb0[1] , rgb0[2]) 
		glVertex2f(x0,y0)
		glColor3ub(rgb1[0] , rgb1[1] , rgb1[2]) 
		glVertex2f(x1 , y1)		
		glEnd
		glEnable GL_TEXTURE_2D
		glColor3ub(255,255,255)
	End Function
