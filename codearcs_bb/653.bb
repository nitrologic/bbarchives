; ID: 653
; Author: BlitzSupport
; Date: 2003-04-14 17:03:11
; Title: TextureQuad
; Description: Draws an image on an arbitrary quad (4-sided shape)...

; -----------------------------------------------------------------------------
; 2D quad texturer by james @ hi - toro . com
; -----------------------------------------------------------------------------





; -----------------------------------------------------------------------------
; 					**** CHANGE THIS BEFORE RUNNING!!! ****
; -----------------------------------------------------------------------------
	picture$ = "boing.bmp"					; An image on your hard drive...
; -----------------------------------------------------------------------------
; Note: picture size is irrelevant in speed terms; only quad size matters.
; -----------------------------------------------------------------------------
; Note also that you can see a very simple demo by uncommenting the appropriate
; line at the top of the demo section (the simple demo source is at the bottom).
; -----------------------------------------------------------------------------





; -----------------------------------------------------------------------------
; Current 'issues'...
; -----------------------------------------------------------------------------
; Holes in rendered image due to line algorithm. Got some ideas, though feel
; free to beat me to it. Looking for any speedups too!
; -----------------------------------------------------------------------------
; Slight problem with right/bottom edges with certain image sizes. Should be
; able to sort this!
; -----------------------------------------------------------------------------





; -----------------------------------------------------------------------------
; Globals and constants required...
; -----------------------------------------------------------------------------

Global GfxWidth, GfxHeight
Const RenderDEBUG = True

; -----------------------------------------------------------------------------
; Types required...
; -----------------------------------------------------------------------------

Type Quad
	Field x [3]
	Field y [3]
End Type

; -----------------------------------------------------------------------------
; Functions required...
; -----------------------------------------------------------------------------

Function InitRenderer ()
	GfxWidth = GraphicsWidth () - 1
	GfxHeight = GraphicsHeight () - 1
End Function

; BELOW: x0, y0, etc, are the x/y positions of each corner of the quad. Define
; them in clockwise order...

Function CreateQuad.Quad (x0, y0, x1, y1, x2, y2, x3, y3)
	q.Quad = New Quad
	q\x[0] = x0: q\y[0] = y0
	q\x[1] = x1: q\y[1] = y1
	q\x[2] = x2: q\y[2] = y2
	q\x[3] = x3: q\y[3] = y3
	Return q
End Function

Function Dist# (x0#, y0#, x1#, y1#)
	Return Sqr (((x1 - x0) * (x1 - x0)) + ((y1 - y0) * (y1 - y0)))
End Function

Function TextureQuad (q.Quad, image)

	; ----------------------------------------------------------------------------
	; Check globals have been set...
	; ----------------------------------------------------------------------------

	; NOTE: RenderDEBUG is a constant set at top of code. Setting it to False will
	; result in this part of the code not being compiled, so you can leave it in... :)
	
	If RenderDEBUG
		If (Not GfxWidth) Or (Not GfxHeight)
			RuntimeError "Programmer error!" + Chr (10) + Chr (10) + "You need to call InitRenderer () after setting graphics mode!"
		EndIf
	EndIf
	
	; ----------------------------------------------------------------------------
	; A quad, with a long side and a short side, points marked...
	; ----------------------------------------------------------------------------

	;	o---------------
	;	|0				------------			
	;	|							------------o
	;	|										|1
	;	|										|
	;	|										|
	;	|										|
	;	|										|
	;	|										|
	;	|										|
	;	|										|
	;	|										|
	;	|										|
	;	|										|
	;	|										|
	;	|										|
	;	|							------------o
	;	|					--------			 2
	;	|			--------
	;	o-----------
	;	 3
	
	; ----------------------------------------------------------------------------



	; ----------------------------------------------------------------------------
	; Cache image information...
	; ----------------------------------------------------------------------------

	IW = ImageWidth (image) - 1
	IH = ImageHeight (image) - 1
	IB = ImageBuffer (image)
	
	; ----------------------------------------------------------------------------
	; Compare lengths of left side and right side...
	; ----------------------------------------------------------------------------

	leftside# = Dist (q\x[0], q\y[0], q\x[3], q\y[3])
	rightside# = Dist (q\x[1], q\y[1], q\x[2], q\y[2])

	; ----------------------------------------------------------------------------
	; Get index numbers for points making up each side...
	; ----------------------------------------------------------------------------
	
	If leftside > rightside
	
		; Left side longer...
		
		long1  = 0
		long2  = 3
		
		short1 = 1
		short2 = 2
		
		plotdir = 1
		
	Else
		
		; Right side longer...
		
		long1  = 1
		long2  = 2
		
		short1 = 0
		short2 = 3
		
		plotdir = -1
		
	EndIf

	; ----------------------------------------------------------------------------
	; The 'plotdir' variable above corrects mirroring that occurs when right side
	; is longer (don't ask)...
	; ----------------------------------------------------------------------------





	; ----------------------------------------------------------------------------
	; OK, now we step down both sides of the quad, using the step size according to
	; the longer of the two sides...
	; ----------------------------------------------------------------------------




	
	; ----------------------------------------------------------------------------
	; Taking the long side, the division used to step down it (stepper)
	; is 1 / length of line. This is used to move down both sides...
	; ----------------------------------------------------------------------------
	
	ystepper# = 1.0 / Dist (q\x[long1], q\y[long1], q\x[long2], q\y[long2])

	; ----------------------------------------------------------------------------
	; Since the step size is variable, can't use For... Next, so I use
	; While... Wend to count up to this value...
	; ----------------------------------------------------------------------------
		
	ylimit# = 1.0 + ystepper

	; ----------------------------------------------------------------------------
	; Current position down the sides (will be from 0.0 to 1.0 along the line length)...
	; ----------------------------------------------------------------------------
	
	ypos# = 0

	; ----------------------------------------------------------------------------
	; Lock back buffer and image buffer ready for Read/WritePixelFast action...
	; ----------------------------------------------------------------------------
	
	LockBuffer BackBuffer ()
	LockBuffer IB
	
	; ----------------------------------------------------------------------------
	; OK, going down both sides...
	; ----------------------------------------------------------------------------
		
	While ypos < ylimit
		
		; ------------------------------------------------------------------------
		; Get x and y position of current point on long side...
		; ------------------------------------------------------------------------
		
		xlong# = (q\x[long1] * (1 - ypos) + q\x[long2] * ypos)
		ylong# = (q\y[long1] * (1 - ypos) + q\y[long2] * ypos)

		; ------------------------------------------------------------------------
		; Get x and y position of current point on short side...
		; ------------------------------------------------------------------------

		xshort# = (q\x[short1] * (1 - ypos) + q\x[short2] * ypos)
		yshort# = (q\y[short1] * (1 - ypos) + q\y[short2] * ypos)

		; ------------------------------------------------------------------------
		; Now we need to get the step size to traverse horizontally between the side points...
		; ------------------------------------------------------------------------
		
		xstepper# = 1.0 / Dist (xlong, ylong, xshort, yshort)

		; ------------------------------------------------------------------------
		; Since the step size is variable, can't use For... Next, so I use
		; While... Wend to count up to this value...
		; ------------------------------------------------------------------------

		xlimit# = 1.0 + xstepper
		
		; ------------------------------------------------------------------------
		; Current position between the sides (will be from 0.0 to 1.0 along the line length)...
		; ------------------------------------------------------------------------

		xpos# = 0

		; ------------------------------------------------------------------------
		; OK, go across between the two current side points...
		; ------------------------------------------------------------------------
		
		While xpos < xlimit

			; --------------------------------------------------------------------
			; Get current x and y position	on horizontal line...
			; --------------------------------------------------------------------
			
			If plotdir = 1
				plotx = ((xlong * (1 - xpos)) + (xshort * xpos))
				ploty = ((ylong * (1 - xpos)) + (yshort * xpos))
			Else
				plotx = ((xshort * (1 - xpos)) + (xlong * xpos))
				ploty = ((yshort * (1 - xpos)) + (ylong * xpos))
			EndIf

			; --------------------------------------------------------------------
			; Check the point is on the screen (NOTE GLOBALS GW AND GH)...
			; --------------------------------------------------------------------
			
			If (plotx > 0) And (ploty > 0)
				If (plotx < GfxWidth) And (ploty < GfxHeight)
				
					; ------------------------------------------------------------
					; Plot pixel from the original image by reading from the fractional
					; position across/down the image (xpos and ypos are from 0.0 to 1.0,
					; remember)...
					; ------------------------------------------------------------
					
					WritePixelFast plotx, ploty, ReadPixelFast (IW * xpos, IH * ypos, IB)

				EndIf
			EndIf

			; --------------------------------------------------------------------
			; Move across current line...
			; --------------------------------------------------------------------
					
			xpos = xpos + xstepper
	
		Wend
		
		; ------------------------------------------------------------------------
		; Move down sides...
		; ------------------------------------------------------------------------
		
		ypos = ypos + ystepper

	Wend
	
	; ----------------------------------------------------------------------------
	; Unlock buffers...
	; ----------------------------------------------------------------------------

	UnlockBuffer IB
	UnlockBuffer BackBuffer ()
	
End Function






; Goto SimpleDemo ; Uncomment for bare-minimum demo (see bottom of source code)...




; -----------------------------------------------------------------------------
; Demo...
; -----------------------------------------------------------------------------

AppTitle "Grab the circles to move the corners..."

Graphics 640, 480, 0, 2
SetBuffer BackBuffer ()

InitRenderer ()

image = LoadImage (picture$)

; Drawing a 256 x 256 image actual size, though this part isn't important!

poly.Quad = CreateQuad (100, 100, 356, 100, 356, 356, 100, 356)

ClsColor 64, 96, 128

mousepointsize = 16

Repeat

	If MouseDown (1)
		If movepoint = False
			For p = 0 To 3
				If Dist (MouseX (), MouseY (), poly\x[p], poly\y[p]) < mousepointsize
					movepoint = True
					point = p
				EndIf
			Next
		EndIf
	Else
		movepoint = False
	EndIf

	If movepoint
		poly\x[point] = MouseX ()
		poly\y[point] = MouseY ()
	EndIf
	
	Cls

	drawtime = MilliSecs ()
	TextureQuad (poly, image)
	drawtime = MilliSecs () - drawtime
	
	For p = 0 To 3
		Oval poly\x[p] - mousepointsize / 2, poly\y[p] - mousepointsize / 2, mousepointsize, mousepointsize, 0
		Text poly\x[p] + mousepointsize, poly\y[p] + mousepointsize, "Point " + p
	Next

	Text 20, 20, "Render time (millisecs): " + drawtime
	
	Flip

Until KeyHit (1)

End







.SimpleDemo

; -----------------------------------------------------------------------------
; Simpler demo (bare minimum usage)...
; -----------------------------------------------------------------------------

Graphics 640, 480, 0, 2
SetBuffer BackBuffer ()

InitRenderer ()

poly.Quad = CreateQuad (100, 150, 500, 50, 300, 400, 150, 300)

image = LoadImage (picture$)

Repeat
	Cls
	TextureQuad (poly, image)
	Flip
Until KeyHit (1)

End
