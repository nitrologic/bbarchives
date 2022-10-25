; ID: 183
; Author: Matty B
; Date: 2002-01-07 11:24:35
; Title: BlurPix
; Description: Blurs pixels into colorful mess

;This code just blends different colored pixels, if you want it to go 
;quicker reduce BLUR_W or BLUR_H and play around with the MAX_BALL value
;to get ahigher or lower intensity
;Please drop Any speed improvements or general code improvements to 
;Mattblackbeard@aol.com

;Graphics Width/Height
Const WIDTH=640,HEIGHT=480
;blur constants
Const MAX_BALL=1500,BLUR_W=320,BLUR_H=120
;divide array const
Const DIV_NUM=255*5

;ball X_pos,Y_pos,X-speed,Y_speed,Red,Green,Blue
Type ball
	Field x#,y#,x_s#,y_s#,r,g,b
End Type

;blur array
Dim in(BLUR_W,BLUR_H)
;divide array used to speed it up a bit
Dim div(DIV_NUM)

;set graphics
Graphics WIDTH,HEIGHT
SetBuffer BackBuffer()

;create an image to draw blur to
Global page = CreateImage(BLUR_W,BLUR_H)

;give each bal start pos, speed etc
setup_balls()

;Loop while ESC not pressed
While Not KeyDown(1)
	;draw balls to IN array
	draw_balls()
	;Blur IN array and draw to PAGE
	blur()
	SetBuffer BackBuffer()
	;Tile and draw PAGE
	TileBlock(page,0,0)
	Flip
Wend
End

Function blur()
;set buffer to page then lock the buffer
	SetBuffer  ImageBuffer(page)
	LockBuffer ImageBuffer(page)
	;Start X loop
		For x = 0 To BLUR_W - 1
			xm = x - 1 : xp = x + 1
			;Check we dont go outside X array bounderies
			If xm < 0 Then 
			xm = BLUR_W-1
			ElseIf xp > BLUR_W-1 Then 
			xp = 0
			EndIf
		;start y loop
		For y = 0 To BLUR_H - 1
			ym = y - 1 : yp = y + 1 
			;Check Y array bounds
			If ym < 0 Then 
			ym = BLUR_H - 1
			ElseIf yp > BLUR_H - 1 Then 
			yp = 0
			EndIf
			;Add together 5 pixel red values and divide by 5
			;for average
		cr = in(x,y) Shr 16
		cr = cr + (in(x,ym) Shr 16)
		cr = cr + (in(x,yp) Shr 16)
		cr = cr + (in(xm,y) Shr 16)
		cr = cr + (in(xp,y) Shr 16)
		cr = div(cr) - 1
		If(cr < 0)cr = 0
		cg = (in(x,y) And 65280)Shr 8
		cg = cg + (in(x,ym) And 65280)Shr 8
		cg = cg + (in(x,yp) And 65280)Shr 8
		cg = cg + (in(xm,y) And 65280)Shr 8
		cg = cg + (in(xp,y) And 65280)Shr 8
		cg = div(cg) - 1
		If(cg < 0)cg = 0
		cb = (in(x,y) And 255)
		cb = cb + (in(x,ym) And 255)
		cb = cb + (in(x,yp) And 255)
		cb = cb + (in(xm,y) And 255)
		cb = cb + (in(xp,y) And 255)
		cb = div(cb) - 1
		If(cb < 0)cb = 0
		;Write back values to IN array
		in(x,y) = cr Shl 16 Or cg Shl 8 Or cb
		;Write pixel
		WritePixelFast x,y,in(x,y)
		Next 
		Next
	UnlockBuffer ImageBuffer(page)
End Function

Function draw_balls()
	For b.ball = Each ball
	;Move Pixels
	b.ball\x = b.ball\x + b.ball\x_s
	b.ball\y = b.ball\y + b.ball\y_s
	;Check position 
	If b.ball\x < 0 Then  
	b.ball\x = BLUR_W-1
	ElseIf b.ball\x >= BLUR_W Then  
	b.ball\x = 0
	EndIf
	If b.ball\y < 0 Then  
	b.ball\y = BLUR_H-1
	ElseIf b.ball\y >= BLUR_H Then  
	b.ball\y = 0
	EndIf
	;add the pixels color val to the col values in the array
	cr = b.ball\r + (in(b.ball\x,b.ball\y) Shr 16)  
	If cr > 255 Then cr = 255
	cg = b.ball\g + (in(b.ball\x,b.ball\y) And 65280)Shr 8
	If cg > 255 Then cg = 255
	cb = b.ball\b + (in(b.ball\x,b.ball\y) And 255)
	If cb > 255 Then cb = 255
	in(b.ball\x,b.ball\y) = cr Shl 16 Or cg Shl 8 Or cb
	Next
End Function

Function setup_balls()
;Create divide table 
	For t = 0 To DIV_NUM-1
	div(t) = t / 5
	Next
	;give random values for speed, position etc 
	SeedRnd(MilliSecs())
  	For c=0 To MAX_BALL
	b.ball = New ball
	b.ball\x = Rnd(0,BLUR_W-1)
	b.ball\y = Rnd(0,BLUR_H-1)
	b.ball\x_s = Rnd(-2,2)
	b.ball\y_s = Rnd(-2,2)
	;pick a color for the pixel
	;play around with the combinations of colors ie 0,3
	r = Rand(0,5)
	Select r
	Case 0 b.ball\r = Rand(36,255)
	Case 1 b.ball\g = Rand(36,255)
	Case 2 b.ball\b = Rand(36,255)
	Case 3 b.ball\r = Rand(36,255) : b.ball\g = Rand(36,255)
	Case 4 b.ball\g = Rand(36,255) : b.ball\b = Rand(36,255)
	Case 5 b.ball\r = Rand(36,255) : b.ball\b = Rand(36,255)
	End Select
	Next 
End Function
