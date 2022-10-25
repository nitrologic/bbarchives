; ID: 1186
; Author: Eikon
; Date: 2004-11-04 13:06:22
; Title: Mouse userlib
; Description: How to automate mouse movement and clicks

; // [Userlib Entries]
;.lib "user32.dll"
;SetCursorPos%(x%, y%)
;mouse_event%(dwFlags%, dx%, dy%, cButtons%, dwExtraInfo%)

; // B+ Code
; //=============================
; // Mouse API Function Example by Eikon
; //=============================
Desk_W = ClientWidth(Desktop())
Desk_H = ClientHeight(Desktop())

; // [GUI]
Parent = CreateWindow("Automate Mouse", (Desk_W / 2) - 75, (Desk_H / 2) - 50, 150, 100, 0, 1)
New_X  = CreateTextField(5, 5, 64, 18, Parent, 0)
New_Y  = CreateTextField(75, 5, 64, 18, Parent, 0)
Move   = CreateButton ("Move Mouse", 5, 26, 134, 18, Parent, 1)
Simu   = CreateButton ("Simulate Click", 5, 48, 134, 18, Parent, 1)

SetGadgetText New_X, Desk_W / 2 ; // [Defaults]
SetGadgetText New_Y, Desk_H / 2

; // [Mouse_Event dwFlag Constants]
Const MOUSEEVENTF_ABSOLUTE   = -32768 ; // Use absolute coords
Const MOUSEEVENTF_MOVE       = 1      ; // Trigger move event
Const MOUSEEVENTF_LEFTDOWN   = 2      ; // LMB Down
Const MOUSEEVENTF_LEFTUP     = 4      ; // LMB Up
Const MOUSEEVENTF_RIGHTDOWN  = 8      ; // RMB Down
Const MOUSEEVENTF_RIGHTUP    = 16     ; // RMB Up
Const MOUSEEVENTF_MIDDLEDOWN = 32     ; // MMB Down
Const MOUSEEVENTF_MIDDLEUP   = 64     ; // MMB Up
Const MOUSEEVENTF_WHEEL      = 128    ; // NT Only: Mouse wheel moved

; // [Main]
Repeat
	
Select WaitEvent()
    Case $803: End
    Case $401 ; // [Gadget Event]
    Select EventSource()
        Case Move ; // [Move Button]
        SetCursorPos Int(TextFieldText(New_X)), Int(TextFieldText(New_Y)) 		        
        Case Simu  ; // [Simulate Left Click] 
        Val% = MOUSEEVENTF_LEFTDOWN + MOUSEEVENTF_LEFTUP ; Left Click
        mouse_event Val, 0, 0, 0, 0
	
    End Select
End Select
Forever
