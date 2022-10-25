; ID: 224
; Author: jfk EO-11110
; Date: 2002-02-06 19:57:29
; Title: 2D Image Transition
; Description: Fade from Pic A to Pic B

; This Prog will fade from one Picture to an other one and back in 2D.
; Don't forget to turn off the Debugger.

Graphics 640,480

SetBuffer BackBuffer()
bild1=LoadImage("s1.jpg") ; use 2 Pictures with 640*480 Pixels
bild2=LoadImage("s3.jpg")

bild_op=CreateImage(640,1440)
VWait 10
SetBuffer ImageBuffer(bild_op)
DrawImage bild1,0,0
DrawImage bild1,0,480
DrawImage bild2,0,960
SetBuffer BackBuffer()
DrawImage bild_op,0,0

bmax=15

; mainloop
While a<>27
	a=GetKey()
	Gosub myfader12
	Gosub myfader21
Wend
End

.myfader12
For b=0 To bmax
	SetBuffer ImageBuffer(bild_op):LockBuffer:fx=0:fy=0:Gosub zeichne:UnlockBuffer:SetBuffer BackBuffer():DrawImage bild_op,0,0:Flip
	SetBuffer ImageBuffer(bild_op):LockBuffer:fx=1:fy=0:Gosub zeichne:UnlockBuffer:SetBuffer BackBuffer():DrawImage bild_op,0,0:Flip
	SetBuffer ImageBuffer(bild_op):LockBuffer:fx=0:fy=1:Gosub zeichne:UnlockBuffer:SetBuffer BackBuffer():DrawImage bild_op,0,0:Flip
	SetBuffer ImageBuffer(bild_op):LockBuffer:fx=1:fy=1:Gosub zeichne:UnlockBuffer:SetBuffer BackBuffer():DrawImage bild_op,0,0:Flip
	If KeyHit(1) Then Exit
Next
Return
.myfader21
For b=bmax To 0 Step -1
	SetBuffer ImageBuffer(bild_op):LockBuffer:fx=0:fy=0:Gosub zeichne:UnlockBuffer:SetBuffer BackBuffer():DrawImage bild_op,0,0:Flip
	SetBuffer ImageBuffer(bild_op):LockBuffer:fx=1:fy=0:Gosub zeichne:UnlockBuffer:SetBuffer BackBuffer():DrawImage bild_op,0,0:Flip
	SetBuffer ImageBuffer(bild_op):LockBuffer:fx=0:fy=1:Gosub zeichne:UnlockBuffer:SetBuffer BackBuffer():DrawImage bild_op,0,0:Flip
	SetBuffer ImageBuffer(bild_op):LockBuffer:fx=1:fy=1:Gosub zeichne:UnlockBuffer:SetBuffer BackBuffer():DrawImage bild_op,0,0:Flip
If KeyHit(1) Then Exit
Next
Return

.zeichne
For y = 0 To 479 Step 2
	For x = 0 To 639 Step 2

		f1=ReadPixelFast(x+fx,y+480+fy) And $ffffff
		f1r=f1 And $ff0000
		f1g=f1 And $ff00
		f1b=f1 And $ff

		f2=ReadPixelFast(x+fx,y+960+fy) And $ffffff
		f2r=f2 And $ff0000
		f2g=f2 And $ff00
		f2b=f2 And $ff

		b2#=b
		bma#=bmax
		bbm#=b2#/bma#
		b2m#=(bma#-b2#)/bma#

		f3r=((f2r*bbm#)+(f1r*b2m#)) And $ff0000
		f3g=((f2g*bbm#)+(f1g*b2m#)) And $ff00
		f3b=((f2b*bbm#)+(f1b*b2m#)) And $ff

		f3=f3r Or f3g Or f3b
		WritePixelFast x+fx,y+fy,f3
	Next 
Next
Return
