; ID: 2153
; Author: UUICEO
; Date: 2007-11-11 19:25:38
; Title: Trapping the ENTER keypress
; Description: This will trap the users enter key

dType = False
win = CreateWindow("test",0,500,400,400,0) 
tbox = CreateTextArea(0,0,200,100,win)
gbox = CreateTextField(0,102,200,20,win)
HotKeyEvent 28,0,$028,0,0,0,0,win 
Repeat
id = WaitEvent()
	Select id
		Case $028
			If (Not dType) id = 0:Goto badreturnkeypress
			Stop
		Case 260; ENTER Key press ID when in a text field
			If (Not dType) id = 0:Goto badreturnkeypress
			Stop
		Case $401
			dType = False
			If (Not dType) And EventSource() = tbox dType = True:Stop
			If (Not dType) And EventSource() = gbox dType = True
		Case $803
			Exit
.badreturnkeypress
	End Select
Forever 

End
