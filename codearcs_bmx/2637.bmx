; ID: 2637
; Author: Nilium
; Date: 2010-01-07 18:40:04
; Title: Multitouch Trackpad Events
; Description: Adds handling for mutlitouch trackpad events (magnification, rotation, and swiping) under Mac OS

SuperStrict

?MacOS
Import "tracktouch.m"

Extern "C"
	Function CreateTrackpadHandler%(gfx:TGraphics)
	Function DestroyTrackpadHandler%(handle:Int)
End Extern
?

Const EVENT_TOUCH_GESTURE:Int = EVENT_USEREVENTMASK+88 ' unused
Const EVENT_TOUCH_MAGNIFY:Int = EVENT_TOUCH_GESTURE+1
Const EVENT_TOUCH_SWIPE:Int = EVENT_TOUCH_MAGNIFY+1
Const EVENT_TOUCH_ROTATE:Int = EVENT_TOUCH_SWIPE+1
Const EVENT_TOUCH_BEGINGESTURE:Int = EVENT_TOUCH_ROTATE+1 ' unused
Const EVENT_TOUCH_ENDGESTURE:Int = EVENT_TOUCH_BEGINGESTURE+1 ' unused
