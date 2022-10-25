; ID: 1385
; Author: Pete
; Date: 2005-05-27 11:52:59
; Title: bouncy
; Description: bouncy sprite simulation

;Bouncy
;Copyright ©2005 Pete Harrison
;written by Pete Harrison

Const scrwidth=1280
Const scrheight=1024


Graphics scrwidth,scrheight

SetBuffer BackBuffer()

Global ball=LoadImage("c:\bouncy\graphics\six.png")

Global xpos%=scrwidth/2		;integer
Global ypos%=scrheight-96		;integer
Global xvel#=0			;floating point
Global yvel#=0			;floating point
Global yvelram#=0		;floating point
Global ballactive%=0		;integer
Global gravity#=0.5				;floating point

;main menu loop
.menuloop

While Not KeyDown(1)

Cls

Gosub startball
Gosub moveball
Gosub drawball

Flip
Wend

End


.startball
If ballactive=1 Return
If MouseDown(1)=1 Then yvelram=16:yvel=16:ballactive=1
Return

.moveball
If ballactive=0 Return
yvel=yvel-gravity
ypos=ypos-yvel
If ypos>=scrheight-96 Goto bounceball
Return

.bounceball
If yvel=0 Then ballactive=0:Return
ypos=scrheight-96
yvelram=yvelram/2
yvel=yvelram
Return

.drawball
DrawImage ball,xpos,ypos
Return
