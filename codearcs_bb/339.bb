; ID: 339
; Author: BlitzSupport
; Date: 2002-06-07 22:05:46
; Title: Martin Process
; Description: Just a nice fractal pattern


; The 'Martin Process'... from 'Computers and Chaos' by Conrad Bessant,
; converted from AmigaBASIC to Blitz by punkrockprogrammer@hi-toro.com :)

; CONTROLS: Up and down cursors restart with different zoom levels.

Graphics 1024, 768

gw = GraphicsWidth ()
gh = GraphicsHeight ()
gw2 = gw / 2
gh2 = gh / 2

scaler# = 0.8

.loop

a# = 45
b# = 2
c# = -300

x# = 0
y# = 0
xnew# = 0
ynew# = 0

LockBuffer ()

Repeat

	If MilliSecs () - ticks > 100
		rgb = ((Rnd (64, 255) Shl 16) + (Rnd (64, 255) Shl 8) + Rnd (64, 255))
		ticks = MilliSecs ()
	EndIf
	
	xnew = y - Sgn (x) * Sqr (Abs (b * x - c))
	ynew = a - x
	x = xnew
	y = ynew

	plotx = gw2 + x * scaler
	ploty = gh2 - y * scaler
	
	If (plotx > -1) And (plotx < gw - 1) And (ploty > -1) And (ploty < gh - 1)
		WritePixelFast plotx, ploty, rgb
	EndIf
	
	If KeyHit (200) Then scaler = scaler * 2.0: UnlockBuffer (): Cls: Goto loop
	If KeyHit (208) Then scaler = scaler / 2.0: UnlockBuffer (): Cls: Goto loop
		
Until KeyHit (1)

UnlockBuffer ()

End
