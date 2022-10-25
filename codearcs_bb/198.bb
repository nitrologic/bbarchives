; ID: 198
; Author: BlitzSupport
; Date: 2002-01-21 09:12:04
; Title: Automatic 2D collision response
; Description: Makes a second image move when a first image bumps into it


; -----------------------------------------------------------------------------
; Automatic collision response in 2D...
; -----------------------------------------------------------------------------
; support@blitzbasic.com
; -----------------------------------------------------------------------------

Graphics 640, 480
AppTitle "Automatic 2D collision response"
SetBuffer BackBuffer ()
ClsColor 64, 128, 180

; -----------------------------------------------------------------------------
; Load images...
; -----------------------------------------------------------------------------

sheep1 = LoadImage ("big-moo.bmp")
MaskImage sheep1, 255, 0, 255
sheep2 = CopyImage (sheep1)

px = 10: py = 10 ; Player x and y positions

; -----------------------------------------------------------------------------
; The variables that get the resulting movement values must be floats!
; -----------------------------------------------------------------------------

cx# = 90: cy# = 90 ; Computer x and y positions

; -----------------------------------------------------------------------------
; The 'mover' variable specifies how much to move in a collision. You could
; make this dynamic within your main loop, of course...
; -----------------------------------------------------------------------------

mover# = 1

Repeat

	Cls

	If KeyDown (203) px = px - 1
	If KeyDown (205) px = px + 1
	If KeyDown (200) py = py - 1
	If KeyDown (208) py = py + 1
	
	If ImagesCollide (sheep1, px, py, 0, sheep2, cx, cy, 0)
		angle# = ATan2 (cy - py, cx - px)
		cx = cx + (mover * (Cos (angle)))
		cy = cy + (mover * (Sin (angle)))
	EndIf
	
	DrawImage sheep1, px, py
	DrawImage sheep2, cx, cy
		
	Flip
	
Until KeyHit (1)

End
