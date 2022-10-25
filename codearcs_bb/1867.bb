; ID: 1867
; Author: Nebula
; Date: 2006-11-19 07:08:34
; Title: Statische ruis
; Description: No Tv channel, and the rochard experience

;
; If you really look into this animated static tv, you will see your memory. 
; This is becourse our memory can recall images.
; Look into the static, and think of something.
;
;
; Based on the rochard test.
;
;
;
Graphics 640,480,16,2
SetBuffer BackBuffer()

timer = CreateTimer(5)
While KeyDown(1) = False
	Cls
	;
	For x=0 To 640 Step 3
	For y=0 To 480 Step 3
		a = Rand(0,255)
		Color a,a,a
		Rect x,y,3,3,True
	Next:Next
	;
	WaitTimer timer
	;
	Flip
Wend
End
