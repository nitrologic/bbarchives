; ID: 911
; Author: Mr Brine
; Date: 2004-02-04 12:30:04
; Title: Distance Between 2 Rectangles
; Description: Calculates the distance between two rectangles (FAST!)

; (c)oded by Mr Brine
;
; Distance Between 2 Rectangles
;
; RMB - Set Rect
; LMB - Quit

Graphics 640, 480, 32

test()

Function Test()
	
	Local cx = 250, cy = 200, cw = 300, ch = 20
	Local mx, my, mw = 120, mh = 40
	
	Repeat
	
		Cls

		Color $ff, 0, 0
		Rect mx, my, mw, mh
		
		Color 0, $ff, 0
		Rect cx, cy, cw, ch
		
		Color $ff, $ff, $ff
		Text 0, 00, "mx:" + mx + "  my:" + my + "  mw:" + mw + "  mh:" + mh
		Text 0, 12, "cx:" + cx + "  cy:" + cy + "  cw:" + cw + "  ch:" + ch
		Text 0, 36, "Dist:" + Dist(cx, cy, cw, ch, mx, my, mw, mh)
	
		If(MouseHit(2) = True)
		
			cx = MouseX()
			cy = MouseY()
		
		Else
			
			mx = mx + MouseXSpeed()
			my = my + MouseYSpeed()
		
		End If 
		
		If(KeyHit(200) = True) my = my - 1
		If(KeyHit(208) = True) my = my + 1
		If(KeyHit(203) = True) mx = mx - 1
		If(KeyHit(205) = True) mx = mx + 1
	
		Flip
	
	Until MouseHit(1) = True
	
End Function 



Function Dist(cx, cy, cw, ch, ox, oy, ow, oh)

	Local oct

	; Determin Octant
	;
	; 0 | 1 | 2
	; __|___|__
	; 7 | 9 | 3
	; __|___|__
	; 6 | 5 | 4

	If(cx + cw <= ox)
		
		If(cy + ch <= oy)
		
			oct = 0
		
		Else If(cy => oy + oh)
		
			oct = 6
		
		Else

			oct = 7
	
		End If 
		
	Else If(cx => ox + ow)
		
		If(cy + ch <= oy)
		
			oct = 2
		
		Else If(cy => oy + oh)
		
			oct = 4
		
		Else

			oct = 3
	
		End If 
		
	Else If(cy + ch <= oy)
	
		oct = 1
	
	Else If(cy => oy + oh)
	
		oct = 5
	
	Else
	
		Return 0

	End If 

	; Determin Distance based on Quad
	;
	Select oct
	
		Case 0
		
			cx = (cx + cw) - ox
			cy = (cy + ch) - oy
			
			Return -(cx + cy) 
		
		Case 1
		
			Return -((cy + ch) - oy)
		
		Case 2

			cx = (ox + ow) - cx
			cy = (cy + ch) - oy

			Return -(cx + cy)
		
		Case 3
		
			Return -((ox + ow) - cx)
		
		Case 4
			
			cx = (ox + ow) - cx
			cy = (oy + oh) - cy

			Return -(cx + cy)
					
		Case 5
		
			Return -((oy + oh) - cy)
		
		Case 6
		
			cx = (cx + cw) - ox
			cy = (oy + oh) - cy

			Return -(cx + cy)

		Case 7
		
			Return -((cx + cw) - ox)
	
	End Select
	
End Function
