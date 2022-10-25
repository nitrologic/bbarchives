; ID: 1461
; Author: Beaker
; Date: 2005-09-14 11:56:47
; Title: Flash and BlitzPlus
; Description: How to use Flash for a user interface

Global window=CreateWindow( "Flash UI in Blitz+",0,0,340,380, 0, 1)
SetMinWindowSize window,200,0

Global html=CreateHtmlView( 0,0,ClientWidth(window),ClientHeight(window),window,3 )
SetGadgetLayout html,1,1,1,1

HtmlViewGo html,CurrentDir()+"flash URL test.html"

While WaitEvent()
;	DebugLog "eventID $"+Hex(EventID())+"  eventDATA "+EventData()

	Select EventID()

		Case $401
			Select EventData()
				Case 1
					DebugLog "BUTTON PRESSED"
			End Select
			

		Case $803	;WINDOW CLOSED EVENT
			Select EventSource()
				Case window
					End
			End Select
	End Select
	

Wend
End
