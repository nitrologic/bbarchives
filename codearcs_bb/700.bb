; ID: 700
; Author: Red
; Date: 2003-05-21 15:14:19
; Title: cut / copy / paste / undo / redo
; Description: easy way to edit TextArea

;---------------------------------------------------

Function TextAreaUndo(txt)
	Local EM_UNDO=$C7
	SendMessage(QueryObject(txt,1), EN_UNDO, 0, 0)
End Function 

;---------------------------------------------------

Function TextAreaRedo(txt)
	Local EM_REDO=$454
	SendMessage(QueryObject(txt,1), EM_REDO, 0, 0)
End Function 

;---------------------------------------------------

Function TextAreaCut(txt)
	Local WM_CUT=$300
	SendMessage(QueryObject(txt,1), WM_CUT, 0, 0)
End Function 

;---------------------------------------------------

Function TextAreaCopy(txt)
	Local WM_COPY=$301
	SendMessage(QueryObject(txt,1), WM_COPY, 0, 0)
End Function 

;---------------------------------------------------

Function TextAreaPaste(txt)
	Local WM_PASTE=$302
	SendMessage(QueryObject(txt,1), WM_PASTE, 0, 0)
End Function 
	
;---------------------------------------------------
