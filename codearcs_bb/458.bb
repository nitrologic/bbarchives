; ID: 458
; Author: djr2cool
; Date: 2002-10-12 01:15:18
; Title: Liquid Fun + Reflections
; Description: With speedup help from SunTeam

Graphics 640,480,16
SetBuffer BackBuffer() 
Global CurrentTexture=1,NextTexture=0,TempValue=0,mx,my,img
Const MAXWIDTH = 255
Const MAXHEIGHT = 255
;Change these to get different results 
Global DEPTH=-75
Global VISCOSITY#=0.0000001
Global DripRadius=12
Global DripRadiusSqu=DripRadius*DripRadius

Dim reflect(256,256)


img = LoadImage("logo.bmp") ;this is a 256x256 image
rpool = LoadImage("pool.bmp")
DrawImage rpool,0,0
For y = 0 To 255
For x = 0 To 255
 GetColor x,y
 reflect(x,y)=(ColorRed() Shl 16) + (ColorGreen() Shl 8) + ColorBlue()
Next
Next
DrawImage img,0,0
For y = 0 To 255
For x = 0 To 255
 GetColor x,y
 r1=reflect(x,y) Shr 16
 g1=reflect(x,y) Shr 8 And 255
 b1=reflect(x,y) And 255
 r2=ColorRed()
 g2=ColorGreen()
 b2=ColorBlue()
 reflect(x,y)=(((r1+r2)/2) Shl 16)+(((g1+g2)/2) Shl 8)+(((b1+b2)/2))
Next
Next


FreeImage img 
FreeImage rpool

Dim WaveMap#(4,MAXWIDTH,MAXHEIGHT) 

MoveMouse 100,100

While Not KeyDown(1) 
	Cls
	mx=MouseX()
	my=MouseY()
	If mx<3Then mx=3
	If mx>MAXWIDTH Then mx=MAXWIDTH
	If my<3Then my=3
	If my>MAXHEIGHT Then my=MAXHEIGHT
	
	UpdateWaveMap#() 

	If MouseDown(1) Then MakeDrip(mx,my,DEPTH)
	If KeyDown(57)
		For i = 1 To 360
			MakeDrip(Cos(i)*100+(255/2),Sin(i)*100+(255/2),DEPTH)
		Next
	EndIf
	
a=a+1
If MilliSecs()>ft
 b=a
 a=0
 ft=MilliSecs()+1000
EndIf
Text 1,1,Str b
	
	Flip
Wend 

Function UpdateWaveMap#() 
	LockBuffer BackBuffer()
	For y = 2 To MAXHEIGHT-2
		For x = 2 To MAXWIDTH-2 
			n#=WaveMap#(CurrentTexture,x-1,y)+WaveMap#(CurrentTexture,x+1,y)+WaveMap#(CurrentTexture,x,y-1)+WaveMap#(CurrentTexture,x,y+1)+WaveMap#(CurrentTexture,x-2,y)+WaveMap#(CurrentTexture,x+2,y)+WaveMap#(CurrentTexture,x,y-2)+WaveMap#(CurrentTexture,x,y+2)+WaveMap#(CurrentTexture,x-1,y+1)+WaveMap#(CurrentTexture,x+1,y-1)+WaveMap#(CurrentTexture,x-1,y-1)+WaveMap#(CurrentTexture,x+1,y+1)
			n#=n#/6.0
			n#=n#-WaveMap#(NextTexture,x,y)
			n#=n#-n#*VISCOSITY#
			If n#=<1 Then n#=0
			WaveMap#(NextTexture,x,y)=Int(n#)
		Next 
	Next

	TempValue=CurrentTexture 
	CurrentTexture=NextTexture 
	NextTexture=TempValue 
	
	
	For j = 1 To MAXHEIGHT-1
		For i = 1 To MAXWIDTH-1
			xoff=i
			If i >0 And i<MAXWIDTH-1
				xoff = xoff - WaveMap#(CurrentTexture,i-1,j)
				xoff = xoff + WaveMap#(CurrentTexture,i+1,j)
			EndIf
			yoff = j
			If j >0 And i<MAXHEIGHT-1
				yoff = yoff - WaveMap#(CurrentTexture,i,j-1)
				yoff = yoff + WaveMap#(CurrentTexture,i,j+1)
			EndIf
			If xoff < 0 Then xoff = 0
			If xoff > 255 Then xoff = 255
			If yoff < 0 Then yoff = 0
			If yoff > 255 Then yoff = 255
			col = reflect(xoff,yoff)
			r = col Shr 16
			g = col Shr 8 And 255
			b = col And 255
			
			r = r + WaveMap#(CurrentTexture,i,j)
			g = g + WaveMap#(CurrentTexture,i,j)
			b = b + WaveMap#(CurrentTexture,i,j)
			
			If r<0 Then r = 0
			If g<0 Then g = 0
			If b<0 Then b = 0
			If r>255 Then r = 255
			If g>255 Then g = 255
			If b>255 Then b = 255
			
			;WritePixelFast i*2,j*2,(r Shl 16) + (g Shl 8) + b
			;WritePixelFast i*2-1,j*2,(r Shl 16) + (g Shl 8) + b
			;WritePixelFast i*2,j*2-1,(r Shl 16) + (g Shl 8) + b
			;WritePixelFast i*2-1,j*2-1,(r Shl 16) + (g Shl 8) + b
			WritePixelFast i,j,(r Shl 16) + (g Shl 8) + b
		Next
	Next
	
	
	WritePixelFast mx,my,255 Shl 16
	WritePixelFast mx+1,my,255 Shl 16
	WritePixelFast mx-1,my,255 Shl 16
	WritePixelFast mx,my+1,255 Shl 16
	WritePixelFast mx,my-1,255 Shl 16

	UnlockBuffer BackBuffer()
	
End Function 

Function SquaredDist(sx,sy,dx,dy)
	Return (dx - sx) * (dx - sx) + (dy - sy) * (dy - sy)
End Function

Function MakeDrip(dmx,dmy,dep)
	For y = dmy - DripRadius To dmy + DripRadius
		For x = dmx - DripRadius To dmx + DripRadius
			If y>3 And y<253 And x>3 And x<253
				dist = SquaredDist(dmx,dmy,x,y)
				If dist<DripRadius
					fd = (depth * DripRadius - Sqr(dist))/DripRadius
					If fd >127 Then fd=127
					If fd <-127 Then fd=-127
					WaveMap#(CurrentTexture,x,y)=fd
				EndIf
			EndIf
		Next
	Next	
End Function
