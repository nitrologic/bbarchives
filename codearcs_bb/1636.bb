; ID: 1636
; Author: Fernhout
; Date: 2006-03-07 14:29:38
; Title: Button3D
; Description: 3D look button

;******************************************************************************
;* Button3D. Version 1.00                                                     *
;* Created: march 2006                                                        *
;* Created by: Bart Fernhout.												  *
;******************************************************************************
;* Description                                                                *
;* Create a button in a 3D look.  Its drawing on the backbuffer screen.       *
;* Then it wil be fliped to the front. The 3D colors is given during the call *
;* to this function. The function gives a true back if the button is released *
;* color 2 for button pressed is given in the input. Button down is color     *
;* 2 and 3 swaped.   Call of the function is a follow:                        *
;* Button3D (Xpos_LeftUp,Ypos_LeftUP,WithButton,HeihtButton,Color1,Color2, _  *
;*           Color3,Color4,Color5,Text)                                       *
;* Color 1 = button color                                                     *      
;* Color 2 = Left And Up side of button                                       *
;* Color 3 = Right and down side color of the button                          *
;* Color 4 = Button down color                                                *
;* Color 5 = Text color                                                       *
;*                                                                            *
;* NOTE: Even if the placing of the button is outside the visual screen there *
;* will be no checking for this.                                              *
;*                                                                            *
;* The text font is used by the standard font what is used in the program.    *
;* to change the font do this in the main program. And change back after      *
;* call of this program. Font color is not changed                            *
;******************************************************************************

;* * * *
;* Extra in main program include is SetColors_inc.BB
;* * * *

Function Button3D (XposButton,YposButton,ButWith,ButHeight,Col1$,Col2$,Col3$,Col4$,Col5$,ButText$)
;Make sure the back buffer is the target.
	
; Side colors
Col20$ = Col2$
Col30$ = Col3$
; Button color
Col10$ = Col1$
Col40$ = Col4$

; now everything what was visable is in the drawing area.
; And make a repeating area so the swapping of the button is always on screen
Repeat
;Flip
; Check if the mouse button left is down then swap colors
If (MouseX()>XposButton And MouseX()<XposButton+ButWith) And (MouseY()>YposButton And MouseY()<YposButton+ButHeight) And MouseDown(1) = 1 Then
	; Swap button in colors
	Col2$=Col30$
	Col3$=Col20$
	; button
	Col1$=Col40$
	Col4$=Col10$
Else
	; make button in colors
	Col2$=Col20$
	Col3$=Col30$
	; button
	Col1$=Col10$
	Col4$=Col40$
EndIf

; Draw the left and uppers side.
SetColor Col2$
Line XposButton,YposButton,XposButton+ButWith,YposButton
Line XposButton,YposButton+1,XposButton+ButWith,YposButton+1
Line XposButton,YposButton,XposButton,YposButton+ButHeight
Line XposButton+1,YposButton,XposButton+1,YposButton+ButHeight

;Draw the right and down side
SetColor Col3$
Line XposButton+ButWith,YposButton+1,XposButton+ButWith,YposButton+ButHeight
Line XposButton+ButWith-1,YposButton+2,XposButton+ButWith-1,YposButton+ButHeight
Line XposButton+1,YposButton+ButHeight,XposButton+ButWith,YposButton+ButHeight
Line XposButton+2,YposButton+ButHeight-1,XposButton+ButWith,YposButton+ButHeight-1

;Draw the button
SetColor Col1$
Rect XposButton+2,YposButton+2,ButWith-3,ButHeight-3

;Place the text in the center of the button program wil find out font height and with
SetColor Col5$
Text XposButton+(ButWith/2),YposButton+(ButHeight/2),ButText$,True,True

; bring it al to the front
Flip

;Check to see if the button is hit on a button if so hold program for releas mouse on button
	If MouseX()>XposButton And MouseX()<XposButton+ButWith And MouseY()>YposButton And MouseY()<YposButton+ButHeight And MouseDown(1) = 1 Then
		Mouse = True
	Else 
		If MouseX()>XposButton And MouseX()<XposButton+ButWith And MouseY()>YposButton And MouseY()<YposButton+ButHeight And Mouse=1 Then
			Mouse = False
			MouseReleas = True
		Else
			Mouse = False
			MouseReleas = False
		EndIf
	EndIf
Until Mouse = False

; Send back the button status
Return MouseReleas
End Function
