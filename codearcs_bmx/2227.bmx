; ID: 2227
; Author: JoshK
; Date: 2008-03-10 16:57:46
; Title: SetComboboxHeight
; Description: Sets the maximum height a combobox list drops down to when opened

Function SetComboboxHeight(gadget:TGadget,height)
	Local combohwnd=SendMessageA(QueryGadget(gadget,QUERY_HWND),CBEM_GETCOMBOCONTROL,0,0)
	SetWindowPos combohwnd,0,0,0,GadgetWidth(gadget),Desktop().ClientHeight(),SWP_NOMOVE|SWP_NOZORDER|SWP_NOREDRAW|SWP_NOACTIVATE
EndFunction
