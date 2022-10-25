; ID: 1293
; Author: sswift
; Date: 2005-02-16 02:23:45
; Title: GadgetEnabled()
; Description: Tells you if a gadget or window is currently enabled.

; -----------------------------------------------------------------------------------------------------------------------------------
; To use this function, place the following in the file "user32.decls" in your userlibs folder:
;
; 	.lib "user32.dll"
; 	IsWindowEnabled%(hWnd%):"IsWindowEnabled"
; -----------------------------------------------------------------------------------------------------------------------------------


; -----------------------------------------------------------------------------------------------------------------------------------
; This function returns true if the specified gadget is enabled.
; -----------------------------------------------------------------------------------------------------------------------------------
Function GadgetEnabled(Gadget)

	Local HWnd

	; Get a windows pointer to the gadget.
		HWnd = QueryObject(Gadget, 1)
		
	; Is the gadget enabled?
		Return IsWindowEnabled(HWnd)
		
End Function
