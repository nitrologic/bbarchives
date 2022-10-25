; ID: 749
; Author: Second Chance
; Date: 2003-07-19 04:59:12
; Title: Dynamic, accurate, and smart frame limiting
; Description: Accurate frame limiting

;This code will delay main loop execution to provide accurate frame limiting
;"on the fly" based on how long the main loop takes to execute each cycle.

fRate = (1000/30) ;*set second number to desired frame rate*

While Not KeyHit(1)
	;this code must go at the very start of the loop
	time1 = MilliSecs() ;time at start of processing loop
	
	;***********************************
	;all processing loop code goes here*
	;***********************************
	
	;this code must go at the very end of the loop
	time2 = MilliSecs() ;time at end of processing loop
	time3 = time2 - time1 ;number of miiliseconds to process loop
	time4 = fRate - time3 ;number of milliseconds to delay each processing loop to achieve dynamically accurate framerate
	Delay time4 ;delay execution
Wend

;Use any "frame per second" code to verify correct frame speed.
;Frame speed variance is usually within 1 fps. Not bad, eh? :)
