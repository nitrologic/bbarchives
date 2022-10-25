; ID: 1982
; Author: Yahfree
; Date: 2007-04-06 21:12:04
; Title: Breakout Physics ..
; Description: Breakout physics good for new people

;Breakout by Yahfree
;2007-04-4 -> ????-??-(?)?
AppTitle "Breakout by Yahfree","Are you sure?"

;Hides our pointer
HidePointer

;sets the graphics mode, and the setbuffer
Graphics 800,600,32,2
SetBuffer BackBuffer()

;Globalized stuff
Global paddle, paddlex=380, paddley=560
Global ball,ballx=380, bally=580, ballxdir#=0, ballydir=-1, move=1, ballsleft=3, life1, life2, life3, speed=1
Global ;blope=LoadSound("Beep.wav")

;Constant numbers that never change..
Const ESC=1, RIGHTARR=205, LEFTARR=203, SPACE=57, MOUSE1=1


;Mid handles all of the images and creates a image called paddle.
AutoMidHandle True
   paddle=CreateImage(70,15)
   ball=CreateImage(10,10)

;draws on the empty image container..
SetBuffer ImageBuffer(paddle)
   Rect 0,0,70,15,1
SetBuffer ImageBuffer(ball)
   Oval 0,0,10,10,1
SetBuffer BackBuffer()

;Copys the ball image 3 times these will be our lifes displayed on the bottom of the screen.
   life1=CopyImage(ball)
   life2=CopyImage(ball)
   life3=CopyImage(ball)

;!@!@!@!@!@!@!@!@!@!@!@!@!@!@
;!@!@!@!@ MAIN LOOP !@!@!@!!@
;!@!@!@!@!@!!@!@!@!@!@!@!@!@!
While Not KeyHit(1)
Cls

;Calls functions.
addplayercontrols()
drawmyimages()
addcollisions()
addtext()

;small delay in the loop for less slow down.
;flips, ends the loop and termanates the program..
Delay 5
  Flip
Wend
End

;Include "Functions.bb"


;Player controls
Function addplayercontrols()

;Paddle movemovement and controls
paddlex=paddlex+MouseXSpeed()
MoveMouse GraphicsWidth()/2,GraphicsHeight()/2
If paddlex<35 paddlex=35
If paddlex>765 paddlex=765

;Ball control.
If move=1 ballx=paddlex
If move=1 And ballx<35 ballx=35
If move=1 bally=paddley-15
If move=1 ballydir=-1 ballxdir=0
If KeyHit(30) And speed<25 speed=speed+1
If KeyHit(44) And speed>1 speed=speed-1
If KeyHit(SPACE) Or MouseHit(MOUSE1)
   move=2
End If

;calls the moveball function if move=2
If move=2
   moveball()
End If

End Function


;Moving the ball
Function moveball()

;All physics behind moving the ball and bouncing it.
bally=bally+ballydir*speed
ballx=ballx+ballxdir*speed
If bally<0 ballydir=1 ;PlaySound(blope)
If bally>600 move=1 ballydir=-1 ballsleft=ballsleft-1
If ballx<0 ballxdir=1 ;PlaySound(blope)
If ballx>800 ballxdir=-1 ;PlaySound(blope)

End Function

;All the games collisions..
Function addcollisions()

;collisions
If ImagesCollide(paddle,paddlex,paddley,0,ball,ballx,bally,0)
   ballxdir=(ballx - paddlex)*.025
   ballydir=-1
   ;PlaySound(blope)
End If

End Function

;Draws the images
Function drawmyimages()

;Draws the images...
DrawImage paddle,paddlex,paddley
DrawImage ball,ballx,bally
If ballsleft>2 DrawImage life3,680,580
If ballsleft>1 DrawImage life2,700,580
If ballsleft>0 DrawImage life1,720,580
If ballsleft=0 ballsleft=3

End Function

;Text maths ect...
Function addtext()

;All the text..
Text 20,50,"Breakout physics by Yahfree"
Text 20,80,"Controls: Press ESC to exit, LMB to release the ball,"
Text 20,100," mouse to move, A accelerate ball, Z decelerate ball."
Text 20,140,"Status':"
Text 20,160,"Ballspeed: "+speed+" MPH"
Text 20,180,"Ball X: "+ballx
Text 20,200,"Ball Y: "+bally
If ballxdir#<0 And move=2
Text 20,220,"Ball direction Left or right? Left"
Else
Text 20,220,"Ball direction Left or right? Right"
End If
If ballydir<0 And move=2
Text 20,240,"Ball direction Up or Down? Up"
Else
Text 20,240,"Ball direction Up or Down? down"
End If

End Function
