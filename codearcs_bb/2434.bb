; ID: 2434
; Author: RifRaf
; Date: 2009-03-15 13:09:18
; Title: RenderTween Easymode
; Description: Tween include, one line tweening

;to use this as in include.. you must make youre main game loop in a function called updategame()
;so you call checktween() and inside checktween it calls updategame() for your game logic

;Include



;example main program
Include "tween.bb"
Graphics3D 800,600,0,2
Camera=CreateCamera()
Global Box=CreateCube()
PositionEntity camera,0,10,-10
PointEntity camera,box
PositionEntity Box,0,0,3

While Not KeyDown(1)
     CheckTween()
     RenderWorld (frametween)
     Flip 
Wend

Function UpdateGame()
 MoveEntity Box,0,0,.2
 TurnEntity Box,0,.5,0
End Function
;end main code example


;include code : tween.bb
;tween.bb
Global gameFPS = 60 ;your logic frames per second.. rendering will happen as fast as it can 
Global Frameperiod,Frametime,frameticks
framePeriod = 1000 / gameFPS
frameTime = MilliSecs () - framePeriod

Function checktween()
    Repeat
	frameElapsed = MilliSecs () - frameTime
	Until frameElapsed
	frameTicks = frameElapsed / framePeriod
	frameTween# = Float (frameElapsed Mod framePeriod) / Float (framePeriod)
    If frameTicks>7 Then frameTicks=7:frametime=MilliSecs() 
	For frameLimit = 1 To frameTicks
		        Updategame()
                UpdateWorld()
		If frameLimit = frameTicks Then CaptureWorld()
		frameTime = frameTime + framePeriod
      Next

End Function
