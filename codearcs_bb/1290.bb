; ID: 1290
; Author: sswift
; Date: 2005-02-13 23:51:40
; Title: Improved Mouse Functions
; Description: This set of functions tells you where the mouse or a gadget is on the screen (or relative to a gadget), and whether the mouse is over a gadget.

; -----------------------------------------------------------------------------------------------------------------------------------
;
; This code allows you to determine where the mouse or a gadget is on the screen, and whether the mouse is over a gadget.
; To use this function, first place the following in the file "user32.decls" in your userlibs folder:
;
; 	.lib "user32.dll"
; 	GetCursorPos%(lpPoint*):"GetCursorPos"
; 	ClientToScreen%(hWnd%, lpPoint*):"ClientToScreen"
;
; -----------------------------------------------------------------------------------------------------------------------------------


Type TYPE_lpPoint
	Field X
	Field Y
End Type

Global lpPoint.TYPE_lpPoint = New TYPE_lpPoint


; -----------------------------------------------------------------------------------------------------------------------------------
; This function returns TRUE if the mouse is over the specified gadget, and FALSE if it is not.
;
; Note:
; It has not been tested to see if it works properly with gadgets that are clipped by another gadget or window!
; -----------------------------------------------------------------------------------------------------------------------------------
Function MouseOver(Gadget)

	Local Gx, Gy
	Local Mx, My

	; Get the screen coordinates of the gadget.
		Gx = GadgetScreenX(Gadget)
		Gy = GadgetScreenY(Gadget)
		
	; Get the screen coordinates of the mouse cursor.
		Mx = MouseScreenX()
		My = MouseScreenY()
	
	; Is the mouse within the rectangle defined by the gadget?
		If (Mx >= Gx) And (Mx < Gx+GadgetWidth(Gadget)) And (My >= Gy) And (My < Gy+GadgetHeight(Gadget)) Then Return True
		Return False
	
End Function


; -----------------------------------------------------------------------------------------------------------------------------------
; This function returns the X coordinates of a gadget on the screen.
; -----------------------------------------------------------------------------------------------------------------------------------
Function GadgetScreenX(Gadget) 

	Local HWnd

	; Get a windows pointer to the gadget.
		HWnd = QueryObject(Gadget, 1)

	; Set the point in the gadget which we want to find the screen coordinates of.
		lpPoint\X = 0 
		lpPoint\Y = 0 
		
	; Get the screen coordinates of the pixel in the gadget.
		ClientToScreen(HWnd, lpPoint)
		Return lpPoint\X

End Function 


; -----------------------------------------------------------------------------------------------------------------------------------
; This function returns the Y coordinates of a gadget on the screen.
; -----------------------------------------------------------------------------------------------------------------------------------
Function GadgetScreenY(Gadget) 

	Local HWnd

	; Get a windows pointer to the gadget.
		HWnd = QueryObject(Gadget, 1)

	; Set the point in the gadget which we want to find the screen coordinates of.
		lpPoint\X = 0 
		lpPoint\Y = 0 
		
	; Get the screen coordinates of the pixel in the gadget.
		ClientToScreen(HWnd, lpPoint)
		Return lpPoint\Y

End Function 


; -----------------------------------------------------------------------------------------------------------------------------------
; This function returns the X coorindate of the mouse cursor on the screen.
; -----------------------------------------------------------------------------------------------------------------------------------
Function MouseScreenX()
	GetCursorPos(lpPoint)
	Return lpPoint\X
End Function


; -----------------------------------------------------------------------------------------------------------------------------------
; This function returns the Y coorindate of the mouse cursor on the screen.
; -----------------------------------------------------------------------------------------------------------------------------------
Function MouseScreenY()
	GetCursorPos(lpPoint)
	Return lpPoint\Y
End Function


; -----------------------------------------------------------------------------------------------------------------------------------
; This function returns the X coordinate of the mouse relative to a gadget.
; -----------------------------------------------------------------------------------------------------------------------------------
Function MouseGadgetX(Gadget)
	Return MouseScreenX()-GadgetScreenX(Gadget)
End Function


; -----------------------------------------------------------------------------------------------------------------------------------
; This function returns the X coordinate of the mouse relative to a gadget.
; -----------------------------------------------------------------------------------------------------------------------------------
Function MouseGadgetY(Gadget)
	Return MouseScreenY()-GadgetScreenY(Gadget)
End Function
