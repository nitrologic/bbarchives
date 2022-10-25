; ID: 1605
; Author: Subirenihil
; Date: 2006-01-30 15:46:53
; Title: Fireworks II
; Description: A program for doing fireworks.

SeedRnd MilliSecs()

Const scrwid=1024
Const scrhgh=768
Const depth=32           ;This may work better at 32
Const grav#=0.055*(480.0/scrhgh)
Const intensity=250      ;Smaller numbers run faster
Const SparkIntensity=1    ;sparks generated each frame. more than this starts looking fake
Const SparkFade#=.005    ;.075 smaller numbers mean more processor required
Const frequency=25
Const fadespeed#=.015    ;.015 smaller numbers mean more processor required
Const DudRate=50         ;out of 1000 are duds
Const explodesize#=2     ;1 to 2 is about as big as you want. bigger numbers don't look good
Const variance#=1
Const spiralchance=5
Const spinchance=500
Const screenlimit=10     ;don't overdo this, to many will bog your processor down.

Graphics scrwid,scrhgh,depth,1
SetBuffer BackBuffer()

Type shot
	Field x#,y#,xs#,ys#,t,sp#,sa,spn
End Type

Type frag
	Field x#,y#,xs#,ys#,r,g,b,br#,fad#,sr,sg,sb,st#
End Type

Type sprk
	Field x#,y#,xs#,ys#,r,g,b,br#,fad#
End Type

Global currentcount=0

t=MilliSecs()
Repeat
	Cls
	LockBuffer
	If Rand(0,1000)<frequency And currentcount<screenlimit
		Launch
	EndIf
	UpDate
	Render
	dla=30-(MilliSecs()-t)
	t=MilliSecs()
	Delay dla
	UnlockBuffer
	Flip
Until KeyHit(1)

End

Function Launch()
	s.shot=New shot
	s\x=Rand(scrwid)
	s\y=scrhgh-1
	s\ys=Rnd(-7,-6)
	s\xs=Rnd(-1,1)
	s\t=Rand(-30,30)+(158000.0/scrhgh)
	If Rand(1000)<DudRate Then s\t=s\t+Rand(50,s\t-5)*Rand(-1,2)
	If (s\x+(s\t*s\xs))<-20 Or (s\x+(s\t*s\xs))>scrwid+20 Then s\xs=-s\xs
	s\sp=0
	s\sa=90
	If Rand(1000)<spiralchance
		s\sp=Rnd(30,60)
		s\sa=Rnd(360)
		s\spn=0
		If Rand(1000)<spinchance Then s\spn=1
	EndIf
End Function

Function Explode(s.shot)
	c=Rand(1,3)
	e#=Rnd(1/variance,variance)
	st#=Rnd(.25,1)
	For l=0 To c-1
		fd#=sparkfade/Rnd(1/variance/3,variance*3)
		r=Rand(1,255)
		g=Rand(1,255)
		b=Rand(1,255)
		gr#=r
		If g>gr Then gr=g
		If b>gr Then gr=b
		r=r*255/gr#
		g=g*255/gr#
		b=b*255/gr#
		sr=Rand(1,255)
		sg=Rand(1,255)
		sb=Rand(1,255)
		gr#=sr
		If sg>gr Then gr=sg
		If sb>gr Then gr=sb
		sr=sr*255/gr#
		sg=sg*255/gr#
		sb=sb*255/gr#
		For a=0 To (intensity/c)*e;+Rand(-intensity/(c*2),intensity/(c*2))
			ang=Rand(360)
			If c=1
				spd#=Rnd(.5,2)
			ElseIf c=2
				If l=0
					spd#=Rnd(.5,1.6)
				Else
					spd#=Rnd(.9,2)
				EndIf
			ElseIf c=3
				If l=0
					spd#=Rnd(.5,1.25)
				ElseIf l=1
					spd#=Rnd(.75,1.75)
				Else
					spd#=Rnd(1.25,2)
				EndIf
			EndIf
			f.frag=New frag
			f\fad=fd
			f\x=s\x
			f\y=s\y
			f\xs=s\xs+Cos(ang)*spd*explodesize*e
			f\ys=s\ys-Sin(ang)*spd*explodesize*e
			If s\y>=scrhgh-7
				f\y=scrhgh-2
				f\ys=-Abs(f\ys)/3
			EndIf
			f\r=r
			f\g=g
			f\b=b
			f\sr=sr
			f\sg=sg
			f\sb=sb
			f\br=Rnd(.01,1)
			f\st=f\br-st
		Next
	Next
	Delete s
End Function

Function Update()
	currentcount=0
	For s.shot=Each shot
		currentcount=currentcount+1
		s\x=s\x+s\xs+Cos(s\sa)
		If s\spn=1 Then s\y=s\y+Sin(s\sa)
		s\y=s\y+s\ys
		s\ys=s\ys+grav
		s\sa=(s\sa+s\sp) Mod 360
		For a=0 To SparkIntensity
			p.sprk=New sprk
			p\x=s\x
			p\y=s\y
			p\ys=(s\ys+Sin(s\sa)*s\spn)/2+Rnd(-.1,.1)
			p\xs=(s\xs+Cos(s\sa))/2+Rnd(-.1,.1)
			p\br=Rnd(.01,1)
			p\r=255
			p\g=191
			p\b=191
			p\fad=sparkfade/4
		Next
		s\t=s\t-1
		If s\t<=0 Or s\y>scrhgh-1
			Explode s
		EndIf
	Next
	For f.frag=Each frag
		f\x=f\x+f\xs
		f\y=f\y+f\ys
		f\ys=f\ys+grav/2
		f\br=f\br-fadespeed
		If f\st<f\br
			For a=0 To SparkIntensity
				p.sprk=New sprk
				p\fad=f\fad
				p\x=f\x
				p\y=f\y
				p\ys=f\ys/2
				p\xs=f\xs/2+Rnd(-.25,.25)
				p\br=Rnd(.01,f\br)
				p\r=f\sr
				p\g=f\sg
				p\b=f\sb
			Next
		EndIf
		If f\y>scrhgh
			f\ys=-(f\ys/4)
		EndIf
		If (f\x<0 And f\xs<0) Or (f\xs>0 And f\x>scrwid) Or f\br<=0
			Delete f
		EndIf
	Next
	For p.sprk=Each sprk
		p\x=p\x+p\xs
		p\y=p\y+p\ys
		p\ys#=p\ys#+grav#/10
		p\ys#=p\ys#*.95
		p\xs#=p\xs#*.95
		p\br=p\br-p\fad;SparkFade
		If p\y>scrhgh Or (p\x<0 And p\xs<0) Or (p\x>scrwid And p\xs>0) Or p\br<=0
			Delete p
		EndIf
	Next
End Function

Function Render()
	For p.sprk=Each sprk
		If p\x>=0 And p\y>=0 And p\x<=scrwid-1 And p\y<=scrhgh-1 Then WritePixelFast p\x,p\y,((p\r*p\br) Shl 16)+((p\g*p\br) Shl 8)+(p\b*p\br); CompressInts(p\b*p\br,p\g*p\br,p\r*p\br,255)
;		Color p\r*p\br,p\g*p\br,p\b*p\br
;		Plot p\x,p\y
	Next
	For f.frag=Each frag
		If f\br>fadespeed*15
			If f\x>=0 And f\y>=0 And f\x<=scrwid-1 And f\y<=scrhgh-1 Then WritePixelFast f\x,f\y,$ffffff;CompressInts(255,255,255,255)
			If f\x-1>=0 And f\y>=0 And f\x-1<=scrwid-1 And f\y<=scrhgh-1 Then WritePixelFast f\x-1,f\y,(f\r Shl 16)+(f\g Shl 8)+f\b;CompressInts(f\b,f\g,f\r,255)
			If f\x+1>=0 And f\y>=0 And f\x+1<=scrwid-1 And f\y<=scrhgh-1 Then WritePixelFast f\x+1,f\y,(f\r Shl 16)+(f\g Shl 8)+f\b;CompressInts(f\b,f\g,f\r,255)
			If f\x>=0 And f\y-1>=0 And f\x<=scrwid-1 And f\y-1<=scrhgh-1 Then WritePixelFast f\x,f\y-1,(f\r Shl 16)+(f\g Shl 8)+f\b;CompressInts(f\b,f\g,f\r,255)
			If f\x>=0 And f\y+1>=0 And f\x<=scrwid-1 And f\y+1<=scrhgh-1 Then WritePixelFast f\x,f\y+1,(f\r Shl 16)+(f\g Shl 8)+f\b;CompressInts(f\b,f\g,f\r,255)
;			Color 255,255,255
;			Plot f\x,f\y
;			Color f\r,f\g,f\b;*f\br,f\g*f\br,f\b*f\br;
;			Plot f\x-1,f\y
;			Plot f\x+1,f\y
;			Plot f\x,f\y-1
;			Plot f\x,f\y+1
		Else
			If f\x>=0 And f\y>=0 And f\x<=scrwid-1 And f\y<=scrhgh-1 Then WritePixelFast f\x,f\y,((f\r*f\br*4) Shl 16)+((f\g*f\br*4) Shl 8)+(f\b*f\br*4);CompressInts(f\b*br*4,f\g*f\br*4,f\r*f\br*4,255)
			If f\x-1>=0 And f\y>=0 And f\x-1<=scrwid-1 And f\y<=scrhgh-1 Then WritePixelFast f\x-1,f\y,((f\r*f\br) Shl 16)+((f\g*f\br) Shl 8)+(f\b*f\br);CompressInts(f\b*f\br,f\g*f\br,f\r*f\br,255)
			If f\x+1>=0 And f\y>=0 And f\x+1<=scrwid-1 And f\y<=scrhgh-1 Then WritePixelFast f\x+1,f\y,((f\r*f\br) Shl 16)+((f\g*f\br) Shl 8)+(f\b*f\br);CompressInts(f\b*f\br,f\g*f\br,f\r*f\br,255)
			If f\x>=0 And f\y-1>=0 And f\x<=scrwid-1 And f\y-1<=scrhgh-1 Then WritePixelFast f\x,f\y-1,((f\r*f\br) Shl 16)+((f\g*f\br) Shl 8)+(f\b*f\br);CompressInts(f\b*f\br,f\g*f\br,f\r*f\br,255)
			If f\x>=0 And f\y+1>=0 And f\x<=scrwid-1 And f\y+1<=scrhgh-1 Then WritePixelFast f\x,f\y+1,((f\r*f\br) Shl 16)+((f\g*f\br) Shl 8)+(f\b*f\br);CompressInts(f\b*f\br,f\g*f\br,f\r*f\br,255)
;			Color f\r*f\br*4,f\g*f\br*4,f\b*f\br*4
;			Plot f\x,f\y
;			Color f\r*f\br,f\g*f\br,f\b*f\br
;			Plot f\x-1,f\y
;			Plot f\x+1,f\y
;			Plot f\x,f\y-1
;			Plot f\x,f\y+1
		EndIf
	Next
	For s.shot=Each shot
		If s\x>=0 And s\y>=0 And s\x<=scrwid-1 And s\y<=scrhgh-1 Then WritePixelFast s\x,s\y,$ffffff;CompressInts(255,255,255,255)
		If s\x+1>=0 And s\y>=0 And s\x+1<=scrwid-1 And s\y<=scrhgh-1 Then WritePixelFast s\x+1,s\y,$ff0000;CompressInts(0,0,255,255)
		If s\x-1>=0 And s\y>=0 And s\x-1<=scrwid-1 And s\y<=scrhgh-1 Then WritePixelFast s\x-1,s\y,$ff0000;CompressInts(0,0,255,255)
		If s\x>=0 And s\y+1>=0 And s\x<=scrwid-1 And s\y+1<=scrhgh-1 Then WritePixelFast s\x,s\y+1,$ff0000;CompressInts(0,0,255,255)
		If s\x>=0 And s\y-1>=0 And s\x<=scrwid-1 And s\y-1<=scrhgh-1 Then WritePixelFast s\x,s\y-1,$ff0000;CompressInts(0,0,255,255)
;		Color 255,255,255
;		Plot s\x,s\y
;		Color 255,0,0
;		Plot s\x-1,s\y
;		Plot s\x+1,s\y
;		Plot s\x,s\y-1
;		Plot s\x,s\y+1
	Next
End Function

Function CompressInts(n1%,n2%,n3%,n4%)
	a%=n1+(n2 Shl 8)+(n3 Shl 16)+((n4 Mod 128) Shl 24)
	If n4>128 Then a=a-2147483648
	Return a
End Function
