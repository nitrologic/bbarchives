; ID: 2120
; Author: Ked
; Date: 2007-10-15 18:08:32
; Title: Kev's READONLY TextArea
; Description: Kev's explaination on how to make a textarea readonly.

; .lib "user32.dll"
; user32_SendMessage%(hWnd%,Msg%,wParam%,lParam%):"SendMessageA"

Const ES_READONLY = $800
Const EM_SETREADONLY = $CF

; Example provided by Mag, added to documentation by Mark Tiffany 
win = CreateWindow ("Test Text Area",0,0,300,300) ; create a window first 
txtbox = CreateTextArea(0,0,200,200,win) ; <--- CREATE TEXTAREA (Multi line text field) 

hwnd = QueryObject(txtbox,1)

user32_SendMessage(hwnd,EM_SETREADONLY,1,0)
user32_SendMessage(hwnd,ES_READONLY,1,0)

SetGadgetText txtbox,"Type anything (multiline) "+Chr$(13)+"And press Get Text button" ;put some text on that textare for info 
gt=CreateButton("Get Text",200,0,80,20,win);create button 
Repeat 
id=WaitEvent() ;wait for user action (in a form of event) 
If id=$803 Then End ;to quit program when receive Window close event 
If id=$401 And EventSource()=gt Then ;when ok is press 
Notify "This is your text in TextArea:"+Chr$(13)+TextAreaText$(txtbox);<<--TO GET TEXT FROM TEXTFIELD 
End If 
Forever
