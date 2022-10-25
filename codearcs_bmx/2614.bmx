; ID: 2614
; Author: Zakk
; Date: 2009-11-25 14:03:38
; Title: Simple Query
; Description: Popup window that prompts the user for a string

'query.bmx, requires maxgui

'=============================
Global query_window:TGadget=CreateWindow("Query", 0, 0, 182, 58, Null, WINDOW_CENTER|WINDOW_TITLEBAR|WINDOW_TOOL|WINDOW_CLIENTCOORDS|WINDOW_HIDDEN)
Global query_field:TGadget=CreateTextField(4, 4, 174, 22, query_window)
Global query_okay:TGadget=CreateButton("Okay", 4, 30, 85, 24, query_window)
Global query_cancel:TGadget=CreateButton("Cancel", 93, 30, 85, 24, query_window)
'=============================
Function Query:String(text:String, initial:String)
Local id:Int, es:Object
	SetGadgetText(query_window, text)
	SetGadgetText(query_field, initial)
	ActivateGadget(query_field)
	ShowGadget query_window
	Repeat
		id=WaitEvent()
		es=EventSource()
		Select id
			Case EVENT_WINDOWCLOSE
				Select es
					Case query_window
						HideGadget query_window
						Return initial
					Default
				End Select
			Case EVENT_WINDOWACTIVATE
				Select es
					Case query_window
					Default
						ActivateGadget(query_window)
				End Select
			Case EVENT_GADGETACTION
				Select es
					Case query_okay
						HideGadget query_window
						Return GadgetText(query_field)
					Case query_cancel
						HideGadget query_window
						Return initial
				End Select
			Default
		End Select
	Forever
End Function
'=============================
