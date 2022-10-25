; ID: 133
; Author: semar
; Date: 2001-11-08 16:59:14
; Title: Yes No Cancel Window
; Description: A window that displays up to three buttons, and returns the button that was pressed with the mouse

;windowed mode
Graphics 640,480,0,2

;application title
AppTitle "Simple Window Demo"

SetBuffer BackBuffer()

;global array to be declared in the main program
Dim area(3,3)

;just a test screen
ClsColor 0,128,128
Cls

;call of the function
retcode = win_popup("Sure you want to quit ?","YES","NO","CANCEL")

;shows the returned value
Color 255,255,0
Print "Button pressed = " + retcode

;waits for any key pressed
FlushKeys
WaitKey
End

;==================================================================================
Function win_popup(title$,but1_caption$="",but2_caption$="",but3_caption$="")
;==================================================================================
;BY SEMAR - semar63@hotmail.com
;
;displays a window and waits for a mouse click on a button, or esc with ESC key
;works only in windowed mode
;----------------------------------------------------------------------------------
;parameters:
;
;title$ = The title of the window to be displayed in the window bar
;butX_caption$ = The caption of the X button
;max three buttons available
;----------------------------------------------------------------------------------
;returned value:
;a number that represents the button pressed (1..3)
;or 0 if ESC was pressed instead
;----------------------------------------------------------------------------------

Local window_font = LoadFont("arial.ttf",20,True)
SetFont window_font

;show mouse pointer
ShowPointer

;memo the current graphic buffer
Local oldbuffer = GraphicsBuffer()

;dimension of the window based on the lenght of the title
Local window_width = StringWidth(title) + 100

;height of the window (constant)
Local window_height= 100

;center a viewport on the screen
Local window_x = (GraphicsWidth() - window_width)/2
Local window_y = (GraphicsHeight() - window_height)/2

;buttons vars
Local button_width = 80 ;width of each button
Local button_height = 30 ;height of each button
Local x_button ;starting x point for the first button
Local y_button ;y for each button

;make the viewport bigger for the shadow
Local shadow = 10
Viewport (window_x-shadow,window_y-shadow,window_width+2*shadow,window_height+2*shadow)

;draw a grey shadow
Color 72,72,72
Rect window_x+shadow,window_y+shadow,window_width,window_height,1

;draw the window
Color 192,192,192
Rect window_x,window_y,window_width,window_height,1

;draw the window bar
Color 0,0,128
Rect window_x+1,window_y+1,window_width-2,25,1

;center the window title text to the window bar
Color 255,255,255
Text window_x + window_width/2 ,window_y+3, title, True

;draw buttons
x_button = window_x + 10 ;starting x point for the first button
y_button = window_y + 50 ;y for each button

;draw first button
If but1_caption <> "" Then
	Color 0,0,0
	Rect x_button,y_button,button_width,button_height,0
	Text x_button + button_width/2, y_button+5,but1_caption,True
	
	;make the button 3d
	Color 255,255,255
	Line x_button,y_button,x_button + button_width - 1,y_button
	Line x_button,y_button,x_button,y_button + button_height - 1
	
	;memo the button coordinates in the array
	area(1,1) = x_button
	area(1,2) = y_button
EndIf

;determine the x position for the middle button
x_button = window_x + window_width/2 - button_width/2
;draw middle button
If but2_caption <> "" Then
	Color 0,0,0
	Rect x_button,y_button,button_width,button_height,0
	Text x_button + button_width/2, y_button+5,but2_caption,True

	;make the button 3d
	Color 255,255,255
	Line x_button,y_button,x_button + button_width - 1,y_button
	Line x_button,y_button,x_button,y_button + button_height - 1

	;memo the button coordinates in the array
	area(2,1) = x_button
	area(2,2) = y_button
EndIf


;determine the x position for the third button
x_button = window_x + window_width - button_width - 10

;draw third button
If but3_caption <> "" Then
	Color 0,0,0
	Rect x_button,y_button,button_width,button_height,0
	Text x_button + button_width/2, y_button+5,but3_caption,True

	;make the button 3d
	Color 255,255,255
	Line x_button,y_button,x_button + button_width - 1,y_button
	Line x_button,y_button,x_button,y_button + button_height - 1

	;memo the button coordinates in the array
	area(3,1) = x_button
	area(3,2) = y_button
EndIf


;show the window
Flip

;loop until we click on a button or press the ESC key
click = 0
While (Not KeyDown(1)) And  (Not click)

	;if click with the left button
	If MouseHit(1)
	
		;check all the buttons
		For n = 1 To 3
		
			;check if the mouse click was inside a button
			If (MouseX() > area(n,1)) And (MouseX()< area(n,1) + button_width) Then
				If (MouseY() > area(n,2)) And (MouseY()< area(n,2) + button_height) Then
				
					;acquire the button pressed
					click = n
					
					;exits from the for..next loop
					Exit
					
				EndIf
			EndIf
		Next
	EndIf
	
Wend

;delete the window
Cls

;reset the viewport
Viewport 0,0,GraphicsWidth(),GraphicsHeight()

;reset the buffer to the previous one
SetBuffer oldbuffer

;free the font
FreeFont window_font

;return the button pressed, or 0 if ESC was pressed instead
Return click

End Function

