; ID: 1353
; Author: Cold Harbour
; Date: 2005-04-16 12:15:57
; Title: Buttons with multiple lines of text
; Description: Multiline Buttons

; 	.lib "User32.dll"
;	GetWindowLong% (hwnd%, nIndex%) : "GetWindowLongA"
;	SetWindowLong% (hwnd%, nIndex%, dwNewLong%) : "SetWindowLongA"

Const BM_SETSTYLE	=	244
Const GWL_STYLE		=	-16
Const BS_MULTILINE	=	8192

win=CreateWindow ("Win",10,10,600,600,main,15)

b1=Createmultibutton("A quite long bit of text to show multlineness",10,10,80,70,win,1)
b2=Createmultibutton("A quite long bit of text to show multlineness",10,100,90,70,win,2)
b3=Createmultibutton("A quite long bit of text to show multlineness",10,200,80,70,win,3)

While WaitEvent(10)<>$803
Wend
End 


Function Createmultibutton(name$, x, y, w, h, p,style) 

	button = CreateButton(name, x, y, w, h, p,style)

	hwnd = QueryObject(button, 1)

	SetWindowLong (hwnd, GWL_STYLE, GetWindowLong(hwnd, GWL_STYLE) Or BS_MULTILINE)

	;force button redraw kludge
	SetGadgetShape button,GadgetX(button),GadgetY(button),GadgetWidth (button)+1,GadgetHeight (button)
	SetGadgetShape button,GadgetX(button),GadgetY(button),GadgetWidth (button)-1,GadgetHeight (button)

	Return button

End Function
