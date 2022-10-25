; ID: 237
; Author: SurreaL
; Date: 2002-02-13 23:11:42
; Title: FPS counter
; Description: Does your taxes..no wait. counts your prog's FPS (really!)

;Slip this code somewheres immediately before your Flip
curTime = MilliSecs()
If curTime > checkTime Then
	checkTime = curTime + 1000
	curFPS = fpscounter
	fpscounter = 0
Else
	fpscounter = fpscounter + 1
End If
