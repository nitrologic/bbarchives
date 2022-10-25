; ID: 402
; Author: Glitch01
; Date: 2002-08-22 07:35:09
; Title: Rounded Rectangles
; Description: Works very similar to the Rect() function but draws hollow, beveled rectangles instead.

;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
; 
; Rounded Rectangles
;
; Stephen C. Demuth -- sdemuth@flashmail.com
; August 22, 2002
;
;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Graphics 800,600
SetBuffer BackBuffer()

SeedRnd(MilliSecs())

Cls()

Color 255,255,0
RRect(25,25,400,50,10)

Color 0,255,0
RRect(100,100,100,300,50)

Color 0,255,255
RRect(50,50,200,200,25)

Flip()
WaitKey
End



;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
;
; RRect(x,y,width,height,radius=5)
;
;~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
Function RRect(x,y,width,height,radius=5)

	If radius > width/2 Then radius = width/2
	If radius > height/2 Then radius = height/2

	;---DRAW BORDERS
	Line x+radius,y,x+width-radius,y			   ;Top
	Line x+radius,y+height,x+width-radius,y+height ;Bottom	
	Line x,y+radius,x,y+height-radius			   ;Left
	Line x+width,y+radius,x+width,y+height-radius  ;Right	


	;---DRAW CORNERS

	;Upper Left
	For deg = 90 To 180
		yp = Sin(deg) * radius * -1 + y + radius
		xp = Cos(deg) * radius + x + radius		
		Plot xp,yp
	Next

	;Lower Left
	For deg = 180 To 270
		yp = Sin(deg) * radius * -1 + y + height - radius
		xp = Cos(deg) * radius + x + radius		
		Plot xp,yp
	Next

	;Upper Right
	For deg = 0 To 90
		yp = Sin(deg) * radius * -1 + y + radius
		xp = Cos(deg) * radius + x + width - radius		
		Plot xp,yp
	Next

	;Lower Right
	For deg = 270 To 359
		yp = Sin(deg) * radius * -1 + y + height - radius
		xp = Cos(deg) * radius + x + width - radius		
		Plot xp,yp
	Next

End Function
