; ID: 1352
; Author: Cold Harbour
; Date: 2005-04-16 09:05:51
; Title: Two new button styles using API
; Description: New buttons

; 	.lib "User32.dll"
;	SendMessage%(hWnd%,Msg%,wParam%,lParam%):"SendMessageA" 

Const BM_SETSTYLE	=	244
Const BS_AUTO3STATE	=	6
Const BS_DEFPUSHBUTTON	=	1

win=CreateWindow ("Win",10,10,600,300,main,1)

button1=api_CreateButton("Tickbox with three states",10,10,300,20,win,BS_AUTO3STATE)

button2=api_CreateButton("Chunky button",10,50,200,30,win,BS_DEFPUSHBUTTON)

While WaitEvent(10)<>$803
Wend
End 

Function api_CreateButton(title$,x, y, w, h, parent,style) 

	button = CreateButton(title, x, y, w, h, parent)
	hwnd = QueryObject(button, 1)
	SendMessage (hwnd, BM_SETSTYLE, style  ,1)
	Return button

End Function
