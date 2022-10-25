; ID: 227
; Author: MikeK
; Date: 2002-02-10 10:56:21
; Title: Anti-aliased line
; Description: Draws an anti-aliased line from (x1,y1) to (x2,y2)

; Anti-aliased lines demo & routine.   By Mike Keith, Feb 2002
;
;  To use anti-aliased lines in your program, just take the two lines below (between ===),
;  which set up a 256-byte lookup table, and the two functions after End.
;
;  AntiLine(x1,y1,x2,y2) draws a line from (x1,y1) to (x2,y2) in the
;  current drawing color to the currently selected buffer.
;
;  You can Lock and Unlock the buffer used (before and after batches of calls to AntiLine)
;  to gain some additional speed, as is done in the sample code below, which draws
;  the same pattern using regular and anti-aliased lines, so you can see the visual difference.

Graphics 800,600,32,1

;==================================================
;  Lookup table required for Anti-aliased line routine.
Dim AlineTable%(64)

InitAlineTable   ; You MUST do this once to initialize table
;==================================================


SetBuffer BackBuffer()
For y=0 To 599
	z = y*255/599
	Color z/2,z/3,0
	Line 0,y,799,y
Next

Color 0,255,255
t1 = MilliSecs()
For i=0 To 360 Step 5
	dx = 150*Sin(i)
	dy = 150*Cos(i)
	Line 160+dx,300+dy,160-dx,300-dy
Next
t2 = MilliSecs()

Color 0,0,0
Text 50,535,"Regular lines"
Text 50,550,"Took "+Str$(t2-t1)+" ms"

If 1
LockBuffer BackBuffer()
Color 0,255,255
t3 = MilliSecs()
For i=0 To 360 Step 5
	dx = 150*Sin(i)
	dy = 150*Cos(i)
	AntiLine(600+dx,300+dy,600-dx,300-dy)
Next
t4 = MilliSecs()
UnlockBuffer BackBuffer()
EndIf

Color 0,0,0
Text 500,535,"Anti-aliased lines"
Text 500,550,"Took "+Str$(t4-t3)+" ms"

Flip

While Not(KeyHit(1))
Wend

End



;=========================================================
;  Here are the actual Anti-aliased line functions
;--------------------------------------------------------------------------
Function AntiLine(x1%, y1%, x2%, y2%)

	xd = x2-x1
	yd = y2-y1
	
	If (xd = 0 Or yd = 0)
		Line(x1,y1,x2,y2)
		Return
	EndIf

	r = ColorRed() Shl 16
	g = ColorGreen() Shl 8
	b = ColorBlue()
	
	WritePixel x1,y1,r+g+b
	WritePixel x2,y2,r+g+b
	
	If (Abs(xd) > Abs(yd))
		If (x1 > x2)
			tmp = x1: x1 = x2: x2 = tmp
			tmp = y1: y1 = y2: y2 = tmp
			xd = x2-x1
			yd = y2-y1
		EndIf
		
		grad = yd*65536/xd
		yf = y1*65536
		
		For x=x1+1 To x2-1
			yf = yf + grad		
			w = (yf Sar 10) And $3f
			y = yf Sar 16
			
			MergePixel(x,y,r,g,b,63-w)
			MergePixel(x,y+1,r,g,b,w)
		
		Next
	Else
		If (y1 > y2)
			tmp = x1: x1 = x2: x2 = tmp
			tmp = y1: y1 = y2: y2 = tmp
			xd = x2-x1
			yd = y2-y1
		EndIf
		
		grad = xd*65536/yd
		xf = x1*65536
		
		For y=y1+1 To y2-1
			xf = xf + grad		
			w = (xf Sar 10) And $3f
			x = xf Sar 16
			
			MergePixel(x,y,r,g,b,63-w)
			MergePixel(x+1,y,r,g,b,w)
			
		Next
	EndIf

End Function

;--------------------------------------------------------------------------
Function MergePixel(x,y,r,g,b,w)

	w = AlineTable(w)
	pix = ReadPixel(x,y)

	ro = pix And $ff0000
	go = pix And $ff00
	bo = pix And $ff
	
	rnew = (ro + ((w*(r-ro)) Sar 8)) And $ff0000
	gnew = (go + ((w*(g-go)) Sar 8)) And $ff00
	bnew = bo + ((w*(b-bo)) Sar 8)
	
	WritePixel x,y,rnew+gnew+bnew

End Function

;--------------------------------------------------------------------------
Function InitAlineTable()

	For i=0 To 63
		ALineTable(i) = (Sqr(Float(4*i))*16)*.4 + (4*i)*.6
	Next

End Function
