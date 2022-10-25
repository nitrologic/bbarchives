; ID: 2974
; Author: Captain Wicker Soft
; Date: 2012-09-09 18:25:17
; Title: ShowFPS
; Description: a simple code to show the screen fps

Function ShowFPS(x#,y#)
timenow=MilliSecs()

If timenow>telltime Then
	telltime=timenow+1000
	getframes=frames
	frames=0
Else
	frames=frames+1
EndIf
	Text x#,y#,getframes
End Function
