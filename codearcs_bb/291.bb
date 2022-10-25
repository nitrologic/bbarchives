; ID: 291
; Author: johnpeat
; Date: 2002-04-10 15:36:49
; Title: BBThugha
; Description: A quick 2d trippy graphics thingy

; Converted from JCthugha
; http://www.afn.org/~cthugha/ for much much more...
;
; Blitz Note: Disable Debug or this runs WAY too slow...

;Set screen size
Const gwidth=400,gheight=200
Graphics gwidth,gheight,16,2

Dim snd(gwidth)

Dim col(256)
Dim colmap(gwidth,gheight)

; Make a "palette"
For i = 0 To 63
	col(i)=i*4*256*256 ; Reds
	col(i+64)=i*4*256 ; Greens
	col(i+128)=i*4 ; Blues
	col(i+192)=i*4*256*256+i*4*256+i*4 ; Greys
Next


Repeat
	
	; Fake a 'sound wave'
	snd(0) = Rand(-gheight/3,gheight/3)
	For i = 1 To gwidth-1
		snd(i) = snd(i-1) + Rand(-10,10)
		If snd(i) < -gheight Then snd(i) = snd(i) = 2*gheight
		If snd(i) > gheight  Then snd(i) = snd(i) - 2*gheight
	Next

	;Draw black outline to ensure 'flameout'
	Color 0,0,0
	Line 0,0,gwidth-1,0
	Line 0,gheight-1,gwidth-1,gheight-1
	Line 0,0,0,gheight
	Line gwidth-1,0,gwidth-1,gheight-1

	LockBuffer FrontBuffer()

	; 'Flame' effect
	For x = 0 To gwidth-1
		For y = 0 To gheight-2
			p1 = colmap(x+1,y) 
			p2 = colmap(x,y+1)
	        p3 = colmap(x+1,y+1)
			p4 = colmap(x,y)
			If  p1 < 0 Then p1 = p1 + 256
			If  p2 < 0 Then	p2 = p2 + 256
			If  p3 < 0 Then p3 = p3 + 256
			If  p4 < 0 Then p4 = p4 + 256
			s = p1 + p2 + p3 + p4;
			s = s / 4;
			If s > 0 Then s = s - 1
			colmap(x,y)=s
	        WritePixelFast x,y,col(s)
		Next
		colmap(x,gheight/2+snd(x)/3)=255
		WritePixelFast x,gheight/2+snd(x)/3,$ffffff ; White
	Next

	UnlockBuffer FrontBuffer()

Until KeyHit(1)

End
