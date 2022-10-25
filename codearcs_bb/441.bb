; ID: 441
; Author: Rob Farley
; Date: 2002-09-28 18:42:21
; Title: Simple FPS Counter
; Description: Very simple FPS counter with no flutter

;set up fps counter
fps_milli=MilliSecs(): fps_counter=0: update_frequency=10



mainloop
..
..
updateworld
renderworld

; fps counter
fps_counter=fps_counter+1
If fps_counter=update_frequency
     fps=1000/Float(((MilliSecs()-fps_milli))/update_frequency)
     fps_milli=MilliSecs()
     fps_counter=0
     endif

; print fps
Text 0,0,"FPS:"+fps
