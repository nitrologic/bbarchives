; ID: 48
; Author: Jim Brown
; Date: 2001-09-22 10:16:08
; Title: Stereo View
; Description: Combine 2 images to create a 3rd image with depth

; Stereo 3D Image
; ===============

; Author: Jim Brown

; When this program is run you'll see two scenes which look
; identical but the moving objects on the right are offset
; according to how deep in the Z field they are.

; If you stare through the screen and try to get the two RED focal
; points (at the top of each view) to merge then you will end up
; seeing a third view which is the left and right views overlapping.
; When you get the third view you'll see that the objects have
; a depth to them, i.e. the brighter/bigger ones are nearer.

; TIP:
; Stare beyond the screen to an imaginary distance (try to imagine
; looking at the back of the monitor).  At the same time try to make
; the RED focal points meet up in the middle and converge.
; It can take a long time to master but the end result is great!

Const maxobjects=50	; maximum objects on screen
Const ScreenW=800,ScreenH=600
Const ViewW=170, ViewH=ScreenH-20 ; view width & height
Const loffs=(ScreenW/2)-ViewW-10 , roffs=ScreenW/2+10 ; left & right offsets
Const toffs=(ScreenH-ViewH)/2 ; top offset
Graphics ScreenW,ScreenH ; set up the screen
SetBuffer BackBuffer()

Type obj
  Field x#,y#,z  ; simple object containing x,y, and z vars
End Type

Global o.obj
Global num=0

; give object new coordinates
Function newcoords(o.obj)
	o\x=Rnd(10,ViewW-10) : o\y=-Rnd(20)
	o\z=Rand(1,16)
End Function

; create the objects
For d=1 To maxobjects
	o.obj=New obj
	newcoords(o)
	o\y=Rnd(1,ViewH)
	num=num+1
Next

; main loop
While Not KeyDown(1)
  Cls
	; draw the background (just a simple plain color)
	Color 10,16,42
	Rect loffs,toffs,ViewW,ViewH
	Rect roffs,toffs,ViewW,ViewH
	; draw the RED focal points
	Color 200,60,60
	Oval loffs+(ViewW/2),toffs+8,8,6,1
	Oval roffs+(ViewW/2),toffs+8,8,6,1
	; process each object
	For o.obj = Each obj
		o\x=o\x#+Rnd(2)-Rnd(2)
		If o\x<5 Or o\x>=ViewW-8 Then newcoords(o)
		If o\y>ViewH-14 Then newcoords(o)
	Next
	; draw each object on screen (low to high order)
	For d=1 To 16 ; there are 16 levels of depth available
		osize#=3+(d*500)/950 ; object size
		Color d*15,d*15,d*15 ; color of object
		For o.obj = Each obj
			If o\z=d
				o\y=o\y+Float(d*100)/600 +.4
				If o\y>5
					Oval loffs+o\x,toffs+o\y,osize,osize,1        ; x is normal for LEFT view
					Oval roffs+o\x+(16-d),toffs+o\y,osize,osize,1 ; x is offset for RIGHT view
				EndIf
			EndIf
		Next
	Next
	; show text message at the bottom of the screen
	Color 60,105,25
	Text 20,ScreenH-40,"Objects = "+num
	Text 20,ScreenH-20,"Hold SPACEBAR to pause"
	; use this line to show how much frame time is left
	; Line 0,ScanLine(),ScreenW,ScanLine()
	Flip
	While KeyDown(57) : Wend
Wend

End
