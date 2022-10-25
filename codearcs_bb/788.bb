; ID: 788
; Author: 86it
; Date: 2003-09-04 17:06:29
; Title: Movement with sin &amp; cos
; Description: Movement with sin & cos

;==================================================================
;Project Title: Movement with sin & cos    
;Author: 86itProductions®  http://64.40.92.90  
;Email: kshaffer@grics.net                   
;Version: 0.x          
;Date:  8.21.03            
;Notes:Some old stuff from the days of "AMIGA®" I dont even
;know if they are very good as I just started programming
;with Blitz® on PC let me know If you like them everything 
;will be
;in a zip file for download.  ENJOY!
;OH I forgot I like small screen programs thats why the program
;is in a 320x240 I just like mini games OLD SCHOOL Atari® you know.         
;                "AMIGA® and Blitz® Forever"
;          Thanks Guys for the support for my Blitz® v1.83                                                             
;==================================================================
Const scrX=320,scrY=240
;------------------------------------
Global info1$="Movement with Sin() & Cos()"
Global info2$="86! The Program?"
Global r1#,r2#,r3#
Global frmTim,ifrm,yscrTim#,iyTim#,xscrTim#,ixTim# ;Timers
;------------------------------------
;setup
AppTitle info1$,info2$
Graphics scrX,scrY,32,2
SetBuffer BackBuffer()
;------------------------------------
;loadgrafx
Global backGnd=LoadImage("crate1.jpg")
Global enemys=LoadAnimImage("enemy.bmp",28,28,0,4)
;------------------------------------
;loop
While Not KeyDown(1)

Cls
TileImage backGnd,ixTim,iyTim ;ixTim,iyTim
Timers()
circleMov()
strightMov()
updownMov()

Flip
Wend
End
;------------------------------------
Function Timers()
;Just tried some junk with Timers
;like scrolling the screen x&y
;animTimer
If MilliSecs() > frmTim + 128 Then 
frmTim=MilliSecs() ;reset timer
ifrm=(ifrm+1) Mod 2 ;increment
End If

;y\scrollTimer
If MilliSecs() > yscrTim + 2 Then 
yscrTim=MilliSecs() ;reset timer
iyTim=(iyTim+1) Mod 240 ;increment
End If

;x\scrollTimer
If MilliSecs() > xscrTim + 30 Then 
xscrTim=MilliSecs() ;reset timer
ixTim=(ixTim-2) Mod 320 ;increment
End If
End Function
;------------------------------------
;Circle Motion
Function circleMov()
	x1=160+Sin(r1)*32
	y1=64+Cos(r1)*32
	DrawImage enemys,x1,y1,ifrm
	r1=r1+2.5
End Function
;------------------------------------
;Forward & Backward Motion
Function strightMov()
	x2=144+Sin(r2)*144
	y2=192
	DrawImage enemys,x2,y2,ifrm
	r2=r2+2.5
End Function
;------------------------------------
;Up & Down Motion
Function updownMov()
	x3=144
	y3=124+Cos(r3)*32
	DrawImage enemys,x3,y3,2+ifrm
	r3=r3+2.5
End Function
;------------------------------------
