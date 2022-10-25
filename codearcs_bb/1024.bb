; ID: 1024
; Author: Binary_Moon
; Date: 2004-05-10 17:34:01
; Title: Change Cursor Icon
; Description: Change the icon of a cursor in your blitz+ apps (also works in B3d)

window = CreateWindow("Cursor Test",200,200,200,200,0,1)
canvas = CreateCanvas(0,0,200,200,window)

quit = False

; Mouse Pointers
Const IDC_ARROW			= 32512
Const IDC_IBEAM			= 32513
Const IDC_WAIT			= 32514
Const IDC_CROSS 		= 32515 
Const IDC_UPARROW 		= 32516
Const IDC_SIZENWSE 		= 32642
Const IDC_SIZENESW 		= 32643
Const IDC_SIZEWE 		= 32644 
Const IDC_SIZENS 		= 32645 
Const IDC_SIZEALL 		= 32646 
Const IDC_NO 			= 32648 
Const IDC_HAND 			= 32649
Const IDC_APPSTARTING 	= 32650
Const IDC_HELP 			= 32651
Const IDC_ICON 			= 32641
Const IDC_SIZE 			= 32640

cursor = LoadCursor(0,IDC_IBEAM)

Repeat

	Select WaitEvent()

	Case $201 ;mouse down
	
		SetCursor cursor
	
	Case $803 ; window close
	
		quit = True
	
	End Select

Until quit = True

End
