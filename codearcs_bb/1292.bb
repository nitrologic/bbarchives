; ID: 1292
; Author: n8r2k
; Date: 2005-02-14 20:52:48
; Title: Take Randomness to the next leval!
; Description: Pretty and Pointless!- Update 1.02!

;A realy sad program
;By Supernatendo
;Really Pointless!
;Really Random!
;Really Stupid!
SeedRnd MilliSecs()
w = 1
l = 1
While Not KeyHit(1)
Graphics w,l,16,2
w = Rand(100,800)
l = Rand(100,600)
m = Rand(1,2)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Rect Rand(0,w),Rand(0,l),Rand(0,w),Rand(0,l),Rand(0,1)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Text 0,0,"PRESS AND HOLD SPACE TO PAUSE"
While KeyDown(57)
Color Rand(0,255),Rand(0,255),Rand(0,255)
Cls
Text 0,0,"RELEASE SPACE TO RESUME"
Flip
Wend
Delay(Rand(100,700))
Flip
randnum = Rand(0,100000000000124124)
AppTitle randnum
Wend
WaitKey()
