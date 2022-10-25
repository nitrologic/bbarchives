; ID: 806
; Author: Techlord
; Date: 2003-10-06 08:40:24
; Title: Simple Color Fade
; Description: The Simplest Color Fade Algorithm

Graphics 800,600,16,2
SetBuffer BackBuffer()

;from color
r=255
g=127
b=63

;to color
r2=0
g2=0
b2=0

While Not KeyHit(1)
Cls

;color fade algo
r=r+Sgn(r2-r) 
g=g+Sgn(g2-g)
b=b+Sgn(b2-b)
;end color fade algo

Color r,g,b
Rect 0,0,200,200	

Flip(True)
Wend
End
