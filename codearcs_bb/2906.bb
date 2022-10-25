; ID: 2906
; Author: Captain Wicker
; Date: 2011-12-11 21:59:50
; Title: CraZy Sticks!
; Description: My home brewed code for an odd series of visual effects!

;************************************
;********** CRAZY - STICKS **********
;*** Small program written in b2d ***
;************************************

AppTitle("CraZy Sticks")
Graphics ((640),(480),(16),(2))
SetBuffer FrontBuffer()


Repeat

RandomColor()

Delay(250)

Cls
Flip
Until KeyDown(1)
End

;Make a drawing function! \/
Function RandomColor%()
ClsColor (Rnd(255),Rnd(255),Rnd(255)) 
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
Color (Rnd(255),Rnd(255),Rnd(255))
Line (Rnd(640),Rnd(480),Rnd(640),Rnd(480))
End Function
;Finished!
