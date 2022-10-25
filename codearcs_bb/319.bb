; ID: 319
; Author: smitty
; Date: 2002-05-10 23:04:00
; Title: antialias/blend
; Description: anti-alias/blend sorta like dpaint had..

; anti alias/blend example (for sprite editor maybe?)
Graphics 800,600,32
Global backgnd=CreateImage(800,600);
Global mx,my ;global mouse coords
Dim zz(7),rt(7),gt(7),bt(7) ;holds colour values
; draw a cursor
Global cursor=CreateImage(32,32)
SetBuffer ImageBuffer(cursor)
Color $aa,$aa,$aa
Oval 0,0,32,32,0
MidHandle cursor

SetBuffer BackBuffer()
;draw the large grid
drawgrid()
;draw a random colour palette
For x=0 To 31
	For y=0 To 3
		Color Rnd(255),Rnd(255),Rnd(255)
		Rect (x*16)+16,(y*16)+528,16,16,1
	Next
Next
; draw a frame for the pixel grid
Color $ff,$ff,$ff
Rect 598,198,36,36,0
;copy screen to background
CopyRect(0,0,800,600,0,0,BackBuffer(),ImageBuffer(backgnd))


;main loop
While Not KeyHit(1)
CopyRect(0,0,800,600,0,0,ImageBuffer(backgnd),BackBuffer())
mx=MouseX()
my=MouseY()
tmx=mx/16
tmy=my/16
DrawImage cursor,mx,my
If mx<512 And my<512 ;check if within the grid area
	If MouseDown(1); normal draw 
		SetBuffer ImageBuffer(backgnd)
		Rect  tmx*16,tmy*16,16,16,1
		Plot tmx+600,tmy+200
	EndIf 
	If MouseDown(2);anti alias
		SetBuffer ImageBuffer(backgnd)
		;get the surrounding colour values		
		zz(0)=ReadPixel(tmx+600,tmy+200-1) ;get color of pixel top
		zz(1)=ReadPixel(tmx+600,tmy+200+1) ;get color of pixel bottom
		zz(2)=ReadPixel(tmx+600+1,tmy+200); pixel to right	
		zz(3)=ReadPixel(tmx+600-1,tmy+200); pixel to left
		zz(4)=ReadPixel(tmx+600-1,tmy+200-1); pixel to topleft
		zz(5)=ReadPixel(tmx+600+1,tmy+200-1); pixel to topright
		zz(6)=ReadPixel(tmx+600-1,tmy+200+1); pixel to leftbottom
		zz(7)=ReadPixel(tmx+600+1,tmy+200+1); pixel to rightbottom
				
		;calculate  color value for the anti-aliased pixel
		antialias()
		;draw with the anti-aliased Color
		Rect  tmx*16,tmy*16,16,16,1 ;draw on grid
		Plot tmx+600,tmy+200 ;draw at pixel size
	EndIf
Else
	; change drawing color
	; this  checks any area outside the grid
	; not just the palette bar
	If MouseDown(1)
		GetColor mx,my
	EndIf
EndIf
drawgrid()
SetBuffer BackBuffer()
Flip
Wend

End

Function antialias()
; mask out alpha
For f=0 To 7
	zz(f)=zz(f) And $ffffff
Next

;seperate r,g,b values
For f=0 To 7
	rt(f)=(zz(f) Shr 16) And $ff
	gt(f)=(zz(f) Shr 8) And $ff
	bt(f)=zz(f) And $ff
Next
	
; add samples then  average.
For f=0 To 7
	rx=rx+rt(f)
	gx=gx+gt(f)
	bx=bx+bt(f)
Next
rx=rx Shr 3
gx=gx Shr 3
bx=bx Shr 3
; assign new color
Color rx,gx,bx
	
End Function






Function drawgrid()
	r=ColorRed():g=ColorGreen():b=ColorBlue()
	Color $80,$80,$80
	For x=0 To 31
		For y=0 To 31
			Rect x*16,y*16,16,16,0
		Next
	Next
	Color r,g,b
End Function
