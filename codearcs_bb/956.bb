; ID: 956
; Author: PowerPC603
; Date: 2004-03-02 13:41:10
; Title: LimitFrameRate
; Description: Another dynamic framerate limiter

Global FrameTime
Global Period

Function LimitFrameRate(FPS = 50)
	If FrameTime = 0 Then
		Period = 1000 / FPS
		FrameTime = MilliSecs()
	EndIf

	; Make sure the framerate isn't above the specified setting
	While (FrameTime + Period) > MilliSecs()
			Delay 1
	Wend
	FrameTime = MilliSecs()
End Function
