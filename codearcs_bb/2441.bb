; ID: 2441
; Author: CloseToPerfect
; Date: 2009-03-23 17:58:20
; Title: simple text input for graphic modes
; Description: Blitz plus has no input command for the graphic modes only the console

Global ccstring$=""
Global cccursorblink 
Global ccursorblinkrate = 30; adjust this value to speed up or slow down the blink
Function cc_text_input$(ccinputtext$,ccinputxloc,ccinputyloc,ccmaxcharacters,ccmaxstringwidth)
;usage getstring$ = cc_text_input("display text", screen x location, screen y location, max number of characters, max string width)
;this function accepts ascii input 
;this function erases with backspace and has a blinking cusor
;this function returns the finial string after the user presses the enter key
;this function does not stop the program execution if used in a loop
keyin=GetKey()
;8 is backspace
;13 is enter
If keyin > 0 Then 
	If keyin = 8 
		;backspace
		If Len(ccstring$)>0 Then ccstring$=Left$(ccstring$,Len(ccstring$)-1)
	ElseIf keyin = 13  
		;enter
		ccreturnstring$ = ccstring$
		ccstring$ =""
		Return ccreturnstring$
	Else 
		ccstring$=ccstring$+Chr$(keyin)
	EndIf
EndIf 
;mod line if it has too many characters
If Len(ccstring$) > ccmaxcharacters  
	ccstring$=Left$(ccstring$,Len(ccstring$)-1)
EndIf
;mod line if it is to wide by removing last letter added
If StringWidth(ccstring$) > ccmaxstringwidth 
	ccstring$=Left$(ccstring$,Len(ccstring$)-1)
EndIf
;add blinking cursor and show line
cccursorblink=cccursorblink-1
If cccursorblink <= 0 Then cccursorblink = ccursorblinkrate
If cccursorblink < ccursorblinkrate/2  
	Text ccinputxloc,ccinputyloc,ccinputtext$+ccstring$+"_"
Else 
	Text ccinputxloc,ccinputyloc,ccinputtext$+ccstring$
EndIf 
End Function 


Function cc_color_int%(ccvalue)
;break integer color into rgb color 
;why isn't there a included way To use integer colors values in blitz
;sets the color using a integer color value
red   = ccvalue Shr 16 And %11111111
green = ccvalue Shr 8 And %11111111
blue  = ccvalue And %11111111
Color red,green,blue
End Function 




;example code

Type Tball
	Field x,y,speed,direction,Colors
End Type

For i = 0 To 99 ; make 100 moving balls 
	ball.Tball 		= New Tball
	ball\x 			= Rand(0,GraphicsWidth())
	ball\y 			= Rand(0,GraphicsHeight())
	ball\speed 		= Rand(1,4)
	ball\direction  = Rand(0,3)
	ball\Colors		= Rand(0,255*255*255)
Next

Graphics 800,640,16,2
Repeat
	Cls
	;draw background items first
	For ball.Tball = Each Tball
		Select ball\direction
		Case 0 ;up
			ball\y = ball\y - ball\speed
			If ball\y<0 Then ball\direction = 1
		Case 1 ;down
			ball\y = ball\y + ball\speed
			If ball\y>GraphicsHeight() Then ball\direction = 0
		Case 2 ; left
			ball\x = ball\x - ball\speed
			If ball\x<0 Then ball\direction = 3
		Case 3 ; right
			ball\x = ball\x + ball\speed
			If ball\x>GraphicsWidth() Then ball\direction = 2
		End Select 
		cc_color_int(ball\Colors)
		Oval ball\x,ball\y,20,20
	Next

	Color 255,255,255 ; set text color
	entry$=cc_text_input("enter something :",0,200,20,200)
	If entry$<>"" Then lastentry$=entry$
	Text 0,220,lastentry$
	Flip
Until KeyHit(1)
