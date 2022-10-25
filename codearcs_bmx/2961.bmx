; ID: 2961
; Author: tesuji
; Date: 2012-07-21 14:02:54
; Title: Hilbert Curve
; Description: Hilbert continuous fractal space-filling curve

' THilbertCurve
' Port of Hilbert Curve Fractal for Blitzmax
' Tesuji 2012
' http://en.wikipedia.org/wiki/Hilbert_curve
' http://xkcd.com/195/

SuperStrict

'Rem

hilbertDemo()

Function hilbertDemo()
	Graphics 800,600
	
	Local scale:Int = 6
	Local ox:Int, oy:Int
	Local x:Int,y:Int
	Local c:Int = 0
	
	While Not KeyHit(KEY_ESCAPE)
	
		Local n:Int = 64*64
		SetColor 128,128,128
		For Local d:Int = 0 To n-1
		
			THilbertCurve.d2xy(64,d,x,y)
			DrawLine ox*scale,oy*scale,x*scale,y*scale
			If d = c
				SetColor 255,255,255
				DrawLine ox*scale,oy*scale,x*scale,y*scale
				SetColor 64,64,64
			End If
			ox = x
			oy = y
		Next
		
		c :+ 1
		c = c Mod n
	
		Flip
	
	Wend
	
	End
	
End Function

'End Rem

' -----------------------------------------------------------
Type THilbertCurve

	'convert (x,y) To d
	Function xy2d:Int(n:Int, x:Int, y:Int) 
	    Local rx:Int, ry:Int, s:Int=n/2, d:Int=0
		
		While (s>0)
   	    	rx = ((x & s) > 0)
   	     	ry = ((y & s) > 0)
   	     	d :+ s * s * ((3 * rx) ~ ry)
   	     	rot(s, x, y, rx, ry)
			s :/ 2
   	 	Wend

   		Return d
	End Function

	'convert d To (x,y)
	Function d2xy(n:Int, d:Int, x:Int Var, y:Int Var) 
	    Local rx:Int, ry:Int, s:Int=1, t:Int=d
	    x = 0
	    y = 0
		While (s < n)
	        rx = 1 & (t/2)
			Local trx:Int = t ~ rx
	        ry = 1 & trx
	        rot(s, x, y, rx, ry)
	        x :+ s * rx
	        y :+ s * ry
	        t :/ 4
			s :* 2
	    Wend
	End Function

	'rotate/Flip a quadrant appropriately
	Function rot(n:Int, x:Int Var, y:Int Var, rx:Int, ry:Int) 
	    If ry = 0 
	        If rx = 1 
	            x = n-1 - x
	            y = n-1 - y
	        End If
	 
	        'Swap x And y
	        Local t:Int  = x
	        x = y
	        y = t
	    End If
	End Function

End Type
