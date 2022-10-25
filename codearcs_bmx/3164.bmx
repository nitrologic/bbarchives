; ID: 3164
; Author: AdamStrange
; Date: 2014-11-27 02:24:08
; Title: Efficient multitasking
; Description: main program loop for good multitasking

Method ProgMain()
	local AppX:int
	local AppY:int
	local AppMouseDown:int
	While Not AppQuit
		WaitEvent()
		Select EventID()
			Case EVENT_APPTERMINATE, EVENT_WINDOWCLOSE 
				AppQuit = True
			Case EVENT_MOUSEMOVE
				AppX = EventX()
				AppY = EventY()
				AppMouseDown = MouseDown(1)
		end select
		delay(2)
	wend
end method
