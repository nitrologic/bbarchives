; ID: 1403
; Author: Hip Teen
; Date: 2005-06-20 14:18:18
; Title: transparent windows
; Description: Change the opacity of a window and/or set one color transparent

;--------------------------------------------------------;
; Funktion made by Thorsten Ludwig (thorsten.ludwig1@gmx.de)  ;
;--------------------------------------------------------;
; add this in the user32.decls
;
; .lib "User32.dll"
; SetWindowLong%(hWnd%, nIndex%, dwNewLong%):"SetWindowLongA"	
; SetLayeredWindowAttributes%(hwnd, crKey, bAlpha, dwFlags):"SetLayeredWindowAttributes"
;--------------------------------------------------------------------------------------;


timer = CreateTimer (50)
window = CreateWindow("Test",0,0,600,400,Desktop(),0)
panel = CreatePanel (0,0,gadgetwidth(window), GadgetHeight(window), window)
example = CreateTextField (250,120,100,20,panel)
skin_window(window,"test.bmp",panel, $FF00FF,100,3)
Repeat
  Select WaitEvent()
  Case $101
    Select EventData()
    Case 1
      End
    End Select    
	Case $4001
    If MouseDown(1) Then
     If gedrueckt = 0 Then
       maus_X = MouseX()
       maus_Y = MouseY()
       fenster_X = GadgetX(window)
       fenster_Y = GadgetY(window)
       gedrueckt = 1
     Else 
       neues_maus_X = MouseX()
       neues_maus_Y = MouseY()
       differenz_X = maus_X - neues_maus_X
       differenz_Y = maus_Y - neues_maus_Y
       fenster_X = fenster_X - differenz_X
       fenster_Y = fenster_Y - differenz_Y
       maus_X = neues_maus_X
       maus_Y = neues_maus_Y
       SetGadgetShape (window, fenster_X, fenster_Y, 600,400)
     End If
    Else 
	    gedrueckt = 0
	  End If
	 End Select
Forever

Function skin_window(window_handle, image_path$,panel,colorkey,alpha,colororalpha)
; colorkey is transparency color in hexadecimal form, black ist for example $FFFFFF
; alpha describes the opacity of the window, 0 for full translucent and 255 for non translucent
; use coloralpha to choose the mode you want to use
; 1 to set one color fully lucent
; 2 to set the level of the transperence of the whole window
; 3 to do both
; I think the rest ist self-explanatory

   If Not panel Then
     panel = CreatePanel (0,0,gadgetwidth (window_handle), GadgetHeight (window_handle), window_handle)
   End If
   SetPanelImage panel, image_path$
   hwnd = QueryObject (window_handle, 1)
   SetWindowLong(hwnd, -20,  $80000)
   SetLayeredWindowAttributes(hwnd, colorkey, alpha, colororalpha)
End Function
