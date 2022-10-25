; ID: 1447
; Author: Blaine
; Date: 2005-08-18 15:28:18
; Title: Set Client Width\Height
; Description: Set the client area width\height of a window without the 'client coordinates' flag!

Function SetClientWidth(win,width)
	SetGadgetShape win,GadgetX(win),GadgetY(win),GadgetWidth(win)+(width-ClientWidth(win)),GadgetHeight(win)
End Function

Function SetClientHeight(win,height)
	SetGadgetShape win,GadgetX(win),GadgetY(win),GadgetWidth(win),GadgetHeight(win)+(height-ClientHeight(win))
End Function
