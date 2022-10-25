; ID: 901
; Author: skn3[ac]
; Date: 2004-02-01 03:06:01
; Title: Blitz+ Event wrapper
; Description: A premade chunk of code, to wrap the blitz event system

repeat
	event = WaitEvent()
	If event
		event_source = EventSource()
		event_data   = EventData()
		event_x      = EventX()
		event_y      = EventY()
		Select event
			Case $101  : Event_KeyDown       (event_source,event_data,event_x,event_y)
			Case $102  : Event_KeyUp         (event_source,event_data,event_x,event_y)
			Case $103  : Event_KeyStroke     (event_source,event_data,event_x,event_y)
			Case $201  : Event_MouseDown     (event_source,event_data,event_x,event_y)
			Case $202  : Event_MouseUp       (event_source,event_data,event_x,event_y)
			Case $203  : Event_MouseMove     (event_source,event_data,event_x,event_y)
			Case $204  : Event_MouseWheel    (event_source,event_data,event_x,event_y)
			Case $205  : Event_MouseEnter    (event_source,event_data,event_x,event_y)
			Case $206  : Event_MouseLeave    (event_source,event_data,event_x,event_y)
			Case $401  : Event_GadgetAction  (event_source,event_data,event_x,event_y)
			Case $801  : Event_WindowMove    (event_source,event_data,event_x,event_y)
			Case $802  : Event_WindowSize    (event_source,event_data,event_x,event_y)
			Case $803  : Event_WindowClose   (event_source,event_data,event_x,event_y)
			Case $804  : Event_WindowActive  (event_source,event_data,event_x,event_y)
			Case $1001 : Event_MenuAction    (event_source,event_data,event_x,event_y)
			Case $2001 : Event_AppSuspend    (event_source,event_data,event_x,event_y)
			Case $2002 : Event_AppResume     (event_source,event_data,event_x,event_y)
			Case $2003 : Event_DisplayChange (event_source,event_data,event_x,event_y)
			Case $2004 : Event_BeginModal    (event_source,event_data,event_x,event_y)
			Case $2005 : Event_EndModal      (event_source,event_data,event_x,event_y)
			Case $4001 : Event_Timer         (event_source,event_data,event_x,event_y)
		End Select
	End If
forever

Function Event_KeyDown(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_KeyUp(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_KeyStroke(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_MouseDown(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_MouseUp(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_MouseMove(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_MouseWheel(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_MouseEnter(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_MouseLeave(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function
 
Function Event_GadgetAction(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_WindowMove(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_WindowSize(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_WindowClose(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_WindowActive(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_MenuAction(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_AppSuspend(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_AppResume(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_DisplayChange(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_BeginModal(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_EndModal(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function

Function Event_Timer(source,sourcedata,sourcex,sourcey,param1$="",param2$="",param3$="")
End Function
