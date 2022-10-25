; ID: 1067
; Author: Rob Farley
; Date: 2004-06-03 09:44:28
; Title: Fast Line
; Description: Draws a line horizonal or vertical very quickly

; Fast line, 2004 Rob Farley
; rob@mentalillusion.co.uk

;==========================================================
; example code
;==========================================================

Graphics 640,480
SetBuffer BackBuffer()
LockBuffer BackBuffer()
For x=0 To 200 Step 20
For y=0 To 200 Step 20
fline(0,y,200,y,255,128,0)
fline(x,0,x,200,255,0,0)
Next
Next
UnlockBuffer BackBuffer()
Flip
WaitKey

;==========================================================
; end of example code
;==========================================================


Function fLine(x,y,x1,y1,r=255,g=255,b=255)
; fLine will only draw horizonal or vertical lines, no diagonals
; Defaults to a white line.

argb=(b Or (g Shl 8) Or (r Shl 16) Or ($ff000000))

If x=x1
	If y>y1 Then t=y1:y1=y:y=t
	For n=y To y1
	WritePixelFast x,n,argb,BackBuffer()
	Next
EndIf

If y=y1
	If x>x1 Then t=x1:x1=x:x=t
	For n=x To x1
	WritePixelFast n,y,argb,BackBuffer()
	Next
EndIf 
End Function
