; ID: 1341
; Author: Rook Zimbabwe
; Date: 2005-04-01 01:45:09
; Title: Fade in out
; Description: Fade in and Out your intro screen

; fadein by Rook Zimbabwe
;
Graphics3D 800,600,16,1
SetBuffer BackBuffer()
AppTitle "FADER 1.0"

cam = CreateCamera()
PositionEntity cam,0,0,-10

Cls

startright=LoadTexture("image/TWSintro.png")

Global ghost
Global b#
Global flipper = CreateSprite()
EntityTexture flipper,startright

PositionEntity flipper,0,0,-8.5

oldtime = MilliSecs()
b#=0
ghost=1

While Not KeyHit (1)

Select ghost
	Case 1
		fadein()
	Case 0
		fadeout()
End Select

RenderWorld()

Text 0,12,"ALPHA: "+b#

Flip

Cls

Wend

End
; ########################### FUNCTIONS
;
; ########################### FADEIN
Function fadein()

If MilliSecs() > oldtime+1500 Then
b# = b# + .01
If b# > 1 Then 
	b# = 1
	ghost = 0
EndIf
EntityAlpha flipper,b#
oldtime = MilliSecs()

EndIf

End Function
; ########################### FADEOUT
Function fadeout()

If MilliSecs() > oldtime+1500 Then
b#=b#-.01
If b#<.001 Then 
	b#=0
	ghost = 1
EndIf
EntityAlpha flipper,b#
oldtime = MilliSecs()

EndIf

End Function
; ###########################
