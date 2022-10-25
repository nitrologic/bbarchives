; ID: 2201
; Author: Nebula
; Date: 2008-01-20 14:42:13
; Title: Sand texture map generator
; Description: Sand colored (pixeled)

;
;
;
;
;
Graphics 640,480,16,2
SetBuffer BackBuffer()
;
Global bbuffer = CreateImage(GraphicsWidth(),GraphicsHeight())
;
Type bmap	
	Field map
End Type
;
SeedRnd MilliSecs()
Global n# , nn#
Global uy  = 245
Global ly  = 245
Global n2# = 360/2
Global n3# = 284
Global ax  = GraphicsWidth()
;
Dim clock360(5000)
For i=0 To 5000 : clock360(i) = Rand(360) : Next
;
Dim sy(600,1);x1,x2
; 
setscanline()
;
While KeyDown( 1 ) = False
	Cls
	;
	ms = MilliSecs()
	For i=0 To 10
		r=255-i*5
		g=255-i*9
		b=255-i*12
		tex Rand(-100,100)*i,Rand(-i*15,300-(i*10)),r,g,b;
;		q1 = q1 + 5
;		q2 = q2 + 5
;		q3 = q3 + 5
	Next
	;
	Color 255,255,255
	Text GraphicsWidth()-120,0,MilliSecs()-ms
	;
	Flip
	WaitKey
	;
	;Delay 100
	;Color 0,0,0 : Rect 0,0,100,50,True : Color 255,255,255
	;Text 0,0,outval$
	;Text 0,20,n2
	;
	;
	Flip
Wend
End
;
Function tex(x,y,r,g,b)
	SetBuffer ImageBuffer(bbuffer)
	Cls
	makeredsurface ;100,100
	connectscanline r,g,b
	im.bmap = New bmap
	im\map = CreateImage(256,256)
	GrabImage im\map,0,0
	SetBuffer BackBuffer()
	For i=0 To 4
		DrawImage im\map,Rand(400)+x,Rand(200)+y
	Next
End Function

;
Function makeredsurface(nx=0,ny=0)
;n# , nn#
setscanline

SeedRnd MilliSecs()
uy  = 245
ly  = 245
n2# = 360/2
n3# = 284
ax  = GraphicsWidth()
n=Rand(0,350)
While KeyDown(1) = False
	If n < 359 Then
	n = n + 1 : n2 = n2 + 4.1
	If n3 < 359 Then n3 = n3 + 1 Else n3=clock360(cnt+100)
	If n2>360 Then n2 = clock360(cnt+400) 
	nn = nn + .1
	Else
;	Flip
	Return
	End If
	;
	ax = (Cos(n) * 90) 			
	ay = ((Sin(n3) * (50 )))
	tx = ( ax ) 
	ty = ( ay ) + ( Cos(n2)*Rand(1,12) )
	;	
	If ty < uy Then 
		touch = 1  : uy = ty : 
  
		mrect(tx,uy,2,ly-uy,0)
		uy = 96
		ly = 96
	End If
	If ty > ly Then 
		touch = -1 : ly = ty :
		mrect(tx,(ly-uy+20),2,ly-uy,1)
		uy = 96
		ly = 96
	End If
	cnt=cnt+1
Wend

End Function

;
Function mrect(x1,y1,w1,h1#,tp)
	Local w#,h#,x#,y#

	y1=y1+100
	x1=x1+100

	x = (x1*3/2)
	y = (y1*2/2)
	w = (((w1*Rand(2,36))/2)*2)
	h = 2
	x=x/2.4;1.5
	y=(y*1)+32;2
	w=w*1;3
	h=h*1;3
	
	If x < sy(y,0) Then sy(y,0) = x
	If x+w+28 < sy(y,1) Then sy(y,1) = x+w
End Function

Function connectscanline(ar#,ag#,ab#)
		;
		Local mm#[16201]
		Local n#
		;
		For i=0 To 16200
			mm[i] = 255-n
			n = n +.01566
		Next
		n=0
		g=ag;20
		b=ab;10
		LockBuffer ImageBuffer(bbuffer)
		For nx=-4 To 4
		For ny=-4 To 4		
		For i=1 To 200	
			Color ar-mm[n],ag-mm[n],ab-mm[n]
			
			If sy(i,0) < 250 Then

			WritePixelFast sy(i,0)+nx*Rand(10)+32,i+ny*Rand(10),getrgb(ColorRed(),ColorGreen(),ColorBlue())
			WritePixelFast sy(i,1)+nx*Rand(10)+32,i+ny*Rand(10),getrgb(ColorRed(),ColorGreen(),ColorBlue())


			End If
			n = n + 1
		Next
		Next
		Next
		UnlockBuffer ImageBuffer(bbuffer)
	;
End Function

Function setscanline()
	For i=0 To GraphicsHeight()
		sy(i,0) = GraphicsWidth()/2
		sy(i,1) = GraphicsWidth()/2
	Next
End Function

Function makescanline()
	Color 100,0,0
	For i=1 To GraphicsHeight()
		If sy(i,1) <> sy(i+1,1)
			Rect sy(i,0),i,sy(i,1),1		
		End If
	Next
End Function

;Standard functions for converting colour to RGB values, for WritePixelFast and ReadPixelFast
Function GetRGB(r,g,b)
	Return b Or (g Shl 8) Or (r Shl 16)
End Function

Function GetR(rgb)
    Return rgb Shr 16 And %11111111
End Function

Function GetG(rgb)
	Return rgb Shr 8 And %11111111
End Function

Function GetB(rgb)
	Return rgb And %11111111
End Function
