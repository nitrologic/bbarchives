; ID: 1395
; Author: Pete
; Date: 2005-06-12 11:20:40
; Title: star field
; Description: simple star field simulation

;StarField
;game code,game idea,and graphics Copyright ©2005 pete harrison


Global scrwidth=1024
Global scrheight=768

Graphics scrwidth,scrheight

SetBuffer BackBuffer()

Global star1=LoadAnimImage("star1.bmp",6,6,0,4)	;small red,blue,green,& white stars
Global star2=LoadAnimImage("star2.bmp",6,6,0,4)	;medium	"    "     "      "     "
Global star3=LoadAnimImage("star3.bmp",6,6,0,4)	;large  "    "     "      "     "

Global nostars=100
Global z_start=300
Global D=250
Global centrex=scrwidth/2-D	;calculate x offset to position stars in centre of the screen
Global centrey=scrheight/2-D;    "     y    "    "     "      "    "    "    "   "    "

Type starfile
Field size%
Field colour%
Field xpos#
Field ypos#
Field zpos#
Field screenx%
Field screeny%
End Type

Dim stars.starfile(nostars)

initstars()

;main loop
While Not KeyDown(1)
Cls

movestars()
drawstars()

VWait
Flip
Wend
End

Function initstars()

For n=0 To nostars-1
stars(n)=New starfile
stars(n)\size=1
stars(n)\colour=Rnd(3)
stars(n)\xpos=Rnd(100)-50
stars(n)\ypos=Rnd(100)-50
stars(n)\zpos=Rnd(z_start)
Next

End Function

;	Star Field Formula
;	screenx = xpos*D/zpos+D
;	screeny = ypos*D/zpos+D
;
Function movestars()

For n=0 To nostars-1
stars(n)\zpos=stars(n)\zpos-2
If stars(n)\zpos<0 stars(n)\zpos=300:stars(n)\colour=Rnd(3)
Restore stardata
m=Int(stars(n)\zpos/100)
For p=1 To m
Read stars(n)\size
Next
stars(n)\screenx=(stars(n)\xpos*D)/stars(n)\zpos+D
stars(n)\screeny=(stars(n)\ypos*D)/stars(n)\zpos+D
Next

End Function

Function drawstars()

For n=0 To nostars-1
If stars(n)\size=1 Then DrawImage (star1,stars(n)\screenx+centrex,stars(n)\screeny+centrey,stars(n)\colour)
If stars(n)\size=2 Then DrawImage (star2,stars(n)\screenx+centrex,stars(n)\screeny+centrey,stars(n)\colour)
If stars(n)\size=3 Then DrawImage (star3,stars(n)\screenx+centrex,stars(n)\screeny+centrey,stars(n)\colour)
Next

End Function


.stardata
Data 3,2,1
