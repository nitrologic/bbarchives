; ID: 629
; Author: Markus Rauch
; Date: 2003-03-18 15:37:30
; Title: Fast Smileys
; Description: Fast Smileys moving around

; Blitz Basic 2D Example

; MR 18.03.2003

; 640x640 =5000 Sprites 16x16 ohne zu ruckeln mit Geforce4 TI4200 !!! :-)))))

Const width=640 , height=480

Graphics width,height,16,1
SetBuffer BackBuffer()

Global numcolours=256	; play with this value
SeedRnd(312498756)		; and this too

Type Ball
	Field x,y,xs,ys
	Field col
End Type

Global bcnt=0

Dim ball_image(numcolours)

For loop=0 To numcolours
	rff=70:gff=50:bff=70: Color rff+Rnd(255-rff),gff+Rnd(255-gff),bff+Rnd(255-bff)

	Oval 0,0,16,16

	; draws eyes and mouth..
	Color 4,4,4
	Plot 5,5:Plot 11,5
	Plot 4,8:Plot 4,9:Plot 5,10 Plot 12,8:Plot 12,9:Plot 11,10: Line 6,11,10,11
	
	ball_image(loop)=CreateImage( 16,16 )
	GrabImage ball_image(loop),0,0
  MidHandle ball_image(loop)
Next

Color 255,255,0

CreateBalls()

Local t#

While Not KeyDown( 1 )
 
  t=MilliSecs()
	Cls
	Text 0,0,"Balls="+bcnt
	Text 0,FontHeight(),"Arrow key left to remove - Arrow key right to add"
	UpdateBalls()
	RenderBalls()
	
  While Abs(MilliSecs()-t)<10.0 
  Wend

	Flip
Wend

End

Function CreateBalls()

  Local dx,dy,k

	For k=1 To 2
		bcnt = bcnt + 1
		b.Ball=New Ball
		b\x=Rnd( 8,width-8 )
		b\y=Rnd( 8,height-8 )

    dx=Rnd(0,1) 
    If dx=0 Then dx=-1 
    dy=Rnd(0,1) 
    If dy=0 Then dy=-1 

		b\xs=Rnd(1,4 )*dx
		b\ys=Rnd(1,4 )*dy
		b\col=Rnd(numcolours)
	Next

End Function

Function RemoveBalls()

	For k=1 To 2
	 bcnt = bcnt - 1
   Delete First Ball
  Next

End Function

Function UpdateBalls()

	If KeyDown( 205 )
		CreateBalls()
	Else If KeyDown( 203 )
		RemoveBalls()
	EndIf
	For b.Ball=Each Ball
		b\x=b\x+b\xs
		If b\x<8 Or b\x>width-8 Then b\xs=-b\xs:b\x=b\x+b\xs
		b\y=b\y+b\ys
		If b\y<8 Or b\y> height-8 Then b\ys=-b\ys:b\y=b\y+b\ys
	Next
	
End Function

Function RenderBalls()

	For b.Ball=Each Ball
		DrawImage ball_image(b\col),b\x,b\y
	Next

End Function
