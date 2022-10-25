; ID: 1333
; Author: n8r2k
; Date: 2005-03-19 17:06:19
; Title: Mouse Follow Text
; Description: The text follows the mouse with a trail

;set the graphics
Graphics 800,600,32,2

n = 5

;call the arrays
Dim textcolor(n)
Dim x(n)
Dim y(n)
Dim speed(n)

;set the color and speed starting variables
d = 255
s = 2

;set the color For the Text
For a = 1 To n
	textcolor(a) = d
	d = d - 200/n
Next 

;set the speed for the text
For b = 1 To n
	speed(b) = s
	s = s + 1
Next 

;set the text string value
Const followtext$ = "This is the followtext demo"

;start the loop
While Not KeyHit(1)

;clear the screen
Cls

;move the text strings around on screen
For c = n To 1 Step -1
	x(c) = x(c) + ((MouseX() - x(c))/speed(c))
	y(c) = y(c) + ((MouseY() - Y(c))/speed(c))

	Color textcolor(c),textcolor(c),textcolor(c)
				
	Text x(c)+10,y(c),followtext$
Next

;flip the buffers
Flip

;loop to beginning
Wend

;end the program
End
