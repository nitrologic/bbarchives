; ID: 1109
; Author: Mr Brine
; Date: 2004-07-17 08:42:33
; Title: Gamma Fader
; Description: Alternative RGB Fader

; (c)oded by Mr Brine
;
; - r/g/bint: rgb intensity (0 - minimum, 255 - maximum)
; - requires full screen mode to work
; - wont work with debug on


test()


Function test()

	Graphics 640, 480
		
	Local r = 255, g = 255, b = 255, x
		
	Repeat 
		
		For x = 0 To 620 Step 20
			
			Color Rand(0,255),Rand(0,255),Rand(0,255)
			Rect x, 0, 20, 480
			
		Next
		
		Color 0, 0, 0
		Rect 10, 10, 250, 90
		
		Color 255, 255, 255
		Text 11, 11, "Keys"
		Text 11, 23, "Q: r+   W: g+    E: b+"
		Text 11, 36, "A: r-   S: g-    D: b-"
		Text 11, 48, "Esc: Quit"
		Text 11, 60, "LMB: Regenerate background"
		Text 11, 72, "RMB: Reset r, g, b intensity"
		
		Flip
			
		Repeat
		
			If(KeyDown(30))
			
				r = r - 1
						
				If(r < 0)
				
					r = 0
					
				End If 
			
			End If 
			
			If(KeyDown(16))
			
				r = r + 1
			
				If(r > 255)
			
					r = 255
					
				End If 
			
			End If 
			
			If(KeyDown(31))
			
				g = g - 1
						
				If(g < 0)
				
					g = 0
					
				End If 
			
			End If 
			
			If(KeyDown(17))
			
				g = g + 1
			
				If(g > 255)
			
					g = 255
					
				End If 
			
			End If 	
		
			If(KeyDown(32))
			
				b = b - 1
						
				If(b < 0)
				
					b = 0
					
				End If 
			
			End If 
			
			If(KeyDown(18))
			
				b = b + 1
			
				If(b > 255)
			
					b = 255
					
				End If 
			
			End If 
			
			GammaFader(r, g, b)
		
			If(KeyHit(1)) Return 
			
			If(MouseHit(2)) 
			
				r = 255 
				g = 255
				b = 255
			
			End If 	
		
		Until MouseHit(1)
		
	Forever 

End Function 



Function GammaFader(rint#, gint#, bint#)

	Local r#, g#, b#

	Local ri# = rint / 255.0
	Local gi# = gint / 255.0
	Local bi# = bint / 255.0

	For w = 0 To 255

		SetGamma w, w, w, r, g, b
		r = r + ri
		g = g + gi
		b = b + bi

	Next	

	UpdateGamma
	
End Function
