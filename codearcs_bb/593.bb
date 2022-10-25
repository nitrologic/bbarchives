; ID: 593
; Author: cyberseth
; Date: 2003-02-18 05:00:57
; Title: Popupmenus!
; Description: How to make a popupmenu at mouse x,y

; .lib "user32.dll"
; FindWindow$ ( hwnd ):"FindWindowA"
; GetMenu%( hnd )
; GetSubMenu%( hnd,npos )
; TrackPopupMenuEx%( mnu,flags,x,y,hwnd,tpm )

Const TPM_RETURNCMD =	$100

;Create window
win = CreateWindow("Popupmenu Test",100,100,400,300)
txt = CreateLabel("Right-click anywhere to open popupmenu",10,10,250,50,win)

mnuFile = CreateMenu("&File",0,WindowMenu(win))
mnuFileOpen = CreateMenu("&Open",1,mnuFile)
mnuFileOpen = CreateMenu("&Save",1,mnuFile)
mnuFileOpen = CreateMenu("&Exit",1,mnuFile)
mnuEdit = CreateMenu("&Edit",0,WindowMenu(win))
mnuEditCut = CreateMenu("&Cut",    1,mnuEdit)
mnuEditCopy = CreateMenu("&Copy",  2,mnuEdit)
mnuEditPaste = CreateMenu("&Paste",3,mnuEdit)
mnuEditMore = CreateMenu("More",   4,mnuEdit)
mnuEditMoreStuff = CreateMenu("Stuff",5,mnuEditMore)
UpdateWindowMenu(win)

;Find handle for "Edit" submenu
hwnd = FindWindow("BLITZMAX_WINDOW_CLASS","Popupmenu Test")
hmnu = GetSubMenu(GetMenu(hwnd),1) ;0 = File, 1 = Edit, etc...

Repeat
	If WaitEvent(1)=$803 Or KeyHit(1) Then End
	If MouseHit(2) Then
		index = TrackPopupMenuEx (hmnu,TPM_RETURNCMD,MouseX(),MouseY(),hwnd,0)
		SetGadgetText txt,"You clicked item : " + index
	End If
Forever
